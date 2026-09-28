package com.example.osiris.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.osiris.ui.OsirisUiState
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
fun ConsoleScreen(
    state: OsirisUiState,
    onExecuteIntent: (String) -> Unit,
    onExecuteReplay: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskInput by remember { mutableStateOf("replay fixture echo-v1") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Status Card
            OsirisCard(
                borderColor = OsirisCyan.copy(alpha = 0.5f),
                backgroundColor = OsirisSurfaceCard
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(OsirisEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVLM BETA CONTROL PLANE",
                            color = OsirisCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    StatusPill(
                        text = if (state.chainIntegrityValid) "CHAIN VALID" else "CORRUPTED",
                        statusColor = if (state.chainIntegrityValid) OsirisEmerald else OsirisRose
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Deterministic Capability Governor",
                    color = OsirisTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Release: OSIRIS-LIVLM-BETA-0.1.0-REF • Commit 62c3492",
                    color = OsirisTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OsirisSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Process Epoch", color = OsirisTextMuted, fontSize = 10.sp)
                            Text(
                                state.processEpochId,
                                color = OsirisTextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OsirisSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Ledger Height", color = OsirisTextMuted, fontSize = 10.sp)
                            Text(
                                "${state.events.size} Events",
                                color = OsirisEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                DigestChip(label = "Latest Digest", digest = state.latestDigest)
            }
        }

        // Natural Language Intent Console
        item {
            OsirisCard {
                SectionHeader(
                    title = "Intent Interpreter Console",
                    subtitle = "Substrates propose natural tasks; governor enforces deterministic bounds."
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = taskInput,
                    onValueChange = { taskInput = it },
                    label = { Text("Enter intent or governance command") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary,
                        focusedLabelColor = OsirisCyan,
                        unfocusedLabelColor = OsirisTextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_input_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestion chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val presets = listOf(
                        "replay fixture echo-v1",
                        "benchmark perf",
                        "audit ledger chain",
                        "sample tokens",
                        "health check"
                    )
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(OsirisSurfaceElevated)
                                .border(1.dp, OsirisBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    taskInput = preset
                                    onExecuteIntent(preset)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(preset, color = OsirisTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onExecuteIntent(taskInput) },
                    enabled = !state.isExecuting && taskInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisCyan,
                        contentColor = Color(0xFF0A0E17)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("execute_task_button")
                ) {
                    if (state.isExecuting) {
                        CircularProgressIndicator(
                            color = Color(0xFF0A0E17),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Governing Execution...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Propose & Authorize Intent", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Direct Replay Fixture Runner
        item {
            OsirisCard {
                SectionHeader(
                    title = "Offline Replay Runner (Zero Filesystem)",
                    subtitle = "Permits have scope REPLAY_ONLY_NON_CRYPTOGRAPHIC. Zero external authority."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val fixtures = listOf(
                        "echo-v1" to "Echo Test",
                        "benchmark-sample-v1" to "Benchmark",
                        "fixture-energy-test-v1" to "Energy Sim"
                    )
                    fixtures.forEach { (fixtureId, title) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OsirisSurfaceElevated)
                                .border(1.dp, OsirisCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onExecuteReplay(fixtureId) }
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Run $fixtureId",
                                    tint = OsirisCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    title,
                                    color = OsirisTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    fixtureId,
                                    color = OsirisTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Execution Output Card
        state.lastExecutionResult?.let { result ->
            item {
                OsirisCard(
                    borderColor = OsirisEmerald.copy(alpha = 0.6f),
                    backgroundColor = Color(0xFF0F172A)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = OsirisEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "GOVERNED EXECUTION RECEIPT",
                                color = OsirisEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusPill(
                            text = result.epistemicStatus,
                            statusColor = OsirisViolet
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = result.resultSummary,
                        color = OsirisTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill(
                            text = "INTENT: ${result.intent.type}",
                            statusColor = OsirisCyan
                        )
                        StatusPill(
                            text = "CAPABILITY: ${result.intent.capability}",
                            statusColor = OsirisAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    DigestChip(label = "Canonical Hash", digest = result.requestHash)
                    Spacer(modifier = Modifier.height(6.dp))
                    DigestChip(label = "Ledger Event", digest = result.evidenceDigest)

                    if (!result.permitNonce.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        DigestChip(label = "Consumed Nonce", digest = result.permitNonce)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Result Payload:",
                        color = OsirisTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070B12))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = result.resultPayload.entries.joinToString("\n") { (k, v) -> "  \"$k\": \"$v\"" },
                            color = OsirisEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
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
