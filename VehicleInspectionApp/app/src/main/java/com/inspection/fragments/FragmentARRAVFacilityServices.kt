package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
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
import com.inspection.Utils.Constants.UpdateFacilityServicesData
import com.inspection.Utils.Utility.showValidationAlertDialog
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_arrav_facility_services.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityServices.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityServices.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityServices : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    var servicesArray= ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        scopeOfServiceChangesWatcher()


        exitFC_ServicesDialogeBtnId.setOnClickListener({
            facilityServicesCard.visibility=View.GONE
            alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
        })

        edit_exitFC_ServicesDialogeBtnId.setOnClickListener({
            editFacilityServicesCard.visibility=View.GONE
            alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
        })

        showNewserviceDialogueButton.setOnClickListener(View.OnClickListener {
            comments_editTextVal.setText("")
            fceffective_date_textviewVal.setText("SELECT DATE")
            fcexpiration_date_textviewVal.setText("SELECT DATE")
            fc_services_textviewVal.setSelection(0)
            comments_editTextVal.setError(null)
            fceffective_date_textviewVal.setError(null)
            facilityServicesCard.visibility=View.VISIBLE
            alphaBackgroundForFC_ServicesDialogs.visibility = View.VISIBLE
        })


        fcexpiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                fcexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        fceffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                fceffective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        edit_fcexpiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_fcexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        edit_fceffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_fceffective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        servicesArray.add("Select Service")

        for (fac in TypeTablesModel.getInstance().ServicesType) {


            servicesArray.add(fac.ServiceTypeName)
        }

        var servicesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, servicesArray)
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fc_services_textviewVal.adapter = servicesAdapter
        edit_fc_services_textviewVal.adapter = servicesAdapter

        submitNewserviceButton.setOnClickListener {

            if (validateInputs()){
                progressBarText.text = "Saving ..."
                FC_LoadingView.visibility = View.VISIBLE



                var item = TblFacilityServices()
                for (fac in TypeTablesModel.getInstance().ServicesType) {
                    if (fc_services_textviewVal.getSelectedItem().toString().equals(fac.ServiceTypeName))

                        item.ServiceID =fac.ServiceTypeID
                }
                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.effDate = if (fceffective_date_textviewVal.text.equals("SELECT DATE")) "" else fceffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.expDate = if (fcexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else fcexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.Comments=comments_editTextVal.text.toString()

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo +"&clubCode="+FacilityDataModel.getInstance().clubCode+"&facilityServicesId=&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=E110997&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat(),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode&gt;0&",false)) {
                                    item.FacilityServicesID= response.toString().substring(response.toString().indexOf(";FacilityServicesID")+23,response.toString().indexOf("&lt;/FacilityServicesID"))
                                    FacilityDataModel.getInstance().tblFacilityServices.add(item)
                                    Utility.showSubmitAlertDialog(activity, true, "Facility Services")
                                    fillPortalTrackingTableView()
                                    altFacServiceTableRow(2)
                                    HasChangedModel.getInstance().groupSoSFacilityServices[0].SoSFacilityServices=true
                                    HasChangedModel.getInstance().changeDoneForSoSFacilityServices()
                                    IndicatorsDataModel.getInstance().validateSOSFacilityServices()
                                    if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServices) (activity as FormsActivity).facilityServicesButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).facilityServicesButton.setTextColor(Color.parseColor("#A42600"))
                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+errorMessage+" )")
                                }
                                facilityServicesCard.visibility = View.GONE
                                FC_LoadingView.visibility = View.GONE
                                progressBarText.text = "Loading ..."
                                alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+it.message+" )")
                        facilityServicesCard.visibility = View.GONE
                        FC_LoadingView.visibility = View.GONE
                        progressBarText.text = "Loading ..."
                        alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
                }))
            }else {
                showValidationAlertDialog(activity,"Please fill all the required fields")
            }
        }
        fillPortalTrackingTableView();
        altFacServiceTableRow(2)
    }

    fun fillPortalTrackingTableView() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (aarPortalTrackingTableLayout.childCount > 1) {
            for (i in aarPortalTrackingTableLayout.childCount - 1 downTo 1) {
                aarPortalTrackingTableLayout.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 3F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.5F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.5F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 3F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT


        FacilityDataModel.getInstance().tblFacilityServices.apply {
            (0 until size).forEach {
                if (!get(it).FacilityServicesID.equals("-1")) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    var textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 18f
                    textView1.minimumHeight = 30

                    for (fac in TypeTablesModel.getInstance().ServicesType) {
                        if (get(it).ServiceID.equals(fac.ServiceTypeID))

                            textView1.text = fac.ServiceTypeName
                    }
                    tableRow.addView(textView1)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    if (get(it).effDate.isNullOrBlank()) {
                        textView.text = ""
                    } else {
                        try {
                            textView.text = get(it).effDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {

                            textView.text = get(it).effDate

                        }
                    }
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    TableRow.LayoutParams()
                    if (get(it).expDate.isNullOrBlank()) {
                        textView.text = ""
                    } else {
                        try {
                            textView.text = get(it).expDate.apiToAppFormatMMDDYYYY()
                        } catch (e: Exception) {

                            textView.text = get(it).expDate

                        }
                    }
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    textView.text = get(it).Comments
                    tableRow.addView(textView)


                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam4
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 18f
                    updateButton.minimumHeight = 30
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)

                    aarPortalTrackingTableLayout.addView(tableRow)
                    updateButton.setOnClickListener {
                        var currentTableRowIndex = aarPortalTrackingTableLayout.indexOfChild(tableRow)
                        var currentfacilityDataModelIndex = currentTableRowIndex - 1

                        edit_comments_editTextVal.setText(FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].Comments)
                        edit_fceffective_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate.equals("")) "SELECT DATE" else FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate.apiToAppFormatMMDDYYYY())
                        edit_fcexpiration_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate.equals("")) "SELECT DATE" else FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate.apiToAppFormatMMDDYYYY())


                        var i = servicesArray.indexOf(textView1.text)
                        edit_fc_services_textviewVal.setSelection(i)
                        editFacilityServicesCard.visibility = View.VISIBLE
                        alphaBackgroundForFC_ServicesDialogs.visibility = View.VISIBLE
                        edit_submitNewserviceButton.setOnClickListener {
                            if (edit_validateInputs()) {
                                editFacilityServicesCard.visibility = View.GONE
                                progressBarText.text = "Saving ..."
                                FC_LoadingView.visibility = View.VISIBLE
                                var item = TblFacilityServices()
                                for (fac in TypeTablesModel.getInstance().ServicesType) {
                                    if (edit_fc_services_textviewVal.getSelectedItem().toString().equals(fac.ServiceTypeName))
                                        item.ServiceID = fac.ServiceTypeID
                                }
                                item.effDate = if (edit_fceffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_fceffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                item.expDate = if (edit_fcexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_fcexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                                item.Comments = edit_comments_editTextVal.text.toString()
                                item.FacilityServicesID = FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].FacilityServicesID

                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityServicesData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&facilityServicesId=${item.FacilityServicesID}&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=E110997&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=SumA&updateDate=" + Date().toApiSubmitFormat(),
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread {
                                                if (response.toString().contains("returnCode&gt;0&", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "Facility Services")
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].Comments = item.Comments
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].effDate = item.effDate
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].expDate = item.expDate
                                                    FacilityDataModel.getInstance().tblFacilityServices[currentfacilityDataModelIndex].ServiceID = item.ServiceID
                                                    fillPortalTrackingTableView()
                                                    altFacServiceTableRow(2)
                                                    HasChangedModel.getInstance().groupSoSFacilityServices[0].SoSFacilityServices = true
                                                    HasChangedModel.getInstance().changeDoneForSoSFacilityServices()
                                                    IndicatorsDataModel.getInstance().validateSOSFacilityServices()
                                                    if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServices) (activity as FormsActivity).facilityServicesButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).facilityServicesButton.setTextColor(Color.parseColor("#A42600"))
                                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+errorMessage+" )")
                                                }
                                                FC_LoadingView.visibility = View.GONE
                                                progressBarText.text = "Loading ..."
                                                alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
                                            }
                                        }, Response.ErrorListener {
                                    Utility.showSubmitAlertDialog(activity, false, "Facility Services (Error: "+it.message+" )")
                                    editFacilityServicesCard.visibility = View.GONE
                                    FC_LoadingView.visibility = View.GONE
                                    progressBarText.text = "Loading ..."
                                    alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
                                }))
                            } else {
                                showValidationAlertDialog(activity, "Please fill all the required fields")
                            }
                        }
                    }
                }
            }
        }
    }

