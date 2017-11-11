package com.matics.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.matics.LivePhoneReadings
import com.matics.MainActivity
import com.matics.R
import com.matics.adapter.DataBaseHelper
import com.matics.imageloader.Utils
import com.matics.model.VehicleMaintenanceDetailModel
import com.matics.model.VehicleMaintenanceModel
import com.matics.model.VehicleProfileModel
import com.matics.model.VehicleServicesModel
import com.matics.model.VinResolvedObject
import com.matics.Utils.Utility
import com.matics.serverTasks.GetVehicleDetailsResolvedFromVinTask

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import edu.sfsu.cs.orange.ocr.CaptureActivity

import android.app.Activity.RESULT_OK
import android.widget.*
import kotlinx.android.synthetic.main.fragment_user_vehicles.*
import kotlinx.android.synthetic.main.spinner_item.view.*
import kotlinx.android.synthetic.main.vehicle_services_list.*


class FragmentUserVehicles : Fragment() {

    private var userVehiclesAdapter: UserVehiclesExpandableAdapter? = null

    private var asyncTaskCount = 0

    private var isEditingVehicle = false
    private var selectedVinIdForEdit: String? = null

    internal lateinit var userVehiclesArrayList: ArrayList<VehicleProfileModel>

    internal var vehiclesUpcomingServicesArrayList: ArrayList<VehicleServicesModel>? = null
    internal var vehiclesRecomnendedServicesArrayList: ArrayList<VehicleServicesModel>? = null

    internal var vehicleServicesHashMap: MutableMap<String, ArrayList<VehicleServicesModel>> = HashMap()
    internal var vehicleMaintenanceHistoryHashMap: MutableMap<String, ArrayList<VehicleMaintenanceModel>> = HashMap()
    internal var vehicleMaintenanceHistoryDetailsHashMap = HashMap<String, VehicleMaintenanceDetailModel>()

    internal lateinit var view: View

    internal var tf: Typeface? = null

    internal var vehicleMaintenanceModelArrayList = ArrayList<VehicleMaintenanceModel>()

    private var vehicleMaintenanceDetailModel: VehicleMaintenanceDetailModel? = null

    var vehicleServicesExpandableListView: ExpandableListView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // ------------initializing variables
        view = inflater!!.inflate(R.layout.fragment_user_vehicles, container, false)

        (activity as MainActivity).supportActionBar!!.title = "User Vehicles"

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dbHelper = DataBaseHelper(activity)
        userVehiclesArrayList = dbHelper.getprofiledataforlist()

        Utils.setShopImage(activity, imagebg)

        initializeUI()

