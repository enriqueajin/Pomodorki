package com.enriqueajin.pomidorki.presentation.permissionhandling

import android.Manifest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionUtilsTest {
    @Test
    fun `GIVEN CAMERA WHEN asManifestPermission THEN returns camera permission`() {
        assertEquals(Manifest.permission.CAMERA, Permissions.CAMERA.asManifestPermission())
    }

    @Test
    fun `GIVEN RECORD_AUDIO WHEN asManifestPermission THEN returns record audio permission`() {
        assertEquals(
            Manifest.permission.RECORD_AUDIO,
            Permissions.RECORD_AUDIO.asManifestPermission(),
        )
    }

    @Test
    fun `GIVEN CALL_PHONE WHEN asManifestPermission THEN returns call phone permission`() {
        assertEquals(Manifest.permission.CALL_PHONE, Permissions.CALL_PHONE.asManifestPermission())
    }

    @Test
    fun `GIVEN POST_NOTIFICATIONS WHEN asManifestPermission THEN returns post notifications permission`() {
        assertEquals(
            Manifest.permission.POST_NOTIFICATIONS,
            Permissions.POST_NOTIFICATIONS.asManifestPermission(),
        )
    }

    @Test
    fun `GIVEN NONE WHEN asManifestPermission THEN returns empty string`() {
        assertEquals("", Permissions.NONE.asManifestPermission())
    }

    @Test
    fun `GIVEN CAMERA WHEN asPermissionText THEN returns CameraPermissionTextProvider`() {
        assertTrue(Permissions.CAMERA.asPermissionText() is CameraPermissionTextProvider)
    }

    @Test
    fun `GIVEN RECORD_AUDIO WHEN asPermissionText THEN returns RecordAudioPermissionTextProvider`() {
        assertTrue(
            Permissions.RECORD_AUDIO.asPermissionText() is RecordAudioPermissionTextProvider,
        )
    }

    @Test
    fun `GIVEN CALL_PHONE WHEN asPermissionText THEN returns PhoneCallPermissionTextProvider`() {
        assertTrue(Permissions.CALL_PHONE.asPermissionText() is PhoneCallPermissionTextProvider)
    }

    @Test
    fun `GIVEN POST_NOTIFICATIONS WHEN asPermissionText THEN returns PostNotificationsPermissionTextProvider`() {
        assertTrue(
            Permissions.POST_NOTIFICATIONS.asPermissionText()
                is PostNotificationsPermissionTextProvider,
        )
    }

    @Test
    fun `GIVEN NONE WHEN asPermissionText THEN returns NonePermission`() {
        assertTrue(Permissions.NONE.asPermissionText() is NonePermission)
    }
}
