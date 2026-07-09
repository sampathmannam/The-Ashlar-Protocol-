package com.ashlarprotocol

import com.ashlarprotocol.tools.PracticeReminder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Cue-anchored reminders (SPEC T1.5). Preset times, delay-until-next, and — the hard AC — copy that
 * is never loss/guilt/FOMO framed.
 */
class PracticeReminderTest {

    @Test
    fun slotsAreWellFormedTimesOfDay() {
        assertTrue(PracticeReminder.SLOTS.size >= 3)
        PracticeReminder.SLOTS.forEach {
            assertTrue(it.label.isNotBlank())
            assertTrue(it.minutesOfDay in 0 until 1440)
        }
        assertEquals("Evening", PracticeReminder.slotLabel(19 * 60))
        assertEquals("No reminder", PracticeReminder.slotLabel(null))
    }

    @Test
    fun delayIsLaterTodayOrTomorrow_neverInstantOrNegative() {
        // Target ahead of now → later today.
        assertEquals(60L, PracticeReminder.initialDelayMinutes(targetMinuteOfDay = 13 * 60, nowMinuteOfDay = 12 * 60))
        // Target already passed → same time tomorrow.
        assertEquals((1440L - 60L), PracticeReminder.initialDelayMinutes(targetMinuteOfDay = 9 * 60, nowMinuteOfDay = 10 * 60))
        // Exactly now → tomorrow, never 0.
        assertEquals(1440L, PracticeReminder.initialDelayMinutes(targetMinuteOfDay = 600, nowMinuteOfDay = 600))
    }

    @Test
    fun reminderCopyOffersThePracticeAndPermissionToSkip_neverLossFramed() {
        val title = PracticeReminder.reminderTitle("I pour my coffee")
        val body = PracticeReminder.reminderBody("write one line")
        assertTrue(title.contains("After"))
        assertTrue(body.contains("write one line"))
        assertTrue("must offer a way out", body.lowercase().contains("skip"))
        val all = (title + " " + body).lowercase()
        assertFalse(
            "reminder copy must never use loss/FOMO/streak framing",
            all.contains("streak") || all.contains("don't lose") || all.contains("at risk") ||
                all.contains("behind") || all.contains("keep it going") || all.contains("miss")
        )
    }
}
