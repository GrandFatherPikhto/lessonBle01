package com.pikhto.lessonble01.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BleScanManager constructor (context: Context, dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : DefaultLifecycleObserver {
    companion object {
        @Volatile
        private var instance:BleScanManager? = null

        fun getInstance(context: Context, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
            instance ?: synchronized(this) {
                instance ?: BleScanManager(context, dispatcher).also { instance = it }
            }
    }

    private val logTag = this.javaClass.simpleName
    private val scope = CoroutineScope(dispatcher)

    private val scanResults = mutableListOf<ScanResult>()
    val results get() = scanResults.toList()

    enum class State(val value: Int) {
        Stopped(0x00),
        Scanning(0x01),
        Error(0x02)
    }

    private val bleScanCallback = BleScanCallback(this)

    private val bluetoothAdapter : BluetoothAdapter
        = context.applicationContext
        .getSystemService(Context.BLUETOOTH_SERVICE).let {
            (it as BluetoothManager).adapter
        }

    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    private val mutableStateFlowScannerState = MutableStateFlow(State.Stopped)
    val stateFlowScannerState get() = mutableStateFlowScannerState.asStateFlow()
    val scannerState get() = mutableStateFlowScannerState.value

    private val mutableSharedFlowScanResult = MutableSharedFlow<ScanResult>(replay = 100)
    val sharedFlowScanResult get() = mutableSharedFlowScanResult.asSharedFlow()

    private val mutableStateFlowError = MutableStateFlow(-1)
    val stateFlowError get() = mutableStateFlowError.asSharedFlow()
    val error get() = mutableStateFlowError.value

    private var _scanIdling:ScanIdling? = null
    val scanIdling: ScanIdling get() {
        return ScanIdling.getInstance().let {
            _scanIdling = it
            it
        }
    }

    init {
        scope.launch {
            sharedFlowScanResult.collect { scanResult ->
                _scanIdling?.let {
                    it.scanned = true
                }
            }
        }
    }


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopScan()
        super.onDestroy(owner)
    }

    fun onReceiveScanResult(scanResult: ScanResult) {
        if (!scanResults.map { it.device }.toList().contains(scanResult.device)) {
            scanResults.add(scanResult)
            mutableSharedFlowScanResult.tryEmit(scanResult)
        }
    }

    fun onScanError(errorCode: Int) {
        mutableStateFlowScannerState.tryEmit(State.Error)
        Log.e(logTag, "Error: $errorCode")
        stopScan()
    }

    fun startScan() {
        _scanIdling?.let {
            it.scanned = false
        }
        bluetoothLeScanner.startScan(bleScanCallback)
        mutableStateFlowScannerState.tryEmit(State.Scanning)
    }

    fun stopScan() {
        _scanIdling?.let {
            it.scanned = true
        }
        bluetoothLeScanner.stopScan(bleScanCallback)
        mutableStateFlowScannerState.tryEmit(State.Stopped)
    }

    fun errorSixStartStop() = runBlocking {
        (0..5).forEach {
            Log.e(logTag, "Attempt $it")
            bluetoothLeScanner.startScan(bleScanCallback)
            delay(50)
            bluetoothLeScanner.stopScan(bleScanCallback)
            delay(50)
        }
    }
}