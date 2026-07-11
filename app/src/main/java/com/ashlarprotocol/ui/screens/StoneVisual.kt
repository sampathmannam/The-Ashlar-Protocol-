package com.ashlarprotocol.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.tools.Strength
import com.ashlarprotocol.tools.Strengths
import com.ashlarprotocol.tools.Readiness
import com.ashlarprotocol.ui.AshlarAppViewModel
import com.ashlarprotocol.ui.DailyWorking
import com.ashlarprotocol.ui.theme.Charcoal
import com.ashlarprotocol.ui.theme.DividerWhite
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import com.ashlarprotocol.ui.theme.Surface
import com.ashlarprotocol.ui.theme.ashlarCard
import com.ashlarprotocol.ui.theme.CardEmphasis

// The 3D ashlar renderer — extracted from BoardScreen (Phase-4 breakup of the 1,715-line file).
// Same package as BoardScreen, so the call site (TracingBoardVisual) needs no new import.

@Composable
fun TracingBoardVisual(
    progress: Float,
    degreeName: String,
    daysTended: Int,
    pulse: Int = 0,
    facets: FloatArray? = null,
    graceLabel: String? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .ashlarCard(CardEmphasis.Hero)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THE PATH",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.55f),
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AshlarStone(
            progress = animatedProgress,
            pulse = pulse,
            facets = facets,
            // The hero was invisible to TalkBack; describe its state so screen readers get the metaphor.
            modifier = Modifier
                .size(190.dp)
                .semantics {
                    contentDescription =
                        "The stone, in the working — $daysTended ${if (daysTended == 1) "day" else "days"} tended, $degreeName."
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Never a "complete"/"perfect achieved" state — the work is lifelong (SPEC P0.1).
        Text(
            text = "THE STONE, IN THE WORKING",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = degreeName.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = LightText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // The primary continuity signal — total days tended, which only ever grows (SPEC P0.3). Kept
        // quiet beneath the stone: no scoreboard, no deadline, no "streak at risk". The stone itself
        // (visual closure) is the reward; this line just names what fed it.
        Text(
            text = if (daysTended <= 0) "the work begins the first day you tend the stone"
                   else "$daysTended ${if (daysTended == 1) "DAY" else "DAYS"} TENDED · THE WORK IS LIFELONG",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        // The grace reserve, made visible (F3). A small, scarce, protectable number the stone holds
        // for you — surfacing it (rather than a silent buffer) raises persistence after a miss because
        // a named, limited reserve is something you protect (Sharif & Shu). Never a warning.
        if (graceLabel != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = graceLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.45f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * The ashlar — a real 3D carved stone that slowly turns, its faces catching a fixed light as it
 * rotates (that's the life in it). Roughness (chisel hatching, dull dark faces, thick edges) smooths
 * toward gold-edged, brighter stone as [progress] rises: the rough ashlar becoming the perfect one.
 * Pure Canvas + 3D math — no assets.
 */
@Composable
fun AshlarStone(progress: Float, pulse: Int = 0, facets: FloatArray? = null, modifier: Modifier = Modifier) {
    // Respect reduced-motion: if the system disables animations, the stone rests at a still angle
    // instead of the slow 30s spin — a vestibular + battery courtesy (no continuous Canvas redraw).
    val motionOn = com.ashlarprotocol.ui.theme.animationsEnabled()
    val rotation by rememberInfiniteTransition(label = "ashlar").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    // Micro-feedback (SPEC P0.4 / T2.3): a brief light-catch when a REAL action lands. [pulse] is
    // bumped only on genuine completions (a practice, the Working check-in) — never a timer or a
    // login — so the flash is always an honest mirror of something the person just did.
    val flash = remember { Animatable(0f) }
    LaunchedEffect(pulse) {
        if (pulse > 0) {
            flash.snapTo(1f)
            flash.animateTo(0f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        }
    }
    Canvas(modifier = modifier) {
        drawAshlar(rotationDeg = if (motionOn) rotation else 22f, progress = progress, flash = flash.value, facets = facets)
    }
}

/** A cube face carried through depth-sorting, keeping its ordinal so it maps to a virtue facet. */
private class VisibleFace(val faceOrdinal: Int, val idx: IntArray, val rn: FloatArray, val depth: Float)

private fun DrawScope.drawAshlar(rotationDeg: Float, progress: Float, flash: Float = 0f, facets: FloatArray? = null) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val unit = size.minDimension * 0.23f * (1f + flash * 0.04f) // a subtle pop as the chisel strikes

    val a = Math.toRadians(rotationDeg.toDouble())
    val tilt = Math.toRadians(24.0)
    val ca = kotlin.math.cos(a); val sa = kotlin.math.sin(a)
    val ct = kotlin.math.cos(tilt); val st = kotlin.math.sin(tilt)

    fun rot(p: FloatArray): FloatArray {
        val x = p[0].toDouble(); val y = p[1].toDouble(); val z = p[2].toDouble()
        val x1 = x * ca + z * sa
        val z1 = -x * sa + z * ca
        val y2 = y * ct - z1 * st
        val z2 = y * st + z1 * ct
        return floatArrayOf(x1.toFloat(), y2.toFloat(), z2.toFloat())
    }
    val focal = 6f
    fun proj(r: FloatArray): Offset {
        val s = focal / (focal - r[2])
        return Offset(cx + r[0] * s * unit, cy - r[1] * s * unit)
    }

    val h = 1f
    val cube = arrayOf(
        floatArrayOf(-h, -h, -h), floatArrayOf(h, -h, -h), floatArrayOf(h, h, -h), floatArrayOf(-h, h, -h),
        floatArrayOf(-h, -h, h), floatArrayOf(h, -h, h), floatArrayOf(h, h, h), floatArrayOf(-h, h, h)
    )
    val rotated = Array(8) { rot(cube[it]) }
    val screen = Array(8) { proj(rotated[it]) }

    drawOval(
        color = Color.Black.copy(alpha = 0.35f),
        topLeft = Offset(cx - unit * 1.3f, cy + unit * 1.15f),
        size = Size(unit * 2.6f, unit * 0.7f)
    )

    val faces = listOf(
        intArrayOf(4, 5, 6, 7) to floatArrayOf(0f, 0f, 1f),
        intArrayOf(1, 0, 3, 2) to floatArrayOf(0f, 0f, -1f),
        intArrayOf(5, 1, 2, 6) to floatArrayOf(1f, 0f, 0f),
        intArrayOf(0, 4, 7, 3) to floatArrayOf(-1f, 0f, 0f),
        intArrayOf(3, 2, 6, 7) to floatArrayOf(0f, 1f, 0f),
        intArrayOf(0, 1, 5, 4) to floatArrayOf(0f, -1f, 0f)
    )

    val lx = -0.4f; val ly = 0.72f; val lz = 0.56f
    val stoneDark = Color(0xFF221A12)
    val edgeColor = lerp(Color(0xFF3A2E1E), Gold, progress)
    val brightGold = Color(0xFFF6E6B4) // the light the stone catches the instant it's worked

    val visible = faces
        .mapIndexed { faceOrdinal, pair ->
            val (idx, n) = pair
            val rn = rot(n)
            val depth = idx.map { rotated[it][2] }.average().toFloat()
            VisibleFace(faceOrdinal, idx, rn, depth)
        }
        .filter { it.rn[2] > 0.02f }
        .sortedBy { it.depth }

    for (face in visible) {
        val idx = face.idx
        val rn = face.rn
        // Each of the six faces is a VIA virtue's facet (tools/StoneFacets). Its own refinement gently
        // nudges this face's smoothness and light — bounded (≤28%) so the stone reads as one coherent
        // whole, never patchy. Null facets => every face uses the global progress (unchanged look).
        val faceProgress = facets?.getOrNull(face.faceOrdinal)
            ?.let { (progress * 0.72f + it * 0.28f).coerceIn(0f, 1f) }
            ?: progress
        val stoneLitFace = lerp(Color(0xFF6E5A3C), Gold, (faceProgress * 0.55f).coerceIn(0f, 1f))
        val roughnessFace = 1f - faceProgress

        val nl = (rn[0] * lx + rn[1] * ly + rn[2] * lz).coerceIn(0f, 1f)
        val shade = 0.30f + 0.70f * nl
        val baseFace = lerp(stoneDark, stoneLitFace, shade)
        val faceColor = if (flash > 0f) lerp(baseFace, brightGold, flash * 0.5f) else baseFace

        val path = Path().apply {
            moveTo(screen[idx[0]].x, screen[idx[0]].y)
            lineTo(screen[idx[1]].x, screen[idx[1]].y)
            lineTo(screen[idx[2]].x, screen[idx[2]].y)
            lineTo(screen[idx[3]].x, screen[idx[3]].y)
            close()
        }
        drawPath(path, color = faceColor)

        if (roughnessFace > 0.02f) {
            val p0 = screen[idx[0]]; val p1 = screen[idx[1]]; val p2 = screen[idx[2]]; val p3 = screen[idx[3]]
            for (f in listOf(0.32f, 0.5f, 0.68f)) {
                drawLine(
                    color = Color(0xFF120D08).copy(alpha = roughnessFace * 0.30f),
                    start = Offset(p0.x + (p3.x - p0.x) * f, p0.y + (p3.y - p0.y) * f),
                    end = Offset(p1.x + (p2.x - p1.x) * f, p1.y + (p2.y - p1.y) * f),
                    strokeWidth = 1f
                )
            }
        }

        drawPath(
            path,
            color = (if (flash > 0f) lerp(edgeColor, brightGold, flash) else edgeColor)
                .copy(alpha = (0.45f + 0.55f * faceProgress + flash * 0.4f).coerceIn(0f, 1f)),
            style = Stroke(width = 1.5f + roughnessFace * 1.5f + flash * 1.5f)
        )
    }
}

