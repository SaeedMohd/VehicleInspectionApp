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
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.toBitmap
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.R.id.*;


import com.inspection.Utils.toast
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_aarav_photos.*
import java.io.File
import java.io.IOException
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
        }

        addNewPhotoCancelButton.setOnClickListener {
            addNewPhotoDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }


        photosPreviewDialogCloseButton.setOnClickListener {
            photosPreviewDialog.visibility = View.GONE
            photosLoadingView.visibility = View.GONE
        }
    }


    fun addTableRow() {
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT


        var tableRow = TableRow(context)

        var imageView = ImageView(context)
        imageView.setImageBitmap(photoThumbnailBitmap)
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
                var photoURI = FileProvider.getUriForFile(context!!, "com.inspection.android.fileprovider", File(photoFile.absolutePath));
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoFile.absolutePath)))
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, MainActivity.PHOTO_CAPTURE_ACTIVITY_REQUEST_ID)
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

        if (requestCode == MainActivity.PHOTO_CAPTURE_ACTIVITY_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            photoBitmap = getBitmapWithPath(mCurrentPhotoPath, false)
            photoThumbnailBitmap = getBitmapWithPath(mCurrentThumbPath, true)
            addNewPhotoDialog.visibility = View.GONE
            photosPreviewImageView.setImageBitmap(photoBitmap)
            photosPreviewDialog.visibility = View.VISIBLE
            addTableRow()
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
            addNewPhotoDialog.visibility = View.GONE
            photosPreviewImageView.setImageBitmap(photoBitmap)
            photosPreviewDialog.visibility = View.VISIBLE
            addTableRow()
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
