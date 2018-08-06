package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.Constants.UpdateAmendmentOrderTrackingData
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.Utils.toast
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.TypeTablesModel
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_aaram_order_tracking.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAARAmOrderTracking.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVAmOrderTracking : Fragment() {
    private var reasonsTypesArray = ArrayList<String>()
    private var eventsTypesArray = ArrayList<String>()
    private var employeeNamesArray = ArrayList<String>()


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_aaram_order_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (FacilityDataModel.getInstance().tblAmendmentOrderTracking.size>0) {
//            aoIDVal.text = FacilityDataModel.getInstance().tblAmendmentOrderTracking[0].AOID
//            employeeDropDown.text = FacilityDataModel.getInstance().tblAmendmentOrderTracking[0].AOTEmployee
//            eventIDVal.text = FacilityDataModel.getInstance().tblAmendmentOrderTracking[0].EventID
//        }
//        var reasonArray = arrayOf("Update", "Terminate", "Add New")
//        var reasonAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasonArray)
//        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        employeeDropDown.adapter = reasonAdapter
        scopeOfServiceChangesWatcher()
        exitNewAO_DialogeBtnId.setOnClickListener({

            AOCardView.visibility=View.GONE
            alphaBackgroundForAOT_Dialogs.visibility = View.GONE



        })

        showNewAarCardButton.setOnClickListener(View.OnClickListener {

            AOCardView.visibility=View.VISIBLE
            alphaBackgroundForAOT_Dialogs.visibility = View.VISIBLE


        })


        exitEventDialogeBtnId.setOnClickListener({

            addNewEventCard.visibility=View.GONE
            alphaBackgroundForAOT_Dialogs.visibility = View.GONE


        })

        showNewEventDialogue.setOnClickListener(View.OnClickListener {

            addNewEventCard.visibility=View.VISIBLE
            alphaBackgroundForAOT_Dialogs.visibility = View.VISIBLE


        })


        event_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                event_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        fillAmendmentOrdersAndTrackingTableLayout()
        fillNewEventTableLayout()
        prepareSpinnerEmployees()
        prepareSpinnerReasonTypes()
        prepareSpinnerEventsTypes()
        submitNewEventButton.setOnClickListener(View.OnClickListener {


            addNewEventCard.visibility=View.GONE
            alphaBackgroundForAOT_Dialogs.visibility = View.GONE


            var item = FacilityDataModel.TblAmendmentOrderTracking()
                for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {
                    if (eventsDropDown.getSelectedItem().toString().equals(fac.AmendmentEventName))

                        item.EventTypeID = fac.AmendmentEventID

                }
                for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {
                    if (eventsDropDown.getSelectedItem().toString().equals(fac.AmendmentEventName))
                        for (fac2 in FacilityDataModel.getInstance().tblAmendmentOrderTracking) {
                            if (fac2.EventTypeID.equals(fac.AmendmentEventID)){

                                item.EventID=fac2.EventID
                            }


                        }


                }
                for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {
                    if (eventsDropDown.getSelectedItem().toString().equals(fac.AmendmentEventName))

                        for (fac2 in FacilityDataModel.getInstance().tblAmendmentOrderTracking) {
                            if (fac2.EventTypeID.equals(fac.AmendmentEventID)){

                                item.AOID=fac2.AOID
                            }


                        }

                }


                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(item)
                //  BuildProgramsList()

                addTheLatestRowOfNewEventTableView()


        })
        submitNewAOTButton.setOnClickListener(View.OnClickListener {


            var item = FacilityDataModel.TblAmendmentOrderTracking()
                for (fac in FacilityDataModel.getInstance().tblAmendmentOrderTracking) {
                    if (employeeDropDown.getSelectedItem().toString().equals(fac.AOTEmployee))

                        item.AOID = fac.AOID

                }

                        item.AOTEmployee=employeeDropDown.getSelectedItem().toString()
            for (fac in TypeTablesModel.getInstance().tblAmendmentOrderTrackingSubReasonsType) {
                if (reasonDropDown.getSelectedItem().toString().equals(fac.AmendmentSubReasonName))

                   item.ReasonID = fac.AmendmentSubReasonID

            }



            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAmendmentOrderTrackingData + "&facId=540554&aoId=${item.AOID.toString()}&employeeId=E654117&reasonId=${item.ReasonID.toString()}&insertBy=E110997&insertDate=2015-06-15T12:38:01.087-07:00&updateBy=SumA&updateDate=2018-06-29T00:02:15.573-07:00&active=1",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            Log.v("RESPONSE",response.toString())
                            amendmentLoadingView.visibility = View.GONE

                            AOCardView.visibility=View.GONE
                            alphaBackgroundForAOT_Dialogs.visibility = View.GONE
                            FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(item)


                            addTheLatestRowOfAmendmentAndTrackingTableView()
                            alt_AOT_TableRow(2)


                            var itemOrgArray = FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking
                            var itemArray = FacilityDataModel.getInstance().tblAmendmentOrderTracking
                            if (itemOrgArray.size!=itemArray.size){

                                MarkChangeWasDone()
                            }else {

                                for (itemAr in itemArray) {
                                    for (itemOrgAr in itemOrgArray) {

                                        if (itemAr.AOID != itemOrgAr.AOID || itemAr.EventTypeID != itemOrgAr.EventTypeID
                                                || itemAr.EventID != itemOrgAr.EventID || itemAr.ReasonID != itemOrgAr.ReasonID) {
                                            MarkChangeWasDone()
                                            Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                                        } else {

                                            Toast.makeText(context, "no changes found", Toast.LENGTH_SHORT).show()

                                        }

                                    }
                                }
                            }


                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading personnal record")
                amendmentLoadingView.visibility = View.GONE

            }))


                //  BuildProgramsList()



        })

        alt_AOT_TableRow(2)
        altEventTableRow(2)

    }
    fun prepareSpinnerReasonTypes() {

        for (fac in TypeTablesModel.getInstance().tblAmendmentOrderTrackingSubReasonsType) {


            reasonsTypesArray.add(fac.AmendmentSubReasonName)
        }
        var reasonAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasonsTypesArray)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonDropDown.adapter = reasonAdapter


    }
    fun prepareSpinnerEventsTypes() {

        for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {


            eventsTypesArray.add(fac.AmendmentEventName)
        }
        var reasonAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, eventsTypesArray)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventsDropDown.adapter = reasonAdapter


    }
    fun prepareSpinnerEmployees() {

        var employeesArray=ArrayList<String>()

        for (fac in FacilityDataModel.getInstance().tblAmendmentOrderTracking) {


            employeesArray.add(fac.AOTEmployee)
        }
        var employeeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, employeesArray)
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeDropDown.adapter = employeeAdapter


    }
