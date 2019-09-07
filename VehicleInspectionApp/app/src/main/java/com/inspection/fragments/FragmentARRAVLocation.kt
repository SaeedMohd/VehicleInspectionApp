package com.inspection.fragments


import android.app.ActionBar
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Debug
import androidx.fragment.app.Fragment
import androidx.core.widget.TextViewCompat
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateFacilityLanguageData
import com.inspection.adapter.LanguageListAdapter
import com.inspection.model.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import java.net.URLEncoder
import java.text.Normalizer
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
    private var hoursArray:Array<String>? = null


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
//      ArrayAdapter<String> adapterJazyky = new ArrayAdapter<String>(this,
//              R.layout.spinner_text_layout.xml, {"one","two","etc...."});
        val officeTimes = resources.getStringArray(R.array.officeTimes)
        sunCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        sunOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        monCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        monOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        tueCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        tueOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        wedCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        wedOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        thuCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        thuOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        friCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        friOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        satCloseSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)
        satOpenSpinner.adapter = ArrayAdapter.createFromResource(activity,R.array.officeTimes,R.layout.spinner_time_item)


        FacilityDataModel.getInstance().tblHours[0].apply {
            if (SunClose.equals("00:00:01 AM")) SunClose="Closed"
            if (MonClose.equals("00:00:01 AM")) MonClose="Closed"
            if (TueClose.equals("00:00:01 AM")) TueClose="Closed"
            if (WedClose.equals("00:00:01 AM")) WedClose="Closed"
            if (ThuClose.equals("00:00:01 AM")) ThuClose="Closed"
            if (FriClose.equals("00:00:01 AM")) FriClose="Closed"
            if (SatClose.equals("00:00:01 AM")) SatClose="Closed"
            if (SunOpen.equals("00:00:01 AM")) SunOpen="Closed"
            if (MonOpen.equals("00:00:01 AM")) MonOpen="Closed"
            if (TueOpen.equals("00:00:01 AM")) TueOpen="Closed"
            if (WedOpen.equals("00:00:01 AM")) WedOpen="Closed"
            if (ThuOpen.equals("00:00:01 AM")) ThuOpen="Closed"
            if (FriOpen.equals("00:00:01 AM")) FriOpen="Closed"
            if (SatOpen.equals("00:00:01 AM")) SatOpen="Closed"
        }
        FacilityDataModelOrg.getInstance().tblHours[0].apply {
            if (SunClose.equals("00:00:01 AM")) SunClose="Closed"
            if (MonClose.equals("00:00:01 AM")) MonClose="Closed"
            if (TueClose.equals("00:00:01 AM")) TueClose="Closed"
            if (WedClose.equals("00:00:01 AM")) WedClose="Closed"
            if (ThuClose.equals("00:00:01 AM")) ThuClose="Closed"
            if (FriClose.equals("00:00:01 AM")) FriClose="Closed"
            if (SatClose.equals("00:00:01 AM")) SatClose="Closed"
            if (SunOpen.equals("00:00:01 AM")) SunOpen="Closed"
            if (MonOpen.equals("00:00:01 AM")) MonOpen="Closed"
            if (TueOpen.equals("00:00:01 AM")) TueOpen="Closed"
            if (WedOpen.equals("00:00:01 AM")) WedOpen="Closed"
            if (ThuOpen.equals("00:00:01 AM")) ThuOpen="Closed"
            if (FriOpen.equals("00:00:01 AM")) FriOpen="Closed"
            if (SatOpen.equals("00:00:01 AM")) SatOpen="Closed"
        }


        hoursArray = resources.getStringArray(R.array.officeTimes)
        fillLocationTableView()
        fillPhoneTableView()
        fillOpenHoursTableView()
        fillClosedHoursTableView()
        fillEmailTableView()
        copyHoursBtn.setOnClickListener {
            alphaBackgroundForDialogs.visibility = View.VISIBLE
            copyHoursDialog.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true

            copyButtonCV.setOnClickListener {
                var openValue = 0
                var closeValue = 0
                when (fromDaySpinner.selectedItemPosition) {
                    0 -> {
                        openValue = sunOpenSpinner.selectedItemPosition
                        closeValue = sunCloseSpinner.selectedItemPosition
                    }
                    1 -> {
                        openValue = monOpenSpinner.selectedItemPosition
                        closeValue = monCloseSpinner.selectedItemPosition
                    }
                    2 -> {
                        openValue = tueOpenSpinner.selectedItemPosition
                        closeValue = tueCloseSpinner.selectedItemPosition
                    }
                    3 -> {
                        openValue = wedOpenSpinner.selectedItemPosition
                        closeValue = wedCloseSpinner.selectedItemPosition
                    }
                    4 -> {
                        openValue = thuOpenSpinner.selectedItemPosition
                        closeValue = thuCloseSpinner.selectedItemPosition
                    }
                    5 -> {
                        openValue = friOpenSpinner.selectedItemPosition
                        closeValue = friCloseSpinner.selectedItemPosition
                    }
                    6 -> {
                        openValue = satOpenSpinner.selectedItemPosition
                        closeValue = satCloseSpinner.selectedItemPosition
                    }
                }
                if (toSunCB.isChecked) {
                    sunOpenSpinner.setSelection(openValue)
                    sunCloseSpinner.setSelection(closeValue)
                }
                if (toMonCB.isChecked) {
                    monOpenSpinner.setSelection(openValue)
                    monCloseSpinner.setSelection(closeValue)
                }
                if (toTueCB.isChecked) {
                    tueOpenSpinner.setSelection(openValue)
                    tueCloseSpinner.setSelection(closeValue)
                }
                if (toWedCB.isChecked) {
                    wedOpenSpinner.setSelection(openValue)
                    wedCloseSpinner.setSelection(closeValue)
                }
                if (toThuCB.isChecked) {
                    thuOpenSpinner.setSelection(openValue)
                    thuCloseSpinner.setSelection(closeValue)
                }
                if (toFriCB.isChecked) {
                    friOpenSpinner.setSelection(openValue)
                    friCloseSpinner.setSelection(closeValue)
                }
                if (toSatCB.isChecked) {
                    satOpenSpinner.setSelection(openValue)
                    satCloseSpinner.setSelection(closeValue)
                }
                exitCopyDialogeBtnId.callOnClick()
            }
            fromDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    toSunCB.isEnabled = (position!=0)
                    toMonCB.isEnabled = (position!=1)
                    toTueCB.isEnabled = (position!=2)
                    toWedCB.isEnabled = (position!=3)
                    toThuCB.isEnabled = (position!=4)
                    toFriCB.isEnabled = (position!=5)
                    toSatCB.isEnabled = (position!=6)
                }
            }

        }


        IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited = true
        (activity as FormsActivity).contactInfoButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        setServices()
        setFieldsListeners()
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()

    }

    fun refreshButtonsState(){
        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }




    fun setFieldsListeners(){



        cancelButton.setOnClickListener {
            cancelButton.hideKeyboard()
            FacilityDataModel.getInstance().tblHours[0].SunClose = FacilityDataModelOrg.getInstance().tblHours[0].SunClose
            FacilityDataModel.getInstance().tblHours[0].SunOpen = FacilityDataModelOrg.getInstance().tblHours[0].SunOpen
            FacilityDataModel.getInstance().tblHours[0].SatClose = FacilityDataModelOrg.getInstance().tblHours[0].SatClose
            FacilityDataModel.getInstance().tblHours[0].SatOpen = FacilityDataModelOrg.getInstance().tblHours[0].SatOpen
            FacilityDataModel.getInstance().tblHours[0].MonOpen= FacilityDataModelOrg.getInstance().tblHours[0].MonOpen
            FacilityDataModel.getInstance().tblHours[0].MonClose= FacilityDataModelOrg.getInstance().tblHours[0].MonClose
            FacilityDataModel.getInstance().tblHours[0].TueOpen = FacilityDataModelOrg.getInstance().tblHours[0].TueOpen
            FacilityDataModel.getInstance().tblHours[0].TueClose = FacilityDataModelOrg.getInstance().tblHours[0].TueClose
            FacilityDataModel.getInstance().tblHours[0].WedClose= FacilityDataModelOrg.getInstance().tblHours[0].WedClose
            FacilityDataModel.getInstance().tblHours[0].WedOpen= FacilityDataModelOrg.getInstance().tblHours[0].WedOpen
            FacilityDataModel.getInstance().tblHours[0].ThuOpen= FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen
            FacilityDataModel.getInstance().tblHours[0].ThuClose= FacilityDataModelOrg.getInstance().tblHours[0].ThuClose
            FacilityDataModel.getInstance().tblHours[0].FriClose= FacilityDataModelOrg.getInstance().tblHours[0].FriClose
            FacilityDataModel.getInstance().tblHours[0].FriOpen= FacilityDataModelOrg.getInstance().tblHours[0].FriOpen
            FacilityDataModel.getInstance().tblHours[0].NightDrop= FacilityDataModelOrg.getInstance().tblHours[0].NightDrop
            FacilityDataModel.getInstance().tblHours[0].NightDropInstr= FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr
            FacilityDataModel.getInstance().tblLanguage.clear()

            FacilityDataModelOrg.getInstance().tblLanguage.apply {
                (0 until size).forEach {
                    var langItem = TblLanguage()
                    langItem.LangTypeID = get(it).LangTypeID
                    FacilityDataModel.getInstance().tblLanguage.add(langItem)
                }
            }

            setServices()

            fillOpenHoursTableView()
            fillClosedHoursTableView()
            (activity as FormsActivity).saveRequired = false
            refreshButtonsState()
            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully")
        }


        sunOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!sunOpenSpinner.tag.equals(p2) || sunOpenSpinner.tag.equals("-1")) {
                    sunOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SunOpen = sunOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        sunCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!sunCloseSpinner.tag.equals(p2) || sunCloseSpinner.tag.equals("-1")) {
                    sunCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SunClose = sunCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        monCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!monCloseSpinner.tag.equals(p2) || monCloseSpinner.tag.equals("-1")) {
                    monCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].MonClose = monCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        monOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!monOpenSpinner.tag.equals(p2) || monOpenSpinner.tag.equals("-1")) {
                    monOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].MonOpen = monOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        tueCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!tueCloseSpinner.tag.equals(p2) || tueCloseSpinner.tag.equals("-1")) {
                    tueCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].TueClose = tueCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        tueOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!tueOpenSpinner.tag.equals(p2) || tueOpenSpinner.tag.equals("-1")) {
                    tueOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].TueOpen = tueOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        wedOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!wedOpenSpinner.tag.equals(p2) || wedOpenSpinner.tag.equals("-1")) {
                    wedOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].WedOpen = wedOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        wedCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!wedCloseSpinner.tag.equals(p2) || wedCloseSpinner.tag.equals("-1")) {
                    wedCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].WedClose = wedCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        thuCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!thuCloseSpinner.tag.equals(p2) || thuCloseSpinner.tag.equals("-1")) {
                    thuCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].ThuClose = thuCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        thuOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!thuOpenSpinner.tag.equals(p2) || thuOpenSpinner.tag.equals("-1")) {
                    thuOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].ThuOpen = thuOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        friOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!friOpenSpinner.tag.equals(p2) || friOpenSpinner.tag.equals("-1")) {
                    friOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].FriOpen = friOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        friCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!friCloseSpinner.tag.equals(p2) || friCloseSpinner.tag.equals("-1")) {
                    friCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].FriClose = friCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        satCloseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!satCloseSpinner.tag.equals(p2) || satCloseSpinner.tag.equals("-1")) {
                    satCloseSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SatClose = satCloseSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }
        satOpenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!satOpenSpinner.tag.equals(p2) || satOpenSpinner.tag.equals("-1")) {
                    satOpenSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblHours[0].SatOpen = satOpenSpinner.getItemAtPosition(p2).toString()
                    HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                    (activity as FormsActivity).saveRequired = true
                    refreshButtonsState()
                }
            }
        }


        facilityIsOpenEffDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenEffDateBtn!!.text = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            }, year, month, day)
            dpd.show()
        }
        facilityIsOpenExpDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenExpDateBtn!!.text = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            }, year, month, day)
            dpd.show()
        }

        exitAddEmailDialogeBtnId.setOnClickListener({
            addNewEmailDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = false
        })

        exitCopyDialogeBtnId.setOnClickListener({
            copyHoursDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = false
        })


        exitUpdateEmailDialogeBtnId.setOnClickListener({
            editEmailDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })

