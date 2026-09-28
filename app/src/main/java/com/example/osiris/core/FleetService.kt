package com.example.osiris.core

import java.time.Instant
import java.util.UUID

class FleetService {

    val filamentRegistry: Map<String, FilamentProperties> = mapOf(
        "PETG-CF" to FilamentProperties(
            materialType = "PETG-CF",
            tensileStrengthMpa = 63.8,
            flexuralModulusGpa = 4.7,
            hdt045Mpa = 74.0,
            nozzleTempMin = 240,
            nozzleTempMax = 270,
            bedTemp = 70,
            maxVolumetricSpeedMm3s = 14.5,
            shrinkageFactor = 0.003
        ),
        "PLA_BASIC" to FilamentProperties(
            materialType = "PLA_BASIC",
            tensileStrengthMpa = 45.0,
            flexuralModulusGpa = 2.8,
            hdt045Mpa = 53.0,
            nozzleTempMin = 190,
            nozzleTempMax = 230,
            bedTemp = 55,
            maxVolumetricSpeedMm3s = 21.0,
            shrinkageFactor = 0.002
        ),
        "ABS" to FilamentProperties(
            materialType = "ABS",
            tensileStrengthMpa = 40.0,
            flexuralModulusGpa = 2.4,
            hdt045Mpa = 86.0,
            nozzleTempMin = 240,
            nozzleTempMax = 270,
            bedTemp = 90,
            maxVolumetricSpeedMm3s = 16.0,
            shrinkageFactor = 0.008
        ),
        "ASA" to FilamentProperties(
            materialType = "ASA",
            tensileStrengthMpa = 42.0,
            flexuralModulusGpa = 2.5,
            hdt045Mpa = 88.0,
            nozzleTempMin = 240,
            nozzleTempMax = 270,
            bedTemp = 95,
            maxVolumetricSpeedMm3s = 15.0,
            shrinkageFactor = 0.007
        ),
        "TPU_95A" to FilamentProperties(
            materialType = "TPU_95A",
            tensileStrengthMpa = 35.0,
            flexuralModulusGpa = 0.2,
            hdt045Mpa = 45.0,
            nozzleTempMin = 210,
            nozzleTempMax = 240,
            bedTemp = 40,
            maxVolumetricSpeedMm3s = 6.0,
            shrinkageFactor = 0.004
        )
    )

    private val printers = mutableListOf<PrinterNode>()

    init {
        initializeFleet()
    }

