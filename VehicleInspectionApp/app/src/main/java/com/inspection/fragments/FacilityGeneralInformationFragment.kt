package com.inspection.fragments

//import android.app.Fragment

import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdatePaymentMethodsData
import com.inspection.databinding.BillingGroupLayoutBinding
import com.inspection.databinding.FragmentArravFacilityBinding
//import com.inspection.databinding.FragmentArravfacilityContinuedBinding
import com.inspection.model.*
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FacilityGeneralInformationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FacilityGeneralInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FacilityGeneralInformationFragment : Fragment() {
    private var _binding: FragmentArravFacilityBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentArravFacilityBinding.bind(view)
        refreshButtonsState()

        termReasonList = TypeTablesModel.getInstance().TerminationCodeType
        termReasonArray .clear()
        for (fac in termReasonList) {
            termReasonArray .add(fac.TerminationCodeName)
        }

        var termReasonAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, termReasonArray)
        termReasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.terminationReasonTextviewVal.adapter = termReasonAdapter

        busTypeList = TypeTablesModel.getInstance().BusinessType
        busTypeArray .clear()
        for (fac in busTypeList) {
            busTypeArray .add(fac.BusTypeName)
        }

        var busTypeAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, busTypeArray)
        busTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bustypeTextviewVal.adapter = busTypeAdapter


        timeZoneList = TypeTablesModel.getInstance().TimezoneType
        timeZoneArray .clear()
        for (fac in timeZoneList) {
            timeZoneArray .add(fac.TimezoneName)
        }

        var tzdataAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, timeZoneArray)
        tzdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timeZoneSpinner.adapter = tzdataAdapter

        svcAvailabilityList = TypeTablesModel.getInstance().ServiceAvailabilityType
        svcAvailabilityArray .clear()
        for (fac in svcAvailabilityList) {
            svcAvailabilityArray .add(fac.SrvAvaName)
        }
        var svcAvldataAdapter = ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, svcAvailabilityArray)
        svcAvldataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.availabilityTextviewVal.adapter = svcAvldataAdapter

        facTypeList = TypeTablesModel.getInstance().FacilityType
        facTypeArray .clear()
        for (fac in facTypeList) {
            facTypeArray .add(fac.FacilityTypeName)
        }
        var facilityTypedataAdapter = ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, facTypeArray)
        facilityTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.facilitytypeTextviewVal.adapter = facilityTypedataAdapter


        contractTypeList = TypeTablesModel.getInstance().ContractType
        contractTypeArray .clear()
        for (fac in contractTypeList) {
            contractTypeArray .add(fac.ContractTypeName)
        }

        var contractTypesAdapter = ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, contractTypeArray )
        contractTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contractTypeValueSpinner.adapter = contractTypesAdapter

        IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited = true
        // SAEED TO BE REVIEWED
        requireActivity().findViewById<Button>(R.id.generalInformationButton).setTextColor(Color.parseColor("#26C3AA"))
//        (activity as FormsActivity).generalInformationButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()


        setFieldsValues()
        ImplementBusinessRules()
        setFieldsListeners()
        setAlertColoring()
    }

    private fun setAlertColoring() {
        var InsuranceExpDateStr = ""
        var toolTipStr = ""
        var ARDExpDateStr = FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY()
        if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.contains('T')) {
            InsuranceExpDateStr = FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY()
        } else {
            InsuranceExpDateStr = FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate
        }
