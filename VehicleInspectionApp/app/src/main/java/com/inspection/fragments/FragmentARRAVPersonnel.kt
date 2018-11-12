package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.*
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.app_adhoc_visitation_filter_fragment.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_personnel.*

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
    var zipFormat=true
    var selectedPersonnelID = 0
       var contractSignatureIsChecked=false
    private var mParam1: String? = null
    var countIfContractSignedBefore=0
    private var mParam2: String? = null
    private var personnelTypeList = ArrayList<TypeTablesModel.personnelType>()
    private var certificationTypeList= ArrayList<TypeTablesModel.personnelCertificationType>()
    private var states= arrayOf("Select State","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming")

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
        scopeOfServiceChangesWatcher()
        preparePersonnelPage()
        fillPersonnelTableView()
//        fillCertificationTableView()
        rspUserId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName.toString())
        rspEmailId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email.toString())

        IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited = true
        (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

//        AddNewCertBtn.isEnabled=false

        exitDialogeBtnId.setOnClickListener {



            addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        }
        edit_exitDialogeBtnId.setOnClickListener {



            edit_addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        }

        exitCertificateDialogeBtnId.setOnClickListener {

            addNewCertificateDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        }

        AddNewCertBtn.setOnClickListener {
            if (selectedPersonnelID.equals(0)) {
                Utility.showValidationAlertDialog(activity,"Please select the related personnel from the list")
            } else {
                newCertTypeSpinner.setSelection(0)
                newCertStartDateBtn.setText("SELECT DATE")
                newCertEndDateBtn.setText("SELECT DATE")
                newCertDescText.setText("")
                newCertStartDateBtn.setError(null)
                certTypeTextView.setError(null)
                addNewCertificateDialogue.visibility = View.VISIBLE
                alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
            }
        }
        addNewPersnRecordBtn.setOnClickListener {
            newFirstNameText.setText("")
            newLastNameText.setText("")
            newCertNoText.setText("")
            newStartDateBtn.setText("SELECT DATE")
            newEndDateBtn.setText("SELECT DATE")
            newSeniorityDateBtn.setText("SELECT DATE")
            newCoStartDateBtn.setText("SELECT DATE")
            newCoEndDateBtn.setText("SELECT DATE")
            newPhoneText.setText("")
            newZipText.setText("")
            newAdd1Text.setText("")
            newAdd2Text.setText("")
            newCityText.setText("")
            newZipText2.setText("")
            newEmailText.setText("")
            newStateSpinner.setSelection(0)
            newPersonnelTypeSpinner.setSelection(0)

            newZipText.setError(null)
            newZipText2.setError(null)
            newPhoneText.setError(null)
            newCertNoText.setError(null)
            stateTextView.setError(null)
            newEmailText.setError(null)
            personnelTypeTextViewId.setError(null)
            onlyOneContractSignerLogic()
            addNewPersonnelDialogue.visibility=View.VISIBLE
            alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
        }


        // contractSignerIsNotCheckedLogic()

        newCertStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newCertStartDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }






        newCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCoStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }
        edit_newEndDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        edit_newCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newCoStartDateBtn!!.text = sdf.format(c.time)
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
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newSeniorityDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }
        edit_newSeniorityDateBtn.setOnClickListener {
//            if (newSeniorityDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newSeniorityDateBtn!!.text = sdf.format(c.time)
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
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }
        edit_newStartDateBtn.setOnClickListener {
//            if (newStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }


        fillData()

        submitNewCertBtn.setOnClickListener {
            if (validateCertificationInputs()) {
                addNewCertificateDialogue.visibility=View.GONE
                alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                personnelLoadingText.text = "Saving ..."
                personnelLoadingView.visibility = View.VISIBLE

                var item = TblPersonnelCertification()
                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                    if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))

                        item.CertificationTypeId = fac.PersonnelCertID
                }

                item.CertificationDate = if (newCertStartDateBtn.text.equals("SELECT DATE")) "" else newCertStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.ExpirationDate = if (newCertEndDateBtn.text.equals("SELECT DATE")) "" else newCertEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.CertDesc = if (newCertDescText.text.isNullOrEmpty()) "" else newCertDescText.text.toString()
                item.PersonnelID = selectedPersonnelID

                var urlString = "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubcode=${FacilityDataModel.getInstance().clubCode}&PersonnelID=${selectedPersonnelID}"+
                        "&certID=&CertificationTypeId=${item.CertificationTypeId}&CertificationDate=${item.CertificationDate}&ExpirationDate=${item.ExpirationDate}"+
                        "&certDesc=${item.CertDesc}&insertBy=sa&insertDate=${Date().toApiSubmitFormat()}&updateBy=sa&updateDate=${Date().toApiSubmitFormat()}&active=1"
                Log.v("Data To Submit", urlString)
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdatePersonnelCertification + urlString,
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode&gt;0&", false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Certification")
                                    item.CertID= response.toString().substring(response.toString().indexOf(";CertID")+11,response.toString().indexOf("&lt;/CertID"))
                                    FacilityDataModel.getInstance().tblPersonnelCertification.add(item)
                                    HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                    HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                    fillCertificationTableView(selectedPersonnelID)
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf(";message") + 12, response.toString().indexOf("&lt;/message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Certification (Error: " + errorMessage + " )")
                                }
                                personnelLoadingView.visibility = View.GONE
                                personnelLoadingText.text = "Loading ..."
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Certification (Error: "+it.message+" )")
                    personnelLoadingView.visibility = View.GONE
                    personnelLoadingText.text = "Loading ..."

                }))
            }

        }

        submitNewPersnRecordBtn.setOnClickListener {

            if (validateInputs()){
                addNewPersonnelDialogue.visibility=View.GONE
                alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                personnelLoadingText.text = "Saving ..."
                personnelLoadingView.visibility = View.VISIBLE


                var PersonnelTypeId=""

                for (fac in TypeTablesModel.getInstance().PersonnelType) {
                    if (newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))

                        PersonnelTypeId =fac.PersonnelTypeID
                }

                var FirstName=if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString()
                var LastName=if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                var RSP_UserName=FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName
                var RSP_Email=FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email
                var facNo=FacilityDataModel.getInstance().tblFacilities[0].FACNo
                var CertificationNum=if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
                var ContractSigner=if (newSignerCheck.isChecked==true) "true" else "false"
                var PrimaryMailRecipient=if (newACSCheck.isChecked==true) "true" else "false"
                var startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var ExpirationDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPersonnelData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=sa&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}",
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode&gt;0&",false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Personnel")
                                    var item = TblPersonnel()
                                    for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                        if (newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))
                                            item.PersonnelTypeID = fac.PersonnelTypeID.toInt()
                                    }
                                    item.PersonnelID= response.toString().substring(response.toString().indexOf(";PersonnelID")+16,response.toString().indexOf("&lt;/PersonnelID")).toInt()
                                    item.FirstName = if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString()
                                    item.LastName = if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.RSP_UserName = if (rspUserId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.RSP_Email = if (rspEmailId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.CertificationNum = if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
                                    item.ContractSigner = if (newSignerCheck.isChecked == true) true else false
                                    item.PrimaryMailRecipient = if (newACSCheck.isChecked == true) true else false
                                    item.startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.ExpirationDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                    HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                    if (ContractSigner.toBoolean()){
                                        item.Addr1= if (newAdd1Text.text.toString().isNullOrEmpty()) "" else newAdd1Text.text.toString()
                                        item.Addr2= if (newAdd2Text.text.toString().isNullOrEmpty()) "" else newAdd2Text.text.toString()
                                        item.CITY= if (newCityText.text.toString().isNullOrEmpty()) "" else newCityText.text.toString()
                                        item.ST= if (newStateSpinner.selectedItem.toString().isNullOrEmpty()) "" else newStateSpinner.selectedItem.toString()
                                        item.ZIP= if (newZipText.text.toString().isNullOrEmpty()) "" else newZipText.text.toString()
                                        item.ZIP4= if (newZipText2.text.toString().isNullOrEmpty()) "" else newZipText2.text.toString()
                                        item.Phone= if (newPhoneText.text.equals("SELECT DATE")) "" else newPhoneText.text.toString()
                                        item.email= if (newEmailText.text.equals("SELECT DATE")) "" else newEmailText.text.toString()
                                        item.ContractStartDate = if (newCoStartDateBtn.text.equals("SELECT DATE")) "" else newCoStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                        item.ContractStartDate = if (newCoEndDateBtn.text.equals("SELECT DATE")) "" else newCoEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPersonnelSignerData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=&addr1=%22test%22&addr2=%22test%22&city=%22123&st=%22123%22&phone=%22123%22&email=%22123%22&zip=&zip4=&contractStartDate=&contractEndDate=&insertBy=sa&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat()+"&active=1",
                                                Response.Listener { response ->
                                                    activity!!.runOnUiThread {
                                                        if (response.toString().contains("returnCode&gt;0&",false)) {
                                                            Utility.showSubmitAlertDialog(activity, true, "Contract Signer")
                                                            FacilityDataModel.getInstance().tblPersonnel.add(item)
                                                            fillPersonnelTableView()
                                                            altTableRow(2)
//                                                            IndicatorsDataModel.getInstance().validateFacilityPersonnelVisited()
//                                                            if (IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited) (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#A42600"))
//                                                            (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                                        } else {
                                                            var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                                            Utility.showSubmitAlertDialog(activity,false,"Contract Signer (Error: "+ errorMessage+" )")
                                                        }
                                                        personnelLoadingView.visibility = View.GONE
                                                        personnelLoadingText.text = "Loading ..."
                                                    }
                                                }, Response.ErrorListener {
                                            Utility.showSubmitAlertDialog(activity, false, "Contract Signer (Error: "+it.message+" )")
                                            personnelLoadingView.visibility = View.GONE
                                            personnelLoadingText.text = "Loading ..."
                                        }))
                                    } else {
                                        FacilityDataModel.getInstance().tblPersonnel.add(item)
                                        fillPersonnelTableView()
                                        altTableRow(2)
//                                        IndicatorsDataModel.getInstance().validateFacilityPersonnelVisited()
//                                        if (IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited) (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#A42600"))
//                                        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                    }
                                    fillPersonnelTableView()
                                    altTableRow(2)
//                                    IndicatorsDataModel.getInstance().validateFacilityPersonnelVisited()
//                                    if (IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited) (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#A42600"))
//                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                    Utility.showSubmitAlertDialog(activity,false,"Personnel (Error: "+ errorMessage+" )")
                                }
                                personnelLoadingView.visibility = View.GONE
                                personnelLoadingText.text = "Loading ..."
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Personnel (Error: "+it.message+" )")
                    personnelLoadingView.visibility = View.GONE
                    personnelLoadingText.text = "Loading ..."
                }))

            }
            else
            {
                Utility.showValidationAlertDialog(activity,"Please fill all the required fields")
            }
        }
        onlyOneMailRecepientLogic()
        altTableRow(2)
        altCertTableRow(2)
    }


    var isFirstRun: Boolean = true

    fun enable_contractSignerFeilds(){

            newEmailText.isEnabled = true
            newCoStartDateBtn.isEnabled = true
            newPhoneText.isEnabled = true
            newZipText.isEnabled = true
            newCityText.isEnabled = true
            newAdd1Text.isEnabled = true
            newStateSpinner.isEnabled = true
            newZipText2.isEnabled = true
            newAdd2Text.isEnabled = true
            stateTextView.isEnabled = true
            phoneTextId.isEnabled = true
            zipCodeTextId.isEnabled = true
            emailAddressTextId.isEnabled = true
            contractSignerStartDateTextId.isEnabled = true
            contractSignerEndDateTextId.isEnabled = true
            newCoEndDateBtn.isEnabled = true
            cityTextId.isEnabled = true
            address2TextId.isEnabled = true
            address1TextId.isEnabled = true
            newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.blue));
            newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.blue));
            contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.white));

    }
    fun disablecontractSignerFeilds(){

            newEmailText.isEnabled = false
            newCoStartDateBtn.isEnabled = false
            newPhoneText.isEnabled = false
            newZipText.isEnabled = false
            newCityText.isEnabled = false
            newAdd1Text.isEnabled = false
            newStateSpinner.isEnabled = false
            newZipText2.isEnabled = false
            newAdd2Text.isEnabled = false
            stateTextView.isEnabled = false
            phoneTextId.isEnabled = false
            zipCodeTextId.isEnabled = false
            emailAddressTextId.isEnabled = false
            contractSignerStartDateTextId.isEnabled = false
            contractSignerEndDateTextId.isEnabled = false
            newCoEndDateBtn.isEnabled = false
            cityTextId.isEnabled = false
            address2TextId.isEnabled = false
            address1TextId.isEnabled = false
            newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
            newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
            contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.contractSignerFieldsAreDisabledColor));

    }
    fun edit_disableContractSignerIsChecked(){

            edit_newEmailText.isEnabled = false
            edit_newCoStartDateBtn.isEnabled = false
            edit_newPhoneText.isEnabled = false
            edit_newZipText.isEnabled = false
            edit_newCityText.isEnabled = false
            edit_newAdd1Text.isEnabled = false
            edit_newStateSpinner.isEnabled = false
            edit_newZipText2.isEnabled = false
            edit_newAdd2Text.isEnabled = false
            edit_stateTextView.isEnabled = false
            edit_phoneTextId.isEnabled = false
            edit_zipCodeTextId.isEnabled = false
            edit_emailAddressTextId.isEnabled = false
            edit_contractSignerStartDateTextId.isEnabled = false
            edit_contractSignerEndDateTextId.isEnabled = false
            edit_newCoEndDateBtn.isEnabled = false
            edit_cityTextId.isEnabled = false
            edit_address2TextId.isEnabled = false
            edit_address1TextId.isEnabled = false
            edit_newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
            edit_newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
            edit_contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.contractSignerFieldsAreDisabledColor));







    }
    fun edit_enableContractSignerIsChecked(){

            edit_newEmailText.isEnabled = true
            edit_newCoStartDateBtn.isEnabled = true
            edit_newPhoneText.isEnabled = true
            edit_newZipText.isEnabled = true
            edit_newCityText.isEnabled = true
            edit_newAdd1Text.isEnabled = true
            edit_newStateSpinner.isEnabled = true
            edit_newZipText2.isEnabled = true
            edit_newAdd2Text.isEnabled = true
            edit_stateTextView.isEnabled = true
            edit_phoneTextId.isEnabled = true
            edit_zipCodeTextId.isEnabled = true
            edit_emailAddressTextId.isEnabled = true
            edit_contractSignerStartDateTextId.isEnabled = true
            edit_contractSignerEndDateTextId.isEnabled = true
            edit_newCoEndDateBtn.isEnabled = true
            edit_cityTextId.isEnabled = true
            edit_address2TextId.isEnabled = true
            edit_address1TextId.isEnabled = true
            edit_newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.green));
            edit_newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.green));
            edit_contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.white));







    }

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
        edit_newPersonnelTypeSpinner.adapter = personTypeAdapter

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
        edit_newStateSpinner.adapter = citiesAdapter

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
        //newCertNoText.addTextChangedListener(certificateIdNoLengthWatcher)
        //2-
        endDateMustBeAfterStartDateLogic()
        edit_endDateMustBeAfterStartDateLogic()
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
                zipFormat=false

            }else zipFormat=true



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

                newCoEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Start Date")
