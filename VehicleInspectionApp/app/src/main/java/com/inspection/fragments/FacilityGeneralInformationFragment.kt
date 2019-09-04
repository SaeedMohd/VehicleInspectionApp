package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
//import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Debug
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.R.id.terminationReason_textviewVal
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdatePaymentMethodsData
import com.inspection.imageloader.Utils
import com.inspection.model.*
import kotlinx.android.synthetic.main.app_bar_forms.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import org.w3c.dom.CDATASection
import org.w3c.dom.CharacterData
import org.w3c.dom.Text
import java.net.URI
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FacilityGeneralInformationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FacilityGeneralInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FacilityGeneralInformationFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility, container, false)
    }

    private var submitPaymentRequired = false
    private var submitGeneralInfoRequired = false
    private var contractTypeList = ArrayList<TypeTablesModel.contractType>()
    private var contractTypeArray = ArrayList<String>()

    private var timeZoneList = ArrayList<TypeTablesModel.timezoneType>()
    private var timeZoneArray = ArrayList<String>()

    private var svcAvailabilityList = ArrayList<TypeTablesModel.serviceAvailabilityType>()
    private var svcAvailabilityArray = ArrayList<String>()

    private var facTypeList = ArrayList<TypeTablesModel.facilityType>()
    private var facTypeArray = ArrayList<String>()

    private var busTypeList = ArrayList<TypeTablesModel.businessType>()
    private var busTypeArray = ArrayList<String>()

    private var termReasonList = ArrayList<TypeTablesModel.terminationCodeType>()
    private var termReasonArray = ArrayList<String>()
    private var inspectionMonths= arrayOf("jan","feb","march","april","may","june","july","aug","sep","oct","nov","dec")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshButtonsState()

        termReasonList = TypeTablesModel.getInstance().TerminationCodeType
        termReasonArray .clear()
        for (fac in termReasonList) {
            termReasonArray .add(fac.TerminationCodeName)
        }

        var termReasonAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, termReasonArray)
        termReasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        terminationReason_textviewVal.adapter = termReasonAdapter

        busTypeList = TypeTablesModel.getInstance().BusinessType
        busTypeArray .clear()
        for (fac in busTypeList) {
            busTypeArray .add(fac.BusTypeName)
        }

        var busTypeAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, busTypeArray)
        busTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bustype_textviewVal.adapter = busTypeAdapter


        timeZoneList = TypeTablesModel.getInstance().TimezoneType
        timeZoneArray .clear()
        for (fac in timeZoneList) {
            timeZoneArray .add(fac.TimezoneName)
        }

        var tzdataAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, timeZoneArray)
        tzdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeZoneSpinner.adapter = tzdataAdapter

        svcAvailabilityList = TypeTablesModel.getInstance().ServiceAvailabilityType
        svcAvailabilityArray .clear()
        for (fac in svcAvailabilityList) {
            svcAvailabilityArray .add(fac.SrvAvaName)
        }
        var svcAvldataAdapter = ArrayAdapter<String>(activity, R.layout.spinner_item, svcAvailabilityArray)
        svcAvldataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_textviewVal.adapter = svcAvldataAdapter

        facTypeList = TypeTablesModel.getInstance().FacilityType
        facTypeArray .clear()
        for (fac in facTypeList) {
            facTypeArray .add(fac.FacilityTypeName)
        }
        var facilityTypedataAdapter = ArrayAdapter<String>(activity, R.layout.spinner_item, facTypeArray)
        facilityTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilitytype_textviewVal.adapter = facilityTypedataAdapter


        contractTypeList = TypeTablesModel.getInstance().ContractType
        contractTypeArray .clear()
        for (fac in contractTypeList) {
            contractTypeArray .add(fac.ContractTypeName)
        }

        var contractTypesAdapter = ArrayAdapter<String>(activity, R.layout.spinner_item, contractTypeArray )
        contractTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractTypeValueSpinner.adapter = contractTypesAdapter

        IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited = true
        (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()



        setFieldsValues()
        ImplementBusinessRules()
        setFieldsListeners()


    }


    private fun setFieldsValues() {


        FacilityDataModel.getInstance().apply {
            contractStatusTextViewVal.text = TypeTablesModel.getInstance().FacilityStatusType.filter { s -> s.FacilityStatusID .toInt() == tblFacilities[0].ContractTypeID }[0].FacilityStatusName
            contractStatusTextViewVal.setTextColor(Color.BLUE)

                        for (provider in tblFacilityServiceProvider) {
                            when(provider.SrvProviderId){
                                "AAR" -> {
                                    aarCheckBox.isChecked = true
                                    aarEditText.setText(""+provider.ProviderNum)
                                }
                                "AABِ" -> {
                                    aabCheckBox.isChecked = true
                                    aabEditText.setText(provider.ProviderNum)
                                }
                                "ِAAG" -> {
                                    aagCheckBox.isChecked = true
                                    aagEditText.setText(provider.ProviderNum)
                                }
                                "COG" -> {
                                    cogCheckBox.isChecked = true
                                    cogEditText.setText(provider.ProviderNum)
                                }
                                "CCR" -> {
                                    corCheckBox.isChecked = true
                                    corEditText.setText(provider.ProviderNum)
                                }
                                "ERS" -> {
                                    ersCheckBox.isChecked = true
                                    ersEditText.setText(provider.ProviderNum)
                                }
                                "MPR" -> {
                                    mprCheckBox.isChecked = true
                                    mprEditText.setText(provider.ProviderNum)
                                }
                                "PSP" -> {
                                    pspCheckBox.isChecked = true
                                    pspEditText.setText(provider.ProviderNum)
                                }

                            }
                        }



                for(contractType in tblContractType){
                    for (typeReference in contractTypeArray){
                        if(contractType.ContractTypeName == typeReference){
                            contractTypeValueSpinner.setSelection(contractTypeArray.indexOf(typeReference))
                        }
                    }
                }

                contract_number_textviewVal.text = "" + tblFacilities[0].FACNo


            office_textviewVal.text = if (tblOfficeType[0].OfficeName.equals("")) "" else tblOfficeType[0].OfficeName

                assignedto_textviewVal.text = tblFacilities[0].AssignedTo
                dba_textviewVal.text = tblFacilities[0].BusinessName
                entity_textviewVal.text = tblFacilities[0].EntityName
                if (tblBusinessType.size>0) {
                    bustype_textviewVal.setSelection(busTypeArray.indexOf(tblFacilities[0].BusTypeID.toString()))
                } else {
                    bustype_textviewVal.setSelection(0)
                }
                if (tblTimezoneType.size>0) {
                    timeZoneSpinner.setSelection(timeZoneArray.indexOf(tblTimezoneType[0].TimezoneName))
                } else {
                    timeZoneSpinner.setSelection(0)
                }
                timeZoneSpinner.tag = timeZoneSpinner.selectedItemPosition
                website_textviewVal.setText(tblFacilities[0].WebSite)
                wifiAvailableCheckBox.isChecked = tblFacilities[0].InternetAccess
                taxno_textviewVal.text = tblFacilities[0].TaxIDNumber
                repairorder_textviewVal.setText("" + tblFacilities[0].FacilityRepairOrderCount)
                if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==tblFacilities[0].SvcAvailability}.size > 0) {
                    availability_textviewVal.setSelection(svcAvailabilityArray.indexOf(TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == tblFacilities[0].SvcAvailability }[0].SrvAvaName))
                } else {
                    availability_textviewVal.setSelection(0)
                }
                availability_textviewVal.tag = availability_textviewVal.selectedItemPosition
                if (tblFacilityType.size>0) {
                    facilitytype_textviewVal.setSelection(facTypeArray.indexOf(tblFacilityType[0].FacilityTypeName))
                } else {
                    facilitytype_textviewVal.setSelection(0)
                }
                facilitytype_textviewVal.tag = facilitytype_textviewVal.selectedItemPosition
                ARDno_textviewVal.setText(tblFacilities[0].AutomotiveRepairNumber)
                ARDexp_textviewVal.text = tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY()
                terminationDateButton.text = ""+tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY()
                //SAEED
                terminationCommentEditText.setText(""+tblFacilities[0].TerminationComments)
                if (!tblFacilities[0].TerminationDate.equals("")) {
                    terminationReason_textviewVal.setSelection(termReasonArray.indexOf(tblTerminationCodeType[0].TerminationCodeName))
                } else
                    terminationReason_textviewVal.visibility=View.GONE

                currcodate_textviewVal.text = tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY()
                initcodate_textviewVal.text = tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY()
                InsuranceExpDate_textviewVal.text = tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY()
                inspectionCycleTextViewVal.text = if (tblFacilities[0].InspectionCycle.isNullOrEmpty()) "" else tblFacilities[0].InspectionCycle
                inspectionMonthsTextViewVal.text = tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName()
                manager_textviewVal.text = if (tblFacilityManagers.isNotEmpty()) tblFacilityManagers[0].Manager else ""
                admin_textviewVal.text = tblFacilities[0].AdminAssistants

