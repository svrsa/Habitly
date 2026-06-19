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
                dailyStudyGoalMinutes = preferences[DAILY_STUDY_GOAL_KEY]
                    ?: SettingsUiState.DEFAULT_DAILY_STUDY_GOAL_MINUTES,
                isDailyReminderEnabled =
                    preferences[DAILY_REMINDER_ENABLED_KEY] ?: false,
                reminderHour = preferences[REMINDER_HOUR_KEY]
                    ?: SettingsUiState.DEFAULT_REMINDER_HOUR,
                reminderMinute = preferences[REMINDER_MINUTE_KEY]
                    ?: SettingsUiState.DEFAULT_REMINDER_MINUTE
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

    suspend fun setDailyStudyGoal(minutes: Int) {
        require(
            minutes in SettingsUiState.MIN_DAILY_STUDY_GOAL_MINUTES..
                SettingsUiState.MAX_DAILY_STUDY_GOAL_MINUTES
        ) { "Daily study goal is outside the supported range" }
        require(
            minutes % SettingsUiState.DAILY_STUDY_GOAL_STEP_MINUTES == 0
        ) { "Daily study goal must use 15-minute steps" }

        dataStore.edit { preferences ->
            preferences[DAILY_STUDY_GOAL_KEY] = minutes
        }
    }

    suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        require(hour in 0..23) { "Reminder hour must be between 0 and 23" }
        require(minute in 0..59) { "Reminder minute must be between 0 and 59" }

        dataStore.edit { preferences ->
            preferences[REMINDER_HOUR_KEY] = hour
            preferences[REMINDER_MINUTE_KEY] = minute
        }
    }

    private companion object {
        val DEFAULT_FOCUS_DURATION_KEY = intPreferencesKey("default_focus_duration_minutes")
        val DAILY_STUDY_GOAL_KEY = intPreferencesKey("daily_study_goal_minutes")
        val DAILY_REMINDER_ENABLED_KEY = booleanPreferencesKey("daily_reminder_enabled")
        val REMINDER_HOUR_KEY = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE_KEY = intPreferencesKey("reminder_minute")
    }
}
