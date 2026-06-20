package com.example.habitly.ui.evidence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.local.entity.StudyEvidenceEntity
import com.example.habitly.data.repository.StudyEvidenceRepository
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvidenceViewModel(
    private val repository: StudyEvidenceRepository
) : ViewModel() {
    private val operationState = MutableStateFlow(EvidenceOperationState())

    val uiState: StateFlow<EvidenceUiState> = combine(
        repository.allEvidence,
        operationState
    ) { evidence, operation ->
        EvidenceUiState(
            evidence = evidence,
            isSaving = operation.isSaving,
            errorMessage = operation.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EvidenceUiState()
    )

    fun createCaptureFile(): File = repository.createCaptureFile()

    fun saveCapture(sessionId: Long, file: File, onSaved: () -> Unit) {
        operationState.update { state -> state.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { repository.saveEvidence(sessionId, file) }
                .onSuccess {
                    operationState.update { state -> state.copy(isSaving = false) }
                    onSaved()
                }
                .onFailure { error ->
                    file.delete()
                    operationState.update { state ->
                        state.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Unable to save photo"
                        )
                    }
                }
        }
    }

    fun deleteEvidence(evidence: StudyEvidenceEntity) {
        viewModelScope.launch { repository.deleteEvidence(evidence) }
    }

    fun clearError() {
        operationState.update { state -> state.copy(errorMessage = null) }
    }

    private data class EvidenceOperationState(
        val isSaving: Boolean = false,
        val errorMessage: String? = null
    )
}
