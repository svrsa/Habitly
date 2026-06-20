package com.example.habitly.data.repository

import com.example.habitly.data.local.dao.StudyEvidenceDao
import com.example.habitly.data.local.entity.StudyEvidenceEntity
import java.io.File
import kotlinx.coroutines.flow.Flow

class StudyEvidenceRepository(
    private val evidenceDao: StudyEvidenceDao,
    private val fileStore: EvidenceFileStore
) {
    val allEvidence: Flow<List<StudyEvidenceEntity>> = evidenceDao.getAllEvidence()

    fun createCaptureFile(): File = fileStore.createImageFile()

    suspend fun saveEvidence(sessionId: Long, imageFile: File, note: String = ""): Long {
        require(imageFile.exists()) { "Captured image does not exist" }
        return evidenceDao.insertEvidence(
            StudyEvidenceEntity(
                sessionId = sessionId,
                imagePath = imageFile.absolutePath,
                note = note.trim()
            )
        )
    }

    suspend fun deleteEvidence(evidence: StudyEvidenceEntity) {
        evidenceDao.deleteEvidence(evidence)
        fileStore.deleteImage(evidence.imagePath)
    }

    suspend fun deleteFilesForSession(sessionId: Long) {
        evidenceDao.getEvidenceForSession(sessionId).forEach { evidence ->
            fileStore.deleteImage(evidence.imagePath)
        }
    }
}
