package com.example.habitly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.format.formatFocusDuration
import com.example.habitly.ui.statistics.DailyFocusStat
import com.example.habitly.ui.statistics.RecentFocusSession
import com.example.habitly.ui.statistics.StatisticsViewModel
import com.example.habitly.ui.statistics.StatisticsViewModelFactory
import com.example.habitly.ui.statistics.StudyHeatmapCalculator
import com.example.habitly.ui.statistics.StudyHeatmapDay
import com.example.habitly.ui.statistics.TaskFocusStat
import com.example.habitly.ui.evidence.EvidenceViewModel
import com.example.habitly.ui.evidence.EvidenceViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    onOpenJournal: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(
            taskRepository = application.studyTaskRepository,
            sessionRepository = application.studySessionRepository,
            evidenceRepository = application.studyEvidenceRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val evidenceViewModel: EvidenceViewModel = viewModel(
        factory = EvidenceViewModelFactory(application.studyEvidenceRepository)
    )
    val evidenceUiState by evidenceViewModel.uiState.collectAsState()
    val focusAccent = MaterialTheme.colorScheme.primary
    val sessionAccent = Color(0xFF5E5CE6)

    HabitlyScreen(
        title = "Progress",
        subtitle = "${formatFocusDuration(uiState.totalFocusMinutes)} saved across ${uiState.totalSessions} focus sessions.",
        modifier = modifier
    ) {
        WeeklyAnalyticsCard(
            dailyStats = uiState.dailyFocusStats,
            focusAccent = focusAccent,
            sessionAccent = sessionAccent
        )

        StudyHeatmapCard(days = uiState.studyHeatmap)

        StudyActivityCard(
            evidenceCount = evidenceUiState.evidence.size,
            taskFocusStats = uiState.taskFocusStats,
            onOpenJournal = onOpenJournal,
            sessions = uiState.recentSessions,
            onDeleteSession = viewModel::deleteSession
        )
    }
}

