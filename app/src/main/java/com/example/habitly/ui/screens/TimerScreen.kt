package com.example.habitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.timer.TimerViewModel
import com.example.habitly.ui.timer.TimerViewModelFactory
import com.example.habitly.ui.planner.PlannedFocusRequest

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    plannedFocusRequest: PlannedFocusRequest? = null,
    onAddEvidence: (Long) -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TimerViewModel = viewModel(
        key = "timer-${plannedFocusRequest?.planId ?: "free"}",
        factory = TimerViewModelFactory(
            sessionRepository = application.studySessionRepository,
            planRepository = application.studyPlanRepository,
            taskRepository = application.studyTaskRepository,
            settingsRepository = application.settingsRepository,
            plannedFocusRequest = plannedFocusRequest
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val minutes = uiState.remainingSeconds / 60
    val seconds = uiState.remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)
    val durations = listOf(15, 25, 45)
    val totalSeconds = uiState.selectedDurationMinutes * 60
    val progress = (1f - (uiState.remainingSeconds.toFloat() / totalSeconds)).coerceIn(0f, 1f)
    val selectedTask = uiState.availableTasks.firstOrNull { task ->
        task.id == uiState.selectedTaskId
    }
    var taskMenuExpanded by remember {
        mutableStateOf(false)
    }
    val statusText = when {
        uiState.wasSessionSaved -> "Focus session saved"
        uiState.remainingSeconds == 0 -> "Focus session complete"
        uiState.isRunning -> "Focus time is running"
        else -> "Ready to focus"
    }

    HabitlyScreen(
        title = "Focus timer",
        subtitle = statusText,
        modifier = modifier
    ) {
        uiState.lastSavedSessionId?.let { sessionId ->
            HabitlyCard {
                Text("Session complete", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Capture your notes or solved exercises as a study snapshot.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { onAddEvidence(sessionId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add study snapshot")
                }
            }
        }

        uiState.activeTaskTitle?.let { taskTitle ->
            HabitlyCard {
                Text("Planned focus", style = MaterialTheme.typography.labelLarge)
                Text(taskTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Completing this timer finishes one planned block.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HabitlyCard(
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FocusTimerDisplay(
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
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (uiState.isRunning) "Pause" else "Start focus",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = viewModel::resetTimer,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Text(text = "Reset")
                    }
                }

                if (uiState.activePlanId == null) {
                    TimerTaskSelector(
                        selectedTaskTitle = selectedTask?.title,
                        availableTasks = uiState.availableTasks,
                        expanded = taskMenuExpanded,
                        enabled = !uiState.isRunning,
                        onExpandedChange = { taskMenuExpanded = it },
                        onTaskSelected = { taskId ->
                            viewModel.selectTask(taskId)
                            taskMenuExpanded = false
                        }
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
    }
}

@Composable
private fun FocusTimerDisplay(
    timerText: String,
    progress: Float,
    durationText: String,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = progressColor.copy(alpha = 0.14f),
            contentColor = progressColor
        ) {
            Text(
                text = "FOCUS BLOCK",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
                    shape = MaterialTheme.shapes.extraLarge
                )
        ) {
            val safeProgress = progress.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(safeProgress)
                    .height(18.dp)
                    .background(
                        color = progressColor.copy(alpha = 0.22f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(safeProgress)
                    .height(10.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        color = progressColor,
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
            if (safeProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(safeProgress)
                        .height(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 5.dp)
                            .size(12.dp)
                            .background(
                                color = Color.White,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerTaskSelector(
    selectedTaskTitle: String?,
    availableTasks: List<StudyTaskEntity>,
    expanded: Boolean,
    enabled: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTaskSelected: (Long?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Linked task",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { onExpandedChange(true) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedTaskTitle ?: "No task selected")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No task",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { onTaskSelected(null) }
                )
                if (availableTasks.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Create an open task first",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = { onExpandedChange(false) },
                        enabled = false
                    )
                }
                availableTasks.forEach { task ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = task.title,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { onTaskSelected(task.id) }
                    )
                }
            }
        }
    }
}
