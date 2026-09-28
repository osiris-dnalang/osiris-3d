package com.example.osiris.core

import java.time.Instant

const val GENESIS_DIGEST = "sha256:0000000000000000000000000000000000000000000000000000000000000000"

val PERMITTED_PLANES_BY_CLAIM_TYPE = mapOf<ClaimType, Set<EvidencePlane>>(
    ClaimType.REPLAY_MATCH to setOf(EvidencePlane.REPRODUCIBILITY),
    ClaimType.RUNTIME_EGRESS_BLOCKED to setOf(EvidencePlane.RUNTIME_SECURITY),
    ClaimType.SANDBOX_ATTESTED to setOf(EvidencePlane.RUNTIME_SECURITY),
    ClaimType.ANALYSIS_REPRODUCED to setOf(EvidencePlane.SCIENTIFIC_EXPERIMENTAL),
    ClaimType.COMPUTATIONAL_REPRODUCED to setOf(
        EvidencePlane.REPRODUCIBILITY,
        EvidencePlane.SCIENTIFIC_EXPERIMENTAL
    ),
    ClaimType.HARDWARE_EXECUTION_CONFIRMED to setOf(EvidencePlane.HARDWARE_EXECUTION),
    ClaimType.SCIENTIFIC_CLAIM_SUPPORTED to setOf(EvidencePlane.SCIENTIFIC_EXPERIMENTAL),
    ClaimType.RELEASE_AUTHORIZED to setOf(EvidencePlane.GOVERNANCE)
)

data class ClaimEvaluationResult(
    val claimType: ClaimType,
    val subject: String,
    val status: ClaimStatus,
    val violations: List<ViolationType>,
    val citedEvidenceIds: List<String>,
    val evaluationEventId: String,
    val details: Map<String, String> = emptyMap()
)

class DynamicEvidenceLedger(val ledgerId: String = "livlm-beta-ledger-01") {
    private val eventsList = mutableListOf<LedgerEvent>()
    private val eventsById = mutableMapOf<String, LedgerEvent>()
    private var currentLatestDigest: String = GENESIS_DIGEST

    val latestDigest: String
        get() = currentLatestDigest

    val eventCount: Int
        get() = eventsList.size

    val events: List<LedgerEvent>
        get() = eventsList.toList()

    fun recordEvent(
        eventType: LedgerEventType,
        plane: EvidencePlane,
        actor: Map<String, String>,
        artifact: Map<String, String>,
        scope: ScopeBinding,
        payload: Map<String, String>,
        evidenceRefs: List<String> = emptyList(),
        eventId: String? = null,
        recordedAt: String? = null
    ): LedgerEvent {
        val actualRecordedAt = recordedAt ?: Instant.now().toString()
        val actualEventId = eventId ?: "evt-%06d".format(eventsList.size + 1)

        if (eventsById.containsKey(actualEventId)) {
            throw SchemaValidationError("DUPLICATE_EVENT_ID: Event ID '$actualEventId' already exists in ledger")
        }

        val draftEvent = LedgerEvent(
            eventId = actualEventId,
            eventType = eventType,
            plane = plane,
            recordedAt = actualRecordedAt,
            actor = actor,
            artifact = artifact,
            scope = scope,
            payload = payload,
            evidenceRefs = evidenceRefs,
            previousRecordDigest = currentLatestDigest,
            recordDigest = ""
        )

        val digest = draftEvent.computeDigest()
        val sealedEvent = draftEvent.copy(recordDigest = digest)

        eventsList.add(sealedEvent)
        eventsById[actualEventId] = sealedEvent
        currentLatestDigest = digest
        return sealedEvent
    }

    fun getEvent(eventId: String): LedgerEvent? = eventsById[eventId]

    fun getEventsByPlane(plane: EvidencePlane): List<LedgerEvent> =
        eventsList.filter { it.plane == plane }

    fun verifyChainIntegrity(): Boolean {
        var expectedPrev = GENESIS_DIGEST
        for (event in eventsList) {
            if (event.previousRecordDigest != expectedPrev) {
                return false
            }
            val computed = event.computeDigest()
            if (event.recordDigest != computed) {
                return false
            }
            expectedPrev = event.recordDigest
        }
        return true
    }

