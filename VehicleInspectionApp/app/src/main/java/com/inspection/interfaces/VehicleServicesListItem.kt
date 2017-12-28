package com.inspection.interfaces

import android.view.LayoutInflater
import android.view.View


/**
 * Created by sheri on 12/24/2017.
 */
interface VehicleServicesListItem {
    fun getViewType(): Int
    fun getView(inflater: LayoutInflater, convertView: View): View
}