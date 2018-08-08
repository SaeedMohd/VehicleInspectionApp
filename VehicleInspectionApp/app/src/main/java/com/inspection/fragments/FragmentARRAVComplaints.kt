package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.toast
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.validationProblemFoundForOtherFragments
import com.inspection.model.AAAFacilityComplaints
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_aarav_complaints.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVComplaints.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVComplaints.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVComplaints : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var facilityComplaintsList = ArrayList<AAAFacilityComplaints>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_aarav_complaints, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService()
        scopeOfServiceChangesWatcher()
        programsChangesMade = false
        // The no os complaints , justified and ratio need to be clarified when all are showed
//        compeditBtn.setOnClickListener({
//            for (fac in facilityComplaintsList) {
//                if (fac.complaintid.equals(complaint_id_textviewVal.text.toString())){
//                    fac.comments = comments_textviewVal.text.toString()
//                }
//                BuildComplaintsList()
//            }
//        })
//        compShowAllBtn.setOnClickListener({
//            prepareComplaints(true)
//        })
//        prepareComplaints(true)
        //  prepareComplaintsSpinners()
        fillComplaintsTableView()
        comNoTextViewId.text = getNoOfComplaintsForPast12M()
        justComNoTextViewId.text = getNoOfJustComplaintsForPast12M()
        justComRatioTextViewId.text = getJustComplaintsRatio()
//
//        newRecDateBtn.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                // Display Selected date in textbox
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year, monthOfYear, dayOfMonth)
//                newRecDateBtn!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//
//        newOpenDateBtn.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                // Display Selected date in textbox
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year, monthOfYear, dayOfMonth)
//                newOpenDateBtn!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//
//        new2ndOpenDateBtn.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                // Display Selected date in textbox
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year, monthOfYear, dayOfMonth)
//                new2ndOpenDateBtn!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//
//        newClosedDateBtn.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                // Display Selected date in textbox
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year, monthOfYear, dayOfMonth)
//                newClosedDateBtn!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//
//        new2ndClosedDateBtn.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                // Display Selected date in textbox
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year, monthOfYear, dayOfMonth)
//                new2ndClosedDateBtn!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//
//
//        addNewBtn.setOnClickListener({
//            var validProgram = true
//            if (validProgram) {
//                var item = FacilityDataModel.TblComplaintFiles()
//
//
//                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
//                item.ReceivedDate = if (newRecDateBtn.text.equals("SELECT DATE")) "" else newRecDateBtn.text.toString()
//                item.ComplaintID=newComplaintIDText.text.toString()
//                item.FirstName=searchFirstNameText.text.toString()
//                item.LastName=searchSecondNameText.text.toString()
//                 FacilityDataModel.getInstance().tblComplaintFiles.add(item)
//                //  BuildProgramsList()
//
//                addTheLatestRowOfPortalAdmin()
//
//            }
//        })

        altDeffTableRow(2)


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
        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        FacilityDataModel.getInstance().tblComplaintFiles[FacilityDataModel.getInstance().tblComplaintFiles.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ComplaintID
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = ""
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = FirstName
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam5
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = LastName
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam6
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = TypeTablesModel.getInstance().ComplaintFilesReasonType.filter { s -> s.ComplaintReasonID == ComplaintID }[0].ComplaintReasonName
            for (fac in TypeTablesModel.getInstance().ComplaintFilesReasonType) {


                if (ComplaintID.equals(fac.ComplaintReasonID)) {

                    textView.text = fac.ComplaintReasonName
                    Toast.makeText(context, "match", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "doesnt match", Toast.LENGTH_SHORT).show()
                }
            }

            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam7
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)


            textView = TextView(context)
            textView.layoutParams = rowLayoutParam8
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ""
            tableRow.addView(textView)


            ComplaintsResultsTbl.addView(tableRow)

        }
        altDeffTableRow(2)
    }

    fun altDeffTableRow(alt_row: Int) {
        var childViewCount = ComplaintsResultsTbl.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = ComplaintsResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }


    private var initiatedTypeList = ArrayList<TypeTablesModel.complaintInitiatedType>()
    private var initiatedTypeArray = ArrayList<String>()
    private var reasonTypeList = ArrayList<TypeTablesModel.complaintFilesReasonType>()
    private var reasonTypeArray = ArrayList<String>()
    private var resTypeList = ArrayList<TypeTablesModel.complaintFilesResolutionType>()
    private var resTypeArray = ArrayList<String>()


