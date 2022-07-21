package com.pikhto.lessonble01.ui.fragments

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pikhto.lessonble01.BleApp01
import com.pikhto.lessonble01.R
import com.pikhto.lessonble01.databinding.FragmentDeviceBinding
import com.pikhto.lessonble01.models.MainActivityViewModel
import com.pikhto.lessonble01.scanner.BleScanManager
import com.pikhto.lessonble01.ui.fragments.adapter.RvBtAdapter
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentDevice : Fragment() {

    private val logTag = this.javaClass.simpleName
    private var _binding: FragmentDeviceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val _bleScanManager by lazy {
        (requireContext().applicationContext as BleApp01).bleScanManager
    }
    private val bleScanManager get() = _bleScanManager!!

    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_device, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when(menuItem.itemId) {
                R.id.action_to_scanner -> {
                    findNavController().navigate(R.id.action_DeviceFragment_to_ScanFragment)
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
    ): View? {

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        binding.apply {

        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mainActivityViewModel.scanResult?.let { result ->
                includeLayout.tvBleName.text = result.device.name ?: getString(R.string.unknown_device)
                includeLayout.tvBleAddress.text = result.device.address
                if (result.device.bondState == BluetoothDevice.BOND_BONDED) {
                    includeLayout.ivPaired.setImageResource(R.drawable.ic_paired)
                } else {
                    includeLayout.ivPaired.setImageResource(R.drawable.ic_unpaired)
                }
                tvRssi.text = String.format("%03d", result.rssi)
                if (result.isConnectable) {
                    ivConnectable.setImageResource(R.drawable.ic_connectable)
                } else {
                    ivConnectable.setImageResource(R.drawable.ic_no_connectable)
                }
            }
        }

        linkMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
}