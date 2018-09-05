package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
//import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.constraint.R.id.gone
import android.support.v4.app.Fragment
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
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdatePaymentMethodsData
import com.inspection.imageloader.Utils
import com.inspection.model.*
import kotlinx.android.synthetic.main.app_bar_forms.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

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
//    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
//    private val appFprmat = SimpleDateFormat("dd MMM yyyy")
//    private var timeZonesArray = arrayOf("")
//    private var facilityTypeArray = arrayOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility, container, false)
    }

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

//    private var assignedToList = ArrayList<CsiSpecialist>()
//    private var assignedToArray = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fill Dop Down
//        assignedToArray.clear()
//        (0 until CsiSpecialistSingletonModel.getInstance().csiSpecialists.size).forEach {
//            assignedToArray.add(CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].specialistname)
//        }
//        assignedToArray.sort()
//
//        var assignToAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, termReasonArray)
//        assignToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        assignedto_textviewVal.adapter = assignToAdapter
        refreshButtonsState()
//        scopeOfServiceChangesWatcher()
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

        setFieldsValues()
        ImplementBusinessRules()
        setFieldsListeners()

    }


    private fun setFieldsValues() {
        FacilityDataModel.getInstance().apply {
//            try {

                if(tblFacilities[0].ACTIVE==1){
                    activeRadioButton.isChecked = true
                }else{
                    inActiveRadioButton.isChecked = true
                }

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
                bustype_textviewVal.setSelection(busTypeArray.indexOf(tblBusinessType[0].BusTypeName))

                timeZoneSpinner.setSelection(timeZoneArray.indexOf(tblTimezoneType[0].TimezoneName))
                website_textviewVal.setText(tblFacilities[0].WebSite)
                wifiAvailableCheckBox.isChecked = tblFacilities[0].InternetAccess
                taxno_textviewVal.text = tblFacilities[0].TaxIDNumber
                repairorder_textviewVal.setText("" + tblFacilities[0].FacilityRepairOrderCount)
                availability_textviewVal.setSelection(svcAvailabilityArray.indexOf(TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==tblFacilities[0].SvcAvailability}[0].SrvAvaName))
                facilitytype_textviewVal.setSelection(facTypeArray.indexOf(tblFacilityType[0].FacilityTypeName))
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


                inspectionMonthsTextViewVal.text=inspectionMonths[(tblFacilities[0].FacilityAnnualInspectionMonth)-1]

                if (inspectionMonthsTextViewVal.text==inspectionMonths[0]||inspectionMonthsTextViewVal.text==inspectionMonths[3]||inspectionMonthsTextViewVal.text==inspectionMonths[6]||inspectionMonthsTextViewVal.text==inspectionMonths[9]){

                    inspectionCycleTextViewVal.text="1"
                }
                if (inspectionMonthsTextViewVal.text==inspectionMonths[1]||inspectionMonthsTextViewVal.text==inspectionMonths[4]||inspectionMonthsTextViewVal.text==inspectionMonths[7]||inspectionMonthsTextViewVal.text==inspectionMonths[10]){

                    inspectionCycleTextViewVal.text="2"
                }
                if (inspectionMonthsTextViewVal.text==inspectionMonths[2]||inspectionMonthsTextViewVal.text==inspectionMonths[5]||inspectionMonthsTextViewVal.text==inspectionMonths[8]||inspectionMonthsTextViewVal.text==inspectionMonths[11]){

                    inspectionCycleTextViewVal.text="3"
                }


                for(paymentMethod in tblPaymentMethods){

                }

//            }catch (exp:Exception){
//                exp.printStackTrace()
//            }
        }

        if (arguments!!.getBoolean(isValidating)) {
            validateInputs()
        }
