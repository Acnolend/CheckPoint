package com.example.checkpoint

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

fun CreateSubscriptionTest(composeTestRule: AndroidComposeTestRule<*, MainActivity>) {
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("createSubscriptionButton", useUnmergedTree = true)
        .performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("CREAR SUSCRIPCIÓN").assertIsDisplayed()
    composeTestRule.onNodeWithText("NOMBRE").assertIsDisplayed()
    composeTestRule.onNodeWithText("COSTE").assertIsDisplayed()
    composeTestRule.onNodeWithText("GENERAR").assertIsDisplayed()
    composeTestRule.onNodeWithText("RENOVACIÓN").assertIsDisplayed()

    composeTestRule.onNodeWithText("NOMBRE")
        .performTextInput("Mi suscripción")

    composeTestRule.onNodeWithText("COSTE")
        .performTextInput("10.99")


    composeTestRule.onNodeWithText("GENERAR")
        .performClick()

    composeTestRule.waitForIdle()
}