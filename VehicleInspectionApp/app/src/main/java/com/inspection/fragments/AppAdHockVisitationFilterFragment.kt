package com.inspection.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.adhoc_visitation_facility_list_item.*
import kotlinx.android.synthetic.main.app_adhoc_visitation_filter_fragment.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient

import org.json.JSONObject
import org.json.XML
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FrgmentARRAnnualVisitationRecords.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FrgmentARRAnnualVisitationRecords.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppAdHockVisitationFilterFragment : android.support.v4.app.Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var fragment: android.support.v4.app.Fragment? = null
    private var mListener: OnFragmentInteractionListener? = null
    var facilityNames = ArrayList<String>()
    var facilitiesList = ArrayList<CsiFacility>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null
    var firstLoading = true
    var isVisitationPlanning = false
    var allClubCodes = ArrayList<String>()
    var requiredSpecialistName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.app_adhoc_visitation_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFieldsListeners()

        recordsProgressView.visibility = View.VISIBLE
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
                        loadClubCodes()
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading facilities")
            Log.v("Loading error", "" + it.message)
        }))

        firstLoading = false
    }

    private fun setFieldsListeners() {
        adHocFacilityNameButton.setOnClickListener(View.OnClickListener {
            recordsProgressView.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllFacilities + "",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            recordsProgressView.visibility = View.INVISIBLE
                            var facilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
                            var facilityNames = ArrayList<String>()
                            (0 until facilities.size).forEach {
                                facilityNames.add(facilities[it].facname)
                            }
                            facilityNames.sort()
                            facilityNames.add(0, "Any")
                            var searchDialog = SearchDialog(context, facilityNames)
                            searchDialog.show()
                            searchDialog.setOnDismissListener {
                                if (searchDialog.selectedString == "Any") {
                                    adHocFacilityNameButton.setText("")
                                } else {
                                    adHocFacilityNameButton.setText(searchDialog.selectedString)
                                }
                            }
                        })
                    }, Response.ErrorListener {
                context!!.toast("Connection Error")
                recordsProgressView.visibility = View.INVISIBLE
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        })


        adHocFacilitySpecialistButton.setOnClickListener(View.OnClickListener {
            var personnelNames = ArrayList<String>()

            if (clubCodeEditText.text.isNotEmpty() && false) {
                var specialistIds = StringBuilder()
                (0 until CsiSpecialistSingletonModel.getInstance().csiSpecialists.size).forEach {
                    specialistIds.append("" + CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].id + ", ")
                }

                var specialistIdsString = specialistIds.trim().removeSuffix(",").toString()

                Log.v("requesting........****", Constants.getSpecialistIdsForClubCode + "specialistIds=" + specialistIdsString + "&clubCode=" + clubCodeEditText.text.toString())

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistIdsForClubCode + "specialistIds=" + URLEncoder.encode(specialistIdsString, "UTF-8") + "&clubCode=" + clubCodeEditText.text.toString(),
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("responsingg******.....", response)

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading facilities")
                    Log.v("Loading error", "" + it.message)
                }))

