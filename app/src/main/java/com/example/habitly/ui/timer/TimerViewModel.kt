package com.example.habitly.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.SettingsRepository
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.data.repository.StudyTaskRepository
import com.example.habitly.ui.planner.PlannedFocusRequest
import com.example.habitly.ui.settings.SettingsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(
    private val sessionRepository: StudySessionRepository,
    private val planRepository: StudyPlanRepository,
    taskRepository: StudyTaskRepository,
    settingsRepository: SettingsRepository,
    plannedFocusRequest: PlannedFocusRequest?
) : ViewModel() {
    private val initialDurationMinutes = plannedFocusRequest?.blockDurationMinutes
        ?: SettingsUiState.DEFAULT_FOCUS_DURATION_MINUTES
    private val timerState = MutableStateFlow(
        TimerUiState(
            selectedDurationMinutes = initialDurationMinutes,
            remainingSeconds = initialDurationMinutes * 60,
            activePlanId = plannedFocusRequest?.planId,
            activeTaskTitle = plannedFocusRequest?.taskTitle
        )
    )
    val uiState: StateFlow<TimerUiState> = combine(
        timerState,
        taskRepository.allTasks
    ) { state, tasks ->
        val availableTasks = tasks.filterNot { task -> task.isCompleted }
        val selectedTaskId = state.selectedTaskId
            ?.takeIf { id -> availableTasks.any { task -> task.id == id } }

        state.copy(
            selectedTaskId = selectedTaskId,
            availableTasks = availableTasks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = timerState.value
    )

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings
                .map { settings -> settings.defaultFocusDurationMinutes }
                .distinctUntilChanged()
                .collect { minutes ->
                    timerState.update { state ->
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
        if (timerState.value.isRunning) {
            return
        }

        timerState.update { state ->
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
            while (timerState.value.isRunning && timerState.value.remainingSeconds > 0) {
                delay(1_000)
                timerState.update { state ->
                    if (state.isRunning) {
                        state.copy(remainingSeconds = state.remainingSeconds - 1)
                    } else {
                        state
                    }
                }
            }

            if (timerState.value.remainingSeconds == 0) {
                saveCompletedSession()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        timerState.update { state ->
            state.copy(isRunning = false)
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null

        viewModelScope.launch {
            val savedPartialSession = saveCurrentProgressSession()

            timerState.update { state ->
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

            timerState.update { state ->
                state.copy(
                    selectedDurationMinutes = minutes,
                    remainingSeconds = minutes * 60,
                    isRunning = false,
                    wasSessionSaved = false
                )
            }
        }
    }

    fun selectTask(taskId: Long?) {
        timerState.update { state ->
            if (state.isRunning || state.activePlanId != null) {
                state
            } else {
                state.copy(
                    selectedTaskId = taskId,
                    wasSessionSaved = false,
                    lastSavedSessionId = null
                )
            }
        }
    }

    private suspend fun saveCompletedSession() {
        val state = timerState.value
        val durationMinutes = state.selectedDurationMinutes

        timerJob = null
        timerState.update { currentState ->
            currentState.copy(isRunning = false)
        }

        val activePlanId = state.activePlanId
        val sessionTaskId = if (activePlanId == null) {
            uiState.value.selectedTaskId
        } else {
            null
        }
        val sessionId = sessionRepository.addSession(
            durationMinutes = durationMinutes,
            planEntryId = activePlanId,
            taskId = sessionTaskId
        )
        if (activePlanId != null) {
            planRepository.completeNextBlock(activePlanId)
        }
        timerState.update { currentState ->
            currentState.copy(wasSessionSaved = true, lastSavedSessionId = sessionId)
        }
    }

    private suspend fun saveCurrentProgressSession(): Boolean {
        val state = timerState.value
        val elapsedSeconds = state.selectedDurationMinutes * 60 - state.remainingSeconds

        if (elapsedSeconds < MINIMUM_PARTIAL_SESSION_SECONDS || state.wasSessionSaved) {
            return false
        }

        val elapsedMinutes = (elapsedSeconds + 59) / 60
        val sessionTaskId = if (state.activePlanId == null) {
            uiState.value.selectedTaskId
        } else {
            null
        }
        sessionRepository.addSession(
            durationMinutes = elapsedMinutes,
            planEntryId = state.activePlanId,
            taskId = sessionTaskId
        )
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
