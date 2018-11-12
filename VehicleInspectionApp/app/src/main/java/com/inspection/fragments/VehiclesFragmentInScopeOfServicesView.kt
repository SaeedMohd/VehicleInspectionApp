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
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.adapter.VehicleListAdapter
import com.inspection.adapter.VehicleTypesListAdapter
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_vehicles_fragment_in_scope_of_services_view.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import java.util.*

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

    internal var domesticAdapter: VehicleListAdapter? = null
    internal var asianAdapter: VehicleListAdapter? = null
    internal var europeanAdapter: VehicleListAdapter? = null
    internal var exoticAdapter: VehicleListAdapter? = null
    internal var otherAdapter: VehicleListAdapter? = null

    var domesticListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var asianListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var europeanListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var exoticListItems=ArrayList<TypeTablesModel.vehicleMakes>()
    var otherListItems=ArrayList<TypeTablesModel.vehicleMakes>()



    private var vehicleTypeList = ArrayList<TypeTablesModel.vehiclesType>()
    private var vehicleTypeArray = ArrayList<String>()





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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (progressbarVehicleServices != null) {
//            progressbarVehicleServices.visibility = View.VISIBLE
//        }

        vehicleTypeList = TypeTablesModel.getInstance().VehiclesType
        vehicleTypeArray.clear()
        for (fac in vehicleTypeList) {
            vehicleTypeArray.add(fac.VehiclesTypeName)
        }

        var vehicleTypeAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, vehicleTypeArray)
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.adapter = vehicleTypeAdapter

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited= true
        (activity as FormsActivity).vehiclesButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        vehicleTypeSpinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setServices()
            }
        }

        setServices()

    }



    private fun setServices() {

        domesticListItems.clear()
        asianListItems.clear()
        europeanListItems.clear()
        exoticListItems.clear()
        otherListItems.clear()
        DomesticVehiclesListView?.adapter = null
        AsianVehiclesListView?.adapter = null
        EuropeanVehiclesListView?.adapter = null
        ExoticVehiclesListView?.adapter = null
        OtherVehiclesListView?.adapter = null
        DomesticVehiclesListView?.isVisible = false
        AsianVehiclesListView?.isVisible = false
        EuropeanVehiclesListView?.isVisible = false
        ExoticVehiclesListView?.isVisible = false
        OtherVehiclesListView?.isVisible = false

        for (model in TypeTablesModel.getInstance().VehicleMakes.filter { S -> S.VehicleTypeID==TypeTablesModel.getInstance().VehiclesType.filter { S->S.VehiclesTypeName.equals(vehicleTypeSpinner.selectedItem.toString())}[0].VehiclesTypeID.toInt()}) {
            if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Domestic")}[0].VehCategoryID.toInt()){
                domesticListItems.add(model)
            } else if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Asian")}[0].VehCategoryID.toInt()){
                asianListItems.add(model)
            } else if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("European")}[0].VehCategoryID.toInt()){
                europeanListItems.add(model)
            } else if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Exotic")}[0].VehCategoryID.toInt()){
                exoticListItems.add(model)
            } else if (model.VehicleCategoryID==TypeTablesModel.getInstance().VehiclesMakesCategoryType.filter { S->S.VehCategoryName.equals("Other Types")}[0].VehCategoryID.toInt()){
                otherListItems.add(model)
            }
        }


        if (domesticListItems.count() > 0) {
            domesticAdapter = VehicleListAdapter(context!!, R.layout.vehicle_services_item, this, "", domesticListItems)
            DomesticVehiclesListView?.adapter = domesticAdapter
            DomesticVehiclesListView?.isExpanded = true
            DomesticVehiclesListView?.isVisible = true
        }
        if (asianListItems.count() > 0) {
            asianAdapter = VehicleListAdapter(context!!, R.layout.vehicle_services_item, this, "", asianListItems)
            AsianVehiclesListView?.adapter = asianAdapter
            AsianVehiclesListView?.isExpanded = true
            AsianVehiclesListView?.isVisible = true
        }
        if (europeanListItems.count() > 0) {
            europeanAdapter = VehicleListAdapter(context!!, R.layout.vehicle_services_item, this, "", europeanListItems)
            EuropeanVehiclesListView?.adapter = europeanAdapter
            EuropeanVehiclesListView?.isExpanded = true
            EuropeanVehiclesListView?.isVisible = true
        }
        if (exoticListItems.count() > 0) {
            exoticAdapter = VehicleListAdapter(context!!, R.layout.vehicle_services_item, this, "", exoticListItems)
            ExoticVehiclesListView?.adapter = exoticAdapter
            ExoticVehiclesListView?.isExpanded = true
            ExoticVehiclesListView?.isVisible = true
        }
        if (otherListItems.count() > 0) {
            otherAdapter = VehicleListAdapter(context!!, R.layout.vehicle_services_item, this, "", otherListItems)
            OtherVehiclesListView?.adapter = otherAdapter
            OtherVehiclesListView?.isExpanded = true
            OtherVehiclesListView?.isVisible = true
        }

        expandablell.visibility = View.VISIBLE
//
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
