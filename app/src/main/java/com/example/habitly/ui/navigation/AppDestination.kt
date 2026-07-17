package com.example.habitly.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val title: String,
    val icon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    Dashboard(
        title = "Dashboard",
        icon = Icons.Outlined.Home
    ),
    Planner(
        title = "Planner",
        icon = Icons.Outlined.CalendarMonth,
        showInBottomBar = false
    ),
    EvidenceCapture(
        title = "Camera",
        icon = Icons.Outlined.PhotoCamera,
        showInBottomBar = false
    ),
    Journal(
        title = "Journal",
        icon = Icons.Outlined.Collections,
        showInBottomBar = false
    ),
    Tasks(
        title = "Tasks",
        icon = Icons.Outlined.CheckCircle
    ),
    TaskDetail(
        title = "Task Detail",
        icon = Icons.Outlined.Description,
        showInBottomBar = false
    ),
    Timer(
        title = "Timer",
        icon = Icons.Outlined.PlayArrow
    ),
    Statistics(
        title = "Statistics",
        icon = Icons.Outlined.BarChart
    ),
    Settings(
        title = "Settings",
        icon = Icons.Outlined.Settings
    )
}
