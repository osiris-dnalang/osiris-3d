package com.example.osiris.core

import kotlinx.coroutines.delay
import java.time.Instant

class MakerWorldService {

    private val catalog = listOf(
        MakerWorldModel(
            modelId = "mw-965301",
            title = "FPV Racing Drone v2 - Kinetic Airframe",
            creator = "AeroForge",
            category = "Robotics & RC",
            downloadsCount = 14820,
            likesCount = 3410,
            rating = 4.9f,
            sourceFile = "drone_frame.3mf",
            tags = listOf("Drone", "FPV", "CarbonFiber", "HighImpact", "CrashResistant"),
            structuralClass = "HIGH_IMPACT",
            suggestedMaterial = "PETG-CF",
            estimatedTimeMinutes = 97,
            estimatedWeightGrams = 84.5,
            description = "High-tensile carbon fiber reinforced unibody drone frame designed to survive high-speed kinetic crashes. Optimized for gyroid infill and perimeter-heavy slicing.",
            primaryColorHex = "#38BDF8"
        ),
        MakerWorldModel(
            modelId = "mw-442109",
            title = "Heavy-Duty 15kg Dynamic Gear Bracket",
            creator = "StructuraLab",
            category = "Mechanical & Tools",
            downloadsCount = 9240,
            likesCount = 1890,
            rating = 4.8f,
            sourceFile = "bracket_15kg.3mf",
            tags = listOf("LoadBearing", "Bracket", "Industrial", "Tensile", "PETG"),
            structuralClass = "RIGID_TENSILE",
            suggestedMaterial = "PETG-CF",
            estimatedTimeMinutes = 62,
            estimatedWeightGrams = 58.0,
            description = "Engineered to sustain a minimum 147.15N static/dynamic shear load. Recommends minimum 5 wall perimeters and 25% isotropic infill.",
            primaryColorHex = "#F59E0B"
        ),
        MakerWorldModel(
            modelId = "mw-318490",
            title = "All-Weather Rugged IP67 Electronics Enclosure",
            creator = "Overland3D",
            category = "Electronics Enclosures",
            downloadsCount = 22400,
            likesCount = 5120,
            rating = 4.9f,
            sourceFile = "rugged_box_ip67.3mf",
            tags = listOf("Weatherproof", "UVResistant", "ASA", "ABS", "Waterproof", "Outdoor"),
            structuralClass = "WEATHER_RESISTANT",
            suggestedMaterial = "ASA",
            estimatedTimeMinutes = 140,
            estimatedWeightGrams = 112.0,
            description = "UV-stabilized, high heat deflection (HDT > 88°C) outdoor junction box with integrated dual-shot TPU sealing channel.",
            primaryColorHex = "#10B981"
        ),
        MakerWorldModel(
            modelId = "mw-771204",
            title = "Precision Herringbone Planetary Reducer (5:1)",
            creator = "MechKinetics",
            category = "Mechanical & Tools",
            downloadsCount = 31050,
            likesCount = 7430,
            rating = 5.0f,
            sourceFile = "herringbone_gears.stl",
            tags = listOf("PlanetaryGear", "Herringbone", "Mechanism", "Precision", "PLA"),
            structuralClass = "RAPID_PROTOTYPE",
            suggestedMaterial = "PLA_BASIC",
            estimatedTimeMinutes = 88,
            estimatedWeightGrams = 72.0,
            description = "Zero backlash herringbone gear profile with 0.15mm mechanical tolerances. Print-in-place assembly.",
            primaryColorHex = "#A78BFA"
        ),
        MakerWorldModel(
            modelId = "mw-883192",
            title = "Articulated Chromatic Cyber Dragon (16-Color)",
            creator = "ChromaSculpt",
            category = "Articulated",
            downloadsCount = 54200,
            likesCount = 12900,
            rating = 4.9f,
            sourceFile = "cyber_dragon_ams.3mf",
            tags = listOf("MultiColor", "AMS", "Articulated", "16Color", "H2C"),
            structuralClass = "RAPID_PROTOTYPE",
            suggestedMaterial = "PLA_BASIC",
            estimatedTimeMinutes = 310,
            estimatedWeightGrams = 240.0,
            description = "Flagship multi-material design pre-calibrated for Bambu H2C 4x AMS arrays with custom purge-reduction flush volumes.",
            primaryColorHex = "#EC4899"
        ),
        MakerWorldModel(
            modelId = "mw-559102",
            title = "Aerospace Cable Routing Backbone v3",
            creator = "Avionix",
            category = "Automotive",
            downloadsCount = 8700,
            likesCount = 1650,
            rating = 4.7f,
            sourceFile = "cable_chain_v3.3mf",
            tags = listOf("CableChain", "Aerospace", "WireManagement", "HighFlex"),
            structuralClass = "RIGID_TENSILE",
            suggestedMaterial = "PETG-CF",
            estimatedTimeMinutes = 54,
            estimatedWeightGrams = 42.0,
            description = "Interlocking cable spine resisting high thermal cycling and repetitive bending fatigue.",
            primaryColorHex = "#38BDF8"
        ),
        MakerWorldModel(
            modelId = "mw-661840",
            title = "GoPro Hero 13 Kinetic Crash Bumper",
            creator = "FPV_Extreme",
            category = "Robotics & RC",
            downloadsCount = 18500,
            likesCount = 4200,
            rating = 4.8f,
            sourceFile = "gopro13_bumper.stl",
            tags = listOf("GoPro", "TPU", "CrashProtection", "ShockAbsorbing", "Flexible"),
            structuralClass = "FLEX_DAMPENED",
            suggestedMaterial = "TPU_95A",
            estimatedTimeMinutes = 45,
            estimatedWeightGrams = 32.0,
            description = "95A Shore hardness elastomer shock absorber dampening high-g vibration and impact spikes on action cameras.",
            primaryColorHex = "#14B8A6"
        ),
        MakerWorldModel(
            modelId = "mw-229410",
            title = "Modular DIN-Rail Snap Mount (Cam Lock)",
            creator = "ToolCraft",
            category = "Mechanical & Tools",
            downloadsCount = 12100,
            likesCount = 2800,
            rating = 4.9f,
            sourceFile = "snapfit_camlock.3mf",
            tags = listOf("DINRail", "SnapFit", "Modular", "ShopTools", "QuickRelease"),
            structuralClass = "RIGID_TENSILE",
            suggestedMaterial = "PLA_BASIC",
            estimatedTimeMinutes = 38,
            estimatedWeightGrams = 28.0,
            description = "Quick-release cam mechanism snapping securely onto standard 35mm top-hat DIN rails.",
            primaryColorHex = "#F97316"
        ),
        MakerWorldModel(
            modelId = "mw-891032",
            title = "Red-Bellied Piranha - Ultra-Realistic Figure",
            creator = "Ingenium",
            category = "Art & Figurines",
            downloadsCount = 9840,
            likesCount = 3120,
            rating = 5.0f,
            sourceFile = "piranha_ultra_realistic.3mf",
            tags = listOf("Piranha", "Fish", "MultiColor", "AMS", "UltraRealistic", "Articulated"),
            structuralClass = "HIGH_DETAIL_ORGANIC",
            suggestedMaterial = "Multi-Color PLA",
            estimatedTimeMinutes = 410,
            estimatedWeightGrams = 769.0, // 204g + 95g + 215g + 255g
            description = "Freshwater fish native to South America with silvery-gray body, crimson underside, and razor-sharp interlocking teeth. 4-color AMS profile with variable layer heights (0.08-0.12mm) for micro-scale dermal and teeth sharpness.",
            primaryColorHex = "#EF4444"
        ),
        MakerWorldModel(
            modelId = "mw-771204",
            title = "Modular Flexi PIRANHA - Articulated Fish",
            creator = "Flexi JIMGA",
            category = "Flexi & Toys",
            downloadsCount = 8300,
            likesCount = 5200,
            rating = 4.9f,
            sourceFile = "flexi_piranha_modular.3mf",
            tags = listOf("Flexi", "Piranha", "Articulated", "PrintInPlace"),
            structuralClass = "FLEX_DAMPENED",
            suggestedMaterial = "PLA_BASIC",
            estimatedTimeMinutes = 115,
            estimatedWeightGrams = 88.0,
            description = "Print-in-place articulated segment fish with interlocking spine vertebrae.",
            primaryColorHex = "#F59E0B"
        ),
        MakerWorldModel(
            modelId = "mw-992314",
            title = "Realistic Night Fury",
            creator = "Becca_3D / Kaijumon",
            category = "Art & Figurines",
            downloadsCount = 11200,
            likesCount = 8200,
            rating = 4.9f,
            sourceFile = "night_fury_detailed.3mf",
            tags = listOf("Dragon", "NightFury", "MultiColor", "Sculpture"),
            structuralClass = "HIGH_DETAIL_ORGANIC",
            suggestedMaterial = "PLA_BASIC",
            estimatedTimeMinutes = 320,
            estimatedWeightGrams = 195.0,
            description = "High polygon dragon sculpture with membrane wings and precision scale textures.",
            primaryColorHex = "#6366F1"
        )
    )

