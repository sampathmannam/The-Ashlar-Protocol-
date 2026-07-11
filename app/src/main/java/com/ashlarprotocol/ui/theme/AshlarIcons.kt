package com.ashlarprotocol.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * A few hand-drawn glyphs, so the app doesn't depend on the heavy material-icons-extended set (which
 * ships unshrunk while release minify is off) and can stay on-theme — candlelight, not clip-art.
 * Icons render in whatever `tint` the caller passes; the fill here is a placeholder.
 */

/** A candle flame — the Chamber of Reflection, lit. Replaces the stray "trash can" that was the tab. */
val AshlarFlame: ImageVector by lazy {
    ImageVector.Builder(
        name = "AshlarFlame", defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // A teardrop flame with a small inner curl.
            moveTo(12f, 2.5f)
            curveTo(12f, 2.5f, 6.5f, 8.2f, 6.5f, 13.8f)
            curveTo(6.5f, 17.1f, 8.9f, 19.8f, 12f, 19.8f)
            curveTo(15.1f, 19.8f, 17.5f, 17.1f, 17.5f, 13.8f)
            curveTo(17.5f, 8.2f, 12f, 2.5f, 12f, 2.5f)
            close()
            // inner light
            moveTo(12f, 16.6f)
            curveTo(10.4f, 16.6f, 9.3f, 15.4f, 9.3f, 13.9f)
            curveTo(9.3f, 11.9f, 12f, 8.9f, 12f, 8.9f)
            curveTo(12f, 8.9f, 14.7f, 11.9f, 14.7f, 13.9f)
            curveTo(14.7f, 15.4f, 13.6f, 16.6f, 12f, 16.6f)
            close()
        }
    }.build()
}
