package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    HabitlyScreen(
        title = task?.title ?: "Task detail",
        subtitle = if (task == null) {
            "This task could not be found."
        } else {
            "Focus history and progress for this study task."
        },
        modifier = modifier
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back to tasks"
            )
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
                horizontalArrangement = Arrangement.SpaceBetween,
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
}

@Composable
private fun DetailMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    HabitlyCard(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
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
private fun TaskSessionHistory(
    sessions: List<TaskSessionSummary>,
    modifier: Modifier = Modifier
) {
    HabitlyCard(modifier = modifier) {
        Text(
            text = "Focus history",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (sessions.isEmpty()) {
            Text(
                text = "Linked timer sessions will appear here.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatSessionDate(session.completedAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
        TaskPriority.LOW -> Color(0xFFE7F1FF)
        TaskPriority.MEDIUM -> Color(0xFFFFF1CC)
        TaskPriority.HIGH -> Color(0xFFFFE2E0)
    }

private val TaskPriority.contentColor: Color
    @Composable
    get() = when (this) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
        TaskPriority.MEDIUM -> Color(0xFF8A5A00)
        TaskPriority.HIGH -> Color(0xFFD92D20)
    }
