package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.planner.PlannedFocusRequest
import com.example.habitly.ui.planner.PlannerEntry
import com.example.habitly.ui.planner.PlannerViewModel
import com.example.habitly.ui.planner.PlannerViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlannerScreen(
    onStartFocus: (PlannedFocusRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: PlannerViewModel = viewModel(
        factory = PlannerViewModelFactory(
            application.studyPlanRepository,
            application.studyTaskRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    var taskMenuExpanded by remember { mutableStateOf(false) }
    val selectedTask = uiState.availableTasks.firstOrNull { task ->
        task.id == uiState.selectedTaskId
    }
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    }
    val today = LocalDate.now()
    val selectedDateLabel = when (uiState.selectedDate) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        today.plusDays(1) -> "Tomorrow"
        else -> uiState.selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    HabitlyScreen(
        title = "Planner",
        subtitle = "$selectedDateLabel · ${uiState.totalBlocks} planned focus blocks.",
        modifier = modifier
    ) {
        HabitlyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.minusDays(1)) }) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous day")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedDateLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = uiState.selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.selectDate(today) }) {
                        Text(if (uiState.selectedDate == today) "Planning today" else "Back to today")
                    }
                }
                IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.plusDays(1)) }) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Next day")
                }
            }
        }

        if (uiState.entries.isNotEmpty()) {
            DayProgressCard(
                completedBlocks = uiState.completedBlocks,
                totalBlocks = uiState.totalBlocks
            )
        }

        HabitlyCard {
            Text(
                "Create a focus plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            PlannerStepHeader(number = 1, title = "Choose a task")
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { taskMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedTask?.title ?: "Choose an open task")
                }
                DropdownMenu(
                    expanded = taskMenuExpanded,
                    onDismissRequest = { taskMenuExpanded = false }
                ) {
                    if (uiState.availableTasks.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Create an open task first") },
                            onClick = { taskMenuExpanded = false },
                            enabled = false
                        )
                    }
                    uiState.availableTasks.forEach { task ->
                        DropdownMenuItem(
                            text = { Text(task.title) },
                            onClick = {
                                viewModel.selectTask(task.id)
                                taskMenuExpanded = false
                            }
                        )
                    }
                }
            }

            PlannerStepHeader(number = 2, title = "Choose a block length")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 25, 45).forEach { duration ->
                    FilterChip(
                        selected = uiState.blockDurationMinutes == duration,
                        onClick = { viewModel.selectBlockDuration(duration) },
                        label = { Text("$duration min") }
                    )
                }
            }

            PlannerStepHeader(number = 3, title = "Set the number of blocks")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Planned workload", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${uiState.plannedBlocks} blocks, ${uiState.blockDurationMinutes * uiState.plannedBlocks} min total",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.setPlannedBlocks(uiState.plannedBlocks - 1) },
                        enabled = uiState.plannedBlocks > 1
                    ) {
                        Icon(Icons.Outlined.Remove, contentDescription = "Remove block")
                    }
                    Text("${uiState.plannedBlocks}", style = MaterialTheme.typography.titleLarge)
                    IconButton(
                        onClick = { viewModel.setPlannedBlocks(uiState.plannedBlocks + 1) },
                        enabled = uiState.plannedBlocks < 8
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add block")
                    }
                }
            }
            Button(
                onClick = viewModel::addPlan,
                enabled = uiState.canAddPlan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Text("Add focus plan", modifier = Modifier.padding(start = 6.dp))
            }
        }

        if (uiState.entries.isEmpty()) {
            HabitlyCard {
                Text("Nothing planned yet", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Choose an open task and split it into manageable focus blocks.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text("Planned tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            uiState.entries.forEach { entry ->
                PlannerEntryCard(
                    entry = entry,
                    onStart = {
                        onStartFocus(
                            PlannedFocusRequest(
                                planId = entry.plan.id,
                                taskTitle = entry.taskTitle,
                                blockDurationMinutes = entry.plan.blockDurationMinutes
                            )
                        )
                    },
                    onComplete = { viewModel.completeNextBlock(entry.plan.id) },
                    onUndo = { viewModel.undoCompletedBlock(entry.plan.id) },
                    onDelete = { viewModel.deletePlan(entry.plan) }
                )
            }
        }
    }
}

@Composable
private fun DayProgressCard(completedBlocks: Int, totalBlocks: Int) {
    val progress = completedBlocks / totalBlocks.coerceAtLeast(1).toFloat()
    HabitlyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Day progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "$completedBlocks of $totalBlocks focus blocks done",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun PlannerStepHeader(number: Int, title: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = "$number",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontWeight = FontWeight.Bold
            )
        }
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PlannerEntryCard(
    entry: PlannerEntry,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onUndo: () -> Unit,
    onDelete: () -> Unit
) {
    val isFinished = entry.plan.completedBlocks >= entry.plan.plannedBlocks
    HabitlyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.taskTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${entry.plan.plannedBlocks} x ${entry.plan.blockDurationMinutes} min - ${entry.totalMinutes} min total",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete plan")
            }
        }
        LinearProgressIndicator(progress = { entry.progress }, modifier = Modifier.fillMaxWidth())
        Text(
            if (isFinished) "Plan completed" else "${entry.plan.completedBlocks} of ${entry.plan.plannedBlocks} blocks completed",
            color = if (isFinished) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isFinished) FontWeight.SemiBold else FontWeight.Normal
        )
        Button(
            onClick = onStart,
            enabled = !isFinished,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = null)
            Text("Start focus", modifier = Modifier.padding(start = 6.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onUndo,
                enabled = entry.plan.completedBlocks > 0,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Remove, contentDescription = null)
                Text("Undo", modifier = Modifier.padding(start = 4.dp))
            }
            FilledTonalButton(
                onClick = onComplete,
                enabled = !isFinished,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                Text("Done", modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}
