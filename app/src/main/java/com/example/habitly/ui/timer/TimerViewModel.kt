package com.example.habitly.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.SettingsRepository
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.ui.planner.PlannedFocusRequest
import com.example.habitly.ui.settings.SettingsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(
    private val sessionRepository: StudySessionRepository,
    private val planRepository: StudyPlanRepository,
    settingsRepository: SettingsRepository,
    plannedFocusRequest: PlannedFocusRequest?
) : ViewModel() {
    private val initialDurationMinutes = plannedFocusRequest?.blockDurationMinutes
        ?: SettingsUiState.DEFAULT_FOCUS_DURATION_MINUTES
    private val _uiState = MutableStateFlow(
        TimerUiState(
            selectedDurationMinutes = initialDurationMinutes,
            remainingSeconds = initialDurationMinutes * 60,
            activePlanId = plannedFocusRequest?.planId,
            activeTaskTitle = plannedFocusRequest?.taskTitle
        )
    )
    val uiState: StateFlow<TimerUiState> = _uiState

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings
                .map { settings -> settings.defaultFocusDurationMinutes }
                .distinctUntilChanged()
                .collect { minutes ->
                    _uiState.update { state ->
                        if (state.isRunning || state.activePlanId != null) {
                            state
                        } else {
                            state.copy(
                                selectedDurationMinutes = minutes,
                                remainingSeconds = minutes * 60,
                                wasSessionSaved = false
                            )
                        }
                    }
                }
        }
    }

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
        timerJob?.cancel()
        timerJob = null

        viewModelScope.launch {
            val savedPartialSession = saveCurrentProgressSession()

            _uiState.update { state ->
                state.copy(
                    remainingSeconds = state.selectedDurationMinutes * 60,
                    isRunning = false,
                    wasSessionSaved = savedPartialSession
                )
            }
        }
    }

    fun selectDuration(minutes: Int) {
        timerJob?.cancel()
        timerJob = null

        viewModelScope.launch {
            saveCurrentProgressSession()

            _uiState.update { state ->
                state.copy(
                    selectedDurationMinutes = minutes,
                    remainingSeconds = minutes * 60,
                    isRunning = false,
                    wasSessionSaved = false
                )
            }
        }
    }

    private suspend fun saveCompletedSession() {
        val durationMinutes = _uiState.value.selectedDurationMinutes

        timerJob = null
        _uiState.update { state ->
            state.copy(isRunning = false)
        }

        val activePlanId = _uiState.value.activePlanId
        val sessionId = sessionRepository.addSession(durationMinutes, activePlanId)
        if (activePlanId != null) {
            planRepository.completeNextBlock(activePlanId)
        }
        _uiState.update { state ->
            state.copy(wasSessionSaved = true, lastSavedSessionId = sessionId)
        }
    }

    private suspend fun saveCurrentProgressSession(): Boolean {
        val state = _uiState.value
        val elapsedSeconds = state.selectedDurationMinutes * 60 - state.remainingSeconds

        if (elapsedSeconds < MINIMUM_PARTIAL_SESSION_SECONDS || state.wasSessionSaved) {
            return false
        }

        val elapsedMinutes = (elapsedSeconds + 59) / 60
        sessionRepository.addSession(elapsedMinutes, state.activePlanId)
        return true
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val MINIMUM_PARTIAL_SESSION_SECONDS = 60
    }
}
