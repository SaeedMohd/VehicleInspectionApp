package com.inspection.fragments


//import com.inspection.adapter.MultipartRequest
//import kotlinx.android.synthetic.main.fragment_aarav_photos.*
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.R.id.*
import com.inspection.Utils.*
import com.inspection.Utils.Constants.getS3Url
import com.inspection.Utils.Constants.uploadPhoto
import com.inspection.databinding.FragmentAaravPhotosBinding
import com.inspection.model.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.Locale.getDefault


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
    private val PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 2001
    private val CAMERA_REQUEST_CODE = 3001
    var tblFacilityPhotos = ArrayList<PRGFacilityPhotos>()
    var photoBitmap: Bitmap? = null
    var photoBitmapBase64: String= ""
    var photoThumbnailBitmap: Bitmap? = null
    var fileSuffix = ""
    var fileprefix = ""
    var filePath = ""
    var file : File = File("")
    private lateinit var pickImagesLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentAaravPhotosBinding? = null
    private val binding get() = _binding!!
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
//                val selectedUris = result.data?.clipData?.let { clipData ->
//                    (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }
//                } ?: result.data?.data?.let { listOf(it) }
//
//                // Handle the selected URIs
//                selectedUris?.forEach { uri ->
//                    // Do something with the selected image URIs
//                    println("Selected URI: $uri")
//                }
                requireContext().toast("Image picked successfully")
                var uri = result.data!!.data
                var imageName : String = ""
                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri);
                    photoBitmap = bitmap
                    photoThumbnailBitmap = bitmap
//            addNewPhotoDialog.visibility = View.GONE
                    binding.loadedImage.setImageBitmap(photoBitmap)
//                loadedImage.tag = getPathFromURI(uri)
//                loadedImage.tag = getPath(context,uri)
                    var saveAsBase64 = false
                    if (!saveAsBase64) {
                        if (uri != null) {
                            val imageUri: Uri = result.data?.data!!
                            filePath = getRealPathFromURI(requireContext(), imageUri).toString()
                            Log.v("File Path", filePath)
                            Log.v("Image URI", imageUri.toString())
                            imageName = getFileName(imageUri).toString()
                            imageUri?.let { uri ->
                                requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                                    file = inputStreamToFile(inputStream, imageName)
                                }
                            }
//                        file
                        }
                        fileSuffix = imageName.substring(imageName.lastIndexOf("."),imageName.length)
                        if (imageName.length>30)
                            imageName = imageName.substring(0,25)
                        else
                            imageName = imageName.substring(0,imageName.lastIndexOf(".")-1)
                        binding.fileNameText.setText(imageName.lowercase())
                        binding.fileNameTitle.text = (fileprefix + binding.fileNameText.text + fileSuffix).lowercase()
                    } else {
                        binding.fileNameTitle.text = ""
                        photoBitmapBase64 = bitmapToBase64(photoBitmap!!)
                        Log.v("BASE64 ->" , photoBitmapBase64)
                    }
                    Log.v("Image Name", fileSuffix)
                    enableControls(true)
//            photosPreviewDialog.visibility = View.VISIBLE
//            addTableRow()
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aarav_photos, container, false)
    }

    fun getRandomAlphanumericString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAaravPhotosBinding.bind(view)
//        fileprefix = "FACID" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "CC" + FacilityDataModel.getInstance().clubCode
        fileprefix = ""+FacilityDataModel.getInstance().clubCode+"_"+FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + getRandomAlphanumericString(6)
//        browseBtn.setOnClickListener {
//            dispatchTakePictureIntent()
//        }
        val headerScrollView: HorizontalScrollView = binding.headerScroll
        val bodyScrollView: HorizontalScrollView = binding.detailScroll


        // Sync horizontal scrolling
        headerScrollView.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            bodyScrollView.scrollTo(
                scrollX,
                scrollY
            )
        }

        bodyScrollView.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            headerScrollView.scrollTo(
                scrollX,
                scrollY
            )
        }
//        loadFacilityPhotos()
        tblFacilityPhotos = PRGDataModel.getInstance().tblPRGFacilitiesPhotos
        fillPhotosTableView()

//        synchronizeHeaderWithBody()
        binding.fileNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                fileNameTitle.text = fileprefix + p0 //+ fileSuffix
                binding.fileNameTitle.text = fileprefix + p0 + fileSuffix
            }
        })

        binding.reloadPhoto.setOnClickListener {
            getPhotosS3Urls()
        }

        binding.addNewPhoto.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = true
            enableControls(false)
            binding.fileDescText.setText("")
            binding.fileNameText.setText("")
            binding.fileNameTitle.text = ""
            binding.clubCHeck.isChecked = false
            binding.commCheck.isChecked = false
            binding.modCheck.isChecked = false
            binding.irasCheck.isChecked = false
            binding.rspCheck.isChecked = false
            binding.envCheck.isChecked = false
            binding.approvalReqCheck.isChecked = false
            binding.loadedImage.setImageBitmap(null)
            binding.photosLoadingView.visibility = View.VISIBLE
            binding.addNewPhotoDialog.visibility = View.VISIBLE
        }



        binding.addNewPhotoPickPhotoButton.setOnClickListener {
            checkAndRequestPermissions()
        }

        binding.addNewPhotoCapturePhotoButton.setOnClickListener {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // For Android 13+
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request permissions for image/media
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        // Permission already granted
                        captureImage()
                    }
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // For Android 10 (API level 29) to Android 12
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request permissions for camera and storage
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        // Permissions already granted
                        captureImage()
                    }
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    // For Android 6.0 (API level 23) to Android 9 (API level 28)
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request permissions for camera and storage
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        // Permissions already granted
                        captureImage()
                    }
                }

                else -> {
                    // For devices running below Android 6.0, no need for runtime permissions
                    captureImage()
                }
            }

//            when {
//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
//                    if (ContextCompat.checkSelfPermission(
//                            requireContext(),
//                            Manifest.permission.CAMERA
//                        ) != PackageManager.PERMISSION_GRANTED ||
//                        ContextCompat.checkSelfPermission(
//                            requireContext(),
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                        ) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        // Request permissions if not granted
//                        requestPermissions(
//                            arrayOf(Manifest.permission.CAMERA,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                                    CAMERA_PERMISSION_REQUEST_CODE
//                        )
//                    } else {
//                        // Permissions are granted, start the camera intent
//                        captureImage()
//                    }
//                }
//                else -> {
//                    // For older versions, no need to request permissions
//                    captureImage()
//                }
//            }