    private var telemetry = IngestionTelemetry()

    fun getModels(): List<MakerWorldModel> = catalog

    fun getTelemetry(): IngestionTelemetry = telemetry

    fun searchModels(query: String): List<MakerWorldModel> {
        if (query.isBlank()) return catalog
        val q = query.trim().lowercase()

        // Semantic Intent Deduction
        val isUvOutdoor = q.contains("uv") || q.contains("outdoor") || q.contains("weather")
        val isImpact = q.contains("impact") || q.contains("shatter") || q.contains("crash") || q.contains("heavy duty")
        val isLoad = q.contains("15kg") || q.contains("load") || q.contains("bearing") || q.contains("shear")
        val isMultiColor = q.contains("color") || q.contains("multi") || q.contains("ams")
        val isFlexible = q.contains("flex") || q.contains("tpu") || q.contains("rubber") || q.contains("bumper")

        return catalog.filter { model ->
            model.title.lowercase().contains(q) ||
                    model.description.lowercase().contains(q) ||
                    model.creator.lowercase().contains(q) ||
                    model.tags.any { it.lowercase().contains(q) } ||
                    model.category.lowercase().contains(q) ||
                    (isUvOutdoor && (model.structuralClass == "WEATHER_RESISTANT" || model.suggestedMaterial == "ASA")) ||
                    (isImpact && (model.structuralClass == "HIGH_IMPACT" || model.suggestedMaterial == "PETG-CF")) ||
                    (isLoad && model.structuralClass == "RIGID_TENSILE") ||
                    (isMultiColor && model.tags.contains("MultiColor")) ||
                    (isFlexible && model.suggestedMaterial == "TPU_95A")
        }
    }

