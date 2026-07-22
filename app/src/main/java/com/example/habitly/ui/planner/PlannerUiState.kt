package com.example.habitly.ui.planner

import com.example.habitly.data.local.entity.StudyPlanEntity
import com.example.habitly.data.local.entity.StudyTaskEntity
import java.time.LocalDate

data class PlannerUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val availableTasks: List<StudyTaskEntity> = emptyList(),
    val selectedTaskId: Long? = null,
    val blockDurationMinutes: Int = 25,
    val plannedBlocks: Int = 1,
    val entries: List<PlannerEntry> = emptyList(),
    val todayEntries: List<PlannerEntry> = emptyList()
) {
    val canAddPlan: Boolean
        get() = selectedTaskId != null

    val totalBlocks: Int
        get() = entries.sumOf { entry -> entry.plan.plannedBlocks }

    val completedBlocks: Int
        get() = entries.sumOf { entry -> entry.plan.completedBlocks }

    val todayTotalBlocks: Int
        get() = todayEntries.sumOf { entry -> entry.plan.plannedBlocks }

    val todayCompletedBlocks: Int
        get() = todayEntries.sumOf { entry -> entry.plan.completedBlocks }
}

data class PlannerEntry(
    val plan: StudyPlanEntity,
    val taskTitle: String,
    val isTaskCompleted: Boolean
) {
    val totalMinutes: Int
        get() = plan.blockDurationMinutes * plan.plannedBlocks

    val progress: Float
        get() = if (plan.plannedBlocks == 0) 0f
        else plan.completedBlocks / plan.plannedBlocks.toFloat()
}

data class PlannedFocusRequest(
    val planId: Long,
    val taskTitle: String,
    val blockDurationMinutes: Int,
    val requestId: Long = System.nanoTime()
)
