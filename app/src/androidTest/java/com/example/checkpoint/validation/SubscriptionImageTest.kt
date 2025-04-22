package com.example.checkpoint.validation


import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SubscriptionImageTest {

    @Test
    fun shouldThrowExceptionIfImageURLIsInvalid() {
        val invalidImage = "invalid_image_url"

        val exception = assertThrows(IllegalArgumentException::class.java) {
            SubscriptionImage(invalidImage)
        }

        assertEquals(
            "The image of the subscription must be a valid content, file, or web URL",
            exception.message
        )
    }

    @Test
    fun shouldAllowValidImageURL() {
        val validImage = "https://www.example.com/image.jpg"
        val image = SubscriptionImage(validImage)

        assertEquals(validImage, image.image)
    }
}