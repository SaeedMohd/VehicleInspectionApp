package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.database.DatabaseErrorHandler
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arrav_deficiency.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVDeficiency.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVDeficiency.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVDeficiency : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_deficiency , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var deffArray= arrayOf("AAR Portal", "Advertising", "Appearance", "ASE Certification", "Battery Service Program", "Billing", "Customer Facilities", "Customer Satisfaction", "Excessive Complaints", "Fraudulent ASE Certificate", "Gross incompetence" ,"Insurance Certificate Licenses", "Priority Service", "Quality Control", "Scope of Service", "Show Your Card and Save", "Signs and Certificates", "Staff Training", "Termination", "Tools and Equipment", "Vehicle Health Alerts", "Warranty")
        var deffAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, deffArray)
        deffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deff_dropdown.adapter = deffAdapter

        visitationDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                visitationDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        clearedDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                clearedDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        DateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                DateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }


    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        clearedDateBtn.setError(null)
        visitationDateBtn.setError(null)
        DateBtn.setError(null)


        if(clearedDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            clearedDateBtn.setError("Required Field")
        }

        if(visitationDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            visitationDateBtn.setError("Required Field")
        }

        if(DateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            DateBtn.setError("Required Field")
        }

        return isInputsValid
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
        fun newInstance(param1: String, param2: String): FragmentARRAVDeficiency {
            val fragment = FragmentARRAVDeficiency()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
