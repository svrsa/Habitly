package com.example.habitly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_plans",
    foreignKeys = [
        ForeignKey(
            entity = StudyTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("plannedDate")]
)
data class StudyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val plannedDate: Long,
    val blockDurationMinutes: Int,
    val plannedBlocks: Int,
    val completedBlocks: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
