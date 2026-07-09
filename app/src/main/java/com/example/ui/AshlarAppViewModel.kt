package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalDataStore
import com.example.tools.DailyWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import com.example.tools.KindStreak
import com.example.tools.Strength
import com.example.tools.Strengths
import com.example.tools.Working
import com.example.tools.Readiness
import com.example.tools.Effort

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

    val aarEntries: StateFlow<List<com.example.data.AarEntry>> = dataStore.aarEntries.stateIn(
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

    val plumbRecords: StateFlow<List<com.example.data.PlumbRecord>> = dataStore.plumbRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addPlumbRecord(thought: String, reflection: String) {
        if (thought.isBlank()) return
        viewModelScope.launch {
            val entry = com.example.data.PlumbRecord(
                id = java.util.UUID.randomUUID().toString(),
                thought = thought.trim(),
                reflection = reflection.trim(),
                timestamp = System.currentTimeMillis()
            )
            dataStore.setPlumbRecords((listOf(entry) + plumbRecords.value).take(50))
        }
    }

    // Chamber reflections the user chose to keep.
    val reflections: StateFlow<List<com.example.data.Reflection>> = dataStore.reflections.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun addReflection(text: String) {
        viewModelScope.launch {
            val entry = com.example.data.Reflection(
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

    fun addAarEntry(entry: com.example.data.AarEntry) {
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
