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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.osiris.core.PrinterNode
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
fun FleetScreen(
    fleet: List<PrinterNode>,
    isWifiConnected: Boolean,
    wifiSsid: String,
    isScanning: Boolean,
    onConnectWifi: () -> Unit,
    onScanSubnet: () -> Unit,
    onTogglePrinter: (String) -> Unit,
    onSelectAll: (Boolean) -> Unit,
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
            // Wi-Fi Onboarding & Subnet Scanner Card
            OsirisCard(
                borderColor = if (isWifiConnected) OsirisEmerald.copy(alpha = 0.6f) else OsirisCyan,
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isWifiConnected) OsirisEmerald else OsirisCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "LOCAL FACTORY WI-FI ONBOARDING",
                                color = OsirisCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                if (isWifiConnected) "Bound to '$wifiSsid' (Subnet 192.168.10.0/24)" else "Disconnected from print farm subnet",
                                color = OsirisTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    StatusPill(
                        text = if (isWifiConnected) "CONNECTED" else "OFFLINE",
                        statusColor = if (isWifiConnected) OsirisEmerald else OsirisAmber
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onConnectWifi,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWifiConnected) OsirisSurfaceElevated else OsirisCyan,
                            contentColor = if (isWifiConnected) OsirisTextPrimary else Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("wifi_signon_button")
                    ) {
                        Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isWifiConnected) "Re-authenticate Wi-Fi" else "Sign onto Wi-Fi Network",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onScanSubnet,
                        enabled = isWifiConnected && !isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OsirisViolet,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("scan_subnet_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scanning mDNS...", fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("mDNS Discover", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Fleet Overview & Batch Controls
        item {
            val selectedCount = fleet.count { it.isSelected }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Active Fleet Fabric (${fleet.size} Nodes)",
                        color = OsirisTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "$selectedCount of ${fleet.size} nodes bound to OSIRIS cluster",
                        color = OsirisTextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Select All",
                        color = OsirisCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onSelectAll(selectedCount < fleet.size) }
                    )
                }
            }
        }

        // Printer Cards
        items(fleet, key = { it.deviceId }) { printer ->
            PrinterNodeCard(
                printer = printer,
                onToggleSelect = { onTogglePrinter(printer.deviceId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PrinterNodeCard(
    printer: PrinterNode,
    onToggleSelect: () -> Unit
) {
    val modelColor = when (printer.model) {
        "P1S" -> OsirisCyan
        "H2C" -> OsirisViolet
        "A1" -> OsirisEmerald
        else -> OsirisAmber
    }

    val statusColor = when (printer.status) {
        "PRINTING" -> OsirisEmerald
        "IDLE" -> OsirisCyan
        "PAUSED" -> OsirisAmber
        else -> OsirisRose
    }

    OsirisCard(
        borderColor = if (printer.isSelected) modelColor.copy(alpha = 0.6f) else OsirisBorder,
        backgroundColor = if (printer.isSelected) OsirisSurfaceCard else Color(0xFF0D131F)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = printer.isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = modelColor,
                        uncheckedColor = OsirisBorder
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            printer.name,
                            color = OsirisTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusPill(text = printer.model, statusColor = modelColor)
                    }
                    Text(
                        "${printer.ipAddress} • ${printer.enclosureType} • ${printer.nozzleMaterial} (${printer.nozzleSizeMm}mm)",
                        color = OsirisTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            StatusPill(text = printer.status, statusColor = statusColor)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Telemetry stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(OsirisSurfaceElevated)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nozzle", color = OsirisTextMuted, fontSize = 9.sp)
                Text("${printer.nozzleTemp.toInt()}°C", color = OsirisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Bed", color = OsirisTextMuted, fontSize = 9.sp)
                Text("${printer.bedTemp.toInt()}°C", color = OsirisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            printer.chamberTemp?.let {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Chamber", color = OsirisTextMuted, fontSize = 9.sp)
                    Text("${it.toInt()}°C", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Progress", color = OsirisTextMuted, fontSize = 9.sp)
                Text("${printer.progressPercent.toInt()}%", color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (printer.status == "PRINTING") {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (printer.progressPercent / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = OsirisEmerald,
                trackColor = OsirisSurfaceElevated
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AMS Slot Visualizer
        Text(
            "AMS Filament Slots (${printer.amsUnits.size * 4} channels):",
            color = OsirisTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        printer.amsUnits.forEach { ams ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..4).forEach { slotNum ->
                    val spool = ams.slots[slotNum]
                    val spoolColor = try {
                        Color(android.graphics.Color.parseColor(spool?.colorHex ?: "#334155"))
                    } catch (e: Exception) {
                        Color.Gray
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(OsirisSurfaceElevated)
                            .border(1.dp, if (spool != null) spoolColor else OsirisBorder, RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (spool != null) spoolColor else Color.Transparent)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "S$slotNum",
                                    color = OsirisTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                spool?.properties?.materialType ?: "EMPTY",
                                color = if (spool != null) OsirisTextPrimary else OsirisTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            if (spool != null) {
                                Text(
                                    "${spool.remainingWeightG.toInt()}g",
                                    color = OsirisTextMuted,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
