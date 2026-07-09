package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.core.graphics.createBitmap
import androidx.compose.foundation.border
import java.util.Random
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.CrisisController
import com.example.ui.components.CrisisSupportDialog
import com.example.ui.components.LocalCrisisController
import com.example.ui.screens.BoardScreen
import com.example.ui.screens.ChamberScreen
import com.example.ui.screens.InitiationScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DividerWhite
import com.example.ui.theme.Gold
import com.example.ui.theme.LightText
import com.example.ui.theme.RedAlert
import com.example.ui.theme.Surface
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate

@Composable
fun TracingBoardApp(
    viewModel: AshlarAppViewModel = viewModel()
) {
    val navController = rememberNavController()
    val crisisController = remember { CrisisController() }
    val initiated by viewModel.initiated.collectAsState()

    CompositionLocalProvider(LocalCrisisController provides crisisController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Charcoal,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Header(onNeedHelp = { crisisController.openManual() })

                when (initiated) {
                    // First run: the initiation rite. Help stays reachable via the header above.
                    false -> Box(modifier = Modifier.weight(1f)) {
                        InitiationScreen(onComplete = { intention, weight ->
                            viewModel.completeInitiation(intention, weight)
                        })
                    }
                    // Still loading — neutral space, so neither screen flashes on cold start.
                    null -> Spacer(modifier = Modifier.weight(1f))
                    // Initiated: the app proper.
                    true -> NavHost(
                    navController = navController,
                    startDestination = "board",
                    modifier = Modifier.weight(1f).padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    composable("board") { BoardScreen(viewModel) }
                    composable("chamber") { ChamberScreen(viewModel) }
                    composable("tools") {
                        val streak by viewModel.briefingStreak.collectAsState()
                        val entries by viewModel.aarEntries.collectAsState()
                        val plumb by viewModel.plumbSessions.collectAsState()
                        val gauge by viewModel.gaugeDaysComplete.collectAsState()
                        val recall by viewModel.recallSessions.collectAsState()
                        val degree = com.example.tools.Degrees.current(
                            com.example.tools.Degrees.score(
                                com.example.tools.WorkStats(
                                    briefingStreak = streak,
                                    journalEntries = entries.size,
                                    plumbSessions = plumb,
                                    gaugeDaysComplete = gauge,
                                    recallSessions = recall
                                )
                            )
                        )
                        ToolsScreen(
                            currentDegree = degree,
                            onPlumbComplete = { thought, reflection ->
                                viewModel.recordPlumbSession()
                                viewModel.addPlumbRecord(thought, reflection)
                            },
                            onGaugeDayComplete = { viewModel.recordGaugeDayComplete() },
                            onRecallHeld = { viewModel.recordRecallSession() },
                            onSquareSetIntention = { viewModel.setIntention(it) }
                        )
                    }
                }
                }
            }
        }

        FilmGrainOverlay()
        
        // Floating Bottom Nav — only once initiated (hidden during the rite).
        if (initiated == true) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.9f)
            ) {
                AshlarBottomNav(navController = navController)
            }
        }

        // Crisis support surface — rendered last so it sits above everything, including the
        // film grain and nav. Unconditional, tappable human help. See docs/VISION.md §8.
        CrisisSupportDialog(controller = crisisController)
    }
    }
}

@Composable
fun FilmGrainOverlay() {
    val brush = remember {
        val width = 256
        val height = 256
        val bitmap = createBitmap(width, height)
        val pixels = IntArray(width * height)
        val random = Random(42)
        for (i in pixels.indices) {
            val noise = random.nextInt(256)
            val alpha = random.nextInt(16) 
            pixels[i] = android.graphics.Color.argb(alpha, noise, noise, noise)
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        val imageBitmap = bitmap.asImageBitmap()
        ShaderBrush(ImageShader(imageBitmap, TileMode.Repeated, TileMode.Repeated))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
fun Header(onNeedHelp: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 8.dp)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "THE WORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "THE ASHLAR",
                    style = MaterialTheme.typography.titleLarge,
                    color = LightText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            // Always-available path to human help. Reachable from every screen.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(RedAlert.copy(alpha = 0.12f))
                    .border(1.dp, RedAlert.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .clickable { onNeedHelp() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "NEED HELP?",
                    style = MaterialTheme.typography.labelSmall,
                    color = RedAlert,
                    letterSpacing = 1.sp
                )
            }
        }
        HorizontalDivider(color = Slate.copy(alpha = 0.5f), modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun AshlarBottomNav(navController: NavHostController) {
    val items = listOf(
        NavItem("Board", "board", Icons.Default.Home),
        NavItem("Chamber", "chamber", Icons.Default.Delete),
        NavItem("Tools", "tools", Icons.Default.Build)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface.copy(alpha = 0.95f))
            .border(1.dp, DividerWhite.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
            .padding(vertical = 12.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Gold.copy(alpha = 0.2f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = if (selected) Gold else Silver,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = item.name.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) Gold else Silver,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 9.sp
                )
            }
        }
    }
}

data class NavItem(val name: String, val route: String, val icon: ImageVector)