// Removed as per feedback
//        if (FacilityDataModel.getInstance().tblSurveySoftwares!= null && FacilityDataModel.getInstance().tblSurveySoftwares[0] != null) {
//            shopManagmentSystem_textviewVal.setText(FacilityDataModel.getInstance().tblSurveySoftwares[0].shopMgmtSoftwareName)
//        }
//
//        if (FacilityDataModel.getInstance().tblSurveySoftwares!= null && FacilityDataModel.getInstance().tblSurveySoftwares[0] != null) {
//            shopManagmentSystem_textviewVal.setText(FacilityDataModel.getInstance().tblSurveySoftwares[0].shopMgmtSoftwareName)
//        }

        setPaymentMethods()


        saveButton.setOnClickListener {
            if (validateInputs()){
                submitFacilityGeneralInfo()
                submitPaymentMethods()
        }else
            {
                Utility.showValidationAlertDialog(activity,"Please fill all required fields")
            }
        }
        cancelButton.setOnClickListener {
            FacilityDataModel.getInstance().tblFacilities[0] = FacilityDataModelOrg.getInstance().tblFacilities[0] as FacilityDataModel.TblFacilities
            FacilityDataModel.getInstance().tblTimezoneType[0] = FacilityDataModelOrg.getInstance().tblTimezoneType[0] as FacilityDataModel.TblTimezoneType
            FacilityDataModel.getInstance().tblFacilityType[0] = FacilityDataModelOrg.getInstance().tblFacilityType[0] as FacilityDataModel.TblFacilityType
            FacilityDataModel.getInstance().tblPaymentMethods[0].PmtMethodID = FacilityDataModelOrg.getInstance().tblPaymentMethods[0].PmtMethodID
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


//                inspectionMonthsTextViewVal.text=inspectionMonths[(tblFacilities[0].FacilityAnnualInspectionMonth)]

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
                //                if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate!=FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate) MarkChangeWasDone()
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                (activity as FormsActivity).saveRequired = true
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
//                if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate!=FacilityDataModelOrg.getInstance().tblFacilities[0].InsuranceExpDate) MarkChangeWasDone()
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        timeZoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = timeZoneArray[p2]
                if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName !=FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName) {
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone = true
                } else {
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone= false

                }
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

        }

        website_textviewVal.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblFacilities[0].WebSite = p0.toString()
//                if (FacilityDataModel.getInstance().tblFacilities[0].WebSite !=FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite ) MarkChangeWasDone()
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        wifiAvailableCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = b
//            if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess !=FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess ) MarkChangeWasDone()
            HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
        }

        repairorder_textviewVal.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 0) {
                    FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = p0.toString().toInt()
//                    if (FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount !=FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount ) MarkChangeWasDone()
                    HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                    (activity as FormsActivity).saveRequired = true
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
         //       FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = svcAvailabilityArray[p2]
                FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability= TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName==svcAvailabilityArray[p2]}[0].SrvAvaID
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
//                if (FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability !=FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability ) MarkChangeWasDone()

            }

        }

        facilitytype_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = facTypeArray[p2]
                if (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName  !=FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName  ) {
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType = true
                } else {
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType = false
                }
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }

        }

//        shopManagmentSystem_textviewVal.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//                if (FacilityDataModel.getInstance().tblSurveySoftwares == null){
//                    FacilityDataModel.getInstance().tblSurveySoftwares = ArrayList<FacilityDataModel.TblSurveySoftwares>()
//                }
//                if (FacilityDataModel.getInstance().tblSurveySoftwares.count() > 0 ) {
//                    if (FacilityDataModel.getInstance().tblSurveySoftwares[0] == null){
//                        FacilityDataModel.getInstance().tblSurveySoftwares[0] = FacilityDataModel.TblSurveySoftwares()
//                    }
//                    FacilityDataModel.getInstance().tblSurveySoftwares[0].shopMgmtSoftwareName = p0.toString()
//                }else{
//                    var survey = FacilityDataModel.TblSurveySoftwares()
//                    survey.shopMgmtSoftwareName = p0.toString()
//                    FacilityDataModel.getInstance().tblSurveySoftwares.add(survey)
//                }
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//        })

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

        var facValide=FacilityDataModel.TblFacilities().isInputsValid

        facValide=true

        timezone_textview.setError(null)
        repairorder_textviewVal.setError(null)
        availability_textview.setError(null)
        facilitytype_textview.setError(null)
        ARDexp_textviewVal.setError(null)
