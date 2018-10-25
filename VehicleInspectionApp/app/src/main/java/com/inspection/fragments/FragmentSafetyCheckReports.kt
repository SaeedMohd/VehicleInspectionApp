package com.inspection.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.adapter.SafetyCheckReportsRecyclerViewAdapter
import com.inspection.model.SafetyCheckReportModel
import com.inspection.serverTasks.GenericServerTask
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Timer
import android.widget.*


class FragmentSafetyCheckReports : androidx.fragment.app.Fragment() {

    private var view2: View? = null
    private var safetyCheckRecyclerView: RecyclerView? = null
    private var safetyCheckListLayout: RelativeLayout? = null
    private var noSafetyCheckHistoryTextView: TextView? = null

    private var safetyCheckReportModels: ArrayList<SafetyCheckReportModel>? = null
    private var modifiedSafetyCheckReportsModels: ArrayList<SafetyCheckReportModel>? = null

    var selectedMobileUserProfileID: Int = 0
    var selectVehicleID: Int = 0
    var safetyCheckReportID: Int = 0

    var isSelectedReportCompleted = false

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view2 = inflater?.inflate(R.layout.fragment_safety_check_reports, container, false)


        safetyCheckRecyclerView = view2!!.findViewById<RecyclerView>(R.id.safetyCheckRecyclerView)

        safetyCheckRecyclerView!!.layoutManager = LinearLayoutManager(context)
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        safetyCheckRecyclerView!!.addItemDecoration(itemDecoration)
        // Added newly
        noSafetyCheckHistoryTextView=view2!!.findViewById<TextView>(R.id.noSafetyCheckHistoryTextView)

        safetyCheckListLayout = view2!!.findViewById<RelativeLayout>(R.id.safetyCheckListLayout)
        safetyCheckListLayout!!.visibility = View.INVISIBLE


        selectedMobileUserProfileID = ApplicationPrefs.getInstance(context).userProfilePref.mobileUserProfileId
        selectVehicleID = -1
        //            startSafetyCheckTimer();
        getSafetyCheckReports()

        (activity as MainActivity).supportActionBar!!.title = ApplicationPrefs.getInstance(context).safetyCheckProgramName+" Reports"

        return view2 as View
    }

    private fun getSafetyCheckReports() {
        val progressDialog = ProgressDialog(context)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        object : GenericServerTask(context!!, context!!.getString(R.string.getSafetyCheckReportsListForCustomer), arrayOf("mobileUserProfileID", "vehicleID"), arrayOf("" + selectedMobileUserProfileID, "" + selectVehicleID)) {
            override fun onTaskCompleted(result: String) {
                try {
                    progressDialog.dismiss()

                    val jObject = JSONObject(result.toString())

                    val resultArray = jObject.getJSONArray("GetSafetyCheckReportsListForCustomerResult")

                    safetyCheckReportModels = Gson().fromJson<ArrayList<SafetyCheckReportModel>>(resultArray.toString(), object : TypeToken<ArrayList<SafetyCheckReportModel>>() {

                    }.type)

                    if (safetyCheckReportModels!!.size == 0) {
                        noSafetyCheckHistoryTextView!!.visibility = View.VISIBLE
                        safetyCheckListLayout!!.visibility = View.GONE
                        return
                    } else {
                        noSafetyCheckHistoryTextView!!.visibility = View.GONE
                        safetyCheckListLayout!!.visibility = View.VISIBLE
                    }


                    modifiedSafetyCheckReportsModels = safetyCheckReportModels
                    val headerPositions = ArrayList<Int>()
                    var vehicleName = ""
                    for (x in safetyCheckReportModels!!.indices) {
                        if (x == 0) {
                            headerPositions.add(0)
                            vehicleName = safetyCheckReportModels!![x].MMY!!
                        } else {
                            if (vehicleName != safetyCheckReportModels!![x].MMY) {
                                vehicleName = safetyCheckReportModels!![x].MMY!!
                                headerPositions.add(x)
                            }
                        }
                    }

                    for (x in headerPositions.indices.reversed()) {
                        val sf = SafetyCheckReportModel()
                        sf.safetyCheckReportsID = -500

                        sf.MMY = safetyCheckReportModels!![headerPositions[x]].MMY
                        modifiedSafetyCheckReportsModels!!.add(headerPositions[x], sf)
                    }


                    //safetyCheckRecyclerView.setAdapter(new SafetyCheckItemsRecyclerViewAdapter(getContext(), safetyCheckListView, modifiedSafetyCheckItemsModels, safetyCheckReportID));

//                    val adapter = SafetyCheckReportsRecyclerViewAdapter(context, safetyCheckListView, modifiedSafetyCheckReportsModels!!, safetyCheckReportID)
                    val adapter = SafetyCheckReportsRecyclerViewAdapter(context!!, modifiedSafetyCheckReportsModels!!, safetyCheckReportID)

                    adapter.setClickListener (View.OnClickListener { view ->
                            val position = safetyCheckRecyclerView!!.indexOfChild(view)
                            safetyCheckReportID = modifiedSafetyCheckReportsModels!![position].safetyCheckReportsID
                            isSelectedReportCompleted = modifiedSafetyCheckReportsModels!![position].status == 3
                            val safetyCheckItemsFragment = FragmentSafetyCheckItems()
                            safetyCheckItemsFragment.selectedMobileUserProfileID = selectedMobileUserProfileID
                            safetyCheckItemsFragment.safetyCheckReportID = safetyCheckReportID
                            safetyCheckItemsFragment.isSelectedReportCompleted = isSelectedReportCompleted
                            safetyCheckItemsFragment.vehicleName = if (modifiedSafetyCheckReportsModels!![position].MMY!!.length > 0) modifiedSafetyCheckReportsModels!![position].MMY!! else modifiedSafetyCheckReportsModels!![position].vin!!
                            safetyCheckItemsFragment.reportDate = modifiedSafetyCheckReportsModels!![position].safetyCheckReportDate!!
                            safetyCheckItemsFragment.customerName = modifiedSafetyCheckReportsModels!![position].customerName
                            safetyCheckItemsFragment.customerEmail = modifiedSafetyCheckReportsModels!![position].customerEmail
                            safetyCheckItemsFragment.customerPhoneNumber = modifiedSafetyCheckReportsModels!![position].customerPhoneNumber
                            safetyCheckItemsFragment.safetyCheckReportSummaryDescription = modifiedSafetyCheckReportsModels!![position].safetyCheckReportSummaryDescription
                            val fragmentManagerSC = fragmentManager
                            val ftSC = fragmentManagerSC!!.beginTransaction()
                            ftSC.replace(R.id.fragment, safetyCheckItemsFragment)
                            ftSC.addToBackStack("")
                            ftSC.commit()
                        })

                    safetyCheckRecyclerView!!.adapter = adapter
                    safetyCheckListLayout!!.visibility = View.VISIBLE//TODO RecyclerView Change

                } catch (jsonException: JSONException) {
                    jsonException.printStackTrace()
                }

            }
        }.execute()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
        Log.v("OnStop******", "OnStop******")
        try {
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
        }

    }


}