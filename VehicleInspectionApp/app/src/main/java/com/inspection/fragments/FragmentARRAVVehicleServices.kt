package com.inspection.fragments

import android.app.AlertDialog
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
import com.inspection.R
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.adapter.DatesListAdapter
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VehicleServiceItem
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*
import java.util.ArrayList

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

    //changed from listview to gridview > sherif yousry
    var vehicleServicesListView: ExpandableHeightGridView? = null
    var autoBodyServicesListView: ExpandableHeightGridView? = null
    var MarineServicesListView: ExpandableHeightGridView? = null
    var RecreationalServicesListView: ExpandableHeightGridView? = null
        internal var arrayAdapter: DatesListAdapter? = null
    internal var arrayAdapter2: DatesListAdapter? = null
    internal var arrayAdapter3: DatesListAdapter? = null
    internal var arrayAdapter4: DatesListAdapter? = null
        var vehicleServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var autoBodyServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var marineServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()
    var recreationalServicesListItems=ArrayList<TypeTablesModel.scopeofServiceTypeByVehicleType>()


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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (progressbarVehicleServices != null) {
//            progressbarVehicleServices.visibility = View.VISIBLE
//        }
        scopeOfServiceChangesWatcher()
            setServices()


    }



    private fun setServices() {


        // commented out for testing to adjust gridview> sherif yousry
      //  vehicleServicesListItems.add(VehicleServiceHeader("Automobile"))

        for (model in TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType) {

            for (model2 in TypeTablesModel.getInstance().VehiclesType){

                if (model.VehiclesTypeID==model2.VehiclesTypeID){

                    if (model2.VehiclesTypeName.toString().contains("Autom")) {

                        vehicleServicesListItems.add(model)
                    }
                    if (model2.VehiclesTypeName.toString().contains("Body")) {

                        autoBodyServicesListItems.add(model)
                    }
                    if (model2.VehiclesTypeName.toString().contains("Marin")) {

                        marineServicesListItems.add(model)
                    }
                    if (model2.VehiclesTypeName.toString().contains("RV")) {

                        recreationalServicesListItems.add(model)
                    }


                                    }
            }

            }






            arrayAdapter = DatesListAdapter(context!!, R.layout.vehicle_services_item, vehicleServicesListItems)

            vehicleServicesListView?.adapter = arrayAdapter
        vehicleServicesListView?.isExpanded=true

            arrayAdapter2 = DatesListAdapter(context!!, R.layout.vehicle_services_item, autoBodyServicesListItems)

        autoBodyServicesListView?.adapter = arrayAdapter2
        autoBodyServicesListView?.isExpanded=true


            arrayAdapter3 = DatesListAdapter(context!!, R.layout.vehicle_services_item, marineServicesListItems)

        MarineServicesListView?.adapter = arrayAdapter3
        MarineServicesListView?.isExpanded=true


            arrayAdapter4 = DatesListAdapter(context!!, R.layout.vehicle_services_item, recreationalServicesListItems)

        RecreationalServicesListView?.adapter = arrayAdapter4
        RecreationalServicesListView?.isExpanded=true


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

        if (FragmentARRAVScopeOfService.dataChanged) {

            val builder = AlertDialog.Builder(context)

            // Set the alert dialog title
            builder.setTitle("Changes made confirmation")

            // Display a message on alert dialog
            builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES") { dialog, which ->


                scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE


                Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE", response.toString())
                                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                                Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                    FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                        LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                        LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank())LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                        FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank())FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                        DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank())DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                        NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank())NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                        NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank())NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                        FragmentARRAVScopeOfService.dataChanged =false

                                    }

                                }

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()

                    scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                }))


            }





            // Display a negative button on alert dialog
            builder.setNegativeButton("No") { dialog, which ->
                FragmentARRAVScopeOfService.dataChanged =false
                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

            }




            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            // Display the alert dialog on app interface
            dialog.show()

        }

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
