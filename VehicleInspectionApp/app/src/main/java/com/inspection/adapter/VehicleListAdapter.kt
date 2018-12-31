package com.inspection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.inspection.FormsActivity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.fragments.FragmentARRAVVehicleServices
import com.inspection.fragments.VehiclesFragmentInScopeOfServicesView
import com.inspection.model.FacilityDataModel
import com.inspection.model.TblFacVehicles
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.vehicle_services_item.view.*

class VehicleListAdapter(internal var context: Context, internal var recource: Int, parentFragment : VehiclesFragmentInScopeOfServicesView , gridType : String, objects: List<TypeTablesModel.vehicleMakes>) : ArrayAdapter<TypeTablesModel.vehicleMakes>(context, recource, objects) {
    internal var namesList: List<TypeTablesModel.vehicleMakes>
    internal var parentFragment: VehiclesFragmentInScopeOfServicesView
    internal var gridType: String

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

        if (FacilityDataModel.getInstance().tblFacVehicles[0].VehicleID != -1) {
            if (FacilityDataModel.getInstance().tblFacVehicles.filter { s -> s.VehicleID == namesList.get(position).VehicleID }.isNotEmpty()) {
                checkBoxItem.isChecked = true
                updateSelectedVehicles(namesList.get(position).VehicleID,1,true)
            }
        }

//        if (namesList.get(position).active=="true"){
//
//        }
        checkBoxItem.setOnClickListener {
            (context as FormsActivity).saveRequired = true
            parentFragment.refreshButtonsState()
            if (checkBoxItem.isChecked == true) {
                updateSelectedVehicles(namesList.get(position).VehicleID,1,true)
                val newVehicle = TblFacVehicles()
                newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                newVehicle.VehicleID = namesList.get(position).VehicleID
                FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
            } else {
                updateSelectedVehicles(namesList.get(position).VehicleID,0,true)
                FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==namesList.get(position).VehicleID}
            }
            //FacilityDataModel.getInstance().tblLanguage = langArray
        }
        return view
    }

    fun updateSelectedVehicles(value: Int, action: Int, isChanged: Boolean) { //1:add,0:remove

//        parentFragment.selectedVehicles = isChanged
        if (action == 1) {
            if (!parentFragment.selectedVehicles.contains(value.toString())) parentFragment.selectedVehicles.add(value.toString())
        } else
            parentFragment.selectedVehicles.remove(value.toString())
    }


}
