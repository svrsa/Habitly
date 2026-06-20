package com.example.habitly.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.data.repository.StudyTaskRepository

class PlannerViewModelFactory(
    private val planRepository: StudyPlanRepository,
    private val taskRepository: StudyTaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlannerViewModel(planRepository, taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
