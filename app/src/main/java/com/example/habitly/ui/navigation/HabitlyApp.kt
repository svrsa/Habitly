package com.example.habitly.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.habitly.ui.screens.DashboardScreen
import com.example.habitly.ui.screens.SettingsScreen
import com.example.habitly.ui.screens.StatisticsScreen
import com.example.habitly.ui.screens.TasksScreen
import com.example.habitly.ui.screens.TimerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitlyApp() {
    var selectedDestination by rememberSaveable {
        mutableStateOf(AppDestination.Dashboard)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Habitly")
                }
            )
        },
        bottomBar = {
            HabitlyBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it }
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (selectedDestination) {
            AppDestination.Dashboard -> DashboardScreen(screenModifier)
            AppDestination.Tasks -> TasksScreen(screenModifier)
            AppDestination.Timer -> TimerScreen(screenModifier)
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
    NavigationBar {
        AppDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
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
