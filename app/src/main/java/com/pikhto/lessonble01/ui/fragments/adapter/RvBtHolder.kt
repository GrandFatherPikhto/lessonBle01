package com.pikhto.lessonble01.ui.fragments.adapter

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble01.R
import com.pikhto.lessonble01.databinding.LayoutBleDeviceBinding

class RvBtHolder constructor(private val view: View) : RecyclerView.ViewHolder(view) {
    private val binding = LayoutBleDeviceBinding.bind(view)

    fun bind(scanResult: ScanResult) {
        binding.apply {
            tvBleName.text =
                scanResult.device.name ?:
                    itemView.context.getString(R.string.unknown_device)
            tvBleAddress.text =
                scanResult.device.address
            if (scanResult.device.bondState == BluetoothDevice.BOND_BONDED) {
                ivPaired.setImageResource(R.drawable.ic_paired)
            } else {
                ivPaired.setImageResource(R.drawable.ic_unpaired)
            }
        }
    }
}