    suspend fun performIngestionSweep(): IngestionTelemetry {
        telemetry = telemetry.copy(scraperStatus = "SCRAPING")
        delay(600)
        telemetry = telemetry.copy(scraperStatus = "INTERCEPTING")
        delay(600)
        telemetry = telemetry.copy(
            scraperStatus = "SYNCED",
            modelsCatalogedCount = telemetry.modelsCatalogedCount + 48,
            filesDownloadedCount = telemetry.filesDownloadedCount + 142,
            storageUsageGb = Math.round((telemetry.storageUsageGb + 0.35) * 10.0) / 10.0,
            lastSyncTimeUtc = Instant.now().toString()
        )
        return telemetry
    }

    fun generateOpenScad(modelId: String, p: ParametricCustomization): String {
        return """
// ========================================================
// OSIRIS NCLM GENERATIVE CAD ENGINE - OPENSCAD V2.4
// Geometry as Code: Parametric Synthesis for $modelId
// Target Material: ${p.selectedMaterial}
// ========================================================

${'$'}fn = 80;

// Injected Parametric Variables from NCLM Intent Engine
bracket_thickness = ${p.thicknessMm};     // Calculated for structural load area
hole_clearance = ${p.holeClearanceMm};        // Dynamic slip/press tolerance
wall_loops = ${p.wallLoops};                 // Peripheral tensile reinforcement
infill_density = ${p.infillDensityPercent};          // Isotropic ${p.infillPattern} infill
load_angle = ${p.loadVectorAngleDeg};           // Z-axis anti-delamination rotation

module mounting_bracket() {
    difference() {
        // Base Solid Shell
        hull() {
            cylinder(h = bracket_thickness, r = 18, center = true);
            translate([45, 0, 0])
                cylinder(h = bracket_thickness, r = 12, center = true);
            translate([0, 35, 0])
                cylinder(h = bracket_thickness, r = 10, center = true);
        }
        
        // Bearing Bore with Precision Clearance
        cylinder(h = bracket_thickness + 2, r = 10 + hole_clearance, center = true);
        
        // M4 Mounting Holes
        translate([45, 0, 0])
            cylinder(h = bracket_thickness + 2, r = 2.1 + hole_clearance, center = true);
        translate([0, 35, 0])
            cylinder(h = bracket_thickness + 2, r = 2.1 + hole_clearance, center = true);
            
        // Stress Relief Fillets
        translate([15, 12, 0])
            cylinder(h = bracket_thickness + 2, r = 5, center = true);
    }
}

// Render rotated to align load vector perpendicular to Z layer lines
rotate([0, load_angle, 0])
    mounting_bracket();
""".trimIndent()
    }

