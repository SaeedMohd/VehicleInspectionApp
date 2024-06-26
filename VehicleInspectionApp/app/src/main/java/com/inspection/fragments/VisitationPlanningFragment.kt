package com.inspection.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.barteksc.pdfviewer.PDFView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.model.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.android.synthetic.main.fragment_visitation_form.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.progressBarRecords
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.json.XML
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FrgmentARRAnnualVisitationRecords.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FrgmentARRAnnualVisitationRecords.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitationPlanningFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var fragment: Fragment? = null
    var defaultClubCode = ""
    lateinit var pdfView : PDFView
    private var mListener: OnFragmentInteractionListener? = null
    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var visitationList = ArrayList<AnnualVisitationInspectionFormData>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null
    var firstLoading = true
    var isVisitationPlanning = false
    var requiredSpecialistName = ""
    var visitationsModel: VisitationsModel = VisitationsModel()
    var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
    var allClubCodes = ArrayList<String>()
    var specialistClubCodes = ArrayList<String>()
    var specialistArrayModel = ArrayList<TypeTablesModel.employeeList>()
    var specialistModel = ArrayList<CsiSpecialistDetails>()
    var clubCode=""
    var visitationID =""
    var totalVisitations = 0
    var overridOverdue = VisitationStatus.Overdue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.visitation_planning_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitationfacilityListView.visibility = View.GONE
        pdfView = view.findViewById(R.id.pdfView)
        var visitationYearFilterSpinnerEntries = mutableListOf<String>()
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)
//        visitationYearFilterSpinnerEntries.add    ("Any")
        (currentYear - 30..currentYear+1).forEach {
            visitationYearFilterSpinnerEntries.add("" + it)
        }
        visitationYearFilterSpinnerEntries.sortDescending()
//        visitationYearFilterSpinnerEntries.add(0, "Any")
        visitationYearFilterSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, visitationYearFilterSpinnerEntries)
        visitationYearFilterSpinner.onItemSelectedListener = spinnersOnItemSelectListener
        visitationYearFilterSpinner.setSelection(visitationYearFilterSpinnerEntries.indexOf("" + Calendar.getInstance().get(Calendar.YEAR)))

        searchVisitaionsButton.setOnClickListener {
            it.hideKeyboard()
            reloadVisitationsList()
        }

//        facilityNameButton.onFocusChangeListener = View.OnFocusChangeListener { view: View, b: Boolean ->
//            if (b) {
//                view.hideKeyboard()
//            }
//        }

