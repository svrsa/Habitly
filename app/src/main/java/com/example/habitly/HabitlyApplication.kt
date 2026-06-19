package com.example.habitly

import android.app.Application
import androidx.room.Room
import com.example.habitly.data.local.HABITLY_DATABASE_NAME
import com.example.habitly.data.local.HabitlyDatabase
import com.example.habitly.data.local.MIGRATION_1_2
import com.example.habitly.data.local.MIGRATION_2_3
import com.example.habitly.data.repository.SettingsRepository
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository

class HabitlyApplication : Application() {
    val database: HabitlyDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            HabitlyDatabase::class.java,
            HABITLY_DATABASE_NAME
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
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
}
