package com.example.habitly.data.repository

import android.content.Context
import java.io.File
import java.util.UUID

class EvidenceFileStore(context: Context) {
    private val evidenceDirectory = File(context.filesDir, DIRECTORY_NAME)

    fun createImageFile(): File {
        if (!evidenceDirectory.exists()) {
            check(evidenceDirectory.mkdirs()) { "Unable to create evidence directory" }
        }
        return File(evidenceDirectory, "evidence_${UUID.randomUUID()}.jpg")
    }

    fun deleteImage(path: String) {
        File(path).takeIf { file -> file.exists() }?.delete()
    }

    private companion object {
        const val DIRECTORY_NAME = "study_evidence"
    }
}
