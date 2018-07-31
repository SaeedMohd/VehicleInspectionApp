package com.inspection.fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.appToApiFormat
import com.inspection.model.AAAPersonnelDetails
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.TypeTablesModel
import com.inspection.singletons.AnnualVisitationSingleton
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
       var contractSignatureIsChecked=false
    private var mParam1: String? = null
    var countIfContractSignedBefore=0
    var theContractSigner=false
    private var mParam2: String? = null
    private var personnelTypeList = ArrayList<TypeTablesModel.personnelType>()
    private var certificationTypeList= ArrayList<TypeTablesModel.personnelCertificationType>()
    private var states= arrayOf("select state","Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming")

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
        fillCertificationTableView()
        rspUserId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName.toString())
        rspEmailId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email.toString())



        exitDialogeBtnId.setOnClickListener({


            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = true
            }

            for (i in 0 until mainViewLinearId2.childCount) {
                val child = mainViewLinearId2.getChildAt(i)
                child.isEnabled = true
            }



            addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        })
        edit_exitDialogeBtnId.setOnClickListener({


            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = true
            }

            for (i in 0 until mainViewLinearId2.childCount) {
                val child = mainViewLinearId2.getChildAt(i)
                child.isEnabled = true
            }



            edit_addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        })

        exitCertificateDialogeBtnId.setOnClickListener({

            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = true
            }

            for (i in 0 until mainViewLinearId2.childCount) {
                val child = mainViewLinearId2.getChildAt(i)
                child.isEnabled = true
            }


            addNewCertificateDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE


        })

        AddNewCertBtn.setOnClickListener(View.OnClickListener {

            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = false
            }

            for (i in 0 until mainViewLinearId2.childCount) {
                val child = mainViewLinearId2.getChildAt(i)
                child.isEnabled = false
            }

            newCertTypeSpinner.setSelection(0)
            newCertStartDateBtn.setText("SELECT DATE")
            newCertEndDateBtn.setText("SELECT DATE")
            newCertDescText.setText("")


            newCertStartDateBtn.setError(null)
            certTypeTextView.setError(null)
            addNewCertificateDialogue.visibility=View.VISIBLE
            alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE


        })
        addNewPersnRecordBtn.setOnClickListener(View.OnClickListener {

            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = false
            }

            for (i in 0 until mainViewLinearId2.childCount) {
                val child = mainViewLinearId2.getChildAt(i)
                child.isEnabled = false
            }


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



            if (newSignerCheck.isChecked){
                newSignerCheck.isChecked=false

                contractSignerIsNotCheckedLogic()
                onlyOneContractSignerLogic()


            }
            addNewPersonnelDialogue.visibility=View.VISIBLE
            alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE


        })



        contractSignerIsNotCheckedLogic()

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
        newCoEndDateBtn.setOnClickListener {
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
                    newCoEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }
        edit_newCoEndDateBtn.setOnClickListener {
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
                    edit_newCoEndDateBtn!!.text = sdf.format(c.time)
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
                    val myFormat = "dd MMM yyyy" // mention the format you need
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
                    val myFormat = "dd MMM yyyy" // mention the format you need
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
                    val myFormat = "dd MMM yyyy" // mention the format you need
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
                    val myFormat = "dd MMM yyyy" // mention the format you need
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
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newStartDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }


        fillData()

        submitNewCertBtn.setOnClickListener({

            if (validateCertificationInputs()) {
                addNewCertificateDialogue.visibility=View.GONE
                alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                personnelLoadingView.visibility = View.VISIBLE



                var CertificationTypeId = ""
                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                    if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))

                        CertificationTypeId = fac.PersonnelCertID
                }

                var CertificationDate = if (newCertStartDateBtn.text.equals("SELECT DATE")) "" else newCertStartDateBtn.text.toString()
                var ExpirationDate = if (newCertEndDateBtn.text.equals("SELECT DATE")) "" else newCertEndDateBtn.text.toString()


                var item = FacilityDataModel.TblPersonnel()
                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                    if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))

                        item.CertificationTypeId = fac.PersonnelCertID
                }

                item.CertificationDate = if (newCertStartDateBtn.text.equals("SELECT DATE")) "" else newCertStartDateBtn.text.toString()
                item.ExpirationDate = if (newCertEndDateBtn.text.equals("SELECT DATE")) "" else newCertEndDateBtn.text.toString()
                FacilityDataModel.getInstance().tblPersonnel.add(item)

                addTheLatestRowOfPortalAdmin()

                for (i in 0 until mainViewLinearId.childCount) {
                    val child = mainViewLinearId.getChildAt(i)
                    child.isEnabled = true
                }

                for (i in 0 until mainViewLinearId2.childCount) {
                    val child = mainViewLinearId2.getChildAt(i)
                    child.isEnabled = true
                }



                var urlString = ""
                Log.v("Data To Submit", urlString)
