package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * One "cornerstone": a self-directed environment change, squared around a cue (Phase 4, F1 — see
 * tools/Cornerstone). The app stores your plan and prompts you to act in the world; it automates
 * nothing and touches nothing outside itself. Local only, never uploaded.
 */
@Serializable
data class CornerstoneEntry(
    /** The behaviour you're designing around (e.g. "read at night", "stop late-night scrolling"). */
    val behavior: String,
    /** The cue you'll design around — a `Cornerstone.CueKind` name (TIME/PLACE/AFTER_ACTION/OBJECT). */
    val cueKind: String,
    /** Your own detail for the cue (e.g. "after dinner", "on the kitchen counter"). */
    val cueDetail: String,
    /** The one friction move you chose — a `Cornerstone.FrictionMove` display string. */
    val move: String
)