//                if (CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].clubcode == clubCodeEditText.text.toString()) {
//
//                    personnelNames.add(CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].specialistname)
//                }

            } else {
                (0 until CsiSpecialistSingletonModel.getInstance().csiSpecialists.size).forEach {
                    personnelNames.add(CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].specialistname)
                }
            }

            Log.v("peronnel Names size......", "" + personnelNames.size)
            personnelNames.sort()
            personnelNames.add(0, "Any")
            var searchDialog = SearchDialog(context, personnelNames)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                if (searchDialog.selectedString == "Any") {
                    adHocFacilitySpecialistButton.setText("")
                } else {
                    adHocFacilitySpecialistButton.setText(searchDialog.selectedString)
                }
            }
        })

        clubCodeEditText.setOnClickListener{
            var searchDialog = SearchDialog(context, allClubCodes)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                clubCodeEditText.setText(searchDialog.selectedString)
            }
        }

        adHocSearchButton.setOnClickListener{
            adHocSearchButton.hideKeyboard()
            reloadFacilitiesList()
        }
    }

    private fun loadClubCodes() {
        Log.v("url*******", "" + Constants.getClubCodes)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getClubCodes,
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        var clubCodeModels = Gson().fromJson(response.toString(), Array<ClubCodeModel>::class.java)
                        allClubCodes.clear()
                        for (cc in clubCodeModels) {
                            allClubCodes.add(cc.clubcode)
                        }
                        recordsProgressView.visibility = View.GONE
                    }
                    reloadFacilitiesList()
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading club codes")
        }))
    }

    fun reloadFacilitiesList() {
        recordsProgressView.visibility = View.VISIBLE
        noRecordsFoundTextView.visibility = View.GONE
        var parametersString = StringBuilder()
        if (clubCodeEditText.text.trim().isNotEmpty()) {
            with(parametersString) {
                append("clubCode=" + clubCodeEditText.text.trim())
                append("&")
            }
        } else {
            with(parametersString) {
                append("clubCode=")
                append("&")
            }
        }

        with(parametersString) {
            append("facilityNumber=" + adHocFacilityIdVal.text.trim())
            append("&")
        }

        if (!adHocFacilitySpecialistButton.text.contains("Select") && adHocFacilitySpecialistButton.text.length > 1) {
            with(parametersString) {

                append("assignedSpecialist=" + CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s -> s.specialistname == adHocFacilitySpecialistButton.text.toString() }[0].id)
                append("&")

            }
        } else {
            with(parametersString) {
                append("assignedSpecialist=")
                append("&")
            }
        }

        if (!adHocFacilityNameButton.text.contains("Select") && adHocFacilityNameButton.text.length > 1) {
            with(parametersString) {
                append("dba=" + URLEncoder.encode(adHocFacilityNameButton.text.toString(), "UTF-8"))
                append("&")
            }
        } else {
            with(parametersString) {
                append("dba=")
                append("&")
            }
        }


        with(parametersString) {
            if (contractStatusActiveRadioButton.isChecked) {
                append("contractStatus=1&")
            } else {
                append("contractStatus=0&")
            }
        }


        Log.v("requesting....", Constants.getFacilitiesWithFilters + parametersString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilitiesWithFilters + parametersString,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        recordsProgressView.visibility = View.INVISIBLE

                        facilitiesList = Gson().fromJson(response, Array<CsiFacility>::class.java).toCollection(ArrayList())

                        if (facilitiesList.size == 0) {
                            noRecordsFoundTextView.visibility = View.VISIBLE
                        } else {
                            noRecordsFoundTextView.visibility = View.GONE
                        }

                        facilitiesListView.visibility = View.VISIBLE
                        var visitationPlanningAdapter = AdhocAdapter(context, facilitiesList)
                        facilitiesListView.adapter = visitationPlanningAdapter
                    })
                }, Response.ErrorListener {
            recordsProgressView.visibility = View.INVISIBLE
            context!!.toast("Error while loading facilities")
            Log.v("error while loading", "error while loading visitation records")
        }))
    }

