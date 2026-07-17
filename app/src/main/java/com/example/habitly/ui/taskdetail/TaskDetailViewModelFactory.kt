package com.example.habitly.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository

class TaskDetailViewModelFactory(
    private val taskId: Long,
    private val taskRepository: StudyTaskRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailViewModel(
                taskId = taskId,
                taskRepository = taskRepository,
                sessionRepository = sessionRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