@Composable
private fun WeeklyAnalyticsCard(
    dailyStats: List<DailyFocusStat>,
    focusAccent: Color,
    sessionAccent: Color,
    modifier: Modifier = Modifier
) {
    val weekFocusMinutes = dailyStats.sumOf { stat -> stat.focusMinutes }
    val activeDays = dailyStats.count { stat -> stat.focusMinutes > 0 }
    val bestDay = dailyStats.maxByOrNull { stat -> stat.focusMinutes }
    val maxMinutes = dailyStats.maxOfOrNull { stat -> stat.focusMinutes }?.coerceAtLeast(1) ?: 1
    val trackColor = MaterialTheme.colorScheme.primaryContainer
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    HabitlyCard(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "This week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Focus minutes saved from completed timer sessions.",
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = formatFocusDuration(weekFocusMinutes),
                    style = MaterialTheme.typography.displaySmall,
                    color = focusAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "saved this week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$activeDays / 7",
                    style = MaterialTheme.typography.headlineSmall,
                    color = sessionAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "active days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            if (dailyStats.isEmpty()) return@Canvas

            val horizontalPadding = 10.dp.toPx()
            val topPadding = 18.dp.toPx()
            val bottomPadding = 24.dp.toPx()
            val chartWidth = size.width - horizontalPadding * 2
            val chartHeight = size.height - topPadding - bottomPadding
            val stepX = if (dailyStats.size > 1) {
                chartWidth / (dailyStats.size - 1)
            } else {
                chartWidth
            }

            val points = dailyStats.mapIndexed { index, stat ->
                val x = horizontalPadding + stepX * index
                val valueProgress = stat.focusMinutes / maxMinutes.toFloat()
                val y = topPadding + chartHeight - chartHeight * valueProgress
                x to y
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = focusAccent,
                    start = androidx.compose.ui.geometry.Offset(start.first, start.second),
                    end = androidx.compose.ui.geometry.Offset(end.first, end.second),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            points.forEach { point ->
                drawCircle(
                    color = trackColor,
                    radius = 9.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                )
                drawCircle(
                    color = focusAccent,
                    radius = 5.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(point.first, point.second)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dailyStats.forEach { stat ->
                Column {
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = labelColor
                    )
                    Text(
                        text = "${stat.focusMinutes}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        bestDay?.let { stat ->
            Text(
                text = "Best day: ${stat.label} with ${formatFocusDuration(stat.focusMinutes)}",
                style = MaterialTheme.typography.bodySmall,
                color = labelColor
            )
        }
    }
}

@Composable
private fun SummaryIcon(
    icon: ImageVector,
    accentColor: Color
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = accentColor.copy(alpha = 0.16f),
        contentColor = accentColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun StudyHeatmapCard(
    days: List<StudyHeatmapDay>,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember(days) {
        mutableStateOf(days.lastOrNull { day -> !day.isFuture })
    }
    val primary = MaterialTheme.colorScheme.primary
    val selectionBorderColor = MaterialTheme.colorScheme.onSurface
    val todayMarkerOuterColor = MaterialTheme.colorScheme.surface
    val today = LocalDate.now()
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val futureColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f)
    val heatmapLow = Color(0xFFFACC15)
    val heatmapMedium = Color(0xFFF59E0B)
    val heatmapHigh = Color(0xFFF97316)
    val heatmapPeak = Color(0xFFEF4444)
    val maxMinutes = days
        .filterNot { day -> day.isFuture }
        .maxOfOrNull { day -> day.focusMinutes }
        ?.coerceAtLeast(1)
        ?: 1
    val dayFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
    }

    HabitlyCard(modifier = modifier) {
        Text(
            text = "Study heatmap",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your focus rhythm across the last 8 weeks. Tap a day for details.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .pointerInput(days) {
                    detectTapGestures { tapOffset ->
                        if (days.size != StudyHeatmapCalculator.DAYS_TO_SHOW) {
                            return@detectTapGestures
                        }
                        val column = (tapOffset.x / (size.width / 7f)).toInt()
                        val row = (tapOffset.y / (size.height / 8f)).toInt()
                        val index = row * 7 + column
                        days.getOrNull(index)
                            ?.takeUnless { day -> day.isFuture }
                            ?.let { day -> selectedDay = day }
                    }
                }
        ) {
            if (days.size != StudyHeatmapCalculator.DAYS_TO_SHOW) return@Canvas

            val columns = 7
            val rows = 8
            val gap = 4.dp.toPx()
            val slotWidth = size.width / columns
            val slotHeight = size.height / rows
            val squareSize = min(slotWidth, slotHeight) - gap

            days.forEachIndexed { index, day ->
                val column = index % columns
                val row = index / columns
                val intensity = day.focusMinutes / maxMinutes.toFloat()
                val cellColor = when {
                    day.isFuture -> futureColor
                    day.focusMinutes == 0 -> emptyColor
                    intensity <= 0.25f -> heatmapLow.copy(alpha = 0.58f)
                    intensity <= 0.50f -> heatmapMedium.copy(alpha = 0.72f)
                    intensity <= 0.75f -> heatmapHigh.copy(alpha = 0.86f)
                    else -> heatmapPeak
                }
                val left = column * slotWidth + (slotWidth - squareSize) / 2f
                val top = row * slotHeight + (slotHeight - squareSize) / 2f

                drawRoundRect(
                    color = cellColor,
                    topLeft = Offset(left, top),
                    size = Size(squareSize, squareSize),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
                if (selectedDay?.date == day.date) {
                    drawRoundRect(
                        color = selectionBorderColor,
                        topLeft = Offset(left, top),
                        size = Size(squareSize, squareSize),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
                if (day.date == today) {
                    val markerCenter = Offset(
                        x = left + squareSize - 5.dp.toPx(),
                        y = top + 5.dp.toPx()
                    )
                    drawCircle(
                        color = todayMarkerOuterColor,
                        radius = 4.dp.toPx(),
                        center = markerCenter
                    )
                    drawCircle(
                        color = primary,
                        radius = 2.4.dp.toPx(),
                        center = markerCenter
                    )
                }
            }
        }

        selectedDay?.let { day ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.date.format(dayFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatFocusDuration(day.focusMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (day.focusMinutes > 0) primary else Color.Unspecified,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            listOf(0.16f, 0.32f, 0.52f, 0.74f, 1f).forEach { alpha ->
                val legendColor = when (alpha) {
                    0.16f -> emptyColor
                    0.32f -> heatmapLow.copy(alpha = 0.58f)
                    0.52f -> heatmapMedium.copy(alpha = 0.72f)
                    0.74f -> heatmapHigh.copy(alpha = 0.86f)
                    else -> heatmapPeak
                }
                Canvas(modifier = Modifier.padding(start = 5.dp).size(12.dp)) {
                    drawRoundRect(
                        color = legendColor,
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
            Text(
                text = "More",
                modifier = Modifier.padding(start = 5.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StudyActivityCard(
    evidenceCount: Int,
    taskFocusStats: List<TaskFocusStat>,
    onOpenJournal: () -> Unit,
    sessions: List<RecentFocusSession>,
    onDeleteSession: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var sessionToDelete by remember {
        mutableStateOf<RecentFocusSession?>(null)
    }

    HabitlyCard(
        modifier = modifier
    ) {
        Text(
            text = "Study activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Journal snapshots and your latest focus blocks.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryIcon(
                icon = Icons.Outlined.Collections,
                accentColor = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Study journal",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (evidenceCount == 0) {
                        "Add a photo after a focus session."
                    } else {
                        "$evidenceCount study snapshots saved"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Button(onClick = onOpenJournal, modifier = Modifier.fillMaxWidth()) {
            Text("Open study journal")
        }

        Text(
            text = "Task focus",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Focus time grouped by linked study tasks.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (taskFocusStats.isEmpty()) {
            Text(
                text = "Choose a task before starting the timer to build this summary.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            taskFocusStats.forEachIndexed { index, stat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(22.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stat.taskTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "linked focus time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = formatFocusDuration(stat.focusMinutes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "Recent sessions",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (sessions.isEmpty()) {
            Text(
                text = "Completed focus sessions will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(22.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = formatFocusDuration(session.durationMinutes),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = session.taskTitle ?: "Focus session",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.completedLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { sessionToDelete = session }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete session",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = {
                Text(text = "Delete session?")
            },
            text = {
                Text(
                    text = "This will remove the ${formatFocusDuration(session.durationMinutes)} focus session from your history."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSession(session.id)
                        sessionToDelete = null
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { sessionToDelete = null }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
