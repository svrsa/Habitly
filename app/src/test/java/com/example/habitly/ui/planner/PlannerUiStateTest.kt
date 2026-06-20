package com.example.habitly.ui.planner

import com.example.habitly.data.local.entity.StudyPlanEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PlannerUiStateTest {
    @Test
    fun entryCalculatesTotalMinutesAndProgress() {
        val entry = plannerEntry(plannedBlocks = 4, completedBlocks = 2, duration = 25)

        assertEquals(100, entry.totalMinutes)
        assertEquals(0.5f, entry.progress)
    }

    @Test
    fun stateAggregatesTodayBlockProgress() {
        val state = PlannerUiState(
            todayEntries = listOf(
                plannerEntry(plannedBlocks = 3, completedBlocks = 1),
                plannerEntry(plannedBlocks = 2, completedBlocks = 2)
            )
        )

        assertEquals(5, state.todayTotalBlocks)
        assertEquals(3, state.todayCompletedBlocks)
    }

    private fun plannerEntry(
        plannedBlocks: Int,
        completedBlocks: Int,
        duration: Int = 25
    ) = PlannerEntry(
        plan = StudyPlanEntity(
            id = 1,
            taskId = 2,
            plannedDate = LocalDate.now().toEpochDay(),
            blockDurationMinutes = duration,
            plannedBlocks = plannedBlocks,
            completedBlocks = completedBlocks
        ),
        taskTitle = "Algorithms",
        isTaskCompleted = false
    )
}
