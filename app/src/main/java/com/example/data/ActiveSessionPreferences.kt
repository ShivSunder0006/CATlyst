package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "active_session")

class ActiveSessionPreferences(private val context: Context) {

    private val COUNT_KEY = intPreferencesKey("current_count")
    private val SECTION_KEY = stringPreferencesKey("current_section")
    private val GOAL_KEY = intPreferencesKey("current_goal")
    private val HAS_GOAL_KEY = stringPreferencesKey("has_goal")
    private val HINT_SEEN_KEY = stringPreferencesKey("has_seen_target_hint")
    private val THEME_KEY = stringPreferencesKey("app_theme")

    val activeSessionFlow: Flow<ActiveSessionData> = context.dataStore.data
        .map { preferences ->
            val count = preferences[COUNT_KEY] ?: 0
            val section = preferences[SECTION_KEY] ?: "VARC"
            val hasGoal = preferences[HAS_GOAL_KEY] == "true"
            val goal = if (hasGoal) preferences[GOAL_KEY] else null
            val hintSeen = preferences[HINT_SEEN_KEY] == "true"
            val theme = preferences[THEME_KEY] ?: "PASTEL"

            ActiveSessionData(count, section, goal, hintSeen, theme)
        }

    suspend fun setHintSeen() {
        context.dataStore.edit { preferences ->
            preferences[HINT_SEEN_KEY] = "true"
        }
    }

    suspend fun saveActiveSession(data: ActiveSessionData) {
        context.dataStore.edit { preferences ->
            preferences[COUNT_KEY] = data.currentCount
            preferences[SECTION_KEY] = data.selectedSection
            if (data.goal != null) {
                preferences[HAS_GOAL_KEY] = "true"
                preferences[GOAL_KEY] = data.goal
            } else {
                preferences[HAS_GOAL_KEY] = "false"
                preferences.remove(GOAL_KEY)
            }
        }
    }
    
    suspend fun clearActiveSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(COUNT_KEY)
            // Do not remove SECTION_KEY to remember the last selected section
            preferences.remove(HAS_GOAL_KEY)
            preferences.remove(GOAL_KEY)
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}

data class ActiveSessionData(
    val currentCount: Int = 0,
    val selectedSection: String = "VARC",
    val goal: Int? = null,
    val hintSeen: Boolean = false,
    val theme: String = "PASTEL"
)
