package com.ashlarprotocol.ui.theme

import androidx.compose.ui.graphics.Color

// The "lodge at night" palette — candlelight, gold, and warm stone. Every screen reads these by
// name, so the whole app is themed from here. (Replaced the old cold tactical-blue/cyan scheme —
// note the accent is now a true warm gold, not the cyan that used to hide behind the name "Gold".)
// Contrast: gold #C9A24A on the dark surfaces passes WCAG AA (~7:1); text uses a candlelit off-white
// rather than pure white to avoid halation on the dark ground.
val Charcoal = Color(0xFF0C0906)     // warm near-black — the page
val Surface = Color(0xFF1A1511)      // warm dark stone — cards
val Slate = Color(0xFF2A2119)        // warm slate — secondary fills
val Gold = Color(0xFFC9A24A)         // true warm gold — the accent
val Silver = Color(0xFF9B8B71)       // warm muted text
val LightText = Color(0xFFEDE3D1)    // candlelit off-white — primary text
val RedAlert = Color(0xFFD9574A)     // warm alert red — crisis (still unmistakably red)
val DividerWhite = Color(0xFFF0E8D8) // warm hairline (used at low alpha)

// Surface elevation tiers — on a dark ground, depth is expressed by *lighter warm surfaces* and
// hairline outlines, never shadows (which vanish). Each step is a few % lighter than the last.
val SurfaceContainer = Color(0xFF201A14)      // one step above a card
val SurfaceContainerHigh = Color(0xFF2A2219)  // sheets, raised affordances
// Outlines — the hairline borders that carry depth on dark. Kept ≥3:1 against surfaces for a11y.
val Outline = Color(0xFF7A6B54)               // visible hairline / focus ring
val OutlineVariant = Color(0xFF3B3025)        // subtle divider
