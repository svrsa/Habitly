package com.example.habitly.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
                onOpenPlanner = { selectedDestination = AppDestination.Planner },
                onStartFocus = {
                    plannedFocusRequest = null
                    selectedDestination = AppDestination.Timer
                }
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
                .padding(horizontal = 2.dp, vertical = 2.dp)
                .shadow(
                    elevation = 22.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    ambientColor = Color.Black.copy(alpha = 0.34f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    clip = false
                ),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Black.copy(alpha = 0.18f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(modifier = Modifier.padding(vertical = 33.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    clip = false
            ),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFF4D5B68).copy(alpha = 0.75f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF242A32),
                                Color(0xFF1A1F26),
                                Color(0xFF15191F)
                            )
                        ),
                        shape = MaterialTheme.shapes.extraLarge
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppDestination.entries.filter { destination -> destination.showInBottomBar }
                        .forEach { destination ->
                            val isSelected = destination == selectedDestination
                            val itemColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onDestinationSelected(destination) }
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(34.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Surface(
                                            modifier = Modifier.size(34.dp),
                                            shape = MaterialTheme.shapes.extraLarge,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ) {}
                                    }
                                    Box(
                                        modifier = Modifier.size(34.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(23.dp),
                                            tint = itemColor
                                        )
                                    }
                                }
                                Text(
                                    text = destination.title,
                                    color = itemColor,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.SemiBold
                                    }
                                )
                            }
                        }
                }
            }
        }
    }
}
