package com.pikhto.lessonble01.ui

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pikhto.lessonble01.BleApp01
import com.pikhto.lessonble01.scanner.BleScanManager
import com.pikhto.lessonble01.R

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var bleScanManager: BleScanManager

    @Before
    fun setUp() {
        // activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        activityRule.scenario.onActivity { mainActivity ->
            (mainActivity.applicationContext as BleApp01).bleScanManager?.let {
                bleScanManager = it
            }
        }
        IdlingRegistry.getInstance().register(bleScanManager.scanIdling)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(bleScanManager.scanIdling)
    }

    @Test(timeout = 10000L)
    fun testScanner() {
        val unknownDevice
            = InstrumentationRegistry
                .getInstrumentation()
                .targetContext
                .getString(R.string.unknown_device)

        onView(withId(R.id.rv_bt_devices)).check(matches(isDisplayed()))
        onView(withId(R.id.action_scan)).perform(click())

        assertTrue(
            "В списке накоплено хотя бы одно обнаруженное устройство",
            bleScanManager.results.isNotEmpty())

        onView(withText(bleScanManager.results[0].device.address)).perform(click())
        onView(withId(R.id.tv_ble_address))
            .check(matches(withText(bleScanManager.results[0].device.address)))
        onView(withId(R.id.tv_ble_name))
            .check(matches(withText(bleScanManager.results[0].device.name ?: unknownDevice)))

        onView(withId(R.id.tv_rssi)).check(
            matches(withText(InstrumentationRegistry
                .getInstrumentation().targetContext.getString(R.string.rssi_title,
                bleScanManager.results[0].rssi))))

        onView(withId(R.id.action_to_scanner)).perform(click())
        onView(withId(R.id.cl_scanner)).check(matches(isDisplayed()))
    }
}