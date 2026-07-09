package com.ashlarprotocol.tools

/**
 * The stone's facets — one per VIA virtue (Peterson & Seligman, 2004; see [Virtue]).
 *
 * The rough ashlar is not a uniform block: it is worked facet by facet, and the facets ARE the six
 * virtues. As the member does the work — and especially work aligned with a virtue they have claimed
 * as a signature strength — that facet of the stone is refined. The cube stone has exactly six faces,
 * so each face is a virtue's facet (docs/RESEARCH_BASIS.md §9; docs/GAMIFICATION_PLAN.md §3).
 *
 * Two invariants, both from the stone's own promise:
 *  - **Never completes.** Refinement is asymptotic (< 1f) — there is no perfect ashlar (SPEC P0.1).
 *  - **Never regresses.** Refinement is monotonic in work done; a facet is never un-worked.
 *
 * Pure, on-device.
 */
object StoneFacets {
    // A signature-aligned virtue refines faster (smaller half-work constant) than an unclaimed one.
    // Neither ever reaches 1f, so no facet — and thus no face of the stone — is ever "finished".
    private const val K_SIGNATURE = 20f
    private const val K_BASE = 45f
    private const val CEILING = 0.999f

    /** Refinement in 0f..CEILING for every virtue, given the [signature] strengths and total [score]. */
    fun refinement(signature: List<Strength>, score: Int): Map<Virtue, Float> {
        val s = score.coerceAtLeast(0).toFloat()
        val signatureVirtues = signature.map { it.virtue }.toSet()
        return Virtue.values().associateWith { v ->
            val k = if (v in signatureVirtues) K_SIGNATURE else K_BASE
            (s / (s + k)).coerceIn(0f, CEILING)
        }
    }

    /**
     * The facet values in canonical [Virtue] order — one per cube face, for the stone renderer.
     * Index i corresponds to Virtue.values()[i].
     */
    fun orderedFacets(signature: List<Strength>, score: Int): FloatArray {
        val m = refinement(signature, score)
        val virtues = Virtue.values()
        return FloatArray(virtues.size) { m[virtues[it]] ?: 0f }
    }
}
