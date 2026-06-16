package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.components.MetricCard
import com.example.habitly.ui.statistics.StatisticsViewModel
import com.example.habitly.ui.statistics.StatisticsViewModelFactory

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(
            taskRepository = application.studyTaskRepository,
            sessionRepository = application.studySessionRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    HabitlyScreen(
        title = "Dashboard",
        subtitle = "A calm overview of today's study momentum.",
        modifier = modifier
    ) {
        HabitlyCard {
            Text(
                text = "Study progress",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${uiState.totalFocusMinutes} focus minutes across ${uiState.totalSessions} sessions",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Open tasks",
                value = "${uiState.openTasks}",
                subtitle = "left to study",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Done tasks",
                value = "${uiState.completedTasks}",
                subtitle = "completed",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
