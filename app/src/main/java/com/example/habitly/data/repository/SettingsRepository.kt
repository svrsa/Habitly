package com.example.habitly.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.habitly.ui.settings.SettingsUiState
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATA_STORE_NAME = "study_streak_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATA_STORE_NAME
)

class SettingsRepository(
    context: Context
) {
    private val dataStore = context.settingsDataStore

    val settings: Flow<SettingsUiState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SettingsUiState(
                defaultFocusDurationMinutes =
                    preferences[DEFAULT_FOCUS_DURATION_KEY]
                        ?: SettingsUiState.DEFAULT_FOCUS_DURATION_MINUTES,
                isDailyReminderEnabled =
                    preferences[DAILY_REMINDER_ENABLED_KEY] ?: false
            )
        }

    suspend fun selectDefaultFocusDuration(minutes: Int) {
        require(minutes > 0) { "Focus duration must be positive" }

        dataStore.edit { preferences ->
            preferences[DEFAULT_FOCUS_DURATION_KEY] = minutes
        }
    }

    suspend fun setDailyReminderEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_ENABLED_KEY] = isEnabled
        }
    }

    private companion object {
        val DEFAULT_FOCUS_DURATION_KEY = intPreferencesKey("default_focus_duration_minutes")
        val DAILY_REMINDER_ENABLED_KEY = booleanPreferencesKey("daily_reminder_enabled")
    }
}
