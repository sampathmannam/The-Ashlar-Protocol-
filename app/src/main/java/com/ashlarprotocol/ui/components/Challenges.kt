package com.ashlarprotocol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.tools.Challenge
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Surface

/**
 * The day's work — the research-designed challenges that pay wages. The weekly challenge is the
 * spine; the daily menu is a small, optional set. Everything here is an INVITATION: a completed
 * item settles to a quiet "tended", and anything left undone carries no penalty, red mark, or shame.
 */
@Composable
fun ChallengesCard(
    daily: List<Challenge>,
    weekly: Challenge,
    doneIds: Set<String>,
    onComplete: (Challenge) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            "THE DAY'S WORK",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.6f),
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Small, real, and yours to take or leave. Each one done pays a wage.",
            style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.7f), lineHeight = 16.sp
        )

        Spacer(Modifier.height(18.dp))
        Text("THIS WEEK", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.45f), letterSpacing = 1.5.sp)
        Spacer(Modifier.height(8.dp))
        ChallengeRow(weekly, done = weekly.id in doneIds, onComplete = onComplete)

        Spacer(Modifier.height(18.dp))
        Text("TODAY", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.45f), letterSpacing = 1.5.sp)
        Spacer(Modifier.height(8.dp))
        daily.forEach { c ->
            ChallengeRow(c, done = c.id in doneIds, onComplete = onComplete)
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ChallengeRow(challenge: Challenge, done: Boolean, onComplete: (Challenge) -> Unit) {
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(if (done) Gold.copy(alpha = 0.06f) else Surface)
        .border(1.dp, (if (done) Gold else Silver).copy(alpha = 0.18f), RoundedCornerShape(18.dp))
    val mod = if (done) base else base.clickable { onComplete(challenge) }
    Row(modifier = mod.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                challenge.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (done) Silver else LightText
            )
            Spacer(Modifier.height(2.dp))
            Text(
                challenge.invite,
                style = MaterialTheme.typography.bodySmall,
                color = Silver.copy(alpha = if (done) 0.6f else 0.85f),
                lineHeight = 18.sp
            )
        }
        Spacer(Modifier.height(0.dp))
        Text(
            if (done) "✓ tended" else "tend",
            style = MaterialTheme.typography.labelSmall,
            color = if (done) Gold.copy(alpha = 0.7f) else Gold,
            textAlign = TextAlign.End
        )
    }
}
