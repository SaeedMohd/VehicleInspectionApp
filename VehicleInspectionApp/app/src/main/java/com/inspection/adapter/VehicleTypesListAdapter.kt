package com.inspection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import com.inspection.R
import com.inspection.model.TypeTablesModel
//import kotlinx.android.synthetic.main.vehicle_services_item.view.*

class VehicleTypesListAdapter (internal var context: Context, internal var recource: Int, objects: List<TypeTablesModel.vehicleMakesType>) : ArrayAdapter<TypeTablesModel.vehicleMakesType>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.vehicleMakesType>

    init {
        this.namesList = objects
        namesList = objects

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


//        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(recource, parent, false)
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(recource, parent, false)
//        val textView3 = view.itemTextView
//        val checkBoxItem = view.itemCheckBox
        val checkBoxItem = view.findViewById<CheckBox>(R.id.itemCheckBox)
//        val textView3 = view.itemTextView
//        val checkBoxItem = view.itemCheckBox

        checkBoxItem.text = namesList.get(position).VehMakeName
        //   checkBoxItem.isEnabled=false


        if (namesList.get(position).Active=="true"){


            checkBoxItem.isChecked=true

        }

        return view
    }

}

