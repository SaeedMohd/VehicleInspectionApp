package com.inspection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.inspection.FormsActivity
import com.inspection.fragments.FragmentARRAVVehicleServices
import com.inspection.fragments.VehiclesFragmentInScopeOfServicesView
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.vehicle_services_item.view.*

class VehicleListAdapter(internal var context: Context, internal var recource: Int, parentFragment : VehiclesFragmentInScopeOfServicesView , gridType : String, objects: List<TypeTablesModel.vehicleMakes>) : ArrayAdapter<TypeTablesModel.vehicleMakes>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.vehicleMakes>
    internal var parentFragment : VehiclesFragmentInScopeOfServicesView
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


        checkBoxItem.text = namesList.get(position).MakeName
//        var vehicleIDRef = TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.contains(gridType)}[0].VehiclesTypeID.toInt()

        if (FacilityDataModel.getInstance().tblFacVehicles[0].VehicleID!=-1) {
            if (FacilityDataModel.getInstance().tblFacVehicles.filter { s -> s.VehicleID== namesList.get(position).VehicleID}.isNotEmpty()){
                checkBoxItem.isChecked = true
//                updateSelectedLists(namesList.get(position).VehMakeTypeId.toInt(), 1,false)
            }
        }

//        if (namesList.get(position).active=="true"){
//
//        }
        checkBoxItem.setOnClickListener{
            if (checkBoxItem.isChecked == true) {
//                updateSelectedLists(namesList.get(position).VehMakeTypeId.toInt(),1,true)
            } else {
//                updateSelectedLists(namesList.get(position).VehMakeTypeId.toInt(),0,true)
            }
            //FacilityDataModel.getInstance().tblLanguage = langArray
            (context as FormsActivity).saveRequired = true
//            parentFragment.refreshButtonsState()
        }
        return view
    }
//    fun updateSelectedLists(value : Int, action :  Int, isChanged: Boolean) { //1:add,0:remove
//
//        if (gridType.equals("Autom")) {
//            parentFragment.selectedVehicleServicesChanged = isChanged
//            if (action==1) {
//                if (!parentFragment.selectedVehicleServices.contains(value.toString())) parentFragment.selectedVehicleServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedVehicleServices.remove(value.toString())
//        }
//        if (gridType.equals("Body")) {
//            parentFragment.selectedAutoBodyServicesChanged = isChanged
//            if (action==1) {
//                if (!parentFragment.selectedAutoBodyServices.contains(value.toString()))
//                    parentFragment.selectedAutoBodyServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedAutoBodyServices.remove(value.toString())
//        }
//        if (gridType.equals("Marin")) {
//            parentFragment.selectedMarineServicesChanged = isChanged
//            if (action==1) {
//                if (!parentFragment.selectedMarineServices.contains(value.toString()))
//                    parentFragment.selectedMarineServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedMarineServices.remove(value.toString())
//        }
//        if (gridType.equals("RV")) {
//            parentFragment.selectedRecreationServicesChanged = isChanged
//            if (action==1) {
//                if (!parentFragment.selectedRecreationServices.contains(value.toString()))
//                    parentFragment.selectedRecreationServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedRecreationServices.remove(value.toString())
//        }
//        if (gridType.equals("Auto Glass")) {
//            parentFragment.selectedAutoGlassServicesChanged= isChanged
//            if (action==1) {
//                if (!parentFragment.selectedAutoGlassServices.contains(value.toString()))
//                    parentFragment.selectedAutoGlassServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedAutoGlassServices.remove(value.toString())
//        }
//
//        if (gridType.equals("Other")) {
//            parentFragment.selectedOthersServicesChanged= isChanged
//            if (action==1) {
//                if (!parentFragment.selectedOthersServices.contains(value.toString()))
//                    parentFragment.selectedOthersServices.add(value.toString())
//            }
//            else
//                parentFragment.selectedOthersServices.remove(value.toString())
//        }
//
//    }
}
