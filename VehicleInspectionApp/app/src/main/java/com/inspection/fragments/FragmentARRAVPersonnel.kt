package com.inspection.fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.inspection.MainActivity.Companion.activity

import com.inspection.R
import com.inspection.R.id.*
import com.inspection.Utils.apiToAppFormat
import com.inspection.model.AAAPersonnelDetails
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
import kotlinx.android.synthetic.main.fragment_visitation_form.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVPersonnel.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPersonnel : Fragment() {


    // TODO: Rename and change types of parameters
    var emailValid=true
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var personnelTypeList = ArrayList<TypeTablesModel.personnelType>()
    private var certificationTypeList= ArrayList<TypeTablesModel.personnelCertificationType>()
    private var states= arrayOf("select city","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming")

    private var personTypeArray = ArrayList<String>()
    private var certTypeArray = ArrayList<String>()
    private var personTypeIDsArray = ArrayList<String>()
    private var personListArray = ArrayList<String>()
    private var statesArray = ArrayList<String>()
    private var firstSelection = false // Variable used as the first item in the personnelType drop down is selected by default when the ata is loaded
    //    private val strFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        return inflater!!.inflate(R.layout.fragment_arravpersonnel, container, false)
        return inflater!!.inflate(R.layout.fragment_aarav_personnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preparePersonnelPage()
        fillPersonnelTableView()


        newCertStartDateBtn.setOnClickListener {
//            if (newCertStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCertStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }






        newCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCoStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newSeniorityDateBtn.setOnClickListener {
//            if (newSeniorityDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newSeniorityDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newStartDateBtn.setOnClickListener {
//            if (newStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }


        fillData()



    }

    var isFirstRun: Boolean = true

    fun preparePersonnelPage() {
        isFirstRun = false
//        progressbarPersonnel.visibility = View.VISIBLE
//        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//                        personnelTypeList = Gson().fromJson(TypeTablesModel.getInstance().PersonnelType.toString(), Array<AAAPersonnelType>::class.java).toCollection(ArrayList())
        personnelTypeList=TypeTablesModel.getInstance().PersonnelType
        personTypeArray.clear()
        personTypeIDsArray.clear()
        personTypeIDsArray.add("-1")
        personTypeArray.add("Not Selected")
        for (fac in personnelTypeList) {
                personTypeArray.add(fac.PersonnelTypeName)
                personTypeIDsArray.add(fac.PersonnelTypeID)
        }
         var personTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personTypeArray)
        personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPersonnelTypeSpinner.adapter = personTypeAdapter

        certificationTypeList=TypeTablesModel.getInstance().PersonnelCertificationType
        certTypeArray.clear()
        certTypeArray.add("Not Selected")
        for (fac in certificationTypeList) {
            certTypeArray.add(fac.PersonnelCertName)
//            cert.add(fac.PersonnelCertID)
        }
        var certTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, certTypeArray)
        certTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newCertTypeSpinner.adapter = certTypeAdapter
//                    progressbarPersonnel.visibility = View.INVISIBLE
                    if (AnnualVisitationSingleton.getInstance().personnelId > -1) {
//                            getLastYearPersonnel()
                    }



//        var personnelNamesListViewAdapter = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
////            personnelNamesList.visibility = View.GONE
////            setPersonnelDetails(FacilityDataModel.getInstance().tblPersonnel.get(i))
//        })

//        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

//        personType_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                personnelNamesList.visibility = View.GONE
//                if (position > 0) {
////                    progressbarPersonnel.visibility = View.VISIBLE
////                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Constants.personnelDetailsURL, AnnualVisitationSingleton.getInstance().facilityId, getTypeID(personType_textviewVal.selectedItem.toString())),
////                            Response.Listener { response ->
////                                activity!!.runOnUiThread(Runnable {
////                                    Log.v("*****Response....", response)
////                                    personnelDetailsList = Gson().fromJson(response.toString(), Array<AAAPersonnelDetails>::class.java).toCollection(ArrayList())
////                                    Log.v("*****Response....", "Count is: " + personnelDetailsList.size)
////                                    if (personnelDetailsList.size >= 1) {
////                                        personListArray.clear()
//////                                        if (personnelDetailsList.size == 1) personListArray.add("Add New")
////                                        for (perDetails in personnelDetailsList) {
////                                            personListArray.add(perDetails.firstname + " " + perDetails.lastname)
////                                        }
////                                        var personDtlsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personListArray)
////                                        personDtlsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                                        personnelNamesList.visibility = View.VISIBLE
////                                        personnelNamesList.adapter = personDtlsAdapter
////                                        personnelNamesList.itemsCanFocus = true
////                                        personnelNamesList.onItemClickListener = personnelNamesListViewAdapter
////                                        if (AnnualVisitationSingleton.getInstance().personnelId > -1) {
////                                            (0..personnelDetailsList.size - 1)
////                                                    .filter { personnelDetailsList.get(it).personnelid == personnelDetails.personnelid }
////                                                    .forEach {
////                                                        setPersonnelDetails(personnelDetailsList.get(it))
////                                                    }
////                                        }
////                                    }
////                                })
////                                progressbarPersonnel.visibility = View.INVISIBLE
////                            }, Response.ErrorListener {
////                        Log.v("error while loading", "error while loading personnel Types")
////                        activity!!.toast("Connection Error. Please check the internet connection")
////                    }))
//
//                    if (FacilityDataModel.getInstance().tblPersonnel.size >= 1) {
//                        personListArray.clear()
////                                        if (personnelDetailsList.size == 1) personListArray.add("Add New")
//                        for (perDetails in FacilityDataModel.getInstance().tblPersonnel) {
//                            if (perDetails.PersonnelTypeID.toInt() == personTypeIDsArray.get(position)) {
//                                personListArray.add(perDetails.FirstName + " " + perDetails.LastName)
//                            }
//                        }
//                        var personDtlsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personListArray)
//                        personDtlsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        personnelNamesList.visibility = View.VISIBLE
//                        personnelNamesList.adapter = personDtlsAdapter
//                        personnelNamesList.itemsCanFocus = true
//                        personnelNamesList.onItemClickListener = personnelNamesListViewAdapter
////                                        if (AnnualVisitationSingleton.getInstance().personnelId > -1) {
////                                            context!!.toast(AnnualVisitationSingleton.getInstance().personnelFirstName +" "
////                                                    + AnnualVisitationSingleton.getInstance().personnelLastName)
////                                            (0 until FacilityDataModel.getInstance().tblPersonnel.size)
////                                                    .filter {
////                                                        (FacilityDataModel.getInstance().tblPersonnel.get(it).FirstName + " " + FacilityDataModel.getInstance().tblPersonnel.get(it).LastName) == (AnnualVisitationSingleton.getInstance().personnelFirstName +" "
////                                                                + AnnualVisitationSingleton.getInstance().personnelLastName)
////                                                    }
////                                                    .forEach {
////                                                        context!!.toast("Found it")
////                                                        setPersonnelDetails(FacilityDataModel.getInstance().tblPersonnel.get(it))
////                                                    }
////                                        }
//                    }
//
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//                /*Do something if nothing selected*/
//
//
//            }
//        }

        var citiesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, states)
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newStateSpinner.adapter = citiesAdapter

    }

    private lateinit var personnelDetails: AAAPersonnelDetails

    private var isUsingLastInspectionPersonnel: Boolean = false

