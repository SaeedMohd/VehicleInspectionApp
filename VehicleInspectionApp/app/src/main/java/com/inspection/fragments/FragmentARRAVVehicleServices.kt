package com.inspection.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.Utils.Utility
import com.inspection.Utils.toApiSubmitFormat
import com.inspection.adapter.DatesListAdapter
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import java.util.*

//import kotlinx.android.synthetic.main.temp.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVVehicleServices.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVVehicleServices.newInstance] factory method to
 * create an instance of this fragment.
 */

class FragmentARRAVVehicleServices : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    private var isServicesLoaded = false

    var selectedVehicleServices = "-1"
    var selectedAutoBodyServices = "-1"
    var selectedMarineServices = "-1"
    var selectedRecreationServices = "-1"
    var selectedAutoGlassServices = "-1"
    var selectedOthersServices = "-1"

    var vehicleServicesListView: ExpandableHeightGridView? = null
    var autoBodyServicesListView: ExpandableHeightGridView? = null
    var MarineServicesListView: ExpandableHeightGridView? = null
    var RecreationalServicesListView: ExpandableHeightGridView? = null
    var AutoGlassServicesListView: ExpandableHeightGridView? = null
    var OtherServicesListView: ExpandableHeightGridView? = null

    internal var arrayAdapter: DatesListAdapter? = null
    internal var arrayAdapter2: DatesListAdapter? = null
    internal var arrayAdapter3: DatesListAdapter? = null
    internal var arrayAdapter4: DatesListAdapter? = null
    internal var arrayAdapter5: DatesListAdapter? = null
    internal var arrayAdapter6: DatesListAdapter? = null
    var vehicleServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var autoBodyServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var marineServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var recreationalServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var autoGlassServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var otherServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var vehiclesArrayAdapter: VehicleServicesArrayAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_array_vehicle_services, container, false)
        vehicleServicesListView = view.findViewById(R.id.vehicleServicesListView)
        autoBodyServicesListView = view.findViewById(R.id.languagesGridView)
        MarineServicesListView = view.findViewById(R.id.MarineServicesListView)
        RecreationalServicesListView = view.findViewById(R.id.RecreationalServicesListView)
