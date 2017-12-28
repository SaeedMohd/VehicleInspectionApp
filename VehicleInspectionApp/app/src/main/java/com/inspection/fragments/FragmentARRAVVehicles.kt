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
import kotlinx.android.synthetic.main.fragment_array_vehicle_services.*

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

        var vehiclesListView = view.findViewById<ListView>(R.id.vehiclesListView)


        var vehiclesListItems = ArrayList<VehicleServicesListItem>()

        vehiclesListItems.add(VehicleServiceHeader("Domestic"))
        vehiclesListItems.add(VehicleServiceItem("AMC"))
        vehiclesListItems.add(VehicleServiceItem("Buick"))
        vehiclesListItems.add(VehicleServiceItem("Geo"))
        vehiclesListItems.add(VehicleServiceItem("Chrysler"))
        vehiclesListItems.add(VehicleServiceItem("Cadillac"))
        vehiclesListItems.add(VehicleServiceItem("Nash"))
        vehiclesListItems.add(VehicleServiceItem("Dodge"))
        vehiclesListItems.add(VehicleServiceItem("Chevrolet"))
        vehiclesListItems.add(VehicleServiceItem("Jeep"))
        vehiclesListItems.add(VehicleServiceItem("GMC"))
        vehiclesListItems.add(VehicleServiceItem("Ford"))
        vehiclesListItems.add(VehicleServiceItem("Oldsmobile"))
        vehiclesListItems.add(VehicleServiceItem("Lincoln"))
        vehiclesListItems.add(VehicleServiceItem("Pontiac"))
        vehiclesListItems.add(VehicleServiceItem("Mercury"))
        vehiclesListItems.add(VehicleServiceItem("Saturn"))

        vehiclesListItems.add(VehicleServiceHeader("Asian"))
        vehiclesListItems.add(VehicleServiceItem("Acura"))
        vehiclesListItems.add(VehicleServiceItem("Lexus"))
        vehiclesListItems.add(VehicleServiceItem("Daewoo"))
        vehiclesListItems.add(VehicleServiceItem("Mazda"))
        vehiclesListItems.add(VehicleServiceItem("Datsun"))
        vehiclesListItems.add(VehicleServiceItem("Mitsubishi"))
        vehiclesListItems.add(VehicleServiceItem("Honda"))
        vehiclesListItems.add(VehicleServiceItem("Nissan"))
        vehiclesListItems.add(VehicleServiceItem("Hyundai"))
        vehiclesListItems.add(VehicleServiceItem("Scion"))
        vehiclesListItems.add(VehicleServiceItem("Infiniti"))
        vehiclesListItems.add(VehicleServiceItem("Subaru"))
        vehiclesListItems.add(VehicleServiceItem("Isuzu"))
        vehiclesListItems.add(VehicleServiceItem("Suzuki"))
        vehiclesListItems.add(VehicleServiceItem("Kia"))
        vehiclesListItems.add(VehicleServiceItem("Toyota"))

        vehiclesListItems.add(VehicleServiceHeader("European"))
        vehiclesListItems.add(VehicleServiceItem("Alfa Romeo"))
        vehiclesListItems.add(VehicleServiceItem("Peugeot"))
        vehiclesListItems.add(VehicleServiceItem("Audi"))
        vehiclesListItems.add(VehicleServiceItem("Porsche"))
        vehiclesListItems.add(VehicleServiceItem("BMW"))
        vehiclesListItems.add(VehicleServiceItem("Renault"))
        vehiclesListItems.add(VehicleServiceItem("Fiat"))
        vehiclesListItems.add(VehicleServiceItem("Saab"))
        vehiclesListItems.add(VehicleServiceItem("Jaguar"))
        vehiclesListItems.add(VehicleServiceItem("Volkswagen"))
        vehiclesListItems.add(VehicleServiceItem("Land Rover"))
        vehiclesListItems.add(VehicleServiceItem("Volvo"))
        vehiclesListItems.add(VehicleServiceItem("Mercedes-Benz"))
        vehiclesListItems.add(VehicleServiceItem("Smart"))
        vehiclesListItems.add(VehicleServiceItem("Mini"))
        vehiclesListItems.add(VehicleServiceItem("Yugo"))

        vehiclesListItems.add(VehicleServiceHeader("Exotic"))
        vehiclesListItems.add(VehicleServiceItem("Aston Martin"))
        vehiclesListItems.add(VehicleServiceItem("Maserati"))
        vehiclesListItems.add(VehicleServiceItem("DeLorean"))
        vehiclesListItems.add(VehicleServiceItem("Bentley"))
        vehiclesListItems.add(VehicleServiceItem("Maybach"))
        vehiclesListItems.add(VehicleServiceItem("MG"))
        vehiclesListItems.add(VehicleServiceItem("Bugatti"))
        vehiclesListItems.add(VehicleServiceItem("McLaren"))
        vehiclesListItems.add(VehicleServiceItem("Shelby"))
        vehiclesListItems.add(VehicleServiceItem("DeTomaso"))
        vehiclesListItems.add(VehicleServiceItem("Rolls-Royce"))
        vehiclesListItems.add(VehicleServiceItem("Bertone"))
        vehiclesListItems.add(VehicleServiceItem("Ferrari"))
        vehiclesListItems.add(VehicleServiceItem("Saleen"))
        vehiclesListItems.add(VehicleServiceItem("Eagle"))
        vehiclesListItems.add(VehicleServiceItem("Ford GT"))
        vehiclesListItems.add(VehicleServiceItem("Daihatsu"))
        vehiclesListItems.add(VehicleServiceItem("Panoz"))
        vehiclesListItems.add(VehicleServiceItem("Lamborghini"))
        vehiclesListItems.add(VehicleServiceItem("International"))
        vehiclesListItems.add(VehicleServiceItem("Triumph"))
        vehiclesListItems.add(VehicleServiceItem("Lotus"))
        vehiclesListItems.add(VehicleServiceItem("Merkur"))

        vehiclesListItems.add(VehicleServiceHeader("Other Types"))
        vehiclesListItems.add(VehicleServiceItem("Hybrid"))
        vehiclesListItems.add(VehicleServiceItem("Diesel Vehicles"))
        vehiclesListItems.add(VehicleServiceItem("Electric Vehicles"))
        vehiclesListItems.add(VehicleServiceItem("Hydrogen Vehicles"))
        vehiclesListItems.add(VehicleServiceItem("CNG Vehicles"))
        vehiclesListItems.add(VehicleServiceItem("Propane Vehicle"))
        vehiclesListItems.add(VehicleServiceItem("RV"))

        var vehiclesArrayAdapter = VehicleServicesArrayAdapter(context, vehiclesListItems)

        vehiclesListView.adapter = vehiclesArrayAdapter

        return  view

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
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
