package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.size
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateProgramsData

import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_arrav_affliations.*

import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import kotlinx.android.synthetic.main.fragment_arrav_programs.mainViewLinearId

import kotlinx.android.synthetic.main.scope_of_service_group_layout.*

import java.text.ParseException
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_arrav_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited= true
        (activity as FormsActivity).programsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        exitProgramDialogeBtnId.setOnClickListener({
            programCard.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
            alphaBackgroundForProgramDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()

        })
        edit_exitProgramDialogeBtnId.setOnClickListener({
            edit_programCard.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
            alphaBackgroundForProgramDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()
        })

        showNewProgramDialogueButton.setOnClickListener(View.OnClickListener {
            disableAllAddButnsAndDialog()
            comments_editTextVal.setText("")
            effective_date_textviewVal.setText("SELECT DATE")
            expiration_date_textviewVal.setText("SELECT DATE")
            program_name_textviewVal.setSelection(0)
            comments_editTextVal.setError(null)
            effective_date_textviewVal.setError(null)
            expiration_date_textviewVal.setError(null)
            programCard.visibility = View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
            alphaBackgroundForProgramDialogs.visibility = View.VISIBLE
        })


        effective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!effective_date_textviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(effective_date_textviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat2 = "MM/dd/yyy" // mention the format you need
                val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                effective_date_textviewVal!!.text = sdf.format(c.time)
                dateOne = sdf2.format(c.time)

            }, year, month, day)
            dpd.show()
        }
        edit_effective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_effective_date_textviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_effective_date_textviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat2 = "dd MM yyyy" // mention the format you need
                val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                edit_effective_date_textviewVal!!.text = sdf.format(c.time)
                dateOne = sdf2.format(c.time)

            }, year, month, day)
            dpd.show()
        }


        expiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!expiration_date_textviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(expiration_date_textviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat3 = "dd MM yyyy" // mention the format you need
                val sdf3 = SimpleDateFormat(myFormat3, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                expiration_date_textviewVal!!.text = sdf.format(c.time)
                dateTwo = sdf3.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        edit_expiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!edit_expiration_date_textviewVal.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(edit_expiration_date_textviewVal.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat3 = "dd MM yyyy" // mention the format you need
                val sdf3 = SimpleDateFormat(myFormat3, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                edit_expiration_date_textviewVal!!.text = sdf.format(c.time)
                dateTwo = sdf3.format(c.time)
            }, year, month, day)
            dpd.show()
        }




        submitNewProgramButton.setOnClickListener {
            if (validateInputs()) {
                var validProgram = true
                var valid_validProgram = false
                for (fac in TypeTablesModel.getInstance().ProgramsType) {
                    if (program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName)) {
                        for (item1 in FacilityDataModel.getInstance().tblPrograms)
                            if (item1.ProgramTypeID.toString().equals(fac.ProgramTypeID.toString())) {
                                val dateFormat = SimpleDateFormat("MM/dd/yyyy")
                                var newEffDate = Date()
                                var newExpDate = dateFormat.parse("01/01/2500")
                                var DB_EffDate = Date()
                                var DB_ExpDate = dateFormat.parse("01/01/2500")
                                try {
                                    //
                                    newEffDate = dateFormat.parse(effective_date_textviewVal!!.text.toString())
                                    if (expiration_date_textviewVal!!.text.toString().isNullOrEmpty() || expiration_date_textviewVal!!.text.toString().equals("SELECT DATE"))
                                    else
                                        newExpDate = dateFormat.parse(expiration_date_textviewVal!!.text.toString())
                                    DB_EffDate = dateFormat.parse(item1.effDate.apiToAppFormatMMDDYYYY())
                                    if (item1.expDate.apiToAppFormatMMDDYYYY().isNullOrEmpty() || item1.expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900"))
                                    else
                                        DB_ExpDate = dateFormat.parse(item1.expDate.apiToAppFormatMMDDYYYY())
                                } catch (e: ParseException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                }

//                                if (!item1.expDate.isNullOrEmpty() || !item1.expDate.isNullOrBlank()) {
//                                if (item1.expDate.isNullOrEmpty() || item1.expDate.equals("01/01/1900")) {
                                    // Option 1 .. Old program has no expiry date
                                    if (DB_ExpDate==dateFormat.parse("01/01/2500")){
                                        // Option 1.1 .. New Program b4 old program and expiry b4 old program as well
                                        if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate<DB_EffDate) {
                                            validProgram = true
                                            valid_validProgram=true
                                        // Option 1.2 .. New Program b4 old program and expiry after start of old program
                                        } else if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate>=DB_EffDate) {
                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                            validProgram = false
                                            valid_validProgram= false
                                        } else if (newEffDate<=DB_EffDate && newExpDate==dateFormat.parse("01/01/2500")) {
                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                            validProgram = false
                                            valid_validProgram= false
                                        // Option 1.3 .. New Program after old program start date while old program has no expiry
                                        } else if (newEffDate>DB_EffDate) {
                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                            validProgram = false
                                            valid_validProgram= false
                                        }
                                        // Option 2 Old Program has expiry date
                                    } else {
                                        // Option 2.1 .. New Program b4 old program and expiry b4 old program as well
                                        if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate<DB_EffDate) {
                                            validProgram = true
                                            valid_validProgram=true
                                            // Option 2.2 .. New Program b4 old program and expiry after start of old program
                                        } else if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate>=DB_EffDate) {
                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                            validProgram = false
                                            valid_validProgram= false
                                        } else if (newEffDate>DB_EffDate && newEffDate<DB_ExpDate) {
                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                            validProgram = false
                                            valid_validProgram= false
                                        } else if (newEffDate>DB_ExpDate){
                                            validProgram = true
                                            valid_validProgram=true
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
                if (validProgram||valid_validProgram) {
                    progressBarTextVal.text = "Saving ..."
                    programsLoadingView.visibility = View.VISIBLE

                    var item = TblPrograms()
                    for (fac in TypeTablesModel.getInstance().ProgramsType) {
                        if (program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName))

                            item.ProgramTypeID = fac.ProgramTypeID
                    }

                    item.effDate = if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                    item.expDate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                    item.Comments = comments_editTextVal.text.toString()
                    Log.v("PROGRAMS ADD --- ",UpdateProgramsData +FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&programId=&programTypeId=${item.ProgramTypeID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat())
                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateProgramsData +FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&programId=&programTypeId=${item.ProgramTypeID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 0, getProgramChanges(0,0)),
                            Response.Listener { response ->
                                activity!!.runOnUiThread {
                                    if (response.toString().contains("returnCode>0<",false)) {
                                        alphaBackgroundForProgramDialogs.visibility = View.GONE
                                        (activity as FormsActivity).overrideBackButton = false
                                        // collect program id
                                        Utility.showSubmitAlertDialog(activity,true,"Program")
                                        item.ProgramID= response.toString().substring(response.toString().indexOf("<programID")+11,response.toString().indexOf("</programID"))
                                        FacilityDataModel.getInstance().tblPrograms.add(item)
                                        FacilityDataModelOrg.getInstance().tblPrograms.add(item)
//                                        Utility.showMessageDialog(activity,"Program ID",item.ProgramID)
                                        HasChangedModel.getInstance().groupSoSPrograms[0].SoSPrograms= true
                                        HasChangedModel.getInstance().checkIfChangeWasDoneforSoSPrograms()
                                        fillPortalTrackingTableView()
                                        altTableRow(2)
                                        (activity as FormsActivity).saveDone = true
                                        programCard.visibility = View.GONE
                                        progressBarTextVal.text = "Loading ..."
                                        programsLoadingView.visibility = View.GONE
                                    } else {
                                        progressBarTextVal.text = "Loading ..."
                                        programsLoadingView.visibility = View.GONE
                                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                        Utility.showSubmitAlertDialog(activity, false, "Program (Error: "+errorMessage+" )")

                                    }
                                    enableAllAddButnsAndDialog()


                                }
                            }, Response.ErrorListener {
                        Utility.showSubmitAlertDialog(activity, false, "Program (Error: "+it.message+" )")
                        enableAllAddButnsAndDialog()
                        progressBarTextVal.text = "Loading ..."
                        programsLoadingView.visibility = View.GONE
                        alphaBackgroundForProgramDialogs.visibility = View.GONE
                    }))
                }
            } else {
//                Utility.showValidationAlertDialog(activity,"Please fill the required fields")
                Utility.showValidationAlertDialog(activity, "Please fill all required fields \nExpiration Date should be after Effective Date")
            }

        }
        prepareProgramTypes()
        fillPortalTrackingTableView();
        altTableRow(2)
    }

    fun getProgramChanges(action : Int, rowId: Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        if (action==0) {
            strChanges = "Program added with "
            strChanges += "Type (" + program_name_textviewVal.getSelectedItem().toString()+ ") - "
            strChanges += "Effective Date (" + if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString() + ") - "
            strChanges += "Expiration Date (" + if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString() + ") - "
            strChanges += "Comments (" + comments_editTextVal.text.toString() + ")"
        }
        val Comments = edit_comments_editTextVal.text.toString()
        val effDate = if (edit_effective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_effective_date_textviewVal.text.toString()
        val expDate = if (edit_expiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_expiration_date_textviewVal.text.toString()
        val programName = edit_program_name_textviewVal.selectedItem.toString()
        if (action==1) {
            if (Comments != FacilityDataModelOrg.getInstance().tblPrograms[rowId].Comments) {
                strChanges += "Program comments changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].Comments+ ") to (${Comments}) - "
            }
            if (effDate != FacilityDataModelOrg.getInstance().tblPrograms[rowId].effDate.apiToAppFormatMMDDYYYY()) {
                strChanges += "Effective Date changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].effDate.apiToAppFormatMMDDYYYY() + ") to (" + effDate + ") - "
            }
            if (expDate != FacilityDataModelOrg.getInstance().tblPrograms[rowId].expDate.apiToAppFormatMMDDYYYY()) {
                strChanges += "Expiration Date changed from (" + FacilityDataModelOrg.getInstance().tblPrograms[rowId].expDate.apiToAppFormatMMDDYYYY() + ") to (" + expDate + ") - "
            }
            if (programName != (TypeTablesModel.getInstance().ProgramsType.filter { s->s.ProgramTypeID.equals(FacilityDataModelOrg.getInstance().tblPrograms[rowId].ProgramTypeID)}[0].ProgramTypeName)) {
                strChanges += "Program Type changed from (" + TypeTablesModel.getInstance().ProgramsType.filter { s->s.ProgramTypeID.equals(FacilityDataModelOrg.getInstance().tblPrograms[rowId].ProgramTypeID)}[0].ProgramTypeName + ") to (" + programName + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun prepareProgramTypes() {

        for (fac in TypeTablesModel.getInstance().ProgramsType.filter { s->s.active.equals("true")}.toCollection(ArrayList())) {
            programTypesArray.add(fac.ProgramTypeName)
        }
        var programsAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, programTypesArray)
        programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        program_name_textviewVal.adapter = programsAdapter
        edit_program_name_textviewVal.adapter = programsAdapter

    }

    fun fillPortalTrackingTableView() {

        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (aarPortalTrackingTableLayout.childCount>1) {
            for (i in aarPortalTrackingTableLayout.childCount - 1 downTo 1) {
                aarPortalTrackingTableLayout.removeViewAt(i)
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

                        var currentTableRowIndex = aarPortalTrackingTableLayout.indexOfChild(tableRow)
                        var currentfacilityDataModelIndex = currentTableRowIndex - 1


                        disableAllAddButnsAndDialog()
                        edit_comments_editTextVal.setText(textView4.text)
                        edit_effective_date_textviewVal.setText(if (textView2.text.equals("")) "SELECT DATE" else textView2.text.toString())
                        edit_expiration_date_textviewVal.setText(if (textView3.text.equals("")) "SELECT DATE" else textView3.text.toString())
                        var i = programTypesArray.indexOf(textView1.text)
                        edit_program_name_textviewVal.setSelection(i)

                        edit_programCard.visibility = View.VISIBLE
                        alphaBackgroundForProgramDialogs.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true

                        edit_submitNewProgramButton.setOnClickListener {
                            var currentRowDataModel = FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex]
                            var originalDataModel = FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex]
                            if (edit_validateInputs()) {
                                var validProgram = true
                                var valid_validProgram = false
                                for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                    if (edit_program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName)) {
                                        for (item1 in FacilityDataModel.getInstance().tblPrograms)
                                            if (item1.ProgramTypeID.toString().equals(fac.ProgramTypeID.toString())) {
                                                val selectemProgramName = edit_program_name_textviewVal.getSelectedItem().toString()
                                                var numToCopare = FacilityDataModel.getInstance().tblPrograms.indexOf(item1)
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


                                                    val dateFormat = SimpleDateFormat("MM/dd/yyyy")
                                                    var newEffDate = Date()
                                                    var newExpDate = dateFormat.parse("01/01/2500")
                                                    var DB_EffDate = Date()
                                                    var DB_ExpDate = dateFormat.parse("01/01/2500")
                                                    try {
                                                        //
                                                        newEffDate = dateFormat.parse(edit_effective_date_textviewVal!!.text.toString())
                                                        if (edit_expiration_date_textviewVal!!.text.toString().isNullOrEmpty() || edit_expiration_date_textviewVal!!.text.toString().equals("SELECT DATE"))
                                                        else
                                                            newExpDate = dateFormat.parse(edit_expiration_date_textviewVal!!.text.toString())
                                                        DB_EffDate = dateFormat.parse(item1.effDate.apiToAppFormatMMDDYYYY())
                                                        if (item1.expDate.apiToAppFormatMMDDYYYY().isNullOrEmpty() || item1.expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900"))
                                                        else
                                                            DB_ExpDate = dateFormat.parse(item1.expDate.apiToAppFormatMMDDYYYY())
                                                    } catch (e: ParseException) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace()
                                                    }

                                                    if (DB_ExpDate==dateFormat.parse("01/01/2500")){
                                                        // Option 1.1 .. New Program b4 old program and expiry b4 old program as well
                                                        if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate<DB_EffDate) {
                                                            validProgram = true
                                                            valid_validProgram=true
                                                            // Option 1.2 .. New Program b4 old program and expiry after start of old program
                                                        } else if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate>=DB_EffDate) {
                                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                                            validProgram = false
                                                            valid_validProgram= false
                                                        } else if (newEffDate<=DB_EffDate && newExpDate==dateFormat.parse("01/01/2500")) {
                                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                                            validProgram = false
                                                            valid_validProgram= false
                                                            // Option 1.3 .. New Program after old program start date while old program has no expiry
                                                        } else if (newEffDate>DB_EffDate) {
                                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                                            validProgram = false
                                                            valid_validProgram= false
                                                        }
                                                        // Option 2 Old Program has expiry date
                                                    } else {
                                                        // Option 2.1 .. New Program b4 old program and expiry b4 old program as well
                                                        if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate<DB_EffDate) {
                                                            validProgram = true
                                                            valid_validProgram=true
                                                            // Option 2.2 .. New Program b4 old program and expiry after start of old program
                                                        } else if (newEffDate<=DB_EffDate && newExpDate!=dateFormat.parse("01/01/2500") && newExpDate>=DB_EffDate) {
                                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                                            validProgram = false
                                                            valid_validProgram= false
                                                        } else if (newEffDate>DB_EffDate && newEffDate<DB_ExpDate) {
                                                            Utility.showValidationAlertDialog(activity,"This program is already active within the same dates")
                                                            validProgram = false
                                                            valid_validProgram= false
                                                        } else if (newEffDate>DB_ExpDate){
                                                            validProgram = true
                                                            valid_validProgram=true
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                    ///////////////////////









                                    /////////////////////

                                }


                                if (validProgram || valid_validProgram) {


                                    edit_programsLoadingView.visibility = View.VISIBLE

                                    currentRowDataModel.Comments = edit_comments_editTextVal.text.toString()

                                    currentRowDataModel.effDate = if (edit_effective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_effective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    currentRowDataModel.expDate = if (edit_expiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_expiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()


                                    var effdateForSubmit = if (edit_effective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_effective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    var expdateForSubmit = if (edit_expiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_expiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                    for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                        if (edit_program_name_textviewVal.selectedItem.toString().equals(fac.ProgramTypeName)) {
                                            currentRowDataModel.ProgramTypeID = fac.ProgramTypeID
                                        }
                                    }

                                    Log.v("PROGRAMS EDIT --- ",UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=${currentRowDataModel.ProgramID}&programTypeId=${currentRowDataModel.ProgramTypeID}&effDate=$effdateForSubmit&expDate=$expdateForSubmit&comments=${currentRowDataModel.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat())
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateProgramsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&programId=${currentRowDataModel.ProgramID}&programTypeId=${currentRowDataModel.ProgramTypeID}&effDate=$effdateForSubmit&expDate=$expdateForSubmit&comments=${currentRowDataModel.Comments}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 1, getProgramChanges(1,currentfacilityDataModelIndex)),
                                            Response.Listener { response ->
                                                requireActivity().runOnUiThread {
                                                    if (response.toString().contains("returnCode>0<", false)) {
                                                        Utility.showSubmitAlertDialog(activity, true, "Program")
                                                        HasChangedModel.getInstance().groupSoSPrograms[0].SoSPrograms = true
                                                        HasChangedModel.getInstance().checkIfChangeWasDoneforSoSPrograms()
                                                        FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].Comments = currentRowDataModel.Comments
                                                        FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].expDate = currentRowDataModel.expDate
                                                        FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].effDate= currentRowDataModel.effDate
                                                        FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex].ProgramTypeID = currentRowDataModel.ProgramTypeID
                                                        FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].Comments = currentRowDataModel.Comments
                                                        FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].expDate = currentRowDataModel.expDate
                                                        FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].effDate= currentRowDataModel.effDate
                                                        FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex].ProgramTypeID = currentRowDataModel.ProgramTypeID
                                                        var tempPrograms = ArrayList<TblPrograms>()
                                                        FacilityDataModel.getInstance().tblPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(tempPrograms)
                                                        FacilityDataModel.getInstance().tblPrograms.clear()
                                                        FacilityDataModelOrg.getInstance().tblPrograms.clear()
                                                        (activity as FormsActivity).saveDone = true
                                                        tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModel.getInstance().tblPrograms)
                                                        tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModelOrg.getInstance().tblPrograms)
                                                        fillPortalTrackingTableView()
                                                    } else {
                                                        var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                        Utility.showSubmitAlertDialog(activity, false, "Program (Error: "+ errorMessage+" )")
                                                    }
                                                    edit_programCard.visibility = View.GONE
                                                    edit_programsLoadingView.visibility = View.GONE
                                                    alphaBackgroundForProgramDialogs.visibility = View.GONE
                                                    (activity as FormsActivity).overrideBackButton = false
                                                    enableAllAddButnsAndDialog()
                                                }
                                            }, Response.ErrorListener {
                                        Utility.showSubmitAlertDialog(activity, false, "Program (Error: "+it.message+" )")
                                        edit_programCard.visibility = View.GONE
                                        edit_programsLoadingView.visibility = View.GONE
                                        alphaBackgroundForProgramDialogs.visibility = View.GONE
                                        (activity as FormsActivity).overrideBackButton = false
                                        enableAllAddButnsAndDialog()
                                    }))
                                }
                            } else {
                                //                            Toast.makeText(context, "please fill all required fields", Toast.LENGTH_SHORT).show()
//                                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
                                Utility.showValidationAlertDialog(activity, "Please fill all required fields \nExpiration Date should be after Effective Date")
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

                    if (textView1.text.toString().isNullOrBlank() && textView2.text.toString().isNullOrBlank() && textView3.text.toString().isNullOrBlank() && textView4.text.toString().isNullOrBlank()) {

                    } else {
                        aarPortalTrackingTableLayout.addView(tableRow)
                    }
                }
            }

        }
        altTableRow(2)
    }

    fun altTableRow(alt_row: Int) {
        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                        R.drawable.alt_row_color);
            } else {
                row.background = getResources().getDrawable(
                        R.drawable.row_color);
            }

        }
    }


    fun disableAllAddButnsAndDialog(){

        for (i in 0 until mainViewLinearId.childCount)
    {
        val child = mainViewLinearId.getChildAt(i)
        child.isEnabled = false
    }

    var childViewCount = aarPortalTrackingTableLayout.getChildCount();

    for ( i in 1..childViewCount-1)
    {
        var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

        for (j in 0..row.getChildCount() - 1) {

            var tv: TextView = row.getChildAt(j) as TextView
            tv.isEnabled = false

        }

    }
}
    fun enableAllAddButnsAndDialog(){


        for (i in 0 until mainViewLinearId.childCount)
    {
        val child = mainViewLinearId.getChildAt(i)
        child.isEnabled = true
    }

    var childViewCount = aarPortalTrackingTableLayout.getChildCount();

    for ( i in 1..childViewCount-1)
    {
        var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

        for (j in 0..row.getChildCount() - 1) {

            var tv: TextView = row.getChildAt(j) as TextView
            tv.isEnabled = true

        }

    }
}

    fun validateInputs(): Boolean {

        var programValide= TblPrograms().isInputsValid
        programValide = true

        effective_date_textviewVal.setError(null)
        comments_editTextVal.setError(null)


        if (effective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            programValide = false
            effective_date_textviewVal.setError("Required Field")
        }

        if(!expiration_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(effective_date_textviewVal!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(expiration_date_textviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                programValide = false
                expiration_date_textviewVal.setError("Should be after Effective Date")
            }
        }

        if (comments_editTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            comments_editTextVal.setError("Required Field")
        }

        return  programValide
    }
    fun edit_validateInputs(): Boolean {

        var programValide= TblPrograms().isInputsValid
        programValide = true

        edit_comments_editTextVal.setError(null)
        edit_effective_date_textviewVal.setError(null)

        if (edit_effective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            programValide = false
            edit_effective_date_textviewVal.setError("Required Field")
        }

        if(!edit_expiration_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val effDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_effective_date_textviewVal!!.text.toString())
            val expDate = SimpleDateFormat(myFormat, Locale.US).parse(edit_expiration_date_textviewVal!!.text.toString())
            if (expDate.before(effDate)) {
                programValide = false
                edit_expiration_date_textviewVal.setError("Should be after Effective Date")
            }
        }


        if (edit_comments_editTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            edit_comments_editTextVal.setError("Required Field")
        }

        return  programValide
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
}// Required empty public constructor
