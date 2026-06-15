package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Focus time",
                value = "${uiState.totalFocusMinutes}",
                subtitle = "minutes",
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Sessions",
                value = "${uiState.totalSessions}",
                subtitle = "completed",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Open tasks",
                value = "${uiState.openTasks}",
                subtitle = "to study",
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Done tasks",
                value = "${uiState.completedTasks}",
                subtitle = "completed",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
