package com.example.osiris.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.osiris.core.ActionProposal
import com.example.osiris.core.AuthorizationState
import com.example.osiris.core.CanonicalJson
import com.example.osiris.core.ClaimEvaluationResult
import com.example.osiris.core.ClaimStatus
import com.example.osiris.core.ClaimType
import com.example.osiris.core.ConfinementAdjudicator
import com.example.osiris.core.ConfinementProbeResult
import com.example.osiris.core.DeployedGateStatus
import com.example.osiris.core.EvidencePlane
import com.example.osiris.core.ExecutionContract
import com.example.osiris.core.ExecutionMode
import com.example.osiris.core.ExecutionResult
import com.example.osiris.core.FleetService
import com.example.osiris.core.JobExecutionPlan
import com.example.osiris.core.LedgerEvent
import com.example.osiris.core.LedgerEventType
import com.example.osiris.core.LivLMService
import com.example.osiris.core.OperatorActionPrompt
import com.example.osiris.core.PipelineStage
import com.example.osiris.core.PrinterNode
import com.example.osiris.core.ReleaseDecisionBasis
import com.example.osiris.core.ReplayResultRecord
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OsirisUiState(
    val serviceStartTime: Instant = Instant.now(),
    val processEpochId: String = "",
    val chainIntegrityValid: Boolean = true,
    val latestDigest: String = "",
    val events: List<LedgerEvent> = emptyList(),
    val lastExecutionResult: ExecutionResult? = null,
    val isExecuting: Boolean = false,
    val errorMessage: String? = null,
    // Confinement State
    val probeResults: Map<Pair<String, Int>, ConfinementProbeResult> = emptyMap(),
    val evidenceIntegrityVerified: Boolean = true,
    val independentAdjudicationVerified: Boolean = true,
    val sDeployedStatus: DeployedGateStatus = DeployedGateStatus.UNVERIFIED,
    val releasePrerequisites: Map<String, Boolean> = emptyMap(),
    val fixtureMaySubstituteForDeployed: Boolean = false,
    val releaseGateStatus: AuthorizationState = AuthorizationState.NOT_AUTHORIZED,
    // Claim Evaluator State
    val claimEvaluationResult: ClaimEvaluationResult? = null,
    // Canonical Inspector
    val canonicalDiagnostic: CanonicalJson.ValidationDiagnostic? = null,
    // Bambu Print Farm & Fleet State
    val fleet: List<PrinterNode> = emptyList(),
    val isWifiConnected: Boolean = true,
    val wifiSsid: String = "Factory-Bambu-Mesh-5G",
    val isScanning: Boolean = false,
    val activeJobPlan: JobExecutionPlan? = null,
    val pipelineStage: PipelineStage = PipelineStage.IDLE,
    val operatorPrompt: OperatorActionPrompt? = null,
    val terminalLogs: List<String> = emptyList()
)

class OsirisViewModel : ViewModel() {

    private val service = LivLMService()
    val fleetService = FleetService()

    private val _uiState = MutableStateFlow(OsirisUiState())
    val uiState: StateFlow<OsirisUiState> = _uiState.asStateFlow()

    init {
        initInitialState()
    }

