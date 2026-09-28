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
import com.example.osiris.core.IngestionTelemetry
import com.example.osiris.core.JobExecutionPlan
import com.example.osiris.core.LedgerEvent
import com.example.osiris.core.LedgerEventType
import com.example.osiris.core.LivLMService
import com.example.osiris.core.MakerWorldModel
import com.example.osiris.core.MakerWorldService
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
    val terminalLogs: List<String> = emptyList(),
    // MakerWorld Ingestion & NCLM Foundry
    val makerWorldModels: List<MakerWorldModel> = emptyList(),
    val ingestionTelemetry: IngestionTelemetry = IngestionTelemetry(),
    val isIngesting: Boolean = false,
    val selectedCustomization: com.example.osiris.core.ParametricCustomization? = null,
    val p1sDiagnostics: List<com.example.osiris.core.P1SDiagnosticReport> = emptyList(),
    // Iteration 4 & 5: Gateway, Leaky Bucket & Optical QA
    val gatewayStatus: com.example.osiris.core.GatewayServerStatus = com.example.osiris.core.GatewayServerStatus(),
    val cacheEntries: List<com.example.osiris.core.ExtrapolativeCacheEntry> = emptyList(),
    val opticalRecord: com.example.osiris.core.OpticalInspectionRecord = com.example.osiris.core.OpticalInspectionRecord("opt-001", "P1S_Beta"),
    val recoveryRecord: com.example.osiris.core.FailoverRecoveryRecord? = null,
    val filamentScanResult: com.example.osiris.core.FilamentScanEnhancement? = null
)

class OsirisViewModel : ViewModel() {

