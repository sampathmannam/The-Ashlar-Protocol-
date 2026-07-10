package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * One "rough edge" the person is working (Phase 4b, F5): a bad habit/compulsion, the cue around it, a
 * self-directed environment change, a replacement approach-action, and an APPEND-ONLY ledger of lapse
 * timestamps. The ledger only ever grows and is never a "streak" to break — a slip is data, not a
 * verdict (anti-AVE; see tools/RoughEdge). Local only, never uploaded.
 */
@Serializable
data class RoughEdgeEntry(
    val name: String,
    val cue: String,
    val environmentMove: String,
    val replacement: String,
    /** Lapse timestamps — append-only. Never reset; there is no streak here to lose. */
    val lapses: List<Long> = emptyList()
)
