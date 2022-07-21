package com.pikhto.lessonble01.ui.fragments.adapter

import android.bluetooth.le.ScanResult
import android.icu.number.ScientificNotation
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pikhto.lessonble01.R

typealias OnClickListener<T> = (T, View) -> Unit
typealias OnLongClickListener<T> = (T, View) -> Unit

class RvBtAdapter : RecyclerView.Adapter<RvBtHolder>() {
    private val logTag = this.javaClass.simpleName
    private val scanResults = mutableListOf<ScanResult>()

    private var onClickListener:OnClickListener<ScanResult>? = null
    private var onLongClickListener:OnLongClickListener<ScanResult>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvBtHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_ble_device, parent, false)
        return RvBtHolder(view)
    }

    override fun onBindViewHolder(holder: RvBtHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            onClickListener?.let { listener ->
                listener(scanResults[position], view) }
        }

        holder.itemView.setOnLongClickListener { view ->
            onLongClickListener?.let { listener ->
                listener(scanResults[position], view) }
            true
        }
        holder.bind(scanResults[position])
    }

    override fun getItemCount(): Int = scanResults.size

    fun addScanResult(scanResult: ScanResult) {
        if (!scanResults.map { it.device }.contains(scanResult.device)) {
            scanResults.add(scanResult)
            // Log.d(logTag, "Add ${scanResult.device.address} ${scanResults.indexOf(scanResult)}")
            notifyItemInserted(scanResults.indexOf(scanResult))
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener<ScanResult>) {
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener<ScanResult>) {
        this.onLongClickListener = onLongClickListener
    }
}