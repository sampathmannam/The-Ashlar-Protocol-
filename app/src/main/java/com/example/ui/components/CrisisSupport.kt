package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.safety.CrisisDetector
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DividerWhite
import com.example.ui.theme.Gold
import com.example.ui.theme.LightText
import com.example.ui.theme.RedAlert
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import com.example.ui.theme.Surface

/**
 * A single, tappable way to reach human help. Kind types:
 *  - PHONE  -> opens the dialer (ACTION_DIAL, no CALL_PHONE permission needed)
 *  - SMS    -> opens the messaging app pre-addressed
 *  - WEB    -> opens a browser (used for the international "find a helpline" directory)
 */
data class CrisisResource(
    val name: String,
    val detail: String,
    val actionLabel: String,
    val kind: Kind,
    val target: String,
    val smsBody: String? = null
) {
    enum class Kind { PHONE, SMS, WEB }
}

/**
 * Curated crisis resources. Bundled on-device so help is reachable with NO network.
 *
 * Honest note (see docs/RESEARCH_BASIS.md): crisis numbers are region-specific. We show a
 * few widely-used lines plus a "find a helpline for your country" link, and we say so plainly
 * rather than pretending one number fits the world. Localizing this list by region is a
 * follow-on task in MASTER_PLAN Phase 1A.
 */
val CRISIS_RESOURCES: List<CrisisResource> = listOf(
    CrisisResource(
        name = "988 Suicide & Crisis Lifeline",
        detail = "United States & Canada · call or text, 24/7",
        actionLabel = "CALL 988",
        kind = CrisisResource.Kind.PHONE,
        target = "988"
    ),
    CrisisResource(
        name = "Crisis Text Line",
        detail = "US · text a trained counselor",
        actionLabel = "TEXT HOME TO 741741",
        kind = CrisisResource.Kind.SMS,
        target = "741741",
        smsBody = "HOME"
    ),
    CrisisResource(
        name = "Samaritans",
        detail = "United Kingdom & Ireland · free, 24/7",
        actionLabel = "CALL 116 123",
        kind = CrisisResource.Kind.PHONE,
        target = "116123"
    ),
    CrisisResource(
        name = "Find a Helpline",
        detail = "Anywhere in the world · your country's crisis lines",
        actionLabel = "OPEN DIRECTORY",
        kind = CrisisResource.Kind.WEB,
        target = "https://findahelpline.com"
    )
)

/**
 * Hoisted controller so ANY screen (Chamber, journal, etc.) can offer help without threading
 * dialog state through every function signature. Provided via [LocalCrisisController] at the
 * app root; the dialog itself is rendered once, above everything.
 */
class CrisisController {
    var visible by mutableStateOf(false)
        private set

    // Once we auto-surface for a given piece of text, don't nag on every keystroke.
    // Reset when the text is cleared/purged so a new episode is caught fresh.
    private var autoSurfaced = false

    /** User tapped the always-available "Need help now?" affordance. Always honored. */
    fun openManual() { visible = true }

    /** Passive scan of free text. Additive: only ever opens help, never blocks input. */
    fun scan(text: String?) {
        if (autoSurfaced) return
        if (CrisisDetector.detect(text)) {
            visible = true
            autoSurfaced = true
        }
    }

    fun close() { visible = false }

    /** Call when the associated text is cleared so a later crisis is caught again. */
    fun resetAuto() { autoSurfaced = false }
}

val LocalCrisisController = staticCompositionLocalOf<CrisisController> {
    error("No CrisisController provided. Wrap content in a CrisisController provider.")
}

/**
 * The unconditional help surface. Never behind AI, never gated by "are you sure?".
 * Immediate, tappable human help + a calm, non-clinical message.
 */
@Composable
fun CrisisSupportDialog(controller: CrisisController) {
    if (!controller.visible) return
    val context = LocalContext.current

    Dialog(onDismissRequest = { controller.close() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, RedAlert.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "YOU ARE NOT ALONE",
                style = MaterialTheme.typography.labelSmall,
                color = RedAlert,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "If you're in pain right now, please reach out. Talking to a real person helps, and it's available any time — free and confidential.",
                style = MaterialTheme.typography.bodyMedium,
                color = LightText,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            CRISIS_RESOURCES.forEach { resource ->
                CrisisResourceRow(resource = resource, onAct = { act(context, resource) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "If you are in immediate danger, call your local emergency number (112, 911, or 999).",
                style = MaterialTheme.typography.labelSmall,
                color = Silver,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "The Ashlar Protocol is a practice, not a clinician. It can't replace human help — but it can point you to it.",
                style = MaterialTheme.typography.labelSmall,
                color = Silver.copy(alpha = 0.6f),
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
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

@Composable
private fun CrisisResourceRow(resource: CrisisResource, onAct: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate.copy(alpha = 0.35f))
            .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable { onAct() }
            .padding(16.dp)
    ) {
        Text(text = resource.name, style = MaterialTheme.typography.bodyMedium, color = LightText)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = resource.detail,
            style = MaterialTheme.typography.labelSmall,
            color = Silver,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = resource.actionLabel,
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 1.sp
        )
    }
}

/** Fire the appropriate intent for a resource. Wrapped so a missing handler never crashes. */
private fun act(context: Context, resource: CrisisResource) {
    val intent = when (resource.kind) {
        CrisisResource.Kind.PHONE ->
            Intent(Intent.ACTION_DIAL, "tel:${resource.target}".toUri())
        CrisisResource.Kind.SMS ->
            Intent(Intent.ACTION_SENDTO, "smsto:${resource.target}".toUri()).apply {
                resource.smsBody?.let { putExtra("sms_body", it) }
            }
        CrisisResource.Kind.WEB ->
            Intent(Intent.ACTION_VIEW, resource.target.toUri())
    }.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // No dialer/browser available — silently ignore; other resources remain tappable.
    }
}