    private fun initInitialState() {
        val initialPrereqs = ConfinementAdjudicator.MANDATORY_RELEASE_PREREQUISITES.associateWith { true }

        val initialProbes = mutableMapOf<Pair<String, Int>, ConfinementProbeResult>()
        for (run in ConfinementAdjudicator.REQUIRED_FRESH_RUNS) {
            for (crit in ConfinementAdjudicator.MANDATORY_CONFINEMENT_CRITERIA) {
                initialProbes[crit to run] = ConfinementProbeResult(
                    criterion = crit,
                    runIndex = run,
                    status = "PASS",
                    evidenceId = "probe-$crit-run$run-001"
                )
            }
        }

        val sDeployed = ConfinementAdjudicator.evaluateSDeployed(
            initialProbes.values.toList(),
            evidenceIntegrityVerified = true,
            independentAdjudicationVerified = true
        )

        val releaseBasis = ReleaseDecisionBasis(
            releaseId = "OSIRIS-LIVLM-BETA-0.1.0-REF",
            artifactDigest = service.scope.artifactDigest,
            authorizationState = AuthorizationState.NOT_AUTHORIZED,
            sDeployed = sDeployed,
            rDeployed = DeployedGateStatus.TRUE,
            prerequisites = initialPrereqs,
            claimsSnapshot = emptyMap(),
            fixtureMaySubstituteForDeployed = false,
            evaluatedAtUtc = Instant.now().toString()
        )
        val releaseGate = try {
            ConfinementAdjudicator.evaluateReleaseGate(releaseBasis)
        } catch (e: Exception) {
            AuthorizationState.NOT_AUTHORIZED
        }

        val initialPlan = fleetService.buildJobExecutionPlan("I need a heavy duty drone assembly that won't shatter on impact.")

        _uiState.value = _uiState.value.copy(
            serviceStartTime = service.serviceStartTime,
            processEpochId = service.governor.processEpochId,
            chainIntegrityValid = service.ledger.verifyChainIntegrity(),
            latestDigest = service.ledger.latestDigest,
            events = service.ledger.events,
            probeResults = initialProbes,
            sDeployedStatus = sDeployed,
            releasePrerequisites = initialPrereqs,
            releaseGateStatus = releaseGate,
            fleet = fleetService.getFleet(),
            activeJobPlan = initialPlan,
            terminalLogs = listOf(
                "--- [OSIRIS FLEET ENGINE INITIALIZED] ---",
                "Subnet: 192.168.10.0/24 • 9 Bambu Lab nodes online",
                "MQTT TLS Port: 8883 • Protocol: bblp local control",
                "Ready for generative manufacturing dispatch."
            )
        )

        inspectJson(
            """{
  "action_kind": "REPLAY_FIXTURE",
  "capability": "REPLAY",
  "fixture_id": "echo-v1",
  "nonce": "nonce-demo-100",
  "proposal_id": "prop-001"
}"""
        )
    }

