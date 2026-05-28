package com.inspection.fragments


import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.adapter.VehicleListAdapter
import com.inspection.adapter.VehicleTypesListAdapter
import com.inspection.databinding.FragmentAaravPaymentsBinding
import com.inspection.databinding.FragmentVehiclesFragmentInScopeOfServicesViewBinding
import com.inspection.model.*
import java.util.*
import kotlin.collections.ArrayList

//vehicleType_textviewVal

/**
 * A simple [Fragment] subclass.
 */
class VehiclesFragmentInScopeOfServicesView : Fragment() {


    var DomesticVehiclesListView: ExpandableHeightGridView? = null
    var AsianVehiclesListView: ExpandableHeightGridView? = null
    var EuropeanVehiclesListView: ExpandableHeightGridView? = null
    var ExoticVehiclesListView: ExpandableHeightGridView? = null
    var OtherVehiclesListView: ExpandableHeightGridView? = null
    var CNGVehiclesListView: ExpandableHeightGridView? = null
    var DieselVehiclesListView: ExpandableHeightGridView? = null
    var ElectricVehiclesListView: ExpandableHeightGridView? = null
    var GasVehiclesListView: ExpandableHeightGridView? = null
    var HybridVehiclesListView: ExpandableHeightGridView? = null
    var HydrogenVehiclesListView: ExpandableHeightGridView? = null



    internal var domesticAdapter: VehicleListAdapter? = null
    internal var asianAdapter: VehicleListAdapter? = null
    internal var europeanAdapter: VehicleListAdapter? = null
    internal var exoticAdapter: VehicleListAdapter? = null
    internal var otherAdapter: VehicleListAdapter? = null
    internal var cngAdapter: VehicleListAdapter? = null
    internal var dieselAdapter: VehicleListAdapter? = null
    internal var electricAdapter: VehicleListAdapter? = null
    internal var gasAdapter: VehicleListAdapter? = null
    internal var hybridAdapter: VehicleListAdapter? = null
    internal var hydrogenAdapter: VehicleListAdapter? = null

    var domesticListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var asianListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var europeanListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var exoticListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var otherListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var cngListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var dieselListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var electricListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var gasListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var hybridListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var hydrogenListItems=ArrayList<TypeTablesModel.vehicleMakes>()

    var selectedVehicles = ArrayList<String>()

    private var vehicleTypeList = ArrayList<TypeTablesModel.vehiclesType>()
    private var vehicleTypeArray = ArrayList<String>()

