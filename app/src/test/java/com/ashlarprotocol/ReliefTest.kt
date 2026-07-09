package com.ashlarprotocol

import com.ashlarprotocol.tools.Relief
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Well — bundled words of relief surfaced at the cathartic moment (after releasing weight in
 * the Chamber). Zero-infrastructure "being met": no server, no accounts, no network. This is the
 * Phase 3 (the Lodge) mechanism made affordable — mutual-aid warmth without hosting a relationship.
 */
class ReliefTest {

    @Test
    fun wellIsNonEmptyAndClean() {
        assertTrue(Relief.WORDS.size >= 6)
        Relief.WORDS.forEach { assertTrue(it.isNotBlank()) }
    }

    @Test
    fun reliefAtWrapsAndIsStable() {
        assertEquals(Relief.WORDS[0], Relief.reliefAt(0))
        assertEquals(Relief.WORDS[0], Relief.reliefAt(Relief.WORDS.size))
        assertEquals(Relief.reliefAt(3), Relief.reliefAt(3))
    }

    @Test
    fun reliefAtHandlesNegativeSafely() {
        assertEquals(Relief.WORDS.last(), Relief.reliefAt(-1))
    }
}
