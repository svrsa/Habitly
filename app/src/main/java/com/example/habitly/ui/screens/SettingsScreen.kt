package com.example.habitly.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.rememberTimePickerState
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.settings.SettingsViewModel
import com.example.habitly.ui.settings.SettingsViewModelFactory
import com.example.habitly.ui.settings.SettingsUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val application = context.applicationContext as HabitlyApplication
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(application.settingsRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val focusDurations = listOf(15, 25, 45)
    var dailyGoalDraft by rememberSaveable {
        mutableIntStateOf(SettingsUiState.DEFAULT_DAILY_STUDY_GOAL_MINUTES)
    }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.dailyStudyGoalMinutes) {
        dailyGoalDraft = uiState.dailyStudyGoalMinutes
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setDailyReminderEnabled(true)
        }
    }

    val onReminderEnabledChange: (Boolean) -> Unit = { isEnabled ->
        when {
            !isEnabled -> viewModel.setDailyReminderEnabled(false)
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.setDailyReminderEnabled(true)
            }
            else -> notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    HabitlyScreen(
        title = "Settings",
        subtitle = "Adjust your study defaults.",
        modifier = modifier
    ) {
        HabitlyCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Default focus duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    focusDurations.forEach { duration ->
                        FilterChip(
                            selected = uiState.defaultFocusDurationMinutes == duration,
                            onClick = { viewModel.selectDefaultFocusDuration(duration) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = uiState.defaultFocusDurationMinutes == duration,
                                borderColor = MaterialTheme.colorScheme.surfaceVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            label = {
                                Text(text = "$duration min")
                            }
                        )
                    }
                }
            }
        }

        HabitlyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Daily study goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose how much focus time you want each day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$dailyGoalDraft min",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = dailyGoalDraft.toFloat(),
                onValueChange = { value ->
                    val step = SettingsUiState.DAILY_STUDY_GOAL_STEP_MINUTES
                    dailyGoalDraft = (value / step).roundToInt() * step
                },
                onValueChangeFinished = {
                    viewModel.setDailyStudyGoal(dailyGoalDraft)
                },
                valueRange = SettingsUiState.MIN_DAILY_STUDY_GOAL_MINUTES.toFloat()..
                    SettingsUiState.MAX_DAILY_STUDY_GOAL_MINUTES.toFloat(),
                steps = (
                    SettingsUiState.MAX_DAILY_STUDY_GOAL_MINUTES -
                        SettingsUiState.MIN_DAILY_STUDY_GOAL_MINUTES
                    ) / SettingsUiState.DAILY_STUDY_GOAL_STEP_MINUTES - 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "30 min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "5 h",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HabitlyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Daily reminder",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Every day at %02d:%02d.".format(
                            uiState.reminderHour,
                            uiState.reminderMinute
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.isDailyReminderEnabled,
                    onCheckedChange = onReminderEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            TextButton(onClick = { showTimePicker = true }) {
                Text(text = "Change reminder time")
            }
        }

        HabitlyCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Habitly",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Study habit tracker for tasks, focus sessions, and progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.reminderHour,
            initialMinute = uiState.reminderMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(text = "Reminder time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setDailyReminderTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
