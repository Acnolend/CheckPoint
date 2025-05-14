package com.example.checkpoint.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.checkpoint.R
import com.example.checkpoint.application.services.scheduleReminder
import com.example.checkpoint.core.backend.adapter.LocalDateTimeAdapter
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class SubscriptionReminderWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val appwriteService = AppwriteService(context)
    private val subscriptionRepository = SubscriptionRepository(appwriteService)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val subscriptionJson = inputData.getString("subscription")
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        val subscription = gson.fromJson(subscriptionJson, Subscription::class.java)

        if (subscription.cost.type != SubscriptionCostType.DAILY) {
            sendNotification(subscription)
        }

        val updatedReminderDate = calculateNextReminderDate(subscription)
        subscriptionRepository.updateSubscriptionReminderDate(subscription.ID, updatedReminderDate)

        val subscriptionNewDate = subscription.copy(reminder = subscription.reminder.copy(_dateTime = updatedReminderDate))
        scheduleReminder(applicationContext, subscriptionNewDate)

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextReminderDate(subscription: Subscription): LocalDateTime {
        var nextRenewal = subscription.renewalDate.dateTime
        while (nextRenewal.isBefore(LocalDateTime.now())) {
            nextRenewal = when (subscription.cost.type) {
                SubscriptionCostType.DAILY -> nextRenewal.plusDays(1)
                SubscriptionCostType.WEEKLY -> nextRenewal.plusWeeks(1)
                SubscriptionCostType.BIWEEKLY -> nextRenewal.plusWeeks(2)
                SubscriptionCostType.MONTHLY -> nextRenewal.plusMonths(1)
                SubscriptionCostType.BIMONTHLY -> nextRenewal.plusMonths(2)
                SubscriptionCostType.QUARTERLY -> nextRenewal.plusMonths(3)
                SubscriptionCostType.SEMIANNUAL -> nextRenewal.plusMonths(6)
                SubscriptionCostType.ANNUAL -> nextRenewal.plusYears(1)
            }
        }

        val daysBefore = java.time.Duration.between(
            subscription.reminder.dateTime,
            subscription.renewalDate.dateTime
        ).toDays()

        return nextRenewal.minusDays(daysBefore)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(subscription: Subscription) {
        val channelId = "subscription_reminder_channel"
        val notificationId = subscription.ID.hashCode()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Subscription Renewal",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for subscription renewal reminders"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val daysRemaining = ChronoUnit.DAYS.between(subscription.reminder.dateTime, subscription.renewalDate.dateTime)
        val notificationText = applicationContext.getString(R.string.subscription_reminder_notification_text, daysRemaining)
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.icon_info)
            .setContentTitle(subscription.name.name)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}