package com.example.osiris.core

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.delay

class ApiGatewayService {

    private var serverStatus = GatewayServerStatus()

    private val cacheEntries = mutableMapOf<String, ExtrapolativeCacheEntry>(
        "P1S_Alpha" to ExtrapolativeCacheEntry("P1S_Alpha", reportedProgressPercent = 0.0, extrapolatedProgressPercent = 0.0, printStatus = "IDLE"),
        "P1S_Beta" to ExtrapolativeCacheEntry("P1S_Beta", reportedProgressPercent = 68.0, extrapolatedProgressPercent = 68.0, printStatus = "RUNNING", remainingTimeMinutes = 42),
        "P1S_Gamma" to ExtrapolativeCacheEntry("P1S_Gamma", reportedProgressPercent = 14.0, extrapolatedProgressPercent = 14.0, printStatus = "RUNNING", remainingTimeMinutes = 110),
        "H2C_Omega" to ExtrapolativeCacheEntry("H2C_Omega", reportedProgressPercent = 0.0, extrapolatedProgressPercent = 0.0, printStatus = "IDLE")
    )

    private var latestOpticalRecord = OpticalInspectionRecord(
        inspectionId = "opt-scan-001",
        printerId = "P1S_Beta",
        spaghettiConfidencePercent = 2.1f,
        failureClass = OpticalFailureClass.NONE,
        failureDescription = "Nominal extrusion. First layer perimeter adhesion uniform.",
        mitigationAction = "MAINTAIN_TRAJECTORY"
    )

    private var latestRecoveryRecord: FailoverRecoveryRecord? = null

    fun getServerStatus(): GatewayServerStatus = serverStatus

    fun toggleServerPower(): GatewayServerStatus {
        serverStatus = serverStatus.copy(isOnline = !serverStatus.isOnline)
        return serverStatus
    }

    fun getCacheEntries(): List<ExtrapolativeCacheEntry> = cacheEntries.values.toList()

    fun toggleSimulatedBrownout(deviceId: String): ExtrapolativeCacheEntry {
        val current = cacheEntries[deviceId] ?: ExtrapolativeCacheEntry(deviceId)
        val nowBrownout = !current.isSimulatedBrownout
        val updated = if (nowBrownout) {
            // Dropouts trigger extrapolative cache
            val inferred = Math.min(99.0, current.reportedProgressPercent + 4.5)
            current.copy(
                isSimulatedBrownout = true,
                secondsSinceLastHeartbeat = 28.5,
                telemetryState = "DEGRADED_EXTRAPOLATING",
                extrapolatedProgressPercent = inferred,
                printStatus = "PRINTING (DEGRADED_TELEMETRY)"
            )
        } else {
            current.copy(
                isSimulatedBrownout = false,
                secondsSinceLastHeartbeat = 0.8,
                telemetryState = "HEALTHY_TLS",
                extrapolatedProgressPercent = current.reportedProgressPercent,
                printStatus = "RUNNING"
            )
        }
        cacheEntries[deviceId] = updated
        return updated
    }

    fun getLatestOpticalRecord(): OpticalInspectionRecord = latestOpticalRecord

    fun getLatestRecoveryRecord(): FailoverRecoveryRecord? = latestRecoveryRecord

    fun runOpticalInspection(printerId: String): OpticalInspectionRecord {
        latestOpticalRecord = OpticalInspectionRecord(
            inspectionId = "opt-scan-${UUID.randomUUID().toString().take(6)}",
            printerId = printerId,
            spaghettiConfidencePercent = 1.4f,
            failureClass = OpticalFailureClass.NONE,
            failureDescription = "Camera inspection clean: 0.20mm layer lines contiguous, no stringing or warping.",
            mitigationAction = "CONTINUE_PRINT",
            isFlaggedForIntervention = false
        )
        return latestOpticalRecord
    }

    fun injectSpaghettiIncident(printerId: String): OpticalInspectionRecord {
        latestOpticalRecord = OpticalInspectionRecord(
            inspectionId = "opt-scan-${UUID.randomUUID().toString().take(6)}",
            printerId = printerId,
            spaghettiConfidencePercent = 94.8f,
            failureClass = OpticalFailureClass.SPAGHETTI_DETECTION,
            failureDescription = "CRITICAL: Filament detachment detected at layer 42/120. Air-printing cluster forming.",
            mitigationAction = "HALT_TOOLHEAD_AND_FAILOVER",
            isFlaggedForIntervention = true,
            boundingBoxLabel = "SPAGHETTI_CLUSTER_94.8% [X:112, Y:84, W:45, H:38]"
        )
        return latestOpticalRecord
    }

    fun executeAutomatedFailover(
        failedPrinterId: String = "P1S_Beta",
        rescuePrinterId: String = "P1S_Gamma",
        evidenceHash: String = "sha256:optical_incident_l42"
    ): FailoverRecoveryRecord {
        val record = FailoverRecoveryRecord(
            recoveryId = "recov-${UUID.randomUUID().toString().take(6)}",
            failedPrinterId = failedPrinterId,
            rescuePrinterId = rescuePrinterId,
            failureLayer = 42,
            totalLayers = 120,
            salvagedGcodeOffsetBytes = 412950L,
            failoverStatus = "COMPLETED",
            timestampUtc = Instant.now().toString(),
            ledgerEvidenceHash = evidenceHash
        )
        latestRecoveryRecord = record

        // Update optical record state
        latestOpticalRecord = latestOpticalRecord.copy(
            mitigationAction = "FAILOVER_EXECUTED_TO_$rescuePrinterId",
            isFlaggedForIntervention = false
        )
        return record
    }

    suspend fun dispatchViaFastApi(payloadJson: String): Map<String, Any> {
        delay(400)
        serverStatus = serverStatus.copy(
            totalRequestsHandled = serverStatus.totalRequestsHandled + 1
        )
        return mapOf(
            "status" to "QUEUED_FASTAPI",
            "task_id" to "bg_task_${UUID.randomUUID().toString().take(8)}",
            "worker_thread" to "worker_pool_0",
            "cli_invocation" to "bambu-studio --slice 0 --export-3mf ...",
            "timestamp" to Instant.now().toString()
        )
    }
}
