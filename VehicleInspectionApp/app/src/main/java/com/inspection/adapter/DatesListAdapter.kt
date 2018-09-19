package com.inspection.adapter

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.inspection.R.id.itemCheckBox
import com.inspection.model.FacilityDataModel
import com.inspection.model.TblVehicleServices
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.vehicle_services_item.view.*

import java.util.ArrayList

/**
 * Created by kesho on 4/14/2018.
 */

class DatesListAdapter(internal var context: Context, internal var recource: Int, objects: List<TypeTablesModel.scopeofServiceTypeByVehicleType>) : ArrayAdapter<TypeTablesModel.scopeofServiceTypeByVehicleType>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.scopeofServiceTypeByVehicleType>

    init {
        this.namesList = objects
        namesList = objects


    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


       var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(recource, parent, false)
//        val textView3 = view.itemTextView
        val checkBoxItem = view.itemCheckBox

        checkBoxItem.text = namesList.get(position).ScopeServiceName
     //   checkBoxItem.isEnabled=false
        if (FacilityDataModel.getInstance().tblVehicleServices[0].VehiclesTypeID!=-1) {
            if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.ScopeServiceID == namesList.get(position).ScopeServiceID.toInt()}.isNotEmpty()){
                checkBoxItem.isChecked = true
            }
        }

        if (namesList.get(position).active=="true"){

        }

        return view
    }

}

