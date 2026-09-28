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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTetheringError
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.osiris.core.ExtrapolativeCacheEntry
import com.example.osiris.core.FailoverRecoveryRecord
import com.example.osiris.core.GatewayServerStatus
import com.example.osiris.core.OpticalFailureClass
import com.example.osiris.core.OpticalInspectionRecord
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
fun GatewayScreen(
    status: GatewayServerStatus,
    cacheEntries: List<ExtrapolativeCacheEntry>,
    opticalRecord: OpticalInspectionRecord,
    recoveryRecord: FailoverRecoveryRecord?,
    onToggleGatewayPower: () -> Unit,
    onTestFastApiDispatch: () -> Unit,
    onToggleBrownout: (String) -> Unit,
    onRunOpticalScan: () -> Unit,
    onInjectSpaghetti: () -> Unit,
    onExecuteFailover: () -> Unit,
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
            // FastAPI Uvicorn Server Header
            OsirisCard(
                borderColor = if (status.isOnline) OsirisEmerald.copy(alpha = 0.6f) else OsirisRose,
                backgroundColor = Color(0xFF0C1929)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Hub,
                            contentDescription = null,
                            tint = if (status.isOnline) OsirisEmerald else OsirisRose,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "FASTAPI DISPATCH GATEWAY (uvicorn:8000)",
                                color = if (status.isOnline) OsirisEmerald else OsirisRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Server-authoritative BackgroundTasks • Host: ${status.host}:${status.port}",
                                color = OsirisTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    StatusPill(
                        text = if (status.isOnline) "UVICORN ONLINE" else "OFFLINE",
                        statusColor = if (status.isOnline) OsirisEmerald else OsirisRose
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(OsirisSurfaceElevated)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Workers", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("${status.activeWorkerThreads} Threads", color = OsirisTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Handled", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("${status.totalRequestsHandled} Dispatches", color = OsirisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Uptime", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("${status.uptimeSeconds / 3600}h ${(status.uptimeSeconds % 3600) / 60}m", color = OsirisTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTestFastApiDispatch,
                        enabled = status.isOnline,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("test_fastapi_dispatch_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("POST /api/v1/fleet/dispatch", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onToggleGatewayPower,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (status.isOnline) OsirisSurfaceElevated else OsirisEmerald,
                            contentColor = if (status.isOnline) OsirisRose else Color.Black
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("toggle_gateway_power_button")
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (status.isOnline) "Stop" else "Start", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Extrapolative Telemetry Cache Card
        item {
            OsirisCard(
                borderColor = OsirisViolet.copy(alpha = 0.5f),
                backgroundColor = OsirisSurfaceCard
            ) {
                SectionHeader(
                    title = "Extrapolative State Cache (Leaky Bucket)",
                    subtitle = "Maintains mathematical progress interpolation when ESP32 microcontrollers suffer TLS drops."
                )

                Spacer(modifier = Modifier.height(10.dp))

                cacheEntries.forEach { entry ->
                    val isDegraded = entry.isSimulatedBrownout
                    val stateColor = if (isDegraded) OsirisAmber else OsirisEmerald

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B12))
                            .border(1.dp, if (isDegraded) OsirisAmber else OsirisBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isDegraded) Icons.Default.WifiTetheringError else Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = stateColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        entry.deviceId,
                                        color = OsirisTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                StatusPill(text = entry.telemetryState, statusColor = stateColor)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Reported Progress: ${entry.reportedProgressPercent.toInt()}%",
                                    color = OsirisTextMuted,
                                    fontSize = 10.sp
                                )
                                Text(
                                    "Inferred Progress: ${entry.extrapolatedProgressPercent.toInt()}%",
                                    color = if (isDegraded) OsirisAmber else OsirisEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { (entry.extrapolatedProgressPercent / 100.0).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = stateColor,
                                trackColor = OsirisSurfaceElevated
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Last Heartbeat: ${entry.secondsSinceLastHeartbeat}s ago",
                                    color = OsirisTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                Button(
                                    onClick = { onToggleBrownout(entry.deviceId) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDegraded) OsirisEmerald else OsirisSurfaceElevated,
                                        contentColor = if (isDegraded) Color.Black else OsirisAmber
                                    ),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        if (isDegraded) "Restore Wi-Fi Handshake" else "Simulate TLS Drop",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Iteration 5: Optical Inspection & Spaghetti Detection
        item {
            val isSpaghetti = opticalRecord.failureClass == OpticalFailureClass.SPAGHETTI_DETECTION
            val alertColor = if (isSpaghetti) OsirisRose else OsirisEmerald

            OsirisCard(
                borderColor = alertColor,
                backgroundColor = if (isSpaghetti) Color(0xFF261014) else Color(0xFF0F1A2A)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            tint = alertColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "ITERATION 5: OPTICAL QA & SPAGHETTI DETECTOR",
                                color = alertColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "Chamber Camera 1080p @ 30 FPS • Target: ${opticalRecord.printerId}",
                                color = OsirisTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    StatusPill(
                        text = if (isSpaghetti) "SPAGHETTI DETECTED" else "SURFACE NOMINAL",
                        statusColor = alertColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Confidence Meter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF070B12))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Anomaly Confidence Score:", color = OsirisTextMuted, fontSize = 10.sp)
                        Text(
                            "${opticalRecord.spaghettiConfidencePercent}%",
                            color = alertColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Mitigation Action:", color = OsirisTextMuted, fontSize = 10.sp)
                        Text(
                            opticalRecord.mitigationAction,
                            color = alertColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    opticalRecord.failureDescription,
                    color = OsirisTextPrimary,
                    fontSize = 11.sp
                )

                opticalRecord.boundingBoxLabel?.let { label ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(OsirisRose.copy(alpha = 0.2f))
                            .border(1.dp, OsirisRose, RoundedCornerShape(4.dp))
                            .padding(6.dp)
                    ) {
                        Text(
                            "AI BOUNDING BOX: $label",
                            color = OsirisRose,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onRunOpticalScan,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Camera Sweep", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onInjectSpaghetti,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisSurfaceElevated,
                            contentColor = OsirisRose
                        ),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inject Spaghetti", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isSpaghetti) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onExecuteFailover,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisRose,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("execute_failover_button")
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Execute Autonomous Fleet Failover -> P1S_Gamma", fontWeight = FontWeight.Bold)
                    }
                }

                recoveryRecord?.let { recov ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF091422))
                            .border(1.dp, OsirisEmerald, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "FAILOVER DISPATCH CONFIRMED",
                                    color = OsirisEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusPill(text = recov.failoverStatus, statusColor = OsirisEmerald)
                            }
                            Text(
                                "Halted: ${recov.failedPrinterId} @ Layer ${recov.failureLayer}/${recov.totalLayers} • Rescued on: ${recov.rescuePrinterId}",
                                color = OsirisTextPrimary,
                                fontSize = 10.sp
                            )
                            Text(
                                "Evidence: ${recov.ledgerEvidenceHash}",
                                color = OsirisTextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
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
