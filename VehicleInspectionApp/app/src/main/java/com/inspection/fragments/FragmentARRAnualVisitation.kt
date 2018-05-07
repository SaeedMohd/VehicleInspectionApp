package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


import com.inspection.MainActivity
import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import com.google.gson.Gson
import com.inspection.Utils.*
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import java.text.SimpleDateFormat
import java.util.*


class FragmentARRAnualVisitation : android.support.v4.app.Fragment() {

    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_aar_manual_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facilityNameInputField = facilityNameEditText
        facilityIdEditText.visibility = View.GONE
        facilityIdTextView.visibility = View.GONE
        facilityNameListView.visibility = View.GONE

        if (!FacilityDataModel.getInstance().tblFacilities.get(0).BusinessName.isNullOrEmpty()) {
            facilityNameEditText.isEnabled = false
            facilityNameEditText.setText(FacilityDataModel.getInstance().tblFacilities.get(0).BusinessName)
        }

        facilityNameEditText.onFocusChangeListener = View.OnFocusChangeListener({ view: View, b: Boolean ->
            Log.v("********** focus is", "Focus is: " + b)
            itemSelected = !b
        })
        generalInformationTextView.setOnClickListener({
            validateInputs()
        })

//        if ((activity as MainActivity).FacilityName != null && (activity as MainActivity).FacilityName.isNotEmpty()){
//            facilityNameEditText.setText((activity as MainActivity).FacilityName)
//            itemSelected = true
//            (activity as MainActivity).isLoadNewDetailsRequired = true
//        }

        if (FacilityDataModel.getInstance().annualVisitationId == -1) {
            facilityNameEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (!itemSelected && facilityNameEditText.text.length >= 1) {
//                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "http://jet-matics.com/JetComService/JetCom.svc/getAAAFacilityFormDetails?facilityName="+facilityNameEditText.text,
                        Log.v("Facility URL:  --> ", Constants.getfacilitiesURL + facilityNameEditText.text)
                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getfacilitiesURL + facilityNameEditText.text,
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        //                                val jObject = JSONObject(response.toString())
//                                val facResult = jObject.getJSONArray("getAAAFacilityDetailsResult")
//                                facilitiesList = Gson().fromJson(facResult.toString() , Array<AAAFacility>::class.java).toCollection(ArrayList())
                                        facilitiesList = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
                                        facilityNames.clear()
                                        for (fac in facilitiesList) {
                                            facilityNames.add(fac.businessname)
                                        }

                                        facilityNameListView.visibility = View.VISIBLE
                                        facilityNameListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, facilityNames)
                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading")
                        }))


                    }
                }

            })
        }

        // Inspection Type
        var inspectionTypes = arrayOf("Quarterly", "Ad Hoc")
        var dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionTypes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inspectionTypeSpinner.adapter = dataAdapter

        // Changes Made
        var changesMadeArray = arrayOf("Yes", "No")
        var chgdataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, changesMadeArray)
        chgdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changesMadeSwitch.adapter = chgdataAdapter

        var monthsArray = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        var monthdataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, monthsArray)
        monthdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthDueSpinner.adapter = monthdataAdapter

        dateOfVisitationButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                dateOfVisitationButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }





        facilityNameListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(facilityNameEditText.getWindowToken(), 0)
            itemSelected = true
            (activity as MainActivity).isLoadNewDetailsRequired = true
            facilityNameEditText.setText(facilityNames.get(i).toString())
            (activity as MainActivity).FacilityName = facilityNames.get(i).toString()
            (activity as MainActivity).facilitySelected = facilitiesList.filter { s -> s.businessname == facilityNames.get(i) }.get(0)
            (activity as MainActivity).FacilityNumber = (activity as MainActivity).facilitySelected.facid.toString()
            facilityNameListView.visibility = View.GONE
            facilityNameListView.adapter = null
            facilityNameEditText?.setError(null)
            facilityRepresentativeNameEditText?.setError(null)
            automotiveSpecialistEditText?.setError(null)

