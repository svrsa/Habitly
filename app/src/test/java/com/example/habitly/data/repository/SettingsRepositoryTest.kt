package com.example.habitly.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.habitly.ui.settings.SettingsUiState
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun settingsUseDefaultValuesWhenNothingWasSaved() = runBlocking {
        val repository = createRepository()

        val settings = repository.settings.first()

        assertEquals(SettingsUiState(), settings)
    }

    @Test
    fun selectedFocusDurationIsPersisted() = runBlocking {
        val repository = createRepository()

        repository.selectDefaultFocusDuration(45)

        assertEquals(45, repository.settings.first().defaultFocusDurationMinutes)
    }

    @Test
    fun dailyStudyGoalIsPersisted() = runBlocking {
        val repository = createRepository()

        repository.setDailyStudyGoal(180)

        assertEquals(180, repository.settings.first().dailyStudyGoalMinutes)
    }

    @Test
    fun reminderSettingsArePersisted() = runBlocking {
        val repository = createRepository()

        repository.setDailyReminderEnabled(true)
        repository.setDailyReminderTime(hour = 7, minute = 30)
        val settings = repository.settings.first()

        assertEquals(true, settings.isDailyReminderEnabled)
        assertEquals(7, settings.reminderHour)
        assertEquals(30, settings.reminderMinute)
    }

    @Test
    fun focusDurationMustBePositive() {
        val repository = createRepository()

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.selectDefaultFocusDuration(0)
            }
        }
    }

    @Test
    fun dailyStudyGoalMustUseSupportedRangeAndStep() {
        val repository = createRepository()

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.setDailyStudyGoal(20)
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.setDailyStudyGoal(95)
            }
        }
    }

    @Test
    fun reminderTimeMustUseValidClockValues() {
        val repository = createRepository()

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.setDailyReminderTime(hour = 24, minute = 0)
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.setDailyReminderTime(hour = 10, minute = 60)
            }
        }
    }

    private fun createRepository(): SettingsRepository {
        val file = File(
            temporaryFolder.root,
            "settings-${System.nanoTime()}.preferences_pb"
        )
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { file }
        )

        return SettingsRepository(dataStore)
    }
}
