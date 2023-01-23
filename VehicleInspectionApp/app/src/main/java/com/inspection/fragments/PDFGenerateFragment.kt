package com.inspection.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.SearchDialog
import com.inspection.Utils.Utility
import com.inspection.model.*
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.fragment_pdfgenerate.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.clubCodeEditText
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.facilityNameButton
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.recordsProgressView
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.searchVisitaionsButton
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.visitationfacilityIdVal
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.json.XML
import java.io.IOException
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PDFGenerateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PDFGenerateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var allClubCodes = ArrayList<String>()
    var facilityNames = ArrayList<String>()
    var defaultClubCode = ""
    var clubCode=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdfgenerate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadClubCodes()
    }

    private fun loadClubCodes() {
        Log.v("url*******", ""+ Constants.getClubCodes)
        Log.v("VISITATION CLUB --- ", Constants.getClubCodes)
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
                        Log.v("Logged User --- >  ", ApplicationPrefs.getInstance(activity).loggedInUserID)
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

    fun firstLoadingCompleted(){
        clubCodeEditText.setOnClickListener {
            var searchDialog = SearchDialog(context, allClubCodes)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                clubCodeEditText.setText(searchDialog.selectedString)
            }
        }
        facilityNameButton.setOnClickListener {
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
            }
        }

        searchVisitaionsButton.setOnClickListener {
            val stepsArray = ArrayList<String>()
            stepsArray.add("Load ACE Facility Details")
            stepsArray.add("Load PRG Visitation Details")
            stepsArray.add("Validate Visitation ID")
            clubCode = clubCodeEditText.text.toString()
            getFullFacilityDataFromAAA(visitationfacilityIdVal.text.toString().toInt(),clubCodeEditText.text.toString(),false,VisitationTypes.Annual)
        }

    }

    fun getFullFacilityDataFromAAA(facilityNumber: Int, clubCode: String,isCompleted : Boolean,visitationType : VisitationTypes) {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Facility Details")
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
        var request2 = okhttp3.Request.Builder().url(String.format(Constants.getFacilityData+Utility.getLoggingParameters(activity, 0, "Load Visitations ..."), facilityNumber, clubCode)).build()

        recordsProgressView.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity!!.runOnUiThread {
                    Utility.showMessageDialog(activity, "Retrieve Data Error", "Origin ERROR Connection Error. Please check internet connection - " + e?.message)
                    recordsProgressView.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response!!.body!!.string()
                activity!!.runOnUiThread {
                    recordsProgressView.visibility = View.GONE
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
                            step_view.go(1,true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                getFacilityPRGData(isCompleted)
                                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = visitationType
                            }, 1000)
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
                                                                                                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                                                                                                            step_view.go(2,true)
                                                                                                                                            validateFacilityData()
                                                                                                                                        }, 1000)
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
//                                                                                            launchNextAction(isCompleted)
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

    fun validateFacilityData(){
        Handler(Looper.getMainLooper()).postDelayed({
            step_view.go(3,true)
            step_view.done(true)
        }, 1000)
        val visitationID = visitationIDEditText.text.toString()
        if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s->s.visitationID.equals(visitationID)}.isEmpty()) {
            Utility.showMessageDialog(activity,"Warning","Visitation ID Not Found")
        } else {
            // Open Specialist PDF
//            recordsProgressView.visibility = View.VISIBLE
//            webViewPDF!!.webViewClient = object : WebViewClient() {
//                override fun onPageFinished(view: WebView?, url: String?) {
//                }
//            }
//            webCardPDFView.visibility = View.VISIBLE
//            pdfNameWeb.text = "Visitation PDF For Specialist (ID: "+visitationID+")"
//            exitDialogeBtn.setOnClickListener {
//                webViewPDF.loadUrl("about:blank")
//                recordsProgressView.visibility = View.GONE
//                webCardPDFView.visibility = View.GONE
//            }
//            webViewPDF.getSettings().setJavaScriptEnabled(true);
//            webViewPDF!!.loadUrl("http://docs.google.com/gview?embedded=true&url="+Constants.getPDF+visitationID)

        }
    }

    fun validatePDFData(){


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
            oneArray.Phone=""
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
        }
        return jsonObj;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PDFGenerateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PDFGenerateFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}