//            if (ContextCompat.checkSelfPermission((activity as FormsActivity),
//                            Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission((activity as FormsActivity),
//                            Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                dispatchTakePictureIntent()
//
//            } else {
//                requireContext().toast("Please make sure camera and storage permissions are granted")
//            }
        }

        binding.addNewPhotoConfirmButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                if (validateInputs()) {
                    binding.progressBarText.text = "Uploading Photo ..."
                    binding.photoLoadingView.visibility = View.VISIBLE
    //                submitPhotoDetails()
                //                no need for base 64
    //                uploadPhoto(file, fileNameTitle.text.toString())
//                    &facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubNum=${FacilityDataModel.getInstance().clubCode}
                    uploadFileWithOkHttp(file, uploadPhoto + binding.fileNameTitle.text.toString() + "&sessionId=${ApplicationPrefs.getInstance(activity).sessionID}&aws=Y") {
                        Log.v("Upload Photo", it.toString())
                        requireActivity().runOnUiThread {
    //                        progressBarText.text = "Uploading Photo ..."
                            binding.photoLoadingView.visibility = View.GONE
                            if (it.toString().contains("Error", false)) {
    //                            Utility.showMessageDialog(context, "Error", "Uploading File Failed with error (" + it + ")")
//                                var errorMsg = it.toString().split("]")
                                var photoUpload = ""
                                var generatePhotos = ""
                                var uploadLowToAWS = ""
                                var uploadHighToAWS = ""
                                if (it.toString().contains("PhotoUpload")) photoUpload = it.toString().substring(
                                    it.toString().indexOf("PhotoUpload:[") + 13,
                                    it.toString().indexOf("]")
                                )
                                if (it.toString().contains("GeneratePhotos")) generatePhotos = it.toString().substring(
                                    it.toString().indexOf("GeneratePhotos:[") + 16,
                                    it.toString().indexOf("UploadLowToAWS") - 1
                                )
                                if (it.toString().contains("UploadLowToAWS")) uploadLowToAWS = it.toString().substring(
                                    it.toString().indexOf("UploadLowToAWS:[") + 16,
                                    it.toString().indexOf("UploadHighToAWS") - 1
                                )
                                if (it.toString().contains("UploadHighToAWS")) uploadHighToAWS = it.toString().substring(
                                    it.toString().indexOf("UploadHighToAWS:[") + 16,
                                    it.toString().lastIndexOf("]")
                                )
//                                if (photoUpload.equals("")) photoUpload = it.toString()
//                                if (awsUplodStr.equals("")) awsUplodStr = it.toString()
//                                if (appLinkStr.equals("")) appLinkStr = it.toString()
//                                if (appLinkStr.equals("")) appLinkStr = it.toString()
                                val message = Array(4) { "" }
                                val status = Array(4) { "" }
                                status[0] = if (photoUpload.contains("Error", true)) "Error" else "Success"
                                status[1] = if (generatePhotos.contains("Error", true)) "Error" else "Success"
                                status[2] = if (uploadLowToAWS.contains("Error", true)) "Error" else "Success"
                                status[3] = if (uploadHighToAWS.contains("Error", true)) "Error" else "Success"
                                message[0] = "Save Photo: ${photoUpload}"
                                message[1] = "Generate Different Resolutions: ${generatePhotos.replace("Error: ","").replace("[","").replace("]","")}"
                                message[2] = "Upload Low Resolution to AWS: ${uploadLowToAWS.replace("Error: ","").replace("[","").replace("]","")}"
                                message[3] = "Upload High Resolution to AWS: ${uploadHighToAWS.replace("Error: ","").replace("[","").replace("]","")}"
                                Utility.showUnifiedErrorDialogWithSteps(activity,message,status)
                            } else {
                                Log.v("DONE => ","SHOULD BE SUBMITTED")
                                submitPhotoDetails()
                            }
                        }
                    }

                //                photosLoadingView.visibility = View.GONE
                } else {
                    Utility.showValidationAlertDialog(activity, "Please fill all the required activity")
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }

        binding.addNewPhotoCancelButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.addNewPhotoDialog.visibility = View.GONE
            binding.photosLoadingView.visibility = View.GONE
        }

        binding.editPhotoCancelButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.editPhotoDialog.visibility = View.GONE
            binding.photosLoadingView.visibility = View.GONE
        }

        binding.photosPreviewDialogCloseButton.setOnClickListener {
            (activity as FormsActivity).overrideBackButton = false
            binding.photosPreviewDialog.visibility = View.GONE
            binding.photosLoadingView.visibility = View.GONE
        }


        IndicatorsDataModel.getInstance().tblPhotos[0].visited = true
        binding.photosTitle.setTextColor(Color.parseColor("#26C3AA"))
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
        binding.photoLoadingView.visibility = View.VISIBLE

        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}",
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        tblFacilityPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())
//                        Utility.showMessageDialog(context,"ajshd","COUNT ---> "+tblFacilityPhotos.size)
                        fillPhotosTableView()
                    }
                }, Response.ErrorListener {
            Log.v("Loading error", "" + it.message)
                binding.photoLoadingView.visibility = View.GONE
            it.printStackTrace()
        }))
    }

    fun getPhotosS3Urls() {
        var counter = FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.FileName.isNotEmpty() && s.PhotoId>-1}.size
        FacilityDataModel.getInstance().FacilityPhotos.apply {
            (0 until size).forEach {
                if (get(it).PhotoId > -1 && get(it).FileName.isNotEmpty()) {
                    Log.v("S3 => " , getS3Url + get(it).FileName + "&type=lowResPhoto&approved=" + (if (get(
                            it
                        ).Approved == "true"
                    ) "1" else "0"))
                    Volley.newRequestQueue(context).add(
                        StringRequest(Request.Method.GET,
                            getS3Url + get(it).FileName + "&type=lowResPhoto&approved=" + (if (get(
                                    it
                                ).Approved == "true"
                            ) "1" else "0"),
                            Response.Listener { response ->
                                requireActivity().runOnUiThread {
                                    counter--
                                    if (!response.toString().contains("Error", false)) {
                                        get(it).imageUrl = response.toString()
                                    }
                                    if (counter==0) fillPhotosTableView()
                                }
                            },
                            Response.ErrorListener {
                                counter--
                                Utility.showSubmitAlertDialog(
                                    activity,
                                    false,
                                    "AWS URLs -> " + it.message + " )"
                                )
                                if (counter==0) fillPhotosTableView()
                            })
                    )
                }
            }
        }
        Log.v("TEST ==>","HERE")
    }

    private fun checkAndRequestPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ permissions
                val permissions = mutableListOf<String>()
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                }
//                if (ContextCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.READ_MEDIA_VIDEO
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
//                }

                if (permissions.isNotEmpty()) {
                    this.requestPermissions(
//                        requireActivity(),
                        permissions.toTypedArray(),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Permissions already granted, proceed to pick photos
                    pickPhotos()
                }
            }

            else -> {
                // For Android 12 and below
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    this.requestPermissions(
//                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Permission already granted
                    pickPhotos()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All requested permissions are granted, proceed to pick photos
                pickPhotos()
            } else {
                // Permissions denied, show a message or fallback behavior
                println("Permissions denied")
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed to capture image
                captureImage()
            } else {
                var allGranted = true
                var permanentlyDenied = false

                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        allGranted = false

                        // Check if "Don't Ask Again" was selected
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions[i])) {
                            permanentlyDenied = true
                        }
                    }
                }

                when {
                    allGranted -> {
                        // All permissions are granted
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)
                    }
                    permanentlyDenied -> {
                        // Some permissions are permanently denied
//                        private fun showPermissionSettingsDialog() {
                            val builder = android.app.AlertDialog.Builder(requireContext())
                            builder.setTitle("Permission Required")
                            builder.setMessage("Some permissions are permanently denied. You need to enable them from settings.")
                            builder.setPositiveButton("Go to Settings") { _, _ ->
                                // Redirect to app settings
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", requireActivity().packageName, null)
                                }
                                startActivity(intent)
                            }
                            builder.setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            builder.show()
