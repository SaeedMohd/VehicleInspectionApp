package com.inspection.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
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
import com.inspection.Utils.Utility.getPath
import com.inspection.adapter.MultipartRequest
import com.inspection.model.*


import kotlinx.android.synthetic.main.fragment_aarav_photos.*
import org.json.XML
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
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
    var fileSuffix = ""
    var fileprefix = ""

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
        fileprefix = "FACID" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "CC" + FacilityDataModel.getInstance().clubCode
//        browseBtn.setOnClickListener {
//            dispatchTakePictureIntent()
//        }

//        loadFacilityPhotos()
        tblFacilityPhotos = PRGDataModel.getInstance().tblPRGFacilitiesPhotos
        fillPhotosTableView()


        fileNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                fileNameTitle.text = fileprefix + p0 + fileSuffix
            }
        })

        addNewPhoto.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = true
            enableControls(false)
            fileDescText.setText("")
            fileNameText.setText("")
            fileNameTitle.text = ""
            clubCHeck.isChecked = false
            commCheck.isChecked = false
            modCheck.isChecked = false
            irasCheck.isChecked = false
            rspCheck.isChecked = false
            envCheck.isChecked = false
            approvalReqCheck.isChecked = false
            loadedImage.setImageBitmap(null)
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
                requireContext().toast("Please make sure camera and storage permissions are granted")
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
                requireContext().toast("Please make sure camera and storage permissions are granted")
            }
        }

        addNewPhotoConfirmButton.setOnClickListener {
            val file = File(loadedImage.tag.toString())
//            (activity as FormsActivity).
            if (validateInputs()) {
                progressBarText.text = "Uploading Photo ..."
                photoLoadingView.visibility = View.VISIBLE
                uploadPhoto(file, fileNameTitle.text.toString())
//                photosLoadingView.visibility = View.GONE
            } else {
                Utility.showValidationAlertDialog(activity, "Please fill all the required activity")
            }

        }

        addNewPhotoCancelButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            addNewPhotoDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }

        editPhotoCancelButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            editPhotoDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }

        photosPreviewDialogCloseButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            photosPreviewDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }


        IndicatorsDataModel.getInstance().tblPhotos[0].visited = true
        photosTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }


    fun loadFacilityPhotos() {
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
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}",
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        tblFacilityPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())
//                        Utility.showMessageDialog(context,"ajshd","COUNT ---> "+tblFacilityPhotos.size)
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
        tableRow.setPadding(0, 4, 0, 4)
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
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
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
                    var photoURI = FileProvider.getUriForFile(requireContext(), "com.inspection.android.fileprovider", File(it.absolutePath))
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
        val cachePath = File(requireContext().cacheDir, "images")
        cachePath.mkdirs() // don't forget to make the directory
//        val storageDir = File("" + cachePath + "/" + mCurrentFileName)
//        val storageDir = File("" + cachePath)
        val storageDir: File = (context as Context).getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

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
            fileSuffix = loadedImage.tag.toString().substring(loadedImage.tag.toString().lastIndexOf("."))
            fileNameTitle.text = fileprefix + fileNameText.text + fileSuffix
            enableControls(true)
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
            requireContext().toast("Image picked successfully")
            var uri = data!!.data

            try {
                var bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri);

                // Log.d(TAG, String.valueOf(bitmap));
                photoBitmap = bitmap
                photoThumbnailBitmap = bitmap
//            addNewPhotoDialog.visibility = View.GONE
                loadedImage.setImageBitmap(photoBitmap)
//                loadedImage.tag = getPathFromURI(uri)
                loadedImage.tag = getPath(context,uri)
                fileSuffix = loadedImage.tag.toString().substring(loadedImage.tag.toString().lastIndexOf("."))
                fileNameTitle.text = fileprefix + fileNameText.text + fileSuffix
                enableControls(true)