//        shopManagmentSystem_textviewVal.setError(null)
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

//        if (shopManagmentSystem_textviewVal.text.toString().isNullOrEmpty()){
//            shopManagmentSystem_textviewVal.setError("reqiured field")
//            facValide=false
//        }
        if (ARDexp_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            ARDexp_textviewVal.setError("Required Field")
            facValide=false
        }

        if (americanexpress_checkbox.isChecked==false &&
                cash_checkbox.isChecked==false &&
                check_checkbox.isChecked==false &&
                debit_checkbox.isChecked==false &&
                discover_checkbox.isChecked==false &&
                goodyear_checkbox.isChecked==false &&
                mastercard_checkbox.isChecked==false &&
                paypal_checkbox.isChecked==false &&
                visa_checkbox.isChecked==false){

            payment_methods_textview.setError("Required Field")
            facValide=false

        }

//        AnnualVisitationSingleton.getInstance().apply {
//            if (ardNumber == -1){
//                ARDno_textviewVal.error = ""
//            }
//
//            if (ardExpirationDate == -1L) {
//                ARDexp_textviewVal.error = ""
//            }
//
//            if (insuranceExpirationDate == -1L) {
////                InsuranceExpDate_textviewVal.error = ""
//            }
//        }
        return facValide
    }

    fun handlePaymentMethodsSelection(paymentMethodId: Int, isSelected: Boolean){
        if (isSelected){
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == paymentMethodId }.isEmpty()){
                var pmethod = FacilityDataModel.TblPaymentMethods()
                pmethod.PmtMethodID = ""+paymentMethodId
                FacilityDataModel.getInstance().tblPaymentMethods.add(pmethod)
            }
        }else {
            FacilityDataModel.getInstance().tblPaymentMethods.removeIf { i -> i.PmtMethodID.toInt() == paymentMethodId }
        }
//        if (FacilityDataModel.getInstance().tblPaymentMethods[0].PmtMethodID !=FacilityDataModelOrg.getInstance().tblPaymentMethods[0].PmtMethodID) MarkChangeWasDone()
    }

    fun checkMarkChangesWasDoneForFacilityGeneralInfo(){

        var paymentMethodsCount=0

if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate != FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate){

    MarkChangeWasDone()

}


