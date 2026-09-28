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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.osiris.core.JobExecutionPlan
import com.example.osiris.ui.components.DigestChip
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
fun DevOpsSlicerScreen(
    plan: JobExecutionPlan?,
    onGeneratePlan: (String) -> Unit,
    onLaunchPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember {
        mutableStateOf("I need a heavy duty drone assembly that won't shatter on impact.")
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // DevOps Agent Header
            OsirisCard(
                borderColor = OsirisCyan.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = OsirisCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "GENERATIVE DEVOPS SLICING AGENT",
                            color = OsirisCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Treating .3mf profiles as declarative infrastructure (Geometry as Code)",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Manufacturing Request Input
        item {
            OsirisCard {
                SectionHeader(
                    title = "Manufacturing Intent Parser",
                    subtitle = "Extracts load vectors, selects materials, modifies walls/infill, and routes nodes."
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Engineering intent & constraints") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary,
                        focusedLabelColor = OsirisCyan
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("slicer_prompt_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestion chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        "I need a heavy duty drone assembly that won't shatter on impact.",
                        "Gear bracket to hold 15kg load using PETG-CF on fastest machine.",
                        "Rapid prototype in PLA Matte."
                    )
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(OsirisSurfaceElevated)
                                .border(1.dp, OsirisBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    promptInput = preset
                                    onGeneratePlan(preset)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(preset.take(36) + "...", color = OsirisTextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onGeneratePlan(promptInput) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("compile_plan_button")
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Synthesize Slicing Modifiers & Route Nodes", fontWeight = FontWeight.Bold)
                }
            }
        }

        // JobExecutionPlan Result
        plan?.let { executionPlan ->
            item {
                OsirisCard(
                    borderColor = OsirisEmerald.copy(alpha = 0.7f),
                    backgroundColor = Color(0xFF0F172A)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "JOB EXECUTION PLAN",
                            color = OsirisEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        StatusPill(
                            text = executionPlan.governanceStatus,
                            statusColor = OsirisEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        executionPlan.projectName,
                        color = OsirisTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Plan ID: ${executionPlan.planId} • Verified Nodes: ${executionPlan.verifiedNodesCount}",
                        color = OsirisTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill(
                            text = "CLASS: ${executionPlan.loadBearingClass}",
                            statusColor = OsirisViolet
                        )
                        StatusPill(
                            text = "EST. CONSUMPTION: ${executionPlan.totalFilamentConsumptionG}g",
                            statusColor = OsirisCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Decomposed Part Directives (${executionPlan.parts.size} Parts):",
                        color = OsirisTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    executionPlan.parts.forEach { part ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF070B12))
                                .border(1.dp, OsirisBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${part.partName} (${part.sourceAsset})",
                                        color = OsirisCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    StatusPill(
                                        text = part.resolvedMaterial,
                                        statusColor = OsirisEmerald
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    part.substitutionRationale,
                                    color = OsirisTextSecondary,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Wall Loops: ${part.modifiers.wallLoops}",
                                        color = OsirisAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Infill: ${part.modifiers.infillDensityPercent}% ${part.modifiers.infillPattern}",
                                        color = OsirisAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Target: ${part.targetPrinterId} (AMS S${part.targetAmsSlot})",
                                        color = OsirisCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    "CLI: ${part.cliArguments.joinToString(" ")}",
                                    color = OsirisTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onLaunchPipeline,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("launch_pipeline_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger 4-Phase Execution Pipeline", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Raw JSON Schema Viewer
            item {
                OsirisCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "JSON-RPC 2.0 Payload Preview",
                            color = OsirisTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val jsonStr = formatPlanToJson(executionPlan)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("JobExecutionPlan", jsonStr)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied JSON-RPC Payload", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy JSON", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070B12))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = formatPlanToJson(executionPlan),
                            color = OsirisEmerald,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
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

fun formatPlanToJson(plan: JobExecutionPlan): String {
    val sb = StringBuilder()
    sb.append("{\n")
    sb.append("  \"jsonrpc\": \"2.0\",\n")
    sb.append("  \"method\": \"osiris.fleet.dispatch_execution_plan\",\n")
    sb.append("  \"params\": {\n")
    sb.append("    \"plan_id\": \"${plan.planId}\",\n")
    sb.append("    \"project_name\": \"${plan.projectName}\",\n")
    sb.append("    \"timestamp_utc\": \"${plan.timestampUtc}\",\n")
    sb.append("    \"constraint_analysis\": {\n")
    sb.append("      \"raw_request\": \"${plan.rawRequest}\",\n")
    sb.append("      \"extracted_constraints\": {\n")
    sb.append("        \"load_bearing_class\": \"${plan.loadBearingClass}\",\n")
    sb.append("        \"primary_failure_mode\": \"${plan.primaryFailureMode}\",\n")
    sb.append("        \"aesthetic_preference\": \"${plan.aestheticPreference}\",\n")
    sb.append("        \"time_constraint\": \"${plan.timeConstraint}\"\n")
    sb.append("      }\n")
    sb.append("    },\n")
    sb.append("    \"decomposed_jobs\": [\n")
    plan.parts.forEachIndexed { idx, p ->
        sb.append("      {\n")
        sb.append("        \"part_id\": \"${p.partId}\",\n")
        sb.append("        \"part_name\": \"${p.partName}\",\n")
        sb.append("        \"material_resolution\": {\n")
        sb.append("          \"resolved_material\": \"${p.resolvedMaterial}\",\n")
        sb.append("          \"tensile_strength_mpa\": ${p.tensileStrengthMpa},\n")
        sb.append("          \"flexural_modulus_gpa\": ${p.flexuralModulusGpa}\n")
        sb.append("        },\n")
        sb.append("        \"generative_slicing\": {\n")
        sb.append("          \"wall_loops\": ${p.modifiers.wallLoops},\n")
        sb.append("          \"infill_pattern\": \"${p.modifiers.infillPattern}\",\n")
        sb.append("          \"infill_density\": ${p.modifiers.infillDensityPercent}\n")
        sb.append("        },\n")
        sb.append("        \"node_routing\": {\n")
        sb.append("          \"target_node_id\": \"${p.targetPrinterId}\",\n")
        sb.append("          \"ams_slot\": ${p.targetAmsSlot}\n")
        sb.append("        }\n")
        sb.append("      }${if (idx < plan.parts.size - 1) "," else ""}\n")
    }
    sb.append("    ]\n")
    sb.append("  }\n")
    sb.append("}")
    return sb.toString()
}
