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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.tools.Reflection
import com.ashlarprotocol.tools.ReflectionKind
import com.ashlarprotocol.ui.theme.DividerWhite
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Surface

/**
 * "What the Stone Remembers" (the mirror / F-AI) — a pull-only reflective card on the Board. Tap to
 * expand; it shows FACTS (your own history) first, then, set apart and visibly hedged, any NOTICINGS.
 * It only ever displays what the deterministic engine produced from your own data — no generation.
 */
@Composable
fun StoneRemembersCard(expanded: Boolean, reflections: List<Reflection>, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "WHAT THE STONE REMEMBERS",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
                modifier = Modifier.weight(1f)
            )
            Text(if (expanded) "–" else "→", style = MaterialTheme.typography.titleMedium, color = Gold)
        }

        if (expanded) {
            Spacer(Modifier.height(16.dp))
            val facts = reflections.filter { it.kind == ReflectionKind.FACT }
            val noticings = reflections.filter { it.kind == ReflectionKind.NOTICING }

            if (reflections.isEmpty()) {
                Text(
                    "The stone is still learning your shape — tend it a while, and it will remember.",
                    style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp
                )
            } else {
                facts.forEach { r ->
                    Text("·  ${r.text}", style = MaterialTheme.typography.bodyMedium, color = LightText, lineHeight = 23.sp)
                    Spacer(Modifier.height(10.dp))
                }
                if (noticings.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerWhite.copy(alpha = 0.12f))
                    ) {}
                    Spacer(Modifier.height(16.dp))
                    Text("WORTH NOTICING", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Patterns from your own days — held lightly, never proof.",
                        style = MaterialTheme.typography.labelSmall, color = Silver.copy(alpha = 0.6f), lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    noticings.forEach { r ->
                        Text("·  ${r.text}", style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 23.sp)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