//                if (tblFacilities[0].FacilityAnnualInspectionMonth>0) {
//                    if (inspectionMonthsTextViewVal.text==inspectionMonths[0]||inspectionMonthsTextViewVal.text==inspectionMonths[3]||inspectionMonthsTextViewVal.text==inspectionMonths[6]||inspectionMonthsTextViewVal.text==inspectionMonths[9]){
//                        inspectionCycleTextViewVal.text="1"
//                    }
//                    if (inspectionMonthsTextViewVal.text==inspectionMonths[1]||inspectionMonthsTextViewVal.text==inspectionMonths[4]||inspectionMonthsTextViewVal.text==inspectionMonths[7]||inspectionMonthsTextViewVal.text==inspectionMonths[10]){
//
//                        inspectionCycleTextViewVal.text="2"
//                    }
//                    if (inspectionMonthsTextViewVal.text==inspectionMonths[2]||inspectionMonthsTextViewVal.text==inspectionMonths[5]||inspectionMonthsTextViewVal.text==inspectionMonths[8]||inspectionMonthsTextViewVal.text==inspectionMonths[11]){
//
//                        inspectionCycleTextViewVal.text="3"

//                    }
//                } else {
//                    inspectionMonthsTextViewVal.text = ""
//                    inspectionCycleTextViewVal.text = ""
//                }



                for(paymentMethod in tblPaymentMethods){

                }
        }

        if (arguments!!.getBoolean(isValidating)) {
            validateInputs()
        }

        setPaymentMethods()


        saveButton.setOnClickListener {
            if (validateInputs()) {
                if (submitPaymentRequired) submitPaymentMethods()
                if (submitGeneralInfoRequired) submitFacilityGeneralInfo()
            }   else {
                Utility.showValidationAlertDialog(activity,"Please fill all required fields")
            }
        }

        cancelButton.setOnClickListener {
            cancelButton.hideKeyboard()
            FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount
            FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName
            FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount
            FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability
            FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate = FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate
            FacilityDataModel.getInstance().tblFacilities[0].WebSite = FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite
            FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess
            FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName
            FacilityDataModel.getInstance().tblPaymentMethods.clear()
            for (i in 0..FacilityDataModelOrg.getInstance().tblPaymentMethods.size-1) {
                var payMethod = TblPaymentMethods()
                payMethod.PmtMethodID = FacilityDataModelOrg.getInstance().tblPaymentMethods[i].PmtMethodID
                FacilityDataModel.getInstance().tblPaymentMethods.add(payMethod)
            }

            setFieldsValues()
            (activity as FormsActivity).saveRequired = false
            refreshButtonsState()
            submitGeneralInfoRequired=false
            submitPaymentRequired=false
            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
        }
    }


    fun ImplementBusinessRules() {
        activeRadioButton.isClickable = false
        inActiveRadioButton.isClickable = false
        activeRadioButton.isEnabled = false
        inActiveRadioButton.isEnabled = false
        contractTypeValueSpinner.isEnabled = false
        aarCheckBox.isClickable = false
        aarEditText.isEnabled=false
        aabCheckBox.isClickable = false
        aabEditText.isEnabled=false
        aagCheckBox.isClickable = false
        aagEditText.isEnabled=false
        cogCheckBox.isClickable = false
        cogEditText.isEnabled=false
        corCheckBox.isClickable = false
        corEditText.isEnabled=false
        ersCheckBox.isClickable = false
        ersEditText.isEnabled=false
        mprCheckBox.isClickable = false
        mprEditText.isEnabled=false
        pspCheckBox.isClickable = false
        pspEditText.isEnabled=false
        office_textviewVal.isEnabled = false
        assignedto_textviewVal.isEnabled = false
        dba_textviewVal.isEnabled = false
        entity_textviewVal.isEnabled = false
        bustype_textviewVal.isEnabled = false
        terminationDateButton.isClickable = false
        terminationDateButton.isEnabled = false
        terminationReason_textviewVal.isEnabled=false
        terminationCommentEditText.isEnabled=false
        inspectionMonthsTextViewVal.isEnabled = false
        inspectionCycleTextViewVal.isEnabled=false
        ARDno_textviewVal.isEnabled=false
        currcodate_textviewVal.isEnabled=false
        initcodate_textviewVal.isEnabled=false
        InsuranceExpDate_textviewVal.isEnabled=false
    }


    private fun setFieldsListeners(){
        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
                FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate= sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                (activity as FormsActivity).saveRequired = true
                submitGeneralInfoRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }
