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
import com.inspection.Utils.Consts
import com.inspection.model.AAAFacilityComplete
import com.inspection.model.AnnualVisitationInspectionFormData
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentARRAnualVisitation : android.support.v4.app.Fragment() {

    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null

    private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        (activity as MainActivity).supportActionBar!!.title = "Forms"

        return inflater.inflate(R.layout.fragment_aar_manual_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facilityNameInputField = facilityNameEditText
        facilityIdEditText.visibility = View.GONE
        facilityIdTextView.visibility = View.GONE
        facilityNameListView.visibility = View.GONE

        facilityNameEditText.onFocusChangeListener = View.OnFocusChangeListener({ view: View, b: Boolean ->
            Log.v("********** focus is", "Focus is: "+b)
            itemSelected = !b
        })
        generalInformationTextView.setOnClickListener({
            validateInputs()
        })

        if ((activity as MainActivity).FacilityName != null && (activity as MainActivity).FacilityName.isNotEmpty()){
            facilityNameEditText.setText((activity as MainActivity).FacilityName)
            itemSelected = true
            (activity as MainActivity).isLoadNewDetailsRequired = true
        }

        facilityNameEditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!itemSelected && facilityNameEditText.text.length >= 1){
//                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "http://jet-matics.com/JetComService/JetCom.svc/getAAAFacilityFormDetails?facilityName="+facilityNameEditText.text,
                    Log.v("Facility URL:  --> " , Consts.getfacilitiesURL+facilityNameEditText.text)
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getfacilitiesURL+facilityNameEditText.text,
                            Response.Listener { response ->
                                activity!!.runOnUiThread(Runnable {
//                                val jObject = JSONObject(response.toString())
//                                val facResult = jObject.getJSONArray("getAAAFacilityDetailsResult")
//                                facilitiesList = Gson().fromJson(facResult.toString() , Array<AAAFacility>::class.java).toCollection(ArrayList())
                                facilitiesList = Gson().fromJson(response.toString() , Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
                                facilityNames.clear()
                                for (fac in facilitiesList){
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

        // Inspection Type
        var inspectionTypes = arrayOf("Quarterly","Ad Hoc")
        var dataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, inspectionTypes)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inspectionTypeSpinner.adapter = dataAdapter

        // Changes Made
        var changesMadeArray= arrayOf("Yes","No")
        var chgdataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, changesMadeArray)
        chgdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changesMadeSwitch.adapter = chgdataAdapter

        var monthsArray= arrayOf("January","February","March","April","May","June","July","August","September","October","November","December")
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
                c.set(year,monthOfYear,dayOfMonth)
                dateOfVisitationButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }





        facilityNameListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(facilityNameEditText.getWindowToken(), 0)
            itemSelected = true
//            (parentFragment as? FragmentAnnualVisitationPager)!!.flagLoadNewDetailsRequired()
//            (activity as MainActivity).fragment!!.fragment!!.flagLoadNewDetailsRequired()
            (activity as MainActivity).isLoadNewDetailsRequired = true

            facilityNameEditText.setText(facilityNames.get(i).toString())
//            facilityAddressEditText.requestFocus()
            // Facility Name will be needed in other forms
            (activity as MainActivity).FacilityName = facilityNames.get(i).toString()
            (activity as MainActivity).facilitySelected = facilitiesList.filter { s -> s.businessname == facilityNames.get(i) }.get(0)
            // Facility Number will be needed in other forms
            (activity as MainActivity).FacilityNumber = (activity as MainActivity).facilitySelected.facid.toString()
//            automotiveSpecialistEditText.setText(facilitySelected.)
//            facilityRepresentativeNameEditText.setText(if (facilitySelected.ow == " ")  "" else facilitySelected.ownerName)
//            facilityAddressEditText.setText((activity as MainActivity).facilitySelected.fac_addr1)
//            facilityCityEditText.setText((activity as MainActivity).facilitySelected.city)
//            facilityStateEditText.setText((activity as MainActivity).facilitySelected.state)
////            facilityPhoneEditText.setText((activity as MainActivity).facilitySelected.p)
////            facilityEmailEditText.setText(facilitySelected.email)
//            facilityZipEditText.setText((activity as MainActivity).facilitySelected.zip)
//            automotiveSpecialistEmailEditText.setText(facilitySelected.specialistEmail)
            facilityNameListView.visibility = View.GONE
            facilityNameListView.adapter = null
            facilityNameEditText?.setError(null)
            facilityRepresentativeNameEditText?.setError(null)
            automotiveSpecialistEditText?.setError(null)

            loadLastInspection()

        })

        loadLastInspection()
    }

    fun loadLastInspection() {
        progressbarGeneralInformation.visibility = View.VISIBLE
        (activity as MainActivity).window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getLastInspectionForFacility+(activity as MainActivity).facilitySelected.facid,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        try {
                            (activity as MainActivity).lastInspection = Gson().fromJson(response.toString(), Array<AnnualVisitationInspectionFormData>::class.java).toCollection(ArrayList()).get(0)
                            automotiveSpecialistEditText.setText((activity as MainActivity).lastInspection!!.automotivespecialistname)
                            facilityRepresentativeNameEditText.setText((activity as MainActivity).lastInspection!!.facilityrepresentativename)
                            inspectionTypeSpinner.setSelection((activity as MainActivity).lastInspection!!.inspectiontypeid-1)
                            monthDueSpinner.setSelection((activity as MainActivity).lastInspection!!.monthdue-1)

                            if ((activity as MainActivity).lastInspection!!.changesmade){
                                changesMadeSwitch.setSelection(0)
                            }else{
                                changesMadeSwitch.setSelection(1)
                            }
                            changesMadeSwitch.selectedItem
                            dateOfVisitationButton.text = appFormat.format(dbFormat.parse( (activity as MainActivity).lastInspection!!.dateofinspection))

                        }catch (exp: Exception){

                        }
                        progressbarGeneralInformation.visibility = View.INVISIBLE
                        (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading")
            progressbarGeneralInformation.visibility = View.INVISIBLE
            (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    public fun validateInputs() : Boolean {
        var isInputsValid = true
        facilityNameEditText?.setError(null)
        facilityRepresentativeNameEditText?.setError(null)
        automotiveSpecialistEditText?.setError(null)
        dateOfVisitationButton?.setError(null)
        if (facilityNameInputField?.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            facilityNameInputField?.setError("Required Field")
        }

        if (inspectionTypeSpinner?.selectedItem.toString().isNullOrEmpty()) {
            isInputsValid=false
        }

        if (facilityRepresentativeNameEditText?.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            facilityRepresentativeNameEditText?.setError("Required Field")
        }

        if (automotiveSpecialistEditText?.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            automotiveSpecialistEditText?.setError("Required Field")
        }

        if (dateOfVisitationButton?.text.toString().equals("SELECT DATE")) {
            isInputsValid=false
            dateOfVisitationButton?.setError("Required Field")
        }

        return isInputsValid
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
         * @return A new instance of fragment FragmentAnnualVisitationPager.
         */
        // TODO: Rename and change types and number of parameters

        fun newInstance(param1: String, param2: String): FragmentARRAnualVisitation {
            val fragment = FragmentARRAnualVisitation ()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}