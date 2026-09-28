package com.example.osiris.core

import kotlinx.serialization.Serializable

@Serializable
data class MakerWorldModel(
    val modelId: String,
    val title: String,
    val creator: String,
    val creatorAvatar: String = "",
    val category: String,
    val downloadsCount: Int,
    val likesCount: Int,
    val rating: Float,
    val sourceFile: String,
    val tags: List<String>,
    val structuralClass: String,
    val suggestedMaterial: String,
    val estimatedTimeMinutes: Int,
    val estimatedWeightGrams: Double,
    val description: String,
    val primaryColorHex: String = "#38BDF8"
)

@Serializable
data class ParametricCustomization(
    val modelId: String,
    val thicknessMm: Double = 6.0,
    val wallLoops: Int = 6,
    val infillDensityPercent: Int = 25,
    val infillPattern: String = "gyroid",
    val holeClearanceMm: Double = 0.25,
    val loadVectorAngleDeg: Double = 90.0,
    val selectedMaterial: String = "PETG-CF",
    val openScadScript: String = ""
)

@Serializable
data class P1SDiagnosticReport(
    val deviceId: String,
    val jobType: String,
    val amsSwaps: Int,
    val avgVolumetricFlow: Double,
    val silentModeActive: Boolean,
    val coolingSlowdowns: Int,
    val issues: List<String>,
    val recommendations: List<String>,
    val isAutoFixed: Boolean = false
)

@Serializable
data class FoundryStateUpdate(
    val jsonrpc: String = "2.0",
    val method: String = "osiris.foundry.state_update",
    val modelId: String,
    val intentSummary: String,
    val parametricOverrides: ParametricCustomization,
    val generatedOpenScad: String,
    val flushVolumeMatrixMm3: Map<String, Double>,
    val fleetDispatchRoute: String,
    val targetAmsSlot: Int,
    val status: String = "APPLIED"
)

@Serializable
data class IngestionTelemetry(
    val scraperStatus: String = "IDLE", // "IDLE", "SCRAPING", "INTERCEPTING", "SYNCED"
    val proxyNodesActive: Int = 12,
    val turnstileBypassRate: Float = 99.8f,
    val modelsCatalogedCount: Int = 8420,
    val filesDownloadedCount: Int = 24150,
    val storageUsageGb: Double = 18.4,
    val lastSyncTimeUtc: String = "2026-09-28T05:20:00Z"
)
