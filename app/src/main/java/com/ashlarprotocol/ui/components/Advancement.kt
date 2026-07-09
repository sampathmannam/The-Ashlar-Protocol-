package com.ashlarprotocol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.ui.theme.DividerWhite
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Surface

/**
 * The words spoken when a member is raised into a new degree. Kept as pure data (not baked into the
 * composable) so the mortality-symbolism safety gate can read and audit every line (SafetyAuditTest).
 */
data class RaisingText(
    val overline: String,
    val degreeLine: String,
    val toolsGiven: String,
    val meaning: String,
    val cta: String
)

/**
 * The Raising — the rite that marks an advancement.
 *
 * The Craft's degrees are earned by *doing the work* (see tools/Degrees.kt), never by paying. When a
 * new degree is reached, [com.ashlarprotocol.tools.Advancement] surfaces it and this rite marks the
 * crossing once. Each degree also *confers a tool* (mirrors the veil in ToolsScreen: the Plumb opens
 * at Fellowcraft, Mouth to Ear at Master) — pedagogy, not paywall.
 *
 * ⚠️ Copy is mortality-clean by construction (RESEARCH_BASIS §10; SafetyAuditTest sweeps [allText]),
 * and the rite renders *below* the §9 crisis layer, which always wins the top of the screen.
 */
object RaisingCopy {
    fun forDegree(degree: Degree): RaisingText = when (degree) {
        Degree.FELLOWCRAFT -> RaisingText(
            overline = "YOU ARE RAISED",
            degreeLine = "to the degree of Fellowcraft",
            toolsGiven = "The Plumb is placed in your hands — to straighten a thought against the true vertical.",
            meaning = "You have shown up, and kept showing up. The mind's work opens to you now.",
            cta = "TAKE UP THE TOOLS"
        )
        Degree.MASTER_MASON -> RaisingText(
            overline = "YOU ARE RAISED",
            degreeLine = "to the degree of Master Mason",
            toolsGiven = "Mouth to Ear is entrusted to you — the memory work, to carry and to teach onward.",
            meaning = "The rough edges are worn smoother. The work from here is lifelong, and it is yours.",
            cta = "TAKE UP THE TOOLS"
        )
        // The entry degree needs no raising (you are received in the initiation rite), but the copy
        // exists so the audit sweep covers every branch.
        Degree.ENTERED_APPRENTICE -> RaisingText(
            overline = "YOU ARE RECEIVED",
            degreeLine = "as an Entered Apprentice",
            toolsGiven = "The first working tools are yours.",
            meaning = "The rough ashlar is set before you. The work begins.",
            cta = "BEGIN THE WORK"
        )
    }

    /** Every ceremony string, for the mortality-symbolism safety sweep. */
    fun allText(): List<String> = Degree.values().map { forDegree(it) }.flatMap {
        listOf(it.overline, it.degreeLine, it.toolsGiven, it.meaning, it.cta)
    }
}

/**
 * A solemn, single-purpose rite. Dismissible only by taking up the tools — no accidental scrim tap —
 * because a raising is a deliberate moment, not a toast. [onAcknowledge] persists the degree so the
 * rite never fires twice.
 */
@Composable
fun AdvancementCeremony(degree: Degree, onAcknowledge: () -> Unit) {
    val text = RaisingCopy.forDegree(degree)

    Dialog(
        onDismissRequest = onAcknowledge,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, Gold.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text.overline,
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = text.degreeLine,
                style = MaterialTheme.typography.headlineSmall,
                color = LightText,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            // A thin rule — the threshold crossed.
            Text(
                text = "· · ·",
                style = MaterialTheme.typography.titleMedium,
                color = DividerWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = text.toolsGiven,
                style = MaterialTheme.typography.bodyMedium,
                color = LightText,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = text.meaning,
                style = MaterialTheme.typography.bodySmall,
                color = Silver,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = text.cta,
                style = MaterialTheme.typography.labelLarge,
                color = Gold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { onAcknowledge() }
                    .padding(vertical = 16.dp)
            )
        }
    }
}