    fun evaluateClaim(
        claimType: ClaimType,
        subject: String,
        scope: ScopeBinding,
        citedEvidenceIds: List<String>,
        requireIndependentAdjudication: Boolean = false
    ): ClaimEvaluationResult {
        val violations = mutableListOf<ViolationType>()
        val statuses = mutableListOf<ClaimStatus>()
        val permittedPlanes = PERMITTED_PLANES_BY_CLAIM_TYPE[claimType] ?: emptySet()

        if (citedEvidenceIds.isEmpty()) {
            violations.add(ViolationType.MISSING_PREREQUISITE)
            statuses.add(ClaimStatus.UNVERIFIED)
        }

        // Check for contradictory evidence in ledger matching this scope
        for (event in eventsList) {
            if (event.scope.artifactDigest == scope.artifactDigest) {
                if (event.eventType == LedgerEventType.RUNTIME_PROBE_OBSERVED) {
                    if (event.payload["status"] == "FAIL" || event.payload["confinement_breached"] == "true") {
                        violations.add(ViolationType.CONTRADICTORY_EVIDENCE)
                        statuses.add(ClaimStatus.FALSE)
                    }
                }
            }
        }

        var validSupportingEvidenceCount = 0

        for (evId in citedEvidenceIds) {
            val ev = getEvent(evId)
            if (ev == null) {
                violations.add(ViolationType.MISSING_PREREQUISITE)
                statuses.add(ClaimStatus.BLOCKED)
                continue
            }

            // 0. Transitive Claim Laundering Prohibition
            if (ev.eventType in setOf(
                    LedgerEventType.CLAIM_EVALUATED,
                    LedgerEventType.INVARIANT_VIOLATION_RECORDED,
                    LedgerEventType.CLAIM_ASSERTED
                )
            ) {
                violations.add(ViolationType.UNSUPPORTED_INFERENCE)
                statuses.add(ClaimStatus.UNVERIFIED)
                continue
            }

            // 1. Plane Support Check (Evidence Non-Substitution Invariant)
            if (ev.plane !in permittedPlanes) {
                violations.add(ViolationType.CROSS_PLANE_SUBSTITUTION)
                statuses.add(ClaimStatus.UNVERIFIED)
                continue
            }

            // 2. Scope Matching Check
            if (ev.scope.artifactDigest != scope.artifactDigest) {
                violations.add(ViolationType.SCOPE_MISMATCH)
                statuses.add(ClaimStatus.UNVERIFIED)
                continue
            }

            if (ev.scope.environmentDigest != scope.environmentDigest) {
                violations.add(ViolationType.SCOPE_MISMATCH)
                statuses.add(ClaimStatus.UNVERIFIED)
                continue
            }

            validSupportingEvidenceCount++
        }

        if (validSupportingEvidenceCount > 0 && violations.isEmpty()) {
            statuses.add(ClaimStatus.TRUE)
        } else if (statuses.isEmpty()) {
            statuses.add(ClaimStatus.UNVERIFIED)
        }

        val finalStatus = resolveStatusPrecedence(statuses)

        // Record claim evaluation in ledger
        val evalEvent = recordEvent(
            eventType = if (finalStatus == ClaimStatus.FALSE) LedgerEventType.INVARIANT_VIOLATION_RECORDED else LedgerEventType.CLAIM_EVALUATED,
            plane = EvidencePlane.GOVERNANCE,
            actor = mapOf("type" to "adjudicator", "id" to "dynamic-evidence-ledger"),
            artifact = mapOf("digest" to scope.artifactDigest),
            scope = scope,
            payload = mapOf(
                "claim_type" to claimType.name,
                "subject" to subject,
                "status" to finalStatus.name,
                "violations" to violations.joinToString(",") { it.name }
            ),
            evidenceRefs = citedEvidenceIds
        )

        return ClaimEvaluationResult(
            claimType = claimType,
            subject = subject,
            status = finalStatus,
            violations = violations.distinct(),
            citedEvidenceIds = citedEvidenceIds,
            evaluationEventId = evalEvent.eventId,
            details = mapOf(
                "valid_supporting_count" to validSupportingEvidenceCount.toString(),
                "permitted_planes" to permittedPlanes.joinToString(", ") { it.name }
            )
        )
    }
}
