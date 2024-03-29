package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateAARPortalAdminData
import com.inspection.Utils.Constants.UpdateAARPortalTrackingData
import com.inspection.Utils.Constants.rspLoginGet
import com.inspection.Utils.Constants.rspLoginPost
import com.inspection.model.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_array_repair_shop_portal_addendum.*
import java.text.SimpleDateFormat
import java.util.*


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
    var csrf_token = ""
    var rawCookies : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_array_repair_shop_portal_addendum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IndicatorsDataModel.getInstance().tblFacility[0].RSPVisited = true
        (activity as FormsActivity).rspButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        addcancelButton.setOnClickListener {
            addcancelButton.hideKeyboard()
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders= FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].CardReaders
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate= FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].startDate
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate= FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].endDate
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned= FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].AddendumSigned
            fillData()
            (activity as FormsActivity).saveRequired = false
            refreshButtonsState()
            Utility.showMessageDialog(activity,"Confirmation ...","Changes cancelled succesfully ---")
        }


        addsaveButton.setOnClickListener {
            if (validateAdminInputs()) {
                addnumberOfCardsReaderEditText.hideKeyboard()
                rspLoadingText.text = "Saving ..."
                RSP_LoadingView.visibility = View.VISIBLE
                var portalAdminEntry = TblAARPortalAdmin()
                portalAdminEntry.startDate = if (addstartDateButton.text.equals("SELECT DATE")) "" else addstartDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                portalAdminEntry.endDate = if (addendDateButton.text.equals("SELECT DATE")) "" else addendDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                portalAdminEntry.AddendumSigned = if (addsignDateButton.text.equals("SELECT DATE")) "" else addsignDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                portalAdminEntry.CardReaders = addnumberOfCardsReaderEditText.text.toString()
                Log.v("ARR PORTAL SAVE --- ", UpdateAARPortalAdminData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode +
                        "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() +
                        "&startDate=${portalAdminEntry.startDate}&endDate=${portalAdminEntry.endDate}&AddendumSigned=${portalAdminEntry.AddendumSigned}&cardReaders=${portalAdminEntry.CardReaders}&active=1")
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAARPortalAdminData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode +
                        "&facId=${FacilityDataModel.getInstance().tblFacilities[0].FACID}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() +
                        "&startDate=${portalAdminEntry.startDate}&endDate=${portalAdminEntry.endDate}&AddendumSigned=${portalAdminEntry.AddendumSigned}&cardReaders=${portalAdminEntry.CardReaders}&active=1" + Utility.getLoggingParameters(activity, 0, getRSPChanges()),
                        Response.Listener { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().contains("returnCode>0<", false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "RSP Admin")
                                    FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate = portalAdminEntry.endDate
                                    FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate = portalAdminEntry.startDate
                                    FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders = portalAdminEntry.CardReaders
                                    FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned = portalAdminEntry.AddendumSigned
                                    FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].endDate = portalAdminEntry.endDate
                                    FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].startDate = portalAdminEntry.startDate
                                    FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].CardReaders = portalAdminEntry.CardReaders
                                    FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].AddendumSigned = portalAdminEntry.AddendumSigned
                                    (activity as FormsActivity).saveRequired = false
                                    refreshButtonsState()
                                    (activity as FormsActivity).saveDone = true
                                    HasChangedModel.getInstance().groupFacilityRSP[0].FacilityRSP = true
                                    HasChangedModel.getInstance().changeDoneForFacilityRSP()

                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "RSP Admin (Error: " + errorMessage + " )")
                                }
                                RSP_LoadingView.visibility = View.GONE
                                rspLoadingText.text = "Loading ..."
                                alphaBackgroundForRSPDialogs.visibility = View.GONE
                                Add_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "RSP Admin (Error: " + it.message + " )")
                    RSP_LoadingView.visibility = View.GONE
                    rspLoadingText.text = "Loading ..."
                    alphaBackgroundForRSPDialogs.visibility = View.GONE
                    Add_AAR_PortalTrackingEntryCard.visibility = View.GONE
                    (activity as FormsActivity).overrideBackButton = false
                }))
            } else {
                Utility.showValidationAlertDialog(activity,"Please fill all required fields")
            }
        }


        addstartDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!addstartDateButton.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(addstartDateButton.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                addstartDateButton!!.text = sdf.format(c.time)
                FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate= sdf.format(c.time)
                HasChangedModel.getInstance().checkRSPFacilityChange()
                HasChangedModel.getInstance().changeDoneForFacilityRSP()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        addendDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!addendDateButton.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(addendDateButton.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                addendDateButton!!.text = sdf.format(c.time)
                FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate= sdf.format(c.time)
                HasChangedModel.getInstance().checkRSPFacilityChange()
                HasChangedModel.getInstance().changeDoneForFacilityRSP()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        addsignDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val myFormat = "MM/dd/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            if (!addsignDateButton.text.toString().equals("SELECT DATE")) {
                var currentDate = (sdf.parse(addsignDateButton.text.toString()))
                c.setTime(currentDate)
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                addsignDateButton!!.text = sdf.format(c.time)
                FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned = sdf.format(c.time)
                HasChangedModel.getInstance().checkRSPFacilityChange()
                HasChangedModel.getInstance().changeDoneForFacilityRSP()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }, year, month, day)
            dpd.show()
        }

        exitRSPDialogeBtnId.setOnClickListener {
            fillPortalTrackingTableView()
            altLocationTableRow(2)
            Add_AAR_PortalTrackingEntryCard.visibility=View.GONE
            alphaBackgroundForRSPDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
        }

        edit_exitRSPDialogeBtnId.setOnClickListener {
            fillPortalTrackingTableView()
            altLocationTableRow(2)
            edit_AAR_PortalTrackingEntryCard.visibility=View.GONE
            alphaBackgroundForRSPDialogs.visibility = View.GONE
            (activity as FormsActivity).overrideBackButton = false
        }

        addNewAarButton.setOnClickListener {

            numberOfUnacknowledgedRecordsEditText.setText("")
            numberOfInProgressTwoIns.setText("")
            numberOfInProgressWalkIns.setText("")
            inspectionDateButton.setText(Date().toAppFormatMMDDYYYY())
            numberOfUnacknowledgedRecordsEditText.setError(null)
            numberOfInProgressTwoIns.setError(null)
            numberOfInProgressWalkIns.setError(null)
            inspectionDateButton.setError(null)
            (activity as FormsActivity).overrideBackButton = true
            Add_AAR_PortalTrackingEntryCard.visibility=View.VISIBLE
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
        }

        addnumberOfCardsReaderEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders= p0.toString()
                HasChangedModel.getInstance().checkRSPFacilityChange()
                HasChangedModel.getInstance().changeDoneForFacilityRSP()
                (activity as FormsActivity).saveRequired = true
                refreshButtonsState()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