//        exitAddLocationDialogeBtnId.setOnClickListener({
//            addNewLocationDialog.visibility = View.GONE
//                     alphaBackgroundForDialogs.visibility = View.GONE
//            enableAllAddButnsAndDialog()
//        })

        exitEditLocationDialogeBtnId.setOnClickListener({
            editLocationDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })
        exitUpdatePhoneDialogeBtnId.setOnClickListener({
            alphaBackgroundForDialogs.visibility = View.GONE
            editPhoneDialog.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })
        exitAddPhoneDialogeBtnId.setOnClickListener({
            addNewPhoneDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
        })

//        addNewLocationButton.setOnClickListener({
//
//            disableAllAddButnsAndDialog()
//            showLocationDialog()
//        })

        addNewPhoneButton.setOnClickListener({
//            disableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = true
            showPhoneDialog()
        })

        addNewEmailButton.setOnClickListener {
//            disableAllAddButnsAndDialog()
            (activity as FormsActivity).overrideBackButton = true
            showEmailDialog()
        }

        setLocations()



        locationSubmitButton.setOnClickListener({
            // missing validation for states when the lookup is ready

            var location =TblAddress().locIsInputsValid

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

            var phoneValide=TblPhone().phoneIsInputsValid
            if (newPhoneNoText.text.isNullOrEmpty()) {
                newPhoneNoText.setError("please enter phone number")
                phoneValide=false
            } else {
                phoneValide=true
                submitFacilityPhone()
//                enableAllAddButnsAndDialog()
            }
        })

        emailSubmitButton.setOnClickListener({

            var emailValid=TblFacilityEmail().emailIsInputsValid
            if (newEmailAddrText.text.isNullOrEmpty()) {
                emailValid=false
                newEmailAddrText.setError("Required Field")
            } else if (!Utility.isEmailValid(newEmailAddrText.text.toString())) {
                Utility.showValidationAlertDialog(activity,"Please enter a valid Email address")
            }else {
                emailValid=true
                submitFacilityEmail()
//                enableAllAddButnsAndDialog()
            }
        })

        saveButton.setOnClickListener(View.OnClickListener {
            contactInfoLoadingText.text = "Saving ..."
            contactInfoLoadingView.visibility = View.VISIBLE
            submitHours()
            submitLanguages()

        })

        nightDropCheck.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblHours[0].NightDrop = b
            HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        nightDropInstText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblHours[0].NightDropInstr = p0.toString()
                HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
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

