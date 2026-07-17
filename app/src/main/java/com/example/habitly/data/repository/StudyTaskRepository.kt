package com.example.habitly.data.repository

import com.example.habitly.data.local.dao.StudyTaskDao
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import kotlinx.coroutines.flow.Flow

class StudyTaskRepository(
    private val studyTaskDao: StudyTaskDao
) {
    val allTasks: Flow<List<StudyTaskEntity>> = studyTaskDao.getAllTasks()

    fun getTaskById(taskId: Long): Flow<StudyTaskEntity?> {
        return studyTaskDao.getTaskById(taskId)
    }

    suspend fun addTask(
        title: String,
        description: String = "",
        priority: TaskPriority = TaskPriority.MEDIUM
    ) {
        val task = StudyTaskEntity(
            title = title,
            description = description,
            priority = priority
        )

        studyTaskDao.insertTask(task)
    }

    suspend fun updateTask(task: StudyTaskEntity) {
        studyTaskDao.updateTask(task)
    }

    suspend fun deleteTask(task: StudyTaskEntity) {
        studyTaskDao.deleteTask(task)
    }
}
