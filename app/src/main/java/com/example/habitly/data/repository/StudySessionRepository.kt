package com.example.habitly.data.repository

import com.example.habitly.data.local.dao.StudySessionDao
import com.example.habitly.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

class StudySessionRepository(
    private val studySessionDao: StudySessionDao
) {
    val allSessions: Flow<List<StudySessionEntity>> = studySessionDao.getAllSessions()

    suspend fun addSession(
        durationMinutes: Int,
        planEntryId: Long? = null,
        taskId: Long? = null
    ): Long {
        val session = StudySessionEntity(
            durationMinutes = durationMinutes,
            planEntryId = planEntryId,
            taskId = taskId
        )

        return studySessionDao.insertSession(session)
    }

    suspend fun deleteSession(sessionId: Long) {
        studySessionDao.deleteSession(sessionId)
    }
}
