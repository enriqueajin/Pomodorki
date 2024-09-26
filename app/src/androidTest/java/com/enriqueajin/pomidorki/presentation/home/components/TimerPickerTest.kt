package com.enriqueajin.pomidorki.presentation.home.components

import android.content.Context
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.enriqueajin.pomidorki.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerPickerTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var tabRowItems: TabRowItems

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        tabRowItems = TabRowItems(
            pomodoro = context.resources.getString(R.string.tab_pomodoro),
            shortBreak = context.resources.getString(R.string.tab_short_break),
           longBreak = context.resources.getString(R.string.tab_long_break),
        )

        composeTestRule.setContent {
            TimerPicker()
        }
    }

    @Test
    fun verifyTimerPickerExists() {
        val contentDesc = context.resources.getString(R.string.timer_picker_tab_row)
        composeTestRule.onNode(hasContentDescription(contentDesc)).apply {
            assertExists()
        }
    }

    @Test
    fun verifyPomodoroAndShortAndLongBreaksTabsExists() {
        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).assertExists()
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).assertExists()
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).assertExists()
    }

    @Test
    fun whenFirstComposition_thenPomodoroTabIsSelectedAndOthersArent() {
        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).assertIsSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).assertIsNotSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).assertIsNotSelected()
    }

    @Test
    fun whenClickPomodoroTab_thenPomodoroTabIsSelectedAndOthersArent() {
        // Action
        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).performClick()

        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).assertIsSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).assertIsNotSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).assertIsNotSelected()
    }

    @Test
    fun whenClickShortBreakTab_thenShortBreakTabIsSelectedAndOthersArent() {
        // Action
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).performClick()

        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).assertIsNotSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).assertIsSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).assertIsNotSelected()
    }

    @Test
    fun whenClickLongBreakTab_thenLongBreakTabIsSelectedAndOthersArent() {
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).performClick()

        composeTestRule.onNode(hasContentDescription(tabRowItems.pomodoro)).assertIsNotSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.shortBreak)).assertIsNotSelected()
        composeTestRule.onNode(hasContentDescription(tabRowItems.longBreak)).assertIsSelected()
    }
}

data class TabRowItems(
    val pomodoro: String,
    val shortBreak: String,
    val longBreak: String,
)