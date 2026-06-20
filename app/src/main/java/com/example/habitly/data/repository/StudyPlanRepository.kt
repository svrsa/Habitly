package com.example.habitly.data.repository

import com.example.habitly.data.local.dao.StudyPlanDao
import com.example.habitly.data.local.entity.StudyPlanEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class StudyPlanRepository(
    private val studyPlanDao: StudyPlanDao
) {
    val allPlans: Flow<List<StudyPlanEntity>> = studyPlanDao.getAllPlans()

    suspend fun addPlan(
        taskId: Long,
        date: LocalDate,
        blockDurationMinutes: Int,
        plannedBlocks: Int
    ) {
        require(blockDurationMinutes > 0)
        require(plannedBlocks in 1..8)

        studyPlanDao.insertPlan(
            StudyPlanEntity(
                taskId = taskId,
                plannedDate = date.toEpochDay(),
                blockDurationMinutes = blockDurationMinutes,
                plannedBlocks = plannedBlocks
            )
        )
    }

    suspend fun deletePlan(plan: StudyPlanEntity) = studyPlanDao.deletePlan(plan)

    suspend fun completeNextBlock(planId: Long) = studyPlanDao.completeNextBlock(planId)

    suspend fun undoCompletedBlock(planId: Long) = studyPlanDao.undoCompletedBlock(planId)
}