        vehicleServicesProgressBar.visibility = View.VISIBLE
        for (x in userVehiclesArrayList.indices) {
            asyncTaskCount += 3
            if (x == userVehiclesArrayList.size - 1) {
                // CHECK: Validate VIN is not Null -
                CallGetVehicleMaintenanceByVIN().execute(userVehiclesArrayList[x].vinid)
                GetRecommendedServicesTask().execute(userVehiclesArrayList[x].vinid)
                GetUpcomingServicesTask().execute(userVehiclesArrayList[x].vinid)
            } else if (x == 0) {
                CallGetVehicleMaintenanceByVIN().execute(userVehiclesArrayList[x].vinid)
                GetRecommendedServicesTask().execute(userVehiclesArrayList[x].vinid)
                GetUpcomingServicesTask().execute(userVehiclesArrayList[x].vinid)
            } else {
                CallGetVehicleMaintenanceByVIN().execute(userVehiclesArrayList[x].vinid)
                GetRecommendedServicesTask().execute(userVehiclesArrayList[x].vinid)
                GetUpcomingServicesTask().execute(userVehiclesArrayList[x].vinid)
            }

        }

    }

    private fun initializeUI() {
        userVehiclesAdapter = UserVehiclesExpandableAdapter()

        vehiclesList!!.setAdapter(userVehiclesAdapter)

//        vehicleServicesProgressBar.visibility = View.INVISIBLE
//        progressBarTextView.visibility = View.INVISIBLE

        addVehicleButton!!.setOnClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setMessage("You can add vehicle by adding details using two methods:\n\n-Manual:\nYou will add vehicle VIN, Make, Model, Year.\n\n-Scan VIN:\nYou will scan your vehicle's VIN with your camera and all vehicle details will be add automatically.")
            alertDialog.setPositiveButton("Manual") { dialog, which -> showAddVehicleForm() }
            alertDialog.setNegativeButton("Scan VIN") { dialog, which ->
                val intent = Intent(context, CaptureActivity::class.java)
                startActivityForResult(intent, 2134)
            }
            alertDialog.show()
        }

        cancelVehicleButton!!.setOnClickListener {
            clearEditTexts()
            showUserVehiclesList()
        }

        saveVehicleButton!!.setOnClickListener {
            if (validateInputs()) {
                if (vinValueEditText!!.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    object : GetVehicleDetailsResolvedFromVinTask(context, vinValueEditText!!.text.toString().trim { it <= ' ' }) {
                        override fun onTaskCompleted(result: String) {
                            var vinResolvedObject = VinResolvedObject("", "", "", "", 0.0f)
                            if (!result.contains("failed")) {
                                vinResolvedObject.year = result.split("!-!")[0]
                                vinResolvedObject.make = result.split("!-!")[1]
                                vinResolvedObject.model = result.split("!-!")[2]
                                vinResolvedObject.vin = result.split("!-!")[3]
                                AddVehicleTask().execute(vinResolvedObject.vin, vinResolvedObject.year, vinResolvedObject.make, vinResolvedObject.model, mileageValueEditText!!.text.toString())
                            } else {
                                Toast.makeText(activity, "Invalid VIN, Please make sure to add a valid vin. Otherwise, leave it blank and fill the rest details", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.execute()

                } else {
                    AddVehicleTask().execute("", yearValueEditText!!.text.toString(), makeValueEditText!!.text.toString(), modelValueEditText!!.text.toString(), mileageValueEditText!!.text.toString())
                }
            } else {
                Toast.makeText(activity, "Please enter valid data", Toast.LENGTH_SHORT).show()
            }
        }

        deleteVehicleButton!!.setOnClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setMessage("Are you sure you want to delete this vehicle?")
            alertDialog.setPositiveButton("Yes") { dialog, which -> DeleteVehicleTask().execute() }
            alertDialog.setNegativeButton("No", null)
            alertDialog.show()
        }


    }

    private fun clearEditTexts() {
        yearValueEditText!!.setText("")
        makeValueEditText!!.setText("")
        modelValueEditText!!.setText("")
        vinValueEditText!!.setText("")
        mileageValueEditText!!.setText("")
    }

    private fun showUserVehiclesList() {
        isEditingVehicle = false
        deleteVehicleButton!!.visibility = View.INVISIBLE
        selectedVinIdForEdit = ""
        vehiclesList!!.visibility = View.VISIBLE
        addVehicleForm!!.visibility = View.INVISIBLE
        addVehicleButton!!.visibility = View.VISIBLE
        vehiclesTitleTextView!!.setText("VEHICLES")
    }

    private fun showAddVehicleForm() {
        vehiclesList!!.visibility = View.INVISIBLE
        addVehicleForm!!.visibility = View.VISIBLE
        addVehicleButton!!.visibility = View.INVISIBLE

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == RESULT_OK) {
            handleOcrResult(data.data.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun validateInputs(): Boolean {

        if (vinValueEditText!!.text.toString().trim { it <= ' ' }.length > 0) {
            //            if (mileageValueEditText.getText().toString().trim().length() > 0) {
            //                return true;
            //            } else {
            //                Toast.makeText(getActivity(), "Please enter Mileage value", Toast.LENGTH_SHORT).show();
            //                return false;
            //            }
            return true
        } else {
            if (yearValueEditText!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(activity, "Please enter Year value", Toast.LENGTH_SHORT).show()
                return false
            }

            if (makeValueEditText!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(activity, "Please enter Make value", Toast.LENGTH_SHORT).show()
                return false
            }

            if (modelValueEditText!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(activity, "Please enter Model value", Toast.LENGTH_SHORT).show()
                return false
            }

            if (mileageValueEditText!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(activity, "Please enter Mileage value", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    private inner class AddVehicleTask : AsyncTask<String, Void, String>() {

        internal var vinResolvedObject: VinResolvedObject? = null
        internal lateinit var mileage: String
        internal var isVinEntered = false

        override fun onPreExecute() {
            super.onPreExecute()


        }

        override fun doInBackground(vararg params: String): String {
            vinResolvedObject = VinResolvedObject(params[0], params[1], params[2], params[3], params[4].toFloat())
            mileage = params[4]
            if (params[0].length > 0) {
                isVinEntered = true
                if (!isEditingVehicle) {
                    return addVehicleRequest(params[0], params[1], params[2], params[3], params[4])
                } else {
                    return updateVehicleRequest(params[0], params[1], params[2], params[3], params[4])
                }

            } else {
                if (!isEditingVehicle) {
                    return addVehicleRequest("", params[1], params[2], params[3], params[4])
                } else {
                    return updateVehicleRequest("", params[1], params[2], params[3], params[4])
                }
            }
        }


        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            //Log.dMainActivity.TAG, result)
            val profileResult: JSONObject
            if (result.contains("IsSuccess\":true")) {
                try {
                    val jObject = JSONObject(result)
                    if (!isEditingVehicle) {
                        profileResult = jObject
                                .getJSONObject("AddVehicleInformationResult")
                    } else {
                        profileResult = jObject
                                .getJSONObject("UpdateVehicleInformationResult")
                    }
                    val vinID = profileResult.getString("VINID")


                    val vehicleProfileModel = VehicleProfileModel()
                    vehicleProfileModel.vin = vinResolvedObject!!.vin
                    vehicleProfileModel.vinid = vinID
                    vehicleProfileModel.make = vinResolvedObject!!.make
                    vehicleProfileModel.model = vinResolvedObject!!.model
                    vehicleProfileModel.year = vinResolvedObject!!.year
                    vehicleProfileModel.androidPhoneId = LivePhoneReadings.phoneId
                    vehicleProfileModel.btID = LivePhoneReadings.bluetoothId

                    if (isVinEntered) {
                        vehicleProfileModel.vinRetrievable = true
                    } else {
                        vehicleProfileModel.vinRetrievable = false
                    }
                    vehicleProfileModel.mileage = mileage

                    val dbHelper = DataBaseHelper(activity)
                    if (!isEditingVehicle) {
                        vehicleProfileModel.vehicleHealth = 70
                        vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
                        dbHelper.addProfile(vehicleProfileModel)
                    } else {
                        dbHelper.updateprofile(vehicleProfileModel)
                    }
                    userVehiclesArrayList.clear()

                    userVehiclesArrayList = dbHelper.getprofiledataforlist()

                    userVehiclesAdapter!!.notifyDataSetChanged()

                    Toast.makeText(activity, "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                    clearEditTexts()
                    showUserVehiclesList()

                } catch (jsonExp: JSONException) {
                    jsonExp.printStackTrace()
                    Toast.makeText(activity, "Failed to add vehicle, please try again", Toast.LENGTH_SHORT).show()
                }

            } else if (result == "Invalid VIN") {
                Toast.makeText(activity, "Invalid VIN, Please make sure to add a valid vin. Otherwise, leave it blank and fill the rest details", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Failed to add vehicle, please try again", Toast.LENGTH_SHORT).show()
            }
        }


        private fun addVehicleRequest(vin: String, year: String, make: String, model: String, mileage: String): String {
            //Log.dMainActivity.TAG, "Calling Web Service")
            var result = ""
            try {

                val values = ContentValues()
                values.put("VIN", vin)
                values.put("MFYear", year)
                values.put("Make", make)
                values.put("Model", model)
                values.put("Mileage", mileage)
                values.put("MobileUserProfileId", LivePhoneReadings.getMobileUserProfileId())
                values.put("MacId", "user vehicles")

                result = Utility.postRequest(getString(R.string.addVehicleURL), values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
                result = "failed"
            }

            return result
        }


        private fun updateVehicleRequest(vin: String, year: String, make: String, model: String, mileage: String): String {
            //Log.dMainActivity.TAG, "Calling Web Service")
            var result = ""
            try {

                val values = ContentValues()
                values.put("VINID", selectedVinIdForEdit)
                values.put("VIN", vin)
                values.put("MFYear", year)
                values.put("Make", make)
                values.put("Model", model)
                values.put("Mileage", mileage)
                values.put("MobileUserProfileId", LivePhoneReadings.getMobileUserProfileId())
                values.put("MacId", "")

                result = Utility.postRequest(getString(R.string.updateVehicleURL), values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
                result = "failed"
            }

            return result
        }
    }

    private inner class DeleteVehicleTask : AsyncTask<String, Void, String>() {

        internal var vinResolvedObject: VinResolvedObject? = null
        internal var mileage: String? = null
        internal var isVinEntered = false

        override fun doInBackground(vararg params: String): String? {

            return deleteVehicleRequest(selectedVinIdForEdit!!)

        }


        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            //Log.dMainActivity.TAG, result)

            if (result.contains("IsSuccess\":true")) {

                val dbHelper = DataBaseHelper(activity)
                dbHelper.deleteVehicle(selectedVinIdForEdit)

                userVehiclesArrayList.clear()

                userVehiclesArrayList = dbHelper.getprofiledataforlist()

                userVehiclesAdapter!!.notifyDataSetChanged()

                Toast.makeText(activity, "Vehicle Deleted successfully", Toast.LENGTH_SHORT).show()
                clearEditTexts()
                showUserVehiclesList()


            } else {
                Toast.makeText(activity, "Failed to add vehicle, please try again", Toast.LENGTH_SHORT).show()
            }

        }


        private fun deleteVehicleRequest(vinid: String): String {
            var result = ""
            try {

                val values = ContentValues()
                values.put("VINID", vinid)

                result = Utility.postRequest(getString(R.string.deleteVehicleURL), values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
                result = "failed"
            }

            return result
        }
    }

    private inner class UserVehiclesExpandableAdapter internal constructor() : BaseExpandableListAdapter() {

        internal lateinit var inflater: LayoutInflater
        internal var fragmentManager = getFragmentManager()

        init {
            try {
                inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            } catch (exp: NullPointerException) {

            }

            //Log.dMainActivity.TAG, "" + userVehiclesArrayList.size)
        }

        override fun getGroupCount(): Int {
            return userVehiclesArrayList.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return 1
        }

        override fun getGroup(groupPosition: Int): Any {
            return userVehiclesArrayList[groupPosition].model
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return userVehiclesArrayList[groupPosition].year
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun onGroupExpanded(groupPosition: Int) {
            super.onGroupExpanded(groupPosition)

            for (x in 0..groupCount - 1) {
                if (x != groupPosition) {
                    vehiclesList!!.collapseGroup(x)
                }
            }
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            var view: View? = convertView


            if (view == null) {
                view = inflater.inflate(R.layout.user_vehicles_list_header_view, null)
            }

            val userVehicleHeaderTextView = view!!.findViewById<TextView>(R.id.userVehiclesListHeaderTextView)
            val userVehicleHeaderRelativeLayout = view.findViewById<RelativeLayout>(R.id.user_vehicle_list_header_relative_layout)
            val editVehicleButton = view.findViewById<ImageButton>(R.id.editVehicleButton)
            val groupIndicatorButton = view.findViewById<ImageButton>(R.id.groupIndicatorIcon)

            userVehicleHeaderTextView.text = userVehiclesArrayList[groupPosition].model

            userVehicleHeaderRelativeLayout.setOnClickListener {
                if (isExpanded) {
                    vehiclesList!!.collapseGroup(groupPosition)
                    groupIndicatorButton.setBackgroundResource(R.drawable.arrow_down)
                } else {
                    vehiclesList!!.expandGroup(groupPosition)
                    groupIndicatorButton.setBackgroundResource(R.drawable.arrow_right)
                }
            }

            editVehicleButton.setOnClickListener {
                vehiclesTitleTextView!!.text = "Edit " + userVehiclesArrayList[groupPosition].model
                vinValueEditText!!.setText(userVehiclesArrayList[groupPosition].vin)
                //Log.dMainActivity.TAG, "VIN = " + userVehiclesArrayList[groupPosition].vin)
                yearValueEditText!!.setText(userVehiclesArrayList[groupPosition].year)
                makeValueEditText!!.setText(userVehiclesArrayList[groupPosition].make)
                modelValueEditText!!.setText(userVehiclesArrayList[groupPosition].model)
                mileageValueEditText!!.setText(userVehiclesArrayList[groupPosition].mileage)
                selectedVinIdForEdit = userVehiclesArrayList[groupPosition].vinid
                isEditingVehicle = true
                deleteVehicleButton!!.visibility = View.VISIBLE
                showAddVehicleForm()
            }

            return view
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            var view: View? = convertView


            if (view == null) {
                view = inflater.inflate(R.layout.vehicle_services_list, null)
            }

            vehicleServicesExpandableListView = view!!.findViewById<ExpandableListView>(R.id.vehicleServicesExpandableListView)
            vehicleServicesExpandableListView!!.setAdapter(VehicleServicesDetailsExpandableListView(vehicleServicesHashMap[userVehiclesArrayList[groupPosition].vinid + "Upcoming"]!!, vehicleServicesHashMap[userVehiclesArrayList[groupPosition].vinid + "Recommended"]!!, vehicleMaintenanceHistoryHashMap[userVehiclesArrayList[groupPosition].vinid + "History"]!!, vehicleMaintenanceHistoryDetailsHashMap))

            return view!!
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return false
        }

        override fun onGroupCollapsed(groupPosition: Int) {
            super.onGroupCollapsed(groupPosition)

        }


    }

    private inner class VehicleServicesDetailsExpandableListView(internal var upcomingServices: ArrayList<VehicleServicesModel>, internal var recommendedServices: ArrayList<VehicleServicesModel>, internal var historyServices: ArrayList<VehicleMaintenanceModel>, internal var historyDetailsServices: HashMap<String, VehicleMaintenanceDetailModel>) : BaseExpandableListAdapter() {

        internal var listBackgroundColorIndex = 0
        internal var groupNames = arrayOf("Upcoming Service", "Recommended Services", "Maintenance History")

        internal var inflater: LayoutInflater


        init {

            inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        }

        override fun getGroupCount(): Int {
            return 3
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            try {
                if (groupPosition == 0) {
                    return upcomingServices.size
                } else if (groupPosition == 1) {
                    return recommendedServices.size
                } else {
                    return historyServices.size
                }
            } catch (exp: Exception) {
                return 0
            }

        }

        override fun getGroup(groupPosition: Int): Any? {
            return null
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any? {
            return null
        }

        override fun getGroupId(groupPosition: Int): Long {
            return 0
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return 0
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
            var view: View? = convertView

            if (view == null) {
                view = inflater.inflate(R.layout.vehicle_services_groups_layout, null)
            }


            val vehicleServiceGroupNameTextView = view!!.findViewById<TextView>(R.id.groupNameTextView)

            vehicleServiceGroupNameTextView.text = groupNames[groupPosition]

            if (listBackgroundColorIndex % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#F1F1F1"))
            } else {
                view.setBackgroundColor(Color.WHITE)
            }

            listBackgroundColorIndex++


            return view
        }



        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            var view = convertView


            if (view == null) {
                view = inflater.inflate(R.layout.vehicle_services_childs_layout, null)
            }

            if (groupPosition == 0) {
                if (upcomingServices.size > 0) {

                    val scheduleServiceButton = view!!.findViewById<ImageButton>(R.id.scheduleServiceButton)
                    val vehicleServiceNameTextView = view.findViewById<TextView>(R.id.serviceDescriptionTextView)


                    vehicleServiceNameTextView.text = upcomingServices[childPosition].serviceDesc


                    if (listBackgroundColorIndex % 2 == 0) {
                        view.setBackgroundColor(Color.parseColor("#F1F1F1"))
                    } else {
                        view.setBackgroundColor(Color.WHITE)
                    }

                    listBackgroundColorIndex++
                }
            }

            if (groupPosition == 1) {
                if (recommendedServices.size > 0) {

                    val scheduleServiceButton = view!!.findViewById<ImageButton>(R.id.scheduleServiceButton)
                    val vehicleServiceNameTextView = view.findViewById<TextView>(R.id.serviceDescriptionTextView)


                    vehicleServiceNameTextView.text = recommendedServices[childPosition].serviceDesc


                    if (listBackgroundColorIndex % 2 == 0) {
                        view.setBackgroundColor(Color.parseColor("#F1F1F1"))
                    } else {
                        view.setBackgroundColor(Color.WHITE)
                    }

                    listBackgroundColorIndex++
                }
            }

            if (groupPosition == 2) {
                if (historyServices.size > 0) {

                    val scheduleServiceButton = view!!.findViewById<ImageButton>(R.id.scheduleServiceButton)
                    val vehicleServiceNameTextView = view.findViewById<TextView>(R.id.serviceDescriptionTextView)
                    val invoiceID = historyServices[childPosition].invoiceID

                    try {
                        vehicleServiceNameTextView.text = historyServices[childPosition].serviceDate + " " + historyServices[childPosition].facility
                        //                        vehicleServiceNameTextView.setText(historyServices.get(childPosition).getS®erviceDate() + " " + Arrays.toString(historyDetailsServices.get(invoiceID + "HistoryDetails").getServicePerformedList()) + " " + historyServices.get(childPosition).getFacility());
                    } catch (nullExp: NullPointerException) {
                        nullExp.printStackTrace()
                    }

                    vehicleServiceNameTextView.setOnClickListener { CallGetVehicleMaintenanceDetails2().execute(invoiceID) }


                    if (listBackgroundColorIndex % 2 == 0) {
                        view.setBackgroundColor(Color.parseColor("#F1F1F1"))
                    } else {
                        view.setBackgroundColor(Color.WHITE)
                    }

                    listBackgroundColorIndex++
                }
            }
            vehicleServicesExpandableListView!!.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT,
                    getListViewHeight(vehicleServicesExpandableListView!!))

            return view!!
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return false
        }

        override fun onGroupCollapsed(groupPosition: Int) {
            super.onGroupCollapsed(groupPosition)
            listBackgroundColorIndex = 0
            vehicleServicesExpandableListView!!.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    getListViewHeight(vehicleServicesExpandableListView!!))
        }

        override fun onGroupExpanded(groupPosition: Int) {
            super.onGroupExpanded(groupPosition)
            listBackgroundColorIndex = 0
            vehicleServicesExpandableListView!!.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    getListViewHeight(vehicleServicesExpandableListView!!))

        }
    }


    private inner class GetUpcomingServicesTask : AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): Void? {
            getUpcomingServicesRequest(params[0])
            return null
        }


        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            asyncTaskCount--
            if (asyncTaskCount == 0) {
                vehicleServicesProgressBar.visibility = View.GONE
                initializeUI()
            }

        }

        private fun getUpcomingServicesRequest(vinId: String): String {
            var result: String? = null

            try {
                val values = ContentValues()
                values.put("VINID", vinId)

                result = Utility.postRequest(getString(R.string.vehicle_upcoming_services_url), values)

                val jObject = JSONObject(result!!.toString())
                val ProfileResult = jObject.getJSONObject("GetVehicleUpcomingServiceResult")
                val j2 = ProfileResult.getJSONArray("VehicleUpcomingService")
                val gson = Gson()
                val vehicleServiceModelArrayList = ArrayList<VehicleServicesModel>()
                var vehicleServicesModel: VehicleServicesModel
                for (i in 0..j2.length() - 1) {
                    vehicleServicesModel = gson.fromJson(j2.get(i).toString(), VehicleServicesModel::class.java)
                    vehicleServiceModelArrayList.add(vehicleServicesModel)
                }
                vehicleServicesHashMap.put(vinId + "Upcoming", vehicleServiceModelArrayList)


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result!!
        }
    }


    private inner class GetRecommendedServicesTask : AsyncTask<String, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: String): Void? {

            getRecommendedServicesRequest(params[0])
            return null
        }


        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            asyncTaskCount--
            if (asyncTaskCount == 0) {
                vehicleServicesProgressBar.visibility = View.GONE
                initializeUI()
            }
        }
    }

    private fun getRecommendedServicesRequest(vinId: String): String? {
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("VINID", vinId)

            result = Utility.postRequest(getString(R.string.vehicle_recommended_services_url), values)

            val jObject = JSONObject(result!!.toString())
            val ProfileResult = jObject.getJSONObject("GetVehicleRecommendedServiceResult")
            val j2 = ProfileResult.getJSONArray("VehicleRecommendedService")


            val vehicleRecommendedServicesModelArrayList = ArrayList<VehicleServicesModel>()
            var vehicleRecommendedServicesModel: VehicleServicesModel
            for (i in 0..j2.length() - 1) {
                vehicleRecommendedServicesModel = VehicleServicesModel()
                vehicleRecommendedServicesModel.serviceDesc = j2.getJSONObject(i).getString("ServiceDesc")
                vehicleRecommendedServicesModel.serviceDate = j2.getJSONObject(i).getString("ServiceDate")

                vehicleRecommendedServicesModelArrayList.add(vehicleRecommendedServicesModel)
            }

            vehicleServicesHashMap.put(vinId + "Recommended", vehicleRecommendedServicesModelArrayList)

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        return result!!
    }


    internal inner class CallGetVehicleMaintenanceDetails : AsyncTask<String, Void, ArrayList<String>>() {

        var vinId: String = ""

        override fun doInBackground(vararg params: String): ArrayList<String>? {
            vinId = params[0]

            try {
                var result: String? = null
                val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetVehicleMaintenanceDetails?"

                val values = ContentValues()
                values.put("InvoiceID",
                        params[1])

                result = Utility.postRequest(getServerPath, values)
                //Log.dMainActivity.TAG, "" + result!!)

                val aObject = JSONObject(result.toString())
                val jObject = aObject
                        .getJSONObject("GetVehicleMaintenanceDetailsResult")
                val vehicleMaintenanceDetailJsonArray = jObject
                        .getJSONArray("VehicleMaintenanceDetail")

                vehicleMaintenanceDetailModel = Gson().fromJson(
                        vehicleMaintenanceDetailJsonArray.get(0).toString(),
                        VehicleMaintenanceDetailModel::class.java)
                //Log.dMainActivity.TAG,
//                        "model array:" + Arrays.toString(vehicleMaintenanceDetailModel!!
//                                .servicePerformedList))


                //                vehicleMaintenanceHistoryHashMap.put(vinId + "History",new ArrayList<String>(Arrays.asList(vehicleMaintenanceDetailModel.getServicePerformedList())));
                vehicleMaintenanceHistoryDetailsHashMap.put(params[1] + "HistoryDetails", vehicleMaintenanceDetailModel!!)

            } catch (e1: JSONException) {
                e1.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(vinsArray: ArrayList<String>?) {

            asyncTaskCount--
            if (asyncTaskCount == 0) {
                vehicleServicesProgressBar.visibility = View.GONE
                initializeUI()
            }
        }

    }


    private fun getListViewHeight(list: ListView): Int {
        val adapter = list.adapter
        var listviewHeight = 0
        list.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED))

        listviewHeight = list.measuredHeight * adapter.count + adapter.count * list.dividerHeight

        return listviewHeight
    }


    internal inner class CallGetVehicleMaintenanceDetails2 : AsyncTask<String, Void, ArrayList<String>>() {

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String): ArrayList<String>? {
            try {
                var result: String? = null
                val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetVehicleMaintenanceDetails?"

                val values = ContentValues()
                values.put("InvoiceID", params[0])
                Log.v("---------------", " CallGetVehicleMaintenanceDetails2 " + params[0])
                result = Utility.postRequest(getServerPath, values)
                //Log.dMainActivity.TAG, "" + result!!)

                val aObject = JSONObject(result.toString())
                val jObject = aObject
                        .getJSONObject("GetVehicleMaintenanceDetailsResult")
                val vehicleMaintenanceDetailJsonArray = jObject
                        .getJSONArray("VehicleMaintenanceDetail")

                vehicleMaintenanceDetailModel = Gson().fromJson(
                        vehicleMaintenanceDetailJsonArray.get(0).toString(),
                        VehicleMaintenanceDetailModel::class.java)
                //Log.dMainActivity.TAG,
//                        "model array:" + vehicleMaintenanceDetailModel!!
//                                .servicePerformedList.toString())


            } catch (e1: JSONException) {
                e1.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(vinsArray: ArrayList<String>?) {
            showVehicleMaintenanceDetailDialog()
        }

    }


    internal inner class CallGetVehicleMaintenanceByVIN : AsyncTask<String, Void, ArrayList<String>>() {
        var vinId = ""

        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String): ArrayList<String>? {

            vinId = params[0]
            Log.v("---------------", " CallGetVehicleMaintenanceByVIN " + vinId)
            vehicleMaintenanceModelArrayList = ArrayList<VehicleMaintenanceModel>()
            try {
                var result: String? = null
                val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetVehicleMaintenance?"

                val values = ContentValues()
                values.put("VINID", params[0])

                result = Utility.postRequest(getServerPath, values)
                //Log.dMainActivity.TAG, "" + result!!)

                val aObject = JSONObject(result.toString())
                val jObject = aObject
                        .getJSONObject("GetVehicleMaintenanceResult")
                val vehicleMaintenanceJsonArray = jObject
                        .getJSONArray("VehicleMaintenance")

                var vehicleMaintenanceModel: VehicleMaintenanceModel?
                for (x in 0..vehicleMaintenanceJsonArray.length() - 1) {
                    vehicleMaintenanceModel = Gson().fromJson(
                            vehicleMaintenanceJsonArray.get(x).toString(),
                            VehicleMaintenanceModel::class.java)

                    vehicleMaintenanceModelArrayList
                            .add(vehicleMaintenanceModel)
                    vehicleMaintenanceModel = null
                }
            } catch (e1: JSONException) {
                e1.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(vinsArray: ArrayList<String>?) {

            //updateMaintenanceHistoryListView(vehicleMaintenanceModelArrayList);


            val db = DataBaseHelper(activity)
            for (vehicleMaintenanceModel in vehicleMaintenanceModelArrayList) {
                db.addMaintenanceHistory(vehicleMaintenanceModel)
            }

            //Log.dMainActivity.TAG, "with value =" + vinId + "History")
            //Log.dMainActivity.TAG, "" + vehicleMaintenanceModelArrayList.size)
            vehicleMaintenanceHistoryHashMap.put(vinId + "History", vehicleMaintenanceModelArrayList)

            asyncTaskCount--
            Log.v("*********number of tasks", "Number Of Tasks: "+asyncTaskCount)
            if (asyncTaskCount == 0) {
                vehicleServicesProgressBar.visibility = View.GONE
                initializeUI()
            }


            //Log.dMainActivity.TAG, "" + vehicleMaintenanceHistoryHashMap[vinId + "History"]!!.size)


        }

    }

    private fun showVehicleMaintenanceDetailDialog() {
        val servicesStringBuilder = StringBuilder()
        for (service in vehicleMaintenanceDetailModel!!.servicePerformedList) {
            servicesStringBuilder.append(service + ", ")
        }
        val servicesString = servicesStringBuilder.substring(0, servicesStringBuilder.length - 2)
        val vehicleMaintenanceDetailDialog = AlertDialog.Builder(
                activity)
        vehicleMaintenanceDetailDialog.setTitle("Maintenance Details")
        vehicleMaintenanceDetailDialog.setMessage("Mileage was: "
                + vehicleMaintenanceDetailModel!!.mileage
                + "\nService Date: "
                + vehicleMaintenanceDetailModel!!.serviceDate
                + "\nServices Performed:"
                + servicesString)
        vehicleMaintenanceDetailDialog.setPositiveButton("OK", null)
        vehicleMaintenanceDetailDialog.show()
    }

    fun handleOcrResult(ocrResult: String) {
        object : GetVehicleDetailsResolvedFromVinTask(context, ocrResult) {
            override fun onTaskCompleted(result: String) {
                (context as MainActivity).runOnUiThread {
                    if (!result.contains("failed")) {
                        makeValueEditText!!.setText(result.split("!-!".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                        modelValueEditText!!.setText(result.split("!-!".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2])
                        yearValueEditText!!.setText(result.split("!-!".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                        vinValueEditText!!.setText(result.split("!-!".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3])
                        mileageValueEditText!!.requestFocus()
                    }

                    showAddVehicleForm()
                }
            }
        }.execute()
    }

}