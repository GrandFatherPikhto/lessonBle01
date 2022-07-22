package com.pikhto.lessonble01.scanner

import android.app.Notification
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pikhto.lessonble01.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class BleScanManagerTest {

    private lateinit var closeable:AutoCloseable
    private val bleScanManager =
        BleScanManager(ApplicationProvider.getApplicationContext<Context?>().applicationContext,
            UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun testScan() = runTest(UnconfinedTestDispatcher()) {
        val results = mockRandomScanResults(7)
        bleScanManager.startScan()
        assertEquals(BleScanManager.State.Scanning, bleScanManager.scannerState)
        results.forEach { scanResult ->
            bleScanManager.onScanResult(scanResult)
        }
        bleScanManager.stopScan()
        assertEquals(results.map { it.device }.toList(),
            bleScanManager.results.map { it.device }.toList())
        assertEquals(BleScanManager.State.Stopped, bleScanManager.scannerState)
    }
}