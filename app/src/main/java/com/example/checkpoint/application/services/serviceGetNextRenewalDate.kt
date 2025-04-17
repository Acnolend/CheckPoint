package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.core.backend.domain.entities.Subscription
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun getNextRenewalDate(subscriptions: List<Subscription>): Pair<String, String>? {
    val currentDate = System.currentTimeMillis()

    val nextRenewal = subscriptions
        .filter { true }
        .map {
            val name = it.name.name
            val dateTime = it.renewalDate.dateTime
            val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            Triple(name, millis, dateTime)
        }
        .filter { it.second > currentDate }
        .minByOrNull { it.second }

    return nextRenewal?.let {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date.from(it.third.atZone(ZoneId.systemDefault()).toInstant()))
        Pair(it.first, formattedDate)
    }
}