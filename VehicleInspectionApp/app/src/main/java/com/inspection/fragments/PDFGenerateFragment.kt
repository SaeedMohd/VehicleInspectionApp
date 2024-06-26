package com.inspection.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.adapter.MultipartRequest
import com.inspection.model.*
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import kotlinx.android.synthetic.main.fragment_pdfgenerate.*
import kotlinx.android.synthetic.main.fragment_visitation_form.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.json.XML
import java.io.*
import java.net.URLEncoder
import java.util.*
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
    var repSignatureBmp : Bitmap? = null
    var specSignatureBmp : Bitmap? = null
    var deffSignatureBmp : Bitmap? = null
    var repwaiverSignatureBmp : Bitmap? = null
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
                        recordsProgressViewPDF.visibility = View.INVISIBLE
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
                            clubCodeEditTextPDF.setText(defaultClubCode)
                        } else {
                            clubCodeEditTextPDF.setText("252")
                        }
                        facilityNames.sort()
                        facilityNames.add(0, "Any")
                        firstLoadingCompleted()
                    }
                }, Response.ErrorListener {
            Utility.showMessageDialog(activity, "Retrieve Data Error", "Connection Error while retrieving Facilities - " + it.message)
            recordsProgressViewPDF.visibility = View.INVISIBLE
            Log.v("error while loading", "error while loading facilities")
            Log.v("Loading error", "" + it.message)
        }))

    }

    fun firstLoadingCompleted(){
        clubCodeEditTextPDF.setOnClickListener {
            var searchDialog = SearchDialog(context, allClubCodes)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                clubCodeEditTextPDF.setText(searchDialog.selectedString)
            }
        }
        facilityNameButtonPDF.setOnClickListener {
            var searchDialog = SearchDialog(context, facilityNames)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                if (searchDialog.selectedString == "Any" || searchDialog.selectedString == "") {
                    facilityNameButtonPDF.setText("")
                    visitationfacilityIdValPDF.setText("")
                } else {
                    facilityNameButtonPDF.setText(searchDialog.selectedString.substring(0,searchDialog.selectedString.indexOf(" || ")))
                    visitationfacilityIdValPDF.setText(searchDialog.selectedString.substringAfter("|| "))
                }
            }
        }

        searchVisitaionsButtonPDF.setOnClickListener {
            val stepsArray = ArrayList<String>()
            stepsArray.add("Load ACE Facility Details")
            stepsArray.add("Load PRG Visitation Details")
            stepsArray.add("Validate Visitation ID")
            clubCode = clubCodeEditTextPDF.text.toString()
            step_view.go(0, true);
            statusTextView.text = "Loading ACE Facility Details ..."
            getFullFacilityDataFromAAA(visitationfacilityIdValPDF.text.toString().toInt(),clubCodeEditTextPDF.text.toString(),false,VisitationTypes.Annual)
        }

    }

    fun getFullFacilityDataFromAAA(facilityNumber: Int, clubCode: String,isCompleted : Boolean,visitationType : VisitationTypes) {
//        FirebaseCrashlytics.getInstance().setCustomKey("Details", "Load Facility Details")
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
        var request2 = okhttp3.Request.Builder().url(String.format(Constants.getFacilityData+Utility.getLoggingParameters(activity, 0, "Load Visitations ..."), facilityNumber, clubCode)).build()

        recordsProgressViewPDF.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity!!.runOnUiThread {
                    Utility.showMessageDialog(activity, "Retrieve Data Error", "Origin ERROR Connection Error. Please check internet connection - " + e?.message)
                    recordsProgressViewPDF.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response!!.body!!.string()
                activity!!.runOnUiThread {
                    recordsProgressViewPDF.visibility = View.GONE
                    if (!responseString.contains("FacID not found")) {
                        if (responseString.toString().contains("returnCode>1<", false)) {
                            activity!!.runOnUiThread {
                                Utility.showMessageDialog(activity, "Retrieve Data Error", responseString.substring(responseString.indexOf("<message") + 9, responseString.indexOf("</message")))
                            }
                        } else {
//                            var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("<responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
//                                    .replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                            statusTextView.append("Completed \nLoading PRG Visitation Details ...")
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
        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getVisitationHeaderByID + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}&visitationId=${visitationIDEditText.text.toString()}",
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        if (!response.toString().replace(" ","").equals("[]")) {
                            PRGDataModel.getInstance().tblPRGVisitationHeader= Gson().fromJson(response.toString(), Array<PRGVisitationHeader>::class.java).toCollection(ArrayList())
                        } else {
                            var item = PRGVisitationHeader()
                            item.recordid=-1
                            PRGDataModel.getInstance().tblPRGVisitationHeader.add(item)
                        }
                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActionsBySession + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}&sessionId="+PRGDataModel.getInstance().tblPRGVisitationHeader[0].sessionid,
                                Response.Listener { response ->
                                    requireActivity().runOnUiThread {
                                        if (!response.toString().replace(" ","").equals("[]")) {
                                            PRGDataModel.getInstance().tblPRGLogChanges = Gson().fromJson(response.toString(), Array<PRGLogChanges>::class.java).toCollection(ArrayList())
                                        } else {
                                            var item = PRGLogChanges()
                                            item.recordid=-1
                                            PRGDataModel.getInstance().tblPRGLogChanges.add(item)
                                        }
//                                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getVisitationHeader + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}",
//                                                Response.Listener { response ->
//                                                    requireActivity().runOnUiThread {
//                                                        if (!response.toString().replace(" ","").equals("[]")) {
//                                                            PRGDataModel.getInstance().tblPRGVisitationHeader= Gson().fromJson(response.toString(), Array<PRGVisitationHeader>::class.java).toCollection(ArrayList())
//                                                        } else {
//                                                            var item = PRGVisitationHeader()
//                                                            item.recordid=-1
//                                                            PRGDataModel.getInstance().tblPRGVisitationHeader.add(item)
//                                                        }
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
                                                                                                                                        statusTextView.append("Completed \nValidate Visitation ID ...")
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
//                                                }, Response.ErrorListener {
//                                            Log.v("Loading PRG Data error", "" + it.message)
////                                            launchNextAction(isCompleted)
//                                            it.printStackTrace()
//                                        }))
//                                        launchNextAction(isCompleted)
//                                    }
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
            statusTextView.append("Completed \nValidate PDF Data...")
            Handler(Looper.getMainLooper()).postDelayed({
                step_view.go(4,true)
                validatePDFData()
            }, 1000)
        }
    }

    fun validatePDFData(){
        // Check Signatures
//        var visitationTypeTemp = PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype
        FacilityDataModel.getInstance().tblVisitationTracking.filter { s->s.visitationID.equals(visitationIDEditText.text.toString())}[0].apply {
            statusTextView.append("\nSignatures ... ")// + DatePerformed.apiToAppFormatMMDDYYYY())
            var strMonth = DatePerformed.apiToAppFormatMMDDYYYY().split("/")[0].toInt()-1
            var strYear = DatePerformed.apiToAppFormatMMDDYYYY().split("/")[2]
            var strPrefix = "_"+ strMonth + "_" + strYear;
            var visitationTypeTemp = VisitationTypeID
            if (visitationTypeTemp.equals("1")) {
                visitationTypeTemp = "Annual"
            } else if (visitationTypeTemp.equals("2")) {
                visitationTypeTemp = "Quarterly"
            } else if (visitationTypeTemp.equals("3")) {
                visitationTypeTemp = "Adhoc"
            } else if (visitationTypeTemp.equals("4")) {
                visitationTypeTemp = "Deficiency"
            }
            var strRepSignature = visitationfacilityIdValPDF.text.toString() + "_" + clubCodeEditTextPDF.text.toString() + "_"+visitationTypeTemp+"_RepSignature"+strPrefix+".png"
            var strSpecSignature = visitationfacilityIdValPDF.text.toString() + "_" + clubCodeEditTextPDF.text.toString() + "_"+visitationTypeTemp+"_SpecSignature"+strPrefix+".png"
            var strWSignature = visitationfacilityIdValPDF.text.toString() + "_" + clubCodeEditTextPDF.text.toString() + "_"+visitationTypeTemp+"_WSignature"+strPrefix+".png"
            var strDefSignature = visitationfacilityIdValPDF.text.toString() + "_" + clubCodeEditTextPDF.text.toString() + "_"+visitationTypeTemp+"_DefSignature"+strPrefix+".png"
            if (!waiveVisitations && VisitationMethodTypeID.equals("1")) {
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.checkFileExists + strRepSignature,
                        Response.Listener { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().equals("true")) {
                                    statusTextView.append("\n------> Representative Signature ... Found ")
                                    Volley.newRequestQueue(context).add(ImageRequest(
                                            Constants.getSignature + strRepSignature,
                                            Response.Listener<Bitmap?> { response ->
                                                requireActivity().runOnUiThread {
//                                                    signatureImage.setImageBitmap(response)
                                                    repSignatureBmp = response
                                                    statusTextView.append("\n------> Representative Signature ... Loaded")
                                                }
                                            },
                                            400, 400, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                                            Response.ErrorListener {
                                                statusTextView.append("\n------> Representative Signature Load Image: " + it.message)
                                            },
                                    ))
                                } else {
                                    statusTextView.append("\n------> Representative Signature ... ${strRepSignature} Not Found")
                                }
                            }
                        }, Response.ErrorListener {
                    statusTextView.append("\n------> Representative Signature ... " + it.message)
                }))
            } else if (waiveVisitations) {
                statusTextView.append("\n------> Representative Signature ... Not Needed as this is Waived Visitation")
            } else {
                statusTextView.append("\n------> Representative Signature ... Not Needed as this is not In Person Visit")
            }


            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.checkFileExists + strSpecSignature,
                    Response.Listener { response ->
                        requireActivity().runOnUiThread {
                            if (response.toString().equals("true")) {
                                statusTextView.append("\n------> Specialist Signature ... Found ")
                                Volley.newRequestQueue(context).add(ImageRequest(
                                        Constants.getSignature + strSpecSignature,
                                        Response.Listener<Bitmap?> { response ->
                                            requireActivity().runOnUiThread {
//                                                signatureImage.setImageBitmap(response)
                                                specSignatureBmp = response
                                                statusTextView.append("\n------> Specialist Signature ... Loaded ")
                                            }
                                        },
                                        400, 400, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                                        Response.ErrorListener {
                                            statusTextView.append("\n------> Specialist Signature Load Image: " + it.message)
                                        },
                                ))
                            } else {
                                statusTextView.append("\n------> Specialist Signature ... Not Found")
                            }
                        }
                    }, Response.ErrorListener {
                statusTextView.append("\n------> Specialist Signature ... " + it.message)
            }))

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.checkFileExists + strDefSignature,
                    Response.Listener { response ->
                        requireActivity().runOnUiThread {
                            if (response.toString().equals("true")) {
                                statusTextView.append("\n------> Deficiency Signature ... Found ")
                                Volley.newRequestQueue(context).add(ImageRequest(
                                        Constants.getSignature + strDefSignature,
                                        Response.Listener<Bitmap?> { response ->
                                            requireActivity().runOnUiThread {
//                                                signatureImage.setImageBitmap(response)
                                                deffSignatureBmp = response
                                                statusTextView.append("\n------> Deficiency Signature ... Loaded ")
                                            }
                                        },
                                        400, 400, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                                        Response.ErrorListener {
                                            statusTextView.append("\n------> Deficiency Signature Load Image: " + it.message)
                                        },
                                ))
                            } else {
                                statusTextView.append("\n------> Deficiency Signature ... Not Found")
                            }
                        }
                    }, Response.ErrorListener {
                statusTextView.append("\n------> Deficiency Signature ... " + it.message)
            }))


            if (waiveVisitations) {
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.checkFileExists + strWSignature,
                        Response.Listener { response ->
                            requireActivity().runOnUiThread {
                                if (response.toString().equals("true")) {
                                    statusTextView.append("\n------> Waiver Signature ... Found")
                                    Volley.newRequestQueue(context).add(ImageRequest(
                                            Constants.getSignature + strWSignature,
                                            Response.Listener<Bitmap?> { response ->
                                                requireActivity().runOnUiThread {
//                                                    signatureImage.setImageBitmap(response)
                                                    repwaiverSignatureBmp = response
                                                    statusTextView.append("\n------> Waiver Signature ... Loaded")
                                                }
                                            },
                                            400, 400, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                                            Response.ErrorListener {
                                                statusTextView.append("------> Waiver Signature Load Image: " + it.message)
                                            },
                                    ))
                                } else {
                                    statusTextView.append("\n------> Waiver Signature ... Not Found")
                                }
                            }
                        }, Response.ErrorListener {
                    statusTextView.append("\n------> Waiver Signature ... " + it.message)
                }))
            }
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    //  drawVisitaionSection
                    statusTextView.append("\nVisitaion Section ... ")
                    Log.v("Facility ", "Rep Name:" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep)
                    Log.v("Automotive ", "Spec Name:" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist)
                    Log.v("Visitation Type: ", visitationType.toString())
                    Log.v("Visitation Reason: ", TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeID == VisitationReasonTypeID.toInt() }[0].VisitationReasonTypeName)
                    Log.v("Visitation Method: ", TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeID == VisitationMethodTypeID.toInt() }[0].TypeName)
                } catch (e: Exception) {
                    statusTextView.append("\nVisitaion Section Data Issue ............... " +e.message)
                }
                try {
                    //  drawFacilitySection
                    statusTextView.append("\nFacility Section ... ")
                    Log.v("Contract Type: ", FacilityDataModel.getInstance().tblContractType[0].ContractTypeName)
                    Log.v("Office: ", FacilityDataModel.getInstance().tblOfficeType[0].OfficeName)
                    Log.v("Assigned To: ", FacilityDataModel.getInstance().tblFacilities[0].AssignedTo)
                    Log.v("DBA: ", FacilityDataModel.getInstance().tblFacilities[0].BusinessName)
                    Log.v("Entity Name: ", FacilityDataModel.getInstance().tblFacilities[0].EntityName)
                    Log.v("Business Type: ", TypeTablesModel.getInstance().BusinessType.filter { s->s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())}[0].BusTypeName)
                    Log.v("Time Zone: ", FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName)
                    Log.v("Website URL: ", FacilityDataModel.getInstance().tblFacilities[0].WebSite)
                    Log.v("Wi-Fi Available: ", if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess) "Yes" else "No")
                    Log.v("TAX ID: ", FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber)
                    Log.v("Repair Order Count: ", FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount.toString())
                    Log.v("Annual Inspection: ", FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName())
                    Log.v("Inspection Cycle: ", FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle)
                    if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}.size > 0)
                        Log.v("Service Availability: ", TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}[0].SrvAvaName)
                    else
                        Log.v("Service Availability: ","Undetermined")
                    Log.v("Facility Type: ", FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName)
                    Log.v("ARD Number: ", FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber)
                    statusTextView.append("\n------> ARD Expiration Date: ")
                    if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900"))
                    else
                        Log.v("ARD Date: ", FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY())
                    statusTextView.append("\n------> Provider Type: ")
                    if (!FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId.equals("-1"))
                    Log.v("Provider Type: ", FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId)
                    Log.v("Contract Date: ", FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY())
                    Log.v("Initial Date: ", FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY())
                    Log.v("Billing Month: ", FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName())
                    Log.v("Billing Amount: $", "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()))
                    if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900"))
                    else
                        Log.v("Insurance Exp Date: ", FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY())
                } catch (e: Exception) {
                    statusTextView.append("\nFacility Section Data Issue ............... " + e.message)
                }
                try {
                    //  drawAddressOverallSection
                    statusTextView.append("\nAddress Overall Section ... ")
                    statusTextView.append("\n------> Payment Section ... ")
                    TypeTablesModel.getInstance().PaymentMethodsType.apply {
                        (0 until size).forEach {
                            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.equals(get(it).PmtMethodID)}.isNotEmpty()) {
                                Log.v("Payment Type: ", get(it).PmtMethodName)
                            }
                        }
                    }
                    statusTextView.append("\n------> Language Section ... ")
                    TypeTablesModel.getInstance().LanguageType.apply {
                        (0 until size).forEach {
                            if (FacilityDataModel.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.size>0) {
//                                statusTextView.append(get(it).LangTypeName + " ")
                                Log.v("Language : ", get(it).LangTypeName)
                            }
                        }
                    }
                    statusTextView.append("\n------> Hours Section ... ")
                    FacilityDataModel.getInstance().tblHours[0].apply {
                        statusTextView.append("\n------> Hours Section ... ")
                        Log.v("Sunday - Open: ", if (SunOpen.isNullOrEmpty()) "Closed" else SunOpen)
                        Log.v("Sunday - Close: ", if (SunClose.isNullOrEmpty()) "Closed" else SunClose)
                        Log.v("Monday - Open: ", if (MonOpen.isNullOrEmpty()) "Closed" else MonOpen)
                        Log.v("Monday - Close: ", if (MonClose.isNullOrEmpty()) "Closed" else MonClose)
                        Log.v("Tuesday - Open: ", if (TueOpen.isNullOrEmpty()) "Closed" else TueOpen)
                        Log.v("Tuesday - Close: ", if (TueClose.isNullOrEmpty()) "Closed" else TueClose)
                        Log.v("Wednesday - Open: ", if (WedOpen.isNullOrEmpty()) "Closed" else WedOpen)
                        Log.v("Wednesday - Close: ", if (WedClose.isNullOrEmpty()) "Closed" else WedClose)
                        Log.v("Thursday - Open: ", if (ThuOpen.isNullOrEmpty()) "Closed" else ThuOpen)
                        Log.v("Thursday - Close: ", if (ThuClose.isNullOrEmpty()) "Closed" else ThuClose)
                        Log.v("Friday - Open: ", if (FriOpen.isNullOrEmpty()) "Closed" else FriOpen)
                        Log.v("Friday - Close: ", if (FriClose.isNullOrEmpty()) "Closed" else FriClose)
                        Log.v("Saturday - Open: ", if (SatOpen.isNullOrEmpty()) "Closed" else SatOpen)
                        Log.v("Saturday - Close: ", if (SatClose.isNullOrEmpty()) "Closed" else SatClose)
                        statusTextView.append("\n------------> Nigh Drop Instructions: ")
                        if (NightDropInstr.isNullOrEmpty()) statusTextView.append("NA") else statusTextView.append(NightDropInstr)
                    }
                    statusTextView.append("\n------> Email Section ... ")
                    FacilityDataModel.getInstance().tblFacilityEmail.apply {
                        (0 until size).forEach {
                            if (!get(it).emailID.equals("-1")) {
                                Log.v("Email Type: : ", TypeTablesModel.getInstance().EmailType.filter { s->s.EmailID.equals(get(it).emailTypeId)}[0].EmailName + "- Email : " + get(it).email)

                            }
                        }
                    }

                    statusTextView.append("\n------> Phone Section ... ")
                    FacilityDataModel.getInstance().tblPhone.apply {
                        (0 until size).forEach {
                            if (!get(it).PhoneID.equals("-1")) {
                                Log.v("Phone Type: ", TypeTablesModel.getInstance().LocationPhoneType.filter { s->s.LocPhoneID.equals(get(it).PhoneTypeID)}[0].LocPhoneName + "- Phone : " + get(it).PhoneNumber)
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nAddress Overall Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nPersonnel Section ... ")
                    FacilityDataModel.getInstance().tblPersonnel.apply {
                        (0 until size).forEach {
                            if (get(it).PersonnelID>-1 && get(it).PersonnelTypeID!=TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeName.equals("PRG")}[0].PersonnelTypeID.toInt()) {
                                Log.v("First Name: ", get(it).FirstName)
                                Log.v("Last Name: ", get(it).LastName)
                                Log.v("Certification #: ", get(it).CertificationNum)
                                Log.v("Personnel Type: ", TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeID.equals(get(it).PersonnelTypeID.toString())}[0].PersonnelTypeName)
                                Log.v("RSP User Name: ", get(it).RSP_UserName)
                                Log.v("RSP Email: ", get(it).RSP_UserName)
                                Log.v("Senior Date: ", if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY())
                                Log.v("Start Date: ", if (get(it).startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).startDate.apiToAppFormatMMDDYYYY())
                                Log.v("End Date: ", if (get(it).endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).endDate.apiToAppFormatMMDDYYYY())
                                Log.v("Contract Signer: ", if (get(it).ContractSigner) "X" else "")
                                Log.v("Primary Mail: ", if (get(it).PrimaryMailRecipient) "X" else "")
                                Log.v("Report Recipient: ", if (get(it).ReportRecipient) "X" else "")
                                Log.v("Notification Rec.: ", if (get(it).NotificationRecipient) "X" else "")
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nPersonnel Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nCertification Section ... ")
                    var personnelWithCert = ArrayList<Int>()
                    FacilityDataModel.getInstance().tblPersonnelCertification.apply {
                        (0 until size).forEach {
                            if (!personnelWithCert.contains(get(it).PersonnelID)){
                                personnelWithCert.add(get(it).PersonnelID)
                            }
                        }
                    }

                    personnelWithCert.apply {
                        (0 until size).forEach {
                            if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }.isNotEmpty()) {
                                Log.v("First Name: ", FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].FirstName)
                                Log.v("Last Name: ", FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].LastName)
                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                                    Log.v("A1 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }
                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                                    Log.v("A2 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                }
                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                                    Log.v("A3 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                                    Log.v("A4 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                                    Log.v("A5 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                                    Log.v("A6 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                                    Log.v("A7 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                                    Log.v("A8 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                                    Log.v("A9 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                                    Log.v("C1 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }

                                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()) {
                                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                                    Log.v("L1 Exp. Date: ", if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY())
                                } else {
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nCertification Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nContract Signer Section ... ")
                    FacilityDataModel.getInstance().tblPersonnelSigner.apply {
                        (0 until size).forEach {
                            if (get(it).PersonnelID>-1) {
                                Log.v("First Name: ", get(it).FirstName)
                                Log.v("Last Name: ", if (FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}.isNotEmpty()) FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}[0].LastName else "")
                                Log.v("Addr1: ", get(it).Addr1)
                                Log.v("Addr2: ", get(it).Addr2)
                                Log.v("City: ", get(it).CITY)
                                Log.v("State: ", get(it).ST)
                                Log.v("Zip: ", get(it).ZIP)
                                Log.v("Zip 4: ", get(it).ZIP4)
                                Log.v("Phone: ", get(it).Phone)
                                Log.v("Email: ", get(it).email)
                                Log.v("Contract Start Date: ", if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY())
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nContract Signer Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nAAR Header Section ... ")
                    Log.v("Start Date: ", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY())
                    Log.v("End Date: ", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY())
                    Log.v("Signed Date: ", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY())
                    Log.v("Card Readers: ", FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders)
                } catch (e: Exception) {
                    statusTextView.append("\nAAR Header Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nAAR Portal Tracking Section ... ")
                    FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending { it.PortalInspectionDate }).apply {
                        (0 until size).forEach {
                            if ( !get(it).TrackingID.equals("-1") ) {
                                Log.v("Portal Inspection: ", get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY())
                                Log.v("Logged In: ", if (get(it).LoggedIntoPortal.equals("true")) "Yes" else "No")
                                Log.v("Unacknowledged Tows: ", get(it).NumberUnacknowledgedTows)
                                Log.v("InProgress Tows: ", get(it).InProgressTows)
                                Log.v("InProgress WalkIns: ", get(it).InProgressWalkIns)
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nAAR Portal Tracking Section Data Issue ............... " + e.message)
                }
                try {
                    statusTextView.append("\nVisitation Tracking Section ... ")
                    if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals("00")) {
                            if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.isNotEmpty()) {
                                FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed }).apply {
                                    (0 until size).forEach {
                                        if (!get(it).performedBy.equals("00")) {
                                            Log.v("Date Performed: ", if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY())
                                            var visitationTypeStr = ""
                                            if (get(it).VisitationTypeID.equals("1")) {
                                                visitationTypeStr = VisitationTypes.Annual.toString()
                                            } else if (get(it).VisitationTypeID.equals("2")) {
                                                visitationTypeStr = VisitationTypes.Quarterly.toString()
                                            } else if (get(it).VisitationTypeID.equals("3")) {
                                                visitationTypeStr = VisitationTypes.AdHoc.toString()
                                            } else if (get(it).VisitationTypeID.equals("4")) {
                                                visitationTypeStr = VisitationTypes.Deficiency.toString()
                                            }
                                            Log.v("Visitation Type: ", visitationTypeStr)
                                            Log.v("Performed By: ", get(it).performedBy)
                                            Log.v("AARSigns: ", get(it).AARSigns)
                                            Log.v("Approval Cert.: ", get(it).CertificateOfApproval)
                                            Log.v("Member benefits.: ", get(it).MemberBenefitPoster)
                                            Log.v("QC: ", get(it).QualityControl)
                                            Log.v("Staff Training: ", get(it).StaffTraining)

                                        }
                                    }
                                }
                            }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nVisitation Tracking Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nSoS Section ... ")
                    Log.v("Fixed Labor Rate: ",FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate)
                    Log.v("Diagnostic Rate: ", FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate)
                    Log.v("Labor Min: ", FacilityDataModel.getInstance().tblScopeofService[0].LaborMin)
                    Log.v("Labor Max: ", FacilityDataModel.getInstance().tblScopeofService[0].LaborMax)
                    Log.v("# of Bays: ", FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays)
                    Log.v("# of Lifts: ", FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts)
                    Log.v("Warranty Period: ", if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}.size>0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}[0].WarrantyTypeName else "")
                    Log.v("Discount Percentage: ",FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap)
                    Log.v("Max Discount: ", FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount)
                } catch (e: Exception) {
                    statusTextView.append("\nSoS Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nVehicle Services Section ... ")
                    Log.v("Fixed Labor Rate: ",FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate)
                    var vehicleTypeID = ""

                    TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.equals("Automobile") }.apply {
                        (0 until size).forEach {
                            vehicleTypeID = get(it).VehiclesTypeID
                            Log.v("Vehicle Type: ",get(it).VehiclesTypeName)
                            if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.isNotEmpty()) {
                                TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.apply {
                                    (0 until size).forEach { innerIt ->
                                        if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(innerIt).ScopeServiceID.toInt() }.isNotEmpty()) {
                                            Log.v("Scope Service Name: ",get(innerIt).ScopeServiceName)
                                        } else {
                                            Log.v("Scope Service Name: ",get(innerIt).ScopeServiceName)
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    statusTextView.append("\nVehicle Services Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nVehicles Section ... ")
                    Log.v("Vehcile Type: ", TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeID.toInt()==1 }[0].VehiclesTypeName)
                    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
                        (0 until size).forEach {
                            Log.v("Category Name: ", get(it).VehCategoryName)
                            TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == get(it).VehCategoryID.toInt() && s.VehicleCategoryID == get(it).VehCategoryID.toInt() }.apply {
                                (0 until size).forEach { vMakeIt ->
                                    if (FacilityDataModel.getInstance().tblFacVehicles.filter { s -> s.VehicleID == get(vMakeIt).VehicleID }.isNotEmpty()) {
                                        Log.v("Make Name: ", get(vMakeIt).MakeName)
                                    } else {
                                        Log.v("Make Name: ", get(vMakeIt).MakeName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nVehicles Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nProgram Section ... ")
                    Log.v("Fixed Labor Rate: ",FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate)
                    FacilityDataModel.getInstance().tblPrograms.apply {
                        (0 until size).forEach {
                            if ( !get(it).ProgramID.equals("-1") ) {
                                if (TypeTablesModel.getInstance().ProgramsType.filter { s->s.ProgramTypeID.equals(get(it).ProgramTypeID)}.isNotEmpty()) {
                                    Log.v("Program Type: ",TypeTablesModel.getInstance().ProgramsType.filter { s -> s.ProgramTypeID.equals(get(it).ProgramTypeID) }[0].ProgramTypeName)
                                    Log.v("Effective Date: ",if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Exp. Date: ",if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Comments: ",get(it).Comments)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nProgram Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nFacility Services Section ... ")
                    FacilityDataModel.getInstance().tblFacilityServices.apply {
                        (0 until size).forEach {
                            if ( !get(it).FacilityServicesID.equals("-1") ) {
                                if (TypeTablesModel.getInstance().ServicesType.filter { s->s.ServiceTypeID.equals(get(it).ServiceID)}.isNotEmpty()) {
                                    Log.v("Service Type: ",TypeTablesModel.getInstance().ServicesType.filter { s -> s.ServiceTypeID.equals(get(it).ServiceID) }[0].ServiceTypeName)
                                    Log.v("Eff Date: ",if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Exp Date: ",if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Comments: ",get(it).Comments)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nFacility Services Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nAffiliation Section ... ")
                    FacilityDataModel.getInstance().tblAffiliations.apply {
                        (0 until size).forEach {
                            if ( !get(it).AffiliationID.equals("-1") ) {
                                if (TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID)}.isNotEmpty()) {
                                    Log.v("Affiliation Type: ",TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID) }[0].AffiliationTypeName)
                                    if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }.isNotEmpty()) {
                                        Log.v("Affiliation Detail: ",TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }[0].AffiliationDetailTypeName)
                                    }
                                    Log.v("Eff. Date: ",if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Exp. Date: ",if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Comment: ",get(it).comment)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nAffiliation Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nAffiliation Section ... ")
                    FacilityDataModel.getInstance().tblAffiliations.apply {
                        (0 until size).forEach {
                            if ( !get(it).AffiliationID.equals("-1") ) {
                                if (TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID)}.isNotEmpty()) {
                                    Log.v("Affiliation Type: ",TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID) }[0].AffiliationTypeName)
                                    if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }.isNotEmpty()) {
                                        Log.v("Affiliation Detail: ",TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }[0].AffiliationDetailTypeName)
                                    }
                                    Log.v("Eff. Date: ",if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Exp. Date: ",if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY())
                                    Log.v("Comment: ",get(it).comment)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nAffiliation Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nDeficiency Section ... ")
                    FacilityDataModel.getInstance().tblDeficiency.apply {
                        (0 until size).forEach {
                            if (!get(it).DefTypeID.equals("-1") && get(it).ClearedDate.isNullOrEmpty() && !get(it).DefTypeID.equals("0")) {
                                Log.v("Type: ",TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.equals(get(it).DefTypeID) }[0].DeficiencyName)
                                Log.v("Visitation Date: ",if (get(it).VisitationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).VisitationDate.apiToAppFormatMMDDYYYY())
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nDeficiency Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nComplaints Section ... ")
                    FacilityDataModel.getInstance().tblComplaintFiles.apply {
                        (0 until size).forEach {
                            if (!get(it).ComplaintID.isNullOrEmpty()) {
                                Log.v("Complaint ID: ",get(it).ComplaintID)
                                Log.v("First Name: ",get(it).FirstName)
                                Log.v("Last Name: ",get(it).LastName)
                                Log.v("Received Date: ",if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY())
                                Log.v("Reason: ",get(it).ComplaintReasonName)
                                Log.v("Resolution: ",get(it).ComplaintResolutionName)
                            }
                        }
                    }
                } catch (e: Exception) {
                    statusTextView.append("\nComplaints Section Data Issue ............... " + e.message)
                }

                try {
                    statusTextView.append("\nComments Section ... ")
                    Log.v("Visitation Comments: ",PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                } catch (e: Exception) {
                    statusTextView.append("\nComments Section Data Issue ............... " + e.message)
                }

                val alertDialogBuilder = AlertDialog.Builder(
                        context)

                // set title
                alertDialogBuilder.setTitle("Confirmation ...")

                // set dialog message
                alertDialogBuilder
                        .setMessage("Generate Missing PDF ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            statusTextView.text = ""
                            createSpecialistPDF()
                            dialog.cancel()
                        }
                        .setNegativeButton("No") { dialog, id ->
                            dialog.cancel()
                        }
                // create alert dialog
                val alertDialog = alertDialogBuilder.create()
                // show it
                alertDialog.show()
            }, 5000)

        }

        // Check All Data
    }

    fun createSpecialistPDF() {
        var imageRepSignature: Image;
        var imageSpecSignature: Image;
        var imageDefSignature: Image;
        var imageWaiveSignature: Image;
        val ims = activity?.assets?.open("nosignatureicon.png");
        val bmp = BitmapFactory.decodeStream(ims);
        val stream = ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageRepSignature = Image.getInstance(stream.toByteArray());
        imageSpecSignature = Image.getInstance(stream.toByteArray());
        imageDefSignature = Image.getInstance(stream.toByteArray());
        imageWaiveSignature = Image.getInstance(stream.toByteArray());

        if (repwaiverSignatureBmp != null) {
            try {
                var baos = ByteArrayOutputStream();
                repwaiverSignatureBmp?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                var imageInByte = baos.toByteArray();
                imageWaiveSignature = Image.getInstance(imageInByte)
                imageWaiveSignature.scaleToFit(5F, 5F)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }

        if (deffSignatureBmp != null) {
            try {
                var baos = ByteArrayOutputStream();
                deffSignatureBmp?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                var imageInByte = baos.toByteArray();
                imageDefSignature = Image.getInstance(imageInByte)
                imageDefSignature.scaleToFit(5F, 5F)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }

        if (repSignatureBmp != null) {
            try {
                var baos = ByteArrayOutputStream();
                repSignatureBmp?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                var imageInByte = baos.toByteArray();
                imageRepSignature = Image.getInstance(imageInByte)
                imageRepSignature.scaleToFit(5F, 5F)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }

        if (specSignatureBmp != null) {
            try {
                val baos = ByteArrayOutputStream();
                specSignatureBmp?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                val imageInByte = baos.toByteArray();
                imageSpecSignature = Image.getInstance(imageInByte)
                imageSpecSignature.scaleToFit(10F, 10F)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }

//        if (defS != null) {
//            try {
//                val baos = ByteArrayOutputStream();
//                (activity as FormsActivity).imageDefSignature?.compress(Bitmap.CompressFormat.PNG, 70, baos);
//                val imageInByte = baos.toByteArray();
//                imageDefSignature = Image.getInstance(imageInByte)
//                imageDefSignature.scaleToFit(10F, 10F)
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace();
//            }
//        }
        val document = Document()
        var filePath = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+visitationIDEditText.text.toString()+"_VisitationDetails_ForSpecialist.pdf";
        } else {
            filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+visitationIDEditText.text.toString()+"_VisitationDetails_ForSpecialist.pdf";
        }
        val file = File(filePath)
        var writer = PdfWriter.getInstance(document, FileOutputStream(file))
        val event = HeaderFooterPageEvent()
        writer.pageEvent = event
        document.setMargins(10f,10f,20f,30f)
        document.open()

        document.addTitle("AAR Visitation")
        // Headewr Section
        var paragraph = Paragraph("AAR Visitation", MaintitleFont)
        paragraph.alignment = Element.ALIGN_CENTER
        document.add(paragraph)

        paragraph = Paragraph("Facility " + FacilityDataModel.getInstance().tblFacilities[0].FACNo + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName, MaintitleFont)
        paragraph.alignment = Element.ALIGN_CENTER
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)

        // Visitation Section
        paragraph = Paragraph("")
        paragraph.add(drawVisitaionSection(imageRepSignature,imageSpecSignature,imageWaiveSignature))
        document.add(paragraph)


        paragraph = Paragraph("FACILITY", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawFacilitySection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("")
        paragraph.add(drawAddressOverallSection())
        document.add(paragraph)

        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawAddressSection())
        document.add(paragraph)

        paragraph = Paragraph("Personnel", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawPersonnelSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("ASE Certifications", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawCertificationsSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Contract Signers", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawSignersSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("RSP", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawAARHeaderSection())
        document.add(paragraph)
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawAARTrackingSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Visitation Tracking", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawVisitationTrackingSection())
        document.add(paragraph)
        addEmptyLine(document, 1)


        paragraph = Paragraph("Scope of Service", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawSoSSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Vehicle Services", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)

        var vehicleTypeID = ""

        TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.equals("Automobile") }.apply {
            (0 until size).forEach {
                vehicleTypeID = get(it).VehiclesTypeID
                paragraph = Paragraph(get(it).VehiclesTypeName, MaintitleFont)
                paragraph.alignment = Element.ALIGN_LEFT
                document.add(paragraph)
                document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
                addEmptyLine(document, 1)
                paragraph = Paragraph("")
                paragraph.add(drawVehicleServicesSection(vehicleTypeID))
                document.add(paragraph)
                addEmptyLine(document, 1)
            }
        }

        paragraph = Paragraph("Vehicles", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)

        paragraph = Paragraph(TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeID.toInt()==1 }[0].VehiclesTypeName, SubtitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
            (0 until size).forEach {
                paragraph = Paragraph(get(it).VehCategoryName, titleFont)
                paragraph.alignment = Element.ALIGN_LEFT
                document.add(paragraph)
                document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
                addEmptyLine(document, 1)
                paragraph = Paragraph("")
                paragraph.add(drawVehiclesSection(get(it).VehCategoryID))
                document.add(paragraph)
                addEmptyLine(document, 1)
            }
        }

        paragraph = Paragraph("Programs", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawProgramsSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Facility Services", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawFacServicesSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Affiliations", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawAffiliationSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Deficiencies", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
//    paragraph.add(drawDeficienciesSection())
        paragraph.add(drawDeficiencySectionForShop())
        document.add(paragraph)
        addEmptyLine(document, 1)

        paragraph = Paragraph("Complaints", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")
        paragraph.add(drawComplaintsSection())
        document.add(paragraph)
        addEmptyLine(document, 1)

        createPDFLogData += " - drawPhotosSection"
        paragraph = Paragraph("Photos", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
        paragraph = Paragraph("")

        // Load Faciloty Photos from PRG DB
        val table = PdfPTable(13)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Thumbnail", 2,true))
        table.addCell(addCellWithBorder("File Name", 1,true))
        table.addCell(addCellWithBorder("File Description", 2,true))
        table.addCell(addCellWithBorder("Approval Requested", 1,true))
        table.addCell(addCellWithBorder("Approved", 1,true))
        table.addCell(addCellWithBorder("Approved By", 1,true))
        table.addCell(addCellWithBorder("Approved Date", 1,true))
        table.addCell(addCellWithBorder("Downstream Apps", 2,true))
        table.addCell(addCellWithBorder("Image URL", 2,true))

        document.add(table)
        addEmptyLine(document, 1)
        paragraph = Paragraph("Visitation Comments", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)

        paragraph = Paragraph("")
        paragraph.add(drawCommentsSection())
        document.add(paragraph)
        addEmptyLine(document, 1)
        document.close()
        val target = Intent(Intent.ACTION_VIEW)
        target.setDataAndType(FileProvider.getUriForFile(requireContext(), "com.inspection.android.fileprovider", file), "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

        val intent = Intent.createChooser(target, "Open File")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Instruct the user to install a PDF reader here, or something
        }
        uploadPDF(file, "Specialist")
    }

    fun uploadPDF(file: File, type: String) {
        var performedBy = FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.split(" ")
        var email = TypeTablesModel.getInstance().EmployeeList.filter { s->s.FirstName.equals(performedBy[0]) && s.LastName.equals(performedBy[1])}[0].Email
        Log.v("Specialist Email --> " , email)
//        if (type.equals("Shop")) {
//            if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf && PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto.isNotEmpty()){
//                email = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto
//            }
//        }
        var facNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo
        var visitationType = PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype
        var waived = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) 'Y' else 'N'
        var waivedComments = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments else ""
        var emailPDF = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf) "1" else "0"
        var busName = URLEncoder.encode(FacilityDataModel.getInstance().tblFacilities[0].BusinessName , "UTF-8");
        var directorEmail = if (!PRGDataModel.getInstance().tblPRGFacilityDirectors.isNullOrEmpty()) PRGDataModel.getInstance().tblPRGFacilityDirectors[0].directoremail else ""
        Log.v("Director Email --> " , directorEmail)
        val multipartRequest = MultipartRequest(Constants.uploadFile+email+"&emailPDF=${emailPDF}&director=${directorEmail}&waived=${waived}&type=${type}&specialistEmail="+email+"&facName=${busName}&facNo=${facNo}&visitationType=${visitationType}&waivedComments=${waivedComments}&sessionId="+ApplicationPrefs.getInstance(activity).getSessionID(), null, file, Response.Listener { response ->
            try {
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
        })
        val socketTimeout = 30000//30 seconds
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        multipartRequest.retryPolicy = policy
        Volley.newRequestQueue(activity).add(multipartRequest)
    }

    private fun drawVisitaionSection(imageRep: Image?,imageSpec: Image?,imageWaiver: Image?) : PdfPTable {
//        createPDFLogData += " - drawVisitaionSection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
//    table.addCell(addCell("Type of Inspection: " + FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),1,false));
        PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod = TypeTablesModel.getInstance().VisitationMethodType.filter { s->s.TypeID==FacilityDataModel.getInstance().tblVisitationTracking.filter { s->s.visitationID.equals(visitationIDEditText.text.toString())}[0].VisitationMethodTypeID.toInt()}[0].TypeName
        table.addCell(addCell("Type of Inspection: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype,1,false));
        table.addCell(addCell("Month Due: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.toInt().monthNoToName(),1,false));
        table.addCell(addCell("Changes Made: "+if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes" ,1,false))
        table.addCell(addCell("Date of Visitation: "+ PRGDataModel.getInstance().tblPRGVisitationHeader[0].changedate.apiToAppFormatMMDDYYYY(),1,false));

        table.addCell(addCell("Visitation Reason: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason,2,false));
        table.addCell(addCell("Visitation Method: " + if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) "NA" else PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod,2,false));
        FacilityDataModel.getInstance().tblVisitationTracking[0].VisitationMethodTypeID

        if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
            table.addCell(addCell("Facility Representative's Name: ",1,false));
            table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep,1,false));
        }
        table.addCell(addCell("Automotive Specialist:",1,false));
        table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist,1,false));
        if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
            if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("In Person"))
                table.addCell(addCell("Facility Representative's Signature:", 2, false));
            else
                table.addCell(addCell("", 2, false));
        } else  {
            table.addCell(addCell("",2,false));
            table.addCell(addCell("Waiver Comments:",1,false));
            table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments.toString(),3,false));
        }
        if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
            table.addCell(addCell("Specialist Signature:",2,false));
            if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("In Person")) {
                imageRep?.scaleAbsolute(50F, 50F)
                val c = PdfPCell(imageRep)
                c.colspan = 2
                c.border = Rectangle.NO_BORDER
                c.horizontalAlignment = Element.ALIGN_CENTER
                c.rowspan = 3
                table.addCell(c)
            } else {
//            table.addCell(addCell("",2,false));
                val c = PdfPCell()
                c.colspan = 2
                c.border = Rectangle.NO_BORDER
                c.horizontalAlignment = Element.ALIGN_CENTER
                c.rowspan = 3
                table.addCell(c)
            }
            imageSpec?.scaleAbsolute(50F, 50F)
            val d = PdfPCell(imageSpec)
            d.colspan = 2
            d.border = Rectangle.NO_BORDER
            d.horizontalAlignment = Element.ALIGN_CENTER
            d.rowspan = 3
            table.addCell(d)
        } else  {
            table.addCell(addCell("Waiver Signature:",2,false));
            table.addCell(addCell("",2,false));
            imageWaiver?.scaleAbsolute(50F,50F)
            val d = PdfPCell(imageWaiver)
            d.colspan = 2
            d.border = Rectangle.NO_BORDER
            d.horizontalAlignment = Element.ALIGN_CENTER
            d.rowspan = 3
            table.addCell(d)
            table.addCell(addCell("",2,false));
        }
//    imageSpec?.scaleAbsolute(50F,50F)
//    val d = PdfPCell(imageSpec)
//    d.colspan = 2
//    d.border = Rectangle.NO_BORDER
//    d.horizontalAlignment = Element.ALIGN_CENTER
//    d.rowspan = 3
//    table.addCell(d)

//    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature.toString(),1,false));
//    if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
//        table.addCell(addCell("",1,false));
//    }

        return table
    }

    private fun drawAARHeaderSection() : PdfPTable {
        createPDFLogData += " - drawAARHeaderSection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
        table.addCell(addCell("Start Date:",1,true));
        table.addCell(addCell("End Date:",1,true));
        table.addCell(addCell("Addendum Signed Date:",1,true));
        table.addCell(addCell("#of Card Readers:",1,true));
        table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY(),1,true))
        table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY(),1,true))
        table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY(),1,true))
        table.addCell(addCell(FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders,1,true));
        return table
    }

    private fun drawProgramsSection() : PdfPTable {
        createPDFLogData += " - drawProgramsSection"
        val table = PdfPTable(6)
        table.setWidthPercentage(80f)
        table.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(addCellWithBorder("Program Name", 2,false))
        table.addCell(addCellWithBorder("Effective Date", 1,true))
        table.addCell(addCellWithBorder("Expiration Date", 1,true))
        table.addCell(addCellWithBorder("Comments", 2,false))
        FacilityDataModel.getInstance().tblPrograms.apply {
            (0 until size).forEach {
                if ( !get(it).ProgramID.equals("-1") ) {
                    if (TypeTablesModel.getInstance().ProgramsType.filter { s->s.ProgramTypeID.equals(get(it).ProgramTypeID)}.isNotEmpty()) {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ProgramsType.filter { s -> s.ProgramTypeID.equals(get(it).ProgramTypeID) }[0].ProgramTypeName, 2, false))
                        table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(get(it).Comments, 2, false))
                    }
                }
            }
        }
        return table
    }

    private fun drawComplaintsSection() : PdfPTable {
        createPDFLogData += " - drawComplaintsSection"
        val table = PdfPTable(9)
        table.setWidthPercentage(100f)
        table.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(addCellWithBorder("Complaint ID", 2,false))
        table.addCell(addCellWithBorder("First Name", 1,true))
        table.addCell(addCellWithBorder("Last Name", 1,true))
        table.addCell(addCellWithBorder("Received Date", 1,false))
        table.addCell(addCellWithBorder("Complaint Reason", 2,false))
        table.addCell(addCellWithBorder("Complaint Resolution", 2,false))
        FacilityDataModel.getInstance().tblComplaintFiles.apply {
            (0 until size).forEach {
                if (!get(it).ComplaintID.isNullOrEmpty()) {
                    table.addCell(addCellWithBorder(get(it).ComplaintID, 2, false))
                    table.addCell(addCellWithBorder(get(it).FirstName, 1, false))
                    table.addCell(addCellWithBorder(get(it).LastName, 1, false))
                    table.addCell(addCellWithBorder(if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(get(it).ComplaintReasonName, 2, false))
                    table.addCell(addCellWithBorder(get(it).ComplaintResolutionName, 2, false))
                }
            }
        }
        return table
    }

    private fun drawFacServicesSection() : PdfPTable {
        createPDFLogData += " - drawFacServicesSection"
        val table = PdfPTable(6)
        table.setWidthPercentage(80f)
        table.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(addCellWithBorder("Service Name", 2,false))
        table.addCell(addCellWithBorder("Effective Date", 1,true))
        table.addCell(addCellWithBorder("Expiration Date", 1,true))
        table.addCell(addCellWithBorder("Comments", 2,false))
        FacilityDataModel.getInstance().tblFacilityServices.apply {
            (0 until size).forEach {
                if ( !get(it).FacilityServicesID.equals("-1") ) {
                    if (TypeTablesModel.getInstance().ServicesType.filter { s->s.ServiceTypeID.equals(get(it).ServiceID)}.isNotEmpty()) {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ServicesType.filter { s -> s.ServiceTypeID.equals(get(it).ServiceID) }[0].ServiceTypeName, 2, false))
                        table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(get(it).Comments, 2, false))
                    }
                }
            }
        }
        return table
    }

    private fun drawAffiliationSection() : PdfPTable {
        createPDFLogData += " - drawAffiliationSection"
        val table = PdfPTable(8)
        table.setWidthPercentage(80f)
        table.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(addCellWithBorder("Affiliation Name", 2,false))
        table.addCell(addCellWithBorder("Affiliation Details", 2,false))
        table.addCell(addCellWithBorder("Effective Date", 1,true))
        table.addCell(addCellWithBorder("Expiration Date", 1,true))
        table.addCell(addCellWithBorder("Comments", 2,false))
        FacilityDataModel.getInstance().tblAffiliations.apply {
            (0 until size).forEach {
                if ( !get(it).AffiliationID.equals("-1") ) {
                    if (TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID)}.isNotEmpty()) {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID) }[0].AffiliationTypeName, 2, false))
                        if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }.isNotEmpty()) {
                            table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }[0].AffiliationDetailTypeName, 2, false))
                        } else {
                            table.addCell(addCellWithBorder("", 2, false))
                        }
                        table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                        table.addCell(addCellWithBorder(get(it).comment, 2, false))
                    }
                }
            }
        }
        return table
    }

    private fun drawDeficiencySectionForShop() : PdfPTable {
        createPDFLogData += " - drawDeficiencySectionForShop"
        val table = PdfPTable(3)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Deficiency", 1,true))
        table.addCell(addCellWithBorder("Inspection Date", 1,true))
        table.addCell(addCellWithBorder("Due Date", 1,true))
        FacilityDataModel.getInstance().tblDeficiency.apply {
            (0 until size).forEach {
                if (!get(it).DefTypeID.equals("-1") && get(it).ClearedDate.isNullOrEmpty() && !get(it).DefTypeID.equals("0")) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.equals(get(it).DefTypeID) }[0].DeficiencyName,1,true))
                    table.addCell(addCellWithBorder(if (get(it).VisitationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).VisitationDate.apiToAppFormatMMDDYYYY(),1,true));
                    table.addCell(addCellWithBorder("",1,true));
                }
            }
        }
        if (FacilityDataModel.getInstance().tblDeficiency.size % 3 > 0) {
            table.addCell(addCell(" ", (FacilityDataModel.getInstance().tblDeficiency.size % 3)*2, false))
        }
        return table
    }

    fun drawCommentsSection() :PdfPTable {
        val commentsTable = PdfPTable(1)
        commentsTable.setWidthPercentage(100f)
        val cell = PdfPCell(Paragraph((PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments), normalFont));
        cell.colspan=1
        cell.setBorder(Rectangle.NO_BORDER);
        commentsTable.addCell(cell)
//    commentsTable.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments, 1,false))
        return commentsTable
    }

    private fun drawVehiclesSection(vehicleCatID : String) : PdfPTable {
        createPDFLogData += " - drawVehiclesSection"
        val columnWidths = floatArrayOf(1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f)
        val table = PdfPTable(columnWidths)
        val vehicleTypeID=1
        table.widthPercentage = 100f
        TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.apply {
            (0 until size).forEach { vMakeIt ->
                if (FacilityDataModel.getInstance().tblFacVehicles.filter { s -> s.VehicleID == get(vMakeIt).VehicleID }.isNotEmpty()) {
                    table.addCell(addTick(false,false))
                    table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
                } else {
                    table.addCell(addCell(" ", 1, false))
                    table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
                }
            }
        }
        if (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8 > 0) {
            table.addCell(addCell(" ", (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8)*2, false))
        }
        return table
    }

    private fun drawVehicleServicesSection(vehicleTypeID: String) : PdfPTable {
        createPDFLogData += " - drawVehicleServicesSection"
        val columnWidths = floatArrayOf(1f, 4f,1f, 4f,1f, 4f)
        val table = PdfPTable(columnWidths)
        table.widthPercentage = 100f

        if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.isNotEmpty()) {
            TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.apply {
                (0 until size).forEach { innerIt ->
                    if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(innerIt).ScopeServiceID.toInt() }.isNotEmpty()) {
                        table.addCell(addTick(false,false))
                        table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
                    } else {
                        table.addCell(addCell(" ",1,false))
                        table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
                    }
                }
            }
        }
