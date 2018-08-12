package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateFacilityLanguageData
import com.inspection.adapter.LanguageListAdapter
import com.inspection.model.AAALocations
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVLocation : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var facLocationsList = ArrayList<AAALocations>()
    private var facLocationsArray = ArrayList<String>()
    private var states= arrayOf("select state","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming")



    var languagesGridView: ExpandableHeightGridView? = null
    internal var arrayAdapter: LanguageListAdapter? = null
    var langListItems=ArrayList<TypeTablesModel.languageType>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_aarav_location, container, false)


        languagesGridView = view.findViewById(R.id.languagesGridView)

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scopeOfServiceChangesWatcher()
        fillLocationTableView()
        fillPhoneTableView()
        fillOpenHoursTableView()
        fillClosedHoursTableView()
        fillEmailTableView()


        setServices()


        setFieldsListeners()


    }


    fun setFieldsListeners(){
        facilityIsOpenEffDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenEffDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        facilityIsOpenExpDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenExpDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        exitAddEmailDialogeBtnId.setOnClickListener({
            addNewEmailDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })
//        exitAddLocationDialogeBtnId.setOnClickListener({
//            addNewLocationDialog.visibility = View.GONE
//                     alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
//        })

        exitEditLocationDialogeBtnId.setOnClickListener({
            editLocationDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })
        exitUpdatePhoneDialogeBtnId.setOnClickListener({
            alphaBackgroundForDialogs.visibility = View.GONE
            editPhoneDialog.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })
        exitAddPhoneDialogeBtnId.setOnClickListener({
            addNewPhoneDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })

//        addNewLocationButton.setOnClickListener({
//
//            disableAllAddButnsAndDialog()
//            showLocationDialog()
//        })

        addNewPhoneButton.setOnClickListener({
            disableAllAddButnsAndDialog()
            showPhoneDialog()
        })

        addNewEmailButton.setOnClickListener {
            disableAllAddButnsAndDialog()
            showEmailDialog()
        }

        setLocations()



        locationSubmitButton.setOnClickListener({
            // missing validation for states when the lookup is ready

            var location =FacilityDataModel.TblAddress().locIsInputsValid

//            if (newStateSpinner.selectedItem.toString().contains("select")){
//                location=false
//                stateTextView.setError("required field")
//
//            }    else
//            { stateTextView.setError(null)}
//
//
//            if (newLocAddr1Text.text.isNullOrEmpty()) {
//                location=false
//                newLocAddr1Text.setError("please enter address 1")
//            }
//            if (newLocCityText.text.isNullOrEmpty()) {
//                location=false
//                newLocCityText.setError("please enter city")
//            }
//            if (newLocCountryText.text.isNullOrEmpty()) {
//                location=false
//                newLocCountryText.setError("please enter country")
//            }
//            if (newLocZipText.text.isNullOrEmpty()) {
//                location=false
//                newLocZipText.setError("please enter country")
//            }
//            if (newLocTypeSpinner.selectedItem.equals("Physical") && newLocLongText.text.isNullOrEmpty()) {
//                location=false
//                newLocLongText.setError("please enter longitude")
//            }
//            if (newLocTypeSpinner.selectedItem.equals("Physical") && newLocLatText.text.isNullOrEmpty()) {
//                location=false
//                newLocLatText.setError("please enter latitude")
//            }
//            if (newLocBranchNoText.text.isNullOrEmpty()) {
//                location=false
//                newLocBranchNoText.setError("please enter branch number")
//            }
//            if (newLocBranchNameText.text.isNullOrEmpty()) {
//                location=false
//                newLocBranchNameText.setError("please enter branch name")
//            }
//                if (newLocAddr1Text.text.isNullOrEmpty()||newLocCityText.text.isNullOrEmpty()||newLocCountryText.text.isNullOrEmpty()
//                ||newLocZipText.text.isNullOrEmpty()||(newLocTypeSpinner.selectedItem.equals("Physical") && newLocLongText.text.isNullOrEmpty())
//                ||(newLocTypeSpinner.selectedItem.equals("Physical") && newLocLatText.text.isNullOrEmpty())
//                ||newLocBranchNoText.text.isNullOrEmpty()||newLocBranchNameText.text.isNullOrEmpty()||newLocBranchNameText.text.isNullOrEmpty()
//                ||newStateSpinner.selectedItem.toString().contains("select")){
//                    location=false
//                    Toast.makeText(context,"please fill required fields",Toast.LENGTH_SHORT).show()
//
//                }else {
//                    location=true
//                    submitFacilityAddress()
//                    enableAllAddButnsAndDialog()
//                        }

        })


        phoneSubmitButton.setOnClickListener({

            var phoneValide=FacilityDataModel.TblPhone().phoneIsInputsValid
            if (newPhoneNoText.text.isNullOrEmpty()) {
                newPhoneNoText.setError("please enter phone number")
                phoneValide=false
            } else {
                phoneValide=true
                submitFacilityPhone()
                enableAllAddButnsAndDialog()
            }
        })

        emailSubmitButton.setOnClickListener({

            var emailValid=FacilityDataModel.TblFacilityEmail().emailIsInputsValid
            if (newEmailAddrText.text.isNullOrEmpty()) {
                emailValid=false
                newEmailAddrText.setError("please enter email address")
            } else {
                emailValid=true
                submitFacilityEmail()
                enableAllAddButnsAndDialog()
            }
        })

        saveButton.setOnClickListener(View.OnClickListener {


            submitLanguages()


        })


        nightDropCheck.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblHours[0].NightDrop = b
        }

        nightDropInstText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblHours[0].NightDropInstr = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


    }

    fun enableAllAddButnsAndDialog(){

        for (i in 0 until mainViewLinearId.childCount) {
            val child = mainViewLinearId.getChildAt(i)
            child.isEnabled = true
        }

        for (i in 0 until mainViewLinearId2.childCount) {
            val child = mainViewLinearId2.getChildAt(i)
            child.isEnabled = true
        }

        for (i in 0 until mainViewLinearId3.childCount) {
            val child = mainViewLinearId3.getChildAt(i)
            child.isEnabled = true
        }



        var childViewCount = phoneTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= phoneTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=true

            }

        }
        var locationChildViewCount = locationTbl.getChildCount();

        for ( i in 1..locationChildViewCount-1) {
            var row : TableRow= locationTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=true

            }

        }
        var emailChildViewCount = emailTbl.getChildCount();

        for ( i in 1..emailChildViewCount-1) {
            var row : TableRow= emailTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=true

            }

        }



    }
    fun disableAllAddButnsAndDialog(){

        for (i in 0 until mainViewLinearId.childCount) {
            val child = mainViewLinearId.getChildAt(i)
            child.isEnabled = false
        }

        for (i in 0 until mainViewLinearId2.childCount) {
            val child = mainViewLinearId2.getChildAt(i)
            child.isEnabled = false
        }

        for (i in 0 until mainViewLinearId3.childCount) {
            val child = mainViewLinearId3.getChildAt(i)
            child.isEnabled = false
        }



        var childViewCount = phoneTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= phoneTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=false

            }

        }
        var locationChildViewCount = locationTbl.getChildCount();

        for ( i in 1..locationChildViewCount-1) {
            var row : TableRow= locationTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=false

            }

        }
        var emailChildViewCount = emailTbl.getChildCount();

        for ( i in 1..emailChildViewCount-1) {
            var row : TableRow= emailTbl.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=false

            }

        }



    }

    private fun setServices() {


        for (model in TypeTablesModel.getInstance().LanguageType) {

            langListItems.add(model)

        }

        arrayAdapter = LanguageListAdapter(context!!, R.layout.lang_checkbox_item, langListItems)

        languagesGridView?.adapter = arrayAdapter
        languagesGridView?.isExpanded=true

    }

    private var locationTypeList = ArrayList<TypeTablesModel.locationType>()
    private var locationypeArray = ArrayList<String>()

    private var phoneTypeList = ArrayList<TypeTablesModel.locationPhoneType>()
    private var phoneTypeArray = ArrayList<String>()

    private var emailTypeList = ArrayList<TypeTablesModel.emailType>()
    private var emailTypeArray = ArrayList<String>()



