package com.ashlarprotocol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.data.RoughEdgeEntry
import com.ashlarprotocol.tools.RoughEdge
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.RedAlert
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate

/**
 * The Rough-Edge tool (F5) — work one bad habit on the anti-AVE spine: change the cue, put something
 * better in its place, ride the wave, and let a slip be *data*, not a verdict. No streak to break. The
 * clinical-handoff note is always present and taps straight to real help. See tools/RoughEdge.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheRoughEdge(
    existing: RoughEdgeEntry?,
    onSet: (name: String, cue: String, environmentMove: String, replacement: String) -> Unit,
    onLapse: () -> Unit
) {
    val crisis = LocalCrisisController.current
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var cue by remember { mutableStateOf(existing?.cue ?: "") }
    var move by remember { mutableStateOf(existing?.environmentMove ?: "") }
    var replacement by remember { mutableStateOf(existing?.replacement ?: "") }
    var showLapse by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("THE ROUGH EDGE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Text(RoughEdge.INTRO, style = MaterialTheme.typography.bodySmall, color = Silver, lineHeight = 20.sp)

        Spacer(Modifier.height(18.dp))
        Label("THE EDGE")
        EdgeField(name, { name = it }, "the habit you mean to work — e.g. late-night scrolling")
        Spacer(Modifier.height(14.dp))
        Label("ITS CUE — WHEN / WHERE IT STARTS")
        EdgeField(cue, { cue = it }, "e.g. lying in bed, phone in hand")
        Spacer(Modifier.height(14.dp))
        Label("CHANGE ONE THING AROUND IT")
        EdgeField(move, { move = it }, "e.g. charge the phone in another room")
        Spacer(Modifier.height(14.dp))
        Label("PUT SOMETHING BETTER IN ITS PLACE")
        EdgeField(replacement, { replacement = it }, "an approach action — e.g. read a page")

        Spacer(Modifier.height(18.dp))
        val canSave = name.isNotBlank() && replacement.isNotBlank()
        Text(
            text = if (existing == null) "SET THE PLAN" else "UPDATE THE PLAN",
            style = MaterialTheme.typography.labelSmall,
            color = if (canSave) Gold else Silver.copy(alpha = 0.4f),
            textAlign = TextAlign.Center, letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, (if (canSave) Gold else Slate).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .then(if (canSave) Modifier.clickable { onSet(name, cue, move, replacement) } else Modifier)
                .padding(vertical = 14.dp)
        )

        if (existing != null) {
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)).background(Slate.copy(alpha = 0.2f)).padding(16.dp)
            ) {
                Text("WHEN THE WAVE COMES", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Don't wrestle it — ride it. Pull \"Ride the urge\" from STEADY and let the wave pass.",
                    style = MaterialTheme.typography.bodyMedium, color = LightText, lineHeight = 22.sp
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "${existing.lapses.size} ${if (existing.lapses.size == 1) "slip" else "slips"} logged, and forgiven. The work continues.",
                    style = MaterialTheme.typography.labelSmall, color = Silver, lineHeight = 18.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "A slip happened →",
                    style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.7f),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onLapse(); showLapse = true }.padding(vertical = 6.dp)
                )
                if (showLapse) {
                    Spacer(Modifier.height(6.dp))
                    Text(RoughEdge.lapseResponse(), style = MaterialTheme.typography.bodyMedium, color = LightText, lineHeight = 22.sp)
                }
            }
        }

        // The clinical-handoff floor — always present, taps straight to real human help (§9).
        Spacer(Modifier.height(20.dp))
        Text(
            text = RoughEdge.SAFETY_NOTE,
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.75f),
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, RedAlert.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable { crisis.openManual() }
                .padding(14.dp)
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.5.sp)
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EdgeField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value, onValueChange = onChange, singleLine = true,
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Slate.copy(alpha = 0.2f),
            unfocusedContainerColor = Slate.copy(alpha = 0.2f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = LightText, unfocusedTextColor = LightText, cursorColor = Gold
        ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.45f)) },
        textStyle = MaterialTheme.typography.bodyMedium
    )
}
