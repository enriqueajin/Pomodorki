package com.enriqueajin.pomidorki.presentation.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.presentation.ui.theme.darkPink
import com.enriqueajin.pomidorki.presentation.ui.theme.greenPomodoro
import com.enriqueajin.pomidorki.presentation.ui.theme.lightGrayPomodoro
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakArcBar
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakBackground
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakPickerContainer
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakPickerIndicator
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakTimerText
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkPrimary
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkSecondary
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakArcBar
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakBackground
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakPickerContainer
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakPickerIndicator
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakTimerText

data class TimerScreenVisuals(
    val background: Color,
    val pickerContainer: Color,
    val pickerIndicator: Color,
    val arc: Color,
    val track: Color,
    val timerTextColor: Color,
    val buttonText: String,
    @DrawableRes val buttonIconId: Int,
    val buttonContainerColor: Color,
    @StringRes val dialogTitle: Int,
    @StringRes val dialogDescription: Int,
)

@Composable
fun timerScreenVisuals(
    selectedTimer: Int,
    currentState: CountdownState,
): TimerScreenVisuals {
    val isDarkTheme = isSystemInDarkTheme()
    val onSurface = MaterialTheme.colorScheme.onSurface
    val tab = tabColors(selectedTimer)
    val control = controlChrome(currentState)

    return TimerScreenVisuals(
        background = tab.background,
        pickerContainer = tab.pickerContainer,
        pickerIndicator = tab.pickerIndicator,
        arc = tab.arc,
        track = trackColor(isDarkTheme, onSurface),
        timerTextColor = timerTextColor(tab.lightTimerText, isDarkTheme, onSurface),
        buttonText = control.buttonText,
        buttonIconId = control.buttonIconId,
        buttonContainerColor = control.buttonContainerColor,
        dialogTitle = control.dialogTitle,
        dialogDescription = control.dialogDescription,
    )
}

private data class TabColors(
    val background: Color,
    val pickerContainer: Color,
    val pickerIndicator: Color,
    val arc: Color,
    val lightTimerText: Color,
)

private data class ControlChrome(
    val buttonText: String,
    @DrawableRes val buttonIconId: Int,
    val buttonContainerColor: Color,
    @StringRes val dialogTitle: Int,
    @StringRes val dialogDescription: Int,
)

private fun tabColors(selectedTimer: Int): TabColors =
    when (selectedTimer) {
        0 ->
            TabColors(
                background = pinkPrimary,
                pickerContainer = pinkSecondary,
                pickerIndicator = darkPink,
                arc = pinkSecondary,
                lightTimerText = darkPink,
            )
        1 ->
            TabColors(
                background = shortBreakBackground,
                pickerContainer = shortBreakPickerContainer,
                pickerIndicator = shortBreakPickerIndicator,
                arc = shortBreakArcBar,
                lightTimerText = shortBreakTimerText,
            )
        2 ->
            TabColors(
                background = longBreakBackground,
                pickerContainer = longBreakPickerContainer,
                pickerIndicator = longBreakPickerIndicator,
                arc = longBreakArcBar,
                lightTimerText = longBreakTimerText,
            )
        else ->
            TabColors(
                background = pinkPrimary,
                pickerContainer = pinkSecondary,
                pickerIndicator = pinkSecondary,
                arc = pinkSecondary,
                lightTimerText = darkPink,
            )
    }

private fun controlChrome(currentState: CountdownState): ControlChrome =
    when (currentState) {
        CountdownState.Started ->
            ControlChrome(
                buttonText = "Pause",
                buttonIconId = R.drawable.ic_pause,
                buttonContainerColor = darkPink,
                dialogTitle = R.string.dialog_cancel_title,
                dialogDescription = R.string.dialog_cancel_text,
            )
        CountdownState.Paused ->
            ControlChrome(
                buttonText = "Resume",
                buttonIconId = R.drawable.ic_play,
                buttonContainerColor = greenPomodoro,
                dialogTitle = R.string.dialog_reset_title,
                dialogDescription = R.string.dialog_reset_text,
            )
        else ->
            ControlChrome(
                buttonText = "Start",
                buttonIconId = R.drawable.ic_play,
                buttonContainerColor = greenPomodoro,
                dialogTitle = R.string.dialog_reset_title,
                dialogDescription = R.string.dialog_reset_text,
            )
    }

private fun trackColor(
    isDarkTheme: Boolean,
    onSurface: Color,
): Color = if (isDarkTheme) onSurface else lightGrayPomodoro

private fun timerTextColor(
    lightTimerText: Color,
    isDarkTheme: Boolean,
    onSurface: Color,
): Color = if (isDarkTheme) onSurface else lightTimerText
