package com.example.checkpoint.validation

import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows

import org.junit.Test

class SubscriptionCostTest {

    @Test
    fun shouldThrowExceptionIfCostIsNegative() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionCost(-10.0, SubscriptionCostType.MONTHLY)
        }

        assertEquals("The cost of the subscription must be a positive value or zero", exception.message)
    }

    @Test
    fun shouldAllowZeroOrPositiveCost() {
        val costZero = SubscriptionCost(0.0, SubscriptionCostType.MONTHLY)
        val costPositive = SubscriptionCost(100.0, SubscriptionCostType.MONTHLY)

        val delta = 0.0001
        assertEquals(0.0, costZero.cost, delta)
        assertEquals(100.0, costPositive.cost, delta)
    }
}