//        getTypeTables()
        loadSpecialists()
        loadSpecialistDetails()
        loadSpecialistName()
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
//                Response.Listener { response ->
//                    Log.v("****response", response)
//                    activity!!.runOnUiThread {
//                        CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
//                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread {
//                                        var specialistName = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
//                                        if (specialistName != null && specialistName.size > 0) {
//                                            requiredSpecialistName = specialistName[0].specialistname
//                                            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistName[0].accspecid
//                                            for (sn in specialistName){
//                                                specialistClubCodes.add(sn.clubcode)
//                                            }
////                                            var firstName = requiredSpecialistName .substring(requiredSpecialistName .indexOf(",")+2,requiredSpecialistName .length)
////                                            var lastName = requiredSpecialistName .substring(0,requiredSpecialistName .indexOf(","))
////                                            var reformattedName = firstName + " " + lastName
////                                            visitationSpecialistName.setText(reformattedName)
////                                            visitationSpecialistName.setText(requiredSpecialistName)
//                                        }
//                                        loadSpecialistName()
////                                        loadClubCodes()
//                                    }
//                                }, Response.ErrorListener {
//                            Log.v("error while loading", "error while loading facilities")
//                            Log.v("Loading error", "" + it.message)
//                        }))
//                    }
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading specialists")
//            Log.v("Loading error", "" + it.message)
//        }))


    }

    val spinnersOnItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //reloadVisitationsList()
        }

    }

    fun prepareInitialStateForFilters(){
//        clubCodeEditText.setText("252")
//        clubCodeEditText.setText(specialistArrayModel.sortedWith(compareBy { it.clubcode })[0].clubcode)

    }


    fun firstLoadingCompleted(){

        prepareInitialStateForFilters()



        visitationMonthsSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH))
        visitationMonthsSpinner.onItemSelectedListener = spinnersOnItemSelectListener


        visitationYearFilterSpinner.onItemSelectedListener = spinnersOnItemSelectListener

        progressBarRecords.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        searchVisitaionsButton.setOnClickListener({
            reloadVisitationsList()
            it.hideKeyboard()
        })

        clubCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //reloadVisitationsList()
            }
        })

        visitationfacilityIdVal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //reloadVisitationsList()

            }

        })

        visitationSpecialistName.setOnClickListener {
            var personnelNames = ArrayList<String>()
//            (0 until CsiSpecialistSingletonModel.getInstance().csiSpecialists.size).forEach {
//                personnelNames.add(CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].specialistname)
//            }
//            personnelNames.sort()
//            personnelNames.add(0, "Any")
            (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
//                personnelNames.add(TypeTablesModel.getInstance().EmployeeList[it].LastName + " " + TypeTablesModel.getInstance().EmployeeList[it].FirstName)
                personnelNames.add(TypeTablesModel.getInstance().EmployeeList[it].FullName)
            }
            personnelNames.sort()
            personnelNames.add(0, "Any")
            var searchDialog = SearchDialog(context, personnelNames)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                if (searchDialog.selectedString == "Any") {
                    visitationSpecialistName.setText("")
                } else {
                    visitationSpecialistName.setText(searchDialog.selectedString)
                }
                //reloadVisitationsList()
            }
        }

        clubCodeEditText.setOnClickListener {
            var searchDialog = SearchDialog(context, allClubCodes)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                clubCodeEditText.setText(searchDialog.selectedString)
                //reloadVisitationsList()
            }
        }

        facilityNameButton.setOnClickListener {
//            recordsProgressView.visibility = View.VISIBLE
//            Log.v("VISITATION FAC NAME --- ",Constants.getAllFacilities + "")
//            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllFacilities + "",
//                    Response.Listener { response ->
//                        activity!!.runOnUiThread {
//                            recordsProgressView.visibility = View.INVISIBLE
//                            var facilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
//                            var facilityNames = ArrayList<String>()
//                            (0 until facilities.size).forEach {
//                                facilityNames.add(facilities[it].facname)
//                            }
//                            facilityNames.sort()
//                            facilityNames.add(0, "Any")
                            var searchDialog = SearchDialog(context, facilityNames)
                            searchDialog.show()
                            searchDialog.setOnDismissListener {
                                if (searchDialog.selectedString == "Any" || searchDialog.selectedString == "") {
                                    facilityNameButton.setText("")
                                    visitationfacilityIdVal.setText("")
                                } else {
                                    facilityNameButton.setText(searchDialog.selectedString.substring(0,searchDialog.selectedString.indexOf(" || ")))
                                    visitationfacilityIdVal.setText(searchDialog.selectedString.substringAfter("|| "))
                                }
                                //reloadVisitationsList()
                            }
//                        }
//                    }, Response.ErrorListener {
//
//                //                context!!.toast("Connection Error")
//                Utility.showMessageDialog(activity,"Retrieve Data Error","Connection Error while retrieving Facilities - " + it.message)
//                Log.v("error while loading", "error while loading facilities")
//                Log.v("Loading error", "" + it.message)
//                it.printStackTrace()
//            }))
        }

        annualVisitationCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           // reloadVisitationsList()
        }

        quarterlyOrOtherVisistationsCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           // reloadVisitationsList()
        }

        adHocVisitationsCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           // reloadVisitationsList()
        }


        deficienciesCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           // reloadVisitationsList()
        }



        pendingCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           // reloadVisitationsList()
        }



        completedCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->


           // reloadVisitationsList()


        }


        reloadVisitationsList()
    }


    private fun loadFacilityNames(){
        var facilities : ArrayList<CsiFacility>
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllFacilities + "",
                Response.Listener { response ->
                    Log.v("test","testtesttest-----------")
                    requireActivity().runOnUiThread {
                        recordsProgressView.visibility = View.INVISIBLE
                        CSIFacilitySingelton.getInstance().csiFacilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
                        facilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
                        (0 until facilities.size).forEach {
                            facilityNames.add(facilities[it].facname + " || " + facilities[it].facnum)
                        }
                        Log.v("Logged User --- >  ",ApplicationPrefs.getInstance(activity).loggedInUserID)
                        facilities.removeIf { s->s.accspecid.isNullOrEmpty() }
                        if (facilities.filter { s->s.accspecid.equals(ApplicationPrefs.getInstance(activity).loggedInUserID)}.isNotEmpty()) {
//                            defaultFacNumber = facilities.filter { s -> s.specialistid.equals(ApplicationPrefs.getInstance(activity).loggedInUserID) }.sortedWith(compareBy { it.facnum })[0].facnum
//                            adHocFacilityIdVal.setText(defaultFacNumber)
                            defaultClubCode = facilities.filter { s->s.accspecid.equals(ApplicationPrefs.getInstance(activity).loggedInUserID)}.sortedWith(compareBy { it.clubcode})[0].clubcode
                            clubCodeEditText.setText(defaultClubCode)
                        } else {
                            clubCodeEditText.setText("252")
                        }
                        facilityNames.sort()
                        facilityNames.add(0, "Any")
                        firstLoadingCompleted()
                    }
                }, Response.ErrorListener {
            Utility.showMessageDialog(activity, "Retrieve Data Error", "Connection Error while retrieving Facilities - " + it.message)
            recordsProgressView.visibility = View.INVISIBLE
            Log.v("error while loading", "error while loading facilities")
            Log.v("Loading error", "" + it.message)
        }))
    }


    fun reloadVisitationsList() {

        var parametersString = StringBuilder()
        if (true) {
            if (clubCodeEditText.text.trim().isNotEmpty()) {
                with(parametersString) {
                    append("clubCode=" + clubCodeEditText.text.trim())
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("clubCode=252")
                    append("&")
                }
            }

            with(parametersString) {
                append("facNum=" + visitationfacilityIdVal.text.trim())
                append("&")
            }

            if (!visitationSpecialistName.text.contains("Select") && visitationSpecialistName.text.length > 1) {
                with(parametersString) {
                    //TODO added to void specialist value until they let us know how we will use it
                    try {
                        var specialistName = visitationSpecialistName.text
//                        var specialistId =  CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s -> s.specialistname.equals(specialistName.toString()) }[0].accspecid
                        var specialistId =  TypeTablesModel.getInstance().EmployeeList.filter { s -> s.FullName.equals(specialistName.toString()) }[0].NTLogin
//                        var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
//                        Utility.showMessageDialog(activity,"TEST",reformattedName)
//                        append("specialist=" + CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s -> s.specialistname == visitationSpecialistName.text.toString() }[0].accspecid)
//                        append("specialist=" + CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s -> s.specialistname == reformattedName }[0].accspecid)
                        append("specialist=" + specialistId)
                        append("&")
                    } catch (exp: Exception) {
                        append("specialist=")
                        append("&")
                    }
                }
            } else {
                with(parametersString) {
                    append("specialist=")
                    append("&")
                }
            }

            if (!facilityNameButton.text.contains("Select") && facilityNameButton.text.length > 1) {
                with(parametersString) {
//                    append(("dba=" + URLEncoder.encode(facilityNameButton.text.toString(), "UTF-8")))
//                    append("dba=" + facilityNameButton.text.toString())
                    append("dba=")
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("dba=")
                    append("&")
                }
            }


            if (visitationYearFilterSpinner.selectedItem != "Any") {
                with(parametersString) {
                    append("inspectionYear=" + visitationYearFilterSpinner.selectedItem)
                    append("&")
                }
            }else{
                with(parametersString) {
                    append("inspectionYear=")
                    append("&")
                }
            }
            var InsMonth = 0
            InsMonth = visitationMonthsSpinner.selectedItemPosition+1
            if (visitationMonthsSpinner.selectedItem != "Any") {
                with(parametersString) {
                    append("inspectionMonth=" + InsMonth)
                    append("&")
                }
            }else{
                with(parametersString) {
                    append("inspectionMonth=")
                    append("&")
                }
            }

            if (annualVisitationCheckBox.isChecked) {
                with(parametersString) {
                    append("annualVisitations=1")
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("annualVisitations=0")
                    append("&")
                }
            }

            if (quarterlyOrOtherVisistationsCheckBox.isChecked) {
                with(parametersString) {
                    append("quarterlyVisitations=1")
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("quarterlyVisitations=0")
                    append("&")
                }
            }


            if (pendingCheckBox.isChecked || overdueCheckBox.isChecked || inProgressCheckBox.isChecked) {
                with(parametersString) {
                    append("pendingVisitations=1")
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("pendingVisitations=0")
                    append("&")
                }
            }

            if (completedCheckBox.isChecked) {
                with(parametersString) {
                    append("completedVisitations=1")
                    append("&")
                }
            } else {
                with(parametersString) {
                    append("completedVisitations=0")
                    append("&")
                }
            }

            if (deficienciesCheckBox.isChecked) {
                with(parametersString) {
                    append("deficiencies=1")
                }
            } else {
                with(parametersString) {
                    append("deficiencies=0")
                }
            }
            recordsProgressView.visibility = View.VISIBLE

            var client = OkHttpClient()
//                    .newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            var request = okhttp3.Request.Builder().url(Constants.getVisitations + parametersString+Utility.getLoggingParameters(activity, 0, "Search Visitations ...")).build()

            Log.v("******get visitation", Constants.getVisitations+parametersString+Utility.getLoggingParameters(activity, 0, "Search Visitations ..."))

//            FirebaseCrashlytics.getInstance().setCustomKey("Details", parametersString.toString())

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("failure http", "failed with exception : " + e!!.message)
                    requireActivity().runOnUiThread {
                        Utility.showMessageDialog(activity, "Retrieve Data Error", e.message)
                        recordsProgressView.visibility = View.INVISIBLE
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {

                    var responseString = response!!.body!!.string()
                    //  activity!!.toast("success!!!")
                    //     recordsProgressView.visibility = View.INVISIBLE
//                    FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Visitation --> ${responseString}")
                    if (responseString.toString().contains("returnCode>1<",false) || !responseString.toString().contains("returnCode>",false)) {
                        requireActivity().runOnUiThread {
//                            if (responseString.toString().contains("message", false))
//                                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
//                            else
                            Utility.showMessageDialog(activity, "Retrieve Data Error", responseString)
                            recordsProgressView.visibility = View.GONE
                            visitationfacilityListView.visibility = View.VISIBLE
                            visitationsModel.pendingVisitationsArray.clear()
                            visitationsModel.completedVisitationsArray.clear()
                            visitationsModel.deficienciesArray.clear()
                            var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                            visitationfacilityListView.adapter = visitationPlanningAdapter
                            totalVisitations = 0

                        }
                    } else {
//                    var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("&lt;responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                        var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")))
                        if (obj.toString().equals("{\"responseXml\":\"\"}")) {
                            activity!!.runOnUiThread {
                                visitationsModel.listArray.clear()
                                visitationfacilityListView.adapter = null
                                recordsProgressView.visibility = View.GONE
                                visitationfacilityListView.visibility = View.VISIBLE
                                Utility.showMessageDialog(requireContext(), "Information", "No available visitations to show")
                            }
                        } else {
                            var jsonObj = obj.getJSONObject("responseXml")

                            visitationsModel = parseVisitationsData(jsonObj)

                            Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getPRGCompletedVisitations,
                                    { response ->
                                        activity!!.runOnUiThread {
                                            if (!response.toString().replace(" ", "").equals("[]")) {
                                                PRGDataModel.getInstance().tblPRGCompletedVisitations = Gson().fromJson(response.toString(), Array<PRGCompletedVisitations>::class.java).toCollection(ArrayList())
                                            } else {
                                                var item = PRGCompletedVisitations()
                                                item.recordid = -1
                                                PRGDataModel.getInstance().tblPRGCompletedVisitations.add(item)
                                            }
                                            activity!!.runOnUiThread {
                                                Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getPRGVisitationsLog,
                                                        Response.Listener { response ->
                                                            activity!!.runOnUiThread {
                                                                if (!response.toString().replace(" ", "").equals("[]")) {
                                                                    PRGDataModel.getInstance().tblPRGVisitationsLog = Gson().fromJson(response.toString(), Array<PRGVisitationsLog>::class.java).toCollection(ArrayList())
                                                                } else {
                                                                    var item = PRGVisitationsLog()
                                                                    item.recordid = -1
                                                                    PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                                                                }
                                                                activity!!.runOnUiThread {
                                                                    recordsProgressView.visibility = View.GONE
                                                                    visitationfacilityListView.visibility = View.VISIBLE
                                                                    // New Logic
                                                                    FillVisitationList()
                                                                    //
//                                                            var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                                                                    var visitationPlanningAdapter = VisitationPlanningNewAdapter(context, visitationsModel)
                                                                    visitationfacilityListView.adapter = visitationPlanningAdapter
                                                                    totalVisitations = visitationsModel.completedVisitationsArray.size + visitationsModel.deficienciesArray.size + visitationsModel.pendingVisitationsArray.size
//                                                            Utility.showMessageDialog(activity,"Filter Result"," " + totalVisitations + " Visitations Filtered ...")
                                                                    resultsCount.text = "Filtered Visitations --> ( " + totalVisitations + " )"
                                                                }

                                                            }
                                                        }, Response.ErrorListener {
                                                    Log.v("Loading PRG Data error", "" + it.message)
                                                    var item = PRGVisitationsLog()
                                                    item.recordid = -1
                                                    PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                                                    activity!!.runOnUiThread {
                                                        recordsProgressView.visibility = View.GONE
                                                        visitationfacilityListView.visibility = View.VISIBLE
                                                        var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                                                        visitationfacilityListView.adapter = visitationPlanningAdapter
                                                        totalVisitations = visitationsModel.completedVisitationsArray.size + visitationsModel.deficienciesArray.size + visitationsModel.pendingVisitationsArray.size
//                                                Utility.showMessageDialog(activity,"Filter Result"," " + totalVisitations + " Visitations Filtered ...")
                                                    }
                                                    it.printStackTrace()
                                                }))
                                            }

                                        }
                                    }, {
                                Log.v("Loading PRG Data error", "" + it.message)
                                var item = PRGCompletedVisitations()
                                item.recordid = -1
                                PRGDataModel.getInstance().tblPRGCompletedVisitations.add(item)
                                activity!!.runOnUiThread {
                                    recordsProgressView.visibility = View.GONE
                                    visitationfacilityListView.visibility = View.VISIBLE
                                    var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                                    visitationfacilityListView.adapter = visitationPlanningAdapter
                                    totalVisitations = visitationsModel.completedVisitationsArray.size + visitationsModel.deficienciesArray.size + visitationsModel.pendingVisitationsArray.size
                                    Utility.showMessageDialog(activity, "Filter Result", " " + totalVisitations + " Visitations Filtered ...")
                                }
                                it.printStackTrace()
                            }))
                        }
                    }
            }
            })


        } else {
            Log.v("VISITATION GET --- ",Constants.getVisitations + parametersString)
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getVisitations + parametersString,
                    Response.Listener { response ->
                        requireActivity().runOnUiThread {
                            var responseString = response.toString()
                            if (responseString.toString().contains("returnCode>1<",false)) {
                                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message")+9,responseString.indexOf("</message")))
                            } else {
                //                                var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                                var obj = XML.toJSONObject(response.substring(response.indexOf("<responseXml"), response.indexOf("<returnCode")))
                                var jsonObj = obj.getJSONObject("responseXml")
                                var visitationsModel = parseVisitationsData(jsonObj)
                                recordsProgressView.visibility = View.GONE
                                visitationfacilityListView.visibility = View.VISIBLE
                                var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                                visitationfacilityListView.adapter = visitationPlanningAdapter
                            }
                        }
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading visitation records")
                Utility.showMessageDialog(activity,"Retrieve Data Error","Connection Error while retrieving Visitation records - " + it.message)
            }))
        }
    }

    fun reviewFilters(visitationType : String, visitationStatus : String) : Boolean {
        var hideVisitation = false
        if (!annualVisitationCheckBox.isChecked && visitationType.equals(VisitationTypes.Annual.toString())) return true
        if (!quarterlyOrOtherVisistationsCheckBox.isChecked && visitationType.equals(VisitationTypes.Quarterly.toString())) return true
        if (!adHocVisitationsCheckBox.isChecked && visitationType.equals(VisitationTypes.AdHoc.toString())) return true
        if (!deficienciesCheckBox.isChecked && visitationType.equals(VisitationTypes.Deficiency.toString())) return true
        if (!pendingCheckBox.isChecked && visitationStatus.equals("Not Started")) return true
        if (!inProgressCheckBox.isChecked && visitationStatus.contains("Progress",true)) return true
        if (!overdueCheckBox.isChecked && visitationStatus.contains("Overdue",true)) return true
        if (!completedCheckBox.isChecked && visitationStatus.equals("Completed")) return true
        return hideVisitation
    }

    fun FillVisitationList() { // New Method to
//        val c = Calendar.getInstance()
//        val month = c.get(Calendar.MONTH)+1
//        var filteredMonth = if (visitationMonthsSpinner.selectedItemPosition==0) "0" else visitationMonthsSpinner.selectedItemPosition.toString()
//        var filteredYear = if (visitationYearFilterSpinner.selectedItemPosition==0) "0" else visitationYearFilterSpinner.selectedItem.toString()
//        if (visitationsModel.deficienciesArray.size>0) {
//            visitationsModel.deficienciesArray.removeIf { s -> s.DueDate.substring(5, 7).toInt() > filteredMonth.toInt() }
//            visitationsModel.deficienciesArray.removeIf { s -> s.DueDate.substring(0, 4).toInt() > filteredYear.toInt() }
//        }
        visitationsModel.listArray.clear()
        visitationsModel.pendingVisitationsArray.forEach {
            val item = VisitationsModel.VisitationListModel()
            item.active = "1"
            item.BusinessName = it.BusinessName
            item.ClubCode = it.ClubCode
            item.ContractCurrentDate = it.ContractCurrentDate
            item.ContractInitialDate = it.ContractInitialDate
            item.FACNo = it.FACNo
            item.FacID = it.FacID
            item.FacilityAnnualInspectionMonth = it.FacilityAnnualInspectionMonth
//            visitationsModel.listArray.add(item)
            // Check Visitation Type
            var visitationTypeAndStatus = determineVisitationTypeAndStatus(item.FacilityAnnualInspectionMonth.toInt(),item.FacID.toInt(),item.ClubCode.toInt())
            item.VisitationType = visitationTypeAndStatus.first.toString()
            item.VisitationStatus = visitationTypeAndStatus.second.toString()
            item.InspectionCycle = it.InspectionCycle
            // CHeck Visitation Status

            if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
                if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == item.FACNo.toInt() && s.clubcode == item.ClubCode.toInt() && s.facannualinspectionmonth == item.FacilityAnnualInspectionMonth.toInt() && s.inspectioncycle == item.InspectionCycle && s.visitationtype == visitationTypeAndStatus.first.toString() }.isNotEmpty()) {
                    if (visitationTypeAndStatus.second == VisitationStatus.NotStarted) {
                        item.VisitationStatus = VisitationStatus.InProgress.toString().replace("In", "In ")
                    } else {
                        item.VisitationStatus = visitationTypeAndStatus.second.toString() + " / In Progress"
                    }
                }
            }

            if (item.VisitationStatus.equals(VisitationStatus.NotStarted.toString())){
                item.VisitationStatus = VisitationStatus.NotStarted.toString().replace("Not","Not ")
            }
            // CHeck if has deficiency

            if (visitationsModel.deficienciesArray.filter { s->s.FACNo.equals(item.FACNo) && s.ClubCode.equals(item.ClubCode)}.isNotEmpty()) {
                item.VisitationStatus = item.VisitationStatus + "/Deficiency"
                visitationsModel.deficienciesArray.removeIf { s->s.FACNo.equals(item.FACNo) && s.ClubCode.equals(item.ClubCode)}
            }
            if (!reviewFilters(item.VisitationType,item.VisitationStatus)) visitationsModel.listArray.add(item)
        }

        visitationsModel.completedVisitationsArray.forEach {
            val item = VisitationsModel.VisitationListModel()
            item.active = "1"
            item.BusinessName = it.BusinessName
            item.ClubCode = it.ClubCode
            item.ContractCurrentDate = it.ContractCurrentDate
            item.ContractInitialDate = it.ContractInitialDate
            item.FACNo = it.FACNo
            item.FacID = it.FacID
            item.FacilityAnnualInspectionMonth = it.FacilityAnnualInspectionMonth
            item.CompletionDate = it.DatePerformed.apiToAppFormatMMDDYYYY()
            item.VisitationID = it.visitationID
            item.VisitationType = it.VisitationTypeID.toString()
            item.VisitationStatus = "Completed"
            item.insertBy1 = it.insertBy1
            when (item.VisitationType.toInt()) {
                1 -> item.VisitationType = "Annual"
                2 -> item.VisitationType = "Quarterly"
                3 -> item.VisitationType = "AdHoc"
                4 -> item.VisitationType = "Deficiency"
            }

            if (!reviewFilters(item.VisitationType,item.VisitationStatus))
                visitationsModel.listArray.add(item)
        }


        visitationsModel.deficienciesArray.forEach {
            val item = VisitationsModel.VisitationListModel()
            item.active = "1"
            item.BusinessName = it.BusinessName
            item.ClubCode = it.ClubCode
            item.ContractCurrentDate = it.ContractCurrentDate
            item.ContractInitialDate = it.ContractInitialDate
            item.FACNo = it.FACNo
            item.FacID = it.FacID
            item.FacilityAnnualInspectionMonth = it.FacilityAnnualInspectionMonth
            item.DueDate = it.DueDate.apiToAppFormatMMDDYYYY()
            item.VisitationType = "Deficiency"
            val dueDate = it.DueDate.substring(0,10)
            val format = SimpleDateFormat("yyyy-MM-dd");
            try {
                val date = format.parse(dueDate);
                if (Date()<=date) {
                    item.VisitationStatus = VisitationStatus.NotStarted.toString().replace("Not","Not ")
                } else {
                    item.VisitationStatus = VisitationStatus.Overdue.toString()
                }
            } catch (e : ParseException) {
                e.printStackTrace();
            }

            if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
                if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == it.FACNo.toInt() && s.clubcode == it.ClubCode.toInt() && s.facannualinspectionmonth == it.FacilityAnnualInspectionMonth.toInt() && s.inspectioncycle == it.InspectionCycle && s.visitationtype == VisitationTypes.Deficiency.toString() }.isNotEmpty()) {
                    if (item.VisitationStatus == VisitationStatus.NotStarted.toString().replace("Not","Not ")) {
                        item.VisitationStatus = VisitationStatus.InProgress.toString().replace("In", "In ")
                    } else {
                        item.VisitationStatus = item.VisitationStatus + " / In Progress"
                    }
                }
            }
            if (!reviewFilters(item.VisitationType,item.VisitationStatus)) visitationsModel.listArray.add(item)
                visitationsModel.listArray.add(item)
        }

        var sortedList = ArrayList<VisitationsModel.VisitationListModel>()

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Overdue") && !it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }
        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Overdue") && it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }
        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Progress") && !it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Progress") && it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Not") && !it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Not") && it.VisitationStatus.contains("Deficiency")){
                sortedList.add(it)
                it.active = "0"
            }
        }

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && !it.VisitationStatus.contains("Completed")){
                sortedList.add(it)
                it.active = "0"
            }
        }

        visitationsModel.listArray.forEach {
            if (it.active.equals("1") && it.VisitationStatus.contains("Completed")){
                sortedList.add(it)
                it.active = "0"
            }
        }
        visitationsModel.listArray.clear()
        visitationsModel.listArray = sortedList
        visitationsModel.pendingVisitationsArray.clear()
        visitationsModel.completedVisitationsArray.clear()
        visitationsModel.deficienciesArray.clear()
    }

    fun parseVisitationsData(jsonObject: JSONObject): VisitationsModel {
        var visitationsModel = VisitationsModel()

        if (jsonObject.has("PendingVisitations")) {
            if (jsonObject.get("PendingVisitations").toString().startsWith("[")) {
                visitationsModel.pendingVisitationsArray = Gson().fromJson(jsonObject.get("PendingVisitations").toString(), Array<VisitationsModel.PendingVisitationModel>::class.java).toCollection(ArrayList())
            } else {
                visitationsModel.pendingVisitationsArray.add(Gson().fromJson(jsonObject.get("PendingVisitations").toString(), VisitationsModel.PendingVisitationModel::class.java))
            }
        }

        if (jsonObject.has("CompletedVisitations")) {
            if (jsonObject.get("CompletedVisitations").toString().startsWith("[")) {
                visitationsModel.completedVisitationsArray = Gson().fromJson(jsonObject.get("CompletedVisitations").toString(), Array<VisitationsModel.CompletedVisitationModel>::class.java).toCollection(ArrayList())
            } else {
                visitationsModel.completedVisitationsArray.add(Gson().fromJson(jsonObject.get("CompletedVisitations").toString(), VisitationsModel.CompletedVisitationModel::class.java))
            }
        }

        if (jsonObject.has("Deficiencies")) {
            if (jsonObject.get("Deficiencies").toString().startsWith("[")) {
                visitationsModel.deficienciesArray = Gson().fromJson(jsonObject.get("Deficiencies").toString(), Array<VisitationsModel.DeficiencyModel>::class.java).toCollection(ArrayList())
            } else {
                visitationsModel.deficienciesArray.add(Gson().fromJson(jsonObject.get("Deficiencies").toString(), VisitationsModel.DeficiencyModel::class.java))
            }
        }
//        visitationsModel.completedVisitationsArray = visitationsModel.completedVisitationsArray.filter { s->s.DatePerformed. }
        //2019-07-05
        //  APPLY FILTERS TO OVERCOME GET VISITATION API ISSUES
//        var filteredMonth = if (visitationMonthsSpinner.selectedItemPosition==0) "0" else visitationMonthsSpinner.selectedItemPosition.toString()
//        var filteredYear = if (visitationYearFilterSpinner.selectedItemPosition==0) "0" else visitationYearFilterSpinner.selectedItem.toString()
//        if (visitationsModel.deficienciesArray.size>0) {
//            visitationsModel.deficienciesArray.removeIf { s -> s.DueDate.substring(5, 7).toInt() > filteredMonth.toInt() }
//            visitationsModel.deficienciesArray.removeIf { s -> s.DueDate.substring(0, 4).toInt() > filteredYear.toInt() }
//        }

        if (visitationsModel.completedVisitationsArray.size>0) {
            // Quarterly , Annual & Add Hoc
            if (!annualVisitationCheckBox.isChecked) {
                visitationsModel.completedVisitationsArray.removeIf { s -> s.VisitationTypeID==1}
            }
            if (!quarterlyOrOtherVisistationsCheckBox.isChecked) {
                visitationsModel.completedVisitationsArray.removeIf { s -> s.VisitationTypeID==2}
            }
            if (!adHocVisitationsCheckBox.isChecked) {
                visitationsModel.completedVisitationsArray.removeIf { s -> s.VisitationTypeID==3}
            }
//            visitationsModel.completedVisitationsArray.removeIf { s -> !s.DatePerformed.substring(5, 7).equals(filteredMonth) }
//            visitationsModel.completedVisitationsArray.removeIf { s -> !s.DatePerformed.substring(0, 4).equals(filteredYear) }
        }

//        visitationsModel.pendingVisitationsArray.removeIf { s->!s..substring(5,7).equals(filteredMonth)}
//        visitationsModel.pendingVisitationsArray.removeIf { s->!s.DatePerformed.substring(0,4).equals(filteredYear)}
//        if (!annualVisitationCheckBox.isChecked) {
//        }
        return visitationsModel
    }

    private fun loadClubCodes() {
        Log.v("url*******", ""+ Constants.getClubCodes)
        Log.v("VISITATION CLUB --- ",Constants.getClubCodes)
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Club Codes")
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getClubCodes,
                Response.Listener { response ->
                    var clubCodeModels = Gson().fromJson(response.toString(), Array<ClubCodeModel>::class.java)
                    allClubCodes.clear()
                    for (cc in clubCodeModels) {
                        allClubCodes.add(cc.clubcode)
                    }
                    loadFacilityNames()
//                    firstLoadingCompleted()
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading club codes")
        }))
    }

    fun loadSpecialists() {
        Log.v("ADHOC ALL SPECIAL --- ",Constants.getAllSpecialists + "")
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
                Response.Listener { response ->
                    Log.v("****response", response)
                    activity!!.runOnUiThread {
                        CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(java.util.ArrayList())

//                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread {
//                                        var specialistName = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
//                                        if (specialistName != null && specialistName.size > 0) {
//                                            requiredSpecialistName = specialistName[0].specialistname
//                                            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistName[0].accspecid
////                                            var firstName = requiredSpecialistName .substring(requiredSpecialistName .indexOf(",")+2,requiredSpecialistName .length)
////                                            var lastName = requiredSpecialistName .substring(0,requiredSpecialistName .indexOf(","))
////                                            var reformattedName = firstName + " " + lastName
////                                            adHocFacilitySpecialistButton.setText(reformattedName)
//                                        }
//                                        loadSpecialistName()
////                                        loadClubCodes()
//                                    }
//                                }, Response.ErrorListener {
//                            Log.v("error while loading", "error while loading facilities")
//                            Log.v("Loading error", "" + it.message)
//                        }))
                    }
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading specialists")
            Log.v("Loading error", "" + it.message)
        }))

    }

    private fun loadSpecialistName() {
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread {
//                        specialistArrayModel = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
                        specialistArrayModel = TypeTablesModel.getInstance().EmployeeList
                        var specMail = ApplicationPrefs.getInstance(context).loggedInUserEmail.substring(0,ApplicationPrefs.getInstance(context).loggedInUserEmail.indexOf("@")).lowercase()
                        if (specialistArrayModel != null && specialistArrayModel.size > 0) {
//                            requiredSpecialistName = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(ApplicationPrefs.getInstance(context).loggedInUserEmail.toLowerCase()) }[0].FullName
                            requiredSpecialistName = specialistArrayModel.filter { s -> s.Email.toLowerCase().startsWith(specMail)}[0].FullName
                            var positionID = specialistArrayModel.filter { s -> s.Email.toLowerCase().startsWith(specMail)}[0].PositionID
                            if (positionID.equals("1")) {
                                visitationSpecialistName.setText(requiredSpecialistName)
                            }
//                            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().equals(ApplicationPrefs.getInstance(context).loggedInUserEmail.toLowerCase()) }[0].NTLogin
                            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().startsWith(specMail)}[0].NTLogin
                        }
                        loadClubCodes()
//                    }
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading facilities")
//            Log.v("Loading error", "" + it.message)
//        }))

    }


    private fun loadSpecialistDetails() {
//        FirebaseCrashlytics.getInstance().setCustomKey("Screen", "Visitation Planning Screen")
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Specialist Details")
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistDetails + ApplicationPrefs.getInstance(context).loggedInUserEmail,
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        specialistModel = Gson().fromJson(response.toString(), Array<CsiSpecialistDetails>::class.java).toCollection(ArrayList())
                        if (specialistModel != null && specialistModel.size > 0) {
                            visitationSpecialistName.setText(specialistModel[0].specialistfname.toLowerCase().capitalize()+" "+specialistModel[0].specialistlname.toLowerCase().capitalize() )
                            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistModel[0].accspecid
                            ApplicationPrefs.getInstance(activity).loggedInUserFullName = specialistModel[0].specialistfname.toLowerCase().capitalize()+" "+specialistModel[0].specialistlname.toLowerCase().capitalize()
                        }
                    }
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading Specialist Details")
            Log.v("Loading error", "" + it.message)
        }))
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
////        if (context is OnFragmentInteractionListener) {
////            mListener = context
////        } else {
////            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
////        }
//    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
    }


    fun determineVisitationTypeAndStatusUpdatedLogic (facAnnualMonth : Int,facId:Int, clubCode:Int) : Pair<VisitationTypes,VisitationStatus> {
        val c = Calendar.getInstance()
        val month = c.get(Calendar.MONTH)+1
        when (facAnnualMonth) {
            1 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            2 -> {
                when(month) {
                    2 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            3 -> {
                when(month) {
                    3 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            4 -> {
                when(month) {
                    4 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            5 -> {
                when(month) {
                    5 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            6 -> {
                when(month) {
                    6 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            7 -> {
                when(month) {
                    7 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            8 -> {
                when(month) {
                    8 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            9 -> {
                when(month) {
                    9 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            10 -> {
                when(month) {
                    10 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    11 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            11 -> {
                when(month) {
                    11 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    1 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            12 -> {
                when(month) {
                    12 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    1 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
        }
        return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
    }


    fun determineVisitationTypeAndStatus (facAnnualMonth : Int,facId:Int, clubCode:Int) : Pair<VisitationTypes,VisitationStatus> {
        val c = Calendar.getInstance()
        val month = c.get(Calendar.MONTH)+1
        when (facAnnualMonth) {
            1 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    3 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<3}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 5..6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    8,9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    11,12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            2 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    4 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<4}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 6 ..7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    9,10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            3 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    5 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<5}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 7..8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    10,11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                }
            }
            4 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    6 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<6}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 8..9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    11,12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            5 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    7 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<7}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 9..10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            6 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    8 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<8}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 10..11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                }
            }
            7 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    9 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<9}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    in 11..12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            8 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    10 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<10}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                }
            }
            9 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    11 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<11}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    12 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                }
            }
            10 -> {
                when(month) {
                    1 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    11 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                    12 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual) && s.completionmonth<12}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                }
            }
            11 -> {
                when(month) {
                    1 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual)}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    2 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    12 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                }
            }
            12 -> {
                when(month) {
                    2 -> {
                        if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==facId && s.clubcode==clubCode && s.visitationtype.equals(VisitationTypes.Annual)}.isNotEmpty()) {
                            return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                        } else {
                            return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                        }
                    }
                    3 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    4 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    5 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    6 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    7 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    8 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    9 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.NotStarted)
                    10 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    11 -> return Pair(VisitationTypes.Quarterly,VisitationStatus.Overdue)
                    12 -> return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
                    1 -> return Pair(VisitationTypes.Annual,VisitationStatus.Overdue)
                }
            }
        }
        return Pair(VisitationTypes.Annual,VisitationStatus.NotStarted)
    }


    fun getTextColor(strValue : String) : Int {
        when (strValue) {
            VisitationStatus.NotStarted.toString().replace("Not", "Not ") -> return Color.BLACK
            VisitationStatus.InProgress.toString().replace("In", "In ") -> return Color.rgb(0  ,200,0)
            VisitationStatus.Overdue.toString() -> return Color.RED
            VisitationStatus.Overdue.toString()+" / In Progress" -> return Color.BLUE
        }
        return Color.BLUE
    }

    inner class VisitationPlanningAdapter : BaseAdapter {

        private var visitationPlanningModelList = VisitationsModel()
        private var context: Context? = null

        constructor(context: Context?, visitationsModel: VisitationsModel) : super() {
            this.visitationPlanningModelList = visitationsModel
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: VisitationPlanningViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.visitation_planning_list_item, parent, false)
                vh = VisitationPlanningViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as VisitationPlanningViewHolder
            }

            vh.loadBtn.text = "LOAD VISITATION"
            vh.emailPDFBtn.text = "EMAIL PDF"
            vh.emailPDFBtn.visibility = View.GONE
            if (position < visitationPlanningModelList.pendingVisitationsArray.size && visitationPlanningModelList.pendingVisitationsArray.size > 0) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].BusinessName
                vh.facilityNoValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].FACNo
                vh.visitationStatusTextView.text = "Status:"
                vh.visitationTypeValueTextView.visibility = View.VISIBLE
                vh.visitationTypeTextView.visibility = View.VISIBLE


                var visitationTypeAndStatus = determineVisitationTypeAndStatus(visitationPlanningModelList.pendingVisitationsArray[position].FacilityAnnualInspectionMonth.toInt(),visitationPlanningModelList.pendingVisitationsArray[position].FacID.toInt(),visitationPlanningModelList.pendingVisitationsArray[position].ClubCode.toInt())
                vh.visitationTypeValueTextView.text = visitationTypeAndStatus.first.toString()
                val facAnnualMonth = visitationPlanningModelList.pendingVisitationsArray[position].FacilityAnnualInspectionMonth.toInt()
                val c = Calendar.getInstance()
                val month = c.get(Calendar.MONTH)+1
                var overrideOverdueFlag = false
