package com.ashlarprotocol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ashlarprotocol.tools.PowerUps
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import com.ashlarprotocol.ui.theme.Surface

/**
 * Hoisted controller so ANY screen can open the always-available Power-Ups without threading dialog
 * state through every signature. Provided via [LocalPowerUpsController] at the app root; the sheet is
 * rendered once, above everything (SPEC_PHASE1_STONE P0.6 / ticket T2.4).
 */
class PowerUpsController {
    var visible by mutableStateOf(false)
        private set

    /** Always honored — Power-Ups are never gated behind the Working, a streak, or payment. */
    fun open() { visible = true }
    fun close() { visible = false }
}

val LocalPowerUpsController = staticCompositionLocalOf<PowerUpsController> {
    error("No PowerUpsController provided. Wrap content in a PowerUpsController provider.")
}

/**
 * The always-available quick mood-lifters. Ordinary steadying a person can pull any time, in ≤2 taps,
 * decoupled from streak/task — distinct from the crisis pathway (the red NEED HELP surface), which
 * takes precedence on any risk signal. Gold, not alarm-red: this is calm, not emergency.
 */
@Composable
fun PowerUpsSheet(controller: PowerUpsController) {
    if (!controller.visible) return
    var openId by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { controller.close() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "A MOMENT TO STEADY",
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pull any of these, any time. Nothing to finish, nothing to earn — just something small that helps.",
                style = MaterialTheme.typography.bodyMedium,
                color = LightText,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            PowerUps.POWER_UPS.forEach { p ->
                val open = openId == p.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.35f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { openId = if (open) null else p.id }
                        .padding(16.dp)
                ) {
                    Text(text = p.title, style = MaterialTheme.typography.bodyMedium, color = LightText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = p.invite,
                        style = MaterialTheme.typography.labelSmall,
                        color = Silver,
                        fontSize = 11.sp
                    )
                    if (open) {
                        Spacer(modifier = Modifier.height(12.dp))
                        p.steps.forEach { step ->
                            Text(
                                text = "·  $step",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Silver,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CLOSE",
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { controller.close() }
                    .padding(vertical = 12.dp)
            )
        }
    }
}