//        val pattern = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val InsuranceExpDate = sdf.parse(InsuranceExpDateStr)
        val InsuranceExpDatedays =  (InsuranceExpDate.time - Date().time) / 1000 / 60 / 60 / 24
        val ARDExpDate = sdf.parse(ARDExpDateStr)
        val ARDExpDatedays =  (ARDExpDate.time - Date().time) / 1000 / 60 / 60 / 24
        binding.alertRIcon.isVisible = (InsuranceExpDatedays <= 0) || (ARDExpDatedays <= 0)
        binding.alertYIcon.isVisible = (InsuranceExpDatedays <= 180 || ARDExpDatedays <= 180) && !binding.alertRIcon.isVisible
        val animation: Animation =  AlphaAnimation(1.0f,0.0f)
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        binding.alertYIcon.startAnimation(animation) //to start animation
        binding.alertRIcon.startAnimation(animation) //to start animation
        if (ARDExpDatedays<=0){
            toolTipStr = "ARD Expiration Date has passed\n\n"
            binding.ARDexpTextview.setTextColor(Color.RED)
            binding.ARDexpTextview.startAnimation(animation) //to start animation
        } else if (ARDExpDatedays<=180) {
            toolTipStr = "ARD Expiration Date is within 180 days\n\n"
            binding.ARDexpTextview.setTextColor(resources.getColor(R.color.dark_yellow))
            binding.ARDexpTextview.startAnimation(animation) //to start animation
        } else {
            toolTipStr = ""
            binding.ARDexpTextview.setTextColor(Color.BLACK)
            binding.ARDexpTextview.clearAnimation()
        }
        if (toolTipStr.equals("")) toolTipStr = "\n\n"
        if (InsuranceExpDatedays<=0){
            toolTipStr += "Insurance Expiration Date has passed\n\n"
            binding.InsuranceExpDateTextview.setTextColor(Color.RED)
            binding.InsuranceExpDateTextview.startAnimation(animation) //to start animation
        } else if (InsuranceExpDatedays<=180) {
            toolTipStr += "Insurance Expiration Date is within 180 days\n\n"
            binding.InsuranceExpDateTextview.setTextColor(resources.getColor(R.color.dark_yellow))
            binding.InsuranceExpDateTextview.startAnimation(animation) //to start animation
        } else {
            binding.InsuranceExpDateTextview.setTextColor(Color.BLACK)
            binding.InsuranceExpDateTextview.clearAnimation()
        }

        binding.alertRIcon.tooltipText = toolTipStr
        binding.alertYIcon.tooltipText = toolTipStr
        binding.alertRIcon.isClickable = true
        binding.alertRIcon.setOnClickListener({
            Utility.showUnifiedInformationDialog(requireContext(),toolTipStr)
        })
        binding.alertYIcon.isClickable = true
        binding.alertYIcon.setOnClickListener({
            Utility.showUnifiedInformationDialog(requireContext(),toolTipStr)
        })
        var defAlert = checkUnClearedDeficiencies()
        if (defAlert.isNullOrEmpty()) {
            binding.deficienciesText.visibility = View.GONE
        } else {
            defAlert = "Uncleared Deficiencies: $defAlert"
            binding.deficienciesText.text = defAlert
            binding.deficienciesText.visibility = View.VISIBLE
//            binding.deficienciesText.startAnimation(animation)
        }
        if (checkDownStreamFlag()) {
            binding.downStreamFlagText.visibility = View.GONE
        } else {
            binding.downStreamFlagText.visibility = View.VISIBLE
        }
    }

    private fun setFieldsValues() {

        fillPortalTrackingTableView()
        FacilityDataModel.getInstance().apply {
            var statusID = "1"
            try {
                statusID = CSIFacilitySingelton.getInstance().csiFacilities.filter { s -> s.facnum.equals(tblFacilities[0].FACNo.toString()) && s.clubcode.equals(clubCode) }[0].status
            } catch (e: Exception) {

            }
            binding.statusCommentEditText.setText(tblFacilities[0].StatusComment)
            binding.contractStatusTextViewVal.text = TypeTablesModel.getInstance().FacilityStatusType.filter { s -> s.FacilityStatusID == statusID}[0].FacilityStatusName
//            contractStatusTextViewVal.setTextColor(Color.BLUE)
                        for (provider in tblFacilityServiceProvider) {
                            when(provider.SrvProviderId){
                                "AAR" -> {
                                    binding.aarCheckBox.isChecked = true
                                    binding.aarEditText.setText(""+provider.ProviderNum)
                                }
                                "AABِ" -> {
                                    binding.aabCheckBox.isChecked = true
                                    binding.aabEditText.setText(provider.ProviderNum)
                                }
                                "ِAAG" -> {
                                    binding.aagCheckBox.isChecked = true
                                    binding.aagEditText.setText(provider.ProviderNum)
                                }
                                "COG" -> {
                                    binding.cogCheckBox.isChecked = true
                                    binding.cogEditText.setText(provider.ProviderNum)
                                }
                                "CCR" -> {
                                    binding.corCheckBox.isChecked = true
                                    binding.corEditText.setText(provider.ProviderNum)
                                }
                                "ERS" -> {
                                    binding.ersCheckBox.isChecked = true
                                    binding.ersEditText.setText(provider.ProviderNum)
                                }
                                "MPR" -> {
                                    binding.mprCheckBox.isChecked = true
                                    binding.mprEditText.setText(provider.ProviderNum)
                                }
                                "PSP" -> {
                                    binding.pspCheckBox.isChecked = true
                                    binding.pspEditText.setText(provider.ProviderNum)
                                }

                            }
                        }



                for(contractType in tblContractType){
                    for (typeReference in contractTypeArray){
                        if(contractType.ContractTypeName == typeReference){
                            binding.contractTypeValueSpinner.setSelection(contractTypeArray.indexOf(typeReference))
                        }
                    }
                }

            binding.contractNumberTextviewVal.text = "" + tblFacilities[0].FACNo


            binding.officeTextviewVal.text = if (tblOfficeType[0].OfficeName.equals("")) "" else tblOfficeType[0].OfficeName

            binding.assignedtoTextviewVal.text = tblFacilities[0].AssignedTo
            binding.dbaTextviewVal.text = tblFacilities[0].BusinessName
            binding.entityTextviewVal.text = tblFacilities[0].EntityName
            if (tblBusinessType.size>0) {
                    binding.bustypeTextviewVal.setSelection(busTypeArray.indexOf(tblFacilities[0].BusTypeID.toString()))
                } else {
                    binding.bustypeTextviewVal.setSelection(0)
                }
                if (tblTimezoneType.size>0) {
                    binding.timeZoneSpinner.setSelection(timeZoneArray.indexOf(tblTimezoneType[0].TimezoneName))
                } else {
                    binding.timeZoneSpinner.setSelection(0)
                }
            binding.timeZoneSpinner.tag = binding.timeZoneSpinner.selectedItemPosition
            binding.websiteTextviewVal.setText(tblFacilities[0].WebSite)
            binding.wifiAvailableCheckBox.isChecked = tblFacilities[0].InternetAccess
            binding.taxnoTextviewVal.text = tblFacilities[0].TaxIDNumber
            binding.repairorderTextviewVal.setText("" + tblFacilities[0].FacilityRepairOrderCount)
                if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==tblFacilities[0].SvcAvailability}.size > 0) {
                    binding.availabilityTextviewVal.setSelection(svcAvailabilityArray.indexOf(TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == tblFacilities[0].SvcAvailability }[0].SrvAvaName))
                } else {
                    binding.availabilityTextviewVal.setSelection(0)
                }
            binding.availabilityTextviewVal.tag = binding.availabilityTextviewVal.selectedItemPosition
                if (tblFacilityType.size>0) {
                    binding.facilitytypeTextviewVal.setSelection(facTypeArray.indexOf(tblFacilityType[0].FacilityTypeName))
                } else {
                    binding.facilitytypeTextviewVal.setSelection(0)
                }
            binding.facilitytypeTextviewVal.tag = binding.facilitytypeTextviewVal.selectedItemPosition
            binding.ARDnoTextviewVal.setText(tblFacilities[0].AutomotiveRepairNumber)
            binding.ARDexpTextviewVal.setText(tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY())
            binding.terminationDateButton.text = if (tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY()
                //SAEED
            binding.terminationCommentEditText.setText(""+tblFacilities[0].TerminationComments)

            if (tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900") || tblFacilities[0].TerminationDate.equals("") ){
                binding.terminationReasonTextviewVal.visibility=View.GONE
            } else {
                binding.terminationReasonTextviewVal.setSelection(termReasonArray.indexOf(tblTerminationCodeType[0].TerminationCodeName))
            }
//                if (!tblFacilities[0].TerminationDate.equals("")) {
//                    terminationReason_textviewVal.setSelection(termReasonArray.indexOf(tblTerminationCodeType[0].TerminationCodeName))
//                } else {
//                    terminationReason_textviewVal.visibility=View.GONE

                binding.currcodateTextviewVal.text = tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY()
            binding.initcodateTextviewVal.text = tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY()
                if (tblFacilities[0].InsuranceExpDate.contains('T')) {
                    binding.InsuranceExpDateTextviewVal.text = tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY()
                } else {
                    binding.InsuranceExpDateTextviewVal.text = tblFacilities[0].InsuranceExpDate
                }


            binding.inspectionCycleTextViewVal.text = if (tblFacilities[0].InspectionCycle.isNullOrEmpty()) "" else tblFacilities[0].InspectionCycle
            binding.inspectionMonthsTextViewVal.text = tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName()
                var FacManagersList = ""
                var AdminsList = ""
                for (facMgr in tblFacilityManagers) {
                    if (!facMgr.Manager.isNullOrEmpty()) FacManagersList  += facMgr.Manager+", "
                }
                binding.managerTextviewVal.text = FacManagersList.removeSuffix(", ")
                for (facAdmin in tblFacilities) {
                    if (!facAdmin.AdminAssistants.isNullOrEmpty())  AdminsList += facAdmin.AdminAssistants+", "
                }
            binding.adminTextviewVal.text = AdminsList.removeSuffix(", ")

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

        if (requireArguments().getBoolean(isValidating)) {
            validateInputs()
        }

//        trackingTableLayout

//        if (PRGDataModel.getInstance().tblPRGFacilityDetails.isNotEmpty()) {
//            affiliateNAPAEditText.setText(PRGDataModel.getInstance().tblPRGFacilityDetails[0].napanumber)
//            affiliateNationalEditText.setText(PRGDataModel.getInstance().tblPRGFacilityDetails[0].nationalnumber)
//        }

        setPaymentMethods()


        binding.saveButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()) {
                    if (submitPaymentRequired) submitPaymentMethods()
                    if (submitGeneralInfoRequired) submitFacilityGeneralInfo()
                }   else {
                    Utility.showValidationAlertDialog(activity,"Please fill all required fields")
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.cancelButton.hideKeyboard()
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
//            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
            Utility.showUnifiedConfirmationDialog(activity,  "Changes cancelled successfully")
        }
    }
    fun fillPortalTrackingTableView() {

        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (binding.trackingTableLayout.childCount>1) {
            for (i in binding.trackingTableLayout.childCount - 1 downTo 1) {
                binding.trackingTableLayout.removeViewAt(i)
            }

        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin=10
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.5F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 0.5F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam3.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblAffiliateVendorFacilities.apply {
            (0 until size).forEach {
//                if (!get(it).AffiliateVendorFacilityID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30
//                    tableRow.weightSum = 4.5F

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam
                    textView1.gravity = Gravity.CENTER
                    textView1.textSize = 14f
                    textView1.minimumHeight = 30
                    textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView1.text = get(it).AffiliateVendorName
                    tableRow.addView(textView1)

                    val editView2 = EditText(context)
                    editView2.layoutParams = rowLayoutParam1
                    editView2.gravity = Gravity.CENTER
                    editView2.textSize = 14f
                    editView2.minimumHeight = 30
                    editView2.setBackgroundColor(Color.WHITE)
                    editView2.inputType = InputType.TYPE_CLASS_NUMBER
                    editView2.setText(get(it).AffiliateVendor)
                    editView2.textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                    tableRow.addView(editView2)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam2
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = if (get(it).AffiliateVendor.isNullOrEmpty()) "ADD" else "EDIT"
                    updateButton.tag = if (get(it).AffiliateVendorFacilityID.isNullOrEmpty()) 0 else get(it).AffiliateVendorFacilityID
                    updateButton.textSize = 14f
                    updateButton.minimumHeight = 30
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)

                    updateButton.setOnClickListener {
                        if (editView2.text.toString().isNullOrEmpty()) {
                            Utility.showValidationAlertDialog(activity, "Please fill  the Affiliate vendor number")
                        } else {
                            binding.progressBarText.text = "Saving ..."
                            binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
                            var currentTableRowIndex = binding.trackingTableLayout.indexOfChild(tableRow)
                            var affVendorTypeID = FacilityDataModel.getInstance().tblAffiliateVendorFacilities.filter { s -> s.AffiliateVendorName.equals(textView1.text.toString()) }[0].AffiliateVendorTypeID
                            var affVendor = editView2.text.toString()
                            var affVendorID = updateButton.tag.toString()
                            var strChanges = if (updateButton.tag == 0) "Affiliate Vendor Number for " + textView1.text + " added to be " + affVendor else "Affiliate Vendor Number for " + textView1.text + " updated to be " + affVendor
                            var currentfacilityDataModelIndex = currentTableRowIndex - 1
                            Log.v("Aff Vendor Add --- ", Constants.AddAffiliateVendorFacilities + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&affVendorTypeID=${affVendorTypeID}&affVendor=$affVendor&affVendorID=$affVendorID&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat())
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.AddAffiliateVendorFacilities + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&affVendorTypeID=${affVendorTypeID}&affVendor=$affVendor&affVendorID=$affVendorID&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 1, strChanges),
                                    Response.Listener { response ->
                                        requireActivity().runOnUiThread {
                                            if (response.toString().contains("returnCode>0<", false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Affiliate Vendor")
                                                HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityGeneral = true
                                                HasChangedModel.getInstance().checkGeneralInfoTblAffiliateVendor()
                                                FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendor = affVendor
                                                FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorName = textView1.text.toString()
                                                FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorTypeID = affVendorTypeID
                                                FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorFacilityID = response.toString().substring(response.toString().indexOf("<AffiliateVendorFacilityID") + 27, response.toString().indexOf("</AffiliateVendorFacilityID"))
                                                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendor = affVendor
                                                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorName = textView1.text.toString()
                                                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorTypeID = affVendorTypeID
                                                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorFacilityID = FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorFacilityID
                                                fillPortalTrackingTableView()
                                                binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                                                (activity as FormsActivity).saveDone = true
                                                binding.progressBarText.text = "Loading ..."
                                            } else {
                                                binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                                                binding.progressBarText.text = "Loading ..."
                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                Utility.showSubmitAlertDialog(activity, false, "Affiliate Vendor (Error: " + errorMessage + " )")
                                            }
                                            (activity as FormsActivity).overrideBackButton = false
                                        }
                                    }, Response.ErrorListener {
                                Utility.showSubmitAlertDialog(activity, false, "Affiliate Vendor (Error: " + it.message + " )")
                                    binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                                    binding.progressBarText.text = "Loading ..."
                                (activity as FormsActivity).overrideBackButton = false
                            }))
//                    }
                        }
                    }
                    val deleteButton = Button(context)
                    deleteButton.layoutParams = rowLayoutParam3
                    deleteButton.setTextColor(Color.BLUE)
                    deleteButton.text = if (get(it).AffiliateVendor.isNullOrEmpty()) "" else "DELETE"
                    deleteButton.tag = if (get(it).AffiliateVendorFacilityID.isNullOrEmpty()) 0 else get(it).AffiliateVendorFacilityID
                    deleteButton.textSize = 14f
                    deleteButton.minimumHeight = 30
                    deleteButton.gravity = Gravity.CENTER
                    deleteButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(deleteButton)


                deleteButton.setOnClickListener {
                    binding.progressBarText.text = "Saving ..."
                    binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
                    var currentTableRowIndex = binding.trackingTableLayout.indexOfChild(tableRow)
//                    var affVendorTypeID = FacilityDataModel.getInstance().tblAffiliateVendorFacilities.filter { s->s.AffiliateVendorName.equals(textView1.text.toString())}[0].AffiliateVendorTypeID
//                    var affVendor = editView2.text.toString()
                    var affVendorID = updateButton.tag.toString()
                    var strChanges = "Affiliate Vendor Number for "+textView1.text.toString()+" deleted"
                    var currentfacilityDataModelIndex = currentTableRowIndex - 1

                    Log.v("Aff Vendor Delete --- ", Constants.DeleteAffiliateVendorFacilities + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&affVendorID=$affVendorID&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat())
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.DeleteAffiliateVendorFacilities + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&affVendorID=$affVendorID&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 1, strChanges),
                            { response ->
                                requireActivity().runOnUiThread {
                                    if (response.toString().contains("returnCode>0<", false)) {
                                        Utility.showSubmitAlertDialog(activity, true, "Affiliate Vendor")
                                        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityGeneral = true
                                        HasChangedModel.getInstance().checkGeneralInfoTblAffiliateVendor()
                                        FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendor = ""
                                        FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorName = textView1.text.toString()
                                        FacilityDataModel.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorFacilityID = ""
                                        FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendor = ""
                                        FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorFacilityID = ""
                                        FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities[currentfacilityDataModelIndex].AffiliateVendorName = textView1.text.toString()
                                        fillPortalTrackingTableView()
                                        (activity as FormsActivity).saveDone = true
                                        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                                        binding.progressBarText.text = "Loading ..."
                                    } else {
                                        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                                        binding.progressBarText.text = "Loading ..."
                                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                        Utility.showSubmitAlertDialog(activity, false, "Affiliate Vendor (Error: "+ errorMessage+" )")
                                    }
                                    (activity as FormsActivity).overrideBackButton = false
                                }
                            }, {
                        Utility.showSubmitAlertDialog(activity, false, "Affiliate Vendor (Error: "+it.message+" )")
                            binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                            binding.progressBarText.text = "Loading ..."
                        (activity as FormsActivity).overrideBackButton = false
                    }))
