package com.inspection.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.adapter.SafetyCheckItemsRecyclerViewAdapter
import com.inspection.model.SafetyCheckItemModel
import com.inspection.model.SafetyCheckItemResultsModel
import com.inspection.serverTasks.EndSafetyCheckReportTask
import com.inspection.serverTasks.GenericServerTask
import com.inspection.serverTasks.GetSafetyCheckListTask
import com.inspection.serverTasks.GetSafetyCheckReportStatusTask
import com.inspection.serverTasks.GetSafetyCheckResultsScaleOptionsTask
import com.inspection.serverTasks.InitiateSafetyCheckProcessTask
import com.inspection.serverTasks.SubmitSafetyCheckItemResultTask
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.View.GONE
import android.widget.*
import com.inspection.MainActivity.PHOTO_CAPTURE_ACTIVITY_REQUEST_ID
import com.inspection.MainActivity.fragmentRequestingPermission

import kotlinx.android.synthetic.main.fragment_safety_check_view.*
import java.util.*


class FragmentSafetyCheckItems : android.support.v4.app.Fragment() {
    private var view2: View? = null
    var customerName = ""
    var customerEmail = ""
    var customerPhoneNumber = ""
    var safetyCheckReportSummaryDescription = ""

    private var safetyCheckItemsModels: ArrayList<SafetyCheckItemModel>? = null
    private var modifiedSafetyCheckItemsModels: ArrayList<SafetyCheckItemModel>? = null

    var selectedQuestionSetID = -1
    var selectedMobileUserProfileID: Int = 0
    var selectVehicleID: Int = 0
    var safetyCheckReportID: Int = 0

    var isSelectedReportCompleted = false
    var vehicleName: String = ""
    var reportDate: String = ""
    private var timer: Timer? = null

    internal var selectedItemForPhoto = -1

    internal var imagesMap = HashMap<String, Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


//        Toast.makeText(context, "vehicle name= " + vehicleName, Toast.LENGTH_SHORT).show()
        view2 = inflater?.inflate(R.layout.fragment_safety_check_view, container, false)




        return view2 as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehicleNameTextView.text = vehicleName
        safetyCheckReportDateValueTextView.text = reportDate
        customerNameTextView.text = customerName
        customerEmailTextView.text = customerEmail
        customerPhoneNumberTextView.text = customerPhoneNumber
        safetyCheckSummaryValueTextView.text = safetyCheckReportSummaryDescription