//        inspectionDateButton.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                val myFormat = "MM/dd/yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year,monthOfYear,dayOfMonth)
//                inspectionDateButton!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }
//        edit_inspectionDateButton.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                val myFormat = "MM/dd/yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year,monthOfYear,dayOfMonth)
//                edit_inspectionDateButton!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }

        submitNewAAR_PortalTracking.setOnClickListener {

            if (validateInputs()) {
                if (getRSPTrackingChanges(0).isNullOrEmpty()) {
                    RSP_LoadingView.visibility = View.GONE
                    rspLoadingText.text = "Loading ..."
                    alphaBackgroundForRSPDialogs.visibility = View.GONE
                    Add_AAR_PortalTrackingEntryCard.visibility = View.GONE
                    (activity as FormsActivity).overrideBackButton = false
                } else {
                    rspLoadingText.text = "Saving ..."
                    RSP_LoadingView.visibility = View.VISIBLE
                    val isLoggedInRsp = loggedIntoRspButton.isChecked
                    val numberOfUnacknowledgedRecords = numberOfUnacknowledgedRecordsEditText.text.toString().toInt()
                    val numberOfInProgressTwoInsvalue = numberOfInProgressTwoIns.text.toString().toInt()
                    val numberOfInProgressWalkInsValue = numberOfInProgressWalkIns.text.toString().toInt()
                    var portalTrackingEntry = TblAARPortalTracking()
                    var portalAdminEntry = TblAARPortalAdmin()
                    var readersCount = "1"


                    portalTrackingEntry.FACID = FacilityDataModel.getInstance().tblFacilities[0].FACID.toString()
                    portalTrackingEntry.InProgressTows = numberOfInProgressTwoInsvalue.toString()
                    portalTrackingEntry.InProgressWalkIns = numberOfInProgressWalkInsValue.toString()
                    portalTrackingEntry.LoggedIntoPortal = isLoggedInRsp.toString()
                    portalTrackingEntry.PortalInspectionDate = if (inspectionDateButton.text.equals("SELECT DATE")) "" else inspectionDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                    portalTrackingEntry.NumberUnacknowledgedTows = numberOfUnacknowledgedRecords.toString()
                    portalTrackingEntry.active = "1"
                    Log.v("THE CHANGES", getRSPTrackingChanges(0))

                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAARPortalTrackingData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode +
                            "&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() +
                            "&trackingId=0&portalInspectionDate=${portalTrackingEntry.PortalInspectionDate}&loggedIntoPortal=${isLoggedInRsp}&numberUnacknowledgedTows=${numberOfUnacknowledgedRecords}&inProgressTows=${numberOfInProgressTwoInsvalue}&inProgressWalkIns=${numberOfInProgressWalkInsValue}&active=1" + Utility.getLoggingParameters(activity, 1, getRSPTrackingChanges(0)),
                            Response.Listener { response ->
                                activity!!.runOnUiThread {
                                    if (response.toString().contains("returnCode>0<", false)) {
                                        Utility.showSubmitAlertDialog(activity, true, "RSP")
                                        portalTrackingEntry.TrackingID = response.toString().substring(response.toString().indexOf("<TrackingID") + 12, response.toString().indexOf("</TrackingID"))
//                                        FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate = portalAdminEntry.endDate
//                                        FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate = portalAdminEntry.startDate
//                                        FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders = portalAdminEntry.CardReaders
//                                        FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned = portalAdminEntry.AddendumSigned
                                        FacilityDataModel.getInstance().tblAARPortalTracking.add(portalTrackingEntry)
                                        FacilityDataModelOrg.getInstance().tblAARPortalTracking.add(portalTrackingEntry)
                                        fillPortalTrackingTableView()
                                        altLocationTableRow(2)
                                        (activity as FormsActivity).saveDone = true
                                        HasChangedModel.getInstance().groupFacilityRSP[0].FacilityRSP = true
                                        HasChangedModel.getInstance().changeDoneForFacilityRSP()
                                    } else {
                                        var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                        Utility.showSubmitAlertDialog(activity, false, "RSP (Error: " + errorMessage + " )")
                                    }
                                    RSP_LoadingView.visibility = View.GONE
                                    rspLoadingText.text = "Loading ..."
                                    alphaBackgroundForRSPDialogs.visibility = View.GONE
                                    Add_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                    (activity as FormsActivity).overrideBackButton = false
                                }
                            }, Response.ErrorListener {
                        Utility.showSubmitAlertDialog(activity, false, "RSP (Error: " + it.message + " )")
                        RSP_LoadingView.visibility = View.GONE
                        rspLoadingText.text = "Loading ..."
                        alphaBackgroundForRSPDialogs.visibility = View.GONE
                        Add_AAR_PortalTrackingEntryCard.visibility = View.GONE
                        (activity as FormsActivity).overrideBackButton = false
                    }))
                }
            } else {
                Utility.showValidationAlertDialog(activity,"Please fill all required fields")
            }
        }

        fillData()
        fillPortalTrackingTableView()
        altLocationTableRow(2)
        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
