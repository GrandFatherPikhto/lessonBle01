package com.pikhto.lessonble01.scanner

import androidx.test.espresso.IdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class ScanIdling (private val bleScanManager: BleScanManager) : IdlingResource {
    companion object {
        private var scanIdling:ScanIdling? = null
        fun getInstance(bleManager: BleScanManager) : ScanIdling {
            return scanIdling ?: ScanIdling(bleManager)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private var isIdling = AtomicBoolean(false)

    var idling by Delegates.observable(false) { _, _, newState ->
        isIdling.set(newState)
        if (newState) {
            resourceCallback?.let { callback ->
                callback.onTransitionToIdle()
            }
        }
    }

    init {
        scope.launch {
            bleScanManager.stateFlowScanState.collect { state ->
                when(state) {
                    BleScanManager.State.Stopped -> {
                        idling = true
                    }
                    BleScanManager.State.Scanning -> {
                        idling = false
                    }
                    else -> { }
                }
            }
        }
        scope.launch {
            bleScanManager.sharedFlowScanResult.collect {
                if (it.isConnectable) {
                    idling = true
                }
            }
        }
    }

    override fun getName(): String = this.javaClass.simpleName

    override fun isIdleNow(): Boolean = isIdling.get()
    // override fun isIdleNow(): Boolean = idling

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}