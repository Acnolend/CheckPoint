package com.example.checkpoint

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog

fun RegisterScreenTest(composeTestRule: AndroidComposeTestRule<*, MainActivity>) {
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().printToLog("TestTree")
    composeTestRule
        .onNodeWithTag("usernameField")
        .assertExists()
        .performTextInput("juanito")

    composeTestRule
        .onNodeWithTag("passwordField")
        .assertExists()
        .performTextInput("Clave123!")

    composeTestRule
        .onNodeWithTag("emailField")
        .assertExists()
        .performTextInput("juanito@mail.com")

    composeTestRule
        .onNodeWithTag("registerButton", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
        .assertIsEnabled()
        .performClick()

    composeTestRule.waitForIdle()

}