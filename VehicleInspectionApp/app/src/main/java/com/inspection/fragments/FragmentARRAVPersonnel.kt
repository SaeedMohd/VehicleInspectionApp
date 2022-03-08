package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
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
import com.inspection.Utils.Constants.UpdateFacilityPersonnelData
import com.inspection.Utils.Constants.UpdateFacilityPersonnelSignerData
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.app_adhoc_visitation_filter_fragment.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
import kotlinx.android.synthetic.main.fragment_arrav_programs.*

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
    private var statesAbbrev= arrayOf("Select State","AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN ", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY")
    private var personTypeArray = ArrayList<String>()
    private var certTypeArray = ArrayList<String>()
    private var personTypeIDsArray = ArrayList<String>()
    private var personListArray = ArrayList<String>()
    private var statesArray = ArrayList<String>()
    var hyperlinktxt : String =""
    var validationMsg = ""
    var edithyperlinktxt : String =""
    private var firstSelection = false // Variable used as the first item in the personnelType drop down is selected by default when the ata is loaded
    //    private val strFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFormat = SimpleDateFormat("dd MMM yyyy")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_aarav_personnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scopeOfServiceChangesWatcher()
        preparePersonnelPage()
        fillPersonnelTableView()
//        rspUserId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName.toString())
//        rspEmailId.setText(FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email.toString())
        IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited = true
        (activity as FormsActivity).personnelButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        exitDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }
        edit_exitDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            edit_addNewPersonnelDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }

        exitCertificateDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            addNewCertificateDialogue.visibility=View.GONE
            alphaBackgroundForPersonnelDialogs.visibility = View.GONE
        }

        exitEditCertificateDialogeBtnId.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            editNewCertificateDialogue.visibility=View.GONE
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
                (activity as FormsActivity).overrideBackButton = true
                addNewCertificateDialogue.visibility = View.VISIBLE
                alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
            }
        }


        editurlLink.isClickable = true;
        editurlLink.movementMethod = LinkMovementMethod.getInstance()


        addNewPersnRecordBtn.setOnClickListener {
            newFirstNameText.setText("")
            newLastNameText.setText("")
            newCertNoText.setText("")
            newStartDateBtn.setText("SELECT DATE")
            newEndDateBtn.setText("SELECT DATE")
            newSeniorityDateBtn.setText("SELECT DATE")
            newCoStartDateBtn.setText("SELECT DATE")
            newCoEndDateBtn.setText("SELECT DATE")
            newOEMStartDateBtn.setText("SELECT DATE")
            newOEMEndDateBtn.setText("SELECT DATE")
            newPhoneText.setText("")
            newZipText.setText("")
            newAdd1Text.setText("")
            newAdd2Text.setText("")
            newCityText.setText("")
            newZipText2.setText("")
            newEmailText.setText("")
            newStateSpinner.setSelection(0)
            newPersonnelTypeSpinner.setSelection(0)
            rspUserId.setText("")
            rspEmailId.setText("")
            newZipText.setError(null)
            newZipText2.setError(null)
            newPhoneText.setError(null)
            newCertNoText.setError(null)
            stateTextView.setError(null)
            newEmailText.setError(null)
            personnelTypeTextViewId.setError(null)
            onlyOneContractSignerLogic()
            addNewPersonnelDialogue.visibility=View.VISIBLE
//            val hyperlinktxt : String = "<a href='"+textViewAceURL.text.toString()+"'>Tap here to open Link</a>"

            urlLink.isClickable = true;
            urlLink.movementMethod = LinkMovementMethod.getInstance()
            hyperlinktxt = "<a href='"+newACEURLText.text.toString()+"'>"+newACEURLText.text.toString()+"</a>"
            urlLink.text = Html.fromHtml(hyperlinktxt,Html.FROM_HTML_MODE_COMPACT)
            alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
        }



        // contractSignerIsNotCheckedLogic()

        newCertStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newCertStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newCertStartDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        edit_newCertStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_newCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_newCertStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                edit_newCertStartDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newOEMStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newOEMStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newOEMStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newOEMStartDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newOEMEndDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newOEMEndDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newOEMEndDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newOEMEndDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newEditOEMStartDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newEditOEMStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newEditOEMStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newEditOEMStartDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newEditOEMEndDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newEditOEMEndDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newEditOEMEndDateBtn.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                newEditOEMEndDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newCoStartDateBtn.setOnClickListener {
//            if (newCoStartDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!newCoStartDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(newCoStartDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!edit_newEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(edit_newEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_newCoStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_newCoStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newSeniorityDateBtn.text.toString()))
                c.setTime(currentDate)
            }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_newSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_newSeniorityDateBtn.text.toString()))
                c.setTime(currentDate)
            }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!newStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(newStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_newStartDateBtn.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_newStartDateBtn.text.toString()))
                c.setTime(currentDate)
            }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
                (activity as FormsActivity).overrideBackButton = false
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

                var urlString = "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&personnelId=${selectedPersonnelID}"+
                        "&certId=&certificationTypeId=${item.CertificationTypeId}&certificationDate=${item.CertificationDate}&expirationDate=${item.ExpirationDate}"+
                        "&certDesc=${item.CertDesc}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}&active=1"
                Log.v("CERTIFICATION ADD --- ",Constants.UpdatePersonnelCertification + urlString)
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdatePersonnelCertification + urlString + Utility.getLoggingParameters(activity, 0, getCertificationChanges(0,selectedPersonnelID)),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<", false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Certification")
                                    item.CertID= response.toString().substring(response.toString().indexOf("<CertID")+8,response.toString().indexOf("</CertID"))
                                    FacilityDataModel.getInstance().tblPersonnelCertification.add(item)
                                    FacilityDataModelOrg.getInstance().tblPersonnelCertification.add(item)
                                    HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                    HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                    fillCertificationTableView(selectedPersonnelID)
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
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
            } else {
                Utility.showValidationAlertDialog(activity, validationMsg)
            }

        }

        submitNewPersnRecordBtn.setOnClickListener {

            if (validateInputs()){
                addNewPersonnelDialogue.visibility=View.GONE
                alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                personnelLoadingText.text = "Saving ..."
                personnelLoadingView.visibility = View.VISIBLE
                (activity as FormsActivity).overrideBackButton = false

                var PersonnelTypeId=""

                for (fac in TypeTablesModel.getInstance().PersonnelType) {
                    if (newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))

                        PersonnelTypeId =fac.PersonnelTypeID
                }

                var FirstName=if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString()
                var LastName=if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                var RSP_UserName=""//FacilityDataModel.getInstance().tblPersonnel[0].RSP_UserName
                var RSP_Email=""//FacilityDataModel.getInstance().tblPersonnel[0].RSP_Email
                var facNo=FacilityDataModel.getInstance().tblFacilities[0].FACNo
                var CertificationNum=if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
                var ContractSigner=if (newSignerCheck.isChecked==true) "true" else "false"
                var PrimaryMailRecipient=if (newACSCheck.isChecked==true) "true" else "false"
                var ReportRec =if (newReportCheck.isChecked==true) "1" else "0"
                var NotificationRec =if (newNotificationCheck.isChecked==true) "1" else "0"
                var startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var ExpirationDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var OEMStartDate = if (newOEMStartDateBtn.text.equals("SELECT DATE")) "" else newOEMStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var OEMEndDate = if (newOEMEndDateBtn.text.equals("SELECT DATE")) "" else newOEMEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                var ace_url=if (newACEURLText.text.toString().isNullOrEmpty()) "" else newACEURLText.text.toString()
//                Log.v("PERSONNEL ADD --- ",UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}")
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}" + Utility.getLoggingParameters(activity, 0, getPersonnelChanges(0,0)),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<",false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Personnel")
                                    var item = TblPersonnel()
                                    for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                        if (newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))
                                            item.PersonnelTypeID = fac.PersonnelTypeID.toInt()
                                    }
                                    item.PersonnelID= response.toString().substring(response.toString().indexOf("<PersonnelID")+13,response.toString().indexOf("</PersonnelID")).toInt()
                                    item.FirstName = if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString()
                                    item.LastName = if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.RSP_UserName = if (rspUserId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.RSP_Email = if (rspEmailId.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
                                    item.CertificationNum = if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
                                    item.ContractSigner = if (newSignerCheck.isChecked == true) true else false
                                    item.PrimaryMailRecipient = if (newACSCheck.isChecked == true) true else false
                                    item.startDate = if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.endDate = if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.SeniorityDate = if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    item.ReportRecipient = newReportCheck.isChecked
                                    item.NotificationRecipient = newNotificationCheck.isChecked
                                    HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                    HasChangedModel.getInstance().changeDoneForFacilityPersonnel()

                                    if (ContractSigner.toBoolean()){
                                        var signerItem = TblPersonnelSigner()
                                        signerItem.Addr1= if (newAdd1Text.text.toString().isNullOrEmpty()) "" else newAdd1Text.text.toString()
                                        signerItem.Addr2= if (newAdd2Text.text.toString().isNullOrEmpty()) "" else newAdd2Text.text.toString()
                                        signerItem.CITY= if (newCityText.text.toString().isNullOrEmpty()) "" else newCityText.text.toString()
//                                        signerItem.ST = if (newStateSpinner.selectedItem.toString().isNullOrEmpty()) "" else newStateSpinner.selectedItem.toString()
                                        signerItem.ST = if (newStateSpinner.selectedItemPosition==0) "" else statesAbbrev.get(newStateSpinner.selectedItemPosition);
                                        signerItem.ZIP= if (newZipText.text.toString().isNullOrEmpty()) "" else newZipText.text.toString()
                                        signerItem.ZIP4= if (newZipText2.text.toString().isNullOrEmpty()) "" else newZipText2.text.toString()
                                        signerItem.Phone= if (newPhoneText.text.equals("SELECT DATE")) "" else newPhoneText.text.toString()
                                        signerItem.email= newEmailText.text.toString()
                                        signerItem.ContractStartDate = if (newCoStartDateBtn.text.equals("SELECT DATE")) "" else newCoStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                        signerItem.PersonnelID = item.PersonnelID
                                        item.ContractEndDate = if (newCoEndDateBtn.text.equals("SELECT DATE")) "" else newCoEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityPersonnelSignerData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=${signerItem.PersonnelID}&addr1=${signerItem.Addr1}&addr2=${signerItem.Addr2}&city=${signerItem.CITY}&st=${signerItem.ST}&phone=${signerItem.Phone}&email=${signerItem.email}&zip=${signerItem.ZIP}&zip4=${signerItem.ZIP4}&contractStartDate=${signerItem.ContractStartDate}&contractEndDate=${item.ContractEndDate}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1",
                                                Response.Listener { response ->
                                                    activity!!.runOnUiThread {
                                                        if (response.toString().contains("returnCode>0<",false)) {
                                                            Utility.showSubmitAlertDialog(activity, true, "Contract Signer")
                                                            FacilityDataModel.getInstance().tblPersonnel.add(item)
                                                            if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { s->s.PersonnelID==signerItem.PersonnelID }.isEmpty())
                                                                FacilityDataModel.getInstance().tblPersonnelSigner.add(signerItem)
                                                            else {
                                                                FacilityDataModel.getInstance().tblPersonnelSigner.removeIf {s->s.PersonnelID==signerItem.PersonnelID }
                                                                FacilityDataModel.getInstance().tblPersonnelSigner.add(signerItem)
                                                            }
                                                            fillPersonnelTableView()
                                                            altTableRow(2)
                                                        } else {
                                                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
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
                                    }
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
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
//                Utility.showValidationAlertDialog(activity,"Please fill all the required fields")
                Utility.showValidationAlertDialog(activity, validationMsg)
            }
        }
        onlyOneMailRecepientLogic()
        altTableRow(2)
        altCertTableRow(2)
    }

    fun getCertificationChanges(action : Int,personnelId: Int) : String {
        var strChanges = ""
        if (action==0) {
            strChanges += "New Certification for personnel id ("+personnelId+") added as: Certification Type (" + newCertTypeSpinner.selectedItem.toString() + ") , Description (" + newCertDescText.text.toString() + "), Start Date (" + newCertStartDateBtn.text.toString() + ") and End Date (" + newCertEndDateBtn.text.toString() + ")"
        }
//        else { // personnelID for Edit is the rowID
//            if (edit_.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnelCertification[personnelId].CertID) {
//                strChanges += "Email changed from (" + FacilityDataModelOrg.getInstance().tblFacilityEmail[rowId].email + ") to (" + newChangesEmailText.text.toString() + ") - "
//            }
//        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun getPersonnelChanges(action : Int,rowId: Int) : String {
        var strChanges = ""
        if (action==0) {
            strChanges += "New personnel added with first name (" + if (newFirstNameText.text.toString().isNullOrEmpty()) "" else newFirstNameText.text.toString() + ") , last name (" + if (newLastNameText.text.toString().isNullOrEmpty()) "" else newLastNameText.text.toString()
            strChanges += "), position (" + newPersonnelTypeSpinner.getSelectedItem().toString() + ") and start date (" + if (newStartDateBtn.text.equals("SELECT DATE")) "" else newStartDateBtn.text.toString()
            strChanges += "), end date (" + if (newEndDateBtn.text.equals("SELECT DATE")) "" else newEndDateBtn.text.toString() + ") and certification ID # (" + if (newCertNoText.text.toString().isNullOrEmpty()) "" else newCertNoText.text.toString()
            strChanges += "), siniority date (" + if (newSeniorityDateBtn.text.equals("SELECT DATE")) "" else newSeniorityDateBtn.text.toString() + ") and contract signer (" + if (newSignerCheck.isChecked==true) "true" else "false"
            strChanges += ") and primary mail recipient (" + if (newACSCheck.isChecked==true) "true" else "false" + ")"
        }
        else {
            if (edit_newFirstNameText.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].FirstName) {
                strChanges += "First Name changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].FirstName + ") to (" + edit_newFirstNameText.text.toString() + ") - "
            }
            if (edit_newLastNameText.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].LastName) {
                strChanges += "Last Name changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].LastName + ") to (" + edit_newLastNameText.text.toString() + ") - "
            }
            if (edit_newPersonnelTypeSpinner.getSelectedItem().toString() != (TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeID.toInt().equals(FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PersonnelTypeID)}[0].PersonnelTypeName) ) {
                strChanges += "Position changed from (" + TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeID.equals(FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PersonnelTypeID.toString())}[0].PersonnelTypeName + ") to (" + edit_newPersonnelTypeSpinner.getSelectedItem().toString() + ") - "
            }
            if (!edit_newStartDateBtn.text.toString().equals("SELECT DATE")) {
                if (edit_newStartDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].startDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "Start Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].startDate.apiToAppFormatMMDDYYYY() + ") to (" + edit_newStartDateBtn.text.toString() + ") - "
                }
            }
            if (!edit_newEndDateBtn.text.toString().equals("SELECT DATE")) {
                if (edit_newEndDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].endDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "End Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].endDate.apiToAppFormatMMDDYYYY() + ") to (" + edit_newEndDateBtn.text.toString() + ") - "
                }
            }
            if (!edit_newSeniorityDateBtn.text.toString().equals("SELECT DATE")) {
                if (edit_newSeniorityDateBtn.text.toString() != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].SeniorityDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "End Date changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].SeniorityDate.apiToAppFormatMMDDYYYY() + ") to (" + edit_newSeniorityDateBtn.text.toString() + ") - "
                }
            }
            if (edit_newSignerCheck.isChecked != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ContractSigner) {
                strChanges += "Contract Signer flag changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].ContractSigner + ") to (" + edit_newSignerCheck.isChecked + ") - "
            }
            if (edit_newACSCheck.isChecked != FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PrimaryMailRecipient) {
                strChanges += "Contract Signer flag changed from (" + FacilityDataModelOrg.getInstance().tblPersonnel[rowId].PrimaryMailRecipient + ") to (" + edit_newACSCheck.isChecked + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

//    var ContractSigner=
//    var PrimaryMailRecipient=

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
        personnelTypeList=TypeTablesModel.getInstance().PersonnelType
        personTypeArray.clear()
        personTypeIDsArray.clear()
        personTypeIDsArray.add("-1")
        personTypeArray.add("Not Selected")
        for (fac in personnelTypeList) {
                personTypeArray.add(fac.PersonnelTypeName)
                personTypeIDsArray.add(fac.PersonnelTypeID)
        }
         var personTypeAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, personTypeArray)
        personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPersonnelTypeSpinner.adapter = personTypeAdapter
        edit_newPersonnelTypeSpinner.adapter = personTypeAdapter

        certificationTypeList=TypeTablesModel.getInstance().PersonnelCertificationType
        certTypeArray.clear()
        certTypeArray.add("Not Selected")
        for (fac in certificationTypeList) {
            certTypeArray.add(fac.PersonnelCertName)
        }
        var certTypeAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, certTypeArray)
        certTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newCertTypeSpinner.adapter = certTypeAdapter

        var citiesAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, states)
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newStateSpinner.adapter = citiesAdapter
        edit_newStateSpinner.adapter = citiesAdapter
