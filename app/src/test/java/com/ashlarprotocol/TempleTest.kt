package com.ashlarprotocol

import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Temple
import org.junit.Assert.*
import org.junit.Test

class TempleTest {
    @Test fun fellowcraftTrancheIsRealAndCited() {
        val fc = Temple.COURSES.filter { it.degree == Degree.FELLOWCRAFT }
        assertTrue("the Fellowcraft tranche is authored", fc.size >= 15)
        // The ladder now spans two degrees, and every FC course is a real, cited practice.
        assertTrue(Temple.COURSES.any { it.degree == Degree.ENTERED_APPRENTICE })
        fc.forEach {
            assertTrue("named: ${it.index}", it.name.isNotBlank())
            assertTrue("unlocks: ${it.index}", it.unlocks.isNotBlank())
            assertTrue("cited: ${it.index}", it.basis.isNotBlank())
            assertTrue("costs more than the opening Apprentice courses: ${it.index}", it.cost >= 8)
        }
        // Indices stay contiguous 1..N across the join.
        Temple.COURSES.forEachIndexed { i, c -> assertEquals(i + 1, c.index) }
    }

    @Test fun masterMasonTrancheCompletesTheFiftyCourseJourney() {
        assertEquals("all 50 courses authored", Temple.PLANNED_COURSES, Temple.COURSES.size)
        val mm = Temple.COURSES.filter { it.degree == Degree.MASTER_MASON }
        assertTrue("Master Mason tranche authored", mm.size >= 15)
        // The ladder spans all three degrees.
        assertTrue(Temple.COURSES.any { it.degree == Degree.ENTERED_APPRENTICE })
        assertTrue(Temple.COURSES.any { it.degree == Degree.FELLOWCRAFT })
        assertTrue(Temple.COURSES.any { it.degree == Degree.MASTER_MASON })
        // Course 50 is the summit; nothing lies past it.
        assertEquals(50, Temple.courseAt(50)!!.index)
        assertNull(Temple.nextCourse(Temple.COURSES.size))
        mm.forEach { assertTrue("cited: ${it.index}", it.basis.isNotBlank()); assertTrue(it.cost >= 13) }
    }

    @Test fun apprenticeTrancheIsRealAndSequential() {
        val courses = Temple.COURSES
        assertTrue("MVP authors at least the Apprentice tranche", courses.size >= 10)
        courses.forEachIndexed { i, c ->
            assertEquals(i + 1, c.index)
            assertTrue("course ${c.index} names a craft", c.name.isNotBlank())
            assertTrue("course ${c.index} unlocks something real", c.unlocks.isNotBlank())
            assertTrue("course ${c.index} cites a basis", c.basis.isNotBlank())
            assertTrue("course ${c.index} costs wages", c.cost > 0)
        }
    }

    @Test fun firstCoursesAreApprenticeDegree() {
        assertEquals(Degree.ENTERED_APPRENTICE, Temple.courseAt(1)!!.degree)
    }

    @Test fun cumulativeCostAndBalance() {
        val c1 = Temple.courseAt(1)!!.cost
        val c2 = Temple.courseAt(2)!!.cost
        assertEquals(0, Temple.cumulativeCost(0))
        assertEquals(c1, Temple.cumulativeCost(1))
        assertEquals(c1 + c2, Temple.cumulativeCost(2))
        assertEquals(100 - (c1 + c2), Temple.balance(totalEarned = 100, coursesRaised = 2))
    }

    @Test fun canRaiseNextOnlyWhenAffordable() {
        val cost1 = Temple.courseAt(1)!!.cost
        assertFalse(Temple.canRaiseNext(totalEarned = cost1 - 1, coursesRaised = 0))
        assertTrue(Temple.canRaiseNext(totalEarned = cost1, coursesRaised = 0))
    }

    @Test fun nextCourseWalksThenStops() {
        assertEquals(1, Temple.nextCourse(0)!!.index)
        assertEquals(2, Temple.nextCourse(1)!!.index)
        assertNull("no next course past the authored tranche", Temple.nextCourse(Temple.COURSES.size))
    }

    @Test fun balanceNeverNegativeGuard() {
        assertEquals(0, Temple.balance(totalEarned = 0, coursesRaised = 5))
    }

    @Test fun allTextCoversEveryCourse() {
        val text = Temple.allText().joinToString(" ")
        Temple.COURSES.forEach { assertTrue(text.contains(it.name)) }
    }
}
