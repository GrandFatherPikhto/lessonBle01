package com.pikhto.lessonble01.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pikhto.lessonble01.BleApp01
import com.pikhto.lessonble01.R
import com.pikhto.lessonble01.databinding.FragmentScannerBinding
import com.pikhto.lessonble01.models.MainActivityViewModel
import com.pikhto.lessonble01.models.ScanViewModel
import com.pikhto.lessonble01.scanner.BleScanManager
import com.pikhto.lessonble01.ui.fragments.adapter.RvBtAdapter
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentScanner : Fragment() {

    private val logTag = this.javaClass.simpleName

    private var _binding: FragmentScannerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val _bleScanManager by lazy {
        (requireContext().applicationContext as BleApp01).bleScanManager
    }
    private val bleScanManager get() = _bleScanManager!!

    private val rvBtAdapter = RvBtAdapter()

    private val scanViewModel by viewModels<ScanViewModel>()
    private val mainActivityViewModel by activityViewModels<MainActivityViewModel> ()


    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_scan, menu)
            menu.findItem(R.id.action_scan)?.let { actionScan ->
                lifecycleScope.launch {
                    scanViewModel.stateFlowScannerState.collect { state ->
                        when (state) {
                            BleScanManager.State.Stopped -> {
                                actionScan.title = getString(R.string.scan_start)
                                actionScan.setIcon(R.drawable.ic_scan)
                            }
                            BleScanManager.State.Scanning -> {
                                actionScan.title = getString(R.string.scan_stop)
                                actionScan.setIcon(R.drawable.ic_stop)
                            }
                            BleScanManager.State.Error -> {

                            }
                        }
                    }
                }
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when(menuItem.itemId) {
                R.id.action_scan -> {
                    when(scanViewModel.scannerState) {
                        BleScanManager.State.Stopped -> {
                            bleScanManager.startScan()
                        }
                        BleScanManager.State.Scanning -> {
                            bleScanManager.stopScan()
                        }
                        BleScanManager.State.Error -> {

                        }
                    }
                    false
                }
                R.id.action_error -> {
                    bleScanManager.errorSixStartStop()
                    false
                }
                else -> {
                    false
                }
            }
        }
    }

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleScanManagerToViewModel()

        binding.apply {
            rvBtDevices.adapter = rvBtAdapter
            rvBtDevices.layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            scanViewModel.sharedFlowScanResult.collect { scanResult ->
                rvBtAdapter.addScanResult(scanResult)
            }
        }

        rvBtAdapter.setOnClickListener { scanResult, _ ->
            mainActivityViewModel.changeScanResult(scanResult)
            findNavController().navigate(R.id.action_ScanFragment_to_DeviceFragment)
        }

        linkMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bleScanManager.stopScan()
        linkMenu(false)
        _binding = null
    }

    private fun linkMenu(link: Boolean) {
        (requireActivity() as MenuHost).let { menuHost ->
            if (link) {
                menuHost.addMenuProvider(menuProvider)
            } else {
                menuHost.removeMenuProvider(menuProvider)
            }
        }
    }

    private fun bleScanManagerToViewModel() {
        lifecycleScope.launch {
            bleScanManager.stateFlowScannerState.collect { state ->
                scanViewModel.changeState(state)
            }
        }

        lifecycleScope.launch {
            bleScanManager.sharedFlowScanResult.collect { scanResult ->
                scanViewModel.addScanResult(scanResult)
            }
        }

        lifecycleScope.launch {
            bleScanManager.stateFlowError.collect { error ->
                scanViewModel.changeError(error)
            }
        }
    }
}