package com.example.habitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.tasks.TaskPriorityFilter
import com.example.habitly.ui.tasks.TasksViewModel
import com.example.habitly.ui.tasks.TasksViewModelFactory

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    onOpenTaskDetail: (Long) -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(application.studyTaskRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val sortedTasks = uiState.tasks.sortedWith(
        compareBy<StudyTaskEntity> { task -> task.priority.sortOrder }
            .thenByDescending { task -> task.createdAt }
    )
    val openTasks = sortedTasks.filter { task -> !task.isCompleted }
    val completedTasks = sortedTasks.filter { task -> task.isCompleted }
    val filteredOpenTasks = openTasks.filterByPriority(uiState.selectedFilter)
    val filteredCompletedTasks = completedTasks.filterByPriority(uiState.selectedFilter)
    val taskListState = rememberLazyListState()
    var showCompletedTasks by rememberSaveable {
        mutableStateOf(false)
    }
    var taskToEdit by remember {
        mutableStateOf<StudyTaskEntity?>(null)
    }

    LaunchedEffect(filteredOpenTasks.size, uiState.selectedFilter) {
        if (filteredOpenTasks.isNotEmpty()) {
            taskListState.animateScrollToItem(0)
        }
    }

    HabitlyScreen(
        title = "Tasks",
        subtitle = "${filteredOpenTasks.size} open, ${filteredCompletedTasks.size} completed",
        modifier = modifier,
        scrollable = false
    ) {
        HabitlyCard {
            Text(
                text = "Add study task",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.newTaskTitle,
                    onValueChange = viewModel::onNewTaskTitleChange,
                    label = { Text(text = "New study task") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = viewModel::addTask,
                    enabled = uiState.newTaskTitle.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add task"
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { priority ->
                    FilterChip(
                        selected = uiState.selectedPriority == priority,
                        onClick = { viewModel.onPrioritySelected(priority) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = priority.containerColor,
                            selectedLabelColor = priority.contentColor,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedPriority == priority,
                            borderColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedBorderColor = priority.containerColor
                        ),
                        label = {
                            Text(text = priority.label)
                        }
                    )
                }
            }
        }

        TaskFilterRow(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = viewModel::onFilterSelected
        )

        if (uiState.tasks.isEmpty()) {
            HabitlyCard {
                Text(
                    text = "No study tasks yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Add your first topic, chapter, or exam prep step.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = taskListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredOpenTasks,
                    key = { task -> task.id }
                ) { task ->
                    TaskListItem(
                        task = task,
                        onTaskClick = { onOpenTaskDetail(task.id) },
                        onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                        onEditClick = { taskToEdit = task },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
                }

                if (filteredCompletedTasks.isNotEmpty()) {
                    item {
                        CompletedTasksHeader(
                            completedCount = filteredCompletedTasks.size,
                            expanded = showCompletedTasks,
                            onToggle = { showCompletedTasks = !showCompletedTasks }
                        )
                    }
                }

                if (showCompletedTasks) {
                    items(
                        items = filteredCompletedTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskListItem(
                            task = task,
                            onTaskClick = { onOpenTaskDetail(task.id) },
                            onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                            onEditClick = { taskToEdit = task },
                            onDeleteClick = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = { title, priority ->
                viewModel.updateTaskDetails(
                    task = task,
                    title = title,
                    priority = priority
                )
                taskToEdit = null
            }
        )
    }
}

private fun List<StudyTaskEntity>.filterByPriority(
    filter: TaskPriorityFilter
): List<StudyTaskEntity> {
    return filter.priority?.let { priority ->
        filter { task -> task.priority == priority }
    } ?: this
}

private val TaskPriority.label: String
    get() = when (this) {
        TaskPriority.LOW -> "Low"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.HIGH -> "High"
    }

private val TaskPriorityFilter.label: String
    get() = when (this) {
        TaskPriorityFilter.ALL -> "All"
        TaskPriorityFilter.HIGH -> "High"
        TaskPriorityFilter.MEDIUM -> "Medium"
        TaskPriorityFilter.LOW -> "Low"
    }

@Composable
private fun EditTaskDialog(
    task: StudyTaskEntity,
    onDismiss: () -> Unit,
    onSave: (String, TaskPriority) -> Unit
) {
    var title by remember(task.id) {
        mutableStateOf(task.title)
    }
    var priority by remember(task.id) {
        mutableStateOf(task.priority)
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
private fun TaskFilterRow(
    selectedFilter: TaskPriorityFilter,
    onFilterSelected: (TaskPriorityFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskPriorityFilter.entries.forEach { filter ->
            val priority = filter.priority
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                colors = if (priority != null) {
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = priority.containerColor,
                        selectedLabelColor = priority.contentColor,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == filter,
                    borderColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedBorderColor = priority?.containerColor
                        ?: MaterialTheme.colorScheme.primaryContainer
                ),
                label = {
                    Text(text = filter.label)
                }
            )
        }
    }
}

private val TaskPriority.sortOrder: Int
    get() = when (this) {
        TaskPriority.HIGH -> 0
        TaskPriority.MEDIUM -> 1
        TaskPriority.LOW -> 2
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListItem(
    task: StudyTaskEntity,
    onTaskClick: () -> Unit,
    onCheckedChange: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onCheckedChange()
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteClick()
                    true
                }

                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )
    val dismissDirection = dismissState.dismissDirection
    val isDeleteAction = dismissDirection == SwipeToDismissBoxValue.EndToStart

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isDeleteAction) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isDeleteAction) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
            ) {
                Icon(
                    imageVector = if (isDeleteAction) {
                        Icons.Outlined.Delete
                    } else {
                        Icons.Outlined.Check
                    },
                    contentDescription = if (isDeleteAction) {
                        "Delete task"
                    } else {
                        "Complete task"
                    },
                    tint = if (isDeleteAction) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }
    ) {
        HabitlyCard(
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onTaskClick)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onCheckedChange() }
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    PriorityChip(priority = task.priority)
                }
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit task"
                    )
                }
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete task"
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedTasksHeader(
    completedCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    HabitlyCard(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$completedCount completed tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onToggle
            ) {
                Icon(
                    imageVector = if (expanded) {
                        Icons.Outlined.KeyboardArrowUp
                    } else {
                        Icons.Outlined.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) {
                        "Hide completed tasks"
                    } else {
                        "Show completed tasks"
                    }
                )
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
