package com.ashlarprotocol.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ashlarprotocol.data.LocalDataStore
import com.ashlarprotocol.tools.DailyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import com.ashlarprotocol.tools.KindStreak
import com.ashlarprotocol.tools.Strength
import com.ashlarprotocol.tools.Strengths
import com.ashlarprotocol.tools.Working
import com.ashlarprotocol.tools.Readiness
import com.ashlarprotocol.tools.Degree
import com.ashlarprotocol.tools.Advancement
import com.ashlarprotocol.tools.Effort

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

/** Today's Working: how the person arrives, the adapted effort tier, and a warm acknowledgment. */
data class DailyWorking(val readiness: Readiness, val effort: Effort, val acknowledgment: String)

class AshlarAppViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = LocalDataStore(application)

    // Backed by the CUMULATIVE days-tended count (KindStreak) — monotonic, so a missed day never
    // drags the degree score or the stone backward. Name kept for the Board/Degrees consumers.
    val briefingStreak: StateFlow<Int> = dataStore.daysTended.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // The grace reserve — surfaced (F3) so it can be protected, not a silent buffer (Sharif & Shu).
    val graceRemaining: StateFlow<Int> = dataStore.streakState
        .map { it.graceRemaining }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.ashlarprotocol.tools.KindStreak.MAX_GRACE)

    // The current consecutive run — for the mirror ("What the Stone Remembers").
    val currentRun: StateFlow<Int> = dataStore.streakState
        .map { it.currentRun }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // The Cornerstone — the person's one self-directed environment change (F1).
    val cornerstone: StateFlow<com.ashlarprotocol.data.CornerstoneEntry?> = dataStore.cornerstone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setCornerstone(entry: com.ashlarprotocol.data.CornerstoneEntry) {
        viewModelScope.launch { dataStore.setCornerstone(entry) }
    }

    // Automaticity — the honest progress signal (F4). The epoch-day last asked; -1 = never.
    val automaticityDay: StateFlow<Int> = dataStore.automaticityDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    val automaticityLevel: StateFlow<Int> = dataStore.automaticityLevel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    // ── What the Stone Remembers (the mirror) — a deterministic scribe over the on-device data ──
    /** A snapshot for the reflection engine; precomputes epoch-days/spans so the engine stays pure. */
    /**
     * A snapshot of the mirror's inputs, read straight from the DataStore source of truth.
     *
     * This deliberately does NOT read the VM's StateFlow `.value`s: those are `WhileSubscribed`, so a
     * flow that isn't being collected by whatever screen is on top reads its cold initial value — and
     * the mirror would silently forget real data (e.g. the rough edge, only observed on the Tools
     * screen). A faithful scribe must read what's persisted, not what a screen happens to be watching,
     * so every field comes from `dataStore.*.first()`. Suspends; called on card-open only.
     */
    suspend fun buildReflectionInput(): com.ashlarprotocol.tools.ReflectionInput {
        val now = System.currentTimeMillis()
        val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
        val who = dataStore.whoFiveResults.first().sortedBy { it.timestamp }
        val whoSpanDays = if (who.size >= 2) ((who.last().timestamp - who.first().timestamp) / 86_400_000L).toInt() else 0
        val re = dataStore.roughEdge.first()
        val lapseDays = re?.lapses?.map { KindStreak.epochDay(it, TimeZone.getDefault().getOffset(it)) } ?: emptyList()
        val rh = dataStore.rhythm.first()
        // The practice tallies the mirror reflects back as plain counts.
        val streak = dataStore.daysTended.first()
        val aar = dataStore.aarEntries.first()
        val plumb = dataStore.plumbSessions.first()
        val gauge = dataStore.gaugeDaysComplete.first()
        val recall = dataStore.recallSessions.first()
        // The Temple (the monthly review): progress on the 50-course journey + the month's challenge rhythm.
        val coursesRaised = dataStore.coursesRaised.first()
        // Degree = the Temple's milestone (Phase-3 single source of truth). Deriving it here the same way
        // the Board does keeps the mirror from ever telling the user a different degree than the Board shows.
        val degree = com.ashlarprotocol.tools.Temple.degreeFor(coursesRaised)
        val completions = dataStore.challengeCompletions.first()
        val challengeDaysLast30 = completions
            .map { KindStreak.epochDay(it.timestamp, TimeZone.getDefault().getOffset(it.timestamp)) }
            .filter { it > today - 30 }
            .distinct().size
        return com.ashlarprotocol.tools.ReflectionInput(
            daysTended = streak,
            currentRun = dataStore.streakState.first().currentRun,
            degreeDisplay = degree.display,
            intention = dataStore.intention.first(),
            practicesCount = dataStore.practices.first().size,
            journalCount = aar.size,
            plumbCount = plumb,
            gaugeDays = gauge,
            recallCount = recall,
            keptReflectionsCount = dataStore.reflections.first().size,
            signatureStrengths = dataStore.signatureStrengths.first().map { it.display },
            automaticityLevel = dataStore.automaticityLevel.first(),
            rhythmWake = rh?.let { com.ashlarprotocol.tools.Rhythm.formatTime(it.wakeMinutesOfDay) },
            rhythmWindDown = rh?.let { com.ashlarprotocol.tools.Rhythm.formatTime(it.windDownMinutesOfDay) },
            roughEdgeName = re?.name,
            roughEdgeLapseDays = lapseDays,
            todayEpochDay = today,
            whoFiveScores = who.map { it.score },
            whoFiveSpanDays = whoSpanDays,
            coursesRaised = coursesRaised,
            latestCourseName = com.ashlarprotocol.tools.Temple.courseAt(coursesRaised)?.name ?: "",
            challengesAnswered = completions.size,
            challengeDaysLast30 = challengeDaysLast30
        )
    }

    /** The reflections for "What the Stone Remembers" — computed on demand (pull-only, snapshot on open). */
    suspend fun reflect(): List<com.ashlarprotocol.tools.Reflection> =
        com.ashlarprotocol.tools.Reflections.reflect(buildReflectionInput())

    // ── The Temple (progression) ────────────────────────────────────────────────────────────
    val totalWagesEarned: StateFlow<Int> = dataStore.totalWagesEarned
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val coursesRaised: StateFlow<Int> = dataStore.coursesRaised
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val challengeCompletions: StateFlow<List<com.ashlarprotocol.data.ChallengeCompletion>> =
        dataStore.challengeCompletions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    // Adornment (finishes bought + the one selected). Buying spends wages; selecting an owned finish is free.
    val unlockedFinishes: StateFlow<List<String>> = dataStore.unlockedFinishes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val selectedFinish: StateFlow<String> = dataStore.selectedFinish
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.ashlarprotocol.tools.Adornment.DEFAULT_ID)

    /** Wages in hand = earned − laid into courses − spent on finishes. Floors at 0 (never a scarcity debt). */
    val wageBalance: StateFlow<Int> =
        kotlinx.coroutines.flow.combine(
            dataStore.totalWagesEarned, dataStore.coursesRaised, dataStore.unlockedFinishes
        ) { earned, raised, finishes ->
            (com.ashlarprotocol.tools.Temple.balance(earned, raised) -
                com.ashlarprotocol.tools.Adornment.totalSpend(finishes)).coerceAtLeast(0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** The true wages in hand, read from persisted state (never a cold StateFlow). */
    private suspend fun currentBalance(): Int {
        val earned = dataStore.totalWagesEarned.first()
        val raised = dataStore.coursesRaised.first()
        val finishes = dataStore.unlockedFinishes.first()
        return (com.ashlarprotocol.tools.Temple.balance(earned, raised) -
            com.ashlarprotocol.tools.Adornment.totalSpend(finishes)).coerceAtLeast(0)
    }

    /** The period a challenge is idempotent within: epoch-day for DAILY, epoch-week for WEEKLY. */
    private fun periodKey(cadence: com.ashlarprotocol.tools.Cadence, today: Long): Long =
        if (cadence == com.ashlarprotocol.tools.Cadence.WEEKLY) Math.floorDiv(today, 7L) else today

    /** Complete a challenge: pays wages ONCE per period, records it, tends the stone. A no-op if
     *  already done this period. Missing a challenge is never modelled — it simply never calls this. */
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
            updateStreak()
            // The stone catches the light on the actual daily action (P2.1). The Day's Work is the
            // on-Board spine, so the pulse now fires where the stone is actually on screen.
            bumpActionPulse()
        }
    }

    /** Lay wages to raise the next course — only if affordable. Reads persisted truth; never punitive. */
    fun raiseCourse() {
        viewModelScope.launch {
            val raised = dataStore.coursesRaised.first()
            val next = com.ashlarprotocol.tools.Temple.nextCourse(raised) ?: return@launch
            if (currentBalance() >= next.cost) {
                dataStore.setCoursesRaised(raised + 1)
                bumpActionPulse()   // laying a course is a real act of building — the stone catches it too
            }
        }
    }

    /** Buy a finish (once) and select it — only if affordable. Bought finishes are permanent (spent, not lost). */
    fun unlockFinish(id: String) {
        viewModelScope.launch {
            val owned = dataStore.unlockedFinishes.first()
            if (id in owned) { dataStore.setSelectedFinish(id); return@launch }
            if (currentBalance() >= com.ashlarprotocol.tools.Adornment.costOf(id)) {
                dataStore.setUnlockedFinishes(owned + id)
                dataStore.setSelectedFinish(id)
            }
        }
    }

    /** Select a finish you already own (or the free default). Free to switch, any time. */
    fun selectFinish(id: String) {
        viewModelScope.launch {
            if (com.ashlarprotocol.tools.Adornment.isAvailable(id, dataStore.unlockedFinishes.first()))
                dataStore.setSelectedFinish(id)
        }
    }

    fun recordAutomaticity(value: Int) {
        val now = System.currentTimeMillis()
        val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now)).toInt()
        viewModelScope.launch { dataStore.setAutomaticity(value, today) }
    }

    // The rhythm anchor (F6) — a steady rise + wind-down. Associational, never an alarm.
    val rhythm: StateFlow<com.ashlarprotocol.data.RhythmAnchor?> = dataStore.rhythm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setRhythm(wakeMinutesOfDay: Int, windDownMinutesOfDay: Int) {
        viewModelScope.launch {
            dataStore.setRhythm(com.ashlarprotocol.data.RhythmAnchor(wakeMinutesOfDay, windDownMinutesOfDay))
        }
    }

    // The Rough Edge (F5) — one bad habit worked on the anti-AVE spine; the lapse ledger only ever grows.
    val roughEdge: StateFlow<com.ashlarprotocol.data.RoughEdgeEntry?> = dataStore.roughEdge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setRoughEdge(name: String, cue: String, environmentMove: String, replacement: String) {
        viewModelScope.launch {
            // Read the persisted ledger, not a cold StateFlow — a re-authored edge must never lose its lapses.
            val existingLapses = dataStore.roughEdge.first()?.lapses ?: emptyList()
            dataStore.setRoughEdge(
                com.ashlarprotocol.data.RoughEdgeEntry(
                    name = name.trim(), cue = cue.trim(),
                    environmentMove = environmentMove.trim(), replacement = replacement.trim(),
                    lapses = existingLapses // preserve the ledger; never reset
                )
            )
        }
    }

    /** Log a slip — appended to the ledger, never a streak to break (anti-AVE). */
    fun recordLapse() {
        viewModelScope.launch {
            val current = dataStore.roughEdge.first() ?: return@launch
            dataStore.setRoughEdge(current.copy(lapses = current.lapses + System.currentTimeMillis()))
        }
    }

    val aarEntries: StateFlow<List<com.ashlarprotocol.data.AarEntry>> = dataStore.aarEntries.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val aarDraft: StateFlow<String> = dataStore.aarDraft.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    // Deliberate-practice counts that feed the degree progression (see tools/Degrees.kt).
    val plumbSessions: StateFlow<Int> = dataStore.plumbSessions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val gaugeDaysComplete: StateFlow<Int> = dataStore.gaugeDaysComplete.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val recallSessions: StateFlow<Int> = dataStore.recallSessions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    // ── The rite of passage — degrees are the Temple's milestones (Phase-3 consolidation) ──────
    // Degree is no longer a separate practice-count score; it's how far you've built the Temple
    // (Temple.degreeFor). "Fellowcraft" now means "you built into the Fellowcraft courses", one
    // meaning instead of two. The stone stays tending-driven; only the degree moved.
    /** The degree you stand in — derived from courses raised. Names the layer beneath the stone. */
    val currentDegree: StateFlow<Degree> = dataStore.coursesRaised
        .map { com.ashlarprotocol.tools.Temple.degreeFor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Degree.ENTERED_APPRENTICE)

    private val ackDegreeOrdinal: StateFlow<Int> = dataStore.acknowledgedDegreeOrdinal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Non-null when you've built into a new degree whose Raising hasn't been received yet. */
    val pendingAdvancement: StateFlow<Degree?> = combine(ackDegreeOrdinal, dataStore.coursesRaised) { ack, raised ->
        val d = com.ashlarprotocol.tools.Temple.degreeFor(raised)
        if (d.ordinal > ack) d else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Mark the pending raising as received, so the rite fires exactly once per degree. */
    fun acknowledgeAdvancement() {
        val d = pendingAdvancement.value ?: return
        viewModelScope.launch { dataStore.setAcknowledgedDegreeOrdinal(d.ordinal) }
    }

    // (Phase-3) The deliberate practices feed the one engine: doing them pays wages toward the Temple.
    fun recordPlumbSession() {
        viewModelScope.launch {
            dataStore.incrementPlumbSessions()
            dataStore.addWages(com.ashlarprotocol.tools.Temple.PRACTICE_WAGE)
        }
        bumpActionPulse()
    }

    fun recordGaugeDayComplete() {
        viewModelScope.launch {
            dataStore.incrementGaugeDaysComplete()
            dataStore.addWages(com.ashlarprotocol.tools.Temple.PRACTICE_WAGE)
        }
        bumpActionPulse()
    }

    fun recordRecallSession() {
        viewModelScope.launch {
            dataStore.incrementRecallSessions()
            dataStore.addWages(com.ashlarprotocol.tools.Temple.PRACTICE_WAGE)
        }
        bumpActionPulse()
    }

    // Micro-feedback pulse (SPEC P0.4 / ticket T2.3): bumped ONLY on a genuine completed action —
    // a practice or the Working check-in — never on launch or a timer, so the stone's light-catch
    // is always an honest mirror of something the person just did, never a decoupled reward.
    private val _actionPulse = MutableStateFlow(0)
    val actionPulse: StateFlow<Int> = _actionPulse.asStateFlow()
    private fun bumpActionPulse() { _actionPulse.value++ }

    // First-run initiation rite. null = still loading (avoids flashing the wrong screen on cold start).
    val initiated: StateFlow<Boolean?> = dataStore.initiated.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun completeInitiation(intention: String, weight: Float) {
        viewModelScope.launch { dataStore.completeInitiation(intention, weight) }
    }

    /** Set the intention from the Square rite (updates what the Board shows). */
    fun setIntention(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch { dataStore.setIntention(text.trim()) }
    }

    // The re-authoring engine: the app remembers what you're working toward, and your own words.
    val intention: StateFlow<String> = dataStore.intention.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )

    val plumbRecords: StateFlow<List<com.ashlarprotocol.data.PlumbRecord>> = dataStore.plumbRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // WHO-5 wellbeing checks — the primary outcome metric (SPEC T3.1). On-device only, newest first.
    val whoFiveResults: StateFlow<List<com.ashlarprotocol.data.WhoFiveResult>> = dataStore.whoFiveResults.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addWhoFiveResult(score: Int) {
        viewModelScope.launch {
            val entry = com.ashlarprotocol.data.WhoFiveResult(
                id = java.util.UUID.randomUUID().toString(),
                score = score.coerceIn(0, 100),
                timestamp = System.currentTimeMillis()
            )
            dataStore.setWhoFiveResults((listOf(entry) + dataStore.whoFiveResults.first()).take(60))
        }
    }

    // Self-authored practices — "After [anchor], I will [action]" (T1.4). On-device only, newest first.
    val practices: StateFlow<List<com.ashlarprotocol.data.Practice>> = dataStore.practices.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addPractice(anchor: String, action: String, reminderMinutesOfDay: Int? = null, cueKind: String? = null) {
        if (!com.ashlarprotocol.tools.PracticeAuthoring.canSave(anchor, action)) return
        viewModelScope.launch {
            val entry = com.ashlarprotocol.data.Practice(
                id = java.util.UUID.randomUUID().toString(),
                anchor = anchor.trim(),
                action = action.trim(),
                timestamp = System.currentTimeMillis(),
                reminderMinutesOfDay = reminderMinutesOfDay,
                cueKind = cueKind
            )
            dataStore.setPractices((listOf(entry) + dataStore.practices.first()).take(30))
            if (reminderMinutesOfDay != null) schedulePracticeReminder(entry)
        }
    }

    fun removePractice(id: String) {
        viewModelScope.launch { dataStore.setPractices(dataStore.practices.first().filter { it.id != id }) }
        cancelPracticeReminder(id)
    }

    /** Schedule a daily cue-anchored reminder for [p] at its chosen time (T1.5). Gentle, opt-in. */
    private fun schedulePracticeReminder(p: com.ashlarprotocol.data.Practice) {
        val minutes = p.reminderMinutesOfDay ?: return
        val cal = java.util.Calendar.getInstance()
        val nowMinuteOfDay = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
        val delayMin = com.ashlarprotocol.tools.PracticeReminder.initialDelayMinutes(minutes, nowMinuteOfDay)
        val data = androidx.work.Data.Builder()
            .putString(com.ashlarprotocol.worker.PracticeReminderWorker.KEY_ID, p.id)
            .putString(com.ashlarprotocol.worker.PracticeReminderWorker.KEY_ANCHOR, p.anchor)
            .putString(com.ashlarprotocol.worker.PracticeReminderWorker.KEY_ACTION, p.action)
            .build()
        val request = androidx.work.PeriodicWorkRequestBuilder<com.ashlarprotocol.worker.PracticeReminderWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
            .setInitialDelay(delayMin, java.util.concurrent.TimeUnit.MINUTES)
            .setInputData(data)
            .build()
        androidx.work.WorkManager.getInstance(getApplication())
            .enqueueUniquePeriodicWork("practice_${p.id}", androidx.work.ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private fun cancelPracticeReminder(id: String) {
        androidx.work.WorkManager.getInstance(getApplication()).cancelUniqueWork("practice_$id")
    }

    fun addPlumbRecord(thought: String, reflection: String) {
        if (thought.isBlank()) return
        viewModelScope.launch {
            val entry = com.ashlarprotocol.data.PlumbRecord(
                id = java.util.UUID.randomUUID().toString(),
                thought = thought.trim(),
                reflection = reflection.trim(),
                timestamp = System.currentTimeMillis()
            )
            dataStore.setPlumbRecords((listOf(entry) + dataStore.plumbRecords.first()).take(50))
        }
    }

    // Chamber reflections the user chose to keep.
    val reflections: StateFlow<List<com.ashlarprotocol.data.Reflection>> = dataStore.reflections.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addReflection(text: String) {
        viewModelScope.launch {
            val entry = com.ashlarprotocol.data.Reflection(
                id = java.util.UUID.randomUUID().toString(),
                text = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            dataStore.setReflections(listOf(entry) + dataStore.reflections.first())
        }
    }

    fun removeReflection(id: String) {
        viewModelScope.launch {
            dataStore.setReflections(dataStore.reflections.first().filter { it.id != id })
        }
    }

    private val _dailyBriefing = MutableStateFlow<String?>(null)
    val dailyBriefing: StateFlow<String?> = _dailyBriefing.asStateFlow()

    // A warm, self-forgiving message surfaced when the user returns after a lapse (see KindStreak).
    private val _streakComeback = MutableStateFlow<String?>(null)
    val streakComeback: StateFlow<String?> = _streakComeback.asStateFlow()
    fun clearStreakComeback() { _streakComeback.value = null }

    // VIA signature strengths (intrinsic progression) + today's "use it in a new way" prompt.
    val signatureStrengths: StateFlow<List<Strength>> = dataStore.signatureStrengths.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun setSignatureStrengths(strengths: List<Strength>) {
        viewModelScope.launch { dataStore.setSignatureStrengths(strengths) }
    }

    val todayStrengthPrompt: StateFlow<String?> = dataStore.signatureStrengths.map { sig ->
        val now = System.currentTimeMillis()
        val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
        Strengths.strengthOfTheDay(sig, today)?.let { Strengths.newWayPrompt(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // The daily "Working": today's readiness check-in -> adaptive effort + warm acknowledgment.
    val dial: StateFlow<Int> = dataStore.dial.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val todayWorking: StateFlow<DailyWorking?> = combine(
        dataStore.readiness, dataStore.readinessDay, dataStore.dial
    ) { stored, day, d ->
        val now = System.currentTimeMillis()
        val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
        Working.readinessForToday(stored, day, today)?.let { r ->
            DailyWorking(r, Working.effortFor(r, d), Working.acknowledgment(r))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun checkInReadiness(readiness: Readiness) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
            dataStore.setReadiness(readiness, today)
        }
        bumpActionPulse()
    }

    fun nudgeDial(delta: Int) {
        viewModelScope.launch { dataStore.setDial((dataStore.dial.first() + delta).coerceIn(-1, 1)) }
    }

    // Bundled, on-device word rotation — no network, no API key, no cost. Starts on today's word;
    // SYNC advances to the next. Replaces the old paid Gemini call.
    private var wordIndex: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    init {
        fetchDailyBriefing()
    }

    /** Show a word (today's on launch; the next each time the user taps SYNC). Local, always succeeds. */
    fun fetchDailyBriefing() {
        _dailyBriefing.value = DailyWord.wordAt(wordIndex)
        wordIndex++
        viewModelScope.launch { updateStreak() }
    }

    private suspend fun updateStreak() {
        // Tend the stone: a cumulative total that only grows, softened by grace days, with a warm
        // comeback after a lapse — never the old reset-to-1. See tools/KindStreak.kt.
        val state = dataStore.streakState.first()
        val now = System.currentTimeMillis()
        val today = KindStreak.epochDay(now, TimeZone.getDefault().getOffset(now))
        val outcome = KindStreak.tend(state, today)
        dataStore.saveStreakState(outcome.state)
        if (outcome.isComeback) {
            // A warm comeback, plus a landmark "clean page" if it's a new week (fresh-start effect, F8).
            _streakComeback.value = KindStreak.comebackMessage() +
                (KindStreak.freshStartLine(today)?.let { "\n\n$it" } ?: "")
        }
    }

    fun addAarEntry(entry: com.ashlarprotocol.data.AarEntry) {
        viewModelScope.launch {
            val current = dataStore.aarEntries.first().toMutableList()
            current.add(0, entry)
            dataStore.setAarEntries(current)
        }
    }

    fun removeAarEntry(id: String) {
        viewModelScope.launch {
            val current = dataStore.aarEntries.first().filter { it.id != id }
            dataStore.setAarEntries(current)
        }
    }

    fun setAarDraft(draft: String) {
        viewModelScope.launch {
            dataStore.setAarDraft(draft)
        }
    }
}
