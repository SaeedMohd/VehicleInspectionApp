package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
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
import com.google.android.gms.drive.events.ChangeListener
import com.google.gson.Gson
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateProgramsData
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.dataChanged
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.diagnosticLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.fixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.implementOnAnyFragment
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.laborRateMatrixMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.laborRateMatrixMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.numberOfBaysEditText_
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.numberOfLiftsEditText_
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.typeIdCompare
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_DiagnosticsRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_FixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfBays
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfLifts
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

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
    val dateFormat1 = SimpleDateFormat("dd MMM yyyy")

    var dateOne = ""
    var dateTwo = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_programs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        scopeOfServiceChangesWatcher()
        exitProgramDialogeBtnId.setOnClickListener({

            programCard.visibility = View.GONE
            alphaBackgroundForProgramDialogs.visibility = View.GONE
            enableAllAddButnsAndDialog()

        })
        edit_exitProgramDialogeBtnId.setOnClickListener({

            edit_programCard.visibility = View.GONE
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
            alphaBackgroundForProgramDialogs.visibility = View.VISIBLE


        })


        effective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat2 = "dd MM yyyy" // mention the format you need
                val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                effective_date_textviewVal!!.text = sdf.format(c.time)
                dateOne = sdf2.format(c.time)

            }, year, month, day)
            dpd.show()
        }
        edit_effective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
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
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
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
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val myFormat3 = "dd MM yyyy" // mention the format you need
                val sdf3 = SimpleDateFormat(myFormat3, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                edit_expiration_date_textviewVal!!.text = sdf.format(c.time)
                dateTwo = sdf3.format(c.time)
            }, year, month, day)
            dpd.show()
        }



        submitNewProgramButton.setOnClickListener({

            if (validateInputs()) {

                var validProgram = true
                var valid_validProgram = false


                for (fac in TypeTablesModel.getInstance().ProgramsType) {
                    if (program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName)) {
                        //   Toast.makeText(context,"spinner match",Toast.LENGTH_SHORT).show()


                        for (item1 in FacilityDataModel.getInstance().tblPrograms)
                            if (item1.ProgramTypeID.toString().equals(fac.ProgramTypeID.toString())) {

//
                                val dateFormat = SimpleDateFormat("dd MMM yyyy")
                                var newEffDate = Date()
                                var newExpDate = Date()
                                var DB_EffDate = Date()
                                var DB_ExpDate = Date()
                                try {
                                    newEffDate = dateFormat.parse(effective_date_textviewVal!!.text.toString())
                                    newExpDate = dateFormat.parse(expiration_date_textviewVal!!.text.toString())
                                    DB_EffDate = dateFormat.parse(item1.effDate.apiToAppFormat())
                                    DB_ExpDate = dateFormat.parse(item1.expDate.apiToAppFormat())
                                } catch (e: ParseException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                }

                                if (!item1.expDate.isNullOrEmpty() || !item1.expDate.isNullOrBlank()) {


                                    if ((DB_ExpDate <= newEffDate) && (newExpDate >= DB_EffDate) ) {

                                        validProgram = true
                                        valid_validProgram=true



                                    } else
                                        Toast.makeText(context, "this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()
                                    validProgram = false


                                } else
                                    Toast.makeText(context, "this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()

                                validProgram = false


                            }

                    }


                }
                if (validProgram||valid_validProgram) {

                    var item = FacilityDataModel.TblPrograms()
                    for (fac in TypeTablesModel.getInstance().ProgramsType) {
                        if (program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName))

                            item.ProgramTypeID = fac.ProgramTypeID
                    }


                    //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()

                    item.effDate = if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString().appToApiFormat()
                    item.expDate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString().appToApiFormat()
                    item.Comments = comments_editTextVal.text.toString()
                    //  BuildProgramsList()


                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateProgramsData + "&programId=29385&programTypeId=${item.ProgramTypeID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=sa&insertDate=2013-04-24T13:40:34.240&updateBy=SumA&updateDate=2013-06-24T15:25:12.513",
                            Response.Listener { response ->
                                activity!!.runOnUiThread(Runnable {
                                    Log.v("RESPONSE", response.toString())
                                    programsLoadingView.visibility = View.GONE
                                    FacilityDataModel.getInstance().tblPrograms.add(item)
                                    fillPortalTrackingTableView()
                                    altTableRow(2)
                                    enableAllAddButnsAndDialog()
                                    programCard.visibility = View.GONE
                                    alphaBackgroundForProgramDialogs.visibility = View.GONE
                                    checkMarkChangesWasDone()

                                })
                            }, Response.ErrorListener {
                        Log.v("error while loading", "error while loading personnal record")
                        programsLoadingView.visibility = View.GONE

                    }))


                }
            } else {
                Toast.makeText(context, "please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        })
//
//        deleteBtn.setOnClickListener({
//            var itemFound =false
//            var item = AAAFacilityPrograms()
//            for (fac in facilityProgramsList) {
//                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
//                    item = fac
//                    itemFound=true
//                }
//            }
//            if (itemFound) {
//                facilityProgramsList.remove(item)
//           //     BuildProgramsList()
//            }
//        })
//
//        editBtn.setOnClickListener({
//            for (fac in facilityProgramsList) {
//                if (fac.programtypename.equals(program_name_textviewVal.getSelectedItem().toString())){
//                    fac.effdate = if (effective_date_textviewVal.text.equals("SELECT DATE")) "" else effective_date_textviewVal.text.toString()
//                    fac.expdate = if (expiration_date_textviewVal.text.equals("SELECT DATE")) "" else expiration_date_textviewVal.text.toString()
//                    fac.comments = comments_editTextVal.text.toString()
//                }
//          //      BuildProgramsList()
//            }
//        })
        prepareProgramTypes()
        fillPortalTrackingTableView();

        altTableRow(2)





    }



    fun checkMarkChangesWasDone() {




        var itemOrgArray = FacilityDataModelOrg.getInstance().tblPrograms
        var itemArray = FacilityDataModel.getInstance().tblPrograms
        if (itemOrgArray.size == itemArray.size) {
            for (i in 0 until itemOrgArray.size){
                var expDate=""
                if (itemOrgArray[i].expDate.isNullOrBlank()&&!itemArray[i].expDate.isNullOrBlank()) {

                    MarkChangeWasDone()
                }
                else
                    if (itemOrgArray[i].expDate.isNullOrBlank()&&itemArray[i].expDate.isNullOrBlank()) {
                        if (itemOrgArray[i].Comments != itemArray[i].Comments ||
                                dateFormat1.parse(itemOrgArray[i].effDate.apiToAppFormat()) != dateFormat1.parse(itemArray[i].effDate.apiToAppFormat()) ||
                                itemOrgArray[i].ProgramTypeID != itemArray[i].ProgramTypeID) {
                            MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgArray[i].Comments + "=="+ itemArray[i].Comments)
                            Log.v("checkkk", itemOrgArray[i].expDate + "=="+ itemArray[i].expDate)
                            Log.v("checkkk", itemOrgArray[i].effDate + "=="+ itemArray[i].effDate)
                            Log.v("checkkk", itemOrgArray[i].ProgramTypeID + "=="+ itemArray[i].ProgramTypeID)

                        }
                    }
       else
                        if (itemOrgArray[i].Comments != itemArray[i].Comments || dateFormat1.parse(itemOrgArray[i].expDate.apiToAppFormat()) != dateFormat1.parse(itemArray[i].expDate.apiToAppFormat()) ||
                    dateFormat1.parse(itemOrgArray[i].effDate.apiToAppFormat()) != dateFormat1.parse(itemArray[i].effDate.apiToAppFormat()) ||
                        itemOrgArray[i].ProgramTypeID != itemArray[i].ProgramTypeID) {
                    MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                    Log.v("checkkk", itemOrgArray[i].Comments + "=="+ itemArray[i].Comments)
                    Log.v("checkkk", itemOrgArray[i].expDate + "=="+ itemArray[i].expDate)
                    Log.v("checkkk", itemOrgArray[i].effDate + "=="+ itemArray[i].effDate)
                    Log.v("checkkk", itemOrgArray[i].ProgramTypeID + "=="+ itemArray[i].ProgramTypeID)

                }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "2ndddd")

        }
    }




    fun prepareProgramTypes() {
//        programTypesArray.add("select program")

        for (fac in TypeTablesModel.getInstance().ProgramsType) {
            programTypesArray.add(fac.ProgramTypeName)
        }
        var programsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, programTypesArray)
        programsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        program_name_textviewVal.adapter = programsAdapter
        edit_program_name_textviewVal.adapter = programsAdapter

