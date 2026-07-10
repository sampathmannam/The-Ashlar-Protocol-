package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * The rhythm anchor (F6): a consistent rise and wind-down time. Stored as minutes-of-day. Sleep-wake
 * *regularity* (not duration or earliness) is what the evidence associates with steadier mood
 * (Windred 2024; Li 2025). Local only; never an alarm, never tracked. See tools/Rhythm.
 */
@Serializable
data class RhythmAnchor(
    val wakeMinutesOfDay: Int,
    val windDownMinutesOfDay: Int
)
