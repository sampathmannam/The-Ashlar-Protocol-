package com.ashlarprotocol

import com.ashlarprotocol.tools.Greeting
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The remembered greeting (SPEC T1.9, re-scoped). Composed from on-device memory: references what the
 * app remembers (intention + days tended), warm, never obligation/pressure.
 */
class GreetingTest {

    @Test
    fun greetsByTimeOfDay() {
        fun g(h: Int) = Greeting.greeting(Greeting.Context(hourOfDay = h, daysTended = 0, intention = ""))
        assertTrue(g(8).startsWith("Good morning"))
        assertTrue(g(14).startsWith("Good afternoon"))
        assertTrue(g(20).startsWith("Good evening"))
        assertTrue(g(2).startsWith("You're up late"))
    }

    @Test
    fun remembersTheIntentionAndTheDaysTended() {
        val g = Greeting.greeting(Greeting.Context(hourOfDay = 19, daysTended = 5, intention = "steadier days"))
        assertTrue(g.contains("5 days tended"))
        assertTrue(g.contains("steadier days"))
    }

    @Test
    fun handlesDaySingularAndTheFreshStart() {
        assertTrue(Greeting.greeting(Greeting.Context(19, 1, "")).contains("1 day tended"))
        // Nothing tended yet → a fresh, welcoming line with no day-count.
        val fresh = Greeting.greeting(Greeting.Context(9, 0, ""))
        assertFalse(fresh.contains("tended"))
        assertTrue(fresh.contains("Good to have you here"))
    }

    @Test
    fun isWelcomeNeverObligation() {
        val all = listOf(
            Greeting.greeting(Greeting.Context(9, 0, "a clearer head")),
            Greeting.greeting(Greeting.Context(23, 10, "steadier days"))
        ).joinToString(" ").lowercase()
        assertFalse(
            "a greeting must welcome, never pressure",
            all.contains("you should") || all.contains("don't lose") || all.contains("streak") ||
                all.contains("must ") || all.contains("behind")
        )
    }
}
