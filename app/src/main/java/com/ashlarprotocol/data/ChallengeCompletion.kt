package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * A record that a challenge was completed in a given period. Append-only — the ledger only grows;
 * a missed challenge writes nothing and takes nothing. [periodKey] is the epoch-day (DAILY) or
 * epoch-week (WEEKLY) so a challenge pays wages at most once per period (idempotent, un-farmable).
 */
@Serializable
data class ChallengeCompletion(
    val challengeId: String,
    val cadence: String,
    val periodKey: Long,
    val timestamp: Long
)
