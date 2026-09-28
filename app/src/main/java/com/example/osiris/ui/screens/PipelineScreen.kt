package com.example.osiris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.osiris.core.OperatorActionPrompt
import com.example.osiris.core.PipelineStage
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

@Composable
fun PipelineScreen(
    currentStage: PipelineStage,
    operatorPrompt: OperatorActionPrompt?,
    terminalLogs: List<String>,
    onConfirmOperatorLoad: () -> Unit,
    onResetPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Pipeline Status Banner
            val stageColor = when (currentStage) {
                PipelineStage.COMPLETED -> OsirisEmerald
                PipelineStage.PHASE_3_OPERATOR_ACTION -> OsirisAmber
                PipelineStage.IDLE -> OsirisCyan
                else -> OsirisViolet
            }

            OsirisCard(
                borderColor = stageColor.copy(alpha = 0.7f),
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ORCHESTRATION PIPELINE ENGINE",
                            color = OsirisCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Target: FPV Racing Drone v2",
                            color = OsirisTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusPill(
                        text = currentStage.name.replace("_", " "),
                        statusColor = stageColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phase progression steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PipelineStepIndicator(stepNum = 1, label = "Refine", isActive = currentStage >= PipelineStage.PHASE_1_REFINEMENT)
                    PipelineStepIndicator(stepNum = 2, label = "Route", isActive = currentStage >= PipelineStage.PHASE_2_ROUTING)
                    PipelineStepIndicator(stepNum = 3, label = "Action", isActive = currentStage >= PipelineStage.PHASE_3_OPERATOR_ACTION, isAlert = currentStage == PipelineStage.PHASE_3_OPERATOR_ACTION)
                    PipelineStepIndicator(stepNum = 4, label = "Dispatch", isActive = currentStage >= PipelineStage.PHASE_4_DISPATCH)
                }
            }
        }

        // Operator Action Card (Phase 3)
        if (operatorPrompt != null && currentStage == PipelineStage.PHASE_3_OPERATOR_ACTION) {
            item {
                OsirisCard(
                    borderColor = OsirisAmber,
                    backgroundColor = Color(0xFF261D0C)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Engineering,
                            contentDescription = null,
                            tint = OsirisAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "OPERATOR ACTION REQUIRED (AMS SPOOL LOAD)",
                            color = OsirisAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        operatorPrompt.instruction,
                        color = OsirisTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill(text = "MATERIAL: ${operatorPrompt.requiredMaterial}", statusColor = OsirisEmerald)
                        StatusPill(text = "COLOR: ${operatorPrompt.requiredColor}", statusColor = OsirisCyan)
                        StatusPill(text = "${operatorPrompt.targetPrinterName} (S${operatorPrompt.targetSlot})", statusColor = OsirisViolet)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onConfirmOperatorLoad,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisAmber,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("confirm_operator_load_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Spool Loaded into AMS Slot 2", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Execution Console Logs
        item {
            OsirisCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Execution Terminal", color = OsirisTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (currentStage == PipelineStage.COMPLETED) {
                        Text(
                            "Restart Pipeline",
                            color = OsirisCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onResetPipeline() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070B12))
                        .padding(10.dp)
                ) {
                    LazyColumn {
                        items(terminalLogs) { log ->
                            val color = when {
                                log.contains("[BLOCKED]") -> OsirisAmber
                                log.contains("[ACTION]") -> OsirisAmber
                                log.contains("[SUCCESS]") -> OsirisEmerald
                                log.contains("SUCCESSFULLY ORCHESTRATED") -> OsirisEmerald
                                log.contains(">> PHASE") -> OsirisCyan
                                log.contains("[SYSTEM]") -> OsirisViolet
                                else -> OsirisTextSecondary
                            }
                            Text(
                                text = log,
                                color = color,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PipelineStepIndicator(
    stepNum: Int,
    label: String,
    isActive: Boolean,
    isAlert: Boolean = false
) {
    val color = when {
        isAlert -> OsirisAmber
        isActive -> OsirisEmerald
        else -> OsirisBorder
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isActive) color else OsirisSurfaceElevated)
                .border(1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stepNum.toString(),
                color = if (isActive) Color.Black else OsirisTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = if (isActive) OsirisTextPrimary else OsirisTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
