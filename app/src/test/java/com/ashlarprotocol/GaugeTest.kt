package com.ashlarprotocol

import com.ashlarprotocol.tools.DayPart
import com.ashlarprotocol.tools.Gauge
import com.ashlarprotocol.tools.GaugeItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The 24-inch Gauge — pure logic for dividing the day into its three parts and tracking that the
 * planned activity actually happens. Grounds the Masonic "divide your day" into Behavioral
 * Activation (schedule meaningful/mastery/restful activity, then do it). See docs/RESEARCH_BASIS.md.
 */
class GaugeTest {

    private fun item(part: DayPart, text: String, done: Boolean = false) =
        GaugeItem(id = "$part-$text", part = part, text = text, done = done)

    @Test
    fun threePartsExistWithLabels() {
        assertEquals(3, DayPart.values().size)
        DayPart.values().forEach {
            assertTrue(it.display.isNotBlank())
            assertTrue(it.intent.isNotBlank())
        }
    }

    @Test
    fun completionIsZeroForEmptyGauge() {
        assertEquals(0f, Gauge.completion(emptyList()), 0.0001f)
    }

    @Test
    fun completionIsDoneOverTotal() {
        val items = listOf(
            item(DayPart.WORK, "ship the report", done = true),
            item(DayPart.REST, "walk", done = true),
            item(DayPart.SERVICE, "call dad", done = false),
            item(DayPart.WORK, "inbox", done = false)
        )
        assertEquals(0.5f, Gauge.completion(items), 0.0001f)
    }

    @Test
    fun missingPartsListsPartsWithNoItems() {
        assertEquals(DayPart.values().toList(), Gauge.missingParts(emptyList()))
        val onlyWork = listOf(item(DayPart.WORK, "labour"))
        assertEquals(listOf(DayPart.SERVICE, DayPart.REST), Gauge.missingParts(onlyWork))
    }

    @Test
    fun balanceMessageNudgesTowardMeaningAndRest() {
        val empty = Gauge.balanceMessage(emptyList()).lowercase()
        assertTrue(empty.contains("divide") || empty.contains("three"))

        val onlyWork = Gauge.balanceMessage(listOf(item(DayPart.WORK, "labour"))).lowercase()
        // With only work planned, it should nudge toward something that matters and toward rest.
        assertTrue(onlyWork.contains("service") || onlyWork.contains("matter") || onlyWork.contains("rest"))
    }

    @Test
    fun implementationIntentionFormsAnIfThenPlan() {
        assertEquals(
            "If I finish dinner, then I will walk ten minutes",
            Gauge.implementationIntention("I finish dinner", "walk ten minutes")
        )
    }

    @Test
    fun implementationIntentionFallsBackToActionWhenNoCue() {
        assertEquals("Call a friend", Gauge.implementationIntention("", "call a friend"))
        assertEquals("Call a friend", Gauge.implementationIntention("   ", "  call a friend "))
    }

    @Test
    fun implementationIntentionIsBlankWithNoAction() {
        assertEquals("", Gauge.implementationIntention("after lunch", ""))
    }

    @Test
    fun isDayCompleteOnlyWhenAllThreePartsPresentAndEveryItemDone() {
        assertTrue("empty day is not complete", !Gauge.isDayComplete(emptyList()))

        val allPartsNotAllDone = listOf(
            item(DayPart.SERVICE, "call dad", done = true),
            item(DayPart.WORK, "labour", done = false),
            item(DayPart.REST, "sleep", done = true)
        )
        assertTrue("undone item -> not complete", !Gauge.isDayComplete(allPartsNotAllDone))

        val twoPartsAllDone = listOf(
            item(DayPart.WORK, "labour", done = true),
            item(DayPart.REST, "sleep", done = true)
        )
        assertTrue("missing a part -> not complete", !Gauge.isDayComplete(twoPartsAllDone))

        val allPartsAllDone = listOf(
            item(DayPart.SERVICE, "call dad", done = true),
            item(DayPart.WORK, "labour", done = true),
            item(DayPart.REST, "sleep", done = true)
        )
        assertTrue("all three parts, all done -> complete", Gauge.isDayComplete(allPartsAllDone))
    }

    @Test
    fun balanceMessageAffirmsWhenAllThreePresent() {
        val full = listOf(
            item(DayPart.SERVICE, "call dad"),
            item(DayPart.WORK, "labour"),
            item(DayPart.REST, "sleep")
        )
        val msg = Gauge.balanceMessage(full).lowercase()
        assertTrue(msg.contains("squared") || msg.contains("all three") || msg.contains("balanced"))
    }
}
