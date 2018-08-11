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

import com.inspection.R
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.appToApiFormat
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.fragment_array_repair_shop_portal_addendum.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.LinearLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.Utils.Constants.UpdateAARPortalAdminData
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.model.FacilityDataModelOrg


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVRepairShopPortalAddendum.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVRepairShopPortalAddendum.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVRepairShopPortalAddendum : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    var rowIndex=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_array_repair_shop_portal_addendum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scopeOfServiceChangesWatcher()
        exitRSPDialogeBtnId.setOnClickListener({

            fillPortalTrackingTableView()
            altLocationTableRow(2)
            addNewAAR_PortalTrackingCard.visibility=View.GONE
            alphaBackgroundForRSPDialogs.visibility = View.GONE

        })

        edit_exitRSPDialogeBtnId.setOnClickListener({

            fillPortalTrackingTableView()
            altLocationTableRow(2)
            edit_AAR_PortalTrackingEntryCard.visibility=View.GONE
            alphaBackgroundForRSPDialogs.visibility = View.GONE


        })

        addNewAarButton.setOnClickListener(View.OnClickListener {

            numberOfCardsReaderEditText.setText("")
            numberOfUnacknowledgedRecordsEditText.setText("")
            numberOfInProgressTwoIns.setText("")
            numberOfInProgressWalkIns.setText("")
            startDateButton.setText("SELECT DATE")
            endDateButton.setText("SELECT DATE")
            addendumSignedDateButton.setText("SELECT DATE")
            inspectionDateButton.setText("SELECT DATE")


            numberOfCardsReaderEditText.setError(null)
            numberOfUnacknowledgedRecordsEditText.setError(null)
            numberOfInProgressTwoIns.setError(null)
            numberOfInProgressWalkIns.setError(null)
            startDateButton.setError(null)
            addendumSignedDateButton.setError(null)
            inspectionDateButton.setError(null)

            addNewAAR_PortalTrackingCard.visibility=View.VISIBLE
            alphaBackgroundForRSPDialogs.visibility = View.VISIBLE

            for (i in 0 until mainViewLinearId.childCount) {
                val child = mainViewLinearId.getChildAt(i)
                child.isEnabled = false
            }

            var childViewCount = aarPortalTrackingTableLayout.getChildCount();

            for ( i in 1..childViewCount-1) {
                var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

                for (j in 0..row.getChildCount()-1) {

                    var tv : TextView= row.getChildAt(j) as TextView
                    tv.isEnabled=false
                }

            }



        })


        startDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                startDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        edit_startDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_startDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        endDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                endDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        edit_endDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_endDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        addendumSignedDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                addendumSignedDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        edit_addendumSignedDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_addendumSignedDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        inspectionDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                inspectionDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        edit_inspectionDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_inspectionDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        submitNewAAR_PortalTracking.setOnClickListener {

            if (validateInputs()) {
                RSP_LoadingView.visibility = View.VISIBLE

                var startDate = if (startDateButton.text.equals("SELECT DATE")) "" else startDateButton.text.toString()
                var endDate = if (endDateButton.text.equals("SELECT DATE")) "" else endDateButton.text.toString()
                var signedDate = if (addendumSignedDateButton.text.equals("SELECT DATE")) "" else addendumSignedDateButton.text.toString()

                val date = inspectionDateButton.text
                val isLoggedInRsp = loggedIntoRspButton.isChecked
                val numberOfUnacknowledgedRecords = numberOfUnacknowledgedRecordsEditText.text.toString().toInt()
                val numberOfInProgressTwoInsvalue = numberOfInProgressTwoIns.text.toString().toInt()
                val numberOfInProgressWalkInsValue = numberOfInProgressWalkIns.text.toString().toInt()
                var portalTrackingentry = FacilityDataModel.TblAARPortalAdmin()
                portalTrackingentry.startDate = startDateButton.text.toString()
                portalTrackingentry.PortalInspectionDate = "" + date
                portalTrackingentry.LoggedIntoPortal = "" + isLoggedInRsp
                portalTrackingentry.InProgressTows = "" + numberOfInProgressTwoInsvalue
                portalTrackingentry.InProgressWalkIns = "" + numberOfInProgressWalkInsValue
                portalTrackingentry.NumberUnacknowledgedTows = "" + numberOfUnacknowledgedRecords
                portalTrackingentry.CardReaders = numberOfCardsReaderEditText.text.toString()
                portalTrackingentry.AddendumSigned = addendumSignedDateButton.text.toString()



                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAARPortalAdminData + "&startDate=${startDate.toString()}&endDate=${endDate.toString()}&addendumSigned=${signedDate.toString()}&" +
                        "cardReaders=${numberOfCardsReaderEditText.text.toString()}&insertBy=E642707&insertDate=2015-07-31T11:53:02.190&updateBy=SumA&updateDate=2015-07-31T11:53:02.190&active=1",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE_LOOK",response.toString())
                                RSP_LoadingView.visibility = View.GONE


                                alphaBackgroundForRSPDialogs.visibility = View.GONE
                                addNewAAR_PortalTrackingCard.visibility = View.GONE
                                FacilityDataModel.getInstance().tblAARPortalAdmin.add(portalTrackingentry)
                                fillPortalTrackingTableView()
                                altLocationTableRow(2)




                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    RSP_LoadingView.visibility = View.GONE

                }))
            }else
                Toast.makeText(context,"please fill all required field",Toast.LENGTH_SHORT).show()



        }

        //  fillData()
        fillPortalTrackingTableView()
        altLocationTableRow(2)

    }

    fun fillData(){
        FacilityDataModel.getInstance().tblAARPortalAdmin[0].apply {
            // startDateButton.text = startDate
//            endDateButton.text = PortalInspectionDate
            //   addendumSignedDateButton.text = AddendumSigned
            //   numberOfCardsReaderEditText.setText(CardReaders)
//            inspectionDateButton.text = PortalInspectionDate
//            numberOfUnacknowledgedRecordsEditText.setText(NumberUnacknowledgedTows)
//            numberOfInProgressTwoIns.setText(InProgressTows)
//            numberOfInProgressWalkIns.setText(InProgressWalkIns)

        }
    }

    fun checkMarkChangesWasDone() {
        val dateFormat1 = SimpleDateFormat("dd MMM yyyy")

        var itemOrgArray = FacilityDataModelOrg.getInstance().tblAARPortalAdmin
        var itemArray = FacilityDataModel.getInstance().tblAARPortalAdmin
        if (itemOrgArray.size == itemArray.size) {
            for (i in 0 until itemOrgArray.size){
                var startDate= try {
                    dateFormat1.parse(itemArray[i].startDate.apiToAppFormat())
                } catch (e: Exception) {
                    dateFormat1.parse(itemArray[i].startDate)

                }
                var assignedDate= try {
                    dateFormat1.parse(itemArray[i].AddendumSigned.apiToAppFormat())
                } catch (e: Exception) {
                    dateFormat1.parse(itemArray[i].AddendumSigned)

                }
                var portalDate= try {
                    dateFormat1.parse(itemArray[i].PortalInspectionDate.apiToAppFormat())
                } catch (e: Exception) {
                    dateFormat1.parse(itemArray[i].PortalInspectionDate)

                }

                if (
                        itemOrgArray[i].PortalInspectionDate.isNullOrBlank()&&!itemArray[i].PortalInspectionDate.isNullOrBlank()||
                        itemOrgArray[i].startDate.isNullOrBlank()&&!itemArray[i].startDate.isNullOrBlank()||
                        itemOrgArray[i].AddendumSigned.isNullOrBlank()&&!itemArray[i].AddendumSigned.isNullOrBlank()


                ) {

                    MarkChangeWasDone()
                }
                else
                    if (
                            itemOrgArray[i].PortalInspectionDate.isNullOrBlank()&&itemArray[i].PortalInspectionDate.isNullOrBlank()||
                            itemOrgArray[i].startDate.isNullOrBlank()&&itemArray[i].startDate.isNullOrBlank()||
                            itemOrgArray[i].AddendumSigned.isNullOrBlank()&&itemArray[i].AddendumSigned.isNullOrBlank()

                    ) {
                        if (

                                itemOrgArray[i].LoggedIntoPortal != itemArray[i].LoggedIntoPortal ||
                                itemOrgArray[i].InProgressTows != itemArray[i].InProgressTows ||
                                itemOrgArray[i].InProgressWalkIns != itemArray[i].InProgressWalkIns ||
                                itemOrgArray[i].NumberUnacknowledgedTows != itemArray[i].NumberUnacknowledgedTows
                        ) {


                            MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                            Log.v("checkkk", itemOrgArray[i].LoggedIntoPortal + "=="+ itemArray[i].LoggedIntoPortal)
                            Log.v("checkkk", itemOrgArray[i].InProgressTows + "=="+ itemArray[i].InProgressTows)
                            Log.v("checkkk", itemOrgArray[i].InProgressWalkIns + "=="+ itemArray[i].InProgressWalkIns)
                            Log.v("checkkk", itemOrgArray[i].NumberUnacknowledgedTows + "=="+ itemArray[i].NumberUnacknowledgedTows)

                        }
                    }

                    else
                        if (

                                itemOrgArray[i].LoggedIntoPortal != itemArray[i].LoggedIntoPortal ||
                                itemOrgArray[i].InProgressTows != itemArray[i].InProgressTows ||
                                itemOrgArray[i].InProgressWalkIns != itemArray[i].InProgressWalkIns ||
                                itemOrgArray[i].NumberUnacknowledgedTows != itemArray[i].NumberUnacknowledgedTows ||
                                dateFormat1.parse(itemOrgArray[i].AddendumSigned.apiToAppFormat()) != assignedDate ||
                                dateFormat1.parse(itemOrgArray[i].startDate.apiToAppFormat()) != startDate ||
                                dateFormat1.parse(itemOrgArray[i].PortalInspectionDate.apiToAppFormat()) != portalDate)

                {



                            MarkChangeWasDone()
//                             Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                    Log.v("checkkk", itemOrgArray[i].LoggedIntoPortal + "=="+ itemArray[i].LoggedIntoPortal)
                    Log.v("checkkk", itemOrgArray[i].InProgressTows + "=="+ itemArray[i].InProgressTows)
                    Log.v("checkkk", itemOrgArray[i].InProgressWalkIns + "=="+ itemArray[i].InProgressWalkIns)
                    Log.v("checkkk", itemOrgArray[i].NumberUnacknowledgedTows + "=="+ itemArray[i].NumberUnacknowledgedTows)
                    Log.v("checkkk", itemOrgArray[i].AddendumSigned + "=="+ itemArray[i].AddendumSigned)
                    Log.v("checkkk", itemOrgArray[i].startDate + "=="+ itemArray[i].startDate)
                    Log.v("checkkk", itemOrgArray[i].PortalInspectionDate + "=="+ itemArray[i].PortalInspectionDate)


                }
            }
        }else{
            MarkChangeWasDone()
            Log.v("checkkk", "2ndddd")

        }
    }

    fun validateInputs() : Boolean {

        var portalValide= FacilityDataModel.TblAARPortalAdmin().isInputsValid
        portalValide = true

        startDateButton.setError(null)
        endDateButton.setError(null)
        addendumSignedDateButton.setError(null)
        numberOfCardsReaderEditText.setError(null)
        inspectionDateButton.setError(null)
        loggedIntoRspButton.setError(null)
        numberOfUnacknowledgedRecordsEditText.setError(null)
        numberOfInProgressTwoIns.setError(null)
        numberOfInProgressWalkIns.setError(null)

        if (!startDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {

            if (addendumSignedDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
                portalValide = false
                addendumSignedDateButton.setError("Required Field")
            }


            if (numberOfCardsReaderEditText.text.toString().isNullOrEmpty()) {
                portalValide = false
                numberOfCardsReaderEditText.setError("Required Field")
            }

        }




        if (inspectionDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
            portalValide = false
            inspectionDateButton.setError("Required Field")
        }


        if (numberOfUnacknowledgedRecordsEditText.text.toString().isNullOrEmpty()) {
            portalValide = false
            numberOfUnacknowledgedRecordsEditText.setError("Required Field")
        }

        if (numberOfInProgressTwoIns.text.toString().isNullOrEmpty()) {
            portalValide = false
            numberOfInProgressTwoIns.setError("Required Field")
        }

        if (numberOfInProgressWalkIns.text.toString().isNullOrEmpty()) {
            portalValide = false
            numberOfInProgressWalkIns.setError("Required Field")
        }


        return portalValide
    }
    fun validateInputsForUpdate() : Boolean {
        var isInputsValid = true

        edit_startDateButton.setError(null)
        edit_endDateButton.setError(null)
        edit_addendumSignedDateButton.setError(null)
        edit_numberOfCardsReaderEditText.setError(null)
        edit_inspectionDateButton.setError(null)
        edit_loggedIntoRspButton.setError(null)
        edit_numberOfUnacknowledgedRecordsEditText.setError(null)
        edit_numberOfInProgressTwoIns.setError(null)
        edit_numberOfInProgressWalkIns.setError(null)

        if (!edit_startDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {

            if (edit_addendumSignedDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
                isInputsValid = false
                edit_addendumSignedDateButton.setError("Required Field")
            }


            if (edit_numberOfCardsReaderEditText.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                edit_numberOfCardsReaderEditText.setError("Required Field")
            }

        }




        if (edit_inspectionDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid = false
            edit_inspectionDateButton.setError("Required Field")
        }


        if (edit_numberOfUnacknowledgedRecordsEditText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            edit_numberOfUnacknowledgedRecordsEditText.setError("Required Field")
        }

        if (edit_numberOfInProgressTwoIns.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            edit_numberOfInProgressTwoIns.setError("Required Field")
        }

        if (edit_numberOfInProgressWalkIns.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            edit_numberOfInProgressWalkIns.setError("Required Field")
        }






        return isInputsValid
    }

    fun fillPortalTrackingTableView(){

        mainViewLinearId.isEnabled=true

        //val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0



        if (aarPortalTrackingTableLayout.childCount>1) {
            for (i in aarPortalTrackingTableLayout.childCount - 1 downTo 1) {
                aarPortalTrackingTableLayout.removeViewAt(i)
            }
        }

        for (i in 0 until mainViewLinearId.childCount) {
            val child = mainViewLinearId.getChildAt(i)
            child.isEnabled = true
        }

        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=true
            }

        }



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


        FacilityDataModel.getInstance().tblAARPortalAdmin.apply {



            (0 until size).forEach {
                val tableRow = TableRow(context)

                val textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                try {
                    textView.text = get(it).PortalInspectionDate.apiToAppFormat()
                } catch (e: Exception) {
                    textView.text = get(it).PortalInspectionDate

                }
                tableRow.addView(textView)

                val textView1 = TextView(context)
                textView1.layoutParams = rowLayoutParam1
                textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView1.text = get(it).LoggedIntoPortal
                tableRow.addView(textView1)

               val textView2 = TextView(context)
                textView2.layoutParams = rowLayoutParam2
                textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView2.text = get(it).NumberUnacknowledgedTows
                tableRow.addView(textView2)

                val textView3 = TextView(context)
                textView3.layoutParams = rowLayoutParam3
                textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView3.text = get(it).InProgressTows
                tableRow.addView(textView3)

               val textView4 = TextView(context)
                textView4.layoutParams = rowLayoutParam4
                textView4.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView4.text = get(it).InProgressWalkIns
                tableRow.addView(textView4)

                val updateButton = Button(context)
                updateButton.layoutParams = rowLayoutParam5
                updateButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
                updateButton.text = "update"
                tableRow.addView(updateButton)


                updateButton.setOnClickListener(View.OnClickListener {
                    rowIndex = aarPortalTrackingTableLayout.indexOfChild(tableRow)


                    edit_numberOfUnacknowledgedRecordsEditText.setText(textView2.text)
                    edit_numberOfInProgressTwoIns.setText(textView3.text)
                    edit_numberOfInProgressWalkIns.setText(textView4.text)
                    edit_inspectionDateButton.setText(textView.text)
                    try {
                        edit_startDateButton.setText(if (get(rowIndex-1).startDate.isNullOrBlank()) "" else get(rowIndex-1).startDate.apiToAppFormat())
                    } catch (e: Exception) {
                        edit_startDateButton.setText(if (get(rowIndex-1).startDate.isNullOrBlank()) "" else get(rowIndex-1).startDate)
                    }
                    try {
                        edit_addendumSignedDateButton.setText(if (get(rowIndex-1).AddendumSigned.isNullOrBlank()) "" else get(rowIndex-1).AddendumSigned.apiToAppFormat())
                    } catch (e: Exception) {
                        edit_addendumSignedDateButton.setText(if (get(rowIndex-1).AddendumSigned.isNullOrBlank()) "" else get(rowIndex-1).AddendumSigned)
                    }
                    edit_numberOfCardsReaderEditText.setText(get(rowIndex-1).CardReaders)

                    if (textView1.text.toString().contains("true")){

                        edit_loggedIntoRspButton.isChecked=true
                    }else
                    {
                        edit_loggedIntoRspButton.isChecked=false

                    }





                    edit_numberOfCardsReaderEditText.setError(null)
                    edit_numberOfUnacknowledgedRecordsEditText.setError(null)
                    edit_numberOfInProgressTwoIns.setError(null)
                    edit_numberOfInProgressWalkIns.setError(null)
                    edit_startDateButton.setError(null)
                    edit_addendumSignedDateButton.setError(null)
                    edit_inspectionDateButton.setError(null)

                    edit_AAR_PortalTrackingEntryCard.visibility=View.VISIBLE
                    alphaBackgroundForRSPDialogs.visibility = View.VISIBLE




                    for (i in 0 until mainViewLinearId.childCount) {
                        val child = mainViewLinearId.getChildAt(i)
                        child.isEnabled = false
                    }

                        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

                        for ( i in 1..childViewCount-1) {
                            var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

                            for (j in 0..row.getChildCount()-1) {

                                var tv : TextView= row.getChildAt(j) as TextView
                                    tv.isEnabled=false

                            }

                        }




                })
                edit_submitNewAAR_PortalTracking.setOnClickListener {


                    if (validateInputsForUpdate()) {
                        RSP_LoadingView.visibility = View.VISIBLE

                        val date = edit_inspectionDateButton.text
                        val isLoggedInRsp = edit_loggedIntoRspButton.isChecked
                        var numberOfUnacknowledgedRecords = edit_numberOfUnacknowledgedRecordsEditText.text.toString().toInt()
                        var numberOfInProgressTwoInsvalue = edit_numberOfInProgressTwoIns.text.toString().toInt()
                        var numberOfInProgressWalkInsValue = edit_numberOfInProgressWalkIns.text.toString().toInt()


                        var startDate = if (edit_startDateButton.text.equals("SELECT DATE")) "" else edit_startDateButton.text.toString()
                        var endDate = if (edit_endDateButton.text.equals("SELECT DATE")) "" else edit_endDateButton.text.toString()
                        var signedDate = if (edit_addendumSignedDateButton.text.equals("SELECT DATE")) "" else edit_addendumSignedDateButton.text.toString()






                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAARPortalAdminData + "&startDate=${startDate.toString()}&endDate=${endDate.toString()}&addendumSigned=${signedDate.toString()}&" +
                                "cardReaders=${edit_numberOfCardsReaderEditText.text.toString()}&insertBy=E642707&insertDate=2015-07-31T11:53:02.190&updateBy=SumA&updateDate=2015-07-31T11:53:02.190&active=1",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE",response.toString())
                                        Log.v("RowIndex",rowIndex.toString())

                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].startDate = edit_startDateButton.text.toString()
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].PortalInspectionDate = "" + date
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].LoggedIntoPortal = "" + isLoggedInRsp
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].InProgressTows = "" + numberOfInProgressTwoInsvalue
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].InProgressWalkIns = "" + numberOfInProgressWalkInsValue
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].NumberUnacknowledgedTows = "" + numberOfUnacknowledgedRecords
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].CardReaders = edit_numberOfCardsReaderEditText.text.toString()
                                        FacilityDataModel.getInstance().tblAARPortalAdmin[rowIndex-1].AddendumSigned = edit_addendumSignedDateButton.text.toString()

                                            RSP_LoadingView.visibility = View.GONE
                                            alphaBackgroundForRSPDialogs.visibility = View.GONE
                                            edit_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                            fillPortalTrackingTableView()
                                            altLocationTableRow(2)




                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading personnal record")
                            RSP_LoadingView.visibility = View.GONE

                        }))

                    }else
                        Toast.makeText(context,"please fill all required field",Toast.LENGTH_SHORT).show()



                }

                aarPortalTrackingTableLayout.addView(tableRow)
                       // Toast.makeText(context,indexToRemove.toString(),Toast.LENGTH_SHORT).show()


            }
        }

    }

    fun addTheLatestRowOfPortalAdmin(){
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
        FacilityDataModel.getInstance().tblAARPortalAdmin[FacilityDataModel.getInstance().tblAARPortalAdmin.size-1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            try {
                textView.text = PortalInspectionDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = PortalInspectionDate

            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = LoggedIntoPortal
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = NumberUnacknowledgedTows
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = InProgressTows
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = InProgressWalkIns
            tableRow.addView(textView)


            val updateButton = Button(context)
            updateButton.layoutParams = rowLayoutParam5
            updateButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
            updateButton.text = "update"
            tableRow.addView(updateButton)


            aarPortalTrackingTableLayout.addView(tableRow)

        }
        altLocationTableRow(2)
    }
    fun altLocationTableRow(alt_row : Int) {
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

                        RSP_LoadingView.visibility = View.VISIBLE



                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())
                                        RSP_LoadingView.visibility = View.GONE

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
                            RSP_LoadingView.visibility = View.GONE


                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        RSP_LoadingView.visibility = View.GONE

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
        fun newInstance(param1: String, param2: String): FragmentARRAVRepairShopPortalAddendum {
            val fragment = FragmentARRAVRepairShopPortalAddendum()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

