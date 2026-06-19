package com.example.habitly.ui.statistics

import java.time.LocalDate

data class LearningStreak(
    val currentDays: Int = 0,
    val longestDays: Int = 0
)

object LearningStreakCalculator {
    fun calculate(
        studyDates: Collection<LocalDate>,
        today: LocalDate = LocalDate.now()
    ): LearningStreak {
        val dates = studyDates
            .asSequence()
            .filterNot { date -> date.isAfter(today) }
            .toSet()

        if (dates.isEmpty()) return LearningStreak()

        var longest = 0
        var running = 0
        var previousDate: LocalDate? = null

        dates.sorted().forEach { date ->
            running = if (previousDate?.plusDays(1) == date) running + 1 else 1
            longest = maxOf(longest, running)
            previousDate = date
        }

        val latestActiveDate = when {
            today in dates -> today
            today.minusDays(1) in dates -> today.minusDays(1)
            else -> null
        }

        var current = 0
        var date = latestActiveDate
        while (date != null && date in dates) {
            current++
            date = date.minusDays(1)
        }

        return LearningStreak(
            currentDays = current,
            longestDays = longest
        )
    }
}
