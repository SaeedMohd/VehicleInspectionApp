package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.R.id.progressBar
import com.inspection.Utils.Consts
import com.inspection.Utils.toast
import com.inspection.model.AAAFacility
import com.inspection.model.AAAPersonnelDetails
import com.inspection.model.AAAPersonnelType
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVPersonnel.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPersonnel : Fragment() {


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var personnelTypeList = ArrayList<AAAPersonnelType>()
    private var personnelDetailsList = ArrayList<AAAPersonnelDetails>()
    private var personTypeArray = ArrayList<String>()
    private var personListArray = ArrayList<String>()
    private var statesArray = ArrayList<String>()
    private var firstSelection = false // Variable used as the first item in the personnelType drop down is selected by default when the ata is loaded
    //    private val strFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFprmat = SimpleDateFormat("dd MMM yyyy")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater!!.inflate(R.layout.fragment_arravpersonnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statesArray = arrayOf("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming").toCollection(ArrayList<String>())

        var statesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, statesArray)
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coSignerStateVal.adapter = statesAdapter

        endDateVal.setOnClickListener {
            if (endDateVal.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    endDateVal!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
            }
        }

        a1CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a1CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a2CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a2CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a3CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a3CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a4CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a4CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a5CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a5CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a6CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a6CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a7CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a7CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a8CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a8CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        c1CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                c1CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        a1ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a1ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a2ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a2ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a3ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a3ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a4ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a4ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a5ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a5ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a6ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a6ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a7ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a7ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a8ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                a8ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        c1ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                c1ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        preparePersonnelPage()

    }

    var isFirstRun: Boolean = true

    fun preparePersonnelPage() {
            isFirstRun = false
            progressbarPersonnel.visibility = View.VISIBLE
            activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.personnelTypeURL + AnnualVisitationSingleton.getInstance().facilityId,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            personnelTypeList = Gson().fromJson(response.toString(), Array<AAAPersonnelType>::class.java).toCollection(ArrayList())
                            personTypeArray.clear()
                            personTypeArray.add("Not Selected")
                            for (fac in personnelTypeList) {
                                personTypeArray.add(fac.personneltypename)
                            }
                            var personTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personTypeArray)
                            personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            personType_textviewVal.adapter = personTypeAdapter
                        })
                        progressbarPersonnel.visibility = View.INVISIBLE
                        if (AnnualVisitationSingleton.getInstance().personnelId > -1) {
                            getLastYearPersonnel()
                        }
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading personnel Types")
                activity!!.toast("Connection Error. Please check the internet connection")
            }))


        var personnelNamesListViewAdapter = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            personnelNamesList.visibility = View.GONE
            setPersonnelDetails(personnelDetailsList.get(i))
        })

        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        personType_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                personnelNamesList.visibility = View.GONE
                if (position > 0) {
                    progressbarPersonnel.visibility = View.VISIBLE
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Consts.personnelDetailsURL, AnnualVisitationSingleton.getInstance().facilityId, getTypeID(personType_textviewVal.selectedItem.toString())),
                            Response.Listener { response ->
                                activity!!.runOnUiThread(Runnable {
                                    Log.v("*****Response....", response)
                                    personnelDetailsList = Gson().fromJson(response.toString(), Array<AAAPersonnelDetails>::class.java).toCollection(ArrayList())
                                    Log.v("*****Response....", "Count is: " + personnelDetailsList.size)
                                    if (personnelDetailsList.size >= 1) {
                                        personListArray.clear()
//                                        if (personnelDetailsList.size == 1) personListArray.add("Add New")
                                        for (perDetails in personnelDetailsList) {
                                            personListArray.add(perDetails.firstname + " " + perDetails.lastname)
                                        }
                                        var personDtlsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personListArray)
                                        personDtlsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        personnelNamesList.visibility = View.VISIBLE
                                        personnelNamesList.adapter = personDtlsAdapter
                                        personnelNamesList.itemsCanFocus = true
                                        personnelNamesList.onItemClickListener = personnelNamesListViewAdapter
                                        if (AnnualVisitationSingleton.getInstance().personnelId > -1) {
                                            (0..personnelDetailsList.size - 1)
                                                    .filter { personnelDetailsList.get(it).personnelid == personnelDetails.personnelid }
                                                    .forEach {
                                                        setPersonnelDetails(personnelDetailsList.get(it))
                                                    }
                                        }
                                    }
                                })
                                progressbarPersonnel.visibility = View.INVISIBLE
                            }, Response.ErrorListener {
                        Log.v("error while loading", "error while loading personnel Types")
                        activity!!.toast("Connection Error. Please check the internet connection")
                    }))

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                /*Do something if nothing selected*/


            }
        }


    }

    private lateinit var personnelDetails: AAAPersonnelDetails

    private var isUsingLastInspectionPersonnel: Boolean = false

    private fun getLastYearPersonnel() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.personnelDetailsWithIdUrl + AnnualVisitationSingleton.getInstance().personnelId,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        personnelDetails = Gson().fromJson(response.toString(), Array<AAAPersonnelDetails>::class.java).toCollection(ArrayList()).get(0)
                        isUsingLastInspectionPersonnel = true
                        (0..personnelTypeList.size - 1)
                                .filter { personnelTypeList.get(it).personneltypeid == personnelDetails.personneltypeid }
                                .forEach { personType_textviewVal.setSelection(it + 1) }

                    })

                    progressbarPersonnel.visibility = View.INVISIBLE
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading personnel Types")
            activity!!.toast("Connection Error. Please check the internet connection")
        }))
    }

    private fun setPersonnelDetails(personnelDetails: AAAPersonnelDetails) {
        personnelNamesList.visibility = View.GONE
        personnelNamesList.adapter = null
        firstName_textviewVal.setText(personnelDetails.firstname)
        lastName_textviewVal.setText(personnelDetails.lastname)
        certNo_textviewVal.setText(personnelDetails.certificationnum)
        rspUserID_textviewVal.text = if ((personnelDetails.rsp_username).equals("NULL")) "" else (personnelDetails.rsp_username)
        rspEmail_textviewVal.text = if ((personnelDetails.rsp_email).equals("NULL")) "" else (personnelDetails.rsp_email)
        certNo_textviewVal.setText(personnelDetails.certificationnum)
        seniorityDateVal.text = if ((personnelDetails.senioritydate).equals("NULL") || (personnelDetails.senioritydate).length < 10) "" else (personnelDetails.senioritydate)
        var dateTobeFormated = ""
        if (!((personnelDetails.startdate).isNullOrEmpty())) {
            dateTobeFormated = appFprmat.format(dbFormat.parse(personnelDetails.startdate.substring(0, 10)))
        }
        startDateVal.setText(dateTobeFormated)
        Log.v("FORMAT 1 ----- : ", dateTobeFormated)
        dateTobeFormated = ""
        if ( personnelDetails.startdate != null &&  !(personnelDetails.startdate).equals("NULL") && personnelDetails.enddate != null && (personnelDetails.enddate).equals("NULL")) {
            dateTobeFormated = "SELECT DATE"
        } else {
//            dateTobeFormated = appFprmat.format(dbFormat.parse(personnelDetails.enddate.substring(0, 10)))
        }
        endDateVal.setText(dateTobeFormated)
        primaryEmailCheckBox.isChecked = (personnelDetails.primarymailrecipient == 1)
        if (personnelDetails.contractsigner == 1) {
            try {
                contractSignerCheckBox.isChecked = true
                coSignerAddr1Val.setText(personnelDetails.addr1)
                coSignerAddr2Val.setText(personnelDetails.addr2)
                coSignerCityVal.setText(personnelDetails.city)
                coSignerCoEndDateVal.text = personnelDetails.contractenddate
                coSignerCoStartDateVal.text = personnelDetails.contractstartdate
                coSignerEmailVal.setText(personnelDetails.email)
                coSignerPhoneVal.setText(personnelDetails.phone.toString())
                coSignerStateVal.setSelection(statesArray.indexOf(personnelDetails.state.toString()))
                coSignerZip4Val.setText(personnelDetails.zip4.toString())
                coSignerZipVal.setText(personnelDetails.zip.toString())
            } catch (exp: Exception) {
                exp.printStackTrace()
            }
        }
    }

    fun getTypeID(typeName: String): Int {
        var typeID = -1
        for (fac in personnelTypeList) {
            if (fac.personneltypename.equals(typeName)) {
                typeID = fac.personneltypeid
            }
        }
        return typeID
    }

    fun validateInputs(): Boolean {
        var isInputsValid = true

        firstName_textviewVal.setError(null)
        lastName_textviewVal.setError(null)
        coSignerAddr1Val.setError(null)
        coSignerAddr2Val.setError(null)
        coSignerCityVal.setError(null)
        coSignerZip4Val.setError(null)
        coSignerZipVal.setError(null)
        coSignerEmailVal.setError(null)
        coSignerPhoneVal.setError(null)
        coSignerCoEndDateVal.setError(null)
        coSignerCoStartDateVal.setError(null)
        a1CertDateVal.setError(null)
        a1ExpDateVal.setError(null)
        a2CertDateVal.setError(null)
        a2ExpDateVal.setError(null)
        a3CertDateVal.setError(null)
        a3ExpDateVal.setError(null)
        a4CertDateVal.setError(null)
        a4ExpDateVal.setError(null)
        a5CertDateVal.setError(null)
        a5ExpDateVal.setError(null)
        a6CertDateVal.setError(null)
        a6ExpDateVal.setError(null)
        a7CertDateVal.setError(null)
        a7ExpDateVal.setError(null)
        a8CertDateVal.setError(null)
        a8ExpDateVal.setError(null)
        c1CertDateVal.setError(null)
        c1ExpDateVal.setError(null)

        if (firstName_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            firstName_textviewVal.setError("Required Field")
        }

        if (lastName_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            lastName_textviewVal.setError("Required Field")
        }

        if (contractSignerCheckBox.isChecked) {
            if (coSignerAddr1Val.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerAddr1Val.setError("Required Field")
            }
            if (coSignerAddr2Val.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerAddr2Val.setError("Required Field")
            }
            if (coSignerCityVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerCityVal.setError("Required Field")
            }
            if (coSignerZipVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerZipVal.setError("Required Field")
            }
            if (coSignerZip4Val.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerZip4Val.setError("Required Field")
            }
            if (coSignerPhoneVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerPhoneVal.setError("Required Field")
            }
            if (coSignerEmailVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerEmailVal.setError("Required Field")
            }
            if (coSignerCoStartDateVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerCoStartDateVal.setError("Required Field")
            }
            if (coSignerCoEndDateVal.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                coSignerCoEndDateVal.setError("Required Field")
            }
        }
        if (!a1CertDateVal.text.equals("SELECT DATE")) {
            if (a1ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a1ExpDateVal.setError("Required Field")
            }
        }

        if (!a2CertDateVal.text.equals("SELECT DATE")) {
            if (a2ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a2ExpDateVal.setError("Required Field")
            }
        }

        if (!a3CertDateVal.text.equals("SELECT DATE")) {
            if (a3ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a3ExpDateVal.setError("Required Field")
            }
        }

        if (!a4CertDateVal.text.equals("SELECT DATE")) {
            if (a4ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a4ExpDateVal.setError("Required Field")
            }
        }

        if (!a5CertDateVal.text.equals("SELECT DATE")) {
            if (a5ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a5ExpDateVal.setError("Required Field")
            }
        }

        if (!a6CertDateVal.text.equals("SELECT DATE")) {
            if (a6ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a6ExpDateVal.setError("Required Field")
            }
        }

        if (!a7CertDateVal.text.equals("SELECT DATE")) {
            if (a7ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a7ExpDateVal.setError("Required Field")
            }
        }

        if (!a8CertDateVal.text.equals("SELECT DATE")) {
            if (a8ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                a8ExpDateVal.setError("Required Field")
            }
        }

        if (!c1CertDateVal.text.equals("SELECT DATE")) {
            if (c1ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid = false
                c1ExpDateVal.setError("Required Field")
            }
        }


        return isInputsValid
    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1..
         * @return A new instance of fragment FragmentARRAVPersonnel.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVPersonnel {
            val fragment = FragmentARRAVPersonnel()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
