package com.example.habitly.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.habitly.ui.screens.DashboardScreen
import com.example.habitly.ui.screens.SettingsScreen
import com.example.habitly.ui.screens.PlannerScreen
import com.example.habitly.ui.screens.StatisticsScreen
import com.example.habitly.ui.screens.TaskDetailScreen
import com.example.habitly.ui.screens.TasksScreen
import com.example.habitly.ui.screens.TimerScreen
import com.example.habitly.ui.screens.EvidenceCaptureScreen
import com.example.habitly.ui.screens.EvidenceJournalScreen
import com.example.habitly.ui.planner.PlannedFocusRequest

@Composable
fun HabitlyApp() {
    var selectedDestination by rememberSaveable {
        mutableStateOf(AppDestination.Dashboard)
    }
    var selectedTaskId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    var plannedFocusRequest by remember { mutableStateOf<PlannedFocusRequest?>(null) }
    var evidenceSessionId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedDestination != AppDestination.EvidenceCapture) {
                HabitlyBottomBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = {
                        if (it == AppDestination.Timer) plannedFocusRequest = null
                        selectedDestination = it
                    }
                )
            }
        }
    ) { _ ->
        val screenModifier = Modifier
            .fillMaxSize()

        when (selectedDestination) {
            AppDestination.Dashboard -> DashboardScreen(
                modifier = screenModifier,
                onOpenPlanner = { selectedDestination = AppDestination.Planner }
            )
            AppDestination.Planner -> PlannerScreen(
                modifier = screenModifier,
                onStartFocus = { request ->
                    plannedFocusRequest = request
                    selectedDestination = AppDestination.Timer
                }
            )
            AppDestination.Tasks -> TasksScreen(
                modifier = screenModifier,
                onOpenTaskDetail = { taskId ->
                    selectedTaskId = taskId
                    selectedDestination = AppDestination.TaskDetail
                }
            )
            AppDestination.TaskDetail -> selectedTaskId?.let { taskId ->
                TaskDetailScreen(
                    taskId = taskId,
                    modifier = screenModifier,
                    onBack = { selectedDestination = AppDestination.Tasks }
                )
            } ?: run { selectedDestination = AppDestination.Tasks }
            AppDestination.Timer -> TimerScreen(
                modifier = screenModifier,
                plannedFocusRequest = plannedFocusRequest,
                onAddEvidence = { sessionId ->
                    evidenceSessionId = sessionId
                    selectedDestination = AppDestination.EvidenceCapture
                }
            )
            AppDestination.EvidenceCapture -> evidenceSessionId?.let { sessionId ->
                EvidenceCaptureScreen(
                    sessionId = sessionId,
                    modifier = screenModifier,
                    onSaved = { selectedDestination = AppDestination.Journal },
                    onCancel = { selectedDestination = AppDestination.Timer }
                )
            } ?: run { selectedDestination = AppDestination.Timer }
            AppDestination.Journal -> EvidenceJournalScreen(
                modifier = screenModifier,
                onBack = { selectedDestination = AppDestination.Statistics }
            )
            AppDestination.Statistics -> StatisticsScreen(
                modifier = screenModifier,
                onOpenJournal = { selectedDestination = AppDestination.Journal }
            )
            AppDestination.Settings -> SettingsScreen(screenModifier)
        }
    }
}

@Composable
private fun HabitlyBottomBar(
    selectedDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    clip = false
                ),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                AppDestination.entries.filter { destination -> destination.showInBottomBar }
                    .forEach { destination ->
                        NavigationBarItem(
                            selected = destination == selectedDestination,
                            onClick = { onDestinationSelected(destination) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(text = destination.title)
                            }
                        )
                    }
            }
        }
    }
}