//        urlString = URLEncoder.encode(urlString, "UTF-8")
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.POST, Constants.submitFacilityGeneralInfo + urlString,
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE", response.toString())
                                personnelLoadingView.visibility = View.GONE



                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading certificate record")
                    personnelLoadingView.visibility = View.GONE

                }))
            }

        })

        submitNewPersnRecordBtn.setOnClickListener({

            if (validateInputs()){
                addNewPersonnelDialogue.visibility=View.GONE
                alphaBackgroundForPersonnelDialogs.visibility = View.GONE

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
            var startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString()
            var ExpirationDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString()
            var SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString()




//        urlString = URLEncoder.encode(urlString, "UTF-8")
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPersonnelData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&personnelId=63384&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=McCaulley&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=sa&insertDate=2013-04-24T13:39:56.490&updateBy=SumA&updateDate=2017-03-23T11:11:08.997&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            Log.v("RESPONSE",response.toString())
//
                            var item = FacilityDataModel.TblPersonnel()
                            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                if (newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))

                                    item.PersonnelTypeID =fac.PersonnelTypeID
                            }

                            item.FirstName=if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString()
                            item.LastName=if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                            item.RSP_UserName=if (rspUserId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                            item.RSP_Email=if (rspEmailId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                            item.CertificationNum=if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
                            item.ContractSigner=if (newSignerCheck.isChecked==true) "true" else "false"
                            item.PrimaryMailRecipient=if (newACSCheck.isChecked==true) "true" else "false"
                            item.startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString()
                            item.ExpirationDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString()
                            item.SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString()
                            FacilityDataModel.getInstance().tblPersonnel.add(item)

                            fillPersonnelTableView()
                            altTableRow(2)
                            personnelLoadingView.visibility = View.GONE

                            for (i in 0 until mainViewLinearId.childCount) {
                                val child = mainViewLinearId.getChildAt(i)
                                child.isEnabled = true
                            }

                            for (i in 0 until mainViewLinearId2.childCount) {
                                val child = mainViewLinearId2.getChildAt(i)
                                child.isEnabled = true
                            }


                            var itemOrgArray = FacilityDataModelOrg.getInstance().tblPersonnel
                            var itemArray = FacilityDataModel.getInstance().tblPersonnel
                            for (itemAr in itemArray){
                                for (itemOrgAr in itemOrgArray){

                                    if (itemAr.FirstName!=itemOrgAr.FirstName||itemAr.LastName!=itemOrgAr.LastName||
                                            itemAr.RSP_UserName!=itemOrgAr.RSP_UserName||
                                            itemAr.RSP_Email!=itemOrgAr.RSP_Email||
                                            itemAr.CertificationNum!=itemOrgAr.CertificationNum||
                                            itemAr.ContractSigner!=itemOrgAr.ContractSigner||
                                            itemAr.PrimaryMailRecipient!=itemOrgAr.PrimaryMailRecipient||
                                            itemAr.startDate!=itemOrgAr.startDate||
                                            itemAr.ExpirationDate!=itemOrgAr.ExpirationDate||
                                            itemAr.SeniorityDate!=itemOrgAr.SeniorityDate){
                                        MarkChangeWasDone()
                                        Toast.makeText(context,"changes submitted",Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }


                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading personnal record")
                personnelLoadingView.visibility = View.GONE

            }))

            }
            else
            {

                Toast.makeText(context,"please fill the required fields",Toast.LENGTH_SHORT).show()

            }


        })

        onlyOneContractSignerLogic()
        onlyOneMailRecepientLogic()
        conractSignerCheckedCondition()
        altTableRow(2)
        altCertTableRow(2)



    }


    var isFirstRun: Boolean = true

    fun contractSignerIsNotCheckedLogic(){

        if (newSignerCheck.isChecked==false) {
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
            edit_newCoEndDateBtn.isEnabled = false
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

        if (PersonnelResultsTbl.childCount>1) {
            for (i in PersonnelResultsTbl.childCount - 1 downTo 1) {
                PersonnelResultsTbl.removeViewAt(i)
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
        var dateTobeFormated = ""

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 1F
        rowLayoutParam11.height = TableLayout.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.column = 11
        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam
                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView1.text = getTypeName(get(it).PersonnelTypeID)
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView2.text = get(it).FirstName
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView3.text = get(it).LastName
                tableRow.addView(textView3)

                val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView4.text = get(it).RSP_UserName
                tableRow.addView(textView4)

                val textView5 = TextView(context)
                textView5.layoutParams = rowLayoutParam4
                textView5.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView5.text = get(it).RSP_Email
                tableRow.addView(textView5)

                val textView6 = TextView(context)
                textView6.layoutParams = rowLayoutParam5
                TableRow.LayoutParams()
                textView6.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (!(get(it).SeniorityDate.isNullOrEmpty())) {
                    textView6.text = get(it).SeniorityDate.apiToAppFormat()
                } else {
                    textView6.text = ""
                }

                tableRow.addView(textView6)

                val textView7 = TextView(context)
                textView7.layoutParams = rowLayoutParam6
                textView7.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView7.text = get(it).CertificationNum
                tableRow.addView(textView7)

                val textView8 = TextView(context)
                textView8.layoutParams = rowLayoutParam7
                textView8.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                if (!(get(it).startDate.isNullOrEmpty())) {
                    try {
                        textView8.text  = get(it).startDate.apiToAppFormat()
                    } catch (e: Exception) {
                        textView8.text  = get(it).startDate.appToApiFormat()

                    }
                } else {
                    textView8.text  = ""
                }

                tableRow.addView(textView8)

                val textView9 = TextView(context)
                textView9.layoutParams = rowLayoutParam8
                textView9.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                if (!(get(it).ExpirationDate.isNullOrEmpty())) {
                    try {
                        textView9.text = get(it).ExpirationDate.apiToAppFormat()
                    } catch (e: Exception) {
                        textView9.text = get(it).ExpirationDate.appToApiFormat()

                    }
                } else {
                    textView9.text = ""
                }


                tableRow.addView(textView9)


                val checkBox10 = CheckBox(context)
                checkBox10.layoutParams = rowLayoutParam9
                checkBox10.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
                checkBox10.isChecked = (get(it).ContractSigner.equals("true"))
                TableRow.LayoutParams()
                checkBox10.isEnabled=false
                tableRow.addView(checkBox10)

                val checkBox11 = CheckBox(context)
                checkBox11.layoutParams = rowLayoutParam10
                checkBox11.textAlignment = CheckBox.TEXT_ALIGNMENT_CENTER
                checkBox11.isChecked = (get(it).PrimaryMailRecipient.equals("true"))
                TableRow.LayoutParams()
                checkBox11.isEnabled=false
                tableRow.addView(checkBox11)

                val updateBtn = Button(context)
                updateBtn.layoutParams = rowLayoutParam11
                updateBtn.textAlignment = Button.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                updateBtn.text = "Update"

                tableRow.addView(updateBtn)

                PersonnelResultsTbl.addView(tableRow)

                updateBtn.setOnClickListener(View.OnClickListener {
                    var contractSignerFound = 0
                    var emailPrimaryFound = 0

                    if (checkBox10.isChecked){




                        edit_enableContractSignerIsChecked()
                        edit_newSignerCheck.isChecked=true

                        edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                            if (edit_newSignerCheck.isChecked ) {

                                edit_enableContractSignerIsChecked()
                                Toast.makeText(context, "open this maaaan", Toast.LENGTH_SHORT).show()

                            } else {

                                edit_disableContractSignerIsChecked()
                                Toast.makeText(context, "close this thing", Toast.LENGTH_SHORT).show()

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

                                Toast.makeText(context, "there's already contract signer for this contract", Toast.LENGTH_SHORT).show()
                                edit_newSignerCheck.isChecked=false

                            } else {

                                edit_disableContractSignerIsChecked()

                            }

                        }


                    }
                    if (emailPrimaryFound>0&&!checkBox11.isChecked){
                        edit_newACSCheck.setOnCheckedChangeListener { buttonView, isChecked ->


                            if (edit_newACSCheck.isChecked ) {

                                Toast.makeText(context, "there's already primary email assigned for this contract", Toast.LENGTH_SHORT).show()
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
                       //         Toast.makeText(context, "suppose to enable", Toast.LENGTH_SHORT).show()

                            } else {

                                edit_disableContractSignerIsChecked()
                             //   Toast.makeText(context, "close this thing", Toast.LENGTH_SHORT).show()

                            }

                        }
                    }



                    var currentTableRowIndex=PersonnelResultsTbl.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex=currentTableRowIndex-1


                    for (i in 0 until mainViewLinearId.childCount) {
                        val child = mainViewLinearId.getChildAt(i)
                        child.isEnabled = false
                    }

                    for (i in 0 until mainViewLinearId2.childCount) {
                        val child = mainViewLinearId2.getChildAt(i)
                        child.isEnabled = false
                    }


                    edit_newFirstNameText.setText(textView2.text)
                    edit_newLastNameText.setText(textView3.text)
                    edit_newCertNoText.setText(textView7.text)
                    edit_newStartDateBtn.setText(textView8.text)
                    edit_newEndDateBtn.setText(textView9.text)
                    edit_newSeniorityDateBtn.setText(textView6.text)
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





                    if (checkBox11.isChecked){

                        edit_newACSCheck.isChecked=true



                    }else{
                        edit_newACSCheck.isChecked=false



                    }
                    edit_addNewPersonnelDialogue.visibility=View.VISIBLE
                    alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
                    edit_submitNewPersnRecordBtn.setOnClickListener({

                        if (edit_validateInputs()){
                            edit_addNewPersonnelDialogue.visibility=View.GONE
                            alphaBackgroundForPersonnelDialogs.visibility = View.GONE

                            edit_personnelLoadingView.visibility = View.VISIBLE


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
                            var startDate = if (edit_newStartDateBtn.text.equals("SELECT DATE")) "" else edit_newStartDateBtn.text.toString()
                            var ExpirationDate = if (edit_newEndDateBtn.text.equals("SELECT DATE")) "" else edit_newEndDateBtn.text.toString()
                            var SeniorityDate = if (edit_newSeniorityDateBtn.text.equals("SELECT DATE")) "" else edit_newSeniorityDateBtn.text.toString()




//        urlString = URLEncoder.encode(urlString, "UTF-8")
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateFacilityPersonnelData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&personnelId=63384&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=McCaulley&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=sa&insertDate=2013-04-24T13:39:56.490&updateBy=SumA&updateDate=2017-03-23T11:11:08.997&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=",
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread(Runnable {
                                            Log.v("RESPONSE",response.toString())
//
                                            var item = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex]
                                            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                                if (edit_newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))

                                                    item.PersonnelTypeID =fac.PersonnelTypeID
                                            }

                                            item.FirstName=if (edit_newFirstNameText.text.toString().isNullOrEmpty()) "" else edit_newFirstNameText.text.toString()
                                            item.LastName=if (edit_newLastNameText.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                            item.RSP_UserName=if (edit_rspUserId.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                            item.RSP_Email=if (edit_rspEmailId.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                                            item.CertificationNum=if (edit_newCertNoText.text.toString().isNullOrEmpty()) "" else edit_newCertNoText.text.toString()
                                            item.ContractSigner=if (edit_newSignerCheck.isChecked==true) "true" else "false"
                                            item.PrimaryMailRecipient=if (edit_newACSCheck.isChecked==true) "true" else "false"
                                            item.startDate = if (edit_newStartDateBtn.text.equals("SELECT DATE")) "" else edit_newStartDateBtn.text.toString()
                                            item.ExpirationDate = if (edit_newEndDateBtn.text.equals("SELECT DATE")) "" else edit_newEndDateBtn.text.toString()
                                            item.SeniorityDate = if (edit_newSeniorityDateBtn.text.equals("SELECT DATE")) "" else edit_newSeniorityDateBtn.text.toString()

                                            fillPersonnelTableView()
                                            altTableRow(2)
                                            edit_personnelLoadingView.visibility = View.GONE

                                            for (i in 0 until mainViewLinearId.childCount) {
                                                val child = mainViewLinearId.getChildAt(i)
                                                child.isEnabled = true
                                            }

                                            for (i in 0 until mainViewLinearId2.childCount) {
                                                val child = mainViewLinearId2.getChildAt(i)
                                                child.isEnabled = true
                                            }


                                            var itemOrgAr = FacilityDataModelOrg.getInstance().tblPersonnel[currentfacilityDataModelIndex]
                                            var itemAr = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex]

                                                    if (itemAr.FirstName!=itemOrgAr.FirstName||itemAr.LastName!=itemOrgAr.LastName||
                                                            itemAr.RSP_UserName!=itemOrgAr.RSP_UserName||
                                                            itemAr.RSP_Email!=itemOrgAr.RSP_Email||
                                                            itemAr.CertificationNum!=itemOrgAr.CertificationNum||
                                                            itemAr.ContractSigner!=itemOrgAr.ContractSigner||
                                                            itemAr.PrimaryMailRecipient!=itemOrgAr.PrimaryMailRecipient||
                                                            itemAr.startDate!=itemOrgAr.startDate||
                                                            itemAr.ExpirationDate!=itemOrgAr.ExpirationDate||
                                                            itemAr.SeniorityDate!=itemOrgAr.SeniorityDate){
                                                        MarkChangeWasDone()
                                                        Toast.makeText(context,"changes submitted",Toast.LENGTH_SHORT).show()
                                                    }





                                        })
                                    }, Response.ErrorListener {
                                Log.v("error while loading", "error while loading personnal record")
                                personnelLoadingView.visibility = View.GONE

                            }))

                        }
                        else
                        {

                            Toast.makeText(context,"please fill the required fields",Toast.LENGTH_SHORT).show()

                        }


                    })


                })


            }
        }
    }
    fun fillCertificationTableView() {
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
        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                var forCompr=""
                val tableRow = TableRow(context)

                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam
                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
                    if (get(it).CertificationTypeId.equals(fac.PersonnelCertID))

                        textView1.text =fac.PersonnelCertName
                    forCompr=fac.PersonnelCertName
                }
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                try {
                    textView2.text = get(it).CertificationDate.apiToAppFormat()
                } catch (e: Exception) {
                }
                tableRow.addView(textView2)

               val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                try {
                    textView3.text = get(it).ExpirationDate.apiToAppFormat()
                } catch (e: Exception) {
                }
                tableRow.addView(textView3)

               val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView4.text = ""
                tableRow.addView(textView4)

                if (textView1.text.toString().isNullOrBlank()&&textView2.text.toString().isNullOrBlank()&&textView3.text.toString().isNullOrBlank())
                {

                }else{
                    certificationsTable.addView(tableRow)
                }

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

                textView.text = CertificationDate

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
                textView.text = ExpirationDate

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
            textView.text = SeniorityDate
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam6
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = CertificationNum
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam7
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = startDate
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam8
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ExpirationDate
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

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {

                if (get(it).ContractSigner.equals("true")){

                    newSignerCheck.isEnabled=false
                    theContractSigner=true

                }

            }
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

    fun conractSignerCheckedCondition(){

        newSignerCheck.setOnClickListener(View.OnClickListener {

            if (newSignerCheck.isChecked==true){


                contractSignatureIsChecked=true

                newEmailText.isEnabled=true
                newCoStartDateBtn.isEnabled=true
                newPhoneText.isEnabled=true
                newZipText.isEnabled=true
                newCityText.isEnabled=true
                newAdd1Text.isEnabled=true
                newStateSpinner.isEnabled=true
                newZipText2.isEnabled=true
                newAdd2Text.isEnabled=true
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
                contractSignerFieldsLinearLayourId.setBackgroundColor(newCoStartDateBtn.getContext().getResources().getColor(R.color.table_row_color));




            }
            if (newSignerCheck.isChecked==false) {
                contractSignatureIsChecked = false


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



        })


        }

    fun validateCertificationInputs() : Boolean{

        certDateTextView.setError(null)
        certTypeTextView.setError(null)


           var cert = FacilityDataModel.TblPersonnel()

        cert.iscertInputValid=true


        if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            cert.iscertInputValid = false
            certDateTextView.setError("Required Field")
        }
        if (newCertTypeSpinner.selectedItem.toString().contains("Not")){

            cert.iscertInputValid=false
            certTypeTextView.setError("required field")


        }



        return cert.iscertInputValid
    }



    fun validateInputs() : Boolean{

        var persn = FacilityDataModel.TblPersonnel()


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

                if (newStateSpinner.selectedItem.toString().contains("select")){

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

        var persn = FacilityDataModel.TblPersonnel()


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
