package com.enriqueajin.pomidorki.presentation.pomodorosettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.UserSettingsEvent
import com.enriqueajin.pomidorki.presentation.UserSettingsState
import com.enriqueajin.pomidorki.presentation.UserSettingsViewModel
import com.enriqueajin.pomidorki.presentation.ui.theme.darkPink
import com.enriqueajin.pomidorki.presentation.ui.theme.greenPomodoro
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakArcBar
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakBackground
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakPickerIndicator

private const val POMODORO = "Pomodoro"
private const val SHORT_BREAK = "Short Break"
private const val LONG_BREAK = "Long Break"

@Composable
fun PomodoroSettingsScreenRoot(userSettingsViewModel: UserSettingsViewModel = hiltViewModel()) {
    val userSettingsState by userSettingsViewModel.userSettingsState.collectAsStateWithLifecycle()

    userSettingsState?.let { state ->
        PomodoroSettingsScreen(
            userSettingsState = state,
            onUserSettingsEvent = userSettingsViewModel::onUserSettingsEvent,
        )
    }
}

@Composable
fun PomodoroSettingsScreen(
    userSettingsState: UserSettingsState,
    onUserSettingsEvent: (UserSettingsEvent) -> Unit,
) {
    var isSettingDialogOpen by remember { mutableStateOf(false) }
    var settingDialogTime by remember { mutableIntStateOf(0) }
    var currentSettingName by remember { mutableStateOf(POMODORO) }
    val settingsList =
        mapOf(
            POMODORO to userSettingsState.pomodoroDuration,
            SHORT_BREAK to userSettingsState.shortBreakDuration,
            LONG_BREAK to userSettingsState.longBreakDuration,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(shortBreakPickerIndicator),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier =
                Modifier
                    .padding(top = 20.dp),
            text = stringResource(R.string.pomodoro_settings_title),
            color = Color.White,
            fontSize = 20.sp,
            style =
                TextStyle(
                    fontSize = 16.sp,
                    fontFamily =
                        FontFamily(
                            Font(
                                resId = R.font.montserrat_medium,
                            ),
                        ),
                ),
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            settingsList.map {
                SettingTime(
                    title = it.key,
                    time = it.value.toInt(),
                    onSettingClicked = { time, title ->
                        currentSettingName = title
                        settingDialogTime = time
                        isSettingDialogOpen = true
                    },
                )
            }
        }
        if (isSettingDialogOpen) {
            EditTimeDialog(
                onDismissRequest = { isSettingDialogOpen = false },
                currentSettingName = currentSettingName,
                settingDialogTime = settingDialogTime,
                onTimeSettingChange = { newTime ->
                    settingDialogTime = newTime
                },
                onConfirmUpdateSetting = {
                    when (currentSettingName) {
                        POMODORO -> onUserSettingsEvent(UserSettingsEvent.UpdatePomodoroDuration(settingDialogTime.toLong()))
                        SHORT_BREAK -> onUserSettingsEvent(UserSettingsEvent.UpdateShortBreakDuration(settingDialogTime.toLong()))
                        LONG_BREAK -> onUserSettingsEvent(UserSettingsEvent.UpdateLongBreakDuration(settingDialogTime.toLong()))
                    }
                    isSettingDialogOpen = false
                },
            )
        }
    }
}

@Composable
fun EditTimeDialog(
    modifier: Modifier = Modifier,
    currentSettingName: String,
    settingDialogTime: Int,
    onDismissRequest: () -> Unit,
    onTimeSettingChange: (Int) -> Unit,
    onConfirmUpdateSetting: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 20.dp,
                            ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = currentSettingName,
                        fontSize = 22.sp,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier =
                            modifier
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            modifier =
                                modifier
                                    .clip(CircleShape)
                                    .background(shortBreakBackground),
                            onClick = {
                                onTimeSettingChange(settingDialogTime - 1)
                            },
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.minus),
                                contentDescription = null,
                            )
                        }
                        Text(
                            text = "$settingDialogTime",
                            fontSize = 32.sp,
                        )
                        IconButton(
                            modifier =
                                modifier
                                    .clip(CircleShape)
                                    .background(shortBreakBackground),
                            onClick = {
                                onTimeSettingChange(settingDialogTime + 1)
                            },
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row {
                        TextButton(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(darkPink),
                            onClick = onDismissRequest,
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                color = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        TextButton(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(greenPomodoro),
                            shape = CircleShape,
                            onClick = onConfirmUpdateSetting,
                        ) {
                            Text(
                                text = stringResource(R.string.accept),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun SettingTime(
    time: Int,
    title: String,
    onSettingClicked: (Int, String) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(95.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(shortBreakArcBar)
                .clickable { onSettingClicked(time, title) },
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$time",
                fontSize = 34.sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
fun PomodoroSettingsPreview() {
    PomodoroSettingsScreen(
        userSettingsState = UserSettingsState(),
        onUserSettingsEvent = {},
    )
}
