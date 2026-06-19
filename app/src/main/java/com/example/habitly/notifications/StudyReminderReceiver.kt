package com.example.habitly.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        StudyReminderManager(context.applicationContext).showReminderNotification()
    }
}
