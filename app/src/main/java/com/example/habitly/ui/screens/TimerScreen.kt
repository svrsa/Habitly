package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.timer.TimerViewModel
import com.example.habitly.ui.timer.TimerViewModelFactory

@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(application.studySessionRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val minutes = uiState.remainingSeconds / 60
    val seconds = uiState.remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)
    val durations = listOf(15, 25, 45)
    val totalSeconds = uiState.selectedDurationMinutes * 60
    val progress = 1f - (uiState.remainingSeconds.toFloat() / totalSeconds)
    val statusText = when {
        uiState.remainingSeconds == 0 -> "Focus session complete"
        uiState.isRunning -> "Focus time is running"
        else -> "Ready to focus"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Timer",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = timerText,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            durations.forEach { duration ->
                FilterChip(
                    selected = uiState.selectedDurationMinutes == duration,
                    onClick = { viewModel.selectDuration(duration) },
                    label = {
                        Text(text = "$duration min")
                    },
                    enabled = !uiState.isRunning
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (uiState.isRunning) {
                        viewModel.pauseTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (uiState.isRunning) "Pause" else "Start"
                )
            }

            OutlinedButton(
                onClick = viewModel::resetTimer,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Reset")
            }
        }
    }
}