if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName != FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName){

    MarkChangeWasDone()
    Log.v("MarkChangeWasDone", "TimezoneName")

}




        if ( FacilityDataModel.getInstance().tblFacilities[0].WebSite!= FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite){

            MarkChangeWasDone()
            Log.v("MarkChangeWasDone", "WebSite")

        }



        if ( FacilityDataModel.getInstance().tblFacilities[0].InternetAccess!= FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess){

            MarkChangeWasDone()
            Log.v("MarkChangeWasDone", "InternetAccess")

        }





        if (    FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount!= FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount){

            MarkChangeWasDone()
            Log.v("MarkChangeWasDone", "FacilityRepairOrderCount")

        }






        if (    FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability!= FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability){

            MarkChangeWasDone()
            Log.v("MarkChangeWasDone", FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability + "----->"+ FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability)

        }






        if (     FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName!= FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName){

            MarkChangeWasDone()
            Log.v("MarkChangeWasDone", "FacilityTypeName")

        }





        var itemOrgArray = FacilityDataModelOrg.getInstance().tblPaymentMethods
        var itemArray = FacilityDataModel.getInstance().tblPaymentMethods
        for (itemAr in itemArray) {
            for (itemOrgAr in itemOrgArray) {



                if (itemAr.PmtMethodID == itemOrgAr.PmtMethodID ) {
                    paymentMethodsCount++
                }


            }

        }

        if (paymentMethodsCount!=itemArray.size||itemOrgArray.size!=itemArray.size){

            Log.v("MarkChangeWasDone", paymentMethodsCount.toString()  +  "---"+itemArray.size.toString() +"------" +itemOrgArray.size.toString())
            MarkChangeWasDone()

        }

    }

    fun setPaymentMethods() {

        for (fac in FacilityDataModel.getInstance().tblPaymentMethods) {
            when (fac.PmtMethodID.toInt()) {
                1 -> visa_checkbox.isChecked = true
                2 -> mastercard_checkbox.isChecked = true
                3 -> americanexpress_checkbox.isChecked = true
                4 -> discover_checkbox.isChecked = true
                5 -> paypal_checkbox.isChecked = true
                6 -> debit_checkbox.isChecked = true
                7 -> cash_checkbox.isChecked = true
                8 -> check_checkbox.isChecked = true
                9 -> goodyear_checkbox.isChecked = true
            }
        }
    }

    fun submitFacilityGeneralInfo(){
        val busName =  if (dba_textviewVal.text.isNullOrEmpty())  "" else dba_textviewVal.text
        val busType = TypeTablesModel.getInstance().BusinessType.filter { s -> s.BusTypeName==bustype_textviewVal.selectedItem.toString()}[0].BusTypeID
        val entityName =  if (entity_textviewVal.text.isNullOrEmpty())  "" else entity_textviewVal.text
        val assignedTo = if (assignedto_textviewVal.text.isNullOrEmpty())  "" else assignedto_textviewVal.text // get the ID
        val officeID = if (office_textviewVal.text.isNullOrEmpty())  "" else "0" // get The ID
        val taxIDNo = if (taxno_textviewVal.text.isNullOrEmpty())  "" else taxno_textviewVal.text
        val facRepairCnt = if (repairorder_textviewVal.text.isNullOrEmpty())  "" else repairorder_textviewVal.text
        val inspectionMonth = (FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth).toString()
        val inspectionCycle = inspectionCycleTextViewVal.text.toString()
        val timeZoneID = (timeZoneSpinner.selectedItemPosition+1).toString()
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
        val insertBy ="sa"
        val updateDate = Date().toApiSubmitFormat()
        val updateBy ="sa"
        val activeVal = "0"
        val insuranceExpDate = if (InsuranceExpDate_textviewVal.text.equals("")) "" else InsuranceExpDate_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractType = TypeTablesModel.getInstance().ContractType.filter { s -> s.ContractTypeName==contractTypeValueSpinner.selectedItem.toString()}[0].ContractTypeID
        val billingMonth = FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.toString()
        val billingAmount = FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toString()
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode =FacilityDataModel.getInstance().clubCode
        FacilityDataModel.getInstance().tblFacilities[0]
        //https://dev.facilityappointment.com/ACEAPI.asmx/updateFacilityInfo?facNum=155&clubcode=&businessName=De Lillo Chevrolet&busTypeId=1&entityName=De Lillo Chevrolet Co.&assignToId=Jeffrey Moss&officeId=0&taxIdNumber=95-1496182&facilityRepairOrderCount=12000&facilityAnnualInspectionMonth=4&inspectionCycle=1&timeZoneId=6&svcAvailability=1&facilityTypeId=1&automotiveRepairNumber=AA001515&automotiveRepairExpDate=2018-01-31T00:00:00&contractCurrentDate=2015-05-14T00:00:00&contractInitialDate=1979-04-27T00:00:00&billingMonth=10&billingAmount=495&internetAccess=1&webSite=www.delillo.com&terminationDate=&terminationId=1&terminationComments=&insertBy=sa&insertDate=2018-08-25T10:00:05&updateBy=sa&updateDate=2018-08-25T10:00:05&active=0&achParticipant=0&insuranceExpDate=2018-07-01T00:00:00&contractTypeId=1
        var urlString = facilityNo+"&clubcode="+clubCode+"&businessName="+busName+"&busTypeId="+busType+"&entityName="+entityName+"&assignToId="+assignedTo+"&officeId="+officeID+"&taxIdNumber="+taxIDNo+"&facilityRepairOrderCount="+facRepairCnt+"&facilityAnnualInspectionMonth="+inspectionMonth.toString()+"&inspectionCycle="+inspectionCycle+"&timeZoneId="+timeZoneID.toString()+"&svcAvailability="+svcAvailability+"&facilityTypeId="+facType+"&automotiveRepairNumber="+automtiveRepairNo+"&automotiveRepairExpDate="+automtiveRepairExpDate+"&contractCurrentDate="+contractCurrDate+"&contractInitialDate="+contractInitDate+"&billingMonth="+billingMonth+"&billingAmount="+billingAmount+"&internetAccess="+internetAccess+"&webSite="+webSite+"&terminationDate="+terminationDate+"&terminationId="+terminationReasonID+"&terminationComments="+terminationComments+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&active=0&achParticipant=0&insuranceExpDate="+insuranceExpDate.toString()+"&contractTypeId="+contractType
        Log.v("Data To Submit", urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityGeneralInfo + urlString,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        Log.v("RESPONSE",response.toString())
                        if (response.toString().contains("returnCode&gt;0&",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Facility General Information")
                            FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = timeZoneSpinner.selectedItem.toString()
                            FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = facRepairCnt.toString().toInt()
                            FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = svcAvailability
                            FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate = automtiveRepairExpDate
                            FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = facilitytype_textviewVal.selectedItem.toString()
//                            (activity as FormsActivity).saveRequired = false
                            IndicatorsDataModel.getInstance().validateFacilityGeneralInfo()
                            if (IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfo) (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#A42600"))
                            (activity as FormsActivity).refreshMenuIndicators()
                        } else {
                            Utility.showSubmitAlertDialog(activity,false,"Facility General Information")
                        }
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading")
        }))
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


        var payments : String? =""

        for (pm in paymentMethods){

            if (!pm.equals("")){


                paymentMethodArray.add(pm)




            }

            payments = paymentMethodArray.toString().replace("[","").replace("]","").replace(" ","")


        }
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdatePaymentMethodsData + "&paymentMethodID=${payments.toString()}&insertBy=GurovichY&insertDate=${insertDate.appToApiSubmitFormatMMDDYYYY()}",
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        Log.v("PAYMENT REQUEST",UpdatePaymentMethodsData + "&paymentMethodID=${payments.toString()}&insertBy=GurovichY&insertDate=${insertDate.appToApiSubmitFormatMMDDYYYY()}")
                        Log.v("paymentSUBMIT_RESPONSE",response.toString())
                        Log.v("paymentsTRING",payments.toString())
                        if (response.toString().contains("returnCode&gt;0&",false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Payment Methods")
                            (activity as FormsActivity).saveRequired = false
                            refreshButtonsState()
                        } else {
                            Utility.showSubmitAlertDialog(activity,false,"Payment Methods")
                        }
                        IndicatorsDataModel.getInstance().validateFacilityGeneralInfo()
                        if (IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfo) (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#A42600"))
                        (activity as FormsActivity).refreshMenuIndicators()
                    })
                }, Response.ErrorListener {
                Utility.showSubmitAlertDialog(activity,false,"Payment Methods")
        }))


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


                        scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE


                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())

//                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                                                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

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
//                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()

                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE


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
                    FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = true
                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true


                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }

        }

    }

    fun refreshButtonsState(){
        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }


    override fun onPause() {
        super.onPause()
        checkMarkChangesWasDoneForFacilityGeneralInfo()
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