//                    }
                }


                binding.trackingTableLayout.addView(tableRow)

            }

        }
        altTableRow(2)
    }

    fun altTableRow(alt_row: Int) {
        var childViewCount = binding.trackingTableLayout.getChildCount();


        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.trackingTableLayout.getChildAt(i) as TableRow;

//            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                        R.drawable.alt_row_color);
//            } else {
//                row.background = getResources().getDrawable(
//                        R.drawable.row_color);
//            }

        }
    }

    fun ImplementBusinessRules() {
        binding.activeRadioButton.isClickable = false
        binding.inActiveRadioButton.isClickable = false
        binding.activeRadioButton.isEnabled = false
        binding.inActiveRadioButton.isEnabled = false
        binding.contractTypeValueSpinner.isEnabled = false
        binding.aarCheckBox.isClickable = false
        binding.aarEditText.isEnabled=false
        binding.aabCheckBox.isClickable = false
        binding.aabEditText.isEnabled=false
        binding.aagCheckBox.isClickable = false
        binding.aagEditText.isEnabled=false
        binding.cogCheckBox.isClickable = false
        binding.cogEditText.isEnabled=false
        binding.corCheckBox.isClickable = false
        binding.corEditText.isEnabled=false
        binding.ersCheckBox.isClickable = false
        binding.ersEditText.isEnabled=false
        binding.mprCheckBox.isClickable = false
        binding.mprEditText.isEnabled=false
        binding.pspCheckBox.isClickable = false
        binding.pspEditText.isEnabled=false
        binding.officeTextviewVal.isEnabled = false
        binding.assignedtoTextviewVal.isEnabled = false
        binding.dbaTextviewVal.isEnabled = false
        binding.entityTextviewVal.isEnabled = false
        binding.bustypeTextviewVal.isEnabled = false
        binding.terminationDateButton.isClickable = false
        binding.terminationDateButton.isEnabled = false
        binding.terminationReasonTextviewVal.isEnabled=false
        binding.terminationCommentEditText.isEnabled=false
        binding.inspectionMonthsTextViewVal.isEnabled = false
        binding.inspectionCycleTextViewVal.isEnabled=false
        binding.ARDnoTextviewVal.isEnabled=false
        binding.currcodateTextviewVal.isEnabled=false
        binding.initcodateTextviewVal.isEnabled=false
        binding.InsuranceExpDateTextviewVal.isEnabled=true
    }
    private fun setFieldsListeners(){
        binding.ARDexpTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.ARDexpTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.ARDexpTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                binding.ARDexpTextviewVal!!.setText(sdf.format(c.time))
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

        binding.InsuranceExpDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                binding.InsuranceExpDateTextviewVal.setText(sdf.format(c.time))
                FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate = sdf.format(c.time)
                HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                (activity as FormsActivity).saveRequired = true
                submitGeneralInfoRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        binding.timeZoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.timeZoneSpinner.tag.equals(p2) || binding.timeZoneSpinner.tag.equals("-1")) {
                    binding.timeZoneSpinner.tag = "-1"
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

        binding.websiteTextviewVal.addTextChangedListener(object : TextWatcher{
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

//        affiliateNationalEditText.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//                HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityNationalNo = true
//                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
//                (activity as FormsActivity).saveRequired = true
//                submitGeneralInfoRequired = true
//                refreshButtonsState()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//        })
//
//        affiliateNAPAEditText.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//                HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityNAPANo = true
//                HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
//                (activity as FormsActivity).saveRequired = true
//                submitGeneralInfoRequired = true
//                refreshButtonsState()
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//        })

        binding.wifiAvailableCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = b
            HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
            HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
            (activity as FormsActivity).saveRequired = true
            submitGeneralInfoRequired = true
            refreshButtonsState()
        }

        binding.repairorderTextviewVal.addTextChangedListener(object : TextWatcher{
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

        binding.availabilityTextviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.availabilityTextviewVal.tag.equals(p2) || binding.availabilityTextviewVal.tag.equals("-1")) {
                    binding.availabilityTextviewVal.tag = "-1"
                    FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName == svcAvailabilityArray[p2] }[0].SrvAvaID
                    HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                    HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true
                    refreshButtonsState()
                }
            }
        }

        binding.facilitytypeTextviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (!binding.facilitytypeTextviewVal.tag.equals(p2) || binding.facilitytypeTextviewVal.tag.equals("-1")) {
                    binding.facilitytypeTextviewVal.tag = "-1"
                    FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = facTypeArray[p2]
                    HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType = (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName != FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName)
                    HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()

                    (activity as FormsActivity).saveRequired = true
                    submitGeneralInfoRequired = true

                    refreshButtonsState()
                }
            }



        }
        binding.visaCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(1, b)
        }

        binding.mastercardCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(2, b)
        }

        binding.americanexpressCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(3, b)
        }

        binding.discoverCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(4, b)
        }

        binding.paypalCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(5, b)
        }

        binding.debitCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(6, b)
        }

        binding.cashCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(7, b)
        }

        binding.checkCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(8, b)
        }

        binding.goodyearCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(9, b)
        }

        binding.appleCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(10, b)
        }

        binding.zelleCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(12, b)
        }

        binding.venmoCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            handlePaymentMethodsSelection(11, b)
        }

    }

    fun validateInputs() : Boolean{

        var facValide= TblFacilities().isInputsValid

        facValide=true

        binding.timezoneTextview.setError(null)
        binding.repairorderTextviewVal.setError(null)
        binding.availabilityTextview.setError(null)
        binding.facilitytypeTextview.setError(null)
        binding.ARDnoTextviewVal.setError(null)
        binding.paymentMethodsTextview.setError(null)



        if (binding.timeZoneSpinner.selectedItem.toString().isNullOrEmpty()){
            binding.timezoneTextview.setError("reqiured field")
            facValide=false

        }

        if (binding.facilitytypeTextviewVal.selectedItem.toString().isNullOrEmpty()){
            binding.facilitytypeTextview .setError("reqiured field")
            facValide=false
        }
        if (binding.availabilityTextviewVal.selectedItem.toString().isNullOrEmpty()){
            binding.availabilityTextview.setError("reqiured field")
            facValide=false
        }
        if (binding.repairorderTextviewVal.text.toString().isNullOrEmpty()){
            binding.repairorderTextviewVal.setError("reqiured field")
            facValide=false
        }

        if (binding.ARDexpTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            binding.ARDexpTextviewVal.setError("Required Field")
            facValide=false
        }

        if (!binding.americanexpressCheckbox.isChecked && !binding.cashCheckbox.isChecked && !binding.checkCheckbox.isChecked && !binding.debitCheckbox.isChecked && !binding.discoverCheckbox.isChecked && !binding.goodyearCheckbox.isChecked && !binding.mastercardCheckbox.isChecked && !binding.paypalCheckbox.isChecked && !binding.visaCheckbox.isChecked){
            binding.paymentMethodsTextview.setError("Required Field")
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
        binding.visaCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==1 }.size>0)
        binding.mastercardCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==2 }.size>0)
        binding.americanexpressCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==3 }.size>0)
        binding.discoverCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==4 }.size>0)
        binding.paypalCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==5 }.size>0)
        binding.debitCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==6 }.size>0)
        binding.cashCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==7 }.size>0)
        binding.checkCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==8 }.size>0)
        binding.goodyearCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==9 }.size>0)
        binding.appleCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==10 }.size>0)
        binding.venmoCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==11 }.size>0)
        binding.zelleCheckbox.isChecked = (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.toInt()==12 }.size>0)

    }

    fun getGeneralInfoChanges() : String {
        var strChanges = ""
        try {
            if (FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount != FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount) {
                strChanges += "Repair order count changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount + ") to (" + FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount + ") - "
            }
            if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName != FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName) {
                strChanges += "Time Zone changed from (" + FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName + ") to (" + FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName + ") - "
            }
            if (FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability != FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability) {
                strChanges += "Service Availability changed from (" + TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID.equals(FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability) }[0].SrvAvaName + ") to (" + TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID.equals(FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability) }[0].SrvAvaName + ") - "
            }
            if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate != FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate) {
                strChanges += "ARD Expiration date changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY() + ") to (" + FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate + ") - "
            }
            if (FacilityDataModel.getInstance().tblFacilities[0].WebSite != FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite) {
                strChanges += "Website URL changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite + ") to (" + FacilityDataModel.getInstance().tblFacilities[0].WebSite + ") - "
            }
            if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess != FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess) {
                strChanges += "Wi-Fi Availability changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess + ") to (" + FacilityDataModel.getInstance().tblFacilities[0].InternetAccess + ") - "
            }
            if (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName != FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName) {
                strChanges += "Facility Type changed from (" + FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName + ") to (" + FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName + ") - "
            }

        } catch (e: Exception) {

        }
        strChanges = strChanges.removeSuffix(" - ")
        return if (strChanges.isNullOrEmpty()) "No Changes" else strChanges
    }

    fun submitFacilityGeneralInfo(){
        var busName =  if (binding.dbaTextviewVal.text.isNullOrEmpty())  "" else binding.dbaTextviewVal.text
        busName = URLEncoder.encode(busName.toString() , "UTF-8");

//        busName = URLEncoder.encode(busName.toString() , "UTF-8");
        val busType = TypeTablesModel.getInstance().BusinessType.filter { s -> s.BusTypeName==binding.bustypeTextviewVal.selectedItem.toString()}[0].BusTypeID
        var entityName =  if (binding.entityTextviewVal.text.isNullOrEmpty())  "" else binding.entityTextviewVal.text
        entityName = URLEncoder.encode(entityName.toString() , "UTF-8");
//        val assignedTo = if (assignedto_textviewVal.text.isNullOrEmpty())  "" else assignedto_textviewVal.text // get the ID
        val assignedTo = if (binding.assignedtoTextviewVal.text.isNullOrEmpty())  "" else FacilityDataModel.getInstance().tblFacilities[0].assignedToID
        val officeID = if (binding.officeTextviewVal.text.isNullOrEmpty())  "" else FacilityDataModel.getInstance().tblFacilities[0].officeID // get The ID
        val taxIDNo = if (binding.taxnoTextviewVal.text.isNullOrEmpty())  "" else binding.taxnoTextviewVal.text
        val facRepairCnt = if (binding.repairorderTextviewVal.text.isNullOrEmpty())  "" else binding.repairorderTextviewVal.text
        val inspectionMonth = (FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth).toString()
        val inspectionCycle = binding.inspectionCycleTextViewVal.text.toString()
        val timeZoneID = TypeTablesModel.getInstance().TimezoneType.filter { s->s.TimezoneName.equals(binding.timeZoneSpinner.selectedItem.toString()) }[0].TimezoneID
        val svcAvailability= TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName==binding.availabilityTextviewVal.selectedItem.toString()}[0].SrvAvaID
        val facType = TypeTablesModel.getInstance().FacilityType.filter { s -> s.FacilityTypeName==binding.facilitytypeTextviewVal.selectedItem.toString()}[0].FacilityTypeID
        val automtiveRepairNo = if (binding.ARDnoTextviewVal.text.isNullOrEmpty())  "" else binding.ARDnoTextviewVal.text
        val automtiveRepairExpDate = if (binding.ARDexpTextviewVal.text.equals("")) "" else binding.ARDexpTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractCurrDate = if (binding.currcodateTextviewVal.text.equals("")) "" else binding.currcodateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractInitDate = if (binding.initcodateTextviewVal.text.equals("")) "" else binding.initcodateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val internetAccess = if (binding.wifiAvailableCheckBox.isChecked) "1" else "0"
        val webSite = if (binding.websiteTextviewVal.text.isNullOrEmpty())  "" else binding.websiteTextviewVal.text
        val terminationDate = if (binding.terminationDateButton.text.equals("")) "" else binding.terminationDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
        var terminationReasonID = if (binding.terminationReasonTextviewVal.isVisible) TypeTablesModel.getInstance().TerminationCodeType.filter { s -> s.TerminationCodeName==binding.terminationReasonTextviewVal.selectedItem.toString()}[0].TerminationCodeID else 0
        val terminationComments = if (binding.terminationCommentEditText.text.isNullOrEmpty())  "" else binding.terminationCommentEditText.text
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val activeVal = "0"
        val insuranceExpDate = if (binding.InsuranceExpDateTextviewVal.text.equals("")) "" else binding.InsuranceExpDateTextviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
        val contractType = TypeTablesModel.getInstance().ContractType.filter { s -> s.ContractTypeName==binding.contractTypeValueSpinner.selectedItem.toString()}[0].ContractTypeID
        val billingMonth = FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.toString()
        val billingAmount = FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toString()
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val statusComment = binding.statusCommentEditText.text.toString()
//        val napaNumber = affiliateNAPAEditText.text.toString()
//        val nationalNumber = affiliateNationalEditText.text.toString()
//        FacilityDataModel.getInstance().tblFacilities[0]
        binding.progressBarText.text = "Saving ..."
        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
        var urlString = facilityNo+"&clubCode="+clubCode+"&businessName="+busName+"&busTypeId="+busType+"&entityName="+entityName+"&assignToId="+assignedTo+"&officeId="+officeID+"&taxIdNumber="+taxIDNo+"&facilityRepairOrderCount="+facRepairCnt+"&facilityAnnualInspectionMonth="+inspectionMonth.toString()+"&inspectionCycle="+inspectionCycle+"&timeZoneId="+timeZoneID.toString()+"&svcAvailability="+svcAvailability+"&facilityTypeId="+facType+"&automotiveRepairNumber="+automtiveRepairNo+"&automotiveRepairExpDate="+automtiveRepairExpDate+"&contractCurrentDate="+contractCurrDate+"&contractInitialDate="+contractInitDate+"&billingMonth="+billingMonth+"&billingAmount="+billingAmount+"&internetAccess="+internetAccess+"&webSite="+webSite+"&terminationDate="+terminationDate+"&terminationId="+terminationReasonID+"&terminationComments="+terminationComments+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&active=${FacilityDataModel.getInstance().tblFacilities[0].ACTIVE}&achParticipant=0&insuranceExpDate="+insuranceExpDate.toString()+"&contractTypeId="+contractType+"&statusComments="+statusComment