//                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()
            }
            else {
                newCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCoEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
        newEndDateBtn.setOnClickListener(View.OnClickListener {
            if (newStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                newStartDateBtn.setError("Required Field")
//                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()
                Utility.showValidationAlertDialog(activity,"Please enter Start Date")
            }
            else {
                newEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
        newCertEndDateBtn.setOnClickListener {
            if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                newCertStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Certificate Start Date")
            }
            else {
                newCertStartDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCertEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        }
    }
    fun edit_endDateMustBeAfterStartDateLogic(){

        edit_newCoEndDateBtn.setOnClickListener(View.OnClickListener {
            if (edit_newCoStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){

                edit_newCoEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Contract End Date")
//                Toast.makeText(context,"please enter a start date first",Toast.LENGTH_LONG).show()
            }
            else {
                edit_newCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newCoEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        })
        edit_newEndDateBtn.setOnClickListener(View.OnClickListener {
            if (edit_newStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                edit_newEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter ِEnd Date")
            }
            else {
                edit_newEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newEndDateBtn!!.text = sdf.format(c.time)
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

        if (PersonnelResultsTbl.childCount>1) {
            for (i in PersonnelResultsTbl.childCount - 1 downTo 1) {
                PersonnelResultsTbl.removeViewAt(i)
            }
        }
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1.4F
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1.5F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1.5F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.width = 0

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.column = 9
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.width = 0

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.column = 10
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.width = 0
        var dateTobeFormated = ""

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 0.8F
        rowLayoutParam11.column = 11
        rowLayoutParam11.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.weight=1F

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)
                tableRow.layoutParams = rowLayoutParamRow

                tableRow.minimumHeight = 30

                tableRow.setOnClickListener {
                    altTableRow(2)
                    tableRow.setBackgroundColor(Color.GREEN)
                    var currentTableRowIndex=PersonnelResultsTbl.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex=currentTableRowIndex-1
                    certTextViewVal.text = "Personnel Certification(s) - ${FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].FirstName} ${FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].LastName}  "
                    fillCertificationTableView(FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID)
                    selectedPersonnelID = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID
                }
                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam
//                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView1.gravity = Gravity.CENTER_VERTICAL
                textView1.text = getTypeName(get(it).PersonnelTypeID.toString())
                textView1.minimumHeight = 30
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.gravity = Gravity.CENTER_VERTICAL
                textView2.text = get(it).FirstName
                textView2.minimumHeight = 30
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.gravity = Gravity.CENTER_VERTICAL
                textView3.minimumHeight = 30
                textView3.text = get(it).LastName
                tableRow.addView(textView3)

                val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.gravity = Gravity.CENTER_VERTICAL
                textView4.text = get(it).RSP_UserName
                textView4.minimumHeight = 30
                tableRow.addView(textView4)

                val textView5 = TextView(context)
                textView5.layoutParams = rowLayoutParam4
                textView5.gravity = Gravity.CENTER_VERTICAL
//                TableRow.LayoutParams()
                textView5.text = get(it).email
                textView5.minimumHeight = 30
                tableRow.addView(textView5)

                val textView6 = TextView(context)
                textView6.layoutParams = rowLayoutParam5
                textView6.minimumHeight = 30
                textView6.gravity = Gravity.CENTER_VERTICAL
                if (!(get(it).SeniorityDate.isNullOrEmpty()) ) {
                    try {
                        textView6.text  = if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView6.text  = get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView6.text  = ""
                }


                tableRow.addView(textView6)

                val textView7 = TextView(context)
                textView7.layoutParams = rowLayoutParam6
                textView7.gravity = Gravity.CENTER_VERTICAL
                textView7.text = get(it).CertificationNum
                textView7.minimumHeight = 30
                tableRow.addView(textView7)

                val textView8 = TextView(context)
                textView8.layoutParams = rowLayoutParam7
                textView8.gravity = Gravity.CENTER_VERTICAL
                textView8.minimumHeight = 30
                if (!(get(it).startDate.isNullOrEmpty())) {
                    try {
                        textView8.text  = if (get(it).startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).startDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView8.text  = get(it).startDate.apiToAppFormatMMDDYYYY()

                    }
                } else {
                    textView8.text  = ""
                }

                tableRow.addView(textView8)

                val textView9 = TextView(context)
                textView9.layoutParams = rowLayoutParam8
                textView9.gravity = Gravity.CENTER_VERTICAL
                textView9.minimumHeight = 30
                if (!(get(it).ExpirationDate.isNullOrEmpty())) {
                    try {
                        textView9.text = if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView9.text = get(it).ExpirationDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView9.text = ""
                }

                tableRow.addView(textView9)
                val checkBox10 = CheckBox(context)
                checkBox10.layoutParams = rowLayoutParam9
                checkBox10.gravity = Gravity.CENTER
                checkBox10.isChecked = get(it).ContractSigner
                checkBox10.minimumHeight = 30
                checkBox10.isEnabled=false
                tableRow.addView(checkBox10)

                val checkBox11 = CheckBox(context)
                checkBox11.layoutParams = rowLayoutParam10
                checkBox11.gravity = Gravity.CENTER
                checkBox11.isChecked = get(it).PrimaryMailRecipient
                checkBox11.isEnabled=false
                checkBox11.minimumHeight = 30
                tableRow.addView(checkBox11)



                val updateBtn = TextView(context)
                updateBtn.layoutParams = rowLayoutParam11
                updateBtn .setTextColor(Color.BLUE)
                updateBtn .text = "EDIT"
                updateBtn.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                updateBtn .setBackgroundColor(Color.TRANSPARENT)

                tableRow.addView(updateBtn)

                PersonnelResultsTbl.addView(tableRow)

                updateBtn.setOnClickListener {
                    var contractSignerFound = 0
                    var emailPrimaryFound = 0

                    if (checkBox10.isChecked){
                        edit_enableContractSignerIsChecked()
                        edit_newSignerCheck.isChecked=true

                        edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                            if (edit_newSignerCheck.isChecked ) {

                                edit_enableContractSignerIsChecked()

                            } else {

                                edit_disableContractSignerIsChecked()

                            }

                        }


                    }else{
                        edit_disableContractSignerIsChecked()
                        edit_newSignerCheck.isChecked=false


                    }




                    FacilityDataModel.getInstance().tblPersonnel.apply {
                        (0 until size).forEach {


                            if (get(it).ContractSigner.equals("true")) {
                                contractSignerFound++


                            }
                            if (get(it).PrimaryMailRecipient.equals("true")) {
                                emailPrimaryFound++


                            }


                            if (contractSignerFound>0&&!checkBox10.isChecked){
                                edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                                    if (edit_newSignerCheck.isChecked ) {

        //                                Toast.makeText(context, "there's already contract signer for this contract", Toast.LENGTH_SHORT).show()
                                        Utility.showValidationAlertDialog(activity,"There is already contract signer for this contract")
                                        edit_newSignerCheck.isChecked=false

                                    } else {

                                        edit_disableContractSignerIsChecked()

                                    }

                                }


                            }
                            if (emailPrimaryFound>0&&!checkBox11.isChecked){
                                edit_newACSCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                                    if (edit_newACSCheck.isChecked ) {

                                        Utility.showValidationAlertDialog(activity,"There's already primary email assigned for this contract")
        //                                Toast.makeText(context, "there's already primary email assigned for this contract", Toast.LENGTH_SHORT).show()
                                        edit_newACSCheck.isChecked=false

                                    }

                                }


                            }
                            if (emailPrimaryFound>0&&checkBox11.isChecked){
                                edit_newACSCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                                    if (edit_newACSCheck.isChecked ) {

                                        edit_newACSCheck.isChecked=true

                                    }

                                }


                            }
                            if (contractSignerFound==0){
                                edit_newSignerCheck.setOnClickListener(View.OnClickListener {
                                    if (edit_newSignerCheck.isChecked) {
                                        edit_enableContractSignerIsChecked()
                                    }


                                })

                            }


                        }
                    }
                    if (contractSignerFound==0) {
                        edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                            if (edit_newSignerCheck.isChecked ) {

                                edit_enableContractSignerIsChecked()

                            } else {

                                edit_disableContractSignerIsChecked()

                            }

                        }
                    }
                    if (contractSignerFound>0&&checkBox10.isChecked){
                        edit_newSignerCheck.isChecked=true


                    }


                    var currentTableRowIndex=PersonnelResultsTbl.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex=currentTableRowIndex-1


        //                    for (i in 0 until mainViewLinearId.childCount) {
        //                        val child = mainViewLinearId.getChildAt(i)
        //                        child.isEnabled = false
        //                    }
        //
        //                    for (i in 0 until mainViewLinearId2.childCount) {
        //                        val child = mainViewLinearId2.getChildAt(i)
        //                        child.isEnabled = false
        //                    }


                    edit_newFirstNameText.setText(textView2.text)
                    edit_newLastNameText.setText(textView3.text)
                    edit_newCertNoText.setText(textView7.text)
                    edit_newStartDateBtn.setText(textView8.text)
                    if (textView9.text.isNullOrEmpty() || textView9.equals("01/01/1900")) {
                        edit_newEndDateBtn.setText("SELECT DATE")
                    }else{
                        edit_newEndDateBtn.setText(textView9.text)

                    }
                    if (textView6.text.isNullOrEmpty() || textView6.equals("01/01/1900")) {
                        edit_newSeniorityDateBtn.setText("SELECT DATE")
                    }else{
                        edit_newSeniorityDateBtn.setText(textView6.text)

                    }

                    if (textView6.text.isNullOrEmpty() || textView6.equals("01/01/1900")) {
                        edit_newSeniorityDateBtn.setText("SELECT DATE")
                    }else{
                        edit_newSeniorityDateBtn.setText(textView6.text)

                    }

                    edit_newCoStartDateBtn.setText("SELECT DATE")
                    edit_newCoEndDateBtn.setText("SELECT DATE")
                    edit_newPhoneText.setText("")
                    edit_newZipText.setText("")
                    edit_newAdd1Text.setText("")
                    edit_newAdd2Text.setText("")
                    edit_newCityText.setText("")
                    edit_newZipText2.setText("")
                    edit_newEmailText.setText(textView5.text)
                    edit_newStateSpinner.setSelection(0)
                    var i = personTypeArray.indexOf(textView1.text.toString())
                    edit_newPersonnelTypeSpinner.setSelection(i)


                    edit_newZipText.setError(null)
                    edit_newZipText2.setError(null)
                    edit_newPhoneText.setError(null)
                    edit_newCertNoText.setError(null)
                    edit_stateTextView.setError(null)
                    edit_newEmailText.setError(null)
                    edit_personnelTypeTextViewId.setError(null)





                    edit_newACSCheck.isChecked = checkBox11.isChecked
                    edit_addNewPersonnelDialogue.visibility=View.VISIBLE
                    alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
                    edit_submitNewPersnRecordBtn.setOnClickListener {

                        if (edit_validateInputs()){
                            edit_addNewPersonnelDialogue.visibility=View.GONE
                            alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                            personnelLoadingText.text = "Saving ..."
                            personnelLoadingView.visibility = View.VISIBLE

                            var PersonnelTypeId=""

                            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                if (edit_newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))

                                    PersonnelTypeId =fac.PersonnelTypeID
                            }

                            var FirstName=if (edit_newFirstNameText.text.toString().isNullOrEmpty()) "" else edit_newFirstNameText.text.toString()
                            var LastName=if (edit_newLastNameText.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                            var RSP_UserName=FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName
                            var RSP_Email=FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email
                            var facNo=FacilityDataModel.getInstance().tblFacilities[0].FACNo
                            var CertificationNum=if (edit_newCertNoText.text.toString().isNullOrEmpty()) "" else edit_newCertNoText.text.toString()
                            var ContractSigner=if (edit_newSignerCheck.isChecked==true) "true" else "false"
                            var PrimaryMailRecipient=if (edit_newACSCheck.isChecked==true) "true" else "false"
                            var startDate = if (edit_newStartDateBtn.text.equals("SELECT DATE")) "" else edit_newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var ExpirationDate = if (edit_newEndDateBtn.text.equals("SELECT DATE")) "" else edit_newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var SeniorityDate = if (edit_newSeniorityDateBtn.text.equals("SELECT DATE")) "" else edit_newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var personnelID = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID

                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPersonnelData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=${personnelID}&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=sa&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}",
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread {
                                            if (response.toString().contains("returnCode&gt;0&",false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Personnel")
                                                var item = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex]
                                                for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                                    if (edit_newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))
                                                        item.PersonnelTypeID = fac.PersonnelTypeID.toInt()
                                                }
                                                item.FirstName = if (edit_newFirstNameText.text.toString().isNullOrEmpty()) "" else edit_newFirstNameText.text.toString()
                                                item.LastName = if (edit_newLastNameText.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                                item.RSP_UserName = if (edit_rspUserId.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                                item.RSP_Email = if (edit_rspEmailId.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                                item.CertificationNum = if (edit_newCertNoText.text.toString().isNullOrEmpty()) "" else edit_newCertNoText.text.toString()
                                                item.ContractSigner = if (edit_newSignerCheck.isChecked == true) true else false
                                                item.PrimaryMailRecipient = if (edit_newACSCheck.isChecked == true) true else false
                                                item.startDate = if (edit_newStartDateBtn.text.equals("SELECT DATE")) "" else edit_newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                item.ExpirationDate = if (edit_newEndDateBtn.text.equals("SELECT DATE")) "" else edit_newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                item.SeniorityDate = if (edit_newSeniorityDateBtn.text.equals("SELECT DATE")) "" else edit_newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                                HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                                fillPersonnelTableView()
                                                altTableRow(2)
                                                //                                                edit_personnelLoadingView.visibility = View.GONE
                                                personnelLoadingView.visibility = View.GONE
                                                personnelLoadingText.text = "Loading ..."

                                                //                                                for (i in 0 until mainViewLinearId.childCount) {
                                                //                                                    val child = mainViewLinearId.getChildAt(i)
                                                //                                                    child.isEnabled = true
                                                //                                                }
                                                //
                                                //                                                for (i in 0 until mainViewLinearId2.childCount) {
                                                //                                                    val child = mainViewLinearId2.getChildAt(i)
                                                //                                                    child.isEnabled = true
                                                //                                                }

//                                                IndicatorsDataModel.getInstance().validateFacilityPersonnelVisited()
//                                                if (IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited) (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#A42600"))
//                                                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                            } else {
                                                //                                                Utility.showSubmitAlertDialog(activity, false, "Personnel")
                                                var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                                Utility.showSubmitAlertDialog(activity,false,"Personnel (Error: "+ errorMessage+" )")
                                            }
                                        }
                                    }, Response.ErrorListener {
                                Utility.showSubmitAlertDialog(activity, false, "Personnel (Error: "+it.message+" )")
                                personnelLoadingView.visibility = View.GONE
                                personnelLoadingText.text = "Loading ..."

                            }))

                        } else {

                            //                            Toast.makeText(context,"please fill the required fields",Toast.LENGTH_SHORT).show()
                            Utility.showValidationAlertDialog(activity,"Please fill all the required fields")

                        }


                    }


                }


            }
        }
    }
    fun fillCertificationTableView(personnelID : Int) {

        if (certificationsTable.childCount>1) {
            for (i in certificationsTable.childCount - 1 downTo 1) {
                certificationsTable.removeViewAt(i)
            }
        }

        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.PersonnelID.equals(personnelID)}.apply  {
            (0 until size).forEach {
                if (!get(it).CertificationTypeId.isNullOrEmpty()) {
                    val rowLayoutParam = TableRow.LayoutParams()
                    rowLayoutParam.weight = 1F
                    rowLayoutParam.column = 0
                    rowLayoutParam.leftMargin = 10
                    rowLayoutParam.height = 30
                    rowLayoutParam.width = 0

                    val rowLayoutParam1 = TableRow.LayoutParams()
                    rowLayoutParam1.weight = 1F
                    rowLayoutParam1.column = 1
                    rowLayoutParam1.height = 30
                    rowLayoutParam1.width = 0

                    val rowLayoutParam2 = TableRow.LayoutParams()
                    rowLayoutParam2.weight = 1F
                    rowLayoutParam2.column = 2
                    rowLayoutParam2.height = 30
                    rowLayoutParam2.width = 0

                    val rowLayoutParam3 = TableRow.LayoutParams()
                    rowLayoutParam3.weight = 1F
                    rowLayoutParam3.column = 3
                    rowLayoutParam3.height = 30
                    rowLayoutParam3.width = 0

                    val rowLayoutParamRow = TableRow.LayoutParams()
                    rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParamRow.weight=1F


                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam
                    textView1.gravity = Gravity.CENTER
                    textView1.text = get(it).CertificationTypeId
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam1
                    textView2.gravity = Gravity.CENTER
                    try {
                        textView2.text = get(it).CertificationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView2.text = ""
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam2
                    textView3.gravity = Gravity.CENTER
                    TableRow.LayoutParams()
                    try {
                        textView3.text = get(it).ExpirationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView3.text = ""
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam3
                    textView4.gravity = Gravity.CENTER
                    textView4.text = get(it).CertDesc
                    tableRow.addView(textView4)
                    certificationsTable.addView(tableRow)
                }
                altCertTableRow(2)
            }
        }
    }

    fun addTheLatestRowOfPortalAdmin() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3

        FacilityDataModel.getInstance().tblPersonnel[FacilityDataModel.getInstance().tblPersonnel.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                if (CertificationTypeId.equals(fac.PersonnelCertID))

                    textView.text =fac.PersonnelCertName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                textView.text = CertificationDate.apiToAppFormatMMDDYYYY()

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
                textView.text = ExpirationDate.apiToAppFormatMMDDYYYY()

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)


            certificationsTable.addView(tableRow)

        }
        altCertTableRow(2)
    }
    fun addTheLatestRowOfPersonnelTable() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
  val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
  val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
  val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
  val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
  val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.column = 9
  val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.column = 10

        FacilityDataModel.getInstance().tblPersonnel[FacilityDataModel.getInstance().tblPersonnel.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                if (PersonnelTypeID.equals(fac.PersonnelTypeID))

                    textView.text =fac.PersonnelTypeName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = FirstName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = LastName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = RSP_UserName
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = RSP_Email
            tableRow.addView(textView)



            textView = TextView(context)
            textView.layoutParams = rowLayoutParam5
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = SeniorityDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam6
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = CertificationNum
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam7
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = startDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam8
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ExpirationDate.apiToAppFormatMMDDYYYY()
            tableRow.addView(textView)

            var checkBox = CheckBox(context)

            checkBox = CheckBox(context)
            checkBox.layoutParams = rowLayoutParam9
            checkBox.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
            checkBox.isChecked = (ContractSigner.equals("true"))
            checkBox.isEnabled=false
            tableRow.addView(checkBox)

            checkBox = CheckBox(context)
            checkBox.layoutParams = rowLayoutParam10
            checkBox.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
            checkBox.isChecked = (PrimaryMailRecipient.equals("true"))
            checkBox.isEnabled=false
            tableRow.addView(checkBox)

            PersonnelResultsTbl.addView(tableRow)

        }
        altTableRow(2)
    }

    fun onlyOneContractSignerLogic(){
        var alreadyContractSignerFound=false


        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {



                if (get(it).ContractSigner.equals("true")){



                    newSignerCheck.isEnabled=false
                    alreadyContractSignerFound=true
                    disablecontractSignerFeilds()




                    }


            }
            if (!alreadyContractSignerFound){
                newSignerCheck.isEnabled=true


            }
            newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->

                if (newSignerCheck.isChecked) {

                    if (alreadyContractSignerFound) {

                        newSignerCheck.isChecked=false
                        disablecontractSignerFeilds()
//                        Toast.makeText(context,"there's already a contract signer for this contract",Toast.LENGTH_SHORT).show()
                        Utility.showValidationAlertDialog(activity,"There is already a contract signer for this contract")
                    }else
                    {
                        Utility.showValidationAlertDialog(activity,"No contract signer for this contract")
//                        Toast.makeText(context,"no contract signer for the contract",Toast.LENGTH_SHORT).show()
                        newSignerCheck.isChecked=true
                        enable_contractSignerFeilds()


                    }
                }
                if (!newSignerCheck.isChecked){

                    newSignerCheck.isChecked=false
                    disablecontractSignerFeilds()


                }


            }

        }



    }

    fun checkMarkChangesWasDoneForPersonnel() {

        val dateFormat1 = SimpleDateFormat("MM/dd/yyyy")

        var itemOrgAr = FacilityDataModelOrg.getInstance().tblPersonnel
        var itemAr = FacilityDataModel.getInstance().tblPersonnel
        if (itemOrgAr.size == itemAr.size) {
            for (i in 0 until itemOrgAr.size){

                  //if (itemAr[i].PrimaryMailRecipient.o){itemAr[i].PrimaryMailRecipient=false}
                if (
                        ( itemOrgAr[i].ExpirationDate.isNullOrBlank()&&!itemAr[i].ExpirationDate.isNullOrBlank()) ||
                        ( itemOrgAr[i].startDate.isNullOrBlank()&&!itemAr[i].startDate.isNullOrBlank() )||
                        (  itemOrgAr[i].SeniorityDate.isNullOrBlank()&&!itemAr[i].SeniorityDate.isNullOrBlank())
                ) {

                    MarkChangeWasDone()
                }
                else
                    if (
                            ( itemOrgAr[i].ExpirationDate.isNullOrBlank()&&itemAr[i].ExpirationDate.isNullOrBlank()) ||
                            ( itemOrgAr[i].startDate.isNullOrBlank()&&itemAr[i].startDate.isNullOrBlank() )||
                            (  itemOrgAr[i].SeniorityDate.isNullOrBlank()&&itemAr[i].SeniorityDate.isNullOrBlank())
                    ) {

                        if (
                                itemAr[i].FirstName != itemOrgAr[i].FirstName || itemAr[i].LastName != itemOrgAr[i].LastName ||
                                itemAr[i].RSP_UserName != itemOrgAr[i].RSP_UserName ||
                                itemAr[i].PersonnelTypeID != itemOrgAr[i].PersonnelTypeID ||
                                itemAr[i].RSP_Email != itemOrgAr[i].RSP_Email ||
                                itemAr[i].CertificationNum != itemOrgAr[i].CertificationNum ||
                                itemAr[i].ContractSigner != itemOrgAr[i].ContractSigner ||
                                itemAr[i].PrimaryMailRecipient != itemOrgAr[i].PrimaryMailRecipient


                        ) {
                            MarkChangeWasDone()

//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgAr[i].FirstName + "=="+ itemAr[i].FirstName)
                            Log.v("checkkk", itemOrgAr[i].LastName + "=="+ itemAr[i].LastName)
                            Log.v("checkkk", itemOrgAr[i].RSP_UserName + "=="+ itemAr[i].RSP_UserName)
                            Log.v("checkkk", itemOrgAr[i].RSP_Email + "=="+ itemAr[i].RSP_Email)
                            Log.v("checkkk", itemOrgAr[i].CertificationNum + "=="+ itemAr[i].CertificationNum)
                            //Log.v("checkkk", itemOrgAr[i].ContractSigner + "=="+ itemAr[i].ContractSigner)
                            //Log.v("checkkk", itemOrgAr[i].PrimaryMailRecipient + "=="+ itemAr[i].PrimaryMailRecipient)
                            //Log.v("checkkk", itemOrgAr[i].PersonnelTypeID + "=="+ itemAr[i].PersonnelTypeID)

                        }
                    }
                    else
                        if (

                                itemAr[i].FirstName != itemOrgAr[i].FirstName || itemAr[i].LastName != itemOrgAr[i].LastName ||
                                itemAr[i].RSP_UserName != itemOrgAr[i].RSP_UserName ||
                                itemAr[i].RSP_Email != itemOrgAr[i].RSP_Email ||
                                itemAr[i].CertificationNum != itemOrgAr[i].CertificationNum ||
                                itemAr[i].PersonnelTypeID != itemOrgAr[i].PersonnelTypeID ||
                                itemAr[i].ContractSigner != itemOrgAr[i].ContractSigner ||
                                itemAr[i].PrimaryMailRecipient != itemOrgAr[i].PrimaryMailRecipient ||
                                dateFormat1.parse(itemAr[i].startDate.apiToAppFormat()) != dateFormat1.parse(itemOrgAr[i].startDate.apiToAppFormat()) ||
                                dateFormat1.parse(itemAr[i].ExpirationDate.apiToAppFormat()) != dateFormat1.parse(itemOrgAr[i].ExpirationDate.apiToAppFormat()) ||
                                dateFormat1.parse(itemAr[i].SeniorityDate.apiToAppFormat()) != dateFormat1.parse(itemOrgAr[i].SeniorityDate.apiToAppFormat())


                        ) {
                            MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgAr[i].FirstName + "=="+ itemAr[i].FirstName)
                            Log.v("checkkk", itemOrgAr[i].LastName + "=="+ itemAr[i].LastName)
                            Log.v("checkkk", itemOrgAr[i].RSP_UserName + "=="+ itemAr[i].RSP_UserName)
                            Log.v("checkkk", itemOrgAr[i].RSP_Email + "=="+ itemAr[i].RSP_Email)
                            Log.v("checkkk", itemOrgAr[i].CertificationNum + "=="+ itemAr[i].CertificationNum)
                            //Log.v("checkkk", itemOrgAr[i].ContractSigner + "=="+ itemAr[i].ContractSigner)
                            //Log.v("checkkk", itemOrgAr[i].PrimaryMailRecipient + "=="+ itemAr[i].PrimaryMailRecipient)
                            //Log.v("checkkk", itemOrgAr[i].PersonnelTypeID + "=="+ itemAr[i].PersonnelTypeID)
                            Log.v("checkkk", itemOrgAr[i].startDate + "=="+ itemAr[i].startDate)
                            Log.v("checkkk", itemOrgAr[i].ExpirationDate + "=="+ itemAr[i].ExpirationDate)
                            Log.v("checkkk", itemOrgAr[i].SeniorityDate + "=="+ itemAr[i].SeniorityDate)


                        }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "array not equal")


        }
    }

    fun onlyOneMailRecepientLogic(){

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {


                if (get(it).PrimaryMailRecipient.equals("true")){

                    newACSCheck.isEnabled=false
                }

            }
        }

    }

    fun altTableRow(alt_row : Int) {
        var childViewCount = PersonnelResultsTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= PersonnelResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }
    fun altCertTableRow(alt_row : Int) {
        var childViewCount = certificationsTable.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= certificationsTable.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }

