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
import com.ashlarprotocol.data.CornerstoneEntry
import com.ashlarprotocol.tools.Cornerstone
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate

/**
 * The Cornerstone tool (F1) — square your surroundings so the right choice is the easy one. Stores
 * one self-directed environment change and prompts you to go make it; it automates nothing (see
 * tools/Cornerstone). Copy is self-directed ("engineer your room"), never "we'll nudge you".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheCornerstone(existing: CornerstoneEntry?, onSave: (CornerstoneEntry) -> Unit) {
    var behavior by remember { mutableStateOf("") }
    var cueKind by remember { mutableStateOf<Cornerstone.CueKind?>(null) }
    var cueDetail by remember { mutableStateOf("") }
    var move by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("THE CORNERSTONE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Willpower is the weakest tool. Square the room instead: change one thing in your " +
                "surroundings so the good choice is the easy one — and the bad one takes real effort. " +
                "The habit follows the cue.",
            style = MaterialTheme.typography.bodySmall, color = Silver, lineHeight = 19.sp
        )

        if (existing != null && !saved) {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)).background(Slate.copy(alpha = 0.2f)).padding(14.dp)
            ) {
                Text("YOUR CORNERSTONE", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text("${existing.behavior} — ${existing.move}", style = MaterialTheme.typography.bodyMedium, color = LightText)
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel("WHAT ARE YOU WORKING ON?")
        CornerField(behavior, { behavior = it }, "read at night · put the phone down earlier …")

        Spacer(Modifier.height(16.dp))
        SectionLabel("WHAT'S THE CUE?")
        Cornerstone.CueKind.values().forEach { ck ->
            SelectRow(ck.display, cueKind == ck) { cueKind = ck }
        }
        Spacer(Modifier.height(8.dp))
        CornerField(cueDetail, { cueDetail = it }, "after dinner · on the kitchen counter …")

        Spacer(Modifier.height(16.dp))
        SectionLabel("CHANGE ONE THING — MAKE A GOOD HABIT EASIER")
        Cornerstone.reduceMoves().forEach { m -> SelectRow(m.display, move == m.display) { move = m.display } }
        Spacer(Modifier.height(10.dp))
        SectionLabel("… OR MAKE A BAD DEFAULT HARDER")
        Cornerstone.addMoves().forEach { m -> SelectRow(m.display, move == m.display) { move = m.display } }

        Spacer(Modifier.height(20.dp))
        val canSave = behavior.isNotBlank() && move != null
        Text(
            text = if (saved) "SET — NOW GO SQUARE THE ROOM" else "SET THE CORNERSTONE",
            style = MaterialTheme.typography.labelSmall,
            color = if (canSave || saved) Gold else Silver.copy(alpha = 0.4f),
            textAlign = TextAlign.Center, letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, (if (canSave) Gold else Slate).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .then(
                    if (canSave && !saved) Modifier.clickable {
                        onSave(
                            CornerstoneEntry(
                                behavior = behavior.trim(),
                                cueKind = (cueKind ?: Cornerstone.CueKind.AFTER_ACTION).name,
                                cueDetail = cueDetail.trim(),
                                move = move!!
                            )
                        )
                        saved = true
                    } else Modifier
                )
                .padding(vertical = 15.dp)
        )
        if (saved) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "The app changed nothing — that part is yours to do, out in the world.",
                style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.6f),
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.5.sp)
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CornerField(value: String, onChange: (String) -> Unit, placeholder: String) {
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

@Composable
private fun SelectRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = if (selected) Gold else Silver,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Gold.copy(alpha = 0.15f) else Slate.copy(alpha = 0.18f))
            .border(1.dp, if (selected) Gold.copy(alpha = 0.5f) else Slate.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    )
}
