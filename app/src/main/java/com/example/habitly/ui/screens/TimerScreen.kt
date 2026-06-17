package com.example.habitly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                FocusTimerRing(
                    timerText = timerText,
                    progress = progress,
                    durationText = "${uiState.selectedDurationMinutes} min"
                )

                Text(
                    text = if (uiState.isRunning) {
                        "Stay with one task until the session ends."
                    } else {
                        "Choose a duration and begin a focused study block."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                            text = if (uiState.isRunning) "Pause" else "Start focus"
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
    }
}

@Composable
private fun FocusTimerRing(
    timerText: String,
    progress: Float,
    durationText: String,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.primaryContainer
    val progressColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(236.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(236.dp)
        ) {
            val strokeWidth = 14.dp.toPx()
            drawCircle(
                color = trackColor,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = timerText,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = durationText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
