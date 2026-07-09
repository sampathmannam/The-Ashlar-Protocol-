package com.ashlarprotocol

import com.ashlarprotocol.tools.DailyWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The daily word — a bundled, on-device rotation replacing the paid Gemini call. Pure and
 * deterministic: no network, no API key, no per-use cost. See docs/MASTER_PLAN.md.
 */
class DailyWordTest {

    @Test
    fun libraryIsNonEmptyAndClean() {
        assertTrue("expected a real rotation of words", DailyWord.WORDS.size >= 10)
        DailyWord.WORDS.forEach { assertTrue(it.isNotBlank()) }
    }

    @Test
    fun wordAtIsStableForTheSameIndex() {
        assertEquals(DailyWord.wordAt(5), DailyWord.wordAt(5))
    }

    @Test
    fun wordAtWrapsAroundTheLibrary() {
        assertEquals(DailyWord.WORDS[0], DailyWord.wordAt(0))
        assertEquals(DailyWord.WORDS[0], DailyWord.wordAt(DailyWord.WORDS.size))
        assertEquals(DailyWord.WORDS[1 % DailyWord.WORDS.size], DailyWord.wordAt(DailyWord.WORDS.size + 1))
    }

    @Test
    fun wordAtHandlesNegativeIndexSafely() {
        assertEquals(DailyWord.WORDS.last(), DailyWord.wordAt(-1)) // never crashes on a stray negative
    }
}
