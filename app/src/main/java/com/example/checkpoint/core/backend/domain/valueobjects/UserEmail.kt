package com.example.checkpoint.core.backend.domain.valueobjects

import java.util.regex.Pattern

data class UserEmail(
    private var _email: String
) {
    init {
        validateEmail(_email)
    }

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$"
        private const val ERROR_EMPTY = "Email cannot be empty!"
        private const val ERROR_INVALID_FORMAT = "Please enter a valid email address!"
    }

    var email: String
        get() = _email
        set(value) {
            validateEmail(value)
            _email = value
        }

    private fun validateEmail(email: String) {
        require(email.isNotEmpty()) { ERROR_EMPTY }
        require(Pattern.matches(EMAIL_REGEX, email)) { ERROR_INVALID_FORMAT }
    }
}