//    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3 > 0) {
//        table.addCell(addCell(" ", (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3)*2, false))
//    }
        table.addCell(addCell(" ",6,false))
        return table
    }

    private fun drawSoSSection() : PdfPTable {
        createPDFLogData += " - drawSoSSection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
        table.addCell(addCell("Fixed Labor Rate\n$" + FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate ,1,true));
        table.addCell(addCell("Diagnostic Rate\n$" + FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate,1,true));
        table.addCell(addCell("Labor Rate Matrix Min\n$" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMin ,1,true));
        table.addCell(addCell("Labor Rate Matrix Min\n$" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMax,1,true));
        table.addCell(addCell("# of Bays: "+FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays,1,true));
        table.addCell(addCell("# of Lifts: "+FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts,1,true));
        table.addCell(addCell("Warranty Period: "+if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}.size>0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}[0].WarrantyTypeName else "",1,true));
        table.addCell(addCell("",1,true));
        table.addCell(addCell("Discount Percentage: "+FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap + "%",1,true));
        table.addCell(addCell("Max Discount Amount: "+FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount,1,true));
        table.addCell(addCell("",2,true));
        return table
    }

    private fun drawVisitationTrackingSection() : PdfPTable {
        val table = PdfPTable(9)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Date Performed", 1,true))
        table.addCell(addCellWithBorder("Visitation Type", 1,true))
        table.addCell(addCellWithBorder("Deficiency (Yes/No)", 1,true))
        table.addCell(addCellWithBorder("Performed By", 1,true))
        table.addCell(addCellWithBorder("AAR Sign", 1,true))
        table.addCell(addCellWithBorder("Certificate of Approval", 1,true))
        table.addCell(addCellWithBorder("Member Benefits Poster(s)", 1,true))
        table.addCell(addCellWithBorder("Quality Control Process", 1,true))
        table.addCell(addCellWithBorder("Staff Training Process", 1,true))
        var visitationType = ""
        if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals("00")) {
            try {
                if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.isNotEmpty()) {
                    FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed }).apply {
                        (0 until size).forEach {
                            if (!get(it).performedBy.equals("00")) {
                                table.addCell(addCellWithBorder(if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY(), 1, true));
//                            table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(), 1, true));
                                visitationType = ""
                                if (get(it).VisitationTypeID.equals("1")) {
                                    visitationType = VisitationTypes.Annual.toString()
                                } else if (get(it).VisitationTypeID.equals("2")) {
                                    visitationType = VisitationTypes.Quarterly.toString()
                                } else if (get(it).VisitationTypeID.equals("3")) {
                                    visitationType = VisitationTypes.AdHoc.toString()
                                } else if (get(it).VisitationTypeID.equals("4")) {
                                    visitationType = VisitationTypes.Deficiency.toString()
                                }
                                table.addCell(addCellWithBorder(visitationType, 1, true));
                                table.addCell(addCellWithBorder("", 1, true));
                                table.addCell(addCellWithBorder(get(it).performedBy, 1, true))
                                table.addCell(addCellWithBorder(get(it).AARSigns, 1, false))
                                table.addCell(addCellWithBorder(get(it).CertificateOfApproval, 1, false))
                                table.addCell(addCellWithBorder(get(it).MemberBenefitPoster, 1, false))
                                table.addCell(addCellWithBorder(get(it).QualityControl, 1, false))
                                table.addCell(addCellWithBorder(get(it).StaffTraining, 1, false))
                            }
                        }
                    }
                }
            } catch (e: java.lang.Exception){

            }
        }
        return table
    }

    private fun drawAARTrackingSection() : PdfPTable {
        val table = PdfPTable(7)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("RSP Inspection Date", 1,true))
        table.addCell(addCellWithBorder("Logged Into RSP", 1,true))
        table.addCell(addCellWithBorder("# Unacknowledged Tows", 1,true))
        table.addCell(addCellWithBorder("In Progress Tows", 1,true))
        table.addCell(addCellWithBorder("In Progress Walk Ins", 1,true))
        table.addCell(addCell("", 1,true))
        table.addCell(addCell("", 1,true))
        FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending { it.PortalInspectionDate }).apply {
            (0 until size).forEach {
                if ( !get(it).TrackingID.equals("-1") ) {
                    table.addCell(addCellWithBorder(get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY(),1,true))
                    table.addCell(addCellWithBorder(if (get(it).LoggedIntoPortal.equals("true")) "Yes" else "No",1,true))
                    table.addCell(addCellWithBorder(get(it).NumberUnacknowledgedTows,1,true))
                    table.addCell(addCellWithBorder(get(it).InProgressTows,1,true))
                    table.addCell(addCellWithBorder(get(it).InProgressWalkIns,1,true))
                    table.addCell(addCell("",1,true))
                    table.addCell(addCell("",1,true))
                }
            }
        }
        return table

    }

    private fun drawSignersSection () : PdfPTable {
        createPDFLogData += " - drawSignersSection"
        val table = PdfPTable(12)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("First Name", 1,true))
        table.addCell(addCellWithBorder("Last Name", 1,true))
        table.addCell(addCellWithBorder("Address", 1,true))
        table.addCell(addCellWithBorder("Address2", 1,true))
        table.addCell(addCellWithBorder("City", 1,true))
        table.addCell(addCellWithBorder("State", 1,true))
        table.addCell(addCellWithBorder("ZIP", 1,true))
        table.addCell(addCellWithBorder("ZIP4", 1,true))
        table.addCell(addCellWithBorder("Phone", 1,true))
        table.addCell(addCellWithBorder("Email", 1,true))
        table.addCell(addCellWithBorder("Contract Start Date", 1,true))
        table.addCell(addCellWithBorder("Contract End Date", 1,true))
        FacilityDataModel.getInstance().tblPersonnelSigner.apply {
            (0 until size).forEach {
                if (get(it).PersonnelID>-1) {
                    table.addCell(addCellWithBorder(get(it).FirstName,1,true))
                    table.addCell(addCellWithBorder(if (FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}.isNotEmpty()) FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}[0].LastName else "",1,true))
                    table.addCell(addCellWithBorder(get(it).Addr1,1,true))
                    table.addCell(addCellWithBorder(get(it).Addr2,1,true))
                    table.addCell(addCellWithBorder(get(it).CITY,1,true))
                    table.addCell(addCellWithBorder(get(it).ST,1,true))
                    table.addCell(addCellWithBorder(get(it).ZIP,1,true))
                    table.addCell(addCellWithBorder(get(it).ZIP4,1,true))
                    table.addCell(addCellWithBorder(get(it).Phone,1,true))
                    table.addCell(addCellWithBorder(get(it).email,1,true))
                    table.addCell(addCellWithBorder(if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(),1,true));
                    table.addCell(addCellWithBorder("",1,true));
                }
            }
        }
        return table
    }

    private fun drawCertificationsSection () : PdfPTable {
        createPDFLogData += " - drawCertificationsSection"
        val table = PdfPTable(13)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("First Name", 1,true))
        table.addCell(addCellWithBorder("Last Name", 1,true))
        table.addCell(addCellWithBorder("A1", 1,true))
        table.addCell(addCellWithBorder("A2", 1,true))
        table.addCell(addCellWithBorder("A3", 1,true))
        table.addCell(addCellWithBorder("A4", 1,true))
        table.addCell(addCellWithBorder("A5", 1,true))
        table.addCell(addCellWithBorder("A6", 1,true))
        table.addCell(addCellWithBorder("A7", 1,true))
        table.addCell(addCellWithBorder("A8", 1,true))
        table.addCell(addCellWithBorder("A9", 1,true))
        table.addCell(addCellWithBorder("C1", 1,true))
        table.addCell(addCellWithBorder("L1", 1,true))

        var personnelWithCert = ArrayList<Int>()
        FacilityDataModel.getInstance().tblPersonnelCertification.apply {
            (0 until size).forEach {
                if (!personnelWithCert.contains(get(it).PersonnelID)){
                    personnelWithCert.add(get(it).PersonnelID)
                }
            }
        }

        personnelWithCert.apply {
            (0 until size).forEach {
                if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }.isNotEmpty()) {
                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].FirstName, 1, true))
                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].LastName, 1, true))
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                    if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()) {
                        val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                        table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                    } else {
                        table.addCell(addCellWithBorder("", 1, true))
                    }

                }
            }
        }
        return table
    }

    private fun drawPersonnelSection () : PdfPTable {
        val table = PdfPTable(15)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Personnel Type", 1,true))
        table.addCell(addCellWithBorder("First Name", 1,true))
        table.addCell(addCellWithBorder("Last Name", 1,true))
        table.addCell(addCellWithBorder("Certification #", 2,true))
        table.addCell(addCellWithBorder("RSP User ID", 1,true))
        table.addCell(addCellWithBorder("Email Address", 2,true))
        table.addCell(addCellWithBorder("Seniority Date", 1,true))
        table.addCell(addCellWithBorder("Start Date", 1,true))
        table.addCell(addCellWithBorder("End Date", 1,true))
        table.addCell(addCellWithBorder("Contract Signer", 1,true))
        table.addCell(addCellWithBorder("Primary Mail Recipient", 1,true))
        table.addCell(addCellWithBorder("Report Recipient", 1,true))
        table.addCell(addCellWithBorder("Notification Recipient", 1,true))
        FacilityDataModel.getInstance().tblPersonnel.apply {
            (0 until size).forEach {
                if (get(it).PersonnelID>-1 && get(it).PersonnelTypeID!=TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeName.equals("PRG")}[0].PersonnelTypeID.toInt()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeID.equals(get(it).PersonnelTypeID.toString())}[0].PersonnelTypeName, 1,true))
                    table.addCell(addCellWithBorder(get(it).FirstName,1,true))
                    table.addCell(addCellWithBorder(get(it).LastName,1,true))
                    table.addCell(addCellWithBorder(get(it).CertificationNum,2,true))
                    table.addCell(addCellWithBorder(get(it).RSP_UserName,1,true))
                    table.addCell(addCellWithBorder(get(it).RSP_Email,2,true))
                    table.addCell(addCellWithBorder(if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(),1,true));
                    table.addCell(addCellWithBorder(if (get(it).startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).startDate.apiToAppFormatMMDDYYYY(),1,true));
                    table.addCell(addCellWithBorder(if (get(it).endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).endDate.apiToAppFormatMMDDYYYY(),1,true));
                    table.addCell(addCellWithBorder(if (get(it).ContractSigner) "X" else "",1,true))
                    table.addCell(addCellWithBorder(if (get(it).PrimaryMailRecipient) "X" else "",1,true))
                    table.addCell(addCellWithBorder(if (get(it).ReportRecipient) "X" else "",1,true))
                    table.addCell(addCellWithBorder(if (get(it).NotificationRecipient) "X" else "",1,true))
                }
            }
        }
        return table
    }

    private fun drawFacilitySection() : PdfPTable {
        createPDFLogData += " - drawFacilitySection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
        table.addCell(addCell("Contract Number: " ,1,false));
        table.addCell(addCell("Contract Type: "+FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,1,false));
        table.addCell(addCell("Office: "+FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,1,false));
        table.addCell(addCell("Assigned To: "+ FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,1,false));
        table.addCell(addCell("DBA: "+FacilityDataModel.getInstance().tblFacilities[0].BusinessName ,1,false));
        table.addCell(addCell("Entity Name: "+FacilityDataModel.getInstance().tblFacilities[0].EntityName,1,false));
        table.addCell(addCell("Business Type: "+TypeTablesModel.getInstance().BusinessType.filter { s->s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())}[0].BusTypeName,1,false));
        table.addCell(addCell("Time Zone: "+ FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,1,false));
        table.addCell(addCell("Website URL: "+ FacilityDataModel.getInstance().tblFacilities[0].WebSite,1,false));
        table.addCell(addCell("Wi-Fi Available: "+ if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess) "Yes" else "No",1,false));
        table.addCell(addCell("Tax ID: "+ FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,1,false));
        table.addCell(addCell("Repair Order Count: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount,1,false));
        table.addCell(addCell("Annual Inspection Month: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),1,false));
        table.addCell(addCell("Inspection Cycle: "+ FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,1,false));
        table.addCell(addCell("Service Availability: "+ if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}[0].SrvAvaName else "Undetermined",1,false));
        table.addCell(addCell("Facility Type: "+ FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,1,false));
        table.addCell(addCell("ARD Number: "+ FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,1,false));
        table.addCell(addCell("ARD Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY(),1,false));
        table.addCell(addCell("Provider Type: "+ if (!FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId.equals("-1")) FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId else "",1,false));
        table.addCell(addCell("Shop Management System: ",1,false));
        table.addCell(addCell("Current Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),1,false));
        table.addCell(addCell("Initial Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),1,false));
        table.addCell(addCell("Billing Month: "+ FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),1,false));
        table.addCell(addCell("Billing Amount: $"+ "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),1,false));
        table.addCell(addCell("Insurance Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),2,false));
        table.addCell(addCell("",2,false))
        createPDFLogData += "...Done"
        return table
    }

    private fun drawAddressSection() : PdfPTable {
        createPDFLogData += " - drawAddressSection"
        val table = PdfPTable(17)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Type", 1,true))
        table.addCell(addCellWithBorder("Address1", 2,false))
        table.addCell(addCellWithBorder("Address2", 1,false))
        table.addCell(addCellWithBorder("City", 1,true))
        table.addCell(addCellWithBorder("State", 1,true))
        table.addCell(addCellWithBorder("ZIP", 1,true))
        table.addCell(addCellWithBorder("ZIP4", 1,true))
        table.addCell(addCellWithBorder("Country", 2,true))
        table.addCell(addCellWithBorder("Branch Name", 2,true))
        table.addCell(addCellWithBorder("Branch #", 1,true))
        table.addCell(addCellWithBorder("LATITUDE", 2,true))
        table.addCell(addCellWithBorder("LONGITUDE", 2,true))
        FacilityDataModel.getInstance().tblAddress.apply {
            (0 until size).forEach {
                if (!get(it).LocationTypeID.isNullOrEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationType.filter { s->s.LocTypeID.equals(get(it).LocationTypeID)}[0].LocTypeName, 1,true))
                    table.addCell(addCellWithBorder(get(it).FAC_Addr1,2,false))
                    table.addCell(addCellWithBorder(get(it).FAC_Addr2,1,false))
                    table.addCell(addCellWithBorder(get(it).CITY,1,true))
                    table.addCell(addCellWithBorder(get(it).ST,1,true))
                    table.addCell(addCellWithBorder(get(it).ZIP,1,true))
                    table.addCell(addCellWithBorder(get(it).ZIP4,1,true))
                    table.addCell(addCellWithBorder(get(it).County,2,true))
                    table.addCell(addCellWithBorder(get(it).BranchName,2,true))
                    table.addCell(addCellWithBorder(get(it).BranchNumber,1,true))
                    table.addCell(addCellWithBorder(get(it).LATITUDE,2,true))
                    table.addCell(addCellWithBorder(get(it).LONGITUDE,2,true))
                }
            }
        }
        return table
    }

    private fun drawAddressOverallSection() : PdfPTable {
        createPDFLogData += " - drawAddressOverallSection"
        val columnWidths = floatArrayOf(5f, 1f,5f, 1f,10f)
        val table = PdfPTable(columnWidths)
        table.setWidthPercentage(100f)
        table.addCell(addTableInCell(drawPaymentSection(),1,true));
        table.addCell(addCell("",1,true));
        table.addCell(addTableInCell(drawLanguageSection(),1,true));
        table.addCell(addCell("",1,true));
        table.addCell(addTableInCell(drawHoursSection(),1,true));
        return table
    }

    private fun drawPaymentSection() : PdfPTable {
        createPDFLogData += " - drawPaymentSection"
        val table = PdfPTable(3)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Payment Methods", 2,false))
        table.addCell(addCellWithBorder("Accepted", 1,true))
        TypeTablesModel.getInstance().PaymentMethodsType.apply {
            (0 until size).forEach {
                table.addCell(addCellWithBorder(get(it).PmtMethodName, 2,false))
                if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.equals(get(it).PmtMethodID)}.isNotEmpty()) {
                    Log.v("TICK "," TICK")
                    table.addCell(addTick(true,true))
                } else {
                    table.addCell(addCellWithBorder(" ", 1,true))
                }
            }
        }
        var noOfRowsToBeAdded = TypeTablesModel.getInstance().LanguageType.size - TypeTablesModel.getInstance().PaymentMethodsType.size
        if (FacilityDataModel.getInstance().tblPhone.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
        if (FacilityDataModel.getInstance().tblFacilityEmail.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

        for (i in 1.. noOfRowsToBeAdded){
            table.addCell(addCell(" ", 3,true))
        }
        return table
    }

    private fun drawPhoneSection() : PdfPTable {
        createPDFLogData += " - drawPhoneSection"
        val table = PdfPTable(2)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Phone Type", 1,false))
        table.addCell(addCellWithBorder("Phone", 1,true))
        FacilityDataModel.getInstance().tblPhone.apply {
            (0 until size).forEach {
                if (!get(it).PhoneID.equals("-1")) {
                    try {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneID.equals(get(it).PhoneTypeID) }[0].LocPhoneName, 1, false))
                        table.addCell(addCellWithBorder(get(it).PhoneNumber, 2, false))
                    } catch (e: java.lang.Exception){

                    }
                }
            }
        }
        return table
    }

    private fun drawEmailSection() : PdfPTable {
        createPDFLogData += " - drawEmailSection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Email Type", 1,false))
        table.addCell(addCellWithBorder("Email", 3,true))
        FacilityDataModel.getInstance().tblFacilityEmail.apply {
            (0 until size).forEach {
                if (!get(it).emailID.equals("-1") && !get(it).emailTypeId.equals("0")) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().EmailType.filter { s->s.EmailID.equals(get(it).emailTypeId)}[0].EmailName, 1, false))
                    table.addCell(addCellWithBorder(get(it).email, 3, false))
                }
            }
        }
        return table
    }

    private fun drawLanguageSection() : PdfPTable {
        createPDFLogData += " - drawLanguageSection"
        val table = PdfPTable(3)
        table.setWidthPercentage(100f)
        table.addCell(addCellWithBorder("Language(s)", 2,false))
        table.addCell(addCellWithBorder("Spoken", 1,true))
        TypeTablesModel.getInstance().LanguageType.apply {
            (0 until size).forEach {
                table.addCell(addCellWithBorder(get(it).LangTypeName, 2,false))
                if (FacilityDataModel.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.size>0) {
                    table.addCell(addTick(true,true))
                } else {
                    table.addCell(addCellWithBorder(" ", 1,true))
                }
            }
        }

        var noOfRowsToBeAdded = 0
        if (FacilityDataModel.getInstance().tblPhone.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
        if (FacilityDataModel.getInstance().tblFacilityEmail.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

        for (i in 1.. noOfRowsToBeAdded){
            table.addCell(addCell(" ", 3,true))
        }

        return table
    }

    private fun drawHoursSection() : PdfPTable {
        createPDFLogData += " - drawHoursSection"
        val table = PdfPTable(4)
        table.setWidthPercentage(100f)
        val hoursTable = PdfPTable(4)
        hoursTable.setWidthPercentage(100f)
        hoursTable.addCell(addCell("HOURS",1,false))
        hoursTable.addCell(addCell("Open Time",1,false))
        hoursTable.addCell(addCell("Close Time",1,false))
        hoursTable.addCell(addCell("Night Drop:",1,false))
        FacilityDataModel.getInstance().tblHours[0].apply {
            hoursTable.addCell(addCell("Sun",1,false))
            hoursTable.addCell(addCell(if (SunOpen.isNullOrEmpty()) "Closed" else SunOpen,1,false))
            hoursTable.addCell(addCell(if (SunClose.isNullOrEmpty()) "Closed" else SunClose,1,false))
            hoursTable.addCell(addCell(if (NightDrop) "True" else "False",1,false))
            hoursTable.addCell(addCell("Mon",1,false))
            hoursTable.addCell(addCell(if (MonOpen.isNullOrEmpty()) "Closed" else MonOpen,1,false))
            hoursTable.addCell(addCell(if (MonClose.isNullOrEmpty()) "Closed" else MonClose,1,false))
            hoursTable.addCell(addCell(" ",1,false))
            hoursTable.addCell(addCell("Tue",1,false))
            hoursTable.addCell(addCell(if (TueOpen.isNullOrEmpty()) "Closed" else TueOpen,1,false))
            hoursTable.addCell(addCell(if (TueClose.isNullOrEmpty()) "Closed" else TueClose,1,false))
            val cell = PdfPCell(Paragraph(("Nigh Drop Instructions:" + if (NightDropInstr.isNullOrEmpty()) "" else NightDropInstr), normalFont));
            cell.colspan=1
            cell.setBorder(Rectangle.NO_BORDER);
            cell.rowspan = 5
            hoursTable.addCell(cell)
            hoursTable.addCell(addCell("Wed",1,false))
            hoursTable.addCell(addCell(if (WedOpen.isNullOrEmpty()) "Closed" else WedOpen,1,false))
            hoursTable.addCell(addCell(if (WedClose.isNullOrEmpty()) "Closed" else WedClose,1,false))
            hoursTable.addCell(addCell("Thu",1,false))
            hoursTable.addCell(addCell(if (ThuOpen.isNullOrEmpty()) "Closed" else ThuOpen,1,false))
            hoursTable.addCell(addCell(if (ThuClose.isNullOrEmpty()) "Closed" else ThuClose,1,false))
            hoursTable.addCell(addCell("Fri",1,false))
            hoursTable.addCell(addCell(if (FriOpen.isNullOrEmpty()) "Closed" else FriOpen,1,false))
            hoursTable.addCell(addCell(if (FriClose.isNullOrEmpty()) "Closed" else FriClose,1,false))
            hoursTable.addCell(addCell("Sat",1,false))
            hoursTable.addCell(addCell(if (SatOpen.isNullOrEmpty()) "Closed" else SatOpen,1,false))
            hoursTable.addCell(addCell(if (SatClose.isNullOrEmpty()) "Closed" else SatClose,1,false))
        }
        table.addCell(addTableInCell(hoursTable,4,false))
        table.addCell(addCell(" ",4,true))
        table.addCell(addTableInCell(drawEmailSection(),4,false))
        table.addCell(addCell(" ",4,true))
        table.addCell(addTableInCell(drawPhoneSection(),2,false))
        table.addCell(addCell("",2,true))
        return table
    }

    private fun addEmptyLine(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
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

