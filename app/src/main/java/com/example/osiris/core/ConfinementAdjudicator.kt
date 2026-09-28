package com.example.osiris.core

object ConfinementAdjudicator {
    val MANDATORY_CONFINEMENT_CRITERIA = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    val REQUIRED_FRESH_RUNS = listOf(1, 2, 3)

    val CRITERIA_DESCRIPTIONS = mapOf(
        "A" to "Filesystem write isolation (read-only root / ephemeral tmpfs)",
        "B" to "Network egress restriction (unshare / loopback strictly)",
        "C" to "Process isolation (PID namespace / zero subproc spawn)",
        "D" to "Environment scrub (zero secret or ambient leakage)",
        "E" to "Memory quota confinement (cgroup strict limits)",
        "F" to "CPU cgroup quota / deterministic time-slice bounds",
        "G" to "IPC namespace isolation (isolated msg queues/semaphores)",
        "H" to "Device node access denial (mknod blocked / restricted /dev)",
        "I" to "Syscall filtering (seccomp-bpf deterministic profile)",
        "J" to "User namespace isolation (non-root unprivileged execution)"
    )

    val MANDATORY_RELEASE_PREREQUISITES = listOf(
        "locked_artifact",
        "preregistration",
        "lineage_pinned",
        "independent_review",
        "adversarial_audit",
        "external_custody",
        "governance_approval"
    )

    fun evaluateSDeployed(
        probeResults: List<ConfinementProbeResult>,
        evidenceIntegrityVerified: Boolean,
        independentAdjudicationVerified: Boolean
    ): DeployedGateStatus {
        // 1. Any confirmed violation immediately yields FALSE (precedence 1)
        for (probe in probeResults) {
            if (probe.status == "FAIL") {
                return DeployedGateStatus.FALSE
            }
        }

        // 2. Any environmental limitation yields BLOCKED (precedence 2)
        for (probe in probeResults) {
            if (probe.status == "BLOCKED") {
                return DeployedGateStatus.BLOCKED
            }
        }

        // 3. Check for completeness of the 30 required probe observations (10 criteria x 3 runs)
        val passedSet = mutableSetOf<Pair<String, Int>>()
        for (probe in probeResults) {
            if (probe.status == "PASS") {
                passedSet.add(probe.criterion to probe.runIndex)
            }
        }

        for (runIdx in REQUIRED_FRESH_RUNS) {
            for (crit in MANDATORY_CONFINEMENT_CRITERIA) {
                if (!passedSet.contains(crit to runIdx)) {
                    return DeployedGateStatus.UNVERIFIED
                }
            }
        }

        // 4. Check cryptographic evidence binding (K) and independent review (L)
        if (!evidenceIntegrityVerified || !independentAdjudicationVerified) {
            return DeployedGateStatus.UNVERIFIED
        }

        // 5. Complete conjunction satisfied
        return DeployedGateStatus.TRUE
    }

    fun evaluateReleaseGate(basis: ReleaseDecisionBasis): AuthorizationState {
        // Hard Non-Substitution Boundary
        if (basis.fixtureMaySubstituteForDeployed) {
            throw SubstitutionViolationError(
                "Boundary breach: fixture_may_substitute_for_deployed=true violates non-substitution invariant."
            )
        }

        // Deployed Confinement Check
        if (basis.sDeployed != DeployedGateStatus.TRUE) {
            return AuthorizationState.NOT_AUTHORIZED
        }

        // Lineage Check
        if (basis.rDeployed != DeployedGateStatus.TRUE) {
            return AuthorizationState.NOT_AUTHORIZED
        }

        // Prerequisites check
        for (prereq in MANDATORY_RELEASE_PREREQUISITES) {
            if (basis.prerequisites[prereq] != true) {
                return AuthorizationState.NOT_AUTHORIZED
            }
        }

        return AuthorizationState.ELIGIBLE
    }

    fun admitExecution(
        contract: ExecutionContract,
        releaseBasis: ReleaseDecisionBasis? = null
    ): ExecutionMode {
        return when (contract.mode) {
            ExecutionMode.DRY_RUN -> ExecutionMode.DRY_RUN
            ExecutionMode.SIMULATION -> {
                if (contract.parameters["require_physical_hardware"] == "true") {
                    throw AuthorityGateError("Physical hardware execution requested under SIMULATION mode.")
                }
                ExecutionMode.SIMULATION
            }
            ExecutionMode.SANDBOX -> {
                if (contract.parameters["sandbox_initialized"] != "true") {
                    throw AuthorityGateError("Sandbox environment is not initialized or verified.")
                }
                ExecutionMode.SANDBOX
            }
            ExecutionMode.AUTHORIZED -> {
                if (releaseBasis == null) {
                    throw AuthorityGateError("Release decision basis is missing for AUTHORIZED execution.")
                }
                if (releaseBasis.authorizationState != AuthorizationState.ELIGIBLE) {
                    throw AuthorityGateError(
                        "Release gate state is ${releaseBasis.authorizationState}; must be ELIGIBLE."
                    )
                }
                if (contract.signedPermitNonce.isNullOrEmpty()) {
                    throw AuthorityGateError("Signed permit nonce is required for AUTHORIZED execution.")
                }
                ExecutionMode.AUTHORIZED
            }
        }
    }
}
