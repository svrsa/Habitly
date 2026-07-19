package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.format.formatFocusDuration
import com.example.habitly.ui.statistics.StatisticsViewModel
import com.example.habitly.ui.statistics.StatisticsViewModelFactory
import com.example.habitly.ui.settings.SettingsViewModel
import com.example.habitly.ui.settings.SettingsViewModelFactory
import com.example.habitly.ui.planner.PlannerViewModel
import com.example.habitly.ui.planner.PlannerViewModelFactory

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onOpenPlanner: () -> Unit = {},
    onStartFocus: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(
            taskRepository = application.studyTaskRepository,
            sessionRepository = application.studySessionRepository,
            evidenceRepository = application.studyEvidenceRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(application.settingsRepository)
    )
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val plannerViewModel: PlannerViewModel = viewModel(
        factory = PlannerViewModelFactory(
            application.studyPlanRepository,
            application.studyTaskRepository
        )
    )
    val plannerUiState by plannerViewModel.uiState.collectAsState()
    val studyGoalMinutes = settingsUiState.dailyStudyGoalMinutes
    val goalProgress = (uiState.todayFocusMinutes / studyGoalMinutes.toFloat()).coerceIn(0f, 1f)
    val goalPercent = (goalProgress * 100).toInt()
    val focusAccent = MaterialTheme.colorScheme.primary
    val sessionAccent = Color(0xFF5E5CE6)
    val openTaskAccent = Color(0xFFB7791F)
    val completedTaskAccent = Color(0xFF1F8A5B)
    val streakAccent = Color(0xFFE15A35)

    HabitlyScreen(
        title = "Today",
        subtitle = "${uiState.todayFocusMinutes} of $studyGoalMinutes focus minutes logged.",
        modifier = modifier
    ) {
        HabitlyCard(
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today’s focus",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.todayFocusMinutes} / $studyGoalMinutes min",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily goal progress",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatFocusDuration(studyGoalMinutes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { goalProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Logged today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$goalPercent%",
                            style = MaterialTheme.typography.bodySmall,
                            color = focusAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.todayFocusMinutes == 0) {
                            "Ready for your first focus block."
                        } else {
                            "Good rhythm. Add one more block."
                        },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Button(
                        onClick = onStartFocus,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = focusAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start")
                    }
                }
            }
        }

        DashboardOverviewCard(
            openTasks = uiState.openTasks,
            completedTasks = uiState.completedTasks,
            totalSessions = uiState.totalSessions,
            currentStreakDays = uiState.currentStreakDays,
            openTaskAccent = openTaskAccent,
            completedTaskAccent = completedTaskAccent,
            sessionAccent = sessionAccent,
            streakAccent = streakAccent
        )

        HabitlyCard(
            contentPadding = PaddingValues(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = openTaskAccent.copy(alpha = 0.16f),
                    contentColor = openTaskAccent
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Today's plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (plannerUiState.todayEntries.isEmpty()) {
                            "No focus blocks planned yet. Build a realistic plan for today."
                        } else {
                            "${plannerUiState.todayCompletedBlocks} of ${plannerUiState.todayTotalBlocks} blocks completed across ${plannerUiState.todayEntries.size} tasks."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = onOpenPlanner,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (plannerUiState.todayEntries.isEmpty()) "Create today's plan" else "Open planner")
            }
        }
    }
}

@Composable
private fun DashboardOverviewCard(
    openTasks: Int,
    completedTasks: Int,
    totalSessions: Int,
    currentStreakDays: Int,
    openTaskAccent: Color,
    completedTaskAccent: Color,
    sessionAccent: Color,
    streakAccent: Color
) {
    HabitlyCard(
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OverviewMetric(
                icon = Icons.Outlined.TaskAlt,
                title = "Open",
                value = "$openTasks",
                accentColor = openTaskAccent,
                modifier = Modifier.weight(1f)
            )
            OverviewMetric(
                icon = Icons.Outlined.CheckCircle,
                title = "Done",
                value = "$completedTasks",
                accentColor = completedTaskAccent,
                modifier = Modifier.weight(1f)
            )
            OverviewMetric(
                icon = Icons.Outlined.Schedule,
                title = "Blocks",
                value = "$totalSessions",
                accentColor = sessionAccent,
                modifier = Modifier.weight(1f)
            )
            OverviewMetric(
                icon = Icons.Outlined.LocalFireDepartment,
                title = "Streak",
                value = "$currentStreakDays",
                accentColor = streakAccent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewMetric(
    icon: ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = accentColor.copy(alpha = 0.16f),
            contentColor = accentColor
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
