package com.ashlarprotocol.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.ashlarprotocol.tools.KindStreak
import com.ashlarprotocol.tools.StreakState
import com.ashlarprotocol.tools.Strength
import com.ashlarprotocol.tools.Readiness

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ashlar_prefs")

class LocalDataStore(private val context: Context) {
    private val STREAK_KEY = androidx.datastore.preferences.core.intPreferencesKey("briefing_streak")
    private val LAST_DATE_KEY = androidx.datastore.preferences.core.longPreferencesKey("last_briefing_date")
    private val AAR_ENTRIES_KEY = androidx.datastore.preferences.core.stringPreferencesKey("aar_entries")
    private val AAR_DRAFT_KEY = androidx.datastore.preferences.core.stringPreferencesKey("aar_draft")
    private val REFLECTIONS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("chamber_reflections")
    private val PLUMB_RECORDS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("plumb_records")
    // WHO-5 wellbeing checks (score + timestamp). The primary outcome metric; on-device only. See tools/WhoFive.
    private val WHO5_KEY = androidx.datastore.preferences.core.stringPreferencesKey("who5_results")
    // Self-authored practices ("After [anchor], I will [action]"). On-device only. See tools/PracticeAuthoring.
    private val PRACTICES_KEY = androidx.datastore.preferences.core.stringPreferencesKey("practices")
    // Counts of deliberate practice, feeding the degree progression (see tools/Degrees.kt).
    private val PLUMB_SESSIONS_KEY = androidx.datastore.preferences.core.intPreferencesKey("plumb_sessions")
    private val GAUGE_DAYS_KEY = androidx.datastore.preferences.core.intPreferencesKey("gauge_days_complete")
    private val RECALL_SESSIONS_KEY = androidx.datastore.preferences.core.intPreferencesKey("recall_sessions")
    // The highest degree the member has been ceremonially raised into (ordinal). Drives the rite
    // so an advancement is marked exactly once (see tools/Advancement.kt). Local only.
    private val ACK_DEGREE_KEY = androidx.datastore.preferences.core.intPreferencesKey("acknowledged_degree_ordinal")
    // The initiation rite (first-run). Stored locally only, like everything else.
    private val INITIATED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("initiated")
    private val INTENTION_KEY = androidx.datastore.preferences.core.stringPreferencesKey("intention")
    // One self-directed environment change (Phase 4, F1). JSON of a CornerstoneEntry, or blank.
    private val CORNERSTONE_KEY = androidx.datastore.preferences.core.stringPreferencesKey("cornerstone")
    private val BASELINE_KEY = floatPreferencesKey("baseline_weight")
    // The kind streak ("tending the stone", tools/KindStreak.kt): a cumulative total that never
    // decreases, plus a grace-softened current run. Supersedes the old resettable briefing_streak.
    private val DAYS_TENDED_KEY = androidx.datastore.preferences.core.intPreferencesKey("days_tended")
    private val CURRENT_RUN_KEY = androidx.datastore.preferences.core.intPreferencesKey("current_run")
    private val GRACE_KEY = androidx.datastore.preferences.core.intPreferencesKey("grace_remaining")
    private val LAST_TENDED_DAY_KEY = androidx.datastore.preferences.core.longPreferencesKey("last_tended_day")

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

