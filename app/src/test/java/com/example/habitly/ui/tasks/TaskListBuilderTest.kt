package com.example.habitly.ui.tasks

import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskListBuilderTest {
    @Test
    fun separatesOpenAndCompletedTasks() {
        val openTask = task(id = 1, title = "Open task", isCompleted = false)
        val completedTask = task(id = 2, title = "Done task", isCompleted = true)

        val result = TaskListBuilder.build(
            tasks = listOf(openTask, completedTask),
            selectedFilter = TaskPriorityFilter.ALL
        )

        assertEquals(listOf(openTask), result.openTasks)
        assertEquals(listOf(completedTask), result.completedTasks)
    }

    @Test
    fun filtersTasksByPriority() {
        val highTask = task(id = 1, title = "High", priority = TaskPriority.HIGH)
        val mediumTask = task(id = 2, title = "Medium", priority = TaskPriority.MEDIUM)
        val lowTask = task(id = 3, title = "Low", priority = TaskPriority.LOW)

        val result = TaskListBuilder.build(
            tasks = listOf(lowTask, mediumTask, highTask),
            selectedFilter = TaskPriorityFilter.HIGH
        )

        assertEquals(listOf(highTask), result.openTasks)
        assertEquals(emptyList<StudyTaskEntity>(), result.completedTasks)
    }

    @Test
    fun sortsOpenTasksByPriorityAndNewestFirst() {
        val olderHighTask = task(
            id = 1,
            title = "Older high",
            priority = TaskPriority.HIGH,
            createdAt = 100
        )
        val newerHighTask = task(
            id = 2,
            title = "Newer high",
            priority = TaskPriority.HIGH,
            createdAt = 200
        )
        val mediumTask = task(
            id = 3,
            title = "Medium",
            priority = TaskPriority.MEDIUM,
            createdAt = 300
        )
        val lowTask = task(
            id = 4,
            title = "Low",
            priority = TaskPriority.LOW,
            createdAt = 400
        )

        val result = TaskListBuilder.build(
            tasks = listOf(lowTask, mediumTask, olderHighTask, newerHighTask),
            selectedFilter = TaskPriorityFilter.ALL
        )

        assertEquals(
            listOf(newerHighTask, olderHighTask, mediumTask, lowTask),
            result.openTasks
        )
    }

    @Test
    fun completedTasksUseSameSortingRules() {
        val highTask = task(
            id = 1,
            title = "High",
            priority = TaskPriority.HIGH,
            isCompleted = true,
            createdAt = 100
        )
        val lowTask = task(
            id = 2,
            title = "Low",
            priority = TaskPriority.LOW,
            isCompleted = true,
            createdAt = 200
        )

        val result = TaskListBuilder.build(
            tasks = listOf(lowTask, highTask),
            selectedFilter = TaskPriorityFilter.ALL
        )

        assertEquals(listOf(highTask, lowTask), result.completedTasks)
    }

    private fun task(
        id: Long,
        title: String,
        priority: TaskPriority = TaskPriority.MEDIUM,
        isCompleted: Boolean = false,
        createdAt: Long = id
    ): StudyTaskEntity {
        return StudyTaskEntity(
            id = id,
            title = title,
            priority = priority,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }
}
