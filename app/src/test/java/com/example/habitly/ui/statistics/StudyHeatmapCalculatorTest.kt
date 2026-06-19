package com.example.habitly.ui.statistics

import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyHeatmapCalculatorTest {
    private val today = LocalDate.of(2026, 6, 19)

    @Test
    fun heatmapAlwaysContainsEightFullWeeks() {
        val result = StudyHeatmapCalculator.build(emptyMap(), today)

        assertEquals(56, result.size)
        assertEquals(DayOfWeek.MONDAY, result.first().date.dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, result.last().date.dayOfWeek)
    }

    @Test
    fun focusMinutesAreMappedToTheirCalendarDay() {
        val studyDate = today.minusDays(2)

        val result = StudyHeatmapCalculator.build(
            focusMinutesByDate = mapOf(studyDate to 75),
            today = today
        )

        assertEquals(75, result.single { day -> day.date == studyDate }.focusMinutes)
    }

    @Test
    fun remainingDaysOfCurrentWeekAreMarkedAsFuture() {
        val result = StudyHeatmapCalculator.build(emptyMap(), today)

        assertTrue(result.filter { day -> day.date.isAfter(today) }.all { day -> day.isFuture })
    }
}