        safetyCheckItemsRecyclerView.layoutManager = LinearLayoutManager(context)

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        safetyCheckItemsRecyclerView!!.addItemDecoration(itemDecoration)


        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
            summaryCommentIcon.setOnClickListener {
                showChangeSummary()
            }
        } else {
            summaryCommentIcon.visibility = GONE
        }

        safetyCheckItemsListLayout!!.visibility = View.INVISIBLE



        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
            if (safetyCheckReportID > 0) {
                initiateSafetyCheckProcess(safetyCheckReportID)
            } else {
                initiateSafetyCheckProcess(-1)
            }
            endSafetyCheckProcessButton!!.setOnClickListener { endSafetyCheckReport() }

            if (isSelectedReportCompleted) {
                endSafetyCheckProcessButton!!.visibility = View.GONE
            }
        } else {
            initiateSafetyCheckProcess(safetyCheckReportID);
        }


        (activity as MainActivity).supportActionBar!!.title = ApplicationPrefs.getInstance(context).safetyCheckProgramName
    }

    private fun initiateSafetyCheckProcess(uncompleteSafetyCheckReportID: Int) {


        if (uncompleteSafetyCheckReportID == -1) {

            val progressDialog = ProgressDialog(context)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("Loading...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            object : InitiateSafetyCheckProcessTask(context!!, selectedMobileUserProfileID, selectVehicleID, selectedQuestionSetID) {
                override fun onTaskCompleted(result: String) {
                    progressDialog.dismiss()
                    if (!result.contains("-1")) {
                        try {
                            val jsonObject = JSONObject(result.toString())
                            safetyCheckReportID = Integer.parseInt(jsonObject.get("InitiateSafetyCheckProcessResult").toString())
                            isSelectedReportCompleted = false


                            val date = Date() // your date
                            val cal = Calendar.getInstance()
                            cal.time = date
                            val year = cal.get(Calendar.YEAR)
                            val month = cal.get(Calendar.MONTH)
                            val day = cal.get(Calendar.DAY_OF_MONTH)

                            safetyCheckReportDateValueTextView.text = "" + day + "/" + month + "/" + year

                            loadItemsDetails()


                        } catch (jsonExp: JSONException) {
                            jsonExp.printStackTrace()

                        }
                    } else {

                    }
                }
            }.execute()
        } else {
            loadItemsDetails()
        }

    }

    fun loadItemsDetails() {

        val progressDialog = ProgressDialog(context)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        object : GetSafetyCheckListTask(context!!, safetyCheckReportID) {
            override fun onTaskCompleted(result: String) {

                if (result != null) {
                    try {
                        val jsonObject = JSONObject(result.toString())
                        val jsonArray = jsonObject.getJSONArray("GetSafetyCheckListResult")
                        val safetyCheckItemsModels = Gson().fromJson<ArrayList<SafetyCheckItemModel>>(jsonArray.toString(), object : TypeToken<ArrayList<SafetyCheckItemModel>>() {

                        }.type)

                        modifiedSafetyCheckItemsModels = safetyCheckItemsModels
                        val headerPositions = ArrayList<Int>()
                        var categoryName = ""
                        for (x in safetyCheckItemsModels!!.indices) {
                            if (x == 0) {
                                headerPositions.add(0)
                                categoryName = safetyCheckItemsModels!![x].category
                            } else {
                                if (categoryName != safetyCheckItemsModels!![x].category) {
                                    categoryName = safetyCheckItemsModels!![x].category
                                    headerPositions.add(x)
                                }
                            }
                        }

                        for (x in headerPositions.indices.reversed()) {
                            val sf = SafetyCheckItemModel()
                            sf.safetyCheckItemID = -500
                            sf.category = safetyCheckItemsModels!![headerPositions[x]].category
                            modifiedSafetyCheckItemsModels!!.add(headerPositions[x], sf)
                        }

//                        safetyCheckItemsExpandableAdapter = SafetyCheckItemsExpandableAdapter()
//                        safetyCheckExpandableListView!!.setAdapter(safetyCheckItemsExpandableAdapter)
                        safetyCheckItemsListLayout!!.visibility = View.VISIBLE
                        val adapter = SafetyCheckItemsRecyclerViewAdapter(context!!, this@FragmentSafetyCheckItems, safetyCheckItemsRecyclerView!!, modifiedSafetyCheckItemsModels, safetyCheckReportID)
                        safetyCheckItemsRecyclerView!!.adapter = adapter
                        safetyCheckItemsListLayout!!.visibility = View.VISIBLE
                        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
                            endSafetyCheckProcessButton!!.visibility = View.GONE
                            if (isSelectedReportCompleted) {
                                endSafetyCheckProcessButton!!.visibility = View.GONE
                            } else {
                                endSafetyCheckProcessButton!!.visibility = View.VISIBLE
                            }
                        } else {
                            endSafetyCheckProcessButton!!.visibility = View.GONE

                        }

                        if (vehicleNameTextView.text.length == 0) {
                            vehicleNameTextView.text = safetyCheckItemsModels?.get(safetyCheckItemsModels!!.size - 1)!!.MMY
                        }

                    } catch (jsonExp: JSONException) {
                        jsonExp.printStackTrace()
                    }

                }
                progressDialog.dismiss()
            }
        }.execute()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }


