package com.pikhto.lessonble01.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.pikhto.lessonble01.BleApp01
import com.pikhto.lessonble01.R
import com.pikhto.lessonble01.databinding.ActivityMainBinding
import com.pikhto.lessonble01.scanner.BleScanManager
import com.pikhto.lessonble01.scanner.RequestPermissions
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val bleScanManager by lazy {
        val scanManager = BleScanManager(this)
        (applicationContext as BleApp01).bleScanManager = scanManager
        scanManager
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when(menuItem.itemId) {
                R.id.action_settings -> { true }
                else -> { false }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(bleScanManager)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val requestPermissions = RequestPermissions(this)
        requestPermissions.requestPermissions(listOf(
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
        ))

        lifecycleScope.launch {
            requestPermissions.sharedFlowPermission.collect { requestPermission ->
                if (!requestPermission.granted) {
                    finishAndRemoveTask()
                    exitProcess(0)
                }
            }
        }

        linkMenu(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        linkMenu(false)
        super.onDestroy()
    }

    private fun linkMenu(link: Boolean) {
        (this as MenuHost).let { menuHost ->
            if (link) {
                menuHost.addMenuProvider(menuProvider)
            } else {
                menuHost.removeMenuProvider(menuProvider)
            }
        }
    }
}