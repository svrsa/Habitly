package com.example.habitly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val planEntryId: Long? = null,
    val taskId: Long? = null,
    val completedAt: Long = System.currentTimeMillis()
)
