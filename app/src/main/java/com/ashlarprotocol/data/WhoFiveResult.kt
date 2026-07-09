package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * One WHO-5 wellbeing check the person completed: the 0–100 score and when. Stored locally only —
 * a private record for their own noticing, never uploaded. See tools/WhoFive.
 */
@Serializable
data class WhoFiveResult(
    val id: String,
    val score: Int,
    val timestamp: Long
)
