package com.inspection.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.inspection.MainActivity
import com.inspection.RecyclerViewClickListener
import com.inspection.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.model.SafetyCheckItemModel
import com.inspection.serverTasks.DeleteSafetyCheckPhotoTask

import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.Arrays

import com.inspection.Utils.Utility.imagesMap
import com.inspection.fragments.FragmentSafetyCheckItems
import java.io.File

/**
 * Created by sheri on 5/8/2017.
 */

class SafetyCheckItemsRecyclerViewAdapter(private val context: Context, private val parentView: FragmentSafetyCheckItems, private val safetyCheckRecyclerView: RecyclerView, private val safetyCheckItemModelArrayList: ArrayList<SafetyCheckItemModel>?, private val selectedSafetyCheckReportsID: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeNormal = 0
    private val viewTypeHeader = 1


    companion object {
        private var itemListener: RecyclerViewClickListener? = null
    }

    inner class MyItemViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var itemNameTextView: TextView
        var itemResultDescriptionTextView: TextView
        var itemCommentsTextView: TextView
        var itemPhotosLayout: LinearLayout
        var addCommentIcon: ImageView
        var addPhotoIcon: ImageView

        init {
            itemNameTextView = view.findViewById<TextView>(R.id.safetyCheckItemNameTextView)
            itemResultDescriptionTextView = view.findViewById<TextView>(R.id.safetyCheckItemResultTextView)
            itemCommentsTextView = view.findViewById<TextView>(R.id.safetyCheckItemCommentsTextView)
            itemPhotosLayout = view.findViewById(R.id.photosLayout)
            addCommentIcon = view.findViewById<ImageView>(R.id.addCommentIcon)
            addPhotoIcon = view.findViewById<ImageView>(R.id.addPhotoIcon)
            view.setOnClickListener(this)
        }


        override fun onClick(view: View) {
            parentView.recyclerViewListClicked(view, adapterPosition)

        }
    }

    inner class MyHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemHeaderTextView: TextView

        init {
            itemHeaderTextView = view.findViewById<TextView>(R.id.safetyCheckHeaderCategoryTextView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (safetyCheckItemModelArrayList!![position].safetyCheckItemID == -500) {
            return viewTypeHeader
        } else {
            return viewTypeNormal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        if (viewType == viewTypeHeader) {
            itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.safety_check_header_layout, parent, false)
            return MyHeaderViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.safety_check_item_layout, parent, false)
            return MyItemViewHolder(itemView)
        }


    }

    override fun onBindViewHolder(holderr: RecyclerView.ViewHolder, position: Int) {
        holderr.setIsRecyclable(false)
        val item = safetyCheckItemModelArrayList!![position]
        if (item.safetyCheckItemID == -500) {
            val holder = holderr as MyHeaderViewHolder
            holder.itemHeaderTextView.text = item.category
        } else {
            val holder = holderr as MyItemViewHolder
            if (holder.itemNameTextView.text.toString() == item.item) {
                return
            }


            if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
                holder.addPhotoIcon.setOnClickListener {
                    parentView.selectedItemForPhoto = position
                    parentView.checkFileAndCameraPermissions()
//                    parentView.dispatchTakePictureIntent()
                }


                holder.addCommentIcon.setOnClickListener {
                    parentView.showAddCommentDialog(position)
                }

            } else {


                holder.addCommentIcon.visibility = View.INVISIBLE
                holder.addPhotoIcon.visibility = View.INVISIBLE
            }


            holder.itemNameTextView.text = item.item
            holder.itemResultDescriptionTextView.text = item.safetyCheckItemResultDescription
            holder.itemCommentsTextView.text = item.safetyCheckItemComment

            if (item.safetyCheckItemComment != null) {
                if (item.safetyCheckItemComment.trim { it <= ' ' }.length > 1) {
                    holder.itemCommentsTextView.text = item.safetyCheckItemComment
                } else {
                    holder.itemCommentsTextView.visibility = View.GONE
                }
            } else {
                holder.itemCommentsTextView.visibility = View.GONE
            }


            if (item.safetyCheckItemPhoto != null && item.safetyCheckItemPhoto.length > 2) {
                val photosString = item.safetyCheckItemPhoto.toString().trim { it <= ' ' }
                val photosStringArray = Arrays.asList(*photosString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                var imageView: ImageView
                for (photoName in photosStringArray) {
                    Log.v("an image", "An image with photo Name: " + photoName)
                    imageView = ImageView(context)
                    imageView.layoutParams = LinearLayout.LayoutParams(200, 200)
                    imageView.setOnClickListener {
                        if (ApplicationPrefs.getInstance(context).userProfilePref.isShop) {
                            val alert = AlertDialog.Builder(MainActivity.mContext)
                            alert.setTitle("Select Action For Photo")
                            alert.setItems(arrayOf("Show", "Delete")) { dialog, which ->
                                if (which == 0) {
                                    LoadImageFromURL(photoName).execute()
                                } else if (which == 1) {
                                    object : DeleteSafetyCheckPhotoTask(context, selectedSafetyCheckReportsID, item.safetyCheckItemID, photoName) {
                                        override fun onTaskCompleted(result: String) {
                                            item.safetyCheckItemPhoto = item.safetyCheckItemPhoto.replace(" " + photoName, "").replace(photoName, "")
                                            safetyCheckRecyclerView.adapter.notifyDataSetChanged()
                                        }
                                    }.execute()
                                }
                            }
                            alert.create().show()
                        } else {
                            LoadImageFromURL(photoName).execute()
                        }
                    }
                    LoadImageFromURLAndSetImageView(photoName.replace(" ", ""), imageView).execute()
                    holder.itemPhotosLayout.addView(imageView)
                }

            } else {
                holder.itemPhotosLayout.visibility = View.GONE
            }

            holder.itemNameTextView.text = safetyCheckItemModelArrayList[position].item
            if (safetyCheckItemModelArrayList != null && safetyCheckItemModelArrayList[position].safetyCheckItemResultDescription != null && safetyCheckItemModelArrayList[position].safetyCheckItemResultDescription.length > 0) {
                holder.itemResultDescriptionTextView.text = safetyCheckItemModelArrayList[position].safetyCheckItemResultDescription
            } else {
                holder.itemResultDescriptionTextView.text = "Not Started"
            }
        }
    }

    override fun getItemCount(): Int {
        return safetyCheckItemModelArrayList!!.size
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

                val url = URL("" + context.getString(R.string.safetyCheckPhotosURL) + fileName)
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
            val imagePath = File(context.cacheDir, "images")
            val newFile = File(imagePath, fileName)
            val contentUri = FileProvider.getUriForFile(context, "com.matics.android.fileprovider", newFile)
            intent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            context.startActivity(intent)
        }
    }


    internal inner class LoadImageFromURLAndSetImageView(var photoName: String, var imageView: ImageView) : AsyncTask<Void, Void, Void>() {
        var progressDialog: ProgressDialog? = null
        var bmp: Bitmap? = null

        override fun doInBackground(vararg voids: Void): Void? {
            try {

                val url = URL("" + context.getString(R.string.safetyCheckThumbsURL) + photoName.replace("jpg", "thumb.jpg"))
                if (!imagesMap.containsKey(url.toString())) {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    imagesMap.put(url.toString(), bmp)
                } else {
                    bmp = imagesMap[url.toString()]
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
            val cachePath = File(context.cacheDir, "images")
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


}