// HERE


        newCertCatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                certTypeArray.clear()
                certTypeArray.add("Not Selected")
                for (fac in certificationTypeList) {
                    if (newCertCatSpinner.selectedItem.toString().equals("ASE")) {
                        if (fac.Category.equals(newCertCatSpinner.selectedItem.toString()) || fac.Category.isNullOrEmpty())
                            certTypeArray.add(fac.PersonnelCertName)
                    } else {
                        if (fac.Category.equals(newCertCatSpinner.selectedItem.toString()))
                            certTypeArray.add(fac.PersonnelCertName)
                    }
                }
                newCertTypeSpinner.setSelection(0);
            }
        }
        edit_newCertTypeSpinner.isEnabled = false
        edit_newCertCatSpinner.isEnabled = false
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

    fun fillData(){
        endDateMustBeAfterStartDateLogic()
        edit_endDateMustBeAfterStartDateLogic()
        newZipText.addTextChangedListener(zipOfFiveDigitsWatcher)
        newZipText2.addTextChangedListener(zipOfFourDigitsWatcher)
        newPhoneText.addTextChangedListener(phoneTenDigitsWatcher)
        newEmailText.addTextChangedListener(emailValidationWatcher)
        newACEURLText.addTextChangedListener(addNewAceUrlWatcher)
        newEditACEURLText.addTextChangedListener(editAceUrlWatcher)
    }

    fun emailFormatValidation(target : CharSequence) : Boolean{
        emailValid = !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        return emailValid }


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


    var editAceUrlWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            edithyperlinktxt = "<a href='"+newEditACEURLText.text.toString()+"'>"+newEditACEURLText.text.toString()+"</a>"
            editurlLink.text = Html.fromHtml(edithyperlinktxt,Html.FROM_HTML_MODE_COMPACT)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    var addNewAceUrlWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            hyperlinktxt = "<a href='"+newACEURLText.text.toString()+"'>"+newACEURLText.text.toString()+"</a>"
            urlLink.text = Html.fromHtml(hyperlinktxt,Html.FROM_HTML_MODE_COMPACT)
        }

        override fun afterTextChanged(s: Editable) {

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

        newCoEndDateBtn.setOnClickListener {
            if (newCoStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                newCoEndDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Start Date")
            }
            else {
                newCoEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!newCoEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(newCoEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCoEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        }
        newEndDateBtn.setOnClickListener {
            if (newStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                newStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Start Date")
            }
            else {
                newEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!newEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(newEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        }
        newCertEndDateBtn.setOnClickListener {
            if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                newCertStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Certificate Start Date")
            }
            else {
                newCertStartDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!newCertStartDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(newCertStartDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCertEndDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()

            }

        }

        edit_newCertEndDateBtn.setOnClickListener {
            if (edit_newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
                edit_newCertStartDateBtn.setError("Required Field")
                Utility.showValidationAlertDialog(activity,"Please enter Certificate Start Date")
            }
            else {
                edit_newCertEndDateBtn.setError(null)
                val c = Calendar.getInstance()
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!edit_newCertEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(edit_newCertEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    edit_newCertEndDateBtn!!.text = sdf.format(c.time)
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
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!edit_newCoEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(edit_newCoEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                if (!edit_newEndDateBtn.text.toString().equals("SELECT DATE")) {
                    var currentDate = (sdf.parse(edit_newEndDateBtn.text.toString()))
                    c.setTime(currentDate)
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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

        addNewPersnRecordBtn.isEnabled = (FacilityDataModel.getInstance().tblPersonnel.filter {s->s.PrimaryMailRecipient==true}.isNotEmpty())
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

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1.5F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1.5F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0
        rowLayoutParam7.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.width = 0
        rowLayoutParam8.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1F
        rowLayoutParam9.column = 9
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.width = 0
        rowLayoutParam9.gravity = Gravity.CENTER_HORIZONTAL

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 1F
        rowLayoutParam10.column = 10
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.width = 0
        rowLayoutParam10.gravity = Gravity.CENTER_HORIZONTAL
        var dateTobeFormated = ""

        val rowLayoutParam11 = TableRow.LayoutParams()
        rowLayoutParam11.weight = 0.8F
        rowLayoutParam11.column = 13
        rowLayoutParam11.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam11.width = 0
        rowLayoutParam11.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam12 = TableRow.LayoutParams()
        rowLayoutParam12.weight = 1F
        rowLayoutParam12.column = 11
        rowLayoutParam12.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam12.width = 0
        rowLayoutParam12.gravity = Gravity.CENTER_HORIZONTAL
//        var dateTobeFormated = ""

        val rowLayoutParam13 = TableRow.LayoutParams()
        rowLayoutParam13.weight = 0.8F
        rowLayoutParam13.column = 12
        rowLayoutParam13.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam13.width = 0
        rowLayoutParam13.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParamRow.width = TableRow.LayoutParams.WRAP_CONTENT
//        rowLayoutParamRow.weight=1F

        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {

                if (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}.isNotEmpty()) {
//                    get(it).ASE_Cert_URL = PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].asecerturl
                    get(it).OEMstartDate = PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].oemstartdate
                    get(it).OEMendDate = PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].oemenddate
//                    get(it).ReportRecipient = (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].reportrecipient==1)
//                    get(it).NotificationRecipient = (PRGDataModel.getInstance().tblPRGPersonnelDetails.filter { s->s.personnelid.equals(get(it).PersonnelID.toString())}[0].notificationrecipient==1)
                }

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
                textView1.textSize = 12f
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.gravity = Gravity.CENTER_VERTICAL
                textView2.text = get(it).FirstName
                textView2.tag = get(it).PersonnelID
                textView2.minimumHeight = 30
                textView2.textSize = 12f
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.gravity = Gravity.CENTER_VERTICAL
                textView3.minimumHeight = 30
                textView3.text = get(it).LastName
                textView3.textSize = 12f
                tableRow.addView(textView3)

                val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.gravity = Gravity.CENTER_VERTICAL
                textView4.text = get(it).RSP_UserName
                textView4.minimumHeight = 30
                textView4.textSize = 12f
                tableRow.addView(textView4)

                val textView5 = TextView(context)
                textView5.layoutParams = rowLayoutParam4
                textView5.gravity = Gravity.CENTER_VERTICAL
                textView5.textSize = 12f
                textView5.text = if (get(it).ContractSigner) FacilityDataModel.getInstance().tblPersonnelSigner.filter { s->s.PersonnelID==get(it).PersonnelID}[0].email else get(it).RSP_Email
                textView5.minimumHeight = 30
                tableRow.addView(textView5)

                val textView50 = TextView(context)
                textView50.text = get(it).RSP_Email

                val textView6 = TextView(context)
                textView6.layoutParams = rowLayoutParam5
                textView6.minimumHeight = 30
                textView6.gravity = Gravity.CENTER_VERTICAL
                textView6.textSize = 12f
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



                val textView8 = TextView(context)
                textView8.layoutParams = rowLayoutParam6
                textView8.gravity = Gravity.CENTER_VERTICAL
                textView8.minimumHeight = 30
                textView8.textSize = 12f
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
                textView9.layoutParams = rowLayoutParam7
                textView9.gravity = Gravity.CENTER_VERTICAL
                textView9.minimumHeight = 30
                textView9.textSize = 12f
                if (!(get(it).endDate.isNullOrEmpty())) {
                    try {
                        textView9.text = if (get(it).endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).endDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView9.text = get(it).endDate.apiToAppFormatMMDDYYYY()
                    }
                } else {
                    textView9.text = ""
                }

                tableRow.addView(textView9)

                val textView7 = TextView(context)
                textView7.layoutParams = rowLayoutParam8
                textView7.gravity = Gravity.CENTER_VERTICAL
                textView7.text = get(it).CertificationNum
                textView7.minimumHeight = 30
                textView7.textSize = 12f
                tableRow.addView(textView7)

                val checkBox10 = CheckBox(context)
                checkBox10.layoutParams = rowLayoutParam9
                checkBox10.gravity = Gravity.CENTER
                checkBox10.isChecked = get(it).ContractSigner
                checkBox10.minimumHeight = 30
                checkBox10.isEnabled=false
                checkBox10.textSize = 12f
                tableRow.addView(checkBox10)

                val checkBox11 = CheckBox(context)
                checkBox11.layoutParams = rowLayoutParam10
                checkBox11.gravity = Gravity.CENTER
                checkBox11.isChecked = get(it).PrimaryMailRecipient
                checkBox11.isEnabled=false
                checkBox11.minimumHeight = 30
                checkBox11.textSize = 12f
                tableRow.addView(checkBox11)

                val checkBox12 = CheckBox(context)
                checkBox12.layoutParams = rowLayoutParam12
                checkBox12.gravity = Gravity.CENTER
                checkBox12.isChecked = get(it).ReportRecipient
                checkBox12.minimumHeight = 30
                checkBox12.isEnabled=false
                checkBox12.textSize = 12f
                tableRow.addView(checkBox12)

                val checkBox13 = CheckBox(context)
                checkBox13.layoutParams = rowLayoutParam13
                checkBox13.gravity = Gravity.CENTER
                checkBox13.isChecked = get(it).NotificationRecipient
                checkBox13.isEnabled=false
                checkBox13.minimumHeight = 30
                checkBox13.textSize = 12f
                tableRow.addView(checkBox13)

                val textViewOEMStart = TextView(context)
                if (!(get(it).OEMstartDate.isNullOrEmpty())) {
                    try {
                        textViewOEMStart.text = if (get(it).OEMstartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).OEMstartDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textViewOEMStart.text = ""
                    }
                } else {
                    textViewOEMStart.text = ""
                }
                val textViewOEMEnd = TextView(context)

                if (!(get(it).OEMendDate.isNullOrEmpty())) {
                    try {
                        textViewOEMEnd.text = if (get(it).OEMendDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).OEMendDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textViewOEMEnd.text = ""
                    }
                } else {
                    textViewOEMEnd.text = ""
                }

                val textViewAceURL = TextView(context)
                textViewAceURL.text = get(it).ASE_Cert_URL
                textViewAceURL.text = get(it).ASE_Cert_URL



                val updateBtn = TextView(context)
                updateBtn.layoutParams = rowLayoutParam11
                updateBtn.setTextColor(Color.BLUE)
                updateBtn.text = "EDIT"
                updateBtn.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                updateBtn.textSize = 12f
                updateBtn .setBackgroundColor(Color.TRANSPARENT)

                tableRow.addView(updateBtn)

                updateBtn.isEnabled = (FacilityDataModel.getInstance().tblPersonnel.filter {s->s.PrimaryMailRecipient==true}.isNotEmpty())

                PersonnelResultsTbl.addView(tableRow)

                updateBtn.setOnClickListener {
                    var contractSignerFound = 0
                    var emailPrimaryFound = 0

                    if (checkBox10.isChecked){
                        edit_enableContractSignerIsChecked()
                        edit_newSignerCheck.isChecked=true
                        edit_newFirstNameText.isEnabled = false
                        edit_newLastNameText.isEnabled = false
                        edit_newSignerCheck.isEnabled=false
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
                        edit_newSignerCheck.isEnabled=false
                        edit_newFirstNameText.isEnabled = true
                        edit_newLastNameText.isEnabled = true
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
                    edit_rspEmailId.setText(textView50.text)
                    edit_rspUserId.setText(textView4.text)
                    edit_rspEmailId.isEnabled = (edit_rspUserId.text.isNullOrEmpty())
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

                    if (textViewOEMEnd.text.isNullOrEmpty()) {
                        newEditOEMEndDateBtn.setText("SELECT DATE")
                    }else{
                        newEditOEMEndDateBtn.setText(textViewOEMEnd.text)
                    }

                    if (textViewOEMStart.text.isNullOrEmpty()) {
                        newEditOEMStartDateBtn.setText("SELECT DATE")
                    }else{
                        newEditOEMStartDateBtn.setText(textViewOEMStart.text)
                    }

                    newEditACEURLText.setText(textViewAceURL.text)
                    edithyperlinktxt = "<a href='"+newEditACEURLText.text.toString()+"'>"+newEditACEURLText.text.toString()+"</a>"
                    editurlLink.text = Html.fromHtml(edithyperlinktxt,Html.FROM_HTML_MODE_COMPACT)

                    if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { S->S.PersonnelID==textView2.tag}.count()>0){
                        var model = TblPersonnelSigner()
                        model = FacilityDataModel.getInstance().tblPersonnelSigner.filter { S->S.PersonnelID==textView2.tag}[0]
                        edit_newPhoneText.setText(model.Phone)
                        edit_newZipText.setText(model.ZIP)
                        edit_newAdd1Text.setText(model.Addr1)
                        edit_newAdd2Text.setText(model.Addr2)
                        edit_newCityText.setText(model.CITY)
                        edit_newZipText2.setText(model.ZIP4)
                        edit_newEmailText.setText(model.email)
                        edit_newStateSpinner.setSelection(statesAbbrev.indexOf(model.ST))
                        if (model.ContractStartDate.isNullOrEmpty() || model.ContractStartDate.equals("01/01/1900")) {
                            edit_newCoStartDateBtn.setText("SELECT DATE")
                        } else {
                            edit_newCoStartDateBtn.setText(model.ContractStartDate.apiToAppFormatMMDDYYYY())
                        }
                    } else {
                        edit_newPhoneText.setText("")
                        edit_newZipText.setText("")
                        edit_newAdd1Text.setText("")
                        edit_newAdd2Text.setText("")
                        edit_newCityText.setText("")
                        edit_newZipText2.setText("")
                        edit_newEmailText.setText(textView5.text)
                        edit_newStateSpinner.setSelection(0)
                        edit_newCoStartDateBtn.setText("SELECT DATE")
                    }
                    edit_newCoEndDateBtn.setText("SELECT DATE")
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
                    edit_newReportCheck.isChecked = checkBox12.isChecked
                    edit_newNotificationCheck.isChecked = checkBox13.isChecked
                    edit_addNewPersonnelDialogue.visibility=View.VISIBLE
                    (activity as FormsActivity).overrideBackButton = true
                    alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
                    edit_submitNewPersnRecordBtn.setOnClickListener {

                        if (edit_validateInputs()){
                            edit_addNewPersonnelDialogue.visibility=View.GONE
                            alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                            (activity as FormsActivity).overrideBackButton = false
                            personnelLoadingText.text = "Saving ..."
                            personnelLoadingView.visibility = View.VISIBLE
                            var PersonnelTypeId=""
                            for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                if (edit_newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))
                                    PersonnelTypeId =fac.PersonnelTypeID
                            }
                            var FirstName=if (edit_newFirstNameText.text.toString().isNullOrEmpty()) "" else edit_newFirstNameText.text.toString()
                            var LastName=if (edit_newLastNameText.text.toString().isNullOrEmpty()) "" else edit_newLastNameText.text.toString()
                            var RSP_UserName=edit_rspUserId.text.toString()
                            var RSP_Email=edit_rspEmailId.text.toString()
                            var facNo=FacilityDataModel.getInstance().tblFacilities[0].FACNo
                            var CertificationNum=if (edit_newCertNoText.text.toString().isNullOrEmpty()) "" else edit_newCertNoText.text.toString()
                            var ContractSigner=if (edit_newSignerCheck.isChecked==true) "true" else "false"
                            var PrimaryMailRecipient=if (edit_newACSCheck.isChecked==true) "true" else "false"
                            var ReportRec = edit_newReportCheck.isChecked
                            var NotificationRec = edit_newNotificationCheck.isChecked
                            var startDate = if (edit_newStartDateBtn.text.equals("SELECT DATE")) "" else edit_newStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var ExpirationDate = if (edit_newEndDateBtn.text.equals("SELECT DATE")) "" else edit_newEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var SeniorityDate = if (edit_newSeniorityDateBtn.text.equals("SELECT DATE")) "" else edit_newSeniorityDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var personnelID = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex].PersonnelID
                            var OEMStartDate = if (newEditOEMStartDateBtn.text.equals("SELECT DATE")) "" else newEditOEMStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var OEMEndDate = if (newEditOEMEndDateBtn.text.equals("SELECT DATE")) "" else newEditOEMEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var ace_url=if (newEditACEURLText.text.toString().isNullOrEmpty()) "" else newEditACEURLText.text.toString()
//                            Log.v("PERSONNEL EDIT --- ",UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=${personnelID}&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}")
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityPersonnelData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=${personnelID}&personnelTypeId=$PersonnelTypeId&firstName=$FirstName&lastName=${LastName}&seniorityDate=$SeniorityDate&certificationNum=$CertificationNum&startDate=$startDate&contractSigner=$ContractSigner&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1&primaryMailRecipient=$PrimaryMailRecipient&rsp_userName=$RSP_UserName&rsp_email=$RSP_Email&rsp_phone=&endDate=${ExpirationDate}&ASE_URL=${ace_url}&OEMStartDate=${OEMStartDate}&OEMEndDate=${OEMEndDate}&ReportRecipient=${ReportRec}&NotificationRecipient=${NotificationRec}" + Utility.getLoggingParameters(activity, 1, getPersonnelChanges(1,currentfacilityDataModelIndex)),
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread {
                                            if (response.toString().contains("returnCode>0<",false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Personnel")
                                                var item = FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex]
                                                for (fac in TypeTablesModel.getInstance().PersonnelType) {
                                                    if (edit_newPersonnelTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelTypeName))
                                                        item.PersonnelTypeID = fac.PersonnelTypeID.toInt()
                                                }
                                                item.FirstName = FirstName
                                                item.LastName = LastName
                                                item.RSP_UserName = RSP_UserName
                                                item.RSP_Email = RSP_Email
                                                item.CertificationNum = CertificationNum
                                                item.ContractSigner = ContractSigner.toBoolean()
                                                item.PrimaryMailRecipient = PrimaryMailRecipient.toBoolean()
                                                item.startDate = startDate
                                                item.endDate = ExpirationDate
                                                item.SeniorityDate = SeniorityDate
                                                item.ASE_Cert_URL = ace_url
                                                item.OEMstartDate = OEMStartDate
                                                item.OEMendDate= OEMEndDate
                                                item.ReportRecipient = ReportRec
                                                item.NotificationRecipient = NotificationRec
                                                HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                                HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                                if (ContractSigner.toBoolean()){
                                                    val coAddr1= if (edit_newAdd1Text.text.toString().isNullOrEmpty()) "" else edit_newAdd1Text.text.toString()
                                                    val coAddr2= if (edit_newAdd2Text.text.toString().isNullOrEmpty()) "" else edit_newAdd2Text.text.toString()
                                                    val coCITY= if (edit_newCityText.text.toString().isNullOrEmpty()) "" else edit_newCityText.text.toString()
//                                                    val coST= if (edit_newStateSpinner.selectedItem.toString().isNullOrEmpty()) "" else edit_newStateSpinner.selectedItem.toString()
                                                    val coST = if (edit_newStateSpinner.selectedItemPosition==0) "" else statesAbbrev.get(edit_newStateSpinner.selectedItemPosition);
                                                    val coZIP= if (edit_newZipText.text.toString().isNullOrEmpty()) "" else edit_newZipText.text.toString()
                                                    val coZIP4= if (edit_newZipText2.text.toString().isNullOrEmpty()) "" else edit_newZipText2.text.toString()
                                                    val coPhone= if (edit_newPhoneText.text.equals("SELECT DATE")) "" else edit_newPhoneText.text.toString()
                                                    val coemail= edit_newEmailText.text.toString()
                                                    val coContractStartDate = if (edit_newCoStartDateBtn.text.equals("SELECT DATE")) "" else edit_newCoStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                    val coContractEndDate = if (edit_newCoEndDateBtn.text.equals("SELECT DATE")) "" else edit_newCoEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityPersonnelSignerData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&personnelId=${item.PersonnelID}&addr1=${coAddr1}&addr2=${coAddr2}&city=${coCITY}&st=${coST}&phone=${coPhone}&email=${coemail}&zip=${coZIP}&zip4=${coZIP4}&contractStartDate=${coContractStartDate}&contractEndDate=${coContractEndDate}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat()+"&active=1" ,
                                                            Response.Listener { response ->
                                                                activity!!.runOnUiThread {
                                                                    if (response.toString().contains("returnCode>0<",false)) {
                                                                        Utility.showSubmitAlertDialog(activity, true, "Contract Signer")
                                                                        item.ContractStartDate=coContractStartDate
                                                                        item.ContractEndDate=coContractEndDate
                                                                        item.email=coemail
                                                                        item.Addr1=coAddr1
                                                                        item.Addr2=coAddr2
                                                                        item.CITY=coCITY
                                                                        item.ST=coST
                                                                        item.ZIP4=coZIP4
                                                                        item.ZIP=coZIP
                                                                        item.Phone=coPhone
                                                                        var signerItem = TblPersonnelSigner()
                                                                        signerItem.PersonnelID = item.PersonnelID
                                                                        signerItem.ContractStartDate=coContractStartDate
                                                                        signerItem.email=coemail
                                                                        signerItem.Addr1=coAddr1
                                                                        signerItem.Addr2=coAddr2
                                                                        signerItem.CITY=coCITY
                                                                        signerItem.ST=coST
                                                                        signerItem.ZIP4=coZIP4
                                                                        signerItem.ZIP=coZIP
                                                                        signerItem.Phone=coPhone
                                                                        if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { s->s.PersonnelID==signerItem.PersonnelID }.isEmpty())
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.add(signerItem)
                                                                        else {
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.removeIf {s->s.PersonnelID==signerItem.PersonnelID }
                                                                            FacilityDataModel.getInstance().tblPersonnelSigner.add(signerItem)
                                                                        }
                                                                        fillPersonnelTableView()
                                                                        altTableRow(2)
                                                                    } else {
                                                                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
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
                                                    FacilityDataModel.getInstance().tblPersonnel[currentfacilityDataModelIndex] = item
                                                    FacilityDataModelOrg.getInstance().tblPersonnel[currentfacilityDataModelIndex] = item
                                                    FacilityDataModelOrg.getInstance().tblPersonnel[currentfacilityDataModelIndex] = item
                                                    fillPersonnelTableView()
                                                    altTableRow(2)
                                                }
                                                personnelLoadingView.visibility = View.GONE
                                                personnelLoadingText.text = "Loading ..."
                                            } else {
                                                //                                                Utility.showSubmitAlertDialog(activity, false, "Personnel")
                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                Utility.showSubmitAlertDialog(activity,false,"Personnel (Error: "+ errorMessage+" )")
                                            }
                                        }
                                    }, Response.ErrorListener {
                                Utility.showSubmitAlertDialog(activity, false, "Personnel (Error: "+it.message+" )")
                                personnelLoadingView.visibility = View.GONE
                                personnelLoadingText.text = "Loading ..."
                                fillPersonnelTableView()
                                altTableRow(2)
                            }))

                        } else {

                            //                            Toast.makeText(context,"please fill the required fields",Toast.LENGTH_SHORT).show()
//                            Utility.showValidationAlertDialog(activity,"Please fill all the required fields")
                            Utility.showValidationAlertDialog(activity, validationMsg)

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
                    rowLayoutParam.weight = 0.5F
                    rowLayoutParam.column = 0
                    rowLayoutParam.leftMargin = 10
                    rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam.width = 0
                    rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam1 = TableRow.LayoutParams()
                    rowLayoutParam1.weight = 2F
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

                    val rowLayoutParam3 = TableRow.LayoutParams()
                    rowLayoutParam3.weight = 1F
                    rowLayoutParam3.column = 3
                    rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam3.width = 0
                    rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam4 = TableRow.LayoutParams()
                    rowLayoutParam4.weight = 1F
                    rowLayoutParam4.column = 4
                    rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam4.width = 0
                    rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParam5 = TableRow.LayoutParams()
                    rowLayoutParam5.weight = 0.5F
                    rowLayoutParam5.column = 5
                    rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParam5.width = 0
                    rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

                    val rowLayoutParamhidden = TableRow.LayoutParams()
                    rowLayoutParamhidden.weight = 0F
                    rowLayoutParamhidden.column = 5
                    rowLayoutParamhidden.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParamhidden.width = 0
                    rowLayoutParamhidden.gravity = Gravity.CENTER_VERTICAL


                    val rowLayoutParamRow = TableRow.LayoutParams()
                    rowLayoutParamRow.height = TableRow.LayoutParams.WRAP_CONTENT
                    rowLayoutParamRow.weight=1F


                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow


                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.text = if (get(it).CertificationTypeId.contains("OEM")) "OEM" else "ASE"
                    textView.textSize = 14f
                    tableRow.addView(textView)


                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    textView1.text = TypeTablesModel.getInstance().PersonnelCertificationType.filter { s->s.PersonnelCertID.equals(get(it).CertificationTypeId)}[0].PersonnelCertName
                    textView1.textSize = 14f
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    try {
                        textView2.text = get(it).CertificationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView2.text = ""
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
//                    TableRow.LayoutParams()
                    try {
                        textView3.text = get(it).ExpirationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView3.text = ""
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER
                    textView4.text = get(it).CertDesc
                    textView4.textSize = 14f
                    tableRow.addView(textView4)

                    val textViewhidden = TextView(context)
                    textViewhidden.layoutParams = rowLayoutParamhidden
                    textViewhidden.gravity = Gravity.CENTER
                    textViewhidden.text = get(it).CertID
                    textViewhidden.textSize = 14f
                    tableRow.addView(textViewhidden)

                    val updateCertBtn = TextView(context)
                    updateCertBtn.layoutParams = rowLayoutParam5
                    updateCertBtn.setTextColor(Color.BLUE)
                    updateCertBtn.text = "EDIT"
                    updateCertBtn.gravity = Gravity.CENTER
                    updateCertBtn.textSize = 12f
//                    updateCertBtn.tag = get(it).CertID
                    updateCertBtn .setBackgroundColor(Color.TRANSPARENT)

                    tableRow.addView(updateCertBtn)

                    certificationsTable.addView(tableRow)

                    updateCertBtn.setOnClickListener {
                        editNewCertificateDialogue.visibility=View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        alphaBackgroundForPersonnelDialogs.visibility = View.VISIBLE
//                        var currentModelIndex = FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.CertificationTypeId.equals(textView.text.toString()) && s.}

//                        FacilityDataModel.getInstance().tblPersonnelCertification[currentTableRowIndex].apply {
//                            (0 until size).forEach { it2 ->
//                                if (get(it2).CertificationTypeId.contains("OEM")) edit_newCertCatSpinner.text = "OEM" else edit_newCertCatSpinner.text = "ASE"
//                                edit_newCertTypeSpinner.text = get(it2).CertificationTypeId
//                            }
//                        }

                        edit_newCertCatSpinner.text = textView.text.toString()
                        edit_newCertTypeSpinner.text = textView1.text.toString()
                        edit_newCertStartDateBtn.text = textView2.text.toString()
                        edit_newCertEndDateBtn.text = textView3.text.toString()
                        var currentCertId = textViewhidden.text.toString()

                        editNewCertBtn.setOnClickListener {
                            if (edit_validateCertificationInputs()) {
                                editNewCertificateDialogue.visibility = View.GONE
                                alphaBackgroundForPersonnelDialogs.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                personnelLoadingText.text = "Saving ..."
                                personnelLoadingView.visibility = View.VISIBLE

                                var item = TblPersonnelCertification()
//                                for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
//                                    if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
//                                        item.CertificationTypeId = fac.PersonnelCertID
//                                }
                                item = FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId) }[0]


                                var urlString = "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&personnelId=${item.PersonnelID}"+
                                        "&certId=${currentCertId}&certificationTypeId=${item.CertificationTypeId}&certificationDate=${edit_newCertStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()}&expirationDate=${edit_newCertEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()}"+
                                        "&certDesc=${item.CertDesc}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}&active=1"
                                Log.v("CERTIFICATION ADD --- ",Constants.UpdatePersonnelCertification + urlString)
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdatePersonnelCertification + urlString + Utility.getLoggingParameters(activity, 0, getCertificationChanges(0,selectedPersonnelID)),
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "Certification")
//                                                    item.CertID= response.toString().substring(response.toString().indexOf("<CertID")+8,response.toString().indexOf("</CertID"))
//                                                    FacilityDataModelOrg.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId)}[0].apply {
//                                                        (0 until size).forEach {
//                                                            get(it).CertificationDate = item.CertificationDate
//                                                            get(it).ExpirationDate = item.ExpirationDate
//                                                        }
//                                                    }
                                                    item.CertificationDate = edit_newCertStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                    item.ExpirationDate = edit_newCertEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                    item = FacilityDataModelOrg.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId) }[0]
                                                    item.CertificationDate = edit_newCertStartDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                                                    item.ExpirationDate = edit_newCertEndDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
//                                                    FacilityDataModel.getInstance().tblPersonnelCertification.filter { s->s.CertID.equals(currentCertId)}[0].apply {
//                                                        (0 until size).forEach {
//                                                            get(it).CertificationDate = item.CertificationDate
//                                                            get(it).ExpirationDate = item.ExpirationDate
//                                                        }
//                                                    }
                                                    HasChangedModel.getInstance().groupFacilityPersonnel[0].FacilityPersonnel= true
                                                    HasChangedModel.getInstance().changeDoneForFacilityPersonnel()
                                                    fillCertificationTableView(selectedPersonnelID)
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
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
                                editNewCertificateDialogue.visibility = View.GONE
                            }
                            else {
                                Utility.showValidationAlertDialog(activity, validationMsg)
                            }
                        }
                    }
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
                textView.text = endDate.apiToAppFormatMMDDYYYY()

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
            textView.text = if (ContractSigner) FacilityDataModel.getInstance().tblPersonnelSigner.filter { s->s.PersonnelID==PersonnelID}[0].email else RSP_Email
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
            textView.text = endDate.apiToAppFormatMMDDYYYY()
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
                if (get(it).ContractSigner) {
                    newSignerCheck.isEnabled = false
//                    edit_newSignerCheck.isEnabled = false
                    alreadyContractSignerFound = true
                    disablecontractSignerFeilds()
                }
            }
            if (!alreadyContractSignerFound){
                newSignerCheck.isEnabled=false
//                edit_newSignerCheck.isEnabled=true
                disablecontractSignerFeilds()
            }
            newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                if (newSignerCheck.isChecked) {
                    if (alreadyContractSignerFound) {
                        newSignerCheck.isChecked=false
                        disablecontractSignerFeilds()
                        Utility.showValidationAlertDialog(activity,"There is already a contract signer for this contract")
                    }else
                    {
                        Utility.showValidationAlertDialog(activity,"No contract signer for this contract")
                        newSignerCheck.isChecked=true
                        enable_contractSignerFeilds()
                    }
                }
                if (!newSignerCheck.isChecked){
                    newSignerCheck.isChecked=false
                    disablecontractSignerFeilds()
                }
            }
//            edit_newSignerCheck.setOnCheckedChangeListener { buttonView, isChecked ->
//                if (edit_newSignerCheck.isChecked) {
//                    if (alreadyContractSignerFound) {
//                        edit_newSignerCheck.isChecked=false
//                        disablecontractSignerFeilds()
//                        Utility.showValidationAlertDialog(activity,"There is already a contract signer for this contract")
//                    }else
//                    {
//                        Utility.showValidationAlertDialog(activity,"No contract signer for this contract")
//                        edit_newSignerCheck.isChecked=true
//                        enable_contractSignerFeilds()
//                    }
//                }
//                if (!edit_newSignerCheck.isChecked){
//                    edit_newSignerCheck.isChecked=false
//                    disablecontractSignerFeilds()
//                }
//            }
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
                        ( itemOrgAr[i].endDate.isNullOrBlank()&&!itemAr[i].endDate.isNullOrBlank()) ||
                        ( itemOrgAr[i].startDate.isNullOrBlank()&&!itemAr[i].startDate.isNullOrBlank() )||
                        (  itemOrgAr[i].SeniorityDate.isNullOrBlank()&&!itemAr[i].SeniorityDate.isNullOrBlank())
                ) {

                    MarkChangeWasDone()
                }
                else
                    if (
                            ( itemOrgAr[i].endDate.isNullOrBlank()&&itemAr[i].endDate.isNullOrBlank()) ||
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
                                dateFormat1.parse(itemAr[i].endDate.apiToAppFormat()) != dateFormat1.parse(itemOrgAr[i].endDate.apiToAppFormat()) ||
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
                            Log.v("checkkk", itemOrgAr[i].endDate + "=="+ itemAr[i].endDate)
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
        validationMsg = ""

           var cert = TblPersonnel()

        cert.iscertInputValid=true

        if (newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            cert.iscertInputValid = false
            certDateTextView.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }
        if (!newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE") && newCertEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
            cert.iscertInputValid = false
            expirationDateText.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }




        if (newCertTypeSpinner.selectedItem.toString().contains("Not")){
            cert.iscertInputValid=false
            certTypeTextView.setError("required field")
            validationMsg = "Please fill all required fields"
        }
        var certificateType = ""
        for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
            if (newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
                certificateType = fac.PersonnelCertID
        }

        var datesOverlapping = false
        var certTypeExists = false
        FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.CertificationTypeId.equals(certificateType)}.apply {
            (0 until size).forEach {
                if (get(it).PersonnelID.equals(selectedPersonnelID)) {
                    if (Utility.datesAreOverlapping(newCertStartDateBtn.text.toString().toDateMMDDYYYY(), newCertEndDateBtn.text.toString().toDateMMDDYYYY(), get(it).CertificationDate.toDateDBFormat(), get(it).ExpirationDate.toDateDBFormat())) {
//                        datesOverlapping = true
                    } else  {
                        certTypeExists = true
                    }
                }
            }
        }
        if (datesOverlapping) {
            Utility.showValidationAlertDialog(activity,"The certification overlaps with another active certification from the same type")
            cert.iscertInputValid = !datesOverlapping
        }

        if (certTypeExists) {
            Utility.showValidationAlertDialog(activity,"This Personnel has a certification from the same type")
            cert.iscertInputValid = !certTypeExists
        }

        if(!newCertEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(newCertStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(newCertEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                cert.iscertInputValid = false
                newCertEndDateBtn.setError("Should be after Effective Date")
                if (validationMsg.equals(""))
                    validationMsg = "Expiration Date should be after Start Date"
                else
                    validationMsg += "\nExpiration Date should be after Start Date"
            }
        }

        return cert.iscertInputValid
    }

    fun edit_validateCertificationInputs() : Boolean{

        edit_certDateTextView.setError(null)
        edit_expirationDateText.setError(null)
//        certTypeTextView.setError(null)
        validationMsg = ""
        var cert = TblPersonnel()

        cert.iscertInputValid=true

        if (edit_newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            cert.iscertInputValid = false
            edit_newCertStartDateBtn.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }
        if (!edit_newCertStartDateBtn.text.toString().toUpperCase().equals("SELECT DATE") && edit_newCertEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")){
            cert.iscertInputValid = false
            edit_expirationDateText.setError("Required Field")
            validationMsg = "Please fill all required fields"
        }



        var certificateType = ""
//        for (fac in TypeTablesModel.getInstance().PersonnelCertificationType) {
//            if (edit_newCertTypeSpinner.getSelectedItem().toString().equals(fac.PersonnelCertName))
//                certificateType = fac.PersonnelCertID
//        }
        certificateType = edit_newCertTypeSpinner.text.toString()

        var datesOverlapping = false
        if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.CertificationTypeId.equals(certificateType)}.size==1) {
            datesOverlapping = true
        } else {
            FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.CertificationTypeId.equals(certificateType) }.apply {
                (0 until size).forEach {
                    if (get(it).PersonnelID.equals(selectedPersonnelID)) {
                        if (Utility.datesAreOverlapping(edit_newCertStartDateBtn.text.toString().toDateMMDDYYYY(), edit_newCertEndDateBtn.text.toString().toDateMMDDYYYY(), get(it).CertificationDate.toDateDBFormat(), get(it).ExpirationDate.toDateDBFormat())) {
                            datesOverlapping = true
                        }
                    }
                }
            }
        }

        if (datesOverlapping) {
            Utility.showValidationAlertDialog(activity,"The certification overlaps with another active certification from the same type")
            cert.iscertInputValid = !datesOverlapping
        }

        if(!edit_newCertEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_newCertStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_newCertEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                cert.iscertInputValid = false
                edit_newCertEndDateBtn.setError("Should be after Effective Date")
                if (validationMsg.equals(""))
                    validationMsg = "Expiration Date should be after Start Date"
                else
                    validationMsg += "\nExpiration Date should be after Start Date"
            }
        }

        return cert.iscertInputValid
    }

    fun validateInputs() : Boolean{

        var persn = TblPersonnel()

        validationMsg = ""
        persn.personnelIsInputsValid=true

        if (newFirstNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            newFirstNameText.setError("required field")
            validationMsg = "Please fill all required fields"
        }
        else
            newFirstNameText.setError(null)

        if (!newCertNoText.text.toString().isNullOrEmpty()){
            if (FacilityDataModel.getInstance().tblPersonnel.filter{ s->s.CertificationNum==newCertNoText.text.toString()}.isNotEmpty()) {
                persn.personnelIsInputsValid=false
                newCertNoText.setError("Duplicated Cert ID")
                validationMsg = "Please fill all required fields"
            }
        }
        else
            newCertNoText.setError(null)

//        if (newCertNoText.text.toString().isNullOrEmpty()){
//            persn.personnelIsInputsValid=false
//            newCertNoText.setError("required field")
//        }
//        else
//            newCertNoText.setError(null)

        if (newLastNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            newLastNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        }
        else
            newLastNameText.setError(null)


        if (newPersonnelTypeSpinner.selectedItem.toString().contains("Selected")){
            persn.personnelIsInputsValid=false
            personnelTypeTextViewId.setError("required field")
            validationMsg = "Please fill all required fields"
        }
        else
            personnelTypeTextViewId.setError(null)

        if (newStartDateBtn.text.toString().contains("SELECT")){
            persn.personnelIsInputsValid=false
            newStartDateBtn.setError("required field")
            validationMsg = "Please fill all required fields"
        }  else
            newStartDateBtn.setError(null)




        if (newSignerCheck.isChecked){

                if (newAdd1Text.text.toString().isNullOrEmpty()){
                    persn.personnelIsInputsValid=false
                    newAdd1Text.setError("required field")
                    validationMsg = "Please fill all required fields"
                }
                else
                    newAdd1Text.setError(null)

                if (newCityText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    newCityText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    newCityText.setError(null)

                if (newStateSpinner.selectedItem.toString().contains("Select")){

                    persn.personnelIsInputsValid=false
                    stateTextView.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    stateTextView.setError(null)


                if (newZipText.text.toString().isNullOrEmpty()||zipFormat==false){

                    persn.personnelIsInputsValid=false
                    newZipText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    newZipText.setError(null)


                if (newPhoneText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    newPhoneText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    newPhoneText.setError(null)


                if (newCoStartDateBtn.text.toString().contains("SELECT")){

                    persn.personnelIsInputsValid=false
                    newCoStartDateBtn.setError("required field")
                    validationMsg = "Please fill all required fields"

                }  else
                    newCoStartDateBtn.setError(null)




                if (newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(newEmailText.text.toString())){

                    persn.personnelIsInputsValid=false
                    newEmailText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }  else
                    newEmailText.setError(null)



            }
        else {
                newEmailText.setError(null)
                newCoStartDateBtn.setError(null)
                newPhoneText.setError(null)
                newZipText.setError(null)
                stateTextView.setError(null)
                newCityText.setError(null)
                newAdd1Text.setError(null)


            }

        if(!newEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(newStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(newEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                persn.personnelIsInputsValid = false
                if (validationMsg.equals(""))
                    validationMsg = "End Date should be after Start Date"
                else
                    validationMsg += "\nEnd Date should be after Start Date"
                newEndDateBtn.setError("Should be after Start Date")

//                \n" +
//                "End Date should be after Start Date"
            }
        }


        return persn.personnelIsInputsValid
    }
    fun edit_validateInputs() : Boolean{

        var persn = TblPersonnel()
        validationMsg = ""

        persn.personnelIsInputsValid=true

        if (edit_newFirstNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            edit_newFirstNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        }
        else
            edit_newFirstNameText.setError(null)


        if (edit_newLastNameText.text.toString().isNullOrEmpty()){

            persn.personnelIsInputsValid=false
            edit_newLastNameText.setError("required field")
            validationMsg = "Please fill all required fields"

        }
        else
            edit_newLastNameText.setError(null)


        if (edit_newPersonnelTypeSpinner.selectedItem.toString().contains("Selected")){
            persn.personnelIsInputsValid=false
            edit_personnelTypeTextViewId.setError("required field")
            validationMsg = "Please fill all required fields"
        }
        else
            edit_personnelTypeTextViewId.setError(null)

        if (edit_newStartDateBtn.text.toString().contains("SELECT")){
            persn.personnelIsInputsValid=false
            edit_newStartDateBtn.setError("required field")
            validationMsg = "Please fill all required fields"
        }  else
            edit_newStartDateBtn.setError(null)



        if (edit_newSignerCheck.isChecked){

                if (edit_newAdd1Text.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newAdd1Text.setError("required field")
                    validationMsg = "Please fill all required fields"
                }
                else
                    edit_newAdd1Text.setError(null)

                if (edit_newCityText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newCityText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    edit_newCityText.setError(null)

                if (edit_newStateSpinner.selectedItem.toString().contains("Select")){
                    persn.personnelIsInputsValid=false
                    edit_stateTextView.setError("required field")
                    validationMsg = "Please fill all required fields"
                }
                else
                    edit_stateTextView.setError(null)


                if (edit_newZipText.text.toString().isNullOrEmpty()||zipFormat==false){

                    persn.personnelIsInputsValid=false
                    edit_newZipText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    edit_newZipText.setError(null)


                if (edit_newPhoneText.text.toString().isNullOrEmpty()){

                    persn.personnelIsInputsValid=false
                    edit_newPhoneText.setError("required field")
                    validationMsg = "Please fill all required fields"

                }
                else
                    edit_newPhoneText.setError(null)


                if (edit_newCoStartDateBtn.text.toString().contains("SELECT")){

                    persn.personnelIsInputsValid=false
                    edit_newCoStartDateBtn.setError("required field")

                    validationMsg = "Please fill all required fields"
                }  else
                    edit_newCoStartDateBtn.setError(null)




                if (edit_newEmailText.text.toString().isNullOrEmpty()||!emailFormatValidation(edit_newEmailText.text.toString())){

                    persn.personnelIsInputsValid=false
                    edit_newEmailText.setError("required field")
                    validationMsg = "Please fill all required fields"

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

        if(!edit_newEndDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_newStartDateBtn!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_newEndDateBtn!!.text.toString())
            if (expDate.before(effDate)) {
                persn.personnelIsInputsValid = false
                edit_newEndDateBtn.setError("Should be after Start Date")
                if (validationMsg.equals(""))
                    validationMsg = "End Date should be after Start Date"
                else
                    validationMsg += "\nEnd Date should be after Start Date"
            }
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
