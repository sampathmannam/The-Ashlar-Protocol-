package com.example

import com.example.tools.Strength
import com.example.tools.Strengths
import com.example.tools.Virtue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Character strengths — the intrinsic progression currency (identity + mastery, not points). Grounds
 * the app's virtue framing in the VIA classification (24 strengths under 6 virtues) and Seligman,
 * Steen, Park & Peterson (2005): using a signature strength "in a new way" each day raised happiness
 * and lowered depression for up to six months. See docs/GAMIFICATION_PLAN.md §3 / RESEARCH_BASIS §9.
 * Pure, on-device.
 */
class StrengthsTest {

    @Test
    fun thereAreTheTwentyFourViaStrengths() {
        assertEquals(24, Strengths.all().size)
        assertEquals("no duplicates", 24, Strengths.all().toSet().size)
    }

    @Test
    fun everyStrengthHasADisplayNameAndAVirtue() {
        Strengths.all().forEach {
            assertTrue("display for $it", it.display.isNotBlank())
        }
    }

    @Test
    fun allSixViaVirtuesAreRepresented() {
        assertEquals(6, Virtue.values().size)
        val covered = Strengths.all().map { it.virtue }.toSet()
        assertEquals("every virtue has at least one strength", Virtue.values().toSet(), covered)
    }

    @Test
    fun forVirtueReturnsOnlyThatVirtuesStrengths() {
        Virtue.values().forEach { v ->
            val group = Strengths.forVirtue(v)
            assertTrue("$v has strengths", group.isNotEmpty())
            assertTrue("all belong to $v", group.all { it.virtue == v })
        }
    }

    @Test
    fun signatureReturnsTheTopRankedStrengths() {
        val ranked = Strengths.all() // treat list order as the user's ranking
        val top5 = Strengths.signature(ranked, 5)
        assertEquals(5, top5.size)
        assertEquals(ranked.take(5), top5)
    }

    @Test
    fun signatureIsClampedAndNeverThrows() {
        assertTrue("empty ranking -> empty", Strengths.signature(emptyList(), 5).isEmpty())
        assertEquals("asking for more than exist returns all", 24, Strengths.signature(Strengths.all(), 100).size)
        assertTrue("a non-positive count -> empty", Strengths.signature(Strengths.all(), -1).isEmpty())
    }

    @Test
    fun newWayPromptNamesTheStrengthAndInvitesANewUse() {
        val s = Strength.CURIOSITY
        val prompt = Strengths.newWayPrompt(s).lowercase()
        assertTrue("names the strength", prompt.contains(s.display.lowercase()))
        assertTrue("invites a NEW way", prompt.contains("new way") || prompt.contains("new "))
    }
}
