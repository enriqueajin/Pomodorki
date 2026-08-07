package com.enriqueajin.pomidorki.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator.TimerProgressIndicator
import com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator.TimerProgressIndicatorDefaults
import com.enriqueajin.pomidorki.presentation.MainActivity
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Effect
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.State
import com.enriqueajin.pomidorki.presentation.home.components.TimerButton
import com.enriqueajin.pomidorki.presentation.home.components.TimerPicker
import com.enriqueajin.pomidorki.presentation.permissionhandling.PermissionDialog
import com.enriqueajin.pomidorki.presentation.permissionhandling.PermissionHandlingViewModel
import com.enriqueajin.pomidorki.presentation.permissionhandling.Permissions
import com.enriqueajin.pomidorki.presentation.permissionhandling.asManifestPermission
import com.enriqueajin.pomidorki.presentation.permissionhandling.asPermissionText
import com.enriqueajin.pomidorki.presentation.permissionhandling.isPermissionGranted
import com.enriqueajin.pomidorki.presentation.permissionhandling.openAppSettings
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
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_PAUSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_START
import com.enriqueajin.pomidorki.utils.Constants.ACTION_TIMER_TYPE
import com.enriqueajin.pomidorki.utils.Constants.pomodoroTabItems

@Composable
fun TimerScreenRoot(
    timerScreenViewModel: TimerScreenViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity),
    onSettingsIconClick: () -> Unit,
) {
    val uiState by timerScreenViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var tabToConfirm by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        timerScreenViewModel.uiEffects.collect { effect ->
            when (effect) {
                is Effect.TriggerIntent -> {
                    val intent =
                        Intent(context, CountdownService::class.java).apply {
                            putExtra(ACTION_TIMER_TYPE, effect.tabIndex)
                        }
                    context.startService(intent)
                }

                is Effect.TimerToBeConfirmed -> tabToConfirm = effect.index
                is Effect.TriggerForegroundService -> {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = effect.action,
                        timerType = effect.timerType,
                    )
                }
            }
        }
    }

    TimerScreen(
        event = timerScreenViewModel::onEvent,
        uiState = uiState,
        tabToConfirm = tabToConfirm,
        onSettingsIconClick = onSettingsIconClick,
    )
}

