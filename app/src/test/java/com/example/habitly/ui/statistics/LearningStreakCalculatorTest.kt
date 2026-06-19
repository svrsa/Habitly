package com.example.habitly.ui.statistics

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class LearningStreakCalculatorTest {
    private val today = LocalDate.of(2026, 6, 19)

    @Test
    fun emptyHistoryHasNoStreak() {
        assertEquals(
            LearningStreak(),
            LearningStreakCalculator.calculate(emptyList(), today)
        )
    }

    @Test
    fun multipleSessionsOnOneDayCountOnce() {
        val result = LearningStreakCalculator.calculate(
            studyDates = listOf(today, today, today.minusDays(1)),
            today = today
        )

        assertEquals(2, result.currentDays)
        assertEquals(2, result.longestDays)
    }

    @Test
    fun streakRemainsActiveWhenLastSessionWasYesterday() {
        val result = LearningStreakCalculator.calculate(
            studyDates = listOf(today.minusDays(1), today.minusDays(2), today.minusDays(3)),
            today = today
        )

        assertEquals(3, result.currentDays)
        assertEquals(3, result.longestDays)
    }

    @Test
    fun missedDayResetsCurrentButKeepsLongestStreak() {
        val result = LearningStreakCalculator.calculate(
            studyDates = listOf(today.minusDays(3), today.minusDays(4), today.minusDays(5)),
            today = today
        )

        assertEquals(0, result.currentDays)
        assertEquals(3, result.longestDays)
    }

    @Test
    fun longestStreakIsCalculatedAcrossSeparateRuns() {
        val result = LearningStreakCalculator.calculate(
            studyDates = listOf(
                today,
                today.minusDays(1),
                today.minusDays(5),
                today.minusDays(6),
                today.minusDays(7),
                today.minusDays(8)
            ),
            today = today
        )

        assertEquals(2, result.currentDays)
        assertEquals(4, result.longestDays)
    }
}