//                        }
                    }
                    else -> {
                        // Permissions are denied but not permanently
                        Toast.makeText(requireContext(),"Permissions denied. Please allow them to continue.",Toast.LENGTH_SHORT).show()
                    }
                }
                // Permission denied
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun pickPhotos(){
        val intent: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Intent(Intent.ACTION_PICK).apply {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // Allow multiple selections
            }
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
        }
        pickImagesLauncher.launch(intent)
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
        val timeStamp: String = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        return File.createTempFile(
                "${timeStamp}_", /* prefix */
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
            Log.v("Photo Path",mCurrentPhotoPath)
//            addNewPhotoDialog.visibility = View.GONE
            binding.loadedImage.setImageBitmap(photoBitmap)
//            loadedImage.tag = mCurrentPhotoPath
//            fileSuffix = loadedImage.tag.toString().substring(loadedImage.tag.toString().lastIndexOf("."))
//            fileNameTitle.text = fileprefix + fileNameText.text + fileSuffix
            filePath = mCurrentPhotoPath
            file = File(mCurrentPhotoPath)
            var imageName = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/")+1,mCurrentPhotoPath.length)
            fileSuffix = imageName.substring(imageName.lastIndexOf("."),imageName.length)
            imageName = if (imageName.length>30)
                imageName.substring(0,25)
            else
                imageName.substring(0,imageName.lastIndexOf(".")-1)
            binding.fileNameText.setText(imageName.lowercase())
            binding.fileNameTitle.text = (fileprefix + binding.fileNameText.text + fileSuffix).lowercase()
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
            var imageName : String = ""
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri);
                photoBitmap = bitmap
                photoThumbnailBitmap = bitmap
//            addNewPhotoDialog.visibility = View.GONE
                binding.loadedImage.setImageBitmap(photoBitmap)
