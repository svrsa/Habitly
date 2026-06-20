package com.example.habitly.ui.evidence

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatEvidenceTimestamp(
    timestamp: Long,
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String = Instant.ofEpochMilli(timestamp)
    .atZone(zoneId)
    .format(DateTimeFormatter.ofPattern("MMM d, HH:mm", locale))
