package com.example.habitly

import android.app.Application
import androidx.room.Room
import com.example.habitly.data.local.HABITLY_DATABASE_NAME
import com.example.habitly.data.local.HabitlyDatabase
import com.example.habitly.data.repository.StudyTaskRepository

class HabitlyApplication : Application() {
    val database: HabitlyDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            HabitlyDatabase::class.java,
            HABITLY_DATABASE_NAME
        ).build()
    }

    val studyTaskRepository: StudyTaskRepository by lazy {
        StudyTaskRepository(database.studyTaskDao())
    }
}
