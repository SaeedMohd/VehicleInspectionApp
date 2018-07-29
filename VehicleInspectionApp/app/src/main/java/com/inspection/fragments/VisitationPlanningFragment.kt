package com.inspection.fragments


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
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
import com.google.gson.reflect.TypeToken
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.model.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
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
class VisitationPlanningFragment : android.support.v4.app.Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var fragment: android.support.v4.app.Fragment? = null
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
    var specialistArrayModel = ArrayList<CsiSpecialist>()

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
        return inflater.inflate(R.layout.visitation_planning_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitationfacilityListView.visibility = View.GONE


        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
                Response.Listener { response ->
                    Log.v("****response", response)
                    activity!!.runOnUiThread(Runnable {
                        CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())

                        Log.v("loading user"," request: "+Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail)
                        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {

                                        var specialistName = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
                                        if (specialistName != null && specialistName.size > 0) {
                                            requiredSpecialistName = specialistName[0].specialistname

                                            for (sn in specialistName){
                                                specialistClubCodes.add(sn.clubcode)
                                            }

                                                visitationSpecialistName.setText(requiredSpecialistName)

                                        }

                                        loadSpecialistName()
                                    })


                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading facilities")
                            Log.v("Loading error", "" + it.message)
                        }))

                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading specialists")
            Log.v("Loading error", "" + it.message)
        }))


    }

    val spinnersOnItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            reloadVisitationsList()
        }

    }

    fun prepareInitialStateForFilters(){
        clubCodeEditText.setText(specialistArrayModel.sortedWith(compareBy { it.clubcode })[0].clubcode)


    }


    fun firstLoadingCompleted(){

        prepareInitialStateForFilters()

        var visitationYearFilterSpinnerEntries = mutableListOf<String>()
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)
//        visitationYearFilterSpinnerEntries.add    ("Any")
        (currentYear - 30..currentYear).forEach {
            visitationYearFilterSpinnerEntries.add("" + it)
        }
        visitationYearFilterSpinnerEntries.sortDescending()
        visitationYearFilterSpinnerEntries.add(0, "Any")
        visitationYearFilterSpinner.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, visitationYearFilterSpinnerEntries)
        visitationYearFilterSpinner.onItemSelectedListener = spinnersOnItemSelectListener

        visitationMonthsSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1)
        visitationMonthsSpinner.onItemSelectedListener = spinnersOnItemSelectListener

        visitationYearFilterSpinner.setSelection(visitationYearFilterSpinnerEntries.indexOf("" + Calendar.getInstance().get(Calendar.YEAR)))
        visitationYearFilterSpinner.onItemSelectedListener = spinnersOnItemSelectListener

        progressBarRecords.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        clubCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reloadVisitationsList()
            }
        })

        visitationfacilityIdVal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reloadVisitationsList()

            }

        })

        visitationSpecialistName.setOnClickListener(View.OnClickListener {
            var personnelNames = ArrayList<String>()
            (0 until CsiSpecialistSingletonModel.getInstance().csiSpecialists.size).forEach {
                personnelNames.add(CsiSpecialistSingletonModel.getInstance().csiSpecialists[it].specialistname)
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
                reloadVisitationsList()
            }
        })

        clubCodeEditText.setOnClickListener(View.OnClickListener {
            var searchDialog = SearchDialog(context, allClubCodes)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                clubCodeEditText.setText(searchDialog.selectedString)
                reloadVisitationsList()
            }
        })

        facilityNameButton.setOnClickListener(View.OnClickListener {
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
                                    facilityNameButton.setText("")
                                } else {
                                    facilityNameButton.setText(searchDialog.selectedString)
                                }
                                reloadVisitationsList()
                            }
                        })
                    }, Response.ErrorListener {

                context!!.toast("Connection Error")
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
                it.printStackTrace()
            }))
        })

        annualVisitationCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            reloadVisitationsList()
        }

        quarterlyOrOtherVisistationsCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            reloadVisitationsList()
        }

        adHocVisitationsCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            reloadVisitationsList()
        }


        deficienciesCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            reloadVisitationsList()
        }



        pendingCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            reloadVisitationsList()
        }



        completedCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->


            reloadVisitationsList()


        }


        reloadVisitationsList()
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
                    append("clubCode=004")
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
                        append("specialist=" + CsiSpecialistSingletonModel.getInstance().csiSpecialists.filter { s -> s.specialistname == visitationSpecialistName.text.toString() }[0].accspecid)
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
                    append(("dba=" + URLEncoder.encode(facilityNameButton.text.toString(), "UTF-8")))
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

            if (visitationMonthsSpinner.selectedItem != "Any") {
                with(parametersString) {
                    append("inspectionMonth=" + visitationMonthsSpinner.selectedItemPosition)
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


            if (pendingCheckBox.isChecked) {
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
            var request = okhttp3.Request.Builder().url(Constants.getVisitations + parametersString).build()

            Log.v("******get visitation", Constants.getVisitations+parametersString)

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.v("failure http", "failed with exception : " + e!!.message)
                    activity!!.runOnUiThread(Runnable {
                        activity!!.toast("Error while loading large data")

                        recordsProgressView.visibility = View.INVISIBLE


                    })
                }

                override fun onResponse(call: Call?, response: okhttp3.Response?) {

                    var responseString = response!!.body()!!.string()
                    //  activity!!.toast("success!!!")
                    //     recordsProgressView.visibility = View.INVISIBLE
                    Log.v("*****u got a  response", Constants.getVisitations+parametersString)


                    var obj = XML.toJSONObject(responseString.substring(responseString.indexOf("&lt;responseXml"), responseString.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                    var jsonObj = obj.getJSONObject("responseXml")


                    visitationsModel = parseVisitationsData(jsonObj)


                    activity!!.runOnUiThread {
                        recordsProgressView.visibility = View.GONE
                        visitationfacilityListView.visibility = View.VISIBLE
                        var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                        visitationfacilityListView.adapter = visitationPlanningAdapter
                    }
                }
            })


        } else {
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getVisitations + parametersString,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {


                            var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                            var jsonObj = obj.getJSONObject("responseXml")


                            var visitationsModel = parseVisitationsData(jsonObj)
                            recordsProgressView.visibility = View.GONE
                            visitationfacilityListView.visibility = View.VISIBLE
                            var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                            visitationfacilityListView.adapter = visitationPlanningAdapter
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading visitation records")
            }))
        }
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

        return visitationsModel
    }

    private fun loadClubCodes() {
        Log.v("url*******", ""+ Constants.getClubCodes)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getClubCodes,
                Response.Listener { response ->
                    var clubCodeModels = Gson().fromJson(response.toString(), Array<ClubCodeModel>::class.java)
                    allClubCodes.clear()
                    for (cc in clubCodeModels) {
                        allClubCodes.add(cc.clubcode)
                    }

                    firstLoadingCompleted()
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading club codes")
        }))
    }

    private fun loadSpecialistName() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getSpecialistNameFromEmail + ApplicationPrefs.getInstance(context).loggedInUserEmail,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {

                        specialistArrayModel = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
                        if (specialistArrayModel != null && specialistArrayModel.size > 0) {
                            requiredSpecialistName = specialistArrayModel[0].specialistname
                            visitationSpecialistName.setText(requiredSpecialistName)
                        }
                        loadClubCodes()
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading facilities")
            Log.v("Loading error", "" + it.message)
        }))

    }

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



            if (position < visitationPlanningModelList.pendingVisitationsArray.size && visitationPlanningModelList.pendingVisitationsArray.size > 0) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].EntityName
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].AutomotiveRepairExpDate.apiToAppFormat()
                vh.visitationTypeValueTextView.text = "Pending"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt(), visitationPlanningModelList.pendingVisitationsArray[position].ClubCode)
                })
            } else if (position >= visitationPlanningModelList.pendingVisitationsArray.size && position < visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].EntityName
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].AutomotiveRepairExpDate.apiToAppFormat()
                vh.visitationTypeValueTextView.text = "Completed"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].FACNo.toInt(), visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].ClubCode)
                })
            } else if (position >= visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].EntityName
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].AutomotiveRepairExpDate.apiToAppFormat()
                vh.visitationTypeValueTextView.text = "Deficiency"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FACNo.toInt(), visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].ClubCode)
                })
            }
            return view
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


    fun getFullFacilityDataFromAAA(facilityNumber: Int, clubCode: String) {

        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
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
                Log.v("time out valueeeeee", ""+client.connectTimeoutMillis()/1000)
                Log.v("read time out valuee", ""+client.readTimeoutMillis()/1000)

                client.newCall(request2).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        activity!!.runOnUiThread(Runnable {
                            Log.v("******eerrrrrror", ""+e!!.message)
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
        FacilityDataModelOrg.getInstance().clear()
        if (jsonObj.has("tblFacilities")) {
            if (jsonObj.get("tblFacilities").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilities = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilities>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilities = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilities>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilities.add(Gson().fromJson<FacilityDataModel.TblFacilities>(jsonObj.get("tblFacilities").toString(), FacilityDataModel.TblFacilities::class.java))
                FacilityDataModelOrg.getInstance().tblFacilities.add(Gson().fromJson<FacilityDataModelOrg.TblFacilities>(jsonObj.get("tblFacilities").toString(), FacilityDataModelOrg.TblFacilities::class.java))
            }
        }



        if (jsonObj.has("tblBusinessType")) {
            if (jsonObj.get("tblBusinessType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblBusinessType = Gson().fromJson<ArrayList<FacilityDataModel.TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblBusinessType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblBusinessType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblBusinessType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblBusinessType.add(Gson().fromJson<FacilityDataModel.TblBusinessType>(jsonObj.get("tblBusinessType").toString(), FacilityDataModel.TblBusinessType::class.java))
                FacilityDataModelOrg.getInstance().tblBusinessType.add(Gson().fromJson<FacilityDataModelOrg.TblBusinessType>(jsonObj.get("tblBusinessType").toString(), FacilityDataModelOrg.TblBusinessType::class.java))
            }
        }


        if (jsonObj.has("tblContractType")) {
            if (jsonObj.get("tblContractType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblContractType = Gson().fromJson<ArrayList<FacilityDataModel.TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblContractType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblContractType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblContractType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblContractType.add(Gson().fromJson<FacilityDataModel.TblContractType>(jsonObj.get("tblContractType").toString(), FacilityDataModel.TblContractType::class.java))
                FacilityDataModelOrg.getInstance().tblContractType.add(Gson().fromJson<FacilityDataModelOrg.TblContractType>(jsonObj.get("tblContractType").toString(), FacilityDataModelOrg.TblContractType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServiceProvider")) {
            if (jsonObj.get("tblFacilityServiceProvider").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityServiceProvider>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityServiceProvider>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<FacilityDataModel.TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), FacilityDataModel.TblFacilityServiceProvider::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), FacilityDataModelOrg.TblFacilityServiceProvider::class.java))
            }
        }

        if (jsonObj.has("tblTerminationCodeType")) {
            if (jsonObj.get("tblTerminationCodeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<FacilityDataModel.TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblTerminationCodeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblTerminationCodeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTerminationCodeType.add(Gson().fromJson<FacilityDataModel.TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), FacilityDataModel.TblTerminationCodeType::class.java))
                FacilityDataModelOrg.getInstance().tblTerminationCodeType.add(Gson().fromJson<FacilityDataModelOrg.TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), FacilityDataModelOrg.TblTerminationCodeType::class.java))
            }
        }

        if (jsonObj.has("tblOfficeType")) {
            if (jsonObj.get("tblOfficeType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblOfficeType = Gson().fromJson<ArrayList<FacilityDataModel.TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblOfficeType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblOfficeType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblOfficeType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblOfficeType.add(Gson().fromJson<FacilityDataModel.TblOfficeType>(jsonObj.get("tblOfficeType").toString(), FacilityDataModel.TblOfficeType::class.java))
                FacilityDataModelOrg.getInstance().tblOfficeType.add(Gson().fromJson<FacilityDataModelOrg.TblOfficeType>(jsonObj.get("tblOfficeType").toString(), FacilityDataModelOrg.TblOfficeType::class.java))
            }
        }

        if (jsonObj.has("tblFacilityManagers")) {
            if (jsonObj.get("tblFacilityManagers").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityManagers>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityManagers>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityManagers.add(Gson().fromJson<FacilityDataModel.TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), FacilityDataModel.TblFacilityManagers::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityManagers.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), FacilityDataModelOrg.TblFacilityManagers::class.java))
            }
        }

        if (jsonObj.has("tblTimezoneType")) {
            if (jsonObj.get("tblTimezoneType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<FacilityDataModel.TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblTimezoneType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblTimezoneType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblTimezoneType.add(Gson().fromJson<FacilityDataModel.TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), FacilityDataModel.TblTimezoneType::class.java))
                FacilityDataModelOrg.getInstance().tblTimezoneType.add(Gson().fromJson<FacilityDataModelOrg.TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), FacilityDataModelOrg.TblTimezoneType::class.java))
            }
        }


        if (jsonObj.has("tblVisitationTracking")) {
            if (jsonObj.get("tblVisitationTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<FacilityDataModel.TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblVisitationTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblVisitationTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblVisitationTracking.add(Gson().fromJson<FacilityDataModel.TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), FacilityDataModel.TblVisitationTracking::class.java))
                FacilityDataModelOrg.getInstance().tblVisitationTracking.add(Gson().fromJson<FacilityDataModelOrg.TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), FacilityDataModelOrg.TblVisitationTracking::class.java))
            }
        }

        if (jsonObj.has("tblFacilityType")) {
            if (jsonObj.get("tblFacilityType").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityType = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityType>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityType = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityType>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityType.add(Gson().fromJson<FacilityDataModel.TblFacilityType>(jsonObj.get("tblFacilityType").toString(), FacilityDataModel.TblFacilityType::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityType.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityType>(jsonObj.get("tblFacilityType").toString(), FacilityDataModelOrg.TblFacilityType::class.java))
            }
        }

        if (jsonObj.has("tblSurveySoftwares")) {
            if (jsonObj.get("tblSurveySoftwares").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<FacilityDataModel.TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblSurveySoftwares>>() {}.type)
                FacilityDataModelOrg.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblSurveySoftwares>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblSurveySoftwares.add(Gson().fromJson<FacilityDataModel.TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), FacilityDataModel.TblSurveySoftwares::class.java))
                FacilityDataModelOrg.getInstance().tblSurveySoftwares.add(Gson().fromJson<FacilityDataModelOrg.TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), FacilityDataModelOrg.TblSurveySoftwares::class.java))
            }
        }


        if (jsonObj.has("tblPaymentMethods")) {
            if (jsonObj.get("tblPaymentMethods").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<FacilityDataModel.TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPaymentMethods>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblPaymentMethods>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPaymentMethods.add(Gson().fromJson<FacilityDataModel.TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), FacilityDataModel.TblPaymentMethods::class.java))
                FacilityDataModelOrg.getInstance().tblPaymentMethods.add(Gson().fromJson<FacilityDataModelOrg.TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), FacilityDataModelOrg.TblPaymentMethods::class.java))
            }
        }

        if (jsonObj.has("tblAddress")) {
            if (jsonObj.get("tblAddress").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAddress = Gson().fromJson<ArrayList<FacilityDataModel.TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAddress>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAddress = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblAddress>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<FacilityDataModel.TblAddress>(jsonObj.get("tblAddress").toString(), FacilityDataModel.TblAddress::class.java))
                FacilityDataModelOrg.getInstance().tblAddress.add(Gson().fromJson<FacilityDataModelOrg.TblAddress>(jsonObj.get("tblAddress").toString(), FacilityDataModelOrg.TblAddress::class.java))
            }
        }

        if (jsonObj.has("tblPhone")) {
            if (jsonObj.get("tblPhone").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPhone = Gson().fromJson<ArrayList<FacilityDataModel.TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPhone>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPhone = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblPhone>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPhone.add(Gson().fromJson<FacilityDataModel.TblPhone>(jsonObj.get("tblPhone").toString(), FacilityDataModel.TblPhone::class.java))
                FacilityDataModelOrg.getInstance().tblPhone.add(Gson().fromJson<FacilityDataModelOrg.TblPhone>(jsonObj.get("tblPhone").toString(), FacilityDataModelOrg.TblPhone::class.java))
            }
        }

        if (jsonObj.has("tblFacilityEmail")) {
            if (jsonObj.get("tblFacilityEmail").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityEmail>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityEmail>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityEmail.add(Gson().fromJson<FacilityDataModel.TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), FacilityDataModel.TblFacilityEmail::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityEmail.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), FacilityDataModelOrg.TblFacilityEmail::class.java))
            }
        }

        if (jsonObj.has("tblHours")) {
            if (jsonObj.get("tblHours").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblHours = Gson().fromJson<ArrayList<FacilityDataModel.TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblHours>>() {}.type)
                FacilityDataModelOrg.getInstance().tblHours = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblHours>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblHours.add(Gson().fromJson<FacilityDataModel.TblHours>(jsonObj.get("tblHours").toString(), FacilityDataModel.TblHours::class.java))
                FacilityDataModelOrg.getInstance().tblHours.add(Gson().fromJson<FacilityDataModelOrg.TblHours>(jsonObj.get("tblHours").toString(), FacilityDataModelOrg.TblHours::class.java))
            }
        }

        if (jsonObj.has("tblFacilityClosure")) {
            if (jsonObj.get("tblFacilityClosure").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityClosure>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityClosure>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityClosure.add(Gson().fromJson<FacilityDataModel.TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), FacilityDataModel.TblFacilityClosure::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityClosure.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), FacilityDataModelOrg.TblFacilityClosure::class.java))
            }
        }

        if (jsonObj.has("tblLanguage")) {
            if (jsonObj.get("tblLanguage").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblLanguage = Gson().fromJson<ArrayList<FacilityDataModel.TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblLanguage>>() {}.type)
                FacilityDataModelOrg.getInstance().tblLanguage = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblLanguage>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblLanguage.add(Gson().fromJson<FacilityDataModel.TblLanguage>(jsonObj.get("tblLanguage").toString(), FacilityDataModel.TblLanguage::class.java))
                FacilityDataModelOrg.getInstance().tblLanguage.add(Gson().fromJson<FacilityDataModelOrg.TblLanguage>(jsonObj.get("tblLanguage").toString(), FacilityDataModelOrg.TblLanguage::class.java))
            }
        }

        if (jsonObj.has("tblPersonnel")) {
            if (jsonObj.get("tblPersonnel").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPersonnel = Gson().fromJson<ArrayList<FacilityDataModel.TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPersonnel>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPersonnel = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblPersonnel>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPersonnel.add(Gson().fromJson<FacilityDataModel.TblPersonnel>(jsonObj.get("tblPersonnel").toString(), FacilityDataModel.TblPersonnel::class.java))
                FacilityDataModelOrg.getInstance().tblPersonnel.add(Gson().fromJson<FacilityDataModelOrg.TblPersonnel>(jsonObj.get("tblPersonnel").toString(), FacilityDataModelOrg.TblPersonnel::class.java))
            }
        }

        if (jsonObj.has("tblAmendmentOrderTracking")) {
            if (jsonObj.get("tblAmendmentOrderTracking").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<FacilityDataModel.TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAmendmentOrderTracking>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblAmendmentOrderTracking>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<FacilityDataModel.TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), FacilityDataModel.TblAmendmentOrderTracking::class.java))
                FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<FacilityDataModelOrg.TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), FacilityDataModelOrg.TblAmendmentOrderTracking::class.java))
            }
        }

        if (jsonObj.has("tblAARPortalAdmin")) {
            if (jsonObj.get("tblAARPortalAdmin").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<FacilityDataModel.TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAARPortalAdmin>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblAARPortalAdmin>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAARPortalAdmin.add(Gson().fromJson<FacilityDataModel.TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), FacilityDataModel.TblAARPortalAdmin::class.java))
                FacilityDataModelOrg.getInstance().tblAARPortalAdmin.add(Gson().fromJson<FacilityDataModelOrg.TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), FacilityDataModelOrg.TblAARPortalAdmin::class.java))
            }
        }

        if (jsonObj.has("tblScopeofService")) {
            if (jsonObj.get("tblScopeofService").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblScopeofService = Gson().fromJson<ArrayList<FacilityDataModel.TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblScopeofService>>() {}.type)
                FacilityDataModelOrg.getInstance().tblScopeofService = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblScopeofService>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblScopeofService.add(Gson().fromJson<FacilityDataModel.TblScopeofService>(jsonObj.get("tblScopeofService").toString(), FacilityDataModel.TblScopeofService::class.java))
                FacilityDataModelOrg.getInstance().tblScopeofService.add(Gson().fromJson<FacilityDataModelOrg.TblScopeofService>(jsonObj.get("tblScopeofService").toString(), FacilityDataModelOrg.TblScopeofService::class.java))
            }
        }

        if (jsonObj.has("tblPrograms")) {
            if (jsonObj.get("tblPrograms").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblPrograms = Gson().fromJson<ArrayList<FacilityDataModel.TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblPrograms>>() {}.type)
                FacilityDataModelOrg.getInstance().tblPrograms = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblPrograms>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblPrograms.add(Gson().fromJson<FacilityDataModel.TblPrograms>(jsonObj.get("tblPrograms").toString(), FacilityDataModel.TblPrograms::class.java))
                FacilityDataModelOrg.getInstance().tblPrograms.add(Gson().fromJson<FacilityDataModelOrg.TblPrograms>(jsonObj.get("tblPrograms").toString(), FacilityDataModelOrg.TblPrograms::class.java))
            }
        }

        if (jsonObj.has("tblFacilityServices")) {
            if (jsonObj.get("tblFacilityServices").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityServices>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityServices>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityServices.add(Gson().fromJson<FacilityDataModel.TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), FacilityDataModel.TblFacilityServices::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityServices.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), FacilityDataModelOrg.TblFacilityServices::class.java))
            }
        }

        if (jsonObj.has("tblAffiliations")) {
            if (jsonObj.get("tblAffiliations").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblAffiliations = Gson().fromJson<ArrayList<FacilityDataModel.TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblAffiliations>>() {}.type)
                FacilityDataModelOrg.getInstance().tblAffiliations = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblAffiliations>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblAffiliations.add(Gson().fromJson<FacilityDataModel.TblAffiliations>(jsonObj.get("tblAffiliations").toString(), FacilityDataModel.TblAffiliations::class.java))
                FacilityDataModelOrg.getInstance().tblAffiliations.add(Gson().fromJson<FacilityDataModelOrg.TblAffiliations>(jsonObj.get("tblAffiliations").toString(), FacilityDataModelOrg.TblAffiliations::class.java))
            }
        }

        if (jsonObj.has("tblDeficiency")) {
            if (jsonObj.get("tblDeficiency").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblDeficiency = Gson().fromJson<ArrayList<FacilityDataModel.TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblDeficiency>>() {}.type)
                FacilityDataModelOrg.getInstance().tblDeficiency = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblDeficiency>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblDeficiency.add(Gson().fromJson<FacilityDataModel.TblDeficiency>(jsonObj.get("tblDeficiency").toString(), FacilityDataModel.TblDeficiency::class.java))
                FacilityDataModelOrg.getInstance().tblDeficiency.add(Gson().fromJson<FacilityDataModelOrg.TblDeficiency>(jsonObj.get("tblDeficiency").toString(), FacilityDataModelOrg.TblDeficiency::class.java))
            }
        }

        if (jsonObj.has("tblComplaintFiles")) {
            if (jsonObj.get("tblComplaintFiles").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<FacilityDataModel.TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblComplaintFiles>>() {}.type)
                FacilityDataModelOrg.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblComplaintFiles>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblComplaintFiles.add(Gson().fromJson<FacilityDataModel.TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), FacilityDataModel.TblComplaintFiles::class.java))
                FacilityDataModelOrg.getInstance().tblComplaintFiles.add(Gson().fromJson<FacilityDataModelOrg.TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), FacilityDataModelOrg.TblComplaintFiles::class.java))
            }
        }

        if (jsonObj.has("NumberofComplaints")) {
            if (jsonObj.get("NumberofComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<FacilityDataModel.numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModel.numberofComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<FacilityDataModelOrg.numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.numberofComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofComplaints.add(Gson().fromJson<FacilityDataModel.numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), FacilityDataModel.numberofComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofComplaints.add(Gson().fromJson<FacilityDataModelOrg.numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), FacilityDataModelOrg.numberofComplaints::class.java))
            }
        }

        if (jsonObj.has("NumberofJustifiedComplaints")) {
            if (jsonObj.get("NumberofJustifiedComplaints").toString().startsWith("[")) {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<FacilityDataModel.numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModel.numberofJustifiedComplaints>>() {}.type)
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<FacilityDataModelOrg.numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.numberofJustifiedComplaints>>() {}.type)
            } else {
                FacilityDataModel.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<FacilityDataModel.numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), FacilityDataModel.numberofJustifiedComplaints::class.java))
                FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<FacilityDataModelOrg.numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), FacilityDataModelOrg.numberofJustifiedComplaints::class.java))
            }
        }

        if (jsonObj.has("JustifiedComplaintRatio")) {
            if (jsonObj.get("JustifiedComplaintRatio").toString().startsWith("[")) {
                FacilityDataModel.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<FacilityDataModel.justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<FacilityDataModel.justifiedComplaintRatio>>() {}.type)
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<FacilityDataModelOrg.justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.justifiedComplaintRatio>>() {}.type)
            } else {
                FacilityDataModel.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<FacilityDataModel.justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), FacilityDataModel.justifiedComplaintRatio::class.java))
                FacilityDataModelOrg.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<FacilityDataModelOrg.justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), FacilityDataModelOrg.justifiedComplaintRatio::class.java))
            }
        }

        if (jsonObj.has("tblFacilityPhotos")) {
            if (jsonObj.get("tblFacilityPhotos").toString().startsWith("[")) {
                FacilityDataModel.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<FacilityDataModel.TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityDataModel.TblFacilityPhotos>>() {}.type)
                FacilityDataModelOrg.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<FacilityDataModelOrg.TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityDataModelOrg.TblFacilityPhotos>>() {}.type)
            } else {
                FacilityDataModel.getInstance().tblFacilityPhotos.add(Gson().fromJson<FacilityDataModel.TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), FacilityDataModel.TblFacilityPhotos::class.java))
                FacilityDataModelOrg.getInstance().tblFacilityPhotos.add(Gson().fromJson<FacilityDataModelOrg.TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), FacilityDataModelOrg.TblFacilityPhotos::class.java))
            }
        }


    }

    private class VisitationPlanningViewHolder(view: View?) {
        val facilityNameValueTextView: TextView
        val initialContractDateValueTextView: TextView
        val visitationTypeValueTextView: TextView
        val loadBtn: Button

        init {
            this.facilityNameValueTextView = view?.findViewById(R.id.facilityNameValueTextView) as TextView
            this.initialContractDateValueTextView = view?.findViewById(R.id.initialContractDateValueTextView) as TextView
            this.visitationTypeValueTextView = view?.findViewById(R.id.visitationTypeValueTextView) as TextView
            this.loadBtn = view?.findViewById(R.id.loadBtn) as Button

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