//    fun prepareComplaintsSpinners () {
//
//        initiatedTypeList = TypeTablesModel.getInstance().ComplaintInitiatedType
//        initiatedTypeArray .clear()
//        for (fac in initiatedTypeList ) {
//            initiatedTypeArray.add(fac.ComplaintInitName)
//        }
//        var ComplaintInitAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, initiatedTypeArray)
//        ComplaintInitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newInitiatedBySpinner.adapter = ComplaintInitAdapter
//
//        reasonTypeList= TypeTablesModel.getInstance().ComplaintFilesReasonType
//        reasonTypeArray.clear()
//        for (fac in reasonTypeList) {
//            reasonTypeArray.add(fac.ComplaintReasonName)
//        }
//        var reasonTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasonTypeArray)
//        reasonTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newReasonSpinner.adapter = reasonTypeAdapter
//
//        resTypeList= TypeTablesModel.getInstance().ComplaintFilesResolutionType
//        resTypeArray.clear()
//        for (fac in resTypeList) {
//            resTypeArray.add(fac.ComplaintResolutionName)
//        }
//        var resTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, resTypeArray)
//        resTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newResolutionSpinner.adapter = resTypeAdapter
//
//        newAssignedToSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName +" " + s.LastName}.distinct())
//        newEnteredBySpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName +" " + s.LastName}.distinct())
//    }


    fun prepareComplaints(boolAll: Boolean) {

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityComplaintsURL + AnnualVisitationSingleton.getInstance().facilityId + "&all=" + boolAll.toString(),
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        facilityComplaintsList = Gson().fromJson(response.toString(), Array<AAAFacilityComplaints>::class.java).toCollection(ArrayList())
//                            drawProgramsTable()
                        //   BuildComplaintsList()
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading facility complaints")
            context!!.toast("Connection Error. Please check the internet connection")
        }))
    }


    fun fillComplaintsTableView() {
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
        rowLayoutParam7.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableLayout.LayoutParams.WRAP_CONTENT

//        val rowLayoutParam8 = TableRow.LayoutParams()
//        rowLayoutParam7.weight = 1F
//        rowLayoutParam7.height = TableLayout.LayoutParams.WRAP_CONTENT
//        rowLayoutParam7.column = 8
        var dateTobeFormated = ""
        FacilityDataModel.getInstance().tblComplaintFiles.apply {
            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = getTypeName(get(it).PersonnelTypeID)
                textView.text = get(it).ComplaintID
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
//                textView.text = get(it).LastName
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                if (!(get(it).SeniorityDate.isNullOrEmpty())) {
//                    textView.text = get(it).SeniorityDate.apiToAppFormat()
//                } else {
//                    textView.text = ""
//                }
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).FirstName
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam5
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).LastName
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam6
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                //  textView.text = TypeTablesModel.getInstance().ComplaintFilesReasonType.filter { s -> s.ComplaintReasonID.toString()==get(it).ComplaintID.toString()}[0].ComplaintReasonName.toString()
                for (fac in TypeTablesModel.getInstance().ComplaintFilesReasonType) {


                    if (get(it).ComplaintID.equals(fac.ComplaintReasonID)) {

                        textView.text = fac.ComplaintReasonName

                    }

                }


                tableRow.addView(textView)


                textView = TextView(context)
                textView.layoutParams = rowLayoutParam7
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                try {
                    textView.text = get(it).ReceivedDate.apiToAppFormat()
                } catch (e: Exception) {

                    textView.text = get(it).ReceivedDate
                }
                tableRow.addView(textView)


                textView = TextView(context)
                textView.layoutParams = rowLayoutParam8
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                if (!(get(it).ComplaintID.isNullOrEmpty())) {
//                    textView.text = get(it).ReceivedDate.apiToAppFormat()
//                } else {
//                    textView.text = ""
//                }
                try {
                    textView.text = get(it).ReceivedDate.apiToAppFormat()
                } catch (e: Exception) {

                    textView.text = get(it).ReceivedDate
                }


                tableRow.addView(textView)


                ComplaintsResultsTbl.addView(tableRow)

            }
        }
    }


    fun getNoOfComplaintsForPast12M(): String {

        var comNo = ""

//        for (com in FacilityDataModel.getInstance().tblComplaintFiles){
//
//            val dateFormat = SimpleDateFormat("dd MMM yyyy")
//            var DB_ReceivedDate = Date()
//            try {
//                DB_ReceivedDate = dateFormat.parse(com.ReceivedDate.apiToAppFormat())
//                var date = Date()
//                var cal = Calendar.getInstance()
//                cal.setTime(date)
//                cal.add(Calendar.YEAR,-1)
//                val pastOneYear = cal.getTimeInMillis()
//
//                var cal2 = Calendar.getInstance()
//                cal2.setTime(DB_ReceivedDate)
//                val DB_Year = cal2.getTimeInMillis()
//
//
//
//                if (DB_Year>= pastOneYear){
//
//                    comNo++
//                }
//
//
//            } catch (e: ParseException) {
//                // TODO Auto-generated catch block
//                e.printStackTrace()
//            }
//
//        }
        for (com in FacilityDataModel.getInstance().NumberofComplaints) {

            comNo = com.NumberofComplaintslast12months

        }


        return comNo
    }

    fun fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService() {

        FragmentARRAVScopeOfService.dataChanged =false

        FragmentARRAVScopeOfService.fixedLaborRate = if (FragmentARRAVScopeOfService.watcher_FixedLaborRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate else FragmentARRAVScopeOfService.watcher_FixedLaborRate
        FragmentARRAVScopeOfService.diagnosticLaborRate = if (FragmentARRAVScopeOfService.watcher_DiagnosticsRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate else FragmentARRAVScopeOfService.watcher_DiagnosticsRate
        FragmentARRAVScopeOfService.laborRateMatrixMax = if (FragmentARRAVScopeOfService.watcher_LaborMax.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMax else FragmentARRAVScopeOfService.watcher_LaborMax
        FragmentARRAVScopeOfService.laborRateMatrixMin = if (FragmentARRAVScopeOfService.watcher_LaborMin.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMin else FragmentARRAVScopeOfService.watcher_LaborMin
        FragmentARRAVScopeOfService.numberOfBaysEditText_ = if (FragmentARRAVScopeOfService.watcher_NumOfBays.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays else FragmentARRAVScopeOfService.watcher_NumOfBays
        FragmentARRAVScopeOfService.numberOfLiftsEditText_ = if (FragmentARRAVScopeOfService.watcher_NumOfLifts.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts else FragmentARRAVScopeOfService.watcher_NumOfLifts


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMax != FragmentARRAVScopeOfService.laborRateMatrixMax) {


            FragmentARRAVScopeOfService.dataChanged = true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMin != FragmentARRAVScopeOfService.laborRateMatrixMin) {

            FragmentARRAVScopeOfService.dataChanged = true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate != FragmentARRAVScopeOfService.fixedLaborRate) {


            FragmentARRAVScopeOfService.dataChanged = true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate != FragmentARRAVScopeOfService.diagnosticLaborRate) {


            FragmentARRAVScopeOfService.dataChanged = true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays != FragmentARRAVScopeOfService.numberOfBaysEditText_) {


            FragmentARRAVScopeOfService.dataChanged = true
        }

        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts != FragmentARRAVScopeOfService.numberOfLiftsEditText_) {


            FragmentARRAVScopeOfService.dataChanged = true
        }

    }

    fun scopeOfServiceChangesWatcher() {

        if (!validationProblemFoundForOtherFragments) {
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
                                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {

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
                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
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
                    validationProblemFoundForOtherFragments = true


                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }
        }
    }


            fun getNoOfJustComplaintsForPast12M() : String{

        var justComNo=""

//        for (com in FacilityDataModel.getInstance().tblComplaintFiles){
//
//            val dateFormat = SimpleDateFormat("dd MMM yyyy")
//            var DB_ReceivedDate = Date()
//            try {
//                DB_ReceivedDate = dateFormat.parse(com.ReceivedDate.apiToAppFormat())
//                var date = Date()
//                var cal = Calendar.getInstance()
//                cal.setTime(date)
//                cal.add(Calendar.YEAR,-1)
//                val pastOneYear = cal.getTimeInMillis()
//
//                var cal2 = Calendar.getInstance()
//                cal2.setTime(DB_ReceivedDate)
//                val DB_Year = cal2.getTimeInMillis()
//
//
//
//                if (DB_Year>= pastOneYear){
//
//                    comNo++
//                }
//
//
//            } catch (e: ParseException) {
//                // TODO Auto-generated catch block
//                e.printStackTrace()
//            }
//
//        }
        for (com in FacilityDataModel.getInstance().NumberofJustifiedComplaints){

            justComNo=com.NumberofJustifiedComplaintslast12months

        }


   return justComNo
    }
    fun getJustComplaintsRatio() : String{

        var justComRatio=""

//        for (com in FacilityDataModel.getInstance().tblComplaintFiles){
//
//            val dateFormat = SimpleDateFormat("dd MMM yyyy")
//            var DB_ReceivedDate = Date()
//            try {
//                DB_ReceivedDate = dateFormat.parse(com.ReceivedDate.apiToAppFormat())
//                var date = Date()
//                var cal = Calendar.getInstance()
//                cal.setTime(date)
//                cal.add(Calendar.YEAR,-1)
//                val pastOneYear = cal.getTimeInMillis()
//
//                var cal2 = Calendar.getInstance()
//                cal2.setTime(DB_ReceivedDate)
//                val DB_Year = cal2.getTimeInMillis()
//
//
//
//                if (DB_Year>= pastOneYear){
//
//                    comNo++
//                }
//
//
//            } catch (e: ParseException) {
//                // TODO Auto-generated catch block
//                e.printStackTrace()
//            }
//
//        }
        for (com in FacilityDataModel.getInstance().JustifiedComplaintRatio){

            justComRatio=com.JustifiedComplaintRatio

        }


   return justComRatio
    }

//    fun BuildComplaintsList() {
//        val inflater = (activity as ItemListActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val parentLayout = compListLL
//        parentLayout.removeAllViews()
//        for (fac in facilityComplaintsList) {
//            val vCompRow = inflater.inflate(R.layout.custom_complain_list_item, parentLayout, false)
//            val compId= vCompRow.findViewById(R.id.compItemId) as TextView
//            val compFirstName= vCompRow.findViewById(R.id.compItem1stName) as TextView
//            val complastName= vCompRow.findViewById(R.id.compItem2ndName) as TextView
//            val compReceievedDate= vCompRow.findViewById(R.id.compItemDate) as TextView
//            val compReason= vCompRow.findViewById(R.id.compItemReason) as TextView
//            val compResolution= vCompRow.findViewById(R.id.compItemResolution) as TextView
//            val compComments= vCompRow.findViewById(R.id.compItemComments) as TextView
//            compId.text = fac.complaintid.toString()
//            compFirstName.text = fac.firstname
//            complastName.text = fac.lastname
//            compReceievedDate.text = if (fac.receiveddate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.receiveddate)) else fac.receiveddate
//            compReason.text = fac.complaintreasonname
//            compResolution.text = fac.complaintresolutionname
//            compComments.text = fac.comments
//            vCompRow.setOnClickListener({
//                complaint_id_textviewVal.setText(fac.complaintid)
//                received_date_textviewVal.text = if (fac.receiveddate.isNullOrEmpty() || fac.receiveddate.equals("NULL") || fac.receiveddate.equals("") || fac.receiveddate.toLowerCase().equals("no date provided")) "No Date Provided" else  {
//                    if (fac.receiveddate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.receiveddate)) else fac.receiveddate
//                }
//                first_name_textviewVal.setText(fac.firstname)
//                last_name_textviewVal.setText(fac.lastname)
//                complaint_reason_textviewVal.setText(fac.complaintreasonname)
//                complaint_resolution_textviewVal.setText(fac.complaintresolutionname)
//                comments_textviewVal.setText(fac.comments)
//
//                no_of_complaints_textviewVal.setText(fac.noofcomplaintslastyear.toString())
//                no_of_justified_complaints_textviewVal.setText(fac.noofjustifiedlastyear.toString())
//                justified_complaint_ratio_textviewVal.setText(Math.round(fac.noofjustifiedlastyear.toFloat() / fac.totalorders.toFloat()).toString())
//            })
//            parentLayout.addView(vCompRow)
//        }
//    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

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
        var programsChangesMade = false

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVComplaints {
            val fragment = FragmentARRAVComplaints()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