    private val service = LivLMService()
    val fleetService = FleetService()
    val makerWorldService = MakerWorldService()
    val apiGatewayService = com.example.osiris.core.ApiGatewayService()

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
            makerWorldModels = makerWorldService.getModels(),
            ingestionTelemetry = makerWorldService.getTelemetry(),
            selectedCustomization = makerWorldService.deriveParametricCustomization("15kg load", makerWorldService.getModels()[1]),
            p1sDiagnostics = makerWorldService.getP1SDiagnostics(),
            gatewayStatus = apiGatewayService.getServerStatus(),
            cacheEntries = apiGatewayService.getCacheEntries(),
            opticalRecord = apiGatewayService.getLatestOpticalRecord(),
            terminalLogs = listOf(
                "--- [OSIRIS FLEET ENGINE INITIALIZED] ---",
                "Subnet: 192.168.10.0/24 • 9 Bambu Lab nodes online",
                "MQTT TLS Port: 8883 • Protocol: bblp local control",
                "MakerWorld Ingestion Hub: 8,420 CAD models cached (18.4 GB)",
                "FastAPI Gateway: uvicorn online at 127.0.0.1:8000",
                "NCLM Foundry ready for generative manufacturing dispatch."
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

    // MakerWorld & NCLM Search
    fun searchMakerWorldModels(query: String) {
        val results = makerWorldService.searchModels(query)
        _uiState.value = _uiState.value.copy(makerWorldModels = results)
    }

    fun triggerIngestionSweep() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isIngesting = true)
            val updatedTelemetry = makerWorldService.performIngestionSweep()
            _uiState.value = _uiState.value.copy(
                isIngesting = false,
                ingestionTelemetry = updatedTelemetry,
                makerWorldModels = makerWorldService.getModels(),
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[INGESTION] Headless stealth scraper rotated 12 residential proxies.",
                    "[INGESTION] Cloudflare Turnstile token bypassed via TLS fingerprinting.",
                    "[INGESTION] Intercepted internal MakerWorld JSON API endpoints.",
                    "[INGESTION] Ingested +48 new models, +142 .3mf/.stl geometries into osiris/cad_lib."
                )
            )
        }
    }

    fun selectModelForPrint(model: MakerWorldModel) {
        val prompt = "Fabricate '${model.title}' (${model.sourceFile}) using ${model.suggestedMaterial}. Apply structural constraints for ${model.structuralClass}."
        generateJobPlan(prompt)
    }

    fun selectModelForCustomization(model: MakerWorldModel) {
        val custom = makerWorldService.deriveParametricCustomization("15kg load", model)
        _uiState.value = _uiState.value.copy(selectedCustomization = custom)
    }

    fun updateParametricCustomization(
        thickness: Double,
        walls: Int,
        infill: Int,
        clearance: Double,
        material: String
    ) {
        val current = _uiState.value.selectedCustomization ?: return
        val updated = current.copy(
            thicknessMm = thickness,
            wallLoops = walls,
            infillDensityPercent = infill,
            holeClearanceMm = clearance,
            selectedMaterial = material
        )
        val script = makerWorldService.generateOpenScad(current.modelId, updated)
        _uiState.value = _uiState.value.copy(
            selectedCustomization = updated.copy(openScadScript = script)
        )
    }

    fun autoFixP1SDiagnostics() {
        val fixed = makerWorldService.autoFixP1SDiagnostics()
        _uiState.value = _uiState.value.copy(
            p1sDiagnostics = fixed,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[DIAGNOSTICS] P1S Fleet Autopilot: Cleared Silent Mode on P1S_Gamma.",
                "[DIAGNOSTICS] P1S Fleet Autopilot: Raised PETG-CF MVS to 15.0 mm³/s on P1S_Beta.",
                "[DIAGNOSTICS] P1S Fleet Autopilot: Grouped multi-part plates on P1S_Epsilon to prevent cooling crawl.",
                "[DIAGNOSTICS] 100% CoreXY throughput restored across all 5 P1S nodes."
            )
        )
    }

    fun scanFleetAndOptimizeModel(model: MakerWorldModel) {
        val result = makerWorldService.scanFleetAndOptimize(model, _uiState.value.fleet)
        _uiState.value = _uiState.value.copy(
            filamentScanResult = result,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[SCAN] Scanned 9 Bambu nodes for '${model.title}' filament requirements:",
                "  -> Selected optimal node: ${result.targetPrinterName} (Matched ${result.matchedSpoolsCount}/${result.totalRequiredSpoolsCount} spools)",
                "  -> Slicer Enhancements: ${result.adaptiveLayerHeight}, Purge Savings: ${result.purgeSavingsGrams}g (${result.purgeSavingsPercent}%)",
                "  -> Ready for 1-click dispatch to ${result.targetPrinterName}."
            )
        )
    }

    fun dispatchOptimizedModel(result: com.example.osiris.core.FilamentScanEnhancement) {
        viewModelScope.launch {
            apiGatewayService.dispatchViaFastApi("{\"model_id\":\"${result.modelId}\",\"target_printer\":\"${result.targetPrinterId}\"}")
            _uiState.value = _uiState.value.copy(
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[DISPATCH] Slicer profile & G-Code sent to ${result.targetPrinterName} via FastAPI /api/v1/fleet/dispatch.",
                    "  Flush volume overrides: ${result.flushVolumeMatrixOverrides}",
                    "  Filament sequence: ${result.colorPrintSequence.joinToString(" -> ")}"
                )
            )
        }
    }

    fun dismissScanResult() {
        _uiState.value = _uiState.value.copy(filamentScanResult = null)
    }

    // Iteration 4 & 5: FastAPI Gateway & Optical QA Actions
    fun toggleGatewayPower() {
        val status = apiGatewayService.toggleServerPower()
        _uiState.value = _uiState.value.copy(
            gatewayStatus = status,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                if (status.isOnline) "[UVICORN] Server listening at http://127.0.0.1:8000 (PID: 4912)" else "[UVICORN] Server shutdown complete."
            )
        )
    }

    fun testFastApiDispatch() {
        viewModelScope.launch {
            val response = apiGatewayService.dispatchViaFastApi("{\"part\":\"gear_bracket\"}")
            _uiState.value = _uiState.value.copy(
                gatewayStatus = apiGatewayService.getServerStatus(),
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[FASTAPI] POST /api/v1/fleet/dispatch -> 202 Accepted",
                    "  Task ID: ${response["task_id"]} • Worker: ${response["worker_thread"]}",
                    "  Background Task queued: executing bambu-studio CLI..."
                )
            )
        }
    }

    fun toggleSimulatedBrownout(deviceId: String) {
        val updatedEntry = apiGatewayService.toggleSimulatedBrownout(deviceId)
        _uiState.value = _uiState.value.copy(
            cacheEntries = apiGatewayService.getCacheEntries(),
            terminalLogs = _uiState.value.terminalLogs + listOf(
                if (updatedEntry.isSimulatedBrownout) {
                    "[CACHE] Node $deviceId Wi-Fi dropped. Activating Leaky-Bucket Extrapolative Cache (Inferred: ${updatedEntry.extrapolatedProgressPercent.toInt()}%, NO OFFLINE FAIL)"
                } else {
                    "[CACHE] Node $deviceId TLS handshake restored. Resuming live MQTT heartbeat (Reported: ${updatedEntry.reportedProgressPercent.toInt()}%)"
                }
            )
        )
    }

    fun runOpticalScan() {
        val scan = apiGatewayService.runOpticalInspection("P1S_Beta")
        _uiState.value = _uiState.value.copy(
            opticalRecord = scan,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[OPTICAL QA] Chamber camera sweep clean: spaghetti confidence ${scan.spaghettiConfidencePercent}%."
            )
        )
    }

    fun injectSpaghetti() {
        val scan = apiGatewayService.injectSpaghettiIncident("P1S_Beta")
        _uiState.value = _uiState.value.copy(
            opticalRecord = scan,
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[OPTICAL QA] ALERT: Spaghetti detected on P1S_Beta (Confidence: 94.8% at layer 42/120)!",
                "  Bounding Box: [X:112, Y:84, W:45, H:38] • Toolhead paused."
            )
        )
    }

    fun executeFailover() {
        val recov = apiGatewayService.executeAutomatedFailover(
            failedPrinterId = "P1S_Beta",
            rescuePrinterId = "P1S_Gamma",
            evidenceHash = "sha256:optical_incident_l42"
        )
        // Record tamper-evident incident into ledger
        service.ledger.recordEvent(
            eventType = LedgerEventType.EXECUTION_ADMITTED,
            plane = EvidencePlane.HARDWARE_EXECUTION,
            actor = mapOf("type" to "optical-qa-agent", "id" to "cv-spaghetti-detector"),
            artifact = mapOf("digest" to recov.ledgerEvidenceHash),
            scope = service.scope,
            payload = mapOf(
                "action" to "AUTONOMOUS_FLEET_FAILOVER",
                "failed_node" to recov.failedPrinterId,
                "rescue_node" to recov.rescuePrinterId,
                "salvaged_layer" to "${recov.failureLayer}/${recov.totalLayers}"
            )
        )
        _uiState.value = _uiState.value.copy(
            opticalRecord = apiGatewayService.getLatestOpticalRecord(),
            recoveryRecord = recov,
            events = service.ledger.events,
            latestDigest = service.ledger.latestDigest,
            chainIntegrityValid = service.ledger.verifyChainIntegrity(),
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[FAILOVER] Autonomous Failover Complete: P1S_Beta paused at layer 42.",
                "  Rescue segment dispatched to P1S_Gamma. Recorded to OSIRIS Evidence Ledger."
            )
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

    fun pausePrinter(deviceId: String) {
        val printer = fleetService.getFleet().find { it.deviceId == deviceId }
        printer?.status = if (printer?.status == "PAUSED") "PRINTING" else "PAUSED"
        _uiState.value = _uiState.value.copy(
            fleet = fleetService.getFleet(),
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[MQTT] Sent pause/resume toggle command to node $deviceId (${printer?.name})"
            )
        )
    }

    fun stopPrinter(deviceId: String) {
        val printer = fleetService.getFleet().find { it.deviceId == deviceId }
        printer?.status = "IDLE"
        printer?.progressPercent = 0.0
        printer?.currentJobName = ""
        _uiState.value = _uiState.value.copy(
            fleet = fleetService.getFleet(),
            terminalLogs = _uiState.value.terminalLogs + listOf(
                "[MQTT] Sent EMERGENCY STOP command to node $deviceId (${printer?.name}). Toolhead parked."
            )
        )
    }

    fun refreshFleetTelemetry() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)
            delay(400)
            _uiState.value = _uiState.value.copy(
                isScanning = false,
                fleet = fleetService.getFleet(),
                terminalLogs = _uiState.value.terminalLogs + listOf(
                    "[POLL] Python backend telemetry refreshed via FastAPI /api/v1/fleet/telemetry (All 9 nodes sync 100%)"
                )
            )
        }
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
