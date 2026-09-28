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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.osiris.core.ClaimStatus
import com.example.osiris.core.ClaimType
import com.example.osiris.core.PERMITTED_PLANES_BY_CLAIM_TYPE
import com.example.osiris.ui.OsirisUiState
import com.example.osiris.ui.components.DigestChip
import com.example.osiris.ui.components.OsirisCard
import com.example.osiris.ui.components.SectionHeader
import com.example.osiris.ui.components.StatusPill
import com.example.osiris.ui.theme.OsirisAmber
import com.example.osiris.ui.theme.OsirisBorder
import com.example.osiris.ui.theme.OsirisCyan
import com.example.osiris.ui.theme.OsirisEmerald
import com.example.osiris.ui.theme.OsirisEmeraldDim
import com.example.osiris.ui.theme.OsirisRose
import com.example.osiris.ui.theme.OsirisSurfaceCard
import com.example.osiris.ui.theme.OsirisSurfaceElevated
import com.example.osiris.ui.theme.OsirisTextMuted
import com.example.osiris.ui.theme.OsirisTextPrimary
import com.example.osiris.ui.theme.OsirisTextSecondary
import com.example.osiris.ui.theme.OsirisViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClaimEvaluatorScreen(
    state: OsirisUiState,
    onEvaluateClaim: (ClaimType, String, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedClaimType by remember { mutableStateOf(ClaimType.RUNTIME_EGRESS_BLOCKED) }
    var subject by remember { mutableStateOf("runtime-seccomp-egress") }
    var selectedEventIds by remember {
        val initialFirst = state.events.firstOrNull()?.eventId
        mutableStateOf(if (initialFirst != null) setOf(initialFirst) else emptySet())
    }

    val permittedPlanes = remember(selectedClaimType) {
        PERMITTED_PLANES_BY_CLAIM_TYPE[selectedClaimType] ?: emptySet()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Non-Substitution Invariant Explainer
            OsirisCard(
                borderColor = OsirisViolet.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF131326)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = OsirisViolet,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "NON-SUBSTITUTION INVARIANT",
                        color = OsirisViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Evidence Class A -/-> Claim Class B",
                    color = OsirisTextPrimary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Scientific claim verification cannot substitute for runtime security controls. Adjudication Precedence: FALSE > BLOCKED > UNVERIFIED > TRUE.",
                    color = OsirisTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Testbench Configuration Card
        item {
            OsirisCard {
                SectionHeader(
                    title = "Adjudication Testbench",
                    subtitle = "Select a claim type, assign subject, and cite ledger evidence items."
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("1. Target Claim Type:", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ClaimType.values().forEach { claimType ->
                        val isSelected = selectedClaimType == claimType
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) OsirisCyan else OsirisSurfaceElevated)
                                .border(1.dp, if (isSelected) OsirisCyan else OsirisBorder, RoundedCornerShape(14.dp))
                                .clickable { selectedClaimType = claimType }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                claimType.name,
                                color = if (isSelected) Color.Black else OsirisTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Permitted planes warning/indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(
                        "Permitted Supporting Planes: ${permittedPlanes.joinToString(", ") { it.name }}",
                        color = OsirisEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("2. Claim Subject:", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject assertion identifier") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("3. Cite Evidence from Ledger:", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.events.forEach { event ->
                        val isCited = selectedEventIds.contains(event.eventId)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCited) OsirisEmeraldDim else OsirisSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isCited) OsirisEmerald else OsirisBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedEventIds = if (isCited) {
                                        selectedEventIds - event.eventId
                                    } else {
                                        selectedEventIds + event.eventId
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text(
                                    "${if (isCited) "✓ " else ""}${event.eventId}",
                                    color = if (isCited) Color.White else OsirisTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    event.plane.name,
                                    color = if (isCited) OsirisTextPrimary else OsirisTextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onEvaluateClaim(selectedClaimType, subject, selectedEventIds.toList())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsirisViolet,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("evaluate_claim_button")
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adjudicate Claim Against Cited Evidence", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Adjudication Result Card
        state.claimEvaluationResult?.let { result ->
            item {
                val statusColor = when (result.status) {
                    ClaimStatus.TRUE -> OsirisEmerald
                    ClaimStatus.FALSE -> OsirisRose
                    ClaimStatus.BLOCKED -> OsirisAmber
                    ClaimStatus.UNVERIFIED -> OsirisTextMuted
                }

                OsirisCard(
                    borderColor = statusColor,
                    backgroundColor = Color(0xFF0F1A2A)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ADJUDICATION RESULT",
                            color = OsirisCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        StatusPill(
                            text = result.status.name,
                            statusColor = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Subject: ${result.subject}",
                        color = OsirisTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Claim Type: ${result.claimType.name}",
                        color = OsirisTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (result.violations.isNotEmpty()) {
                        Text(
                            "Detected Invariant Violations:",
                            color = OsirisRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.violations.forEach { violation ->
                                StatusPill(
                                    text = violation.name,
                                    statusColor = OsirisRose
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    DigestChip(
                        label = "Evaluation Event",
                        digest = result.evaluationEventId
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Cited Evidence: ${result.citedEvidenceIds.joinToString(", ").ifEmpty { "None" }}",
                        color = OsirisTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
