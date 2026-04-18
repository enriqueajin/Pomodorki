package com.enriqueajin.pomidorki.presentation.permissionhandling

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

fun isPermissionGranted(
    context: Context,
    permission: String,
): Boolean = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun Permissions.asPermissionText(): PermissionTextProvider =
    when (this) {
        Permissions.CAMERA -> CameraPermissionTextProvider()
        Permissions.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
        Permissions.CALL_PHONE -> PhoneCallPermissionTextProvider()
        Permissions.POST_NOTIFICATIONS -> PostNotificationsPermissionTextProvider()
        Permissions.NONE -> NonePermission()
    }

fun String.asPermission(): Permissions =
    when (this) {
        Manifest.permission.CAMERA -> Permissions.POST_NOTIFICATIONS
        Manifest.permission.RECORD_AUDIO -> Permissions.POST_NOTIFICATIONS
        Manifest.permission.CALL_PHONE -> Permissions.POST_NOTIFICATIONS
        Manifest.permission.POST_NOTIFICATIONS -> Permissions.POST_NOTIFICATIONS
        else -> {
            Permissions.NONE
        }
    }

fun Permissions.asManifestPermission(): String =
    when (this) {
        Permissions.CAMERA -> Manifest.permission.CAMERA
        Permissions.RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
        Permissions.CALL_PHONE -> Manifest.permission.CALL_PHONE
        Permissions.POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
        Permissions.NONE -> ""
    }

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    ).also(::startActivity)
}
