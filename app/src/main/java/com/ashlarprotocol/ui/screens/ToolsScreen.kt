package com.ashlarprotocol.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.ui.theme.Charcoal
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import com.ashlarprotocol.tools.TILTS
import com.ashlarprotocol.tools.PlumbEntry
import com.ashlarprotocol.tools.composeSquaredReflection
import com.ashlarprotocol.tools.Gauge
import com.ashlarprotocol.tools.GaugeItem
import com.ashlarprotocol.tools.DayPart
import com.ashlarprotocol.tools.MouthToEar
import com.ashlarprotocol.tools.DEFAULT_PRINCIPLES
import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Degrees
import com.ashlarprotocol.data.CornerstoneEntry
import com.ashlarprotocol.ui.components.TheCornerstone
import com.ashlarprotocol.data.RoughEdgeEntry
import com.ashlarprotocol.ui.components.TheRoughEdge
import com.ashlarprotocol.tools.BreathPacer
import com.ashlarprotocol.tools.BreathPattern
import com.ashlarprotocol.tools.BreathPhase
import com.ashlarprotocol.tools.Square
import com.ashlarprotocol.tools.Trowel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

data class TamilChallenge(val question: String, val answer: String)

val TAMIL_CHALLENGES = listOf(
    TamilChallenge("க் + ஆ =", "கா"),
    TamilChallenge("ச் + இ =", "சி"),
    TamilChallenge("ட் + உ =", "டு"),
    TamilChallenge("ந் + ஏ =", "நே")
)

@Composable
fun ToolsScreen(
    currentDegree: Degree = Degree.ENTERED_APPRENTICE,
    onPlumbComplete: (thought: String, reflection: String) -> Unit = { _, _ -> },
    onGaugeDayComplete: () -> Unit = {},
    onRecallHeld: () -> Unit = {},
    onSquareSetIntention: (String) -> Unit = {},
    onTrowelKeep: (String) -> Unit = {},
    existingCornerstone: CornerstoneEntry? = null,
    onCornerstoneSave: (CornerstoneEntry) -> Unit = {},
    existingRoughEdge: RoughEdgeEntry? = null,
    onRoughEdgeSet: (name: String, cue: String, environmentMove: String, replacement: String) -> Unit = { _, _, _, _ -> },
    onRoughEdgeLapse: () -> Unit = {}
) {
    var activeTool by remember { mutableStateOf("menu") } // menu, gavel, plumb, gauge, level, mouth, cornerstone

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (activeTool == "menu") {
            // Tools are received along the path, not handed over all at once. The Apprentice's tools
            // (structure, habit, and breath — the last kept open as basic grounding) are always here;
            // the later tools stay veiled until the degree that needs them is earned. See VISION §6.
            item {
                Text(
                    "THE WORKING TOOLS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.4f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            item { ToolMenuItem("cornerstone", "The Cornerstone", "Square Your Surroundings", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("roughedge", "The Rough Edge", "Work One Bad Habit", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("gauge", "The Gauge", "Divide the Day", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("gavel", "The Gavel", "Sharpen the Mind", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("level", "The Level", "Steady the Breath", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("square", "The Square", "Square to Your Values", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("trowel", "The Trowel", "Spread the Cement Inward", Degree.ENTERED_APPRENTICE, currentDegree) { activeTool = it } }
            item { ToolMenuItem("plumb", "The Plumb", "Straighten a Thought", Degree.FELLOWCRAFT, currentDegree) { activeTool = it } }
            item { ToolMenuItem("mouth", "Mouth to Ear", "Memory Work", Degree.MASTER_MASON, currentDegree) { activeTool = it } }
        } else {
            item {
                Button(
                    onClick = { activeTool = "menu" },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate.copy(alpha = 0.3f), contentColor = Silver),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Text("RETURN TO TOOLS", style = MaterialTheme.typography.labelSmall)
                }
            }

            item {
                when (activeTool) {
                    "cornerstone" -> TheCornerstone(existing = existingCornerstone, onSave = onCornerstoneSave)
                    "roughedge" -> TheRoughEdge(existing = existingRoughEdge, onSet = onRoughEdgeSet, onLapse = onRoughEdgeLapse)
                    "gavel" -> TheGavel()
                    "plumb" -> ThePlumb(onComplete = onPlumbComplete)
                    "gauge" -> TheGauge(onDayComplete = onGaugeDayComplete)
                    "level" -> TheLevel()
                    "square" -> TheSquare(onSetIntention = onSquareSetIntention)
                    "trowel" -> TheTrowel(onKeep = onTrowelKeep)
                    "mouth" -> MouthToEarTool(onRecallHeld = onRecallHeld)
                }
            }
        }
    }
}

