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
import com.example.checkpoint.application.services.scheduleRenewal
import com.example.checkpoint.core.backend.adapter.LocalDateTimeAdapter
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.PaymentRepository
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class SubscriptionRenewalWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val appwriteService = AppwriteService(context)
    val authService = AuthService(context)
    private val subscriptionRepository = SubscriptionRepository(appwriteService)
    private val paymentRepository = PaymentRepository(appwriteService)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val subscriptionJson = inputData.getString("subscription")

        if (subscriptionJson.isNullOrEmpty()) {
            return Result.failure()
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        val subscription = gson.fromJson(subscriptionJson, Subscription::class.java)

        sendNotification(subscription)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = subscription.renewalDate.dateTime.format(formatter)
        val userId = authService.getUserIdActual()
            .toString()
            .substringAfter("(")
            .substringBefore(")")

        paymentRepository.savePayment(
            userId,
            subscription.name.name,
            UUID.randomUUID().toString().replace("-", ""),
            subscription.cost.cost,
            formattedDate
        )

        val updatedRenewalDate = calculateNextRenewalDate(subscription)
        subscriptionRepository.updateSubscriptionRenewalDate(subscription.ID, updatedRenewalDate)

        val subscriptionNewDate = subscription.copy(
            reminder = subscription.reminder.copy(_dateTime = updatedRenewalDate)
        )

        scheduleRenewal(applicationContext, subscriptionNewDate)

        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextRenewalDate(subscription: Subscription): LocalDateTime {
        return when (subscription.cost.type) {
            SubscriptionCostType.MONTHLY -> subscription.renewalDate.dateTime.plusMonths(1)
            SubscriptionCostType.ANNUAL -> subscription.renewalDate.dateTime.plusYears(1)
            SubscriptionCostType.WEEKLY -> subscription.renewalDate.dateTime.plusWeeks(1)
            SubscriptionCostType.BIWEEKLY -> subscription.renewalDate.dateTime.plusWeeks(2)
            SubscriptionCostType.BIMONTHLY -> subscription.renewalDate.dateTime.plusMonths(2)
            SubscriptionCostType.QUARTERLY -> subscription.renewalDate.dateTime.plusMonths(3)
            SubscriptionCostType.SEMIANNUAL -> subscription.renewalDate.dateTime.plusMonths(6)
            SubscriptionCostType.DAILY -> subscription.renewalDate.dateTime.plusDays(1)
        }
    }

    private fun sendNotification(subscription: Subscription) {
        val channelId = "subscription_renewal_channel"
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

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.icon_money)
            .setContentTitle(subscription.name.name)
            .setContentText(applicationContext.getString(R.string.subscription_renewal_notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}