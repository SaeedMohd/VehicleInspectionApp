package com.inspection.model

import java.io.File.separator
import android.widget.TextView
import android.view.LayoutInflater
import com.inspection.adapter.VehicleServicesArrayAdapter.RowType
import android.content.ClipData.Item
import android.view.View
import com.inspection.R
import com.inspection.interfaces.VehicleServicesListItem
import android.R.attr.name
import java.io.File.separator




/**
 * Created by sheri on 12/25/2017.
 */

class VehicleServiceHeader(private val name: String) : VehicleServicesListItem {
    override fun getViewType(): Int {
        return RowType.HEADER_ITEM.ordinal
    }

    override fun getView(inflater: LayoutInflater, convertView: View): View {
        val view: View
        if (convertView == null) {
            view = inflater.inflate(R.layout.vehicle_services_header, null) as View
            // Do some initialization
        } else {
            view = convertView
        }

        val text = view.findViewById(R.id.headerTextView) as TextView
        text.text = name

        return view
    }


}