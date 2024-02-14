package com.inspection.adapter

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import androidx.fragment.app.FragmentActivity
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

class FuelListAdapter(internal var context: Context, internal var recource: Int,parentFragment : FragmentARRAVVehicleServices,gridType : String,  objects: List<TypeTablesModel.scopeofServiceTypeByVehicleType>,categoryID: String) : ArrayAdapter<TypeTablesModel.scopeofServiceTypeByVehicleType>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.scopeofServiceTypeByVehicleType>
    internal var parentFragment : FragmentARRAVVehicleServices
    internal var gridType : String
    internal var categoryID : String

    init {
        this.namesList = objects
        namesList = objects
        this.parentFragment = parentFragment
        this.gridType = gridType
        this.categoryID = categoryID
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(recource, parent, false)
//        val textView3 = view.itemTextView
        val checkBoxItem = view.itemCheckBox


        checkBoxItem.text = namesList.get(position).ScopeServiceName
        var vehicleIDRef = TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.contains(gridType)}[0].VehiclesTypeID.toInt()
        if (FacilityDataModel.getInstance().tblVehicleServices[0].VehiclesTypeID!=-1) {
            if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.ServiceID == namesList.get(position).ServiceID}.filter { s -> s.VehiclesTypeID == vehicleIDRef}.isNotEmpty()){
                checkBoxItem.isChecked = true
                updateSelectedLists(namesList.get(position).ServiceID.toInt(), 1,false,namesList.get(position).ScopeServiceName)
            }
        }


        if (namesList.get(position).active=="true"){

        }
        checkBoxItem.setOnClickListener{
            if (checkBoxItem.isChecked == true) {
                updateSelectedLists(namesList.get(position).ServiceID.toInt(),1,true,namesList.get(position).ScopeServiceName)
                val newVehicle = TblVehicleServices()
                newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                newVehicle.ServiceID = namesList.get(position).ServiceID
                newVehicle.VehiclesTypeID = vehicleIDRef
                newVehicle.VehicleCategoryID = categoryID.toString()
                FacilityDataModel.getInstance().tblVehicleServices.add(newVehicle)
            } else {
                updateSelectedLists(namesList.get(position).ServiceID.toInt(),0,true,namesList.get(position).ScopeServiceName)
                FacilityDataModel.getInstance().tblVehicleServices.removeIf { s->s.ServiceID==namesList.get(position).ServiceID && s.VehiclesTypeID==vehicleIDRef}
            }
            //FacilityDataModel.getInstance().tblLanguage = langArray
            (context as FormsActivity).saveRequired = true
            parentFragment.refreshButtonsState()
        }
        return view
    }

    fun updateSelectedLists(value : Int, action :  Int, isChanged: Boolean, valueName: String) { //1:add,0:remove

        if (gridType.equals("Autom")) {
            if (isChanged) parentFragment.selectedVehicleServicesChanged = isChanged
            if (action==1) {
                if (!parentFragment.selectedVehicleServices.contains(value.toString())) parentFragment.selectedVehicleServices.add(value.toString())
                if (!parentFragment.selectedVehicleServicesNames.contains(valueName)) parentFragment.selectedVehicleServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedVehicleServices.remove(value.toString())
                parentFragment.selectedVehicleServicesNames.remove(valueName)
            }
        }
        if (gridType.equals("Body")) {
            parentFragment.selectedAutoBodyServicesChanged = isChanged
            if (action==1) {
                if (!parentFragment.selectedAutoBodyServices.contains(value.toString())) parentFragment.selectedAutoBodyServices.add(value.toString())
                if (!parentFragment.selectedAutoBodyServicesNames.contains(valueName)) parentFragment.selectedAutoBodyServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedAutoBodyServices.remove(value.toString())
                parentFragment.selectedAutoBodyServicesNames.remove(valueName)
            }
        }
        if (gridType.equals("Marin")) {
            parentFragment.selectedMarineServicesChanged = isChanged
            if (action==1) {
                if (!parentFragment.selectedMarineServices.contains(value.toString())) parentFragment.selectedMarineServices.add(value.toString())
                if (!parentFragment.selectedMarineServicesNames.contains(valueName)) parentFragment.selectedMarineServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedMarineServices.remove(value.toString())
                parentFragment.selectedMarineServicesNames.remove(valueName)
            }
        }
        if (gridType.equals("RV")) {
            parentFragment.selectedRecreationServicesChanged = isChanged
            if (action==1) {
                if (!parentFragment.selectedRecreationServices.contains(value.toString())) parentFragment.selectedRecreationServices.add(value.toString())
                if (!parentFragment.selectedRecreationServicesNames.contains(valueName)) parentFragment.selectedRecreationServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedRecreationServices.remove(value.toString())
                parentFragment.selectedRecreationServicesNames.remove(valueName)
            }
        }
        if (gridType.equals("Auto Glass")) {
            parentFragment.selectedAutoGlassServicesChanged= isChanged
            if (action==1) {
                if (!parentFragment.selectedAutoGlassServices.contains(value.toString())) parentFragment.selectedAutoGlassServices.add(value.toString())
                if (!parentFragment.selectedAutoGlassServicesNames.contains(valueName)) parentFragment.selectedAutoGlassServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedAutoGlassServices.remove(value.toString())
                parentFragment.selectedAutoGlassServicesNames.remove(valueName)
            }
        }

        if (gridType.equals("Other")) {
            parentFragment.selectedOthersServicesChanged= isChanged
            if (action==1) {
                if (!parentFragment.selectedOthersServices.contains(value.toString())) parentFragment.selectedOthersServices.add(value.toString())
                if (!parentFragment.selectedOthersServicesNames.contains(valueName)) parentFragment.selectedOthersServicesNames.add(valueName)
            }
            else {
                parentFragment.selectedOthersServices.remove(value.toString())
                parentFragment.selectedOthersServicesNames.remove(valueName)
            }
        }

    }
}