//                loadedImage.tag = getPathFromURI(uri)
//                loadedImage.tag = getPath(context,uri)
                var saveAsBase64 = false
                if (!saveAsBase64) {
                    if (uri != null) {
                        val imageUri: Uri = data.data!!
                        filePath = getRealPathFromURI(requireContext(), imageUri).toString()
                        Log.v("File Path", filePath)
                        Log.v("Image URI", imageUri.toString())
                        imageName = getFileName(imageUri).toString()
                        imageUri?.let { uri ->
                            requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                                file = inputStreamToFile(inputStream, imageName)
                            }
                        }
//                        file
                    }
                    fileSuffix = imageName.substring(imageName.lastIndexOf("."),imageName.length)
                    if (imageName.length>30)
                        imageName = imageName.substring(0,25)
                    else
                        imageName = imageName.substring(0,imageName.lastIndexOf(".")-1)
                    binding.fileNameText.setText(imageName.lowercase())
                    binding.fileNameTitle.text = (fileprefix + binding.fileNameText.text + fileSuffix).lowercase()
                } else {
                    binding.fileNameTitle.text = ""
                    photoBitmapBase64 = bitmapToBase64(photoBitmap!!)
                    Log.v("BASE64 ->" , photoBitmapBase64)
                }
                Log.v("Image Name", fileSuffix)
                enableControls(true)
            } catch (e: IOException) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The photo has been taken, and the URI is saved in `photoUri`
            photoUri?.let { uri ->
                photoBitmap = getBitmapWithPath(mCurrentPhotoPath, false)
                photoThumbnailBitmap = getBitmapWithPath(mCurrentThumbPath, true)
                Log.v("Photo Path",mCurrentPhotoPath)
                binding.loadedImage.setImageBitmap(photoBitmap)
                filePath = mCurrentPhotoPath
                file = File(mCurrentPhotoPath)
                var imageName = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/")+1,mCurrentPhotoPath.length)
                fileSuffix = imageName.substring(imageName.lastIndexOf("."),imageName.length)
                imageName = if (imageName.length>30)
                    imageName.substring(0,25)
                else
                    imageName.substring(0,imageName.lastIndexOf(".")-1)
                binding.fileNameText.setText(imageName.lowercase())
                binding.fileNameTitle.text = (fileprefix + binding.fileNameText.text + fileSuffix).lowercase()
                enableControls(true)
            }
        }

    }

    fun inputStreamToFile(inputStream: InputStream, fileName: String): File {
//        val cachePath = File(requireContext().cacheDir, "images")
        val file = File(requireContext().cacheDir, fileName)
        FileOutputStream(file).use { output ->
            val buffer = ByteArray(4 * 1024) // 4KB buffer size
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
        }
        inputStream.close()
        return file
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor = requireContext().getContentResolver().query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = cursor.getString(nameIndex) // Get the file name
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment // Fallback to last path segment if name is not available
        }
        return result
    }

    fun enableControls(enable: Boolean) {
        binding.fileNameText.isEnabled = enable
        binding.fileDescText.isEnabled = enable
        binding.clubCHeck.isEnabled = enable
        binding.commCheck.isEnabled = enable
        binding.modCheck.isEnabled = enable
        binding.envCheck.isEnabled = enable
        binding.irasCheck.isEnabled = enable
        binding.rspCheck.isEnabled = enable
        binding.approvalReqCheck.isEnabled = enable
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

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var result: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return result
    }

    private fun synchronizeHeaderWithBody() {
        binding.photosTableLayout.post {
            val bodyRow = binding.photosTableLayout.getChildAt(0) as TableRow
                ?: return@post
            val headerRow =
                binding.headerTable.getChildAt(0) as TableRow ?: return@post
            for (i in 0 until bodyRow.childCount) {
                if (i==0) {
//                    val bodyCell = bodyRow.getChildAt(i) as ImageView
//                    var headerCell = headerRow.getChildAt(i) as ImageView
//
//                    var cellWidth = bodyCell.width
//                    headerCell.width = cellWidth
                } else {
                    val bodyCell = bodyRow.getChildAt(i) as TextView
                    val headerCell = headerRow.getChildAt(i) as TextView

                    val cellWidth = bodyCell.width
                    headerCell.width = cellWidth
                }

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


    fun fillPhotosTableView() {

        binding.photoLoadingView.visibility = View.VISIBLE
//        if (binding.photosTableLayout.childCount > 1) {
//        for (i in binding.photosTableLayout.childCount - 1 downTo 1) {
            binding.photosTableLayout.removeAllViews()
//        }
//        }

        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 2F
        rowLayoutParam.column = 0
//        rowLayoutParam.leftMargin = 5
//        rowLayoutParam.height = 300
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 240.dpToPx(requireContext())

        rowLayoutParam.gravity = Gravity.CENTER

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 280.dpToPx(requireContext())
        rowLayoutParam1.gravity = Gravity.CENTER

        val rowLayoutParam2 = TableRow.LayoutParams()
//        rowLayoutParam2.weight = 2F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 280.dpToPx(requireContext())
        rowLayoutParam2.gravity = Gravity.CENTER

        val rowLayoutParam3 = TableRow.LayoutParams()
//        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 140.dpToPx(requireContext())
        rowLayoutParam3.gravity = Gravity.CENTER

        val rowLayoutParam4 = TableRow.LayoutParams()
//        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 140.dpToPx(requireContext())
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
//        rowLayoutParam5.weight = 1.0F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 140.dpToPx(requireContext())
        rowLayoutParam5.gravity = Gravity.CENTER

        val rowLayoutParam6 = TableRow.LayoutParams()
//        rowLayoutParam6.weight = 1.0F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 140.dpToPx(requireContext())
        rowLayoutParam6.gravity = Gravity.CENTER

        val rowLayoutParam7 = TableRow.LayoutParams()
//        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam7.width = 140.dpToPx(requireContext())
        rowLayoutParam7.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam8 = TableRow.LayoutParams()
//        rowLayoutParam8.weight = 1F
        rowLayoutParam8.column = 8
        rowLayoutParam8.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam8.width = 140.dpToPx(requireContext())
        rowLayoutParam8.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam9 = TableRow.LayoutParams()
//        rowLayoutParam9.weight = 2.0F
        rowLayoutParam9.column = 9
        rowLayoutParam9.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam9.width = 280.dpToPx(requireContext())
        rowLayoutParam9.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam10 = TableRow.LayoutParams()
//        rowLayoutParam10.weight = 1.5F
        rowLayoutParam10.column = 10
        rowLayoutParam10.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam10.width = 210.dpToPx(requireContext())
        rowLayoutParam10.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = 80

        FacilityDataModel.getInstance().FacilityPhotos.apply {
            (0 until size).forEach {
                if (get(it).PhotoId > -1) {
                    val tableRow = TableRow(context)
                    tableRow.tag = "APP"
                    tableRow.layoutParams = rowLayoutParamRow
                    if (get(it).FileName.isNullOrEmpty())
                        tableRow.minimumHeight = if (get(it).FileName.isNullOrEmpty()) 30 else 60
                    tableRow.setPadding(0, 4, 0, 4)

                    val imageView = ImageView(context)
                    imageView.layoutParams = rowLayoutParam
                    imageView.setPadding(5, 5, 5, 5)
                    imageView.minimumWidth = 240.dpToPx(requireContext())
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    imageView.isClickable = true
                    Log.v("S3 URL =>" , Constants.getS3Url + get(it).FileName + "&type=lowResPhoto&approved=" + (if (get(it).Approved=="true") "1" else "0" ))

                    if (!get(it).FileName.isNullOrEmpty()) {
                        Glide.with(this@FragmentAARAVPhotos)
//                            .load(Constants.getImages + get(it).FileName)
                            .load(get(it).imageUrl)
                            .error(R.drawable.image_not_available)
                            .into(imageView);
                        imageView.setOnClickListener { innerIt ->
                            Glide.with(this@FragmentAARAVPhotos).load(get(it).imageUrl).into(binding.photosEnlargeImageView);
//                            photosEnlargeDialog.requestLayout()
                            binding.closeImageView.setOnClickListener {
                                binding.photosEnlargeDialog.visibility = View.GONE
                            }
                            binding.photosEnlargeDialog.visibility = View.VISIBLE
                        }

                    } else {

                    }
                    tableRow.addView(imageView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_HORIZONTAL
                    textView1.minimumHeight = 30
                    textView1.text = get(it).FileName
                    textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView1.textSize = 14f
                    textView1.setMinWidth(280.dpToPx(requireContext())) // Ensure minimum width
                    textView1.setMaxWidth(280.dpToPx(requireContext()))
                    tableRow.addView(textView1)




                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.minimumHeight = 30
                    textView2.text = get(it).FileDescription
                    textView2.textSize = 14f
                    textView2.setMinWidth(280.dpToPx(requireContext())) // Ensure minimum width
                    textView2.setMaxWidth(280.dpToPx(requireContext()))
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER
                    textView3.minimumHeight = 30
                    textView3.text = get(it).LastUpdateBy
                    textView3.textSize = 14f
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.minimumHeight = 30
                    if (get(it).ApprovedDate.isNullOrEmpty()) textView4.text = ""
                    else textView4.text = if (get(it).LastUpdateDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).LastUpdateDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView4)

                    val checkBox1 = CheckBox(context)
                    checkBox1.layoutParams = rowLayoutParam5
                    checkBox1.gravity = Gravity.CENTER
                    checkBox1.minimumHeight = 30
                    checkBox1.isClickable = false
                    checkBox1.textSize = 14f
                    checkBox1.isChecked = (get(it).ApprovalRequested == "true")
                    tableRow.addView(checkBox1)

                    val checkBox2 = CheckBox(context)
                    checkBox2.layoutParams = rowLayoutParam6
                    checkBox2.gravity = Gravity.CENTER
                    checkBox2.minimumHeight = 30
                    checkBox2.isChecked = (get(it).Approved == "true")
                    checkBox2.isClickable = false
                    checkBox2.textSize = 14f
                    tableRow.addView(checkBox2)

                    val textView5 = TextView(context)
                    textView5.layoutParams = rowLayoutParam7
                    textView5.gravity = Gravity.CENTER
                    textView5.minimumHeight = 30
                    textView5.text = get(it).ApprovedBy
                    textView5.textSize = 14f
                    tableRow.addView(textView5)

                    val textView6 = TextView(context)
                    textView6.layoutParams = rowLayoutParam8
                    textView6.gravity = Gravity.CENTER
                    textView6.minimumHeight = 30
                    textView5.textSize = 14f
                    if (get(it).ApprovedDate.isNullOrEmpty()) textView6.text = ""
                    else textView6.text = if (get(it).ApprovedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ApprovedDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView6)

                    val textView7 = TextView(context)
                    textView7.layoutParams = rowLayoutParam9
                    textView7.gravity = Gravity.CENTER
                    textView7.minimumHeight = 30
                    textView7.text = decodeDownStreamApps(get(it).DownstreamAppId)
                    textView7.textSize = 14f
                    tableRow.addView(textView7)

                    val editButton = Button(context)
                    editButton.layoutParams = rowLayoutParam10
                    editButton.setTextColor(Color.BLUE)
                    editButton.text = "EDIT"
                    editButton.minimumHeight = 30
                    editButton.isEnabled = true

                    editButton.textSize = 14f
                    editButton.gravity = Gravity.CENTER
                    editButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(editButton)
                    var photoID = get(it).PhotoId
                    editButton.setOnClickListener {
                        (activity as FormsActivity).overrideBackButton = true
                        binding.editFileDescText.setText(textView2.text)
                        binding.editFileNameText.setText(
                            textView1.text.toString().lowercase(getDefault())
                        )
//                        editFileNameTitle.text = ""
                        binding.editClubCHeck.isChecked = textView7.text.contains("Club Hub/MRM")
                        binding.editCommCHeck.isChecked = textView7.text.contains("eComm")
                        binding.editModCheck.isChecked = textView7.text.contains("Envision")
                        binding.editIrasCheck.isChecked = textView7.text.contains("MOD")
                        binding.editRspCheck.isChecked = textView7.text.contains("IRAS")
                        binding.editEnvCheck.isChecked = textView7.text.contains("RSP")
                        binding.editApprovalReqCheck.isChecked = checkBox1.isChecked
                        binding.photosLoadingView.visibility = View.VISIBLE
                        binding.editPhotoDialog.visibility = View.VISIBLE
                        var currentTableRowIndex = binding.photosTableLayout.indexOfChild(tableRow)
                        var currentPhotoIndex = currentTableRowIndex - 1

                        binding.editPhotoConfirmButton.setOnClickListener {
                            if (validateEditsInputs()) {
                                binding.progressBarText.text = "Saving ..."
                                binding.photoLoadingView.visibility = View.VISIBLE
                                binding.progressBarText.text = "Saving Photo Details ..."
                                binding.photoLoadingView.visibility = View.VISIBLE

//                                var photoID = tblFacilityPhotos[currentPhotoIndex].photoid
                                var fileDescStr =
                                    binding.editFileDescText.text.toString().lowercase(getDefault())
                                var fileNameStr =
                                    binding.editFileNameText.text.toString().lowercase(getDefault())
                                var approvalReq = binding.editApprovalReqCheck.isChecked
                                var downstreamStr = ""
                                if (binding.editClubCHeck.isChecked) downstreamStr += binding.clubCHeck.text.toString() + ", "
                                if (binding.editCommCHeck.isChecked) downstreamStr += binding.commCheck.text.toString() + ", "
                                if (binding.editEnvCheck.isChecked) downstreamStr += binding.envCheck.text.toString() + ", "
                                if (binding.editModCheck.isChecked) downstreamStr += binding.modCheck.text.toString() + ", "
                                if (binding.editIrasCheck.isChecked) downstreamStr += binding.irasCheck.text.toString() + ", "
                                if (binding.editRspCheck.isChecked) downstreamStr += binding.rspCheck.text.toString() + ", "
                                downstreamStr = downstreamStr.removeSuffix(", ")

                                var downstreamAPPStr = ""
                                if (binding.editClubCHeck.isChecked) downstreamAPPStr += "1,"
                                if (binding.editCommCHeck.isChecked) downstreamAPPStr += "2,"
                                if (binding.editIrasCheck.isChecked) downstreamAPPStr += "3,"
                                if (binding.editRspCheck.isChecked) downstreamAPPStr += "4,"
                                if (binding.editEnvCheck.isChecked) downstreamAPPStr += "5,"
                                if (binding.editModCheck.isChecked) downstreamAPPStr += "6,"

                                downstreamAPPStr = downstreamAPPStr.removeSuffix(",")

                                var itemAPP = FacilityPhotos()
                                itemAPP.FileName = fileNameStr
                                itemAPP.FileDescription = fileDescStr
                                itemAPP.ApprovalRequested = if (approvalReq) "1" else "0"
                                itemAPP.DownstreamAppId = downstreamAPPStr
                                itemAPP.Approved = if (FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId == photoID }[0].Approved.equals("true")) "1" else "0"
                                itemAPP.LastUpdateDate = Date().toApiSubmitFormat()
                                itemAPP.ApprovedDate = FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId == photoID }[0].ApprovedDate
                                itemAPP.ApprovedBy = FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId == photoID }[0].ApprovedBy
                                itemAPP.PhotoId = photoID
                                itemAPP.SeqNum = FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId == photoID }[0].SeqNum
                                itemAPP.LastUpdateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                                // START FROM HERE
                                Log.v("SubmitPhoto -> ", Constants.updateFacilityPhotosData + "${FacilityDataModel.getInstance().tblFacilities[0].FACID}&seqNum=${itemAPP.SeqNum}&facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&approved=${itemAPP.Approved}&approvedBy=${itemAPP.ApprovedBy}&operation=EDIT&downstreamAppId=${downstreamAPPStr}&lastUpdateDate=${Date().toApiSubmitFormat()}&approvedDate=${itemAPP.ApprovedDate}&downstreamApps=${downstreamStr}&primaryPhoto=0&lastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${
                                    fileNameStr.lowercase(getDefault())
                                }&fileDescription=${fileDescStr}&photoId=${photoID}&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)))
                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotosData + "${FacilityDataModel.getInstance().tblFacilities[0].FACID}&seqNum=${itemAPP.SeqNum}&facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&approved=${itemAPP.Approved}&approvedBy=${itemAPP.ApprovedBy}&operation=EDIT&downstreamAppId=${downstreamAPPStr}&lastUpdateDate=${Date().toApiSubmitFormat()}&approvedDate=${itemAPP.ApprovedDate}&downstreamApps=${downstreamStr}&primaryPhoto=0&lastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${fileNameStr.lowercase(getDefault())}&fileDescription=${fileDescStr}&photoId=${photoID}&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)),
                                    { response ->
                                        requireActivity().runOnUiThread {
                                            if (response.toString().contains("Success", false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Photos")
                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId==photoID }[0].FileDescription = fileDescStr
                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId==photoID }[0].ApprovalRequested = approvalReq.toString()
                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId==photoID }[0].LastUpdateDate= Date().toApiSubmitFormat()
//                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId==photoID }[0].DownstreamAppId= downstreamAPPStr
//                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s-> s.PhotoId==photoID }[0].ApprovalRequested = approvalReq.toString()
//                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s-> s.PhotoId==photoID }[0].DownstreamAppId = downstreamAPPStr
//                                                FacilityDataModel.getInstance().FacilityPhotos.filter { s-> s.PhotoId==photoID }[0].FileDescription = fileDescStr
//                                                tblFacilityPhotos[currentPhotoIndex].filedescription = fileDescStr
//                                                tblFacilityPhotos[currentPhotoIndex].approvalrequested = binding.editApprovalReqCheck.isChecked
//                                                tblFacilityPhotos[currentPhotoIndex].downstreamapps = downstreamStr
//                                                tblFacilityPhotos[currentPhotoIndex].lastupdatedate= Date().toApiSubmitFormat()
//                                                    loadFacilityPhotos()
                                                fillPhotosTableView()
                                                HasChangedModel.getInstance().groupPhoto[0].Photos= true
                                                HasChangedModel.getInstance().changeDoneForPhotoDef()
                                            } else {
//                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + response.toString() + " )")
                                            }
                                            binding.photoLoadingView.visibility = View.GONE
                                            binding.photosLoadingView.visibility = View.GONE
                                            binding.progressBarText.text = "Loading ..."
                                            binding.editPhotoDialog.visibility = View.GONE
                                            (activity as FormsActivity).overrideBackButton = false
                                        }
                                    },
                                    {
                                        Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + it.message + " )")

                                        binding.editPhotoDialog.visibility = View.GONE
                                        binding.photoLoadingView.visibility = View.GONE
                                        binding.photosLoadingView.visibility = View.GONE
                                        binding.progressBarText.text = "Loading ..."
                                        (activity as FormsActivity).overrideBackButton = false
//                                        fillPhotosTableView()

                                    }))
                            } else
                                Utility.showValidationAlertDialog(activity, "Please fill all the required activity")
                        }

                    }
                    binding.photosTableLayout.addView(tableRow)
                }
            }
