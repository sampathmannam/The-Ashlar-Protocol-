package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.tools.PracticeReminder

/**
 * Fires a single practice's cue-anchored reminder (T1.5): a gentle, skippable notification built from
 * the practice's own words. Never loss-framed — the copy comes from PracticeReminder, which is tested
 * to contain no streak/FOMO language. Reminders are opt-in per practice and cancelled when it's removed.
 */
class PracticeReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_ID) ?: return Result.success()
        val anchor = inputData.getString(KEY_ANCHOR) ?: return Result.success()
        val action = inputData.getString(KEY_ACTION) ?: return Result.success()
        showNotification(id, anchor, action)
        return Result.success()
    }

    private fun showNotification(id: String, anchor: String, action: String) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "practice_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Practice reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Gentle, skippable reminders for the practices you set — never a streak nudge."
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val body = PracticeReminder.reminderBody(action)
        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(PracticeReminder.reminderTitle(anchor))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    companion object {
        const val KEY_ID = "practice_id"
        const val KEY_ANCHOR = "anchor"
        const val KEY_ACTION = "action"
    }
}
