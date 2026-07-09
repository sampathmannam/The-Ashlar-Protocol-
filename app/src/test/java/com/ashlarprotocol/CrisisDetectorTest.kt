package com.ashlarprotocol

import com.ashlarprotocol.safety.CrisisDetector
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Locks in the fail-safe behavior of the on-device crisis detector (docs/MASTER_PLAN Phase 1A).
 * Recall on genuine crisis language is the priority; a handful of false positives is acceptable
 * because the cost is only a dismissible help card.
 */
class CrisisDetectorTest {

    @Test
    fun detectsDirectSuicidalIntent() {
        assertTrue(CrisisDetector.detect("I want to kill myself"))
        assertTrue(CrisisDetector.detect("i just want to die"))
        assertTrue(CrisisDetector.detect("thinking about how to end my life"))
        assertTrue(CrisisDetector.detect("I feel suicidal today"))
    }

    @Test
    fun detectsEuphemisticIdeation() {
        // The cases keyword-only nets miss — no literal "suicide"/"kill".
        assertTrue(CrisisDetector.detect("honestly I don't want to be here anymore"))
        assertTrue(CrisisDetector.detect("everyone would be better without me"))
        assertTrue(CrisisDetector.detect("the world would be lighter without me"))
        assertTrue(CrisisDetector.detect("there's just no reason to live"))
        assertTrue(CrisisDetector.detect("I can't go on like this"))
    }

    @Test
    fun isCaseAndPunctuationInsensitive() {
        assertTrue(CrisisDetector.detect("I WANT TO DIE."))
        assertTrue(CrisisDetector.detect("i...  want    to   die"))
        assertTrue(CrisisDetector.detect("Better off dead, honestly"))
    }

    @Test
    fun doesNotFireOnBenignReflection() {
        assertFalse(CrisisDetector.detect("Had a great session at the gym today."))
        assertFalse(CrisisDetector.detect("Work was stressful but I handled it well."))
        assertFalse(CrisisDetector.detect("Practiced guitar and felt calm afterward."))
        assertFalse(CrisisDetector.detect(""))
        assertFalse(CrisisDetector.detect(null))
    }
}
