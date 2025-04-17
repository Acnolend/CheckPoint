package com.example.checkpoint.core.backend.domain.valueobjects
import java.util.regex.Pattern

data class SubscriptionImage(
    private var _image: String
) {
    init {
        validateImage(_image)
    }

    companion object {
        private const val ERROR_WRONG_FORMAT = "The image of the subscription must be a valid content, file, or web URL"
    }

    var image: String
        get() = _image
        set(value) {
            validateImage(value)
            _image = value
        }

    private fun validateImage(image: String) {
        val urlPattern = Pattern.compile(
            "^(https?|ftp)://[A-Za-z0-9.-]+(?:\\.[A-Za-z]{2,})?(:\\d+)?(/\\S*)?$"
        )
        val matcher = urlPattern.matcher(image)
        require(matcher.matches()) {
            ERROR_WRONG_FORMAT
        }
    }
}