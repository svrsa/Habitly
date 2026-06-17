package com.example.habitly.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class StatisticsViewModel(
    taskRepository: StudyTaskRepository,
    sessionRepository: StudySessionRepository
) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> =
        combine(
            taskRepository.allTasks,
            sessionRepository.allSessions
        ) { tasks, sessions ->
            val completedTasks = tasks.count { task -> task.isCompleted }

            StatisticsUiState(
                totalTasks = tasks.size,
                completedTasks = completedTasks,
                openTasks = tasks.size - completedTasks,
                totalSessions = sessions.size,
                totalFocusMinutes = sessions.sumOf { session -> session.durationMinutes },
                dailyFocusStats = buildDailyFocusStats(
                    sessions = sessions.map { session ->
                        SessionDate(
                            completedAt = session.completedAt,
                            durationMinutes = session.durationMinutes
                        )
                    }
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )

    private fun buildDailyFocusStats(sessions: List<SessionDate>): List<DailyFocusStat> {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        val focusMinutesByDate = sessions.groupBy { session ->
            Instant.ofEpochMilli(session.completedAt)
                .atZone(zoneId)
                .toLocalDate()
        }.mapValues { (_, sessionsForDate) ->
            sessionsForDate.sumOf { session -> session.durationMinutes }
        }

        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            DailyFocusStat(
                label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                focusMinutes = focusMinutesByDate[date] ?: 0
            )
        }
    }

    private data class SessionDate(
        val completedAt: Long,
        val durationMinutes: Int
    )
}
