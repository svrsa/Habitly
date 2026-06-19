package com.example.habitly.ui.statistics

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class StudyHeatmapDay(
    val date: LocalDate,
    val focusMinutes: Int,
    val isFuture: Boolean
)

object StudyHeatmapCalculator {
    private const val WEEKS_TO_SHOW = 8L
    const val DAYS_TO_SHOW = 56

    fun build(
        focusMinutesByDate: Map<LocalDate, Int>,
        today: LocalDate = LocalDate.now()
    ): List<StudyHeatmapDay> {
        val currentWeekMonday = today.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )
        val firstDay = currentWeekMonday.minusWeeks(WEEKS_TO_SHOW - 1)

        return (0 until DAYS_TO_SHOW).map { dayOffset ->
            val date = firstDay.plusDays(dayOffset.toLong())
            StudyHeatmapDay(
                date = date,
                focusMinutes = focusMinutesByDate[date] ?: 0,
                isFuture = date.isAfter(today)
            )
        }
    }
}
