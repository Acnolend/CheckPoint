package com.example.checkpoint.core.backend.domain.valueobjects

import java.util.regex.Pattern

data class UserName(
    private var _name: String
) {
    init {
        validateName(_name)
    }

    companion object {
        private const val MIN_LENGTH = 4
        private const val MAX_LENGTH = 20
        private const val REGULAR_EXPRESSION = "^[a-zA-Z].*[a-zA-Z0-9]$"
        private const val ERROR_EMPTY = "The name of the user cannot be empty"
        private const val ERROR_MIN_LENGTH = "The name of the user cannot have less than $MIN_LENGTH characters"
        private const val ERROR_MAX_LENGTH = "The name of the user cannot have more than $MAX_LENGTH characters"
        private const val ERROR_WRONG_FORMAT = "The name of the user doesn't comply with the pattern"
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


