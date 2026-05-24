package com.inspection.fragments

//import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
//
//import kotlinx.android.synthetic.main.fragment_arrav_programs.*
//
//import kotlinx.android.synthetic.main.scope_of_service_group_layout.*

import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateProgramsData
import com.inspection.databinding.FragmentArravProgramsBinding
import com.inspection.model.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVPrograms.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVPrograms.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPrograms : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var programTypesArray = ArrayList<String>()
    private var programTypesList = ArrayList<AAAProgramTypes>()
    private var facilityProgramsList = ArrayList<AAAFacilityPrograms>()

    var dateOne = ""
    var dateTwo = ""
    private var validationMsg=""
    private var _binding: FragmentArravProgramsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_arrav_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArravProgramsBinding.bind(view)
        IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited = true
        // SAEED TO BE REVIEWED
        (requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as? HasTabIndicators)?.refreshTabIndicators()
//        (activity as FormsActivity).programsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        binding.exitProgramDialogeBtnId.setOnClickListener({
            binding.programCard.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
            binding.alphaBackgroundForProgramDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()

        })
        binding.editExitProgramDialogeBtnId.setOnClickListener({
            binding.editProgramCard.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
            binding.alphaBackgroundForProgramDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })

        binding.showNewProgramDialogueButton.setOnClickListener(View.OnClickListener {
            disableAllAddButnsAndDialog()
            binding.commentsEditTextVal.setText("")
            binding.effectiveDateTextviewVal.setText("SELECT DATE")
            binding.expirationDateTextviewVal.setText("SELECT DATE")
            binding.programNameTextviewVal.setSelection(0)
            binding.commentsEditTextVal.setError(null)
            binding.effectiveDateTextviewVal.setError(null)
            binding.expirationDateTextviewVal.setError(null)
            binding.programCard.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
            binding.alphaBackgroundForProgramDialogs.visibility = View.VISIBLE
        })


        binding.effectiveDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.effectiveDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.effectiveDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val myFormat2 = "MM/dd/yyy" // mention the format you need
                    val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.effectiveDateTextviewVal!!.text = sdf.format(c.time)
                    dateOne = sdf2.format(c.time)

                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.editEffectiveDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editEffectiveDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editEffectiveDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val myFormat2 = "dd MM yyyy" // mention the format you need
                    val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editEffectiveDateTextviewVal!!.text = sdf.format(c.time)
                    dateOne = sdf2.format(c.time)

                },
                year,
                month,
                day
            )
            dpd.show()
        }


        binding.expirationDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.expirationDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.expirationDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val myFormat3 = "dd MM yyyy" // mention the format you need
                    val sdf3 = SimpleDateFormat(myFormat3, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.expirationDateTextviewVal!!.text = sdf.format(c.time)
                    dateTwo = sdf3.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.editExpirationDateTextviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!binding.editExpirationDateTextviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(binding.editExpirationDateTextviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                requireActivity(),
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val myFormat3 = "dd MM yyyy" // mention the format you need
                    val sdf3 = SimpleDateFormat(myFormat3, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    binding.editExpirationDateTextviewVal!!.text = sdf.format(c.time)
                    dateTwo = sdf3.format(c.time)
                },
                year,
                month,
                day
            )
            dpd.show()
        }




        binding.submitNewProgramButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()) {
                    var validProgram = true
                    var valid_validProgram = false
                    for (fac in TypeTablesModel.getInstance().ProgramsType) {
                        if (binding.programNameTextviewVal.getSelectedItem().toString()
                                .equals(fac.ProgramTypeName)
                        ) {
                            for (item1 in FacilityDataModel.getInstance().tblPrograms)
                                if (item1.ProgramTypeID.toString()
                                        .equals(fac.ProgramTypeID.toString())
                                ) {
                                    val dateFormat = SimpleDateFormat("MM/dd/yyyy")
                                    var newEffDate = Date()
                                    var newExpDate = dateFormat.parse("01/01/2500")
                                    var DB_EffDate = Date()
                                    var DB_ExpDate = dateFormat.parse("01/01/2500")
                                    try {
                                        //
                                        newEffDate =
                                            dateFormat.parse(binding.effectiveDateTextviewVal!!.text.toString())
                                        if (binding.expirationDateTextviewVal!!.text.toString()
                                                .isNullOrEmpty() || binding.expirationDateTextviewVal!!.text.toString()
                                                .equals("SELECT DATE")
                                        )
                                        else
                                            newExpDate =
                                                dateFormat.parse(binding.expirationDateTextviewVal!!.text.toString())
                                        DB_EffDate =
                                            dateFormat.parse(item1.effDate.apiToAppFormatMMDDYYYY())
                                        if (item1.expDate.apiToAppFormatMMDDYYYY()
                                                .isNullOrEmpty() || item1.expDate.apiToAppFormatMMDDYYYY()
                                                .equals("01/01/1900")
                                        )
                                        else
                                            DB_ExpDate =
                                                dateFormat.parse(item1.expDate.apiToAppFormatMMDDYYYY())
                                    } catch (e: ParseException) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace()
                                    }

                                    //                                if (!item1.expDate.isNullOrEmpty() || !item1.expDate.isNullOrBlank()) {
                                    //                                if (item1.expDate.isNullOrEmpty() || item1.expDate.equals("01/01/1900")) {
                                    // Option 1 .. Old program has no expiry date
                                    if (DB_ExpDate == dateFormat.parse("01/01/2500")) {
                                        // Option 1.1 .. New Program b4 old program and expiry b4 old program as well
                                        if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                "01/01/2500"
                                            ) && newExpDate < DB_EffDate
                                        ) {
                                            validProgram = true
                                            valid_validProgram = true
                                            // Option 1.2 .. New Program b4 old program and expiry after start of old program
                                        } else if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                "01/01/2500"
                                            ) && newExpDate >= DB_EffDate
                                        ) {
                                            Utility.showValidationAlertDialog(
                                                activity,
                                                "This program is already active within the same dates"
                                            )
                                            validProgram = false
                                            valid_validProgram = false
                                            // Option 1.3 .. New Program b4 old program and no new expiry date
                                        } else if (newEffDate <= DB_EffDate && newExpDate == dateFormat.parse(
                                                "01/01/2500"
                                            )
                                        ) {
                                            Utility.showValidationAlertDialog(
                                                activity,
                                                "This program is already active within the same dates"
                                            )
                                            validProgram = false
                                            valid_validProgram = false
                                            // Option 1.3 .. New Program after old program start date while old program has no expiry
                                        } else if (newEffDate > DB_EffDate) {
                                            Utility.showValidationAlertDialog(
                                                activity,
                                                "This program is already active within the same dates"
                                            )
                                            validProgram = false
                                            valid_validProgram = false
                                        }
                                        // Option 2 Old Program has expiry date
                                    } else {
                                        // Option 2.1 .. New Program b4 old program and expiry b4 old program as well
                                        if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                "01/01/2500"
                                            ) && newExpDate < DB_EffDate
                                        ) {
                                            validProgram = true
                                            valid_validProgram = true
                                            // Option 2.2 .. New Program b4 old program and expiry after start of old program
                                        } else if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                "01/01/2500"
                                            ) && newExpDate >= DB_EffDate
                                        ) {
                                            Utility.showValidationAlertDialog(
                                                activity,
                                                "This program is already active within the same dates"
                                            )
                                            validProgram = false
                                            valid_validProgram = false
                                        } else if (newEffDate > DB_EffDate && newEffDate < DB_ExpDate) {
                                            Utility.showValidationAlertDialog(
                                                activity,
                                                "This program is already active within the same dates"
                                            )
                                            validProgram = false
                                            valid_validProgram = false
                                        } else if (newEffDate > DB_ExpDate) {
                                            validProgram = true
                                            valid_validProgram = true
                                        }
                                    }

                                    //                                    if ((DB_ExpDate <= newEffDate) && (newExpDate == Date() || newExpDate >= DB_EffDate) ) {
                                    //                                        validProgram = true
                                    //                                        valid_validProgram=true
                                    //                                    } else
                                    //                                        Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                    //                                    validProgram = false
                                    //                                } else
                                    //                                    Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                    //                                validProgram = false

                                }
                        }


                    }
                    if (validProgram || valid_validProgram) {
                        binding.progressBarTextVal.text = "Saving ..."
                        binding.programsLoadingView.visibility = View.VISIBLE

                        var item = TblPrograms()
                        for (fac in TypeTablesModel.getInstance().ProgramsType) {
                            if (binding.programNameTextviewVal.getSelectedItem().toString()
                                    .equals(fac.ProgramTypeName)
                            )

                                item.ProgramTypeID = fac.ProgramTypeID
                        }
                        var RSPProgramName = ""
                        if (binding.programNameTextviewVal.getSelectedItem()
                                .equals("Appointment Scheduling")
                        )
                            RSPProgramName = "prgAppointments"
                        else if (binding.programNameTextviewVal.getSelectedItem()
                                .equals("Priority Service")
                        )
                            RSPProgramName = "shopStatus"
                        else if (binding.programNameTextviewVal.getSelectedItem()
                                .equals("Repair Shop Portal")
                        )
                            RSPProgramName = "rsp"
                        else if (binding.programNameTextviewVal.getSelectedItem().toString()
                                .contains("AAA Batteries")
                        )
                            RSPProgramName = "battery"
                        val office = FacilityDataModel.getInstance().tblOfficeType[0].OfficeName
                        item.effDate =
                            if (binding.effectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.effectiveDateTextviewVal.text.toString()
                                .appToApiSubmitFormatMMDDYYYY()
                        item.expDate =
                            if (binding.expirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.expirationDateTextviewVal.text.toString()
                                .appToApiSubmitFormatMMDDYYYY()
                        item.Comments = binding.commentsEditTextVal.text.toString()
                        //                    URLEncoder.encode(item.Comments, "UTF-8")
                        Log.v(
                            "PROGRAMS ADD --- ",
                            UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=&programTypeId=${item.ProgramTypeID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${
                                ApplicationPrefs.getInstance(activity).loggedInUserID
                            }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                ApplicationPrefs.getInstance(
                                    activity
                                ).loggedInUserID
                            }&RSPProgramName=$RSPProgramName&office=$office&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(
                                activity,
                                0,
                                getProgramChanges(0, 0)
                            )
                        )
                        Volley.newRequestQueue(context).add(
                            StringRequest(Request.Method.GET,
                                UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=&programTypeId=${item.ProgramTypeID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${
                                    ApplicationPrefs.getInstance(activity).loggedInUserID
                                }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                    ApplicationPrefs.getInstance(
                                        activity
                                    ).loggedInUserID
                                }&RSPProgramName=$RSPProgramName&office=$office&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(
                                    activity,
                                    0,
                                    getProgramChanges(0, 0)
                                ),
                                { response ->
                                    requireActivity().runOnUiThread {
                                        Log.v("PROGRAMS ADD RES --- ", response.toString())
                                        if (response.toString().contains("returnCode>0<", false)) {
                                            HasChangedModel.getInstance().updateChangedData("Programs","","",getProgramChanges(0, 0))
                                            binding.alphaBackgroundForProgramDialogs.visibility =
                                                View.GONE
                                            (activity as FormsActivity).overrideBackButton = false
                                            // collect program id
//                                            Utility.showSubmitAlertDialog(activity,true,"Program")
                                            var RSPMsg = ""
                                            if (response.toString()
                                                    .contains("UpdateRSPProgram:[Success]")
                                            )
                                                RSPMsg =
                                                    "\n\nProgram changes were successfully updated in RSP"
                                            else if (response.toString()
                                                    .contains("UpdateRSPProgram:[Failed") || response.toString()
                                                    .contains("RSPToken:[Failed:")
                                            )
                                                RSPMsg = if (response.toString()
                                                        .contains("RSPToken:[Failed:")
                                                )
                                                    "\n\nProgram changes could not be updated in RSP due to issue while getting RSP Token"
                                                else
                                                    "\n\nProgram changes could not be updated in RSP due to issue while updating RSP Program"
                                            Utility.showUnifiedConfirmationDialog(
                                                activity,
                                                "Program Data Saved Successfully$RSPMsg"
                                            )
                                            item.ProgramID = response.toString().substring(
                                                response.toString().indexOf("<programID") + 11,
                                                response.toString().indexOf("</programID")
                                            )
                                            FacilityDataModel.getInstance().tblPrograms.add(item)
                                            FacilityDataModelOrg.getInstance().tblPrograms.add(item)
//                                        Utility.showMessageDialog(activity,"Program ID",item.ProgramID)
                                            HasChangedModel.getInstance().groupSoSPrograms[0].SoSPrograms =
                                                true
                                            HasChangedModel.getInstance()
                                                .checkIfChangeWasDoneforSoSPrograms()
                                            fillPortalTrackingTableView()
                                            altTableRow(2)
                                            (activity as FormsActivity).saveDone = true
                                            binding.programCard.visibility = View.GONE
                                            binding.progressBarTextVal.text = "Loading ..."
                                            binding.programsLoadingView.visibility = View.GONE
                                        } else {
                                            binding.progressBarTextVal.text = "Loading ..."
                                            binding.programsLoadingView.visibility = View.GONE
                                            var errorMessage = response.toString().substring(
                                                response.toString().indexOf("<message") + 9,
                                                response.toString().indexOf("</message")
                                            )
                                            Utility.showSubmitAlertDialog(
                                                activity,
                                                false,
                                                "Program (Error: " + errorMessage + " )"
                                            )

                                        }
                                        enableAllAddButnsAndDialog()


                                    }
                                },
                                {
                                    Utility.showSubmitAlertDialog(
                                        activity,
                                        false,
                                        "Program (Error: " + it.message + " )"
                                    )
                                    enableAllAddButnsAndDialog()
                                    binding.progressBarTextVal.text = "Loading ..."
                                    binding.programsLoadingView.visibility = View.GONE
                                    binding.alphaBackgroundForProgramDialogs.visibility = View.GONE
                                })
                        ).setRetryPolicy(DefaultRetryPolicy(30000, 0, 1f))
                    }
                } else {
                    //                Utility.showValidationAlertDialog(activity,"Please fill the required fields")
                    Utility.showValidationAlertDialog(
                        activity,
                        "Please fill all required fields" + validationMsg
                    )
                }
            } else {
                Utility.showInternetWarningDialog(
                    requireContext(),
                    (requireActivity() as FormsActivity).networkStatusErrorMsg
                )
            }
        }
        prepareProgramTypes()
        fillPortalTrackingTableView();
        altTableRow(2)
    }

    fun getProgramChanges(action: Int, rowId: Int): String { // 0: Add 1: Edit
        var strChanges = ""
        try {
            if (action == 0) {
                strChanges = "Program added with "
                strChanges += "Type (" + binding.programNameTextviewVal.getSelectedItem()
                    .toString() + ") - "
                strChanges += "Effective Date (" + if (binding.effectiveDateTextviewVal.text.equals(
                        "SELECT DATE"
                    )
                ) "" else binding.effectiveDateTextviewVal.text.toString() + ") - "
                strChanges += "Expiration Date (" + if (binding.expirationDateTextviewVal.text.equals(
                        "SELECT DATE"
                    )
                ) "" else binding.expirationDateTextviewVal.text.toString() + ") - "
                strChanges += "Comments (" + binding.commentsEditTextVal.text.toString() + ")"
            }
            val Comments = binding.editCommentsEditTextVal.text.toString()
            val effDate =
                if (binding.editEffectiveDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editEffectiveDateTextviewVal.text.toString()
            val expDate =
                if (binding.editExpirationDateTextviewVal.text.equals("SELECT DATE")) "" else binding.editExpirationDateTextviewVal.text.toString()
            val programName = binding.editProgramNameTextviewVal.selectedItem.toString()
            if (action == 1) {
                strChanges = "Program: " + TypeTablesModel.getInstance().ProgramsType.filter { s ->
                    s.ProgramTypeID.equals(
                        FacilityDataModelOrg.getInstance().tblPrograms[rowId].ProgramTypeID
                    )
                }[0].ProgramTypeName + " "
                if (Comments != FacilityDataModelOrg.getInstance().tblPrograms[rowId].Comments) {
                    strChanges += "Program comments changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].Comments + ") to (${Comments}) - "
                }
                if (effDate != FacilityDataModelOrg.getInstance().tblPrograms[rowId].effDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "Effective Date changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].effDate.apiToAppFormatMMDDYYYY() + ") to (" + effDate + ") - "
                }
                if (expDate != FacilityDataModelOrg.getInstance().tblPrograms[rowId].expDate.apiToAppFormatMMDDYYYY()) {
                    strChanges += "Expiration Date changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].expDate.apiToAppFormatMMDDYYYY() + ") to (" + expDate + ") - "
                }
                if (programName != (TypeTablesModel.getInstance().ProgramsType.filter { s ->
                        s.ProgramTypeID.equals(
                            FacilityDataModelOrg.getInstance().tblPrograms[rowId].ProgramTypeID
                        )
                    }[0].ProgramTypeName)) {
                    strChanges += "Program Type changed to ($programName) - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }

    fun prepareProgramTypes() {

        for (fac in TypeTablesModel.getInstance().ProgramsType.filter { s -> s.active.equals("true") }
            .toCollection(ArrayList())) {
            if (fac.ProgramTypeName.equals("AAR Advantage")) {
//                programTypesArray.add(fac.ProgramTypeName)
            } else {
                programTypesArray.add(fac.ProgramTypeName)
            }
        }
        var programsAdapter = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            programTypesArray
        )
        programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.programNameTextviewVal.adapter = programsAdapter
        binding.editProgramNameTextviewVal.adapter = programsAdapter

    }

    fun fillPortalTrackingTableView() {

        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (binding.aarPortalTrackingTableLayout.childCount > 1) {
            for (i in binding.aarPortalTrackingTableLayout.childCount - 1 downTo 1) {
                binding.aarPortalTrackingTableLayout.removeViewAt(i)
            }

        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 0.7F
        rowLayoutParam1.column = 1
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.7F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1.5F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 0.6F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblPrograms.apply {
            (0 until size).forEach {
                if (!get(it).ProgramID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30
                    tableRow.weightSum = 4.5F

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.minimumHeight = 30
                    for (fac in TypeTablesModel.getInstance().ProgramsType) {
                        if (get(it).ProgramTypeID.equals(fac.ProgramTypeID)) {
                            textView1.text = fac.ProgramTypeName
                        }
                    }
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam1
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.minimumHeight = 30
                    if (get(it).effDate.isNullOrBlank()) {
                        textView2.text = ""
                    } else {
                        try {
                            textView2.text = get(it).effDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {
                            textView2.text = get(it).effDate
                        }
                    }
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam2
                    textView3.gravity = Gravity.CENTER_VERTICAL
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
                    textView3.minimumHeight = 30
                    TableRow.LayoutParams()
                    if (get(it).expDate.isNullOrBlank()) {
                        textView3.text = ""
                    } else {

                        try {
                            textView3.text = get(it).expDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {
                            textView3.text = get(it).expDate

                        }
                    }
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam3
                    textView4.gravity = Gravity.CENTER_VERTICAL
                    textView4.minimumHeight = 30
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    textView4.text = get(it).Comments
                    tableRow.addView(textView4)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam4
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 14f
                    updateButton.minimumHeight = 30
                    updateButton.tag = get(it).ProgramID
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)

                    updateButton.setOnClickListener {
                        if (textView1.text.equals("AAR Advantage")) {
//                            Utility.showMessageDialog(activity, "Information", "Specialist are not authorized to edit AAR Advantage Program")
                            Utility.showUnifiedInformationDialog(
                                activity,
                                "Specialist are not authorized to edit AAR Advantage Program"
                            )
                        } else {
                            var currentTableRowIndex =
                                binding.aarPortalTrackingTableLayout.indexOfChild(tableRow)
                            var currentfacilityDataModelIndex = currentTableRowIndex - 1


                            disableAllAddButnsAndDialog()
                            binding.editCommentsEditTextVal.setText(textView4.text)
                            binding.editEffectiveDateTextviewVal.setText(if (textView2.text.equals("")) "SELECT DATE" else textView2.text.toString())
                            binding.editExpirationDateTextviewVal.setText(
                                if (textView3.text.equals(
                                        ""
                                    )
                                ) "SELECT DATE" else textView3.text.toString()
                            )
                            var i = programTypesArray.indexOf(textView1.text)
                            binding.editProgramNameTextviewVal.setSelection(i)

                            binding.editProgramCard.visibility = View.VISIBLE
                            binding.alphaBackgroundForProgramDialogs.visibility = View.VISIBLE
                            (activity as FormsActivity).overrideBackButton = true

                            binding.editSubmitNewProgramButton.setOnClickListener {
                                if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                                    var currentRowDataModel =
                                        FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex]
                                    var originalDataModel =
                                        FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex]
                                    if (edit_validateInputs()) {
                                        var validProgram = true
                                        var valid_validProgram = false
                                        for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                            if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                    .toString().equals(fac.ProgramTypeName)
                                            ) {
                                                for (item1 in FacilityDataModel.getInstance().tblPrograms)
                                                    if (item1.ProgramTypeID.toString()
                                                            .equals(fac.ProgramTypeID.toString())
                                                    ) {
                                                        val selectemProgramName =
                                                            binding.editProgramNameTextviewVal.getSelectedItem()
                                                                .toString()
                                                        var numToCopare =
                                                            FacilityDataModel.getInstance().tblPrograms.indexOf(
                                                                item1
                                                            )
                                                        if (textView1.text.toString() == selectemProgramName && numToCopare == currentfacilityDataModelIndex) {
                                                            validProgram = true
                                                            valid_validProgram = true
                                                        } else {
                                                            //                                                    val dateFormat = SimpleDateFormat("dd MMM yyyy")
                                                            //                                                    var newEffDate = Date()
                                                            //                                                    var newExpDate = Date()
                                                            //                                                    var DB_EffDate = Date()
                                                            //                                                    var DB_ExpDate = Date()
                                                            //                                                    try {
                                                            //                                                        newEffDate = dateFormat.parse(edit_effective_date_textviewVal!!.text.toString())
                                                            //                                                        newExpDate = dateFormat.parse(c!!.text.toString())
                                                            //                                                        DB_EffDate = dateFormat.parse(item1.effDate.apiToAppFormat())
                                                            //                                                        DB_ExpDate = dateFormat.parse(item1.expDate.apiToAppFormat())
                                                            //                                                    } catch (e: ParseException) {
                                                            //                                                        // TODO Auto-generated catch block
                                                            //                                                        e.printStackTrace()
                                                            //                                                    }
                                                            //                                                    if (!item1.expDate.isNullOrEmpty() || !item1.expDate.isNullOrBlank()) {
                                                            //                                                        if ((DB_ExpDate <= newEffDate) && (newExpDate >= DB_EffDate)) {
                                                            //                                                            validProgram = true
                                                            //                                                            valid_validProgram = true
                                                            //                                                        } else
                                                            //                                                        //                                                            Toast.makeText(context, "1st this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()
                                                            //                                                            Utility.showValidationAlertDialog(activity, "This program is already active within the same dates")
                                                            //                                                        validProgram = false
                                                            //                                                    } else
                                                            //                                                    //                                                        Toast.makeText(context, "2nd this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()
                                                            //                                                        Utility.showValidationAlertDialog(activity, "This program is already active within the same dates")
                                                            //                                                    validProgram = false


                                                            val dateFormat =
                                                                SimpleDateFormat("MM/dd/yyyy")
                                                            var newEffDate = Date()
                                                            var newExpDate =
                                                                dateFormat.parse("01/01/2500")
                                                            var DB_EffDate = Date()
                                                            var DB_ExpDate =
                                                                dateFormat.parse("01/01/2500")
                                                            try {
                                                                //
                                                                newEffDate = dateFormat.parse(
                                                                    binding.editEffectiveDateTextviewVal!!.text.toString()
                                                                )
                                                                if (binding.editExpirationDateTextviewVal!!.text.toString()
                                                                        .isNullOrEmpty() || binding.editExpirationDateTextviewVal!!.text.toString()
                                                                        .equals("SELECT DATE")
                                                                )
                                                                else
                                                                    newExpDate = dateFormat.parse(
                                                                        binding.editExpirationDateTextviewVal!!.text.toString()
                                                                    )
                                                                DB_EffDate =
                                                                    dateFormat.parse(item1.effDate.apiToAppFormatMMDDYYYY())
                                                                if (item1.expDate.apiToAppFormatMMDDYYYY()
                                                                        .isNullOrEmpty() || item1.expDate.apiToAppFormatMMDDYYYY()
                                                                        .equals("01/01/1900")
                                                                )
                                                                else
                                                                    DB_ExpDate =
                                                                        dateFormat.parse(item1.expDate.apiToAppFormatMMDDYYYY())
                                                            } catch (e: ParseException) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace()
                                                            }

                                                            if (DB_ExpDate == dateFormat.parse("01/01/2500")) {
                                                                // Option 1.1 .. New Program b4 old program and expiry b4 old program as well
                                                                if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                                        "01/01/2500"
                                                                    ) && newExpDate < DB_EffDate
                                                                ) {
                                                                    validProgram = true
                                                                    valid_validProgram = true
                                                                    // Option 1.2 .. New Program b4 old program and expiry after start of old program
                                                                } else if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                                        "01/01/2500"
                                                                    ) && newExpDate >= DB_EffDate
                                                                ) {
                                                                    Utility.showValidationAlertDialog(
                                                                        activity,
                                                                        "This program is already active within the same dates"
                                                                    )
                                                                    validProgram = false
                                                                    valid_validProgram = false
                                                                } else if (newEffDate <= DB_EffDate && newExpDate == dateFormat.parse(
                                                                        "01/01/2500"
                                                                    )
                                                                ) {
                                                                    Utility.showValidationAlertDialog(
                                                                        activity,
                                                                        "This program is already active within the same dates"
                                                                    )
                                                                    validProgram = false
                                                                    valid_validProgram = false
                                                                    // Option 1.3 .. New Program after old program start date while old program has no expiry
                                                                } else if (newEffDate > DB_EffDate) {
                                                                    Utility.showValidationAlertDialog(
                                                                        activity,
                                                                        "This program is already active within the same dates"
                                                                    )
                                                                    validProgram = false
                                                                    valid_validProgram = false
                                                                }
                                                                // Option 2 Old Program has expiry date
                                                            } else {
                                                                // Option 2.1 .. New Program b4 old program and expiry b4 old program as well
                                                                if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                                        "01/01/2500"
                                                                    ) && newExpDate < DB_EffDate
                                                                ) {
                                                                    validProgram = true
                                                                    valid_validProgram = true
                                                                    // Option 2.2 .. New Program b4 old program and expiry after start of old program
                                                                } else if (newEffDate <= DB_EffDate && newExpDate != dateFormat.parse(
                                                                        "01/01/2500"
                                                                    ) && newExpDate >= DB_EffDate
                                                                ) {
                                                                    Utility.showValidationAlertDialog(
                                                                        activity,
                                                                        "This program is already active within the same dates"
                                                                    )
                                                                    validProgram = false
                                                                    valid_validProgram = false
                                                                } else if (newEffDate > DB_EffDate && newEffDate < DB_ExpDate) {
                                                                    Utility.showValidationAlertDialog(
                                                                        activity,
                                                                        "This program is already active within the same dates"
                                                                    )
                                                                    validProgram = false
                                                                    valid_validProgram = false
                                                                } else if (newEffDate > DB_ExpDate) {
                                                                    validProgram = true
                                                                    valid_validProgram = true
                                                                }
                                                            }
                                                        }
                                                    }
                                            }
                                            ///////////////////////


                                            /////////////////////

                                        }


                                        if (validProgram || valid_validProgram) {


                                            binding.editProgramsLoadingView.visibility =
                                                View.VISIBLE

                                            currentRowDataModel.Comments =
                                                binding.editCommentsEditTextVal.text.toString()

                                            currentRowDataModel.effDate =
                                                if (binding.editEffectiveDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) "" else binding.editEffectiveDateTextviewVal.text.toString()
                                                    .appToApiSubmitFormatMMDDYYYY()
                                            currentRowDataModel.expDate =
                                                if (binding.editExpirationDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) "" else binding.editExpirationDateTextviewVal.text.toString()
                                                    .appToApiSubmitFormatMMDDYYYY()


                                            var effdateForSubmit =
                                                if (binding.editEffectiveDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) "" else binding.editEffectiveDateTextviewVal.text.toString()
                                                    .appToApiSubmitFormatMMDDYYYY()
                                            var expdateForSubmit =
                                                if (binding.editExpirationDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) "" else binding.editExpirationDateTextviewVal.text.toString()
                                                    .appToApiSubmitFormatMMDDYYYY()
                                            for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                                if (binding.editProgramNameTextviewVal.selectedItem.toString()
                                                        .equals(fac.ProgramTypeName)
                                                ) {
                                                    currentRowDataModel.ProgramTypeID =
                                                        fac.ProgramTypeID
                                                }
                                            }
                                            var interstateBatteryID = TypeTablesModel.getInstance().ProgramsType.filter { s ->
                                                s.ProgramTypeName.contains(
                                                    "AAA Batteries"
                                                ) &&
                                                s.ProgramTypeName.contains(
                                                    "Interstate"
                                                )
                                            }[0].ProgramTypeID
                                            var napaBatteryID = TypeTablesModel.getInstance().ProgramsType.filter { s ->
                                                s.ProgramTypeName.contains(
                                                    "AAA Batteries"
                                                ) &&
                                                        s.ProgramTypeName.contains(
                                                            "NAPA"
                                                        )
                                            }[0].ProgramTypeID
                                            var RSPProgramName = ""
                                            if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                    .equals("Appointment Scheduling")
                                            )
                                                RSPProgramName = "prgAppointments"
                                            else if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                    .equals("Priority Service")
                                            )
                                                RSPProgramName = "shopStatus"
                                            else if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                    .equals("Repair Shop Portal")
                                            )
                                                RSPProgramName = "rsp"
                                            else if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                    .toString().contains("AAA Batteries")
                                            ) {
                                                if (binding.editExpirationDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) { // No expiry date added before this action


                                                } else if (!binding.editExpirationDateTextviewVal.text.equals(
                                                        "SELECT DATE"
                                                    )
                                                ) {
                                                    if (FacilityDataModel.getInstance().tblPrograms.filter { s ->
                                                            s.ProgramTypeID==interstateBatteryID || s.ProgramTypeID==napaBatteryID
                                                        }.size == 1) { // 1) No other AAA Batteries program exists
                                                        RSPProgramName = "battery"
                                                    } else { //2) Another AAA Batteries program exists
                                                        if (binding.editProgramNameTextviewVal.getSelectedItem()
                                                                .toString()
                                                                .contains("AAA Batteries") && binding.editProgramNameTextviewVal.getSelectedItem()
                                                                .toString()
                                                                .contains("Interstate")
                                                        ) {
                                                            RSPProgramName = "battery"
                                                        } else if (FacilityDataModel.getInstance().tblPrograms.filter { s ->
                                                                s.ProgramTypeID==interstateBatteryID && s.expDate.isNullOrBlank()
                                                            }.isNotEmpty()) { // 2.1) Another AAA Batteries program exists but not the same as being added now
                                                            RSPProgramName = ""
                                                        } else {
                                                            RSPProgramName = "battery"
                                                        }
                                                    }
                                                } else {
                                                    RSPProgramName = "battery"
                                                }
                                            }
                                            val office =
                                                FacilityDataModel.getInstance().tblOfficeType[0].OfficeName

                                            Log.v(
                                                "PROGRAMS EDIT --- ",
                                                UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=${currentRowDataModel.ProgramID}&programTypeId=${currentRowDataModel.ProgramTypeID}&effDate=$effdateForSubmit&expDate=$expdateForSubmit&comments=${currentRowDataModel.Comments}&active=1&insertBy=${
                                                    ApplicationPrefs.getInstance(activity).loggedInUserID
                                                }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                                    ApplicationPrefs.getInstance(
                                                        activity
                                                    ).loggedInUserID
                                                }&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(
                                                    activity,
                                                    1,
                                                    getProgramChanges(
                                                        1,
                                                        currentfacilityDataModelIndex
                                                    )
                                                ) + "&RSPProgramName=$RSPProgramName&office=$office"
                                            )
                                            Volley.newRequestQueue(context).add(
                                                StringRequest(Request.Method.GET,
                                                    UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=${currentRowDataModel.ProgramID}&programTypeId=${currentRowDataModel.ProgramTypeID}&effDate=$effdateForSubmit&expDate=$expdateForSubmit&comments=${currentRowDataModel.Comments}&active=1&insertBy=${
                                                        ApplicationPrefs.getInstance(activity).loggedInUserID
                                                    }&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${
                                                        ApplicationPrefs.getInstance(
                                                            activity
                                                        ).loggedInUserID
                                                    }&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(
                                                        activity,
                                                        1,
                                                        getProgramChanges(
                                                            1,
                                                            currentfacilityDataModelIndex
                                                        ) + "&RSPProgramName=$RSPProgramName&office=$office"
                                                    ),
                                                    { response ->
                                                        requireActivity().runOnUiThread {
                                                            Log.v(
                                                                "PROGRAMS EDIT RES --- ",
                                                                response.toString()
                                                            )
                                                            if (response.toString()
                                                                    .contains(
                                                                        "returnCode>0<",
                                                                        false
                                                                    )
                                                            ) {
//                                                                Utility.showSubmitAlertDialog(
//                                                                    activity,
//                                                                    true,
//                                                                    "Program"
//                                                                )
                                                                var RSPMsg = ""
                                                                if (response.toString()
                                                                        .contains("UpdateRSPProgram:[Success]")
                                                                )
                                                                    RSPMsg =
                                                                        "\n\nProgram changes were successfully updated in RSP"
                                                                else if (response.toString()
                                                                        .contains("UpdateRSPProgram:[Failed") || response.toString()
                                                                        .contains("RSPToken:[Failed:")
                                                                )
                                                                    RSPMsg = if (response.toString()
                                                                            .contains("RSPToken:[Failed:")
                                                                    )
                                                                        "\n\nProgram changes could not be updated in RSP due to issue while getting RSP Token"
                                                                    else
                                                                        "\n\nProgram changes could not be updated in RSP due to issue while updating RSP Program"
                                                                Utility.showUnifiedConfirmationDialog(
                                                                    activity,
                                                                    "Program Data Saved Successfully$RSPMsg"
                                                                )
                                                                HasChangedModel.getInstance().updateChangedData("Programs","","",getProgramChanges(1, currentfacilityDataModelIndex))
                                                                HasChangedModel.getInstance().groupSoSPrograms[0].SoSPrograms =
                                                                    true
                                                                HasChangedModel.getInstance()
                                                                    .checkIfChangeWasDoneforSoSPrograms()
                                                                FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].Comments =
                                                                    currentRowDataModel.Comments
                                                                FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].expDate =
                                                                    currentRowDataModel.expDate
                                                                FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].effDate =
                                                                    currentRowDataModel.effDate
                                                                FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].ProgramTypeID =
                                                                    currentRowDataModel.ProgramTypeID
                                                                val changeLog = HasChangedModel.getInstance().compareChanges(FacilityDataModelOrg.getInstance().tblPrograms,FacilityDataModel.getInstance().tblPrograms)
                                                                Log.v("CHANGES ---->",changeLog)
                                                                FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].Comments =
                                                                    currentRowDataModel.Comments
                                                                FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].expDate =
                                                                    currentRowDataModel.expDate
                                                                FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].effDate =
                                                                    currentRowDataModel.effDate
                                                                FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].ProgramTypeID =
                                                                    currentRowDataModel.ProgramTypeID
                                                                var tempPrograms =
                                                                    ArrayList<TblPrograms>()
                                                                FacilityDataModel.getInstance().tblPrograms.sortedWith(
                                                                    compareBy<TblPrograms> { it.expDate })
                                                                    .toCollection(tempPrograms)
                                                                FacilityDataModel.getInstance().tblPrograms.clear()
                                                                FacilityDataModelOrg.getInstance().tblPrograms.clear()
                                                                (activity as FormsActivity).saveDone =
                                                                    true
                                                                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate })
                                                                    .toCollection(FacilityDataModel.getInstance().tblPrograms)
                                                                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate })
                                                                    .toCollection(
                                                                        FacilityDataModelOrg.getInstance().tblPrograms
                                                                    )
                                                                fillPortalTrackingTableView()

                                                            } else {
                                                                var errorMessage =
                                                                    response.toString()
                                                                        .substring(
                                                                            response.toString()
                                                                                .indexOf("<message") + 9,
                                                                            response.toString()
                                                                                .indexOf("</message")
                                                                        )
                                                                Utility.showSubmitAlertDialog(
                                                                    activity,
                                                                    false,
                                                                    "Program (Error: " + errorMessage + " )"
                                                                )
                                                            }
                                                            binding.editProgramCard.visibility =
                                                                View.GONE
                                                            binding.editProgramsLoadingView.visibility =
                                                                View.GONE
                                                            binding.alphaBackgroundForProgramDialogs.visibility =
                                                                View.GONE
                                                            (activity as FormsActivity).overrideBackButton =
                                                                false
                                                            enableAllAddButnsAndDialog()
                                                        }
                                                    },
                                                    {
                                                        Utility.showSubmitAlertDialog(
                                                            activity,
                                                            false,
                                                            "Program (Error: " + it.message + " )"
                                                        )
                                                        binding.editProgramCard.visibility =
                                                            View.GONE
                                                        binding.editProgramsLoadingView.visibility =
                                                            View.GONE
                                                        binding.alphaBackgroundForProgramDialogs.visibility =
                                                            View.GONE
                                                        (activity as FormsActivity).overrideBackButton =
                                                            false
                                                        enableAllAddButnsAndDialog()
                                                    }).setRetryPolicy(DefaultRetryPolicy(30000, 0, 1f))
                                            )
                                        }
                                    } else {
                                        //                            Toast.makeText(context, "please fill all required fields", Toast.LENGTH_SHORT).show()
                                        //                                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
                                        Utility.showValidationAlertDialog(
                                            activity,
                                            "Please fill all required fields" + validationMsg
                                        )
                                    }
                                } else {
                                    Utility.showInternetWarningDialog(
                                        requireContext(),
                                        (requireActivity() as FormsActivity).networkStatusErrorMsg
                                    )
                                }
                            }
                        }

