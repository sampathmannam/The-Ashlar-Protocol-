package com.ashlarprotocol

import com.ashlarprotocol.tools.WestGate
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The West Gate — pure logic for the "turn outward" doorways (Phase 3, the Lodge). Grounds social
 * connection as the strongest protective factor (Holt-Lunstad; RESEARCH_BASIS §11). These guarantees
 * are what the composable relies on when it hands off to the phone's own apps — a bad url or a
 * "tappable" PLACE would be a broken door.
 */
class WestGateTest {

    @Test fun offersSeveralDoorways() {
        assertTrue(WestGate.DOORWAYS.size >= 4)
    }

    @Test fun everyWebDoorwayHasHttpsUrlAndAction() {
        WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.WEB }.forEach {
            assertNotNull("WEB doorway '${it.title}' needs a url", it.url)
            assertTrue("WEB url must be https: ${it.url}", it.url!!.startsWith("https://"))
            assertNotNull("WEB doorway '${it.title}' needs a tappable action", it.action)
        }
    }

    @Test fun ownPeopleDoorwayHasActionNoUrl() {
        val own = WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.OWN_PEOPLE }
        assertTrue("there must be a way to reach your own people", own.isNotEmpty())
        own.forEach { assertNotNull(it.action); assertNull(it.url) }
    }

    @Test fun placeDoorwayIsAnInvitationNotAnAppAction() {
        // A real-world room is something you walk to; the app has nothing to open for it.
        WestGate.DOORWAYS.filter { it.kind == WestGate.Kind.PLACE }.forEach {
            assertNull(it.url); assertNull(it.action)
        }
    }

    @Test fun noDoorwayIsEmpty() {
        WestGate.DOORWAYS.forEach {
            assertTrue("blank title", it.title.isNotBlank())
            assertTrue("blank body for '${it.title}'", it.body.isNotBlank())
        }
    }

    @Test fun allTextGathersEveryVisibleString() {
        val text = WestGate.allText()
        WestGate.DOORWAYS.forEach {
            assertTrue(text.contains(it.title))
            assertTrue(text.contains(it.body))
        }
    }
}
