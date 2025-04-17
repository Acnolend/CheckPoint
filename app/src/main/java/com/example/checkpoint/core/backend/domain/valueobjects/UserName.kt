package com.example.checkpoint.core.backend.domain.valueobjects

import java.util.regex.Pattern

data class UserName(
    private var _name: String
) {
    init {
        validateName(_name)
    }

    companion object {
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 15
        private const val REGULAR_EXPRESSION = "^[a-zA-Z].*[a-zA-Z0-9]$"
        private const val ERROR_EMPTY = "No name? That's a problem!"
        private const val ERROR_MIN_LENGTH = "Name too short! It must be at least $MIN_LENGTH characters!"
        private const val ERROR_MAX_LENGTH = "Name too long! Keep it under $MAX_LENGTH characters, please!"
        private const val ERROR_WRONG_FORMAT = "Oops! The name must start with a letter and end with a letter or number!"
    }

    var name: String
        get() = _name
        set(value) {
            validateName(value)
            _name = value
        }

    private fun validateName(name: String) {
        require(name.isNotEmpty()) { ERROR_EMPTY }
        require(name.length >= MIN_LENGTH) { ERROR_MIN_LENGTH }
        require(name.length <= MAX_LENGTH) { ERROR_MAX_LENGTH }
        require(Pattern.matches(REGULAR_EXPRESSION, name)) { ERROR_WRONG_FORMAT }
    }
}


