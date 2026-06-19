package com.example.habitly.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habitly.HabitlyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val application = context.applicationContext as HabitlyApplication

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = application.settingsRepository.settings.first()
                if (settings.isDailyReminderEnabled) {
                    application.studyReminderManager.scheduleDailyReminder(
                        hour = settings.reminderHour,
                        minute = settings.reminderMinute
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
