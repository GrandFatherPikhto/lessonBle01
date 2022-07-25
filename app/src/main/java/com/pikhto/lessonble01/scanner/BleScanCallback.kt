package com.pikhto.lessonble01.scanner

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class BleScanCallback constructor(private  val bleScanManager: BleScanManager)
    : ScanCallback() {

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
        results?.toList()?.forEach { scanResult ->
            bleScanManager.onReceiveScanResult(scanResult)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        bleScanManager.onScanError(errorCode)
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            bleScanManager.onReceiveScanResult(it)
        }
    }
}