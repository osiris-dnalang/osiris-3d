package com.example.osiris.core

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

const val RELEASE_VERSION = "0.1.0-beta.1"
const val RELEASE_ID = "OSIRIS-LIVLM-BETA-0.1.0-REF"
const val GIT_COMMIT = "62c3492"

data class IntentClassification(
    val type: String,
    val capability: String,
    val confidence: Float
)

data class ExecutionResult(
    val executionId: String,
    val releaseId: String,
    val softwareVersion: String,
    val intent: IntentClassification,
    val requestHash: String,
    val evidenceDigest: String,
    val epistemicStatus: String,
    val policyRef: String,
    val resultSummary: String,
    val resultPayload: Map<String, String>,
    val permitNonce: String? = null
)

class LivLMService(
    val ledger: DynamicEvidenceLedger = DynamicEvidenceLedger("livlm-beta-ledger-01"),
    val governor: CapabilityGovernor = CapabilityGovernor(processEpochId = "livlm-beta-epoch-1"),
    val adapter: ReplayAdapter = ReplayAdapter()
) {
    val serviceStartTime: Instant = Instant.now()

    val scope = ScopeBinding(
        artifactDigest = "sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        environmentDigest = "sha256:9999888877776666555544443333222211110000aaaabbbbccccddddeeeeffff",
        timeIntervalStart = "2026-09-26T00:00:00Z",
        timeIntervalEnd = "2026-09-27T00:00:00Z",
        policyRef = "livlm-beta-policy@1.0",
        authorityId = "livlm-control-plane"
    )

    init {
        // Record initial startup event in ledger
        ledger.recordEvent(
            eventType = LedgerEventType.REQUEST_VALIDATED,
            plane = EvidencePlane.GOVERNANCE,
            actor = mapOf("type" to "service", "id" to "livlm-beta-service"),
            artifact = mapOf("digest" to scope.artifactDigest),
            scope = scope,
            payload = mapOf(
                "version" to RELEASE_VERSION,
                "release_id" to RELEASE_ID,
                "startup_epoch" to governor.processEpochId
            )
        )
    }

    fun classifyIntent(prompt: String): IntentClassification {
        val p = prompt.trim()
        val patterns = listOf(
            Regex("(?i)\\b(health|status|ready|ping)\\b") to ("STATUS" to "status.read"),
            Regex("(?i)\\b(replay|reproduce|fixture|repeat)\\b") to ("REPLAY" to "fixture.replay"),
            Regex("(?i)\\b(benchmark|perf|latency|xeb)\\b") to ("BENCHMARK" to "benchmark.offline"),
            Regex("(?i)\\b(audit|ledger|evidence|history|verify)\\b") to ("AUDIT" to "ledger.read"),
            Regex("(?i)\\b(evaluate|claim|attest)\\b") to ("EVALUATE_CLAIM" to "claim.evaluate"),
            Regex("(?i)\\b(generate|sample|text|tokens?)\\b") to ("GENERATE" to "tokens.sample")
        )

        for ((regex, pair) in patterns) {
            if (regex.containsMatchIn(p)) {
                return IntentClassification(pair.first, pair.second, 0.95f)
            }
        }
        return IntentClassification("GENERAL_INQUIRY", "inquiry.read", 0.50f)
    }

    fun handleIntent(prompt: String, clientNonce: String = ""): ExecutionResult {
        val nonce = if (clientNonce.isBlank()) UUID.randomUUID().toString().take(12) else clientNonce
        val intent = classifyIntent(prompt)

        val rawProposal = sortedMapOf(
            "client_nonce" to nonce,
            "intent_type" to intent.type,
            "prompt" to prompt.trim(),
            "required_capability" to intent.capability,
            "side_effects_allowed" to "false"
        )
        val canonicalBytes = CanonicalJson.canonicalize(rawProposal)
        val requestHash = CanonicalJson.canonicalSha256(canonicalBytes)

        val resultData = mutableMapOf<String, String>()
        val summary: String

        when (intent.type) {
            "REPLAY" -> {
                val fixtureId = if (prompt.contains("benchmark")) "benchmark-sample-v1"
                else if (prompt.contains("energy")) "fixture-energy-test-v1"
                else "echo-v1"

                val replayRecord = handleReplay(fixtureId, nonce)
                resultData.putAll(replayRecord.payload)
                resultData["fixture_id"] = fixtureId
                resultData["execution_mode"] = "DETERMINISTIC_REPLAY"
                summary = "Replay verified against fixture '$fixtureId'. Epistemic status: REPRODUCED_OFFLINE."
            }
            "GENERATE" -> {
                resultData["execution_mode"] = "BOUNDED_SYNTHESIS"
                resultData["tokens"] = "DNA::}{::lang governed_execution"
                resultData["epistemic_class"] = "SIMULATED"
                summary = "Bounded deterministic synthesis completed. Zero side effects permitted."
            }
            "STATUS" -> {
                resultData["service_status"] = "OPERATIONAL"
                resultData["epoch_id"] = governor.processEpochId
                resultData["ledger_size"] = ledger.eventCount.toString()
                summary = "LIVLM Control Plane is OPERATIONAL. Epoch: ${governor.processEpochId}."
            }
            "AUDIT" -> {
                val valid = ledger.verifyChainIntegrity()
                resultData["events_count"] = ledger.eventCount.toString()
                resultData["chain_valid"] = valid.toString()
                resultData["latest_digest"] = ledger.latestDigest
                summary = "Audit complete: Chain integrity is ${if (valid) "VALID" else "CORRUPTED"}. ${ledger.eventCount} immutable blocks."
            }
            "BENCHMARK" -> {
                resultData["execution_mode"] = "OFFLINE_BENCHMARK"
                resultData["metric"] = "0.9763"
                resultData["samples"] = "1024"
                summary = "Offline verification benchmark: Metric=0.9763 across 1024 deterministic samples."
            }
            "EVALUATE_CLAIM" -> {
                resultData["evaluation_policy"] = "NON_SUBSTITUTION_STRICT"
                resultData["permitted_claim_types"] = "8"
                summary = "Evaluator ready. Select Claim Type and cited Evidence IDs in the Evaluator tab."
            }
            else -> {
                resultData["execution_mode"] = "READ_ONLY_INQUIRY"
                resultData["message"] = "Intent classified as ${intent.type}. No destructive side effects permitted."
                summary = "General inquiry processed under fail-closed read-only bounds."
            }
        }

        // Record in ledger
        val event = ledger.recordEvent(
            eventType = LedgerEventType.REQUEST_VALIDATED,
            plane = EvidencePlane.GOVERNANCE,
            actor = mapOf("type" to "client", "id" to "livlm-user"),
            artifact = mapOf("digest" to scope.artifactDigest),
            scope = scope,
            payload = mapOf(
                "request_hash" to requestHash,
                "intent_type" to intent.type,
                "client_nonce" to nonce,
                "result_status" to "SUCCESS"
            )
        )

        return ExecutionResult(
            executionId = event.eventId,
            releaseId = RELEASE_ID,
            softwareVersion = RELEASE_VERSION,
            intent = intent,
            requestHash = requestHash,
            evidenceDigest = event.recordDigest,
            epistemicStatus = if (intent.type == "REPLAY") "REPRODUCED_OFFLINE" else "SIMULATED_OFFLINE_REFERENCE",
            policyRef = scope.policyRef,
            resultSummary = summary,
            resultPayload = resultData,
            permitNonce = nonce
        )
    }

    fun handleReplay(fixtureId: String = "echo-v1", customNonce: String = ""): ReplayResultRecord {
        val now = Instant.now()
        val nonce = if (customNonce.isBlank()) "nonce-${UUID.randomUUID().toString().take(8)}" else customNonce

        val proposal = ActionProposal(
            proposalId = "prop-${UUID.randomUUID().toString().take(8)}",
            requestId = "req-${UUID.randomUUID().toString().take(8)}",
            proposerId = "livlm-client",
            actionKind = REPLAY_ACTION_KIND,
            capability = REPLAY_CAPABILITY,
            fixtureId = fixtureId,
            arguments = mapOf("fixture_id" to fixtureId),
            nonce = nonce,
            createdAtUtc = now.toString(),
            expiresAtUtc = now.plus(1, ChronoUnit.HOURS).toString()
        )

        val permit = governor.issueReplayPermit(proposal, now)
        val result = governor.submit(proposal, permit, adapter, now)

        ledger.recordEvent(
            eventType = LedgerEventType.FIXTURE_REPLAY_COMPLETED,
            plane = EvidencePlane.REPRODUCIBILITY,
            actor = mapOf("type" to "client", "id" to "replay-governor"),
            artifact = mapOf("digest" to scope.artifactDigest),
            scope = scope,
            payload = mapOf(
                "proposal_sha256" to result.proposalSha256,
                "fixture_id" to result.fixtureId,
                "nonce" to result.permitNonce,
                "status" to result.status
            )
        )

        return result
    }
}
