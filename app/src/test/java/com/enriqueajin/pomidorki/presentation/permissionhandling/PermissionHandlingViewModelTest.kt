package com.enriqueajin.pomidorki.presentation.permissionhandling

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionHandlingViewModelTest {
    @Test
    fun `GIVEN denied permission WHEN onPermissionResult THEN enqueues if absent`() {
        val viewModel = PermissionHandlingViewModel()

        viewModel.onPermissionResult(Permissions.RECORD_AUDIO, isGranted = false)

        assertEquals(
            listOf(Permissions.RECORD_AUDIO),
            viewModel.visiblePermissionDialogQueue.toList(),
        )
    }

    @Test
    fun `GIVEN granted permission WHEN onPermissionResult THEN does not enqueue`() {
        val viewModel = PermissionHandlingViewModel()

        viewModel.onPermissionResult(Permissions.CAMERA, isGranted = true)

        assertTrue(viewModel.visiblePermissionDialogQueue.isEmpty())
    }

    @Test
    fun `GIVEN same permission already queued WHEN onPermissionResult denied THEN does not duplicate`() {
        val viewModel =
            PermissionHandlingViewModel().apply {
                onPermissionResult(Permissions.CALL_PHONE, isGranted = false)
            }

        viewModel.onPermissionResult(Permissions.CALL_PHONE, isGranted = false)

        assertEquals(
            listOf(Permissions.CALL_PHONE),
            viewModel.visiblePermissionDialogQueue.toList(),
        )
    }

    @Test
    fun `GIVEN queued permissions WHEN dismissDialog THEN removes first (FIFO)`() {
        val viewModel =
            PermissionHandlingViewModel().apply {
                onPermissionResult(Permissions.RECORD_AUDIO, isGranted = false)
                onPermissionResult(Permissions.CAMERA, isGranted = false)
            }

        viewModel.dismissDialog()

        assertEquals(
            listOf(Permissions.CAMERA),
            viewModel.visiblePermissionDialogQueue.toList(),
        )
    }
}
