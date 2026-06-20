package com.example.habitly.ui.evidence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitly.data.repository.StudyEvidenceRepository

class EvidenceViewModelFactory(
    private val repository: StudyEvidenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EvidenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EvidenceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
