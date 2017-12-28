package com.inspection.model

import android.widget.TextView
import android.view.LayoutInflater

import android.view.View
import com.inspection.R
import com.inspection.interfaces.VehicleServicesListItem
import com.inspection.adapter.VehicleServicesArrayAdapter.RowType
import kotlinx.android.synthetic.main.vehicle_services_item.view.*


/**
 * Created by sheri on 12/25/2017.
 */

class VehicleServiceItem(private val name: String) : VehicleServicesListItem {

    var isSelected=false

    override fun getViewType(): Int {
        return RowType.LIST_ITEM.ordinal
    }

    override fun getView(inflater: LayoutInflater, convertView: View): View {
        val view: View
        if (convertView == null) {
            view = inflater.inflate(R.layout.vehicle_services_item, null) as View

            // Do some initialization
        } else {
            view = convertView
        }


        val text1 = view.findViewById(R.id.itemTextView) as TextView
        text1.text = name
        isSelected = view.itemCheckBox.isSelected

        return view
    }

}