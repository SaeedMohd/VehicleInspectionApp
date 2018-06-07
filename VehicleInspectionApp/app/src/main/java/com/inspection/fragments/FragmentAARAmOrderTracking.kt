package com.inspection.fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.toast
import com.inspection.model.FacilityDataModel
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
        addNewEventButton.setOnClickListener(View.OnClickListener {

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
        addNewAarButton.setOnClickListener(View.OnClickListener {

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


                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(item)
                //  BuildProgramsList()

            addTheLatestRowOfAmendmentAndTrackingTableView()


        })

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
