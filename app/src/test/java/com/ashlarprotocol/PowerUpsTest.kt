package com.ashlarprotocol

import com.ashlarprotocol.tools.PowerUps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Power-Ups — pure content for the always-available quick mood-lifters (SPEC P0.6 / T2.4).
 * These guarantees are what the always-open sheet relies on. Pure, on-device.
 */
class PowerUpsTest {

    @Test
    fun libraryIsWellFormed() {
        assertTrue("expected a few lifters", PowerUps.POWER_UPS.size >= 3)
        assertEquals("ids are unique", PowerUps.POWER_UPS.size, PowerUps.POWER_UPS.map { it.id }.toSet().size)
        PowerUps.POWER_UPS.forEach { p ->
            assertTrue(p.title.isNotBlank())
            assertTrue(p.invite.isNotBlank())
            assertTrue("every lifter has steps to follow", p.steps.isNotEmpty())
            p.steps.forEach { assertTrue(it.isNotBlank()) }
        }
    }

    @Test
    fun lookupResolvesAndFailsSafely() {
        assertNotNull(PowerUps.byId("breath"))
        assertNull(PowerUps.byId("not_a_lifter"))
    }

    @Test
    fun coversTheCoreSteadyingModes() {
        // Breath (paced ~6/min), grounding, body-release, and a self-compassion word.
        val ids = PowerUps.POWER_UPS.map { it.id }.toSet()
        assertTrue(ids.contains("breath"))
        assertTrue(ids.contains("ground"))
        assertTrue(ids.contains("kind"))
    }

    @Test
    fun theBreathLifterLeadsWithTheLongerExhale() {
        // The evidence-based detail: exhale longer than the inhale (RESEARCH_BASIS §8).
        val breath = PowerUps.byId("breath")!!
        assertTrue(breath.steps.any { it.contains("six") || it.contains("longer") })
    }
}
