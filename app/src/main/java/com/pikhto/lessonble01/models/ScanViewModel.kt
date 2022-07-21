package com.pikhto.lessonble01.models

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import com.pikhto.lessonble01.scanner.BleScanManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanViewModel : ViewModel() {
    private val mutableStateFlowScannerState = MutableStateFlow(BleScanManager.State.Stopped)
    val stateFlowScannerState get() = mutableStateFlowScannerState.asStateFlow()
    val scannerState get() = mutableStateFlowScannerState.value

    private val mutableSharedFlowScanResult = MutableSharedFlow<ScanResult>(replay = 100)
    val sharedFlowScanResult get() = mutableSharedFlowScanResult.asSharedFlow()

    private val mutableStateFlowError = MutableStateFlow(-1)
    val stateFlowError get() = mutableStateFlowError.asSharedFlow()
    val error get() = mutableStateFlowError.value

    fun changeState(value: BleScanManager.State) {
        mutableStateFlowScannerState.tryEmit(value)
    }

    fun addScanResult(scanResult: ScanResult) {
        mutableSharedFlowScanResult.tryEmit(scanResult)
    }

    fun changeError(errorCode:Int) {
        mutableStateFlowError.tryEmit(errorCode)
    }
}