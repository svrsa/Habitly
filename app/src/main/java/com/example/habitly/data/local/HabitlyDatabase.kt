package com.example.habitly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habitly.data.local.dao.StudySessionDao
import com.example.habitly.data.local.dao.StudyPlanDao
import com.example.habitly.data.local.dao.StudyTaskDao
import com.example.habitly.data.local.dao.StudyEvidenceDao
import com.example.habitly.data.local.entity.StudySessionEntity
import com.example.habitly.data.local.entity.StudyPlanEntity
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.StudyEvidenceEntity

@Database(
    entities = [
        StudyTaskEntity::class,
        StudySessionEntity::class,
        StudyPlanEntity::class,
        StudyEvidenceEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class HabitlyDatabase : RoomDatabase() {
    abstract fun studyTaskDao(): StudyTaskDao

    abstract fun studySessionDao(): StudySessionDao

    abstract fun studyPlanDao(): StudyPlanDao

    abstract fun studyEvidenceDao(): StudyEvidenceDao
}