//    private fun showLocationDialog() {
//
//        alphaBackgroundForDialogs.visibility = View.VISIBLE
//        addNewLocationDialog.visibility = View.VISIBLE
//
//        locationTypeList = TypeTablesModel.getInstance().LocationType
//        locationypeArray.clear()
//        for (fac in locationTypeList) {
//            locationypeArray.add(fac.LocTypeName)
//        }
//
//        var locTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, locationypeArray)
//        locTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newLocTypeSpinner.adapter = locTypeAdapter
////        locationDialogView.newLoc2TypeSpinner.adapter = locTypeAdapter
//
//
//    }

    private fun showLocationDialog(index: Int) {

        alphaBackgroundForDialogs.visibility = View.VISIBLE
        editLocationDialog.visibility = View.VISIBLE

        newLocLatText.setText(FacilityDataModel.getInstance().tblAddress[index].LATITUDE)
        newLocLongText.setText(FacilityDataModel.getInstance().tblAddress[index].LONGITUDE)

        locationSubmitButton.setOnClickListener {
            FacilityDataModel.getInstance().tblAddress[index].LATITUDE = newLocLatText.text.toString()
            FacilityDataModel.getInstance().tblAddress[index].LONGITUDE = newLocLongText.text.toString()

            editLocationDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
            fillLocationTableView()
        }


    }

    private fun getLocationTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().LocationType) {
            if (fac.LocTypeID.equals(typeID)) {
                typeName= fac.LocTypeName
            }
        }
        return typeName
    }

    private fun getPhoneTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().LocationPhoneType) {
            if (fac.LocPhoneID.equals(typeID)) {
                typeName= fac.LocPhoneName
            }
        }
        return typeName
    }

    private fun getEmailTypeName(typeID: String): String {
        var typeName = ""
        for (fac in TypeTablesModel.getInstance().EmailType) {
            if (fac.EmailID.equals(typeID)) {
                typeName= fac.EmailName
            }
        }
        return typeName
    }

    private fun showPhoneDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewPhoneDialog.visibility = View.VISIBLE
        phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
        phoneTypeArray.clear()
        for (fac in phoneTypeList) {
            phoneTypeArray.add(fac.LocPhoneName)
        }

        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPhoneTypeSpinner.adapter = phoneTypeAdapter
    }

    private fun showEmailDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewEmailDialog.visibility = View.VISIBLE
        emailTypeList = TypeTablesModel.getInstance().EmailType
        emailTypeArray.clear()
        for (fac in emailTypeList) {
            emailTypeArray.add(fac.EmailName)
        }

        var emailTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, emailTypeArray)
        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEmailTypeSpinner.adapter = emailTypeAdapter
    }

    fun prepareLocationPage(){
        setLocations()
    }

    private fun setLocations() {

        var citiesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, states)
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newStateSpinner.adapter = citiesAdapter


