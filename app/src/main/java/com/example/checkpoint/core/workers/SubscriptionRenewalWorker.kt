package com.example.checkpoint.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
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

class SubscriptionRenewalWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

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
        sendNotification()
        GlobalScope.launch {
            val updatedRenewalDate = calculateNextRenewalDate(subscription)
            subscriptionRepository.updateSubscriptionRenewalDate(subscription.ID, updatedRenewalDate)
        }

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextRenewalDate(subscription: Subscription): LocalDateTime {
        return when (subscription.cost.type) {
            SubscriptionCostType.MONTHLY -> subscription.renewalDate.dateTime.plusMonths(1)
            SubscriptionCostType.ANNUAL -> subscription.renewalDate.dateTime.plusYears(1)
        }
    }


    private fun sendNotification() {
        val channelId = "subscription_renewal_channel"
        val notificationId = 1

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

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Subscription Renewal Reminder")
            .setContentText("Your subscription is due for renewal!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}