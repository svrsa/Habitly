package com.example.habitly.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.TaskPriority
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.format.formatFocusDuration
import com.example.habitly.ui.taskdetail.TaskDetailViewModel
import com.example.habitly.ui.taskdetail.TaskDetailViewModelFactory
import com.example.habitly.ui.taskdetail.TaskSessionSummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskDetailScreen(
    taskId: Long,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TaskDetailViewModel = viewModel(
        key = "task-detail-$taskId",
        factory = TaskDetailViewModelFactory(
            taskId = taskId,
            taskRepository = application.studyTaskRepository,
            sessionRepository = application.studySessionRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val task = uiState.task
    var showEditDialog by remember(taskId) {
        mutableStateOf(false)
    }

    HabitlyScreen(
        title = task?.title ?: "Task detail",
        subtitle = if (task == null) {
            "This task could not be found."
        } else {
            "Focus history and progress for this study task."
        },
        modifier = modifier
    ) {
        Surface(
            onClick = onBack,
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (task == null) {
            HabitlyCard {
                Text(
                    text = "Task not found",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Go back to the task list and choose another task.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@HabitlyScreen
        }

        HabitlyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityChip(priority = task.priority)
                        StatusChip(isCompleted = task.isCompleted)
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) {
                            Icons.Outlined.CheckCircle
                        } else {
                            Icons.Outlined.Schedule
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(28.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleTaskCompleted(task) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (task.isCompleted) "Mark open" else "Mark done")
                }
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "Edit")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailMetricCard(
                title = "Focus time",
                value = formatFocusDuration(uiState.totalFocusMinutes),
                icon = Icons.Outlined.Schedule,
                modifier = Modifier.weight(1f)
            )
            DetailMetricCard(
                title = "Sessions",
                value = "${uiState.sessionCount}",
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        TaskSessionHistory(sessions = uiState.sessions)
    }

    if (showEditDialog && task != null) {
        EditTaskDetailDialog(
            taskTitle = task.title,
            taskPriority = task.priority,
            onDismiss = { showEditDialog = false },
            onSave = { title, priority ->
                viewModel.updateTaskDetails(
                    task = task,
                    title = title,
                    priority = priority
                )
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun DetailMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    HabitlyCard(modifier = modifier) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EditTaskDetailDialog(
    taskTitle: String,
    taskPriority: TaskPriority,
    onDismiss: () -> Unit,
    onSave: (String, TaskPriority) -> Unit
) {
    var title by remember(taskTitle) {
        mutableStateOf(taskTitle)
    }
    var priority by remember(taskPriority) {
        mutableStateOf(taskPriority)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit task")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Task title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.entries.forEach { option ->
                        FilterChip(
                            selected = priority == option,
                            onClick = { priority = option },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = option.containerColor,
                                selectedLabelColor = option.contentColor,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = priority == option,
                                borderColor = MaterialTheme.colorScheme.surfaceVariant,
                                selectedBorderColor = option.containerColor
                            ),
                            label = {
                                Text(text = option.label)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, priority) },
                enabled = title.isNotBlank()
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
private fun TaskSessionHistory(
    sessions: List<TaskSessionSummary>,
    modifier: Modifier = Modifier
) {
    HabitlyCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Focus history",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (sessions.isNotEmpty()) {
                Text(
                    text = "${sessions.size} saved",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (sessions.isEmpty()) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "No focus history yet",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Start the timer with this task selected to see sessions here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            sessions.forEach { session ->
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Focus session",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = formatSessionDate(session.completedAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = formatFocusDuration(session.durationMinutes),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = priority.containerColor,
        contentColor = priority.contentColor
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun StatusChip(isCompleted: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isCompleted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (isCompleted) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Text(
            text = if (isCompleted) "Done" else "Open",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun formatSessionDate(completedAt: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.getDefault())
    return Instant.ofEpochMilli(completedAt)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private val TaskPriority.label: String
    get() = when (this) {
        TaskPriority.LOW -> "Low"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.HIGH -> "High"
    }

private val TaskPriority.containerColor: Color
    @Composable
    get() = when (this) {
        TaskPriority.LOW -> Color(0xFFE5F0FF)
        TaskPriority.MEDIUM -> Color(0xFFFFE9B8)
        TaskPriority.HIGH -> Color(0xFFFFD8D2)
    }

private val TaskPriority.contentColor: Color
    @Composable
    get() = when (this) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
        TaskPriority.MEDIUM -> Color(0xFF9A5B00)
        TaskPriority.HIGH -> Color(0xFFC6281D)
    }
