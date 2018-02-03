package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.model.AAAPersonnelType
import com.inspection.model.AAAProgramTypes
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVPrograms.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVPrograms.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPrograms : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var programTypesArray = ArrayList<String>()
    private var programTypesList = ArrayList<AAAProgramTypes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        effective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                effective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        expiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                expiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

    }


    fun prepareProgramTypes () {
        if (!(activity as MainActivity).FacilityNumber.isNullOrEmpty()) {
            progressbarPrograms.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.programTypesURL,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            programTypesList= Gson().fromJson(response.toString(), Array<AAAProgramTypes>::class.java).toCollection(ArrayList())
                            programTypesArray.clear()
                            for (fac in programTypesList) {
                                programTypesArray.add(fac.programtypename)
                            }
                            var programsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, programTypesArray)
                            programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            program_name_textviewVal.adapter = programsAdapter
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading program Types")
                Toast.makeText(activity,"Connection Error. Please check the internet connection", Toast.LENGTH_LONG).show()
            }))
            progressbarPrograms.visibility = View.INVISIBLE
        }
    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        effective_date_textviewVal.setError(null)
        comments_editTextVal.setError(null)


        if(effective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            effective_date_textviewVal.setError("Required Field")
        }

        if(comments_editTextVal.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            comments_editTextVal.setError("Required Field")
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
        fun newInstance(param1: String, param2: String): FragmentARRAVPrograms {
            val fragment = FragmentARRAVPrograms()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
