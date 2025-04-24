package com.example.checkpoint.core.backend.domain.valueobjects

import java.util.regex.Pattern

data class UserPassword(
    private var _password: String
) {
    init {
        validatePassword(_password)
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 20
        private const val PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).+$"
        const val ERROR_EMPTY = "Password cannot be empty!"
        const val ERROR_MIN_LENGTH = "Password is too short! It must be at least $MIN_LENGTH characters."
        const val ERROR_MAX_LENGTH = "Password is too long! It must be no longer than $MAX_LENGTH characters."
        const val ERROR_WRONG_FORMAT = "Password must contain at least one uppercase letter, one lowercase letter, and one number."
    }

    var password: String
        get() = _password
        set(value) {
            validatePassword(value)
            _password = value
        }

    private fun validatePassword(password: String) {
        require(password.isNotEmpty()) { ERROR_EMPTY }
        require(password.length >= MIN_LENGTH) { ERROR_MIN_LENGTH }
        require(password.length <= MAX_LENGTH) { ERROR_MAX_LENGTH }
        require(Pattern.matches(PASSWORD_REGEX, password)) { ERROR_WRONG_FORMAT }
    }
}
