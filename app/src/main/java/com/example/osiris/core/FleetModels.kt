package com.example.osiris.core

import kotlinx.serialization.Serializable

@Serializable
data class FilamentProperties(
    val materialType: String, // "PETG-CF", "PLA_BASIC", "ABS", "ASA", "TPU"
    val tensileStrengthMpa: Double,
    val flexuralModulusGpa: Double,
    val hdt045Mpa: Double,
    val nozzleTempMin: Int,
    val nozzleTempMax: Int,
    val bedTemp: Int,
    val maxVolumetricSpeedMm3s: Double,
    val shrinkageFactor: Double
)

@Serializable
data class FilamentInventoryNode(
    val spoolId: String,
    val properties: FilamentProperties,
    val colorHex: String,
    val colorName: String,
    val remainingWeightG: Double,
    val rfidTag: String? = null
)

@Serializable
data class AMSUnit(
    val amsId: String,
    val unitIndex: Int,
    val slots: Map<Int, FilamentInventoryNode?>, // 1..4
    val humidityIndex: Int = 1
)

@Serializable
data class PrinterNode(
    val deviceId: String,
    val name: String,
    val model: String, // "P1S", "A1", "A1_Mini", "H2C"
    val ipAddress: String,
    val accessCode: String,
    val enclosureType: String, // "ENCLOSED", "OPEN"
    val nozzleSizeMm: Double,
    val nozzleMaterial: String, // "Hardened Steel", "Stainless Steel"
    val amsUnits: List<AMSUnit>,
    var status: String = "IDLE", // "IDLE", "PRINTING", "OFFLINE", "ERROR", "PAUSED"
    var currentJobId: String? = null,
    var nozzleTemp: Double = 0.0,
    var bedTemp: Double = 0.0,
    var chamberTemp: Double? = null,
    var progressPercent: Double = 0.0,
    var remainingTimeM: Int = 0,
    var isSelected: Boolean = true,
    var isOnline: Boolean = true
)

@Serializable
data class SlicingModifiers(
    val wallLoops: Int,
    val infillPattern: String,
    val infillDensityPercent: Int,
    val topShellLayers: Int,
    val bottomShellLayers: Int,
    val layerHeightMm: Double,
    val partCoolingFanMinPercent: Int,
    val partCoolingFanMaxPercent: Int
)

@Serializable
data class PartExecutionSpec(
    val partId: String,
    val partName: String,
    val sourceAsset: String,
    val resolvedMaterial: String,
    val spoolId: String,
    val colorHex: String,
    val colorName: String,
    val tensileStrengthMpa: Double,
    val flexuralModulusGpa: Double,
    val substitutionRationale: String,
    val modifiers: SlicingModifiers,
    val cliArguments: List<String>,
    val targetPrinterId: String,
    val targetPrinterModel: String,
    val targetEnclosure: String,
    val targetAmsSlot: Int,
    val requiresOperatorLoad: Boolean = false
)

@Serializable
data class JobExecutionPlan(
    val jsonrpc: String = "2.0",
    val method: String = "osiris.fleet.dispatch_execution_plan",
    val planId: String,
    val projectName: String,
    val timestampUtc: String,
    val rawRequest: String,
    val loadBearingClass: String,
    val primaryFailureMode: String,
    val aestheticPreference: String,
    val timeConstraint: String,
    val parts: List<PartExecutionSpec>,
    val verifiedNodesCount: Int,
    val estimatedParallelRuntimeS: Int,
    val totalFilamentConsumptionG: Double,
    val governanceStatus: String = "GOVERNANCE_ADMITTED"
)

enum class PipelineStage {
    IDLE,
    PHASE_1_REFINEMENT,
    PHASE_2_ROUTING,
    PHASE_3_OPERATOR_ACTION,
    PHASE_4_DISPATCH,
    COMPLETED
}

data class OperatorActionPrompt(
    val partId: String,
    val partName: String,
    val requiredMaterial: String,
    val requiredColor: String,
    val targetPrinterId: String,
    val targetPrinterName: String,
    val targetSlot: Int,
    val instruction: String,
    var isConfirmed: Boolean = false
)
