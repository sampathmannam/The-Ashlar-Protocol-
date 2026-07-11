package com.ashlarprotocol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.tools.Course
import com.ashlarprotocol.tools.Temple
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Surface

/**
 * The Temple — the long-arc progression made visible. The courses you've raised stack upward; laying
 * wages raises the next one. Numbers are kept quiet — the rising edifice is the display, not a score.
 * Nothing here is ever taken away (raising only accrues); the next course simply waits, no pressure.
 */
@Composable
fun TempleCard(
    coursesRaised: Int,
    nextCourse: Course?,
    balance: Int,
    canRaise: Boolean,
    onRaise: () -> Unit
) {
    val standingDegree = (Temple.courseAt(coursesRaised)?.degree ?: nextCourse?.degree)?.display
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("THE TEMPLE", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.6f), letterSpacing = 2.sp)
        Spacer(Modifier.height(16.dp))

        // The rising courses — one row per course of the WHOLE 50-course journey, so every course laid
        // grows the Temple (raised = gold stone; the rest wait as faint outlines above). A wider base
        // taxes upward into a narrowing crown. The taper scales with the row count.
        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            val shown = Temple.PLANNED_COURSES
            val gap = 1.5.dp.toPx()
            val rowH = ((size.height - gap * (shown - 1)) / shown).coerceAtLeast(1f)
            val fullW = size.width
            for (i in 0 until shown) {
                val raised = i < coursesRaised
                val inset = (i.toFloat() / shown) * (fullW * 0.30f)
                val top = size.height - (i + 1) * rowH - i * gap
                val w = fullW - inset * 2
                if (raised) {
                    drawRect(color = Gold.copy(alpha = 0.32f), topLeft = Offset(inset, top), size = Size(w, rowH))
                    drawRect(color = Gold.copy(alpha = 0.55f), topLeft = Offset(inset, top), size = Size(w, rowH), style = Stroke(width = 1f))
                } else {
                    drawRect(color = Silver.copy(alpha = 0.09f), topLeft = Offset(inset, top), size = Size(w, rowH), style = Stroke(width = 1f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        val standing = when {
            coursesRaised <= 0 -> "The ground is level. Lay your first stone."
            nextCourse == null -> "The Temple stands — all $coursesRaised courses raised. The work goes on."
            else -> "Course $coursesRaised of ${Temple.PLANNED_COURSES}" + (standingDegree?.let { " · $it" } ?: "")
        }
        Text(standing, style = MaterialTheme.typography.bodyMedium, color = LightText, lineHeight = 22.sp)

        Spacer(Modifier.height(6.dp))
        Text(
            "$balance in wages — corn, wine, and oil — in hand.",
            style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.75f)
        )

        if (nextCourse != null) {
            Spacer(Modifier.height(16.dp))
            if (canRaise) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Gold.copy(alpha = 0.10f))
                        .border(1.dp, Gold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .clickable { onRaise() }
                        .padding(16.dp)
                ) {
                    Text("Lay the next course — ${nextCourse.name}", style = MaterialTheme.typography.bodyLarge, color = Gold)
                    Spacer(Modifier.height(2.dp))
                    Text(nextCourse.unlocks, style = MaterialTheme.typography.bodySmall, color = Silver, lineHeight = 18.sp)
                }
            } else {
                Text(
                    "Next: ${nextCourse.name} — ${nextCourse.cost} wages to raise.",
                    style = MaterialTheme.typography.bodySmall, color = Silver.copy(alpha = 0.7f), lineHeight = 18.sp
                )
            }
        }
    }
}
