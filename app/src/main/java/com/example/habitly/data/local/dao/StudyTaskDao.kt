package com.example.habitly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habitly.data.local.entity.StudyTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert
    suspend fun insertTask(task: StudyTaskEntity)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)
}
