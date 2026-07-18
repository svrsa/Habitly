package com.example.habitly.ui.timer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TimerSessionSavePolicyTest {
    @Test
    fun completedSessionUsesSelectedDuration() {
        val result = TimerSessionSavePolicy.completedSession(
            state = TimerUiState(selectedDurationMinutes = 25),
            selectedTaskId = null
        )

        assertEquals(25, result.durationMinutes)
    }

    @Test
    fun completedFreeSessionKeepsSelectedTask() {
        val result = TimerSessionSavePolicy.completedSession(
            state = TimerUiState(selectedDurationMinutes = 25, activePlanId = null),
            selectedTaskId = 42L
        )

        assertEquals(42L, result.taskId)
        assertNull(result.planEntryId)
    }

    @Test
    fun completedPlannedSessionKeepsPlanAndIgnoresSelectedTask() {
        val result = TimerSessionSavePolicy.completedSession(
            state = TimerUiState(selectedDurationMinutes = 30, activePlanId = 7L),
            selectedTaskId = 42L
        )

        assertEquals(7L, result.planEntryId)
        assertNull(result.taskId)
    }

    @Test
    fun partialSessionIsNotSavedBeforeOneMinute() {
        val result = TimerSessionSavePolicy.partialSession(
            state = TimerUiState(
                selectedDurationMinutes = 25,
                remainingSeconds = 25 * 60 - 59
            ),
            selectedTaskId = 42L
        )

        assertNull(result)
    }

    @Test
    fun partialSessionRoundsElapsedSecondsUpToMinutes() {
        val result = TimerSessionSavePolicy.partialSession(
            state = TimerUiState(
                selectedDurationMinutes = 25,
                remainingSeconds = 25 * 60 - 61
            ),
            selectedTaskId = 42L
        )

        assertEquals(2, result?.durationMinutes)
        assertEquals(42L, result?.taskId)
    }

    @Test
    fun partialSessionIsNotSavedTwice() {
        val result = TimerSessionSavePolicy.partialSession(
            state = TimerUiState(
                selectedDurationMinutes = 25,
                remainingSeconds = 25 * 60 - 120,
                wasSessionSaved = true
            ),
            selectedTaskId = 42L
        )

        assertNull(result)
    }

    @Test
    fun partialPlannedSessionKeepsPlanAndIgnoresSelectedTask() {
        val result = TimerSessionSavePolicy.partialSession(
            state = TimerUiState(
                selectedDurationMinutes = 25,
                remainingSeconds = 25 * 60 - 120,
                activePlanId = 7L
            ),
            selectedTaskId = 42L
        )

        assertEquals(2, result?.durationMinutes)
        assertEquals(7L, result?.planEntryId)
        assertNull(result?.taskId)
    }
}