//        for (fac in FacilityDataModel.getInstance().tblAddress) {
//            if (fac.LocationTypeID.toInt() == 1) {
//                phyloc1addr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                phyloc1addr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                phyloc1addr1latitude.setText(if (fac.LATITUDE.isNullOrEmpty()) "" else fac.LATITUDE)
//                phyloc1addr1longitude.setText(if (fac.LONGITUDE.isNullOrEmpty()) "" else fac.LONGITUDE)
//                phylocAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                phylocAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            } else if (fac.LocationTypeID.toInt() == 2) {
//                mailaddr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                mailaddr1branchname.text = if ((fac.BranchName.isNullOrEmpty())) "" else fac.BranchName
//                mailaddr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                mailAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                mailAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            } else if (fac.LocationTypeID.toInt() == 3) {
//                billaddr1branchname.text = if (fac.BranchName.isNullOrEmpty()) "" else fac.BranchName
//                billaddr1branchno.text = if (fac.BranchNumber.isNullOrEmpty()) "" else fac.BranchNumber
//                billAddr1address.text = if (fac.FAC_Addr1.isNullOrEmpty()) "" else fac.FAC_Addr1
//                billAddr2address.text = if (fac.FAC_Addr2.isNullOrEmpty()) "" else fac.FAC_Addr2
//            }
//        }
//        progressbarLocation.visibility = View.INVISIBLE
    }


    fun validateInputs(): Boolean {
        var isInputsValid = true

        phyloc1addr1latitude.setError(null)
//        phyloc1addr2latitude.setError(null)
//        phyloc1addr2longitude.setError(null)
        phyloc1addr1longitude.setError(null)
//        stateTextView.setError(null)


        if (phyloc1addr1latitude.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            phyloc1addr1latitude.setError("Required Field")
        }

//        if (newStateSpinner.selectedItem.toString().contains("select")){
//
//            isInputsValid = false
//            stateTextView.setError("required field")
//
//
//        }

//        if(loc1addr2latitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2latitude.setError("Required Field")
//        }

        if (phyloc1addr1longitude.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            phyloc1addr1longitude.setError("Required Field")
        }

//        if(loc1addr2longitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2longitude.setError("Required Field")
//        }


        return isInputsValid
    }

    fun fillPhoneTableView() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
      //  rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        if (phoneTbl.childCount>1) {
            for (i in phoneTbl.childCount - 1 downTo 1) {
                phoneTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
   //     rowLayoutParam1.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
       rowLayoutParam2.width = TableLayout.LayoutParams.WRAP_CONTENT



        FacilityDataModel.getInstance().tblPhone.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                //getTypeName
                textView.text = getPhoneTypeName(get(it).PhoneTypeID)
                tableRow.addView(textView)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                textView2.text = get(it).PhoneNumber
                tableRow.addView(textView2)

                val editPhoneBtn = Button(context)
                editPhoneBtn.layoutParams = rowLayoutParam2
                editPhoneBtn.textAlignment = Button.TEXT_ALIGNMENT_TEXT_START
                editPhoneBtn.text = "Edit"
                editPhoneBtn.setTextColor(Color.WHITE)
//                editPhoneBtn.setBackgroundResource(R.drawable.green_background_button)
                tableRow.addView(editPhoneBtn)


                editPhoneBtn.setOnClickListener(View.OnClickListener {
                    var rowIndex=phoneTbl.indexOfChild(tableRow)
                    var phoneFacilityChangedIndex= rowIndex-1
             //       Toast.makeText(context,rowIndex.toString(),Toast.LENGTH_SHORT).show()


                    disableAllAddButnsAndDialog()
                    newChangesPhoneNoText.text.clear()
                    alphaBackgroundForDialogs.visibility = View.VISIBLE
                    editPhoneDialog.visibility = View.VISIBLE
                    phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
                    phoneTypeArray.clear()
                    for (fac in phoneTypeList) {
                        phoneTypeArray.add(fac.LocPhoneName)
                    }

                    var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
                    phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    newPhoneTypeSpinner.adapter = phoneTypeAdapter

                    phoneSaveChangesButton.setOnClickListener(View.OnClickListener {

                        var phoneTypeID=""
                         if (newChangesPhoneNoText.text.isNullOrEmpty())  {
                             newChangesPhoneNoText.setError("please enter required field")

                             Toast.makeText(context,"please fill required fields",Toast.LENGTH_SHORT).show()
                         }
                        else {
                             val phoneNo =  newChangesPhoneNoText.text.toString()
                             for (phoneTypeTableId in TypeTablesModel.getInstance().LocationPhoneType){
                                     if (phoneTypeTableId.LocPhoneName==textView.text.toString()){

                                         phoneTypeID = phoneTypeTableId.LocPhoneID.toString()

                                 }
                             }
                        val insertDate = Date().toAppFormat()
                        val insertBy ="sa"
                        val updateDate = Date().toAppFormat()
                        val updateBy ="sa"
                        val activeVal = "0"
                        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                        val clubCode ="004"
                        var urlString = facilityNo+"&clubcode="+clubCode+"&phoneTypeId="+phoneTypeID+"&phoneNumber="+phoneNo+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&extension=&description=&phoneId=&active=1"
                        Log.v("Data To Submit", urlString)
                        contactInfoLoadingView.visibility = View.VISIBLE
                        editPhoneDialog.visibility = View.GONE
                        alphaBackgroundForDialogs.visibility = View.GONE

                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString,
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        contactInfoLoadingView.visibility = View.GONE
                                        FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber=newChangesPhoneNoText.text.toString()
                                        Log.v("PHONE_RESPONSE",response.toString())
                                        fillPhoneTableView()

                                        enableAllAddButnsAndDialog()


                                    })
                                }, Response.ErrorListener {
                            contactInfoLoadingView.visibility = View.GONE
                            Log.v("error while submitting", "Phone Details")
                        }))



                    }
                    })

                })

                phoneTbl.addView(tableRow)
            }
        }
        altPhoneTableRow(2)


    }

    fun fillEmailTableView() {
        if (emailTbl.childCount>1) {
            for (i in emailTbl.childCount - 1 downTo 1) {
                emailTbl.removeViewAt(i)
            }
        }

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

        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text = getEmailTypeName(get(it).emailTypeId)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).email
                tableRow.addView(textView)

                emailTbl.addView(tableRow)
            }

        }
        altEmailTableRow(2)

    }
    fun fillOpenHoursTableView() {
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
        if (emailTbl.childCount>1) {
            for (i in emailTbl.childCount - 1 downTo 1) {
                emailTbl.removeViewAt(i)
            }
        }

        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text = "Open : "
                tableRow.addView(textView)


                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text =if (getEmailTypeName(get(it).SunOpen).isNullOrEmpty()) "Closed" else getEmailTypeName(get(it).SunOpen)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).MonOpen
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).TueOpen
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).WedOpen
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).ThuOpen
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).FriOpen
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text =if (getEmailTypeName(get(it).SatOpen).isNullOrEmpty()) "Closed" else getEmailTypeName(get(it).SunOpen)
                tableRow.addView(textView)

                openHoursTblId.addView(tableRow)
            }
        }
    }
    fun fillClosedHoursTableView() {
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
        if (emailTbl.childCount>1) {
            for (i in emailTbl.childCount - 1 downTo 1) {
                emailTbl.removeViewAt(i)
            }
        }

        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text = "Closed : "
                tableRow.addView(textView)


                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text =if (getEmailTypeName(get(it).SunClose).isNullOrEmpty()) "Closed" else getEmailTypeName(get(it).SunOpen)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).MonClose
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).TueClose
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).WedClose
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).ThuClose
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).FriClose
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text =if (getEmailTypeName(get(it).SatClose).isNullOrEmpty()) "Closed" else getEmailTypeName(get(it).SunOpen)
                tableRow.addView(textView)

                closedHoursTblId.addView(tableRow)
            }
        }
    }

    fun scopeOfServiceChangesWatcher() {
        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {

            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {


                if (FragmentARRAVScopeOfService.dataChanged) {

                    val builder = AlertDialog.Builder(context)

                    // Set the alert dialog title
                    builder.setTitle("Changes made confirmation")

                    // Display a message on alert dialog
                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("YES") { dialog, which ->


                        contactInfoLoadingView.visibility = View.VISIBLE


                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())
                                        contactInfoLoadingView.visibility = View.GONE

                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                                FragmentARRAVScopeOfService.dataChanged = false

                                            }

                                        }

                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading personnal record")
                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()

                            contactInfoLoadingView.visibility = View.GONE

                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        contactInfoLoadingView.visibility = View.GONE

                    }


                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    // Display the alert dialog on app interface
                    dialog.show()

                }

            } else {


                val builder = AlertDialog.Builder(context)

                // Set the alert dialog title
                builder.setTitle("Changes made Warning")

                // Display a message on alert dialog
                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ok") { dialog, which ->

                    FragmentARRAVScopeOfService.dataChanged = false

                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true

                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }

        }
    }



    fun fillLocationTableView() {

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        if (locationTbl.childCount>1) {
            for (i in locationTbl.childCount - 1 downTo 1) {
                locationTbl.removeViewAt(i)
            }
        }

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
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.column = 8

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.column = 9

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.column = 10

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 1F
        rowLayoutParam11.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.column = 11
        var dateTobeFormated = ""

        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //getTypeName
                textView.text = getLocationTypeName(get(it).LocationTypeID)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).FAC_Addr1
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).FAC_Addr2
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).CITY

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).County
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam5
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).ST
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam6
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).ZIP + "-" + get(it).ZIP4
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam7
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).LATITUDE
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam8
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).LONGITUDE
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam9
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).BranchNumber
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam10
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).BranchName
                tableRow.addView(textView)

                var editButton = Button(context)
                editButton.layoutParams = rowLayoutParam11
                editButton.text = "EDIT"
                editButton.tag = it
