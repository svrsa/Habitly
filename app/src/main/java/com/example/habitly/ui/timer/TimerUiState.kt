package com.example.habitly.ui.timer

data class TimerUiState(
    val selectedDurationMinutes: Int = 25,
    val remainingSeconds: Int = selectedDurationMinutes * 60,
    val isRunning: Boolean = false
)
