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
import kotlinx.android.synthetic.main.app_adhoc_visitation_filter_fragment.*

import org.json.JSONObject
import org.json.XML
import java.util.*


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
    var facilitiesList = ArrayList<AAAFacilityComplete>()
    var visitationList = ArrayList<AnnualVisitationInspectionFormData>()
    var itemSelected = false
    var facilityNameInputField: EditText? = null
    var firstLoading = true
    var isVisitationPlanning = false
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


        clubCodeEditText.setText("004")
//            loadSpecialistName()


        newVisitationBtn.setOnClickListener({
            shouldShowVisitation = true
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
                            var searchDialog = SearchDialog(context, facilityNames)
                            searchDialog.show()
                            searchDialog.setOnDismissListener {
                                if (searchDialog.selectedString.isNotEmpty()) {
                                    getFullFacilityDataFromAAA(facilities.filter { it.facname == searchDialog.selectedString }[0].facnum.toInt())
                                }
                            }
                        })
                    }, Response.ErrorListener {
                recordsProgressView.visibility = View.INVISIBLE
                context!!.toast("Connection Error")
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))







//            var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
//            startActivity(intent)
        })


        clubCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reloadFacilitiesList()
            }
        })

        visitationfacilityIdVal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reloadFacilitiesList()
            }

        })

        facilityDbaButton.setOnClickListener(View.OnClickListener {
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
                                    facilityDbaButton.setText("")
                                } else {
                                    facilityDbaButton.setText(searchDialog.selectedString)
                                }
                                reloadFacilitiesList()
                            }
                        })
                    }, Response.ErrorListener {
                context!!.toast("Connection Error")
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        })

        entityNameButton.setOnClickListener(View.OnClickListener {
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
                                    entityNameButton.setText("")
                                } else {
                                    entityNameButton.setText(searchDialog.selectedString)
                                }
                                reloadFacilitiesList()
                            }
                        })
                    }, Response.ErrorListener {
                context!!.toast("Connection Error")
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        })


        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(ArrayList())
                        reloadFacilitiesList()
                    })
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading facilities")
            Log.v("Loading error", "" + it.message)
        }))

        firstLoading = false
    }

    val spinnersOnItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            reloadFacilitiesList()
        }

    }

    fun parseVisitationsData(jsonObject: JSONObject): VisitationsModel {
        var visitationsModel = VisitationsModel()

        if (jsonObject.has("PendingVisitations")){
            if (jsonObject.get("PendingVisitations").toString().startsWith("[")) {
                visitationsModel.pendingVisitationsArray = Gson().fromJson(jsonObject.get("PendingVisitations").toString(), Array<VisitationsModel.PendingVisitationModel>::class.java).toCollection(ArrayList())
            }else{
                visitationsModel.pendingVisitationsArray.add(Gson().fromJson(jsonObject.get("PendingVisitations").toString(), VisitationsModel.PendingVisitationModel::class.java))
            }
        }

        if (jsonObject.has("CompletedVisitations")) {
            if (jsonObject.get("CompletedVisitations").toString().startsWith("[")) {
                visitationsModel.completedVisitationsArray = Gson().fromJson(jsonObject.get("CompletedVisitations").toString(), Array<VisitationsModel.CompletedVisitationModel>::class.java).toCollection(ArrayList())
            }else{
                visitationsModel.completedVisitationsArray.add(Gson().fromJson(jsonObject.get("CompletedVisitations").toString(), VisitationsModel.CompletedVisitationModel::class.java))
            }
        }

        if (jsonObject.has("Deficiencies")) {
            if (jsonObject.get("Deficiencies").toString().startsWith("[")) {
                visitationsModel.deficienciesArray = Gson().fromJson(jsonObject.get("Deficiencies").toString(), Array<VisitationsModel.DeficiencyModel>::class.java).toCollection(ArrayList())
            }else{
                visitationsModel.deficienciesArray.add(Gson().fromJson(jsonObject.get("Deficiencies").toString(), VisitationsModel.DeficiencyModel::class.java))
            }
        }

        return visitationsModel
    }

    fun reloadFacilitiesList(){
        var parametersString = StringBuilder()
        if (true) {
            if (clubCodeEditText.text.trim().isNotEmpty()) {
                with(parametersString) {
                    append("clubCode=" + clubCodeEditText.text.trim())
                    append("&")
                }
            }else {
                with(parametersString) {
                    append("clubCode=004")
                    append("&")
                }
            }

            with(parametersString) {
                append("facNum=" + visitationfacilityIdVal.text.trim())
                append("&")
            }

            if (!facilityDbaButton.text.contains("Select") && facilityDbaButton.text.length > 1) {
                with(parametersString) {
                    //TODO added to void specialist value until they let us know how we will use it
                    //**********************
//                        append("specialist=" + visitationSpecialistName.text)
//                        append("&")
                    //**********************

                    append("specialist=")
                    append("&")

                }
            }else{
                with(parametersString) {
                    append("specialist=")
                    append("&")
                }
            }

            if (!entityNameButton.text.contains("Select") && entityNameButton.text.length > 1) {
                with(parametersString) {
                    append(("dba=" + entityNameButton.text))
                    append("&")
                }
            }else{
                with(parametersString) {
                    append("dba=")
                    append("&")
                }
            }


//                recordsProgressView.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getVisitations + parametersString,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            recordsProgressView.visibility = View.INVISIBLE

                            var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                            var jsonObj = obj.getJSONObject("responseXml")


                            var visitationsModel = parseVisitationsData(jsonObj)

                            facilitiesListView.visibility = View.VISIBLE
                            var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                            facilitiesListView.adapter = visitationPlanningAdapter
                        })
                    }, Response.ErrorListener {
                context!!.toast("Error while loading visitations: "+it.message)
                Log.v("error while loading", "error while loading visitation records")
            }))
        } else {
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getVisitations + parametersString,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {

                            var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                            var jsonObj = obj.getJSONObject("responseXml")


                            var visitationsModel = parseVisitationsData(jsonObj)

                            facilitiesListView.visibility = View.VISIBLE
                            var visitationPlanningAdapter = VisitationPlanningAdapter(context, visitationsModel)
                            facilitiesListView.adapter = visitationPlanningAdapter
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading visitation records")
            }))
        }
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

    inner class VisitationListAdapter : BaseAdapter {

        private var visitationList = ArrayList<AnnualVisitationInspectionFormData>()
        private var context: Context? = null

        constructor(context: Context?, visitationList: ArrayList<AnnualVisitationInspectionFormData>) : super() {
            this.visitationList = visitationList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.custom_visitation_list_item, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.vrID.text = visitationList[position].annualvisitationid.toString()
            vh.vrBy.text = visitationList[position].facilityrepresentativename
            vh.vrDate.text = visitationList[position].dateofinspection
            vh.vrPlanned.text = visitationList[position].dateofinspection
            vh.vrPlanned.visibility = if (visitationList[position].dateofinspection.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            vh.vrStatus.text = if (visitationList[position].dateofinspection.toTime() > Date().time) "Planned" else "regular"
            vh.vrType.text = visitationList[position].businessname
//            if (position%2!=0) vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg_rtol)
//            else vh.vrLL.setBackgroundResource(R.drawable.visitation_listitem_bkg)
            vh.vrLoadBtn.setOnClickListener({
                AnnualVisitationSingleton.getInstance().clear()
                AnnualVisitationSingleton.getInstance().apply {
                    facilityId = visitationList[position].facilityid
                    annualVisitationId = visitationList[position].annualvisitationid
                    facilityRepresentative = visitationList[position].facilityrepresentativename
                    automotiveSpecialist = visitationList[position].automotivespecialistname
                    dateOfVisitation = visitationList[position].dateofinspection.toTime()
                    inspectionType = visitationList[position].inspectiontypeid
                    monthDue = visitationList[position].monthdue
                    changesMade = visitationList[position].changesmade
                    paymentMethods = visitationList[position].paymentmethods

                    emailModel = AAAEmailModel()
                    emailModel!!.emailid = visitationList[position].emailaddressid

                    phoneModel = AAAPhoneModel()
                    phoneModel!!.phoneid = visitationList[position].phonenumberid

                    personnelId = visitationList[position].personnelid
                    vehicleServices = visitationList[position].vehicleservices
                    vehicles = visitationList[position].vehicles
                    affliations = visitationList[position].affiliations
                    defeciencies = visitationList[position].defeciencies
                    complaints = visitationList[position].complaints
                }

                FacilityDataModel.getInstance().annualVisitationId = visitationList[position].annualvisitationid
                getFullFacilityDataFromAAA(visitationList[position].facno)

//                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityWithIdUrl + visitationList[position].facilityid,
//                        Response.Listener { response ->
//                            activity!!.runOnUiThread(Runnable {
//                                var facilityComplete = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList()).get(0) as AAAFacilityComplete
//                                AnnualVisitationSingleton.getInstance().apply {
//                                    facilityId = facilityComplete.facid
//                                    facilityName = facilityComplete.businessname
//                                    facilityType = facilityComplete.facilitytypeid
//                                    billingMonth = facilityComplete.billingmonth
//                                    billingAmount = facilityComplete.billingamount
//                                    contractType = facilityComplete.contracttypeid
//                                    webSiteUrl = facilityComplete.website
//                                    facilityType = facilityComplete.facilitytypeid
//                                    currentContractDate = facilityComplete.contractcurrentdate
//                                    setInsuranceExpirationDate(facilityComplete.insuranceexpdate)
//                                    setInitialContractDate(facilityComplete.contractinitialdate)
//                                    assignedTo = facilityComplete.assignedtoid
//                                    office = facilityComplete.officeid
//                                    entityName = facilityComplete.entityname
//                                    timeZone = facilityComplete.timezoneid
//                                    taxId = facilityComplete.taxidnumber
//                                    repairOrderCount = facilityComplete.facilityrepairordercount
//                                    serviceAvailability = facilityComplete.svcavailability
//                                    ardNumber = facilityComplete.automotiverepairnumber
//                                    setArdExpirationDate(facilityComplete.automotiverepairexpdate)
//                                }
//
////                                val fragment: android.support.v4.app.Fragment
////                                fragment = FragmentAnnualVisitationPager()
////                                val fragmentManagerSC = fragmentManager
////                                val ftSC = fragmentManagerSC!!.beginTransaction()
////                                ftSC.replace(R.id.fragment, fragment)
////                                ftSC.addToBackStack("")
////                                ftSC.commit()
////                                var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
////                                startActivity(intent)
//                            })
//                        }, Response.ErrorListener {
//                    Log.v("error while loading", "error while loading facilities")
//                    Log.v("Loading error", "" + it.message)
//                }))


            })
            return view
        }


        override fun getItem(position: Int): Any {
            // return item at 'position'
            return visitationList[position]
        }

        override fun getItemId(position: Int): Long {
            // return item Id by Long datatype
            return position.toLong()
        }

        override fun getCount(): Int {
            // return quantity of the list
            return visitationList.size
        }
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
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.pendingVisitationsArray[position].AutomotiveRepairExpDate
                vh.visitationTypeValueTextView.text = "Pending"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.pendingVisitationsArray[position].FACNo.toInt())
                })
            } else if (position >= visitationPlanningModelList.pendingVisitationsArray.size && position < visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].EntityName
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].AutomotiveRepairExpDate
                vh.visitationTypeValueTextView.text = "Completed"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.completedVisitationsArray[position - visitationPlanningModelList.pendingVisitationsArray.size].FACNo.toInt())
                })
            } else if (position >= visitationPlanningModelList.pendingVisitationsArray.size + visitationPlanningModelList.completedVisitationsArray.size) {
                vh.facilityNameValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].EntityName
                vh.initialContractDateValueTextView.text = visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].AutomotiveRepairExpDate
                vh.visitationTypeValueTextView.text = "Deficiency"
                vh.loadBtn.setOnClickListener({
                    getFullFacilityDataFromAAA(visitationPlanningModelList.deficienciesArray[position - visitationPlanningModelList.pendingVisitationsArray.size - visitationPlanningModelList.completedVisitationsArray.size].FACNo.toInt())
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

    fun getFullFacilityDataFromAAA(facilityNumber: Int) {
//        recordsProgressView.visibility = View.VISIBLE
        if (TypeTablesModel.getInstance().AARDeficiencyType.size == 0) {
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getTypeTables + "",
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                            var jsonObj = obj.getJSONObject("responseXml")

                            TypeTablesModel.setInstance(Gson().fromJson(jsonObj.toString(), TypeTablesModel::class.java))

                            Log.v("*******url", String.format(Constants.getFacilityData, facilityNumber, "004"))
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Constants.getFacilityData, facilityNumber, "004"),
                                    Response.Listener { response ->
                                        Log.v("*****response = ", response)
                                        activity!!.runOnUiThread(Runnable {
                                            recordsProgressView.visibility = View.GONE
                                            if (!response.contains("FacID not found")) {
                                                var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
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
                                    }, Response.ErrorListener {
                                recordsProgressView.visibility = View.GONE
                                context!!.toast("Connection Error.")
                                Log.v("error while loading", "error while loading facilities")
                                Log.v("Loading error", "" + it.message)
                            }))


                        })
                    }, Response.ErrorListener {
                recordsProgressView.visibility = View.GONE
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        } else {
            recordsProgressView.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Constants.getFacilityData, facilityNumber, "004"),
                    Response.Listener { response ->
                        Log.v("*****response = ", response)
                        activity!!.runOnUiThread(Runnable {
                            recordsProgressView.visibility = View.GONE
                            if (!response.contains("FacID not found")) {
                                var obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
                                        .replace("<tblSurveySoftwares/><tblSurveySoftwares><ShopMgmtSoftwareName/></tblSurveySoftwares>", ""))
                                var jsonObj = obj.getJSONObject("responseXml")
                                parseFacilityDataJsonToObject(jsonObj)

                                var intent = Intent(context, com.inspection.fragments.ItemListActivity::class.java)
                                startActivity(intent)
                            } else {
                                context!!.toast("Facility data not found")
                            }
                        })
                    }, Response.ErrorListener {
                recordsProgressView.visibility = View.GONE
                context!!.toast("Connection Error.")
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
        }


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

    private class VisitationPlanningViewHolder(view: View?) {
        val facilityNameValueTextView: TextView
        val initialContractDateValueTextView: TextView
        val visitationTypeValueTextView : TextView
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