//    fun conractSignerCheckedCondition(){
//
//        newSignerCheck.setOnClickListener(View.OnClickListener {
//
//            if (newSignerCheck.isChecked==true){
//
//
//                contractSignatureIsChecked=true
//
//                newEmailText.isEnabled=true
//                newCoStartDateBtn.isEnabled=true
//                newPhoneText.isEnabled=true
//                newZipText.isEnabled=true
//                newCityText.isEnabled=true
//                newAdd1Text.isEnabled=true
//                newStateSpinner.isEnabled=true
//                newZipText2.isEnabled=true
//                newAdd2Text.isEnabled=true
//                stateTextView.isEnabled = true
//                phoneTextId.isEnabled = true
//                zipCodeTextId.isEnabled = true
//                emailAddressTextId.isEnabled = true
//                contractSignerStartDateTextId.isEnabled = true
//                contractSignerEndDateTextId.isEnabled = true
//                newCoEndDateBtn.isEnabled = true
//                cityTextId.isEnabled = true
//                address2TextId.isEnabled = true
//                address1TextId.isEnabled = true
//                newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.blue));
//                newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.blue));
//                contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.table_row_color));
//
//
//
//
//            }
//            if (newSignerCheck.isChecked==false) {
//                contractSignatureIsChecked = false
//
//
//                newEmailText.isEnabled = false
//                newCoStartDateBtn.isEnabled = false
//                newPhoneText.isEnabled = false
//                newZipText.isEnabled = false
//                newCityText.isEnabled = false
//                newAdd1Text.isEnabled = false
//                newStateSpinner.isEnabled = false
//                newZipText2.isEnabled = false
//                newAdd2Text.isEnabled = false
//                stateTextView.isEnabled = false
//                phoneTextId.isEnabled = false
//                zipCodeTextId.isEnabled = false
//                emailAddressTextId.isEnabled = false
//                contractSignerStartDateTextId.isEnabled = false
//                contractSignerEndDateTextId.isEnabled = false
//                newCoEndDateBtn.isEnabled = false
//                cityTextId.isEnabled = false
//                address2TextId.isEnabled = false
//                address1TextId.isEnabled = false
//                newCoStartDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
//                newCoEndDateBtn.setTextColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.gray));
//                contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.contractSignerFieldsAreDisabledColor));
//
//
//
//            }
//
//
//
//        })
//
//
//        }

    fun scopeOfServiceChangesWatcher() {
//        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {
//
//
//            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {
//
//                if (FragmentARRAVScopeOfService.dataChanged) {
//
//                    val builder = AlertDialog.Builder(context)
//
//                    // Set the alert dialog title
//                    builder.setTitle("Changes made confirmation")
//
//                    // Display a message on alert dialog
//                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")
//
//                    // Set a positive button and its click listener on alert dialog
//                    builder.setPositiveButton("YES") { dialog, which ->
//
//
//                        personnelLoadingView.visibility = View.VISIBLE
//
//
//                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat(),
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread(Runnable {
//                                        Log.v("RESPONSE", response.toString())
//                                        personnelLoadingView.visibility = View.GONE
//
//                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
//                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
//                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {
//
//                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
//                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
//                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
//                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
//                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
//                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_
//
//                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare
//
//                                                FragmentARRAVScopeOfService.dataChanged = false
//
//                                            }
//
//                                        }
//
//                                    })
//                                }, Response.ErrorListener {
//                            Log.v("error while loading", "error while loading personnal record")
//                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
//                            personnelLoadingView.visibility = View.GONE
//
//
//                        }))
//
//
//                    }
//
//
//                    // Display a negative button on alert dialog
//                    builder.setNegativeButton("No") { dialog, which ->
//                        FragmentARRAVScopeOfService.dataChanged = false
//                        personnelLoadingView.visibility = View.GONE
//
//                    }
//
//
//                    // Finally, make the alert dialog using builder
//                    val dialog: AlertDialog = builder.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    // Display the alert dialog on app interface
//                    dialog.show()
//
//                }
//
//            } else {
//
//
//                val builder = AlertDialog.Builder(context)
//
//                // Set the alert dialog title
//                builder.setTitle("Changes made Warning")
//
//                // Display a message on alert dialog
//                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")
//
//                // Set a positive button and its click listener on alert dialog
//                builder.setPositiveButton("Ok") { dialog, which ->
//
//                    FragmentARRAVScopeOfService.dataChanged = false
//
//                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true
//
//                }
//
//
//                val dialog: AlertDialog = builder.create()
//                dialog.setCanceledOnTouchOutside(false)
//                dialog.show()
//
//            }
//
//        }
    }



    fun validateCertificationInputs() : Boolean{

        certDateTextView.setError(null)
        certTypeTextView.setError(null)


           var cert = TblPersonnel()

        cert.iscertInputValid=true

        if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            cert.iscertInputValid = false
            certDateTextView.setError("Required Field")
        }
        if (!newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE") && newCertEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
            cert.iscertInputValid = false
            expirationDateText.setError("Required Field")
        }
        if (newCertTypeSpinner.selectedItem.toString().contains("Not")){
            cert.iscertInputValid=false
            certTypeTextView.setError("required field")
        }
        var certificateType = ""
        for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
            if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
                certificateType = fac.PersonnelCertID
        }



        var datesOverlapping = false
        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.CertificationTypeId.equals(certificateType)}.apply {
            (0 until size).forEach {
                if (get(it).PersonnelID.equals(selectedPersonnelID)) {
                    if (Utility.datesAreOverlapping(newCertStartDateBtn.text.toString().toDateMMDDYYYY(), newCertEndDateBtn.text.toString().toDateMMDDYYYY(), get(it).CertificationDate.toDateDBFormat(), get(it).ExpirationDate.toDateDBFormat())) {
                        datesOverlapping = true
                    }
                }
            }
        }
        if (datesOverlapping) {
            Utility.showValidationAlertDialog(activity,"The certification overlaps with another active certification from the same type")
            cert.iscertInputValid = !datesOverlapping
        }

        return cert.iscertInputValid
    }



    fun validateInputs() : Boolean{

        var persn = TblPersonnel()


        persn.personnelIsInputsValid=true

        if (newFirstNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            newFirstNameText.setError("required field")

        }
        else
            newFirstNameText.setError(null)

        if (newCertNoText.text.toString().isNullOrEmpty()){
            persn.personnelIsInputsValid=false
            newCertNoText.setError("required field")
        }
        else
            newCertNoText.setError(null)

        if (newLastNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            newLastNameText.setError("required field")


        }
        else
            newLastNameText.setError(null)


        if (newPersonnelTypeSpinner.selectedItem.toString().contains("Selected")){

            persn.personnelIsInputsValid=false
            personnelTypeTextViewId.setError("required field")


        }
        else
            personnelTypeTextViewId.setError(null)



        if (contractSignatureIsChecked){

                if (newAdd1Text.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    newAdd1Text.setError("required field")

                }
                else
                    newAdd1Text.setError(null)

                if (newCityText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    newCityText.setError("required field")


                }
                else
                    newCityText.setError(null)

                if (newStateSpinner.selectedItem.toString().contains("Select")){

                    persn.personnelIsInputsValid=false
                    stateTextView.setError("required field")


                }
                else
                    stateTextView.setError(null)


                if (newZipText.text.toString().isNullOrEmpty()||zipFormat==false){

                    persn.personnelIsInputsValid=false
                    newZipText.setError("required field")


                }
                else
                    newZipText.setError(null)


                if (newPhoneText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    newPhoneText.setError("required field")


                }
                else
                    newPhoneText.setError(null)


                if (newCoStartDateBtn.text.toString().contains("SELECT")){

                    persn.personnelIsInputsValid=false
                    newCoStartDateBtn.setError("required field")


                }  else
                    newCoStartDateBtn.setError(null)




                if (newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(newEmailText.text.toString())){

                    persn.personnelIsInputsValid=false
                    newEmailText.setError("required field")


                }  else
                    newEmailText.setError(null)



            }else {
                newEmailText.setError(null)
                newCoStartDateBtn.setError(null)
                newPhoneText.setError(null)
                newZipText.setError(null)
                stateTextView.setError(null)
                newCityText.setError(null)
                newAdd1Text.setError(null)


            }




        return persn.personnelIsInputsValid
    }
    fun edit_validateInputs() : Boolean{

        var persn = TblPersonnel()


        persn.personnelIsInputsValid=true

        if (edit_newFirstNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            edit_newFirstNameText.setError("required field")

        }
        else
            edit_newFirstNameText.setError(null)


        if (edit_newLastNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            edit_newLastNameText.setError("required field")


        }
        else
            edit_newLastNameText.setError(null)


        if (edit_newPersonnelTypeSpinner.selectedItem.toString().contains("Selected")){

            persn.personnelIsInputsValid=false
            edit_personnelTypeTextViewId.setError("required field")


        }
        else
            edit_personnelTypeTextViewId.setError(null)



        if (contractSignatureIsChecked){

                if (edit_newAdd1Text.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newAdd1Text.setError("required field")

                }
                else
                    edit_newAdd1Text.setError(null)

                if (edit_newCityText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newCityText.setError("required field")


                }
                else
                    edit_newCityText.setError(null)

                if (edit_newStateSpinner.selectedItem.toString().contains("select")){

                    persn.personnelIsInputsValid=false
                    edit_stateTextView.setError("required field")


                }
                else
                    edit_stateTextView.setError(null)


                if (edit_newZipText.text.toString().isNullOrEmpty()||zipFormat==false){

                    persn.personnelIsInputsValid=false
                    edit_newZipText.setError("required field")


                }
                else
                    edit_newZipText.setError(null)


                if (edit_newPhoneText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newPhoneText.setError("required field")


                }
                else
                    edit_newPhoneText.setError(null)


                if (edit_newCoStartDateBtn.text.toString().contains("SELECT")){

                    persn.personnelIsInputsValid=false
                    edit_newCoStartDateBtn.setError("required field")


                }  else
                    edit_newCoStartDateBtn.setError(null)




                if (edit_newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(edit_newEmailText.text.toString())){

                    persn.personnelIsInputsValid=false
                    edit_newEmailText.setError("required field")


                }  else
                    edit_newEmailText.setError(null)



            }else {
            edit_newEmailText.setError(null)
            edit_newCoStartDateBtn.setError(null)
            edit_newPhoneText.setError(null)
            edit_newZipText.setError(null)
            edit_stateTextView.setError(null)
            edit_newCityText.setError(null)
            edit_newAdd1Text.setError(null)


            }




        return persn.personnelIsInputsValid
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