//    private fun getLastYearPersonnel() {
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.personnelDetailsWithIdUrl + AnnualVisitationSingleton.getInstance().personnelId,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//                        personnelDetails = Gson().fromJson(response.toString(), Array<AAAPersonnelDetails>::class.java).toCollection(ArrayList()).get(0)
//                        isUsingLastInspectionPersonnel = true
//                        (0 until personnelTypeList.size)
//                                .filter { personnelTypeList.get(it).personneltypeid == personnelDetails.personneltypeid }
//                                .forEach { personType_textviewVal.setSelection(it + 1) }
//                    })
//
//                    progressbarPersonnel.visibility = View.INVISIBLE
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading personnel Types")
//            activity!!.toast("Connection Error. Please check the internet connection")
//        }))
//    }

//    private fun setPersonnelDetails(personnelDetails: FacilityDataModel.TblPersonnel) {
//        personnelNamesList.visibility = View.GONE
//        personnelNamesList.adapter = null
//        firstName_textviewVal.setText(personnelDetails.FirstName)
//        lastName_textviewVal.setText(personnelDetails.LastName)
//        certNo_textviewVal.setText(personnelDetails.CertificationNum)
//        rspUserID_textviewVal.text = if ((personnelDetails.RSP_UserName).equals("NULL")) "" else (personnelDetails.RSP_UserName)
//        rspEmail_textviewVal.text = if ((personnelDetails.RSP_Email).equals("NULL")) "" else (personnelDetails.RSP_Email)
//        certNo_textviewVal.setText(personnelDetails.CertificationNum)
//        seniorityDateVal.text = if ((personnelDetails.startDate).equals("NULL") || (personnelDetails.startDate).length < 10) "" else (personnelDetails.startDate)
//        var dateTobeFormated = ""
//        if (!((personnelDetails.startDate).isNullOrEmpty())) {
//            dateTobeFormated = appFprmat.format(dbFormat.parse(personnelDetails.startDate.substring(0, 10)))
//        }
//        startDateVal.setText(dateTobeFormated)
//        Log.v("FORMAT 1 ----- : ", dateTobeFormated)
//        dateTobeFormated = ""
////        if ( personnelDetails.startdate != null &&  !(personnelDetails.startDate).equals("NULL") && personnelDetails. != null && (personnelDetails.enddate).equals("NULL")) {
////            dateTobeFormated = "SELECT DATE"
////        } else {
//////            dateTobeFormated = appFprmat.format(dbFormat.parse(personnelDetails.enddate.substring(0, 10)))
////        }
////        endDateVal.setText(dateTobeFormated)
//        primaryEmailCheckBox.isChecked = personnelDetails.PrimaryMailRecipient.toBoolean()
//        if (personnelDetails.ContractSigner.toBoolean()) {
//            try {
//                contractSignerCheckBox.isChecked = true
//                coSignerAddr1Val.setText(personnelDetails.Addr1)
//                coSignerAddr2Val.setText(personnelDetails.Addr2)
//                coSignerCityVal.setText(personnelDetails.CITY)
//                coSignerCoEndDateVal.text = personnelDetails.CertificationDate
//                coSignerCoStartDateVal.text = personnelDetails.ExpirationDate
//                coSignerEmailVal.setText(personnelDetails.email)
//                coSignerPhoneVal.setText(personnelDetails.Phone.toString())
//                coSignerStateVal.setSelection(statesArray.indexOf(personnelDetails.ST.toString()))
//                coSignerZip4Val.setText(personnelDetails.ZIP4.toString())
//                coSignerZipVal.setText(personnelDetails.ZIP.toString())
//            } catch (exp: Exception) {
//                exp.printStackTrace()
//            }
//        }
//    }

    fun getTypeID(typeName: String): String {
        var typeID = "-1"
        for (fac in personnelTypeList) {
            if (fac.PersonnelTypeName.equals(typeName)) {
                typeID = fac.PersonnelTypeID
            }
        }
        return typeID
    }

    fun getTypeName(typeID: String): String {
        var typeName = "Not Selected"
        for (fac in personnelTypeList) {
            if (fac.PersonnelTypeID.equals(typeID)) {
                typeName= fac.PersonnelTypeName
            }
        }
        return typeName
    }

    fun getCertTypeID(typeName: String): String {
        var typeID = "-1"
        for (fac in certificationTypeList) {
            if (fac.PersonnelCertName.equals(typeName)) {
                typeID = fac.PersonnelCertID
            }
        }
        return typeID
    }

    fun getCertTypeName(typeID: String): String {
        var typeName = "Not Selected"
        for (fac in certificationTypeList) {
            if (fac.PersonnelCertID.equals(typeID)) {
                typeName= fac.PersonnelCertName
            }
        }
        return typeName
    }
    fun fillData(){

        //1-
        newCertNoText.addTextChangedListener(certificateIdNoLengthWatcher)
        //2-
        endDateMustBeAfterStartDateLogic()
        //3-
        newZipText.addTextChangedListener(zipOfFiveDigitsWatcher)
        newZipText2.addTextChangedListener(zipOfFourDigitsWatcher)
        newPhoneText.addTextChangedListener(phoneTenDigitsWatcher)
        newEmailText.addTextChangedListener(emailValidationWatcher)


    }
    fun emailFormatValidation(target : CharSequence) : Boolean{


        if (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            emailValid=true else emailValid=false


        return emailValid }


    var certificateIdNoLengthWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length>16||s.length<16){

                newCertNoText.setError("input required 16 elements")

            }



        }
    }
    var emailValidationWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (!emailFormatValidation(newEmailText.text.toString())){
                newEmailText.setError("assure email format standards")


            }



        }
    }
    var zipOfFiveDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length>5||s.length<5){

                newZipText.setError("input required 5 elements")

            }



        }
    }
    var zipOfFourDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length>4||s.length<4){

                newZipText2.setError("input required 4 elements")

            }



        }
    }
    var phoneTenDigitsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            if (s.length>10||s.length<10){

                newPhoneText.setError("input required 10 elements")

            }



        }
    }
    fun endDateMustBeAfterStartDateLogic(){

        newCoEndDateBtn.setOnClickListener(View.OnClickListener {
            if (newCoStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){

                newCoEndDateBtn.setError("please enter a start date first")

                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()
            }
            else {
                newCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCoEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
        newEndDateBtn.setOnClickListener(View.OnClickListener {
            if (newStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){

                newEndDateBtn.setError("please enter a start date first")
                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()

            }
            else {
                newEndDateBtn.setError(null)

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
        newCertEndDateBtn.setOnClickListener(View.OnClickListener {
            if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){

                newCertEndDateBtn.setError("please enter a start date first")
                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()

            }
            else {
                newCertEndDateBtn.setError(null)

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCertEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
    }



//    fun validateInputs(): Boolean {
//        var isInputsValid = true
//
//        firstName_textviewVal.setError(null)
//        lastName_textviewVal.setError(null)
//        coSignerAddr1Val.setError(null)
//        coSignerAddr2Val.setError(null)
//        coSignerCityVal.setError(null)
//        coSignerZip4Val.setError(null)
//        coSignerZipVal.setError(null)
//        coSignerEmailVal.setError(null)
//        coSignerPhoneVal.setError(null)
//        coSignerCoEndDateVal.setError(null)
//        coSignerCoStartDateVal.setError(null)
//        a1CertDateVal.setError(null)
//        a1ExpDateVal.setError(null)
//        a2CertDateVal.setError(null)
//        a2ExpDateVal.setError(null)
//        a3CertDateVal.setError(null)
//        a3ExpDateVal.setError(null)
//        a4CertDateVal.setError(null)
//        a4ExpDateVal.setError(null)
//        a5CertDateVal.setError(null)
//        a5ExpDateVal.setError(null)
//        a6CertDateVal.setError(null)
//        a6ExpDateVal.setError(null)
//        a7CertDateVal.setError(null)
//        a7ExpDateVal.setError(null)
//        a8CertDateVal.setError(null)
//        a8ExpDateVal.setError(null)
//        c1CertDateVal.setError(null)
//        c1ExpDateVal.setError(null)
//
//        if (firstName_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            firstName_textviewVal.setError("Required Field")
//        }
//
//        if (lastName_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            lastName_textviewVal.setError("Required Field")
//        }
//
//        if (contractSignerCheckBox.isChecked) {
//            if (coSignerAddr1Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerAddr1Val.setError("Required Field")
//            }
//            if (coSignerAddr2Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerAddr2Val.setError("Required Field")
//            }
//            if (coSignerCityVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCityVal.setError("Required Field")
//            }
//            if (coSignerZipVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerZipVal.setError("Required Field")
//            }
//            if (coSignerZip4Val.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerZip4Val.setError("Required Field")
//            }
//            if (coSignerPhoneVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerPhoneVal.setError("Required Field")
//            }
//            if (coSignerEmailVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerEmailVal.setError("Required Field")
//            }
//            if (coSignerCoStartDateVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCoStartDateVal.setError("Required Field")
//            }
//            if (coSignerCoEndDateVal.text.toString().isNullOrEmpty()) {
//                isInputsValid = false
//                coSignerCoEndDateVal.setError("Required Field")
//            }
//        }
//        if (!a1CertDateVal.text.equals("SELECT DATE")) {
//            if (a1ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a1ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a2CertDateVal.text.equals("SELECT DATE")) {
//            if (a2ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a2ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a3CertDateVal.text.equals("SELECT DATE")) {
//            if (a3ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a3ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a4CertDateVal.text.equals("SELECT DATE")) {
//            if (a4ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a4ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a5CertDateVal.text.equals("SELECT DATE")) {
//            if (a5ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a5ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a6CertDateVal.text.equals("SELECT DATE")) {
//            if (a6ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a6ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a7CertDateVal.text.equals("SELECT DATE")) {
//            if (a7ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a7ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!a8CertDateVal.text.equals("SELECT DATE")) {
//            if (a8ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                a8ExpDateVal.setError("Required Field")
//            }
//        }
//
//        if (!c1CertDateVal.text.equals("SELECT DATE")) {
//            if (c1ExpDateVal.text.equals("SELECT DATE")) {
//                isInputsValid = false
//                c1ExpDateVal.setError("Required Field")
//            }
//        }
//
//
//        return isInputsValid
//    }



    fun fillPersonnelTableView() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.column = 8
        var dateTobeFormated = ""
        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = getTypeName(get(it).PersonnelTypeID)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).FirstName
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).LastName
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (!(get(it).SeniorityDate.isNullOrEmpty())) {
                    textView.text = get(it).SeniorityDate.apiToAppFormat()
                } else {
                    textView.text = ""
                }

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).CertificationNum
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam5
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (!(get(it).startDate.isNullOrEmpty())) {
                    textView.text  = get(it).startDate.apiToAppFormat()
                } else {
                    textView.text  = ""
                }

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam6
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (!(get(it).ExpirationDate.isNullOrEmpty())) {
                    textView.text = get(it).ExpirationDate.apiToAppFormat()
                } else {
                    textView.text = ""
                }


                tableRow.addView(textView)

                var checkBox = CheckBox(context)

                checkBox = CheckBox(context)
                checkBox.layoutParams = rowLayoutParam7
                checkBox.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                checkBox.isChecked = (get(it).ContractSigner.equals("true"))
                checkBox.isEnabled=false
                tableRow.addView(checkBox)

                checkBox = CheckBox(context)
                checkBox.layoutParams = rowLayoutParam8
                checkBox.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                checkBox.isChecked = (get(it).PrimaryMailRecipient.equals("true"))
                checkBox.isEnabled=false
                tableRow.addView(checkBox)

                PersonnelResultsTbl.addView(tableRow)

            }
        }
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
