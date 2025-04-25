package com.enriqueajin.pomidorki.presentation.permission_handling

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionHandlingViewModel @Inject constructor() : ViewModel() {

    // FIFO: [RECORD_AUDIO, CAMERA]
    val visiblePermissionDialogQueue = mutableStateListOf<Permissions>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: Permissions,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}