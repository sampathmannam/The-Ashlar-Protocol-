# The Temple (MVP slice) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship the first slice of "The Temple": research-designed daily/weekly challenges pay *wages* (corn, wine, oil) that you *lay* to raise the courses of your Temple (an Apprentice-degree tranche of a 50-course curriculum), on-device and forgiving.

**Architecture:** Pure Kotlin logic (`tools/Temple.kt`, `tools/Challenges.kt`) holds the course ladder, wage rules, and challenge catalog — deterministic and unit-tested. A `data/` model + `LocalDataStore` keys persist monotonic `totalWagesEarned` + `coursesRaised` + an append-only challenge-completion ledger. The ViewModel derives the wage *balance* and exposes forgiving `completeChallenge`/`raiseCourse` actions. Two Compose cards (Challenges, Temple) sit on the Board, mirroring the existing card pattern.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), DataStore Preferences, kotlinx.serialization, JUnit. Build: Android Studio JBR (JDK 21) + cached gradle-9.4.1-bin; `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon --no-configuration-cache`. Device: `adb -s ZD2232FCR5`, package `com.ashlarprotocol`.

## Global Constraints

- Fully **on-device**; no INTERNET permission, no backend, no accounts, no real money, zero-cost.
- **Wages are deterministic** — fixed amounts, never random. No coins economy beyond this.
- **Append-only / never-punitive:** `totalWagesEarned` and `coursesRaised` only ever increase; a missed challenge takes nothing; no streak-loss, no decay, no scarcity block on core function.
- **Core tools + §9 crisis are NEVER gated** behind wages or courses. A raised "course" teaches/affirms a practice; it never locks one away.
- **Challenges are invitations** — no red badges, no shame copy, no penalty for skipping.
- **Quiet numbers:** the Temple rising is the display; the wage counter is understated.
- Every new user-facing string is added to `SafetyAuditTest.copyCorpus()` (passes the mortality-symbolism AND willpower/grit language gates).
- Snapshot/action logic reads the **DataStore source of truth** (`dataStore.X.first()`), never cold `WhileSubscribed` StateFlow `.value` (per PR #32).
- Copy holds the app's altitude: no "willpower/grit/hustle/no excuses/earn your worth"; wages are the *fruit of work*, never a moral scoreboard.

---

## File Structure

- Create `app/src/main/java/com/ashlarprotocol/tools/Temple.kt` — course ladder + wage rules (pure).
- Create `app/src/main/java/com/ashlarprotocol/tools/Challenges.kt` — challenge catalog + cadence selection (pure).
- Create `app/src/main/java/com/ashlarprotocol/data/ChallengeCompletion.kt` — `@Serializable` completion record.
- Modify `app/src/main/java/com/ashlarprotocol/data/LocalDataStore.kt` — wage/course/completion keys, flows, setters.
- Modify `app/src/main/java/com/ashlarprotocol/ui/AshlarAppViewModel.kt` — StateFlows + `wageBalance` + `completeChallenge` + `raiseCourse`.
- Create `app/src/main/java/com/ashlarprotocol/ui/components/Temple.kt` — `TempleCard`.
- Create `app/src/main/java/com/ashlarprotocol/ui/components/Challenges.kt` — `ChallengesCard`.
- Modify `app/src/main/java/com/ashlarprotocol/ui/screens/BoardScreen.kt` — two new items.
- Create `app/src/test/java/com/ashlarprotocol/TempleTest.kt`, `app/src/test/java/com/ashlarprotocol/ChallengesTest.kt`.
- Modify `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt` — corpus additions.

---

### Task 1: `tools/Temple.kt` — course ladder + wage rules (pure)

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/Temple.kt`
- Test: `app/src/test/java/com/ashlarprotocol/TempleTest.kt`

**Interfaces:**
- Consumes: `com.ashlarprotocol.tools.Degree` (EA/FC/MM).
- Produces: `data class Course(index:Int, name:String, degree:Degree, cost:Int, unlocks:String, basis:String)`; `object Temple { val COURSES:List<Course>; const val DAILY_WAGE=1; const val WEEKLY_WAGE=3; fun courseAt(i:Int):Course?; fun cumulativeCost(coursesRaised:Int):Int; fun balance(totalEarned:Int, coursesRaised:Int):Int; fun nextCourse(coursesRaised:Int):Course?; fun canRaiseNext(totalEarned:Int, coursesRaised:Int):Boolean; fun allText():List<String> }`.

- [ ] **Step 1: Write the failing test** — `TempleTest.kt`:

```kotlin
package com.ashlarprotocol

import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Temple
import org.junit.Assert.*
import org.junit.Test

class TempleTest {
    @Test fun apprenticeTrancheIsRealAndSequential() {
        val courses = Temple.COURSES
        assertTrue("MVP authors at least the Apprentice tranche", courses.size >= 10)
        // indices are 1..N, contiguous, each with real content and a research basis
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
        // earned 100, raised 2 → balance = 100 - (c1+c2)
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
        // Even if data is inconsistent, balance floors at 0 (never a scarcity debt).
        assertEquals(0, Temple.balance(totalEarned = 0, coursesRaised = 5))
    }

    @Test fun allTextCoversEveryCourse() {
        val text = Temple.allText().joinToString(" ")
        Temple.COURSES.forEach { assertTrue(text.contains(it.name)) }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.TempleTest" --no-daemon --no-configuration-cache`
Expected: FAIL — unresolved reference `Temple`.

- [ ] **Step 3: Write minimal implementation** — `Temple.kt`:

```kotlin
package com.ashlarprotocol.tools

/**
 * The Temple — the long-arc progression. You earn WAGES (corn, wine, oil) by doing the challenges and
 * tending the stone, and LAY them to raise the COURSES of your Temple. A "course" is both a layer of
 * stone and a unit of study: raising one affirms a real, research-grounded practice. Spending is
 * building — wages laid become permanent stone, spent but never lost. Deterministic and pure.
 *
 * MVP authors the Apprentice tranche of a planned 50-course curriculum (docs/superpowers/specs/
 * 2026-07-10-the-temple-design.md). Guardrail: raising a course NEVER gates a tool — the practices
 * stay open; the course is the teaching, not a lock.
 */
data class Course(
    val index: Int,
    val name: String,
    val degree: Degree,
    val cost: Int,
    val unlocks: String,
    val basis: String
)

object Temple {
    /** Deterministic wages — the fruit of work, never random. Costs are tuned so a course is ~a few
     *  days to a week of genuine engagement, never a grind. */
    const val DAILY_WAGE = 1
    const val WEEKLY_WAGE = 3

    val COURSES: List<Course> = listOf(
        Course(1, "The First Stone", Degree.ENTERED_APPRENTICE, 3,
            "Showing up once — the day is tended.", "Behavioral activation (Ekers 2014, SMD −0.74)"),
        Course(2, "The Level", Degree.ENTERED_APPRENTICE, 3,
            "A line of self-kindness on a hard day.", "Self-compassion (Neff 2003)"),
        Course(3, "The Common Gavel", Degree.ENTERED_APPRENTICE, 4,
            "Catching one rough corner with an if-then.", "Implementation intentions (Gollwitzer & Sheeran 2006, d=0.65)"),
        Course(4, "The Twenty-Four-Inch Gauge", Degree.ENTERED_APPRENTICE, 4,
            "Dividing the day into balanced parts.", "Activity scheduling (Cuijpers 2007, d=0.87)"),
        Course(5, "The Cornerstone", Degree.ENTERED_APPRENTICE, 5,
            "One change to your surroundings that makes the good choice easier.", "Environment/habit design (Wood & Neal 2016)"),
        Course(6, "The Chalk Line", Degree.ENTERED_APPRENTICE, 5,
            "Setting one if-then Practice against a known cue.", "Implementation intentions (Gollwitzer & Sheeran 2006)"),
        Course(7, "The Plumb", Degree.ENTERED_APPRENTICE, 6,
            "Straightening one leaning thought.", "Cognitive restructuring (Beck)"),
        Course(8, "The Tracing Board", Degree.ENTERED_APPRENTICE, 6,
            "Naming what you are working toward.", "Values & autonomy (Ryan & Deci, SDT)"),
        Course(9, "The West Gate", Degree.ENTERED_APPRENTICE, 6,
            "Turning toward one person you trust.", "Social connection (Holt-Lunstad 2010, OR 1.5)"),
        Course(10, "The Rough Ashlar", Degree.ENTERED_APPRENTICE, 7,
            "Naming one rough edge honestly, without shame.", "Anti-AVE relapse prevention (Marlatt & Gordon)"),
        Course(11, "The Working Tools", Degree.ENTERED_APPRENTICE, 7,
            "A slow, paced breath as a daily anchor.", "Paced breathing / arousal down-regulation"),
        Course(12, "The Middle Chamber", Degree.ENTERED_APPRENTICE, 8,
            "Pausing to see the work already done.", "Progress principle (Amabile & Kramer 2011)")
    )

    fun courseAt(index: Int): Course? = COURSES.getOrNull(index - 1)

    fun cumulativeCost(coursesRaised: Int): Int =
        COURSES.take(coursesRaised.coerceIn(0, COURSES.size)).sumOf { it.cost }

    fun balance(totalEarned: Int, coursesRaised: Int): Int =
        (totalEarned - cumulativeCost(coursesRaised)).coerceAtLeast(0)

    fun nextCourse(coursesRaised: Int): Course? = COURSES.getOrNull(coursesRaised)

    fun canRaiseNext(totalEarned: Int, coursesRaised: Int): Boolean {
        val next = nextCourse(coursesRaised) ?: return false
        return balance(totalEarned, coursesRaised) >= next.cost
    }

    fun allText(): List<String> = COURSES.flatMap { listOf(it.name, it.unlocks) }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.TempleTest" --no-daemon --no-configuration-cache`
Expected: PASS (7 tests).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/tools/Temple.kt app/src/test/java/com/ashlarprotocol/TempleTest.kt
git commit -m "feat(temple): the course ladder + wage rules (Apprentice tranche, pure)"
```

---

### Task 2: `tools/Challenges.kt` — challenge catalog + cadence selection (pure)

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/tools/Challenges.kt`
- Test: `app/src/test/java/com/ashlarprotocol/ChallengesTest.kt`

**Interfaces:**
- Produces: `enum class Cadence { DAILY, WEEKLY }`; `data class Challenge(id:String, title:String, invite:String, cadence:Cadence, basis:String)`; `object Challenges { val DAILY:List<Challenge>; val WEEKLY:List<Challenge>; fun dailyMenu(epochDay:Long, size:Int=3):List<Challenge>; fun weeklyChallenge(epochDay:Long):Challenge; fun wageFor(cadence:Cadence):Int; fun allText():List<String> }`.

- [ ] **Step 1: Write the failing test** — `ChallengesTest.kt`:

```kotlin
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
        // epoch day 20000 and 20003 fall in the same 7-day block → same weekly challenge
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
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.ChallengesTest" --no-daemon --no-configuration-cache`
Expected: FAIL — unresolved reference `Challenges`.

- [ ] **Step 3: Write minimal implementation** — `Challenges.kt`:

```kotlin
package com.ashlarprotocol.tools

/**
 * Challenges — the research-designed tasks that pay wages. DAILY is a small, optional menu of
 * behavioral-activation micro-actions (an invitation, never a must-do); WEEKLY is the value-aligned
 * spine. Fixed cue, rotating content: the menu rotates deterministically by day so it stays fresh
 * without random reward. Missing a challenge takes nothing. Pure — no Android, no I/O.
 */
enum class Cadence { DAILY, WEEKLY }

data class Challenge(
    val id: String,
    val title: String,
    val invite: String,
    val cadence: Cadence,
    val basis: String
)

object Challenges {
    val DAILY: List<Challenge> = listOf(
        Challenge("lay_one_stone", "Lay one stone", "One small thing that matters. That's the day tended.",
            Cadence.DAILY, "Behavioral activation (Ekers 2014)"),
        Challenge("slow_breath", "A slow breath", "A minute, out-breath leading. That's enough.",
            Cadence.DAILY, "Paced breathing / vagal tone"),
        Challenge("line_of_thanks", "A line of thanks", "Tell one person, in a sentence, what you're grateful for.",
            Cadence.DAILY, "Gratitude expression (Seligman 2005)"),
        Challenge("square_a_corner", "Square one corner", "Catch one rough moment with an if-then.",
            Cadence.DAILY, "Implementation intentions (Gollwitzer 2006)"),
        Challenge("name_the_load", "Name the load", "A ten-second check: how heavy is today?",
            Cadence.DAILY, "Self-monitoring / behavioral activation")
    )

    val WEEKLY: List<Challenge> = listOf(
        Challenge("set_cornerstone", "Set a cornerstone", "One change to your surroundings this week that makes the good choice easier.",
            Cadence.WEEKLY, "Environment/habit design (Wood & Neal 2016)"),
        Challenge("draw_chalk_line", "Draw a chalk line", "Author one if-then Practice this week and run it.",
            Cadence.WEEKLY, "Implementation intentions (Gollwitzer & Sheeran 2006, d=0.65)"),
        Challenge("open_west_gate", "Open the West Gate", "Reach toward one person you trust this week.",
            Cadence.WEEKLY, "Social connection (Holt-Lunstad 2010)"),
        Challenge("divide_the_day", "Divide the day", "Use the Gauge to balance three days this week.",
            Cadence.WEEKLY, "Activity scheduling (Cuijpers 2007)")
    )

    fun wageFor(cadence: Cadence): Int = when (cadence) {
        Cadence.DAILY -> Temple.DAILY_WAGE
        Cadence.WEEKLY -> Temple.WEEKLY_WAGE
    }

    /** A stable per-day menu: a deterministic rotating window over the DAILY catalog (no randomness). */
    fun dailyMenu(epochDay: Long, size: Int = 3): List<Challenge> {
        if (DAILY.isEmpty()) return emptyList()
        val n = size.coerceAtMost(DAILY.size)
        val start = (((epochDay % DAILY.size) + DAILY.size) % DAILY.size).toInt()
        return (0 until n).map { DAILY[(start + it) % DAILY.size] }
    }

    /** One weekly challenge, stable across a 7-day block, rotating week to week. */
    fun weeklyChallenge(epochDay: Long): Challenge {
        val week = Math.floorDiv(epochDay, 7L)
        val idx = (((week % WEEKLY.size) + WEEKLY.size) % WEEKLY.size).toInt()
        return WEEKLY[idx]
    }

    fun allText(): List<String> = (DAILY + WEEKLY).flatMap { listOf(it.title, it.invite) }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.ChallengesTest" --no-daemon --no-configuration-cache`
Expected: PASS (5 tests).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/tools/Challenges.kt app/src/test/java/com/ashlarprotocol/ChallengesTest.kt
git commit -m "feat(temple): the challenge catalog + deterministic cadence selection (pure)"
```

---

### Task 3: `data/ChallengeCompletion.kt` + serialization round-trip

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/data/ChallengeCompletion.kt`
- Test: add to `app/src/test/java/com/ashlarprotocol/ChallengesTest.kt` (or a small new test).

**Interfaces:**
- Produces: `@Serializable data class ChallengeCompletion(challengeId:String, cadence:String, periodKey:Long, timestamp:Long)`. `periodKey` = epochDay for DAILY, epochWeek for WEEKLY — the idempotency key.

- [ ] **Step 1: Write the failing test** — append to `ChallengesTest.kt`:

```kotlin
    @Test fun challengeCompletionRoundTrips() {
        val e = com.ashlarprotocol.data.ChallengeCompletion("lay_one_stone", "DAILY", 20000L, 123L)
        val json = kotlinx.serialization.json.Json.encodeToString(com.ashlarprotocol.data.ChallengeCompletion.serializer(), e)
        val back = kotlinx.serialization.json.Json.decodeFromString(com.ashlarprotocol.data.ChallengeCompletion.serializer(), json)
        assertEquals(e, back)
    }
```

- [ ] **Step 2: Run to verify it fails**

Run: `gradle :app:testDebugUnitTest --tests "com.ashlarprotocol.ChallengesTest" --no-daemon --no-configuration-cache`
Expected: FAIL — unresolved reference `ChallengeCompletion`.

- [ ] **Step 3: Write implementation** — `ChallengeCompletion.kt`:

```kotlin
package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

/**
 * A record that a challenge was completed in a given period. Append-only — the ledger only grows;
 * a missed challenge writes nothing and takes nothing. [periodKey] is the epoch-day (DAILY) or
 * epoch-week (WEEKLY) so a challenge pays wages at most once per period (idempotent, un-farmable).
 */
@Serializable
data class ChallengeCompletion(
    val challengeId: String,
    val cadence: String,
    val periodKey: Long,
    val timestamp: Long
)
```

- [ ] **Step 4: Run to verify it passes** — Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/data/ChallengeCompletion.kt app/src/test/java/com/ashlarprotocol/ChallengesTest.kt
git commit -m "feat(temple): append-only ChallengeCompletion record"
```

---

### Task 4: `LocalDataStore` — wage/course/completion persistence

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/data/LocalDataStore.kt`

**Interfaces:**
- Produces (on `dataStore`): `val totalWagesEarned: Flow<Int>`; `val coursesRaised: Flow<Int>`; `val challengeCompletions: Flow<List<ChallengeCompletion>>`; `suspend fun addWages(n: Int)`; `suspend fun setCoursesRaised(n: Int)`; `suspend fun setChallengeCompletions(list: List<ChallengeCompletion>)`.

- [ ] **Step 1: Add keys** — near the other keys (follow the existing `intPreferencesKey`/`stringPreferencesKey` block):

```kotlin
    private val TOTAL_WAGES_KEY = androidx.datastore.preferences.core.intPreferencesKey("total_wages_earned")
    private val COURSES_RAISED_KEY = androidx.datastore.preferences.core.intPreferencesKey("courses_raised")
    private val CHALLENGE_COMPLETIONS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("challenge_completions")
```

- [ ] **Step 2: Add flows** — near the other `val x: Flow<...>` declarations:

```kotlin
    // The Temple (progression): wages earned and courses raised only ever increase (never-punitive).
    val totalWagesEarned: Flow<Int> = context.dataStore.data.map { it[TOTAL_WAGES_KEY] ?: 0 }
    val coursesRaised: Flow<Int> = context.dataStore.data.map { it[COURSES_RAISED_KEY] ?: 0 }
    val challengeCompletions: Flow<List<com.ashlarprotocol.data.ChallengeCompletion>> =
        context.dataStore.data.map { prefs ->
            prefs[CHALLENGE_COMPLETIONS_KEY]?.takeIf { it.isNotBlank() }?.let {
                try {
                    kotlinx.serialization.json.Json.decodeFromString(
                        kotlinx.serialization.builtins.ListSerializer(com.ashlarprotocol.data.ChallengeCompletion.serializer()), it
                    )
                } catch (e: Exception) { emptyList() }
            } ?: emptyList()
        }
```

- [ ] **Step 3: Add setters** — near the other `suspend fun set...`:

```kotlin
    suspend fun addWages(n: Int) {
        if (n <= 0) return
        context.dataStore.edit { it[TOTAL_WAGES_KEY] = (it[TOTAL_WAGES_KEY] ?: 0) + n }
    }

    suspend fun setCoursesRaised(n: Int) {
        context.dataStore.edit { it[COURSES_RAISED_KEY] = n }
    }

    suspend fun setChallengeCompletions(list: List<com.ashlarprotocol.data.ChallengeCompletion>) {
        context.dataStore.edit {
            it[CHALLENGE_COMPLETIONS_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(com.ashlarprotocol.data.ChallengeCompletion.serializer()), list
            )
        }
    }
```

- [ ] **Step 4: Compile-check**

Run: `gradle :app:compileDebugKotlin --no-daemon --no-configuration-cache`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/data/LocalDataStore.kt
git commit -m "feat(temple): persist wages, courses raised, and the completion ledger"
```

---

### Task 5: ViewModel — StateFlows + `wageBalance` + `completeChallenge` + `raiseCourse`

**Files:**
- Modify: `app/src/main/java/com/ashlarprotocol/ui/AshlarAppViewModel.kt`

**Interfaces:**
- Consumes: `dataStore.totalWagesEarned/coursesRaised/challengeCompletions`, `dataStore.addWages/setCoursesRaised/setChallengeCompletions`, `Temple`, `Challenges`, `Cadence`, `ChallengeCompletion`, `KindStreak.epochDay`.
- Produces: `val totalWagesEarned: StateFlow<Int>`; `val coursesRaised: StateFlow<Int>`; `val wageBalance: StateFlow<Int>`; `val challengeCompletions: StateFlow<List<ChallengeCompletion>>`; `fun completeChallenge(challenge: Challenge)`; `fun raiseCourse()`; helper `fun isChallengeDone(challenge, completions, today): Boolean`.

- [ ] **Step 1: Add StateFlows + derived balance** — near the other flows (use `combine` for balance):

```kotlin
    val totalWagesEarned: StateFlow<Int> = dataStore.totalWagesEarned
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val coursesRaised: StateFlow<Int> = dataStore.coursesRaised
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val challengeCompletions: StateFlow<List<com.ashlarprotocol.data.ChallengeCompletion>> =
        dataStore.challengeCompletions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val wageBalance: StateFlow<Int> =
        kotlinx.coroutines.flow.combine(dataStore.totalWagesEarned, dataStore.coursesRaised) { earned, raised ->
            com.ashlarprotocol.tools.Temple.balance(earned, raised)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
```

- [ ] **Step 2: Add the actions** — read the DataStore source of truth (never cold `.value`); idempotent per period; forgiving:

```kotlin
    /** The period key a challenge is idempotent within: epoch-day for DAILY, epoch-week for WEEKLY. */
    private fun periodKey(cadence: com.ashlarprotocol.tools.Cadence, today: Long): Long =
        if (cadence == com.ashlarprotocol.tools.Cadence.WEEKLY) Math.floorDiv(today, 7L) else today

    /** Complete a challenge: pays wages ONCE per period, records it, and tends the stone. A no-op if
     *  already done this period. Missing a challenge is not modelled — it simply never calls this. */
    fun completeChallenge(challenge: com.ashlarprotocol.tools.Challenge) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
            val key = periodKey(challenge.cadence, today)
            val done = dataStore.challengeCompletions.first()
            if (done.any { it.challengeId == challenge.id && it.periodKey == key }) return@launch
            dataStore.setChallengeCompletions(
                done + com.ashlarprotocol.data.ChallengeCompletion(challenge.id, challenge.cadence.name, key, now)
            )
            dataStore.addWages(com.ashlarprotocol.tools.Challenges.wageFor(challenge.cadence))
            tendTheStone()
        }
    }

    /** Lay wages to raise the next course — only if affordable. Reads persisted truth, never punitive. */
    fun raiseCourse() {
        viewModelScope.launch {
            val earned = dataStore.totalWagesEarned.first()
            val raised = dataStore.coursesRaised.first()
            if (com.ashlarprotocol.tools.Temple.canRaiseNext(earned, raised)) {
                dataStore.setCoursesRaised(raised + 1)
            }
        }
    }
```

- [ ] **Step 3: Compile-check**

Run: `gradle :app:compileDebugKotlin --no-daemon --no-configuration-cache`
Expected: BUILD SUCCESSFUL. (`combine`, `first`, `TimeZone`, `KindStreak` already imported/used in this file.)

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/ui/AshlarAppViewModel.kt
git commit -m "feat(temple): VM wages balance + forgiving completeChallenge/raiseCourse"
```

---

### Task 6: UI — `ChallengesCard`, `TempleCard`, Board wiring

**Files:**
- Create: `app/src/main/java/com/ashlarprotocol/ui/components/Challenges.kt`
- Create: `app/src/main/java/com/ashlarprotocol/ui/components/Temple.kt`
- Modify: `app/src/main/java/com/ashlarprotocol/ui/screens/BoardScreen.kt`

**Interfaces:**
- `ChallengesCard(daily: List<Challenge>, weekly: Challenge, doneIds: Set<String>, onComplete: (Challenge) -> Unit)`.
- `TempleCard(coursesRaised: Int, nextCourse: Course?, balance: Int, canRaise: Boolean, onRaise: () -> Unit)`.
- Both mirror the existing card idiom (rounded `Surface` card, `Gold` label, theme colors) used by `StoneRemembersCard` / `DegreePathCard`.

- [ ] **Step 1: `ChallengesCard`** — a header label ("THE DAY'S WORK"), the weekly challenge highlighted, then the daily menu as tappable rows; completed rows show a quiet "✓ tended" (never a penalty for the rest). Follow the `StoneRemembersCard` structure (Column + Surface + Gold label + theme colors `LightText`/`Silver`/`Gold`). Each row: title + invite + a tap target calling `onComplete(challenge)`; if `challenge.id in doneIds`, render it settled (Silver, "✓") and non-tappable. Copy stays invitational.

- [ ] **Step 2: `TempleCard`** — a header ("THE TEMPLE"), a Compose `Canvas` drawing `coursesRaised` stacked courses (simple stone rows rising, `Gold`/`Silver` strokes on `Surface`), the current standing ("Course N of 50 · <degree>"), and — only when `canRaise` — a "Lay the next course — <nextCourse.name>" affordance calling `onRaise()`, plus a quiet balance line ("<balance> wages in hand"). When `!canRaise`, show the next course faint with what it needs, no pressure. Numbers understated; the rising stack is the focus.

- [ ] **Step 3: Wire into `BoardScreen`** — after the degree card / near `StoneRemembersCard`, add two items (collect the flows with `collectAsState`, compute today's menu/weekly with `KindStreak.epochDay`, derive `doneIds` for the current period):

```kotlin
        // The day's work — research-designed challenges that pay wages (The Temple).
        item {
            val completions by viewModel.challengeCompletions.collectAsState()
            val now = remember { System.currentTimeMillis() }
            val today = remember { com.ashlarprotocol.tools.KindStreak.epochDay(now, java.util.TimeZone.getDefault().getOffset(now)) }
            val week = java.lang.Math.floorDiv(today, 7L)
            val daily = remember(today) { com.ashlarprotocol.tools.Challenges.dailyMenu(today) }
            val weekly = remember(week) { com.ashlarprotocol.tools.Challenges.weeklyChallenge(today) }
            val doneIds = completions
                .filter { (it.cadence == "WEEKLY" && it.periodKey == week) || (it.cadence == "DAILY" && it.periodKey == today) }
                .map { it.challengeId }.toSet()
            com.ashlarprotocol.ui.components.ChallengesCard(
                daily = daily, weekly = weekly, doneIds = doneIds,
                onComplete = { viewModel.completeChallenge(it) }
            )
        }
        // The Temple — lay wages to raise the next course.
        item {
            val raised by viewModel.coursesRaised.collectAsState()
            val balance by viewModel.wageBalance.collectAsState()
            val next = com.ashlarprotocol.tools.Temple.nextCourse(raised)
            com.ashlarprotocol.ui.components.TempleCard(
                coursesRaised = raised, nextCourse = next, balance = balance,
                canRaise = next != null && balance >= next.cost,
                onRaise = { viewModel.raiseCourse() }
            )
        }
```

- [ ] **Step 4: Build (compile + assemble)**

Run: `gradle :app:assembleDebug --no-daemon --no-configuration-cache`
Expected: BUILD SUCCESSFUL. (If `FlowRow` is used, add `@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)`.)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/ashlarprotocol/ui/components/Challenges.kt app/src/main/java/com/ashlarprotocol/ui/components/Temple.kt app/src/main/java/com/ashlarprotocol/ui/screens/BoardScreen.kt
git commit -m "feat(temple): Board surfaces — the day's challenges + the rising Temple"
```

---

### Task 7: Safety sweep + full green

**Files:**
- Modify: `app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt`

- [ ] **Step 1: Add the new copy to the corpus** — in `copyCorpus()`, add entries (and imports for `Temple`/`Challenges`):

```kotlin
            "Temple" to Temple.allText(),
            "Challenges" to Challenges.allText(),
```
Also add any static card labels used in the UI (e.g. `"THE DAY'S WORK"`, `"THE TEMPLE"`, `"wages in hand"`, `"tended"`) as a literal list entry, mirroring how RoughEdge/StoneRemembers static strings were added.

- [ ] **Step 2: Run the FULL suite + assemble**

Run: `gradle :app:testDebugUnitTest :app:assembleDebug --no-daemon --no-configuration-cache`
Expected: BUILD SUCCESSFUL; both `everyCopySurfaceIsFreeOfMortalitySymbolism` and the language-gate test pass over the new copy. Confirm total test count rose by the new tests and failures=0 (`grep -rhoE 'failures="[0-9]+" errors="[0-9]+"' app/build/test-results/testDebugUnitTest/*.xml | sort -u`).

- [ ] **Step 3: Commit**

```bash
git add app/src/test/java/com/ashlarprotocol/SafetyAuditTest.kt
git commit -m "test(temple): sweep all Temple + challenge copy through the safety gates"
```

---

### Task 8: Device verification + finish

- [ ] **Step 1: Install + launch**

```bash
adb -s ZD2232FCR5 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s ZD2232FCR5 shell am force-stop com.ashlarprotocol
adb -s ZD2232FCR5 shell am start -n com.ashlarprotocol/.MainActivity
```

- [ ] **Step 2: Verify the loop on-device** (screenshots, valid on-screen `x<1264`, scroll `x≈1150`; verify via screenshots not uiautomator dumps):
  - The Board shows **THE DAY'S WORK** (weekly + daily menu) and **THE TEMPLE**.
  - Tap a daily challenge → it settles to "✓ tended"; the Temple's "wages in hand" rises by 1.
  - Earn enough → the **Lay the next course** affordance appears → tap → the Temple stack rises by one course; the standing shows "Course 1 of 50".
  - Skip a challenge / relaunch → nothing is lost or penalised (forgiving).
  - **§9 "NEED HELP?"** still pinned top-right throughout.

- [ ] **Step 3: Finish** — with everything green + device-verified, update memory; then (gated on the founder's go, per "no push without ask") push `feat/the-temple`, open a PR, merge to `origin/main`, remove the worktree.

---

## Notes for the executor
- This is the **MVP slice** only: Apprentice tranche, daily+weekly challenges, wages+courses+Temple. **Later tranches** (separate plans): Fellowcraft/Master Mason courses, monthly reflection into the mirror, and adornment/customization.
- Hold every Global Constraint — especially: never gate a tool, never punish a miss, deterministic wages, quiet numbers, §9 untouched.
