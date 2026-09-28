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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.osiris.core.AuthorizationState
import com.example.osiris.core.ConfinementAdjudicator
import com.example.osiris.core.DeployedGateStatus
import com.example.osiris.ui.OsirisUiState
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
fun ConfinementScreen(
    state: OsirisUiState,
    onSetProbeStatus: (String, Int, String) -> Unit,
    onResetAllPass: () -> Unit,
    onTogglePrereq: (String) -> Unit,
    onToggleFixtureSubstitute: (Boolean) -> Unit,
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
            // Confinement Adjudication Summary Card
            val sDeployedColor = when (state.sDeployedStatus) {
                DeployedGateStatus.TRUE -> OsirisEmerald
                DeployedGateStatus.FALSE -> OsirisRose
                DeployedGateStatus.BLOCKED -> OsirisAmber
                DeployedGateStatus.UNVERIFIED -> OsirisTextMuted
            }

            OsirisCard(
                borderColor = sDeployedColor,
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "S_DEPLOYED ADJUDICATION",
                            color = OsirisCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Formula: ⋀(A..J)_r (r=1..3) & K_integrity & L_independent",
                            color = OsirisTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    StatusPill(
                        text = "S_deployed = ${state.sDeployedStatus.name}",
                        statusColor = sDeployedColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusPill(
                        text = "K_integrity: ${if (state.evidenceIntegrityVerified) "TRUE" else "FALSE"}",
                        statusColor = if (state.evidenceIntegrityVerified) OsirisEmerald else OsirisRose
                    )
                    StatusPill(
                        text = "L_independent: ${if (state.independentAdjudicationVerified) "TRUE" else "FALSE"}",
                        statusColor = if (state.independentAdjudicationVerified) OsirisEmerald else OsirisRose
                    )
                }
            }
        }

        // Quick simulation actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onResetAllPass,
                    colors = ButtonDefaults.buttonColors(containerColor = OsirisEmeraldDim),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("All 30 PASS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onSetProbeStatus("B", 1, "FAIL") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Egress FAIL (B)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onSetProbeStatus("I", 2, "BLOCKED") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Syscall BLOCKED", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 30 Probe Confinement Matrix Grid
        item {
            OsirisCard {
                SectionHeader(
                    title = "30-Probe Confinement Matrix (10 Criteria × 3 Runs)",
                    subtitle = "Tap any cell to cycle status: PASS -> FAIL -> BLOCKED -> UNRUN"
                )

                Spacer(modifier = Modifier.height(12.dp))

                val scrollState = rememberScrollState()
                Column(modifier = Modifier.horizontalScroll(scrollState)) {
                    // Header row
                    Row(
                        modifier = Modifier
                            .background(OsirisSurfaceElevated)
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Crit", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                        Text("Description", color = OsirisTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
                        Text("Run 1", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                        Text("Run 2", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                        Text("Run 3", color = OsirisCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                    }

                    ConfinementAdjudicator.MANDATORY_CONFINEMENT_CRITERIA.forEach { crit ->
                        val desc = ConfinementAdjudicator.CRITERIA_DESCRIPTIONS[crit] ?: ""
                        Row(
                            modifier = Modifier
                                .border(0.5.dp, OsirisBorder.copy(alpha = 0.5f))
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                crit,
                                color = OsirisCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp)
                            )
                            Text(
                                desc,
                                color = OsirisTextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.width(180.dp),
                                maxLines = 1
                            )

                            listOf(1, 2, 3).forEach { run ->
                                val probe = state.probeResults[crit to run]
                                val status = probe?.status ?: "UNRUN"
                                val cellColor = when (status) {
                                    "PASS" -> OsirisEmerald
                                    "FAIL" -> OsirisRose
                                    "BLOCKED" -> OsirisAmber
                                    else -> OsirisTextMuted
                                }

                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(cellColor.copy(alpha = 0.2f))
                                        .border(1.dp, cellColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .clickable {
                                            val nextStatus = when (status) {
                                                "PASS" -> "FAIL"
                                                "FAIL" -> "BLOCKED"
                                                "BLOCKED" -> "UNRUN"
                                                else -> "PASS"
                                            }
                                            onSetProbeStatus(crit, run, nextStatus)
                                        }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        status,
                                        color = cellColor,
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

        // Master Release Gate & Prerequisites
        item {
            val gateColor = when (state.releaseGateStatus) {
                AuthorizationState.ELIGIBLE -> OsirisEmerald
                else -> OsirisRose
            }

            OsirisCard(borderColor = gateColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MASTER RELEASE GATE",
                            color = OsirisCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Authority boundary: Fail-closed evaluation",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusPill(
                        text = state.releaseGateStatus.name,
                        statusColor = gateColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Non-Substitution Guard Breaker Switch (Testing Boundary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261014))
                        .border(1.dp, OsirisRose.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Fixture May Substitute For Deployed",
                            color = OsirisRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "INVARIANT BREACH: Must remain OFF. Enabling triggers SubstitutionViolationError.",
                            color = OsirisTextMuted,
                            fontSize = 9.sp
                        )
                    }
                    Switch(
                        checked = state.fixtureMaySubstituteForDeployed,
                        onCheckedChange = onToggleFixtureSubstitute,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OsirisRose,
                            checkedTrackColor = Color(0xFF7F1D1D)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Mandatory Release Prerequisites (7/7 Required):",
                    color = OsirisTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                ConfinementAdjudicator.MANDATORY_RELEASE_PREREQUISITES.forEach { prereq ->
                    val isChecked = state.releasePrerequisites[prereq] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePrereq(prereq) }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onTogglePrereq(prereq) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OsirisEmerald,
                                uncheckedColor = OsirisBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            prereq,
                            color = if (isChecked) OsirisTextPrimary else OsirisTextMuted,
                            fontSize = 11.sp,
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
