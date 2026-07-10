package com.ashlarprotocol

import com.ashlarprotocol.tools.Automaticity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Automaticity (F4) — the honest progress signal is "becoming automatic," not streak count. The cadence
 * must be gentle (about weekly), never daily surveillance (Milyavskaya).
 */
class AutomaticityTest {

    @Test fun levelsAscendAndAreDistinct() {
        assertTrue(Automaticity.LEVELS.isNotEmpty())
        val values = Automaticity.LEVELS.map { it.value }
        assertEquals("values are distinct", values.size, values.toSet().size)
        assertEquals("values ascend", values.sorted(), values)
    }

    @Test fun everyLevelHasCopyAndAReflection() {
        Automaticity.LEVELS.forEach { assertTrue(it.label.isNotBlank()) }
        (0..2).forEach { assertTrue("reflection $it", Automaticity.reflection(it).isNotBlank()) }
    }

    @Test fun cadenceIsGentle_neverAskedThenWeekly() {
        assertTrue("never asked → due", Automaticity.isDue(lastAskedDay = -1L, today = 100L))
        assertFalse("asked today → not due", Automaticity.isDue(100L, 100L))
        assertFalse("only 5 days → not due", Automaticity.isDue(95L, 100L))
        assertTrue("7 days → due again", Automaticity.isDue(93L, 100L))
    }
}
