package com.ashlarprotocol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.ui.components.LocalCrisisController
import com.ashlarprotocol.ui.theme.DividerWhite
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import com.ashlarprotocol.ui.theme.Surface

/**
 * The initiation rite — first run. Instead of a signup form, the app begins in the Chamber of
 * Reflection: a few quiet, solemn questions that take stock before the work begins (and double as
 * a gentle, private baseline). See docs/VISION.md §6.
 *
 * Two things are load-bearing here:
 *  - The free-text step is crisis-scanned like every other free-text surface (safety is never
 *    gated, not even during onboarding — VISION §8).
 *  - Nothing here can be failed. It is a threshold, not a test.
 *
 * On completion, [onComplete] receives the user's stated intention and how heavily they arrive;
 * the caller persists it and enters the app as an Entered Apprentice.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitiationScreen(onComplete: (intention: String, weight: Float) -> Unit) {
    val crisis = LocalCrisisController.current
    var step by remember { mutableStateOf(0) } // 0 welcome, 1 weight, 2 burden, 3 intention, 4 crossing
    var weight by remember { mutableStateOf(0.5f) }
    var burden by remember { mutableStateOf("") }
    var intention by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "THE CHAMBER OF REFLECTION",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.6f),
            letterSpacing = 3.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            0 -> {
                RiteHeading("Before the tools, the threshold.")
                RiteBody(
                    "Every mason begins here — alone, in a quiet room, taking stock before the work. " +
                        "Sit a moment. There is nothing to pass or fail, and no rush at all."
                )
                Spacer(modifier = Modifier.height(28.dp))
                RiteButton("ENTER THE CHAMBER") { step = 1 }
            }
            1 -> {
                RiteStepLabel(1)
                RiteHeading("How heavily are you carrying things, right now?")
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = weightWord(weight),
                    style = MaterialTheme.typography.titleLarge,
                    color = Gold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = weight,
                    onValueChange = { weight = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Gold, inactiveTrackColor = Slate)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("LIGHT", style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.5f))
                    Text("HEAVY", style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.height(28.dp))
                RiteNav(onBack = { step = 0 }, forward = "NEXT") { step = 2 }
            }
            2 -> {
                RiteStepLabel(2)
                RiteHeading("What is the rough edge you most want to work on?")
                RiteBody("Name it plainly, or leave it for now. This stays on your device.")
                Spacer(modifier = Modifier.height(16.dp))
                RiteField(burden, { burden = it; crisis.scan(it) }, "The weight I carry is…")
                Spacer(modifier = Modifier.height(28.dp))
                RiteNav(onBack = { step = 1 }, forward = "NEXT") { step = 3 }
            }
            3 -> {
                RiteStepLabel(3)
                RiteHeading("When this stone is smoother, what will be true of your life?")
                RiteBody("Not a resolution — a direction. What are you working toward?")
                Spacer(modifier = Modifier.height(16.dp))
                RiteField(intention, { intention = it; crisis.scan(it) }, "I am working toward…")
                Spacer(modifier = Modifier.height(28.dp))
                RiteNav(onBack = { step = 2 }, forward = "CROSS THE THRESHOLD") { step = 4 }
            }
            else -> {
                RiteHeading("You enter as an Entered Apprentice.")
                RiteBody(
                    "The rough ashlar is yours to shape. The first tools are laid out for you. " +
                        "Return to the Chamber whenever the weight is too much to carry alone."
                )
                Spacer(modifier = Modifier.height(28.dp))
                RiteButton("BEGIN THE WORK") { onComplete(intention.trim(), weight) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun weightWord(weight: Float): String = when {
    weight < 0.2f -> "Lightly"
    weight < 0.45f -> "Some weight"
    weight < 0.7f -> "A heavy load"
    else -> "Very heavily"
}

@Composable
private fun RiteHeading(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, color = LightText, lineHeight = 34.sp)
}

@Composable
private fun RiteBody(text: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text, style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 24.sp)
}

@Composable
private fun RiteStepLabel(n: Int) {
    Text(
        "STEP $n OF 3",
        style = MaterialTheme.typography.labelSmall,
        color = Gold.copy(alpha = 0.4f),
        letterSpacing = 2.sp
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RiteField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Slate.copy(alpha = 0.2f))
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Gold
        ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.4f)) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Silver)
    )
}

@Composable
private fun RiteButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(Gold))
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
    }
}

@Composable
private fun RiteNav(onBack: () -> Unit, forward: String, onForward: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "BACK",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.6f),
            modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            RiteButton(forward, onForward)
        }
    }
}
