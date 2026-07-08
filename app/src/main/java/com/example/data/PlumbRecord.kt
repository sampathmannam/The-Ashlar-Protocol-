package com.example.data

import kotlinx.serialization.Serializable

/**
 * A saved Plumb thought-record: the thought you worked, and the "squared" reflection composed from
 * your own words. Kept so growth is visible in *your own words* over time — the substrate of the
 * re-authoring engine (see docs/ACTION_PLAN.md §1A). Stored on-device only.
 */
@Serializable
data class PlumbRecord(
    val id: String,
    val thought: String,
    val reflection: String,
    val timestamp: Long
)
