package com.enriqueajin.pomidorki.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.data.countdown.CountdownTimer
import com.enriqueajin.pomidorki.data.services.Action
import com.enriqueajin.pomidorki.data.services.StopwatchService
import com.enriqueajin.pomidorki.data.services.toAction
import com.enriqueajin.pomidorki.presentation.MainActivity
import com.enriqueajin.pomidorki.presentation.home.components.CountdownView
import com.enriqueajin.pomidorki.presentation.home.components.PomodoroCountdown
import com.enriqueajin.pomidorki.presentation.home.components.TimerButton
import com.enriqueajin.pomidorki.presentation.home.components.TimerPicker
import com.enriqueajin.pomidorki.presentation.permission_handling.PermissionDialog
import com.enriqueajin.pomidorki.presentation.permission_handling.PermissionHandlingViewModel
import com.enriqueajin.pomidorki.presentation.permission_handling.Permissions
import com.enriqueajin.pomidorki.presentation.permission_handling.asManifestPermission
import com.enriqueajin.pomidorki.presentation.permission_handling.asPermissionText
import com.enriqueajin.pomidorki.presentation.permission_handling.isPermissionGranted
import com.enriqueajin.pomidorki.presentation.permission_handling.openAppSettings
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
import com.enriqueajin.pomidorki.utils.Constants.pomodoroTabItems

