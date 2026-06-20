package com.example.habitly.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.habitly.ui.screens.DashboardScreen
import com.example.habitly.ui.screens.SettingsScreen
import com.example.habitly.ui.screens.PlannerScreen
import com.example.habitly.ui.screens.StatisticsScreen
import com.example.habitly.ui.screens.TasksScreen
import com.example.habitly.ui.screens.TimerScreen
import com.example.habitly.ui.planner.PlannedFocusRequest

@Composable
fun HabitlyApp() {
    var selectedDestination by rememberSaveable {
        mutableStateOf(AppDestination.Dashboard)
    }
    var plannedFocusRequest by remember { mutableStateOf<PlannedFocusRequest?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            HabitlyBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = {
                    if (it == AppDestination.Timer) plannedFocusRequest = null
                    selectedDestination = it
                }
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (selectedDestination) {
            AppDestination.Dashboard -> DashboardScreen(
                modifier = screenModifier,
                onOpenPlanner = { selectedDestination = AppDestination.Planner }
            )
            AppDestination.Planner -> PlannerScreen(
                modifier = screenModifier,
                onStartFocus = { request ->
                    plannedFocusRequest = request
                    selectedDestination = AppDestination.Timer
                }
            )
            AppDestination.Tasks -> TasksScreen(screenModifier)
            AppDestination.Timer -> TimerScreen(
                modifier = screenModifier,
                plannedFocusRequest = plannedFocusRequest
            )
            AppDestination.Statistics -> StatisticsScreen(screenModifier)
            AppDestination.Settings -> SettingsScreen(screenModifier)
        }
    }
}

@Composable
private fun HabitlyBottomBar(
    selectedDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        AppDestination.entries.filter { destination -> destination.showInBottomBar }
            .forEach { destination ->
            NavigationBarItem(
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(text = destination.title)
                }
            )
        }
    }
}