    /** Cumulative days tended — monotonic, never decreases. Feeds the degree score and the stone. */
    val daysTended: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DAYS_TENDED_KEY] ?: (preferences[STREAK_KEY] ?: 0)
    }

    /**
     * The full kind-streak state. On first read after the upgrade (no [DAYS_TENDED_KEY] yet), it
     * migrates the legacy briefing streak so no one loses progress (see KindStreak.seedFromLegacy).
     */
    val streakState: Flow<StreakState> = context.dataStore.data.map { preferences ->
        if (preferences[DAYS_TENDED_KEY] != null) {
            StreakState(
                daysTended = preferences[DAYS_TENDED_KEY] ?: 0,
                currentRun = preferences[CURRENT_RUN_KEY] ?: 0,
                graceRemaining = preferences[GRACE_KEY] ?: KindStreak.MAX_GRACE,
                lastTendedDay = preferences[LAST_TENDED_DAY_KEY] ?: -1L
            )
        } else {
            val legacyMillis = preferences[LAST_DATE_KEY] ?: 0L
            val legacyDay = if (legacyMillis > 0L)
                KindStreak.epochDay(legacyMillis, java.util.TimeZone.getDefault().getOffset(legacyMillis))
            else -1L
            KindStreak.seedFromLegacy(preferences[STREAK_KEY] ?: 0, legacyDay)
        }
    }

    suspend fun saveStreakState(state: StreakState) {
        context.dataStore.edit { preferences ->
            preferences[DAYS_TENDED_KEY] = state.daysTended
            preferences[CURRENT_RUN_KEY] = state.currentRun
            preferences[GRACE_KEY] = state.graceRemaining
            preferences[LAST_TENDED_DAY_KEY] = state.lastTendedDay
        }
    }

    // The user's chosen VIA signature strengths, highest first, stored as enum names (see tools/Strengths.kt).
    private val SIGNATURE_STRENGTHS_KEY =
        androidx.datastore.preferences.core.stringPreferencesKey("signature_strengths")

    val signatureStrengths: Flow<List<Strength>> = context.dataStore.data.map { preferences ->
        (preferences[SIGNATURE_STRENGTHS_KEY] ?: "")
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { name -> runCatching { Strength.valueOf(name) }.getOrNull() }
    }

    suspend fun setSignatureStrengths(strengths: List<Strength>) {
        context.dataStore.edit { preferences ->
            preferences[SIGNATURE_STRENGTHS_KEY] = strengths.joinToString(",") { it.name }
        }
    }

    // Today's "Working" check-in: how the person arrives (Readiness) + the epoch-day it was set +
    // the persistent difficulty dial. See tools/Working.kt.
    private val READINESS_KEY =
        androidx.datastore.preferences.core.stringPreferencesKey("readiness")
    private val READINESS_DAY_KEY =
        androidx.datastore.preferences.core.longPreferencesKey("readiness_day")
    private val DIAL_KEY =
        androidx.datastore.preferences.core.intPreferencesKey("effort_dial")

    val readiness: Flow<Readiness?> = context.dataStore.data.map { preferences ->
        preferences[READINESS_KEY]?.let { runCatching { Readiness.valueOf(it) }.getOrNull() }
    }
    val readinessDay: Flow<Long> = context.dataStore.data.map { it[READINESS_DAY_KEY] ?: -1L }
    val dial: Flow<Int> = context.dataStore.data.map { it[DIAL_KEY] ?: 0 }

    suspend fun setReadiness(readiness: Readiness, epochDay: Long) {
        context.dataStore.edit { preferences ->
            preferences[READINESS_KEY] = readiness.name
            preferences[READINESS_DAY_KEY] = epochDay
        }
    }

    suspend fun setDial(value: Int) {
        context.dataStore.edit { preferences -> preferences[DIAL_KEY] = value }
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

    val whoFiveResults: Flow<List<WhoFiveResult>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[WHO5_KEY] ?: "[]"
        try {
            kotlinx.serialization.json.Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setWhoFiveResults(entries: List<WhoFiveResult>) {
        context.dataStore.edit { preferences ->
            preferences[WHO5_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(WhoFiveResult.serializer()),
                entries
            )
        }
    }

    val practices: Flow<List<Practice>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[PRACTICES_KEY] ?: "[]"
        try {
            kotlinx.serialization.json.Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setPractices(entries: List<Practice>) {
        context.dataStore.edit { preferences ->
            preferences[PRACTICES_KEY] = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(Practice.serializer()),
                entries
            )
        }
    }

    val plumbSessions: Flow<Int> = context.dataStore.data.map { it[PLUMB_SESSIONS_KEY] ?: 0 }
    val gaugeDaysComplete: Flow<Int> = context.dataStore.data.map { it[GAUGE_DAYS_KEY] ?: 0 }
    val recallSessions: Flow<Int> = context.dataStore.data.map { it[RECALL_SESSIONS_KEY] ?: 0 }
    val acknowledgedDegreeOrdinal: Flow<Int> = context.dataStore.data.map { it[ACK_DEGREE_KEY] ?: 0 }

    suspend fun incrementPlumbSessions() {
        context.dataStore.edit { it[PLUMB_SESSIONS_KEY] = (it[PLUMB_SESSIONS_KEY] ?: 0) + 1 }
    }

    suspend fun incrementGaugeDaysComplete() {
        context.dataStore.edit { it[GAUGE_DAYS_KEY] = (it[GAUGE_DAYS_KEY] ?: 0) + 1 }
    }

    suspend fun incrementRecallSessions() {
        context.dataStore.edit { it[RECALL_SESSIONS_KEY] = (it[RECALL_SESSIONS_KEY] ?: 0) + 1 }
    }

    suspend fun setAcknowledgedDegreeOrdinal(ordinal: Int) {
        context.dataStore.edit { it[ACK_DEGREE_KEY] = ordinal.coerceAtLeast(0) }
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

    /** Update just the intention (e.g. from the Square rite), without re-running initiation. */
    suspend fun setIntention(text: String) {
        context.dataStore.edit { it[INTENTION_KEY] = text }
    }

    /** The person's current cornerstone (self-directed environment change), or null if none set. */
    val cornerstone: Flow<CornerstoneEntry?> = context.dataStore.data.map { prefs ->
        prefs[CORNERSTONE_KEY]?.takeIf { it.isNotBlank() }?.let {
            try {
                kotlinx.serialization.json.Json.decodeFromString(CornerstoneEntry.serializer(), it)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun setCornerstone(entry: CornerstoneEntry) {
        context.dataStore.edit {
            it[CORNERSTONE_KEY] = kotlinx.serialization.json.Json.encodeToString(CornerstoneEntry.serializer(), entry)
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
