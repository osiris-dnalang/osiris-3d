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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Link
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
import com.example.osiris.core.EvidencePlane
import com.example.osiris.core.LedgerEvent
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
fun LedgerScreen(
    state: OsirisUiState,
    modifier: Modifier = Modifier
) {
    var selectedPlane by remember { mutableStateOf<EvidencePlane?>(null) }

    val filteredEvents = remember(state.events, selectedPlane) {
        if (selectedPlane == null) state.events.reversed()
        else state.events.filter { it.plane == selectedPlane }.reversed()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Cryptographic Integrity Banner
            OsirisCard(
                borderColor = if (state.chainIntegrityValid) OsirisEmerald.copy(alpha = 0.6f) else OsirisRose,
                backgroundColor = Color(0xFF0C1929)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Chain Verified",
                        tint = if (state.chainIntegrityValid) OsirisEmerald else OsirisRose,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (state.chainIntegrityValid) "CRYPTOGRAPHIC INTEGRITY: UNBROKEN" else "CHAIN CORRUPTED",
                            color = if (state.chainIntegrityValid) OsirisEmerald else OsirisRose,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.events.size} Append-Only Blocks • Genesis Bound",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                DigestChip(label = "Head Digest", digest = state.latestDigest)
            }
        }

        // Plane Filter Chips
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        tint = OsirisTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Filter by Evidence Plane:", color = OsirisTextMuted, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isAll = selectedPlane == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isAll) OsirisCyan else OsirisSurfaceElevated)
                            .border(1.dp, if (isAll) OsirisCyan else OsirisBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedPlane = null }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            "ALL (${state.events.size})",
                            color = if (isAll) Color.Black else OsirisTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    EvidencePlane.values().forEach { plane ->
                        val isSelected = selectedPlane == plane
                        val count = state.events.count { it.plane == plane }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) OsirisCyan else OsirisSurfaceElevated)
                                .border(1.dp, if (isSelected) OsirisCyan else OsirisBorder, RoundedCornerShape(16.dp))
                                .clickable { selectedPlane = plane }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "${plane.name} ($count)",
                                color = if (isSelected) Color.Black else OsirisTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Events List
        items(filteredEvents, key = { it.eventId }) { event ->
            LedgerEventCard(event = event)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun LedgerEventCard(event: LedgerEvent) {
    var expanded by remember { mutableStateOf(false) }

    val planeColor = when (event.plane) {
        EvidencePlane.REPRODUCIBILITY -> OsirisCyan
        EvidencePlane.RUNTIME_SECURITY -> OsirisRose
        EvidencePlane.SCIENTIFIC_EXPERIMENTAL -> OsirisAmber
        EvidencePlane.HARDWARE_EXECUTION -> OsirisViolet
        EvidencePlane.GOVERNANCE -> OsirisEmerald
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
                Text(
                    text = event.eventId,
                    color = OsirisTextPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusPill(
                    text = event.eventType.name,
                    statusColor = OsirisCyan
                )
            }
            StatusPill(
                text = event.plane.name,
                statusColor = planeColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Actor: ${event.actor["id"] ?: "system"}",
                color = OsirisTextSecondary,
                fontSize = 11.sp
            )
            Text(
                event.recordedAt.take(19).replace("T", " "),
                color = OsirisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DigestChip(label = "Digest", digest = event.recordDigest)

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Link,
                contentDescription = "Chained to",
                tint = OsirisTextMuted,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Prev: ${event.previousRecordDigest.take(24)}...",
                color = OsirisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (event.evidenceRefs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Cited Refs: ${event.evidenceRefs.joinToString(", ")}",
                color = OsirisAmber,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expand/Collapse Payload
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(OsirisSurfaceElevated)
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (expanded) "Hide Payload ▼" else "View Payload (${event.payload.size} fields) ▶",
                        color = OsirisCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (expanded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    event.payload.forEach { (k, v) ->
                        Row(modifier = Modifier.padding(vertical = 1.dp)) {
                            Text("$k: ", color = OsirisTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(v, color = OsirisEmerald, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