//            loadLastInspection()
            setFieldsValues()
        })


        if (FacilityDataModel.getInstance().annualVisitationId > -1) {
            facilityNameInputField!!.setText(FacilityDataModel.getInstance().tblFacilities.get(0).BusinessName)
            facilityNameInputField!!.isEnabled = false

        }else{

        }

        if (arguments!!.getBoolean(isValidating)) {
            validateInputs()
        }

        setFieldsValues()
    }

    private fun setFieldsValues() {
        automotiveSpecialistEditText.setText(FacilityDataModel.getInstance().tblFacilities[0].AutomotiveSpecialist)
        facilityRepresentativeNameEditText.setText(FacilityDataModel.getInstance().tblFacilities.get(0).AdminAssistants)
        inspectionTypeSpinner.setSelection(0)
        monthDueSpinner.setSelection(FacilityDataModel.getInstance().tblFacilities.get(0).BillingMonth -1)
        changesMadeSwitch.setSelection(1)
        dateOfVisitationButton.text = Date(AnnualVisitationSingleton.getInstance().dateOfVisitation!!).toAppFormat()
    }

//    private fun loadLastInspection() {
//        progressbarGeneralInformation.visibility = View.VISIBLE
//        (activity as MainActivity).window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getLastInspectionForFacility + (activity as MainActivity).facilitySelected.facid,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//                        try {
//                            var lastInspection = Gson().fromJson(response.toString(), Array<AnnualVisitationInspectionFormData>::class.java).toCollection(ArrayList()).get(0)
//                            automotiveSpecialistEditText.setText(lastInspection!!.automotivespecialistname)
//                            facilityRepresentativeNameEditText.setText(lastInspection!!.facilityrepresentativename)
//                            inspectionTypeSpinner.setSelection(lastInspection!!.inspectiontypeid - 1)
//                            monthDueSpinner.setSelection(lastInspection!!.monthdue - 1)
//
//                            changesMadeSwitch.setSelection(if (lastInspection.changesmade) 1 else 0)
//                            dateOfVisitationButton.text = lastInspection.dateofinspection.dbToAppFormat()
//
//                            FacilityDataModel.getInstance().apply {
//                                automotiveSpecialist = lastInspection.automotivespecialistname
////                                = lastInspection.automotivespecialistsignatureid TODO add signature logic
//                                facilityRepresentative = lastInspection.facilityrepresentativename
////                                = lastInspection.facilityrepresentativesignatureid TODO add signature logic
//                                inspectionType = lastInspection.inspectiontypeid
//                                monthDue = lastInspection.monthdue
//                                changesMade = lastInspection.changesmade
//                                dateOfVisitation = lastInspection.dateofinspection.toTime()
//                                paymentMethods = lastInspection.paymentmethods
//                                emailModel = AAAEmailModel()
//                                emailModel!!.emailid = lastInspection.emailaddressid
//                                phoneModel = AAAPhoneModel()
//                                phoneModel!!.phoneid = lastInspection.phonenumberid
//                                personnelId = lastInspection.personnelid
//                                vehicleServices = lastInspection.vehicleservices
//                                vehicles = lastInspection.vehicles
//                                programs = lastInspection.programs
//                                facilityServices = lastInspection.facilityservices
//                                affliations = lastInspection.affiliations
//                                defeciencies = lastInspection.defeciencies
//                                complaints = lastInspection.complaints
//                            }
//
//                            setFieldsValues()
//
//                        } catch (exp: Exception) {
//
//                        }
//                        progressbarGeneralInformation.visibility = View.INVISIBLE
//                        (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                    })
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading")
//            Log.v("Loading error", "" + it.message)
//            progressbarGeneralInformation.visibility = View.INVISIBLE
//            (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        }))
//    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    fun validateInputs(): Boolean {
//        FacilityDataModel.getInstance().apply {
//
//            if (facilityName.isNullOrBlank()) {
//                facilityNameEditText.error = ""
//            }
//
//            if (facilityRepresentative.isNullOrBlank()) {
//                facilityRepresentativeNameEditText.error = ""
//            }
//
//            if (automotiveSpecialist.isNullOrBlank()) {
//                automotiveSpecialistEditText.error = ""
//            }
//
//            if (dateOfVisitation == -1L) {
//                dateOfVisitationButton.error = "Mandatory"
//            }
//
//        }
        return false
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FragmentAnnualVisitationPager.
         */
        // TODO: Rename and change types and number of parameters

        fun newInstance(param1: Boolean): FragmentARRAnualVisitation {
            val fragment = FragmentARRAnualVisitation()
            val args = Bundle()
            args.putBoolean(isValidating, param1)
            fragment.arguments = args
            return fragment
        }
    }
}