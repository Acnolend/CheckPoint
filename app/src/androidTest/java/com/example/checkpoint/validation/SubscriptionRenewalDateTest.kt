package com.example.checkpoint.validation

import java.time.LocalDateTime
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionRenewalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SubscriptionRenewalDateTest {

    @Test
    fun shouldThrowExceptionIfRenewalDateIsInThePast() {
        val pastDate = LocalDateTime.now().minusDays(1)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionRenewalDate(pastDate)
        }

        assertEquals("The renewal date must be in the future", exception.message)
    }

    @Test
    fun shouldAllowCreationWithFutureDate() {
        val futureDate = LocalDateTime.now().plusDays(1)
        val renewalDate = SubscriptionRenewalDate(futureDate)

        assertEquals(futureDate, renewalDate.dateTime)
    }
}