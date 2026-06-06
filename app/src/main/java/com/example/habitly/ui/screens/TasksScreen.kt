package com.example.habitly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.ui.tasks.TasksViewModel
import com.example.habitly.ui.tasks.TasksViewModelFactory

@Composable
fun TasksScreen(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(application.studyTaskRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val completedTaskCount = uiState.tasks.count { task -> task.isCompleted }
    val openTaskCount = uiState.tasks.size - completedTaskCount

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "$openTaskCount open, $completedTaskCount completed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        if (uiState.tasks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No study tasks yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.tasks,
                    key = { task -> task.id }
                ) { task ->
                    TaskListItem(
                        task = task,
                        onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: StudyTaskEntity,
    onCheckedChange: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCheckedChange() }
            )
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
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp)
            )
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
