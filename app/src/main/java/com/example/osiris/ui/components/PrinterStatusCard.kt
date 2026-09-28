package com.example.osiris.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.osiris.core.PrinterNode
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

/**
 * Reusable Jetpack Compose component visualizing live telemetry data for an individual 3D printer node.
 * Displays printer name, connectivity & Wi-Fi signal status, job progress percentage, and current filament load across AMS units.
 */
@Composable
fun PrinterStatusCard(
    printer: PrinterNode,
    modifier: Modifier = Modifier,
    onPauseToggle: (() -> Unit)? = null,
    onEmergencyStop: (() -> Unit)? = null,
    onToggleSelect: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val isPrinting = printer.status == "PRINTING"
    val isPaused = printer.status == "PAUSED"
    val isError = printer.status == "ERROR"
    val isDegraded = printer.telemetryHealth == "DEGRADED" || printer.status.contains("DEGRADED")
    val isOffline = !printer.isOnline || printer.status == "OFFLINE" || printer.telemetryHealth == "OFFLINE"

    val (stateIcon, stateTint, stateLabel) = when {
        isOffline || isError -> Triple(Icons.Default.Error, OsirisRose, "Offline")
        isDegraded -> Triple(Icons.Default.Warning, OsirisAmber, "Degraded Telemetry")
        else -> Triple(Icons.Default.CheckCircle, OsirisEmerald, "Online")
    }

    val badgeColor = when {
        isOffline || isError -> OsirisRose
        isDegraded -> OsirisAmber
        isPrinting -> OsirisEmerald
        isPaused -> OsirisAmber
        else -> OsirisCyan
    }

    var isLightOn by remember { mutableStateOf(isPrinting) }
    var isBoostFan by remember { mutableStateOf(false) }

    OsirisCard(
        modifier = modifier
            .testTag("printer_card_${printer.deviceId}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        borderColor = if (isPrinting) OsirisEmerald.copy(alpha = 0.5f) else if (isDegraded) OsirisAmber.copy(alpha = 0.6f) else OsirisBorder,
        backgroundColor = OsirisSurfaceCard
    ) {
        // 1. Header: Printer Name, Visual Connectivity State Indicator, Model, IP & Status Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Visual State Indicator with Material Icon: check_circle (online), warning (degraded), error (offline)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(stateTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stateIcon,
                        contentDescription = stateLabel,
                        tint = stateTint,
                        modifier = Modifier
                            .size(16.dp)
                            .testTag("printer_status_icon_${printer.deviceId}")
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = printer.name,
                            color = OsirisTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${printer.model} • ${printer.enclosureType.lowercase()})",
                            color = OsirisCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Visual State Label + Connectivity info: IP, Wi-Fi RSSI, Nozzle Spec
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stateLabel,
                            color = stateTint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("•", color = OsirisTextMuted, fontSize = 10.sp)
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wi-Fi Signal",
                            tint = if (!isOffline) OsirisEmerald else OsirisTextMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "${printer.ipAddress} (${printer.wifiRssiDbm} dBm) • ${printer.nozzleMaterial}",
                            color = OsirisTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            StatusPill(
                text = if (isOffline) "OFFLINE" else if (isDegraded) "DEGRADED" else printer.status,
                statusColor = badgeColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Progress Percentage & Active Job Section
        if (isPrinting || isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF070B12))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = OsirisCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (printer.currentJobName.isNotBlank()) printer.currentJobName else "active_job.gcode.3mf",
                                color = OsirisTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "${printer.progressPercent.toInt()}%",
                            color = if (isPaused) OsirisAmber else OsirisEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress Percentage Bar
                    LinearProgressIndicator(
                        progress = { (printer.progressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isPaused) OsirisAmber else OsirisEmerald,
                        trackColor = OsirisSurfaceElevated
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Layer: ${printer.currentLayer}/${printer.totalLayers}",
                            color = OsirisTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ETA: ${printer.remainingTimeM} min remaining",
                            color = OsirisCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            // Idle state indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF070B12))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toolhead parked. Awaiting job dispatch...",
                        color = OsirisTextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "0% Progress",
                        color = OsirisTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 3. Live Telemetry Readings (Thermal & Volumetric Flow)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(OsirisSurfaceElevated)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Nozzle Temp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = OsirisRose, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Nozzle", color = OsirisTextMuted, fontSize = 9.sp)
                }
                Text(
                    text = "${printer.nozzleTemp.toInt()}°C",
                    color = if (printer.nozzleTemp > 100) OsirisRose else OsirisTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (printer.targetNozzleTemp > 0) {
                    Text(text = "/${printer.targetNozzleTemp.toInt()}°C", color = OsirisTextMuted, fontSize = 9.sp)
                }
            }

            // Bed Temp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = OsirisAmber, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Bed", color = OsirisTextMuted, fontSize = 9.sp)
                }
                Text(
                    text = "${printer.bedTemp.toInt()}°C",
                    color = if (printer.bedTemp > 50) OsirisAmber else OsirisTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (printer.targetBedTemp > 0) {
                    Text(text = "/${printer.targetBedTemp.toInt()}°C", color = OsirisTextMuted, fontSize = 9.sp)
                }
            }

            // Chamber Temp (if available)
            printer.chamberTemp?.let { chamber ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Chamber", color = OsirisTextMuted, fontSize = 9.sp)
                    Text("${chamber.toInt()}°C", color = OsirisTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Enclosed", color = OsirisEmerald, fontSize = 9.sp)
                }
            }

            // Melt Flow Rate
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Flow", color = OsirisTextMuted, fontSize = 9.sp)
                }
                Text(
                    text = "${printer.volumetricFlowMm3s} mm³/s",
                    color = OsirisCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "${printer.printSpeedMmS} mm/s", color = OsirisTextMuted, fontSize = 9.sp)
            }

            // Cooling Fan
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Air, contentDescription = null, tint = OsirisViolet, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Fan", color = OsirisTextMuted, fontSize = 9.sp)
                }
                Text(
                    text = "${if (isBoostFan) 100 else printer.fanSpeedPercent}%",
                    color = OsirisViolet,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Part Cool", color = OsirisTextMuted, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Current Filament Load (AMS Units & Slots)
        Text(
            text = "Current Filament Load (AMS Units):",
            color = OsirisTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        printer.amsUnits.forEach { ams ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ams.slots.forEach { (slotIdx, spool) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070B12))
                            .border(1.dp, OsirisBorder, RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "S$slotIdx",
                                    color = OsirisTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (spool != null) {
                                    val swatchColor = try {
                                        Color(android.graphics.Color.parseColor(spool.colorHex))
                                    } catch (e: Exception) {
                                        OsirisCyan
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(swatchColor)
                                            .border(0.5.dp, Color.White, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            if (spool != null) {
                                Text(
                                    text = spool.properties.materialType,
                                    color = OsirisTextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${spool.remainingWeightG.toInt()}g",
                                    color = if (spool.remainingWeightG < 100) OsirisRose else OsirisTextSecondary,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                Text(text = "Empty", color = OsirisTextMuted, fontSize = 9.sp)
                                Text(text = "--", color = OsirisTextMuted, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5. Controls & Action Buttons (if callbacks provided)
        if (onPauseToggle != null || onEmergencyStop != null || onToggleSelect != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Hardware Toggles (Chamber LED, Fan Boost)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { isLightOn = !isLightOn },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isLightOn) OsirisAmber.copy(alpha = 0.2f) else OsirisSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "LED Light",
                            tint = if (isLightOn) OsirisAmber else OsirisTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { isBoostFan = !isBoostFan },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isBoostFan) OsirisViolet.copy(alpha = 0.2f) else OsirisSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "Boost Fan",
                            tint = if (isBoostFan) OsirisViolet else OsirisTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Primary Operational Actions
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isPrinting || isPaused) {
                        if (onPauseToggle != null) {
                            Button(
                                onClick = onPauseToggle,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPaused) OsirisEmerald else OsirisAmber,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("pause_toggle_btn_${printer.deviceId}")
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPaused) "Resume" else "Pause",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (onEmergencyStop != null) {
                            Button(
                                onClick = onEmergencyStop,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OsirisRose,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("emergency_stop_btn_${printer.deviceId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (onToggleSelect != null) {
                        Button(
                            onClick = onToggleSelect,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (printer.isSelected) OsirisCyan else OsirisSurfaceElevated,
                                contentColor = if (printer.isSelected) Color.Black else OsirisTextPrimary
                            ),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("toggle_select_btn_${printer.deviceId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (printer.isSelected) "Cluster Bound" else "Bind Node",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
