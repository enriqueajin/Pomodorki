package com.enriqueajin.pomidorki.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.enriqueajin.pomidorki.presentation.ui.theme.greenPomodoro
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TimerButtonTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun verifyButtonExists() {
        composeTestRule.setContent {
            TimerButton(
                text = "Play",
                icon = Icons.Filled.PlayArrow,
                containerColor = greenPomodoro,
                onClick = {},
            )
        }
        composeTestRule.onNodeWithText("Play").assertExists()
    }

    @Test
    fun whenClickButton_thenClickActionIsCalled() {
        var state = false
        composeTestRule.setContent {
            TimerButton(
                text = "Play",
                icon = Icons.Filled.PlayArrow,
                containerColor = greenPomodoro,
                onClick = { state = true },
            )
        }
        composeTestRule.onNodeWithText("Play").performClick()
        assertEquals(state, true)
    }
}