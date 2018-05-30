package com.av.ajouuniv.avproject2.data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.av.ajouuniv.avproject2.R
import com.philips.lighting.hue.sdk.PHAccessPoint

class AccessPointListAdapter(context: Context, private var accessPoints: List<PHAccessPoint>?) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    internal inner class BridgeListItem {
        var bridgeIp: TextView? = null
        var bridgeMac: TextView? = null
    }

    init {
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val item: BridgeListItem

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.selectbridge_item, null)

            item = BridgeListItem()
            item.bridgeMac = convertView!!.findViewById<View>(R.id.bridge_mac) as TextView
            item.bridgeIp = convertView.findViewById<View>(R.id.bridge_ip) as TextView

            convertView.tag = item
        } else {
            item = convertView.tag as BridgeListItem
        }
        val accessPoint = accessPoints!![position]
        item.bridgeIp!!.setTextColor(Color.BLACK)
        item.bridgeIp!!.text = accessPoint.ipAddress
        item.bridgeMac!!.setTextColor(Color.DKGRAY)
        item.bridgeMac!!.text = accessPoint.macAddress

        return convertView
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return accessPoints!!.size
    }

    override fun getItem(position: Int): Any {
        return accessPoints!![position]
    }

    fun updateData(accessPoints: List<PHAccessPoint>) {
        this.accessPoints = accessPoints
        notifyDataSetChanged()
    }

}