//                if (visitationTypeAndStatus.second == VisitationStatus.Overdue && facAnnualMonth-month==1) {
//                    overridOverdue=VisitationStatus.NotStarted
//                    overrideOverdueFlag = true
//                }
                if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
                    if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt() && s.clubcode == visitationPlanningModelList.pendingVisitationsArray[position].ClubCode.toInt() && s.facannualinspectionmonth == visitationPlanningModelList.pendingVisitationsArray[position].FacilityAnnualInspectionMonth.toInt() && s.inspectioncycle == visitationPlanningModelList.pendingVisitationsArray[position].InspectionCycle && s.visitationtype == visitationTypeAndStatus.first.toString() }.isNotEmpty()) {
                        if (visitationTypeAndStatus.second == VisitationStatus.NotStarted) {
                            vh.visitationStatusValueTextView.text = VisitationStatus.InProgress.toString().replace("In", "In ")
                        } else {
                            vh.visitationStatusValueTextView.text = visitationTypeAndStatus.second.toString() + " / In Progress"
                        }
                    } else {
                        if (visitationTypeAndStatus.second==VisitationStatus.Overdue && overrideOverdueFlag)
                                vh.visitationStatusValueTextView.text = overridOverdue.toString().replace("Not", "Not ")
                        else
                        vh.visitationStatusValueTextView.text = visitationTypeAndStatus.second.toString().replace("Not", "Not ")
                    }
                } else {
                    if (visitationTypeAndStatus.second==VisitationStatus.Overdue && overrideOverdueFlag)
                        vh.visitationStatusValueTextView.text = overridOverdue.toString().replace("Not", "Not ")
                    else
                        vh.visitationStatusValueTextView.text = visitationTypeAndStatus.second.toString().replace("Not", "Not ")
                }


