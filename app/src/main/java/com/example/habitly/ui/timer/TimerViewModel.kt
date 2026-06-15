package com.example.habitly.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.StudySessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(
    private val repository: StudySessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState

    private var timerJob: Job? = null

    fun startTimer() {
        if (_uiState.value.isRunning) {
            return
        }

        _uiState.update { state ->
            val remainingSeconds = if (state.remainingSeconds == 0) {
                state.selectedDurationMinutes * 60
            } else {
                state.remainingSeconds
            }

            state.copy(
                remainingSeconds = remainingSeconds,
                wasSessionSaved = false,
                isRunning = true
            )
        }

        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.remainingSeconds > 0) {
                delay(1_000)
                _uiState.update { state ->
                    if (state.isRunning) {
                        state.copy(remainingSeconds = state.remainingSeconds - 1)
                    } else {
                        state
                    }
                }
            }

            if (_uiState.value.remainingSeconds == 0) {
                saveCompletedSession()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { state ->
            state.copy(isRunning = false)
        }
    }

    fun resetTimer() {
        pauseTimer()
        _uiState.update { state ->
            state.copy(
                remainingSeconds = state.selectedDurationMinutes * 60,
                wasSessionSaved = false
            )
        }
    }

    fun selectDuration(minutes: Int) {
        pauseTimer()
        _uiState.update { state ->
            state.copy(
                selectedDurationMinutes = minutes,
                remainingSeconds = minutes * 60,
                wasSessionSaved = false
            )
        }
    }

    private suspend fun saveCompletedSession() {
        val durationMinutes = _uiState.value.selectedDurationMinutes

        timerJob = null
        _uiState.update { state ->
            state.copy(isRunning = false)
        }

        repository.addSession(durationMinutes)
        _uiState.update { state ->
            state.copy(wasSessionSaved = true)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
