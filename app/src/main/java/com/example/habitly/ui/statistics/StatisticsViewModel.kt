package com.example.habitly.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class StatisticsViewModel(
    taskRepository: StudyTaskRepository,
    private val sessionRepository: StudySessionRepository
) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> =
        combine(
            taskRepository.allTasks,
            sessionRepository.allSessions
        ) { tasks, sessions ->
            val completedTasks = tasks.count { task -> task.isCompleted }
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now(zoneId)
            val sessionDates = sessions.map { session ->
                Instant.ofEpochMilli(session.completedAt)
                    .atZone(zoneId)
                    .toLocalDate()
            }
            val streak = LearningStreakCalculator.calculate(
                studyDates = sessionDates,
                today = today
            )
            val focusMinutesByDate = sessions
                .zip(sessionDates)
                .groupBy(
                    keySelector = { (_, date) -> date },
                    valueTransform = { (session, _) -> session.durationMinutes }
                )
                .mapValues { (_, durations) -> durations.sum() }

            StatisticsUiState(
                totalTasks = tasks.size,
                completedTasks = completedTasks,
                openTasks = tasks.size - completedTasks,
                totalSessions = sessions.size,
                totalFocusMinutes = sessions.sumOf { session -> session.durationMinutes },
                todayFocusMinutes = sessions
                    .filterIndexed { index, _ -> sessionDates[index] == today }
                    .sumOf { session -> session.durationMinutes },
                currentStreakDays = streak.currentDays,
                longestStreakDays = streak.longestDays,
                dailyFocusStats = buildDailyFocusStats(
                    sessions = sessions.map { session ->
                        SessionDate(
                            id = session.id,
                            completedAt = session.completedAt,
                            durationMinutes = session.durationMinutes
                        )
                    }
                ),
                studyHeatmap = StudyHeatmapCalculator.build(
                    focusMinutesByDate = focusMinutesByDate,
                    today = today
                ),
                recentSessions = buildRecentSessions(
                    sessions = sessions.map { session ->
                        SessionDate(
                            id = session.id,
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

    private fun buildRecentSessions(sessions: List<SessionDate>): List<RecentFocusSession> {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)

        return sessions
            .sortedByDescending { session -> session.completedAt }
            .take(5)
            .map { session ->
                val completedDate = Instant.ofEpochMilli(session.completedAt)
                    .atZone(zoneId)
                    .toLocalDate()

                RecentFocusSession(
                    id = session.id,
                    durationMinutes = session.durationMinutes,
                    completedLabel = formatCompletedLabel(
                        completedDate = completedDate,
                        today = today
                    )
                )
            }
    }

    private fun formatCompletedLabel(
        completedDate: LocalDate,
        today: LocalDate
    ): String {
        return when (completedDate) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> completedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }

    private data class SessionDate(
        val id: Long,
        val completedAt: Long,
        val durationMinutes: Int
    )
}
