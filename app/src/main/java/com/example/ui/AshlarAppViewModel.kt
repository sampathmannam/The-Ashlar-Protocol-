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

import kotlinx.coroutines.flow.first

class AshlarAppViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = LocalDataStore(application)

    val workProgress: StateFlow<Float> = dataStore.workProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.1f
    )

    val weeklyVolume: StateFlow<Float> = dataStore.weeklyVolume.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        18.5f
    )

    val briefingStreak: StateFlow<Int> = dataStore.briefingStreak.stateIn(
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
    }

    fun recordGaugeDayComplete() {
        viewModelScope.launch { dataStore.incrementGaugeDaysComplete() }
    }

    fun recordRecallSession() {
        viewModelScope.launch { dataStore.incrementRecallSessions() }
    }

    // First-run initiation rite. null = still loading (avoids flashing the wrong screen on cold start).
    val initiated: StateFlow<Boolean?> = dataStore.initiated.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun completeInitiation(intention: String, weight: Float) {
        viewModelScope.launch { dataStore.completeInitiation(intention, weight) }
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
        val currentStreak = dataStore.briefingStreak.first()
        val lastDate = dataStore.lastBriefingDate.first()
        
        val now = System.currentTimeMillis()
        val calendarNow = Calendar.getInstance().apply { timeInMillis = now }
        val calendarLast = Calendar.getInstance().apply { timeInMillis = lastDate }

        if (lastDate > 0 && 
            calendarNow.get(Calendar.YEAR) == calendarLast.get(Calendar.YEAR) && 
            calendarNow.get(Calendar.DAY_OF_YEAR) == calendarLast.get(Calendar.DAY_OF_YEAR)) {
            dataStore.updateBriefingStreak(currentStreak, now)
            return
        }

        calendarLast.add(Calendar.DAY_OF_YEAR, 1)
        if (lastDate > 0 && 
            calendarNow.get(Calendar.YEAR) == calendarLast.get(Calendar.YEAR) && 
            calendarNow.get(Calendar.DAY_OF_YEAR) == calendarLast.get(Calendar.DAY_OF_YEAR)) {
            dataStore.updateBriefingStreak(currentStreak + 1, now)
        } else {
            dataStore.updateBriefingStreak(1, now)
        }
    }

    fun setWorkProgress(progress: Float) {
        viewModelScope.launch {
            dataStore.setWorkProgress(progress)
        }
    }

    fun setWeeklyVolume(volume: Float) {
        viewModelScope.launch {
            dataStore.setWeeklyVolume(volume)
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