    private fun initializeFleet() {
        // 5x P1S
        val p1sModels = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        p1sModels.forEachIndexed { index, name ->
            val ams = AMSUnit(
                amsId = "AMS_P1S_$name",
                unitIndex = 1,
                slots = mapOf(
                    1 to FilamentInventoryNode("spool_pla_blk_$index", filamentRegistry["PLA_BASIC"]!!, "#111111", "Black", 650.0),
                    2 to null, // Slot 2 empty initially, ready for operator loading
                    3 to FilamentInventoryNode("spool_petg_wht_$index", filamentRegistry["PETG-CF"]!!, "#FFFFFF", "White", 820.0),
                    4 to FilamentInventoryNode("spool_abs_gry_$index", filamentRegistry["ABS"]!!, "#888888", "Gray", 430.0)
                )
            )
            printers.add(
                PrinterNode(
                    deviceId = "01P00A00000${index + 1}",
                    name = "P1S_$name",
                    model = "P1S",
                    ipAddress = "192.168.10.${41 + index}",
                    accessCode = "bblp_${1000 + index}",
                    enclosureType = "ENCLOSED",
                    nozzleSizeMm = 0.4,
                    nozzleMaterial = "Hardened Steel",
                    amsUnits = listOf(ams),
                    status = if (index == 0) "IDLE" else if (index == 1) "PRINTING" else if (index == 2) "PRINTING" else if (index == 3) "PRINTING" else "IDLE",
                    nozzleTemp = if (index == 1) 252.4 else if (index == 2) 259.0 else 24.0,
                    targetNozzleTemp = if (index == 1) 255.0 else if (index == 2) 260.0 else 0.0,
                    bedTemp = if (index == 1) 70.0 else if (index == 2) 94.8 else 22.0,
                    targetBedTemp = if (index == 1) 70.0 else if (index == 2) 95.0 else 0.0,
                    chamberTemp = if (index == 1) 38.5 else if (index == 2) 48.0 else 24.0,
                    fanSpeedPercent = if (index == 1) 35 else if (index == 2) 20 else 0,
                    volumetricFlowMm3s = if (index == 1) 14.2 else if (index == 2) 15.8 else 0.0,
                    progressPercent = if (index == 1) 68.0 else if (index == 2) 14.0 else 0.0,
                    remainingTimeM = if (index == 1) 42 else if (index == 2) 110 else 0,
                    currentLayer = if (index == 1) 82 else if (index == 2) 18 else 0,
                    totalLayers = if (index == 1) 120 else if (index == 2) 130 else 0,
                    printSpeedMmS = if (index == 1) 220 else if (index == 2) 250 else 0,
                    currentJobName = if (index == 1) "gear_bracket_hardened.3mf" else if (index == 2) "rugged_box_ip67.3mf" else "",
                    wifiRssiDbm = -48 - index * 3,
                    telemetryHealth = if (index == 3) "DEGRADED" else "HEALTHY"
                )
            )
        }

        // 2x A1
        listOf("Zeta", "Eta").forEachIndexed { index, name ->
            val ams = AMSUnit(
                amsId = "AMS_Lite_$name",
                unitIndex = 1,
                slots = mapOf(
                    1 to FilamentInventoryNode("spool_pla_blu_$index", filamentRegistry["PLA_BASIC"]!!, "#0284C7", "Blue", 750.0),
                    2 to FilamentInventoryNode("spool_pla_red_$index", filamentRegistry["PLA_BASIC"]!!, "#EF4444", "Red", 550.0),
                    3 to FilamentInventoryNode("spool_pla_yel_$index", filamentRegistry["PLA_BASIC"]!!, "#EAB308", "Yellow", 900.0),
                    4 to FilamentInventoryNode("spool_pla_grn_$index", filamentRegistry["PLA_BASIC"]!!, "#10B981", "Green", 410.0)
                )
            )
            printers.add(
                PrinterNode(
                    deviceId = "02A00B00000${index + 1}",
                    name = "A1_$name",
                    model = "A1",
                    ipAddress = "192.168.10.${46 + index}",
                    accessCode = "bblp_${2000 + index}",
                    enclosureType = "OPEN",
                    nozzleSizeMm = 0.4,
                    nozzleMaterial = "Stainless Steel",
                    amsUnits = listOf(ams),
                    status = if (index == 1) "OFFLINE" else "IDLE",
                    isOnline = index != 1,
                    telemetryHealth = if (index == 1) "OFFLINE" else "HEALTHY",
                    nozzleTemp = if (index == 1) 0.0 else 23.0,
                    bedTemp = if (index == 1) 0.0 else 21.0
                )
            )
        }

        // 1x A1 Mini
        val amsMini = AMSUnit(
            amsId = "AMS_Lite_Mini",
            unitIndex = 1,
            slots = mapOf(
                1 to FilamentInventoryNode("spool_pla_clr_01", filamentRegistry["PLA_BASIC"]!!, "#E5E5E5", "Clear", 480.0),
                2 to FilamentInventoryNode("spool_pla_blk_mini", filamentRegistry["PLA_BASIC"]!!, "#111111", "Black", 320.0),
                3 to null,
                4 to null
            )
        )
        printers.add(
            PrinterNode(
                deviceId = "03A00C000001",
                name = "A1_Mini_Theta",
                model = "A1_Mini",
                ipAddress = "192.168.10.48",
                accessCode = "bblp_3001",
                enclosureType = "OPEN",
                nozzleSizeMm = 0.4,
                nozzleMaterial = "Stainless Steel",
                amsUnits = listOf(amsMini),
                status = "IDLE",
                nozzleTemp = 24.0,
                bedTemp = 22.0
            )
        )

        // 1x H2C Flagship with 4x AMS = 16 channels!
        val h2cAmsUnits = (1..4).map { unitIdx ->
            AMSUnit(
                amsId = "H2C_AMS_Unit_$unitIdx",
                unitIndex = unitIdx,
                slots = (1..4).associateWith { slotIdx ->
                    val colorHex = when ((unitIdx - 1) * 4 + slotIdx) {
                        1 -> "#111111"; 2 -> "#FFFFFF"; 3 -> "#EF4444"; 4 -> "#3B82F6"
                        5 -> "#10B981"; 6 -> "#F59E0B"; 7 -> "#8B5CF6"; 8 -> "#EC4899"
                        9 -> "#14B8A6"; 10 -> "#F97316"; 11 -> "#64748B"; 12 -> "#A855F7"
                        else -> "#D97706"
                    }
                    FilamentInventoryNode(
                        spoolId = "h2c_spool_ch_${(unitIdx - 1) * 4 + slotIdx}",
                        properties = filamentRegistry["PLA_BASIC"]!!,
                        colorHex = colorHex,
                        colorName = "Ch ${(unitIdx - 1) * 4 + slotIdx}",
                        remainingWeightG = 780.0
                    )
                }
            )
        }
        printers.add(
            PrinterNode(
                deviceId = "09H00Z000001",
                name = "H2C_Omega",
                model = "H2C",
                ipAddress = "192.168.10.50",
                accessCode = "bblp_9001",
                enclosureType = "ENCLOSED",
                nozzleSizeMm = 0.4,
                nozzleMaterial = "Hardened Steel",
                amsUnits = h2cAmsUnits,
                status = "PRINTING",
                nozzleTemp = 219.0,
                targetNozzleTemp = 220.0,
                bedTemp = 55.0,
                targetBedTemp = 55.0,
                chamberTemp = 32.0,
                fanSpeedPercent = 90,
                volumetricFlowMm3s = 18.5,
                progressPercent = 43.0,
                remainingTimeM = 176,
                currentLayer = 134,
                totalLayers = 310,
                printSpeedMmS = 280,
                currentJobName = "cyber_dragon_ams.3mf",
                wifiRssiDbm = -42
            )
        )
    }

