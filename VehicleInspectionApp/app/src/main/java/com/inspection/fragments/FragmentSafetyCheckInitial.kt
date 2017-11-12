package com.inspection.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.MainActivity
import com.inspection.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.adapter.SafetyCheckShopInitialRecyclerViewAdapter
import com.inspection.model.SafetyCheckReportModel
import com.inspection.serverTasks.GenericServerTask
import kotlinx.android.synthetic.main.fragment_safety_check_shop_initial.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by sheri on 5/26/2017.
 */
class FragmentSafetyCheckInitial : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val myView = inflater?.inflate(R.layout.fragment_safety_check_shop_initial, container, false)


        (activity as MainActivity).supportActionBar!!.title = ApplicationPrefs.getInstance(context).safetyCheckProgramName

        return myView as View
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        initialsafetyCheckRecyclerView.layoutManager = LinearLayoutManager(context)
        initialsafetyCheckRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        startLoadingSafetyCheckDetails()
    }

    private fun startLoadingSafetyCheckDetails() {
        val progress = ProgressDialog(context)
        progress.isIndeterminate = true
        progress.setCancelable(false)
        progress.setMessage("Loading...")
        progress.show()

        object : GenericServerTask(context, getString(R.string.getSafetyCheckReportsListForShop), arrayOf("accountID"), arrayOf("" + ApplicationPrefs.getInstance(context).userProfilePref.accountID)) {
            override fun onTaskCompleted(result: String) {

                var safetyCheckReportsModelArrayList = ArrayList<SafetyCheckReportModel>()

                var `object`: JSONObject? = null
                var arr: JSONArray? = null
                try {
                    `object` = JSONObject(result)
                    arr = `object`.getJSONArray("GetSafetyCheckReportsListForShopResult")
                    val type = object : TypeToken<List<SafetyCheckReportModel>>() {}.type
                    safetyCheckReportsModelArrayList = Gson().fromJson<ArrayList<SafetyCheckReportModel>>(arr.toString(), type)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                val safetyCheckReprtsTodo = safetyCheckReportsModelArrayList.filter { it.status == 1 } as ArrayList<SafetyCheckReportModel>
                val safetyCheckReportsInProgress = safetyCheckReportsModelArrayList.filter { it.status == 2 } as ArrayList<SafetyCheckReportModel>
                val safetyCheckReportsCompleted = safetyCheckReportsModelArrayList.filter { it.status == 3 } as ArrayList<SafetyCheckReportModel>

                val adapter = SafetyCheckShopInitialRecyclerViewAdapter(context, initialsafetyCheckRecyclerView, safetyCheckReprtsTodo, safetyCheckReportsInProgress, safetyCheckReportsCompleted)
                adapter.setClickListener(View.OnClickListener { view ->
                    val position = initialsafetyCheckRecyclerView.getChildAdapterPosition(view)
                    if (position == 1) {
                        val fragment = FragmentSafetyCheckSearchForCustomerVehicle()
                        val fragmentManagerSC = fragmentManager
                        val ftSC = fragmentManagerSC.beginTransaction()
                        ftSC.replace(R.id.fragment, fragment)
                        ftSC.addToBackStack("")
                        ftSC.commit()
                    } else {
                        val selectedItem = adapter.getSafetyCheckReportItem(position)
                        val fragment = FragmentSafetyCheckItems()
                        fragment.safetyCheckReportID = selectedItem.safetyCheckReportsID
                        fragment.selectedMobileUserProfileID = selectedItem.mobileUserProfileID
                        fragment.isSelectedReportCompleted = (selectedItem.status == 3)
                        fragment.selectVehicleID = selectedItem.vehicleID
                        fragment.vehicleName = if (selectedItem.MMY!!.length > 0) selectedItem.MMY!! else selectedItem.vin!!
                        fragment.reportDate = selectedItem.safetyCheckReportDate!!
                        fragment.customerEmail = selectedItem.customerEmail
                        fragment.customerPhoneNumber = selectedItem.customerPhoneNumber
                        fragment.customerName = selectedItem.customerName
                        fragment.safetyCheckReportSummaryDescription = selectedItem.safetyCheckReportSummaryDescription
                        val fragmentManagerSC = fragmentManager
                        val ftSC = fragmentManagerSC.beginTransaction()
                        ftSC.replace(R.id.fragment, fragment)
                        ftSC.addToBackStack("")
                        ftSC.commit()
                    }
                })
                initialsafetyCheckRecyclerView.adapter = adapter

                progress.dismiss()
            }
        }.execute()
    }

}