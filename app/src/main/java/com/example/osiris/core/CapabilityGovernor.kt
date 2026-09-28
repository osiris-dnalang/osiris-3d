package com.example.osiris.core

import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.UUID

class CapabilityGovernor(
    processEpochId: String? = null,
    val policyVersion: String = DEFAULT_POLICY_VERSION
) {
    val processEpochId: String = processEpochId ?: "epoch_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    private val usedNonces = mutableSetOf<String>()

    val usedNoncesCount: Int
        get() = usedNonces.size

    fun getUsedNonces(): Set<String> = usedNonces.toSet()

    fun issueReplayPermit(
        proposal: ActionProposal,
        now: Instant = Instant.now()
    ): ReplayExecutionPermit {
        val issuedAtUtc = now.toString()
        return ReplayExecutionPermit(
            proposalSha256 = proposal.proposalSha256(),
            canonicalizationVersion = CANONICALIZATION_VERSION,
            policyVersion = policyVersion,
            fixtureId = proposal.fixtureId,
            nonce = proposal.nonce,
            processEpochId = processEpochId,
            issuedAtUtc = issuedAtUtc,
            expiresAtUtc = proposal.expiresAtUtc,
            scope = REPLAY_SCOPE,
            issuer = DEFAULT_ISSUER
        )
    }

    fun submit(
        proposal: ActionProposal,
        permit: ReplayExecutionPermit,
        adapter: ReplayAdapter,
        now: Instant = Instant.now()
    ): ReplayResultRecord {
        // 1. Structural schema verification
        if (proposal.actionKind != REPLAY_ACTION_KIND) {
            throw ActionNotPermitted("Action kind '${proposal.actionKind}' not permitted (must be '$REPLAY_ACTION_KIND')")
        }
        if (proposal.capability != REPLAY_CAPABILITY) {
            throw CapabilityNotPermitted("Capability '${proposal.capability}' not permitted (must be '$REPLAY_CAPABILITY')")
        }

        // 2. Canonical hash binding verification
        val proposalHash = proposal.proposalSha256()
        if (proposalHash != permit.proposalSha256) {
            throw ExecutionBindingMismatch(
                "Proposal digest '$proposalHash' does not match permit binding '${permit.proposalSha256}'"
            )
        }

        // 3. Fixture ID binding
        if (proposal.fixtureId != permit.fixtureId) {
            throw ExecutionBindingMismatch(
                "Proposal fixture_id '${proposal.fixtureId}' does not match permit fixture_id '${permit.fixtureId}'"
            )
        }

        // 4. Scope verification
        permit.requireReplayOnlyScope()

        // 5. Canonicalization version match
        if (permit.canonicalizationVersion != CANONICALIZATION_VERSION) {
            throw SchemaValidationError(
                "Permit canonicalization version '${permit.canonicalizationVersion}' != '$CANONICALIZATION_VERSION'"
            )
        }

        // 6. Expiry check
        try {
            val expiry = Instant.parse(permit.expiresAtUtc)
            if (now.isAfter(expiry)) {
                throw ProposalExpired("Permit expired at ${permit.expiresAtUtc} (current time: $now)")
            }
        } catch (e: DateTimeParseException) {
            throw SchemaValidationError("Invalid timestamp format in permit expiresAtUtc: ${permit.expiresAtUtc}")
        }

        // 7. Process epoch check
        if (permit.processEpochId != this.processEpochId) {
            throw EpochMismatch("Permit processEpochId '${permit.processEpochId}' does not match active governor epoch '$processEpochId'")
        }

        // 8. Nonce deduplication in epoch
        if (usedNonces.contains(permit.nonce)) {
            throw NonceReplayDetected("Nonce '${permit.nonce}' has already been consumed in active epoch '$processEpochId'")
        }

        // 9. Consume nonce
        usedNonces.add(permit.nonce)

        // 10. Execute inside pure adapter
        val outputPayload = adapter.execute(proposal.fixtureId)
        val rawPayloadBytes = CanonicalJson.canonicalize(outputPayload)
        val payloadDigest = CanonicalJson.canonicalSha256(rawPayloadBytes)

        return ReplayResultRecord(
            status = "SUCCESS",
            proposalSha256 = proposalHash,
            fixtureId = proposal.fixtureId,
            permitNonce = permit.nonce,
            processEpochId = processEpochId,
            payload = outputPayload,
            executedAtUtc = now.toString(),
            rawPayloadSha256 = payloadDigest
        )
    }
}
