package com.example.habitly

import android.app.Application
import androidx.room.Room
import com.example.habitly.data.local.HABITLY_DATABASE_NAME
import com.example.habitly.data.local.HabitlyDatabase
import com.example.habitly.data.local.MIGRATION_1_2
import com.example.habitly.data.local.MIGRATION_2_3
import com.example.habitly.data.local.MIGRATION_3_4
import com.example.habitly.data.repository.SettingsRepository
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.data.repository.StudyTaskRepository
import com.example.habitly.notifications.StudyReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HabitlyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: HabitlyDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            HabitlyDatabase::class.java,
            HABITLY_DATABASE_NAME
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }

    val studyTaskRepository: StudyTaskRepository by lazy {
        StudyTaskRepository(database.studyTaskDao())
    }

    val studySessionRepository: StudySessionRepository by lazy {
        StudySessionRepository(database.studySessionDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(applicationContext)
    }

    val studyPlanRepository: StudyPlanRepository by lazy {
        StudyPlanRepository(database.studyPlanDao())
    }

    val studyReminderManager: StudyReminderManager by lazy {
        StudyReminderManager(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        studyReminderManager.createNotificationChannel()

        applicationScope.launch {
            settingsRepository.settings
                .map { settings ->
                    Triple(
                        settings.isDailyReminderEnabled,
                        settings.reminderHour,
                        settings.reminderMinute
                    )
                }
                .distinctUntilChanged()
                .collect { (isEnabled, hour, minute) ->
                    if (isEnabled) {
                        studyReminderManager.scheduleDailyReminder(
                            hour = hour,
                            minute = minute
                        )
                    } else {
                        studyReminderManager.cancelDailyReminder()
                    }
                }
        }
    }
}
