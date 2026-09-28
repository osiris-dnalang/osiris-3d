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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Verified
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
fun CanonicalInspectorScreen(
    state: OsirisUiState,
    onInspectJson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var jsonText by remember {
        mutableStateOf(
            """{
  "action_kind": "REPLAY_FIXTURE",
  "arguments": {
    "fixture_id": "echo-v1"
  },
  "capability": "REPLAY",
  "created_at_utc": "2026-09-26T12:00:00Z",
  "expires_at_utc": "2026-09-26T13:00:00Z",
  "fixture_id": "echo-v1",
  "nonce": "nonce-demo-100",
  "proposal_id": "prop-001",
  "proposer_id": "livlm-client",
  "request_id": "req-001",
  "schema_version": "action-proposal/v1"
}"""
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Canonical Invariant Specs
            OsirisCard(
                borderColor = OsirisCyan.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF0F1A2A)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        tint = OsirisCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "OSIRIS-CANONICAL-JSON-V1",
                        color = OsirisCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Deterministic Byte Binding & Invariant Hashing",
                    color = OsirisTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Decisions bind strictly to exact serialized bytes via SHA-256. Forbidden: Floats, NaN, duplicate keys, non-ASCII keys, BOM, bidi control characters. Unicode NFC normalized.",
                    color = OsirisTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Preset Attack & Validation Vectors
        item {
            OsirisCard {
                Text(
                    "Test Invariant Vectors:",
                    color = OsirisCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "Valid Proposal" to """{
  "action_kind": "REPLAY_FIXTURE",
  "capability": "REPLAY",
  "fixture_id": "echo-v1",
  "nonce": "nonce-demo-100",
  "proposal_id": "prop-001"
}""",
                        "Float Forbidden (12.34)" to """{
  "metric": 12.34,
  "proposal_id": "prop-float-test"
}""",
                        "Bidi Attack (\\u200E)" to "{\n  \"message\": \"test\\u200Einjection\"\n}",
                        "Duplicate Key Violation" to "{\n  \"key\": \"value1\",\n  \"key\": \"value2\"\n}"
                    )

                    presets.forEach { (label, json) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(OsirisSurfaceElevated)
                                .border(1.dp, OsirisBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    jsonText = json
                                    onInspectJson(json)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = OsirisTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Editor & Inspector
        item {
            OsirisCard {
                SectionHeader(
                    title = "Interactive Canonical Serializer",
                    subtitle = "Edits recalculate canonical bytes and SHA-256 digest in real time."
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = {
                        jsonText = it
                        onInspectJson(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OsirisCyan,
                        unfocusedBorderColor = OsirisBorder,
                        focusedTextColor = OsirisTextPrimary,
                        unfocusedTextColor = OsirisTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onInspectJson(jsonText) },
                    colors = ButtonDefaults.buttonColors(containerColor = OsirisCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Re-validate & Hash Payload", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Diagnostic Results
        state.canonicalDiagnostic?.let { diag ->
            item {
                val statusColor = if (diag.isValid) OsirisEmerald else OsirisRose
                OsirisCard(borderColor = statusColor) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (diag.isValid) Icons.Default.Verified else Icons.Default.Error,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (diag.isValid) "CANONICAL INVARIANTS SATISFIED" else "INVARIANT VIOLATION DETECTED",
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusPill(
                            text = if (diag.isValid) "VALID" else "REJECTED",
                            statusColor = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (diag.isValid) {
                        Text(
                            "Canonical Byte Length: ${diag.canonicalBytesLength} bytes",
                            color = OsirisTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        DigestChip(label = "Binding SHA-256", digest = diag.canonicalSha256)
                    } else {
                        Text(
                            "Diagnostic Errors:",
                            color = OsirisRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        diag.errors.forEach { err ->
                            Text(
                                "• $err",
                                color = OsirisRose,
                                fontSize = 11.sp,
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