//

        InsuranceExpDate_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                InsuranceExpDate_textviewVal!!.text = sdf.format(c.time)
                FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                (activity as FormsActivity).saveRequired = true
                submitGeneralInfoRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        timeZoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!timeZoneSpinner.tag.equals(p2) || timeZoneSpinner.tag.equals("-1")) {
                    timeZoneSpinner.tag = "-1"
                    FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = timeZoneArray[p2]
                    if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName != FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName) {
                        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone = true
                        HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                    } else {
                        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone = false
                        HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                    }
                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true
                    refreshButtonsState()
                }
            }
        }

        website_textviewVal.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblFacilities[0].WebSite = p0.toString()
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                (activity as FormsActivity).saveRequired = true
                submitGeneralInfoRequired = true
                refreshButtonsState()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        wifiAvailableCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = b
            HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
            HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
            (activity as FormsActivity).saveRequired = true
            submitGeneralInfoRequired = true
            refreshButtonsState()
        }

        repairorder_textviewVal.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = p0.toString().toInt()
                    HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                    HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true
                    refreshButtonsState()
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        availability_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!availability_textviewVal.tag.equals(p2) || availability_textviewVal.tag.equals("-1")) {
                    availability_textviewVal.tag = "-1"
                    FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName == svcAvailabilityArray[p2] }[0].SrvAvaID
                    HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                    HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true
                    refreshButtonsState()
                }
            }
        }

        facilitytype_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!facilitytype_textviewVal.tag.equals(p2) || facilitytype_textviewVal.tag.equals("-1")) {
                    facilitytype_textviewVal.tag = "-1"
                    FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = facTypeArray[p2]
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType = (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName != FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName)
                    HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()

                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true

                    refreshButtonsState()
                }
            }



        }
        visa_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(1, b)
        }

        mastercard_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(2, b)
        }

        americanexpress_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(3, b)
        }

        discover_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(4, b)
        }

        paypal_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(5, b)
        }

        debit_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(6, b)
        }

        cash_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(7, b)
        }

        check_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(8, b)
        }

        goodyear_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(9, b)
        }

    }

    fun validateInputs() : Boolean{

        var facValide= TblFacilities().isInputsValid

        facValide=true

        timezone_textview.setError(null)
        repairorder_textviewVal.setError(null)
        availability_textview.setError(null)
        facilitytype_textview.setError(null)
        ARDexp_textviewVal.setError(null)
        payment_methods_textview.setError(null)



        if (timeZoneSpinner.selectedItem.toString().isNullOrEmpty()){
            timezone_textview.setError("reqiured field")
            facValide=false

        }

        if (facilitytype_textviewVal.selectedItem.toString().isNullOrEmpty()){
            facilitytype_textview.setError("reqiured field")
            facValide=false
        }
        if (availability_textviewVal.selectedItem.toString().isNullOrEmpty()){
            availability_textview.setError("reqiured field")
            facValide=false
        }
        if (repairorder_textviewVal.text.toString().isNullOrEmpty()){
            repairorder_textviewVal.setError("reqiured field")
            facValide=false
        }

        if (ARDexp_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            ARDexp_textviewVal.setError("Required Field")
            facValide=false
        }

        if (!americanexpress_checkbox.isChecked && !cash_checkbox.isChecked && !check_checkbox.isChecked && !debit_checkbox.isChecked && !discover_checkbox.isChecked && !goodyear_checkbox.isChecked && !mastercard_checkbox.isChecked && !paypal_checkbox.isChecked && !visa_checkbox.isChecked){
            payment_methods_textview.setError("Required Field")
            facValide=false
        }

        return facValide
    }

    fun handlePaymentMethodsSelection(paymentMethodId: Int, isSelected: Boolean){
        if (isSelected){
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == paymentMethodId }.isEmpty()){
                var pmethod = TblPaymentMethods()
                pmethod.PmtMethodID = ""+paymentMethodId
                FacilityDataModel.getInstance().tblPaymentMethods.add(pmethod)
            }
        }else {
            FacilityDataModel.getInstance().tblPaymentMethods.removeIf { i -> i.PmtMethodID.toInt() == paymentMethodId }
        }
        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityGeneralPaymentMethods = true
        HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
        (activity as FormsActivity).saveRequired = true
        submitPaymentRequired=true
        refreshButtonsState()
    }


    fun setPaymentMethods() {
        visa_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==1 }.size>0)
        mastercard_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==2 }.size>0)
        americanexpress_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==3 }.size>0)
        discover_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==4 }.size>0)
        paypal_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==5 }.size>0)
        debit_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==6 }.size>0)
        cash_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==7 }.size>0)
        check_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==8 }.size>0)
        goodyear_checkbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==9 }.size>0)

    }

    fun getGeneralInfoChanges() : String {
        var strChanges = ""
        if (FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount != FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount) {
            strChanges += "Repair order count changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount+") - "
        }
        if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName != FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName) {
            strChanges += "Time Zone changed from (" + FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName + ") to ("+FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName+") - "
        }
        if (FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability != FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability) {
            strChanges += "Service Availability changed from (" + TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s->s.SrvAvaID.equals(FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability)}[0].SrvAvaName + ") to ("+TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s->s.SrvAvaID.equals(FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability)}[0].SrvAvaName+") - "
        }
        if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate != FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate) {
            strChanges += "ARD Expiration date changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY() + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate+") - "
        }
        if (FacilityDataModel.getInstance().tblFacilities[0].WebSite != FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite) {
            strChanges += "Website URL changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].WebSite+") - "
        }
        if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess != FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess) {
            strChanges += "Wi-Fi Availability changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].InternetAccess+") - "
        }
        if (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName != FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName) {
            strChanges += "Facility Type changed from (" + FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName + ") to ("+FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName+") - "
        }
        strChanges = strChanges.removeSuffix(" - ")
        return if (strChanges.isNullOrEmpty()) "No Changes" else strChanges
    }

    fun submitFacilityGeneralInfo(){
        var busName =  if (dba_textviewVal.text.isNullOrEmpty())  "" else dba_textviewVal.text
        busName = URLEncoder.encode(busName.toString() , "UTF-8");

//        busName = URLEncoder.encode(busName.toString() , "UTF-8");
        val busType = TypeTablesModel.getInstance().BusinessType.filter { s -> s.BusTypeName==bustype_textviewVal.selectedItem.toString()}[0].BusTypeID
        var entityName =  if (entity_textviewVal.text.isNullOrEmpty())  "" else entity_textviewVal.text
        entityName = URLEncoder.encode(entityName.toString() , "UTF-8");
//        val assignedTo = if (assignedto_textviewVal.text.isNullOrEmpty())  "" else assignedto_textviewVal.text // get the ID
        val assignedTo = if (assignedto_textviewVal.text.isNullOrEmpty())  "" else FacilityDataModel.getInstance().tblFacilities[0].assignedToID
        val officeID = if (office_textviewVal.text.isNullOrEmpty())  "" else FacilityDataModel.getInstance().tblFacilities[0].officeID // get The ID
        val taxIDNo = if (taxno_textviewVal.text.isNullOrEmpty())  "" else taxno_textviewVal.text
        val facRepairCnt = if (repairorder_textviewVal.text.isNullOrEmpty())  "" else repairorder_textviewVal.text
        val inspectionMonth = (FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth).toString()
        val inspectionCycle = inspectionCycleTextViewVal.text.toString()
        val timeZoneID = TypeTablesModel.getInstance().TimezoneType.filter { s->s.TimezoneName.equals(timeZoneSpinner.selectedItem.toString()) }[0].TimezoneID
        val svcAvailability= TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName==availability_textviewVal.selectedItem.toString()}[0].SrvAvaID
        val facType = TypeTablesModel.getInstance().FacilityType.filter { s -> s.FacilityTypeName==facilitytype_textviewVal.selectedItem.toString()}[0].FacilityTypeID
        val automtiveRepairNo = if (ARDno_textviewVal.text.isNullOrEmpty())  "" else ARDno_textviewVal.text
        val automtiveRepairExpDate = if (ARDexp_textviewVal.text.equals("")) "" else ARDexp_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractCurrDate = if (currcodate_textviewVal.text.equals("")) "" else currcodate_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractInitDate = if (initcodate_textviewVal.text.equals("")) "" else initcodate_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val internetAccess = if (wifiAvailableCheckBox.isChecked) "1" else "0"
        val webSite = if (website_textviewVal.text.isNullOrEmpty())  "" else website_textviewVal.text
        val terminationDate = if (terminationDateButton.text.equals("")) "" else terminationDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
        val terminationReasonID = TypeTablesModel.getInstance().TerminationCodeType.filter { s -> s.TerminationCodeName==terminationReason_textviewVal.selectedItem.toString()}[0].TerminationCodeID
        val terminationComments = if (terminationCommentEditText.text.isNullOrEmpty())  "" else terminationCommentEditText.text
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val insuranceExpDate = if (InsuranceExpDate_textviewVal.text.equals("")) "" else InsuranceExpDate_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractType = TypeTablesModel.getInstance().ContractType.filter { s -> s.ContractTypeName==contractTypeValueSpinner.selectedItem.toString()}[0].ContractTypeID
        val billingMonth = FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.toString()
        val billingAmount = FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toString()
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode =FacilityDataModel.getInstance().clubCode
        FacilityDataModel.getInstance().tblFacilities[0]
        progressBarText.text = "Saving ..."
        scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
        var urlString = facilityNo+"&clubCode="+clubCode+"&businessName="+busName+"&busTypeId="+busType+"&entityName="+entityName+"&assignToId="+assignedTo+"&officeId="+officeID+"&taxIdNumber="+taxIDNo+"&facilityRepairOrderCount="+facRepairCnt+"&facilityAnnualInspectionMonth="+inspectionMonth.toString()+"&inspectionCycle="+inspectionCycle+"&timeZoneId="+timeZoneID.toString()+"&svcAvailability="+svcAvailability+"&facilityTypeId="+facType+"&automotiveRepairNumber="+automtiveRepairNo+"&automotiveRepairExpDate="+automtiveRepairExpDate+"&contractCurrentDate="+contractCurrDate+"&contractInitialDate="+contractInitDate+"&billingMonth="+billingMonth+"&billingAmount="+billingAmount+"&internetAccess="+internetAccess+"&webSite="+webSite+"&terminationDate="+terminationDate+"&terminationId="+terminationReasonID+"&terminationComments="+terminationComments+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&active=0&achParticipant=0&insuranceExpDate="+insuranceExpDate.toString()+"&contractTypeId="+contractType
