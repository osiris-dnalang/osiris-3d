package com.example.osiris.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

const val REPLAY_ACTION_KIND = "REPLAY_FIXTURE"
const val REPLAY_CAPABILITY = "REPLAY"
const val REPLAY_SCOPE = "REPLAY_ONLY_NON_CRYPTOGRAPHIC"
const val DEFAULT_POLICY_VERSION = "osiris-governance/v0.1-replay"
const val DEFAULT_ISSUER = "local-replay-governor"
const val CANONICALIZATION_VERSION = "OSIRIS-CANONICAL-JSON-V1"

enum class EpistemicStatus {
    DECLARED,
    IMPLEMENTED,
    TESTED,
    MEASURED,
    REPRODUCED,
    VERIFIED,
    SIMULATED,
    HYPOTHESIS,
    REFUTED
}

enum class ReleaseRelevance {
    INFORMATIONAL,
    SCIENTIFIC,
    SECURITY,
    RELEASE_CRITICAL
}

enum class DeployedGateStatus {
    TRUE,
    FALSE,
    BLOCKED,
    UNVERIFIED
}

enum class AuthorizationState {
    NOT_AUTHORIZED,
    ELIGIBLE,
    CONFIRMATORY_AUTHORIZED,
    EXECUTING,
    SUSPENDED,
    COMPLETED
}

enum class ExecutionMode {
    DRY_RUN,
    SIMULATION,
    SANDBOX,
    AUTHORIZED
}

enum class EvidencePlane {
    REPRODUCIBILITY,
    RUNTIME_SECURITY,
    SCIENTIFIC_EXPERIMENTAL,
    HARDWARE_EXECUTION,
    GOVERNANCE
}

enum class LedgerEventType {
    REQUEST_VALIDATED,
    PROPOSAL_AUTHORIZATION_EVALUATED,
    PERMIT_ISSUED,
    EXECUTION_ADMITTED,
    FIXTURE_REPLAY_COMPLETED,
    RUNTIME_PROBE_OBSERVED,
    CLAIM_EVALUATED,
    INVARIANT_VIOLATION_RECORDED,
    CLAIM_ASSERTED
}

enum class ClaimType {
    REPLAY_MATCH,
    RUNTIME_EGRESS_BLOCKED,
    SANDBOX_ATTESTED,
    ANALYSIS_REPRODUCED,
    COMPUTATIONAL_REPRODUCED,
    HARDWARE_EXECUTION_CONFIRMED,
    SCIENTIFIC_CLAIM_SUPPORTED,
    RELEASE_AUTHORIZED
}

enum class ViolationType {
    CROSS_PLANE_SUBSTITUTION,
    SCOPE_MISMATCH,
    BROKEN_HASH_CHAIN,
    UNSUPPORTED_INFERENCE,
    UNATTESTED_AUTHORITY,
    CONTRADICTORY_EVIDENCE,
    MISSING_PREREQUISITE,
    EXPIRED_EVIDENCE
}

enum class ClaimStatus {
    TRUE,
    FALSE,
    BLOCKED,
    UNVERIFIED
}

fun resolveStatusPrecedence(statuses: List<ClaimStatus>): ClaimStatus {
    if (statuses.isEmpty()) return ClaimStatus.UNVERIFIED
    if (statuses.contains(ClaimStatus.FALSE)) return ClaimStatus.FALSE
    if (statuses.contains(ClaimStatus.BLOCKED)) return ClaimStatus.BLOCKED
    if (statuses.contains(ClaimStatus.UNVERIFIED)) return ClaimStatus.UNVERIFIED
    return ClaimStatus.TRUE
}

@Serializable
data class ScopeBinding(
    val artifactDigest: String,
    val environmentDigest: String,
    val timeIntervalStart: String,
    val timeIntervalEnd: String,
    val policyRef: String,
    val authorityId: String
)