//        var alertBuilder = AlertDialog.Builder(activity);
//        alertBuilder.setCancelable(true);
//        alertBuilder.setTitle("Confirmation")
//        alertBuilder.setMessage("Get RSP Details ?");
//        alertBuilder.setPositiveButton("Yes") { dialog, which ->
//            getRSPDetails()
//        }
//        alertBuilder.setNegativeButton("No") { dialog, which ->
//
//        }
//        val alert = alertBuilder.create();
//        alert.show();
    }
    fun getRSPDetails() {
        // Get CSRF-TOKEN
        val stringRequest = object: StringRequest(Request.Method.GET, rspLoginGet,
                Response.Listener<String> { response ->
                    Log.v("A", "Response is: " + response.substring(0,1000))
                    if (response.toString().contains("csrf-token", false)) {
//                            Log.v("COOKIE ---> ", response)
                        var strToken = response.toString()
                        strToken = strToken.substring(strToken.indexOf("csrf-token"), strToken.indexOf("csrf-token") + 200)
                        Log.v("CSRF_TOKEN --> ", strToken)
//                          strToken = strToken.replace("\"csrf-token\" content=\"","")
                        strToken = strToken.split(">")[0]
                        Log.v("CSRF_TOKEN --> ", strToken)
                        strToken = strToken.substring(strToken.indexOf("=") + 1)
                        Log.v("CSRF_TOKEN --> ", strToken.replace("\"", ""))
                        csrf_token = strToken.replace("\"", "")

                        // POST LOGIN
                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://rsp.national.aaa.com/app/logout",
                                Response.Listener { response ->
                                    Log.v("A", "Logged Out: " + response.toString())
                                    val stringRequest = object: StringRequest(Request.Method.POST, rspLoginPost,
                                            Response.Listener<String> { response ->
                                                Log.v("A", "Response is: " + response.substring(0,1000))
                                            },
                                            Response.ErrorListener {
                                                Log.v("E", "Error is: " + it.message)
                                            })
                                    {
                                        override fun getParams(): MutableMap<String, String>? {
                                            val params = HashMap<String, String>()
                                            params["username"] = "ace_cherya"
                                            params["password"] = "Surfing12345678!"
                                            Log.v("PARAMETERS --> ", "Done")
                                            return params
//                                    return super.getParams()
                                        }
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()
                                            headers["CSRF-Token"] = csrf_token
                                            headers["Content-Type"] = "application/json"
                                            headers["Connection"] = "keep-alive"
                                            headers["HOST"] = "api.national.aaa.com"
//                                headers["Accept"] = "application/json"
//                                headers["Origin"] = "https://rsp.national.aaa.com"
                                            headers["User-Agent"] = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"
                                            headers["Cookie"] = rawCookies.toString()
                                            Log.v("HEADERS --> ", "Done")
                                            return headers
                                        }
                                    }

                                    Volley.newRequestQueue(context).add(stringRequest)
                                }, Response.ErrorListener {

                        }))

                    }
                },
                Response.ErrorListener {
                    Log.v("E", "Error --> " + it.message)
                })
        {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                val responseHeaders = response!!.headers
                rawCookies = responseHeaders!!["Set-Cookie"]
                Log.v("COOKIE -->", rawCookies.toString())
                return super.parseNetworkResponse(response)
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Connection"] = "keep-alive"
                headers["User-Agent"] = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"
                return headers
            }
        }

