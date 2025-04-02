package com.example.checkpoint.core.backend.domain.valueobjects

import android.net.Uri

data class SubscriptionImage(
    private var _image: Uri
) {
    init {
        validateImage(_image)
    }

    companion object {
        private const val ERROR_WRONG_FORMAT = "The image of the subscription must be a valid content, file, or web URL"
    }

    var image: Uri
        get() = _image
        set(value) {
            validateImage(value)
            _image = value
        }

    private fun validateImage(image: Uri) {
        require(image.scheme == "content" || image.scheme == "file" || image.scheme == "http" || image.scheme == "https") {
            ERROR_WRONG_FORMAT
        }
    }
}