package com.inspection.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.R.id.*;
import com.inspection.Utils.*
import com.inspection.Utils.Constants.uploadPhoto
import com.inspection.model.*


import kotlinx.android.synthetic.main.fragment_aarav_photos.*
import org.json.XML
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVPhotos.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVPhotos.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVPhotos : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var tblFacilityPhotos = ArrayList<PRGFacilityPhotos>()
    var photoBitmap: Bitmap? = null
    var photoThumbnailBitmap: Bitmap? = null

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
        return inflater.inflate(R.layout.fragment_aarav_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        browseBtn.setOnClickListener {
//            dispatchTakePictureIntent()
//        }

        loadFacilityPhotos()



        addNewPhoto.setOnClickListener {
            photosLoadingView.visibility = View.VISIBLE
            addNewPhotoDialog.visibility = View.VISIBLE
        }

        addNewPhotoPickPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission((activity as FormsActivity),
                            Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((activity as FormsActivity),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)

            } else {
                context!!.toast("Please make sure camera and storage permissions are granted")
            }
        }

        addNewPhotoCapturePhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission((activity as FormsActivity),
                            Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((activity as FormsActivity),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()

            } else {
                context!!.toast("Please make sure camera and storage permissions are granted")
            }
        }

        addNewPhotoConfirmButton.setOnClickListener {
            addNewPhotoDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
            val file = File(loadedImage.tag.toString())
            (activity as MainActivity).uploadPhoto(file,"1")
        }

        addNewPhotoCancelButton.setOnClickListener {
            addNewPhotoDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }


        photosPreviewDialogCloseButton.setOnClickListener {
            photosPreviewDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }

        IndicatorsDataModel.getInstance().tblPhotos[0].visited = true
        photosTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }


    fun loadFacilityPhotos(){
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos,
//                Response.Listener { response ->
//                    Log.v("asd","asdsa")
//                    activity!!.runOnUiThread {
//                        Log.v("VT RESPONSE ||| ", response.toString())
////                        if (!response.toString().contains("[ ]", false)) {
////                            var obj = XML.toJSONObject(response.toString())
//////                            var jsonObj = obj.getJSONObject("responseXml")
////                            tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(obj.toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
////                        } else {
////                            var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
////                            Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + errorMessage + " )")
////                        }
//                    }
//                }, Response.ErrorListener {
//            Utility.showSubmitAlertDialog(activity,false,"Visitation Tracking (Error: "+it.message+" )")
//        }))
        photoLoadingView.visibility = View.VISIBLE
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + "",
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        tblFacilityPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())
                        fillPhotosTableView()
                    }
                }, Response.ErrorListener {
                Log.v("Loading error", "" + it.message)
                photoLoadingView.visibility = View.GONE
                it.printStackTrace()
        }))
    }


    fun addTableRow() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT
        var tableRow = TableRow(context)
        var imageView = ImageView(context)
        imageView.setImageBitmap(photoThumbnailBitmap)
        Glide.with(this).load(Constants.getImages).into(imageView);
        imageView.setOnClickListener {
//            photosLoadingView.visibility = View.VISIBLE
//            photosPreviewDialog.visibility = View.VISIBLE
//
//
//            photosPreviewImageView.setImageBitmap(imageView.toBitmap())
        }
        tableRow.setPadding(0,4,0,4)
        tableRow.addView(imageView)

        var textView = TextView(context)
        textView.layoutParams = rowLayoutParam
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.setText("Demo")

        tableRow.addView(textView)

        photosTableLayout.addView(tableRow)

        imageView.layoutParams.height = 60
        imageView.layoutParams.width = 160
        imageView.requestLayout()
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
                photoFile?.also {
                    var photoURI = FileProvider.getUriForFile(context!!, "com.inspection.android.fileprovider", File(it.absolutePath))
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, MainActivity.PHOTO_CAPTURE_ACTIVITY_REQUEST_ID)
                }
            }
        }
    }


    internal var mCurrentPhotoPath = ""
    internal var mCurrentThumbPath = ""
    internal var mCurrentFileName = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        mCurrentFileName = "" + Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR) + "-" + Calendar.getInstance().get(Calendar.MINUTE) + "-" + Calendar.getInstance().get(Calendar.SECOND)
        val cachePath = File(context!!.cacheDir, "images")
        cachePath.mkdirs() // don't forget to make the directory
//        val storageDir = File("" + cachePath + "/" + mCurrentFileName)
//        val storageDir = File("" + cachePath)
        val storageDir: File = (context as Context).getExternalFilesDir(Environment.DIRECTORY_PICTURES)

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