//            binding.photoLoadingView.visibility = View.GONE

        }
        tblFacilityPhotos.apply {
            (0 until size).forEach {
                if (get(it).photoid > -1 && (FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.FileName.equals(get(it).filename)}.isEmpty())) {
                    val tableRow = TableRow(context)
                    tableRow.tag = "PRG"
                    tableRow.layoutParams = rowLayoutParamRow
                    if (get(it).filename.isNullOrEmpty())
                        tableRow.minimumHeight = if (get(it).filename.isNullOrEmpty()) 30 else 60
                    tableRow.setPadding(0, 4, 0, 4)

//                    if (it % 2 == 0) {
//                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
//                    }
                    val imageView = ImageView(context)
                    imageView.layoutParams = rowLayoutParam
                    imageView.setPadding(5, 5, 5, 5)
                    imageView.minimumWidth = 240.dpToPx(requireContext())
                    imageView.scaleType = ImageView.ScaleType.FIT_XY
                    imageView.isClickable = true

//                    Glide.with(this@FragmentAARAVPhotos).load(Constants.getImages + get(it).filename).into(imageView);
                    if (!get(it).filename.isNullOrEmpty()) {
                        Glide.with(this@FragmentAARAVPhotos)
                            .load(Constants.getImages + get(it).filename)
                            .error(R.drawable.image_not_available)
                            .into(imageView);
                        imageView.setOnClickListener { innerIt ->
                            Glide.with(this@FragmentAARAVPhotos).load(Constants.getImages + get(it).filename).into(binding.photosEnlargeImageView);
//                            photosEnlargeDialog.requestLayout()
                            binding.closeImageView.setOnClickListener {
                                binding.photosEnlargeDialog.visibility = View.GONE
                            }
                            binding.photosEnlargeDialog.visibility = View.VISIBLE
                        }
                    } else {

                    }
                    tableRow.addView(imageView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    textView1.minimumHeight = 30
                    textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView1.text = get(it).filename
                    textView1.textSize = 14f
                    textView1.setMinWidth(280.dpToPx(requireContext())) // Ensure minimum width
                    textView1.setMaxWidth(280.dpToPx(requireContext()))
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER
                    textView2.minimumHeight = 30
                    textView2.text = get(it).filedescription
                    textView2.textSize = 14f
                    textView2.setMinWidth(280.dpToPx(requireContext())) // Ensure minimum width
                    textView2.setMaxWidth(280.dpToPx(requireContext()))
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
                    checkBox2.gravity = Gravity.CENTER_VERTICAL
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
                    textView7.textSize = 14f
                    tableRow.addView(textView7)

                    val editButton = Button(context)
                    editButton.layoutParams = rowLayoutParam10
                    editButton.setTextColor(Color.BLUE)
                    editButton.text = "Upload to APP"
                    editButton.minimumHeight = 30
                    editButton.isEnabled = true
                    editButton.textSize = 14f
                    editButton.gravity = Gravity.CENTER
                    editButton.setBackgroundColor(Color.TRANSPARENT)
//                    tableRow.addView(editButton)

                    //
//                    editButton.setOnClickListener {
//                        (activity as FormsActivity).overrideBackButton = true
//                        binding.editFileDescText.setText(textView2.text)
//                        binding.editFileNameText.setText(textView1.text)
////                        editFileNameTitle.text = ""
//                        binding.editClubCHeck.isChecked = textView7.text.contains("Club Hub/MRM")
//                        binding.editCommCHeck.isChecked = textView7.text.contains("eComm")
//                        binding.editModCheck.isChecked = textView7.text.contains("Envision")
//                        binding.editIrasCheck.isChecked = textView7.text.contains("MOD")
//                        binding.editRspCheck.isChecked = textView7.text.contains("IRAS")
//                        binding.editEnvCheck.isChecked = textView7.text.contains("RSP")
//                        binding.editApprovalReqCheck.isChecked = checkBox1.isChecked
//                        binding.photosLoadingView.visibility = View.VISIBLE
//                        binding.editPhotoDialog.visibility = View.VISIBLE
//                        var currentTableRowIndex = binding.photosTableLayout.indexOfChild(tableRow)
//                        var currentPhotoIndex = currentTableRowIndex - 1
//
//                        binding.editPhotoConfirmButton.setOnClickListener {
//
//                            if (validateEditsInputs()) {
//                                binding.progressBarText.text = "Saving ..."
//                                binding.photoLoadingView.visibility = View.VISIBLE
//                                binding.progressBarText.text = "Saving Photo Details ..."
//                                binding.photoLoadingView.visibility = View.VISIBLE
//
//                                var photoID = tblFacilityPhotos[currentPhotoIndex].photoid
//                                var fileDescStr = binding.editFileDescText.text.toString()
//                                var approvalReq = if (binding.editApprovalReqCheck.isChecked) 1 else 0
//                                var downstreamStr = ""
//                                if (binding.editClubCHeck.isChecked) downstreamStr += binding.clubCHeck.text.toString() + ", "
//                                if (binding.editCommCHeck.isChecked) downstreamStr += binding.commCheck.text.toString() + ", "
//                                if (binding.editEnvCheck.isChecked) downstreamStr += binding.envCheck.text.toString() + ", "
//                                if (binding.editModCheck.isChecked) downstreamStr += binding.modCheck.text.toString() + ", "
//                                if (binding.editIrasCheck.isChecked) downstreamStr += binding.irasCheck.text.toString() + ", "
//                                if (binding.editRspCheck.isChecked) downstreamStr += binding.rspCheck.text.toString() + ", "
//                                downstreamStr = downstreamStr.removeSuffix(", ")
//                                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotos + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&operation=EDIT&downstreamApps=${downstreamStr}&LastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=&fileDescription=${fileDescStr}&photoId=${photoID}&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 1, getPhotosChanges(1,currentPhotoIndex)),
//                                        Response.Listener { response ->
//                                            requireActivity().runOnUiThread {
//                                                if (response.toString().contains("Success", false)) {
//                                                    Utility.showSubmitAlertDialog(activity, true, "Photos")
////                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].filedescription = fileDescStr
////                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].approvalrequested = editApprovalReqCheck.isChecked
////                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].downstreamapps = downstreamStr
////                                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos[currentPhotoIndex].lastupdatedate= Date().toApiSubmitFormat()
//                                                    tblFacilityPhotos[currentPhotoIndex].filedescription = fileDescStr
//                                                    tblFacilityPhotos[currentPhotoIndex].approvalrequested = binding.editApprovalReqCheck.isChecked
//                                                    tblFacilityPhotos[currentPhotoIndex].downstreamapps = downstreamStr
//                                                    tblFacilityPhotos[currentPhotoIndex].lastupdatedate= Date().toApiSubmitFormat()
////                                                    loadFacilityPhotos()
//                                                    fillPhotosTableView()
//                                                    HasChangedModel.getInstance().groupPhoto[0].Photos= true
//                                                    HasChangedModel.getInstance().changeDoneForPhotoDef()
//                                                } else {
////                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
//                                                    Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + response.toString() + " )")
//                                                }
//                                                binding.photoLoadingView.visibility = View.GONE
//                                                binding.photosLoadingView.visibility = View.GONE
//                                                binding.progressBarText.text = "Loading ..."
//                                                binding.editPhotoDialog.visibility = View.GONE
//                                                (activity as FormsActivity).overrideBackButton = false
//                                            }
//                                        }, Response.ErrorListener {
//                                    Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + it.message + " )")
//                                    binding.editPhotoDialog.visibility = View.GONE
//                                    binding.photoLoadingView.visibility = View.GONE
//                                    binding.photosLoadingView.visibility = View.GONE
//                                    binding.progressBarText.text = "Loading ..."
//                                    (activity as FormsActivity).overrideBackButton = false
//
//                                }))
//                            } else
//                                Utility.showValidationAlertDialog(activity, "Please fill all the required activity")
//                        }
//
//                    }
                    binding.photosTableLayout.addView(tableRow)
                }
            }

        }

        altPhotosTableRow()
        binding.photoLoadingView.visibility = View.GONE
    }

