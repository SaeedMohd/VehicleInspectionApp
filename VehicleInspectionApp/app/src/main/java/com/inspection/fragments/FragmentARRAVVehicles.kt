package com.inspection.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.ExpandableHeightGridView
import com.inspection.Utils.toast
import com.inspection.adapter.VehicleServicesArrayAdapter
import com.inspection.interfaces.VehicleServicesListItem
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arravvehicles.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVVehicles.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVVehicles.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVVehicles : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    private var vehiclesListView: ListView? = null
    private var vehiclesListItems = ArrayList<VehicleServicesListItem>()
    private var vehiclesList = ArrayList<AAAVehiclesModel>()

    var vehiclesArrayAdapter: VehicleServicesArrayAdapter? = null

    var vehicleMakeListView: ExpandableHeightGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        var view = inflater!!.inflate(R.layout.fragment_arravvehicles, container, false)
        vehiclesListView = view.findViewById<ListView>(R.id.vehiclesListView)


        prepareView()

        vehicleMakeListView = vehicleMakeList



        var vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehiclesListItems)

        vehiclesListView!!.adapter = vehiclesArrayAdapter

        return view
        //<VehicleMakesType> : CHevrolet - Buick
        //<VehicleMakes> Vehicle ID-MakeID-TypeID-CatID
        //<VehiclesMakesCategoryType>: Asian - Domestic
        //<VehiclesType>Automobile - Marine

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited = true
        (activity as FormsActivity).vehiclesButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

    }

    private fun loadVehicles() {
        if (progressbarVehicles != null) {
            progressbarVehicles.visibility = View.VISIBLE
        }
        Log.v("Vehicles --- ",Constants.getVehiclesURL+"")
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getVehiclesURL,
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        isVehiclesLoaded = true
                        (activity as FormsActivity).saveDone = true
                        vehiclesList = Gson().fromJson(response.toString(), Array<AAAVehiclesModel>::class.java).toCollection(ArrayList())
                        vehiclesListItems.add(VehicleServiceHeader("Automobile"))
                        (0..vehiclesList.size - 1).forEach {
                            vehiclesListItems.add(VehicleItem(vehiclesList[it]))
                            vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehiclesListItems)
                            vehiclesListView!!.adapter = vehiclesArrayAdapter
                        }
                        if (progressbarVehicles != null) {
                            progressbarVehicles.visibility = View.INVISIBLE
                        }
                        if (isPreparingView) {
                            prepareView()
                        }
                    }
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading personnel Types")
        }))
    }

    var isFirstRun = true

    private var isPreparingView = false

    private var isVehiclesLoaded = false

    fun prepareView() {
        if (!isVehiclesLoaded) {
            isPreparingView = true
            loadVehicles()
        }
        isFirstRun = false


            (0..AnnualVisitationSingleton.getInstance().vehicles.split(",").size - 1)
                    .forEach {
                        (1 until vehiclesListItems.size)
                                .forEach { it2 ->
                                    if (it2 != 0) {
                                        if (AnnualVisitationSingleton.getInstance().vehicles.split(",")[it].toInt()
                                                == (vehiclesListItems[it2] as VehicleItem).vehicleModel.vehmaketypeid) {
                                            (vehiclesListItems[it2] as VehicleItem).setVehicleSelected(true)
                                            vehiclesArrayAdapter!!.notifyDataSetChanged()
                                        }
                                    }
                                }
                    }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//    }

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
         * @return A new instance of fragment FragmentARRAVVehicles.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVVehicles {
            val fragment = FragmentARRAVVehicles()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
