package com.example.checkpoint.validation

import com.example.checkpoint.core.backend.domain.valueobjects.UserEmail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test


class UserEmailTest {

    @Test
    fun shouldThrowExceptionIfEmailIsEmpty() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserEmail("")
        }
        assertEquals("Email cannot be empty!", exception.message)
    }

    @Test
    fun shouldThrowExceptionIfEmailFormatIsInvalid() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserEmail("invalidemail.com")
        }
        assertEquals("Please enter a valid email address!", exception.message)
    }

    @Test
    fun shouldAllowValidEmail() {
        val validEmail = UserEmail("user@example.com")
        assertEquals("user@example.com", validEmail.email)
    }
}