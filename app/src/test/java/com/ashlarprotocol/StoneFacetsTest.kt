package com.ashlarprotocol

import com.ashlarprotocol.tools.Strength
import com.ashlarprotocol.tools.StoneFacets
import com.ashlarprotocol.tools.Virtue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Each of the stone's six faces is a VIA virtue's facet. This locks the refinement invariants:
 * asymptotic (never a perfect ashlar; SPEC P0.1), monotonic (never un-worked), signature-weighted.
 */
class StoneFacetsTest {

    @Test fun noWorkNoSignature_allFacetsRough() {
        val r = StoneFacets.refinement(emptyList(), 0)
        assertEquals(Virtue.values().size, r.size)
        r.values.forEach { assertEquals(0f, it, 0.0001f) }
    }

    @Test fun signatureVirtueRefinesFasterThanUnclaimed() {
        // BRAVERY is a COURAGE strength; WISDOM is unclaimed here.
        val r = StoneFacets.refinement(listOf(Strength.BRAVERY), score = 30)
        assertTrue("a claimed virtue's facet should lead an unclaimed one", r[Virtue.COURAGE]!! > r[Virtue.WISDOM]!!)
    }

    @Test fun refinementIsMonotonicInWork() {
        val low = StoneFacets.refinement(listOf(Strength.BRAVERY), 10)
        val high = StoneFacets.refinement(listOf(Strength.BRAVERY), 60)
        Virtue.values().forEach { v -> assertTrue("facet ${v} must not regress", high[v]!! >= low[v]!!) }
    }

    @Test fun everyFacetStaysBelowPerfect() {
        val r = StoneFacets.refinement(Strength.values().toList(), score = 100_000)
        r.values.forEach { assertTrue("never a perfect ashlar: $it", it < 1f) }
    }

    @Test fun orderedFacetsMatchVirtueOrderAndCount() {
        val arr = StoneFacets.orderedFacets(listOf(Strength.BRAVERY), 30)
        assertEquals(Virtue.values().size, arr.size)
        val m = StoneFacets.refinement(listOf(Strength.BRAVERY), 30)
        Virtue.values().forEachIndexed { i, v -> assertEquals(m[v]!!, arr[i], 0.0001f) }
    }

    @Test fun negativeScoreClampsToRough() {
        StoneFacets.refinement(listOf(Strength.BRAVERY), -50).values.forEach { assertEquals(0f, it, 0.0001f) }
    }
}
