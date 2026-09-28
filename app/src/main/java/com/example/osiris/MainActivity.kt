package com.example.osiris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.osiris.ui.OsirisViewModel
import com.example.osiris.ui.components.StatusPill
import com.example.osiris.ui.screens.DevOpsSlicerScreen
import com.example.osiris.ui.screens.FilamentMatrixScreen
import com.example.osiris.ui.screens.FleetDashboardScreen
import com.example.osiris.ui.screens.FleetScreen
import com.example.osiris.ui.screens.FoundryScreen
import com.example.osiris.ui.screens.GatewayScreen
import com.example.osiris.ui.screens.LedgerScreen
import com.example.osiris.ui.screens.PipelineScreen
import com.example.osiris.ui.theme.OsirisBg
import com.example.osiris.ui.theme.OsirisCyan
import com.example.osiris.ui.theme.OsirisEmerald
import com.example.osiris.ui.theme.OsirisGovernanceTheme
import com.example.osiris.ui.theme.OsirisRose
import com.example.osiris.ui.theme.OsirisSurface
import com.example.osiris.ui.theme.OsirisTextMuted
import com.example.osiris.ui.theme.OsirisTextPrimary
import com.example.osiris.ui.theme.OsirisViolet

enum class AppScreen(val title: String, val icon: ImageVector) {
    FOUNDRY("Foundry", Icons.Default.AutoAwesome),
    FLEET("Fleet", Icons.Default.DeviceHub),
    GATEWAY("Gateway", Icons.Default.Hub),
    SLICER("Slicer", Icons.Default.Build),
    PIPELINE("Pipeline", Icons.Default.Engineering),
    LEDGER("Ledger", Icons.Default.ReceiptLong)
}

class MainActivity : ComponentActivity() {
    private val viewModel: OsirisViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OsirisGovernanceTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                var currentScreen by remember { mutableStateOf(AppScreen.FOUNDRY) }

                BackHandler(enabled = currentScreen != AppScreen.FOUNDRY) {
                    currentScreen = AppScreen.FOUNDRY
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = OsirisBg,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Foundry Icon",
                                        tint = OsirisViolet,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "OSIRIS Foundry",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OsirisTextPrimary
                                    )
                                }
                            },
                            actions = {
                                StatusPill(
                                    text = "NCLM ACTIVE",
                                    statusColor = OsirisEmerald,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = OsirisBg
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = OsirisSurface,
                            tonalElevation = 8.dp
                        ) {
                            AppScreen.values().forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = OsirisBg,
                                        selectedTextColor = OsirisCyan,
                                        indicatorColor = OsirisCyan,
                                        unselectedIconColor = OsirisTextMuted,
                                        unselectedTextColor = OsirisTextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.FOUNDRY -> FoundryScreen(
                                models = state.makerWorldModels,
                                telemetry = state.ingestionTelemetry,
                                isIngesting = state.isIngesting,
                                customization = state.selectedCustomization,
                                p1sDiagnostics = state.p1sDiagnostics,
                                filamentScanResult = state.filamentScanResult,
                                onTriggerIngestion = { viewModel.triggerIngestionSweep() },
                                onSearchModels = { viewModel.searchMakerWorldModels(it) },
                                onSelectModelForPrint = { model ->
                                    viewModel.selectModelForPrint(model)
                                    currentScreen = AppScreen.SLICER
                                },
                                onSelectModelForCustomization = { model ->
                                    viewModel.selectModelForCustomization(model)
                                },
                                onUpdateCustomization = { thickness, walls, infill, clearance, mat ->
                                    viewModel.updateParametricCustomization(thickness, walls, infill, clearance, mat)
                                },
                                onAutoFixP1SDiagnostics = {
                                    viewModel.autoFixP1SDiagnostics()
                                },
                                onScanFleetAndOptimize = { model ->
                                    viewModel.scanFleetAndOptimizeModel(model)
                                },
                                onDispatchOptimizedModel = { result ->
                                    viewModel.dispatchOptimizedModel(result)
                                },
                                onDismissScanResult = {
                                    viewModel.dismissScanResult()
                                }
                            )
                            AppScreen.FLEET -> FleetDashboardScreen(
                                fleet = state.fleet,
                                isWifiConnected = state.isWifiConnected,
                                wifiSsid = state.wifiSsid,
                                isScanning = state.isScanning,
                                onConnectWifi = { viewModel.connectWifi() },
                                onScanSubnet = { viewModel.scanSubnet() },
                                onTogglePrinter = { viewModel.togglePrinter(it) },
                                onSelectAll = { viewModel.selectAllPrinters(it) },
                                onPausePrinter = { viewModel.pausePrinter(it) },
                                onStopPrinter = { viewModel.stopPrinter(it) },
                                onRefreshTelemetry = { viewModel.refreshFleetTelemetry() }
                            )
                            AppScreen.GATEWAY -> GatewayScreen(
                                status = state.gatewayStatus,
                                cacheEntries = state.cacheEntries,
                                opticalRecord = state.opticalRecord,
                                recoveryRecord = state.recoveryRecord,
                                onToggleGatewayPower = { viewModel.toggleGatewayPower() },
                                onTestFastApiDispatch = { viewModel.testFastApiDispatch() },
                                onToggleBrownout = { viewModel.toggleSimulatedBrownout(it) },
                                onRunOpticalScan = { viewModel.runOpticalScan() },
                                onInjectSpaghetti = { viewModel.injectSpaghetti() },
                                onExecuteFailover = { viewModel.executeFailover() }
                            )
                            AppScreen.SLICER -> DevOpsSlicerScreen(
                                plan = state.activeJobPlan,
                                onGeneratePlan = { viewModel.generateJobPlan(it) },
                                onLaunchPipeline = {
                                    viewModel.launchPipeline()
                                    currentScreen = AppScreen.PIPELINE
                                }
                            )
                            AppScreen.PIPELINE -> PipelineScreen(
                                currentStage = state.pipelineStage,
                                operatorPrompt = state.operatorPrompt,
                                terminalLogs = state.terminalLogs,
                                onConfirmOperatorLoad = { viewModel.confirmOperatorLoad() },
                                onResetPipeline = { viewModel.resetPipeline() }
                            )
                            AppScreen.LEDGER -> LedgerScreen(
                                state = state
                            )
                        }

                        // Floating error banner if any
                        state.errorMessage?.let { error ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF261014))
                                    .border(1.dp, OsirisRose, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = OsirisRose,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = error,
                                        color = OsirisRose,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearError() },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = OsirisTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
