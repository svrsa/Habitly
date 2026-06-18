package com.example.habitly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.habitly.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Insert
    suspend fun insertSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
