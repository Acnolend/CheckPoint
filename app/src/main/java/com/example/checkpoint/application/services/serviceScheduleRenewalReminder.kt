package com.example.checkpoint.application.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.checkpoint.core.backend.adapter.LocalDateTimeAdapter
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.workers.SubscriptionRenewalWorker
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleRenewalReminder(context: Context, subscription: Subscription, isDebugMode: Boolean = false) {
    val delay = if (isDebugMode) {
        10000L  // 10 segundos en modo debug
    } else {
        val renewalDateMillis = subscription.renewalDate.dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val delayMillis = renewalDateMillis - System.currentTimeMillis()
        if (delayMillis > 0) delayMillis else 0L
    }

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    val data = workDataOf(
        "subscription" to gson.toJson(subscription)
    )

    val workRequest = OneTimeWorkRequestBuilder<SubscriptionRenewalWorker>()
        .setInputData(data)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}