package com.example.osiris.core

sealed class GovernanceViolation(message: String) : Exception(message)

class SchemaValidationError(message: String) : GovernanceViolation("SCHEMA_VALIDATION_ERROR: $message")
class ScopeViolation(message: String) : GovernanceViolation("SCOPE_VIOLATION: $message")
class NonceReplayDetected(message: String) : GovernanceViolation("NONCE_REPLAY_DETECTED: $message")
class ProposalExpired(message: String) : GovernanceViolation("PROPOSAL_EXPIRED: $message")
class EpochMismatch(message: String) : GovernanceViolation("EPOCH_MISMATCH: $message")
class ExecutionBindingMismatch(message: String) : GovernanceViolation("EXECUTION_BINDING_MISMATCH: $message")
class CapabilityNotPermitted(message: String) : GovernanceViolation("CAPABILITY_NOT_PERMITTED: $message")
class ActionNotPermitted(message: String) : GovernanceViolation("ACTION_NOT_PERMITTED: $message")
class AuthorityGateError(message: String) : GovernanceViolation("AUTHORITY_GATE_ERROR: $message")
class SubstitutionViolationError(message: String) : GovernanceViolation("SUBSTITUTION_VIOLATION_ERROR: $message")
class AdjudicationError(message: String) : GovernanceViolation("ADJUDICATION_ERROR: $message")
