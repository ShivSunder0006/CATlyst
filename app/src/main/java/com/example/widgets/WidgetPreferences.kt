package com.example.widgets

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(name = "premium_widget_prefs")

data class WidgetConfig(
    val theme: String = "Follow App",
    val cornerRadius: Int = 16,
    val showLabels: Boolean = true,
    val isCompact: Boolean = false
)

class WidgetPreferences(private val context: Context) {
    fun getConfigFlow(widgetId: Int): Flow<WidgetConfig> {
        return context.widgetDataStore.data.map { prefs ->
            WidgetConfig(
                theme = prefs[stringPreferencesKey("theme_$widgetId")] ?: "Follow App",
                cornerRadius = prefs[intPreferencesKey("corner_$widgetId")] ?: 16,
                showLabels = prefs[booleanPreferencesKey("labels_$widgetId")] ?: true,
                isCompact = prefs[booleanPreferencesKey("compact_$widgetId")] ?: false
            )
        }
    }
    
    suspend fun saveConfig(widgetId: Int, config: WidgetConfig) {
        context.widgetDataStore.edit { prefs ->
            prefs[stringPreferencesKey("theme_$widgetId")] = config.theme
            prefs[intPreferencesKey("corner_$widgetId")] = config.cornerRadius
            prefs[booleanPreferencesKey("labels_$widgetId")] = config.showLabels
            prefs[booleanPreferencesKey("compact_$widgetId")] = config.isCompact
        }
    }
}
