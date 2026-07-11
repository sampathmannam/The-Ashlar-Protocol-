package com.ashlarprotocol.ui.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Design tokens — the non-color, non-type half of the system: spacing, radius, motion, and the
 * accessibility minimums. Screens should read these by name instead of hard-coding magic numbers, so
 * the whole app stays on one rhythm. (Colors live in [Color]/[Theme]; type in [Type].)
 */

/** Spacing on a strict 4/8-pt scale. Generous negative space is the cheapest way to look calm. */
object Space {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

/** Corner radii — a small, deliberate scale (was 4→32 ad hoc). */
object Radius {
    val sm = 12.dp    // chips, small controls
    val md = 18.dp    // inner rows, buttons
    val lg = 24.dp    // tool cards
    val xl = 32.dp    // primary Board cards
}

/** Motion durations (ms). Calm = brief and purposeful; nothing loops or pulses for attention. */
object Motion {
    const val short = 150
    const val medium = 250
    const val long = 400
}

/** Accessibility minimums. */
object A11y {
    val minTarget = 48.dp   // Material minimum touch target
}

/**
 * Whether the system allows animation (Settings → animator duration scale > 0). Gate any *continuous*
 * animation (the stone's slow rotation, the breathing loop) on this so motion-sensitive users — and
 * battery — are respected; when false, show the calm static state instead.
 */
@Composable
fun animationsEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    val scale = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    return scale != 0f
}
