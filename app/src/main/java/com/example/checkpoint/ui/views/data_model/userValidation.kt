package com.example.checkpoint.ui.views.data_model

import com.example.checkpoint.core.backend.domain.valueobjects.UserEmail
import com.example.checkpoint.core.backend.domain.valueobjects.UserName
import com.example.checkpoint.core.backend.domain.valueobjects.UserPassword

fun validateUserNameInput(input: String): String? {
    return try {
        UserName(input)
        null
    } catch (e: IllegalArgumentException) {
        when (e.message) {
            UserName.ERROR_EMPTY -> "user_name_error_empty"
            UserName.ERROR_MAX_LENGTH -> "user_name_error_maxLength"
            UserName.ERROR_MIN_LENGTH -> "user_name_error_minLength"
            UserName.ERROR_WRONG_FORMAT -> "user_name_error_wrongFormat"
            else -> null
        }
    }
}

fun validateUserPasswordInput(input: String): String? {
    return try {
        UserPassword(input)
        null
    } catch (e: IllegalArgumentException) {
        when (e.message) {
            UserPassword.ERROR_EMPTY -> "user_password_error_empty"
            UserPassword.ERROR_MAX_LENGTH -> "user_password_error_maxLength"
            UserPassword.ERROR_MIN_LENGTH -> "user_password_error_minLength"
            UserPassword.ERROR_WRONG_FORMAT -> "user_password_error_wrongFormat"
            else -> null
        }
    }
}

fun validateUserEmailInput(input: String): String? {
    return try {
        UserEmail(input)
        null
    } catch (e: IllegalArgumentException) {
        when (e.message) {
            UserEmail.ERROR_EMPTY -> "user_email_error_empty"
            UserEmail.ERROR_INVALID_FORMAT -> "user_email_error_wrongFormat"
            else -> null
        }
    }
}