    fun deriveParametricCustomization(userPrompt: String, model: MakerWorldModel): ParametricCustomization {
        val q = userPrompt.lowercase()
        val is15kg = q.contains("15kg") || q.contains("load") || q.contains("heavy") || q.contains("shatter")
        val isUv = q.contains("uv") || q.contains("outdoor") || q.contains("weather")
        val isFlex = q.contains("flex") || q.contains("tpu") || q.contains("bumper")

        val material = when {
            isUv -> "ASA"
            isFlex -> "TPU_95A"
            is15kg -> "PETG-CF"
            else -> model.suggestedMaterial
        }

        val walls = if (is15kg) 6 else if (isUv) 5 else 4
        val infill = if (is15kg) 28 else if (isUv) 25 else 20
        val thickness = if (is15kg) 8.0 else if (isUv) 6.5 else 5.0
        val rotAngle = if (is15kg) 90.0 else 0.0

        val initial = ParametricCustomization(
            modelId = model.modelId,
            thicknessMm = thickness,
            wallLoops = walls,
            infillDensityPercent = infill,
            infillPattern = "gyroid",
            holeClearanceMm = 0.25,
            loadVectorAngleDeg = rotAngle,
            selectedMaterial = material
        )

        return initial.copy(openScadScript = generateOpenScad(model.modelId, initial))
    }

    private var p1sDiagnostics = listOf(
        P1SDiagnosticReport(
            deviceId = "P1S_Alpha",
            jobType = "Multi-Color PLA",
            amsSwaps = 450,
            avgVolumetricFlow = 12.5,
            silentModeActive = false,
            coolingSlowdowns = 15,
            issues = listOf("Excessive AMS filament swaps (450). Each swap adds ~80s. Total wasted time: ~10.0 hours."),
            recommendations = listOf("Increase 'Flush into objects' or 'Flush into infill' to reduce purge tower size, and group identical colors by layer height."),
            isAutoFixed = false
        ),
        P1SDiagnosticReport(
            deviceId = "P1S_Beta",
            jobType = "PETG-CF Structural",
            amsSwaps = 0,
            avgVolumetricFlow = 8.0,
            silentModeActive = false,
            coolingSlowdowns = 85,
            issues = listOf(
                "Severe volumetric flow restriction (8.0 mm³/s). Check filament profile max volumetric flow limit (MVS).",
                "Minimum layer time constraint hit 85 times. Slicer is artificially slowing print head to allow cooling."
            ),
            recommendations = listOf(
                "Increase MVS from 8.0 to 15.0 mm³/s in filament profile to unlock full CoreXY melt capability.",
                "Print multiple instances simultaneously or add dummy cooling tower to maintain high head speed."
            ),
            isAutoFixed = false
        ),
        P1SDiagnosticReport(
            deviceId = "P1S_Gamma",
            jobType = "ABS Bulk Box",
            amsSwaps = 0,
            avgVolumetricFlow = 21.0,
            silentModeActive = true,
            coolingSlowdowns = 2,
            issues = listOf("Printer operating in 'Silent Mode' (50% acceleration and feedrate)."),
            recommendations = listOf("Disable 'Silent Mode' via MQTT command to restore 100% CoreXY 500mm/s acceleration limits."),
            isAutoFixed = false
        ),
        P1SDiagnosticReport(
            deviceId = "P1S_Delta",
            jobType = "TPU Gaskets",
            amsSwaps = 0,
            avgVolumetricFlow = 3.2,
            silentModeActive = false,
            coolingSlowdowns = 12,
            issues = listOf("Low volumetric speed (3.2 mm³/s) - normal for 95A flexible elastomer."),
            recommendations = listOf("Direct drive flow rate optimal for TPU. No throttling detected."),
            isAutoFixed = true
        ),
        P1SDiagnosticReport(
            deviceId = "P1S_Epsilon",
            jobType = "Small PLA Miniatures",
            amsSwaps = 12,
            avgVolumetricFlow = 15.0,
            silentModeActive = false,
            coolingSlowdowns = 240,
            issues = listOf("Minimum layer time constraint hit 240 times. Slicer is artificially slowing print head to allow cooling."),
            recommendations = listOf("Parts are too small, triggering 'Slow down for better cooling'. Print 4 parts simultaneously on plate to allow natural cooling."),
            isAutoFixed = false
        )
    )

