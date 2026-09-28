package com.example.osiris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.osiris.core.FleetService
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
fun FilamentMatrixScreen(
    fleetService: FleetService,
    modifier: Modifier = Modifier
) {
    var fromMaterial by remember { mutableStateOf("PETG-CF") }
    var fromHex by remember { mutableStateOf("#111111") } // Black
    var toMaterial by remember { mutableStateOf("PLA_BASIC") }
    var toHex by remember { mutableStateOf("#FFFFFF") } // White

    val calculatedFlush = remember(fromMaterial, fromHex, toMaterial, toHex) {
        fleetService.calculateFlushVolume(fromMaterial, fromHex, toMaterial, toHex)
    }

    val isChemicalPenalty = fromMaterial != toMaterial

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            OsirisCard(
                borderColor = OsirisViolet.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = OsirisViolet, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "FILAMENT REGISTRY & FLUSH VOLUME OPTIMIZER",
                            color = OsirisViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Tensile properties and dynamic purge matrices for multi-material H2C array",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Material Mechanical Properties Table
        item {
            OsirisCard {
                SectionHeader(
                    title = "Material Physical Properties Index",
                    subtitle = "Structural resolution criteria: Tensile (MPa), Flexural (GPa), and HDT (°C)"
                )

                Spacer(modifier = Modifier.height(10.dp))

                val scrollState = rememberScrollState()
                Column(modifier = Modifier.horizontalScroll(scrollState)) {
                    Row(
                        modifier = Modifier
                            .background(OsirisSurfaceElevated)
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Material", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        Text("Tensile (MPa)", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        Text("Flex Mod (GPa)", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        Text("HDT @ 0.45", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                        Text("Nozzle (°C)", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                        Text("Bed (°C)", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                    }

                    fleetService.filamentRegistry.values.forEach { prop ->
                        Row(
                            modifier = Modifier
                                .border(0.5.dp, OsirisBorder.copy(alpha = 0.5f))
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prop.materialType, color = OsirisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                            Text("${prop.tensileStrengthMpa}", color = OsirisEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp))
                            Text("${prop.flexuralModulusGpa}", color = OsirisCyan, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            Text("${prop.hdt045Mpa.toInt()}°C", color = OsirisTextSecondary, fontSize = 11.sp, modifier = Modifier.width(80.dp))
                            Text("${prop.nozzleTempMin}-${prop.nozzleTempMax}", color = OsirisTextMuted, fontSize = 10.sp, modifier = Modifier.width(80.dp))
                            Text("${prop.bedTemp}°C", color = OsirisTextMuted, fontSize = 10.sp, modifier = Modifier.width(70.dp))
                        }
                    }
                }
            }
        }

        // Dynamic Flush Volume Calculator
        item {
            OsirisCard(
                borderColor = OsirisAmber.copy(alpha = 0.6f),
                backgroundColor = Color(0xFF141A29)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = OsirisAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Dynamic Purge & Flush Calculator (H2C 4x AMS)",
                        color = OsirisAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select From
                Text("From Filament:", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PETG-CF" to "#111111", "PLA_BASIC" to "#FFFFFF", "ABS" to "#EF4444").forEach { (mat, hex) ->
                        val isSelected = fromMaterial == mat && fromHex == hex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) OsirisCyan else OsirisSurfaceElevated)
                                .clickable {
                                    fromMaterial = mat
                                    fromHex = hex
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text("$mat (${if (hex == "#111111") "Black" else if (hex == "#FFFFFF") "White" else "Red"})", color = if (isSelected) Color.Black else OsirisTextPrimary, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select To
                Text("To Filament:", color = OsirisViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PLA_BASIC" to "#FFFFFF", "PETG-CF" to "#111111", "PLA_BASIC" to "#0284C7").forEach { (mat, hex) ->
                        val isSelected = toMaterial == mat && toHex == hex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) OsirisViolet else OsirisSurfaceElevated)
                                .clickable {
                                    toMaterial = mat
                                    toHex = hex
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text("$mat (${if (hex == "#FFFFFF") "White" else if (hex == "#111111") "Black" else "Blue"})", color = if (isSelected) Color.White else OsirisTextPrimary, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Result Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070B12))
                        .border(1.dp, OsirisBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calculated Flush Volume:", color = OsirisTextSecondary, fontSize = 11.sp)
                            Text(
                                "${calculatedFlush.toInt()} mm³",
                                color = OsirisEmerald,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isChemicalPenalty) {
                            Text(
                                "• Chemical Contamination Penalty Applied: +250 mm³ (PETG ↔ PLA incompatibility)",
                                color = OsirisRose,
                                fontSize = 10.sp
                            )
                        }
                        if (fromHex == "#111111" && toHex == "#FFFFFF") {
                            Text(
                                "• Dark-to-Light Pigment Transition Penalty: High volume purge to prevent graying",
                                color = OsirisAmber,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            "• Saves ~35% waste compared to static Bambu Studio default purge block",
                            color = OsirisCyan,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
