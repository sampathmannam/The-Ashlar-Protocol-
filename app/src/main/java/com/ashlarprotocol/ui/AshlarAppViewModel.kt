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
import com.ashlarprotocol.tools.Degrees
import com.ashlarprotocol.tools.WorkStats
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

    // The Cornerstone — the person's one self-directed environment change (F1).
    val cornerstone: StateFlow<com.ashlarprotocol.data.CornerstoneEntry?> = dataStore.cornerstone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setCornerstone(entry: com.ashlarprotocol.data.CornerstoneEntry) {
        viewModelScope.launch { dataStore.setCornerstone(entry) }
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

    // ── The rite of passage (Phase 2) ──────────────────────────────────────────
    // The same WorkStats the Board builds → the earned degree score.
    private val degreeScore: StateFlow<Int> = combine(
        briefingStreak, aarEntries, plumbSessions, gaugeDaysComplete, recallSessions
    ) { streak, entries, plumb, gauge, recall ->
        Degrees.score(WorkStats(streak, entries.size, plumb, gauge, recall))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** The degree earned by the work done so far. Names the layer beneath the stone. */
    val currentDegree: StateFlow<Degree> = degreeScore
        .map { Degrees.current(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Degree.ENTERED_APPRENTICE)

    /** Progress across the current degree toward the next (0f..1f); 1f at the summit. */
    val degreeProgress: StateFlow<Float> = degreeScore
        .map { Degrees.progressToNext(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val ackDegreeOrdinal: StateFlow<Int> = dataStore.acknowledgedDegreeOrdinal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Non-null when a newly-earned degree awaits its raising rite (see tools/Advancement.kt). */
    val pendingAdvancement: StateFlow<Degree?> = combine(ackDegreeOrdinal, degreeScore) { ack, score ->
        Advancement.pending(ack, score)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Mark the pending raising as received, so the rite fires exactly once per degree. */
    fun acknowledgeAdvancement() {
        val d = pendingAdvancement.value ?: return
        viewModelScope.launch { dataStore.setAcknowledgedDegreeOrdinal(d.ordinal) }
    }

    fun recordPlumbSession() {
        viewModelScope.launch { dataStore.incrementPlumbSessions() }
        bumpActionPulse()
    }

    fun recordGaugeDayComplete() {
        viewModelScope.launch { dataStore.incrementGaugeDaysComplete() }
        bumpActionPulse()
    }

    fun recordRecallSession() {
        viewModelScope.launch { dataStore.incrementRecallSessions() }
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
            dataStore.setWhoFiveResults((listOf(entry) + whoFiveResults.value).take(60))
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
            dataStore.setPractices((listOf(entry) + practices.value).take(30))
            if (reminderMinutesOfDay != null) schedulePracticeReminder(entry)
        }
    }

    fun removePractice(id: String) {
        viewModelScope.launch { dataStore.setPractices(practices.value.filter { it.id != id }) }
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
            dataStore.setPlumbRecords((listOf(entry) + plumbRecords.value).take(50))
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
            dataStore.setReflections(listOf(entry) + reflections.value)
        }
    }

    fun removeReflection(id: String) {
        viewModelScope.launch {
            dataStore.setReflections(reflections.value.filter { it.id != id })
        }
    }

    private val _dailyBriefing = MutableStateFlow<String?>(null)
    val dailyBriefing: StateFlow<String?> = _dailyBriefing.asStateFlow()

    // Kept only so the Board's card keeps its API; the local word never has to "fetch".
    private val _isFetchingBriefing = MutableStateFlow(false)
    val isFetchingBriefing: StateFlow<Boolean> = _isFetchingBriefing.asStateFlow()

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
        viewModelScope.launch { dataStore.setDial((dial.value + delta).coerceIn(-1, 1)) }
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
            _streakComeback.value = KindStreak.comebackMessage()
        }
    }

    fun addAarEntry(entry: com.ashlarprotocol.data.AarEntry) {
        viewModelScope.launch {
            val current = aarEntries.value.toMutableList()
            current.add(0, entry)
            dataStore.setAarEntries(current)
        }
    }
    
    fun removeAarEntry(id: String) {
        viewModelScope.launch {
            val current = aarEntries.value.filter { it.id != id }
            dataStore.setAarEntries(current)
        }
    }

    fun setAarDraft(draft: String) {
        viewModelScope.launch {
            dataStore.setAarDraft(draft)
        }
    }
}