    fun getP1SDiagnostics(): List<P1SDiagnosticReport> = p1sDiagnostics

    fun autoFixP1SDiagnostics(): List<P1SDiagnosticReport> {
        p1sDiagnostics = p1sDiagnostics.map { diag ->
            diag.copy(
                isAutoFixed = true,
                issues = listOf("Optimized via OSIRIS Autopilot: Slicer overrides & MQTT commands applied."),
                recommendations = listOf("All bottlenecks mitigated: 100% CoreXY throughput restored.")
            )
        }
        return p1sDiagnostics
    }

    fun scanFleetAndOptimize(model: MakerWorldModel, fleet: List<PrinterNode>): FilamentScanEnhancement {
        val isPiranha = model.modelId == "mw-891032" || model.title.contains("Piranha", ignoreCase = true)

        // Select optimal printer: Prefer H2C (16-color array) or P1S nodes with loaded AMS
        val targetPrinter = if (isPiranha) {
            fleet.find { it.model == "H2C" && it.isOnline }
                ?: fleet.firstOrNull { it.amsUnits.isNotEmpty() && it.isOnline }
                ?: fleet.first()
        } else {
            fleet.firstOrNull { it.status == "IDLE" && it.isOnline } ?: fleet.first()
        }

        val purgeSavings = if (isPiranha) 184.0 else 45.0
        val purgeSavingsPct = if (isPiranha) 62.5 else 38.0
        val adaptiveLayer = if (isPiranha) "0.08mm - 0.12mm (Adaptive for Sharp Teeth Detail)" else "0.12mm - 0.20mm"
        val colorSeq = if (isPiranha) listOf("Bone White", "Silver Gray", "Crimson Red", "Deep Black") else listOf("Primary", "Accent")

        return FilamentScanEnhancement(
            modelId = model.modelId,
            modelTitle = model.title,
            targetPrinterId = targetPrinter.deviceId,
            targetPrinterName = targetPrinter.name,
            matchedSpoolsCount = if (isPiranha) 4 else 2,
            totalRequiredSpoolsCount = if (isPiranha) 4 else 2,
            isReadyToDispatch = true,
            purgeSavingsGrams = purgeSavings,
            purgeSavingsPercent = purgeSavingsPct,
            adaptiveLayerHeight = adaptiveLayer,
            colorPrintSequence = colorSeq,
            flushVolumeMatrixOverrides = mapOf(
                "WHITE_TO_RED" to 140.0,
                "RED_TO_BLACK" to 110.0,
                "BLACK_TO_WHITE" to 320.0,
                "TOTAL_PURGE_SAVINGS_PERCENT" to purgeSavingsPct
            ),
            enhancementSummary = "OSIRIS Fleet Scanner matched 4/4 required PLA spools on ${targetPrinter.name}. Purge-to-infill applied (saving ${purgeSavings}g filament). Variable layer height enabled for razor-sharp teeth detail."
        )
    }
}
