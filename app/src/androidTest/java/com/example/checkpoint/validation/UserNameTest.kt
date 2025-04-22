package com.example.checkpoint.validation

import com.example.checkpoint.core.backend.domain.valueobjects.UserName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UserNameTest {

    @Test
    fun shouldThrowExceptionIfNameIsEmpty() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserName("")
        }
        assertEquals("No name? That's a problem!", exception.message)
    }

    @Test
    fun shouldThrowExceptionIfNameIsTooShort() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserName("ab")
        }
        assertEquals("Name too short! It must be at least 3 characters!", exception.message)
    }

    @Test
    fun shouldThrowExceptionIfNameIsTooLong() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserName("a".repeat(16))
        }
        assertEquals("Name too long! Keep it under 15 characters, please!", exception.message)
    }

    @Test
    fun shouldThrowExceptionIfNameDoesNotComplyWithThePattern() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserName("12345")
        }
        assertEquals("Oops! The name must start with a letter and end with a letter or number!", exception.message)
    }

    @Test
    fun shouldAllowValidName() {
        val validName = UserName("ValidName1")
        assertEquals("ValidName1", validName.name)
    }
}
