package com.example.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tools.ReachOut
import com.example.tools.Relief
import com.example.ui.AshlarAppViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.Gold
import com.example.ui.theme.RedAlert
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Meaning/values-oriented prompts for the Chamber's "keep" mode. Deliberately about meaning and
 * what matters (RESEARCH_BASIS §3 expressive writing, §10 purpose) — NOT reflection on death.
 */
val MEANING_PROMPTS = listOf(
    "What mattered to you today, even a little?",
    "When did you feel most like yourself lately?",
    "What is this difficulty asking you to become?",
    "What would you want to remember about today?",
    "Who, or what, are you doing this for?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChamberScreen(viewModel: AshlarAppViewModel) {
    var mode by remember { mutableStateOf("letgo") } // letgo (release) | keep (reflect)
    var text by remember { mutableStateOf("") }
    var purging by remember { mutableStateOf(false) }
    var keepText by remember { mutableStateOf("") }
    var promptIndex by remember { mutableStateOf(0) }
    var relief by remember { mutableStateOf<String?>(null) }
    var reliefIndex by remember { mutableStateOf(0) }
    var reachIndex by remember { mutableStateOf(0) }
    val reflections by viewModel.reflections.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val crisisController = com.example.ui.components.LocalCrisisController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(com.example.ui.theme.Surface.copy(alpha = 0.8f))
            .border(1.dp, com.example.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHAMBER OF REFLECTION",
            style = MaterialTheme.typography.titleLarge,
            color = Gold,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Two ways to sit with what you're carrying: let it go, or keep it.
        if (!purging) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChamberModeChip("LET GO", mode == "letgo", RedAlert, Modifier.weight(1f)) { mode = "letgo" }
                ChamberModeChip("KEEP", mode == "keep", Gold, Modifier.weight(1f)) { mode = "keep" }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (mode == "letgo") {
            if (!purging) {
                Text(
                    "Write what you're carrying, then let it go. Nothing here is saved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Silver.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        // Additive safety net: never blocks writing, only offers help.
                        crisisController.scan(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Slate.copy(alpha = 0.15f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Gold
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Silver),
                    placeholder = {
                        Text(
                            "Lay down the weight you're carrying...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Silver.copy(alpha = 0.3f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        purging = true
                        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorManager.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        }

                        if (vibrator.hasVibrator()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val timings = longArrayOf(100, 50, 500)
                                val amplitudes = intArrayOf(255, 0, 255)
                                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(longArrayOf(100, 50, 500), -1)
                            }
                        }

                        coroutineScope.launch {
                            delay(2000)
                            text = ""
                            // The Well: a moment of being met with relief (bundled, on-device).
                            relief = Relief.reliefAt(reliefIndex)
                            reliefIndex++
                            delay(4000)
                            relief = null
                            purging = false
                            // Text is gone; arm the detector for the next entry.
                            crisisController.resetAuto()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedAlert.copy(alpha = 0.1f),
                        contentColor = RedAlert
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RedAlert.copy(alpha = 0.5f))
                ) {
                    Text(
                        "RELEASE IT",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val currentRelief = relief
                    if (currentRelief != null) {
                        // The Well — met with a word of relief once the weight is down.
                        Text(
                            text = currentRelief,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        val infiniteTransition = rememberInfiniteTransition(label = "release")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "alpha"
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Releasing",
                                tint = RedAlert.copy(alpha = alpha),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "LETTING IT GO...",
                                style = MaterialTheme.typography.labelSmall,
                                color = RedAlert.copy(alpha = alpha)
                            )
                        }
                    }
                }
            }
        } else {
            // KEEP mode — a meaning-oriented reflection you hold onto.
            Text(
                text = MEANING_PROMPTS[promptIndex],
                style = MaterialTheme.typography.bodyLarge,
                color = Gold,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "ANOTHER",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.4f),
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clickable { promptIndex = (promptIndex + 1) % MEANING_PROMPTS.size }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = keepText,
                onValueChange = {
                    keepText = it
                    crisisController.scan(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Slate.copy(alpha = 0.15f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .border(1.dp, Gold.copy(alpha = 0.2f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Gold
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Silver),
                placeholder = {
                    Text(
                        "Set it down in words, and keep it...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Silver.copy(alpha = 0.3f)
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (keepText.isNotBlank()) {
                        viewModel.addReflection(keepText)
                        keepText = ""
                        crisisController.resetAuto()
                    }
                },
                enabled = keepText.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold.copy(alpha = 0.12f),
                    contentColor = Gold,
                    disabledContainerColor = Charcoal,
                    disabledContentColor = Silver.copy(alpha = 0.4f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.4f))
            ) {
                Text("KEEP THIS", style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
            }

            if (reflections.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "WHAT YOU'VE KEPT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.4f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reflections.forEach { r ->
                        KeptReflectionCard(r) { viewModel.removeReflection(r.id) }
                    }
                }
            }
        }

        // Reach Out — the affordable turn-outward (Phase 3, the Lodge). Always available, never gated
        // (turning to a real person is help, not advanced curriculum). Opens the phone's own messaging
        // apps with an editable opener; the app hosts no relationship and sends nothing itself.
        if (!purging) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You don't have to carry this alone — reach someone you trust",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { reachOut(context, ReachOut.openerAt(reachIndex)); reachIndex++ }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Hands an editable opener to the phone's own messaging apps (SMS, WhatsApp, Signal, email, …) via
 * a share sheet. The app never picks a contact or sends anything — the user does, in their app.
 */
private fun reachOut(context: Context, message: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(send, "Reach out through…").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
        // No app available to share text — fail quietly.
    }
}

@Composable
private fun ChamberModeChip(
    label: String,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = 0.15f) else Slate.copy(alpha = 0.15f))
            .border(
                1.dp,
                if (selected) accent.copy(alpha = 0.5f) else Slate,
                androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accent else Silver,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun KeptReflectionCard(reflection: com.example.data.Reflection, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(Slate.copy(alpha = 0.2f))
            .border(1.dp, com.example.ui.theme.DividerWhite.copy(alpha = 0.05f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val dateFormat = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
            Text(
                text = dateFormat.format(java.util.Date(reflection.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = Silver.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reflection.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Silver
            )
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete this reflection",
            tint = Silver.copy(alpha = 0.4f),
            modifier = Modifier
                .padding(start = 12.dp)
                .size(20.dp)
                .clickable { onDelete() }
        )
    }
}
