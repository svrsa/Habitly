package com.example.habitly.ui.tasks

import com.example.habitly.data.local.entity.TaskPriority

enum class TaskPriorityFilter(
    val priority: TaskPriority?
) {
    ALL(priority = null),
    HIGH(priority = TaskPriority.HIGH),
    MEDIUM(priority = TaskPriority.MEDIUM),
    LOW(priority = TaskPriority.LOW)
}