    private var _binding: FragmentVehiclesFragmentInScopeOfServicesViewBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_vehicles_fragment_in_scope_of_services_view, container, false)
        DomesticVehiclesListView = view.findViewById(R.id.DomesticVehiclesListView)
        AsianVehiclesListView = view.findViewById(R.id.AsianVehiclesListView)
        EuropeanVehiclesListView = view.findViewById(R.id.EuropeanVehiclesListView)
        ExoticVehiclesListView = view.findViewById(R.id.ExoticVehiclesListView)
        OtherVehiclesListView = view.findViewById(R.id.otherTypesVehiclesListView)
        CNGVehiclesListView = view.findViewById(R.id.cngTypesVehiclesListView)
        DieselVehiclesListView = view.findViewById(R.id.dieselTypesVehiclesListView)
        ElectricVehiclesListView = view.findViewById(R.id.electricTypesVehiclesListView)
        GasVehiclesListView = view.findViewById(R.id.gasolineTypesVehiclesListView)
        HybridVehiclesListView = view.findViewById(R.id.hybridTypesVehiclesListView)
        HydrogenVehiclesListView = view.findViewById(R.id.hydrogenTypesVehiclesListView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (progressbarVehicleServices != null) {
//            progressbarVehicleServices.visibility = View.VISIBLE
//        }
        _binding = FragmentVehiclesFragmentInScopeOfServicesViewBinding.bind(view)
        vehicleTypeList = TypeTablesModel.getInstance().VehiclesType
        vehicleTypeArray.clear()
        for (fac in vehicleTypeList) {
            vehicleTypeArray.add(fac.VehiclesTypeName)
        }

        var vehicleTypeAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item_modern, vehicleTypeArray)
        vehicleTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.vehicleTypeSpinner.adapter = vehicleTypeAdapter
        binding.vehicleTypeSpinner.setPopupBackgroundResource(R.drawable.spinner_popup_bg)
        binding.vehicleTypeSpinner.setSelection(vehicleTypeArray.indexOf("Automobile"))

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited= true
        // SAEED TO BE REVIEWED
        (requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as? HasTabIndicators)?.refreshTabIndicators()
//        (activity as FormsActivity).vehiclesButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        binding.vehicleTypeSpinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setServices()
            }
        }

        setServices()
        binding.cngCheckBox.setOnClickListener {
            if (binding.cngCheckBox.isChecked) {
                for (i in 0..cngListItems.size-1) {
                    if (!selectedVehicles.contains(cngListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(cngListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==cngListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = cngListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..cngListItems.size-1) {
                    if (selectedVehicles.contains(cngListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(cngListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==cngListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==cngListItems[i].VehicleID}
                        }
                    }
                }
            }
            cngAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", cngListItems)
            CNGVehiclesListView?.adapter = cngAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.dieselCheckBox.setOnClickListener {
            if (binding.dieselCheckBox.isChecked) {
                for (i in 0..dieselListItems.size-1) {
                    if (!selectedVehicles.contains(dieselListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(dieselListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==dieselListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = dieselListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..dieselListItems.size-1) {
                    if (selectedVehicles.contains(dieselListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(dieselListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==dieselListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==dieselListItems[i].VehicleID}
                        }
                    }
                }
            }
            dieselAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", dieselListItems)
            DieselVehiclesListView?.adapter = dieselAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.hydrogenCheckBox.setOnClickListener {
            if (binding.hydrogenCheckBox.isChecked) {
                for (i in 0..hydrogenListItems.size-1) {
                    if (!selectedVehicles.contains(hydrogenListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(hydrogenListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==hydrogenListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = hydrogenListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..hydrogenListItems.size-1) {
                    if (selectedVehicles.contains(hydrogenListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(hydrogenListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==hydrogenListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==hydrogenListItems[i].VehicleID}
                        }
                    }
                }
            }
            hydrogenAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", hydrogenListItems)
            HydrogenVehiclesListView?.adapter = hydrogenAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.gasCheckBox.setOnClickListener {
            if (binding.gasCheckBox.isChecked) {
                for (i in 0..gasListItems.size-1) {
                    if (!selectedVehicles.contains(gasListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(gasListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==gasListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = gasListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..gasListItems.size-1) {
                    if (selectedVehicles.contains(gasListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(gasListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==gasListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==gasListItems[i].VehicleID}
                        }
                    }
                }
            }
            gasAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", gasListItems)
            GasVehiclesListView?.adapter = gasAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.hybridCheckBox.setOnClickListener {
            if (binding.hybridCheckBox.isChecked) {
                for (i in 0..hybridListItems.size-1) {
                    if (!selectedVehicles.contains(hybridListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(hybridListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==hybridListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = hybridListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..hybridListItems.size-1) {
                    if (selectedVehicles.contains(hybridListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(hybridListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==hybridListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==hybridListItems[i].VehicleID}
                        }
                    }
                }
            }
            hybridAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", hybridListItems)
            HybridVehiclesListView?.adapter = hybridAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.electricCheckBox.setOnClickListener {
            if (binding.electricCheckBox.isChecked) {
                for (i in 0..electricListItems.size-1) {
                    if (!selectedVehicles.contains(electricListItems[i].VehicleID.toString())) {
                        selectedVehicles.add(electricListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==electricListItems[i].VehicleID}.isEmpty()) {
                            var newVehicle = TblFacVehicles()
                            newVehicle.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID
                            newVehicle.VehicleID = electricListItems[i].VehicleID
                            FacilityDataModel.getInstance().tblFacVehicles.add(newVehicle)
                        }
                    }
                }
            } else {
                for (i in 0..electricListItems.size-1) {
                    if (selectedVehicles.contains(electricListItems[i].VehicleID.toString())) {
                        selectedVehicles.remove(electricListItems[i].VehicleID.toString())
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==electricListItems[i].VehicleID}.isNotEmpty()) {
                            FacilityDataModel.getInstance().tblFacVehicles.removeIf { s->s.VehicleID==electricListItems[i].VehicleID}
                        }
                    }
                }
            }
            electricAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", electricListItems)
            ElectricVehiclesListView?.adapter = electricAdapter
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        binding.cancelButton.setOnClickListener {
            binding.progressBarText.text = "Cancelling ..."
            binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
            FacilityDataModel.getInstance().tblFacVehicles.clear()
            for (i in 0..FacilityDataModelOrg.getInstance().tblFacVehicles.size-1) {
                var vehicleServiceItem = TblFacVehicles()
                vehicleServiceItem.FACID= FacilityDataModelOrg.getInstance().tblFacVehicles[i].FACID
                vehicleServiceItem.VehicleID = FacilityDataModelOrg.getInstance().tblFacVehicles[i].VehicleID
                vehicleServiceItem.insertBy= FacilityDataModelOrg.getInstance().tblFacVehicles[i].insertBy
                vehicleServiceItem.updateBy= FacilityDataModelOrg.getInstance().tblFacVehicles[i].updateBy
                vehicleServiceItem.insertDate = FacilityDataModelOrg.getInstance().tblFacVehicles[i].insertDate
                vehicleServiceItem.updateDate= FacilityDataModelOrg.getInstance().tblFacVehicles[i].updateDate
                FacilityDataModel.getInstance().tblFacVehicles.add(vehicleServiceItem)
            }
            (activity as FormsActivity).saveRequired = false
            binding.vehicleTypeSpinner.setSelection(vehicleTypeArray.indexOf("Automobile"))
            setServices()
            refreshButtonsState()
//            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
            Utility.showUnifiedConfirmationDialog(activity,  "Changes cancelled successfully")
            binding.progressBarText.text = "Loading ..."
        }

        binding.saveButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                binding.progressBarText.text = "Saving ..."
                binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
                saveVehicleChanges()
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }


    }

    fun saveVehicleChanges() {
        var orgVehicles = ""
        var addedData = ""
        var removedData = ""
        var totalDataChanges = ""
        var removedVehicles = ArrayList<String>()
        var addedVehicles = ArrayList<String>()

        for (i in 0..FacilityDataModelOrg.getInstance().tblFacVehicles.size-1) {
            if (!selectedVehicles.contains(FacilityDataModelOrg.getInstance().tblFacVehicles[i].VehicleID.toString())){
                removedVehicles.add(FacilityDataModelOrg.getInstance().tblFacVehicles[i].VehicleID.toString())
            }
        }
        for (i in 0..selectedVehicles.size-1) {
            if (FacilityDataModelOrg.getInstance().tblFacVehicles.filter { s->s.VehicleID==selectedVehicles[i].toInt()}.isEmpty()){
                addedVehicles.add(selectedVehicles[i])
            }
        }

//        addedData += "Added Vehicle(s): ["
        var vehcileCount = 0
        for (i in 0 until addedVehicles.size) {
            if (TypeTablesModel.getInstance().VehicleMakes.filter {s->s.VehicleID==addedVehicles[i].toInt()}.isNotEmpty()) {
                vehcileCount++
                val item = TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleID == addedVehicles[i].toInt() }[0]
//                addedData += "Type (" + TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeID.toInt() == item.VehicleTypeID }[0].VehiclesTypeName + ")"
//                addedData += ", Category (" + TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { s -> s.VehCategoryID.toInt() == item.VehicleCategoryID }[0].VehCategoryName + ")"
//                addedData += ", Make (" + item.MakeName + ") - "
                addedData += TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { s -> s.VehCategoryID.toInt() == item.VehicleCategoryID }[0].VehCategoryName + " " + item.MakeName + " - "
            }
        }
        addedData = "Added Vehicle(s): [ " + vehcileCount + " ]"

//        removedData += "Removed Vehicle(s): ["
        vehcileCount = 0
        for (i in 0 until removedVehicles.size) {
            if (TypeTablesModel.getInstance().VehicleMakes.filter {s->s.VehicleID==removedVehicles[i].toInt()}.isNotEmpty()) {
                vehcileCount++
                val item = TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleID == removedVehicles[i].toInt() }[0]
//                removedData += "Type (" + TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeID.toInt() == item.VehicleTypeID }[0].VehiclesTypeName + ")"
//                removedData += ", Category (" + TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { s -> s.VehCategoryID.toInt() == item.VehicleCategoryID }[0].VehCategoryName + ")"
//                removedData += ", Make (" + item.MakeName + ") - "
                removedData += TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { s -> s.VehCategoryID.toInt() == item.VehicleCategoryID }[0].VehCategoryName + " " + item.MakeName + " - "
            }
        }
        removedData = "Removed Vehicle(s): [ " + vehcileCount + " ]"
//        removedData = removedData.removeSuffix(" - ") + "]"
//        addedData = addedData.removeSuffix(" - ") + "]"
//        addedData = addedData.removeSuffix(" - ")
        totalDataChanges = addedData + " - " + removedData
//        Utility.showUnifiedInformationDialog(requireContext(),  totalDataChanges)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateFacilityVehicles+ FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubcode=${FacilityDataModel.getInstance().clubCode}&VehicleID=${selectedVehicles.toString().removePrefix("[").removeSuffix("]").replace(" ","")}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 1, totalDataChanges),
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<", false)) {
                        HasChangedModel.getInstance().updateChangedData("Vehicles", "", "", totalDataChanges)
                        FacilityDataModelOrg.getInstance().tblFacVehicles.clear()
                        for (i in 0..FacilityDataModel.getInstance().tblFacVehicles.size - 1) {
                            var vehicleServiceItem = TblFacVehicles()
                            vehicleServiceItem.FACID = FacilityDataModel.getInstance().tblFacVehicles[i].FACID
                            vehicleServiceItem.VehicleID = FacilityDataModel.getInstance().tblFacVehicles[i].VehicleID
                            vehicleServiceItem.insertBy = FacilityDataModel.getInstance().tblFacVehicles[i].insertBy
                            vehicleServiceItem.updateBy = FacilityDataModel.getInstance().tblFacVehicles[i].updateBy
                            vehicleServiceItem.insertDate = FacilityDataModel.getInstance().tblFacVehicles[i].insertDate
                            vehicleServiceItem.updateDate = FacilityDataModel.getInstance().tblFacVehicles[i].updateDate
                            FacilityDataModelOrg.getInstance().tblFacVehicles.add(vehicleServiceItem)
                        }
                        HasChangedModel.getInstance().checkIfChangeWasDoneforSoSVehicles()
                        HasChangedModel.getInstance().changeDoneForSoSVehicles()
                        onSaveComplete(true)
                    } else {
                        val errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                        onSaveComplete(false, "Error: $errorMessage")
                    }
                }
            },
            {
                onSaveComplete(false, "Error: ${it.message}")
            }))
    }

    private fun onSaveComplete(success: Boolean, errorMsg: String = "") {
        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
        binding.progressBarText.text = "Loading ..."
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
        if (success) {
            (activity as FormsActivity).saveDone = true
            Utility.showSubmitAlertDialog(activity, true, "Vehicles")
        } else {
            Utility.showSubmitAlertDialog(activity, false, "Vehicles ($errorMsg)")
        }
    }







    fun refreshButtonsState(){
        binding.saveButton.isEnabled = (activity as FormsActivity).saveRequired
        binding.cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }



    private fun setServices() {

        domesticListItems.clear()
        asianListItems.clear()
        europeanListItems.clear()
        exoticListItems.clear()
        otherListItems.clear()
        cngListItems.clear()
        dieselListItems.clear()
        electricListItems.clear()
        gasListItems.clear()
        hybridListItems.clear()
        hydrogenListItems.clear()
        DomesticVehiclesListView?.adapter = null
        AsianVehiclesListView?.adapter = null
        EuropeanVehiclesListView?.adapter = null
        ExoticVehiclesListView?.adapter = null
        OtherVehiclesListView?.adapter = null
        CNGVehiclesListView?.adapter = null
        DieselVehiclesListView?.adapter = null
        ElectricVehiclesListView?.adapter = null
        GasVehiclesListView?.adapter = null
        HybridVehiclesListView?.adapter = null
        HydrogenVehiclesListView?.adapter = null
        DomesticVehiclesListView?.isVisible = false
        AsianVehiclesListView?.isVisible = false
        EuropeanVehiclesListView?.isVisible = false
        ExoticVehiclesListView?.isVisible = false
        OtherVehiclesListView?.isVisible = false
        CNGVehiclesListView?.isVisible = false
        DieselVehiclesListView?.isVisible = false
        ElectricVehiclesListView?.isVisible = false
        GasVehiclesListView?.isVisible = false
        HybridVehiclesListView?.isVisible = false
        HydrogenVehiclesListView?.isVisible = false

        for (model in TypeTablesModel.getInstance().VehicleMakes.filter { S -> S.VehicleTypeID==TypeTablesModel.getInstance().VehiclesType.filter { S->S.VehiclesTypeName.equals(binding.vehicleTypeSpinner.selectedItem.toString())}[0].VehiclesTypeID.toInt()}) {
            if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Domestic")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Domestic")}[0].VehCategoryID.toInt()){
                domesticListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Asian")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Asian")}[0].VehCategoryID.toInt()){
                asianListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - European")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - European")}[0].VehCategoryID.toInt()){
                europeanListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Exotic")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Make - Exotic")}[0].VehCategoryID.toInt()){
                exoticListItems.add(model)
//            } else if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Other Types")}[0].VehCategoryID.toInt()){
//                otherListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("CNG/LNG")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("CNG/LNG")}[0].VehCategoryID.toInt()){
                cngListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Diesel")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Diesel")}[0].VehCategoryID.toInt()){
                dieselListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Electric")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Electric")}[0].VehCategoryID.toInt()){
                electricListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Gasoline")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Gasoline")}[0].VehCategoryID.toInt()){
                gasListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Hybrid")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Hybrid")}[0].VehCategoryID.toInt()){
                hybridListItems.add(model)
            } else if (TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Hydrogen")}.isNotEmpty() && model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Hydrogen")}[0].VehCategoryID.toInt()){
                hydrogenListItems.add(model)
            }
        }


        if (domesticListItems.count() > 0) {
            domesticAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", domesticListItems)
            DomesticVehiclesListView?.adapter = domesticAdapter
            DomesticVehiclesListView?.isExpanded = true
            DomesticVehiclesListView?.isVisible = true
            binding.domesticContainer.visibility = View.VISIBLE
        } else {
            binding.domesticContainer.visibility = View.GONE
        }
        if (asianListItems.count() > 0) {
            asianAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", asianListItems)
            AsianVehiclesListView?.adapter = asianAdapter
            AsianVehiclesListView?.isExpanded = true
            AsianVehiclesListView?.isVisible = true
            binding.asianContainer.visibility = View.VISIBLE
        } else {
            binding.asianContainer.visibility = View.GONE
        }
        if (europeanListItems.count() > 0) {
            europeanAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", europeanListItems)
            EuropeanVehiclesListView?.adapter = europeanAdapter
            EuropeanVehiclesListView?.isExpanded = true
            EuropeanVehiclesListView?.isVisible = true
            binding.europeanContainer.visibility = View.VISIBLE
        } else {
            binding.europeanContainer.visibility = View.GONE
        }
        if (exoticListItems.count() > 0) {
            exoticAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", exoticListItems)
            ExoticVehiclesListView?.adapter = exoticAdapter
            ExoticVehiclesListView?.isExpanded = true
            ExoticVehiclesListView?.isVisible = true
            binding.exoticContainer.visibility = View.VISIBLE
        } else {
            binding.exoticContainer.visibility = View.GONE
        }
        if (otherListItems.count() > 0) {
            otherAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", otherListItems)
            OtherVehiclesListView?.adapter = otherAdapter
            OtherVehiclesListView?.isExpanded = true
            OtherVehiclesListView?.isVisible = true
            binding.otherContainer.visibility = View.VISIBLE
        } else {
            binding.otherContainer.visibility = View.GONE
        }
        if (cngListItems.count() > 0) {
            cngAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", cngListItems)
            CNGVehiclesListView?.adapter = cngAdapter
            CNGVehiclesListView?.isExpanded = true
            CNGVehiclesListView?.isVisible = true
            binding.CNGContainer.visibility = View.VISIBLE
        } else {
            binding.CNGContainer.visibility = View.GONE
        }
        if (dieselListItems.count() > 0) {
            dieselAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", dieselListItems)
            DieselVehiclesListView?.adapter = dieselAdapter
            DieselVehiclesListView?.isExpanded = true
            DieselVehiclesListView?.isVisible = true
            binding.DieselContainer.visibility = View.VISIBLE
        } else {
            binding.DieselContainer.visibility = View.GONE
        }

        if (electricListItems.count() > 0) {
            electricAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", electricListItems)
            ElectricVehiclesListView?.adapter = electricAdapter
            ElectricVehiclesListView?.isExpanded = true
            ElectricVehiclesListView?.isVisible = true
            binding.ElectricContainer.visibility = View.VISIBLE
        } else {
            binding.ElectricContainer.visibility = View.GONE
        }

        if (gasListItems.count() > 0) {
            gasAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", gasListItems)
            GasVehiclesListView?.adapter = gasAdapter
            GasVehiclesListView?.isExpanded = true
            GasVehiclesListView?.isVisible = true
            binding.GasContainer.visibility = View.VISIBLE
        } else {
            binding.GasContainer.visibility = View.GONE
        }

        if (hybridListItems.count() > 0) {
            hybridAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", hybridListItems)
            HybridVehiclesListView?.adapter = hybridAdapter
            HybridVehiclesListView?.isExpanded = true
            HybridVehiclesListView?.isVisible = true
            binding.HybridContainer.visibility = View.VISIBLE
        } else {
            binding.HybridContainer.visibility = View.GONE
        }

        if (hydrogenListItems.count() > 0) {
            hydrogenAdapter = VehicleListAdapter(requireContext(), R.layout.vehicle_services_item, this, "", hydrogenListItems)
            HydrogenVehiclesListView?.adapter = hydrogenAdapter
            HydrogenVehiclesListView?.isExpanded = true
            HydrogenVehiclesListView?.isVisible = true
            binding.HydrogenContainer.visibility = View.VISIBLE
        } else {
            binding.HydrogenContainer.visibility = View.GONE
        }

        refreshButtonsState()
        binding.expandablell.visibility = View.VISIBLE
        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//        DomesticVehiclesListView?.adapter = arrayAdapter
