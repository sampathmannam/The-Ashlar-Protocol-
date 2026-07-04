package com.example.data

import kotlinx.serialization.Serializable

/**
 * A reflection the user chose to KEEP in the Chamber (as opposed to releasing it). Oriented toward
 * meaning and values rather than catharsis. Stored on-device only, like everything else.
 */
@Serializable
data class Reflection(
    val id: String,
    val text: String,
    val timestamp: Long
)
