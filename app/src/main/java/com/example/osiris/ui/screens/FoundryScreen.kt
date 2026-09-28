package com.example.osiris.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.osiris.core.IngestionTelemetry
import com.example.osiris.core.MakerWorldModel
import com.example.osiris.core.P1SDiagnosticReport
import com.example.osiris.core.ParametricCustomization
import com.example.osiris.ui.components.OsirisCard
import com.example.osiris.ui.components.SectionHeader
import com.example.osiris.ui.components.StatusPill
import com.example.osiris.ui.theme.OsirisAmber
import com.example.osiris.ui.theme.OsirisBorder
import com.example.osiris.ui.theme.OsirisCyan
import com.example.osiris.ui.theme.OsirisEmerald
import com.example.osiris.ui.theme.OsirisRose
import com.example.osiris.ui.theme.OsirisSurfaceCard
import com.example.osiris.ui.theme.OsirisSurfaceElevated
import com.example.osiris.ui.theme.OsirisTextMuted
import com.example.osiris.ui.theme.OsirisTextPrimary
import com.example.osiris.ui.theme.OsirisTextSecondary
import com.example.osiris.ui.theme.OsirisViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoundryScreen(
    models: List<MakerWorldModel>,
    telemetry: IngestionTelemetry,
    isIngesting: Boolean,
    customization: ParametricCustomization?,
    p1sDiagnostics: List<P1SDiagnosticReport>,
    filamentScanResult: com.example.osiris.core.FilamentScanEnhancement? = null,
    onTriggerIngestion: () -> Unit,
    onSearchModels: (String) -> Unit,
    onSelectModelForPrint: (MakerWorldModel) -> Unit,
    onSelectModelForCustomization: (MakerWorldModel) -> Unit,
    onUpdateCustomization: (Double, Int, Int, Double, String) -> Unit,
    onAutoFixP1SDiagnostics: () -> Unit,
    onScanFleetAndOptimize: (MakerWorldModel) -> Unit = {},
    onDispatchOptimizedModel: (com.example.osiris.core.FilamentScanEnhancement) -> Unit = {},
    onDismissScanResult: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDiagnostics by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // NCLM Osiris Foundry Startup Card
            OsirisCard(
                borderColor = OsirisViolet.copy(alpha = 0.7f),
                backgroundColor = Color(0xFF121226)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "NCLM osiris foundry dna::}{::lang",
                            color = OsirisViolet,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Decoupled MakerWorld Ingestion Hub & Non-Causal CAD Library",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusPill(text = "NCLM ACTIVE", statusColor = OsirisEmerald)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ingestion telemetry summary row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF090D17))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(OsirisEmerald))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Headless Stealth Scraper (${telemetry.proxyNodesActive} Proxies)",
                                color = OsirisEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Turnstile Bypass: ${telemetry.turnstileBypassRate}% • ${telemetry.modelsCatalogedCount} models • ${telemetry.storageUsageGb} GB",
                            color = OsirisTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showDiagnostics = !showDiagnostics },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showDiagnostics) OsirisAmber else OsirisSurfaceElevated,
                                contentColor = if (showDiagnostics) Color.Black else OsirisAmber
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("toggle_p1s_diagnostics_button")
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("P1S Diagnostics", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onTriggerIngestion,
                            enabled = !isIngesting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OsirisCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("trigger_ingestion_button")
                        ) {
                            if (isIngesting) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ingesting...", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("API Intercept Sweep", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // P1S Fleet Diagnostics Drawer
        if (showDiagnostics) {
            item {
                OsirisCard(
                    borderColor = OsirisAmber.copy(alpha = 0.7f),
                    backgroundColor = Color(0xFF1F1A12)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = OsirisAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "P1S FLEET THROUGHPUT DIAGNOSTICS",
                                color = OsirisAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onAutoFixP1SDiagnostics,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OsirisEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Fix Bottlenecks", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    p1sDiagnostics.forEach { diag ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0D121B))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${diag.deviceId} (${diag.jobType})",
                                        color = OsirisCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    StatusPill(
                                        text = if (diag.isAutoFixed) "OPTIMIZED" else "THROTTLED",
                                        statusColor = if (diag.isAutoFixed) OsirisEmerald else OsirisAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                diag.issues.forEach { issue ->
                                    Text("• $issue", color = if (diag.isAutoFixed) OsirisTextSecondary else OsirisRose, fontSize = 10.sp)
                                }
                                diag.recommendations.forEach { rec ->
                                    Text("  -> Fix: $rec", color = OsirisEmerald, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // NCLM Semantic Intent Search
        item {
            OsirisCard {
                SectionHeader(
                    title = "Non-Causal Language Model (NCLM) Search",
                    subtitle = "Search CAD geometries by structural intent, mechanical load, or abstract engineering requirements."
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchModels(it)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OsirisCyan)
                    },
                    placeholder = {
                        Text("e.g., 'outdoor UV exposure and high impact' or '15kg load bracket'", color = OsirisTextMuted, fontSize = 11.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nclm_search_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Semantic Quick Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val promptPresets = listOf(
                        "outdoor UV exposure and high impact",
                        "heavy duty 15kg load",
                        "herringbone gear 0.15mm",
                        "multi-color AMS dragon",
                        "flexible TPU crash bumper"
                    )

                    promptPresets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(OsirisSurfaceElevated)
                                .border(1.dp, OsirisBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    searchQuery = preset
                                    onSearchModels(preset)
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(preset, color = OsirisTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Fleet Filament Match & Slicer Enhancement Result Card
        filamentScanResult?.let { scan ->
            item {
                OsirisCard(
                    borderColor = OsirisViolet,
                    backgroundColor = Color(0xFF140F24)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = OsirisViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "FLEET FILAMENT SCANNER & SLICER ENHANCEMENT",
                                    color = OsirisViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "Model: ${scan.modelTitle}",
                                    color = OsirisTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = onDismissScanResult, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = OsirisTextMuted, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Matched details grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090814))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Target Dispatched Node:", color = OsirisTextMuted, fontSize = 10.sp)
                                Text(scan.targetPrinterName, color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("AMS Loaded Spool Match:", color = OsirisTextMuted, fontSize = 10.sp)
                                Text("${scan.matchedSpoolsCount} of ${scan.totalRequiredSpoolsCount} Spools Verified", color = OsirisEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Adaptive Layer Height:", color = OsirisTextMuted, fontSize = 10.sp)
                                Text(scan.adaptiveLayerHeight, color = OsirisTextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Purge Waste Savings:", color = OsirisTextMuted, fontSize = 10.sp)
                                Text("${scan.purgeSavingsGrams}g (${scan.purgeSavingsPercent}% Reduction)", color = OsirisEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Color Print Order:", color = OsirisTextMuted, fontSize = 10.sp)
                                Text(scan.colorPrintSequence.joinToString(" -> "), color = OsirisAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        scan.enhancementSummary,
                        color = OsirisTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onDispatchOptimizedModel(scan) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisViolet,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("send_to_printer_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Optimized Model to ${scan.targetPrinterName}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // NCLM Parametric CAD Customizer & OpenSCAD Inspector
        customization?.let { custom ->
            item {
                OsirisCard(
                    borderColor = OsirisCyan.copy(alpha = 0.6f),
                    backgroundColor = Color(0xFF0F1A2A)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "PARAMETRIC CAD SYNTHESIZER (OpenSCAD)",
                                color = OsirisCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusPill(text = custom.selectedMaterial, statusColor = OsirisEmerald)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Interactive Geometry Modification (Model: ${custom.modelId})",
                        color = OsirisTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sliders
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bracket Thickness: ${custom.thicknessMm} mm", color = OsirisTextSecondary, fontSize = 11.sp)
                            Text("Load Area: ~${(custom.thicknessMm * 2.2).toInt()} mm²", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = custom.thicknessMm.toFloat(),
                            onValueChange = {
                                onUpdateCustomization(
                                    Math.round(it * 10.0) / 10.0,
                                    custom.wallLoops,
                                    custom.infillDensityPercent,
                                    custom.holeClearanceMm,
                                    custom.selectedMaterial
                                )
                            },
                            valueRange = 4.0f..14.0f,
                            colors = SliderDefaults.colors(thumbColor = OsirisCyan, activeTrackColor = OsirisCyan)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Wall Loops (Perimeters): ${custom.wallLoops}", color = OsirisTextSecondary, fontSize = 11.sp)
                            Text("Tensile Strength Bias", color = OsirisAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = custom.wallLoops.toFloat(),
                            onValueChange = {
                                onUpdateCustomization(
                                    custom.thicknessMm,
                                    it.toInt(),
                                    custom.infillDensityPercent,
                                    custom.holeClearanceMm,
                                    custom.selectedMaterial
                                )
                            },
                            valueRange = 3.0f..10.0f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = OsirisAmber, activeTrackColor = OsirisAmber)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Infill Density: ${custom.infillDensityPercent}% (${custom.infillPattern})", color = OsirisTextSecondary, fontSize = 11.sp)
                            Text("Isotropic Core", color = OsirisEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = custom.infillDensityPercent.toFloat(),
                            onValueChange = {
                                onUpdateCustomization(
                                    custom.thicknessMm,
                                    custom.wallLoops,
                                    it.toInt(),
                                    custom.holeClearanceMm,
                                    custom.selectedMaterial
                                )
                            },
                            valueRange = 15.0f..50.0f,
                            colors = SliderDefaults.colors(thumbColor = OsirisEmerald, activeTrackColor = OsirisEmerald)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // OpenSCAD Script Code Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Injected OpenSCAD Script:", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("OpenSCAD", custom.openScadScript)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied OpenSCAD Script", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy OpenSCAD", color = OsirisCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070B12))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = custom.openScadScript,
                            color = OsirisEmerald,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // MakerWorld Repository Model Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "MakerWorld Ingested CAD Catalog (${models.size} Models)",
                    color = OsirisTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Cached in osiris/cad_lib",
                    color = OsirisCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        items(models, key = { it.modelId }) { model ->
            MakerWorldModelCard(
                model = model,
                onOneClickPrint = { onSelectModelForPrint(model) },
                onCustomize = { onSelectModelForCustomization(model) },
                onScanAndEnhance = { onScanFleetAndOptimize(model) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MakerWorldModelCard(
    model: MakerWorldModel,
    onOneClickPrint: () -> Unit,
    onCustomize: () -> Unit,
    onScanAndEnhance: () -> Unit
) {
    val materialColor = when (model.suggestedMaterial) {
        "PETG-CF" -> OsirisCyan
        "ASA" -> OsirisEmerald
        "TPU_95A" -> OsirisAmber
        else -> OsirisViolet
    }

    OsirisCard(
        borderColor = OsirisBorder,
        backgroundColor = OsirisSurfaceCard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(text = model.category, statusColor = OsirisCyan)
                Spacer(modifier = Modifier.width(6.dp))
                StatusPill(text = model.sourceFile.substringAfterLast(".").uppercase(), statusColor = OsirisViolet)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = OsirisAmber, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("${model.rating}", color = OsirisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Download, contentDescription = null, tint = OsirisTextMuted, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("${model.downloadsCount / 1000}k", color = OsirisTextMuted, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            model.title,
            color = OsirisTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Creator: @${model.creator} • Bounding Volume: ${model.estimatedWeightGrams.toInt()}g (${model.estimatedTimeMinutes} min)",
            color = OsirisTextMuted,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            model.description,
            color = OsirisTextSecondary,
            fontSize = 11.sp,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusPill(text = model.structuralClass, statusColor = OsirisAmber)
                StatusPill(text = model.suggestedMaterial, statusColor = materialColor)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onScanAndEnhance,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisViolet.copy(alpha = 0.2f),
                        contentColor = OsirisViolet
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("scan_enhance_${model.modelId}")
                ) {
                    Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan Fleet & Enhance", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCustomize,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisSurfaceElevated,
                        contentColor = OsirisCyan
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Customize", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOneClickPrint,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisEmerald,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("one_click_print_${model.modelId}")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("1-Click", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
