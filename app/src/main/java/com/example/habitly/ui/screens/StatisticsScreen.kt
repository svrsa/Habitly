package com.example.habitly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.components.MetricCard
import com.example.habitly.ui.statistics.DailyFocusStat
import com.example.habitly.ui.statistics.RecentFocusSession
import com.example.habitly.ui.statistics.StatisticsViewModel
import com.example.habitly.ui.statistics.StatisticsViewModelFactory

@Composable
fun StatisticsScreen(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(
            taskRepository = application.studyTaskRepository,
            sessionRepository = application.studySessionRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    HabitlyScreen(
        title = "Statistics",
        subtitle = "Progress from your tasks and saved focus sessions.",
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Focus time",
                value = "${uiState.totalFocusMinutes}",
                subtitle = "minutes",
                icon = Icons.Outlined.PlayArrow,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Sessions",
                value = "${uiState.totalSessions}",
                subtitle = "completed",
                icon = Icons.Outlined.Schedule,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Open tasks",
                value = "${uiState.openTasks}",
                subtitle = "to study",
                icon = Icons.Outlined.BarChart,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Done tasks",
                value = "${uiState.completedTasks}",
                subtitle = "completed",
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        WeeklyFocusChart(
            dailyStats = uiState.dailyFocusStats
        )

        RecentSessionsCard(
            sessions = uiState.recentSessions
        )
    }
}

@Composable
private fun WeeklyFocusChart(
    dailyStats: List<DailyFocusStat>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = dailyStats.maxOfOrNull { stat -> stat.focusMinutes }?.coerceAtLeast(1) ?: 1
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    HabitlyCard(
        modifier = modifier
    ) {
        Text(
            text = "Last 7 days",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Focus minutes saved from completed timer sessions.",
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            if (dailyStats.isEmpty()) return@Canvas

            val horizontalPadding = 10.dp.toPx()
            val topPadding = 18.dp.toPx()
            val bottomPadding = 24.dp.toPx()
            val chartWidth = size.width - horizontalPadding * 2
            val chartHeight = size.height - topPadding - bottomPadding
            val stepX = if (dailyStats.size > 1) {
                chartWidth / (dailyStats.size - 1)
            } else {
                chartWidth
            }

            val points = dailyStats.mapIndexed { index, stat ->
                val x = horizontalPadding + stepX * index
                val valueProgress = stat.focusMinutes / maxMinutes.toFloat()
                val y = topPadding + chartHeight - chartHeight * valueProgress
                x to y
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = primaryColor,
                    start = androidx.compose.ui.geometry.Offset(start.first, start.second),
                    end = androidx.compose.ui.geometry.Offset(end.first, end.second),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            points.forEach { point ->
                drawCircle(
                    color = trackColor,
                    radius = 9.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                )
                drawCircle(
                    color = primaryColor,
                    radius = 5.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dailyStats.forEach { stat ->
                Column {
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = labelColor
                    )
                    Text(
                        text = "${stat.focusMinutes}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSessionsCard(
    sessions: List<RecentFocusSession>,
    modifier: Modifier = Modifier
) {
    HabitlyCard(
        modifier = modifier
    ) {
        Text(
            text = "Recent sessions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (sessions.isEmpty()) {
            Text(
                text = "Completed focus sessions will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${session.durationMinutes} min",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Focus session",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = session.completedLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
