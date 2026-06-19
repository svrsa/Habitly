package com.example.habitly.notifications

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class StudyReminderManagerTest {
    private val zone = ZoneId.of("Europe/Vienna")

    @Test
    fun reminderUsesTodayWhenItIsBeforeSixPm() {
        val now = ZonedDateTime.of(2026, 6, 19, 10, 30, 0, 0, zone)

        val result = StudyReminderManager.nextTriggerAt(now, hour = 18, minute = 0)

        assertEquals(ZonedDateTime.of(2026, 6, 19, 18, 0, 0, 0, zone), result)
    }

    @Test
    fun reminderUsesTomorrowWhenSixPmHasPassed() {
        val now = ZonedDateTime.of(2026, 6, 19, 18, 0, 0, 0, zone)

        val result = StudyReminderManager.nextTriggerAt(now, hour = 18, minute = 0)

        assertEquals(ZonedDateTime.of(2026, 6, 20, 18, 0, 0, 0, zone), result)
    }

    @Test
    fun reminderSupportsAUserSelectedTime() {
        val now = ZonedDateTime.of(2026, 6, 19, 8, 0, 0, 0, zone)

        val result = StudyReminderManager.nextTriggerAt(now, hour = 14, minute = 35)

        assertEquals(ZonedDateTime.of(2026, 6, 19, 14, 35, 0, 0, zone), result)
    }
}
