package com.ashlarprotocol

import com.ashlarprotocol.tools.Adornment
import org.junit.Assert.*
import org.junit.Test

class AdornmentTest {
    @Test fun theDefaultFinishIsFreeAndAlwaysAvailable() {
        val d = Adornment.finishOf(Adornment.DEFAULT_ID)!!
        assertEquals(0, d.cost)
        assertTrue(Adornment.isAvailable(Adornment.DEFAULT_ID, emptyList()))
        // selecting with no data falls back to the default (the Temple always has a look)
        assertEquals(d, Adornment.selectedOrDefault(null))
        assertEquals(d, Adornment.selectedOrDefault("nonsense"))
    }

    @Test fun boughtFinishesCostWagesAndBecomeAvailable() {
        val marble = Adornment.finishOf("marble")!!
        assertTrue("a bought finish costs wages", marble.cost > 0)
        assertFalse("locked until bought", Adornment.isAvailable("marble", emptyList()))
        assertTrue("available once owned", Adornment.isAvailable("marble", listOf("marble")))
    }

    @Test fun totalSpendSumsOnlyBoughtFinishes() {
        // the free default adds nothing; two bought finishes sum their costs
        assertEquals(0, Adornment.totalSpend(listOf("sandstone")))
        val expected = Adornment.costOf("marble") + Adornment.costOf("lapis")
        assertEquals(expected, Adornment.totalSpend(listOf("marble", "lapis")))
    }

    @Test fun everyFinishIsRealAndColoured() {
        assertTrue(Adornment.FINISHES.size >= 4)
        Adornment.FINISHES.forEach {
            assertTrue(it.name.isNotBlank())
            assertTrue("opaque colour", (it.argb ushr 24) and 0xFF == 0xFFL)
        }
        assertTrue(Adornment.allText().any { it == "Marble" })
    }
}