//            photosPreviewDialog.visibility = View.VISIBLE
//            addTableRow()
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }

    }

    fun enableControls(enable: Boolean) {
        fileNameText.isEnabled = enable
        fileDescText.isEnabled = enable
        clubCHeck.isEnabled = enable
        commCheck.isEnabled = enable
        modCheck.isEnabled = enable
        envCheck.isEnabled = enable
        irasCheck.isEnabled = enable
        rspCheck.isEnabled = enable
        approvalReqCheck.isEnabled = enable
    }

    fun getPathFromURI(contentUri: Uri): String {
        var res = ""
        val wholeID = DocumentsContract.getDocumentId(contentUri);
        // Split at colon, use second item in the array

        val id = wholeID.split(":")[1];
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?";
//        val cursor = context!!.contentResolver.query(contentUri, proj, null, null, null);
        val cursor = requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            res = contentUri.getPath().toString();
        } else {
            if (cursor.moveToFirst()) {
                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res
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


    fun fillPhotosTableView() {

        photoLoadingView.visibility = View.VISIBLE
        if (photosTableLayout.childCount > 1) {
            for (i in photosTableLayout.childCount - 1 downTo 1) {
                photosTableLayout.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 5
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL

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
        rowLayoutParam5.gravity = Gravity.CENTER_HORIZONTAL

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 0.8F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER_HORIZONTAL

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
                if (get(it).photoid > -1) {

                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    if (get(it).filename.isNullOrEmpty())
                        tableRow.minimumHeight = if (get(it).filename.isNullOrEmpty()) 30 else 60
                    tableRow.setPadding(0, 4, 0, 4)

                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }
                    val imageView = ImageView(context)
                    imageView.layoutParams = rowLayoutParam
                    imageView.setPadding(10, 0, 10, 0)
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    imageView.isClickable = true
                    Glide.with(this@FragmentAARAVPhotos).load(Constants.getImages + get(it).filename).into(imageView);
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
                    textView1.textSize = 14f
                    tableRow.addView(textView1)




                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.minimumHeight = 30
                    textView2.text = get(it).filedescription
                    textView2.textSize = 14f
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.minimumHeight = 30
                    textView3.text = get(it).lastupdateby
                    textView3.textSize = 14f
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.minimumHeight = 30
                    if (get(it).approveddate.isNullOrEmpty()) textView4.text = ""
                    else textView4.text = if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView4)

                    val checkBox1 = CheckBox(context)
                    checkBox1.layoutParams = rowLayoutParam5
                    checkBox1.gravity = Gravity.CENTER
                    checkBox1.minimumHeight = 30
                    checkBox1.isClickable = false
                    checkBox1.textSize = 14f
                    checkBox1.isChecked = get(it).approvalrequested
                    tableRow.addView(checkBox1)

                    val checkBox2 = CheckBox(context)
                    checkBox2.layoutParams = rowLayoutParam6
                    checkBox2.gravity = Gravity.CENTER
                    checkBox2.minimumHeight = 30
                    checkBox2.isChecked = get(it).approved
                    checkBox2.isClickable = false
                    checkBox2.textSize = 14f
                    tableRow.addView(checkBox2)

                    val textView5 = TextView(context)
                    textView5.layoutParams = rowLayoutParam7
                    textView5.gravity = Gravity.CENTER
                    textView5.minimumHeight = 30
                    textView5.text = get(it).approvedby
                    textView5.textSize = 14f
                    tableRow.addView(textView5)

                    val textView6 = TextView(context)
                    textView6.layoutParams = rowLayoutParam8
                    textView6.gravity = Gravity.CENTER
                    textView6.minimumHeight = 30
                    textView5.textSize = 14f
                    if (get(it).approveddate.isNullOrEmpty()) textView6.text = ""
                    else textView6.text = if (get(it).approveddate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).approveddate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView6)

                    val textView7 = TextView(context)
                    textView7.layoutParams = rowLayoutParam9
                    textView7.gravity = Gravity.CENTER_VERTICAL
                    textView7.minimumHeight = 30
                    textView7.text = get(it).downstreamapps
                    textView5.textSize = 14f
                    tableRow.addView(textView7)

                    val editButton = Button(context)
                    editButton.layoutParams = rowLayoutParam10
                    editButton.setTextColor(Color.BLUE)
                    editButton.text = "EDIT"
                    editButton.minimumHeight = 30
                    editButton.isEnabled = true
                    editButton.textSize = 12f
                    editButton.gravity = Gravity.CENTER
                    editButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(editButton)


                    editButton.setOnClickListener {
                        (activity as FormsActivity).overrideBackButton = true
                        editFileDescText.setText(textView2.text)
                        editFileNameText.setText(textView1.text)
                        editFileNameTitle.text = ""
                        editClubCHeck.isChecked = textView7.text.contains("Club Hub/MRM")
                        editCommCHeck.isChecked = textView7.text.contains("eComm")
                        editModCheck.isChecked = textView7.text.contains("Envision")
                        editIrasCheck.isChecked = textView7.text.contains("MOD")
                        editRspCheck.isChecked = textView7.text.contains("IRAS")
                        editEnvCheck.isChecked = textView7.text.contains("RSP")
                        editApprovalReqCheck.isChecked = checkBox1.isChecked
                        photosLoadingView.visibility = View.VISIBLE
                        editPhotoDialog.visibility = View.VISIBLE
                        var currentTableRowIndex = photosTableLayout.indexOfChild(tableRow)
                        var currentPhotoIndex = currentTableRowIndex - 1



                        editPhotoConfirmButton.setOnClickListener {

                            if (validateEditsInputs()) {
                                progressBarText.text = "Saving ..."
                                photoLoadingView.visibility = View.VISIBLE
                                progressBarText.text = "Saving Photo Details ..."
                                photoLoadingView.visibility = View.VISIBLE

                                var photoID = tblFacilityPhotos[currentPhotoIndex].photoid
                                var fileDescStr = editFileDescText.text.toString()
                                var approvalReq = if (editApprovalReqCheck.isChecked) 1 else 0
                                var downstreamStr = ""
                                if (editClubCHeck.isChecked) downstreamStr += clubCHeck.text.toString() + ", "
                                if (editCommCHeck.isChecked) downstreamStr += commCheck.text.toString() + ", "
                                if (editEnvCheck.isChecked) downstreamStr += envCheck.text.toString() + ", "
                                if (editModCheck.isChecked) downstreamStr += modCheck.text.toString() + ", "
                                if (editIrasCheck.isChecked) downstreamStr += irasCheck.text.toString() + ", "
                                if (editRspCheck.isChecked) downstreamStr += rspCheck.text.toString() + ", "
                                downstreamStr = downstreamStr.removeSuffix(", ")
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotos + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&operation=EDIT&downstreamApps=${downstreamStr}&LastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=&fileDescription=${fileDescStr}&photoId=${photoID}&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 1, getPhotosChanges(1,currentPhotoIndex)),
                                        Response.Listener { response ->
                                            requireActivity().runOnUiThread {
                                                if (response.toString().contains("Success", false)) {
                                                    Utility.showSubmitAlertDialog(activity, true, "Photos")
//                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].filedescription = fileDescStr
//                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].approvalrequested = editApprovalReqCheck.isChecked
//                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].downstreamapps = downstreamStr
//                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].lastupdatedate= Date().toApiSubmitFormat()
                                                    tblFacilityPhotos[currentPhotoIndex].filedescription = fileDescStr
                                                    tblFacilityPhotos[currentPhotoIndex].approvalrequested = editApprovalReqCheck.isChecked
                                                    tblFacilityPhotos[currentPhotoIndex].downstreamapps = downstreamStr
                                                    tblFacilityPhotos[currentPhotoIndex].lastupdatedate= Date().toApiSubmitFormat()
//                                                    loadFacilityPhotos()
                                                    fillPhotosTableView()
                                                    HasChangedModel.getInstance().groupPhoto[0].Photos= true
                                                    HasChangedModel.getInstance().changeDoneForPhotoDef()
                                                } else {
//                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                    Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + response.toString() + " )")
                                                }
                                                photoLoadingView.visibility = View.GONE
                                                photosLoadingView.visibility = View.GONE
                                                progressBarText.text = "Loading ..."
                                                editPhotoDialog.visibility = View.GONE
                                                (activity as FormsActivity).overrideBackButton = false
                                            }
                                        }, Response.ErrorListener {
                                    Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + it.message + " )")
                                    editPhotoDialog.visibility = View.GONE
                                    photoLoadingView.visibility = View.GONE
                                    photosLoadingView.visibility = View.GONE
                                    progressBarText.text = "Loading ..."
                                    (activity as FormsActivity).overrideBackButton = false

                                }))
                            } else
                                Utility.showValidationAlertDialog(activity, "Please fill all the required activity")
                        }

                    }
                    photosTableLayout.addView(tableRow)
                }
            }
            photoLoadingView.visibility = View.GONE

        }
    }

    fun uploadPhoto(file: File, fileName: String) {
        val multipartRequest = MultipartRequest(uploadPhoto + fileName, null, file, Response.Listener { response ->
            try {
                submitPhotoDetails()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            Utility.showMessageDialog(context, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
            Log.v("Upload Photo Error : ", it.message.toString())
        })
        val socketTimeout = 30000//30 seconds - change to what you want
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        multipartRequest.retryPolicy = policy
        Volley.newRequestQueue((activity as FormsActivity).applicationContext).add(multipartRequest)
    }

    fun validateInputs() : Boolean {
        fileNameText.setError(null)
        fileDescText.setError(null)

        var isValid = true

        if (fileNameText.text.toString().isNullOrEmpty()) {
            isValid = false
            fileNameText.setError("Required Field")
        }
        if (fileDescText.text.toString().isNullOrEmpty()) {
            isValid = false
            fileDescText.setError("Required Field")
        }
        return isValid
    }

    fun validateEditsInputs() : Boolean {

        editFileDescText.setError(null)

        var isValid = true

        if (editFileDescText.text.toString().isNullOrEmpty()) {
            isValid = false
            editFileDescText.setError("Required Field")
        }
        return isValid
    }

    fun getPhotosChanges(action : Int, rowId: Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        if (action==0) {
            var fileNameStr = fileNameTitle.text.toString()
            var fileDescStr = fileDescText.text.toString()
            var approvalReq = approvalReqCheck.isChecked
            var downstreamStr = ""
            if (clubCHeck.isChecked) downstreamStr += clubCHeck.text.toString() + ", "
            if (commCheck.isChecked) downstreamStr += commCheck.text.toString() + ", "
            if (envCheck.isChecked) downstreamStr += envCheck.text.toString() + ", "
            if (modCheck.isChecked) downstreamStr += modCheck.text.toString() + ", "
            if (irasCheck.isChecked) downstreamStr += irasCheck.text.toString() + ", "
            if (rspCheck.isChecked) downstreamStr += rspCheck.text.toString() + ", "
            downstreamStr = downstreamStr.removeSuffix(", ")
            strChanges = "Photo added with "
            strChanges += "File Name (" + fileNameStr + ") - "
            strChanges += "Description (" + fileDescStr + ") - "
            strChanges += "Approval Requested (" + approvalReq.toString()+ ") - "
            if (!downstreamStr.isNullOrEmpty()) {
                strChanges += "Downstream Apps ("+downstreamStr+") - "
            }
        }
        if (action==1) {
            var fileDescStr = editFileDescText.text.toString()
            var approvalReq = editApprovalReqCheck.isChecked
            var downstreamStr = ""
            if (editClubCHeck.isChecked) downstreamStr += clubCHeck.text.toString() + ", "
            if (editCommCHeck.isChecked) downstreamStr += commCheck.text.toString() + ", "
            if (editEnvCheck.isChecked) downstreamStr += envCheck.text.toString() + ", "
            if (editModCheck.isChecked) downstreamStr += modCheck.text.toString() + ", "
            if (editIrasCheck.isChecked) downstreamStr += irasCheck.text.toString() + ", "
            if (editRspCheck.isChecked) downstreamStr += rspCheck.text.toString() + ", "
            downstreamStr = downstreamStr.removeSuffix(", ")
            if (approvalReq && (!tblFacilityPhotos[rowId].approvalrequested)) {
                strChanges += "Approval requested flag changed from (False) to (True) - "
            }
            if (!approvalReq && (tblFacilityPhotos[rowId].approvalrequested)) {
                strChanges += "Approval requested flag changed from (True) to (False) - "
            }
            if (fileDescStr!= tblFacilityPhotos[rowId].filedescription) {
                strChanges += "File Description changed from (" + tblFacilityPhotos[rowId].filedescription + ") to (" + fileDescStr + ") - "
            }
            if (downstreamStr != tblFacilityPhotos[rowId].downstreamapps) {
                strChanges += "Downstream Apps changed from (" + tblFacilityPhotos[rowId].downstreamapps+ ") to (" + downstreamStr+ ") - "
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun submitPhotoDetails() {

        progressBarText.text = "Saving Photo Details ..."
        photoLoadingView.visibility = View.VISIBLE
        var fileNameStr = fileNameTitle.text.toString()
        var fileDescStr = fileDescText.text.toString()
        var approvalReq = if (approvalReqCheck.isChecked) 1 else 0
        var downstreamStr = ""
        if (clubCHeck.isChecked) downstreamStr += clubCHeck.text.toString() + ", "
        if (commCheck.isChecked) downstreamStr += commCheck.text.toString() + ", "
        if (envCheck.isChecked) downstreamStr += envCheck.text.toString() + ", "
        if (modCheck.isChecked) downstreamStr += modCheck.text.toString() + ", "
        if (irasCheck.isChecked) downstreamStr += irasCheck.text.toString() + ", "
        if (rspCheck.isChecked) downstreamStr += rspCheck.text.toString() + ", "
        downstreamStr = downstreamStr.removeSuffix(", ")
        var item = PRGFacilityPhotos()
        item.filename = fileNameStr
        item.filedescription = fileDescStr
        item.approvalrequested = approvalReqCheck.isChecked
        item.downstreamapps = downstreamStr
        item.approved=false
        item.lastupdatedate= Date().toApiSubmitFormat()
        item.approveddate= ""
        item.lastupdateby = ApplicationPrefs.getInstance(activity).loggedInUserID
        item.facid = FacilityDataModel.getInstance().tblFacilities[0].FACNo
        item.clubCode = FacilityDataModel.getInstance().clubCode.toInt()
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotos + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&operation=ADD&downstreamApps=${downstreamStr}&LastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${fileNameStr}&fileDescription=${fileDescStr}&photoId=&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)),
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        if (response.toString().contains("Success", false)) {
                            Utility.showSubmitAlertDialog(activity, true, "Photos")
                            photoLoadingView.visibility = View.GONE
                            progressBarText.text = "Loading ..."
//                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos.add(item)
                            tblFacilityPhotos.add(item)
                            fillPhotosTableView()
                            HasChangedModel.getInstance().groupPhoto[0].Photos= true
                            HasChangedModel.getInstance().changeDoneForPhotoDef()
                        } else {
                            var errorMessage = response.toString()
                            Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + errorMessage + " )")
                        }
                        photoLoadingView.visibility = View.GONE
                        photosLoadingView.visibility = View.GONE
                        addNewPhotoDialog.visibility = View.GONE
                        progressBarText.text = "Loading ..."
                        (activity as FormsActivity).overrideBackButton = false
                    }
                }, Response.ErrorListener {
            Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: " + it.message + " )")
            photoLoadingView.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
            addNewPhotoDialog.visibility = View.GONE
            progressBarText.text = "Loading ..."
            (activity as FormsActivity).overrideBackButton = false

        }))

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