//                    var childViewCount = aarPortalTrackingTableLayout.getChildCount();

//                for (i in 1..childViewCount - 1) {
//                    var noOfEmpty = 0
//
//
//                    var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;
//
//                    for (j in 0..row.getChildCount() - 1) {
//
//                        var tv: TextView = row.getChildAt(j) as TextView
//
//                        if (tv.text.toString().isNullOrEmpty()) {
//
//                            noOfEmpty++
//
//                        }
//                        if (noOfEmpty == row.getChildCount() - 1) {
//
//                            aarPortalTrackingTableLayout.removeViewAt(i)
//
//                        }
//                    }
//
//                }

//                        if (textView1.text.toString().isNullOrBlank() && textView2.text.toString()
//                                .isNullOrBlank() && textView3.text.toString()
//                                .isNullOrBlank() && textView4.text.toString().isNullOrBlank()
//                        ) {
//
//                        } else {
//                            aarPortalTrackingTableLayout.addView(tableRow)
//                        }
                    }
                    if (textView1.text.toString().isNullOrBlank() && textView2.text.toString()
                            .isNullOrBlank() && textView3.text.toString()
                            .isNullOrBlank() && textView4.text.toString().isNullOrBlank()
                    ) {

                    } else {
                        binding.aarPortalTrackingTableLayout.addView(tableRow)
                    }
                }
                //HERE
            }

        }
        altTableRow(2)
    }

    fun altTableRow(alt_row: Int) {
        var childViewCount = binding.aarPortalTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                    R.drawable.alt_row_color
                );
            } else {
                row.background = getResources().getDrawable(
                    R.drawable.row_color
                );
            }

        }
    }


    fun disableAllAddButnsAndDialog() {

        for (i in 0 until binding.programsViewLinearId.childCount) {
            val child = binding.programsViewLinearId.getChildAt(i)
            child.isEnabled = false
        }

        var childViewCount = binding.aarPortalTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount() - 1) {

                var tv: TextView = row.getChildAt(j) as TextView
                tv.isEnabled = false

            }

        }
    }

    fun enableAllAddButnsAndDialog() {
        for (i in 0 until binding.programsViewLinearId.childCount) {
            val child = binding.programsViewLinearId.getChildAt(i)
            child.isEnabled = true
        }

        var childViewCount = binding.aarPortalTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.aarPortalTrackingTableLayout.getChildAt(i) as TableRow;
            for (j in 0..row.getChildCount() - 1) {
                var tv: TextView = row.getChildAt(j) as TextView
                tv.isEnabled = true
            }
        }
    }

    fun validateInputs(): Boolean {
        var programValide = TblPrograms().isInputsValid
        programValide = true
        validationMsg = ""
        binding.effectiveDateTextviewVal.setError(null)
        binding.commentsEditTextVal.setError(null)

        if (binding.effectiveDateTextviewVal.text.toString().uppercase(getDefault()).equals("SELECT DATE")) {
            programValide = false
            binding.effectiveDateTextviewVal.setError("Required Field")
        }

        if (!binding.expirationDateTextviewVal.text.toString().uppercase(getDefault())
                .equals("SELECT DATE")
        ) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.effectiveDateTextviewVal!!.text.toString())
            val expDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.expirationDateTextviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                programValide = false
                binding.expirationDateTextviewVal.setError("Should be after Effective Date")
                validationMsg += "\nExpiration Date should be after Effective Date"
            }
        }

        if (binding.commentsEditTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            binding.commentsEditTextVal.setError("Required Field")
        }

        return programValide
    }

    fun edit_validateInputs(): Boolean {

        var programValide = TblPrograms().isInputsValid
        programValide = true
        validationMsg = ""
        binding.editCommentsEditTextVal.setError(null)
        binding.editEffectiveDateTextviewVal.setError(null)

        if (binding.editEffectiveDateTextviewVal.text.toString().uppercase(getDefault())
                .equals("SELECT DATE")
        ) {
            programValide = false
            binding.editEffectiveDateTextviewVal.setError("Required Field")
        }

        if (!binding.editExpirationDateTextviewVal.text.toString().uppercase(getDefault())
                .equals("SELECT DATE")
        ) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editEffectiveDateTextviewVal!!.text.toString())
            val expDate = SimpleDateFormat(
                myFormat,
                Locale.US
            ).parse(binding.editExpirationDateTextviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                programValide = false
                binding.editExpirationDateTextviewVal.setError("Should be after Effective Date")
                validationMsg += "\nExpiration Date should be after Effective Date"
            }
        }


        if (binding.editCommentsEditTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            binding.editCommentsEditTextVal.setError("Required Field")
        }

        return programValide
    }


//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//    }

    override fun onResume() {
        super.onResume()
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
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVPrograms {
            val fragment = FragmentARRAVPrograms()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    fun updateDialogs() {
        if (binding.programCard != null) binding.programCard.visibility = View.GONE
        if (binding.editProgramCard != null) binding.editProgramCard.visibility = View.GONE
        if (binding.alphaBackgroundForProgramDialogs != null) binding.alphaBackgroundForProgramDialogs.visibility =
            View.GONE
    }
}// Required empty public constructor