//        UUID.randomUUID().toString()

        Log.v("Facility General --- ",Constants.submitFacilityGeneralInfo + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityGeneralInfo + urlString +Utility.getLoggingParameters(activity,0,getGeneralInfoChanges()),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        Log.v("RESPONSE",response.toString())
                        if (response.toString().contains("returnCode>0<",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Facility General Information")
                            FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = timeZoneSpinner.selectedItem.toString()
                            FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = facRepairCnt.toString().toInt()
                            FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = svcAvailability
                            FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate = automtiveRepairExpDate
                            FacilityDataModel.getInstance().tblFacilities[0].WebSite = webSite.toString()
                            FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = internetAccess.toBoolean()
                            FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = facilitytype_textviewVal.selectedItem.toString()
                            FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName = timeZoneSpinner.selectedItem.toString()
                            FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount = facRepairCnt.toString().toInt()
                            FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability = svcAvailability
                            FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate = automtiveRepairExpDate
                            FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite = webSite.toString()
                            FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess = internetAccess.toBoolean()
                            FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName = facilitytype_textviewVal.selectedItem.toString()
                            (activity as FormsActivity).saveRequired = false
                            submitGeneralInfoRequired = false
                            HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                            HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone=true
                            HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType=true
                            HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                            progressBarText.text = "Loading ..."
                        } else {
                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                            progressBarText.text = "Loading ..."
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity,false,"Facility General Information (Error: "+ errorMessage+" )")
                        }
                    }
                }, Response.ErrorListener {
            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
            progressBarText.text = "Loading ..."
            Utility.showSubmitAlertDialog(activity,false,"Facility General Information (Error: "+it.message+" )")
        }))
    }


    fun getPaymentDataChanges() : String {
        var strChanges = ""
        var strPrefix = "Payment method(s) ("
        var strAdded = ""
        var strRemoved = ""
        if (visa_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==1 }.isEmpty()) {
            strAdded += "VISA - "
        }
        if (mastercard_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==2 }.isEmpty()) {
            strAdded += "Master Card - "
        }
        if (americanexpress_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==3 }.isEmpty()) {
            strAdded += "American Express - "
        }
        if (discover_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==4 }.isEmpty()) {
            strAdded += "Discover - "
        }
        if (paypal_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==5 }.isEmpty()) {
            strAdded += "PayPal - "
        }
        if (debit_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==6 }.isEmpty()) {
            strAdded += "Debit - "
        }
        if (cash_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==7 }.isEmpty()) {
            strAdded += "Cash - "
        }
        if (check_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==8 }.isEmpty()) {
            strAdded += "Check - "
        }
        if (goodyear_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==9 }.isEmpty()) {
            strAdded += "Goodyear Credit Card - "
        }
        if (!strAdded.isNullOrEmpty()) {
            strChanges += strPrefix + strAdded.removeSuffix(" - ") + ") added"
        }
        // REMOVED
        if (!visa_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==1 }.isNotEmpty()) {
            strRemoved += "VISA - "
        }
        if (!mastercard_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==2 }.isNotEmpty()) {
            strRemoved += "Master Card - "
        }
        if (!americanexpress_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==3 }.isNotEmpty()) {
            strRemoved += "American Express - "
        }
        if (!discover_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==4 }.isNotEmpty()) {
            strRemoved += "Discover - "
        }
        if (!paypal_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==5 }.isNotEmpty()) {
            strRemoved += "PayPal - "
        }
        if (!debit_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==6 }.isNotEmpty()) {
            strRemoved += "Debit - "
        }
        if (!cash_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==7 }.isNotEmpty()) {
            strRemoved += "Cash - "
        }
        if (!check_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==8 }.isNotEmpty()) {
            strRemoved += "Check - "
        }
        if (!goodyear_checkbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==9 }.isNotEmpty()) {
            strRemoved += "Goodyear Credit Card - "
        }
        if (!strRemoved.isNullOrEmpty()) {
            if (strAdded.isNotEmpty()) strChanges += " | "
            strChanges += strPrefix + strRemoved.removeSuffix(" - ") + ") removed"
        }

        return strChanges
    }


    fun submitPaymentMethods(){


        var insertDate = Date().toAppFormatMMDDYYYY()

                //  BuildProgramsList()

      val visa : String=   if (visa_checkbox.isChecked == true) "1" else ""
      val mastercard: String=   if (mastercard_checkbox.isChecked == true) "2" else ""
      val americanexpress: String=   if (americanexpress_checkbox.isChecked == true) "3" else ""
      val discover: String=   if (discover_checkbox.isChecked == true) "4" else ""
      val paypal: String=   if (paypal_checkbox.isChecked == true) "5" else ""
      val debit: String=   if (debit_checkbox.isChecked == true) "6" else ""
      val cash: String=   if (cash_checkbox.isChecked == true) "7" else ""
      val check: String=   if (check_checkbox.isChecked == true) "8" else ""
      val goodyear: String=   if (goodyear_checkbox.isChecked == true) "9" else ""

         var paymentMethods= arrayOf(visa,mastercard,americanexpress,discover,paypal,debit,cash,check,goodyear)
        var paymentMethodArray = ArrayList<String>()
        progressBarText.text = "Saving ..."
        scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
        var payments : String? =""

        for (pm in paymentMethods){

            if (!pm.equals("")){
                paymentMethodArray.add(pm)
            }
            payments = paymentMethodArray.toString().replace("[","").replace("]","").replace(" ","")
        }
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode =FacilityDataModel.getInstance().clubCode
        Log.v("Facility Payments --- ",UpdatePaymentMethodsData + "${facilityNo}&clubcode=${clubCode}&paymentMethodID=${payments.toString()}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${insertDate.appToApiSubmitFormatMMDDYYYY()}")
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdatePaymentMethodsData + "${facilityNo}&clubcode=${clubCode}&paymentMethodID=${payments.toString()}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${insertDate.appToApiSubmitFormatMMDDYYYY()}"+Utility.getLoggingParameters(activity,0,getPaymentDataChanges()),
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        if (response.toString().contains("returnCode>0<",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Payment Methods")
                            (activity as FormsActivity).saveRequired = false
                            FacilityDataModelOrg.getInstance().tblPaymentMethods.clear()
                            for (i in 0..FacilityDataModel.getInstance().tblPaymentMethods.size-1) {
                                var payMethod = TblPaymentMethods()
                                payMethod.PmtMethodID = FacilityDataModel.getInstance().tblPaymentMethods[i].PmtMethodID
                                FacilityDataModelOrg.getInstance().tblPaymentMethods.add(payMethod)
                            }
                            HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityGeneralPaymentMethods = true
                            HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                            refreshButtonsState()
                            submitPaymentRequired=false
                        } else {
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity,false,"Payment Methods (Error: "+ errorMessage+" )")
                        }
                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                        progressBarText.text = "Loading ..."
                    }
                }, Response.ErrorListener {
        Utility.showSubmitAlertDialog(activity,false,"Payment Methods (Error: "+it.message+" )")
            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
            progressBarText.text = "Loading ..."
        }))


    }


    fun refreshButtonsState(){

        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }


    override fun onPause() {
        super.onPause()
//        checkMarkChangesWasDoneForFacilityGeneralInfo()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
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
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FacilityGeneralInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FacilityGeneralInformationFragment {
            val fragment = FacilityGeneralInformationFragment()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