//    fun uploadPhoto(file: File, fileName: String) {
//        val multipartRequest = MultipartRequest(uploadPhoto + fileName, null, file, Response.Listener { response ->
//            try {
//                submitPhotoDetails()
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//        }, Response.ErrorListener {
//            Utility.showMessageDialog(context, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
//            Log.v("Upload Photo Error : ", it.message.toString())
//        })
//        val socketTimeout = 30000//30 seconds - change to what you want
//        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//        multipartRequest.retryPolicy = policy
//        Volley.newRequestQueue((activity as FormsActivity).applicationContext).add(multipartRequest)
//    }

    fun altPhotosTableRow() {
        var childViewCount = binding.photosTableLayout.getChildCount();

        for ( i in 0..childViewCount-1) {
            var row : TableRow= binding.photosTableLayout.getChildAt(i) as TableRow;

            if (row.tag.equals("PRG")) {
                row.setBackground(getResources().getDrawable(
                    R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                    R.drawable.green_row_color));
            }
        }

//        d9ead3
    }

    fun uploadFileWithOkHttp(file: File, url: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .build()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Upload failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback("Upload failed with code ${response.code}")
                }
            }
        })
    }

    fun validateInputs() : Boolean {
        binding.fileNameText.setError(null)
        binding.fileDescText.setError(null)

        var isValid = true

        if (binding.fileNameText.text.toString().isNullOrEmpty()) {
            isValid = false
            binding.fileNameText.setError("Please add the desired name")
        }

        if (binding.fileDescText.text.toString().isNullOrEmpty()) {
            isValid = false
            binding.fileDescText.setError("Required Field")
        }
        return isValid
    }

    fun decodeDownStreamApps(strApps : String) : String {
        var decodedStr = ""
        var splitted = strApps.split(",")
        for (item in splitted) {
            when (item) {
                "1" -> decodedStr += "Club Hub/MRM, "
                "2" -> decodedStr += "eComm, "
                "3" -> decodedStr += "IRAS, "
                "4" -> decodedStr += "RSP, "
                "5" -> decodedStr += "Envision, "
                "6" -> decodedStr += "MOD, "
            }
        }
        return if (decodedStr.isNotEmpty()) decodedStr.dropLast(2) else decodedStr
    }

    fun validateEditsInputs() : Boolean {

        binding.editFileDescText.setError(null)

        var isValid = true

        if (binding.editFileDescText.text.toString().isNullOrEmpty()) {
            isValid = false
            binding.editFileDescText.setError("Required Field")
        }
        return isValid
    }

    fun getPhotosChanges(action : Int, rowId: Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        try {
            if (action == 0) {
                var fileNameStr = binding.fileNameTitle.text.toString()
                var fileDescStr = binding.fileDescText.text.toString()
                var approvalReq = binding.approvalReqCheck.isChecked
                var downstreamStr = ""
                if (binding.clubCHeck.isChecked) downstreamStr += binding.clubCHeck.text.toString() + ", "
                if (binding.commCheck.isChecked) downstreamStr += binding.commCheck.text.toString() + ", "
                if (binding.envCheck.isChecked) downstreamStr += binding.envCheck.text.toString() + ", "
                if (binding.modCheck.isChecked) downstreamStr += binding.modCheck.text.toString() + ", "
                if (binding.irasCheck.isChecked) downstreamStr += binding.irasCheck.text.toString() + ", "
                if (binding.rspCheck.isChecked) downstreamStr += binding.rspCheck.text.toString() + ", "
                downstreamStr = downstreamStr.removeSuffix(", ")
                strChanges = "Photo added with "
                strChanges += "File Name (" + fileNameStr + ") - "
                strChanges += "Description (" + fileDescStr + ") - "
                strChanges += "Approval Requested (" + approvalReq.toString() + ") - "
                if (!downstreamStr.isNullOrEmpty()) {
                    strChanges += "Downstream Apps (" + downstreamStr + ") - "
                }
            }
            if (action == 1) {
                var fileDescStr = binding.editFileDescText.text.toString()
                var approvalReq = binding.editApprovalReqCheck.isChecked
                var downstreamStr = ""
                if (binding.editClubCHeck.isChecked) downstreamStr += binding.clubCHeck.text.toString() + ", "
                if (binding.editCommCHeck.isChecked) downstreamStr += binding.commCheck.text.toString() + ", "
                if (binding.editEnvCheck.isChecked) downstreamStr += binding.envCheck.text.toString() + ", "
                if (binding.editModCheck.isChecked) downstreamStr += binding.modCheck.text.toString() + ", "
                if (binding.editIrasCheck.isChecked) downstreamStr += binding.irasCheck.text.toString() + ", "
                if (binding.editRspCheck.isChecked) downstreamStr += binding.rspCheck.text.toString() + ", "
                downstreamStr = downstreamStr.removeSuffix(", ")
                if (approvalReq && (!tblFacilityPhotos[rowId].approvalrequested)) {
                    strChanges += "Approval requested flag changed from (False) to (True) - "
                }
                if (!approvalReq && (tblFacilityPhotos[rowId].approvalrequested)) {
                    strChanges += "Approval requested flag changed from (True) to (False) - "
                }
                if (fileDescStr != tblFacilityPhotos[rowId].filedescription) {
                    strChanges += "File Description changed from (" + tblFacilityPhotos[rowId].filedescription + ") to (" + fileDescStr + ") - "
                }
                if (downstreamStr != tblFacilityPhotos[rowId].downstreamapps) {
                    strChanges += "Downstream Apps changed from (" + tblFacilityPhotos[rowId].downstreamapps + ") to (" + downstreamStr + ") - "
                }
            }
            strChanges = strChanges.removeSuffix(" - ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strChanges
    }

    fun submitPhotoDetails() {
        requireActivity().runOnUiThread {
            binding.progressBarText.text = "Saving Photo Details ..."
            binding.photoLoadingView.visibility = View.VISIBLE
        }
        var fileNameStr = binding.fileNameTitle.text.toString()
        var fileDescStr = binding.fileDescText.text.toString()
        var approvalReq = if (binding.approvalReqCheck.isChecked) 1 else 0
        var downstreamStr = ""
        if (binding.clubCHeck.isChecked) downstreamStr += binding.clubCHeck.text.toString() + ", "
        if (binding.commCheck.isChecked) downstreamStr += binding.commCheck.text.toString() + ", "
        if (binding.envCheck.isChecked) downstreamStr += binding.envCheck.text.toString() + ", "
        if (binding.modCheck.isChecked) downstreamStr += binding.modCheck.text.toString() + ", "
        if (binding.irasCheck.isChecked) downstreamStr += binding.irasCheck.text.toString() + ", "
        if (binding.rspCheck.isChecked) downstreamStr += binding.rspCheck.text.toString() + ", "
        downstreamStr = downstreamStr.removeSuffix(", ")

        var downstreamAPPStr = ""
        if (binding.clubCHeck.isChecked) downstreamAPPStr += "1,"
        if (binding.commCheck.isChecked) downstreamAPPStr += "2,"
        if (binding.irasCheck.isChecked) downstreamAPPStr += "3,"
        if (binding.rspCheck.isChecked) downstreamAPPStr += "4,"
        if (binding.envCheck.isChecked) downstreamAPPStr += "5,"
        if (binding.modCheck.isChecked) downstreamAPPStr += "6,"

        downstreamAPPStr = downstreamAPPStr.removeSuffix(",")

//        var item = PRGFacilityPhotos()
//        item.filename = fileNameStr
//        item.filedescription = fileDescStr
//        item.approvalrequested = binding.approvalReqCheck.isChecked
//        item.downstreamapps = downstreamStr
//        item.approved=false
//        item.lastupdatedate= Date().toApiSubmitFormat()
//        item.approveddate= ""
//        item.lastupdateby = ApplicationPrefs.getInstance(activity).loggedInUserID
//        item.facid = FacilityDataModel.getInstance().tblFacilities[0].FACNo
//        item.clubCode = FacilityDataModel.getInstance().clubCode.toInt()

        var itemAPP = FacilityPhotos()
        itemAPP.FileName = fileNameStr
        itemAPP.FileDescription = fileDescStr
        itemAPP.ApprovalRequested = approvalReq.toString()
        itemAPP.DownstreamAppId = downstreamAPPStr
        itemAPP.Approved="0"
        itemAPP.LastUpdateDate = Date().toApiSubmitFormat()
        itemAPP.ApprovedDate= ""
        itemAPP.SeqNum = if (FacilityDataModel.getInstance().FacilityPhotos.filter { s->s.PhotoId>0}.isEmpty()) 0 else FacilityDataModel.getInstance().FacilityPhotos.maxByOrNull { it.SeqNum }!!.SeqNum + 1
        itemAPP.LastUpdateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        Log.v("SubmitPhoto -> ", Constants.updateFacilityPhotosData + "${FacilityDataModel.getInstance().tblFacilities[0].FACID}&seqNum=${itemAPP.SeqNum}&facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&approved=0&approvedBy=&operation=ADD&downstreamAppId=${downstreamAPPStr}&lastUpdateDate=${Date().toApiSubmitFormat()}&approvedDate=&downstreamApps=${downstreamStr}&primaryPhoto=0&lastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${fileNameStr.lowercase(getDefault())}&fileDescription=${fileDescStr}&photoId=0&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)))
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotos + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&operation=ADD&downstreamApps=${downstreamStr}&lastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${fileNameStr}&fileDescription=${fileDescStr}&photoId=&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)),
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.updateFacilityPhotosData + "${FacilityDataModel.getInstance().tblFacilities[0].FACID}&seqNum=${itemAPP.SeqNum}&facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&approved=0&approvedBy=&operation=ADD&downstreamAppId=${downstreamAPPStr}&lastUpdateDate=${Date().toApiSubmitFormat()}&approvedDate=&downstreamApps=${downstreamStr}&primaryPhoto=0&lastUpdateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&fileName=${fileNameStr.lowercase(getDefault())}&fileDescription=${fileDescStr}&photoId=0&approvalRequested=${approvalReq}" + Utility.getLoggingParameters(activity, 0, getPhotosChanges(0,0)),
            { response ->
                requireActivity().runOnUiThread {
                    Log.v("SubmitPhoto -> ", "result: " + response.toString())
                    if (response.toString().contains("returnCode>0<", false)) {
                        Utility.showSubmitAlertDialog(activity, true, "Photos")
                        binding.photoLoadingView.visibility = View.GONE
                        binding.progressBarText.text = "Loading ..."
                        itemAPP.PhotoId = response.toString().substring(response.toString().indexOf("<PhotoId") + 9, response.toString().indexOf("</PhotoId")).toInt()
//                            PRGDataModel.getInstance().tblPRGFacilitiesPhotos.add(item)
//                            tblFacilityPhotos.add(item)
                        FacilityDataModel.getInstance().FacilityPhotos.add(itemAPP)
                        fillPhotosTableView()
                        getPhotosS3Urls()
                        HasChangedModel.getInstance().groupPhoto[0].Photos= true
                        HasChangedModel.getInstance().changeDoneForPhotoDef()
                    } else {
                        var errorMessage = response.toString()
                        Utility.showSubmitAlertDialog(activity, false, "Photos (Error: " + errorMessage + " )")
                    }
                    binding.photoLoadingView.visibility = View.GONE
                    binding.photosLoadingView.visibility = View.GONE
                    binding.addNewPhotoDialog.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                    (activity as FormsActivity).overrideBackButton = false
                }
            },
            {
        Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: " + it.message + " )")
        binding.photoLoadingView.visibility = View.GONE
        binding.photosLoadingView.visibility = View.GONE
        binding.addNewPhotoDialog.visibility = View.GONE
        binding.progressBarText.text = "Loading ..."
        (activity as FormsActivity).overrideBackButton = false

    }))

    }




