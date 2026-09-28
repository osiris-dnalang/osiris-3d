package com.example.osiris.core

import kotlinx.serialization.Serializable

@Serializable
data class GatewayServerStatus(
    val host: String = "127.0.0.1",
    val port: Int = 8000,
    val isOnline: Boolean = true,
    val activeWorkerThreads: Int = 4,
    val totalRequestsHandled: Int = 184,
    val uptimeSeconds: Long = 14200,
    val openApiEndpoint: String = "/api/v1/openapi.json",
    val backgroundTasksQueued: Int = 0
)

@Serializable
data class ExtrapolativeCacheEntry(
    val deviceId: String,
    val isSimulatedBrownout: Boolean = false,
    val secondsSinceLastHeartbeat: Double = 0.0,
    val printStatus: String = "RUNNING",
    val telemetryState: String = "HEALTHY_TLS", // "HEALTHY_TLS", "DEGRADED_EXTRAPOLATING", "OFFLINE"
    val reportedProgressPercent: Double = 68.0,
    val extrapolatedProgressPercent: Double = 68.0,
    val remainingTimeMinutes: Int = 42
)

@Serializable
enum class OpticalFailureClass {
    NONE,
    SPAGHETTI_DETECTION,
    FIRST_LAYER_DELAMINATION,
    NOZZLE_CLOG_UNDEREXTRUSION,
    LAYER_SHIFT_XY
}

@Serializable
data class OpticalInspectionRecord(
    val inspectionId: String,
    val printerId: String,
    val cameraFps: Int = 30,
    val spaghettiConfidencePercent: Float = 0.0f,
    val failureClass: OpticalFailureClass = OpticalFailureClass.NONE,
    val failureDescription: String = "First layer uniform adhesion verified. Zero thermal warping.",
    val mitigationAction: String = "CONTINUE_PRINT",
    val isFlaggedForIntervention: Boolean = false,
    val boundingBoxLabel: String? = null
)

@Serializable
data class FailoverRecoveryRecord(
    val recoveryId: String,
    val failedPrinterId: String,
    val rescuePrinterId: String,
    val failureLayer: Int,
    val totalLayers: Int,
    val salvagedGcodeOffsetBytes: Long,
    val failoverStatus: String = "COMPLETED", // "INITIATED", "PARKING_HEAD", "GCODE_SLICED_AT_OFFSET", "COMPLETED"
    val timestampUtc: String,
    val ledgerEvidenceHash: String
)
