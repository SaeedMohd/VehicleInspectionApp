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
import com.inspection.FormsActivity
import com.inspection.R.id.itemCheckBox
import com.inspection.adapter.LanguageListAdapter.Companion.langArray
import com.inspection.fragments.FragmentARRAVFacilityServices
import com.inspection.fragments.FragmentARRAVVehicleServices
import com.inspection.model.FacilityDataModel
import com.inspection.model.TblVehicleServices
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.vehicle_services_item.view.*

import java.util.ArrayList

/**
 * Created by kesho on 4/14/2018.
 */

class DatesListAdapter(internal var context: Context, internal var recource: Int,parentFragment : FragmentARRAVVehicleServices,gridType : String,  objects: List<TypeTablesModel.scopeofServiceTypeByVehicleType>) : ArrayAdapter<TypeTablesModel.scopeofServiceTypeByVehicleType>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.scopeofServiceTypeByVehicleType>
    internal var parentFragment : FragmentARRAVVehicleServices
    internal var gridType : String
    init {
        this.namesList = objects
        namesList = objects
        this.parentFragment = parentFragment
        this.gridType = gridType
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
                updateSelectedLists(namesList.get(position).ScopeServiceID.toInt(), 1)
            }
        }

        if (namesList.get(position).active=="true"){

        }
        checkBoxItem.setOnClickListener{
            if (checkBoxItem.isChecked == true) {
                updateSelectedLists(namesList.get(position).ScopeServiceID.toInt(),1)
            } else {
                updateSelectedLists(namesList.get(position).ScopeServiceID.toInt(),0)
            }
            //FacilityDataModel.getInstance().tblLanguage = langArray
            (context as FormsActivity).saveRequired = true
            parentFragment.refreshButtonsState()
        }
        return view
    }
    fun updateSelectedLists(value : Int, action :  Int) { //1:add,0:remove

        if (gridType.equals("Autom")) {
            if (action==1)
                parentFragment.selectedVehicleServices += "${value},"
            else
                parentFragment.selectedVehicleServices = parentFragment.selectedVehicleServices.replace("${value},","")
        }
        if (gridType.equals("Body")) {
            if (action==1)
                parentFragment.selectedAutoBodyServices+= "${value},"
            else
                parentFragment.selectedAutoBodyServices = parentFragment.selectedAutoBodyServices.replace("${value},","")
        }
        if (gridType.equals("Marin")) {
            if (action==1)
                parentFragment.selectedMarineServices+= "${value},"
            else
                parentFragment.selectedMarineServices = parentFragment.selectedMarineServices.replace("${value},","")
        }
        if (gridType.equals("RV")) {
            if (action==1)
                parentFragment.selectedRecreationServices += "${value},"
            else
                parentFragment.selectedRecreationServices = parentFragment.selectedRecreationServices.replace("${value},","")
        }
        if (gridType.equals("Auto Glass")) {
            if (action==1)
                parentFragment.selectedAutoGlassServices += "${value},"
            else
                parentFragment.selectedAutoGlassServices = parentFragment.selectedAutoGlassServices.replace("${value},","")
        }

        if (gridType.equals("Other")) {
            if (action==1)
                parentFragment.selectedOthersServices += "${value},"
            else
                parentFragment.selectedOthersServices = parentFragment.selectedOthersServices.replace("${value},","")
        }

    }
}

