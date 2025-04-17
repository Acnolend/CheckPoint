package com.example.checkpoint.core.backend.domain.valueobjects

import android.os.Build
import java.time.LocalDateTime

data class SubscriptionRenewalDate(
    private var _dateTime: LocalDateTime,
) {
    init {
        validateDateTime(dateTime)
    }

    companion object {
        private const val ERROR_WRONG_FORMAT = "The renewal date must be in the future"
    }

    var dateTime: LocalDateTime
        get() = _dateTime
        set(value) {
            validateDateTime(value)
            _dateTime = value
        }

    private fun validateDateTime(dateTime: LocalDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            require(dateTime.isAfter(LocalDateTime.now())) { ERROR_WRONG_FORMAT }
        }
    }
}
