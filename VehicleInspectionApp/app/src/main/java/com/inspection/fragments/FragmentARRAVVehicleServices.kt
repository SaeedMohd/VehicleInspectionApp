package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ListView

import com.inspection.R
import com.inspection.R.id.vehicleServicesListView
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.interfaces.VehicleServicesListItem
import com.inspection.model.VehicleServiceHeader
import com.inspection.model.VehicleServiceItem
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.temp.view.*
import kotlinx.android.synthetic.main.vehicle_services_item.view.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_array_vehicle_services, container, false)

        var vehicleServicesListView = view.findViewById<ListView>(R.id.vehicleServicesListView)

        var vehicleServicesListItems = ArrayList<VehicleServicesListItem>()
        vehicleServicesListItems.add(VehicleServiceHeader("Automobile"))
        vehicleServicesListItems.add(VehicleServiceItem("Air Check New Mexico"))
        vehicleServicesListItems.add(VehicleServiceItem("Air Check Texas"))
        vehicleServicesListItems.add(VehicleServiceItem("Air Conditioning Service and Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Automatic Transmission"))
        vehicleServicesListItems.add(VehicleServiceItem("Brake Certification"))
        vehicleServicesListItems.add(VehicleServiceItem("Brake Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Clutch / Driveline"))
        vehicleServicesListItems.add(VehicleServiceItem("Cooling/Radiator"))
        vehicleServicesListItems.add(VehicleServiceItem("Diagnostic Services"))
        vehicleServicesListItems.add(VehicleServiceItem("Diesel Engine Service and Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Diesel Vehicle Service"))
        vehicleServicesListItems.add(VehicleServiceItem("Electrical Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("Engine Performance"))
        vehicleServicesListItems.add(VehicleServiceItem("Hybrid Service & Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Lamp Certification"))
        vehicleServicesListItems.add(VehicleServiceItem("Lube/Oil/Filter Service"))
        vehicleServicesListItems.add(VehicleServiceItem("Major Engine Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Manual Transmission/Rear Axle"))
        vehicleServicesListItems.add(VehicleServiceItem("Minor Engine Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Muffler/Exhaust"))
        vehicleServicesListItems.add(VehicleServiceItem("RV Service & Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Smog Repair Only"))
        vehicleServicesListItems.add(VehicleServiceItem("Smog Test 2000 & Newer"))
        vehicleServicesListItems.add(VehicleServiceItem("Smog Test and Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Smog-Diesel"))
        vehicleServicesListItems.add(VehicleServiceItem("STAR Test and Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("State Emissions Inspection"))
        vehicleServicesListItems.add(VehicleServiceItem("State Safety Inspection"))
        vehicleServicesListItems.add(VehicleServiceItem("Steering/Suspension"))
        vehicleServicesListItems.add(VehicleServiceItem("Tire Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Tire Sales"))

        vehicleServicesListItems.add(VehicleServiceHeader("Auto Body"))
        vehicleServicesListItems.add(VehicleServiceItem("Damage Analysis/Estimating"))
        vehicleServicesListItems.add(VehicleServiceItem("Frame/Unibody (Aluminum)"))
        vehicleServicesListItems.add(VehicleServiceItem("Motorcycle Collision Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Paintless Dent Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Body Panel Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Detailing"))
        vehicleServicesListItems.add(VehicleServiceItem("Auto Glass"))
        vehicleServicesListItems.add(VehicleServiceItem("Painting/Refinishing"))
        vehicleServicesListItems.add(VehicleServiceItem("Rustproofing/Undercoating"))
        vehicleServicesListItems.add(VehicleServiceItem("Upholstery"))
        vehicleServicesListItems.add(VehicleServiceItem("Frame/Unibody (Steel)"))
        vehicleServicesListItems.add(VehicleServiceItem("Marine (Boat) Collision Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Plastic/Fiberglass Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("RV/Trailer Collision Repair"))
        vehicleServicesListItems.add(VehicleServiceItem("Welding / Brazing"))

        vehicleServicesListItems.add(VehicleServiceHeader("Marine"))
        vehicleServicesListItems.add(VehicleServiceItem("Air Conditioning (electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Refrigeration (gas/electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Septic Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("Inboard Engines"))
        vehicleServicesListItems.add(VehicleServiceItem("Outboard Engines (4-stroke)"))
        vehicleServicesListItems.add(VehicleServiceItem("Electrical (120-volt ac)"))
        vehicleServicesListItems.add(VehicleServiceItem("Furnaces (gas/electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Water Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("I/O Drive Units"))
        vehicleServicesListItems.add(VehicleServiceItem("V-Drive Units"))
        vehicleServicesListItems.add(VehicleServiceItem("Electrical (12-volt dc)"))
        vehicleServicesListItems.add(VehicleServiceItem("Septic Dump Station"))
        vehicleServicesListItems.add(VehicleServiceItem("Bilge Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("Outboard Engines (2-stroke)"))

        vehicleServicesListItems.add(VehicleServiceHeader("Recreational Vehicle"))
        vehicleServicesListItems.add(VehicleServiceItem("Air Conditioning (electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Electrical (120-volt ac)"))
        vehicleServicesListItems.add(VehicleServiceItem("Furnaces (gas/electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Water Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("Brakes (hydraulic-surge)"))
        vehicleServicesListItems.add(VehicleServiceItem("Electrical (12-volt dc)"))
        vehicleServicesListItems.add(VehicleServiceItem("Hitch Systems"))
        vehicleServicesListItems.add(VehicleServiceItem("Brakes (electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Refrigeration (gas/electric)"))
        vehicleServicesListItems.add(VehicleServiceItem("Septic Systems"))

        var vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehicleServicesListItems)

        vehicleServicesListView.adapter = vehiclesArrayAdapter


//        vehicleServicesListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
//
//        })


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
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