//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == 123) { // Match your request code
//            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                // All permissions granted, proceed with your action
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)
//            } else {
//                // Permissions denied, show a message to the user
//                requireContext().toast("Permissions are required to perform this action.")
//            }
//        }
//    }

    fun compressAndResizeBitmap(bitmap: Bitmap, quality: Int = 70, maxWidth: Int = 1000, maxHeight: Int = 1000): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = if (bitmap.width > maxWidth) maxWidth else bitmap.width
        val height = if (bitmap.height > maxHeight) maxHeight else bitmap.height
        return Bitmap.createScaledBitmap(bitmap, width, (width / aspectRatio).toInt(), true)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)  // JPEG compression to reduce size
        val byteArray = outputStream.toByteArray()
//        return android.util.Base64.encodeToString(byteArray,android.util.Base64.NO_WRAP)  // Use NO_WRAP to avoid line breaks
        return Base64.getEncoder().encodeToString(byteArray)  // Use NO_WRAP to avoid line breaks
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

    fun updateDialogs() {

        if (binding.photoLoadingView != null) binding.photoLoadingView.visibility = View.GONE
        if (binding.photosLoadingView != null) binding.photosLoadingView.visibility = View.GONE

        if (binding.addNewPhotoDialog != null) binding.addNewPhotoDialog.visibility = View.GONE
        if (binding.photosPreviewDialog != null) binding.photosPreviewDialog.visibility = View.GONE
        if (binding.editPhotoDialog != null) binding.editPhotoDialog.visibility = View.GONE

    }

    private fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure the device has a camera app available to handle the intent
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                val photoFile = createImageFile() // This is the file where the image will be saved
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
//                    "${requireContext().packageName}.provider",
                    "com.inspection.android.fileprovider",
                    photoFile
                )

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error creating file for photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun createImageFile(): File {
//        // Create a temporary image file where the camera app will store the photo
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
//        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        )
//    }
}