//                if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt() && s.clubcode==visitationPlanningModelList.pendingVisitationsArray[position].ClubCode.toInt() && s.visitationtype.equals(visitationTypeAndStatus.first.toString())}.isNotEmpty()){
//                    vh.initialContractDateTextView.text = "Last Visitation Date:"
//                    vh.initialContractDateValueTextView.text = PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.facid==visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt() && s.clubcode==visitationPlanningModelList.pendingVisitationsArray[position].ClubCode.toInt() && s.visitationtype.equals(visitationTypeAndStatus.first.toString())}.sortedByDescending{it.completiondate}[0].completiondate.apiToAppFormatMMDDYYYY()
//                } else {

                    if (visitationPlanningModelList.completedVisitationsArray.filter { s->s.FACNo==visitationPlanningModelList.pendingVisitationsArray[position].FACNo && s.ClubCode==visitationPlanningModelList.pendingVisitationsArray[position].ClubCode}.isNotEmpty()) {
                        vh.initialContractDateTextView.text = "Last Visitation Date:"
                        vh.initialContractDateValueTextView.text = visitationPlanningModelList.completedVisitationsArray.filter { s->s.FACNo==visitationPlanningModelList.pendingVisitationsArray[position].FACNo && s.ClubCode==visitationPlanningModelList.pendingVisitationsArray[position].ClubCode}[0].DatePerformed.apiToAppFormatMMDDYYYY()
                    } else {
//                        vh.initialContractDateTextView.text = "Initial Contract Date:"
//                        vh.initialContractDateValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].ContractInitialDate.apiToAppFormatMMDDYYYY()
                        vh.initialContractDateTextView.text = "Annual Visitation Month:"
                        vh.initialContractDateValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].FacilityAnnualInspectionMonth.toInt().monthNoToName()
                    }
//                }

                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt(), visitationPlanningModelList.pendingVisitationsArray[position].ClubCode,false,visitationTypeAndStatus.first)
                })
                }

            else if (position >= visitationPlanningModelList.pendingVisitationsArray.size && position < visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].BusinessName
                vh.facilityNoValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].FACNo
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].DatePerformed.apiToAppFormatMMDDYYYY() + "  (ID: " + visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].visitationID + ")"
                vh.visitationStatusValueTextView.text = "Completed"
                vh.visitationStatusValueTextView.setTextColor(Color.BLACK)
                vh.initialContractDateTextView.text = "Visitation Date & ID:"
                vh.visitationStatusTextView.text = "Status:"
                vh.loadBtn.text = "VIEW  PDF"
                vh.emailPDFBtn.text = "EMAIL PDF"
                vh.emailPDFBtn.visibility = View.VISIBLE
                visitationID = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].visitationID
                vh.loadBtn.tag = visitationID.toInt()
                vh.emailPDFBtn.tag = visitationID.toInt()
                vh.visitationTypeValueTextView.visibility = View.GONE
                vh.visitationTypeTextView.visibility = View.GONE
                if (visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].VisitationTypeID>0){
                    vh.visitationTypeValueTextView.visibility = View.VISIBLE
                    vh.visitationTypeTextView.visibility = View.VISIBLE
                    when (visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].VisitationTypeID) {
                        1 -> vh.visitationTypeValueTextView.text = "Annual"
                        2 -> vh.visitationTypeValueTextView.text = "Quarterly"
                        3 -> vh.visitationTypeValueTextView.text = "AdHoc"
                    }

                }

//                else if (PRGDataModel.getInstance().tblPRGCompletedVisitations[0].recordid > -1){
//                    if (PRGDataModel.getInstance().tblPRGCompletedVisitations.filter { s->s.visitationid==visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].visitationID }.isNotEmpty()){
//                        vh.visitationTypeValueTextView.visibility = View.VISIBLE
//                        vh.visitationTypeTextView.visibility = View.VISIBLE
//                        vh.visitationTypeValueTextView.text = PRGDataModel.getInstance().tblPRGCompletedVisitations.filter {s->s.visitationid==visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].visitationID}.sortedByDescending { it.recordid }[0].visitationtype
//                    }
//                }

                visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].FacilityAnnualInspectionMonth
                vh.loadBtn.setOnClickListener({
                    Constants.visitationIDForPDF = vh.loadBtn.tag.toString()
                    Constants.specialistEmailForPDF = ""
                    Constants.facNoForPDF = ""
                    Constants.facNameForPDF = ""
                    Constants.typeForPDF = ""
                    launchNextAction(true)
                })
                vh.emailPDFBtn.setOnClickListener({
                    Constants.visitationIDForPDF = vh.loadBtn.tag.toString()
                    var ntlogin = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].insertBy1
                    Constants.specialistEmailForPDF = TypeTablesModel.getInstance().EmployeeList.filter { s->s.NTLogin==ntlogin }[0].Email
                    launchNextAction(true)
                })
            } else if (position >= visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].BusinessName
                vh.facilityNoValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FACNo
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].DueDate.apiToAppFormatMMDDYYYY()
                val dueDate = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].DueDate.substring(0,10)
                val format = SimpleDateFormat("yyyy-MM-dd");
                try {
                    val date = format.parse(dueDate);
                    vh.visitationStatusTextView.text = "Status:"
                    if (Date()<=date) {
                        vh.visitationStatusValueTextView.text = VisitationStatus.NotStarted.toString().replace("Not","Not ")
                    } else {
                        vh.visitationStatusValueTextView.text = VisitationStatus.Overdue.toString()
                    }
                } catch (e : ParseException) {
                    e.printStackTrace();
                }
                vh.visitationTypeValueTextView.text = "Deficiency"
                vh.visitationStatusValueTextView.setTextColor(Color.BLACK)
                vh.initialContractDateTextView.text = "Deficiency Due Date:"
                vh.visitationTypeTextView.text = "Type:"
                vh.visitationTypeValueTextView.visibility = View.VISIBLE
                vh.visitationTypeTextView.visibility = View.VISIBLE


                if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
                    if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FACNo.toInt() && s.clubcode == visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].ClubCode.toInt() && s.facannualinspectionmonth == visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FacilityAnnualInspectionMonth.toInt() && s.inspectioncycle == visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].InspectionCycle && s.visitationtype == VisitationTypes.Deficiency.toString() }.isNotEmpty()) {
                        if (vh.visitationStatusValueTextView.text == VisitationStatus.NotStarted.toString().replace("Not","Not ")) {
                            vh.visitationStatusValueTextView.text = VisitationStatus.InProgress.toString().replace("In", "In ")
                        } else {
                            vh.visitationStatusValueTextView.text = vh.visitationStatusValueTextView.text.toString() + " / In Progress"
                        }
                    }
                }
                vh.loadBtn.setOnClickListener {
                    getFullFacilityDataFromAAA(visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FACNo.toInt(), visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].ClubCode,false,VisitationTypes.Deficiency)
                }
            }
            if (vh.visitationTypeValueTextView.isVisible) {
                when (vh.visitationTypeValueTextView.text) {
                    VisitationTypes.Annual.toString() -> vh.listBkg.setBackgroundColor(Color.WHITE)
                    VisitationTypes.Quarterly.toString() -> vh.listBkg.setBackgroundColor(Color.rgb(204, 255, 204))
                    VisitationTypes.Deficiency.toString() -> vh.listBkg.setBackgroundColor(Color.rgb(255, 229, 204))
                }
            }
