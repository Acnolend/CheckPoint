package com.example.checkpoint.validation

import com.example.checkpoint.core.backend.domain.valueobjects.UserPassword
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UserPasswordTest {

    @Test
    fun shouldThrowExceptionIfPasswordIsEmpty() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPassword("")
        }
        assertEquals("Password cannot be empty!", exception.message)
    }

    @Test
    fun shouldThrowExceptionIfPasswordIsTooShort() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPassword("Short1")
        }
        assertEquals(
            "Password is too short! It must be at least 8 characters.",
            exception.message
        )
    }

    @Test
    fun shouldThrowExceptionIfPasswordIsTooLong() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPassword("ThisIsAVeryLongPassword123")
        }
        assertEquals(
            "Password is too long! It must be no longer than 20 characters.",
            exception.message
        )
    }

    @Test
    fun shouldThrowExceptionIfPasswordDoesNotMatchTheRequiredPattern() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPassword("password")
        }
        assertEquals(
            "Password must contain at least one uppercase letter, one lowercase letter, and one number.",
            exception.message
        )
    }

    @Test
    fun shouldAllowValidPassword() {
        val validPassword = UserPassword("Valid123")
        assertEquals("Valid123", validPassword.password)
    }
}