//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPrograms + AnnualVisitationSingleton.getInstance().facilityId,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//                        facilityProgramsList = Gson().fromJson(response.toString(), Array<AAAFacilityPrograms>::class.java).toCollection(ArrayList())
//
//
////                            drawProgramsTable()
//                        //          BuildProgramsList()
//                    })
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading facility programs")
//            context!!.toast("Error loading Facility Program. Please check your internet connectivity")
//
//        }))

    }
//
//    fun fillData(){
//        FacilityDataModel.getInstance().tblPrograms[0].apply {
//            startDateButton.text = startDate
////            endDateButton.text = PortalInspectionDate
//            addendumSignedDateButton.text = AddendumSigned
//            numberOfCardsReaderEditText.setText(CardReaders)
////            inspectionDateButton.text = PortalInspectionDate
////            numberOfUnacknowledgedRecordsEditText.setText(NumberUnacknowledgedTows)
////            numberOfInProgressTwoIns.setText(InProgressTows)
////            numberOfInProgressWalkIns.setText(InProgressWalkIns)
//
//        }

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
        FacilityDataModel.getInstance().tblPrograms.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam
                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                for (fac in TypeTablesModel.getInstance().ProgramsType) {
                    if (get(it).ProgramTypeID.equals(fac.ProgramTypeID)) {

                        textView1.text = fac.ProgramTypeName
                    }

                }
                tableRow.addView(textView1)

                val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam1
                textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (get(it).effDate.isNullOrBlank()){
                    textView2.text =""
                }else {
                    try {
                        textView2.text = get(it).effDate.apiToAppFormat()
                    } catch (e: Exception) {

                        textView2.text = get(it).effDate

                    }
                }
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam2
                textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                if (get(it).expDate.isNullOrBlank()){
                    textView3.text =""
                }else {

                    try {
                        textView3.text = get(it).expDate.apiToAppFormat()
                    } catch (e: Exception) {
                        textView3.text = get(it).expDate

                    }
                }
                tableRow.addView(textView3)

                val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam3
                textView4.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView4.text = get(it).Comments
                tableRow.addView(textView4)

                val updateButton = Button(context)
                updateButton.layoutParams = rowLayoutParam4
                updateButton.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                updateButton.text = "Update"
                tableRow.addView(updateButton)

                updateButton.setOnClickListener(View.OnClickListener {

                    var currentTableRowIndex=aarPortalTrackingTableLayout.indexOfChild(tableRow)
                    var currentfacilityDataModelIndex=currentTableRowIndex-1

                    disableAllAddButnsAndDialog()
                    edit_comments_editTextVal.setText(textView4.text)
                    edit_effective_date_textviewVal.setText( if (textView2.text.equals("")) "SELECT DATE" else textView2.text.toString())
                    edit_expiration_date_textviewVal.setText( if (textView3.text.equals("")) "SELECT DATE" else textView3.text.toString())
                    var i = programTypesArray.indexOf(textView1.text)
                    edit_program_name_textviewVal.setSelection(i)

                    edit_programCard.visibility = View.VISIBLE
                    alphaBackgroundForProgramDialogs.visibility = View.VISIBLE

                    edit_submitNewProgramButton.setOnClickListener(View.OnClickListener {


                        var currentRowDataModel = FacilityDataModel.getInstance().tblPrograms[currentfacilityDataModelIndex]
                        var originalDataModel = FacilityDataModelOrg.getInstance().tblPrograms[currentfacilityDataModelIndex]



                        if (edit_validateInputs()) {


                            var validProgram = true
                            var valid_validProgram = false


                            for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                if (edit_program_name_textviewVal.getSelectedItem().toString().equals(fac.ProgramTypeName)) {
                                    //   Toast.makeText(context,"spinner match",Toast.LENGTH_SHORT).show()


                                    for (item1 in FacilityDataModel.getInstance().tblPrograms)
                                        if (item1.ProgramTypeID.toString().equals(fac.ProgramTypeID.toString())) {

                            val selectemProgramName=edit_program_name_textviewVal.getSelectedItem().toString()
                                            var numToCopare=FacilityDataModel.getInstance().tblPrograms.indexOf(item1)


                                            if (textView1.text.toString() == selectemProgramName&&numToCopare==currentfacilityDataModelIndex) {
                                                validProgram = true
                                                valid_validProgram = true



                                            }

                            else {

//
                                                val dateFormat = SimpleDateFormat("dd MMM yyyy")
                                                var newEffDate = Date()
                                                var newExpDate = Date()
                                                var DB_EffDate = Date()
                                                var DB_ExpDate = Date()
                                                try {
                                                    newEffDate = dateFormat.parse(edit_effective_date_textviewVal!!.text.toString())
                                                    newExpDate = dateFormat.parse(edit_expiration_date_textviewVal!!.text.toString())
                                                    DB_EffDate = dateFormat.parse(item1.effDate.apiToAppFormat())
                                                    DB_ExpDate = dateFormat.parse(item1.expDate.apiToAppFormat())
                                                } catch (e: ParseException) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace()
                                                }

                                                    if (!item1.expDate.isNullOrEmpty() || !item1.expDate.isNullOrBlank()) {


                                                        if ((DB_ExpDate <= newEffDate) && (newExpDate >= DB_EffDate)) {

                                                            validProgram = true
                                                            valid_validProgram = true



                                                        } else
                                                            Toast.makeText(context, "1st this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()
                                                        validProgram = false


                                                    }else
                                                        Toast.makeText(context, "2nd this program is already active within this time frame".toString(), Toast.LENGTH_LONG).show()

                                                    validProgram = false

                                                }

                                            }

                                    }

                            }


                                if (validProgram||valid_validProgram) {



                                edit_programsLoadingView.visibility = View.VISIBLE

                                currentRowDataModel.Comments = edit_comments_editTextVal.text.toString()

                                currentRowDataModel.effDate = if (edit_effective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_effective_date_textviewVal.text.toString().appToDBFormat()
                                currentRowDataModel.expDate = if (edit_expiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_expiration_date_textviewVal.text.toString().appToApiSubmitFormat()


                                var effdateForSubmit=edit_effective_date_textviewVal.text.toString()
                                var expdateForSubmit=edit_expiration_date_textviewVal.text.toString()
                                for (fac in TypeTablesModel.getInstance().ProgramsType) {
                                    if (edit_program_name_textviewVal.selectedItem.toString().equals(fac.ProgramTypeName)) {

                                        currentRowDataModel.ProgramTypeID = fac.ProgramTypeID
                                    }

                                }


                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateProgramsData + "&programId=29385&programTypeId=${currentRowDataModel.ProgramTypeID}&effDate=$effdateForSubmit&expDate=$expdateForSubmit&comments=${currentRowDataModel.Comments}&active=1&insertBy=sa&insertDate=2013-04-24T13:40:34.240&updateBy=SumA&updateDate=2013-06-24T15:25:12.513",
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread(Runnable {
                                                Log.v("RESPONSE", response.toString())
                                                fillPortalTrackingTableView()

                                                edit_programCard.visibility = View.GONE
                                                edit_programsLoadingView.visibility = View.GONE
                                                alphaBackgroundForProgramDialogs.visibility = View.GONE
                                                enableAllAddButnsAndDialog()




                                            })
                                        }, Response.ErrorListener {
                                    Log.v("error while loading", "error while loading program record")
                                    edit_programsLoadingView.visibility = View.GONE

                                    Toast.makeText(context,"error while submitting new program",Toast.LENGTH_SHORT).show()

                                }))


                            }

                        }else{
                            Toast.makeText(context, "please fill all required fields", Toast.LENGTH_SHORT).show()


                        }

                    })



                })

                var childViewCount = aarPortalTrackingTableLayout.getChildCount();

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
        altTableRow(2)
    }

    fun altTableRow(alt_row: Int) {
        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
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

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5

        var i = 1
        FacilityDataModel.getInstance().tblPrograms[FacilityDataModel.getInstance().tblPrograms.size - 1].apply {

            var tableRow = TableRow(context)
            if (i % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.alt_row_color)
                i++
            }

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().ProgramsType) {
                if (ProgramTypeID.equals(fac.ProgramTypeID))

                    textView.text = fac.ProgramTypeName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            try {
                textView.text = effDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = effDate
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            try {
                textView.text = expDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = expDate
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = Comments
            tableRow.addView(textView)

            val updateButton = Button(context)
            updateButton.layoutParams = rowLayoutParam4
            updateButton.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            updateButton.text = "Update"
            tableRow.addView(updateButton)


            aarPortalTrackingTableLayout.addView(tableRow)

        }
//        altTableRow(2)
    }

//    fun drawProgramsTable () {
//        programsTableLayout.removeAllViews()
//        var separatorView = View (context)
//        separatorView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1)
//        separatorView.setBackgroundColor(Color.rgb(51, 51, 51))
//        var tableRow = LayoutInflater.from(context).inflate(R.layout.tablerow5cols, null, false)
//        tableRow.programid.setText("ID")
//        tableRow.programname.setText("Current Programs")
//        tableRow.effdate.setText("Effective Date")
//        programsTableLayout.addView(tableRow)
//        programsTableLayout.addView(separatorView)
//        for (fac in facilityProgramsList) {
//            tableRow = LayoutInflater.from(context).inflate(R.layout.tablerow5cols, null, false)
//            tableRow.effdate.setText(fac.effdate)
//            tableRow.programid.setText(fac.programid.toString())
//            tableRow.effdate.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL")) "No Date Provided" else {
//                if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
//            }
//            tableRow.programname.setText(fac.programtypename)
//            tableRow.setOnClickListener({
//                program_name_textviewVal.setSelection(programTypesArray.indexOf(fac.programtypename))
//                effective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.equals("No Date Provided")) "No Date Provided" else  {
//                    if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
//                }
//                expiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.equals("No Date Provided")) "No Date Provided" else   {
//                    if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
//                }
//                comments_editTextVal.setText(fac.comments)
//            })
//            programsTableLayout.addView(tableRow)
//            var rowseparatorView = View (context)
//            rowseparatorView .layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1)
//            rowseparatorView .setBackgroundColor(Color.rgb(51, 51, 51))
//            programsTableLayout.addView(rowseparatorView )
//        }
//    }
//
//    fun BuildProgramsList() {
//        val inflater = activity!!
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val parentLayout = programsListLL
//        parentLayout.removeAllViews()
//        for (fac in facilityProgramsList) {
//            val vProgramRow = inflater.inflate(R.layout.custom_program_list_item, parentLayout, false)
//            val prId= vProgramRow.findViewById(R.id.programItemId) as TextView
//            val prName= vProgramRow.findViewById(R.id.programItemName) as TextView
//            val prEffDate= vProgramRow.findViewById(R.id.programItemEffDate) as TextView
//            val prExpDate= vProgramRow.findViewById(R.id.programItemExpDate) as TextView
//            prId.text = fac.programid.toString()
//            prName.text = fac.programtypename
//            prEffDate.text = if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
//            prExpDate.text = if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
//            vProgramRow.setOnClickListener({
//                program_name_textviewVal.setSelection(programTypesArray.indexOf(fac.programtypename))
//                effective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.equals("No Date Provided")) "No Date Provided" else  {
//                    if (fac.effdate.length>11 ) appFormat.format(dbFormat.parse(fac.effdate)) else fac.effdate
//                }
//                expiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.equals("No Date Provided")) "No Date Provided" else   {
//                    if (fac.expdate.length>11 ) appFormat.format(dbFormat.parse(fac.expdate)) else fac.expdate
//                }
//                comments_editTextVal.setText(fac.comments)
//            })
//            parentLayout.addView(vProgramRow)
//        }
//    }


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

        var programValide= FacilityDataModel.TblPrograms().isInputsValid
        programValide = true

        effective_date_textviewVal.setError(null)
        comments_editTextVal.setError(null)
//        program_name_textviewToCheckSpinner.setError(null)


        if (effective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            programValide = false
            effective_date_textviewVal.setError("Required Field")
        }

//        if (program_name_textviewVal.selectedItem.toString().contains("select")) {
//            isInputsValid = false
//            program_name_textviewToCheckSpinner.setError("Required Field")
//        }

        if (comments_editTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            comments_editTextVal.setError("Required Field")
        }

        return  programValide
    }
    fun edit_validateInputs(): Boolean {

        var programValide= FacilityDataModel.TblPrograms().isInputsValid
        programValide = true

        edit_comments_editTextVal.setError(null)
        edit_effective_date_textviewVal.setError(null)

        if (edit_effective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            programValide = false
            edit_effective_date_textviewVal.setError("Required Field")
        }


        if (edit_comments_editTextVal.text.toString().isNullOrEmpty()) {
            programValide = false
            edit_comments_editTextVal.setError("Required Field")
        }

        return  programValide
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

                        programsLoadingView.visibility = View.VISIBLE



                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=$numberOfLiftsEditText_&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())

                                        programsLoadingView.visibility = View.GONE

                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                                LaborMax = if (laborRateMatrixMax.isNullOrBlank()) LaborMax else laborRateMatrixMax
                                                LaborMin = if (laborRateMatrixMin.isNullOrBlank()) LaborMin else laborRateMatrixMin
                                                FixedLaborRate = if (fixedLaborRate.isNullOrBlank()) FixedLaborRate else fixedLaborRate
                                                DiagnosticsRate = if (diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else diagnosticLaborRate
                                                NumOfBays = if (numberOfBaysEditText_.isNullOrBlank()) NumOfBays else numberOfBaysEditText_
                                                NumOfLifts = if (numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else numberOfLiftsEditText_

                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = typeIdCompare

                                                dataChanged = false

                                            }

                                        }

                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading personnal record")
                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
                            programsLoadingView.visibility = View.GONE


                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        programsLoadingView.visibility = View.GONE

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
                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true


                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }

        }

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

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
