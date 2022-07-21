package com.pikhto.lessonble01.scanner

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RequestPermissions constructor(private val activity: AppCompatActivity) {
    private val logTag = this.javaClass.simpleName
    private lateinit var currentPermission:String

    private val mutableSharedFlowPermission = MutableSharedFlow<RequestPermission>(replay = 10)
    val sharedFlowPermission get() = mutableSharedFlowPermission.asSharedFlow()

    private val launchMultiplePermissions = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.forEach { permission ->
            mutableSharedFlowPermission.tryEmit(RequestPermission(permission.key, permission.value))
        }
    }

    private val launchPermission = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        mutableSharedFlowPermission.tryEmit(RequestPermission(currentPermission, granted))
    }

    private fun isGranted(permission: String): Boolean =
        activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


    fun requestPermissions(request: List<String>) {
        val permissions = mutableListOf<String>()

        request.forEach { request ->
            if (!isGranted(request)) {
                permissions.add(request)
            }
        }

        if (permissions.isNotEmpty()) {
            launchMultiplePermissions.launch(permissions.toTypedArray())
        }
    }

    fun requestPermission(permission: String) {
        if (!isGranted(permission)) {
            currentPermission = permission
            launchPermission.launch(permission)
        }
    }
}