@Composable
fun TimerScreen(
    event: (TimerScreenContract.Event) -> Unit,
    uiState: State,
    tabToConfirm: Int,
    onSettingsIconClick: () -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor =
        remember(uiState.selectedTimer) {
            when (uiState.selectedTimer) {
                0 -> pinkPrimary
                1 -> shortBreakBackground
                2 -> longBreakBackground
                else -> pinkPrimary
            }
        }
    val containerColor =
        remember(uiState.selectedTimer) {
            when (uiState.selectedTimer) {
                0 -> pinkSecondary
                1 -> shortBreakPickerContainer
                2 -> longBreakPickerContainer
                else -> pinkSecondary
            }
        }
    val indicatorColor =
        remember(uiState.selectedTimer) {
            when (uiState.selectedTimer) {
                0 -> darkPink
                1 -> shortBreakPickerIndicator
                2 -> longBreakPickerIndicator
                else -> pinkSecondary
            }
        }
    val timerArcColor by remember(uiState.selectedTimer) {
        derivedStateOf {
            when (uiState.selectedTimer) {
                0 -> pinkSecondary
                1 -> shortBreakArcBar
                2 -> longBreakArcBar
                else -> pinkSecondary
            }
        }
    }
    val timeElapsedArcColor =
        if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.onSurface
        } else {
            lightGrayPomodoro
        }
    val lightThemeTimerTextColor =
        remember(uiState.selectedTimer) {
            when (uiState.selectedTimer) {
                0 -> darkPink
                1 -> shortBreakTimerText
                2 -> longBreakTimerText
                else -> darkPink
            }
        }
    val timerTextColor =
        if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.onSurface
        } else {
            lightThemeTimerTextColor
        }
    val permissionViewModel: PermissionHandlingViewModel = hiltViewModel()
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue

    val postNotificationsPermissionResultLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionViewModel.onPermissionResult(
                        permission = Permissions.POST_NOTIFICATIONS,
                        isGranted = isGranted,
                    )
                }
            },
        )
    val buttonText =
        remember(uiState.currentState) {
            when (uiState.currentState) {
                CountdownState.Started -> "Pause"
                CountdownState.Paused -> "Resume"
                else -> "Start"
            }
        }

    val buttonIconId =
        remember(uiState.currentState) {
            when (uiState.currentState) {
                CountdownState.Started -> R.drawable.ic_pause
                CountdownState.Paused -> R.drawable.ic_play
                else -> R.drawable.ic_play
            }
        }

    val dialogTitle =
        remember(uiState.currentState) {
            if (uiState.currentState == CountdownState.Started) {
                R.string.dialog_cancel_title
            } else {
                R.string.dialog_reset_title
            }
        }

    val dialogDescription =
        remember(uiState.currentState) {
            if (uiState.currentState == CountdownState.Started) {
                R.string.dialog_cancel_text
            } else {
                R.string.dialog_reset_text
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                        .background(backgroundColor),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    IconButton(
                        modifier =
                            Modifier
                                .align(Alignment.End),
                        onClick = onSettingsIconClick,
                    ) {
                        Icon(
                            modifier =
                                Modifier
                                    .size(45.dp)
                                    .padding(end = 8.dp, top = 10.dp),
                            imageVector = Icons.Default.Settings,
                            tint = Color.DarkGray,
                            contentDescription =
                                stringResource(
                                    R.string.settings_icon_description,
                                ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TimerPicker(
                        modifier = Modifier.padding(horizontal = 30.dp),
                        selected = uiState.selectedTimer,
                        items = pomodoroTabItems,
                        containerColor = containerColor,
                        indicatorColor = indicatorColor,
                        onTabSelected = { event(TimerScreenContract.Event.OnTabClicked(it)) },
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .width(150.dp)
                                .height(66.dp),
                        painter = painterResource(id = R.drawable.tomato_stalk),
                        contentDescription = null,
                    )
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(999.dp),
                                ).padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        TimerProgressIndicator(
                            modifier = Modifier.size(TimerProgressIndicatorDefaults.Size),
                            totalMillis = uiState.initialMillis,
                            remainingMillis = uiState.timeLeft,
                            endsAtElapsedRealtime =
                                if (uiState.currentState == CountdownState.Started) {
                                    uiState.deadlineElapsed
                                } else {
                                    0L
                                },
                            timerText = uiState.timerText,
                            indicatorColor = timerArcColor,
                            trackColor = timeElapsedArcColor,
                            textColor = timerTextColor,
                        )
                    }
                    Spacer(modifier = Modifier.height(35.dp))
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedTextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clickable { },
                        value = "Doing Homework",
                        label = {
                            Text(
                                text =
                                    stringResource(
                                        id = R.string.select_task,
                                    ),
                                color = timerArcColor,
                            )
                        },
                        textStyle =
                            TextStyle(
                                fontSize = 16.sp,
                                fontFamily =
                                    FontFamily(
                                        Font(
                                            resId = R.font.montserrat_medium,
                                        ),
                                    ),
                            ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = timerArcColor,
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        shape = RoundedCornerShape(50),
                        onValueChange = {},
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = timerArcColor,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (mainButton, resetIcon) = createRefs()

                        TimerButton(
                            modifier =
                                Modifier
                                    .constrainAs(mainButton) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    },
                            text = buttonText,
                            icon = ImageVector.vectorResource(buttonIconId),
                            containerColor = if (uiState.currentState == CountdownState.Started) darkPink else greenPomodoro,
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val isPermissionGranted =
                                        isPermissionGranted(
                                            context = context,
                                            permission = Manifest.permission.POST_NOTIFICATIONS,
                                        )
                                    if (isPermissionGranted) {
                                        startCountdownTimerService(
                                            context = context,
                                            currentState = uiState.currentState,
                                        )
                                    } else {
                                        postNotificationsPermissionResultLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS,
                                        )
                                    }
                                } else {
                                    startCountdownTimerService(
                                        context = context,
                                        currentState = uiState.currentState,
                                    )
                                }
                            },
                        )
                        if (uiState.currentState == CountdownState.Paused) {
                            IconButton(
                                modifier =
                                    Modifier
                                        .constrainAs(resetIcon) {
                                            start.linkTo(mainButton.end)
                                        },
                                onClick = {
                                    event(TimerScreenContract.Event.OnRestartIconClick)
                                },
                            ) {
                                Icon(
                                    modifier = Modifier.size(32.dp),
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp)) // This spacer might push content too low if nav bar is opaque
                }
            }
            dialogQueue
                .reversed()
                .forEach { permission ->
                    PermissionDialog(
                        permissionTextProvider = permission.asPermissionText(),
                        isPermanentlyDeclined =
                            !shouldShowRequestPermissionRationale(
                                context as MainActivity, // Make sure context is appropriate, might need LocalContext.current as Activity
                                permission.asManifestPermission(),
                            ),
                        onDismiss = permissionViewModel::dismissDialog,
                        onOkClick = {
                            permissionViewModel.dismissDialog()
                        },
                        onGoToAppSettingsClick = { context.openAppSettings() },
                    )
                }
            if (uiState.isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { event(TimerScreenContract.Event.OnAlertCancelClick) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                event(TimerScreenContract.Event.OnAlertConfirmClick(tabToConfirm))
                            },
                            content = {
                                Text(text = stringResource(R.string.dialog_yes_button))
                            },
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { event(TimerScreenContract.Event.OnAlertCancelClick) },
                            content = {
                                Text(text = stringResource(R.string.dialog_no_button))
                            },
                        )
                    },
                    title = { Text(text = stringResource(dialogTitle)) },
                    text = { Text(text = stringResource(dialogDescription)) },
                )
            }
        }
    }
}

private fun startCountdownTimerService(
    context: Context,
    currentState: CountdownState?,
) {
    ServiceHelper.triggerForegroundService(
        context = context,
        action =
            if (currentState == CountdownState.Started) {
                ACTION_SERVICE_PAUSE
            } else {
                ACTION_SERVICE_START
            },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen(
        event = {},
        uiState = State(currentState = CountdownState.Paused),
        tabToConfirm = 0,
        onSettingsIconClick = {},
    )
}
