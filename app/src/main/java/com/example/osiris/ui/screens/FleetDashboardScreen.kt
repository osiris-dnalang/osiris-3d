package com.example.osiris.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.osiris.core.PrinterNode
import com.example.osiris.ui.components.OsirisCard
import com.example.osiris.ui.components.PrinterStatusCard
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

enum class FleetFilter(val label: String) {
    ALL("All"),
    PRINTING("Printing"),
    IDLE("Idle"),
    P1S("P1S"),
    A1_SERIES("A1 / Mini"),
    H2C("H2C Array")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FleetDashboardScreen(
    fleet: List<PrinterNode>,
    isWifiConnected: Boolean,
    wifiSsid: String,
    isScanning: Boolean,
    onConnectWifi: () -> Unit,
    onScanSubnet: () -> Unit,
    onTogglePrinter: (String) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onPausePrinter: (String) -> Unit,
    onStopPrinter: (String) -> Unit,
    onRefreshTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(FleetFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showWifiOnboarding by remember { mutableStateOf(false) }

    // Telemetry aggregations
    val totalNodes = fleet.size
    val printingNodes = fleet.count { it.status == "PRINTING" }
    val idleNodes = fleet.count { it.status == "IDLE" }
    val pausedNodes = fleet.count { it.status == "PAUSED" }
    val clusterUtilizationPercent = if (totalNodes > 0) (printingNodes * 100) / totalNodes else 0

    val totalSpoolWeightKg = remember(fleet) {
        val totalGrams = fleet.sumOf { printer ->
            printer.amsUnits.sumOf { ams ->
                ams.slots.values.filterNotNull().sumOf { it.remainingWeightG }
            }
        }
        Math.round((totalGrams / 1000.0) * 10.0) / 10.0
    }

    val totalPowerKw = remember(printingNodes) {
        // P1S heated bed ~200W, toolhead ~80W. Idle ~20W
        val printingPower = printingNodes * 0.28
        val idlePower = idleNodes * 0.02
        Math.round((printingPower + idlePower) * 100.0) / 100.0
    }

    val filteredFleet = remember(fleet, selectedFilter, searchQuery) {
        fleet.filter { printer ->
            val matchesFilter = when (selectedFilter) {
                FleetFilter.ALL -> true
                FleetFilter.PRINTING -> printer.status == "PRINTING"
                FleetFilter.IDLE -> printer.status == "IDLE"
                FleetFilter.P1S -> printer.model == "P1S"
                FleetFilter.A1_SERIES -> printer.model == "A1" || printer.model == "A1_Mini"
                FleetFilter.H2C -> printer.model == "H2C"
            }
            val matchesSearch = searchQuery.isBlank() ||
                    printer.name.contains(searchQuery, ignoreCase = true) ||
                    printer.ipAddress.contains(searchQuery, ignoreCase = true) ||
                    printer.model.contains(searchQuery, ignoreCase = true) ||
                    printer.currentJobName.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Executive Fleet KPI Strip
            OsirisCard(
                borderColor = OsirisCyan.copy(alpha = 0.6f),
                backgroundColor = Color(0xFF0C1626)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "OSIRIS FLEET TELEMETRY DASHBOARD",
                                color = OsirisCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            "Real-time Python FastAPI Backend & MQTT Local TLS Bridge",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRefreshTelemetry,
                            modifier = Modifier.size(32.dp).testTag("refresh_telemetry_button")
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(color = OsirisCyan, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = OsirisCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                        StatusPill(text = "LIVE MQTT STREAM", statusColor = OsirisEmerald)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Stat Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070B12))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Stat 1: Printers
                    Column {
                        Text("Active Nodes", color = OsirisTextMuted, fontSize = 10.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$printingNodes", color = OsirisEmerald, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("/$totalNodes", color = OsirisTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
                        }
                        Text("$idleNodes Idle • $pausedNodes Paused", color = OsirisTextMuted, fontSize = 9.sp)
                    }

                    // Stat 2: Utilization
                    Column {
                        Text("Cluster Load", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("$clusterUtilizationPercent%", color = OsirisCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { (clusterUtilizationPercent / 100.0).toFloat() },
                            modifier = Modifier
                                .width(70.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = OsirisCyan,
                            trackColor = OsirisSurfaceElevated
                        )
                    }

                    // Stat 3: Filament Stock
                    Column {
                        Text("AMS Loaded Stock", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("${totalSpoolWeightKg} kg", color = OsirisViolet, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("28 Active Spools", color = OsirisTextMuted, fontSize = 9.sp)
                    }

                    // Stat 4: Estimated Power
                    Column {
                        Text("Power Draw", color = OsirisTextMuted, fontSize = 10.sp)
                        Text("${totalPowerKw} kW", color = OsirisAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("CoreXY Bed Array", color = OsirisTextMuted, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wi-Fi Onboarding Expandable Drawer Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(OsirisSurfaceElevated)
                        .clickable { showWifiOnboarding = !showWifiOnboarding }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isWifiConnected) OsirisEmerald else OsirisCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isWifiConnected) "Subnet Online: '$wifiSsid' (192.168.10.0/24)" else "Local Wi-Fi Offline",
                            color = OsirisTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (showWifiOnboarding) "Hide Scanner" else "Onboarding & mDNS",
                            color = OsirisCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            if (showWifiOnboarding) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = OsirisCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = showWifiOnboarding) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onConnectWifi,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isWifiConnected) OsirisSurfaceElevated else OsirisCyan,
                                    contentColor = if (isWifiConnected) OsirisTextPrimary else Color.Black
                                ),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isWifiConnected) "Re-authenticate" else "Connect Wi-Fi", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onScanSubnet,
                                enabled = isWifiConnected && !isScanning,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OsirisViolet,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                if (isScanning) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scanning...", fontSize = 10.sp)
                                } else {
                                    Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("mDNS Discover", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filters and Search Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OsirisCyan, modifier = Modifier.size(18.dp))
                    },
                    placeholder = {
                        Text("Search node by name, model, IP (e.g. 'P1S_Beta', '192.168.10.42')", color = OsirisTextMuted, fontSize = 11.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("fleet_search_input"),
                    singleLine = true
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FleetFilter.values().forEach { filter ->
                        val selected = selectedFilter == filter
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    filter.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OsirisCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = OsirisSurfaceElevated,
                                labelColor = OsirisTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = if (selected) OsirisCyan else OsirisBorder
                            )
                        )
                    }
                }
            }
        }

        // Fleet Telemetry Node Cards
        items(filteredFleet, key = { it.deviceId }) { printer ->
            PrinterStatusCard(
                printer = printer,
                onPauseToggle = { onPausePrinter(printer.deviceId) },
                onEmergencyStop = { onStopPrinter(printer.deviceId) },
                onToggleSelect = { onTogglePrinter(printer.deviceId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