//    fun loadSpecialistName() {
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//
//                        var specialistName = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
//                        if (specialistName != null && specialistName.size > 0) {
//                            requiredSpecialistName = specialistName[0].specialistname
//                            facilityDbaButton.setText(requiredSpecialistName)
//                        }
//                    })
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading facilities")
//            Log.v("Loading error", "" + it.message)
//        }))
//
//    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
    }

    inner class AdhocAdapter : BaseAdapter {

        private var facilitiesArrayList = ArrayList<CsiFacility>()
        private var context: Context? = null

        constructor(context: Context?, facilitiesArrayList: ArrayList<CsiFacility>) : super() {
            this.facilitiesArrayList = facilitiesArrayList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: AdHocVisitationViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.adhoc_visitation_facility_list_item, parent, false)
                vh = AdHocVisitationViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as AdHocVisitationViewHolder
            }

            vh.facilityNameValueTextView?.text = facilitiesArrayList[position].facname
            vh.facilityNumberValueTextView?.text = facilitiesArrayList[position].facnum
            vh.adHocClubCodeValueTextView?.text = facilitiesArrayList[position].clubcode
            vh.loadFacilityButton!!.setOnClickListener {
                Log.v("required facility number", facilitiesArrayList[position].facnum + " " + facilitiesArrayList[position].clubcode)
                getFullFacilityDataFromAAA(facilitiesArrayList[position].facnum.toInt(), facilitiesArrayList[position].clubcode)
            }


            return view
        }


        override fun getItem(position: Int): Any {
            // return item at 'position'
            return facilitiesArrayList[position]
        }

        override fun getItemId(position: Int): Long {
            // return item Id by Long datatype
            return position.toLong()
        }

        override fun getCount(): Int {
            // return quantity of the list
            return facilitiesArrayList.size
        }
    }

    fun getFullFacilityDataFromAAA(facilityNumber: Int, clubCode: String) {

        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request = okhttp3.Request.Builder().url(Constants.getTypeTables).build()
        var request2 = okhttp3.Request.Builder().url(String.format(Constants.getFacilityData, facilityNumber, clubCode)).build()



        recordsProgressView.visibility = View.VISIBLE
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.v("&&&&&*(*", "failed with exception : " + e!!.message)
                activity!!.runOnUiThread(Runnable {
                    context!!.toast("Connection Error. Please check internet connection")
                })
            }

            override fun onResponse(call: Call?, response: okhttp3.Response?) {

                var responseString = response!!.body()!!.string()
                Log.v("getTypeTables retrieved", "GetTupeTables retrieved")
                var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("&lt;responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                var jsonObj = obj.getJSONObject("responseXml")
                TypeTablesModel.setInstance(Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java))

                Log.v("requesting=========>", request2.url().toString());
                Log.v("time out valueeeeee", "" + client.connectTimeoutMillis() / 1000)
                Log.v("read time out valuee", "" + client.readTimeoutMillis() / 1000)

                client.newCall(request2).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        activity!!.runOnUiThread(Runnable {
                            Log.v("******eerrrrrror", "" + e!!.message)
                            context!!.toast("Origin ERROR Connection Error. Please check internet connection")
                        })
                    }

                    override fun onResponse(call: Call?, response: okhttp3.Response?) {
                        Log.v("GetFacilityData replied", "GetFacilityData replied")
                        var responseString = response!!.body()!!.string()
                        activity!!.runOnUiThread(Runnable {
                            Log.v("POPOOriginal", responseString)

                            recordsProgressView.visibility = View.GONE
                            if (!responseString.contains("FacID not found")) {
                                var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("&lt;responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
                                        .replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                                var jsonObj = obj.getJSONObject("responseXml")
                                parseFacilityDataJsonToObject(jsonObj)

                                if (FacilityDataModel.getInstance().tblVisitationTracking.size == 0) {
                                    FacilityDataModel.getInstance().tblVisitationTracking.add(FacilityDataModel.TblVisitationTracking())
                                }
                                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = FacilityDataModel.VisitationTypes.AdHoc


                                var intent = Intent(context, com.inspection.FormsActivity::class.java)
//                                                var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
                                startActivity(intent)
                            } else {
                                context!!.toast("Facility data not found")
                            }
                        })
                    }

                })


            }

        })
    }


    fun parseFacilityDataJsonToObject(jsonObj: JSONObject) {
        FacilityDataModel.getInstance().clear()
        if (jsonObj.has("tblFacilities")) {
            if (jsonObj.get("tblFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilities = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilities.add(Gson().fromJson<FacilityDataModel.TblFacilities>(jsonObj.get("tblFacilities").toString(), FacilityDataModel.TblFacilities::class.java))
            }
        }

        if (jsonObj.has("tblBusinessType")) {
            if (jsonObj.get("tblBusinessType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBusinessType = Gson().fromJson<ArrayList<FacilityDataModel.TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblBusinessType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBusinessType.add(Gson().fromJson<FacilityDataModel.TblBusinessType>(jsonObj.get("tblBusinessType").toString(), FacilityDataModel.TblBusinessType::class.java))
            }
        }

        if (jsonObj.has("tblContractType")) {
            if (jsonObj.get("tblContractType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblContractType = Gson().fromJson<ArrayList<FacilityDataModel.TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblContractType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblContractType.add(Gson().fromJson<FacilityDataModel.TblContractType>(jsonObj.get("tblContractType").toString(), FacilityDataModel.TblContractType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServiceProvider")) {
            if (jsonObj.get("tblFacilityServiceProvider").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityServiceProvider>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<FacilityDataModel.TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), FacilityDataModel.TblFacilityServiceProvider::class.java))
            }
        }

        if (jsonObj.has("tblTerminationCodeType")) {
            if (jsonObj.get("tblTerminationCodeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<FacilityDataModel.TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblTerminationCodeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTerminationCodeType.add(Gson().fromJson<FacilityDataModel.TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), FacilityDataModel.TblTerminationCodeType::class.java))
            }
        }

        if (jsonObj.has("tblOfficeType")) {
            if (jsonObj.get("tblOfficeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblOfficeType = Gson().fromJson<ArrayList<FacilityDataModel.TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblOfficeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblOfficeType.add(Gson().fromJson<FacilityDataModel.TblOfficeType>(jsonObj.get("tblOfficeType").toString(), FacilityDataModel.TblOfficeType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityManagers")) {
            if (jsonObj.get("tblFacilityManagers").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityManagers>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityManagers.add(Gson().fromJson<FacilityDataModel.TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), FacilityDataModel.TblFacilityManagers::class.java))
            }
        }

        if (jsonObj.has("tblTimezoneType")) {
            if (jsonObj.get("tblTimezoneType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<FacilityDataModel.TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblTimezoneType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTimezoneType.add(Gson().fromJson<FacilityDataModel.TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), FacilityDataModel.TblTimezoneType::class.java))
            }
        }


        if (jsonObj.has("tblVisitationTracking")) {
            if (jsonObj.get("tblVisitationTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<FacilityDataModel.TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblVisitationTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVisitationTracking.add(Gson().fromJson<FacilityDataModel.TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), FacilityDataModel.TblVisitationTracking::class.java))
            }
        }

        if (jsonObj.has("tblFacilityType")) {
            if (jsonObj.get("tblFacilityType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityType = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityType.add(Gson().fromJson<FacilityDataModel.TblFacilityType>(jsonObj.get("tblFacilityType").toString(), FacilityDataModel.TblFacilityType::class.java))
            }
        }

        if (jsonObj.has("tblSurveySoftwares")) {
            if (jsonObj.get("tblSurveySoftwares").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<FacilityDataModel.TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblSurveySoftwares>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblSurveySoftwares.add(Gson().fromJson<FacilityDataModel.TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), FacilityDataModel.TblSurveySoftwares::class.java))
            }
        }


        if (jsonObj.has("tblPaymentMethods")) {
            if (jsonObj.get("tblPaymentMethods").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<FacilityDataModel.TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPaymentMethods>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPaymentMethods.add(Gson().fromJson<FacilityDataModel.TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), FacilityDataModel.TblPaymentMethods::class.java))
            }
        }

        if (jsonObj.has("tblAddress")) {
            if (jsonObj.get("tblAddress").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAddress = Gson().fromJson<ArrayList<FacilityDataModel.TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAddress>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<FacilityDataModel.TblAddress>(jsonObj.get("tblAddress").toString(), FacilityDataModel.TblAddress::class.java))
            }
        }

        if (jsonObj.has("tblPhone")) {
            if (jsonObj.get("tblPhone").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPhone = Gson().fromJson<ArrayList<FacilityDataModel.TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPhone>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPhone.add(Gson().fromJson<FacilityDataModel.TblPhone>(jsonObj.get("tblPhone").toString(), FacilityDataModel.TblPhone::class.java))
            }
        }

        if (jsonObj.has("tblFacilityEmail")) {
            if (jsonObj.get("tblFacilityEmail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityEmail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityEmail.add(Gson().fromJson<FacilityDataModel.TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), FacilityDataModel.TblFacilityEmail::class.java))
            }
        }

        if (jsonObj.has("tblHours")) {
            if (jsonObj.get("tblHours").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblHours = Gson().fromJson<ArrayList<FacilityDataModel.TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblHours>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblHours.add(Gson().fromJson<FacilityDataModel.TblHours>(jsonObj.get("tblHours").toString(), FacilityDataModel.TblHours::class.java))
            }
        }

        if (jsonObj.has("tblFacilityClosure")) {
            if (jsonObj.get("tblFacilityClosure").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityClosure>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityClosure.add(Gson().fromJson<FacilityDataModel.TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), FacilityDataModel.TblFacilityClosure::class.java))
            }
        }

        if (jsonObj.has("tblLanguage")) {
            if (jsonObj.get("tblLanguage").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblLanguage = Gson().fromJson<ArrayList<FacilityDataModel.TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblLanguage>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblLanguage.add(Gson().fromJson<FacilityDataModel.TblLanguage>(jsonObj.get("tblLanguage").toString(), FacilityDataModel.TblLanguage::class.java))
            }
        }

        if (jsonObj.has("tblPersonnel")) {
            if (jsonObj.get("tblPersonnel").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnel = Gson().fromJson<ArrayList<FacilityDataModel.TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPersonnel>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnel.add(Gson().fromJson<FacilityDataModel.TblPersonnel>(jsonObj.get("tblPersonnel").toString(), FacilityDataModel.TblPersonnel::class.java))
            }
        }

        if (jsonObj.has("tblAmendmentOrderTracking")) {
            if (jsonObj.get("tblAmendmentOrderTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<FacilityDataModel.TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAmendmentOrderTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<FacilityDataModel.TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), FacilityDataModel.TblAmendmentOrderTracking::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalAdmin")) {
            if (jsonObj.get("tblAARPortalAdmin").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<FacilityDataModel.TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAARPortalAdmin>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAARPortalAdmin.add(Gson().fromJson<FacilityDataModel.TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), FacilityDataModel.TblAARPortalAdmin::class.java))
            }
        }

        if (jsonObj.has("tblScopeofService")) {
            if (jsonObj.get("tblScopeofService").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblScopeofService = Gson().fromJson<ArrayList<FacilityDataModel.TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblScopeofService>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblScopeofService.add(Gson().fromJson<FacilityDataModel.TblScopeofService>(jsonObj.get("tblScopeofService").toString(), FacilityDataModel.TblScopeofService::class.java))
            }
        }

        if (jsonObj.has("tblPrograms")) {
            if (jsonObj.get("tblPrograms").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPrograms = Gson().fromJson<ArrayList<FacilityDataModel.TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPrograms>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPrograms.add(Gson().fromJson<FacilityDataModel.TblPrograms>(jsonObj.get("tblPrograms").toString(), FacilityDataModel.TblPrograms::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServices")) {
            if (jsonObj.get("tblFacilityServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServices.add(Gson().fromJson<FacilityDataModel.TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), FacilityDataModel.TblFacilityServices::class.java))
            }
        }

        if (jsonObj.has("tblAffiliations")) {
            if (jsonObj.get("tblAffiliations").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliations = Gson().fromJson<ArrayList<FacilityDataModel.TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAffiliations>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliations.add(Gson().fromJson<FacilityDataModel.TblAffiliations>(jsonObj.get("tblAffiliations").toString(), FacilityDataModel.TblAffiliations::class.java))
            }
        }

        if (jsonObj.has("tblDeficiency")) {
            if (jsonObj.get("tblDeficiency").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblDeficiency = Gson().fromJson<ArrayList<FacilityDataModel.TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblDeficiency>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblDeficiency.add(Gson().fromJson<FacilityDataModel.TblDeficiency>(jsonObj.get("tblDeficiency").toString(), FacilityDataModel.TblDeficiency::class.java))
            }
        }

        if (jsonObj.has("tblComplaintFiles")) {
            if (jsonObj.get("tblComplaintFiles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<FacilityDataModel.TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblComplaintFiles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComplaintFiles.add(Gson().fromJson<FacilityDataModel.TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), FacilityDataModel.TblComplaintFiles::class.java))
            }
        }

        if (jsonObj.has("NumberofComplaints")) {
            if (jsonObj.get("NumberofComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<FacilityDataModel.numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModel.numberofComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofComplaints.add(Gson().fromJson<FacilityDataModel.numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), FacilityDataModel.numberofComplaints::class.java))
            }
        }

        if (jsonObj.has("NumberofJustifiedComplaints")) {
            if (jsonObj.get("NumberofJustifiedComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<FacilityDataModel.numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModel.numberofJustifiedComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<FacilityDataModel.numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), FacilityDataModel.numberofJustifiedComplaints::class.java))
            }
        }

        if (jsonObj.has("JustifiedComplaintRatio")) {
            if (jsonObj.get("JustifiedComplaintRatio").toString().startsWith("[")) {
                FacilityDataModel.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<FacilityDataModel.justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<FacilityDataModel.justifiedComplaintRatio>>() {}.type)
            } else {
                FacilityDataModel.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<FacilityDataModel.justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), FacilityDataModel.justifiedComplaintRatio::class.java))
            }
        }

        if (jsonObj.has("tblFacilityPhotos")) {
            if (jsonObj.get("tblFacilityPhotos").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityPhotos>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityPhotos.add(Gson().fromJson<FacilityDataModel.TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), FacilityDataModel.TblFacilityPhotos::class.java))
            }
        }

    }

    private class AdHocVisitationViewHolder(view: View?) {
        var facilityNameValueTextView: TextView? = null
        var facilityNumberValueTextView: TextView? = null
        var adHocClubCodeValueTextView: TextView? = null
        var loadFacilityButton: Button? = null

        init {
            this.facilityNameValueTextView = view?.findViewById(R.id.facilityNameValueTextView) as TextView
            this.facilityNumberValueTextView = view?.findViewById(R.id.facilityNumberValueTextView) as TextView
            this.adHocClubCodeValueTextView = view?.findViewById(R.id.adHocClubCodeValueTextView)
            this.loadFacilityButton = view?.findViewById(R.id.loadFacilityButton) as Button
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
        fun newInstance(param1: String, param2: String): AppAdHockVisitationFilterFragment {
            val fragment = AppAdHockVisitationFilterFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}