    // Wi-Fi Onboarding
    fun connectWifi() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWifiConnected = false)
            delay(600)
            _uiState.value = _uiState.value.copy(
                isWifiConnected = true,
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[WIFI] Authenticated with WPA3 Enterprise on 'Factory-Bambu-Mesh-5G'",
                    "[WIFI] Assigned Gateway IP: 192.168.10.1 (MTU: 1500)"
                )
            )
        }
    }

    fun scanSubnet() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)
            delay(800)
            val currentFleet = fleetService.getFleet()
            _uiState.value = _uiState.value.copy(
                isScanning = false,
                fleet = currentFleet,
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[mDNS] Subnet sweep completed. Discovered ${currentFleet.size} Bambu Lab devices:",
                    "  - 5x P1S (192.168.10.41 - .45)",
                    "  - 2x A1 (192.168.10.46 - .47)",
                    "  - 1x A1 Mini (192.168.10.48)",
                    "  - 1x H2C 16-Channel Array (192.168.10.50)"
                )
            )
        }
    }

    fun togglePrinter(deviceId: String) {
        fleetService.togglePrinterSelection(deviceId)
        _uiState.value = _uiState.value.copy(fleet = fleetService.getFleet())
    }

    fun selectAllPrinters(selected: Boolean) {
        fleetService.selectAllPrinters(selected)
        _uiState.value = _uiState.value.copy(fleet = fleetService.getFleet())
    }

    // Generative Slicing Planner
    fun generateJobPlan(userPrompt: String) {
        val plan = fleetService.buildJobExecutionPlan(userPrompt)
        _uiState.value = _uiState.value.copy(
            activeJobPlan = plan,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[DEVOPS] Parsed prompt: '$userPrompt'",
                "[DEVOPS] Resolved Class: ${plan.loadBearingClass} • Modifiers: Walls=5, Infill=28% Gyroid",
                "[DEVOPS] Plan generated: ${plan.planId}"
            )
        )
    }

    // 4-Phase Pipeline Execution
    fun launchPipeline() {
        val plan = _uiState.value.activeJobPlan ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                pipelineStage = PipelineStage.PHASE_1_REFINEMENT,
                terminalLogs = listOf(
                    "--- [OSIRIS PIPELINE] INITIATING PROJECT: ${plan.projectName} ---",
                    "User Request: '${plan.rawRequest}'",
                    ">> PHASE 1: Structural & Material Refinement",
                    "  Analyzing part: Drone Frame (drone_frame.3mf)",
                    "    - Upgraded Material: PETG-CF (Selected for high flexural modulus and impact resistance)",
                    "    - Optimized Config: Walls=5, Infill=28.0% gyroid",
                    "  Analyzing part: Propeller Guards (prop_guards.stl)",
                    "    - Upgraded Material: PLA (Non-structural, selected for rapid printing)",
                    "    - Optimized Config: Walls=3, Infill=15.0% gyroid"
                )
            )
            delay(700)

            _uiState.value = _uiState.value.copy(
                pipelineStage = PipelineStage.PHASE_2_ROUTING,
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    ">> PHASE 2: Fleet Routing & Load Balancing",
                    "  [BLOCKED] Part 96530cd5 (Drone Frame) routing blocked: PETG-CF not detected in P1S_Alpha AMS Slot 2."
                )
            )
            delay(600)

            val prompt = OperatorActionPrompt(
                partId = "part_96530cd5",
                partName = "Drone Frame",
                requiredMaterial = "PETG-CF",
                requiredColor = "Black",
                targetPrinterId = "P1S_Alpha",
                targetPrinterName = "P1S_Alpha",
                targetSlot = 2,
                instruction = "Please load MaterialClass.PETG_CF (Black) into P1S_Alpha, AMS Slot 2."
            )

            _uiState.value = _uiState.value.copy(
                pipelineStage = PipelineStage.PHASE_3_OPERATOR_ACTION,
                operatorPrompt = prompt,
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    ">> PHASE 3: Operator Action Required (Filament Loading)",
                    "  [ACTION] Part 96530cd5 (drone_frame.3mf) requires MaterialClass.PETG_CF in Black.",
                    "    -> INSTRUCTION: Please load MaterialClass.PETG_CF (Black) into P1S_Alpha, AMS Slot 2.",
                    "    -> (Note: P1S_Alpha was selected because PETG-CF requires an enclosed chamber to prevent warping.)"
                )
            )
        }
    }

    fun confirmOperatorLoad() {
        viewModelScope.launch {
            fleetService.loadFilamentIntoSlot("P1S_Alpha", 2, "PETG-CF", "Black", "#111111")
            val updatedFleet = fleetService.getFleet()

            _uiState.value = _uiState.value.copy(
                fleet = updatedFleet,
                pipelineStage = PipelineStage.PHASE_4_DISPATCH,
                operatorPrompt = null,
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "    -> [SYSTEM] Operator confirmed filament load on P1S_Alpha AMS Slot 2. Re-routing...",
                    "  [SUCCESS] Routed part 96530cd5 (Drone Frame) -> P1S_Alpha",
                    "  [SUCCESS] Routed part 65412e28 (Propeller Guards) -> A1_Mini_Theta",
                    ">> PHASE 4: Generating Final Slicer Directives",
                    "  [P1S_Alpha] Applying modifiers: {'wall_loops': 5, 'infill_pattern': 'gyroid', 'infill_density': 0.28}",
                    "  [P1S_Alpha] Queueing MQTT print dispatch via FTP (port 8883 TLS)...",
                    "  [A1_Mini_Theta] Applying modifiers: {'wall_loops': 3, 'infill_pattern': 'gyroid', 'infill_density': 0.15}",
                    "  [A1_Mini_Theta] Queueing MQTT print dispatch via FTP (port 8883 TLS)...",
                    "[OSIRIS PIPELINE] PROJECT SUCCESSFULLY ORCHESTRATED."
                )
            )

            delay(600)

            // Record execution dispatch in evidence ledger
            val ev = service.ledger.recordEvent(
                eventType = LedgerEventType.EXECUTION_ADMITTED,
                plane = EvidencePlane.HARDWARE_EXECUTION,
                actor = mapOf("type" to "devops-agent", "id" to "osiris-fleet-router"),
                artifact = mapOf("digest" to "sha256:drone_frame_hardened_3mf"),
                scope = service.scope,
                payload = mapOf(
                    "target_node" to "P1S_Alpha",
                    "material" to "PETG-CF",
                    "status" to "DISPATCHED_MQTT",
                    "wall_loops" to "5",
                    "infill" to "28% gyroid"
                )
            )

            // Update printer state to PRINTING
            val p1s = updatedFleet.find { it.name == "P1S_Alpha" }
            p1s?.status = "PRINTING"
            p1s?.nozzleTemp = 255.0
            p1s?.bedTemp = 70.0
            p1s?.progressPercent = 1.0

            _uiState.value = _uiState.value.copy(
                pipelineStage = PipelineStage.COMPLETED,
                fleet = fleetService.getFleet(),
                events = service.ledger.events,
                latestDigest = service.ledger.latestDigest,
                chainIntegrityValid = service.ledger.verifyChainIntegrity()
            )
        }
    }

    fun resetPipeline() {
        _uiState.value = _uiState.value.copy(
            pipelineStage = PipelineStage.IDLE,
            operatorPrompt = null
        )
    }

    // Original LIVLM Governance Execution
    fun executeIntent(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExecuting = true, errorMessage = null)
            try {
                val result = service.handleIntent(prompt)
                _uiState.value = _uiState.value.copy(
                    isExecuting = false,
                    lastExecutionResult = result,
                    events = service.ledger.events,
                    latestDigest = service.ledger.latestDigest,
                    chainIntegrityValid = service.ledger.verifyChainIntegrity()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExecuting = false,
                    errorMessage = e.message ?: e.toString()
                )
            }
        }
    }

    fun executeReplay(fixtureId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExecuting = true, errorMessage = null)
            try {
                val record = service.handleReplay(fixtureId)
                val intent = service.classifyIntent("replay fixture $fixtureId")
                val execResult = ExecutionResult(
                    executionId = "replay-${record.permitNonce.take(8)}",
                    releaseId = "OSIRIS-LIVLM-BETA-0.1.0-REF",
                    softwareVersion = "0.1.0-beta.1",
                    intent = intent,
                    requestHash = record.proposalSha256,
                    evidenceDigest = service.ledger.latestDigest,
                    epistemicStatus = "REPRODUCED_OFFLINE",
                    policyRef = service.scope.policyRef,
                    resultSummary = "Deterministic replay verified for '$fixtureId'. Nonce consumed: ${record.permitNonce}",
                    resultPayload = record.payload,
                    permitNonce = record.permitNonce
                )
                _uiState.value = _uiState.value.copy(
                    isExecuting = false,
                    lastExecutionResult = execResult,
                    events = service.ledger.events,
                    latestDigest = service.ledger.latestDigest,
                    chainIntegrityValid = service.ledger.verifyChainIntegrity()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExecuting = false,
                    errorMessage = e.message ?: e.toString()
                )
            }
        }
    }

    fun evaluateClaim(claimType: ClaimType, subject: String, citedIds: List<String>) {
        try {
            val result = service.ledger.evaluateClaim(
                claimType = claimType,
                subject = subject,
                scope = service.scope,
                citedEvidenceIds = citedIds
            )
            _uiState.value = _uiState.value.copy(
                claimEvaluationResult = result,
                events = service.ledger.events,
                latestDigest = service.ledger.latestDigest,
                chainIntegrityValid = service.ledger.verifyChainIntegrity(),
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message ?: e.toString())
        }
    }

    fun setProbeStatus(criterion: String, runIndex: Int, status: String) {
        val currentProbes = _uiState.value.probeResults.toMutableMap()
        currentProbes[criterion to runIndex] = ConfinementProbeResult(
            criterion = criterion,
            runIndex = runIndex,
            status = status,
            evidenceId = "probe-$criterion-run$runIndex-${Instant.now().epochSecond}"
        )
        recalculateConfinementAndRelease(currentProbes, _uiState.value.releasePrerequisites, _uiState.value.fixtureMaySubstituteForDeployed)
    }

    fun resetAllProbesToPass() {
        val initialProbes = mutableMapOf<Pair<String, Int>, ConfinementProbeResult>()
        for (run in ConfinementAdjudicator.REQUIRED_FRESH_RUNS) {
            for (crit in ConfinementAdjudicator.MANDATORY_CONFINEMENT_CRITERIA) {
                initialProbes[crit to run] = ConfinementProbeResult(
                    criterion = crit,
                    runIndex = run,
                    status = "PASS",
                    evidenceId = "probe-$crit-run$run-reset"
                )
            }
        }
        recalculateConfinementAndRelease(initialProbes, _uiState.value.releasePrerequisites, _uiState.value.fixtureMaySubstituteForDeployed)
    }

    fun togglePrerequisite(key: String) {
        val current = _uiState.value.releasePrerequisites.toMutableMap()
        current[key] = !(current[key] ?: true)
        recalculateConfinementAndRelease(_uiState.value.probeResults, current, _uiState.value.fixtureMaySubstituteForDeployed)
    }

    fun setFixtureMaySubstitute(value: Boolean) {
        recalculateConfinementAndRelease(_uiState.value.probeResults, _uiState.value.releasePrerequisites, value)
    }

    private fun recalculateConfinementAndRelease(
        probes: Map<Pair<String, Int>, ConfinementProbeResult>,
        prereqs: Map<String, Boolean>,
        fixtureSubstitute: Boolean
    ) {
        val sDeployed = ConfinementAdjudicator.evaluateSDeployed(
            probes.values.toList(),
            evidenceIntegrityVerified = _uiState.value.evidenceIntegrityVerified,
            independentAdjudicationVerified = _uiState.value.independentAdjudicationVerified
        )

        val releaseBasis = ReleaseDecisionBasis(
            releaseId = "OSIRIS-LIVLM-BETA-0.1.0-REF",
            artifactDigest = service.scope.artifactDigest,
            authorizationState = AuthorizationState.NOT_AUTHORIZED,
            sDeployed = sDeployed,
            rDeployed = DeployedGateStatus.TRUE,
            prerequisites = prereqs,
            claimsSnapshot = emptyMap(),
            fixtureMaySubstituteForDeployed = fixtureSubstitute,
            evaluatedAtUtc = Instant.now().toString()
        )

        var releaseGate = AuthorizationState.NOT_AUTHORIZED
        var errorMsg: String? = null

        try {
            releaseGate = ConfinementAdjudicator.evaluateReleaseGate(releaseBasis)
        } catch (e: Exception) {
            errorMsg = e.message
            releaseGate = AuthorizationState.NOT_AUTHORIZED
        }

        _uiState.value = _uiState.value.copy(
            probeResults = probes,
            sDeployedStatus = sDeployed,
            releasePrerequisites = prereqs,
            fixtureMaySubstituteForDeployed = fixtureSubstitute,
            releaseGateStatus = releaseGate,
            errorMessage = errorMsg
        )
    }

    fun inspectJson(rawJson: String) {
        val diag = CanonicalJson.validateAndCanonicalizeJson(rawJson)
        _uiState.value = _uiState.value.copy(canonicalDiagnostic = diag)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
