package com.example.checkpoint.ui.views.data_model

import com.example.checkpoint.core.backend.domain.valueobjects.UserEmail
import com.example.checkpoint.core.backend.domain.valueobjects.UserName
import com.example.checkpoint.core.backend.domain.valueobjects.UserPassword

fun validateUserNameInput(input: String): String? {
    return try {
        UserName(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}

fun validateUserPasswordInput(input: String): String? {
    return try {
        UserPassword(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}

fun validateUserEmailInput(input: String): String? {
    return try {
        UserEmail(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}