//                editButton.setBackgroundResource(R.drawable.green_background_button)
                editButton.setTextColor(Color.WHITE)
                tableRow.addView(editButton)
                if (!getLocationTypeName(get(it).LocationTypeID).equals("Physical")){
                    editButton.visibility = View.INVISIBLE
                }

                editButton.setOnClickListener { s ->
                    disableAllAddButnsAndDialog()
                    showLocationDialog(it)
                }

                locationTbl.addView(tableRow)

            }
        }
        altLocationTableRow(2)

    }

    fun submitFacilityEmail(){
        val emailTypeID = TypeTablesModel.getInstance().EmailType.filter { s -> s.EmailName==newEmailTypeSpinner.selectedItem.toString()}[0].EmailID
        val email = if (newEmailAddrText.text.isNullOrEmpty())  "" else newEmailAddrText.text
        val insertDate = Date().toAppFormat()
        val insertBy ="sa"
        val updateDate = Date().toAppFormat()
        val updateBy ="sa"
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode ="004"

        val newEmail = FacilityDataModel.TblFacilityEmail()
        newEmail.email = email.toString()
        newEmail.emailTypeId = emailTypeID

        var urlString = facilityNo+"&clubcode="+clubCode+"&emailTypeId="+emailTypeID+"&email="+email+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&emailId="
        Log.v("Data To Submit", urlString)
        contactInfoLoadingView.visibility = View.VISIBLE
        addNewEmailDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityEmail + urlString,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        contactInfoLoadingView.visibility = View.GONE
                        context!!.toast("Email Submit Successful")
                        Log.v("RESPONSE",response.toString())
                        FacilityDataModel.getInstance().tblFacilityEmail.add(newEmail)
                        fillEmailTableView()



                    })
                }, Response.ErrorListener {
            contactInfoLoadingView.visibility = View.GONE
            context!!.toast("Failed to add new email")
            Log.v("error while submitting", it.message)
        }))
    }


    fun submitLanguages(){


        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityLanguageData + "&langTypeId=${LanguageListAdapter.langArray.toString().replace("[","").replace("]","")}&insertBy=SumA&insertDate=2014-03-17T14:06:18.464",
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        Log.v("LANG_SUBMIT_RESPONSE",response.toString())
                        Toast.makeText(context,"languages submited",Toast.LENGTH_SHORT).show()


                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading personnal record")
            Toast.makeText(context,"error submitting languages",Toast.LENGTH_SHORT).show()

        }))
    }


    fun submitFacilityAddress(){
//        val locTypeID = TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeName==newLocTypeSpinner.selectedItem.toString()}[0].LocTypeID
//        val address1Text = if (newLocAddr1Text.text.isNullOrEmpty())  "" else newLocAddr1Text.text
//        val address2Text = if (newLocAddr2Text.text.isNullOrEmpty())  "" else newLocAddr2Text.text
//        val cityText= if (newLocCityText.text.isNullOrEmpty())  "" else newLocCityText.text
//        val countryText = if (newLocCountryText.text.isNullOrEmpty())  "" else newLocCountryText.text
//        val longText = if (newLocLongText.text.isNullOrEmpty())  "" else newLocLongText.text
//        val latText = if (newLocLatText.text.isNullOrEmpty())  "" else newLocLatText.text
//        val zipText = if (newLocZipText.text.isNullOrEmpty())  "" else newLocZipText.text
//        val branchNameText = if (newLocBranchNameText.text.isNullOrEmpty())  "" else newLocBranchNameText.text
//        val branchNoText = if (newLocBranchNoText.text.isNullOrEmpty())  "" else newLocBranchNoText.text
        val insertDate = Date().toAppFormat()
        val insertBy ="sa"
        val updateDate = Date().toAppFormat()
        val updateBy ="sa"
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode ="004"
        val newLocation = FacilityDataModel.TblAddress()
//        newLocation.BranchName = branchNameText.toString()
//        newLocation.BranchNumber = branchNoText.toString()
//        newLocation.CITY = cityText.toString()
//        newLocation.County = countryText.toString()
//        newLocation.FAC_Addr1 = address1Text.toString()
//        newLocation.FAC_Addr2 = address2Text.toString()
//        newLocation.LATITUDE = latText.toString()
//        newLocation.LONGITUDE = longText.toString()
//        newLocation.LocationTypeID = locTypeID
//        newLocation.ST = "CA"
//        newLocation.ZIP = zipText.toString()
//        newLocation.ZIP4 = ""

//        var urlString = facilityNo+"&clubcode="+clubCode+"&LocationTypeID="+locTypeID+"&FAC_Addr1="+address1Text+"&FAC_Addr2="+address2Text+"&CITY="+cityText+"&ST=CA&ZIP="+zipText+"&ZIP4=&Country="+countryText+"&BranchName="+branchNameText+"&BranchNumber="+branchNoText+"&LATITUDE="+latText+"&LONGITUDE="+longText+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&emailId=&active=1"
//        Log.v("Data To Submit", urlString)
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityAddress + urlString,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//                        Log.v("RESPONSE",response.toString())
//                        FacilityDataModel.getInstance().tblAddress.add(newLocation)
//                        fillLocationTableView()
////                        addNewLocationDialog.visibility=View.GONE
//                        alphaBackgroundForDialogs.visibility=View.GONE
//                    })
//                }, Response.ErrorListener {
//            Log.v("error while submitting", it.message)
//        }))
    }

    fun submitFacilityPhone(){
        val phoneTypeID = TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneName==newPhoneTypeSpinner.selectedItem.toString()}[0].LocPhoneID
        val phoneNo = if (newPhoneNoText.text.isNullOrEmpty())  "" else newPhoneNoText.text
        val insertDate = Date().toAppFormat()
        val insertBy ="sa"
        val updateDate = Date().toAppFormat()
        val updateBy ="sa"
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode ="004"
        val newPhone = FacilityDataModel.TblPhone()
        newPhone.PhoneNumber = phoneNo.toString()
        newPhone.PhoneTypeID= phoneTypeID
        var urlString = facilityNo+"&clubcode="+clubCode+"&phoneTypeId="+phoneTypeID+"&phoneNumber="+phoneNo+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&extension=&description=&phoneId=&active=1"
        Log.v("Data To Submit", urlString)
        contactInfoLoadingView.visibility = View.VISIBLE
        addNewPhoneDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        contactInfoLoadingView.visibility = View.GONE
                        Log.v("RESPONSE",response.toString())
                        FacilityDataModel.getInstance().tblPhone.add(newPhone)
                        fillPhoneTableView()




                    })
                }, Response.ErrorListener {
            contactInfoLoadingView.visibility = View.GONE
            Log.v("error while submitting", "Phone Details")
        }))
    }
    fun altEmailTableRow(alt_row : Int) {
        var childViewCount = emailTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= emailTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }
    fun altPhoneTableRow(alt_row : Int) {
        var childViewCount = phoneTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= phoneTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }
    fun checkMarkChangesWasDoneForPhoneTable() {
        val dateFormat1 = SimpleDateFormat("dd MMM yyyy")

        var itemOrgArray = FacilityDataModelOrg.getInstance().tblPhone
        var itemArray = FacilityDataModel.getInstance().tblPhone
        if (itemOrgArray.size == itemArray.size) {
            for (i in 0 until itemOrgArray.size){

                if (itemOrgArray[i].PhoneNumber != itemArray[i].PhoneNumber ||
                        itemOrgArray[i].PhoneTypeID != itemArray[i].PhoneTypeID

                ) {
                    MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                    Log.v("checkkk", itemOrgArray[i].PhoneNumber + "=="+ itemArray[i].PhoneNumber)
                    Log.v("checkkk", itemOrgArray[i].PhoneTypeID + "=="+ itemArray[i].PhoneTypeID)

                }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "2ndddd")

        }
    }
    fun checkMarkChangesWasDoneForlanguageContactInfoFacilityTab() {

        var langCount=0
        var itemOrgArray = FacilityDataModelOrg.getInstance().tblLanguage
        var itemArray = FacilityDataModel.getInstance().tblLanguage
        for (itemAr in itemArray) {
            for (itemOrgAr in itemOrgArray) {



                if (itemAr.LangTypeID == itemOrgAr.LangTypeID ) {
                    langCount++
                }


            }

        }

        if (langCount!=itemArray.size||itemOrgArray.size!=itemArray.size){

            Log.v("MarkChangeWasDone", langCount.toString()  +  "---"+itemArray.size.toString() +"------" +itemOrgArray.size.toString())
            MarkChangeWasDone()

        }
    }
    fun checkMarkChangesWasDoneForEmailTable() {
        val dateFormat1 = SimpleDateFormat("dd MMM yyyy")

        var itemOrgArray = FacilityDataModelOrg.getInstance().tblFacilityEmail
        var itemArray = FacilityDataModel.getInstance().tblFacilityEmail
        if (itemOrgArray.size == itemArray.size) {
            for (i in 0 until itemOrgArray.size){

                if (itemOrgArray[i].email != itemArray[i].email ||
                        itemOrgArray[i].emailTypeId != itemArray[i].emailTypeId

                ) {
                    MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                    Log.v("checkkk", itemOrgArray[i].email + "=="+ itemArray[i].email)
                    Log.v("checkkk", itemOrgArray[i].emailTypeId + "=="+ itemArray[i].emailTypeId)

                }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "2ndddd")

        }
    }

    fun checkMarkChangesWasDoneForAddressTable() {
        val dateFormat1 = SimpleDateFormat("dd MMM yyyy")

        var itemOrgArray = FacilityDataModelOrg.getInstance().tblAddress
        var itemArray = FacilityDataModel.getInstance().tblAddress
        if (itemOrgArray.size == itemArray.size) {
            for (i in 0 until itemOrgArray.size){

                if (itemOrgArray[i].BranchName != itemArray[i].BranchName ||
                        itemOrgArray[i].BranchNumber != itemArray[i].BranchNumber||
                        itemOrgArray[i].CITY != itemArray[i].CITY||
                        itemOrgArray[i].County != itemArray[i].County||
                        itemOrgArray[i].FAC_Addr1 != itemArray[i].FAC_Addr1||
                        itemOrgArray[i].FAC_Addr2 != itemArray[i].FAC_Addr2||
                        itemOrgArray[i].LATITUDE != itemArray[i].LATITUDE||
                        itemOrgArray[i].LONGITUDE != itemArray[i].LONGITUDE||
                        itemOrgArray[i].LocationTypeID != itemArray[i].LocationTypeID||
                        itemOrgArray[i].ST != itemArray[i].ST||
                        itemOrgArray[i].ZIP != itemArray[i].ZIP||
                        itemOrgArray[i].ZIP4 != itemArray[i].ZIP4

                ) {
                    MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                    Log.v("checkkk", itemOrgArray[i].BranchName + "=="+ itemArray[i].BranchName)
                    Log.v("checkkk", itemOrgArray[i].BranchNumber + "=="+ itemArray[i].BranchNumber)
                    Log.v("checkkk", itemOrgArray[i].CITY + "=="+ itemArray[i].CITY)
                    Log.v("checkkk", itemOrgArray[i].County + "=="+ itemArray[i].County)
                    Log.v("checkkk", itemOrgArray[i].FAC_Addr1 + "=="+ itemArray[i].FAC_Addr1)
                    Log.v("checkkk", itemOrgArray[i].FAC_Addr2 + "=="+ itemArray[i].FAC_Addr2)
                    Log.v("checkkk", itemOrgArray[i].LATITUDE + "=="+ itemArray[i].LATITUDE)
                    Log.v("checkkk", itemOrgArray[i].LONGITUDE + "=="+ itemArray[i].LONGITUDE)
                    Log.v("checkkk", itemOrgArray[i].LocationTypeID + "=="+ itemArray[i].LocationTypeID)
                    Log.v("checkkk", itemOrgArray[i].ST + "=="+ itemArray[i].ST)
                    Log.v("checkkk", itemOrgArray[i].ZIP + "=="+ itemArray[i].ZIP)
                    Log.v("checkkk", itemOrgArray[i].ZIP4 + "=="+ itemArray[i].ZIP4)

                }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "2ndddd")

        }
    }

    fun altLocationTableRow(alt_row : Int) {
        var childViewCount = locationTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= locationTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVLocation.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVLocation {
            val fragment = FragmentARRAVLocation()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
