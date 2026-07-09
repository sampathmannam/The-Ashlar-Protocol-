package com.ashlarprotocol

import com.ashlarprotocol.tools.GracefulExit
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The graceful exit (SPEC P0.7 / T2.5). Enforces the plan's ⚠️ copy-review gate in code: the
 * "enough for today" copy must never use loss / guilt / FOMO / streak-threat framing.
 */
class GracefulExitTest {

    @Test
    fun linesAreWellFormed() {
        assertTrue(GracefulExit.LINES.isNotEmpty())
        GracefulExit.LINES.forEach { assertTrue(it.isNotBlank()) }
    }

    @Test
    fun copyNeverUsesFomoOrLossFraming() {
        // The whole point of a graceful exit is the absence of dark patterns (SPEC P0.7 / cross-cutting
        // copy-review gate). Guard against the usual offenders creeping into the copy later.
        val forbidden = listOf(
            "don't lose", "streak", "at risk", "falling behind", "behind", "miss out",
            "hurry", "before it's gone", "keep it going", "you'll lose", "broken"
        )
        val text = GracefulExit.LINES.joinToString(" ").lowercase()
        forbidden.forEach { bad ->
            assertFalse("graceful-exit copy must not contain FOMO/loss framing: \"$bad\"", text.contains(bad))
        }
    }

    @Test
    fun copyGivesPermissionToRest() {
        val text = GracefulExit.LINES.joinToString(" ").lowercase()
        assertTrue(text.contains("enough") || text.contains("rest"))
    }
}
