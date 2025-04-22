package com.example.checkpoint.validation

import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType

import com.example.checkpoint.core.backend.domain.valueobjects.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SubscriptionTest {

    @Test
    fun shouldCreateValidSubscription() {
        val name = SubscriptionName("ValidName1")
        val image = SubscriptionImage("https://www.example.com/image.jpg")
        val cost = SubscriptionCost(50.0, SubscriptionCostType.MONTHLY)
        val reminder = SubscriptionReminder(LocalDateTime.now().plusDays(1))
        val renewalDate = SubscriptionRenewalDate(LocalDateTime.now().plusDays(2))

        val subscription = Subscription(
            name = name,
            image = image,
            cost = cost,
            reminder = reminder,
            renewalDate = renewalDate,
            ID = "12345"
        )

        assertEquals("ValidName1", subscription.name.name)
        assertEquals("https://www.example.com/image.jpg", subscription.image.image)
        assertEquals(50.0, subscription.cost.cost, 0.0001)
        assertEquals(LocalDateTime.now().plusDays(1), subscription.reminder.dateTime)
        assertEquals(LocalDateTime.now().plusDays(2), subscription.renewalDate.dateTime)
        assertEquals("12345", subscription.ID)
    }
}