//              if (reviewFilters(vh.visitationTypeValueTextView.text.toString(),vh.visitationStatusValueTextView.text.toString())) {
//                  vh.listBkg.visibility = View.GONE
//                  totalVisitations -= 1
////                  resultsCount.text = "Filtered Visitations --> ( " + totalVisitations +" )"
//              } else {
//                  vh.listBkg.visibility = View.VISIBLE
//              }
            vh.visitationStatusValueTextView.setTextColor(getTextColor(vh.visitationStatusValueTextView.text.toString()))
            return view
        }

        fun reviewFilters(visitationType : String, visitationStatus : String) : Boolean {
            var hideVisitation = false
            if (!annualVisitationCheckBox.isChecked && visitationType.equals(VisitationTypes.Annual.toString())) return true
            if (!quarterlyOrOtherVisistationsCheckBox.isChecked && visitationType.equals(VisitationTypes.Quarterly.toString())) return true
            if (!adHocVisitationsCheckBox.isChecked && visitationType.equals(VisitationTypes.AdHoc.toString())) return true
            if (!deficienciesCheckBox.isChecked && visitationType.equals(VisitationTypes.Deficiency.toString())) return true
            if (!pendingCheckBox.isChecked && visitationStatus.equals("Not Started")) return true
            if (!inProgressCheckBox.isChecked && visitationStatus.contains("Progress",true)) return true
            if (!overdueCheckBox.isChecked && visitationStatus.contains("Overdue",true)) return true
            if (!completedCheckBox.isChecked && visitationStatus.equals("Completed")) return true
            return hideVisitation
        }

        override fun getItem(position: Int): Any {
            // return item at 'position'
            return visitationPlanningModelList
        }

        override fun getItemId(position: Int): Long {
            // return item Id by Long datatype
            return position.toLong()
        }

        override fun getCount(): Int {
            // return quantity of the list
            return visitationPlanningModelList.completedVisitationsArray.size + visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.deficienciesArray.size
        }
    }

    inner class VisitationPlanningNewAdapter : BaseAdapter {

        private var visitationPlanningModelList = VisitationsModel()
        private var context: Context? = null

        constructor(context: Context?, visitationsModel: VisitationsModel) : super() {
            this.visitationPlanningModelList = visitationsModel
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: VisitationPlanningViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.visitation_planning_list_item, parent, false)
                vh = VisitationPlanningViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as VisitationPlanningViewHolder
            }

            vh.loadBtn.text = "LOAD VISITATION"
            vh.emailPDFBtn.visibility = View.GONE

            vh.facilityNameValueTextView.text = visitationPlanningModelList.listArray[position].BusinessName
            vh.facilityNoValueTextView.text = visitationPlanningModelList.listArray[position].FACNo
            if (visitationPlanningModelList.listArray[position].FACNo.equals("3114")) {
                Log.v("HERE ---->"," START TRACE -----")
            }
            if (visitationPlanningModelList.listArray[position].VisitationStatus.contains("Not Started") || visitationPlanningModelList.listArray[position].VisitationStatus.contains("In Progress") || visitationPlanningModelList.listArray[position].VisitationStatus.contains("Overdue")) {
                vh.visitationStatusTextView.text = "Status:"
                vh.visitationStatusValueTextView.text = visitationPlanningModelList.listArray[position].VisitationStatus
                vh.visitationTypeValueTextView.visibility = View.VISIBLE
                vh.visitationTypeTextView.visibility = View.VISIBLE
                vh.visitationTypeValueTextView.text = visitationPlanningModelList.listArray[position].VisitationType
//                vh.initialContractDateTextView.text = "Initial Contract Date:"
//                vh.initialContractDateValueTextView.text = visitationPlanningModelList.listArray[position].ContractInitialDate.apiToAppFormatMMDDYYYY()
                vh.initialContractDateTextView.text = "Annual Visitation Month:"
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.listArray[position].FacilityAnnualInspectionMonth.toInt().monthNoToName()
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.listArray[position].FACNo.toInt(), visitationPlanningModelList.listArray[position].ClubCode,false,VisitationTypes.valueOf(visitationPlanningModelList.listArray[position].VisitationType))
                })
            }
            else if (visitationPlanningModelList.listArray[position].VisitationStatus.contains("Completed")) {
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.listArray[position].CompletionDate + "  (ID: " + visitationPlanningModelList.listArray[position].VisitationID + ")"
                vh.visitationStatusValueTextView.text = "Completed"
                vh.visitationStatusValueTextView.setTextColor(Color.BLACK)
                vh.initialContractDateTextView.text = "Visitation Date & ID :"
                vh.visitationStatusTextView.text = "Status:"
                vh.loadBtn.text = "VIEW  PDF"
                vh.emailPDFBtn.text = "EMAIL PDF"
                vh.emailPDFBtn.visibility = View.VISIBLE
                visitationID = visitationPlanningModelList.listArray[position].VisitationID
                vh.loadBtn.tag = visitationID.toInt()
                vh.emailPDFBtn.tag = visitationID.toInt()
                vh.visitationTypeValueTextView.visibility = View.VISIBLE
                vh.visitationTypeTextView.visibility = View.VISIBLE
                vh.visitationTypeValueTextView.text = visitationPlanningModelList.listArray[position].VisitationType
                vh.loadBtn.setOnClickListener({
                    Constants.visitationIDForPDF = vh.loadBtn.tag.toString()
                    Constants.specialistEmailForPDF = ""
                    launchNextAction(true)
                })
                vh.emailPDFBtn.setOnClickListener({
                    Constants.visitationIDForPDF = vh.loadBtn.tag.toString()
                    var ntlogin = visitationPlanningModelList.listArray[position].insertBy1
                    Constants.specialistEmailForPDF = TypeTablesModel.getInstance().EmployeeList.filter { s->s.NTLogin==ntlogin }[0].Email
                    Constants.facNoForPDF = visitationPlanningModelList.listArray[position].FACNo
                    Constants.facNameForPDF = visitationPlanningModelList.listArray[position].BusinessName
                    Constants.typeForPDF = visitationPlanningModelList.listArray[position].VisitationType
                    launchNextAction(true)
                })
            } else if (visitationPlanningModelList.listArray[position].VisitationType.contains("Deficiency")) {
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.listArray[position].DueDate
                vh.visitationTypeValueTextView.text = "Deficiency"
                vh.visitationStatusValueTextView.setTextColor(Color.BLACK)
                vh.initialContractDateTextView.text = "Deficiency Due Date:"
                vh.visitationTypeTextView.text = "Type:"
                vh.visitationTypeValueTextView.visibility = View.VISIBLE
                vh.visitationTypeTextView.visibility = View.VISIBLE
                vh.visitationStatusValueTextView.text = visitationPlanningModelList.listArray[position].VisitationStatus
                vh.loadBtn.setOnClickListener {
                    getFullFacilityDataFromAAA(visitationPlanningModelList.listArray[position].FACNo.toInt(), visitationPlanningModelList.listArray[position].ClubCode,false,VisitationTypes.Deficiency)
                }
            }
            if (vh.visitationTypeValueTextView.isVisible) {
                if (vh.visitationTypeValueTextView.text.contains("Deficiency")) vh.listBkg.setBackgroundColor(Color.rgb(255, 229, 204))
                else if (vh.visitationTypeValueTextView.text.contains("Annual")) vh.listBkg.setBackgroundColor((Color.WHITE))
                else if (vh.visitationTypeValueTextView.text.contains("Quarterly")) vh.listBkg.setBackgroundColor(Color.rgb(255, 229, 204))
            }
            Log.v("Facility NO -->",visitationPlanningModelList.listArray[position].FACNo)

                if (reviewFilters(vh.visitationTypeValueTextView.text.toString(), vh.visitationStatusValueTextView.text.toString())) {
                    vh.listBkg.visibility = View.GONE
                    totalVisitations -= 1
//                  resultsCount.text = "Filtered Visitations --> ( " + totalVisitations +" )"
                }

            vh.visitationStatusValueTextView.setTextColor(getTextColor(vh.visitationStatusValueTextView.text.toString()))
            return view
        }

        fun reviewFilters(visitationType : String, visitationStatus : String) : Boolean {
            var hideVisitation = false
            if (!annualVisitationCheckBox.isChecked && visitationType.contains(VisitationTypes.Annual.toString())) return true
            if (!quarterlyOrOtherVisistationsCheckBox.isChecked && visitationType.contains(VisitationTypes.Quarterly.toString())) return true
            if (!adHocVisitationsCheckBox.isChecked && visitationType.contains(VisitationTypes.AdHoc.toString())) return true
            if (!deficienciesCheckBox.isChecked && visitationType.contains(VisitationTypes.Deficiency.toString())) return true
            if (!pendingCheckBox.isChecked && visitationStatus.contains("Not Started")) return true
            if (!inProgressCheckBox.isChecked && visitationStatus.contains("Progress",true)) return true
            if (!overdueCheckBox.isChecked && visitationStatus.contains("Overdue",true)) return true
            if (!completedCheckBox.isChecked && visitationStatus.equals("Completed")) return true
            return hideVisitation
        }

        override fun getItem(position: Int): Any {
            // return item at 'position'
            return visitationPlanningModelList
        }

        override fun getItemId(position: Int): Long {
            // return item Id by Long datatype
            return position.toLong()
        }

        override fun getCount(): Int {
            // return quantity of the list
            return visitationPlanningModelList.listArray.size
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun launchNextAction(isCompleted : Boolean){
        if (isCompleted) {
            recordsProgressView.visibility = View.VISIBLE
            if (Constants.specialistEmailForPDF.equals("")) {
                webView!!.clearCache(true)
                webView!!.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (view?.getTitle().equals("")) {
                            view?.reload();
                        }
                    }
                }
                webCardView.visibility = View.VISIBLE
//                webView.visibility = View.GONE
                pdfName.text = "Visitation PDF For Specialist (ID: " + Constants.visitationIDForPDF + ")"
                exitPDFDialogeBtn.setOnClickListener {
                    webView.loadUrl("about:blank")
                    recordsProgressView.visibility = View.GONE
                    webCardView.visibility = View.GONE
                }
                    Log.v("DOWNLOAD ",Constants.getPDF + Constants.visitationIDForPDF)
//                RetrievePDFFromURL(pdfView).execute(Constants.getPDF + Constants.visitationIDForPDF)
//                RetrievePDFFromURL(pdfView).execute("https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf")

                webView.requestFocus()
                webView.settings.javaScriptEnabled = true
                webView.settings.loadWithOverviewMode = true;
                webView.settings.useWideViewPort = true;
                webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;
                webView.settings.setSupportZoom(true);
                webView.settings.builtInZoomControls = true;
                webView.settings.allowFileAccess = true;
                webView.settings.allowContentAccess = true;
                webView.settings.domStorageEnabled = true;
                webView.settings.allowFileAccessFromFileURLs = true;
                webView.settings.allowUniversalAccessFromFileURLs = true;

//              var url = URLEncoder.encode(Constants.getPDF + Constants.visitationIDForPDF, "UTF-8" );
                webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + Constants.getPDF + Constants.visitationIDForPDF)
//                webView.loadUrl(Constants.getPDF + Constants.visitationIDForPDF)
//                webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url)
                webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                }
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://docs.google.com/gview?embedded=true&url=" + Constants.getPDF + Constants.visitationIDForPDF))
//                startActivity(browserIntent)
            } else {
                sendCompletedPDF()
            }
        } else {
            IndicatorsDataModel.getInstance().init()

            ////

            Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getPRGVisitationsLog,
                    Response.Listener { response ->
                        requireActivity().runOnUiThread {
                            if (!response.toString().replace(" ","").equals("[]")) {
                                PRGDataModel.getInstance().tblPRGVisitationsLog = Gson().fromJson(response.toString(), Array<PRGVisitationsLog>::class.java).toCollection(ArrayList())
                            } else {
                                var item = PRGVisitationsLog()
                                item.recordid=-1
                                PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                            }
                            AdjustIndicatorsAndStartActivity()
                        }
                    }, Response.ErrorListener {
                        Log.v("Loading PRG Data error", "" + it.message)
                        var item = PRGVisitationsLog()
                        item.recordid=-1
                        PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                        it.printStackTrace()
                        AdjustIndicatorsAndStartActivity()
            }))
            /////
        }
    }

    class RetrievePDFFromURL(pdfView: PDFView) :
            AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView

        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params.get(0))

                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                // on below line we are checking if the response
                // is successful with the help of response code
                // 200 response code means response is successful
                if (urlConnection.responseCode == 200) {
                    // on below line we are initializing our input stream
                    // if the response is successful.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                // on below line we are simply printing
                // our exception and returning null
                e.printStackTrace()
                return null;
            }
            // on below line we are returning input stream.
            return inputStream;
        }

        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()

        }
    }

    fun AdjustIndicatorsAndStartActivity () {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Starting Activity")
        if (PRGDataModel.getInstance().tblPRGVisitationsLog[0].recordid > -1) {
            if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }.isNotEmpty()) {
                IndicatorsDataModel.getInstance().markVisitedScreen(PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }.sortedByDescending { it.changedate }[0].visitedscreens)
            }
        }
        var intent = Intent(context, com.inspection.FormsActivity::class.java)
        startActivity(intent)
    }
    fun getFacilityPRGData(isCompleted : Boolean) {
        PRGDataModel.getInstance().tblPRGVisitationHeader.clear()
        PRGDataModel.getInstance().tblPRGFacilitiesPhotos.clear()
        PRGDataModel.getInstance().tblPRGLogChanges.clear()
        PRGDataModel.getInstance().tblPRGFacilityDetails.clear()
        PRGDataModel.getInstance().tblPRGPersonnelDetails.clear()
        PRGDataModel.getInstance().tblPRGRepairDiscountFactors.clear()
        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}",
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        if (!response.toString().replace(" ","").equals("[ ]")) {
                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())
                        } else {
                            var item = PRGFacilityPhotos()
                            item.photoid = -1
                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos.add(item)
                        }
                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}&userId="+ApplicationPrefs.getInstance(context).loggedInUserID,
                                Response.Listener { response ->
                                    requireActivity().runOnUiThread {
                                        if (!response.toString().replace(" ","").equals("[]")) {
                                            PRGDataModel.getInstance().tblPRGLogChanges = Gson().fromJson(response.toString(), Array<PRGLogChanges>::class.java).toCollection(ArrayList())
                                        } else {
                                            var item = PRGLogChanges()
                                            item.recordid=-1
                                            PRGDataModel.getInstance().tblPRGLogChanges.add(item)
                                        }
                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getVisitationHeader + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}",
                                                Response.Listener { response ->
                                                    requireActivity().runOnUiThread {
                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                            PRGDataModel.getInstance().tblPRGVisitationHeader= Gson().fromJson(response.toString(), Array<PRGVisitationHeader>::class.java).toCollection(ArrayList())
                                                        } else {
                                                            var item = PRGVisitationHeader()
                                                            item.recordid=-1
                                                            PRGDataModel.getInstance().tblPRGVisitationHeader.add(item)
                                                        }
                                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getRepairDiscountFactors + "${FacilityDataModel.getInstance().clubCode}",
                                                                Response.Listener { response ->
                                                                    requireActivity().runOnUiThread {
                                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                                            PRGDataModel.getInstance().tblPRGRepairDiscountFactors= Gson().fromJson(response.toString(), Array<PRGRepairDiscountFactors>::class.java).toCollection(ArrayList())
                                                                        } else {
                                                                            var item = PRGRepairDiscountFactors()
                                                                            item.clubcode= FacilityDataModel.getInstance().clubCode
                                                                            PRGDataModel.getInstance().tblPRGRepairDiscountFactors.add(item)
                                                                        }
                                                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getPersonnelDetails + "${FacilityDataModel.getInstance().clubCode}&facNum="+FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                Response.Listener { response ->
                                                                                    requireActivity().runOnUiThread {
                                                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                                                            PRGDataModel.getInstance().tblPRGPersonnelDetails= Gson().fromJson(response.toString(), Array<PRGPersonnelDetails>::class.java).toCollection(ArrayList())
                                                                                        } else {
                                                                                            var item = PRGPersonnelDetails()
                                                                                            item.clubcode= FacilityDataModel.getInstance().clubCode.toInt()
                                                                                            item.facnum = FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                            PRGDataModel.getInstance().tblPRGPersonnelDetails.add(item)
                                                                                        }
                                                                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getPRGFacilityDetails + "${FacilityDataModel.getInstance().clubCode}&facNum="+FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                Response.Listener { response ->
                                                                                                    requireActivity().runOnUiThread {
                                                                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDetails= Gson().fromJson(response.toString(), Array<PRGFacilityDetails>::class.java).toCollection(ArrayList())
                                                                                                        } else {
                                                                                                            var item = PRGFacilityDetails()
                                                                                                            item.clubcode= FacilityDataModel.getInstance().clubCode.toInt()
                                                                                                            item.facid = FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                                            item.napanumber = ""
                                                                                                            item.nationalnumber = ""
                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDetails.add(item)
                                                                                                        }
                                                                                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getFacilityDirectors + "${FacilityDataModel.getInstance().clubCode}&facNum="+FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                                Response.Listener { response ->
                                                                                                                    requireActivity().runOnUiThread {
                                                                                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDirectors= Gson().fromJson(response.toString(), Array<PRGFacilityDirectors>::class.java).toCollection(ArrayList())
                                                                                                                        } else {
                                                                                                                            var item = PRGFacilityDirectors()
                                                                                                                            item.clubcode= FacilityDataModel.getInstance().clubCode.toInt()
                                                                                                                            item.facnum = FacilityDataModel.getInstance().tblFacilities[0].FACNo
                                                                                                                            item.specialistid = -1
                                                                                                                            item.directorid = -1
                                                                                                                            item.directoremail = ""
                                                                                                                            PRGDataModel.getInstance().tblPRGFacilityDirectors.add(item)
                                                                                                                        }
                                                                                                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getFacilityHolidays + "${FacilityDataModel.getInstance().clubCode}&facNum="+FacilityDataModel.getInstance().tblFacilities[0].FACNo,
                                                                                                                                Response.Listener { response ->
                                                                                                                                    requireActivity().runOnUiThread {
                                                                                                                                        if (!response.toString().replace(" ","").equals("[]")) {
                                                                                                                                            PRGDataModel.getInstance().tblPRGFacilityShopHolidayTimes= Gson().fromJson(response.toString(), Array<PRGFacilityShopHolidayTimes>::class.java).toCollection(ArrayList())
                                                                                                                                        } else {
                                                                                                                                            var item = PRGFacilityShopHolidayTimes()
                                                                                                                                            item.clubcode= FacilityDataModel.getInstance().clubCode.toString()
                                                                                                                                            item.FacNum = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                                                                                                                                            item.comments = "-1"
                                                                                                                                            item.startdate = ""
                                                                                                                                            item.enddate = ""
                                                                                                                                            PRGDataModel.getInstance().tblPRGFacilityShopHolidayTimes.add(item)
                                                                                                                                        }
                                                                                                                                        launchNextAction(isCompleted)
                                                                                                                                    }
                                                                                                                                }, Response.ErrorListener {
                                                                                                                            Log.v("Loading PRG Data error", "" + it.message)
//                                                                                                            launchNextAction(isCompleted)
                                                                                                                            it.printStackTrace()
                                                                                                                        }))                                                                                                    }
                                                                                                                }, Response.ErrorListener {
                                                                                                            Log.v("Loading PRG Data error", "" + it.message)
//                                                                                                            launchNextAction(isCompleted)
                                                                                                            it.printStackTrace()
                                                                                                        }))                                                                                                    }
                                                                                                }, Response.ErrorListener {
                                                                                            Log.v("Loading PRG Data error", "" + it.message)
                                                                                            launchNextAction(isCompleted)
                                                                                            it.printStackTrace()
                                                                                        }))
                                                                                    }
                                                                                }, Response.ErrorListener {
                                                                            Log.v("Loading PRG Data error", "" + it.message)
                                                                            it.printStackTrace()
                                                                        }))
                                                                    }
                                                                }, Response.ErrorListener {
                                                            Log.v("Loading PRG Data error", "" + it.message)
                                                            it.printStackTrace()
                                                        }))
                                                    }
                                                }, Response.ErrorListener {
                                            Log.v("Loading PRG Data error", "" + it.message)
//                                            launchNextAction(isCompleted)
                                            it.printStackTrace()
                                        }))
