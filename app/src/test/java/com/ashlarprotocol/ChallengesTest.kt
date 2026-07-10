package com.ashlarprotocol

import com.ashlarprotocol.tools.Cadence
import com.ashlarprotocol.tools.Challenges
import com.ashlarprotocol.tools.Temple
import org.junit.Assert.*
import org.junit.Test

class ChallengesTest {
    @Test fun catalogsAreRealAndCited() {
        (Challenges.DAILY + Challenges.WEEKLY).forEach {
            assertTrue(it.id.isNotBlank()); assertTrue(it.title.isNotBlank())
            assertTrue(it.invite.isNotBlank()); assertTrue("cites a basis: ${it.title}", it.basis.isNotBlank())
        }
        assertTrue(Challenges.DAILY.size >= 4)
        assertTrue(Challenges.WEEKLY.size >= 3)
    }

    @Test fun dailyMenuIsDeterministicPerDayAndRotates() {
        val a = Challenges.dailyMenu(epochDay = 20000)
        val b = Challenges.dailyMenu(epochDay = 20000)
        assertEquals("same day → same menu (a snapshot, not a shuffle)", a, b)
        val c = Challenges.dailyMenu(epochDay = 20001)
        assertNotEquals("different day → menu rotates", a, c)
        assertTrue(a.all { it.cadence == Cadence.DAILY })
        assertEquals(3, a.size)
    }

    @Test fun weeklyChallengeIsStableWithinAWeek() {
        assertEquals(Challenges.weeklyChallenge(20000), Challenges.weeklyChallenge(20003))
        assertEquals(Cadence.WEEKLY, Challenges.weeklyChallenge(20000).cadence)
    }

    @Test fun wagesMatchTemple() {
        assertEquals(Temple.DAILY_WAGE, Challenges.wageFor(Cadence.DAILY))
        assertEquals(Temple.WEEKLY_WAGE, Challenges.wageFor(Cadence.WEEKLY))
    }

    @Test fun allTextCoversEveryChallenge() {
        val text = Challenges.allText().joinToString(" ")
        (Challenges.DAILY + Challenges.WEEKLY).forEach { assertTrue(text.contains(it.title)) }
    }

    @Test fun challengeCompletionRoundTrips() {
        val e = com.ashlarprotocol.data.ChallengeCompletion("lay_one_stone", "DAILY", 20000L, 123L)
        val json = kotlinx.serialization.json.Json.encodeToString(com.ashlarprotocol.data.ChallengeCompletion.serializer(), e)
        val back = kotlinx.serialization.json.Json.decodeFromString(com.ashlarprotocol.data.ChallengeCompletion.serializer(), json)
        assertEquals(e, back)
    }
}
