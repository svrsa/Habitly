package com.example.habitly.ui.timer

data class TimerSessionSaveRequest(
    val durationMinutes: Int,
    val planEntryId: Long?,
    val taskId: Long?
)

object TimerSessionSavePolicy {
    private const val MINIMUM_PARTIAL_SESSION_SECONDS = 60

    fun completedSession(
        state: TimerUiState,
        selectedTaskId: Long?
    ): TimerSessionSaveRequest {
        return TimerSessionSaveRequest(
            durationMinutes = state.selectedDurationMinutes,
            planEntryId = state.activePlanId,
            taskId = taskIdForSession(
                activePlanId = state.activePlanId,
                selectedTaskId = selectedTaskId
            )
        )
    }

    fun partialSession(
        state: TimerUiState,
        selectedTaskId: Long?
    ): TimerSessionSaveRequest? {
        val elapsedSeconds = state.selectedDurationMinutes * 60 - state.remainingSeconds

        if (elapsedSeconds < MINIMUM_PARTIAL_SESSION_SECONDS || state.wasSessionSaved) {
            return null
        }

        return TimerSessionSaveRequest(
            durationMinutes = (elapsedSeconds + 59) / 60,
            planEntryId = state.activePlanId,
            taskId = taskIdForSession(
                activePlanId = state.activePlanId,
                selectedTaskId = selectedTaskId
            )
        )
    }

    private fun taskIdForSession(
        activePlanId: Long?,
        selectedTaskId: Long?
    ): Long? {
        return if (activePlanId == null) selectedTaskId else null
    }
}
