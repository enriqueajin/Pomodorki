package com.enriqueajin.pomidorki.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.MainActivity
import com.enriqueajin.pomidorki.presentation.home.components.PomodoroCountdown
import com.enriqueajin.pomidorki.presentation.home.components.TimerButton
import com.enriqueajin.pomidorki.presentation.home.components.TimerPicker
import com.enriqueajin.pomidorki.presentation.permission_handling.CameraPermissionTextProvider
import com.enriqueajin.pomidorki.presentation.permission_handling.PermissionDialog
import com.enriqueajin.pomidorki.presentation.permission_handling.PermissionHandlingViewModel
import com.enriqueajin.pomidorki.presentation.permission_handling.PhoneCallPermissionTextProvider
import com.enriqueajin.pomidorki.presentation.permission_handling.RecordAudioPermissionTextProvider
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

    // Single permission
    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionViewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                isGranted = isGranted
            )
        }
    )

    // Multiple permissions
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                permissionViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )

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
                    onClick = {
                        cameraPermissionResultLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
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
                    Text(
                        text = "25:00",
                        fontSize = 82.sp,
                        color = timerTextColor,
                        fontFamily = FontFamily(
                            Font(resId = R.font.bebasneue_regular)
                        )
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
                    text = stringResource(
                        id = R.string.start_button
                    ),
                    icon = Icons.Default.PlayArrow,
                    containerColor = greenPomodoro,
                    onClick = {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    },
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
        dialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> {
                            CameraPermissionTextProvider()
                        }
                        Manifest.permission.RECORD_AUDIO -> {
                            RecordAudioPermissionTextProvider()
                        }
                        Manifest.permission.CALL_PHONE -> {
                            PhoneCallPermissionTextProvider()
                        }
                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        permission
                    ),
                    onDismiss = permissionViewModel::dismissDialog,
                    onOkClick = {
                        permissionViewModel.dismissDialog()
                        multiplePermissionResultLauncher.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoToAppSettingsClick = { context.openAppSettings() }
                )
            }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}