//    fun languageGridViewCLick(v : View){
//        (activity as FormsActivity).saveRequired = true
//        refreshButtonsState()
//    }

    private fun setServices() {

        langListItems.clear()

        for (model in TypeTablesModel.getInstance().LanguageType) {

            langListItems.add(model)

        }

        arrayAdapter = LanguageListAdapter(context!!, R.layout.lang_checkbox_item, this ,langListItems)

        languagesGridView?.adapter = arrayAdapter
        languagesGridView?.isExpanded=true

//        languagesGridView?.setOnClickListener({
//            (activity as FormsActivity).saveRequired = true
//            refreshButtonsState()
//        })

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


    fun getAddressChanges() : String {
        var strChanges = ""
        if (newLocLatText.text.toString() != FacilityDataModelOrg.getInstance().tblAddress[0].LATITUDE) {
            strChanges += "Lattitude changed from (" + FacilityDataModelOrg.getInstance().tblAddress[0].LATITUDE + ") to ("+newLocLatText.text.toString()+") - "
        }
        if (newLocLongText.text.toString() != FacilityDataModelOrg.getInstance().tblAddress[0].LONGITUDE) {
            strChanges += "Longitude changed from (" + FacilityDataModelOrg.getInstance().tblAddress[0].LONGITUDE + ") to ("+newLocLongText.text.toString()+") - "
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }


    fun getEmailChanges(action : Int,rowId: Int) : String {
        var strChanges = ""
        if (action==0) {
            strChanges += "New entry added as Email (" + newEmailAddrText.text.toString() + ") and type (" + newEmailTypeSpinner.selectedItem.toString() + ")"
        } else {
            if (newChangesEmailText.text.toString() != FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email) {
                strChanges += "Email changed from (" + FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email + ") to (" + newChangesEmailText.text.toString() + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }


    fun getHoursChanges() : String {
        var strChanges = ""
        val sunClose = sunCloseSpinner.selectedItem.toString()
        val monClose = monCloseSpinner.selectedItem.toString()
        val tueClose = tueCloseSpinner.selectedItem.toString()
        val wedClose = wedCloseSpinner.selectedItem.toString()
        val thuClose = thuCloseSpinner.selectedItem.toString()
        val friClose = friCloseSpinner.selectedItem.toString()
        val satClose = satCloseSpinner.selectedItem.toString()
        val sunOpen = sunOpenSpinner.selectedItem.toString()
        val monOpen = monOpenSpinner.selectedItem.toString()
        val tueOpen = tueOpenSpinner.selectedItem.toString()
        val wedOpen = wedOpenSpinner.selectedItem.toString()
        val thuOpen = thuOpenSpinner.selectedItem.toString()
        val friOpen = friOpenSpinner.selectedItem.toString()
        val satOpen = satOpenSpinner.selectedItem.toString()
        // HAVE TO HANDLE CLOSED AND OR 00:00:01
        if (sunClose != FacilityDataModelOrg.getInstance().tblHours[0].SunClose) {
            strChanges += "Sunday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SunClose + ") to (" + sunClose + ") - "
        }
        if (monClose != FacilityDataModelOrg.getInstance().tblHours[0].MonClose) {
            strChanges += "Monday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].MonClose + ") to (" + monClose + ") - "
        }
        if (tueClose != FacilityDataModelOrg.getInstance().tblHours[0].TueClose) {
            strChanges += "Tuesday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].TueClose + ") to (" + tueClose + ") - "
        }
        if (wedClose != FacilityDataModelOrg.getInstance().tblHours[0].WedClose) {
            strChanges += "Wednesday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].WedClose+ ") to (" + wedClose + ") - "
        }
        if (thuClose != FacilityDataModelOrg.getInstance().tblHours[0].ThuClose) {
            strChanges += "Thursday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].ThuClose + ") to (" + thuClose + ") - "
        }
        if (friClose != FacilityDataModelOrg.getInstance().tblHours[0].FriClose) {
            strChanges += "Friday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].FriClose + ") to (" + friClose + ") - "
        }
        if (satClose != FacilityDataModelOrg.getInstance().tblHours[0].SatClose) {
            strChanges += "Saturday closing time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SatClose + ") to (" + satClose + ") - "
        }

        if (sunOpen != FacilityDataModelOrg.getInstance().tblHours[0].SunOpen) {
            strChanges += "Sunday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SunOpen + ") to (" + sunOpen + ") - "
        }
        if (monOpen != FacilityDataModelOrg.getInstance().tblHours[0].MonOpen) {
            strChanges += "Monday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].MonOpen + ") to (" + monOpen + ") - "
        }
        if (tueOpen != FacilityDataModelOrg.getInstance().tblHours[0].TueOpen) {
            strChanges += "Tuesday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].TueOpen + ") to (" + tueOpen + ") - "
        }
        if (wedOpen != FacilityDataModelOrg.getInstance().tblHours[0].WedOpen) {
            strChanges += "Wednesday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].WedOpen + ") to (" + wedOpen + ") - "
        }
        if (thuOpen != FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen) {
            strChanges += "Thursday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen + ") to (" + thuOpen + ") - "
        }
        if (friOpen != FacilityDataModelOrg.getInstance().tblHours[0].FriOpen) {
            strChanges += "Friday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].FriOpen + ") to (" + friOpen + ") - "
        }
        if (satOpen != FacilityDataModelOrg.getInstance().tblHours[0].SatOpen) {
            strChanges += "Saturday opening time changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].SatOpen + ") to (" + satOpen + ") - "
        }
        if (nightDropCheck.isChecked != FacilityDataModelOrg.getInstance().tblHours[0].NightDrop) {
            strChanges += "Night Drop availability changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].NightDrop + ") to (" + nightDropCheck.isChecked + ") - "
        }
        if (nightDropInstText.text.toString() != FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr) {
            strChanges += "Night Drop instructions changed from (" + FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr + ") to (" + nightDropInstText.text.toString() + ") - "
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun getLanguageChanges() : String {
        var strChanges = ""
        if (FacilityDataModel.getInstance().tblLanguage.size != FacilityDataModelOrg.getInstance().tblLanguage.size) {
            strChanges += "Facility Languages changed from ("
            FacilityDataModelOrg.getInstance().tblLanguage.apply {
                (0 until size).forEach {
                    strChanges += TypeTablesModel.getInstance().LanguageType.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}[0].LangTypeName + " - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
            strChanges += ") to ("
            FacilityDataModel.getInstance().tblLanguage.apply {
                (0 until size).forEach {
                    strChanges += TypeTablesModel.getInstance().LanguageType.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}[0].LangTypeName + " - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
            strChanges += ")"
        }
        return strChanges
    }



    fun getPhoneChanges(action : Int,rowId: Int) : String {
        var strChanges = ""
        if (action==0) {
            strChanges += "New entry added as Phone Number (" + newPhoneNoText.text.toString() + ") and type (" + newPhoneTypeSpinner.selectedItem.toString() + ")"
        } else {
            if (newChangesPhoneNoText.text.toString() != FacilityDataModelOrg.getInstance().tblPhone[rowId].PhoneNumber) {
                strChanges += "Phone Number  changed from (" + FacilityDataModelOrg.getInstance().tblPhone[rowId].PhoneNumber + ") to (" + newChangesPhoneNoText.text.toString() + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }





    private fun showLocationDialog(index: Int) {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        editLocationDialog.visibility = View.VISIBLE
        (activity as FormsActivity).overrideBackButton = true

        newLocLatText.setText(FacilityDataModel.getInstance().tblAddress[index].LATITUDE)
        newLocLongText.setText(FacilityDataModel.getInstance().tblAddress[index].LONGITUDE)

        locationSubmitButton.setOnClickListener {
            if (getAddressChanges().isNullOrEmpty()) {
                contactInfoLoadingView.visibility = View.GONE
                editLocationDialog.visibility = View.GONE
                alphaBackgroundForDialogs.visibility = View.GONE
                contactInfoLoadingText.text = "Loading ..."
                (activity as FormsActivity).overrideBackButton = false
            } else {
                contactInfoLoadingText.text = "Saving ..."
                contactInfoLoadingView.visibility = View.VISIBLE
                editLocationDialog.visibility = View.GONE
                alphaBackgroundForDialogs.visibility = View.GONE
                (activity as FormsActivity).overrideBackButton = false
//            enableAllAddButnsAndDialog()
//            var rowIndex=phoneTbl.indexOfChild(tableRow)
//            var phoneFacilityChangedIndex= rowIndex-1


                val insertDate = Date().toApiSubmitFormat()
                val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val updateDate = Date().toApiSubmitFormat()
                val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val LocationTypeID = TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeName.equals("Physical") }[0].LocTypeID
                val facAddr1 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr1
                val facAddr2 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].FAC_Addr2
                val facCity = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].CITY
                val facCountry = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].County
                val facST = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ST
                val facZip = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP
                val facZip4 = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].ZIP4
                var facBranchName = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchName
                facBranchName = URLEncoder.encode(facBranchName.toString() , "UTF-8");
                val facBranchNo = FacilityDataModel.getInstance().tblAddress.filter { s -> s.LocationTypeID.equals(LocationTypeID) }[0].BranchNumber
                val Latitude = newLocLatText.text.toString()
                val Longitude = newLocLongText.text.toString()

                val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()

                val clubCode = FacilityDataModel.getInstance().clubCode
                var urlString = facilityNo + "&clubcode=" + clubCode + "&BranchName=" + facBranchName + "&LATITUDE=" + Latitude + "&LONGITUDE=" + Longitude + "&BranchNumber=" + facBranchNo + "&locationTypeID=" + LocationTypeID + "&FAC_Addr1=" + facAddr1 + "&FAC_Addr2=" + facAddr2 + "&CITY=" + facCity + "&Country=" + facCountry + "&ST=" + facST + "&ZIP=" + facZip + "&ZIP4=" + facZip4 + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&active=1"
                Log.v("Location Address --- ", Constants.submitContactInfoAddress + urlString)
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitContactInfoAddress + urlString + Utility.getLoggingParameters(activity, 0, getAddressChanges()),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<", false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Facility Location")
                                    FacilityDataModel.getInstance().tblAddress[index].LATITUDE = newLocLatText.text.toString()
                                    FacilityDataModel.getInstance().tblAddress[index].LONGITUDE = newLocLongText.text.toString()
                                    FacilityDataModelOrg.getInstance().tblAddress[index].LATITUDE = newLocLatText.text.toString()
                                    FacilityDataModelOrg.getInstance().tblAddress[index].LONGITUDE = newLocLongText.text.toString()
                                    fillLocationTableView()
                                    HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityAddress = true
                                    HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Location (Error: " + errorMessage + " )")
                                }
                                contactInfoLoadingView.visibility = View.GONE
                                contactInfoLoadingText.text = "Loading ..."
                                //                            enableAllAddButnsAndDialog()
                            }
                        }, Response.ErrorListener {

                    Utility.showSubmitAlertDialog(activity, true, "Facility Location (Error: " + it.message + " )")
                    contactInfoLoadingView.visibility = View.GONE
                    contactInfoLoadingText.text = "Loading ..."
//                        enableAllAddButnsAndDialog()
                    Log.v("error while submitting", "LOCATION Details")
                }))
            }
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

        if (nightDropCheck.isChecked && nightDropInstText.text.isNullOrEmpty()){
            isInputsValid = false
            nightDropInstText.setError("Required Field")
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
//        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 1F
//        rowLayoutParam.column = 0
//      //  rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        if (phoneTbl.childCount>1) {
            for (i in phoneTbl.childCount - 1 downTo 1) {
                phoneTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin=10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblPhone.apply {
            (0 until size).forEach {
                if (!get(it).PhoneID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
//                    textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.minimumHeight=30
                    textView.text = getPhoneTypeName(get(it).PhoneTypeID)
                    tableRow.addView(textView)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam1
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 14f
                    textView2.minimumHeight=30
                    textView2.text = get(it).PhoneNumber
                    tableRow.addView(textView2)

//                    val editPhoneBtn = Button(context)
//                    editPhoneBtn.layoutParams = rowLayoutParam2
////                    editPhoneBtn.textAlignment = Button.TEXT_ALIGNMENT_TEXT_START
//                    textView.gravity = Gravity.CENTER_VERTICAL
//                    textView.textSize = 18f
//                    editPhoneBtn.text = "Edit"
//                    editPhoneBtn.setTextColor(Color.BLACK)
////                editPhoneBtn.setBackgroundResource(R.drawable.green_background_button)
//                    tableRow.addView(editPhoneBtn)

                    val editPhoneBtn = TextView(context)
                    editPhoneBtn.layoutParams = rowLayoutParam1
                    editPhoneBtn.setTextColor(Color.BLUE)
                    editPhoneBtn.text = "EDIT"
                    editPhoneBtn.textSize = 14f
                    editPhoneBtn.minimumHeight=30
                    editPhoneBtn.gravity = Gravity.CENTER
                    editPhoneBtn.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(editPhoneBtn)

                    editPhoneBtn.setOnClickListener {
                        var rowIndex = phoneTbl.indexOfChild(tableRow)
                        var phoneFacilityChangedIndex = rowIndex - 1
                        newChangesPhoneNoText.text.clear()
                        alphaBackgroundForDialogs.visibility = View.VISIBLE
                        editPhoneDialog.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
                        phoneTypeArray.clear()
                        for (fac in phoneTypeList) {
                            phoneTypeArray.add(fac.LocPhoneName)
                        }


                        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
                        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newPhoneTypeSpinner.adapter = phoneTypeAdapter

                        newChangesPhoneNoText.setText(FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber)

                        phoneSaveChangesButton.setOnClickListener {
                            var phoneTypeID = ""
                            if (newChangesPhoneNoText.text.isNullOrEmpty()) {
                                newChangesPhoneNoText.setError("please enter required field")
                            } else {
                                val phoneNo = newChangesPhoneNoText.text.toString()
                                for (phoneTypeTableId in TypeTablesModel.getInstance().LocationPhoneType) {
                                    if (phoneTypeTableId.LocPhoneName == textView.text.toString()) {
                                        phoneTypeID = phoneTypeTableId.LocPhoneID.toString()
                                    }
                                }
                                val insertDate = Date().toApiSubmitFormat()
                                val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                                val updateDate = Date().toApiSubmitFormat()
                                val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                                val activeVal = "0"
                                val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                                val clubCode = FacilityDataModel.getInstance().clubCode
                                var urlString = facilityNo + "&clubCode=" + clubCode + "&phoneTypeId=" + phoneTypeID + "&phoneNumber=" + phoneNo + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&extension=&description=&phoneId=${FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneID}&active=1"
                                Log.v("Data To Submit", urlString)
                                contactInfoLoadingText.text = "Saving ..."
                                contactInfoLoadingView.visibility = View.VISIBLE
                                editPhoneDialog.visibility = View.GONE
                                alphaBackgroundForDialogs.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                Log.v("Phone Edit --- ",Constants.submitFacilityPhone + urlString)
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString+ Utility.getLoggingParameters(activity, 0, getPhoneChanges(1,phoneFacilityChangedIndex)),
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Phone")
                                                    FacilityDataModel.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber = newChangesPhoneNoText.text.toString()
                                                    FacilityDataModelOrg.getInstance().tblPhone[phoneFacilityChangedIndex].PhoneNumber = newChangesPhoneNoText.text.toString()
                                                    fillPhoneTableView()
                                                    checkIfChangeDone("PHONE")
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+errorMessage+" )")
                                                }
                                                contactInfoLoadingView.visibility = View.GONE
                                                contactInfoLoadingText.text = "Loading ..."
                                            }
                                        }, Response.ErrorListener {
                                    contactInfoLoadingView.visibility = View.GONE
                                    contactInfoLoadingText.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+it.message+" )")
                                    Log.v("error while submitting", "Phone Details")
                                }))


                            }
                        }

                    }
                    phoneTbl.addView(tableRow)
                }
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
        rowLayoutParam.marginStart =10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                if (!get(it).emailID.equals("-1")) {
                    var tableRow = TableRow(context)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.minimumHeight = 30
                    textView.text = getEmailTypeName(get(it).emailTypeId)
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.text = get(it).email
                    textView.minimumHeight = 30
                    textView.textSize = 14f
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.setTextColor(Color.BLUE)
                    textView.text = "EDIT"
                    textView.textSize = 14f
                    textView.minimumHeight = 30
                    textView.gravity = Gravity.CENTER
                    textView.setBackgroundColor(Color.TRANSPARENT)


                    textView.setOnClickListener {
                        var rowIndex = emailTbl.indexOfChild(tableRow)
                        var emailFacilityChangedIndex = rowIndex - 1
                        newChangesEmailText.text.clear()
                        alphaBackgroundForDialogs.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        editEmailDialog.visibility = View.VISIBLE
                        emailTypeList = TypeTablesModel.getInstance().EmailType
                        emailTypeArray.clear()
                        for (fac in emailTypeList) {
                            emailTypeArray.add(fac.EmailName)
                        }

                        var emailTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, emailTypeArray)
                        emailTypeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        newEmailTypeSpinner.adapter = emailTypeAdapter

                        newChangesEmailText.setText(FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email)

                        emailSaveChangesButton.setOnClickListener {

                            var emailTypeID = ""
                            if (newChangesEmailText.text.isNullOrEmpty()) {
                                newChangesEmailText.setError("please enter required field")
                            } else if (!Utility.isEmailValid(newChangesEmailText.text.toString())) {
                                Utility.showValidationAlertDialog(activity,"Please enter a valid Email address")
                            } else {
                                val emailAddress = newChangesEmailText.text.toString()
                                for (emailTypeTableId in TypeTablesModel.getInstance().EmailType) {
                                    if (emailTypeTableId.EmailName == textView.text.toString()) {
                                        emailTypeID = emailTypeTableId.EmailID.toString()
                                    }
                                }
                                val insertDate = Date().toApiSubmitFormat()
                                val insertBy = "sa"
                                val updateDate = Date().toApiSubmitFormat()
                                val updateBy = "sa"
                                val activeVal = "0"

                                val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()

                                val clubCode = FacilityDataModel.getInstance().clubCode
                                var urlString = facilityNo + "&clubcode=" + clubCode + "&emailTypeId=" + FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].emailTypeId + "&email=" + emailAddress+ "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&extension=&description=&emailId=${FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].emailID}&active=1"
                                Log.v("Data To Submit", urlString)
                                contactInfoLoadingText.text = "Saving ..."
                                contactInfoLoadingView.visibility = View.VISIBLE
                                editEmailDialog.visibility = View.GONE
                                alphaBackgroundForDialogs.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                Log.v("Email Edit --- ",Constants.submitFacilityEmail + urlString)
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityEmail + urlString+ Utility.getLoggingParameters(activity, 0, getEmailChanges(1,emailFacilityChangedIndex)),
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Email")
                                                    FacilityDataModel.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email = newChangesEmailText.text.toString()
                                                    FacilityDataModelOrg.getInstance().tblFacilityEmail[emailFacilityChangedIndex].email = newChangesEmailText.text.toString()
                                                    fillEmailTableView()
                                                    checkIfChangeDone("EMAIL")
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+errorMessage+" )")
                                                }
                                                contactInfoLoadingView.visibility = View.GONE
                                                contactInfoLoadingText.text = "Loading ..."
                                            }
                                        }, Response.ErrorListener {
                                    contactInfoLoadingView.visibility = View.GONE
                                    contactInfoLoadingText.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+it.message+" )")
                                }))


                            }
                        }

                    }

                    tableRow.addView(textView)
                    emailTbl.addView(tableRow)
                }
            }

        }
        altEmailTableRow(2)

    }

    fun checkIfChangeDone(strType : String)  {
        var changeWasDone = false
        if (strType.equals("EMAIL")){
            for (i in 0 .. FacilityDataModel.getInstance().tblFacilityEmail.size-1) {
                if (!FacilityDataModel.getInstance().tblFacilityEmail[i].email.equals(FacilityDataModelOrg.getInstance().tblFacilityEmail[i].email)) changeWasDone=true
            }
            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityEmail = changeWasDone
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
        }
        if (strType.equals("PHONE")){
            for (i in 0 .. FacilityDataModel.getInstance().tblPhone.size-1) {
                if (!FacilityDataModel.getInstance().tblPhone[i].PhoneNumber.equals(FacilityDataModelOrg.getInstance().tblPhone[i].PhoneNumber)) changeWasDone=true
            }
            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityPhone = changeWasDone
            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
        }
    }


    fun fillOpenHoursTableView() {


        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                sunOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SunOpen.isNullOrEmpty()) "Closed" else get(it).SunOpen))
                sunOpenSpinner.tag = sunOpenSpinner.selectedItemPosition
                monOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).MonOpen.isNullOrEmpty()) "Closed" else get(it).MonOpen))
                monOpenSpinner.tag = monOpenSpinner.selectedItemPosition
                tueOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).TueOpen.isNullOrEmpty()) "Closed" else get(it).TueOpen))
                tueOpenSpinner.tag = tueOpenSpinner.selectedItemPosition
                wedOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).WedOpen.isNullOrEmpty()) "Closed" else get(it).WedOpen))
                wedOpenSpinner.tag = wedOpenSpinner.selectedItemPosition
                thuOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).ThuOpen.isNullOrEmpty()) "Closed" else get(it).ThuOpen))
                thuOpenSpinner.tag = thuOpenSpinner.selectedItemPosition
                friOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).FriOpen.isNullOrEmpty()) "Closed" else get(it).FriOpen))
                friOpenSpinner.tag = friOpenSpinner.selectedItemPosition
                satOpenSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SatOpen.isNullOrEmpty()) "Closed" else get(it).SatOpen))
                satOpenSpinner.tag = satOpenSpinner.selectedItemPosition
                nightDropCheck.isChecked = get(it).NightDrop
                nightDropInstText.setText(get(it).NightDropInstr)

            }
        }
    }
    fun fillClosedHoursTableView() {

        FacilityDataModel.getInstance().tblHours.apply {
            (0 until size).forEach {
                sunCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SunClose.isNullOrEmpty()) "Closed" else get(it).SunClose))
                sunCloseSpinner.tag = sunCloseSpinner.selectedItemPosition
                monCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).MonClose.isNullOrEmpty()) "Closed" else get(it).MonClose))
                monCloseSpinner.tag = monCloseSpinner.selectedItemPosition
                tueCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).TueClose.isNullOrEmpty()) "Closed" else get(it).TueClose))
                tueCloseSpinner.tag = tueCloseSpinner.selectedItemPosition
                wedCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).WedClose.isNullOrEmpty()) "Closed" else get(it).WedClose))
                wedCloseSpinner.tag = wedCloseSpinner.selectedItemPosition
                thuCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).ThuClose.isNullOrEmpty()) "Closed" else get(it).ThuClose))
                thuCloseSpinner.tag = thuCloseSpinner.selectedItemPosition
                friCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).FriClose.isNullOrEmpty()) "Closed" else get(it).FriClose))
                friCloseSpinner.tag = friCloseSpinner.selectedItemPosition
                satCloseSpinner.setSelection(hoursArray!!.indexOf(if (get(it).SatClose.isNullOrEmpty()) "Closed" else get(it).SatClose))
                satCloseSpinner.tag = satCloseSpinner.selectedItemPosition


            }
        }
    }


    fun fillLocationTableView() {

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 0.8F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.leftMargin = 2
        rowLayoutParam.gravity = Gravity.CENTER

        if (locationTbl.childCount>1) {
            for (i in locationTbl.childCount - 1 downTo 1) {
                locationTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.4F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.4F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.8F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0
        rowLayoutParam7.gravity = Gravity.CENTER

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.column = 8
        rowLayoutParam8.width = 0
        rowLayoutParam8.gravity = Gravity.CENTER

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.column = 9
        rowLayoutParam9.width = 0
        rowLayoutParam9.gravity = Gravity.CENTER

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.column = 10
        rowLayoutParam10.width = 0
        rowLayoutParam10.gravity = Gravity.CENTER

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 0.8F
        rowLayoutParam11.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.column = 11
        rowLayoutParam11.width = 0
        rowLayoutParam11.gravity = Gravity.CENTER

        var dateTobeFormated = ""

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F


        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)
                tableRow.layoutParams = rowLayoutParamRow
                tableRow.minimumHeight = 30


                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.text = getLocationTypeName(get(it).LocationTypeID)
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.setEms(8)
                textView.text = get(it).FAC_Addr1
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.setEms(8)
                textView.textSize = 10f
                textView.text = get(it).FAC_Addr2
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minimumHeight=30
                textView.text = get(it).CITY
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).County
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam5
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).ST
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam6
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).ZIP + "-" + get(it).ZIP4
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam7
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).LATITUDE
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam8
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).LONGITUDE
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam9
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).BranchNumber
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam10
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).BranchName
                textView.minimumHeight=30
                textView.textSize = 10f
                tableRow.addView(textView)

                var editButton = TextView(context)
                editButton.layoutParams = rowLayoutParam11
                editButton.setTextColor(Color.BLUE)
                editButton.minimumHeight=30
                editButton.text = "EDIT"
                editButton.textSize = 12f
                editButton .gravity = Gravity.CENTER
                editButton .setBackgroundColor(Color.TRANSPARENT)
                editButton.tag = it

                tableRow.addView(editButton)
                if (!getLocationTypeName(get(it).LocationTypeID).equals("Physical")){
                    editButton.visibility = View.INVISIBLE
                }

                editButton.setOnClickListener { s ->
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
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode

        val newEmail = TblFacilityEmail()
        newEmail.email = email.toString()
        newEmail.emailTypeId = emailTypeID
//        var seqNo = FacilityDataModel.getInstance().tblFacilityEmail.size+1
        var urlString = facilityNo+"&clubcode="+clubCode+"&emailTypeId="+emailTypeID+"&email="+email+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&emailId="
        Log.v("Data To Submit", urlString)
        contactInfoLoadingText.text = "Saving ..."
        contactInfoLoadingView.visibility = View.VISIBLE
        addNewEmailDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
        (activity as FormsActivity).overrideBackButton = false
        Log.v("Email ADD --- ",Constants.submitFacilityEmail + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityEmail + urlString + Utility.getLoggingParameters(activity, 0, getEmailChanges(0,0)),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode>0<",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Facility Email")
                            if (FacilityDataModel.getInstance().tblFacilityEmail.size==1 && FacilityDataModel.getInstance().tblFacilityEmail[0].emailID.equals("-1")){
                                FacilityDataModel.getInstance().tblFacilityEmail.removeAt(0)
                                FacilityDataModelOrg.getInstance().tblFacilityEmail.removeAt(0)
                            }
                            newEmail.emailID = response.toString().substring(response.toString().indexOf("<emailID")+8,response.toString().indexOf("</emailID"))
                            FacilityDataModel.getInstance().tblFacilityEmail.add(newEmail)
                            FacilityDataModelOrg.getInstance().tblFacilityEmail.add(newEmail)
                            fillEmailTableView()
                            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityEmail= true
                            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity, false, "Facility Email (Error: "+errorMessage+" )")
                        }
                        contactInfoLoadingView.visibility = View.GONE
                        contactInfoLoadingText.text = "Loading ..."

                    }
                }, Response.ErrorListener {
                    contactInfoLoadingView.visibility = View.GONE
            contactInfoLoadingText.text = "Loading ..."

            Utility.showSubmitAlertDialog(activity,false,"Facility Email (Error: "+it.message+" )")
        }))
    }

    fun submitHours(){

        val nightDrop= if (nightDropCheck.isChecked) "1" else "0"
        val nightDropInstructions=nightDropInstText.text
        val sunClose = if (sunCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else sunCloseSpinner.selectedItem.toString()
        val monClose = if (monCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else monCloseSpinner.selectedItem.toString()
        val tueClose = if (tueCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else tueCloseSpinner.selectedItem.toString()
        val wedClose = if (wedCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else wedCloseSpinner.selectedItem.toString()
        val thuClose = if (thuCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else thuCloseSpinner.selectedItem.toString()
        val friClose = if (friCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else friCloseSpinner.selectedItem.toString()
        val satClose = if (satCloseSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else satCloseSpinner.selectedItem.toString()
        val sunOpen = if (sunOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else sunOpenSpinner.selectedItem.toString()
        val monOpen = if (monOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else monOpenSpinner.selectedItem.toString()
        val tueOpen = if (tueOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else tueOpenSpinner.selectedItem.toString()
        val wedOpen = if (wedOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else wedOpenSpinner.selectedItem.toString()
        val thuOpen = if (thuOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else thuOpenSpinner.selectedItem.toString()
        val friOpen = if (friOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else friOpenSpinner.selectedItem.toString()
        val satOpen = if (satOpenSpinner.selectedItem.toString().equals("Closed")) "00:00:01 AM" else satOpenSpinner.selectedItem.toString()
        val facAvail = "1"

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityHours + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&monOpen=${monOpen}&tueOpen=${tueOpen}&wedOpen=${wedOpen}&thuOpen=${thuOpen}" +
                "&friOpen=${friOpen}&satOpen=${satOpen}&sunOpen=${sunOpen}&monClose=${monClose}&tueClose=${tueClose}&wedClose=${wedClose}&thuClose=${thuClose}&friClose=${friClose}" +
                "&satClose=${satClose}&sunClose=${sunClose}&nightDrop=${nightDrop}&nightDropInstr=${nightDropInstructions}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+
                "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}&facAvailability=${facAvail}&availEffDate=${Date().toApiSubmitFormat()}&availExpDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 0, getHoursChanges()),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode>0<",false)) {
                            FacilityDataModel.getInstance().tblHours[0].MonClose = monClose
                            FacilityDataModel.getInstance().tblHours[0].SunClose = sunClose
                            FacilityDataModel.getInstance().tblHours[0].SatClose = satClose
                            FacilityDataModel.getInstance().tblHours[0].FriClose = friClose
                            FacilityDataModel.getInstance().tblHours[0].ThuClose = thuClose
                            FacilityDataModel.getInstance().tblHours[0].WedClose = wedClose
                            FacilityDataModel.getInstance().tblHours[0].TueClose = tueClose
                            FacilityDataModel.getInstance().tblHours[0].MonOpen = monOpen
                            FacilityDataModel.getInstance().tblHours[0].SunOpen = sunOpen
                            FacilityDataModel.getInstance().tblHours[0].SatOpen = satOpen
                            FacilityDataModel.getInstance().tblHours[0].FriOpen = friOpen
                            FacilityDataModel.getInstance().tblHours[0].ThuOpen = thuOpen
                            FacilityDataModel.getInstance().tblHours[0].WedOpen = wedOpen
                            FacilityDataModel.getInstance().tblHours[0].TueOpen = tueOpen
                            FacilityDataModel.getInstance().tblHours[0].NightDrop= nightDropCheck.isChecked
                            FacilityDataModel.getInstance().tblHours[0].NightDropInstr = nightDropInstText.text.toString()
                            FacilityDataModelOrg.getInstance().tblHours[0].MonClose = monClose
                            FacilityDataModelOrg.getInstance().tblHours[0].SunClose = sunClose
                            FacilityDataModelOrg.getInstance().tblHours[0].SatClose = satClose
                            FacilityDataModelOrg.getInstance().tblHours[0].FriClose = friClose
                            FacilityDataModelOrg.getInstance().tblHours[0].ThuClose = thuClose
                            FacilityDataModelOrg.getInstance().tblHours[0].WedClose = wedClose
                            FacilityDataModelOrg.getInstance().tblHours[0].TueClose = tueClose
                            FacilityDataModelOrg.getInstance().tblHours[0].MonOpen = monOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].SunOpen = sunOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].SatOpen = satOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].FriOpen = friOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].ThuOpen = thuOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].WedOpen = wedOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].TueOpen = tueOpen
                            FacilityDataModelOrg.getInstance().tblHours[0].NightDrop= nightDropCheck.isChecked
                            FacilityDataModelOrg.getInstance().tblHours[0].NightDropInstr = nightDropInstText.text.toString()
                            (activity as FormsActivity).saveRequired = false
                            HasChangedModel.getInstance().checkGeneralInfoTblHoursChange()
                            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                            refreshButtonsState()
                            Utility.showSubmitAlertDialog(activity, true, "Facility Hours / Nigh Drop")
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity, false, "Facility Hours / Night Drop (Error: "+errorMessage+" )")
                        }
                    }
                }, Response.ErrorListener {
            Log.v("error while loading", "error submitting hours")
            Utility.showSubmitAlertDialog(activity,false,"Facility Hours / Night Drop (Error: "+it.message+" )")
        }))

    }

    fun submitLanguages(){
        var langTypeId=""
        try {
            for (i in 0..LanguageListAdapter.langArray.size-1) {
                langTypeId += LanguageListAdapter.langArray[i].LangTypeID + ","
            }
            langTypeId=langTypeId.dropLast(1)
        } catch (e: Exception){
            Log.v("ERROR --- >",e.message)
        }
        Log.v("LANGUAGES --- ",UpdateFacilityLanguageData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubcode=${FacilityDataModel.getInstance().clubCode}&langTypeId=${langTypeId}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat())
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityLanguageData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&langTypeId=${langTypeId}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 0, getLanguageChanges()),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode>0<",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Facility Languages")
                            (activity as FormsActivity).saveRequired = false
                            FacilityDataModelOrg.getInstance().tblLanguage.clear()
                            FacilityDataModel.getInstance().tblLanguage.apply {
                                (0 until size).forEach {
                                    var langItem = TblLanguage()
                                    langItem.LangTypeID = get(it).LangTypeID
                                    FacilityDataModelOrg.getInstance().tblLanguage.add(langItem)
                                }
                            }
                            HasChangedModel.getInstance().checkGeneralInfoTblLanguagesChange()
                            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                            refreshButtonsState()
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity, false, "Facility Languages (Error: "+errorMessage+" )")
                        }

                        contactInfoLoadingView.visibility = View.GONE
                        contactInfoLoadingText.text = "Loading ..."
                    }
                }, Response.ErrorListener {
                Utility.showSubmitAlertDialog(activity, false, "Facility Languages (Error: "+it.message+" )")
            contactInfoLoadingView.visibility = View.GONE
            contactInfoLoadingText.text = "Loading ..."
        }))
    }

    fun submitFacilityPhone(){
        val phoneTypeID = TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneName==newPhoneTypeSpinner.selectedItem.toString()}[0].LocPhoneID
        val phoneNo = if (newPhoneNoText.text.isNullOrEmpty())  "" else newPhoneNoText.text
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val newPhone = TblPhone()
        newPhone.PhoneNumber = phoneNo.toString()
        newPhone.PhoneTypeID= phoneTypeID