//    private var safetyCheckItemsExpandableAdapter: SafetyCheckItemsExpandableAdapter? = null
//
//    private inner class SafetyCheckItemsExpandableAdapter internal constructor() : BaseExpandableListAdapter() {
//
//        internal lateinit var inflater: LayoutInflater
//        internal var fragmentManager = getFragmentManager()
//
//        init {
//            try {
//                inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            } catch (exp: NullPointerException) {
//
//            }
//
//            //Log.dMainActivity.TAG, "" + userVehiclesArrayList.size)
//        }
//
//        override fun getGroupCount(): Int {
//            return modifiedSafetyCheckItemsModels!!.filter { s -> s.safetyCheckItemID == -500 }.count()
//        }
//
//        override fun getChildrenCount(groupPosition: Int): Int {
//            //Returns number of safety check items which are not having item id = -500 (which means it is not a header) and category equals category name of the header
//            return modifiedSafetyCheckItemsModels!!.filter { s -> s.safetyCheckItemID != -500 && s.category.equals(modifiedSafetyCheckItemsModels!!.filter { m -> m.safetyCheckItemID == -500 }[groupPosition].category) }.count()
//        }
//
//        override fun getGroup(groupPosition: Int): Any {
//            return groupPosition
//        }
//
//        override fun getChild(groupPosition: Int, childPosition: Int): Any {
//            return childPosition
//        }
//
//        override fun getGroupId(groupPosition: Int): Long {
//            return groupPosition.toLong()
//        }
//
//        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//            return childPosition.toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return false
//        }
//
//        override fun onGroupExpanded(groupPosition: Int) {
//            super.onGroupExpanded(groupPosition)
//
//            for (x in 0..groupCount - 1) {
//                if (x != groupPosition) {
//                    // vehiclesList!!.collapseGroup(x)
//                }
//            }
//        }
//
//        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
//            var view: View? = convertView
//
//
//            if (view == null) {
//                view = inflater.inflate(R.layout.safety_check_header_layout, null)
//            }
//            view!!.findViewById<TextView>(R.id.safetyCheckHeaderCategoryTextView).text = modifiedSafetyCheckItemsModels!!.filter { s -> s.safetyCheckItemID == -500 }[groupPosition].category
//
//
//            return view!!
//        }
//
//        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
//            var view: View? = convertView
//
//            val categoryItem = modifiedSafetyCheckItemsModels!!.filter { s -> s.safetyCheckItemID == -500 }[groupPosition]
//            val item = modifiedSafetyCheckItemsModels!!.filter { s -> s.safetyCheckItemID != -500 && s.category == categoryItem.category }[childPosition]
//
//            if (view == null) {
//                view = inflater.inflate(R.layout.safety_check_item_layout, null)
//
//            }
//
//            if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
//                addPhotoIcon.setOnClickListener {
//                    selectedItemForPhoto = childPosition
//                    checkFileAndCameraPermissions()
////                    parentView.dispatchTakePictureIntent()
//                }
//
//
//                view!!.findViewById<ImageView>(R.id.addCommentIcon).setOnClickListener {
//                    showAddCommentDialog(childPosition)
//                }
//
//            } else {
//
//
//                view!!.findViewById<ImageView>(R.id.addCommentIcon).visibility = View.INVISIBLE
//                view!!.findViewById<ImageView>(R.id.addPhotoIcon).visibility = View.INVISIBLE
//            }
//
//            view!!.findViewById<TextView>(R.id.safetyCheckItemNameTextView).text = item.item
//            view!!.findViewById<TextView>(R.id.safetyCheckItemResultTextView).text = item.safetyCheckItemResultDescription
//            view!!.findViewById<TextView>(R.id.safetyCheckItemCommentsTextView).text = item.safetyCheckItemComment
//
//            if (item.safetyCheckItemComment != null) {
//                if (item.safetyCheckItemComment.trim { it <= ' ' }.length > 1) {
//                    view!!.findViewById<TextView>(R.id.safetyCheckItemCommentsTextView).text = item.safetyCheckItemComment
//                } else {
//                    view!!.findViewById<TextView>(R.id.safetyCheckItemCommentsTextView).visibility = View.GONE
//                }
//            } else {
//                view!!.findViewById<TextView>(R.id.safetyCheckItemCommentsTextView).visibility = View.GONE
//            }
//
//
//            if (item.safetyCheckItemPhoto != null && item.safetyCheckItemPhoto.length > 2) {
//                val photosString = item.safetyCheckItemPhoto.toString().trim { it <= ' ' }
//                val photosStringArray = Arrays.asList(*photosString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
//                var imageView: ImageView
//                view!!.findViewById<LinearLayout>(R.id.photosLayout).removeAllViews()
//                for (photoName in photosStringArray) {
//                    Log.v("an image", "An image with photo Name: " + photoName)
//                    imageView = ImageView(context)
//                    imageView.layoutParams = LinearLayout.LayoutParams(200, 200)
//                    imageView.setOnClickListener {
//                        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
//                            val alert = AlertDialog.Builder(MainActivity.mContext)
//                            alert.setTitle("Select Action For Photo")
//                            alert.setItems(arrayOf("Show", "Delete")) { dialog, which ->
//                                if (which == 0) {
//                                    LoadImageFromURL(photoName).execute()
//                                } else if (which == 1) {
//                                    object : DeleteSafetyCheckPhotoTask(context, safetyCheckReportID, item.safetyCheckItemID, photoName) {
//                                        override fun onTaskCompleted(result: String) {
//                                            item.safetyCheckItemPhoto = item.safetyCheckItemPhoto.replace(" " + photoName, "").replace(photoName, "")
//                                            notifyDataSetChanged()
//                                        }
//                                    }.execute()
//                                }
//                            }
//                            alert.create().show()
//                        } else {
//                            LoadImageFromURL(photoName).execute()
//                        }
//                    }
//                    LoadImageFromURLAndSetImageView(photoName.replace(" ", ""), imageView).execute()
//                    view!!.findViewById<LinearLayout>(R.id.photosLayout).addView(imageView)
//                }
//
//            } else {
//                view!!.findViewById<LinearLayout>(R.id.photosLayout).visibility = View.GONE
//            }
//
//            view!!.findViewById<TextView>(R.id.safetyCheckItemNameTextView).text = item.item
//            if (item != null && item.safetyCheckItemResultDescription != null && item.safetyCheckItemResultDescription.length > 0) {
//                view!!.findViewById<TextView>(R.id.safetyCheckItemResultTextView).text = item.safetyCheckItemResultDescription
//            } else {
//                view!!.findViewById<TextView>(R.id.safetyCheckItemResultTextView).text = "Not Started"
//            }
//
//            return view!!
//        }
//
//        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//            return false
//        }
//
//        override fun onGroupCollapsed(groupPosition: Int) {
//            super.onGroupCollapsed(groupPosition)
//
//        }
//
//        public inner class ViewHolder {
//            public var id = 0
//            public var textView1: TextView? = null
//            public var textView2: TextView? = null
//        }
//
//    }


    private fun showStartOrEndDialog(selectedItem: Int) {
        val alert = AlertDialog.Builder(MainActivity.mContext)
        alert.setTitle("Select Action For Item")
//        alert.setItems(arrayOf("Start", "Done", "Take Photo")) { dialog, which ->
        alert.setItems(arrayOf("Start", "Done")) { dialog, which ->
            if (which == 0) {
                modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemResultDescription = "In Progress"
                object : SubmitSafetyCheckItemResultTask(context!!, safetyCheckReportID, modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemID, -1, "") {
                    override fun onTaskCompleted(result: String) {
//                        object : SendPushNotificationTask("" + selectedMobileUserProfileID, "Started...", "", "Notification", "Safety Check", "") {
//                            override fun onTaskCompleted(result: String?) {
//
//                            }
//                        }.execute()
                    }
                }.execute()
            } else if (which == 1) {
                showResultsDialog(selectedItem)
            } else {
                selectedItemForPhoto = selectedItem
                checkFileAndCameraPermissions()
//                dispatchTakePictureIntent()
            }

        }
        alert.create().show()

    }

    fun showResultsDialog(selectedItem: Int) {
        object : GetSafetyCheckResultsScaleOptionsTask(context!!, modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckResultScaleId) {
            override fun onTaskCompleted(result: String) {
                if (result != null) {
                    try {
                        val aObject = JSONObject(result.toString())
                        val safetyCheckItemResultsArray = aObject.getJSONArray("GetSafetyCheckResultsScaleOptionsResult")
                        val safetyCheckItemResultsModels = Gson().fromJson<ArrayList<SafetyCheckItemResultsModel>>(safetyCheckItemResultsArray.toString(), object : TypeToken<ArrayList<SafetyCheckItemResultsModel>>() {

                        }.type)

                        val alert = AlertDialog.Builder(MainActivity.mContext)
                        alert.setTitle("Select Result:")
                        val results = arrayOfNulls<String>(safetyCheckItemResultsModels.size)
                        for (x in safetyCheckItemResultsModels.indices) {
                            results[x] = safetyCheckItemResultsModels[x].safetyCheckItemResultDescription
                        }
                        alert.setItems(results) { dialog, which ->
                            val alert = AlertDialog.Builder(MainActivity.mContext)
                            alert.setTitle("Add comment if needed:")
                            val input = EditText(context)
                            input.hint = "No Comment"
                            val lp = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT)
                            input.layoutParams = lp
                            alert.setView(input) // uncomment this line
                            alert.setPositiveButton("Submit") { dialogInterface, i ->
                                (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0)
                                if (input.text != null) {
                                    object : SubmitSafetyCheckItemResultTask(context!!, safetyCheckReportID, modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemID, safetyCheckItemResultsModels[which].safetyCheckItemResultID, input.text.toString()) {
                                        override fun onTaskCompleted(result: String) {
                                            modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemResultDescription = results[which]!!
                                            modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemComment = input.text.toString()
//                                            safetyCheckItemsExpandableAdapter!!.notifyDataSetChanged()
                                        }
                                    }.execute()
                                }
                            }
                            alert.setNegativeButton("Cancel") { dialogInterface, i -> (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0) }
                            alert.create().show()
                        }
                        alert.create().show()
                    } catch (jsonException: JSONException) {
                        jsonException.printStackTrace()
                    }

                }
            }
        }.execute()
    }

    fun showAddCommentDialog(selectedItem: Int) {
        val alert = AlertDialog.Builder(MainActivity.mContext)
        alert.setTitle("Add comment if needed:")
        val input = EditText(context)
        input.hint = "No Comment"
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        alert.setView(input) // uncomment this line
        alert.setPositiveButton("Submit") { dialogInterface, i ->
            (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0)
            if (input.text != null) {
                object : GenericServerTask(context!!, context!!.getString(R.string.submitSafetyCheckItemComment), arrayOf("safetyCheckReportsID", "safetyCheckItemsID", "safetyCheckItemComment"), arrayOf("" + safetyCheckReportID, "" + modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemID, input.text.toString())) {
                    override fun onTaskCompleted(result: String) {
                        modifiedSafetyCheckItemsModels!![selectedItem].safetyCheckItemComment = input.text.toString()
//                        safetyCheckItemsExpandableAdapter!!.notifyDataSetChanged()
                    }
                }.execute()
            }
        }
        alert.setNegativeButton("Cancel") { dialogInterface, i -> (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0) }
        alert.create().show()
    }


    private fun endSafetyCheckReport() {
        object : EndSafetyCheckReportTask(context!!, safetyCheckReportID) {
            override fun onTaskCompleted(result: String) {
                fragmentManager!!.popBackStack()
            }
        }.execute()
    }


    private fun startSafetyCheckTimer() {
        try {
            val task = object : TimerTask() {
                override fun run() {
                    try {
                        if (modifiedSafetyCheckItemsModels != null && modifiedSafetyCheckItemsModels!!.size > 0) {
                            object : GetSafetyCheckReportStatusTask(context!!, safetyCheckReportID) {
                                override fun onTaskCompleted(result: String) {

                                    if (result != null) {
                                        try {
                                            val jsonObject = JSONObject(result.toString())
                                            val jsonArray = jsonObject.getJSONArray("GetSafetyCheckReportStatusResult")
                                            val reportStatusModels = Gson().fromJson<ArrayList<SafetyCheckItemModel>>(jsonArray.toString(), object : TypeToken<ArrayList<SafetyCheckItemModel>>() {

                                            }.type)
                                            for (safetyCheckItemModel in modifiedSafetyCheckItemsModels!!) {
                                                for (safetyCheckItemReportStatusModel in reportStatusModels!!) {
                                                    if (safetyCheckItemModel.safetyCheckItemID == safetyCheckItemReportStatusModel.safetyCheckItemID) {
                                                        safetyCheckItemModel.safetyCheckItemResultDescription = safetyCheckItemReportStatusModel.safetyCheckItemResultDescription
                                                        safetyCheckItemModel.safetyCheckItemPhoto = safetyCheckItemReportStatusModel.safetyCheckItemPhoto
                                                    }
                                                }
                                            }


                                            safetyCheckItemsListLayout!!.visibility = View.VISIBLE
                                            if (reportStatusModels != null && reportStatusModels.size > 0) {

                                            }
                                        } catch (jsonExp: JSONException) {
                                            jsonExp.printStackTrace()
                                        }

                                    }
                                }
                            }.execute()
                        }
                    } catch (e: Exception) {
                        // TODO: handle exception
                        e.printStackTrace()
                    }

                }

            }
            if (timer == null) {
                timer = Timer()
            }
            timer!!.schedule(task, 0, (10 * 1000).toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun showSafetyCheckItemComment(selectedPosition: Int) {
        object : GenericServerTask(context!!, context!!.getString(R.string.getSafetyCheckItemComment), arrayOf("safetyCheckReportsID", "safetyCheckItemsID"), arrayOf("" + safetyCheckReportID, "" + modifiedSafetyCheckItemsModels!![selectedPosition].safetyCheckItemID)) {
            override fun onTaskCompleted(result: String) {
                var result = result
                if (result != null) {
                    if (result.contains("\"GetSafetyCheckItemCommentResult\":\"")) {
                        result = result.replace("{\"GetSafetyCheckItemCommentResult\":\"", "").replace("\"}", "")
                        if (result.length == 0) {
                            result = "No Comment"
                        }
                    } else {
                        result = "No Comment"
                    }
                } else {
                    result = "No Comment"
                }

                val alert = AlertDialog.Builder(MainActivity.mContext)
                alert.setTitle("" + modifiedSafetyCheckItemsModels!![selectedPosition].item + " comment: ")
                alert.setMessage(result)
                alert.setPositiveButton("OK") { dialogInterface, i -> }
                if (modifiedSafetyCheckItemsModels!![selectedPosition].safetyCheckItemPhoto != null && modifiedSafetyCheckItemsModels!![selectedPosition].safetyCheckItemPhoto.length > 0) {
                }

                alert.create().show()

            }
        }.execute()
    }

    internal inner class LoadImageFromURL(var fileName: String) : AsyncTask<Void, Void, Void>() {
        var progressDialog: ProgressDialog

        init {
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Loading Photo...")
            progressDialog.isIndeterminate = true
            progressDialog.show()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            try {

                val url = URL("" + context!!.getString(R.string.safetyCheckPhotosURL) + fileName)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                saveBitmapToFile(bmp, fileName)
            } catch (exp: MalformedURLException) {
                exp.printStackTrace()
            } catch (exp: IOException) {
                exp.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            progressDialog.dismiss()
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val imagePath = File(context!!.cacheDir, "images")
            val newFile = File(imagePath, fileName)
            val contentUri = FileProvider.getUriForFile(context!!, "com.matics.android.fileprovider", newFile)
            intent.setDataAndType(contentUri, context!!.contentResolver.getType(contentUri))
            startActivity(intent)
        }
    }


    internal inner class LoadImageFromURLAndSetImageView(var photoName: String, var imageView: ImageView) : AsyncTask<Void, Void, Void>() {
        var progressDialog: ProgressDialog? = null
        var bmp: Bitmap? = null

        override fun doInBackground(vararg voids: Void): Void? {
            try {

                val url = URL("" + context!!.getString(R.string.safetyCheckThumbsURL) + photoName.replace("jpg", "thumb.jpg"))
                if (!imagesMap.containsKey(url.toString())) {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    imagesMap.put(url.toString(), bmp as Bitmap)
                } else {
                    bmp = imagesMap[url.toString()] as Bitmap
                }

                //saveBitmapToFile(bmp, modifiedSafetyCheckItemsModels.get(photoName).getSafetyCheckItemPhoto());
            } catch (exp: MalformedURLException) {
                exp.printStackTrace()
            } catch (exp: IOException) {
                exp.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            imageView.setImageBitmap(bmp)

        }
    }

    private fun saveBitmapToFile(bmp: Bitmap, fileName: String) {
        var out: FileOutputStream? = null
        try {

            val cachePath = File(context!!.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            out = FileOutputStream("" + cachePath + "/" + fileName)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                // Validate how to get image from handset
                // Define permission for Storage & File
            } catch (ex: IOException) {
                // handle exception
                ex.printStackTrace()
            }

            if (photoFile != null) {
                var photoURI = FileProvider.getUriForFile(context!!, "com.matics.android.fileprovider", File(photoFile.absolutePath));
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoFile.absolutePath)))
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, PHOTO_CAPTURE_ACTIVITY_REQUEST_ID)
            }
        }
    }

    internal var mCurrentPhotoPath = ""
    internal var mCurrentThumbPath = ""
    internal var mCurrentFileName = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name


        mCurrentFileName = "" + safetyCheckReportID + "-" + selectedItemForPhoto + "-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR) + "-" + Calendar.getInstance().get(Calendar.MINUTE) + "-" + Calendar.getInstance().get(Calendar.SECOND)


        val cachePath = File(context!!.cacheDir, "images")
        cachePath.mkdirs() // don't forget to make the directory
        val storageDir = File("" + cachePath + "/" + mCurrentFileName)


//        val image = File.createTempFile(
//                mCurrentFileName, /* prefix */
//                "", /* suffix */
//                storageDir      /* directory */
//        )
//
//        val thumb = File.createTempFile(
//                mCurrentFileName, /* prefix */
//                "", /* suffix */
//                storageDir      /* directory */
//        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = storageDir.absolutePath
        mCurrentThumbPath = storageDir.absolutePath

        return storageDir
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_CAPTURE_ACTIVITY_REQUEST_ID && resultCode == RESULT_OK) {
            uploadPhotoTask(mCurrentPhotoPath, false).execute()
            val thumbBitmap = getThumbnailBitmap(mCurrentPhotoPath)
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(mCurrentThumbPath)
                thumbBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) // bmp is your Bitmap instance
                uploadPhotoTask(mCurrentThumbPath, true).execute()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (out != null) {
                        out.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    }

    private fun getThumbnailBitmap(path: String): Bitmap? {
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth == -1 || bounds.outHeight == -1) {
            return null
        }
        val originalSize = if (bounds.outHeight > bounds.outWidth)
            bounds.outHeight
        else
            bounds.outWidth
        val opts = BitmapFactory.Options()
        opts.inSampleSize = 4
        return BitmapFactory.decodeFile(path, opts)
    }

    internal inner class uploadPhotoTask(var filePath: String, var isThumb: Boolean) : AsyncTask<Void, Void, Void>() {
        var progressDialog: ProgressDialog

        init {
            progressDialog = ProgressDialog(context)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("Uploading Photo...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            uploadFile(filePath, isThumb)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
//            super.onPostExecute(aVoid)
            progressDialog.dismiss()
//            safetyCheckItemsExpandableAdapter!!.notifyDataSetChanged()
        }
    }

    private fun uploadFile(sourceFileUri: String, isThumb: Boolean): Any {

        val fileName = sourceFileUri
        var conn: HttpURLConnection? = null
        var dos: DataOutputStream? = null
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1 * 1024 * 1024
        val sourceFile = File(sourceFileUri)
        if (!sourceFile.isFile) {
            return ""
        } else {
            try {
                val fileInputStream = FileInputStream(sourceFile)
                val url: URL
                if (isThumb) {
                    url = URL("http://www.jet-matics.com/JetComService/JetCom.svc/UploadSafetyCheckItemThumbPhoto?fileName=$mCurrentFileName.thumb.jpg")
                } else {
                    Log.v("safetyCheckReportID", "" + safetyCheckReportID)
                    Log.v("mCurrentFileName", "" + mCurrentFileName)
                    Log.v("safetycheckitemsmodels", "" + modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemID)
                    url = URL("http://www.jet-matics.com/JetComService/JetCom.svc/UploadSafetyCheckItemPhoto?safetyCheckItemsID=" + modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemID + "&safetyCheckReportsID=" + safetyCheckReportID + "&fileName=" + mCurrentFileName + ".jpg")
                }
                // Open a HTTP  connection to  the URL
                conn = url.openConnection() as HttpURLConnection
                conn.doInput = true // Allow Inputs
                conn.doOutput = true // Allow Outputs
                conn.useCaches = false // Don't use a Cached Copy
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                //                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("content-type", "mapplication/x-www-form-urlencoded")
                if (isThumb) {
                    conn.setRequestProperty("uploaded_file", mCurrentFileName + ".thumb")
                } else {
                    conn.setRequestProperty("uploaded_file", mCurrentFileName)
                }

                dos = DataOutputStream(conn.outputStream)
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }

                fileInputStream.close()
                dos.flush()
                dos.close()
                Log.v("file uploaded", "file uploaded")
                if (isThumb) {
                    modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemPhoto = modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemPhoto + " " + mCurrentFileName + ".jpg"
                }

                if (modifiedSafetyCheckItemsModels != null && modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemResultDescription != null && modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemResultDescription.length > 0) {

                } else {
                    modifiedSafetyCheckItemsModels!![selectedItemForPhoto].safetyCheckItemResultDescription = "In Progress"
                }
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
                //Log.e("Upload file to server", "error: " + ex.message, ex)
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e("Upload file to server", "Exception : " + e.message, e)
            }

        }  // End else block
        return ""
    }


    fun recyclerViewListClicked(v: View?, position: Int) {
//        showStartOrEndDialog(position)
        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
            if (!isSelectedReportCompleted) {
                showStartOrEndDialog(position)
            } else {
                showChangeToComplete()
            }
        }
    }

    fun showChangeToComplete() {
        val alert = AlertDialog.Builder(MainActivity.mContext)
        alert.setTitle("Note")
        alert.setMessage("Applying any action on any item will set report as InProgress.")
        alert.setPositiveButton("Proceed", { dialog, which ->
            object : GenericServerTask(context!!, context!!.getString(R.string.updateSafetyCheckReportStatus), arrayOf("reportId", "statusId"), arrayOf("" + safetyCheckReportID, "2")) {
                override fun onTaskCompleted(result: String) {
                    val confirmationAlert = AlertDialog.Builder(context)
                    confirmationAlert.setMessage("Safety Check Report Status Updated")
                    confirmationAlert.setPositiveButton("OK", null)
                    confirmationAlert.show()
                }

            }.execute()
        })
        alert.setNegativeButton("Cancel", { dialog, which ->
            Toast.makeText(context, "Cancel Clicked", Toast.LENGTH_SHORT).show()
        })
        alert.create().show()
    }

    fun showChangeSummary() {
        val alert = AlertDialog.Builder(MainActivity.mContext)
        alert.setTitle("Set Summary:")
        val input = EditText(context)
        input.hint = ""
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        alert.setView(input) // uncomment this line
        alert.setPositiveButton("Submit") { dialogInterface, i ->
            (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0)
            if (input.text != null) {
                object : GenericServerTask(context!!, context!!.getString(R.string.submitSafetyCheckReportSummaryDescription), arrayOf("safetyCheckReportsID", "safetyCheckReportSummaryDescription"), arrayOf("" + safetyCheckReportID, input.text.toString())) {
                    override fun onTaskCompleted(result: String) {
                        safetyCheckSummaryValueTextView.text = input.text.toString()
                    }
                }.execute()
            }
        }
        alert.setNegativeButton("Cancel") { dialogInterface, i -> (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0) }
        alert.create().show()
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

    fun checkFileAndCameraPermissions() =
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                Log.v("PERMISSION REQUEST: ", "START")
                fragmentRequestingPermission = "FragmentSafetyCheckItems"
                if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 1)
                } else {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            } else {
                dispatchTakePictureIntent()
            }
}