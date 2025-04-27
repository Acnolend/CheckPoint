package com.example.checkpoint.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.checkpoint.R
import com.example.checkpoint.application.services.scheduleReminder
import com.example.checkpoint.core.backend.adapter.LocalDateTimeAdapter
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.google.gson.GsonBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.LocalDateTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

class SubscriptionReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val appwriteService = AppwriteService(context)
    private val subscriptionRepository = SubscriptionRepository(appwriteService)

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val subscriptionJson = inputData.getString("subscription")
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        val subscription = gson.fromJson(subscriptionJson, Subscription::class.java)
        if(subscription.cost.type != SubscriptionCostType.DAILY) {
            sendNotification(subscription)
            GlobalScope.launch {
                val updatedReminderDate = calculateNextReminderDate(subscription)
                subscriptionRepository.updateSubscriptionReminderDate(subscription.ID, updatedReminderDate)
                val subscriptionNewDate = subscription.copy(reminder = subscription.reminder.copy(_dateTime = updatedReminderDate))
                scheduleReminder(applicationContext, subscriptionNewDate)
            }
        }
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextReminderDate(subscription: Subscription): LocalDateTime {
        val nextRenewal = when (subscription.cost.type) {
            SubscriptionCostType.DAILY -> subscription.renewalDate.dateTime.plusDays(1)
            SubscriptionCostType.WEEKLY -> subscription.renewalDate.dateTime.plusWeeks(1)
            SubscriptionCostType.BIWEEKLY -> subscription.renewalDate.dateTime.plusWeeks(2)
            SubscriptionCostType.MONTHLY -> subscription.renewalDate.dateTime.plusMonths(1)
            SubscriptionCostType.BIMONTHLY -> subscription.renewalDate.dateTime.plusMonths(2)
            SubscriptionCostType.QUARTERLY -> subscription.renewalDate.dateTime.plusMonths(3)
            SubscriptionCostType.SEMIANNUAL -> subscription.renewalDate.dateTime.plusMonths(6)
            SubscriptionCostType.ANNUAL -> subscription.renewalDate.dateTime.plusYears(1)
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
        val notificationText = "$daysRemaining days until your subscription renews!"
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.icon_info)
            .setContentTitle("Subscription Reminder")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}