/** Renders a tool as an open, tappable card if earned — otherwise as a veiled, locked card. */
@Composable
private fun ToolMenuItem(
    key: String,
    title: String,
    subtitle: String,
    required: Degree,
    current: Degree,
    onOpen: (String) -> Unit
) {
    if (Degrees.isUnlocked(required, current)) {
        ToolMenuCard(title = title, subtitle = subtitle) { onOpen(key) }
    } else {
        LockedToolCard(title = title, required = required)
    }
}

@Composable
fun ToolMenuCard(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .background(com.ashlarprotocol.ui.theme.Surface.copy(alpha = 0.8f))
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title.uppercase(), style = MaterialTheme.typography.titleLarge, color = Gold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle.uppercase(), style = MaterialTheme.typography.labelSmall, color = Silver)
        }
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Build, // Placeholder icon
            contentDescription = null,
            tint = Gold.copy(alpha = 0.5f)
        )
    }
}

/** A tool not yet earned: shown but veiled, naming the degree that will confer it. Not tappable. */
@Composable
private fun LockedToolCard(title: String, required: Degree) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .background(com.ashlarprotocol.ui.theme.Surface.copy(alpha = 0.35f))
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title.uppercase(), style = MaterialTheme.typography.titleLarge, color = Silver.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "AWAITS THE ${required.display.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Lock,
            contentDescription = "Locked until the ${required.display}",
            tint = Silver.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun TheGavel() {
    var taskIndex by remember { mutableStateOf(0) }
    var active by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface.copy(alpha = 0.8f))
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("THE GAVEL", style = MaterialTheme.typography.titleLarge, color = Gold)
        Spacer(modifier = Modifier.height(24.dp))

        if (!active) {
            Button(
                onClick = { active = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.1f), contentColor = Gold),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
            ) {
                Text("BEGIN THE DRILL", style = MaterialTheme.typography.labelSmall)
            }
        } else {
            Text(
                text = TAMIL_CHALLENGES[taskIndex].question,
                style = MaterialTheme.typography.titleLarge,
                color = Silver,
                fontSize = 48.dp.value.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            val options = listOf("கா", "சி", "டு", "நே")
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    GavelOption(options[0], modifier = Modifier.weight(1f)) { taskIndex = (taskIndex + 1) % 4 }
                    GavelOption(options[1], modifier = Modifier.weight(1f)) { taskIndex = (taskIndex + 1) % 4 }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    GavelOption(options[2], modifier = Modifier.weight(1f)) { taskIndex = (taskIndex + 1) % 4 }
                    GavelOption(options[3], modifier = Modifier.weight(1f)) { taskIndex = (taskIndex + 1) % 4 }
                }
            }
        }
    }
}

