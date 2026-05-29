package com.example.habitly.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val title: String,
    val icon: ImageVector
) {
    Dashboard(
        title = "Dashboard",
        icon = Icons.Outlined.Home
    ),
    Tasks(
        title = "Tasks",
        icon = Icons.Outlined.CheckCircle
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
