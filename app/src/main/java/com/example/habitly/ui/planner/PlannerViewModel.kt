package com.example.habitly.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.local.entity.StudyPlanEntity
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.data.repository.StudyTaskRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlannerViewModel(
    private val planRepository: StudyPlanRepository,
    taskRepository: StudyTaskRepository
) : ViewModel() {
    private val form = MutableStateFlow(PlannerForm())

    val uiState: StateFlow<PlannerUiState> = combine(
        planRepository.allPlans,
        taskRepository.allTasks,
        form
    ) { plans, tasks, currentForm ->
        val openTasks = tasks.filterNot { task -> task.isCompleted }
        val taskById = tasks.associateBy { task -> task.id }
        fun mapPlansForDate(date: LocalDate): List<PlannerEntry> = plans
            .filter { plan -> plan.plannedDate == date.toEpochDay() }
            .mapNotNull { plan ->
                taskById[plan.taskId]?.let { task ->
                    PlannerEntry(
                        plan = plan,
                        taskTitle = task.title,
                        isTaskCompleted = task.isCompleted
                    )
                }
            }

        PlannerUiState(
            selectedDate = currentForm.selectedDate,
            availableTasks = openTasks,
            selectedTaskId = currentForm.selectedTaskId
                ?.takeIf { id -> openTasks.any { task -> task.id == id } },
            blockDurationMinutes = currentForm.blockDurationMinutes,
            plannedBlocks = currentForm.plannedBlocks,
            entries = mapPlansForDate(currentForm.selectedDate),
            todayEntries = mapPlansForDate(LocalDate.now())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlannerUiState()
    )

    fun selectDate(date: LocalDate) {
        form.update { state -> state.copy(selectedDate = date) }
    }

    fun selectTask(taskId: Long) {
        form.update { state -> state.copy(selectedTaskId = taskId) }
    }

    fun selectBlockDuration(minutes: Int) {
        form.update { state -> state.copy(blockDurationMinutes = minutes) }
    }

    fun setPlannedBlocks(blocks: Int) {
        form.update { state -> state.copy(plannedBlocks = blocks.coerceIn(1, 8)) }
    }

    fun addPlan() {
        val current = form.value
        val taskId = current.selectedTaskId ?: return

        viewModelScope.launch {
            planRepository.addPlan(
                taskId = taskId,
                date = current.selectedDate,
                blockDurationMinutes = current.blockDurationMinutes,
                plannedBlocks = current.plannedBlocks
            )
            form.update { state -> state.copy(selectedTaskId = null, plannedBlocks = 1) }
        }
    }

    fun completeNextBlock(planId: Long) {
        viewModelScope.launch { planRepository.completeNextBlock(planId) }
    }

    fun undoCompletedBlock(planId: Long) {
        viewModelScope.launch { planRepository.undoCompletedBlock(planId) }
    }

    fun deletePlan(plan: StudyPlanEntity) {
        viewModelScope.launch { planRepository.deletePlan(plan) }
    }

    private data class PlannerForm(
        val selectedDate: LocalDate = LocalDate.now(),
        val selectedTaskId: Long? = null,
        val blockDurationMinutes: Int = 25,
        val plannedBlocks: Int = 1
    )
}
