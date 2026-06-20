package com.example.habitly.ui.evidence

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class EvidenceDateFormatterTest {
    private val vienna = ZoneId.of("Europe/Vienna")

    @Test
    fun timestampUsesReadableDateAndTime() {
        val timestamp = LocalDateTime.of(2026, 6, 20, 14, 35)
            .atZone(vienna)
            .toInstant()
            .toEpochMilli()

        assertEquals(
            "Jun 20, 14:35",
            formatEvidenceTimestamp(timestamp, vienna, Locale.ENGLISH)
        )
    }

    @Test
    fun formatterUsesRequestedTimezone() {
        val timestamp = LocalDateTime.of(2026, 6, 20, 12, 0)
            .atZone(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        assertEquals(
            "Jun 20, 14:00",
            formatEvidenceTimestamp(timestamp, vienna, Locale.ENGLISH)
        )
    }
}
