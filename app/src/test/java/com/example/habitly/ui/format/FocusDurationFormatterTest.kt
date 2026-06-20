package com.example.habitly.ui.format

import org.junit.Assert.assertEquals
import org.junit.Test

class FocusDurationFormatterTest {
    @Test
    fun durationUnderOneHourUsesMinutes() {
        assertEquals("45 min", formatFocusDuration(45))
    }

    @Test
    fun fullHourOmitsZeroMinutes() {
        assertEquals("2 h", formatFocusDuration(120))
    }

    @Test
    fun durationOverOneHourUsesHoursAndMinutes() {
        assertEquals("1 h 15 min", formatFocusDuration(75))
    }

    @Test
    fun zeroDurationUsesMinutes() {
        assertEquals("0 min", formatFocusDuration(0))
    }
}
