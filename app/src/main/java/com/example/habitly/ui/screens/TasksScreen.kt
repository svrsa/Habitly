package com.example.habitly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.tasks.TaskListBuilder
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
    val taskSections = TaskListBuilder.build(
        tasks = uiState.tasks,
        selectedFilter = uiState.selectedFilter
    )
    val filteredOpenTasks = taskSections.openTasks
    val filteredCompletedTasks = taskSections.completedTasks
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
                            selectedBorderColor = priority.contentColor
                        ),
                        label = {
                            Text(text = priority.label)
                        }
                    )
                }
            }
        }

        TaskBoard(
            openTasks = filteredOpenTasks,
            completedTasks = filteredCompletedTasks,
            selectedFilter = uiState.selectedFilter,
            showCompletedTasks = showCompletedTasks,
            taskListState = taskListState,
            onFilterSelected = viewModel::onFilterSelected,
            onCompletedToggle = { showCompletedTasks = !showCompletedTasks },
            onTaskClick = onOpenTaskDetail,
            onCheckedChange = viewModel::toggleTaskCompleted,
            onEditClick = { task -> taskToEdit = task },
            onDeleteClick = viewModel::deleteTask
        )
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
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = Color(0xFF202D3A),
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 0.dp,
        title = {
            Text(
                text = "Edit task",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
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
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFF5D7186),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
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
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                containerColor = Color(0xFF283443)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = priority == option,
                                borderColor = Color(0xFF5D7186),
                                selectedBorderColor = option.contentColor
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
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun TaskBoard(
    openTasks: List<StudyTaskEntity>,
    completedTasks: List<StudyTaskEntity>,
    selectedFilter: TaskPriorityFilter,
    showCompletedTasks: Boolean,
    taskListState: LazyListState,
    onFilterSelected: (TaskPriorityFilter) -> Unit,
    onCompletedToggle: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onCheckedChange: (StudyTaskEntity) -> Unit,
    onEditClick: (StudyTaskEntity) -> Unit,
    onDeleteClick: (StudyTaskEntity) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val glassBorderColor = Color(0xFF5D7186)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = MaterialTheme.shapes.extraLarge,
                ambientColor = Color.Black.copy(alpha = 0.34f),
                spotColor = primaryColor.copy(alpha = 0.14f),
                clip = false
            ),
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = glassBorderColor.copy(alpha = 0.70f)
        )
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF34404E),
                        Color(0xFF283443),
                        Color(0xFF202D3A)
                    )
                ),
                shape = MaterialTheme.shapes.extraLarge
            )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val cornerRadius = 28.dp.toPx()
                val inset = 1.2.dp.toPx()
                val cardSize = Size(size.width - inset * 2, size.height - inset * 2)
                val cardTopLeft = Offset(inset, inset)

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.075f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 96.dp.toPx()
                    ),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            glassBorderColor.copy(alpha = 0.58f),
                            primaryColor.copy(alpha = 0.26f)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    topLeft = cardTopLeft,
                    size = cardSize,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 2.2.dp.toPx())
                )

                drawRoundRect(
                    color = Color.White.copy(alpha = 0.16f),
                    topLeft = cardTopLeft,
                    size = cardSize,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 1.dp.toPx())
                )

                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.11f),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = "Task board",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${openTasks.size} open, ${completedTasks.size} done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                                selectedBorderColor = priority?.contentColor
                                    ?: MaterialTheme.colorScheme.primaryContainer
                            ),
                            label = {
                                Text(text = filter.label)
                            }
                        )
                    }
                }

                if (openTasks.isNotEmpty() || completedTasks.isNotEmpty()) {
                    Text(
                        text = "Swipe right to complete · left to delete",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                    )
                }

                if (openTasks.isEmpty() && completedTasks.isEmpty()) {
                    Text(
                        text = "No study tasks yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Add your first topic, chapter, or exam prep step.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = taskListState,
                        contentPadding = PaddingValues(top = 0.dp, bottom = 112.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = openTasks,
                            key = { task -> task.id }
                        ) { task ->
                            TaskListItem(
                                task = task,
                                onTaskClick = { onTaskClick(task.id) },
                                onCheckedChange = { onCheckedChange(task) },
                                onEditClick = { onEditClick(task) },
                                onDeleteClick = { onDeleteClick(task) }
                            )
                        }

                        if (completedTasks.isNotEmpty()) {
                            item {
                                CompletedTasksHeader(
                                    completedCount = completedTasks.size,
                                    expanded = showCompletedTasks,
                                    onToggle = onCompletedToggle
                                )
                            }
                        }

                        if (showCompletedTasks) {
                            items(
                                items = completedTasks,
                                key = { task -> task.id }
                            ) { task ->
                                TaskListItem(
                                    task = task,
                                    onTaskClick = { onTaskClick(task.id) },
                                    onCheckedChange = { onCheckedChange(task) },
                                    onEditClick = { onEditClick(task) },
                                    onDeleteClick = { onDeleteClick(task) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
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
    val swipeDeleteColor = Color(0xFFB42318)
    val swipeCompleteColor = Color(0xFF0F766E)
    val swipeActionContentColor = Color.White

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.padding(vertical = 3.dp),
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isDeleteAction) {
                            swipeDeleteColor
                        } else {
                            swipeCompleteColor
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
                    tint = swipeActionContentColor
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
        TaskPriority.LOW -> Color(0xFF123328)
        TaskPriority.MEDIUM -> Color(0xFF3A2A12)
        TaskPriority.HIGH -> Color(0xFF3D211C)
    }

private val TaskPriority.contentColor: Color
    @Composable
    get() = when (this) {
        TaskPriority.LOW -> Color(0xFF1F8A5B)
        TaskPriority.MEDIUM -> Color(0xFFB7791F)
        TaskPriority.HIGH -> Color(0xFFE15A35)
    }
