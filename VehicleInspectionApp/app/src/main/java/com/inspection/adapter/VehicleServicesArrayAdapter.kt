package com.inspection.adapter

import android.widget.ArrayAdapter
import com.inspection.interfaces.VehicleServicesListItem
import android.view.LayoutInflater
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.inspection.R


/**
 * Created by sheri on 12/24/2017.
 */
class VehicleServicesArrayAdapter(context: Context?, itemsList: List<VehicleServicesListItem>) : ArrayAdapter<VehicleServicesListItem>(context!!, 0, itemsList) {

    private var mInflater = LayoutInflater.from(context)

    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1

    enum class RowType {
        LIST_ITEM, HEADER_ITEM
    }



    override fun getViewTypeCount(): Int {
        return RowType.values().size

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.getViewType()
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder? = null
        val rowType = getItemViewType(position)
        val View: View
        if (convertView == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    convertView = mInflater.inflate(R.layout.vehicle_services_item, null)
                    holder.View = getItem(position)!!.getView(mInflater, convertView!!)
                }
                TYPE_SEPARATOR -> {
                    convertView = mInflater.inflate(R.layout.vehicle_services_header, null)
                    holder.View = getItem(position)!!.getView(mInflater, convertView!!)
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            holder.View = getItem(position)!!.getView(mInflater, convertView!!)
        }
        return convertView
    }

    class ViewHolder {
        var View: View? = null
    }


}