    fun getFleet(): List<PrinterNode> = printers.toList()

    fun togglePrinterSelection(deviceId: String) {
        val printer = printers.find { it.deviceId == deviceId }
        printer?.let { it.isSelected = !it.isSelected }
    }

    fun selectAllPrinters(selected: Boolean) {
        printers.forEach { it.isSelected = selected }
    }

    fun loadFilamentIntoSlot(printerName: String, slotNumber: Int, materialType: String, colorName: String, colorHex: String) {
        val printer = printers.find { it.name == printerName } ?: return
        val ams = printer.amsUnits.firstOrNull() ?: return
        val prop = filamentRegistry[materialType] ?: filamentRegistry["PETG-CF"]!!
        val newSpool = FilamentInventoryNode(
            spoolId = "spool_loaded_${materialType.lowercase()}_${System.currentTimeMillis() % 1000}",
            properties = prop,
            colorHex = colorHex,
            colorName = colorName,
            remainingWeightG = 1000.0
        )
        val updatedSlots = ams.slots.toMutableMap()
        updatedSlots[slotNumber] = newSpool
        val updatedAms = ams.copy(slots = updatedSlots)
        val updatedUnits = printer.amsUnits.toMutableList()
        updatedUnits[0] = updatedAms
        val idx = printers.indexOf(printer)
        if (idx >= 0) {
            printers[idx] = printer.copy(amsUnits = updatedUnits)
        }
    }