//        val requestQueue = Volley.newRequestQueue(context)
        Volley.newRequestQueue(context).add(stringRequest)



/*
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, rspLoginGet,
                Response.Listener { response ->
//                    requireActivity().runOnUiThread {
                        if (response.toString().contains("csrf-token", false)) {
//                            Log.v("COOKIE ---> ", response)
                            var strToken = response.toString()
                            strToken = strToken.substring(strToken.indexOf("csrf-token"),strToken.indexOf("csrf-token")+200)
                            Log.v("CSRF_TOKEN --> " , strToken)
//                          strToken = strToken.replace("\"csrf-token\" content=\"","")
                            strToken = strToken.split(">")[0]
                            Log.v("CSRF_TOKEN --> " , strToken)
                            strToken = strToken.substring(strToken.indexOf("=")+1)
                            Log.v("CSRF_TOKEN --> " , strToken.replace("\"",""))
                            csrf_token = strToken.replace("\"","")
                            // Real Login
//                            val policy =
//                                var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
//                                var client = clientBuilder.build()
//                                var formBody = FormBody.Builder()
//                                        .add("username", "ace_cherya")
//                                        .add("password", "Surfing12345678!").build();
//
//                                val request = okhttp3.Request.Builder()
//                                        .url(rspLoginPost).addHeader("CSRF-Token",csrf_token)
//                                        .post(formBody).build()
////                            var requestBody = RequestBody.create(
////                                    "application/json".toMediaTypeOrNull(), rspLoginPost);
////                            var request = okhttp3.Request.Builder().url(rspLoginPost).addHeader("CSRF-Token",csrf_token).build()
////                            var request = okhttp3.Request.Builder().post(requestBody).addHeader("CSRF-Token",csrf_token).build()
////                            var call = client.newCall(request)
////                            try {
////                                var response = call.execute()
////                            } catch (e: java.lang.Exception) {
////                                Log.v("TEST --> ",e.message.toString())
////                            }
//
//                                try {
//                                    client.newCall(request).enqueue(object : Callback {
//                                        override fun onFailure(call: Call, e: IOException) {
//                                            TODO("Not yet implemented")
//                                            Log.v("A", "Response is: " + e.message)
//                                        }
//
//                                        override fun onResponse(call: Call, response: okhttp3.Response) {
//                                            TODO("Not yet implemented")
//                                            Log.v("A", "Response is: " + response.toString())
//                                        }
//                                    })
//                                } catch (e: java.lang.Exception) {
//                                    Log.v("TEST --> ",e.message.toString())
//                                }


//                            StrictMode.setThreadPolicy(ThreadPolicy.Builder() .build())

                            val stringRequest = object: StringRequest(Request.Method.POST, rspLoginPost,
                                    Response.Listener<String> { response ->
                                        Log.v("A", "Response is: " + response.substring(0,1000))
                                    },
                                    Response.ErrorListener {
                                        Log.v("E", "Error is: " + it.message)
                                    })
                            {
                                override fun getParams(): MutableMap<String, String>? {
                                    val params = HashMap<String, String>()
                                    params["username"] = "ace_cherya"
                                    params["password"] = "Surfing12345678!"
                                    Log.v("PARAMETERS --> ", "Done")
                                    return params
//                                    return super.getParams()
                                }
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["CSRF-Token"] = csrf_token
                                    headers["Content-Type"] = "application/json"
                                    headers["HOST"] = "api.national.aaa.com"
                                    headers["Accept"] = "application/json"
                                    headers["User-Agent"] = "PostmanRuntime/7.29.2"
                                    Log.v("HEADERS --> ", "Done")
                                    return headers
                                }
                            }

                            Volley.newRequestQueue(context).add(stringRequest)
                        }
//                    }
                }, Response.ErrorListener {
            Utility.showSubmitAlertDialog(activity, false, "RSP Admin (Error: " + it.message + " )")
        })
        ) */
    }

    fun fillData() {
        FacilityDataModel.getInstance().tblAARPortalAdmin.apply {
            (0 until size).forEach {
                if (!get(it).CardReaders.equals("-1")) {
                    addsignDateButton.text = if (get(it).AddendumSigned.isNullOrEmpty() || get(it).AddendumSigned.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else get(it).AddendumSigned.apiToAppFormatMMDDYYYY()
                    addstartDateButton.text = if (get(it).startDate.isNullOrEmpty() || get(it).startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else get(it).startDate.apiToAppFormatMMDDYYYY()
                    addendDateButton.text = if (get(it).endDate.isNullOrEmpty() || get(it).endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else get(it).endDate.apiToAppFormatMMDDYYYY()
                    addnumberOfCardsReaderEditText.setText(get(it).CardReaders)
                }
            }
        }
    }

    fun validateAdminInputs() : Boolean {

        var portalValide: Boolean
        portalValide = true

        addstartDateButton.setError(null)
        addendDateButton.setError(null)
        addsignDateButton.setError(null)
        addnumberOfCardsReaderEditText.setError(null)

        if (addstartDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
            portalValide = false
            addstartDateButton.setError("Required Field")
        }

//        if (addendDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
//            portalValide = false
//            addendDateButton.setError("Required Field")
//        }

        if (addsignDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
            portalValide = false
            addsignDateButton.setError("Required Field")
        }

        if (addnumberOfCardsReaderEditText.text.toString().isNullOrEmpty()) {
            portalValide = false
            addnumberOfCardsReaderEditText.setError("Required Field")
        }
        return portalValide
    }


    fun validateInputs() : Boolean {

        var portalValide: Boolean
        portalValide = true

        inspectionDateButton.setError(null)
        loggedIntoRspButton.setError(null)
        numberOfUnacknowledgedRecordsEditText.setError(null)
        numberOfInProgressTwoIns.setError(null)
        numberOfInProgressWalkIns.setError(null)

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

        edit_inspectionDateButton.setError(null)
        edit_loggedIntoRspButton.setError(null)
        edit_numberOfUnacknowledgedRecordsEditText.setError(null)
        edit_numberOfInProgressTwoIns.setError(null)
        edit_numberOfInProgressWalkIns.setError(null)

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

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin=10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER
        rowLayoutParam.width = 0


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
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.gravity = Gravity.CENTER
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.gravity = Gravity.CENTER
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.gravity = Gravity.CENTER
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.gravity = Gravity.CENTER
        rowLayoutParam4.width = 0

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.gravity = Gravity.CENTER
        rowLayoutParam5.width = 0

        FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate }).apply {
            (0 until size).forEach {
                if ( !get(it).TrackingID.equals("-1") ) {
                    val tableRow = TableRow(context)
                    tableRow.minimumHeight = 30

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    try {
                        textView.text = get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView.text = get(it).PortalInspectionDate

                        }
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.text = get(it).LoggedIntoPortal
                    textView1.minimumHeight = 30
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.text = get(it).NumberUnacknowledgedTows
                    textView2.minimumHeight = 30
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
                    textView3.text = get(it).InProgressTows
                    textView3.minimumHeight = 30
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    textView4.minimumHeight = 30
                    textView4.text = get(it).InProgressWalkIns
                    tableRow.addView(textView4)

                    val updateButton  = TextView(context)
                    updateButton.layoutParams = rowLayoutParam5
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 14f
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    updateButton.minimumHeight = 30
                    tableRow.addView(updateButton)

                    updateButton.setOnClickListener {
                        rowIndex = aarPortalTrackingTableLayout.indexOfChild(tableRow)
                        edit_numberOfUnacknowledgedRecordsEditText.setText(textView2.text)
                        edit_numberOfInProgressTwoIns.setText(textView3.text)
                        edit_numberOfInProgressWalkIns.setText(textView4.text)
                        edit_inspectionDateButton.setText(textView.text)
                        edit_loggedIntoRspButton.isChecked = textView1.text.toString().contains("true")
                        edit_numberOfUnacknowledgedRecordsEditText.setError(null)
                        edit_numberOfInProgressTwoIns.setError(null)
                        edit_numberOfInProgressWalkIns.setError(null)
                        edit_inspectionDateButton.setError(null)
                        edit_AAR_PortalTrackingEntryCard.visibility = View.VISIBLE
                        alphaBackgroundForRSPDialogs.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                    }

                    edit_submitNewAAR_PortalTracking.setOnClickListener {
                        if (validateInputsForUpdate() ) {
                            if (getRSPTrackingChanges(1).isNullOrEmpty()) {
                                RSP_LoadingView.visibility = View.GONE
                                rspLoadingText.text = "Loading ..."
                                alphaBackgroundForRSPDialogs.visibility = View.GONE
                                edit_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                            } else {
                                rspLoadingText.text = "Saving ..."
                                RSP_LoadingView.visibility = View.VISIBLE
                                val date = edit_inspectionDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                                val isLoggedInRsp = edit_loggedIntoRspButton.isChecked
                                var numberOfUnacknowledgedRecords = edit_numberOfUnacknowledgedRecordsEditText.text.toString().toInt()
                                var numberOfInProgressTwoInsvalue = edit_numberOfInProgressTwoIns.text.toString().toInt()
                                var numberOfInProgressWalkInsValue = edit_numberOfInProgressWalkIns.text.toString().toInt()
                                var inspectionDate = if (edit_inspectionDateButton.text.equals("SELECT DATE")) "" else edit_inspectionDateButton.text.toString().appToApiSubmitFormatMMDDYYYY()
                                var trackingID = FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].TrackingID
                                Log.v("ARR PORTAL EDIT --- ", UpdateAARPortalTrackingData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode +
                                        "&trackingId=${trackingID}&portalInspectionDate=${inspectionDate}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() +
                                        "&loggedIntoPortal=${isLoggedInRsp}&numberUnacknowledgedTows=${numberOfUnacknowledgedRecords}&inProgressTows=${numberOfInProgressTwoInsvalue}&inProgressWalkIns=${numberOfInProgressWalkInsValue}&active=1")
                                Log.v("THE CHANGES", getRSPTrackingChanges(1))
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAARPortalTrackingData + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=" + FacilityDataModel.getInstance().clubCode +
                                        "&trackingId=${trackingID}&portalInspectionDate=${inspectionDate}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=" + Date().toApiSubmitFormat() + "&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=" + Date().toApiSubmitFormat() +
                                        "&loggedIntoPortal=${isLoggedInRsp}&numberUnacknowledgedTows=${numberOfUnacknowledgedRecords}&inProgressTows=${numberOfInProgressTwoInsvalue}&inProgressWalkIns=${numberOfInProgressWalkInsValue}&active=1" + Utility.getLoggingParameters(activity, 0, getRSPTrackingChanges(1)),
                                        Response.Listener { response ->
                                            activity!!.runOnUiThread {
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "RSP")

                                                    FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].PortalInspectionDate = "" + date
                                                    FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].LoggedIntoPortal = "" + isLoggedInRsp
                                                    FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].InProgressTows = "" + numberOfInProgressTwoInsvalue
                                                    FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].InProgressWalkIns = "" + numberOfInProgressWalkInsValue
                                                    FacilityDataModel.getInstance().tblAARPortalTracking[rowIndex - 1].NumberUnacknowledgedTows = "" + numberOfUnacknowledgedRecords
                                                    FacilityDataModelOrg.getInstance().tblAARPortalTracking[rowIndex - 1].PortalInspectionDate = "" + date
                                                    FacilityDataModelOrg.getInstance().tblAARPortalTracking[rowIndex - 1].LoggedIntoPortal = "" + isLoggedInRsp
                                                    FacilityDataModelOrg.getInstance().tblAARPortalTracking[rowIndex - 1].InProgressTows = "" + numberOfInProgressTwoInsvalue
                                                    FacilityDataModelOrg.getInstance().tblAARPortalTracking[rowIndex - 1].InProgressWalkIns = "" + numberOfInProgressWalkInsValue
                                                    FacilityDataModelOrg.getInstance().tblAARPortalTracking[rowIndex - 1].NumberUnacknowledgedTows = "" + numberOfUnacknowledgedRecords
                                                    fillPortalTrackingTableView()
                                                    altLocationTableRow(2)
                                                    (activity as FormsActivity).saveDone = true
                                                    HasChangedModel.getInstance().groupFacilityRSP[0].FacilityRSP = true
                                                } else {
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "RSP (Error: " + errorMessage + " )")
                                                }
                                                RSP_LoadingView.visibility = View.GONE
                                                alphaBackgroundForRSPDialogs.visibility = View.GONE
                                                edit_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                                (activity as FormsActivity).overrideBackButton = false

                                            }
                                        }, Response.ErrorListener {
                                    Utility.showSubmitAlertDialog(activity, false, "RSP (Error: " + it.message + " )")
                                    RSP_LoadingView.visibility = View.GONE
                                    rspLoadingText.text = "Loading ..."
                                    alphaBackgroundForRSPDialogs.visibility = View.GONE
                                    edit_AAR_PortalTrackingEntryCard.visibility = View.GONE
                                    (activity as FormsActivity).overrideBackButton = false

                                }))
                            }
                        } else
                            Utility.showValidationAlertDialog(activity,"Please fill all required fields")
                    }

                    aarPortalTrackingTableLayout.addView(tableRow)
                }
            }
        }

    }

    fun getRSPChanges() : String {
        var strChanges = ""
        try {
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate != FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].endDate) {
                strChanges += "RSP Admin end date changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY() + ") to (" + FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate + ") - "
            }
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned != FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].AddendumSigned) {
                strChanges += "RSP Admin addendum signed date changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY() + ") to (" + FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned + ") - "
            }
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate != FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].startDate) {
                strChanges += "RSP Admin start date changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY() + ") to (" + FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate + ") - "
            }
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders != FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].CardReaders) {
                strChanges += "RSP Admin card readers changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalAdmin[0].CardReaders + ") to (" + FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders + ") - "
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (strChanges.isNullOrEmpty()) "No Changes" else strChanges
    }

    fun getRSPTrackingChanges(action : Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        if (action==0) {
            strChanges = "RSP Tracking added with "
            if (loggedIntoRspButton.isChecked) {
                strChanges += "logged into portal (true) - "
            }
            if (!loggedIntoRspButton.isChecked) {
                strChanges += "logged into portal (false) - "
            }
            strChanges += "Number of In Progress Tows changed (" + numberOfInProgressTwoIns.text.toString() + ") - "
            strChanges += "Number of In Progress Walk-Ins changed (" + numberOfInProgressWalkIns.text.toString() + ") - "
            strChanges += "Number of Unacknowledged tows (" + numberOfUnacknowledgedRecordsEditText.text.toString() + ") - "
        }
        if (action==1) {
            if (edit_loggedIntoRspButton.isChecked && (FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].LoggedIntoPortal.equals("false") )) {
                strChanges += "RSP Tracking logged into portal changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].LoggedIntoPortal + ") to (true) - "
            }
            if (!edit_loggedIntoRspButton.isChecked && (FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].LoggedIntoPortal.equals("true") )) {
                strChanges += "RSP Tracking logged into portal changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].LoggedIntoPortal + ") to (false) - "
            }
            if (edit_numberOfInProgressTwoIns.text.toString() != FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].InProgressTows) {
                strChanges += "RSP Tracking Number of In Progress Tows changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].InProgressTows + ") to (" + edit_numberOfInProgressTwoIns.text.toString() + ") - "
            }
            if (edit_numberOfInProgressWalkIns.text.toString() != FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].InProgressWalkIns) {
                strChanges += "RSP Tracking Number of In Progress Walk-Ins changed from (" + FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].InProgressWalkIns + ") to (" + edit_numberOfInProgressWalkIns.text.toString() + ") - "
            }
            if (edit_numberOfUnacknowledgedRecordsEditText.text.toString() != FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].NumberUnacknowledgedTows) {
                strChanges += "RSP Tracking Number of Unacknowledged tows from (" + FacilityDataModelOrg.getInstance().tblAARPortalTracking[0].NumberUnacknowledgedTows + ") to (" + edit_numberOfUnacknowledgedRecordsEditText.text.toString() + ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun altLocationTableRow(alt_row : Int) {
        var childViewCount = aarPortalTrackingTableLayout.getChildCount();
        for ( i in 1..childViewCount-1) {
            var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;
            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                        R.drawable.alt_row_color);
            } else {
                row.background = getResources().getDrawable(
                        R.drawable.row_color);
            }
        }
    }

    fun refreshButtonsState(){

        addsaveButton.isEnabled = (activity as FormsActivity).saveRequired
        addcancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//
//    }

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

