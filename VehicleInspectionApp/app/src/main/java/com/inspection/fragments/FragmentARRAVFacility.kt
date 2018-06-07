package com.inspection.fragments

import android.app.DatePickerDialog
//import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.R.id.*
import com.inspection.Utils.*
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacility.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacility.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacility : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFprmat = SimpleDateFormat("dd MMM yyyy")
    private var timeZonesArray = arrayOf("")
    private var facilityTypeArray = arrayOf("")


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fill Dop Down

        termReasonList = TypeTablesModel.getInstance().TerminationCodeType
        termReasonArray .clear()
        for (fac in termReasonList) {
            termReasonArray .add(fac.TerminationCodeName)
        }

        var termReasonAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, termReasonArray)
        termReasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        terminationReason_textviewVal.adapter = termReasonAdapter

        busTypeList = TypeTablesModel.getInstance().BusinessType
        busTypeArray .clear()
        for (fac in busTypeList) {
            busTypeArray .add(fac.BusTypeName)
        }

        var busTypeAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, busTypeArray)
        busTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bustype_textviewVal.adapter = busTypeAdapter


        timeZoneList = TypeTablesModel.getInstance().TimezoneType
        timeZoneArray .clear()
        for (fac in timeZoneList) {
            timeZoneArray .add(fac.TimezoneName)
        }

        var tzdataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, timeZonesArray)
        tzdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezone_textviewVal.adapter = tzdataAdapter

        svcAvailabilityList = TypeTablesModel.getInstance().ServiceAvailabilityType
        svcAvailabilityArray .clear()
        for (fac in svcAvailabilityList) {
            svcAvailabilityArray .add(fac.SrvAvaName)
        }
        var svcAvldataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, svcAvailabilityArray)
        svcAvldataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_textviewVal.adapter = svcAvldataAdapter

        facTypeList = TypeTablesModel.getInstance().FacilityType
        facTypeArray .clear()
        for (fac in facTypeList) {
            facTypeArray .add(fac.FacilityTypeName)
        }
        var facilityTypedataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, facTypeArray)
        facilityTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilitytype_textviewVal.adapter = facilityTypedataAdapter



        contractTypeList = TypeTablesModel.getInstance().ContractType
        contractTypeArray .clear()
        for (fac in contractTypeList) {
            contractTypeArray .add(fac.ContractTypeName)
        }

        var contractTypesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, contractTypeArray )
        contractTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractTypeValueSpinner.adapter = contractTypesAdapter


        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
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
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                InsuranceExpDate_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        setFieldsValues()
        ImplementBusinessRules()

        saveButton.setOnClickListener({
            submitFacilityGeneralInfo()
        })

    }


    private fun setFieldsValues() {
        FacilityDataModel.getInstance().apply {
            try {

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

                if (tblOfficeType[0].OfficeName.isNotEmpty()) {
                    office_textviewVal.text = "" + tblOfficeType[0].OfficeName
                }else{
                    office_textviewVal.text = ""
                }
                assignedto_textviewVal.text = tblFacilities[0].AssignedTo
                dba_textviewVal.text = tblFacilities[0].BusinessName
                entity_textviewVal.text = tblFacilities[0].EntityName
                bustype_textviewVal.setSelection(busTypeArray.indexOf(tblBusinessType[0].BusTypeName))

                timezone_textviewVal.setSelection(timeZonesArray.indexOf(tblTimezoneType[0].TimezoneName))
                website_textviewVal.setText(tblFacilities[0].WebSite)
                wifi_textview.isChecked = tblFacilities[0].SvcAvailability.toInt() == 1
                taxno_textviewVal.text = tblFacilities[0].TaxIDNumber
                repairorder_textviewVal.setText("" + tblFacilities[0].FacilityRepairOrderCount)
                availability_textviewVal.setSelection(tblFacilities[0].SvcAvailability)
                facilitytype_textviewVal.setSelection(facTypeArray.indexOf(tblFacilityType[0].FacilityTypeName))
                ARDno_textviewVal.setText(tblFacilities[0].AutomotiveRepairNumber)
                ARDexp_textviewVal.text = tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormat()
                terminationDateButton.text = ""+tblFacilities[0].TerminationDate.apiToAppFormat()
                //SAEED
                terminationCommentEditText.setText(""+tblFacilities[0].TerminationComments)
                terminationReason_textviewVal.setSelection(termReasonArray.indexOf(tblTerminationCodeType[0].TerminationCodeName))
                currcodate_textviewVal.text = tblFacilities[0].ContractCurrentDate.apiToAppFormat()
                initcodate_textviewVal.text = tblFacilities[0].ContractInitialDate.apiToAppFormat()
                InsuranceExpDate_textviewVal.text = tblFacilities[0].InsuranceExpDate.apiToAppFormat()

                inspectionMonthsSpinner.setSelection(tblFacilities[0].FacilityAnnualInspectionMonth-1)

                for(paymentMethod in tblPaymentMethods){

                }

            }catch (exp:Exception){
                exp.printStackTrace()
            }
        }

        if (arguments!!.getBoolean(isValidating)) {
            validateInputs()
        }

        setPaymentMethods()

        saveButton.setOnClickListener {
            if (validateInputs()){

            }
        }

    }


    fun ImplementBusinessRules() {
        activeRadioButton.isClickable = false
        inActiveRadioButton.isClickable = false
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
        terminationReason_textviewVal.isEnabled=false
        terminationCommentEditText.isEnabled=false
        inspectionMonthsSpinner.isEnabled = false
        inspectionCycleSpinner.isEnabled=false
        ARDno_textviewVal.isEnabled=false
        currcodate_textviewVal.isEnabled=false
        initcodate_textviewVal.isEnabled=false
        InsuranceExpDate_textviewVal.isEnabled=false

//
    }
    fun validateInputs() : Boolean{

        AnnualVisitationSingleton.getInstance().apply {
            if (ardNumber == -1){
                ARDno_textviewVal.error = ""
            }

            if (ardExpirationDate == -1L) {
                ARDexp_textviewVal.error = ""
            }

            if (insuranceExpirationDate == -1L) {
//                InsuranceExpDate_textviewVal.error = ""
            }
        }
        return true
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
        val officeID = if (office_textviewVal.text.isNullOrEmpty())  "" else office_textviewVal.text // get The ID
        val taxIDNo = if (taxno_textviewVal.text.isNullOrEmpty())  "" else taxno_textviewVal.text
        val facRepairCnt = if (repairorder_textviewVal.text.isNullOrEmpty())  "" else repairorder_textviewVal.text
        val inspectionMonth = (inspectionMonthsSpinner.selectedItemPosition + 1).toString()
        val inspectionCycle = inspectionCycleSpinner.selectedItem.toString()
        val timeZoneID = (timezone_textviewVal.selectedItemPosition+1).toString()
        val svcAvailability= TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaName==availability_textviewVal.selectedItem.toString()}[0].SrvAvaID
        val facType = TypeTablesModel.getInstance().FacilityType.filter { s -> s.FacilityTypeName==facilitytype_textviewVal.selectedItem.toString()}[0].FacilityTypeID
        val automtiveRepairNo = if (ARDno_textviewVal.text.isNullOrEmpty())  "" else ARDno_textviewVal.text
        val automtiveRepairExpDate = ARDexp_textviewVal.text.toString().appToApiFormat()
        val contractCurrDate = currcodate_textviewVal.text.toString().appToApiFormat()
        val contractInitDate = initcodate_textviewVal.text.toString().appToApiFormat()
        val internetAccess = if (wifi_textview.isChecked) "1" else "0"
        val webSite = if (website_textviewVal.text.isNullOrEmpty())  "" else website_textviewVal.text
        val terminationDate = terminationDateButton.text.toString().appToApiFormat()
        val terminationReasonID = TypeTablesModel.getInstance().TerminationCodeType.filter { s -> s.TerminationCodeName==terminationReason_textviewVal.selectedItem.toString()}[0].TerminationCodeID
        val terminationComments = if (terminationCommentEditText.text.isNullOrEmpty())  "" else terminationCommentEditText.text
        val insertDate = Date().toApiFormat()
        val insertBy ="sa"
        val updateDate = Date().toApiFormat()
        val updateBy ="sa"
        val activeVal = "0"
        val insuranceExpDate = InsuranceExpDate_textviewVal.text
        val contractType = TypeTablesModel.getInstance().ContractType.filter { s -> s.ContractTypeName==contractTypeValueSpinner.selectedItem.toString()}[0].ContractTypeID
        val billingMonth = FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.toString()
        val billingAmount = FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toString()
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode ="004"


        Log.v("*******parse", ""+automtiveRepairExpDate)
        var urlString = facilityNo+"&clubcode="+clubCode+"&businessName="+busName+"&busTypeId="+busType+"&entityName="+entityName+"&assignToId="+assignedTo+"&officeId="+officeID+"&taxIdNumber="+taxIDNo+"&facilityRepairOrderCount="+facRepairCnt+"&facilityAnnualInspectionMonth="+inspectionMonth.toString()+"&inspectionCycle="+inspectionCycle+"&timeZoneId="+timeZoneID.toString()+"&svcAvailability="+svcAvailability+"&facilityTypeId="+facType+"&automotiveRepairNumber="+automtiveRepairNo+"&automotiveRepairExpDate="+automtiveRepairExpDate+"&contractCurrentDate="+contractCurrDate+"&contractInitialDate="+contractInitDate+"&billingMonth="+billingMonth+"&billingAmount="+billingAmount+"&internetAccess="+internetAccess+"&webSite="+webSite+"&terminationDate="+terminationDate+"&terminationId="+terminationReasonID+"&terminationComments="+terminationComments+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate+"&active=0&achParticipant=0&insuranceExpDate="+insuranceExpDate.toString()+"&contractTypeId="+contractType
        Log.v("Data To Submit", urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.POST, Constants.submitFacilityGeneralInfo + urlString,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        Log.v("RESPONSE",response.toString())

                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading")
        }))
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
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVFacility {
            val fragment = FragmentARRAVFacility()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
