package com.example.habitly.ui.format

fun formatFocusDuration(totalMinutes: Int): String {
    require(totalMinutes >= 0) { "Focus duration cannot be negative" }

    if (totalMinutes < MINUTES_PER_HOUR) {
        return "$totalMinutes min"
    }

    val hours = totalMinutes / MINUTES_PER_HOUR
    val minutes = totalMinutes % MINUTES_PER_HOUR

    return if (minutes == 0) {
        "$hours h"
    } else {
        "$hours h $minutes min"
    }
}

private const val MINUTES_PER_HOUR = 60
