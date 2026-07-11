package com.ashlarprotocol.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The Board/Tools cards, as ONE primitive with three emphasis tiers — so the app reads as a page with
 * a focal point instead of a wall of identical surfaces. It reads the [Radius]/[Space] tokens, so every
 * card sits on the same rhythm, and replaces ~50 copy-pasted `clip/background/border/padding` chains.
 *
 * Depth on a dark ground is expressed by a *lighter warm surface* and a *more-visible hairline*, never
 * by shadow (which vanishes on near-black). So emphasis reads as: how much the card lifts off the page.
 *
 *  - [Hero]     the one thing that matters on the screen (the stone). Lifts: lighter surface + a real
 *               hairline. Draws the eye first.
 *  - [Standard] the working cards. The current default: card surface + a faint hairline.
 *  - [Quiet]    the look-back / record cards. Recedes: no border, tighter radius — present, not loud.
 */
enum class CardEmphasis { Hero, Standard, Quiet }

/**
 * Apply the Ashlar card *surface* (clip + fill + optional hairline) to a container. Padding is left to
 * the caller (keep the existing `.padding(Space.lg)`), so this is a clean drop-in for the old
 * `.clip(RoundedCornerShape(..)).background(Surface).border(..)` chain — replace those three lines with
 * a single `.ashlarCard()` (or `.ashlarCard(CardEmphasis.Hero)`), leaving the `.padding(..)` line.
 */
fun Modifier.ashlarCard(emphasis: CardEmphasis = CardEmphasis.Standard): Modifier {
    val radius: Dp = if (emphasis == CardEmphasis.Quiet) Radius.lg else Radius.xl
    val shape = RoundedCornerShape(radius)
    val fill = if (emphasis == CardEmphasis.Hero) SurfaceContainer else Surface
    val base = this.clip(shape).background(fill)
    return when (emphasis) {
        // A real hairline that carries the lift — kept ≥3:1 against the surface for a11y.
        CardEmphasis.Hero -> base.border(1.dp, Outline.copy(alpha = 0.45f), shape)
        // The faint existing hairline.
        CardEmphasis.Standard -> base.border(1.dp, DividerWhite.copy(alpha = 0.05f), shape)
        // No border — lets the record cards settle back.
        CardEmphasis.Quiet -> base
    }
}
