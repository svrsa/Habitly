package com.example.habitly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.habitly.data.local.entity.StudyEvidenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyEvidenceDao {
    @Query("SELECT * FROM study_evidence ORDER BY createdAt DESC")
    fun getAllEvidence(): Flow<List<StudyEvidenceEntity>>

    @Query("SELECT * FROM study_evidence WHERE sessionId = :sessionId")
    suspend fun getEvidenceForSession(sessionId: Long): List<StudyEvidenceEntity>

    @Insert
    suspend fun insertEvidence(evidence: StudyEvidenceEntity): Long

    @Delete
    suspend fun deleteEvidence(evidence: StudyEvidenceEntity)
}