//        var seqNo = FacilityDataModel.getInstance().tblPhone.size+1
        var urlString = facilityNo+"&clubCode="+clubCode+"&phoneTypeId="+phoneTypeID+"&phoneNumber="+phoneNo+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&extension=&description=&phoneId=&active=1"
        Log.v("Data To Submit", urlString)
        contactInfoLoadingText.text = "Saving ..."
        contactInfoLoadingView.visibility = View.VISIBLE
        addNewPhoneDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
        (activity as FormsActivity).overrideBackButton = false
        Log.v("PHONE ADD --- ",Constants.submitFacilityPhone + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityPhone + urlString+ Utility.getLoggingParameters(activity, 0, getPhoneChanges(0,0)),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode>0<", false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Facility Phone")
                            if (FacilityDataModel.getInstance().tblPhone.size==1 && FacilityDataModel.getInstance().tblPhone[0].PhoneID.equals("-1")){
                                FacilityDataModel.getInstance().tblPhone.removeAt(0)
                                FacilityDataModelOrg.getInstance().tblPhone.removeAt(0)
                            }
                            newPhone.PhoneID = response.toString().substring(response.toString().indexOf("<PhoneID")+9,response.toString().indexOf("</PhoneID"))
                            FacilityDataModel.getInstance().tblPhone.add(newPhone)
                            HasChangedModel.getInstance().groupFacilityContactInfo[0].FacilityPhone = true
                            HasChangedModel.getInstance().changeDoneForFacilityContactInfo()
                            fillPhoneTableView()
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+errorMessage+" )")
                        }
                        contactInfoLoadingView.visibility = View.GONE
                        contactInfoLoadingText.text = "Loading ..."
                    }
                }, Response.ErrorListener {
            contactInfoLoadingView.visibility = View.GONE
            contactInfoLoadingText.text = "Loading ..."
            Utility.showSubmitAlertDialog(activity, false, "Facility Phone (Error: "+it.message+" )")
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