@Composable
fun TimerScreen() {
    val context = LocalContext.current
    var selected by rememberSaveable {
        mutableIntStateOf(0)
    }
    val backgroundColor by remember(selected) {
        derivedStateOf {
            when (selected) {
                0 -> pinkPrimary
                1 -> shortBreakBackground
                2 -> longBreakBackground
                else -> pinkPrimary
            }
        }
    }
    val containerColor by remember(selected) {
        derivedStateOf {
            when (selected) {
                0 -> pinkSecondary
                1 -> shortBreakPickerContainer
                2 -> longBreakPickerContainer
                else -> pinkSecondary
            }
        }
    }
    val indicatorColor by remember(selected) {
        derivedStateOf {
            when (selected) {
                0 -> darkPink
                1 -> shortBreakPickerIndicator
                2 -> longBreakPickerIndicator
                else -> pinkSecondary
            }
        }
    }
    val timerArcColor by remember(selected) {
        derivedStateOf {
            when (selected) {
                0 -> pinkSecondary
                1 -> shortBreakArcBar
                2 -> longBreakArcBar
                else -> pinkSecondary
            }
        }
    }
    val timeElapsedArcColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        lightGrayPomodoro
    }
    val lightThemeTimerTextColor by remember(selected) {
        derivedStateOf {
            when (selected) {
                0 -> darkPink
                1 -> shortBreakTimerText
                2 -> longBreakTimerText
                else -> darkPink
            }
        }
    }
    val timerTextColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        lightThemeTimerTextColor
    }

    val permissionViewModel: PermissionHandlingViewModel = hiltViewModel()
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue

    val permissionsToRequest = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
    )

    val postNotificationsPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionViewModel.onPermissionResult(
                    permission = Permissions.POST_NOTIFICATIONS,
                    isGranted = isGranted
                )
            }
        }
    )

    var currentStopwatchAction by remember {
        mutableStateOf(Action.NONE)
    }
    val buttonText by remember(currentStopwatchAction) {
        derivedStateOf {
            when(currentStopwatchAction) {
                Action.NONE -> "Start"
                Action.START -> "Pause"
                Action.PAUSE -> "Resume"
                Action.RESUME -> "Pause"
                else -> "Start"
            }
        }
    }
    val buttonIconId by remember(currentStopwatchAction) {
        derivedStateOf {
            when(currentStopwatchAction) {
                Action.NONE -> R.drawable.ic_play
                Action.START -> R.drawable.ic_pause
                Action.PAUSE -> R.drawable.ic_play
                Action.RESUME -> R.drawable.ic_pause
                else -> R.drawable.ic_play
            }
        }
    }
    val buttonColor by remember(currentStopwatchAction) {
        derivedStateOf {
            when(currentStopwatchAction) {
                Action.NONE -> greenPomodoro
                Action.START -> darkPink
                Action.PAUSE -> greenPomodoro
                Action.RESUME -> darkPink
                else -> greenPomodoro
            }
        }
    }
    val countDownTimer = remember {
        CountdownTimer(totalTimeMillis = 1_500_000L)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                .background(backgroundColor)
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = { }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(end = 8.dp, top = 10.dp),
                        imageVector = Icons.Default.Settings,
                        tint = Color.DarkGray,
                        contentDescription = stringResource(
                            R.string.settings_icon_description
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TimerPicker(
                    modifier = Modifier.padding(horizontal = 30.dp),
                    selected = selected,
                    items = pomodoroTabItems,
                    containerColor = containerColor,
                    indicatorColor = indicatorColor,
                    onTabSelected = { index -> selected = index }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .width(150.dp)
                        .height(66.dp),
                    painter = painterResource(id = R.drawable.tomato_stalk),
                    contentDescription = null
                )
                Box(
                    modifier = Modifier.padding(top = 42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PomodoroCountdown(
                        modifier = Modifier
                            .size(197.dp)
                            .background(pinkPrimary),
                        initialValue = 67,
                        arcColor = timerArcColor,
                        timeElapsedArcColor = timeElapsedArcColor,
                        circleRadius = 340f,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        onPositionChange = {}
                    )
                    CountdownView(
                        formattedText = countDownTimer.formattedTime,
                        textColor = timerTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(35.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { },
                    value = "Doing Homework",
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.select_task
                            ),
                            color = timerArcColor
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            Font(
                                resId = R.font.montserrat_medium
                            )
                        )
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = timerArcColor
                        )
                    },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(50),
                    onValueChange = {},
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = timerArcColor,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
                Spacer(modifier = Modifier.height(30.dp))
                TimerButton(
                    text = buttonText,
                    icon = ImageVector.vectorResource(buttonIconId),
                    containerColor = buttonColor,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val isPermissionGranted = isPermissionGranted(
                                context = context,
                                permission = Manifest.permission.POST_NOTIFICATIONS
                            )
                            if (isPermissionGranted) {
                                startCountdownTimerService(
                                    context = context,
                                    currentAction = currentStopwatchAction.toString(),
                                    onActionChange = { currentStopwatchAction = it.toAction() },
                                    countdownTimer = countDownTimer,
                                )
                            } else {
                                postNotificationsPermissionResultLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        } else {
                            startCountdownTimerService(
                                context = context,
                                currentAction = currentStopwatchAction.toString(),
                                onActionChange = { currentStopwatchAction = it.toAction() },
                                countdownTimer = countDownTimer,
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        dialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = permission.asPermissionText(),
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        permission.asManifestPermission()
                    ),
                    onDismiss = permissionViewModel::dismissDialog,
                    onOkClick = {
                        permissionViewModel.dismissDialog()
                    },
                    onGoToAppSettingsClick = { context.openAppSettings() }
                )
            }
    }
}

private fun startCountdownTimerService(
    context: Context,
    currentAction: String,
    onActionChange: (String) -> Unit,
    countdownTimer: CountdownTimer,
) {
    when(currentAction) {
        Action.NONE.toString(),
        Action.PAUSE.toString() -> {
            countdownTimer.start()
            Intent(context, StopwatchService::class.java).also {
                it.action = Action.START.toString()
                it.putExtra("remainingTime", countdownTimer.formattedTime)
                it.putExtra("PAUSE,", "PAUSE")
                context.startService(it)
            }
            onActionChange(Action.START.toString())

        }
        Action.START.toString() -> {
            countdownTimer.pause()
            Intent(context, StopwatchService::class.java).also {
                it.action = Action.PAUSE.toString()
                it.putExtra("time", countdownTimer.formattedTime)
                it.putExtra("RESUME,", "RESUME")
                context.startService(it)
            }
            onActionChange(Action.PAUSE.toString())
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}