//    fun prepareSpinnerEmployeeNames() {
//
//        for (fac in TypeTablesModel.getInstance().tblAmendmentOrderTrackingSubReasonsType) {
//
//
//            reasonsTypesArray.add(fac.AmendmentSubReasonName)
//        }
//        var reasonAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasonsTypesArray)
//        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        reasonDropDown.adapter = reasonAdapter
//
//
//    }


    fun fillAmendmentOrdersAndTrackingTableLayout() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

                FacilityDataModel.getInstance().tblAmendmentOrderTracking.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).AOID
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).AOTEmployee
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                for (fac in TypeTablesModel.getInstance().tblAmendmentOrderTrackingSubReasonsType) {
                    if (get(it).ReasonID.equals(fac.AmendmentSubReasonID))

                        textView.text =fac.AmendmentSubReasonName
                }
                tableRow.addView(textView)



                AmendmentOrdersAndTrackingTableLayout.addView(tableRow)
            }
        }
    }

    fun scopeOfServiceChangesWatcher(){

        if (FragmentARRAVScopeOfService.dataChanged) {

            val builder = AlertDialog.Builder(context)

            // Set the alert dialog title
            builder.setTitle("Changes made confirmation")

            // Display a message on alert dialog
            builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES") { dialog, which ->




                Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE", response.toString())

                                Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                    FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                        LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                        LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank())LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                        FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank())FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                        DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank())DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                        NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank())NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                        NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank())NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                        FragmentARRAVScopeOfService.dataChanged =false

                                    }

                                }

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()


                }))


            }





            // Display a negative button on alert dialog
            builder.setNegativeButton("No") { dialog, which ->
                FragmentARRAVScopeOfService.dataChanged =false

            }




            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            // Display the alert dialog on app interface
            dialog.show()

        }

    }


    fun addTheLatestRowOfAmendmentAndTrackingTableView() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2

        FacilityDataModel.getInstance().tblAmendmentOrderTracking[FacilityDataModel.getInstance().tblAmendmentOrderTracking.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = AOID
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = AOTEmployee
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            for (fac in TypeTablesModel.getInstance().tblAmendmentOrderTrackingSubReasonsType) {
                if (ReasonID.equals(fac.AmendmentSubReasonID))

                    textView.text =fac.AmendmentSubReasonName
            }
            tableRow.addView(textView)


            AmendmentOrdersAndTrackingTableLayout.addView(tableRow)

        }
        alt_AOT_TableRow(2)
    }

    fun alt_AOT_TableRow(alt_row : Int) {
        var childViewCount = AmendmentOrdersAndTrackingTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= AmendmentOrdersAndTrackingTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                if (i % alt_row != 0) {
                    tv.setBackground(getResources().getDrawable(
                            R.drawable.alt_row_color));
                } else {
                    tv.setBackground(getResources().getDrawable(
                            R.drawable.row_color));
                }

            }

        }
    }

    fun fillNewEventTableLayout() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

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

                FacilityDataModel.getInstance().tblAmendmentOrderTracking.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).AOID
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).EventID
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
                TableRow.LayoutParams()
                for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {
                    if (get(it).EventTypeID.equals(fac.AmendmentEventID))

                        textView.text =fac.AmendmentEventName
                }
                tableRow.addView(textView)



                newEventTableLayout.addView(tableRow)
            }
        }
    }

    fun addTheLatestRowOfNewEventTableView() {
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

        FacilityDataModel.getInstance().tblAmendmentOrderTracking[FacilityDataModel.getInstance().tblAmendmentOrderTracking.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = AOID
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = EventID
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
            TableRow.LayoutParams()
            for (fac in TypeTablesModel.getInstance().AmendmentOrderTrackingEventsType) {
                if (EventTypeID.equals(fac.AmendmentEventID))

                    textView.text =fac.AmendmentEventName
            }

            tableRow.addView(textView)


            newEventTableLayout.addView(tableRow)

        }

        altEventTableRow(2)
    }

    fun altEventTableRow(alt_row : Int) {
        var childViewCount = newEventTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= newEventTableLayout.getChildAt(i) as TableRow;



            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
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
         * @return A new instance of fragment FragmentAARAmOrderTracking.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVAmOrderTracking {
            val fragment = FragmentARRAVAmOrderTracking()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