//                                        launchNextAction(isCompleted)
                                    }
                                }, Response.ErrorListener {
                            Log.v("Loading PRG Data error", "" + it.message)
//                            launchNextAction(isCompleted)
                            it.printStackTrace()
                        }))

                    }
                }, Response.ErrorListener {
            Log.v("Loading PRG Data error", "" + it.message)
            it.printStackTrace()
        }))
    }

    fun getTypeTables() {
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
        recordsProgressView.visibility = View.VISIBLE
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("&&&&&*(*", "failed with exception : " + e!!.message)
                activity!!.runOnUiThread {
                    Utility.showMessageDialog(activity, "Retrieve Data Error", e.message)
                }
            }
            override fun onResponse(call: Call, response: okhttp3.Response) {

                var responseString = response!!.body!!.string()
                if (responseString.toString().contains("returnCode>1<", false)) {
                    activity!!.runOnUiThread {
                        Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                        recordsProgressView.visibility = View.GONE
                    }
                } else {
//                    var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                    var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")))
                    var jsonObj = obj.getJSONObject("responseXml")
                    TypeTablesModel.setInstance(Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java))
                    (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
                        TypeTablesModel.getInstance().EmployeeList[it].FullName = TypeTablesModel.getInstance().EmployeeList[it].FirstName + " " + TypeTablesModel.getInstance().EmployeeList[it].LastName
                    }

                    //HERE
//                    var specialistName = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
//                    var specialistName = TypeTablesModel.getInstance().EmployeeList
//                    if (specialistName != null && specialistName.size > 0) {
//
//                        requiredSpecialistName = specialistName.filter { s->s.Email.equals(ApplicationPrefs.getInstance(context).loggedInUserEmail)}[0].LastName + " " + specialistName.filter { s->s.Email.equals(ApplicationPrefs.getInstance(context).loggedInUserEmail)}[0].FirstName
////                        ApplicationPrefs.getInstance(activity).loggedInUserID = specialistName[0].NTLogin
////                        for (sn in specialistName){
////                            specialistClubCodes.add(sn.)
////                        }
////                                            var firstName = requiredSpecialistName .substring(requiredSpecialistName .indexOf(",")+2,requiredSpecialistName .length)
////                                            var lastName = requiredSpecialistName .substring(0,requiredSpecialistName .indexOf(","))
////                                            var reformattedName = firstName + " " + lastName
////                                            visitationSpecialistName.setText(reformattedName)
////                                            visitationSpecialistName.setText(requiredSpecialistName)
//                    }
                    activity!!.runOnUiThread {
                        loadSpecialistName()
                    }
                }
            }
        })
    }

    fun getFullFacilityDataFromAAA(facilityNumber: Int, clubCode: String,isCompleted : Boolean,visitationType : VisitationTypes) {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Facility Details")
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
        var request2 = okhttp3.Request.Builder().url(String.format(Constants.getFacilityData+Utility.getLoggingParameters(activity, 0, "Load Visitations ..."), facilityNumber, clubCode)).build()
        this.clubCode = clubCode
        if (isCompleted) {
            progressBarText.text = "Generating PDF ..."
        } else {
            progressBarText.text = "Loading ..."
        }
        recordsProgressView.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity!!.runOnUiThread {
                    Utility.showMessageDialog(activity, "Retrieve Data Error", "Origin ERROR Connection Error. Please check internet connection - " + e?.message)
                    recordsProgressView.visibility = View.GONE
                    progressBarText.text = "Loading ..."
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response!!.body!!.string()
                activity!!.runOnUiThread {
                    recordsProgressView.visibility = View.GONE
                    progressBarText.text = "Loading ..."
                    if (!responseString.contains("FacID not found")) {
                        if (responseString.toString().contains("returnCode>1<", false)) {
                            activity!!.runOnUiThread {
                                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                            }
                        } else {
//                            var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
//                                    .replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                            var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("<returnCode")).replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                            var jsonObj = obj.getJSONObject("responseXml")
                            jsonObj = removeEmptyJsonTags(jsonObj)
                            parseFacilityDataJsonToObject(jsonObj)
                            getFacilityPRGData(isCompleted)

                            FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = visitationType

                        }
                    } else {
                        activity!!.runOnUiThread {
                            Utility.showMessageDialog(activity, "Retrieve Data Error", "Facility data not found")
                        }
                    }
                }
            }

        })

    }

    fun parseFacilityDataJsonToObject(jsonObj: JSONObject) {
        FacilityDataModel.getInstance().clear()
        FacilityDataModelOrg.getInstance().clear()
        FacilityDataModel.getInstance().clubCode = clubCode
        FacilityDataModelOrg.getInstance().clubCode = clubCode
        if (jsonObj.has("tblFacilities")) {
            if (jsonObj.get("tblFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
                FacilityDataModelOrg.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
            }
        }
        // Load PRG DATA

        if (jsonObj.has("tblBusinessType")) {
            if (jsonObj.get("tblBusinessType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
                FacilityDataModelOrg.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
            }
        }


        if (jsonObj.has("tblContractType")) {
            if (jsonObj.get("tblContractType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
                FacilityDataModelOrg.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServiceProvider")) {
            if (jsonObj.get("tblFacilityServiceProvider").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
            }
        }

        if (jsonObj.has("tblTerminationCodeType")) {
            if (jsonObj.get("tblTerminationCodeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
                FacilityDataModelOrg.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
            }
        }

        if (jsonObj.has("tblOfficeType")) {
            if (jsonObj.get("tblOfficeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
                FacilityDataModelOrg.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityManagers")) {
            if (jsonObj.get("tblFacilityManagers").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
            }
        }

        if (jsonObj.has("tblTimezoneType")) {
            if (jsonObj.get("tblTimezoneType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
                FacilityDataModelOrg.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
            }
        }


        if (jsonObj.has("tblVisitationTracking")) {
            if (jsonObj.get("tblVisitationTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
                FacilityDataModelOrg.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
            }
        }

        if (jsonObj.has("tblFacilityType")) {
            if (jsonObj.get("tblFacilityType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
            }
        }

        if (jsonObj.has("tblSurveySoftwares")) {
            if (jsonObj.get("tblSurveySoftwares").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
                FacilityDataModelOrg.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
                FacilityDataModelOrg.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
            }
        }


        if (jsonObj.has("tblPaymentMethods")) {
            if (jsonObj.get("tblPaymentMethods").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
                FacilityDataModelOrg.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
            }
        }

        if (jsonObj.has("tblAddress")) {
            if (jsonObj.get("tblAddress").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
                FacilityDataModelOrg.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
            }
        }

        if (jsonObj.has("tblPhone")) {
            if (jsonObj.get("tblPhone").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
                FacilityDataModelOrg.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
            }
        }



        if (jsonObj.has("tblFacilityEmail")) {
            if (jsonObj.get("tblFacilityEmail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
            }
        }

        if (jsonObj.has("tblHours")) {
            if (jsonObj.get("tblHours").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
                FacilityDataModelOrg.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
                FacilityDataModelOrg.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
            }
        }

        if (jsonObj.has("tblFacilityClosure")) {
            if (jsonObj.get("tblFacilityClosure").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
            }
        }

        if (jsonObj.has("tblLanguage")) {
            if (jsonObj.get("tblLanguage").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
                FacilityDataModelOrg.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
                FacilityDataModelOrg.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
            }
        }

        if (jsonObj.has("tblPersonnel")) {
            if (jsonObj.get("tblPersonnel").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
            }
        }

        if (jsonObj.has("tblAmendmentOrderTracking") && !jsonObj.has("tblAmendmentOrderTracking /")) {
            if (jsonObj.get("tblAmendmentOrderTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalAdmin")) {
            if (jsonObj.get("tblAARPortalAdmin").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
            }
        }

        if (jsonObj.has("tblScopeofService")) {
            if (jsonObj.get("tblScopeofService").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
                FacilityDataModelOrg.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
                FacilityDataModelOrg.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
            }
        }

        if (jsonObj.has("tblPrograms")) {
            var tempPrograms = ArrayList<TblPrograms>()
            if (jsonObj.get("tblPrograms").toString().startsWith("[")) {
                tempPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
//                FacilityDataModelOrg.getInstance().tblPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModel.getInstance().tblPrograms)
                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModelOrg.getInstance().tblPrograms)
            } else {
                tempPrograms .add(Gson().fromJson<TblPrograms>(jsonObj.get("tblPrograms").toString(), TblPrograms::class.java))
                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModel.getInstance().tblPrograms)
                tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModelOrg.getInstance().tblPrograms)
            }
        }

        if (jsonObj.has("tblFacilityServices")) {
            if (jsonObj.get("tblFacilityServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
            }
        }

        if (jsonObj.has("tblAffiliations")) {
            if (jsonObj.get("tblAffiliations").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
                FacilityDataModelOrg.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
            }
        }

        if (jsonObj.has("tblDeficiency")) {
            if (jsonObj.get("tblDeficiency").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
                FacilityDataModelOrg.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
                FacilityDataModelOrg.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
            }
        }

        if (jsonObj.has("tblComplaintFiles")) {
            if (jsonObj.get("tblComplaintFiles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
                FacilityDataModelOrg.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
                FacilityDataModelOrg.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
            }
        }

        if (jsonObj.has("NumberofComplaints")) {
            if (jsonObj.get("NumberofComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
            }
        }

        if (jsonObj.has("NumberofJustifiedComplaints")) {
            if (jsonObj.get("NumberofJustifiedComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
            }
        }

        if (jsonObj.has("JustifiedComplaintRatio")) {
            if (jsonObj.get("JustifiedComplaintRatio").toString().startsWith("[")) {
                FacilityDataModel.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
            } else {
                FacilityDataModel.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
            }
        }

        if (jsonObj.has("tblFacilityPhotos")) {
            if (jsonObj.get("tblFacilityPhotos").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
            }
        }

        if (jsonObj.has("Billing")) {
            if (jsonObj.get("Billing").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
                FacilityDataModelOrg.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
            }
        }

        if (jsonObj.has("BillingPlan")) {
            if (jsonObj.get("BillingPlan").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
                FacilityDataModelOrg.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
            }
        }

        if (jsonObj.has("tblFacilityBillingDetail")) {
            if (jsonObj.get("tblFacilityBillingDetail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
            }
        }

        if (jsonObj.has("tblInvoiceInfo")) {
            if (jsonObj.get("tblInvoiceInfo").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
                FacilityDataModelOrg.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
                FacilityDataModelOrg.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
            }
        }

        if (jsonObj.has("VendorRevenue")) {
            if (jsonObj.get("VendorRevenue").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
                FacilityDataModelOrg.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
            }
        }

        if (jsonObj.has("BillingHistory")) {
            if (jsonObj.get("BillingHistory").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
                FacilityDataModelOrg.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
            }
        }

        if (jsonObj.has("tblComments")) {
            if (jsonObj.get("tblComments").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
                FacilityDataModelOrg.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
                FacilityDataModelOrg.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
            }
        }

        if (jsonObj.has("tblVehicleServices")) {
            if (jsonObj.get("tblVehicleServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVehicleServices = Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVehicleServices= Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
                FacilityDataModelOrg.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalTracking")) {
            if (jsonObj.get("tblAARPortalTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalTracking = Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAARPortalTracking= Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
//                FacilityDataModel.getInstance().tblAARPortalTracking = FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
//                FacilityDataModelOrg.getInstance().tblAARPortalTracking = FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
            } else {
                FacilityDataModel.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
                FacilityDataModelOrg.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
//                FacilityDataModel.getInstance().tblAARPortalTracking = FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
//                FacilityDataModelOrg.getInstance().tblAARPortalTracking = FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
            }
        }

        if (jsonObj.has("tblPersonnelCertification")) {
            if (jsonObj.get("tblPersonnelCertification").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnelCertification = Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnelCertification= Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
            }
        }

        if (jsonObj.has("BillingAdjustments")) {
            if (jsonObj.get("BillingAdjustments").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBillingAdjustments = Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBillingAdjustments= Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
                FacilityDataModelOrg.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
            }
        }

        if (jsonObj.has("AAAPortalEmailFacilityRepTable")) {
            if (jsonObj.get("AAAPortalEmailFacilityRepTable").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable = Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable= Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
                FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
            }
//            FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection()
//            FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable = FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt()}))
        }
//
        if (jsonObj.has("InvoiceInfo")) {
            if (jsonObj.get("InvoiceInfo").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
                FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
                FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
            }
        }

        if (jsonObj.has("tblFacVehicles")) {
            if (jsonObj.get("tblFacVehicles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
                FacilityDataModelOrg.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
            }
        }

        if (jsonObj.has("tblPersonnelSigner")) {
            if (jsonObj.get("tblPersonnelSigner").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
            }
        }

        if (jsonObj.has("tblGeocodes")) {
            if (jsonObj.get("tblGeocodes").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
                FacilityDataModelOrg.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
                FacilityDataModelOrg.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
            }
        }

        if (jsonObj.has("AffiliateVendorFacilities")) {
            if (jsonObj.get("AffiliateVendorFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
                FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
            }
        }

        if (jsonObj.has("Promotions")) {
            if (jsonObj.get("Promotions").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
                FacilityDataModelOrg.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
            }
        }

        HasChangedModel.getInstance().init()
//        IndicatorsDataModel.getInstance().validateAllScreensVisited()
    }

    fun removeEmptyJsonTags(jsonObjOrg : JSONObject) : JSONObject {
        var jsonObj = jsonObjOrg;

        if (jsonObj.has("tblSurveySoftwares")) {
            if (!jsonObj.get("tblSurveySoftwares").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblSurveySoftwares")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblSurveySoftwares"))
                    jsonObj.put("tblSurveySoftwares", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblSurveySoftwares")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblSurveySoftwares")
        }

        if (jsonObj.has("tblAddress")) {
            if (!jsonObj.get("tblAddress").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAddress")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAddress"))
                    jsonObj.put("tblAddress", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAddress")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblAddress")
        }

        if (jsonObj.has("tblGeocodes")) {
            if (!jsonObj.get("tblGeocodes").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblGeocodes")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblGeocodes"))
                    jsonObj.put("tblGeocodes", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblGeocodes")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblGeocodes")
        }
//
        if (jsonObj.has("tblVisitationTracking")) {
            if (!jsonObj.get("tblVisitationTracking").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblVisitationTracking")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblVisitationTracking"))
                    jsonObj.put("tblVisitationTracking", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblVisitationTracking")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblVisitationTracking")
        }

        if (jsonObj.has("tblPhone")) {
            if (!jsonObj.get("tblPhone").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblPhone")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblPhone"))
                    jsonObj.put("tblPhone", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblPhone")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblPhone")
        }

        if (jsonObj.has("tblFacilityEmail")) {
            if (!jsonObj.get("tblFacilityEmail").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityEmail")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityEmail"))
                    jsonObj.put("tblFacilityEmail", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityEmail")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblFacilityEmail")
        }


        if (jsonObj.has("tblOfficeType")) {
            if (!jsonObj.get("tblOfficeType").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblOfficeType")
                    for (i in result.length()-1 downTo 0){
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblOfficeType"))
                    jsonObj.put("tblOfficeType",result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblOfficeType")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblOfficeType")
        }

        if (jsonObj.has("tblPersonnel")) {
            if (!jsonObj.get("tblPersonnel").toString().equals("")) {
                try {
        var result = jsonObj.getJSONArray("tblPersonnel")
        for (i in result.length()-1 downTo 0){
            if (result[i].toString().equals("")) result.remove(i);
        }
        jsonObj.remove(("tblPersonnel"))
        jsonObj.put("tblPersonnel",result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblPersonnel")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblPersonnel")
        }

        if (jsonObj.has("tblAmendmentOrderTracking")) {
            if (!jsonObj.get("tblAmendmentOrderTracking").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAmendmentOrderTracking")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAmendmentOrderTracking"))
                    jsonObj.put("tblAmendmentOrderTracking", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAmendmentOrderTracking")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblAmendmentOrderTracking")
        }

        if (jsonObj.has("tblAARPortalAdmin")) {
            if (!jsonObj.get("tblAARPortalAdmin").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAARPortalAdmin")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAARPortalAdmin"))
                    jsonObj.put("tblAARPortalAdmin", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalAdmin")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblAARPortalAdmin")
        }


        if (jsonObj.has("tblScopeofService")) {
            if (!jsonObj.get("tblScopeofService").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblScopeofService")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblScopeofService"))
                    jsonObj.put("tblScopeofService", result)
                } catch (e:Exception){

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblScopeofService")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblScopeofService")
        }

        if (jsonObj.has("tblPrograms")) {
            if (!jsonObj.get("tblPrograms").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblPrograms")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblPrograms"))
                    jsonObj.put("tblPrograms", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblPrograms")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblPrograms")
        }

        // check if the tag exists
        if (jsonObj.has("tblFacilityServices")) {
            if (!jsonObj.get("tblFacilityServices").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityServices")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityServices"))
                    jsonObj.put("tblFacilityServices", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj,"tblFacilityServices")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj,"tblFacilityServices")
        }

        if (jsonObj.has("tblAffiliations")) {
            if (!jsonObj.get("tblAffiliations").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAffiliations")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAffiliations"))
                    jsonObj.put("tblAffiliations", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAffiliations")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblAffiliations")
        }

        if (jsonObj.has("tblDeficiency")) {
            if (!jsonObj.get("tblDeficiency").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblDeficiency")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblDeficiency"))
                    jsonObj.put("tblDeficiency",result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblDeficiency")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblDeficiency")
        }

        if (jsonObj.has("tblComplaintFiles")) {
            if (!jsonObj.get("tblComplaintFiles").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblComplaintFiles")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblComplaintFiles"))
                    jsonObj.put("tblComplaintFiles", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblComplaintFiles")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblComplaintFiles")
        }

        if (jsonObj.has("tblFacilityPhotos")) {
            if (!jsonObj.get("tblFacilityPhotos").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityPhotos")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityPhotos"))
                    jsonObj.put("tblFacilityPhotos", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityPhotos")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityPhotos")
        }

        if (jsonObj.has("Billing")) {
            if (!jsonObj.get("Billing").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("Billing")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("Billing"))
                    jsonObj.put("Billing", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "Billing")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "Billing")
        }

        if (jsonObj.has("BillingPlan")) {
            if (!jsonObj.get("BillingPlan").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblBillingPlan")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("BillingPlan"))
                    jsonObj.put("BillingPlan", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "BillingPlan")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "BillingPlan")
        }

        if (jsonObj.has("tblFacilityBillingDetail")) {
            if (!jsonObj.get("tblFacilityBillingDetail").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityBillingDetail")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityBillingDetail"))
                    jsonObj.put("tblFacilityBillingDetail", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingDetail")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingDetail")
        }

        if (jsonObj.has("tblInvoiceInfo")) {
            if (!jsonObj.get("tblInvoiceInfo").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblInvoiceInfo")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblInvoiceInfo"))
                    jsonObj.put("tblInvoiceInfo", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblInvoiceInfo")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblInvoiceInfo")
        }

        if (jsonObj.has("VendorRevenue")) {
            if (!jsonObj.get("VendorRevenue").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("VendorRevenue")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("VendorRevenue"))
                    jsonObj.put("VendorRevenue", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
        }

        if (jsonObj.has("BillingHistory")) {
            if (!jsonObj.get("BillingHistory").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("BillingHistory")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("BillingHistory"))
                    jsonObj.put("BillingHistory", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "BillingHistory")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "BillingHistory")
        }

        if (jsonObj.has("tblComments")) {
            if (!jsonObj.get("tblComments").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblComments")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblComments"))
                    jsonObj.put("tblComments", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblComments")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblComments")
        }

        if (jsonObj.has("tblVehicleServices")) {
            if (!jsonObj.get("tblVehicleServices").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblVehicleServices")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblVehicleServices"))
                    jsonObj.put("tblVehicleServices", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblVehicleServices")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblVehicleServices")
        }

        if (jsonObj.has("tblAARPortalTracking")) {
            if (!jsonObj.get("tblAARPortalTracking").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAARPortalTracking")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAARPortalTracking"))
                    jsonObj.put("tblAARPortalTracking", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalTracking")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalTracking")
        }

        if (jsonObj.has("tblPersonnelCertification")) {
            if (!jsonObj.get("tblPersonnelCertification").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblPersonnelCertification")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblPersonnelCertification"))
                    jsonObj.put("tblPersonnelCertification", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelCertification")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelCertification")
        }

        if (jsonObj.has("BillingAdjustments")) {
            if (!jsonObj.get("BillingAdjustments").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("BillingAdjustments")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("BillingAdjustments"))
                    jsonObj.put("BillingAdjustments", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "BillingAdjustments")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "BillingAdjustments")
        }

        if (jsonObj.has("tblAAAPortalEmailFacilityRepTable")) {
            if (!jsonObj.get("tblAAAPortalEmailFacilityRepTable").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblAAAPortalEmailFacilityRepTable")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblAAAPortalEmailFacilityRepTable"))
                    jsonObj.put("tblAAAPortalEmailFacilityRepTable", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblAAAPortalEmailFacilityRepTable")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblAAAPortalEmailFacilityRepTable")
        }

        if (jsonObj.has("InvoiceInfo")) {
            if (!jsonObj.get("InvoiceInfo").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("InvoiceInfo")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("InvoiceInfo"))
                    jsonObj.put("InvoiceInfo", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "InvoiceInfo")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "InvoiceInfo")
        }

        if (jsonObj.has("tblFacVehicles")) {
            if (!jsonObj.get("tblFacVehicles").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacVehicles")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacVehicles"))
                    jsonObj.put("tblFacVehicles", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacVehicles")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacVehicles")
        }

        if (jsonObj.has("tblPersonnelSigner")) {
            if (!jsonObj.get("tblPersonnelSigner").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblPersonnelSigner")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblPersonnelSigner"))
                    jsonObj.put("tblPersonnelSigner", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelSigner")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelSigner")
        }

        if (jsonObj.has("tblHours")) {
            if (!jsonObj.get("tblHours").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblHours")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblHours"))
                    jsonObj.put("tblHours", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblHours")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblHours")
        }

        if (jsonObj.has("tblTerminationCodeType")) {
            if (!jsonObj.get("tblTerminationCodeType").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblTerminationCodeType")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblTerminationCodeType"))
                    jsonObj.put("tblTerminationCodeType", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblTerminationCodeType")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblTerminationCodeType")
        }

        if (jsonObj.has("tblBusinessType")) {
            if (!jsonObj.get("tblBusinessType").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblBusinessType")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblBusinessType"))
                    jsonObj.put("tblBusinessType", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblBusinessType")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblBusinessType")
        }

        if (jsonObj.has("tblFacilityClosure")) {
            if (!jsonObj.get("tblFacilityClosure").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityClosure")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityClosure"))
                    jsonObj.put("tblFacilityClosure", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityClosure")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityClosure")
        }

        if (jsonObj.has("tblFacilityManagers")) {
            if (!jsonObj.get("tblFacilityManagers").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityManagers")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityManagers"))
                    jsonObj.put("tblFacilityManagers", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityManagers")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityManagers")
        }

        if (jsonObj.has("tblFacilityServiceProvider")) {
            if (!jsonObj.get("tblFacilityServiceProvider").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityServiceProvider")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityServiceProvider"))
                    jsonObj.put("tblFacilityServiceProvider", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityServiceProvider")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityServiceProvider")
        }

        if (jsonObj.has("VendorRevenue")) {
            if (!jsonObj.get("VendorRevenue").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("VendorRevenue")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("VendorRevenue"))
                    jsonObj.put("VendorRevenue", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
        }

        if (jsonObj.has("tblFacilityType")) {
            if (!jsonObj.get("tblFacilityType").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("tblFacilityType")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("tblFacilityType"))
                    jsonObj.put("tblFacilityType", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "tblFacilityType")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityType")
        }
        if (jsonObj.has("Promotions")) {
            if (!jsonObj.get("Promotions").toString().equals("")) {
                try {
                    var result = jsonObj.getJSONArray("Promotions")
                    for (i in result.length() - 1 downTo 0) {
                        if (result[i].toString().equals("")) result.remove(i);
                    }
                    jsonObj.remove(("Promotions"))
                    jsonObj.put("Promotions", result)
                } catch (e: Exception) {

                }
            } else {
                jsonObj = addOneElementtoKey(jsonObj, "Promotions")
            }
        } else {
            jsonObj = addOneElementtoKey(jsonObj, "Promotions")
        }
//
        return jsonObj
    }

    fun addOneElementtoKey (jsonObj: JSONObject, key: String) : JSONObject {
        if (key.equals("tblFacilityServices")) {
            var oneArray = TblFacilityServices();
            oneArray.Comments = "";
            oneArray.ServiceID = "";
            oneArray.effDate = "";
            oneArray.expDate = "";
            oneArray.FacilityServicesID="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAffiliations")) {
            var oneArray = TblAffiliations()
            oneArray.AffiliationID = -1
            oneArray.AffiliationTypeDetailID = 0
            oneArray.AffiliationTypeID = 0
            oneArray.effDate = "";
            oneArray.comment = ""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblDeficiency")) {
            var oneArray = TblDeficiency()
            oneArray.ClearedDate = ""
            oneArray.Comments = ""
            oneArray.DefTypeID = "-1"
            oneArray.EnteredDate = ""
            oneArray.VisitationDate = ""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblComplaintFiles")) {
            var oneArray = TblComplaintFiles()
            oneArray.ComplaintID = ""
            oneArray.FirstName = ""
            oneArray.LastName = ""
            oneArray.ReceivedDate = ""
            jsonObj.put(key, Gson().toJson(oneArray))
            //
        } else if (key.equals("tblVisitationTracking")) {
            var oneArray = TblVisitationTracking()
            oneArray.AARSigns=""
            oneArray.visitationID="-1"
            oneArray.CertificateOfApproval=""
            oneArray.DatePerformed=""
            oneArray.MemberBenefitPoster=""
            oneArray.QualityControl=""
            oneArray.StaffTraining=""
            oneArray.automotiveSpecialistName=""
            oneArray.automotiveSpecialistSignature=null
            oneArray.email=""
            oneArray.emailVisitationPdfToFacility=false
            oneArray.facilityRepresentativeDeficienciesSignature=null
            oneArray.performedBy="00"
            oneArray.visitationType=null
            oneArray.waiveVisitations=false
            oneArray.waiverComments=""
            oneArray.waiverSignature=null
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAmendmentOrderTracking")) {
            var oneArray = TblAmendmentOrderTracking()
            oneArray.AOID = ""
            oneArray.ReasonID = ""
            oneArray.EventTypeID = ""
            oneArray.EventID = ""
            oneArray.AOTEmployee = ""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblScopeofService")) {
            var oneArray = TblScopeofService()
            oneArray.WarrantyTypeID=""
            oneArray.NumOfLifts=""
            oneArray.DiagnosticsRate=""
            oneArray.FixedLaborRate=""
            oneArray.LaborMax=""
            oneArray.LaborMin=""
            oneArray.NumOfBays=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblSurveySoftwares")) {
            var oneArray = TblSurveySoftwares()
            oneArray.FACID=0
            oneArray.SoftwareSurveyNum=0
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.updateBy=""
            oneArray.updateDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAddress")) {
            var oneArray = TblAddress()
            oneArray.BranchName=""
            oneArray.BranchNumber=""
            oneArray.CITY=""
            oneArray.County=""
            oneArray.FAC_Addr1=""
            oneArray.FAC_Addr2=""
            oneArray.LATITUDE=""
            oneArray.LONGITUDE=""
            oneArray.LocationTypeID=""
            oneArray.ST=""
            oneArray.ZIP=""
            oneArray.ZIP4=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityEmail")) {
            var oneArray = TblFacilityEmail()
            oneArray.email=""
            oneArray.emailID="-1"
            oneArray.emailTypeId=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblPhone")) {
            var oneArray = TblPhone()
            oneArray.PhoneNumber=""
            oneArray.PhoneTypeID=""
            oneArray.PhoneID="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblGeocodes")) {
            var oneArray = TblGeocodes()
            oneArray.GeoCodeTypeID=-1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAARPortalAdmin")) {
            var oneArray = TblAARPortalAdmin()
            oneArray.AddendumSigned=""
            oneArray.CardReaders="-1"

            oneArray.startDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblPrograms")) {
            var oneArray = TblPrograms()
            oneArray.Comments=""
            oneArray.ProgramID = "-1"
            oneArray.ProgramTypeID=""
            oneArray.effDate=""
            oneArray.expDate=""
            oneArray.programtypename=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityPhotos")) {
            var oneArray = TblFacilityPhotos()
            oneArray.ApprovalRequested=""
            oneArray.Approved=""
            oneArray.ApprovedBy=""
            oneArray.ApprovedDate=""
            oneArray.FileDescription=""
            oneArray.FileName=""
            oneArray.LastUpdateBy=""
            oneArray.LastUpdateDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("Billing")) {
            var oneArray = TblBilling()
            oneArray.ACHParticipant=0
            oneArray.BillingAmount=0.0
            oneArray.BillingDate=""
            oneArray.BillingID=-1
            oneArray.BillingMonthNumber=0
            oneArray.CreditAmountDue=""
            oneArray.FACID=0
            oneArray.PaymentAmount=0.0
            oneArray.PendingAmount=0.0
            oneArray.PaymentDate=""
            oneArray.RevenueSourceID=0
            oneArray.SecondBillDate=""
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.updateBy=""
            oneArray.updateDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("BillingPlan")) {
            var oneArray = TblBillingPlan()
            oneArray.BillingPlanCatgID=0
            oneArray.BillingPlanID=-1
            oneArray.BillingPlanTypeID=0
            oneArray.EffectiveDate=""
            oneArray.ExpirationDate=""
            oneArray.FACID=0
            oneArray.FrequencyTypeID=0
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.updateBy=""
            oneArray.updateDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityBillingDetail")) {
            var oneArray = TblFacilityBillingDetail()
            oneArray.FacBillId=-1
            oneArray.BillingPlanID=-1
            oneArray.BillingPlanTypeID=0
            oneArray.BillAmount=0.0
            oneArray.BillDueDate=""
            oneArray.FACID=0
            oneArray.BillSeqInCycle=0
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.BillingInvoiceDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblInvoiceInfo")) {
            var oneArray = TblInvoiceInfo()
            oneArray.ACHParticipant=false
            oneArray.BillingDueDate=""
            oneArray.CreditAmount=0.0
            oneArray.InvoiceAmount=0.0
            oneArray.InvoiceFileName=""
            oneArray.FACID=0
            oneArray.InvoiceId=-1
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.updateBy=""
            oneArray.updateDate=""
            oneArray.InvoicePrintDate=""
            oneArray.InvoiceStatusId=0
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("VendorRevenue")) {
            var oneArray = TblVendorRevenue()
            oneArray.Amount=""
            oneArray.Comments=""
            oneArray.DateOfCheck=""
            oneArray.FACID=0
            oneArray.ReceiptDate=""
            oneArray.FACID=0
            oneArray.ReceiptNumber=""
            oneArray.insertBy=""
            oneArray.insertDate=""
            oneArray.updateBy=""
            oneArray.updateDate=""
            oneArray.RevenueSourceID=0
            oneArray.StateRevenueAcct=""
            oneArray.VendorRevenueID=-1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("BillingHistory")) {
            var oneArray = TblBillingHistory()
            oneArray.InvoiceId = -1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblOfficeType")) {
            var oneArray = TblOfficeType()
            oneArray.OfficeName=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblComments")) {
            var oneArray = TblComments()
            oneArray.FACID=0
            oneArray.Comment=""
            oneArray.insertDate=""
            oneArray.CommentTypeID=0
            oneArray.SeqNum=0
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblPersonnel")) {
            var oneArray = TblPersonnel()
            oneArray.Addr1=""
            oneArray.Addr2=""
            oneArray.CITY=""
            oneArray.CertificationDate=""
            oneArray.CertificationNum=""
            oneArray.CertificationTypeId=""
            oneArray.ContractSigner=false
            oneArray.endDate=""
            oneArray.FirstName=""
            oneArray.LastName=""
            oneArray.PersonnelTypeID=0
            oneArray.RSP_Phone=""
            oneArray.PersonnelID = -1
            oneArray.PrimaryMailRecipient=false
            oneArray.RSP_Email=""
            oneArray.RSP_UserName=""
            oneArray.ST=""
            oneArray.SeniorityDate=""
            oneArray.ZIP=""
            oneArray.ZIP4=""
            oneArray.email=""
            oneArray.startDate=""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblVehicleServices")) {
            var oneArray = TblVehicleServices()
            oneArray.FACID = 0
            oneArray.ScopeServiceID = -1
            oneArray.VehiclesTypeID = -1
            oneArray.insertBy=""
            oneArray.insertDate = ""
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAARPortalTracking")) {
            var oneArray = TblAARPortalTracking()
            oneArray.TrackingID="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblPersonnelCertification")) {
            var oneArray = TblPersonnelCertification()
            oneArray.PersonnelID=0
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("BillingAdjustments")) {
            var oneArray = TblBillingAdjustments()
            oneArray.AdjustmentId=-1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblAAAPortalEmailFacilityRepTable")) {
            var oneArray = TblAAAPortalEmailFacilityRepTable()
            oneArray.ContractSID="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("InvoiceInfo")) {
            var oneArray = InvoiceInfo()
            oneArray.InvoiceId="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacVehicles")) {
            var oneArray = TblFacVehicles()
            oneArray.VehicleID =-1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblPersonnelSigner")) {
            var oneArray = TblPersonnelSigner()
            oneArray.PersonnelID = -1
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblHours")) {
            var oneArray = TblHours()
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblBusinessType")) {
            var oneArray = TblBusinessType()
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblTerminationCodeType")) {
            var oneArray = TblBusinessType()
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityClosure")) {
            var oneArray = TblFacilityClosure()
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityManagers")) {
            var oneArray = TblFacilityManagers()
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityServiceProvider")) {
            var oneArray = TblFacilityServiceProvider()
            oneArray.SrvProviderId="-1"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("tblFacilityType")) {
            var oneArray = TblFacilityType()
            oneArray.FacilityTypeName="Independent"
            jsonObj.put(key, Gson().toJson(oneArray))
        } else if (key.equals("Promotions")) {
            var oneArray = TblPromotions()
            oneArray.PromoID=-1
            jsonObj.put(key, Gson().toJson(oneArray))
        }
        return jsonObj;
    }

    fun sendCompletedPDF() {
//        var specialistEmail = ApplicationPrefs.getInstance(activity).loggedInUserEmail
//        var specialistEmail = "saeed@pacificresearchgroup.com"//Constants.specialistEmailForPDF
//        var generatePDF = false
        var specialistEmail = Constants.specialistEmailForPDF
        var urlString = "facNo=${Constants.facNoForPDF}&facName=${Constants.facNameForPDF}&type=${Constants.typeForPDF}"
//        var email = ApplicationPrefs.getInstance(activity).loggedInUserEmail
//        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf && PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto.isNotEmpty()) {
//            email = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto
//        }
//        if (generatePDF) {
//
//        } else {
            Log.v("SEND PDF", Constants.sendCompletedPDF + Constants.visitationIDForPDF + "&email=${specialistEmail}&specialistEmail=${specialistEmail}&" + urlString)
            Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.sendCompletedPDF + Constants.visitationIDForPDF + "&email=${specialistEmail}&specialistEmail=${specialistEmail}&" + urlString,
                    { response ->
                        Log.v("Send PDF Response ", "" + response)
                        requireActivity().runOnUiThread {
                            recordsProgressView.visibility = View.GONE
                            Utility.showMessageDialog(activity, "Confirmation...", "PDF for Visitation Number ${Constants.visitationIDForPDF} has been sent to $specialistEmail")
                            Constants.specialistEmailForPDF = ""
                        }
                    }, {
                Log.v("Send PDF Error ", "" + it.message)
                it.printStackTrace()
                recordsProgressView.visibility = View.GONE
            })).setRetryPolicy(DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
//        }
    }


    private class VisitationPlanningViewHolder(view: View?) {
        val facilityNameValueTextView: TextView
        val facilityNoValueTextView: TextView
        val initialContractDateValueTextView: TextView
        val visitationTypeValueTextView: TextView
        val initialContractDateTextView: TextView
        val visitationTypeTextView:TextView
        val visitationStatusValueTextView: TextView
        val visitationStatusTextView:TextView
        val loadBtn: Button
        val emailPDFBtn: Button
        val listBkg: CardView

        init {
            this.facilityNameValueTextView = view?.findViewById(R.id.facilityNameValueTextView) as TextView
            this.facilityNoValueTextView = view?.findViewById(R.id.facilityNoValueTextView) as TextView
            this.initialContractDateValueTextView = view?.findViewById(R.id.initialContractDateValueTextView) as TextView
            this.visitationTypeValueTextView = view?.findViewById(R.id.visitationTypeValueTextView) as TextView
            this.initialContractDateTextView=view?.findViewById(R.id.initialContractDateTextView) as TextView
            this.visitationTypeTextView = view?.findViewById(R.id.visitationTypeTextView ) as TextView
            this.visitationStatusValueTextView = view?.findViewById(R.id.visitationStatusValueTextView) as TextView
            this.visitationStatusTextView = view?.findViewById(R.id.visitationStatusTextView ) as TextView
            this.loadBtn = view?.findViewById(R.id.loadBtn) as Button
            this.emailPDFBtn = view?.findViewById(R.id.emailPDFBtn) as Button
            this.listBkg = view?.findViewById(R.id.listBkg) as CardView

        }

    }

    private class ViewHolder(view: View?) {
        val vrID: TextView
        val vrBy: TextView
        val vrType: TextView
        val vrStatus: TextView
        val vrPlanned: TextView
        val vrDate: TextView
        val vrLoadBtn: TextView
        val vrEmailPDFBtn: TextView
        val vrLL: RelativeLayout

        init {
            this.vrID = view?.findViewById(R.id.visitationItemId) as TextView
            this.vrBy = view?.findViewById(R.id.visitationItemPerformedBy) as TextView
            this.vrStatus = view?.findViewById(R.id.visitationItemStatus) as TextView
            this.vrType = view?.findViewById(R.id.visitationItemType) as TextView
            this.vrDate = view?.findViewById(R.id.visitationItemPerformedDate) as TextView
            this.vrPlanned = view?.findViewById(R.id.visitationItemPlanned) as TextView
            this.vrLL = view?.findViewById(R.id.list_item_ll) as RelativeLayout
            this.vrLoadBtn = view?.findViewById(R.id.loadBtn) as TextView
            this.vrEmailPDFBtn = view?.findViewById(R.id.emailPDFBtn) as TextView
        }

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

        var shouldShowVisitation = false

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FrgmentARRAnnualVisitationRecords.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): VisitationPlanningFragment {
            val fragment = VisitationPlanningFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}




