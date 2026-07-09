package com.example.data

import kotlinx.serialization.Serializable

/**
 * A practice the person authored for themselves: an approach action anchored to an existing routine
 * ("After [anchor], I will [action]"). Stored locally only. See tools/PracticeAuthoring.
 */
@Serializable
data class Practice(
    val id: String,
    val anchor: String,
    val action: String,
    val timestamp: Long
)
