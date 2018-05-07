package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.model.AAAFacilityPrograms
import com.inspection.model.AAAProgramTypes
import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import kotlinx.android.synthetic.main.tablerow5cols.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.*
import com.inspection.Utils.Constants.appFormat
import com.inspection.Utils.Constants.dbFormat
import com.inspection.Utils.toast
import com.inspection.singletons.AnnualVisitationSingleton


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
    private var facilityProgramsList = ArrayList<AAAFacilityPrograms>()


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

        addBtn.setOnClickListener({
            var validProgram = true
            for (fac in facilityProgramsList) {
                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
                    context!!.toast("Program Name cannot be duplicated")
                    validProgram=false
                }
            }
            if (validProgram) {
                var item = AAAFacilityPrograms()
                item.programid = -1
                item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.effdate = if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString()
                item.expdate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString()
                item.comments=comments_editTextVal.text.toString()
                facilityProgramsList.add(facilityProgramsList.size, item)
                BuildProgramsList()
            }
        })

        deleteBtn.setOnClickListener({
            var itemFound =false
            var item = AAAFacilityPrograms()
            for (fac in facilityProgramsList) {
                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
                    item = fac
                    itemFound=true
                }
            }
            if (itemFound) {
                facilityProgramsList.remove(item)
                BuildProgramsList()
            }
        })

        editBtn.setOnClickListener({
            for (fac in facilityProgramsList) {
                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
                    fac.effdate = if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString()
                    fac.expdate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString()
                    fac.comments = comments_editTextVal.text.toString()
                }
                BuildProgramsList()
            }
        })
        prepareProgramTypes()
    }


    fun prepareProgramTypes () {

            progressbarPrograms.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.programTypesURL,
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
                activity!!.toast("Connection Error. Please check the internet connection")
            }))

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPrograms+AnnualVisitationSingleton.getInstance().facilityId,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            facilityProgramsList= Gson().fromJson(response.toString(), Array<AAAFacilityPrograms>::class.java).toCollection(ArrayList())
//                            drawProgramsTable()
                            BuildProgramsList()
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading facility programs")
                context!!.toast("Error loading Facility Program. Please check your internet connectivity")
            }))

            progressbarPrograms.visibility = View.INVISIBLE
    }


    fun drawProgramsTable () {
        programsTableLayout.removeAllViews()
        var separatorView = View (context)
        separatorView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1)
        separatorView.setBackgroundColor(Color.rgb(51, 51, 51))
        var tableRow = LayoutInflater.from(context).inflate(R.layout.tablerow5cols, null, false)
        tableRow.programid.setText("ID")
        tableRow.programname.setText("Current Programs")
        tableRow.effdate.setText("Effective Date")
        programsTableLayout.addView(tableRow)
        programsTableLayout.addView(separatorView)
        for (fac in facilityProgramsList) {
            tableRow = LayoutInflater.from(context).inflate(R.layout.tablerow5cols, null, false)
            tableRow.effdate.setText(fac.effdate)
            tableRow.programid.setText(fac.programid.toString())
            tableRow.effdate.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL")) "No Date Provided" else {
                if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
            }
            tableRow.programname.setText(fac.programtypename)
            tableRow.setOnClickListener({
                program_name_textviewVal.setSelection(programTypesArray.indexOf(fac.programtypename))
                effective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.equals("No Date Provided")) "No Date Provided" else  {
                    if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
                }
                expiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.equals("No Date Provided")) "No Date Provided" else   {
                    if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
                }
                comments_editTextVal.setText(fac.comments)
            })
            programsTableLayout.addView(tableRow)
            var rowseparatorView = View (context)
            rowseparatorView .layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1)
            rowseparatorView .setBackgroundColor(Color.rgb(51, 51, 51))
            programsTableLayout.addView(rowseparatorView )
        }
    }

    fun BuildProgramsList() {
        val inflater = activity!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentLayout = programsListLL
        parentLayout.removeAllViews()
        for (fac in facilityProgramsList) {
            val vProgramRow = inflater.inflate(R.layout.custom_program_list_item, parentLayout, false)
            val prId= vProgramRow.findViewById(R.id.programItemId) as TextView
            val prName= vProgramRow.findViewById(R.id.programItemName) as TextView
            val prEffDate= vProgramRow.findViewById(R.id.programItemEffDate) as TextView
            val prExpDate= vProgramRow.findViewById(R.id.programItemExpDate) as TextView
            prId.text = fac.programid.toString()
            prName.text = fac.programtypename
            prEffDate.text = if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
            prExpDate.text = if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
            vProgramRow.setOnClickListener({
                program_name_textviewVal.setSelection(programTypesArray.indexOf(fac.programtypename))
                effective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.equals("No Date Provided")) "No Date Provided" else  {
                    if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
                }
                expiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.equals("No Date Provided")) "No Date Provided" else   {
                    if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
                }
                comments_editTextVal.setText(fac.comments)
            })
            parentLayout.addView(vProgramRow)
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