//        DomesticVehiclesListView?.isExpanded=true


//
//
//            arrayAdapter5 = DatesListAdapter(context!!, R.layout.vehicle_services_item, GlassServicesListItems)
//
//        GlassServicesListView?.adapter = arrayAdapter5
//        GlassServicesListView?.isExpanded=true
//
//
//
//
//            arrayAdapter6 = DatesListAdapter(context!!, R.layout.vehicle_services_item, OtherServicesListItems)
//
//        OtherServicesListView?.adapter = arrayAdapter6
//        OtherServicesListView?.isExpanded=true
//
//

        //  vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehicleServicesListItems)

        //   prepareView()
//        if (progressbarVehicleServices != null) {
//
//            progressbarVehicleServices.visibility = View.INVISIBLE
//        }
    }
    fun scopeOfServiceChangesWatcher(){

//        if (FragmentARRAVScopeOfService.dataChanged) {
//
//            val builder = AlertDialog.Builder(context)
//
//            // Set the alert dialog title
//            builder.setTitle("Changes made confirmation")
//
//            // Display a message on alert dialog
//            builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")
//
//            // Set a positive button and its click listener on alert dialog
//            builder.setPositiveButton("YES") { dialog, which ->
//
//                scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
//
//
//
//                Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                        Response.Listener { response ->
//                            activity!!.runOnUiThread(Runnable {
//                                Log.v("RESPONSE", response.toString())
//                                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//                                Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
//                                if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
//                                    FacilityDataModel.getInstance().tblScopeofService[0].apply {
//
//                                        LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
//                                        LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank())LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
//                                        FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank())FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
//                                        DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank())DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
//                                        NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank())NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
//                                        NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank())NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_
//
//                                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare
//
//                                        FragmentARRAVScopeOfService.dataChanged =false
//
//                                    }
//
//                                }
//
//                            })
//                        }, Response.ErrorListener {
//                    Log.v("error while loading", "error while loading personnal record")
//                    Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
//                    scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//
//                }))
//
//
//            }
//
//
//
//
//
//            // Display a negative button on alert dialog
//            builder.setNegativeButton("No") { dialog, which ->
//                FragmentARRAVScopeOfService.dataChanged =false
//                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//            }
//
//
//
//
//            // Finally, make the alert dialog using builder
//            val dialog: AlertDialog = builder.create()
//            dialog.setCanceledOnTouchOutside(false)
//            // Display the alert dialog on app interface
//            dialog.show()
//
//        }

    }

    fun spinnerFilling(){

        var vehiclesTypesArray = ArrayList<String>()
        for (fac in TypeTablesModel.getInstance().VehiclesType) {


            vehiclesTypesArray.add(fac.VehiclesTypeName)
        }
//
//        var programsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, vehiclesTypesArray)
//        programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        vehicleType_textviewVal.adapter = programsAdapter

    }
    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): VehiclesFragmentInScopeOfServicesView {
            val fragment = VehiclesFragmentInScopeOfServicesView()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