//
        val changes = getGeneralInfoChanges()
        Log.v("Facility General --- ",Constants.submitFacilityGeneralInfo + urlString + Utility.getLoggingParameters(activity, 0, ""))
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.submitFacilityGeneralInfo + urlString +Utility.getLoggingParameters(activity,0,changes),
            { response ->
                requireActivity().runOnUiThread {
                    Log.v("RESPONSE",response.toString())
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Facility General Information","General Info","",changes)
                        Utility.showSubmitAlertDialog(activity, true, "Facility General Information")
                        FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName = binding.timeZoneSpinner.selectedItem.toString()
                        FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount = facRepairCnt.toString().toInt()
                        FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability = svcAvailability
                        FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate = automtiveRepairExpDate
                        FacilityDataModel.getInstance().tblFacilities[0].WebSite = webSite.toString()
                        FacilityDataModel.getInstance().tblFacilities[0].InternetAccess = internetAccess.toBoolean()
                        FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate = insuranceExpDate
                        FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName = binding.facilitytypeTextviewVal.selectedItem.toString()
                        FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName = binding.timeZoneSpinner.selectedItem.toString()
                        FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount = facRepairCnt.toString().toInt()
                        FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability = svcAvailability
                        FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate = automtiveRepairExpDate
                        FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite = webSite.toString()
                        FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess = internetAccess.toBoolean()
                        FacilityDataModelOrg.getInstance().tblFacilities[0].InsuranceExpDate = insuranceExpDate
                        FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName = binding.facilitytypeTextviewVal.selectedItem.toString()

                        (activity as FormsActivity).saveRequired = false
                        (activity as FormsActivity).saveDone = true
                        submitGeneralInfoRequired = false
                        HasChangedModel.getInstance().checkGeneralInfoTblFacilitiesChange()
                        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityTimeZone=true
                        HasChangedModel.getInstance().groupFacilityGeneralInfo[0].FacilityType=true
                        HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo()
                        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                        setAlertColoring()
                        binding.progressBarText.text = "Loading ..."
                    } else {
                        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                        binding.progressBarText.text = "Loading ..."
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity,false,"Facility General Information (Error: "+ errorMessage+" )")
                    }
                }
            },
            {
            binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
            binding.progressBarText.text = "Loading ..."
        Utility.showSubmitAlertDialog(activity,false,"Facility General Information (Error: "+it.message+" )")
    }))
    }

    fun getPaymentDataChanges() : String {
        var strChanges = ""
        var strPrefix = "Payment method(s) ("
        var strAdded = ""
        var strRemoved = ""
        try {
            if (binding.visaCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 1 }
                    .isEmpty()) {
                strAdded += "VISA - "
            }
            if (binding.mastercardCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 2 }
                    .isEmpty()) {
                strAdded += "Master Card - "
            }
            if (binding.americanexpressCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 3 }
                    .isEmpty()) {
                strAdded += "American Express - "
            }
            if (binding.discoverCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 4 }
                    .isEmpty()) {
                strAdded += "Discover - "
            }
            if (binding.paypalCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 5 }
                    .isEmpty()) {
                strAdded += "PayPal - "
            }
            if (binding.debitCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 6 }
                    .isEmpty()) {
                strAdded += "Debit - "
            }
            if (binding.cashCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 7 }
                    .isEmpty()) {
                strAdded += "Cash - "
            }
            if (binding.checkCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 8 }
                    .isEmpty()) {
                strAdded += "Check - "
            }
            if (binding.goodyearCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 9 }
                    .isEmpty()) {
                strAdded += "Goodyear Credit Card - "
            }
            if (binding.appleCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 10 }
                    .isEmpty()) {
                strAdded += "Apple Pay - "
            }
            if (binding.venmoCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 11 }
                    .isEmpty()) {
                strAdded += "Venmo - "
            }
            if (binding.zelleCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 12 }
                    .isEmpty()) {
                strAdded += "Zelle - "
            }
            if (!strAdded.isNullOrEmpty()) {
                strChanges += strPrefix + strAdded.removeSuffix(" - ") + ") added"
            }
            // REMOVED
            if (!binding.visaCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 1 }
                    .isNotEmpty()) {
                strRemoved += "VISA - "
            }
            if (!binding.mastercardCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 2 }
                    .isNotEmpty()) {
                strRemoved += "Master Card - "
            }
            if (!binding.americanexpressCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 3 }
                    .isNotEmpty()) {
                strRemoved += "American Express - "
            }
            if (!binding.discoverCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 4 }
                    .isNotEmpty()) {
                strRemoved += "Discover - "
            }
            if (!binding.paypalCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 5 }
                    .isNotEmpty()) {
                strRemoved += "PayPal - "
            }
            if (!binding.debitCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 6 }
                    .isNotEmpty()) {
                strRemoved += "Debit - "
            }
            if (!binding.cashCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 7 }
                    .isNotEmpty()) {
                strRemoved += "Cash - "
            }
            if (!binding.checkCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 8 }
                    .isNotEmpty()) {
                strRemoved += "Check - "
            }
            if (!binding.goodyearCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 9 }
                    .isNotEmpty()) {
                strRemoved += "Goodyear Credit Card - "
            }
            if (!binding.appleCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 10 }
                    .isNotEmpty()) {
                strRemoved += "Apple Pay - "
            }
            if (!binding.venmoCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 11 }
                    .isNotEmpty()) {
                strRemoved += "Venmo - "
            }
            if (!binding.zelleCheckbox.isChecked && FacilityDataModelOrg.getInstance().tblPaymentMethods.filter { s -> s.PmtMethodID.toInt() == 12 }
                    .isNotEmpty()) {
                strRemoved += "Zelle - "
            }
            if (!strRemoved.isNullOrEmpty()) {
                if (strAdded.isNotEmpty()) strChanges += " | "
                strChanges += strPrefix + strRemoved.removeSuffix(" - ") + ") removed"
            }
        } catch (e: Exception) {

        }
        return strChanges
    }

    fun submitPaymentMethods(){


        var insertDate = Date().toAppFormatMMDDYYYY()

                //  BuildProgramsList()

          val visa : String=   if (binding.visaCheckbox.isChecked == true) "1" else ""
          val mastercard: String=   if (binding.mastercardCheckbox.isChecked == true) "2" else ""
          val americanexpress: String=   if (binding.americanexpressCheckbox.isChecked == true) "3" else ""
          val discover: String=   if (binding.discoverCheckbox.isChecked == true) "4" else ""
          val paypal: String=   if (binding.paypalCheckbox.isChecked == true) "5" else ""
          val debit: String=   if (binding.debitCheckbox.isChecked == true) "6" else ""
          val cash: String=   if (binding.cashCheckbox.isChecked == true) "7" else ""
          val check: String=   if (binding.checkCheckbox.isChecked == true) "8" else ""
          val goodyear: String=   if (binding.goodyearCheckbox.isChecked == true) "9" else ""
          val applepay: String=   if (binding.appleCheckbox.isChecked == true) "10" else ""
          val venmo: String=   if (binding.venmoCheckbox.isChecked == true) "11" else ""
          val zelle: String=   if (binding.zelleCheckbox.isChecked == true) "12" else ""
          var paymentMethods= arrayOf(visa,mastercard,americanexpress,discover,paypal,debit,cash,check,goodyear,applepay,venmo,zelle)
        var paymentMethodArray = ArrayList<String>()
        binding.progressBarText.text = "Saving ..."
        binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
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
            { response ->
                requireActivity().runOnUiThread {
                    if (response.toString().contains("returnCode>0<",false)) {
                        HasChangedModel.getInstance().updateChangedData("Facility General Information","Payment Methods","",getPaymentDataChanges())
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
                        (activity as FormsActivity).saveDone = true
                    } else {
                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                        Utility.showSubmitAlertDialog(activity,false,"Payment Methods (Error: "+ errorMessage+" )")
                    }
                    binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                }
            },
            {
    Utility.showSubmitAlertDialog(activity,false,"Payment Methods (Error: "+it.message+" )")
            binding.scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
            binding.progressBarText.text = "Loading ..."
    }))


    }

//    fun disableAllAddButnsAndDialog(){
//
//        for (i in 0 until mainViewLinearId.childCount)
//        {
//            val child = mainViewLinearId.getChildAt(i)
//            child.isEnabled = false
//        }
//
//        var childViewCount = aarPortalTrackingTableLayout.getChildCount();
//
//        for ( i in 1..childViewCount-1)
//        {
//            var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount() - 1) {
//
//                var tv: TextView = row.getChildAt(j) as TextView
//                tv.isEnabled = false
//
//            }
//
//        }
//    }

    fun refreshButtonsState(){

        binding.saveButton.isEnabled = (activity as FormsActivity).saveRequired
        binding.cancelButton.isEnabled = (activity as FormsActivity).saveRequired
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