//        AutoGlassServicesListView = view.findViewById(R.id.AutoGlassServicesListView)
//        OtherServicesListView = view.findViewById(R.id.OtherServicesListView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (progressbarVehicleServices != null) {
//            progressbarVehicleServices.visibility = View.VISIBLE
//        }
//        scopeOfServiceChangesWatcher()
            setServices()
        refreshButtonsState()

        saveButton.setOnClickListener {
            progressBarText.text = "Saving ..."
            scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
            if (!selectedVehicleServices.equals("-1")) saveVehicleServiceChanges("0")
            if (!selectedAutoBodyServices.equals("-1")) saveVehicleServiceChanges("1")
            if (!selectedMarineServices.equals("-1")) saveVehicleServiceChanges("2")
            if (!selectedRecreationServices.equals("-1")) saveVehicleServiceChanges("3")
            if (!selectedAutoGlassServices.equals("-1")) saveVehicleServiceChanges("4")
            if (!selectedOthersServices.equals("-1")) saveVehicleServiceChanges("5")
        }
    }

    fun saveVehicleServiceChanges(gridType: String) {
        var vehiclesTypeId=""
        var scopeServiceId=""
        if (gridType.equals("0")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("Autom") }[0].VehiclesTypeID
            scopeServiceId = selectedVehicleServices
        } else if (gridType.equals("1")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("Body") }[0].VehiclesTypeID
            scopeServiceId = selectedAutoBodyServices
        }else if (gridType.equals("2")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("Marin") }[0].VehiclesTypeID
            scopeServiceId ==selectedMarineServices
        }else if (gridType.equals("3")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("RV") }[0].VehiclesTypeID
            scopeServiceId = selectedRecreationServices
        }else if (gridType.equals("4")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("Auto Glass") }[0].VehiclesTypeID
            scopeServiceId = selectedAutoGlassServices
        }else if (gridType.equals("5")) {
            vehiclesTypeId = TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeName.contains("Other") }[0].VehiclesTypeID
            scopeServiceId = selectedOthersServices
        }

        scopeServiceId.removeSuffix(",")

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVehicleServices+ FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubcode=${FacilityDataModel.getInstance().clubCode}&vehiclesTypeId=${vehiclesTypeId}&scopeServiceId=${scopeServiceId}&insertBy=e107369",
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode&gt;0&",false)) {
                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                            progressBarText.text = "Loading ..."
                            Utility.showSubmitAlertDialog(activity, true, "Vehicle Services")
                            (activity as FormsActivity).saveRequired = false
                            HasChangedModel.getInstance().checkIfChangeWasDoneforSoSGeneral()
                            HasChangedModel.getInstance().changeDoneForSoSGeneral()
                            IndicatorsDataModel.getInstance().validateSoSGeneral()
                            if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfo) (activity as FormsActivity).vehicleServicesButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#A42600"))
                            (activity as FormsActivity).refreshMenuIndicators()
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                            Utility.showSubmitAlertDialog(activity, false, "Vehicle Services (Error: "+ errorMessage+" )")
                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                            progressBarText.text = "Loading ..."
                        }
                    }
                }, Response.ErrorListener {
            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
            progressBarText.text = "Loading ..."
            Utility.showSubmitAlertDialog(activity,false,"Vehicle Services (Error: "+it.message+" )")
        }))
    }
    fun refreshButtonsState(){
        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }

    private fun setServices() {


        // commented out for testing to adjust gridview> sherif yousry
      //  vehicleServicesListItems.add(VehicleServiceHeader("Automobile"))

        for (model in TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType) {

            for (model2 in TypeTablesModel.getInstance().VehiclesType){

                if (model.VehiclesTypeID==model2.VehiclesTypeID){

                    if (model2.VehiclesTypeName.toString().contains("Autom") ) {
                        if (vehicleServicesListItems.filter { s->s.ScopeServiceID.equals(model.ScopeServiceID) }.size==0) {
                            vehicleServicesListItems.add(model)
                            selectedVehicleServices = ""
                        }
                    }
                    if (model2.VehiclesTypeName.toString().contains("Body")) {
                        autoBodyServicesListItems.add(model)
                        selectedAutoBodyServices=""
                    }
                    if (model2.VehiclesTypeName.toString().contains("Marin")) {

                        marineServicesListItems.add(model)
                        selectedMarineServices=""
                    }
                    if (model2.VehiclesTypeName.toString().contains("RV")) {

                        recreationalServicesListItems.add(model)
                        selectedRecreationServices=""
                    }
                    if (model2.VehiclesTypeName.toString().contains("Auto Glass")) {

                        autoGlassServicesListItems.add(model)
                        selectedAutoGlassServices=""
                    }

                    if (model2.VehiclesTypeName.toString().contains("Other")) {

                        otherServicesListItems.add(model)
                        selectedOthersServices=""
                    }


                                    }
            }

            }






        arrayAdapter = DatesListAdapter(context!!, R.layout.vehicle_services_item,this,"Autom", vehicleServicesListItems)

        vehicleServicesListView?.adapter = arrayAdapter
        vehicleServicesListView?.isExpanded=true

        arrayAdapter2 = DatesListAdapter(context!!, R.layout.vehicle_services_item, this,"Body",autoBodyServicesListItems)

        autoBodyServicesListView?.adapter = arrayAdapter2
        autoBodyServicesListView?.isExpanded=true


        arrayAdapter3 = DatesListAdapter(context!!, R.layout.vehicle_services_item,this, "Marin",marineServicesListItems)

        MarineServicesListView?.adapter = arrayAdapter3
        MarineServicesListView?.isExpanded=true


        arrayAdapter4 = DatesListAdapter(context!!, R.layout.vehicle_services_item,this, "RV",recreationalServicesListItems)

        RecreationalServicesListView?.adapter = arrayAdapter4
        RecreationalServicesListView?.isExpanded=true

        arrayAdapter5 = DatesListAdapter(context!!, R.layout.vehicle_services_item,this,"Auto Glass", autoGlassServicesListItems)

        AutoGlassServicesListView?.adapter = arrayAdapter5
        AutoGlassServicesListView?.isExpanded=true

        arrayAdapter6 = DatesListAdapter(context!!, R.layout.vehicle_services_item, this,"Other",otherServicesListItems)

        OtherServicesListView?.adapter = arrayAdapter6
        OtherServicesListView?.isExpanded=true


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
//        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {
//
//            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {
//
//
//                if (FragmentARRAVScopeOfService.dataChanged) {
//
//                    val builder = AlertDialog.Builder(context)
//
//                    // Set the alert dialog title
//                    builder.setTitle("Changes made confirmation")
//
//                    // Display a message on alert dialog
//                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")
//
//                    // Set a positive button and its click listener on alert dialog
//                    builder.setPositiveButton("YES") { dialog, which ->
//
//
//                        scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
//
//
//                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread(Runnable {
//                                        Log.v("RESPONSE", response.toString())
//                                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
//                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
//                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {
//
//                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
//                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
//                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
//                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
//                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
//                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_
//
//                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare
//
//                                                FragmentARRAVScopeOfService.dataChanged = false
//
//                                            }
//
//                                        }
//
//                                    })
//                                }, Response.ErrorListener {
//                            Log.v("error while loading", "error while loading personnal record")
//                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
//
//                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//                        }))
//
//
//                    }
//
//
//                    // Display a negative button on alert dialog
//                    builder.setNegativeButton("No") { dialog, which ->
//                        FragmentARRAVScopeOfService.dataChanged = false
//                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//                    }
//
//
//                    // Finally, make the alert dialog using builder
//                    val dialog: AlertDialog = builder.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    // Display the alert dialog on app interface
//                    dialog.show()
//
//                }
//
//            } else {
//
//
//                val builder = AlertDialog.Builder(context)
//
//                // Set the alert dialog title
//                builder.setTitle("Changes made Warning")
//
//                // Display a message on alert dialog
//                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")
//
//                // Set a positive button and its click listener on alert dialog
//                builder.setPositiveButton("Ok") { dialog, which ->
//
//                    FragmentARRAVScopeOfService.dataChanged = false
//                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true
//
//
//                }
//
//
//                val dialog: AlertDialog = builder.create()
//                dialog.setCanceledOnTouchOutside(false)
//                dialog.show()
//
//            }
//        }
    }


    fun prepareView() {

        (0 until AnnualVisitationSingleton.getInstance().vehicleServices.split(",").size)
                .forEach {
                    (1 until vehicleServicesListItems.size)
                            .forEach { it2 ->
                                try {
                                    if (it2 != 0) {
                                        if (AnnualVisitationSingleton.getInstance().vehicleServices.split(",")[it].toInt() == (vehicleServicesListItems[it2] as VehicleServiceItem).vehicleServiceModel.scopeserviceid) {
                                            (vehicleServicesListItems[it2] as VehicleServiceItem).setServiceSelected(true)
                                            vehiclesArrayAdapter.notifyDataSetChanged()
                                        }
                                    }
                                } catch (exp: Exception) {

                                }
                            }
                }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        fun newInstance(param1: String, param2: String): FragmentARRAVVehicleServices {
            val fragment = FragmentARRAVVehicleServices()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
