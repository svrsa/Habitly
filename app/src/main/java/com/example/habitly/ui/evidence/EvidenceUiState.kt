package com.example.habitly.ui.evidence

import com.example.habitly.data.local.entity.StudyEvidenceEntity

data class EvidenceUiState(
    val evidence: List<StudyEvidenceEntity> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
