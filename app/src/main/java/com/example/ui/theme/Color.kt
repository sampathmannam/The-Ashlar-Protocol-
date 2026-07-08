package com.example.ui.theme

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
