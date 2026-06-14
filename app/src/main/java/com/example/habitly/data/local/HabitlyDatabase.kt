package com.example.habitly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habitly.data.local.dao.StudySessionDao
import com.example.habitly.data.local.dao.StudyTaskDao
import com.example.habitly.data.local.entity.StudySessionEntity
import com.example.habitly.data.local.entity.StudyTaskEntity

@Database(
    entities = [
        StudyTaskEntity::class,
        StudySessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HabitlyDatabase : RoomDatabase() {
    abstract fun studyTaskDao(): StudyTaskDao

    abstract fun studySessionDao(): StudySessionDao
}