@Composable
fun GavelOption(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(Slate)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleLarge, color = Gold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThePlumb(onComplete: (thought: String, reflection: String) -> Unit = { _, _ -> }) {
    // A four-step CBT thought-record. Pure logic lives in com.ashlarprotocol.tools.PlumbLine (tested);
    // this composable only gathers the person's own words and reflects them back "squared".
    var step by remember { mutableStateOf(0) } // 0 situation, 1 thought, 2 tilts, 3 evidence, 4 result
    var situation by remember { mutableStateOf("") }
    var thought by remember { mutableStateOf("") }
    val selectedTilts = remember { mutableStateListOf<String>() }
    var evidence by remember { mutableStateOf("") }

    fun reset() {
        step = 0; situation = ""; thought = ""; selectedTilts.clear(); evidence = ""
    }

    // Count one plumb session when the person reaches the "squared" reflection.
    LaunchedEffect(step) {
        if (step == 4) {
            val reflection = composeSquaredReflection(
                PlumbEntry(situation = situation, thought = thought, tiltIds = selectedTilts.toList(), evidence = evidence)
            )
            onComplete(thought, reflection)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("THE PLUMB", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("DOES THIS THOUGHT STAND TRUE, OR IS IT LEANING?", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))

        if (step < 4) {
            Text(
                text = "STEP ${step + 1} OF 4",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.4f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        when (step) {
            0 -> {
                Text("What happened? Set the scene in a line.", style = MaterialTheme.typography.bodyMedium, color = Silver)
                Spacer(modifier = Modifier.height(12.dp))
                PlumbField(situation, { situation = it }, "e.g. Sent a message, no reply yet…")
                Spacer(modifier = Modifier.height(16.dp))
                PlumbPrimary("NEXT", enabled = situation.isNotBlank()) { step = 1 }
            }
            1 -> {
                Text("What's the thought pulling you off-plumb?", style = MaterialTheme.typography.bodyMedium, color = Silver)
                Spacer(modifier = Modifier.height(12.dp))
                PlumbField(thought, { thought = it }, "e.g. They must be angry with me…")
                Spacer(modifier = Modifier.height(16.dp))
                PlumbNav(onBack = { step = 0 }, forwardLabel = "NEXT", forwardEnabled = thought.isNotBlank()) { step = 2 }
            }
            2 -> {
                Text("Is it leaning? Name the tilt — or skip if it stands true.", style = MaterialTheme.typography.bodyMedium, color = Silver)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TILTS.forEach { tilt ->
                        val selected = selectedTilts.contains(tilt.id)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .background(if (selected) Gold.copy(alpha = 0.15f) else Slate.copy(alpha = 0.2f))
                                .border(
                                    1.dp,
                                    if (selected) Gold.copy(alpha = 0.5f) else Slate,
                                    androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (selected) selectedTilts.remove(tilt.id) else selectedTilts.add(tilt.id)
                                }
                                .padding(12.dp)
                        ) {
                            Text(tilt.name, style = MaterialTheme.typography.bodyMedium, color = if (selected) Gold else Silver)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(tilt.description, style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.6f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PlumbNav(onBack = { step = 1 }, forwardLabel = "NEXT", forwardEnabled = true) { step = 3 }
            }
            3 -> {
                Text("What's true that the thought leaves out? Weigh the other side.", style = MaterialTheme.typography.bodyMedium, color = Silver)
                Spacer(modifier = Modifier.height(12.dp))
                PlumbField(evidence, { evidence = it }, "e.g. They replied warmly yesterday; they said they were busy…")
                Spacer(modifier = Modifier.height(16.dp))
                PlumbNav(onBack = { step = 2 }, forwardLabel = "CHECK THE PLUMB", forwardEnabled = true) { step = 4 }
            }
            else -> {
                val reflection = composeSquaredReflection(
                    PlumbEntry(
                        situation = situation,
                        thought = thought,
                        tiltIds = selectedTilts.toList(),
                        evidence = evidence
                    )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.4f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("SQUARED TO REALITY", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        reflection,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Silver,
                        lineHeight = 26.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "CHECK ANOTHER THOUGHT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { reset() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GaugeField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(Slate.copy(alpha = 0.2f))
            .border(1.dp, Gold.copy(alpha = 0.15f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Gold
        ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.4f)) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Silver)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlumbField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(Slate.copy(alpha = 0.3f))
            .border(1.dp, Gold.copy(alpha = 0.1f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Gold
        ),
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.4f))
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Silver)
    )
}

@Composable
private fun PlumbPrimary(label: String, enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(Gold)
        )
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun PlumbNav(
    onBack: () -> Unit,
    forwardLabel: String,
    forwardEnabled: Boolean,
    onForward: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "BACK",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.6f),
            modifier = Modifier
                .clickable { onBack() }
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            PlumbPrimary(forwardLabel, enabled = forwardEnabled, onClick = onForward)
        }
    }
}

@Composable
fun TheLevel() {
    // A real paced-breathing timer. Timing/scale come from the tested BreathPacer; here we just
    // animate a clock over one cycle and render the state. Default ~6 breaths/min (RESEARCH_BASIS §8).
    val pattern = BreathPattern.RESONANCE
    val transition = rememberInfiniteTransition(label = "breath")
    val elapsed by transition.animateFloat(
        initialValue = 0f,
        targetValue = pattern.totalMs.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(pattern.totalMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "elapsed"
    )
    val state = BreathPacer.stateAt(elapsed.toLong(), pattern)
    val label = when (state.phase) {
        BreathPhase.INHALE -> "BREATHE IN"
        BreathPhase.HOLD_IN -> "HOLD"
        BreathPhase.EXHALE -> "BREATHE OUT"
        BreathPhase.HOLD_OUT -> "HOLD"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("THE LEVEL", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("~6 BREATHS A MINUTE. FOLLOW THE CIRCLE.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((80f + 120f * state.scale).dp)
                    .border(2.dp, Gold.copy(alpha = 0.4f), shape = androidx.compose.foundation.shape.CircleShape)
            )
            Text("${state.secondsLeft}", style = MaterialTheme.typography.titleLarge, color = Gold, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(label, style = MaterialTheme.typography.titleLarge, color = Silver, letterSpacing = 4.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MouthToEarTool(onRecallHeld: () -> Unit = {}) {
    var principle by remember { mutableStateOf("") }
    var custom by remember { mutableStateOf("") }
    var practicing by remember { mutableStateOf(false) }
    var level by remember { mutableStateOf(0f) }
    var attempt by remember { mutableStateOf("") }
    var score by remember { mutableStateOf<Float?>(null) }

    fun begin(text: String) {
        principle = text; practicing = true; level = 0f; attempt = ""; score = null
    }

    // Count a memory-work session when the recall is held (>= threshold).
    LaunchedEffect(score) { score?.let { if (MouthToEar.isHeld(it)) onRecallHeld() } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("MOUTH TO EAR", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("COMMIT A PRINCIPLE TO HEART, THEN SAY IT BACK WITH FEWER CUES.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))

        if (!practicing) {
            Text("Choose a principle — or write your own.", style = MaterialTheme.typography.bodyMedium, color = Silver)
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DEFAULT_PRINCIPLES.forEach { p ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(Slate.copy(alpha = 0.2f))
                            .border(1.dp, Slate, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .clickable { begin(p.text) }
                            .padding(12.dp)
                    ) {
                        Text(p.text, style = MaterialTheme.typography.bodyMedium, color = Silver)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            PlumbField(custom, { custom = it }, "Write your own principle…")
            Spacer(modifier = Modifier.height(12.dp))
            PlumbPrimary("BEGIN WITH MY OWN", enabled = custom.isNotBlank()) { begin(custom) }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(Slate.copy(alpha = 0.4f))
                    .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("THE PRINCIPLE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(MouthToEar.mask(principle, level), style = MaterialTheme.typography.bodyLarge, color = Silver, lineHeight = 28.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("HIDE MORE", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))
            Slider(
                value = level,
                onValueChange = { level = it },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Gold, inactiveTrackColor = Slate)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Say it back from memory:", style = MaterialTheme.typography.bodyMedium, color = Silver)
            Spacer(modifier = Modifier.height(8.dp))
            PlumbField(attempt, { attempt = it }, "Recite the principle…")
            Spacer(modifier = Modifier.height(12.dp))
            PlumbPrimary("CHECK RECALL", enabled = attempt.isNotBlank()) { score = MouthToEar.scoreRecall(principle, attempt) }
            score?.let { s ->
                Spacer(modifier = Modifier.height(12.dp))
                val pct = (s * 100).toInt()
                val msg = when {
                    s >= 1f -> "Word-perfect. It's yours."
                    s >= 0.7f -> "Nearly there — $pct% held. Raise the cues and go again."
                    else -> "$pct% so far. Lower the cues, read it once more, then try again."
                }
                Text(msg, style = MaterialTheme.typography.bodyMedium, color = if (s >= 0.7f) Gold else Silver)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "CHOOSE ANOTHER",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.6f),
                modifier = Modifier.clickable { practicing = false; custom = "" }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TheSquare(onSetIntention: (String) -> Unit = {}) {
    val selected = remember { mutableStateListOf<String>() }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("THE SQUARE", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("SQUARE YOUR LIFE TO WHAT YOU VALUE.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Which few things matter most? Choose up to three.", style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Square.VALUES.forEach { value ->
                val isSel = selected.contains(value)
                Box(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .background(if (isSel) Gold.copy(alpha = 0.2f) else Slate.copy(alpha = 0.2f))
                        .border(1.dp, if (isSel) Gold.copy(alpha = 0.5f) else Slate, androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .clickable {
                            if (isSel) selected.remove(value)
                            else if (selected.size < 3) selected.add(value)
                            saved = false
                        }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(value, style = MaterialTheme.typography.bodyMedium, color = if (isSel) Gold else Silver)
                }
            }
        }

        if (selected.isNotEmpty()) {
            val intention = Square.squareIntention(selected.toList())
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(Slate.copy(alpha = 0.3f))
                    .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("YOUR SQUARE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("“$intention”", style = MaterialTheme.typography.bodyLarge, color = Silver, lineHeight = 26.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (saved) {
                Text(
                    "Set as your intention — it's on your Board now.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.7f)
                )
            } else {
                PlumbPrimary("SET AS MY INTENTION", enabled = true) {
                    onSetIntention(intention)
                    saved = true
                }
            }
        }
    }
}

@Composable
fun TheTrowel(onKeep: (String) -> Unit = {}) {
    // The self-compassion rite. Pure logic (the three movements + the words) lives in
    // com.ashlarprotocol.tools.Trowel (tested); this composable only gathers the person's own words and
    // hands them back. It never forces feeling, and the grounding off-ramp is reachable throughout —
    // the gentle exit backdraft demands (see docs/RESEARCH_BASIS.md).
    var step by remember { mutableStateOf(0) } // 0 name, 1 not-alone, 2 spread, 3 keep
    var struggle by remember { mutableStateOf("") }
    var kindWords by remember { mutableStateOf("") }
    var kept by remember { mutableStateOf(false) }
    var showGrounding by remember { mutableStateOf(false) }

    fun reset() { step = 0; struggle = ""; kindWords = ""; kept = false }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("THE TROWEL", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("SPREAD THE CEMENT OF BROTHERLY LOVE — INWARD.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))
        // The unconditional permission to stop, always in view.
        Text(Trowel.grounding.first(), style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.7f), lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        when (step) {
            0 -> {
                Text(Trowel.MOVEMENTS[0].label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.4f), letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(Trowel.MOVEMENTS[0].prompt, style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(12.dp))
                PlumbField(struggle, { struggle = it }, "Name what you're carrying…")
                Spacer(modifier = Modifier.height(16.dp))
                PlumbPrimary("CONTINUE", enabled = struggle.isNotBlank()) { step = 1 }
            }
            1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("YOU ARE NOT ALONE IN IT", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(Trowel.commonHumanity(struggle), style = MaterialTheme.typography.bodyLarge, color = Silver, lineHeight = 26.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                PlumbNav(onBack = { step = 0 }, forwardLabel = "CONTINUE", forwardEnabled = true) { step = 2 }
            }
            2 -> {
                Text(Trowel.MOVEMENTS[2].label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.4f), letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(Trowel.asABrother(struggle), style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp)
                Spacer(modifier = Modifier.height(12.dp))
                PlumbField(kindWords, { kindWords = it }, "Say it to yourself…")
                Spacer(modifier = Modifier.height(16.dp))
                // No pressure to write — the door onward stays open either way.
                PlumbNav(onBack = { step = 1 }, forwardLabel = "READ IT BACK", forwardEnabled = true) { step = 3 }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.4f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("KEEP THIS", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(Trowel.closing(kindWords), style = MaterialTheme.typography.bodyLarge, color = Silver, lineHeight = 26.sp)
                }
                if (kindWords.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (kept) {
                        Text("Kept — it's with what you hold onto.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.7f))
                    } else {
                        PlumbPrimary("KEEP THESE WORDS", enabled = true) { onKeep(kindWords); kept = true }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "BEGIN AGAIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { reset() }
                )
            }
        }

        // The grounding off-ramp — reachable at every step, never gated. The way out backdraft demands.
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Slate.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            if (showGrounding) "HIDE" else "PAUSE & GROUND",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
            modifier = Modifier.clickable { showGrounding = !showGrounding }
        )
        if (showGrounding) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Trowel.grounding.forEach { line ->
                    Text("·  $line", style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.85f), lineHeight = 22.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheGauge(onDayComplete: () -> Unit = {}) {
    val items = remember { mutableStateListOf<GaugeItem>() }
    var draft by remember { mutableStateOf("") }
    var cue by remember { mutableStateOf("") }
    var part by remember { mutableStateOf(DayPart.WORK) }

    // Count a completed gauge-day when all three parts are planned and every item is done.
    val dayComplete = Gauge.isDayComplete(items.toList())
    LaunchedEffect(dayComplete) { if (dayComplete) onDayComplete() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.ashlarprotocol.ui.theme.Surface)
            .border(1.dp, com.ashlarprotocol.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("THE GAUGE", style = MaterialTheme.typography.titleLarge, color = Gold)
        Text("DIVIDE THE DAY INTO THREE PARTS.", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))

        Text(Gauge.balanceMessage(items.toList()), style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Which part of the day does the next item belong to?
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DayPart.values().forEach { p ->
                val selected = p == part
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .background(if (selected) Gold.copy(alpha = 0.2f) else Slate.copy(alpha = 0.2f))
                        .border(1.dp, if (selected) Gold.copy(alpha = 0.5f) else Slate, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .clickable { part = p }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(p.display.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (selected) Gold else Silver)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(part.intent, style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.6f))

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Make it a plan: name the cue, then what you'll do.",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        GaugeField(cue, { cue = it }, "When or where… (e.g. after dinner)")
        Spacer(modifier = Modifier.height(8.dp))
        GaugeField(draft, { draft = it }, "…then I will (e.g. walk ten minutes)")
        Spacer(modifier = Modifier.height(12.dp))
        PlumbPrimary("ADD TO THE DAY", enabled = draft.isNotBlank()) {
            items.add(
                GaugeItem(
                    id = java.util.UUID.randomUUID().toString(),
                    part = part,
                    text = Gauge.implementationIntention(cue, draft)
                )
            )
            cue = ""
            draft = ""
        }

        if (items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            val pct = (Gauge.completion(items.toList()) * 100).toInt()
            Text("HONOURED: $pct%", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEachIndexed { i, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(Slate.copy(alpha = 0.2f))
                            .clickable { items[i] = entry.copy(done = !entry.done) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                .background(if (entry.done) Gold.copy(alpha = 0.8f) else Color.Transparent)
                                .border(1.dp, Gold.copy(alpha = 0.6f), androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.text, style = MaterialTheme.typography.bodyMedium, color = if (entry.done) Silver.copy(alpha = 0.5f) else Silver)
                            Text(entry.part.display.uppercase(), style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}
