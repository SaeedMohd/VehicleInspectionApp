package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.inspection.R
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.interfaces.VehicleServicesListItem
import com.inspection.model.VehicleServiceHeader
import com.inspection.model.VehicleServiceItem

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




        var vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehicleServicesListItems)

        vehicleServicesListView.adapter = vehiclesArrayAdapter

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
