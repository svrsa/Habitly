package com.example.habitly.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.habitly.MainActivity
import com.example.habitly.R
import java.time.ZonedDateTime

class StudyReminderManager(
    private val context: Context
) {
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Study reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminders for your study routine"
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun scheduleDailyReminder(
        hour: Int,
        minute: Int,
        now: ZonedDateTime = ZonedDateTime.now()
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerAt(now, hour, minute).toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            reminderPendingIntent()
        )
    }

    fun cancelDailyReminder() {
        context.getSystemService(AlarmManager::class.java)
            .cancel(reminderPendingIntent())
    }

    fun showReminderNotification() {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Ready for a study streak?")
            .setContentText("One focused session today keeps your learning rhythm alive.")
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun reminderPendingIntent(): PendingIntent {
        val intent = Intent(context, StudyReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val CHANNEL_ID = "daily_study_reminders"
        private const val NOTIFICATION_ID = 1001
        private const val REMINDER_REQUEST_CODE = 2001
        private const val OPEN_APP_REQUEST_CODE = 2002

        fun nextTriggerAt(
            now: ZonedDateTime,
            hour: Int,
            minute: Int
        ): ZonedDateTime {
            require(hour in 0..23)
            require(minute in 0..59)

            val todayAtReminderTime = now
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)

            return if (todayAtReminderTime.isAfter(now)) {
                todayAtReminderTime
            } else {
                todayAtReminderTime.plusDays(1)
            }
        }
    }
}