//    fun addTheLatestRowOfPortalAdmin() {
//        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 1F
//        rowLayoutParam.column = 0
//
//        val rowLayoutParam1 = TableRow.LayoutParams()
//        rowLayoutParam1.weight = 1F
//        rowLayoutParam1.column = 1
//
//        val rowLayoutParam2 = TableRow.LayoutParams()
//        rowLayoutParam2.weight = 1F
//        rowLayoutParam2.column = 2
//
//        val rowLayoutParam3 = TableRow.LayoutParams()
//        rowLayoutParam3.weight = 1F
//        rowLayoutParam3.column = 3
//
//        val rowLayoutParam4 = TableRow.LayoutParams()
//        rowLayoutParam4.weight = 1F
//        rowLayoutParam4.column = 4
//        FacilityDataModel.getInstance().tblFacilityServices[FacilityDataModel.getInstance().tblFacilityServices.size - 1].apply {
//
//
//            var tableRow = TableRow(context)
//
//            var textView = TextView(context)
//            textView.layoutParams = rowLayoutParam
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            for (fac in TypeTablesModel.getInstance().ServicesType) {
//                if (ServiceID.equals(fac.ServiceTypeID))
//
//                    textView.text =fac.ServiceTypeName
//            }
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam1
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            if (effDate.isNullOrBlank()){
//                textView.text =""
//            }else {
//                try {
//                    textView.text = effDate.apiToAppFormatMMDDYYYY()
//                } catch (e: Exception) {
//
//                    textView.text = effDate
//
//                }
//            }
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam2
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            TableRow.LayoutParams()
//            if (expDate.isNullOrBlank()){
//                textView.text =""
//            }else {
//                try {
//                    textView.text = expDate.apiToAppFormatMMDDYYYY()
//                } catch (e: Exception) {
//
//                    textView.text = expDate
//
//                }
//            }
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam3
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            textView.text = Comments
//            tableRow.addView(textView)
//
//
//            aarPortalTrackingTableLayout.addView(tableRow)
//
//        }
//
//        altFacServiceTableRow(2)
//    }

    fun altFacServiceTableRow(alt_row : Int) {
        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }
    fun scopeOfServiceChangesWatcher() {

//        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {
//
//            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {
//
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
//                        FC_LoadingView.visibility = View.VISIBLE
//
//
//                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread(Runnable {
//                                        Log.v("RESPONSE", response.toString())
//                                        FC_LoadingView.visibility = View.GONE
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
//                            FC_LoadingView.visibility = View.GONE
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
//                        FC_LoadingView.visibility = View.GONE
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

    fun checkMarkChangesWasDone() {
//        val dateFormat1 = SimpleDateFormat("dd MMM yyyy")
//
//        var itemOrgArray = FacilityDataModelOrg.getInstance().tblFacilityServices
//        var itemArray = FacilityDataModel.getInstance().tblFacilityServices
//        if (itemOrgArray.size == itemArray.size) {
//            for (i in 0 until itemOrgArray.size){
//                if (
//                        itemOrgArray[i].expDate.isNullOrBlank()&&!itemArray[i].expDate.isNullOrBlank()||itemOrgArray[i].effDate.isNullOrBlank()&&!itemArray[i].effDate.isNullOrBlank()
//                ) {
//
//                    MarkChangeWasDone()
//                }
//                else
//                    if (
//                            itemOrgArray[i].expDate.isNullOrBlank()&&itemArray[i].expDate.isNullOrBlank()||itemOrgArray[i].effDate.isNullOrBlank()&&itemArray[i].effDate.isNullOrBlank()
//                    ) {
//                        if (
//                                itemOrgArray[i].Comments != itemArray[i].Comments ||
//                                itemOrgArray[i].ServiceID != itemArray[i].ServiceID) {
//                            MarkChangeWasDone()
////                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
//                            Log.v("checkkk", itemOrgArray[i].Comments + "=="+ itemArray[i].Comments)
//                            Log.v("checkkk", itemOrgArray[i].expDate + "=="+ itemArray[i].expDate)
//                            Log.v("checkkk", itemOrgArray[i].effDate + "=="+ itemArray[i].effDate)
//                            Log.v("checkkk", itemOrgArray[i].ServiceID + "=="+ itemArray[i].ServiceID)
//
//                        }
//                    }
//                    else
//                        if (itemOrgArray[i].Comments != itemArray[i].Comments || dateFormat1.parse(itemOrgArray[i].expDate.apiToAppFormat()) != dateFormat1.parse(itemArray[i].expDate.apiToAppFormat()) ||
//                                dateFormat1.parse(itemOrgArray[i].effDate.apiToAppFormat()) != dateFormat1.parse(itemArray[i].effDate.apiToAppFormat()) ||
//                                itemOrgArray[i].ServiceID != itemArray[i].ServiceID) {
//                            MarkChangeWasDone()
////                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
//                            Log.v("checkkk", itemOrgArray[i].Comments + "=="+ itemArray[i].Comments)
//                            Log.v("checkkk", itemOrgArray[i].expDate + "=="+ itemArray[i].expDate)
//                            Log.v("checkkk", itemOrgArray[i].effDate + "=="+ itemArray[i].effDate)
//                            Log.v("checkkk", itemOrgArray[i].ServiceID + "=="+ itemArray[i].ServiceID)
//
//                        }
//            }
//        }else{
//            MarkChangeWasDone()
//            Log.v("checkkk", "2ndddd")
//
//        }
    }


    fun validateInputs() : Boolean {

        var facServicesValide= TblFacilityServices().isInputsValid
        facServicesValide = true

        fceffective_date_textviewVal.setError(null)
        fcServiceSpinner.setError(null)
        comments_editTextVal.setError(null)
        if(fceffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            facServicesValide = false
            fceffective_date_textviewVal.setError("Required Field")
        }


        if(fc_services_textviewVal.selectedItemPosition.equals(0)) {
            facServicesValide = false
            fcServiceSpinner.setError("Required Field")
        }

        if(comments_editTextVal.text.isNullOrEmpty()) {
            facServicesValide = false
            comments_editTextVal.setError("Required Field")
        }

        return facServicesValide
    }

    fun edit_validateInputs() : Boolean {

        var facServicesValide= TblFacilityServices().isInputsValid
        facServicesValide = true

        edit_fceffective_date_textviewVal.setError(null)
        edit_fcServiceSpinner.setError(null)
        edit_comments_editTextVal.setError(null)
        if(edit_fceffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            facServicesValide = false
            edit_fceffective_date_textviewVal.setError("Required Field")
        }


        if(edit_fc_services_textviewVal.selectedItemPosition.equals(0)) {
            facServicesValide = false
            edit_fcServiceSpinner.setError("Required Field")
        }

        if(edit_comments_editTextVal.text.isNullOrEmpty()) {
            facServicesValide = false
            edit_comments_editTextVal.setError("Required Field")
        }

        return facServicesValide
    }


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

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FacilityGeneralInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVFacilityServices {
            val fragment = FragmentARRAVFacilityServices()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
