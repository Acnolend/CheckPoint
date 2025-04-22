package com.example.checkpoint.validation

import java.time.LocalDateTime
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test


class SubscriptionReminderTest {

    @Test
    fun shouldThrowExceptionIfReminderDateIsInThePast() {
        val pastDate = LocalDateTime.now().minusDays(1)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionReminder(pastDate)
        }

        assertEquals(
            "The reminder of the subscription must be in the future",
            exception.message
        )
    }

    @Test
    fun shouldAllowCreationWithFutureDate() {
        val futureDate = LocalDateTime.now().plusDays(1)
        val reminder = SubscriptionReminder(futureDate)

        assertEquals(futureDate, reminder.dateTime)
    }
}