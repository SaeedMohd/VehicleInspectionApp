package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.R.id.vehicleServicesListView
import com.inspection.Utils.Consts
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.interfaces.VehicleServicesListItem
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*
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

    private var isServicesLoaded = false

    var vehicleServicesListView: ListView? = null
    var vehicleServicesListItems = ArrayList<VehicleServicesListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var vehicleServicesList: ArrayList<AAAVehicleServicesModel>
    lateinit var vehiclesArrayAdapter: VehicleServicesArrayAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_array_vehicle_services, container, false)
        vehicleServicesListView = view.findViewById<ListView>(R.id.vehicleServicesListView)
        loadServices()

        return view
    }

    private fun loadServices() {
        if (progressbarVehicleServices!=null) {
            progressbarVehicleServices.visibility = View.VISIBLE
        }
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getVehicleServicesURL,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        isServicesLoaded = true
                        vehicleServicesList = Gson().fromJson(response.toString(), Array<AAAVehicleServicesModel>::class.java).toCollection(ArrayList())
                        vehicleServicesListItems.add(VehicleServiceHeader("Automobile"))
                        (0..vehicleServicesList.size - 1).forEach {
                            vehicleServicesListItems.add(VehicleServiceItem(vehicleServicesList[it]))
                            vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehicleServicesListItems)
                            vehicleServicesListView!!.adapter = vehiclesArrayAdapter
                        }
                        if (progressbarVehicleServices!=null) {
                            progressbarVehicleServices.visibility = View.INVISIBLE
                        }
                        if (isPreparingView) {
                            loadServices()
                        }
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading personnel Types")
            Toast.makeText(activity, "Connection Error. Please check the internet connection", Toast.LENGTH_LONG).show()
        }))
    }

    private var isFirstRun = true

    private var isPreparingView = false

    fun prepareView() {
        if (!isServicesLoaded) {
            isPreparingView = true
            loadServices()
        }
        if (!(activity as MainActivity).FacilityNumber.isNullOrEmpty() && isFirstRun) {
            isFirstRun = false

            if ((activity as MainActivity).lastInspection != null) {
                (0..(activity as MainActivity).lastInspection!!.vehicleservices.split(",").size - 1)
                        .forEach {
                            (1 until vehicleServicesListItems.size)
                                    .forEach { it2 ->
                                        if (it2 != 0) {
                                            Log.v("8888888", "" + (activity as MainActivity).lastInspection!!.vehicleservices.split(",")[it].toInt() + "  " + "" + (vehicleServicesListItems[it2] as VehicleServiceItem).vehicleServiceModel.scopeserviceid + "->")
                                            if ((activity as MainActivity).lastInspection!!.vehicleservices.split(",")[it].toInt() == (vehicleServicesListItems[it2] as VehicleServiceItem).vehicleServiceModel.scopeserviceid) {
                                                Log.v("yesss", "yes i am selecteddddddd")
                                                (vehicleServicesListItems[it2] as VehicleServiceItem).setServiceSelected(true)
                                                vehiclesArrayAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                        }
            }


        }
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
