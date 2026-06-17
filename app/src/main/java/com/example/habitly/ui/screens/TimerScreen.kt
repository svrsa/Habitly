package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.timer.TimerViewModel
import com.example.habitly.ui.timer.TimerViewModelFactory

@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(
            sessionRepository = application.studySessionRepository,
            settingsRepository = application.settingsRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val minutes = uiState.remainingSeconds / 60
    val seconds = uiState.remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)
    val durations = listOf(15, 25, 45)
    val totalSeconds = uiState.selectedDurationMinutes * 60
    val progress = (1f - (uiState.remainingSeconds.toFloat() / totalSeconds)).coerceIn(0f, 1f)
    val statusText = when {
        uiState.wasSessionSaved -> "Focus session saved"
        uiState.remainingSeconds == 0 -> "Focus session complete"
        uiState.isRunning -> "Focus time is running"
        else -> "Ready to focus"
    }

    HabitlyScreen(
        title = "Focus",
        subtitle = statusText,
        modifier = modifier
    ) {
        HabitlyCard(
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Current session",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = "${uiState.selectedDurationMinutes} minute focus block",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HabitlyCard {
            Text(
                text = "Duration",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.forEach { duration ->
                    FilterChip(
                        selected = uiState.selectedDurationMinutes == duration,
                        onClick = { viewModel.selectDuration(duration) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = !uiState.isRunning,
                            selected = uiState.selectedDurationMinutes == duration,
                            borderColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedBorderColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        label = {
                            Text(text = "$duration min")
                        },
                        enabled = !uiState.isRunning
                    )
                }
            }
        }

        HabitlyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
}
