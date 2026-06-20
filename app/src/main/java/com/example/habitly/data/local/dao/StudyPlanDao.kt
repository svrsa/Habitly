package com.example.habitly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.habitly.data.local.entity.StudyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plans ORDER BY plannedDate ASC, createdAt ASC")
    fun getAllPlans(): Flow<List<StudyPlanEntity>>

    @Insert
    suspend fun insertPlan(plan: StudyPlanEntity)

    @Delete
    suspend fun deletePlan(plan: StudyPlanEntity)

    @Query(
        """
        UPDATE study_plans
        SET completedBlocks = MIN(completedBlocks + 1, plannedBlocks)
        WHERE id = :planId
        """
    )
    suspend fun completeNextBlock(planId: Long)

    @Query(
        """
        UPDATE study_plans
        SET completedBlocks = MAX(completedBlocks - 1, 0)
        WHERE id = :planId
        """
    )
    suspend fun undoCompletedBlock(planId: Long)
}
