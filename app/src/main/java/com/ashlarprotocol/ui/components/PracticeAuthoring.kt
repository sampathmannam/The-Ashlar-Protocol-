package com.ashlarprotocol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.ashlarprotocol.tools.PracticeAuthoring
import com.ashlarprotocol.tools.PracticeReminder
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import com.ashlarprotocol.ui.theme.Surface

/**
 * The practice-authoring dialog (SPEC T1.4). Two fields — an existing-routine anchor and an approach
 * action — composing "After [anchor], I will [action]." It won't save avoidance phrasing: as soon as
 * the action reads like "stop…" it shows the reframe hint and disables save. Approach, anchored, yours.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PracticeDialog(
    intention: String,
    onDismiss: () -> Unit,
    onSave: (anchor: String, action: String, reminderMinutesOfDay: Int?, cueKind: String?) -> Unit
) {
    var anchor by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var reminderMinutes by remember { mutableStateOf<Int?>(null) }
    var cueKind by remember { mutableStateOf<com.ashlarprotocol.tools.Cornerstone.CueKind?>(null) }
    val needsIntention = PracticeAuthoring.requiresIntentionFirst(intention)
    val avoidance = action.isNotBlank() && PracticeAuthoring.isAvoidanceFramed(action)
    val plan = PracticeAuthoring.composePlan(anchor, action)
    val canSave = PracticeAuthoring.canSave(anchor, action)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("SET A PRACTICE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Anchor a small, doable action to something you already do — then name what you WILL do.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Commitment before if-then (F2): implementation intentions amplify a committed goal, they
            // don't create one (Gollwitzer & Sheeran). Autonomy-supportive — a gentle route, not a block.
            if (needsIntention) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)).background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.55f), RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Text(
                        text = "First, name what you're working toward — set an intention with The Square. " +
                            "A practice holds best in service of something you actually care about.",
                        style = MaterialTheme.typography.bodyMedium, color = Gold.copy(alpha = 0.85f), lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("AFTER…", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            PracticeField(anchor, { anchor = it }, "e.g. I pour my morning coffee")

            Spacer(modifier = Modifier.height(16.dp))
            Text("…I WILL", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            PracticeField(action, { action = it }, "e.g. write one line in my journal")

            if (avoidance) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(PracticeAuthoring.REFRAME_HINT, style = MaterialTheme.typography.bodyMedium, color = Gold.copy(alpha = 0.85f), lineHeight = 22.sp)
                }
            } else if (plan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("YOUR PRACTICE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("“$plan”", style = MaterialTheme.typography.bodyLarge, color = LightText, lineHeight = 26.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("REMIND ME?  (OPTIONAL)", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Map it to when your anchor usually happens — gentle, and you can skip it any day.",
                style = MaterialTheme.typography.labelSmall,
                color = Silver.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ReminderPill("None", reminderMinutes == null) { reminderMinutes = null }
                PracticeReminder.SLOTS.forEach { slot ->
                    ReminderPill(slot.label, reminderMinutes == slot.minutesOfDay) { reminderMinutes = slot.minutesOfDay }
                }
            }

            // The explicit cue behind the anchor (F2) — makes the if-then plan cue-anchored. Optional.
            Spacer(modifier = Modifier.height(16.dp))
            Text("WHAT KIND OF CUE?  (OPTIONAL)", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.ashlarprotocol.tools.Cornerstone.CueKind.values().forEach { ck ->
                    ReminderPill(ck.display, cueKind == ck) { cueKind = if (cueKind == ck) null else ck }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "KEEP THIS PRACTICE",
                style = MaterialTheme.typography.labelSmall,
                color = if (canSave) Gold else Silver.copy(alpha = 0.35f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, (if (canSave) Gold else Silver).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .then(if (canSave) Modifier.clickable { onSave(anchor, action, reminderMinutes, cueKind?.name); onDismiss() } else Modifier)
                    .padding(vertical = 14.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelSmall,
                color = Silver.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { onDismiss() }.padding(vertical = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Slate.copy(alpha = 0.2f),
            unfocusedContainerColor = Slate.copy(alpha = 0.2f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = LightText,
            unfocusedTextColor = LightText,
            cursorColor = Gold
        ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.45f)) },
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ReminderPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (selected) Gold else Silver,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Gold.copy(alpha = 0.2f) else Slate.copy(alpha = 0.2f))
            .border(1.dp, if (selected) Gold.copy(alpha = 0.5f) else Slate, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}
