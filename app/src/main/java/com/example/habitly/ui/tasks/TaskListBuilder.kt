package com.example.habitly.ui.tasks

import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority

data class TaskListSections(
    val openTasks: List<StudyTaskEntity>,
    val completedTasks: List<StudyTaskEntity>
)

object TaskListBuilder {
    fun build(
        tasks: List<StudyTaskEntity>,
        selectedFilter: TaskPriorityFilter
    ): TaskListSections {
        val sortedTasks = tasks.sortedWith(
            compareBy<StudyTaskEntity> { task -> task.priority.sortOrder }
                .thenByDescending { task -> task.createdAt }
        )

        return TaskListSections(
            openTasks = sortedTasks
                .filter { task -> !task.isCompleted }
                .filterByPriority(selectedFilter),
            completedTasks = sortedTasks
                .filter { task -> task.isCompleted }
                .filterByPriority(selectedFilter)
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

private val TaskPriority.sortOrder: Int
    get() = when (this) {
        TaskPriority.HIGH -> 0
        TaskPriority.MEDIUM -> 1
        TaskPriority.LOW -> 2
    }
