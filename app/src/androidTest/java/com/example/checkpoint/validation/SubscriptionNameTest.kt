package com.example.checkpoint.validation


import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SubscriptionNameTest {

    @Test
    fun shouldThrowExceptionIfNameIsTooShort() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionName("abc")
        }
        assertEquals(
            "The name of the subscription cannot have less than 4 characters",
            exception.message
        )
    }

    @Test
    fun shouldThrowExceptionIfNameIsTooLong() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionName("a".repeat(101))
        }
        assertEquals(
            "The name of the subscription cannot have more than 100 characters",
            exception.message
        )
    }

    @Test
    fun shouldThrowExceptionIfNameDoesntComplyWithPattern() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionName("1234")
        }
        assertEquals(
            "The name of the subscription doesn't comply with the pattern",
            exception.message
        )
    }

    @Test
    fun shouldAllowValidName() {
        val validName = SubscriptionName("ValidName1")

        assertEquals("ValidName1", validName.name)
    }
}
