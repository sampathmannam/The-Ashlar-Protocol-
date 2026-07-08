package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ashlar_prefs")

class LocalDataStore(private val context: Context) {
    private val PROGRESS_KEY = floatPreferencesKey("work_progress")
    private val VOLUME_KEY = floatPreferencesKey("weekly_volume")
    private val STREAK_KEY = androidx.datastore.preferences.core.intPreferencesKey("briefing_streak")
    private val LAST_DATE_KEY = androidx.datastore.preferences.core.longPreferencesKey("last_briefing_date")
    private val AAR_ENTRIES_KEY = androidx.datastore.preferences.core.stringPreferencesKey("aar_entries")
    private val AAR_DRAFT_KEY = androidx.datastore.preferences.core.stringPreferencesKey("aar_draft")
    private val REFLECTIONS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("chamber_reflections")
    private val PLUMB_RECORDS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("plumb_records")
    // Counts of deliberate practice, feeding the degree progression (see tools/Degrees.kt).
    private val PLUMB_SESSIONS_KEY = androidx.datastore.preferences.core.intPreferencesKey("plumb_sessions")
    private val GAUGE_DAYS_KEY = androidx.datastore.preferences.core.intPreferencesKey("gauge_days_complete")
    private val RECALL_SESSIONS_KEY = androidx.datastore.preferences.core.intPreferencesKey("recall_sessions")
    // The initiation rite (first-run). Stored locally only, like everything else.
    private val INITIATED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("initiated")
    private val INTENTION_KEY = androidx.datastore.preferences.core.stringPreferencesKey("intention")
    private val BASELINE_KEY = floatPreferencesKey("baseline_weight")

    val workProgress: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PROGRESS_KEY] ?: 0.1f
    }

    suspend fun setWorkProgress(progress: Float) {
        context.dataStore.edit { preferences ->
            preferences[PROGRESS_KEY] = progress
        }
    }

    val weeklyVolume: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[VOLUME_KEY] ?: 18.5f
    }

    suspend fun setWeeklyVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[VOLUME_KEY] = volume
        }
    }

    val briefingStreak: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[STREAK_KEY] ?: 0
    }

    val lastBriefingDate: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LAST_DATE_KEY] ?: 0L
    }

    suspend fun updateBriefingStreak(streak: Int, dateMillis: Long) {
        context.dataStore.edit { preferences ->
            preferences[STREAK_KEY] = streak
            preferences[LAST_DATE_KEY] = dateMillis
        }
    }

    val aarEntries: Flow<List<AarEntry>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[AAR_ENTRIES_KEY] ?: "[]"
        try {
            kotlinx.serialization.json.Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setAarEntries(entries: List<AarEntry>) {
        context.dataStore.edit { preferences ->
            preferences[AAR_ENTRIES_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(AarEntry.serializer()),
                entries
            )
        }
    }

    val plumbSessions: Flow<Int> = context.dataStore.data.map { it[PLUMB_SESSIONS_KEY] ?: 0 }
    val gaugeDaysComplete: Flow<Int> = context.dataStore.data.map { it[GAUGE_DAYS_KEY] ?: 0 }
    val recallSessions: Flow<Int> = context.dataStore.data.map { it[RECALL_SESSIONS_KEY] ?: 0 }

    suspend fun incrementPlumbSessions() {
        context.dataStore.edit { it[PLUMB_SESSIONS_KEY] = (it[PLUMB_SESSIONS_KEY] ?: 0) + 1 }
    }

    suspend fun incrementGaugeDaysComplete() {
        context.dataStore.edit { it[GAUGE_DAYS_KEY] = (it[GAUGE_DAYS_KEY] ?: 0) + 1 }
    }

    suspend fun incrementRecallSessions() {
        context.dataStore.edit { it[RECALL_SESSIONS_KEY] = (it[RECALL_SESSIONS_KEY] ?: 0) + 1 }
    }

    val initiated: Flow<Boolean> = context.dataStore.data.map { it[INITIATED_KEY] ?: false }
    val intention: Flow<String> = context.dataStore.data.map { it[INTENTION_KEY] ?: "" }
    val baselineWeight: Flow<Float> = context.dataStore.data.map { it[BASELINE_KEY] ?: 0.5f }

    /** Records the first-run initiation: the user's stated intention and how heavily they arrive. */
    suspend fun completeInitiation(intention: String, weight: Float) {
        context.dataStore.edit { prefs ->
            prefs[INITIATED_KEY] = true
            prefs[INTENTION_KEY] = intention
            prefs[BASELINE_KEY] = weight
        }
    }

    val reflections: Flow<List<Reflection>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[REFLECTIONS_KEY] ?: "[]"
        try {
            kotlinx.serialization.json.Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setReflections(entries: List<Reflection>) {
        context.dataStore.edit { preferences ->
            preferences[REFLECTIONS_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(Reflection.serializer()),
                entries
            )
        }
    }

    val plumbRecords: Flow<List<PlumbRecord>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[PLUMB_RECORDS_KEY] ?: "[]"
        try {
            kotlinx.serialization.json.Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setPlumbRecords(entries: List<PlumbRecord>) {
        context.dataStore.edit { preferences ->
            preferences[PLUMB_RECORDS_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(PlumbRecord.serializer()),
                entries
            )
        }
    }

    val aarDraft: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AAR_DRAFT_KEY] ?: ""
    }

    suspend fun setAarDraft(draft: String) {
        context.dataStore.edit { preferences ->
            preferences[AAR_DRAFT_KEY] = draft
        }
    }
}