@Serializable
data class ActionProposal(
    val proposalId: String,
    val requestId: String,
    val proposerId: String,
    val actionKind: String,
    val capability: String,
    val fixtureId: String,
    val arguments: Map<String, String> = emptyMap(),
    val nonce: String,
    val createdAtUtc: String,
    val expiresAtUtc: String,
    val schemaVersion: String = "action-proposal/v1"
) {
    fun canonicalBytes(): ByteArray {
        val canonicalMap = sortedMapOf<String, Any>(
            "action_kind" to actionKind,
            "arguments" to arguments.toSortedMap(),
            "capability" to capability,
            "created_at_utc" to createdAtUtc,
            "expires_at_utc" to expiresAtUtc,
            "fixture_id" to fixtureId,
            "nonce" to nonce,
            "proposal_id" to proposalId,
            "proposer_id" to proposerId,
            "request_id" to requestId,
            "schema_version" to schemaVersion
        )
        return CanonicalJson.canonicalize(canonicalMap)
    }

    fun proposalSha256(): String {
        return CanonicalJson.canonicalSha256(canonicalBytes())
    }
}

@Serializable
data class ReplayExecutionPermit(
    val proposalSha256: String,
    val canonicalizationVersion: String = CANONICALIZATION_VERSION,
    val policyVersion: String = DEFAULT_POLICY_VERSION,
    val fixtureId: String,
    val nonce: String,
    val processEpochId: String,
    val issuedAtUtc: String,
    val expiresAtUtc: String,
    val scope: String = REPLAY_SCOPE,
    val issuer: String = DEFAULT_ISSUER
) {
    fun requireReplayOnlyScope() {
        if (scope != REPLAY_SCOPE) {
            throw ScopeViolation("Permit scope '$scope' is unauthorized (must be '$REPLAY_SCOPE')")
        }
    }

    fun prohibitExternalExecution() {
        throw ScopeViolation("Replay permits cannot authorize external execution, subprocesses, network, or cloud dispatch")
    }
}

@Serializable
data class ReplayResultRecord(
    val status: String,
    val proposalSha256: String,
    val fixtureId: String,
    val permitNonce: String,
    val processEpochId: String,
    val payload: Map<String, String>,
    val executedAtUtc: String,
    val rawPayloadSha256: String = ""
)

@Serializable
data class LedgerEvent(
    val eventId: String,
    val eventType: LedgerEventType,
    val plane: EvidencePlane,
    val recordedAt: String,
    val actor: Map<String, String>,
    val artifact: Map<String, String>,
    val scope: ScopeBinding,
    val payload: Map<String, String>,
    val evidenceRefs: List<String> = emptyList(),
    val previousRecordDigest: String,
    val recordDigest: String = ""
) {
    fun computeDigest(): String {
        val canonicalMap = sortedMapOf<String, Any>(
            "actor" to actor.toSortedMap(),
            "artifact" to artifact.toSortedMap(),
            "event_id" to eventId,
            "event_type" to eventType.name,
            "evidence_refs" to evidenceRefs,
            "payload" to payload.toSortedMap(),
            "plane" to plane.name,
            "previous_record_digest" to previousRecordDigest,
            "recorded_at" to recordedAt,
            "scope" to sortedMapOf(
                "artifact_digest" to scope.artifactDigest,
                "authority_id" to scope.authorityId,
                "environment_digest" to scope.environmentDigest,
                "policy_ref" to scope.policyRef,
                "time_interval_end" to scope.timeIntervalEnd,
                "time_interval_start" to scope.timeIntervalStart
            )
        )
        val bytes = CanonicalJson.canonicalize(canonicalMap)
        return "sha256:${CanonicalJson.sha256Hex(bytes)}"
    }
}

@Serializable
data class ConfinementProbeResult(
    val criterion: String,         // A through J
    val runIndex: Int,              // 1, 2, or 3
    val status: String,            // PASS, FAIL, BLOCKED, UNRUN
    val evidenceId: String,
    val errorMessage: String? = null,
    val observedErrno: String? = null
)

@Serializable
data class ExecutionContract(
    val executionId: String,
    val targetArtifactDigest: String,
    val mode: ExecutionMode,
    val frozenAtUtc: String,
    val parameters: Map<String, String> = emptyMap(),
    val signedPermitNonce: String? = null,
    val canonicalHash: String = ""
)

@Serializable
data class ReleaseDecisionBasis(
    val releaseId: String,
    val artifactDigest: String,
    val authorizationState: AuthorizationState,
    val sDeployed: DeployedGateStatus,
    val rDeployed: DeployedGateStatus,
    val prerequisites: Map<String, Boolean>,
    val claimsSnapshot: Map<String, EpistemicStatus>,
    val fixtureMaySubstituteForDeployed: Boolean = false,
    val evaluatedAtUtc: String = "",
    val canonicalHash: String = ""
)
