package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityContinued.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityContinued.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityContinued : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

//    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravfacility_continued, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fill Dop Down

        var opHoursArray = arrayOf("Closed","00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30"
                                           ,"07:00","07:30","08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00"
                                           ,"13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00"
                                           ,"20:30","21:00","21:30","22:00","22:30","23:00","23:30")
        var opHoursAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, opHoursArray)
        opHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        saturday_closed_spinner.adapter = opHoursAdapter
        saturday_open_spinner.adapter = opHoursAdapter
        sunday_closed_spinner.adapter = opHoursAdapter
        sunday_open_spinner.adapter = opHoursAdapter
        monday_closed_spinner.adapter = opHoursAdapter
        monday_open_spinner.adapter = opHoursAdapter
        tuesday_closed_spinner.adapter = opHoursAdapter
        tuesday_open_spinner.adapter = opHoursAdapter
        wednesday_closed_spinner.adapter = opHoursAdapter
        wednesday_open_spinner.adapter = opHoursAdapter
        thursday_closed_spinner.adapter = opHoursAdapter
        thursday_open_spinner.adapter = opHoursAdapter
        friday_closed_spinner.adapter = opHoursAdapter
        friday_open_spinner.adapter = opHoursAdapter

        var emailTypeArray = arrayOf("Business", "Personnel")
        var emailTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, emailTypeArray)
        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailtype_textviewVal.adapter = emailTypeAdapter

        var phoneTypeArray = arrayOf("Business", "Cell", "Fax", "Home")
        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phonetype_textviewVal.adapter = phoneTypeAdapter

    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
//        if (mListener != null) {
//            mListener!!.onFragmentInteraction(uri)
//        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
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
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

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
         * @return A new instance of fragment FragmentARRAVFacilityContinued.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVFacilityContinued {
            val fragment = FragmentARRAVFacilityContinued()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
