package com.example.habitly.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import com.example.habitly.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: StudyTaskRepository
) : ViewModel() {
    private val newTaskTitle = MutableStateFlow("")
    private val selectedPriority = MutableStateFlow(TaskPriority.MEDIUM)

    val uiState: StateFlow<TasksUiState> =
        combine(repository.allTasks, newTaskTitle, selectedPriority) { tasks, title, priority ->
            TasksUiState(
                tasks = tasks,
                newTaskTitle = title,
                selectedPriority = priority
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState()
        )

    fun onNewTaskTitleChange(title: String) {
        newTaskTitle.value = title
    }

    fun onPrioritySelected(priority: TaskPriority) {
        selectedPriority.value = priority
    }

    fun addTask() {
        val title = newTaskTitle.value.trim()

        if (title.isEmpty()) {
            return
        }

        viewModelScope.launch {
            repository.addTask(
                title = title,
                priority = selectedPriority.value
            )
            newTaskTitle.value = ""
        }
    }

    fun toggleTaskCompleted(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(isCompleted = !task.isCompleted)
            )
        }
    }

    fun deleteTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