//        return storageDir
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.PHOTO_CAPTURE_ACTIVITY_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            photoBitmap = getBitmapWithPath(mCurrentPhotoPath, false)
            photoThumbnailBitmap = getBitmapWithPath(mCurrentThumbPath, true)
//            addNewPhotoDialog.visibility = View.GONE
            loadedImage.setImageBitmap(photoBitmap)
            loadedImage.tag = mCurrentPhotoPath
//            photosPreviewDialog.visibility = View.VISIBLE
//            addTableRow()
//            uploadPhotoTask(mCurrentPhotoPath, false).execute()
//            val thumbBitmap = getThumbnailBitmap(mCurrentPhotoPath)
//            var out: FileOutputStream? = null
//            try {
//                out = FileOutputStream(mCurrentThumbPath)
//                thumbBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out) // bmp is your Bitmap instance
//                uploadPhotoTask(mCurrentThumbPath, true).execute()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    if (out != null) {
//                        out.close()
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
        } else if (requestCode == 234 && resultCode == Activity.RESULT_OK) {
            context!!.toast("Image picked successfully")
            var uri = data!!.data

        try {
            var bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri);
            // Log.d(TAG, String.valueOf(bitmap));
            photoBitmap = bitmap
            photoThumbnailBitmap = bitmap
//            addNewPhotoDialog.visibility = View.GONE
            loadedImage.setImageBitmap(photoBitmap)
            loadedImage.tag = uri
//            photosPreviewDialog.visibility = View.VISIBLE
//            addTableRow()
        } catch (e : IOException) {
            e.printStackTrace();
        }
        }

    }


    private fun getBitmapWithPath(path: String, isThumb: Boolean): Bitmap? {
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
//        opts.inSampleSize = 4
        return BitmapFactory.decodeFile(path, opts)
    }


    fun fillPhotosTableView(){
        photoLoadingView.visibility = View.VISIBLE
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 5
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.5F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.8F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 0.8F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER

        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 0
        rowLayoutParam7.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam8 = TableRow.LayoutParams()
        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.width = 0
        rowLayoutParam8.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam9 = TableRow.LayoutParams()
        rowLayoutParam9.weight = 1.5F
        rowLayoutParam9.column = 9
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.width = 0
        rowLayoutParam9.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam10 = TableRow.LayoutParams()
        rowLayoutParam10.weight = 0.8F
        rowLayoutParam10.column = 10
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.width = 0
        rowLayoutParam10.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = 80

        tblFacilityPhotos.apply {
            (0 until size).forEach {
                if (get(it).photoid>0) {

                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    if (get(it).filename.isNullOrEmpty())
                    tableRow.minimumHeight = if (get(it).filename.isNullOrEmpty()) 30 else 60
                    tableRow.setPadding(0,4,0,4)

                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }


                    val imageView = ImageView(context)
                    imageView.layoutParams = rowLayoutParam
                    imageView.setPadding(10,0,10,0)
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    imageView.isClickable = true
                    Glide.with(this@FragmentAARAVPhotos).load(Constants.getImages+get(it).filename).into(imageView);
                    if (!get(it).filename.isNullOrEmpty()) {
                        imageView.setOnClickListener { innerIt ->
                            Glide.with(this@FragmentAARAVPhotos).load(Constants.getImages + get(it).filename).into(photosEnlargeImageView);
//                            photosEnlargeDialog.requestLayout()
                            closeImageView.setOnClickListener {
                                photosEnlargeDialog.visibility = View.GONE
                            }
                            photosEnlargeDialog.visibility = View.VISIBLE
                        }
                    }
                    tableRow.addView(imageView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.minimumHeight = 30
                    textView1.text = get(it).filename
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.minimumHeight = 30
                    textView2.text = get(it).filedescription
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.minimumHeight = 30
                    textView3.text = get(it).lastupdateby
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER

                    textView4.minimumHeight = 30
                    if (get(it).approveddate.isNullOrEmpty()) textView4.text = ""
                    else textView4.text = if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView4)

                    val checkBox1 = CheckBox(context)
                    checkBox1.layoutParams = rowLayoutParam5
                    checkBox1.gravity = Gravity.CENTER
                    checkBox1.minimumHeight = 30
                    checkBox1.isClickable = false
                    checkBox1.isChecked = get(it).approvalrequested
                    tableRow.addView(checkBox1)

                    val checkBox2 = CheckBox(context)
                    checkBox2.layoutParams = rowLayoutParam6
                    checkBox2.gravity = Gravity.CENTER_HORIZONTAL
                    checkBox2.minimumHeight = 30
                    checkBox2.isChecked = get(it).approved
                    checkBox1.isClickable = false
                    tableRow.addView(checkBox2)

                    val textView5 = TextView(context)
                    textView5.layoutParams = rowLayoutParam7
                    textView5.gravity = Gravity.CENTER
                    textView5.minimumHeight = 30
                    textView5.text = get(it).approvedby
                    tableRow.addView(textView5)

                    val textView6 = TextView(context)
                    textView6.layoutParams = rowLayoutParam8
                    textView6.gravity = Gravity.CENTER
                    textView6.minimumHeight = 30
                    if (get(it).approveddate.isNullOrEmpty()) textView6.text = ""
                    else textView6.text = if (get(it).approveddate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).approveddate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView6)

                    val textView7 = TextView(context)
                    textView7.layoutParams = rowLayoutParam9
                    textView7.gravity = Gravity.CENTER_VERTICAL
                    textView7.minimumHeight = 30
                    textView7.text = get(it).downstreamapps
                    tableRow.addView(textView7)

                    val editButton = Button(context)
                    editButton.layoutParams = rowLayoutParam10
                    editButton.setTextColor(Color.BLUE)
                    editButton.text = "EDIT"
                    editButton.minimumHeight = 30
                    editButton.isEnabled=true
                    editButton.gravity = Gravity.CENTER
                    editButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(editButton)


                    editButton.setOnClickListener {
//                        edit_afDtlseffective_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.equals("") || FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else  FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.apiToAppFormatMMDDYYYY())
//                        edit_afDtlsexpiration_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.equals("") || FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else  FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.apiToAppFormatMMDDYYYY())
//                        edit_affcomments_editTextVal.setText(FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment)
//                        edit_affiliations_textviewVal.setSelection(edit_affTypesArray.indexOf(textView.text.toString()))
//                        if (textView1.text.isNotEmpty()) {
//                            edit_afDetails_textviewVal.setSelection(edit_affTypesDetailsArray.indexOf(textView1.text.toString()))
//                        }
//                        edit_afDetails_textviewVal.tag=textView1.text.toString()
//                        edit_afDtlseffective_date_textviewVal.setError(null)
//                        edit_affiliationsCard.visibility = View.VISIBLE
//                        (activity as FormsActivity).overrideBackButton = true
//                        alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE
//
//                        var childViewCount = mainAffTableLayout.getChildCount();
//
                    }
//                    edit_submitNewAffil.setOnClickListener {
//
//                        if (validateInputsForUpdate()) {
//                            progressBarText.text = "Saving ..."
//                            affLoadingView.visibility = View.VISIBLE
//                            var startDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
//                            var endDate = if (edit_afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlsexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
//                            var comment = edit_affcomments_editTextVal.text.toString()
////
//                            var affTypeID = TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AffiliationTypeName.equals(edit_affiliations_textviewVal.selectedItem.toString()) }[0].AARAffiliationTypeID
//                            var affDetailID = if (edit_afDetails_textviewVal.selectedItem != null) TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(edit_afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID else "0"
//                            var affiliationID = if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID>-1) FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID else ""
//                            indexToRemove = rowIndex
//                            Log.v("AFFILIATION EDIT --- ", Constants.UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}")
//                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}",
//                                    Response.Listener { response ->
//                                        activity!!.runOnUiThread {
//                                            if (response.toString().contains("returnCode>0<",false)) {
//                                                Utility.showSubmitAlertDialog(activity, true, "Affiliation")
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment= comment
//                                                affLoadingView.visibility = View.GONE
//                                                progressBarText.text = "Loading ..."
//                                                fillAffTableView()
//                                                altLocationTableRow(2)
//                                                HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
//                                                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
//                                            } else {
//                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
//                                                Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
//                                            }
//                                            affLoadingView.visibility = View.GONE
//                                            (activity as FormsActivity).overrideBackButton = false
//                                            edit_affiliationsCard.visibility = View.GONE
//                                            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
//                                        }
//                                    }, Response.ErrorListener {
//                                Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
//                                affLoadingView.visibility = View.GONE
//                                (activity as FormsActivity).overrideBackButton = false
//                                edit_affiliationsCard.visibility = View.GONE
//                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
//                            }))
//                        } else
//                            Utility.showValidationAlertDialog(activity,"Please fill all the required activity")
//                    }
                    photosTableLayout.addView(tableRow)
                }
            }
        }
        photoLoadingView.visibility = View.GONE

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVPhotos.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVPhotos().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
