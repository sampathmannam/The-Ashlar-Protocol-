package com.example.ui.components

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tools.WhoFive
import com.example.ui.theme.Gold
import com.example.ui.theme.LightText
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import com.example.ui.theme.Surface

/**
 * The WHO-5 check (SPEC T3.1). A gentle, skippable, five-question wellbeing measure — the plan's
 * primary outcome metric. Steps: intro → five items → a warm, non-clinical reflection of the 0–100
 * score. Always dismissible; stored on-device only. [onComplete] gets the score once, at the end.
 */
@Composable
fun WhoFiveDialog(onDismiss: () -> Unit, onComplete: (Int) -> Unit) {
    // step 0 = intro, 1..5 = the items, 6 = result.
    var step by remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<Int>() }

    fun answer(value: Int) {
        if (answers.size >= step) answers[step - 1] = value else answers.add(value)
        step += 1
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 660.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "A GENTLE CHECK",
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            when {
                step == 0 -> {
                    Text(
                        text = "Five short questions about the last two weeks — a way to notice how you've " +
                            "actually been, over time. It's just for you, kept on your phone. Skip any time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryRow(label = "BEGIN") { step = 1 }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuietRow(label = "Not now") { onDismiss() }
                }

                step in 1..5 -> {
                    Text(
                        text = "${WhoFive.PREAMBLE}  (${step} of 5)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = WhoFive.ITEMS[step - 1],
                        style = MaterialTheme.typography.bodyLarge,
                        color = LightText,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WhoFive.OPTIONS.forEach { opt ->
                        Text(
                            text = opt.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Silver,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate.copy(alpha = 0.2f))
                                .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .clickable { answer(opt.value) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    QuietRow(label = "Skip for now") { onDismiss() }
                }

                else -> {
                    val score = WhoFive.score(answers.toList())
                    Text(
                        text = "$score / 100",
                        style = MaterialTheme.typography.titleLarge,
                        color = Gold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = WhoFive.reflection(score),
                        style = MaterialTheme.typography.bodyLarge,
                        color = LightText,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "A number to notice, not a verdict. It's yours, kept on your phone.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Silver.copy(alpha = 0.6f),
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryRow(label = "DONE") { onComplete(score); onDismiss() }
                }
            }
        }
    }
}

@Composable
private fun PrimaryRow(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = Gold,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    )
}

@Composable
private fun QuietRow(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = Silver.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
}