    fun buildJobExecutionPlan(
        userPrompt: String,
        projectName: String = "FPV Racing Drone v2"
    ): JobExecutionPlan {
        val isHeavyDuty = userPrompt.contains("heavy duty", ignoreCase = true) ||
                userPrompt.contains("shatter", ignoreCase = true) ||
                userPrompt.contains("load", ignoreCase = true) ||
                userPrompt.contains("impact", ignoreCase = true)

        val frameMaterial = if (isHeavyDuty) "PETG-CF" else "PLA_BASIC"
        val isGearBracket = userPrompt.contains("gear", ignoreCase = true) || userPrompt.contains("bracket", ignoreCase = true) || userPrompt.contains("15kg", ignoreCase = true)

        val frameWalls = if (isGearBracket) 6 else if (isHeavyDuty) 5 else 3
        val frameInfill = if (isGearBracket) 25 else if (isHeavyDuty) 28 else 18
        val frameLayerHeight = if (isGearBracket) 0.24 else 0.20

        // Automated Z-Axis Load Vector Reorientation:
        // Keeps the primary tensile vector perpendicular to Z layer lines
        val rotX = if (isHeavyDuty || isGearBracket) 0.0 else 0.0
        val rotY = if (isHeavyDuty || isGearBracket) 90.0 else 0.0
        val rotZ = 0.0

        val frameModifiers = SlicingModifiers(
            wallLoops = frameWalls,
            infillPattern = "gyroid",
            infillDensityPercent = frameInfill,
            topShellLayers = 4,
            bottomShellLayers = 4,
            layerHeightMm = frameLayerHeight,
            partCoolingFanMinPercent = 20,
            partCoolingFanMaxPercent = 50,
            rotateXDeg = rotX,
            rotateYDeg = rotY,
            rotateZDeg = rotZ,
            loadBearingAreaMm2 = 11.04,
            safetyFactor = 3.0
        )

        val propGuardModifiers = SlicingModifiers(
            wallLoops = 3,
            infillPattern = "gyroid",
            infillDensityPercent = 15,
            topShellLayers = 3,
            bottomShellLayers = 3,
            layerHeightMm = 0.20,
            partCoolingFanMinPercent = 80,
            partCoolingFanMaxPercent = 100
        )

        // Check if P1S Alpha has PETG-CF in Slot 2
        val p1s = printers.find { it.name == "P1S_Alpha" }
        val p1sSlot2 = p1s?.amsUnits?.firstOrNull()?.slots?.get(2)
        val p1sNeedsLoading = p1sSlot2 == null || p1sSlot2.properties.materialType != frameMaterial

        val frameSpec = PartExecutionSpec(
            partId = "part_96530cd5",
            partName = "Drone Frame",
            sourceAsset = "models/drone_frame.3mf",
            resolvedMaterial = frameMaterial,
            spoolId = "bambu_petg_cf_blk_004",
            colorHex = "#111111",
            colorName = "Black",
            tensileStrengthMpa = 63.8,
            flexuralModulusGpa = 4.7,
            substitutionRationale = "Upgraded to PETG-CF (63.8 MPa tensile, 4.7 GPa flexural modulus) to prevent brittle fracture on high-speed kinetic impact.",
            modifiers = frameModifiers,
            cliArguments = listOf(
                "--slice", "0",
                "--export-3mf", "build/output/drone_frame_hardened.gcode.3mf",
                "--load-settings", "profiles/materials/Bambu_PETG_CF_Hardened_0.4.json",
                "--load-overrides", "{\"wall_loops\":$frameWalls,\"sparse_infill_density\":\"$frameInfill%\",\"sparse_infill_pattern\":\"gyroid\"}"
            ),
            targetPrinterId = "P1S_Alpha",
            targetPrinterModel = "P1S",
            targetEnclosure = "ENCLOSED",
            targetAmsSlot = 2,
            requiresOperatorLoad = p1sNeedsLoading
        )

        val guardsSpec = PartExecutionSpec(
            partId = "part_65412e28",
            partName = "Propeller Guards",
            sourceAsset = "models/prop_guards.stl",
            resolvedMaterial = "PLA_BASIC",
            spoolId = "bambu_pla_clear_012",
            colorHex = "#E5E5E5",
            colorName = "Clear",
            tensileStrengthMpa = 45.0,
            flexuralModulusGpa = 2.8,
            substitutionRationale = "Non-structural energy dampening, lightweight high-speed parallel print.",
            modifiers = propGuardModifiers,
            cliArguments = listOf(
                "--slice", "0",
                "--export-3mf", "build/output/prop_guards_fast.gcode.3mf",
                "--load-settings", "profiles/materials/Bambu_PLA_Basic_Stainless_0.4.json",
                "--load-overrides", "{\"wall_loops\":3,\"sparse_infill_density\":\"15%\",\"sparse_infill_pattern\":\"gyroid\"}"
            ),
            targetPrinterId = "A1_Mini_Theta",
            targetPrinterModel = "A1_Mini",
            targetEnclosure = "OPEN",
            targetAmsSlot = 1,
            requiresOperatorLoad = false
        )

        return JobExecutionPlan(
            planId = "job_plan_${UUID.randomUUID().toString().take(8)}",
            projectName = projectName,
            timestampUtc = Instant.now().toString(),
            rawRequest = userPrompt,
            loadBearingClass = if (isHeavyDuty) "HIGH_IMPACT_CYCLIC_SHOCK" else "STANDARD_PROTOTYPE",
            primaryFailureMode = "inter_layer_delamination_and_shear",
            aestheticPreference = "STEALTH_BLACK_AND_CLEAR",
            timeConstraint = "PARALLEL_BALANCED_DISPATCH",
            parts = listOf(frameSpec, guardsSpec),
            verifiedNodesCount = printers.count { it.isOnline && it.isSelected },
            estimatedParallelRuntimeS = 5820,
            totalFilamentConsumptionG = 142.6
        )
    }

    fun calculateFlushVolume(fromMaterial: String, fromHex: String, toMaterial: String, toHex: String): Double {
        var flush = 100.0
        if (fromMaterial != toMaterial) {
            flush += 250.0 // Chemical contamination penalty
        }
        val lumFrom = hexToLuminance(fromHex)
        val lumTo = hexToLuminance(toHex)
        val delta = lumTo - lumFrom
        if (delta > 0.3) {
            flush += 250.0 * delta // Dark to light heavy flush
        } else if (delta < 0) {
            flush -= 20.0
        }
        return Math.max(flush, 100.0)
    }

    private fun hexToLuminance(hex: String): Double {
        val clean = hex.removePrefix("#")
        if (clean.length < 6) return 0.5
        val r = clean.substring(0, 2).toIntOrNull(16) ?: 0
        val g = clean.substring(2, 4).toIntOrNull(16) ?: 0
        val b = clean.substring(4, 6).toIntOrNull(16) ?: 0
        return (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
    }
}
