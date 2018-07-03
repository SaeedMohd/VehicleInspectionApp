package com.inspection.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inspection.R
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.adapter.DatesListAdapter
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.model.TypeTablesModel
import com.inspection.model.VehicleServiceItem
import com.inspection.singletons.AnnualVisitationSingleton
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
