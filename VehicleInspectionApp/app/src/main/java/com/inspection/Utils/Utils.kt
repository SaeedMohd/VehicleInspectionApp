package com.inspection.Utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.util.Xml
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.inspection.FormsActivity
import com.inspection.Utils.Constants.awsReference
import com.inspection.Utils.Constants.visitationIDForPDF
//import com.inspection.adapter.MultipartRequest
import com.inspection.fragments.Step
import com.inspection.model.*
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.xmlpull.v1.XmlSerializer
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.doAsync
import java.io.*
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonObject
//import shaded.org.json.JSONObject
//import org.json.JSONObject


/**
 * Created by sheri on 3/7/2018.
 */


val MaintitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12F, BaseColor.BLUE)
val SubtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12F, BaseColor(37, 84, 144))
val SubSubtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12F, BaseColor(18, 56, 104))
val titleFont = FontFactory.getFont(FontFactory.HELVETICA, 10F, BaseColor.BLUE)
val normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8F, BaseColor.BLACK)
val normalFontMissing = FontFactory.getFont(FontFactory.HELVETICA, 8F, BaseColor.RED)
var createPDFLogData = ""
val normalFont7 = FontFactory.getFont(FontFactory.HELVETICA, 7F, BaseColor.BLACK)
val normalFont5 = FontFactory.getFont(FontFactory.HELVETICA, 5F, BaseColor.BLACK)
val normalFont6 = FontFactory.getFont(FontFactory.HELVETICA, 6F, BaseColor.BLACK)
val normalFont7L = FontFactory.getFont(FontFactory.HELVETICA, 7F, BaseColor.BLUE)
val symbolsFont = FontFactory.getFont(FontFactory.ZAPFDINGBATS, 8F, BaseColor.BLACK)
private val apiFormat = SimpleDateFormat("yyyy-MM-dd")
private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private val appFormat = SimpleDateFormat("dd MMM yyyy")
private val appFormatMMDDYYYY = SimpleDateFormat("MM/dd/yyyy")

private val appFormatMMDDYY = SimpleDateFormat("MM/dd/yy")

private val apiSubmitFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

private val apiAWSFormat = SimpleDateFormat("yyyyMMdd")

var steps: List<Step> = listOf()
var recyclerView: RecyclerView? = null

fun String.toDate(): Date = dbFormat.parse(this)
fun String.toDateDBFormat(): Date = apiSubmitFormat.parse(this)
fun String.toDateMMDDYYYY(): Date = appFormatMMDDYYYY.parse(this)
fun String.toTime(): Long = this.toDate().time

fun String.appToDBFormat(): String = apiFormat.format(appFormat.parse(this))


fun String.apiToAppFormat(): String {
    //return if (this.equals("")) "" else appFormatMMDDYYYY.format(apiFormat.parse(this.split("T")[0]))
    return if (this.equals("")) "" else appFormat.format(apiFormat.parse(this.split("T")[0]))
}

fun String.apiToAppFormatMMDDYYYY(): String {
    return if (this.equals("")) "" else appFormatMMDDYYYY.format(apiFormat.parse(this.split("T")[0]))
}

fun String.apiToAppFormatMMDDYY(): String {
    return if (this.equals("")) "" else appFormatMMDDYY.format(apiFormat.parse(this.split("T")[0]))
}

fun String.apiToAppFormatMMDDYYYYDelimitSpace(): String {
    return if (this.equals("")) "" else appFormatMMDDYYYY.format(apiFormat.parse(this.split(" ")[0]))
}

fun Int.dpToPx(context : Context) : Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun String.appToApiFormat(): String =
    if (this.equals("")) "" else apiFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormat(): String = apiSubmitFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormatMMDDYYYY(): String =
    apiSubmitFormat.format(appFormatMMDDYYYY.parse(this))

fun Date.toAppFormat(): String = if (this.equals("")) "" else appFormat.format(this)

fun Date.toAppFormatMMDDYYYY(): String = if (this.equals("")) "" else appFormatMMDDYYYY.format(this)

fun Date.toApiFormat(): String = apiFormat.format(this)

fun Date.toApiSubmitFormat(): String = apiSubmitFormat.format(this)

fun Date.toApiAWSFormat(): String = apiAWSFormat.format(this)

fun Date.toDBFormat(): String = dbFormat.format(this)

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(getWindowToken(), 0)
}

fun MarkChangeWasDone() {
    FacilityDataModelOrg.getInstance().changeWasDone = true
    Log.v("Mark Change ---> ", "CALLED *****")
    // compare sizes
//    if (FacilityDataModel.getInstance().tblPersonnel.size != FacilityDataModelOrg.getInstance().tblPersonnel.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblAARPortalAdmin.size != FacilityDataModelOrg.getInstance().tblAARPortalAdmin.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblAffiliations.size != FacilityDataModelOrg.getInstance().tblAffiliations.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblAmendmentOrderTracking.size != FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblComments.size != FacilityDataModelOrg.getInstance().tblComments.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblComplaintFiles.size != FacilityDataModelOrg.getInstance().tblComplaintFiles.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblBilling.size != FacilityDataModelOrg.getInstance().tblBilling.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblBillingPlan.size != FacilityDataModelOrg.getInstance().tblBillingPlan.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblDeficiency.size != FacilityDataModelOrg.getInstance().tblDeficiency.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblFacilityBillingDetail.size != FacilityDataModelOrg.getInstance().tblFacilityBillingDetail.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblFacilityEmail.size != FacilityDataModelOrg.getInstance().tblFacilityEmail.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblFacilityPhotos.size != FacilityDataModelOrg.getInstance().tblFacilityPhotos.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblFacilityServices.size != FacilityDataModelOrg.getInstance().tblFacilityServices.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblInvoiceInfo.size != FacilityDataModelOrg.getInstance().tblInvoiceInfo.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblLanguage.size != FacilityDataModelOrg.getInstance().tblLanguage.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblPhone.size != FacilityDataModelOrg.getInstance().tblPhone.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblPrograms.size != FacilityDataModelOrg.getInstance().tblPrograms.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblVendorRevenue.size != FacilityDataModelOrg.getInstance().tblVendorRevenue.size) FacilityDataModelOrg.getInstance().changeWasDone = true
//    else if (FacilityDataModel.getInstance().tblVisitationTracking.size != FacilityDataModelOrg.getInstance().tblVisitationTracking.size) FacilityDataModelOrg.getInstance().changeWasDone = true

}


fun compareFacilityDataModelTable(type: String) {
    var isDifferent = false
    if (type.equals("Personnel")) {
        for (i in 0..FacilityDataModel.getInstance().tblPersonnel.size) {
            if (FacilityDataModel.getInstance().tblPersonnel[i].ContractSigner != FacilityDataModelOrg.getInstance().tblPersonnel[i].ContractSigner) isDifferent =
                true
            if (FacilityDataModel.getInstance().tblPersonnel[i].PrimaryMailRecipient != FacilityDataModelOrg.getInstance().tblPersonnel[i].PrimaryMailRecipient) isDifferent =
                true
            if (!FacilityDataModel.getInstance().tblPersonnel[i].startDate.equals(
                    FacilityDataModelOrg.getInstance().tblPersonnel[i].startDate
                )
            ) isDifferent = true
            if (FacilityDataModel.getInstance().tblPersonnel[i].email != FacilityDataModelOrg.getInstance().tblPersonnel[i].email) isDifferent =
                true
            if (FacilityDataModel.getInstance().tblPersonnel[i].RSP_Phone != FacilityDataModelOrg.getInstance().tblPersonnel[i].RSP_Phone) isDifferent =
                true
            if (FacilityDataModel.getInstance().tblPersonnel[i].ZIP != FacilityDataModelOrg.getInstance().tblPersonnel[i].ZIP) isDifferent =
                true
            if (FacilityDataModel.getInstance().tblPersonnel[i].CITY != FacilityDataModelOrg.getInstance().tblPersonnel[i].CITY) isDifferent =
                true
            if (FacilityDataModel.getInstance().tblPersonnel[i].RSP_Phone != FacilityDataModelOrg.getInstance().tblPersonnel[i].RSP_Phone) isDifferent =
                true
            if (isDifferent) break
        }
    }
}

fun Int.monthNoToName(): String {
    var monthName = ""
    when (this) {
        0 -> monthName = ""
        1 -> monthName = "January"
        2 -> monthName = "February"
        3 -> monthName = "March"
        4 -> monthName = "April"
        5 -> monthName = "May"
        6 -> monthName = "June"
        7 -> monthName = "July"
        8 -> monthName = "August"
        9 -> monthName = "September"
        10 -> monthName = "October"
        11 -> monthName = "November"
        12 -> monthName = "December"
        else -> monthName = ""
    }
    return monthName
}

fun createPDF(activity: Activity) {
    steps[4].status = "In Progress"
    activity.runOnUiThread {
        recyclerView?.adapter?.notifyItemChanged(4)
    }

    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        FirebaseCrashlytics.getInstance().log("ShopPDF - Started")
        steps[4].comments = "Shop PDF - Started"
        activity.runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(4)
        }
        try {
            createPDFForShop(activity)
            steps[4].comments = "Shop PDF - Created"
            activity.runOnUiThread {
                recyclerView?.adapter?.notifyItemChanged(4)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("ShopPDF - Error ${e.message}")
            e.printStackTrace();
            steps[4].comments = "Shop PDF - Error ${e.message}"
//            steps[4].status = "Failed"
            // to avoid stopping generating Specialist PDF
            activity.runOnUiThread {
                recyclerView?.adapter?.notifyItemChanged(4)
            }
        }
    }
    steps[4].comments = "Specialist PDF - Started"
    activity.runOnUiThread {
        recyclerView?.adapter?.notifyItemChanged(4)
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Started")
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Load Signatures - Started")

    var imageView = ImageView(activity.applicationContext)
//            .doAsync(exceptionHandler = { e ->
//                Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.logCreatePDF + createPDFLogData,
//                        Response.Listener { response ->
//                        }, Response.ErrorListener {
//                    Log.v("ERROR LOGGING", "" + it.message)
//                    it.printStackTrace()
//                }))
//            })
//    {
    createPDFLogData += "Loading Signatures"
    val imageNameRep =
        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_RepSignature_" + Calendar.getInstance()
            .get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR)
            .toString() + ".png"
    val imageNameSpec =
        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_SpecSignature_" + Calendar.getInstance()
            .get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR)
            .toString() + ".png"
    val imageNameDef =
        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_DefSignature_" + Calendar.getInstance()
            .get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR)
            .toString() + ".png"
//                val bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher);
//                var stream = ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100 , stream);

    var imageRepSignature: Image;
    var imageSpecSignature: Image;
    var imageDefSignature: Image;
    var imageWaiveSignature: Image;
    val ims = activity.assets.open("nosignatureicon.png");
    val bmp = BitmapFactory.decodeStream(ims);
    val stream = ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
    imageRepSignature = Image.getInstance(stream.toByteArray());
    imageSpecSignature = Image.getInstance(stream.toByteArray());
    imageDefSignature = Image.getInstance(stream.toByteArray());
    imageWaiveSignature = Image.getInstance(stream.toByteArray());

    if ((activity as FormsActivity).imageWaiveSignature != null) {
        try {
            var baos = ByteArrayOutputStream();
            (activity as FormsActivity).imageWaiveSignature?.compress(
                Bitmap.CompressFormat.PNG,
                70,
                baos
            );
            var imageInByte = baos.toByteArray();
            imageWaiveSignature = Image.getInstance(imageInByte)
            imageWaiveSignature.scaleToFit(5F, 5F)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("SpecialistPDF - Load WaiveSignature - Error ${e.message}")
            e.printStackTrace();
        }
    }

    if ((activity as FormsActivity).imageRepSignature != null) {
        try {
            var baos = ByteArrayOutputStream();
            (activity as FormsActivity).imageRepSignature?.compress(
                Bitmap.CompressFormat.PNG,
                70,
                baos
            );
            var imageInByte = baos.toByteArray();
            imageRepSignature = Image.getInstance(imageInByte)
            imageRepSignature.scaleToFit(5F, 5F)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("SpecialistPDF - Load RepSignature - Error ${e.message}")
            e.printStackTrace();
        }
    }

    if ((activity as FormsActivity).imageSpecSignature != null) {
        try {
            val baos = ByteArrayOutputStream();
            (activity as FormsActivity).imageSpecSignature?.compress(
                Bitmap.CompressFormat.PNG,
                70,
                baos
            );
            val imageInByte = baos.toByteArray();
            imageSpecSignature = Image.getInstance(imageInByte)
            imageSpecSignature.scaleToFit(10F, 10F)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("SpecialistPDF - Load SpecSignature - Error ${e.message}")
            e.printStackTrace();
        }
    }

    if ((activity as FormsActivity).imageDefSignature != null) {
        try {
//                    val bmpDef = Glide.with(activity)
//                            .asBitmap()
//                            .load(Constants.getImages + imageNameDef)
//                            .apply(RequestOptions().dontTransform().skipMemoryCache(true)
//                                    .diskCacheStrategy(DiskCacheStrategy.NONE))
//                            .submit()
//                            .get()
            val baos = ByteArrayOutputStream();
//                        bmpDef.compress(Bitmap.CompressFormat.PNG, 70, baos);
            (activity as FormsActivity).imageDefSignature?.compress(
                Bitmap.CompressFormat.PNG,
                70,
                baos
            );
            val imageInByte = baos.toByteArray();
            imageDefSignature = Image.getInstance(imageInByte)
            imageDefSignature.scaleToFit(10F, 10F)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("SpecialistPDF - Load DefSignature - Error ${e.message}")
            e.printStackTrace();
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Load Signatures - Completed")
    createPDFLogData += "...Done"
    try {
//                    throw Exception("Intentional Exception")
        createPDFForSpecialist(
            activity,
            imageRepSignature,
            imageSpecSignature,
            imageDefSignature,
            imageWaiveSignature
        )
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log("SpecialistPDF - Error ${e.message}")
        e.printStackTrace();
        steps[4].comments = "SpecialistPDF - Error ${e.message}"
        steps[4].status = "Failed"
        activity.runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(4)
        }
    }

}


fun createPDFForShop(activity: Activity) {
    val document = Document()
    //output file path
//    val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForShop.pdf")
    var filePath = ""
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForSpecialist.pdf")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        filePath =
            activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf";
    } else {
        filePath =
            activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf";
    }
//    val file = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf")
    val file = File(filePath)
    var writer = PdfWriter.getInstance(document, FileOutputStream(file))
    val event = HeaderFooterPageEvent()
    writer.pageEvent = event
    document.setMargins(20f, 20f, 20f, 30f)
    document.open()

    document.addTitle("AAR Visitation")
    // Headewr Section
    FirebaseCrashlytics.getInstance().log("ShopPDF - Visitation Section - Started")
    var paragraph = Paragraph("AAR Visitation", MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)
    FirebaseCrashlytics.getInstance().log("ShopPDF - Visitation Section - Completed")
    FirebaseCrashlytics.getInstance().log("ShopPDF - Facility Section - Started")
    paragraph = Paragraph(
        "Facility " + FacilityDataModel.getInstance().tblFacilities[0].FACNo + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName,
        MaintitleFont
    )
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    FirebaseCrashlytics.getInstance().log("ShopPDF - Facility Section - Completed")
    // Visitation Section
    paragraph = Paragraph("")
    paragraph.add(drawVisitaionSectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)

    FirebaseCrashlytics.getInstance().log("ShopPDF - Deficiencies Section - Started")
    paragraph = Paragraph("Deficiencies", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawDeficiencySectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)
    FirebaseCrashlytics.getInstance().log("ShopPDF - Deficiencies Section - Completed")
    FirebaseCrashlytics.getInstance().log("ShopPDF - Vendor Revenue Section - Started")
    paragraph = Paragraph("Vendor Revenue (past 12 months)", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawVendorRevenueSectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)
    FirebaseCrashlytics.getInstance().log("ShopPDF - Vendor Revenue Section - Completed")
    FirebaseCrashlytics.getInstance().log("ShopPDF - Changes Section - Started")
    paragraph = Paragraph("Changes Made", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawDataChangedSectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)
    document.close()
    FirebaseCrashlytics.getInstance().log("ShopPDF - Changes Section - Completed")
    uploadPDF(activity, file, "Shop")
}

fun createPDFForSpecialist(
    activity: Activity,
    imageRep: Image?,
    imageSpec: Image?,
    imageDef: Image?,
    imageWaive: Image?
) {
    val document = Document()
    Log.v("PDF =>" , "1")
    //output file path
    var filePath = ""
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForSpecialist.pdf")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        filePath =
            activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf";
    } else {
        filePath =
            activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf";
    }
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForSpecialist.pdf")
    val file = File(filePath)
    var writer = PdfWriter.getInstance(document, FileOutputStream(file))
    val event = HeaderFooterPageEvent()
    writer.pageEvent = event
    document.setMargins(10f, 10f, 20f, 30f)
    document.open()


    document.addTitle("AAR Visitation")
    // Headewr Section
    var paragraph = Paragraph("AAR Visitation", MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)

    paragraph = Paragraph(
        "Facility " + FacilityDataModel.getInstance().tblFacilities[0].FACNo + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName,
        MaintitleFont
    )
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "2")
    // Visitation Section
    paragraph = Paragraph("")
    paragraph.add(drawVisitaionSection(imageRep, imageSpec, imageWaive))
    document.add(paragraph)


    paragraph = Paragraph("FACILITY", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawFacilitySection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("")
    paragraph.add(drawAddressOverallSection())
    document.add(paragraph)
    Log.v("PDF =>" , "3")

//    paragraph = Paragraph("")
//    paragraph.add(drawPaymentSection())
//    document.add(paragraph)

    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawAddressSection())
    document.add(paragraph)

    paragraph = Paragraph("Personnel", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawPersonnelSection())
    document.add(paragraph)
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "4")
    paragraph = Paragraph("ASE Certifications", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawCertificationsSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Contract Signers", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawSignersSection())
    document.add(paragraph)
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "5")
    paragraph = Paragraph("RSP", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawAARHeaderSection())
    document.add(paragraph)
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.alignment = Element.ALIGN_CENTER
    paragraph.add(drawAARTrackingSection())
    document.add(paragraph)
    addEmptyLine(document, 1)


    paragraph = Paragraph("Visitation Tracking", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawVisitationTrackingSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    Log.v("PDF =>" , "6")
    paragraph = Paragraph("Scope of Service", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawSoSSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Vehicle Services", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)

    paragraph = Paragraph(
        TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeID.toInt() == 1 }[0].VehiclesTypeName,
        SubtitleFont
    )
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "7")
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Vehicle Services Section - Started")
    var vehicleTypeID = ""

//    TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.equals("Automobile") }.apply {
//        (0 until size).forEach {
//            vehicleTypeID = get(it).VehiclesTypeID
//            paragraph = Paragraph(get(it).VehiclesTypeName, MaintitleFont)
//            paragraph.alignment = Element.ALIGN_LEFT
//            document.add(paragraph)
//            document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
//            addEmptyLine(document, 1)
//            paragraph = Paragraph("")
//            paragraph.add(drawVehicleServicesSection(vehicleTypeID))
//            document.add(paragraph)
//            addEmptyLine(document, 1)
//        }
//    }
    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
        (0 until size).forEach {
//            vehicleTypeID = get(it).VehiclesTypeID
            paragraph = Paragraph("   " + get(it).VehCategoryName, SubSubtitleFont)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)
            document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
            addEmptyLine(document, 1)
            paragraph = Paragraph("")
            paragraph.add(drawVehicleServicesSection(get(it).VehCategoryID))
            document.add(paragraph)
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Vehicle Services Section - Completed")
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Vehicles Section - Started")
    addEmptyLine(document, 1)
    paragraph = Paragraph("Vehicles", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)

//    var VehCategoryName = ""
    Log.v("PDF =>" , "8")

    paragraph = Paragraph(
        TypeTablesModel.getInstance().VehiclesType.filter { s -> s.VehiclesTypeID.toInt() == 1 }[0].VehiclesTypeName,
        SubtitleFont
    )
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
        (0 until size).forEach {
            paragraph = Paragraph("   " + get(it).VehCategoryName, SubSubtitleFont)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)
            document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
            addEmptyLine(document, 1)
            paragraph = Paragraph("")
            paragraph.add(drawVehiclesSection(get(it).VehCategoryID))
            document.add(paragraph)
            addEmptyLine(document, 1)
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Vehicles Section - Started")

    paragraph = Paragraph("Programs", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.alignment = Element.ALIGN_CENTER
    paragraph.add(drawProgramsSection())
    document.add(paragraph)
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "9")
    paragraph = Paragraph("Facility Services", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.alignment = Element.ALIGN_CENTER
    paragraph.add(drawFacServicesSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Affiliations", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.alignment = Element.ALIGN_CENTER
    paragraph.add(drawAffiliationSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Deficiencies", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
//    paragraph.add(drawDeficienciesSection())
    paragraph.add(drawDeficiencySectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)

//    val defSignTable = PdfPTable(2)
//    defSignTable.addCell(addTitleCell("Signature: ",1,true,MaintitleFont))
//    imageDef?.scaleAbsolute(50F,50F)
//    val e = PdfPCell(imageDef)
//    e.border = Rectangle.NO_BORDER
//    e.horizontalAlignment = Element.ALIGN_CENTER
//    e.rowspan = 3
//    defSignTable.addCell(e)
//
//    document.add(defSignTable)
//    addEmptyLine(document, 1)
//    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
//    addEmptyLine(document, 1)
//    paragraph = Paragraph("I acknowledge all current deficiencies.", titleFont)
//    paragraph.alignment = Element.ALIGN_CENTER
//    document.add(paragraph)


    paragraph = Paragraph("Complaints", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawComplaintsSection())
    document.add(paragraph)
    addEmptyLine(document, 1)
    Log.v("PDF =>" , "10")
    createPDFLogData += " - drawPhotosSection"
    paragraph = Paragraph("Photos", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Photos Section - Started")
    // Load Faciloty Photos from PRG DB
    val table = PdfPTable(13)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Thumbnail", 2, true))
    table.addCell(addCellWithBorder("File Name", 1, true))
    table.addCell(addCellWithBorder("File Description", 2, true))
    table.addCell(addCellWithBorder("Approval Requested", 1, true))
    table.addCell(addCellWithBorder("Approved", 1, true))
    table.addCell(addCellWithBorder("Approved By", 1, true))
    table.addCell(addCellWithBorder("Approved Date", 1, true))
//    table.addCell(addCellWithBorder("Updated By", 1,true))
//    table.addCell(addCellWithBorder("Updated Date", 1,true))
    table.addCell(addCellWithBorder("Downstream Apps", 2, true))
    table.addCell(addCellWithBorder("Image URL", 2, true))
//    var tblFacilityPhotos = ArrayList<PRGFacilityPhotos>()
//    Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}",
//            Response.Listener { response ->
//                activity!!.runOnUiThread {
//                    tblFacilityPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())

    if (PRGDataModel.getInstance().tblPRGFacilitiesPhotos.size == 0 && FacilityDataModel.getInstance().FacilityPhotos.size == 0) {
        document.add(table)
        addEmptyLine(document, 1)
        paragraph = Paragraph("Visitation Comments", MaintitleFont)
        paragraph.alignment = Element.ALIGN_LEFT
        document.add(paragraph)
        document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
        addEmptyLine(document, 1)
//                        paragraph = Paragraph(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
        paragraph = Paragraph("")
        paragraph.add(drawCommentsSection())
        document.add(paragraph)
        addEmptyLine(document, 1)
        document.close()
        steps[4].comments = "Specialist PDF - Created"
        steps[4].status = "Success"
        steps[5].status = "In Progress"
        steps[6].status = "In Progress"
        steps[7].status = "In Progress"
        activity.runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(4)
            recyclerView?.adapter?.notifyItemChanged(5)
            recyclerView?.adapter?.notifyItemChanged(6)
            recyclerView?.adapter?.notifyItemChanged(7)
        }
        uploadPDF(activity, file, "Specialist")
    } else {
//                        var imageView = ImageView(activity.applicationContext)
//                                .doAsync {

        FacilityDataModel.getInstance().FacilityPhotos.apply {
            (0 until size).forEach {
                if (get(it).PhotoId > -1) {
                    Glide.with(activity)
                        .asBitmap()
                        .override(800, 600)
                        .load(get(it).imageUrl)
                        .apply(RequestOptions().dontTransform())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // Handle the Bitmap here
//                                                            imageView.setImageBitmap(resource) // Set it to your ImageView
                                val bmp = resource;
                                val baos = ByteArrayOutputStream();
                                bmp.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    70,
                                    baos
                                );
                                val imageInByte = baos.toByteArray();
                                var image = Image.getInstance(imageInByte)
                                image.scaleToFit(30F, 30F)
                                image.backgroundColor = BaseColor.WHITE
                                table.addCell(
                                    addImageWithBorder(
                                        image,
                                        2,
                                        true
                                    )
                                )
                                table.addCell(addCellWithBorder(get(it).FileName, 1, true))
                                table.addCell(addCellWithBorder(get(it).FileDescription, 2, true))
                                if (get(it).ApprovalRequested == "true") {
                                    table.addCell(addTick(true, true))
                                } else {
                                    table.addCell(addCellWithBorder(" ", 1, true))
                                }
                                if (get(it).Approved == "true") {
                                    table.addCell(addTick(true, true))
                                } else {
                                    table.addCell(addCellWithBorder(" ", 1, true))
                                }
                                table.addCell(addCellWithBorder(get(it).ApprovedBy, 1, true))
                                table.addCell(
                                    addCellWithBorder(
                                        if (get(it).ApprovedDate.apiToAppFormatMMDDYYYY()
                                                .equals("01/01/1900")
                                        ) "" else get(it).ApprovedDate.apiToAppFormatMMDDYYYY(), 1, true
                                    )
                                )
//                                                table.addCell(addCellWithBorder(get(it).lastupdateby, 1, true))
//                                                table.addCell(addCellWithBorder(if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY(), 1, true))
                                //HERE NOW
                                table.addCell(addCellWithBorder(decodeDownStreamApps(get(it).DownstreamAppId), 2, true))
                                table.addCell(
                                    addHyperLinkWithBorder(
                                        get(it).imageUrl,
                                        2,
                                        true
                                    )
                                )
                                FirebaseCrashlytics.getInstance()
                                    .log("SpecialistPDF - Photos Section - Completed")
                                if (it == size - 1) {
                                    document.add(table)
                                    addEmptyLine(document, 1)
                                    paragraph = Paragraph("Visitation Comments", MaintitleFont)
                                    paragraph.alignment = Element.ALIGN_LEFT
                                    document.add(paragraph)
                                    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
                                    addEmptyLine(document, 1)
//                                                    paragraph = Paragraph(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                                    paragraph = Paragraph("")
                                    paragraph.add(drawCommentsSection())
                                    document.add(paragraph)
                                    addEmptyLine(document, 1)
                                    document.close()
                                    steps[4].comments = "Specialist PDF - Created"
                                    steps[4].status = "Success"
                                    steps[5].status = "In Progress"
                                    steps[6].status = "In Progress"
                                    steps[7].status = "In Progress"
                                    activity.runOnUiThread {
                                        recyclerView?.adapter?.notifyItemChanged(4)
                                        recyclerView?.adapter?.notifyItemChanged(5)
                                        recyclerView?.adapter?.notifyItemChanged(6)
                                        recyclerView?.adapter?.notifyItemChanged(7)
                                    }
                                    uploadPDF(activity, file, "Specialist")
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // Handle clearing resources if needed
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                super.onLoadFailed(errorDrawable)
                                Log.v("Glide Image Error", "")
                                table.addCell(
                                    addCellWithBorder(
                                        "",
                                        2,
                                        true
                                    )
                                )
                                table.addCell(addCellWithBorder(get(it).FileName, 1, true))
                                table.addCell(addCellWithBorder(get(it).FileDescription, 2, true))
                                if (get(it).ApprovalRequested=="true") {
                                    table.addCell(addTick(true, true))
                                } else {
                                    table.addCell(addCellWithBorder(" ", 1, true))
                                }
                                if (get(it).Approved=="true") {
                                    table.addCell(addTick(true, true))
                                } else {
                                    table.addCell(addCellWithBorder(" ", 1, true))
                                }
                                table.addCell(addCellWithBorder(get(it).ApprovedBy, 1, true))
                                table.addCell(
                                    addCellWithBorder(
                                        if (get(it).ApprovedDate.apiToAppFormatMMDDYYYY()
                                                .equals("01/01/1900")
                                        ) "" else get(it).ApprovedDate.apiToAppFormatMMDDYYYY(), 1, true
                                    )
                                )
//                                                table.addCell(addCellWithBorder(get(it).lastupdateby, 1, true))
//                                                table.addCell(addCellWithBorder(if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY(), 1, true))
                                table.addCell(addCellWithBorder(decodeDownStreamApps(get(it).DownstreamAppId), 2, true))
                                table.addCell(
                                    addHyperLinkWithBorder(
                                        Constants.getImagesWithDomain + get(it).FileName,
                                        2,
                                        true
                                    )
                                )
                                FirebaseCrashlytics.getInstance()
                                    .log("SpecialistPDF - Photos Section - Completed")
                                if (it == size - 1) {
                                    document.add(table)
                                    addEmptyLine(document, 1)
                                    paragraph = Paragraph("Visitation Comments", MaintitleFont)
                                    paragraph.alignment = Element.ALIGN_LEFT
                                    document.add(paragraph)
                                    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
                                    addEmptyLine(document, 1)
//                                                    paragraph = Paragraph(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                                    paragraph = Paragraph("")
                                    paragraph.add(drawCommentsSection())
                                    document.add(paragraph)
                                    addEmptyLine(document, 1)
                                    document.close()
                                    steps[4].comments = "Specialist PDF - Created"
                                    steps[4].status = "Success"
                                    steps[5].status = "In Progress"
                                    steps[6].status = "In Progress"
                                    steps[7].status = "In Progress"
                                    activity.runOnUiThread {
                                        recyclerView?.adapter?.notifyItemChanged(4)
                                        recyclerView?.adapter?.notifyItemChanged(5)
                                        recyclerView?.adapter?.notifyItemChanged(6)
                                        recyclerView?.adapter?.notifyItemChanged(7)
                                    }
                                    uploadPDF(activity, file, "Specialist")
                                }
                            }
                        })
                }
            }
        }
        if (false) {
            PRGDataModel.getInstance().tblPRGFacilitiesPhotos.apply {
                (0 until size).forEach {
                    if (get(it).photoid > -1) {
                        Glide.with(activity)
                            .asBitmap()
                            .load(Constants.getImages + get(it).filename)
                            .apply(RequestOptions().dontTransform())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    // Handle the Bitmap here
//                                                            imageView.setImageBitmap(resource) // Set it to your ImageView
                                    val bmp = resource;
                                    val baos = ByteArrayOutputStream();
                                    bmp.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        70,
                                        baos
                                    );
                                    val imageInByte = baos.toByteArray();
                                    var image = Image.getInstance(imageInByte)
                                    image.scaleToFit(30F, 30F)
                                    table.addCell(
                                        addImageWithBorder(
                                            image,
                                            2,
                                            true
                                        )
                                    )
                                    table.addCell(addCellWithBorder(get(it).filename, 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            get(it).filedescription,
                                            2,
                                            true
                                        )
                                    )
                                    if (get(it).approvalrequested) {
                                        table.addCell(addTick(true, true))
                                    } else {
                                        table.addCell(addCellWithBorder(" ", 1, true))
                                    }
                                    if (get(it).approved) {
                                        table.addCell(addTick(true, true))
                                    } else {
                                        table.addCell(addCellWithBorder(" ", 1, true))
                                    }
                                    table.addCell(addCellWithBorder(get(it).approvedby, 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            if (get(it).approveddate.apiToAppFormatMMDDYYYY()
                                                    .equals("01/01/1900")
                                            ) "" else get(it).approveddate.apiToAppFormatMMDDYYYY(),
                                            1,
                                            true
                                        )
                                    )
//                                                table.addCell(addCellWithBorder(get(it).lastupdateby, 1, true))
//                                                table.addCell(addCellWithBorder(if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY(), 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            get(it).downstreamapps,
                                            2,
                                            true
                                        )
                                    )
                                    table.addCell(
                                        addHyperLinkWithBorder(
                                            Constants.getImagesWithDomain + get(it).filename,
                                            2,
                                            true
                                        )
                                    )
                                    FirebaseCrashlytics.getInstance()
                                        .log("SpecialistPDF - Photos Section - Completed")
                                    if (it == size - 1) {
                                        document.add(table)
                                        addEmptyLine(document, 1)
                                        paragraph = Paragraph("Visitation Comments", MaintitleFont)
                                        paragraph.alignment = Element.ALIGN_LEFT
                                        document.add(paragraph)
                                        document.add(
                                            LineSeparator(
                                                0.5f,
                                                100f,
                                                BaseColor.BLACK,
                                                0,
                                                -5f
                                            )
                                        )
                                        addEmptyLine(document, 1)
//                                                    paragraph = Paragraph(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                                        paragraph = Paragraph("")
                                        paragraph.add(drawCommentsSection())
                                        document.add(paragraph)
                                        addEmptyLine(document, 1)
                                        document.close()
                                        steps[4].comments = "Specialist PDF - Created"
                                        steps[4].status = "Success"
                                        steps[5].status = "In Progress"
                                        steps[6].status = "In Progress"
                                        steps[7].status = "In Progress"
                                        activity.runOnUiThread {
                                            recyclerView?.adapter?.notifyItemChanged(4)
                                            recyclerView?.adapter?.notifyItemChanged(5)
                                            recyclerView?.adapter?.notifyItemChanged(6)
                                            recyclerView?.adapter?.notifyItemChanged(7)
                                        }
                                        uploadPDF(activity, file, "Specialist")
                                    }
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    // Handle clearing resources if needed
                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    super.onLoadFailed(errorDrawable)
                                    Log.v("Glide Image Error", "")
                                    table.addCell(
                                        addCellWithBorder(
                                            "",
                                            2,
                                            true
                                        )
                                    )
                                    table.addCell(addCellWithBorder(get(it).filename, 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            get(it).filedescription,
                                            2,
                                            true
                                        )
                                    )
                                    if (get(it).approvalrequested) {
                                        table.addCell(addTick(true, true))
                                    } else {
                                        table.addCell(addCellWithBorder(" ", 1, true))
                                    }
                                    if (get(it).approved) {
                                        table.addCell(addTick(true, true))
                                    } else {
                                        table.addCell(addCellWithBorder(" ", 1, true))
                                    }
                                    table.addCell(addCellWithBorder(get(it).approvedby, 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            if (get(it).approveddate.apiToAppFormatMMDDYYYY()
                                                    .equals("01/01/1900")
                                            ) "" else get(it).approveddate.apiToAppFormatMMDDYYYY(),
                                            1,
                                            true
                                        )
                                    )
//                                                table.addCell(addCellWithBorder(get(it).lastupdateby, 1, true))
//                                                table.addCell(addCellWithBorder(if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY(), 1, true))
                                    table.addCell(
                                        addCellWithBorder(
                                            get(it).downstreamapps,
                                            2,
                                            true
                                        )
                                    )
                                    table.addCell(
                                        addHyperLinkWithBorder(
                                            Constants.getImagesWithDomain + get(it).filename,
                                            2,
                                            true
                                        )
                                    )
                                    FirebaseCrashlytics.getInstance()
                                        .log("SpecialistPDF - Photos Section - Completed")
                                    if (it == size - 1) {
                                        document.add(table)
                                        addEmptyLine(document, 1)
                                        paragraph = Paragraph("Visitation Comments", MaintitleFont)
                                        paragraph.alignment = Element.ALIGN_LEFT
                                        document.add(paragraph)
                                        document.add(
                                            LineSeparator(
                                                0.5f,
                                                100f,
                                                BaseColor.BLACK,
                                                0,
                                                -5f
                                            )
                                        )
                                        addEmptyLine(document, 1)
//                                                    paragraph = Paragraph(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                                        paragraph = Paragraph("")
                                        paragraph.add(drawCommentsSection())
                                        document.add(paragraph)
                                        addEmptyLine(document, 1)
                                        document.close()
                                        steps[4].comments = "Specialist PDF - Created"
                                        steps[4].status = "Success"
                                        steps[5].status = "In Progress"
                                        steps[6].status = "In Progress"
                                        steps[7].status = "In Progress"
                                        activity.runOnUiThread {
                                            recyclerView?.adapter?.notifyItemChanged(4)
                                            recyclerView?.adapter?.notifyItemChanged(5)
                                            recyclerView?.adapter?.notifyItemChanged(6)
                                            recyclerView?.adapter?.notifyItemChanged(7)
                                        }
                                        uploadPDF(activity, file, "Specialist")
                                    }
                                }
                            })

                    }
                }
            }
        }
//                                }
    }
//                }
//            }, Response.ErrorListener {
//        Log.v("Loading error", "" + it.message)
//        it.printStackTrace()
//        document.add(table)
//        addEmptyLine(document, 1)
//        document.close()
//        uploadPDF(activity, file, "Specialist")
//    }))


}

fun drawCommentsSection(): PdfPTable {
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Comments Section - Started")
    val commentsTable = PdfPTable(1)
    commentsTable.setWidthPercentage(100f)
    val cell = PdfPCell(
        Paragraph(
            (PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments),
            normalFont
        )
    );
    cell.colspan = 1
    cell.setBorder(Rectangle.NO_BORDER);
    commentsTable.addCell(cell)
//    commentsTable.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments, 1,false))
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Comments Section - Completed")
    return commentsTable
}

fun uploadPDF(activity: Activity, file: File, type: String) {
    FirebaseCrashlytics.getInstance().log("ShopPDF - Upload PDF - Started")
    var email = ApplicationPrefs.getInstance(activity).loggedInUserEmail
    if (type.equals("Shop")) {
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf && PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto.isNotEmpty()) {
            email = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto
        }
    }
    var facNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo
    var visitationType = PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype
    var waived =
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) 'Y' else 'N'
    var waivedComments =
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments else ""
//    var sendPDF = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf) 'Y' else 'N'
    var emailPDF = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf) "1" else "0"
    var busName =
        URLEncoder.encode(FacilityDataModel.getInstance().tblFacilities[0].BusinessName, "UTF-8");
    var awsRef = URLEncoder.encode(awsReference, "UTF-8");
    var directorEmail =
        if (!PRGDataModel.getInstance().tblPRGFacilityDirectors.isNullOrEmpty()) PRGDataModel.getInstance().tblPRGFacilityDirectors[0].directoremail else ""
    val awsFileName =
        FacilityDataModel.getInstance().tblFacilities[0].FACID.toString() + "_101_" + Constants.visitationIDForPDF + "_" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf"
    Log.v(
        "UploadFile->",
        Constants.uploadFile + email + "&facID=${FacilityDataModel.getInstance().tblFacilities[0].FACID.toString()}&awsFileName=${awsFileName}&docID=0&fieldID=101&reference=${awsRef}&visitationID=${visitationIDForPDF}&emailPDF=${emailPDF}&director=${directorEmail}&waived=${waived}&type=${type}&specialistEmail=" + ApplicationPrefs.getInstance(
            activity
        ).loggedInUserEmail + "&facName=${busName}&facNo=${facNo}&visitationType=${visitationType}&waivedComments=${waivedComments}&updateBy=${
            ApplicationPrefs.getInstance(
                activity
            ).getLoggedInUserID()
        }&sessionId=" + ApplicationPrefs.getInstance(activity).getSessionID()
    )
    uploadPDFWithOkHttp(
        file,
        Constants.uploadFile + email + "&facID=${FacilityDataModel.getInstance().tblFacilities[0].FACID.toString()}&awsFileName=${awsFileName}&docID=0&fieldID=101&reference=${awsRef}&visitationID=${visitationIDForPDF}&emailPDF=${emailPDF}&director=${directorEmail}&waived=${waived}&type=${type}&specialistEmail=" + ApplicationPrefs.getInstance(
            activity
        ).loggedInUserEmail + "&facName=${busName}&facNo=${facNo}&visitationType=${visitationType}&waivedComments=${waivedComments}&updateBy=${
            ApplicationPrefs.getInstance(
                activity
            ).getLoggedInUserID()
        }&sessionId=" + ApplicationPrefs.getInstance(activity).getSessionID()
    ) {
        Log.v("Upload PDF", it.toString())
        activity.runOnUiThread {
            if (it.toString().contains("Error", false) || it.toString().contains("Failed", false)) {
                var pdfUplodStr = ""
                var awsUplodStr = ""
                var appLinkStr = ""
                if (it.toString().contains("PDFUpload")) pdfUplodStr = it.toString().substring(
                    it.toString().indexOf("PDFUpload:[") + 11,
                    it.toString().indexOf("]")
                )
                if (it.toString().contains("AWSUpload")) awsUplodStr = it.toString().substring(
                    it.toString().indexOf("AWSUpload:[") + 11,
                    it.toString().indexOf("APPLink") - 1
                )
                if (it.toString().contains("APPLink")) appLinkStr = it.toString().substring(
                    it.toString().indexOf("APPLink:[") + 9,
                    it.toString().lastIndexOf("]")
                )
                if (pdfUplodStr.equals("")) pdfUplodStr = it.toString()
                if (awsUplodStr.equals("")) awsUplodStr = it.toString()
                if (appLinkStr.equals("")) appLinkStr = it.toString()
//                it.toString().substring(it.toString().indexOf("PDFUpload:[")+11, it.toString().indexOf("]"))
//                it.toString().substring(it.toString().indexOf("AWSUpload:[")+11, it.toString().lastIndexOf("]"))
                if (!type.equals("Shop")) {
                    steps[5].status =
                        if (pdfUplodStr.contains("Error", true) || pdfUplodStr.contains(
                                "Failed",
                                true
                            )
                        ) "Failed" else "Success"
                    steps[5].comments = pdfUplodStr
                    steps[6].status = if (awsUplodStr.contains("Error")) "Failed" else "Success"
                    steps[6].comments = awsUplodStr
                    steps[7].status = if (appLinkStr.contains("Error")) "Failed" else "Success"
                    steps[7].comments = appLinkStr
                    activity.runOnUiThread {
                        recyclerView?.adapter?.notifyItemChanged(5)
                        recyclerView?.adapter?.notifyItemChanged(6)
                        recyclerView?.adapter?.notifyItemChanged(7)
                    }
                }
//                Utility.showMessageDialog(activity, "Error", "Uploading PDF Failed with error (" + it + ")")
            } else {
                if (!type.equals("Shop")) {
                    steps[5].status = "Success"
                    steps[6].status = "Success"
                    steps[7].status = "Success"
//                steps[5].comments = it.toString()
                    activity.runOnUiThread {
                        recyclerView?.adapter?.notifyItemChanged(5)
                        recyclerView?.adapter?.notifyItemChanged(6)
                        recyclerView?.adapter?.notifyItemChanged(7)
                    }
                }

//                if (type.equals("Specialist")) {
//                    FirebaseCrashlytics.getInstance().log("Create PDF For Specialist - Upload PDF - Completed")
//                    uploadToAWS(FacilityDataModel.getInstance().tblFacilities[0].FACID.toString() + "_101_" + Constants.visitationIDForPDF + "_" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf" ,file,activity)
//                } else {
//                    FirebaseCrashlytics.getInstance().log("ShopPDF - Upload PDF - Completed")
//                }
            }
        }
    }

//    val multipartRequest = MultipartRequest(Constants.uploadFile+email+"&emailPDF=${emailPDF}&director=${directorEmail}&waived=${waived}&type=${type}&specialistEmail="+ApplicationPrefs.getInstance(activity).loggedInUserEmail+"&facName=${busName}&facNo=${facNo}&visitationType=${visitationType}&waivedComments=${waivedComments}&sessionId="+ApplicationPrefs.getInstance(activity).getSessionID(), null, file, Response.Listener { response ->
////    val multipartRequest = MultipartRequest(Constants.uploadFile+"saeed@pacificresearchgroup.com&type=${type}", null, file, Response.Listener { response ->
//        Log.v("AWS "," ==> START")
//        FirebaseCrashlytics.getInstance().log("ShopPDF - Upload PDF - Completed")
//        if (type.equals("Specialist")) {
//            FirebaseCrashlytics.getInstance().log("Create PDF For Specialist - Upload PDF - Completed")
//            uploadToAWS(FacilityDataModel.getInstance().tblFacilities[0].FACID.toString() + "_101_" + Constants.visitationIDForPDF + "_" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf" ,file,activity)
//        } else {
//            FirebaseCrashlytics.getInstance().log("ShopPDF - Upload PDF - Completed")
//        }
//    }, Response.ErrorListener {
//    })
//    val socketTimeout = 30000//30 seconds
//    val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//    multipartRequest.retryPolicy = policy
//    Volley.newRequestQueue(activity).add(multipartRequest)
}

fun uploadPDFWithOkHttp(file: File, url: String, callback: (String?) -> Unit) {
    val client = OkHttpClient()
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file", file.name,
            file.asRequestBody("application/pdf".toMediaTypeOrNull())
        )
        .build()

    val request = okhttp3.Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                callback(response.body?.string())
            } else {
                callback("Error: ${response.body}")
            }
        }
    })
}

private fun decodeDownStreamApps(strApps : String) : String {
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

private fun drawVisitaionSectionForShop(): PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Facility Representative's Name:", 1, false));
    table.addCell(
        addCell(
            PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep,
            1,
            false
        )
    );
    table.addCell(addCell("Automotive Specialist:", 1, false));
    table.addCell(
        addCell(
            PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist,
            1,
            false
        )
    );
    table.addCell(addCell("Visitation Type: ", 1, false));
    table.addCell(
        addCell(
            PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype,
            1,
            false
        )
    );
    table.addCell(addCell("Date of Visitation: ", 1, false));
    table.addCell(addCell(Date().toAppFormatMMDDYYYY(), 1, false));

    table.addCell(addCell("Visitation Reason: ", 1, false));
    table.addCell(
        addCell(
            PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason,
            1,
            false
        )
    );
    table.addCell(addCell("Visitation Method: ", 1, false));
    table.addCell(
        addCell(
            PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod,
            1,
            false
        )
    );

    table.addCell(
        addCell(
            "Data Changes Made: " + if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes",
            4,
            false
        )
    );
    return table
}


private fun drawVisitaionSection(
    imageRep: Image?,
    imageSpec: Image?,
    imageWaiver: Image?
): PdfPTable {
    createPDFLogData += " - drawVisitaionSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Visitation Section - Started")
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
//    table.addCell(addCell("Type of Inspection: " + FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),1,false));
    table.addCell(
        addCell(
            "Type of Inspection: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Month Due: " + FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.toInt()
                .monthNoToName(), 1, false
        )
    );
    table.addCell(
        addCell(
            "Changes Made: " + if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes",
            1,
            false
        )
    )
    table.addCell(addCell("Date of Visitation: " + Date().toAppFormatMMDDYYYY(), 1, false));

    table.addCell(
        addCell(
            "Visitation Reason: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason,
            2,
            false
        )
    );
    table.addCell(
        addCell(
            "Visitation Method: " + if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) "NA" else PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod,
            2,
            false
        )
    );

    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        table.addCell(addCell("Facility Representative's Name: ", 1, false));
        table.addCell(
            addCell(
                PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep,
                1,
                false
            )
        );
    }
    table.addCell(addCell("Automotive Specialist:", 1, false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist, 1, false));

    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("In Person"))
            table.addCell(addCell("Facility Representative's Signature:", 2, false));
        else
            table.addCell(addCell("", 2, false));
    } else {
        table.addCell(addCell("", 2, false));
        table.addCell(addCell("Waiver Comments:", 1, false));
        table.addCell(
            addCell(
                FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments.toString(),
                3,
                false
            )
        );
    }
//    table.addCell(addCell("Specialist's Signature:",2,false));
    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        table.addCell(addCell("Specialist Signature:", 2, false));
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("In Person")) {
            imageRep?.scaleAbsolute(50F, 50F)
            val c = PdfPCell(imageRep)
            c.colspan = 2
            c.border = Rectangle.NO_BORDER
            c.horizontalAlignment = Element.ALIGN_CENTER
            c.rowspan = 3
            table.addCell(c)
        } else {
//            table.addCell(addCell("",2,false));
            val c = PdfPCell()
            c.colspan = 2
            c.border = Rectangle.NO_BORDER
            c.horizontalAlignment = Element.ALIGN_CENTER
            c.rowspan = 3
            table.addCell(c)
        }
        imageSpec?.scaleAbsolute(50F, 50F)
        val d = PdfPCell(imageSpec)
        d.colspan = 2
        d.border = Rectangle.NO_BORDER
        d.horizontalAlignment = Element.ALIGN_CENTER
        d.rowspan = 3
        table.addCell(d)
    } else {
        table.addCell(addCell("Waiver Signature:", 2, false));
        table.addCell(addCell("", 2, false));
        imageWaiver?.scaleAbsolute(50F, 50F)
        val d = PdfPCell(imageWaiver)
        d.colspan = 2
        d.border = Rectangle.NO_BORDER
        d.horizontalAlignment = Element.ALIGN_CENTER
        d.rowspan = 3
        table.addCell(d)
        table.addCell(addCell("", 2, false));
    }
//    imageSpec?.scaleAbsolute(50F,50F)
//    val d = PdfPCell(imageSpec)
//    d.colspan = 2
//    d.border = Rectangle.NO_BORDER
//    d.horizontalAlignment = Element.ALIGN_CENTER
//    d.rowspan = 3
//    table.addCell(d)

//    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature.toString(),1,false));
//    if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
//        table.addCell(addCell("",1,false));
//    }
    createPDFLogData += "...Done"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Visitation Section - Completed")
    return table
}

private fun drawAARHeaderSection(): PdfPTable {
    createPDFLogData += " - drawAARHeaderSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - AARHeader Section - Started")
    val table = PdfPTable(4)
//    table.headerRows = 1
    table.setWidthPercentage(100f)
    table.addCell(addCell("Start Date:", 1, true));
    table.addCell(addCell("End Date:", 1, true));
    table.addCell(addCell("Addendum Signed Date:", 1, true));
    table.addCell(addCell("#of Card Readers:", 1, true));
    table.addCell(
        addCell(
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY(),
            1,
            true
        )
    )
    table.addCell(
        addCell(
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY(),
            1,
            true
        )
    )
    table.addCell(
        addCell(
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY(),
            1,
            true
        )
    )
    table.addCell(
        addCell(
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders,
            1,
            true
        )
    );
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - AARHeader Section - Completed")
    return table
}


private fun drawAddressOverallSection(): PdfPTable {
    createPDFLogData += " - drawAddressOverallSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Address Overall Section - Started")
    val columnWidths = floatArrayOf(5f, 1f, 5f, 1f, 10f)
    val table = PdfPTable(columnWidths)
    table.setWidthPercentage(100f)
    table.addCell(addTableInCell(drawPaymentSection(), 1, true));
    table.addCell(addCell("", 1, true));
    table.addCell(addTableInCell(drawLanguageSection(), 1, true));
    table.addCell(addCell("", 1, true));
    table.addCell(addTableInCell(drawHoursSection(), 1, true));
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Address Overall Section - Completed")
    return table
}

private fun drawAARTrackingSection(): PdfPTable {
    createPDFLogData += " - drawAARTrackingSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - AARTracking Section - Started")
    val table = PdfPTable(5)
    table.headerRows = 1
    table.setWidthPercentage(70f)
    table.addCell(addCellWithBorder("RSP Inspection Date", 1, true))
    table.addCell(addCellWithBorder("Logged Into RSP", 1, true))
    table.addCell(addCellWithBorder("# Unacknowledged Tows", 1, true))
    table.addCell(addCellWithBorder("In Progress Tows", 1, true))
    table.addCell(addCellWithBorder("In Progress Walk Ins", 1, true))
//    table.addCell(addCell("", 1,true))
//    table.addCell(addCell("", 1,true))
    FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending { it.PortalInspectionDate })
        .apply {
            (0 until size).forEach {
                if (!get(it).TrackingID.equals("-1")) {
                    table.addCell(
                        addCellWithBorder(
                            get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY(),
                            1,
                            true
                        )
                    )
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).LoggedIntoPortal.equals("true")) "Yes" else "No",
                            1,
                            true
                        )
                    )
                    table.addCell(addCellWithBorder(get(it).NumberUnacknowledgedTows, 1, true))
                    table.addCell(addCellWithBorder(get(it).InProgressTows, 1, true))
                    table.addCell(addCellWithBorder(get(it).InProgressWalkIns, 1, true))
//                table.addCell(addCell("",1,true))
//                table.addCell(addCell("",1,true))
                }
            }
        }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - AARTracking Section - Completed")
    return table

}

private fun drawProgramsSection(): PdfPTable {
    createPDFLogData += " - drawProgramsSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Programs Section - Started")
    val table = PdfPTable(7)
    table.headerRows = 1
    table.setWidthPercentage(80f)
//    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Program Name", 2, false))
    table.addCell(addCellWithBorder("Effective Date", 1, true))
    table.addCell(addCellWithBorder("Expiration Date", 1, true))
    table.addCell(addCellWithBorder("Comments", 3, false))
    FacilityDataModel.getInstance().tblPrograms.apply {
        (0 until size).forEach {
            if (!get(it).ProgramID.equals("-1")) {
                if (TypeTablesModel.getInstance().ProgramsType.filter { s ->
                        s.ProgramTypeID.equals(
                            get(it).ProgramTypeID
                        )
                    }.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ProgramsType.filter { s ->
                        s.ProgramTypeID.equals(
                            get(it).ProgramTypeID
                        )
                    }[0].ProgramTypeName, 2, false))
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).effDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(addCellWithBorder(get(it).Comments, 3, false))
                }
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Programs Section - Completed")
    return table
}

private fun drawComplaintsSection(): PdfPTable {
    createPDFLogData += " - drawComplaintsSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Complaints Section - Started")
    val table = PdfPTable(9)
    table.headerRows = 1
    table.setWidthPercentage(100f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Complaint ID", 2, false))
    table.addCell(addCellWithBorder("First Name", 1, true))
    table.addCell(addCellWithBorder("Last Name", 1, true))
    table.addCell(addCellWithBorder("Received Date", 1, false))
    table.addCell(addCellWithBorder("Complaint Reason", 2, false))
    table.addCell(addCellWithBorder("Complaint Resolution", 2, false))
    FacilityDataModel.getInstance().tblComplaintFiles.apply {
        (0 until size).forEach {
            if (!get(it).ComplaintID.isNullOrEmpty()) {
                table.addCell(addCellWithBorder(get(it).ComplaintID, 2, false))
                table.addCell(addCellWithBorder(get(it).FirstName, 1, false))
                table.addCell(addCellWithBorder(get(it).LastName, 1, false))
                table.addCell(
                    addCellWithBorder(
                        if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                )
                table.addCell(addCellWithBorder(get(it).ComplaintReasonName, 2, false))
                table.addCell(addCellWithBorder(get(it).ComplaintResolutionName, 2, false))
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Complaints Section - Completed")
    return table
}


private fun drawDeficienciesSection(): PdfPTable {
    val table = PdfPTable(9)
    table.setWidthPercentage(100f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCell("", 2, false))
    table.addCell(addCell("Deficient", 1, true))
    table.addCell(addCell("", 2, false))
    table.addCell(addCell("Deficient", 1, true))
    table.addCell(addCell("", 2, false))
    table.addCell(addCell("Deficient", 1, true))
    TypeTablesModel.getInstance().AARDeficiencyType.apply {
        (0 until size).forEach {
            table.addCell(addCell(get(it).DeficiencyName, 2, false))
            if (FacilityDataModel.getInstance().tblDeficiency.filter { s ->
                    s.DefTypeID.equals(
                        get(
                            it
                        ).DeficiencyTypeID
                    )
                }.filter { s -> s.ClearedDate.isNullOrEmpty() }.isNotEmpty()) {
                table.addCell(addCell(" X ", 1, true))
            } else {
                table.addCell(addCell(" ", 1, true))
            }

        }
    }
    if (TypeTablesModel.getInstance().AARDeficiencyType.size % 3 > 0) {
        table.addCell(
            addCell(
                " ",
                (TypeTablesModel.getInstance().AARDeficiencyType.size % 3) * 2,
                false
            )
        )
    }
    return table
}

private fun drawFacServicesSection(): PdfPTable {
    createPDFLogData += " - drawFacServicesSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Facility Services Section - Started")
    val table = PdfPTable(7)
    table.headerRows = 1
    table.setWidthPercentage(80f)
//    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Service Name", 2, false))
    table.addCell(addCellWithBorder("Effective Date", 1, true))
    table.addCell(addCellWithBorder("Expiration Date", 1, true))
    table.addCell(addCellWithBorder("Comments", 3, false))
    FacilityDataModel.getInstance().tblFacilityServices.apply {
        (0 until size).forEach {
            if (!get(it).FacilityServicesID.equals("-1")) {
                if (TypeTablesModel.getInstance().ServicesType.filter { s ->
                        s.ServiceTypeID.equals(
                            get(it).ServiceID
                        )
                    }.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ServicesType.filter { s ->
                        s.ServiceTypeID.equals(
                            get(it).ServiceID
                        )
                    }[0].ServiceTypeName, 2, false))
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).effDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(addCellWithBorder(get(it).Comments, 3, false))
                }
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Facility Services Section - Completed")
    return table
}

private fun drawAffiliationSection(): PdfPTable {
    createPDFLogData += " - drawAffiliationSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Affiliation Section - Started")
    val table = PdfPTable(9)
    table.headerRows = 1
    table.setWidthPercentage(90f)
//    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Affiliation Name", 2, false))
    table.addCell(addCellWithBorder("Affiliation Details", 2, false))
    table.addCell(addCellWithBorder("Effective Date", 1, true))
    table.addCell(addCellWithBorder("Expiration Date", 1, true))
    table.addCell(addCellWithBorder("Comments", 3, false))
    FacilityDataModel.getInstance().tblAffiliations.apply {
        (0 until size).forEach {
            if (!get(it).AffiliationID.equals("-1")) {
                if (TypeTablesModel.getInstance().AARAffiliationType.filter { s ->
                        s.AARAffiliationTypeID.equals(
                            get(it).AffiliationTypeID
                        )
                    }.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARAffiliationType.filter { s ->
                        s.AARAffiliationTypeID.equals(
                            get(it).AffiliationTypeID
                        )
                    }[0].AffiliationTypeName, 2, false))
                    if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s ->
                            s.AffiliationTypeDetailID.equals(
                                get(it).AffiliationTypeDetailID
                            )
                        }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AffiliationDetailType.filter { s ->
                            s.AffiliationTypeDetailID.equals(
                                get(it).AffiliationTypeDetailID
                            )
                        }[0].AffiliationDetailTypeName, 2, false))
                    } else {
                        table.addCell(addCellWithBorder("", 2, false))
                    }
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).effDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(
                        addCellWithBorder(
                            if (get(it).expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    )
                    table.addCell(addCellWithBorder(get(it).comment, 3, false))
                }
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Affiliation Section - Completed")
    return table
}

private fun drawVisitationTrackingSection(): PdfPTable {
    createPDFLogData += " - drawVisitationTrackingSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Visitation Tracking Section - Started")
    val columnWidths = floatArrayOf(5f, 5f, 5f, 5f, 10f, 10f, 10f, 10f, 10f)
    val table = PdfPTable(columnWidths)
    table.headerRows = 1
//    val table = PdfPTable(9)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Date Performed", 1, true))
    table.addCell(addCellWithBorder("Visitation Type", 1, true))
    table.addCell(addCellWithBorder("Deficiency (Yes/No)", 1, true))
    table.addCell(addCellWithBorder("Performed By", 1, true))
    table.addCell(addCellWithBorder("AAR Sign", 1, true))
    table.addCell(addCellWithBorder("Certificate of Approval", 1, true))
    table.addCell(addCellWithBorder("Member Benefits Poster(s)", 1, true))
    table.addCell(addCellWithBorder("Quality Control Process", 1, true))
    table.addCell(addCellWithBorder("Staff Training Process", 1, true))
    var visitationType = ""
    if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals("00")) {
        try {
            if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }
                    .isNotEmpty()) {
                FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed })
                    .apply {
                        (0 until size).forEach {
                            if (!get(it).performedBy.equals("00")) {
                                table.addCell(
                                    addCellWithBorder(
                                        if (get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                                                .equals("01/01/1900")
                                        ) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY(),
                                        1,
                                        true
                                    )
                                );
//                            table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(), 1, true));
                                visitationType = ""
                                if (get(it).VisitationTypeID.equals("1")) {
                                    visitationType = VisitationTypes.Annual.toString()
                                } else if (get(it).VisitationTypeID.equals("2")) {
                                    visitationType = VisitationTypes.Quarterly.toString()
                                } else if (get(it).VisitationTypeID.equals("3")) {
                                    visitationType = VisitationTypes.AdHoc.toString()
                                } else if (get(it).VisitationTypeID.equals("4")) {
                                    visitationType = VisitationTypes.Deficiency.toString()
                                }
                                table.addCell(addCellWithBorder(visitationType, 1, true));
                                table.addCell(addCellWithBorder("", 1, true));
                                table.addCell(addCellWithBorder(get(it).performedBy, 1, true))
                                table.addCell(addCellWithBorder(get(it).AARSigns, 1, false))
                                table.addCell(
                                    addCellWithBorder(
                                        get(it).CertificateOfApproval,
                                        1,
                                        false
                                    )
                                )
                                table.addCell(
                                    addCellWithBorder(
                                        get(it).MemberBenefitPoster,
                                        1,
                                        false
                                    )
                                )
                                table.addCell(addCellWithBorder(get(it).QualityControl, 1, false))
                                table.addCell(addCellWithBorder(get(it).StaffTraining, 1, false))
                            }
                        }
                    }
            }
        } catch (e: Exception) {

        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Visitation Tracking Section - Completed")
    return table
}


private fun drawSoSSection(): PdfPTable {
    createPDFLogData += " - drawSoSSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - SoS Section - Started")
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(
        addCell(
            "Fixed Labor Rate: $" + FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate,
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Diagnostic Rate: $" + FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate,
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Labor Rate Matrix Min: $" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMin,
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Labor Rate Matrix Min: $" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMax,
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Number of Bays: " + FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays,
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Number of Lifts: " + FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts,
            1,
            true
        )
    );
    table.addCell(addCell("Warranty Period: " + if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s ->
            s.WarrantyTypeID.equals(
                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID
            )
        }.size > 0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s ->
        s.WarrantyTypeID.equals(
            FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID
        )
    }[0].WarrantyTypeName else "", 1, true));
    table.addCell(
        addCell(
            "Discount Percentage: " + FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap + "%",
            1,
            true
        )
    );
    table.addCell(
        addCell(
            "Max Discount Amount: " + FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount,
            1,
            true
        )
    );
//    table.addCell(addCell("",2,true));
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - SoS Section - Completed")
    return table
}


private fun drawFacilitySection(): PdfPTable {
    createPDFLogData += " - drawFacilitySection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Facility Section - Started")
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Contract Number: ", 1, false));
    table.addCell(
        addCell(
            "Contract Type: " + FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Office: " + FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Assigned To: " + FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "DBA: " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Entity Name: " + FacilityDataModel.getInstance().tblFacilities[0].EntityName,
            1,
            false
        )
    );
    table.addCell(addCell("Business Type: " + TypeTablesModel.getInstance().BusinessType.filter { s ->
        s.BusTypeID.equals(
            FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString()
        )
    }[0].BusTypeName, 1, false));
    table.addCell(
        addCell(
            "Time Zone: " + FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Website URL: " + FacilityDataModel.getInstance().tblFacilities[0].WebSite,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Wi-Fi Available: " + if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess) "Yes" else "No",
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Tax ID: " + FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Repair Order Count: " + FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Annual Inspection Month: " + FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Inspection Cycle: " + FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Service Availability: " + if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability }.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability }[0].SrvAvaName else "Undetermined",
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Facility Type: " + FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "ARD Number: " + FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "ARD Expiration Date: " + if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY(),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Provider Type: " + if (!FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId.equals(
                    "-1"
                )
            ) FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId else "",
            1,
            false
        )
    );
    table.addCell(addCell("Shop Management System: ", 1, false));
    table.addCell(
        addCell(
            "Current Contract Date: " + FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Initial Contract Date: " + FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Billing Month: " + FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Billing Amount: $" + "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),
            1,
            false
        )
    );
    table.addCell(
        addCell(
            "Insurance Expiration Date: " + if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),
            2,
            false
        )
    );
    table.addCell(addCell("", 2, false))
    createPDFLogData += "...Done"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Facility Section - Completed")
    return table
}

private fun drawHoursSection(): PdfPTable {
    createPDFLogData += " - drawHoursSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Hours Section - Started")
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    val hoursTable = PdfPTable(4)
    hoursTable.setWidthPercentage(100f)
    hoursTable.addCell(addCell("HOURS", 1, false))
    hoursTable.addCell(addCell("Open Time", 1, false))
    hoursTable.addCell(addCell("Close Time", 1, false))
    hoursTable.addCell(addCell("Night Drop:", 1, false))
    FacilityDataModel.getInstance().tblHours[0].apply {
        hoursTable.addCell(addCell("Sun", 1, false))
        hoursTable.addCell(addCell(if (SunOpen.isNullOrEmpty()) "Closed" else SunOpen, 1, false))
        hoursTable.addCell(addCell(if (SunClose.isNullOrEmpty()) "Closed" else SunClose, 1, false))
        hoursTable.addCell(addCell(if (NightDrop) "True" else "False", 1, false))
        hoursTable.addCell(addCell("Mon", 1, false))
        hoursTable.addCell(addCell(if (MonOpen.isNullOrEmpty()) "Closed" else MonOpen, 1, false))
        hoursTable.addCell(addCell(if (MonClose.isNullOrEmpty()) "Closed" else MonClose, 1, false))
        hoursTable.addCell(addCell(" ", 1, false))
        hoursTable.addCell(addCell("Tue", 1, false))
        hoursTable.addCell(addCell(if (TueOpen.isNullOrEmpty()) "Closed" else TueOpen, 1, false))
        hoursTable.addCell(addCell(if (TueClose.isNullOrEmpty()) "Closed" else TueClose, 1, false))
        val cell = PdfPCell(
            Paragraph(
                ("Nigh Drop Instructions:" + if (NightDropInstr.isNullOrEmpty()) "" else NightDropInstr),
                normalFont
            )
        );
        cell.colspan = 1
        cell.setBorder(Rectangle.NO_BORDER);
        cell.rowspan = 5
        hoursTable.addCell(cell)
        hoursTable.addCell(addCell("Wed", 1, false))
        hoursTable.addCell(addCell(if (WedOpen.isNullOrEmpty()) "Closed" else WedOpen, 1, false))
        hoursTable.addCell(addCell(if (WedClose.isNullOrEmpty()) "Closed" else WedClose, 1, false))
        hoursTable.addCell(addCell("Thu", 1, false))
        hoursTable.addCell(addCell(if (ThuOpen.isNullOrEmpty()) "Closed" else ThuOpen, 1, false))
        hoursTable.addCell(addCell(if (ThuClose.isNullOrEmpty()) "Closed" else ThuClose, 1, false))
        hoursTable.addCell(addCell("Fri", 1, false))
        hoursTable.addCell(addCell(if (FriOpen.isNullOrEmpty()) "Closed" else FriOpen, 1, false))
        hoursTable.addCell(addCell(if (FriClose.isNullOrEmpty()) "Closed" else FriClose, 1, false))
        hoursTable.addCell(addCell("Sat", 1, false))
        hoursTable.addCell(addCell(if (SatOpen.isNullOrEmpty()) "Closed" else SatOpen, 1, false))
        hoursTable.addCell(addCell(if (SatClose.isNullOrEmpty()) "Closed" else SatClose, 1, false))
    }
    table.addCell(addTableInCell(hoursTable, 4, false))
    table.addCell(addCell(" ", 4, true))
    table.addCell(addTableInCell(drawEmailSection(), 4, false))
    table.addCell(addCell(" ", 4, true))
    table.addCell(addTableInCell(drawPhoneSection(), 2, false))
    table.addCell(addCell("", 2, true))
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Hours Section - Completed")
    return table
}

private fun drawPaymentSection(): PdfPTable {
    createPDFLogData += " - drawPaymentSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Payment Section - Started")
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Payment Methods", 2, false))
    table.addCell(addCellWithBorder("Accepted", 1, true))
    TypeTablesModel.getInstance().PaymentMethodsType.apply {
        (0 until size).forEach {
            table.addCell(addCellWithBorder(get(it).PmtMethodName, 2, false))
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s ->
                    s.PmtMethodID.equals(
                        get(it).PmtMethodID
                    )
                }.isNotEmpty()) {
                Log.v("TICK ", " TICK")
                table.addCell(addTick(true, true))
            } else {
                table.addCell(addCellWithBorder(" ", 1, true))
            }
        }
    }
    var noOfRowsToBeAdded =
        TypeTablesModel.getInstance().LanguageType.size - TypeTablesModel.getInstance().PaymentMethodsType.size
    if (FacilityDataModel.getInstance().tblPhone.size > 1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
    if (FacilityDataModel.getInstance().tblFacilityEmail.size > 1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

    for (i in 1..noOfRowsToBeAdded) {
        table.addCell(addCell(" ", 3, true))
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Payment Section - Completed")
    return table
}

private fun drawPhoneSection(): PdfPTable {
    createPDFLogData += " - drawPhoneSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Phone Section - Started")
    val table = PdfPTable(2)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Phone Type", 1, false))
    table.addCell(addCellWithBorder("Phone", 1, true))
    FacilityDataModel.getInstance().tblPhone.apply {
        (0 until size).forEach {
            if (!get(it).PhoneID.equals("-1") && !get(it).PhoneTypeID.equals("0")) {
                try {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationPhoneType.filter { s ->
                        s.LocPhoneID.equals(
                            get(it).PhoneTypeID
                        )
                    }[0].LocPhoneName, 1, false))
                    table.addCell(addCellWithBorder(get(it).PhoneNumber, 2, false))
                } catch (e: Exception) {

                }
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Phone Section - Completed")
    return table
}

private fun drawEmailSection(): PdfPTable {
    createPDFLogData += " - drawEmailSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Email Section - Started")
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Email Type", 1, false))
    table.addCell(addCellWithBorder("Email", 3, true))
    FacilityDataModel.getInstance().tblFacilityEmail.apply {
        (0 until size).forEach {
            if (!get(it).emailID.equals("-1") && !get(it).emailTypeId.equals("0")) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().EmailType.filter { s ->
                    s.EmailID.equals(
                        get(it).emailTypeId
                    )
                }[0].EmailName, 1, false))
                table.addCell(addCellWithBorder(get(it).email, 3, false))
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Email Section - Completed")
    return table
}

private fun drawLanguageSection(): PdfPTable {
    createPDFLogData += " - drawLanguageSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Language Section - Started")
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Language(s)", 2, false))
    table.addCell(addCellWithBorder("Spoken", 1, true))
    TypeTablesModel.getInstance().LanguageType.apply {
        (0 until size).forEach {
            table.addCell(addCellWithBorder(get(it).LangTypeName, 2, false))
            if (FacilityDataModel.getInstance().tblLanguage.filter { s -> s.LangTypeID.equals(get(it).LangTypeID) }.size > 0) {
                table.addCell(addTick(true, true))
            } else {
                table.addCell(addCellWithBorder(" ", 1, true))
            }
        }
    }

    var noOfRowsToBeAdded = 0
    if (FacilityDataModel.getInstance().tblPhone.size > 1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
    if (FacilityDataModel.getInstance().tblFacilityEmail.size > 1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

    for (i in 1..noOfRowsToBeAdded) {
        table.addCell(addCell(" ", 3, true))
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Language Section - Completed")
    return table
}


private fun drawDeficiencySectionForShop(): PdfPTable {
    createPDFLogData += " - drawDeficiencySectionForShop"
    FirebaseCrashlytics.getInstance().log("Create PDF - Deficiency Section - Started")
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Deficiency", 1, true))
    table.addCell(addCellWithBorder("Inspection Date", 1, true))
    table.addCell(addCellWithBorder("Due Date", 1, true))
    FacilityDataModel.getInstance().tblDeficiency.apply {
        (0 until size).forEach {
            if (!get(it).DefTypeID.equals("-1") && get(it).ClearedDate.isNullOrEmpty() && !get(it).DefTypeID.equals(
                    "0"
                )
            ) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARDeficiencyType.filter { s ->
                    s.DeficiencyTypeID.equals(
                        get(it).DefTypeID
                    )
                }[0].DeficiencyName, 1, true))
                table.addCell(
                    addCellWithBorder(
                        if (get(it).VisitationDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).VisitationDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                );
                table.addCell(addCellWithBorder("", 1, true));
            }
        }
    }
    if (FacilityDataModel.getInstance().tblDeficiency.size % 3 > 0) {
        table.addCell(
            addCell(
                " ",
                (FacilityDataModel.getInstance().tblDeficiency.size % 3) * 2,
                false
            )
        )
    }
    FirebaseCrashlytics.getInstance().log("Create PDF - Deficiency Section - Completed")
    return table
}


private fun drawVendorRevenueSectionForShop(): PdfPTable {
    val table = PdfPTable(6)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Revenue ID", 1, true))
    table.addCell(addCellWithBorder("Revenue Source", 1, true))
    table.addCell(addCellWithBorder("Date of Check", 1, true))
    table.addCell(addCellWithBorder("Amount", 1, true))
    table.addCell(addCell("", 2, true))
    if (FacilityDataModel.getInstance().tblVendorRevenue[0].VendorRevenueID > 0) {
        if (FacilityDataModel.getInstance().tblVendorRevenue.filter { s -> (Date().time - s.DateOfCheck.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }
                .isNotEmpty()) {
            FacilityDataModel.getInstance().tblVendorRevenue.apply {
                (0 until size).forEach {
                    if (get(it).VendorRevenueID > 0) {
                        table.addCell(
                            addCellWithBorder(
                                get(it).VendorRevenueID.toString(),
                                1,
                                true
                            )
                        )
                        table.addCell(addCellWithBorder(get(it).RevenueSourceName, 1, true))
                        table.addCell(
                            addCellWithBorder(
                                if (get(it).DateOfCheck.apiToAppFormatMMDDYYYY()
                                        .equals("01/01/1900")
                                ) "" else get(it).DateOfCheck.apiToAppFormatMMDDYYYY(), 1, true
                            )
                        );
                        table.addCell(
                            addCellWithBorder(
                                "%.3f".format(get(it).Amount.toFloat()),
                                1,
                                true
                            )
                        );
                        table.addCell(addCell("", 2, true))
                    }
                }
            }
        }
    }

    return table
}


private fun drawDataChangedSectionForShop(): PdfPTable {
    val table = PdfPTable(8)
    table.setWidthPercentage(100f)
//    table.addCell(addCellWithBorder("User ID", 1,true))
    table.addCell(addCellWithBorder("Group Name", 1, true))
    table.addCell(addCellWithBorder("Screen Name", 1, true))
//    table.addCell(addCellWithBorder("Section Name", 1,true))
//    table.addCell(addCellWithBorder("Action", 1,true))
    table.addCell(addCellWithBorder("Change Date", 1, true))
    table.addCell(addCellWithBorder("Changes Made", 5, false))
    PRGDataModel.getInstance().tblPRGLogChanges.apply {
        (0 until size).forEach {
            if (get(it).recordid > -1 && !get(it).sectionname.equals("Load Visitation")) {
//                table.addCell(addCellWithBorder(get(it).userid, 1, true))
                table.addCell(addCellWithBorder(get(it).groupname, 1, true))
                table.addCell(addCellWithBorder(get(it).screenname, 1, true));
//                table.addCell(addCellWithBorder(get(it).sectionname, 1, true));
//                table.addCell(addCellWithBorder(if (get(it).action) "ADD" else "EDIT", 1, true));
                table.addCell(
                    addCellWithBorder(
                        get(it).changedate.apiToAppFormatMMDDYYYY(),
                        1,
                        true
                    )
                );
                table.addCell(addCellWithBorder(get(it).datachanged, 5, false));
            }
        }
    }
    return table
}

private fun drawAddressSection(): PdfPTable {
    createPDFLogData += " - drawAddressSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Address Section - Started")
    val columnWidths = floatArrayOf(4f, 12f, 12f, 5f, 3f, 3f, 3f, 5f, 8f, 4f, 5f, 5f)
    val table = PdfPTable(columnWidths)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Type", 1, true))
    table.addCell(addCellWithBorder("Address1", 1, true))
    table.addCell(addCellWithBorder("Address2", 1, true))
    table.addCell(addCellWithBorder("City", 1, true))
    table.addCell(addCellWithBorder("State", 1, true))
    table.addCell(addCellWithBorder("ZIP", 1, true))
    table.addCell(addCellWithBorder("ZIP4", 1, true))
    table.addCell(addCellWithBorder("Country", 1, true))
    table.addCell(addCellWithBorder("Branch Name", 1, true))
    table.addCell(addCellWithBorder("Branch #", 1, true))
    table.addCell(addCellWithBorder("Latitude", 1, true))
    table.addCell(addCellWithBorder("Longitude", 1, true))
    FacilityDataModel.getInstance().tblAddress.apply {
        (0 until size).forEach {
            if (!get(it).LocationTypeID.isNullOrEmpty()) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationType.filter { s ->
                    s.LocTypeID.equals(
                        get(it).LocationTypeID
                    )
                }[0].LocTypeName, 1, true))
                table.addCell(addCellWithBorder(get(it).FAC_Addr1, 1, true))
                table.addCell(addCellWithBorder(get(it).FAC_Addr2, 1, true))
                table.addCell(addCellWithBorder(get(it).CITY, 1, true))
                table.addCell(addCellWithBorder(get(it).ST, 1, true))
                table.addCell(addCellWithBorder(get(it).ZIP, 1, true))
                table.addCell(addCellWithBorder(get(it).ZIP4, 1, true))
                table.addCell(addCellWithBorder(get(it).County, 1, true))
                table.addCell(addCellWithBorder(get(it).BranchName, 1, true))
                table.addCell(addCellWithBorder(get(it).BranchNumber, 1, true))
                table.addCell(addCellWithBorder(get(it).LATITUDE, 1, true))
                table.addCell(addCellWithBorder(get(it).LONGITUDE, 1, true))
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Address Section - Completed")
    return table
}

private fun drawVehiclesSection(vehicleCatID: String): PdfPTable {
    createPDFLogData += " - drawVehiclesSection"
    val columnWidths = floatArrayOf(1f, 4f, 1f, 4f, 1f, 4f, 1f, 4f, 1f, 4f, 1f, 4f, 1f, 4f, 1f, 4f)
    val table = PdfPTable(columnWidths)
    val vehicleTypeID = 1
    table.widthPercentage = 100f
    TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }
        .apply {
            (0 until size).forEach { vMakeIt ->
                if (FacilityDataModel.getInstance().tblFacVehicles.filter { s ->
                        s.VehicleID == get(
                            vMakeIt
                        ).VehicleID
                    }.isNotEmpty()) {
                    table.addCell(addTick(true, false))
                    table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
                } else {
                    table.addCell(addCell(" ", 1, false))
                    table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
                }
            }
        }
    if (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8 > 0) {
        table.addCell(
            addCell(
                " ",
                (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8) * 2,
                false
            )
        )
    }
    return table
}

private fun drawVehicleServicesSection(vehicleTypeID: String): PdfPTable {
    createPDFLogData += " - drawVehicleServicesSection"
    val columnWidths = floatArrayOf(3f, 30f, 3f, 30f, 3f, 31f)
    val table = PdfPTable(columnWidths)
    table.widthPercentage = 100f

    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s ->
            s.VehiclesTypeID.equals(
                "1"
            ) && s.VehicleCategoryID.equals(vehicleTypeID)
        }.isNotEmpty()) {
        TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s ->
            s.VehiclesTypeID.equals(
                "1"
            ) && s.VehicleCategoryID.equals(vehicleTypeID)
        }.apply {
            (0 until size).forEach { innerIt ->
                if (FacilityDataModel.getInstance().tblVehicleServices.filter { s ->
                        s.VehiclesTypeID == 1 && s.VehicleCategoryID.equals(
                            vehicleTypeID
                        ) && s.ServiceID.equals(get(innerIt).ServiceID)
                    }.isNotEmpty()) {
                    table.addCell(addTick(true, false))
                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName, 1, false))
                } else {
                    table.addCell(addCell(" ", 1, false))
                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName, 1, false))
                }
            }
        }
    }
//    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3 > 0) {
//        table.addCell(addCell(" ", (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3)*2, false))
//    }
    table.addCell(addCell(" ", 6, false))
    return table
}

//private fun drawVehicleServicesSection(vehicleTypeID: String) : PdfPTable {
//    createPDFLogData += " - drawVehicleServicesSection"
//    val columnWidths = floatArrayOf(1f, 4f,1f, 4f,1f, 4f)
//    val table = PdfPTable(columnWidths)
//    table.widthPercentage = 100f
//
//    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.isNotEmpty()) {
//        TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.apply {
//            (0 until size).forEach { innerIt ->
//                if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(innerIt).ScopeServiceID.toInt() }.isNotEmpty()) {
//                    table.addCell(addTick(false,false))
//                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
//                } else {
//                    table.addCell(addCell(" ",1,false))
//                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
//                }
//            }
//        }
//    }
////    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3 > 0) {
////        table.addCell(addCell(" ", (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3)*2, false))
////    }
//    table.addCell(addCell(" ",6,false))
//    return table
//}

private fun drawPersonnelSection(): PdfPTable {
    createPDFLogData += " - drawPersonnelSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Personnel Section - Started")
    val columnWidths = floatArrayOf(5f, 5f, 5f, 10f, 5f, 10f, 5f, 5f, 4f, 4f, 4f, 4f, 5f)
    val table = PdfPTable(columnWidths)
    table.headerRows = 1
//    val table = PdfPTable(15)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorderSmallFont("Personnel Type", 1, true))
    table.addCell(addCellWithBorderSmallFont("First Name", 1, true))
    table.addCell(addCellWithBorderSmallFont("Last Name", 1, true))
    table.addCell(addCellWithBorderSmallFont("Certification #", 1, true))
    table.addCell(addCellWithBorderSmallFont("RSP User ID", 1, true))
    table.addCell(addCellWithBorderSmallFont("Email Address", 1, true))
    table.addCell(addCellWithBorderSmallFont("Start Date", 1, true))
    table.addCell(addCellWithBorderSmallFont("End Date", 1, true))
    table.addCell(addCellWithBorderSmallFont("Seniority Date", 1, true))
    table.addCell(addCellWithBorderSmallFont("Contract Signer", 1, true))
    table.addCell(addCellWithBorderSmallFont("Primary Mail Recipient", 1, true))
    table.addCell(addCellWithBorderSmallFont("Report Recipient", 1, true))
    table.addCell(addCellWithBorderSmallFont("Notification Recipient", 1, true))
    FacilityDataModel.getInstance().tblPersonnel.apply {
        (0 until size).forEach {
            if (get(it).PersonnelID > -1 && get(it).PersonnelTypeID != TypeTablesModel.getInstance().PersonnelType.filter { s ->
                    s.PersonnelTypeName.equals(
                        "PRG"
                    )
                }[0].PersonnelTypeID.toInt()) {
                table.addCell(addCellWithBorderSmallFont(TypeTablesModel.getInstance().PersonnelType.filter { s ->
                    s.PersonnelTypeID.equals(
                        get(it).PersonnelTypeID.toString()
                    )
                }[0].PersonnelTypeName, 1, true))
                table.addCell(addCellWithBorderSmallFont(get(it).FirstName, 1, true))
                table.addCell(addCellWithBorderSmallFont(get(it).LastName, 1, true))
                table.addCell(addCellWithBorderSmallFont(get(it).CertificationNum, 1, true))
                table.addCell(addCellWithBorderSmallFont(get(it).RSP_UserName, 1, true))
                table.addCell(addCellWithBorderSmallFont(get(it).RSP_Email, 1, true))
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).startDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).startDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                );
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).endDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).endDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                );
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                );
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).ContractSigner) "X" else "",
                        1,
                        true
                    )
                )
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).PrimaryMailRecipient) "X" else "",
                        1,
                        true
                    )
                )
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).ReportRecipient) "X" else "",
                        1,
                        true
                    )
                )
                table.addCell(
                    addCellWithBorderSmallFont(
                        if (get(it).NotificationRecipient) "X" else "",
                        1,
                        true
                    )
                )
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Personnel Section - Completed")
    return table
}

private fun drawSignersSection(): PdfPTable {
    createPDFLogData += " - drawSignersSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Signer Section - Started")
    val columnWidths = floatArrayOf(5f, 5f, 10f, 10f, 5f, 3f, 3f, 3f, 5f, 10f, 5f, 5f)
    val table = PdfPTable(columnWidths)
    table.headerRows = 1
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("First Name", 1, true))
    table.addCell(addCellWithBorder("Last Name", 1, true))
    table.addCell(addCellWithBorder("Address", 1, true))
    table.addCell(addCellWithBorder("Address2", 1, true))
    table.addCell(addCellWithBorder("City", 1, true))
    table.addCell(addCellWithBorder("State", 1, true))
    table.addCell(addCellWithBorder("ZIP", 1, true))
    table.addCell(addCellWithBorder("ZIP4", 1, true))
    table.addCell(addCellWithBorder("Phone", 1, true))
    table.addCell(addCellWithBorder("Email", 1, true))
    table.addCell(addCellWithBorder("Contract Start Date", 1, true))
    table.addCell(addCellWithBorder("Contract End Date", 1, true))
    FacilityDataModel.getInstance().tblPersonnelSigner.apply {
        (0 until size).forEach {
            if (get(it).PersonnelID > -1) {
                table.addCell(addCellWithBorder(get(it).FirstName, 1, true))
                table.addCell(addCellWithBorder(if (FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                        s.PersonnelID == get(
                            it
                        ).PersonnelID
                    }.isNotEmpty()) FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                    s.PersonnelID == get(
                        it
                    ).PersonnelID
                }[0].LastName else "", 1, true))
                table.addCell(addCellWithBorder(get(it).Addr1, 1, true))
                table.addCell(addCellWithBorder(get(it).Addr2, 1, true))
                table.addCell(addCellWithBorder(get(it).CITY, 1, true))
                table.addCell(addCellWithBorder(get(it).ST, 1, true))
                table.addCell(addCellWithBorder(get(it).ZIP, 1, true))
                table.addCell(addCellWithBorder(get(it).ZIP4, 1, true))
                table.addCell(addCellWithBorder(get(it).Phone, 1, true))
                table.addCell(addCellWithBorder(get(it).email, 1, true))
                table.addCell(
                    addCellWithBorder(
                        if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY()
                                .equals("01/01/1900")
                        ) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(), 1, true
                    )
                );
                table.addCell(addCellWithBorder("", 1, true));
            }
        }
    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Signer Section - Completed")
    return table
}

private fun drawCertificationsSection(): PdfPTable {
    createPDFLogData += " - drawCertificationsSection"
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Certifications Section - Started")
    val table = PdfPTable(13)
    table.headerRows = 1
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("First Name", 1, true))
    table.addCell(addCellWithBorder("Last Name", 1, true))
    table.addCell(addCellWithBorder("A1", 1, true))
    table.addCell(addCellWithBorder("A2", 1, true))
    table.addCell(addCellWithBorder("A3", 1, true))
    table.addCell(addCellWithBorder("A4", 1, true))
    table.addCell(addCellWithBorder("A5", 1, true))
    table.addCell(addCellWithBorder("A6", 1, true))
    table.addCell(addCellWithBorder("A7", 1, true))
    table.addCell(addCellWithBorder("A8", 1, true))
    table.addCell(addCellWithBorder("A9", 1, true))
    table.addCell(addCellWithBorder("C1", 1, true))
    table.addCell(addCellWithBorder("L1", 1, true))

    var personnelWithCert = ArrayList<Int>()
    FacilityDataModel.getInstance().tblPersonnelCertification.apply {
        (0 until size).forEach {
            if (!personnelWithCert.contains(get(it).PersonnelID)) {
                personnelWithCert.add(get(it).PersonnelID)
            }
        }
    }

    personnelWithCert.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                    s.PersonnelID.equals(
                        personnelWithCert[it]
                    )
                }.isNotEmpty()) {
                table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                    s.PersonnelID.equals(
                        personnelWithCert[it]
                    )
                }[0].FirstName, 1, true))
                table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s ->
                    s.PersonnelID.equals(
                        personnelWithCert[it]
                    )
                }[0].LastName, 1, true))
                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }
                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId })
                        .filter { s -> s.PersonnelID == personnelWithCert[it] }
                        .filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()
                ) {
                    val expDate =
                        FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(
                            compareBy { it.CertificationTypeId })
                            .filter { s -> s.PersonnelID == personnelWithCert[it] }
                            .filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                    table.addCell(
                        addCellWithBorder(
                            if (expDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true
                        )
                    );
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

            }
        }
    }


//    FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.PersonnelID }).apply {
//        (0 until size).forEach {
//            if (!get(it).CertificationTypeId.isNullOrEmpty()) {
//                if (FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID.equals(get(it).PersonnelID) }.isNotEmpty()) {
//                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }[0].FirstName, 1,true))
//                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }[0].LastName, 1,true))
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A1") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A2") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A3") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A4") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A5") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A6") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A7") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A8") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A9") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("C1") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("L1") }.isNotEmpty()) {
//                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
//                    } else {
//                        table.addCell(addCellWithBorder("", 1,true))
//                    }
//                }
//
//            }
//        }
//    }
    FirebaseCrashlytics.getInstance().log("SpecialistPDF - Certifications Section - Completed")
    return table
}

private fun drawFacilityGISection(): PdfPTable {
    val payMethodTable = PdfPTable(2)
    payMethodTable.setWidthPercentage(100f)

    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    val payMethodCel = PdfPCell()
    return table
}

fun addTitleCell(strValue: String, colSpan: Int, alignCenter: Boolean, font: Font): PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, font));
    cell.colspan = colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCell(strValue: String, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont));
    cell.colspan = colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addTableInCell(theTable: PdfPTable, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(theTable);
    cell.colspan = colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCellWithBorder(strValue: String, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont7));
    cell.colspan = colSpan
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCellWithBorderSmallFont(strValue: String, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont6));
    cell.colspan = colSpan
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

internal class LinkInCell(protected var url: String) : PdfPCellEvent {
    override fun cellLayout(
        cell: PdfPCell?, position: Rectangle?,
        canvases: Array<PdfContentByte>
    ) {
        val writer = canvases[0].pdfWriter
        val action = PdfAction(url)
        val link = PdfAnnotation.createLink(
            writer, position, PdfAnnotation.HIGHLIGHT_INVERT, action
        )
        writer.addAnnotation(link)
    }
}

fun addHyperLinkWithBorder(strValue: String, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(Paragraph("Show Image", normalFont7L));
    cell.colspan = colSpan
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.setCellEvent(
        LinkInCell(
            strValue
        )
    );
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}


fun addImageWithBorder(image: Image, colSpan: Int, alignCenter: Boolean): PdfPCell {
    val cell = PdfPCell(image);
    cell.colspan = colSpan
    cell.setPadding(5F)
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addSignatures(image: Image): PdfPCell {
    val cell = PdfPCell(image);
    cell.setPadding(5F)
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.horizontalAlignment = Element.ALIGN_CENTER
    cell.setBorder(Rectangle.NO_BORDER);
    return cell
}

fun addTick(alignCenter: Boolean, withBorder: Boolean): PdfPCell {
//    val tick =  Chunk("4", symbolsFont)
//    tick.font.size = 14.0F
    var p = Paragraph("x ")
    val cell = PdfPCell(p);
    cell.colspan = 1
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) {
        cell.horizontalAlignment = Element.ALIGN_CENTER
    } else {
        cell.horizontalAlignment = Element.ALIGN_RIGHT
    }
    if (!withBorder) {
        cell.setBorder(Rectangle.NO_BORDER);
    }
    return cell
}

private fun createTable(): PdfPTable {
    val columnWidths = floatArrayOf(2f, 4f)
    val table = PdfPTable(columnWidths)
    table.setWidthPercentage(100F);

    // Visitation Section
    var c1 = PdfPCell(Paragraph("Visitation", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
    var c2 = PdfPCell(Paragraph("Details", titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2);

//    table.setHeaderRows(1)
    // Facility Number
    addDataCell(
        table,
        "Facility Number",
        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Facility Name",
        FacilityDataModel.getInstance().tblFacilities[0].BusinessName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Visitation Type",
        FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Date of Visitation",
        FacilityDataModel.getInstance().tblVisitationTracking[0].DatePerformed.apiToAppFormatMMDDYYYY(),
        normalFont,
        false
    )
    if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc) {
        addDataCell(table, "Visitation Reason", "", normalFont, false)
    }
    addDataCell(table, "Data Changes Made", "", normalFont, false)
    addDataCell(table, "Facility Representative", "", normalFont, false)
    addDataCell(table, "Facility Representative Signature", "", normalFont, false)
    addDataCell(table, "Automotive Specialist", "", normalFont, false)
    addDataCell(table, "Automotive Specialist Signature", "", normalFont, false)

    var c3 = PdfPCell(Paragraph("Deficiencies", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan = FacilityDataModel.getInstance().tblDeficiency.size
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    FacilityDataModel.getInstance().tblDeficiency.apply {
        (0 until size).forEach {
            if (!get(it).DefTypeID.equals("-1")) {
                var strDef =
                    "Def Type: " + TypeTablesModel.getInstance().AARDeficiencyType.filter { s ->
                        s.DeficiencyTypeID.toString() == get(it).DefTypeID
                    }[0].DeficiencyName
                strDef += "\nInspection Date: " + get(it).VisitationDate.apiToAppFormatMMDDYYYY() + " - Due Date: " + get(
                    it
                ).DueDate.apiToAppFormatMMDDYYYY()
                strDef += "\nComments: " + get(it).Comments
                table.addCell(Paragraph(strDef, normalFont))
            } else {
                table.addCell(Paragraph("NA", normalFont))
            }
        }
    }

    addDataCell(table, "Facility Representative's Signature (Deficiencies)", "", normalFont, false)

    c3 = PdfPCell(Paragraph("Vendor Revenue (past 12 months)", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan = FacilityDataModel.getInstance().tblVendorRevenue.size
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
//    FacilityDataModel.getInstance().tblVendorRevenue.filter { s->s. }
    FacilityDataModel.getInstance().tblVendorRevenue.apply {
        (0 until size).forEach {
            if (!get(it).VendorRevenueID.equals("-1")) {
                var strDef = "Revenue ID: " + get(it).VendorRevenueID
                strDef += "\nRevenue Source: " + get(it).RevenueSourceName
                strDef += "\nDate of Check: " + get(it).DateOfCheck.apiToAppFormatMMDDYYYY() + " - Amount: " + if (get(
                        it
                    ).Amount.isNullOrEmpty()
                ) "" else "%.3f".format(get(it).Amount.toFloat())
                table.addCell(Paragraph(strDef, normalFont))
            } else {
                table.addCell("NA")
            }
        }
    }



    c3 = PdfPCell(Paragraph("Visitation Tracking (past 12 months)", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT


//    try {
    c3.rowspan =
        if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size > 0) FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strDef = ""
    FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }
        .sortedWith(compareByDescending { it.DatePerformed }).apply {
//    FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed }).apply {
            (0 until size).forEach {
                if (!get(it).performedBy.equals("00")) {
                    strDef =
                        "Performed By: " + get(it).performedBy + " - Date Performed: " + get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                    strDef += "\nVisitation Type: " + get(it).visitationType
                    if (it == 0) {
                        strDef += "\nAAR Sign: " + get(it).AARSigns
                        strDef += "\nCertificate of Approval: " + get(it).CertificateOfApproval
                        strDef += "\nMember Benefits Poster(s): " + get(it).MemberBenefitPoster
                        strDef += "\nQuality Control Process: " + get(it).QualityControl
                        strDef += "\nStaff Training Process: " + get(it).StaffTraining
                    }
                    table.addCell(Paragraph(strDef, normalFont))
                } else {
                    table.addCell(Paragraph("", normalFont))
                }
            }
        }

    if (strDef.equals("")) table.addCell(Paragraph("", normalFont))

    c1 = PdfPCell(Paragraph("General Info", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details" , titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2);
    addDataCell(
        table,
        "DBA",
        FacilityDataModel.getInstance().tblFacilities[0].BusinessName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Entity Name",
        FacilityDataModel.getInstance().tblFacilities[0].EntityName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Business Type",
        TypeTablesModel.getInstance().BusinessType.filter { s ->
            s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())
        }[0].BusTypeName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Contract Status",
        TypeTablesModel.getInstance().FacilityStatusType.filter { s ->
            s.FacilityStatusID.equals(FacilityDataModel.getInstance().tblFacilities[0].ContractTypeID.toString())
        }[0].FacilityStatusID,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Contract Type",
        FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Provider Type",
        FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Provider Number",
        FacilityDataModel.getInstance().tblFacilityServiceProvider[0].ProviderNum,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Termination Date",
        FacilityDataModel.getInstance().tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Termination Reason",
        FacilityDataModel.getInstance().tblTerminationCodeType[0].TerminationCodeName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Termination Comments",
        FacilityDataModel.getInstance().tblFacilities[0].TerminationComments,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Annual Inspection Month",
        FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Quarterly Inspection Cycle",
        FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Office",
        FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Assigned To",
        FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Manager",
        FacilityDataModel.getInstance().tblFacilityManagers[0].Manager,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Admin Assistants",
        FacilityDataModel.getInstance().tblFacilities[0].AdminAssistants,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Time Zone",
        FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Website URL",
        FacilityDataModel.getInstance().tblFacilities[0].WebSite,
        normalFont,
        false
    )
    addDataCell(
        table,
        "Tax-ID",
        FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,
        normalFont,
        false
    )

    addDataCell(
        table,
        "Repair Order Count",
        FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount.toString(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Service Availability",
        if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability }.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID == FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability }[0].SrvAvaName else "",
        normalFont,
        false
    )
    addDataCell(
        table,
        "Facility Type",
        FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,
        normalFont,
        false
    )
    addDataCell(
        table,
        "ARD Number",
        FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,
        normalFont,
        false
    )
    addDataCell(table, "Shop Management System", "", normalFont, false)
    addDataCell(
        table,
        "Current Contract Date",
        FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Initial Contract Date",
        FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Billing Month",
        FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Billing Amount",
        "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),
        normalFont,
        false
    )
    addDataCell(
        table,
        "Insurance Expiration Date",
        FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),
        normalFont,
        false
    )

    var paymentMethodsStr = ""
    TypeTablesModel.getInstance().PaymentMethodsType.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s ->
                    s.PmtMethodID.equals(
                        get(it).PmtMethodID
                    )
                }.size > 0)
                paymentMethodsStr += get(it).PmtMethodName + ", "
        }
    }

    addDataCell(table, "Payment Methods", paymentMethodsStr.removeSuffix(", "), normalFont, false)

    // LOCATION

    c1 = PdfPCell(Paragraph("Location and Contact Info", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details" , titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2);

    FacilityDataModel.getInstance().tblAddress.apply {
        (0 until size).forEach {
            if (!get(it).LocationTypeID.isNullOrEmpty()) {
                var strAddress = "Address1: " + get(it).FAC_Addr1
                strAddress += "\nAddress2: " + get(it).FAC_Addr2
                if (TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeID == get(it).LocationTypeID }[0].LocTypeName.equals(
                        "Physical"
                    )
                ) {
                    strAddress += "\nLatitude: " + get(it).LATITUDE
                    strAddress += "\nLongitude: " + get(it).LONGITUDE
                }
                strAddress += "\nBranch Number: " + get(it).BranchNumber
                strAddress += "\nBranch Name: " + get(it).BranchName
                c2 = PdfPCell(Paragraph(TypeTablesModel.getInstance().LocationType.filter { s ->
                    s.LocTypeID == get(it).LocationTypeID
                }[0].LocTypeName + " Address", normalFont));
                c2.horizontalAlignment = Element.ALIGN_LEFT
                c2.verticalAlignment = Element.ALIGN_MIDDLE
                table.addCell(c2);
                table.addCell(Paragraph(strAddress, normalFont))
            }
        }
    }

    FacilityDataModel.getInstance().tblPhone.apply {
        (0 until size).forEach {
            if (!get(it).PhoneID.equals("-1")) {
                addDataCell(
                    table,
                    "Phone Type - " + TypeTablesModel.getInstance().LocationPhoneType.filter { s ->
                        s.LocPhoneID == get(it).PhoneTypeID
                    }[0].LocPhoneName,
                    get(it).PhoneNumber,
                    normalFont,
                    false
                )
            }
        }
    }

    FacilityDataModel.getInstance().tblFacilityEmail.apply {
        (0 until size).forEach {
            if (!get(it).emailID.equals("-1")) {
                addDataCell(
                    table,
                    "Email Type - " + TypeTablesModel.getInstance().EmailType.filter { s ->
                        s.EmailID == get(it).emailTypeId
                    }[0].EmailName,
                    get(it).email,
                    normalFont,
                    false
                )
            }
        }
    }

    c3 = PdfPCell(Paragraph("Hours of Operation", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan = 1
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    table.addCell(Paragraph(""))
    FacilityDataModel.getInstance().tblHours.apply {
        (0 until size).forEach {
            addDataCell(
                table,
                "     Sunday",
                if (get(it).SunOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).SunOpen + "   -   Closed: " + get(
                    it
                ).SunClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Monday",
                if (get(it).MonOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).MonOpen + "   -   Closed: " + get(
                    it
                ).MonClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Tuesday",
                if (get(it).TueOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).TueOpen + "   -   Closed: " + get(
                    it
                ).TueClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Wednesday",
                if (get(it).WedOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).WedOpen + "   -   Closed: " + get(
                    it
                ).WedClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Thursday",
                if (get(it).ThuOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).ThuOpen + "   -   Closed: " + get(
                    it
                ).ThuClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Friday",
                if (get(it).FriOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).FriOpen + "   -   Closed: " + get(
                    it
                ).FriClose,
                normalFont,
                false
            )
            addDataCell(
                table,
                "     Saturday",
                if (get(it).SatOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).SatOpen + "   -   Closed: " + get(
                    it
                ).SatClose,
                normalFont,
                false
            )
        }
    }


    addDataCell(
        table,
        "Night Drop",
        if (FacilityDataModel.getInstance().tblHours[0].NightDrop) "Available" else "Not Available",
        normalFont,
        false
    )
    addDataCell(
        table,
        "Night Drop Instructions",
        if (FacilityDataModel.getInstance().tblHours[0].NightDropInstr.isNullOrEmpty()) "" else FacilityDataModel.getInstance().tblHours[0].NightDropInstr,
        normalFont,
        false
    )

    var strLanguages = ""
    TypeTablesModel.getInstance().LanguageType.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblLanguage.filter { s -> s.LangTypeID.equals(get(it).LangTypeID) }.size > 0)
                strLanguages += get(it).LangTypeName + ", "
        }
    }

    addDataCell(table, "Languages", strLanguages.removeSuffix(", "), normalFont, false)

    c1 = PdfPCell(Paragraph("Shop Personnel", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details" , titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2);

    FacilityDataModel.getInstance().tblPersonnel.apply {
        (0 until size).forEach {
            if (!get(it).PersonnelID.equals("-1")) {
                c1 = PdfPCell(
                    Paragraph(
                        TypeTablesModel.getInstance().PersonnelType.filter { s ->
                            s.PersonnelTypeID.toInt() == get(it).PersonnelTypeID
                        }[0].PersonnelTypeName + ":  " + get(it).FirstName + " " + get(it).LastName,
                        normalFont
                    )
                )
                c1.horizontalAlignment = Element.ALIGN_CENTER
                c1.verticalAlignment = Element.ALIGN_MIDDLE
                c1.backgroundColor = BaseColor.LIGHT_GRAY
                c1.colspan = 2
                table.addCell(c1);
//                c2 = PdfPCell(Paragraph(get(it).FirstName + " " + get(it).LastName, normalFont));
//                c2.horizontalAlignment = Element.ALIGN_CENTER
//                c2.verticalAlignment = Element.ALIGN_MIDDLE
//                c2.backgroundColor = BaseColor.LIGHT_GRAY
//                table.addCell(c2);
//                addDataCell(table, "Personnel Type", TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeID.toInt() == get(it).PersonnelTypeID }[0].PersonnelTypeName, normalFont)
//                addDataCell(table, "First Name", get(it).FirstName, normalFont)
//                addDataCell(table, "Last Name", get(it).LastName, normalFont)
                addDataCell(
                    table,
                    "Certification Number",
                    get(it).CertificationNum,
                    normalFont,
                    false
                )
                addDataCell(table, "RSP User ID", get(it).RSP_UserName, normalFont, false)
                addDataCell(table, "RSP Email Address", get(it).RSP_Email, normalFont, false)
                addDataCell(
                    table,
                    "Seniority Date",
                    if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY()
                            .equals("01/01/1900")
                    ) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(),
                    normalFont,
                    false
                )
                addDataCell(
                    table,
                    "Start Date",
                    if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY()
                            .equals("01/01/1900")
                    ) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(),
                    normalFont,
                    false
                )
                addDataCell(
                    table,
                    "End Date",
                    if (get(it).ContractEndDate.apiToAppFormatMMDDYYYY()
                            .equals("01/01/1900")
                    ) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(),
                    normalFont,
                    false
                )
                addDataCell(
                    table,
                    "Contract Signer",
                    if (get(it).ContractSigner) "Yes" else "No",
                    normalFont,
                    false
                )
                if (get(it).ContractSigner) {
                    if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { S ->
                            S.PersonnelID == get(
                                it
                            ).PersonnelID
                        }.size > 0) {
                        FacilityDataModel.getInstance().tblPersonnelSigner.filter { S ->
                            S.PersonnelID == get(
                                it
                            ).PersonnelID
                        }.forEach { Signer ->
                            addDataCell(
                                table,
                                "(Contract Signer) Address 1",
                                Signer.Addr1,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Address 2",
                                Signer.Addr2,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) City",
                                Signer.CITY,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) State",
                                Signer.ST,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Zip",
                                Signer.ZIP,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Zip 4",
                                Signer.ZIP4,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Phone",
                                Signer.Phone,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Email Address",
                                Signer.email,
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Contract Start Date",
                                if (Signer.ContractStartDate.apiToAppFormatMMDDYYYY()
                                        .equals("01/01/1900")
                                ) "" else Signer.ContractStartDate.apiToAppFormatMMDDYYYY(),
                                normalFont,
                                false
                            )
                            addDataCell(
                                table,
                                "(Contract Signer) Contract End Date",
                                "",
                                normalFont,
                                false
                            )
                        }
                    }
                }
                addDataCell(
                    table,
                    "Primary Mail Recipient",
                    if (get(it).PrimaryMailRecipient) "Yes" else "No",
                    normalFont,
                    false
                )

                if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
                        s.PersonnelID.equals(
                            get(it).PersonnelID
                        )
                    }.count() > 0) {
                    FacilityDataModel.getInstance().tblPersonnelCertification.filter { s ->
                        s.PersonnelID.equals(
                            get(it).PersonnelID
                        )
                    }.forEach { item ->
                        addDataCell(
                            table,
                            item.CertificationTypeId + " Certification Date",
                            if (item.CertificationDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else item.CertificationDate.apiToAppFormatMMDDYYYY(),
                            normalFont,
                            false
                        )
                        addDataCell(
                            table,
                            item.CertificationTypeId + " Expiration Date",
                            if (item.ExpirationDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else item.ExpirationDate.apiToAppFormatMMDDYYYY(),
                            normalFont,
                            false
                        )
                    }
                }
            }
        }
    }

    c1 = PdfPCell(Paragraph("RSP Addendum & Tracking", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details", titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2)

    if (!FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders.equals("-1")) {
        addDataCell(
            table,
            "Start Date",
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY(),
            normalFont,
            false
        )
        addDataCell(
            table,
            "End Date",
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY(),
            normalFont,
            false
        )
        addDataCell(
            table,
            "Addendum Signed Date",
            if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY()
                    .equals("01/01/1900")
            ) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY(),
            normalFont,
            false
        )
        addDataCell(
            table,
            "Number of Card Readers",
            FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders,
            normalFont,
            false
        )
        var strTracking = ""
//        FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.apply {
//            (0 until size).forEach {
//                if (!get(it).TrackingID.equals("-1")) {
//                    strTracking = "Inspection Date: " + if (get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY()
//                    strTracking += "\nLogged Into RSP: " + if (get(it).LoggedIntoPortal.toBoolean()) "Yes" else "No"
//                    strTracking += "\nNumber of Unacknowledged Records: " + get(it).NumberUnacknowledgedTows
//                    strTracking += "\nNumber of In-Progress Tow-Ins: " + get(it).InProgressTows
//                    strTracking += "\nNumber of In-Progress Walk-Ins: " + get(it).InProgressWalkIns
//                }
//            }
//        }


        c3 = PdfPCell(Paragraph("RSP Tracking (past 12 months)", normalFont));
        c3.horizontalAlignment = Element.ALIGN_LEFT
//        try {
        c3.rowspan =
            if (FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size > 0) FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size else 1
//        } catch (e: Exception) {
//            c3.rowspan = 1
//        }
        c3.verticalAlignment = Element.ALIGN_MIDDLE
        table.addCell(c3)

        FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }
            .apply {
                (0 until size).forEach {
                    if (!get(it).TrackingID.equals("-1")) {
                        strTracking =
                            "Inspection Date: " + if (get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY()
                                    .equals("01/01/1900")
                            ) "" else get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY()
                        strTracking += "\nLogged Into RSP: " + if (get(it).LoggedIntoPortal.toBoolean()) "Yes" else "No"
                        strTracking += "\nNumber of Unacknowledged Records: " + get(it).NumberUnacknowledgedTows
                        strTracking += "\nNumber of In-Progress Tow-Ins: " + get(it).InProgressTows
                        strTracking += "\nNumber of In-Progress Walk-Ins: " + get(it).InProgressWalkIns
                        table.addCell(Paragraph(strTracking, normalFont))
                    } else {
                        table.addCell(Paragraph("", normalFont))
                    }
                }
            }

        if (strTracking.equals("")) table.addCell(Paragraph("", normalFont))
    }

    c1 = PdfPCell(Paragraph("Scope Of Services", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details", titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2)

    FacilityDataModel.getInstance().tblScopeofService[0].apply {
        addDataCell(
            table,
            "Fixed Labor Rate",
            if (FixedLaborRate.isNullOrEmpty()) "0" else FixedLaborRate,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Diagnostic Rate",
            if (DiagnosticsRate.isNullOrEmpty()) "0" else DiagnosticsRate,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Labor Rate Matrix Min",
            if (LaborMin.isNullOrEmpty()) "0" else LaborMin,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Labor Rate Matrix Max",
            if (LaborMax.isNullOrEmpty()) "0" else LaborMax,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Number of Bays",
            if (NumOfBays.isNullOrEmpty()) "0" else NumOfBays,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Number of Lifts",
            if (NumOfLifts.isNullOrEmpty()) "0" else NumOfLifts,
            normalFont,
            false
        )
        addDataCell(
            table,
            "Warranty Period",
            if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s ->
                    s.WarrantyTypeID.equals(WarrantyTypeID)
                }.size > 0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s ->
                s.WarrantyTypeID.equals(
                    WarrantyTypeID
                )
            }[0].WarrantyTypeName else "",
            normalFont,
            false
        )
    }

    c1 = PdfPCell(Paragraph("Vehicle Services", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details", titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2)

    // Vehicle Services
    var vehicleTypeID = ""
    TypeTablesModel.getInstance().VehiclesType.apply {
        (0 until size).forEach {
            vehicleTypeID = get(it).VehiclesTypeID
            if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s ->
                    s.VehiclesTypeID.equals(
                        vehicleTypeID
                    )
                }.isNotEmpty()) {
                addDataCell(table, get(it).VehiclesTypeName, "", titleFont, false)
                TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s ->
                    s.VehiclesTypeID.equals(
                        vehicleTypeID
                    )
                }.apply {
                    (0 until size).forEach { innerIt ->
                        if (FacilityDataModel.getInstance().tblVehicleServices.filter { s ->
                                s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(
                                    innerIt
                                ).ScopeServiceID.toInt()
                            }.isNotEmpty()) {
                            addDataCell(table, get(innerIt).ScopeServiceName, "X", normalFont, true)
                        } else {
                            addDataCell(table, get(innerIt).ScopeServiceName, "", normalFont, false)
                        }
                    }
                }
            }
        }
    }

    c1 = PdfPCell(Paragraph("Vehicles", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);
//    c2 = PdfPCell(Paragraph("Details", titleFont));
//    c2.horizontalAlignment = Element.ALIGN_CENTER
//    c2.verticalAlignment = Element.ALIGN_MIDDLE
//    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2)

    // Vehicles
    vehicleTypeID = "1"
    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
        (0 until size).forEach {
            addDataCell(table, get(it).VehCategoryName, "", titleFont, true)
//            if (TypeTablesModel.getInstance().VehicleMakes.filter { s->s.VehicleTypeID.equals(vehicleTypeID) && s.VehicleCategoryID.equals(get(it).VehCategoryID)}.isNotEmpty()) {
            TypeTablesModel.getInstance().VehicleMakes.filter { s ->
                s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == get(
                    it
                ).VehCategoryID.toInt()
            }.apply {
                (0 until size).forEach { vMakeIt ->
                    addDataCell(
                        table,
                        get(vMakeIt).MakeName,
                        if (FacilityDataModel.getInstance().tblFacVehicles.filter { s ->
                                s.VehicleID == get(vMakeIt).VehicleID
                            }.isNotEmpty()) "X" else "",
                        normalFont,
                        true
                    )
                }
            }
        }
    }

    c1 = PdfPCell(Paragraph("Programs", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);

    // Programs
    c3 = PdfPCell(Paragraph("Programs", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
    c3.rowspan =
        if (FacilityDataModel.getInstance().tblPrograms.size > 0) FacilityDataModel.getInstance().tblPrograms.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strPrograms = ""
    FacilityDataModel.getInstance().tblPrograms.apply {
        (0 until size).forEach {
            if (!get(it).ProgramID.equals("-1")) {
                strPrograms = "Program Name: " + get(it).programtypename
                strPrograms += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY()
                        .equals("01/01/1900")
                ) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(
                        it
                    ).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")
                ) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
                strPrograms += "\nComments: " + get(it).Comments
                table.addCell(Paragraph(strPrograms, normalFont))
            } else {
                table.addCell(Paragraph("", normalFont))
            }
        }
    }

    if (strPrograms.equals("")) table.addCell(Paragraph("", normalFont))

    c1 = PdfPCell(Paragraph("Facility Services", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);

    // Facility Services
    c3 = PdfPCell(Paragraph("Facility Services", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
    c3.rowspan =
        if (FacilityDataModel.getInstance().tblFacilityServices.size > 0) FacilityDataModel.getInstance().tblFacilityServices.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strFacServices = ""
    FacilityDataModel.getInstance().tblFacilityServices.apply {
        (0 until size).forEach {
            if (!get(it).FacilityServicesID.equals("-1")) {
                strFacServices =
                    "Service Name: " + TypeTablesModel.getInstance().ServicesType.filter { s ->
                        s.ServiceTypeID.equals(get(it).ServiceID)
                    }[0].ServiceTypeName
                strFacServices += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY()
                        .equals("01/01/1900")
                ) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(
                        it
                    ).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")
                ) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
                strFacServices += "\nComments: " + get(it).Comments
                table.addCell(Paragraph(strFacServices, normalFont))
            } else {
                table.addCell(Paragraph("", normalFont))
            }
        }
    }

    if (strFacServices.equals("")) table.addCell(Paragraph("", normalFont))

    c1 = PdfPCell(Paragraph("Affiliations", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);

    // Affiliations
    c3 = PdfPCell(Paragraph("Affiliations", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
    c3.rowspan =
        if (FacilityDataModel.getInstance().tblAffiliations.size > 0) FacilityDataModel.getInstance().tblAffiliations.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strAffiliation = ""
    FacilityDataModel.getInstance().tblAffiliations.apply {
        (0 until size).forEach {
            if (!(get(it).AffiliationID == -1)) {
                strAffiliation =
                    "Affiliation Name: " + if (get(it).AffiliationTypeID > 0) TypeTablesModel.getInstance().AARAffiliationType.filter { s ->
                        s.AARAffiliationTypeID.toInt() == get(it).AffiliationTypeID
                    }[0].AffiliationTypeName else ""
                strAffiliation += "\nAffiliation Details: " + if (get(it).AffiliationTypeDetailID > 0) TypeTablesModel.getInstance().AffiliationDetailType.filter { s ->
                    s.AffiliationTypeDetailID.toInt() == get(
                        it
                    ).AffiliationTypeDetailID
                }[0].AffiliationDetailTypeName else ""
                strAffiliation += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY()
                        .equals("01/01/1900")
                ) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(
                        it
                    ).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")
                ) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
                strAffiliation += "\nComments: " + get(it).comment
                table.addCell(Paragraph(strAffiliation, normalFont))
            } else {
                table.addCell(Paragraph("", normalFont))
            }
        }
    }

    if (strAffiliation.equals("")) table.addCell(Paragraph("", normalFont))

    // Complaints

    c1 = PdfPCell(Paragraph("Complaints", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan = 2
    table.addCell(c1);

    c3 = PdfPCell(Paragraph("Complaints", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
    c3.rowspan =
        if (FacilityDataModel.getInstance().tblComplaintFiles.size > 0) FacilityDataModel.getInstance().tblComplaintFiles.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strComplaints = ""
    FacilityDataModel.getInstance().tblComplaintFiles.apply {
        (0 until size).forEach {
            if (!get(it).ComplaintID.equals("")) {
                strComplaints = "Complaint ID: " + get(it).ComplaintID
                strComplaints += "\nFirst Name: " + get(it).FirstName
                strComplaints += "\nLastName: " + get(it).LastName
                strComplaints += "\nReceived Date: " + if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY()
                        .equals("01/01/1900")
                ) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY()
                strComplaints += "\nComplaints Reason: " + get(it).ComplaintReasonName
                strComplaints += "\nComplaints Resolution: " + get(it).ComplaintResolutionName
                table.addCell(Paragraph(strComplaints, normalFont))
            } else {
                table.addCell(Paragraph("", normalFont))
            }
        }
    }

//    if (strComplaints.equals("")) table.addCell(Paragraph("", normalFont))

    addDataCell(
        table,
        "Number of Complaints during previous 12 months",
        FacilityDataModel.getInstance().NumberofComplaints[0].NumberofComplaintslast12months,
        normalFont,
        true
    )
    addDataCell(
        table,
        "Number of Justified Complaints during previous 12 months",
        FacilityDataModel.getInstance().NumberofJustifiedComplaints[0].NumberofJustifiedComplaintslast12months,
        normalFont,
        true
    )
    addDataCell(
        table,
        "Justified Complaints Ratio",
        FacilityDataModel.getInstance().JustifiedComplaintRatio[0].JustifiedComplaintRatio,
        normalFont,
        true
    )

    return table
}

private fun addDataCell(
    table: PdfPTable,
    title: String,
    data: String,
    font: Font,
    alignCenter: Boolean
) {
    val c1 = PdfPCell(Paragraph(title, font))
    c1.horizontalAlignment = Element.ALIGN_LEFT
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c1);
    val c2 = PdfPCell(Paragraph(data, font));
    c2.horizontalAlignment =
        if (alignCenter) Element.ALIGN_CENTER else Element.ALIGN_LEFT // CHECK ALIGNMENT
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.left = 2F
    table.addCell(c2)
}

private fun addEmptyLine(document: Document, number: Int) {
    for (i in 0 until number) {
        document.add(Paragraph(" "))
    }

}


//fun verifyStoragePermissions(activity: FragmentActivity) {
//    // Check if we have write permission
//    if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//        } else {
//            ActivityCompat.requestPermissions(activity,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    100)
//        }
//    } else {
////        createPDF(true,act)
//    }
//}


class HeaderFooterPageEvent : PdfPageEventHelper() {

    var ffont = Font(Font.FontFamily.HELVETICA, 8F, Font.NORMAL)

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
//        val canvas = writer!!.directContentUnder
//        val rect = document!!.pageSize
//        canvas.setColorFill(BaseColor(229, 232, 232 ))
//        if (document!!.pageNumber==1)
//            canvas.rectangle(rect.left, rect.height, rect.width, 100f)
//        else
//            canvas.rectangle(rect.left, rect.height, rect.width, 25f)
//        canvas.fill()
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_CENTER, Phrase("Date: "+Date().toAppFormatMMDDYYYY(),ffont),((document!!.right()-document.left()) / 2 + document.leftMargin()),
//               document!!.bottom().minus(10), 0F)
//        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, Phrase("Top Right"), 550f, 820f, 0f)
    }

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_CENTER, Phrase("http://www.xxxx-your_example.com/"), 110f, 30f, 0f)
        val canvas = writer!!.directContentUnder
        val rect = document!!.pageSize
        canvas.setColorFill(BaseColor(229, 232, 232))
        canvas.rectangle(rect.left, rect.bottom, rect.width, 25f)
        canvas.fill()
        ColumnText.showTextAligned(
            writer?.directContent,
            Element.ALIGN_CENTER,
            Phrase("Page " + document!!.pageNumber, ffont),
            550f,
            10f,
            0f
        )
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_LEFT, Phrase("Date Printed: "+Date().toAppFormatMMDDYYYY(),ffont),35f, 20f, 0f)
        ColumnText.showTextAligned(
            writer!!.directContent,
            Element.ALIGN_LEFT,
            Phrase(
                "Fac No: " + FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + " - Name: " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - Visitation ID:" + Constants.visitationIDForPDF,
                ffont
            ),
            20f,
            10f,
            0f
        )
    }

}



fun checkInternetAndSpeed(context: Context): String {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return "No Internet Connection"
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return "No Internet Connection"

        // Check for Internet availability
        val isInternetAvailable =
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (!isInternetAvailable) {
            FirebaseCrashlytics.getInstance().log("Network Status: No Internet Connection")
            return "No Internet Connection"
        }
        var connectionType = when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                "Connected to WiFi"
            }

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                when {
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                        "Connected to Mobile Data"
                    }

                    else -> "Connected to Mobile Data - Unknown Speed"
                }
            }

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                "Connected to Ethernet"
            }

            else -> "No Internet Connection"
        }
        // Get estimated download and upload speeds
        val downloadSpeedKbps = networkCapabilities.linkDownstreamBandwidthKbps
        val uploadSpeedKbps = networkCapabilities.linkUpstreamBandwidthKbps
        val downloadSpeedMbps = downloadSpeedKbps / 1000.0
        val uploadSpeedMbps = uploadSpeedKbps / 1000.0

        Log.d("NetworkSpeed", "Estimated Download Speed: $downloadSpeedMbps Mbps")
        Log.d("NetworkSpeed", "Estimated Upload Speed: $uploadSpeedMbps Mbps")
        val nwStatus = """
            Internet is Available - 
            ConnectionType: $connectionType - 
            Estimated Download Speed: $downloadSpeedMbps Mbps - 
            Estimated Upload Speed: $uploadSpeedMbps Mbps
        """.trimIndent()
//        FirebaseCrashlytics.getInstance().log("Network Status: ${nwStatus}")
        FirebaseCrashlytics.getInstance()
            .setCustomKey("Internet Speed", "Network Status: ${nwStatus}")
//        return nwStatus
        return "Speed $downloadSpeedMbps-$uploadSpeedMbps"
    } else {
        return "Internet speed estimation is not supported on this Android version"
    }


}

fun capitalizeFirst(text: String): String {
    return text.lowercase().replaceFirstChar { it.uppercase() }
}

fun <T : Any> convertObjectToXml(obj: T): String {
    val xmlSerializer: XmlSerializer = Xml.newSerializer()
    val writer = StringWriter()

    xmlSerializer.setOutput(writer)
    xmlSerializer.startDocument("UTF-8", true)
    xmlSerializer.startTag("", obj::class.simpleName ?: "Object") // Root tag

    // ✅ Use Java Reflection (`declaredFields`)
    obj::class.java.declaredFields.forEach { field ->
        field.isAccessible = true  // Enable access to private fields
        val value = field.get(obj)?.toString() ?: ""

        // ✅ Avoid Java internal properties (e.g., `serialVersionUID`)
        if (!field.name.contains("$")) {
            xmlSerializer.startTag("", field.name)
            xmlSerializer.text(value)
            xmlSerializer.endTag("", field.name)
        }
    }

    xmlSerializer.endTag("", obj::class.simpleName ?: "Object")
    xmlSerializer.endDocument()
    return writer.toString()
}

fun convertObjectToJson(obj: Any): String {
    val gson = Gson()
    return gson.toJson(obj)
}


fun checkUnClearedDeficiencies(): String {
    var returnVal = ""
    var defList = ArrayList<String>()
    for (item in FacilityDataModel.getInstance().tblDeficiency.filter { s->s.DefTypeID!="-1" }) {
        Log.v("DeficiencyCheck", "Def Type ID: ${item.DefTypeID} - Cleared Date: ${item.ClearedDate}")
        val isNotCleared = (item.ClearedDate.contains("1900") || item.ClearedDate.isNullOrEmpty())
        if (isNotCleared) {
            defList.add(TypeTablesModel.getInstance().AARDeficiencyType.first { s -> s.DeficiencyTypeID == item.DefTypeID }.DeficiencyName)
//            returnVal += TypeTablesModel.getInstance().AARDeficiencyType.first { s -> s.DeficiencyTypeID == item.DefTypeID }.DeficiencyName + ", "
        }
    }
    for (def in defList.distinct()) {
        val count = defList.count { it == def }
        val def = if (count > 1) "$def ($count)" else def
        returnVal += "$def, "
    }
    returnVal = returnVal.removeSuffix(", ")
    return returnVal
}

//fun checkBillingAlert(): String {
//    var returnVal = ""
//    val sdf = SimpleDateFormat("MM/dd/yyyy")
//    val dueDate = sdf.parse(FacilityDataModel.getInstance().tblPersonnelCertification[i].ExpirationDate.apiToAppFormatMMDDYYYY())
//    if (FacilityDataModel.getInstance().tblBillingHistory[0].FACID!=-1 &&
//        FacilityDataModel.getInstance().tblBillingHistory[0].BillBalanceDue>0.0 &&
//        FacilityDataModel.getInstance().tblBillingHistory[0].BillingDueDate>0.0 &&
//        ) {
//        returnVal = "Alert Billing"
//
//    }
//    return returnVal
//}

fun checkDownStreamFlag(): Boolean {
    var returnVal = true
    if (!FacilityDataModel.getInstance().tblFacilities.first().Eligible) {
        returnVal = false
    }
    return returnVal
}

fun saveTodayVisitations(context: Context, list: List<TodayVisitationModel>) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString("todayVisitations", json)
    editor.apply()
}

fun addTodayVisitation(context: Context, newItem: TodayVisitationModel) {
    Log.v("TodayVisitation", "Adding Today Visitation: ${convertObjectToJson(newItem)} LAT: ${newItem.latitude} LAT: ${newItem.longitude} LON: ${newItem.longitude}")
    val currentList = getTodayVisitations(context).toMutableList()
    if (newItem.order == 0) {
        newItem.order = currentList.size + 1
    }
    currentList.add(newItem)
    saveTodayVisitations(context, currentList)
}

fun removeTodayVisitation(context: Context, facNumToRemove: String, clubCode: String) {
    Log.v("TodayVisitation", "Removing Today Visitation: ${facNumToRemove} - ClubCode: ${clubCode}")
    val currentList = getTodayVisitations(context).toMutableList()
    val updatedList = currentList.filterNot { it.facNum.toString() == facNumToRemove && it.clubCode == clubCode }
    updatedList.sortedBy { it.order }.forEachIndexed { index, visitation ->
        visitation.order = index + 1
    }
    saveTodayVisitations(context, updatedList)
}


fun updateVisitationNotes(
    context: Context,
    facNum: Int,
    clubCode: String,
    newNotes: String
) {
    val currentList = getTodayVisitations(context).toMutableList()

    val index = currentList.indexOfFirst { it.facNum == facNum && it.clubCode == clubCode }
    if (index != -1) {
        val updatedItem = currentList[index]
        updatedItem.notes = newNotes
        currentList[index] = updatedItem
        saveTodayVisitations(context, currentList)
    }
}

fun updateVisitationETA(
    context: Context,
    facNum: String,
    clubCode: String,
    eta: String
) {
    val currentList = getTodayVisitations(context).toMutableList()

    val index = currentList.indexOfFirst { it.facNum.toString() == facNum && it.clubCode == clubCode }
    if (index != -1) {
        val updatedItem = currentList[index]
        updatedItem.etaLabel = eta
        currentList[index] = updatedItem
        saveTodayVisitations(context, currentList)
    }
}

fun markSavedVisitationAsCompleted(
    context: Context,
    facNum: Int,
    clubCode: String
) {
    val currentList = getTodayVisitations(context).toMutableList()

    val index = currentList.indexOfFirst { it.facNum == facNum && it.clubCode == clubCode }
    if (index != -1) {
        val updatedItem = currentList[index]
        updatedItem.status = "Completed"
        currentList[index] = updatedItem
        saveTodayVisitations(context, currentList)
    }
}


fun updateVisitationLocation(
    context: Context,
    facNum: Int,
    clubCode: String,
    latitude: Double,
    longitude: Double
) {
    val currentList = getTodayVisitations(context).toMutableList()

    val index = currentList.indexOfFirst { it.facNum == facNum && it.clubCode == clubCode }
    if (index != -1) {
        val updatedItem = currentList[index]
        updatedItem.latitude = latitude
        updatedItem.longitude = longitude
        currentList[index] = updatedItem
        saveTodayVisitations(context, currentList)
    }
}

fun getTodayVisitations(context: Context): List<TodayVisitationModel> {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val json = prefs.getString("todayVisitations", null)
    return if (json != null) {
        val type = object : TypeToken<List<TodayVisitationModel>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

fun visitationExists(context: Context,facNum: Int, clubCode: String): Boolean {
    val currentList = getTodayVisitations(context)
    return currentList.any { it.facNum == facNum && it.clubCode == clubCode }
}

//fun parseFacilityDataJsonToObject(jsonObj: JSONObject,clubCode: String) {
//    FacilityDataModel.getInstance().clear()
//    FacilityDataModelOrg.getInstance().clear()
//    FacilityDataModel.getInstance().clubCode = clubCode
//    FacilityDataModelOrg.getInstance().clubCode = clubCode
//    if (jsonObj.has("tblFacilities")) {
//        if (jsonObj.get("tblFacilities").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilities = Gson().fromJson<ArrayList<TblFacilities>>(jsonObj.get("tblFacilities").toString(), object : TypeToken<ArrayList<TblFacilities>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilities.add(Gson().fromJson<TblFacilities>(jsonObj.get("tblFacilities").toString(), TblFacilities::class.java))
//        }
//    }
//    // Load PRG DATA
//
//    if (jsonObj.has("tblBusinessType")) {
//        if (jsonObj.get("tblBusinessType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblBusinessType = Gson().fromJson<ArrayList<TblBusinessType>>(jsonObj.get("tblBusinessType").toString(), object : TypeToken<ArrayList<TblBusinessType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
//            FacilityDataModelOrg.getInstance().tblBusinessType.add(Gson().fromJson<TblBusinessType>(jsonObj.get("tblBusinessType").toString(), TblBusinessType::class.java))
//        }
//    }
//
//
//    if (jsonObj.has("tblContractType")) {
//        if (jsonObj.get("tblContractType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblContractType = Gson().fromJson<ArrayList<TblContractType>>(jsonObj.get("tblContractType").toString(), object : TypeToken<ArrayList<TblContractType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
//            FacilityDataModelOrg.getInstance().tblContractType.add(Gson().fromJson<TblContractType>(jsonObj.get("tblContractType").toString(), TblContractType::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityServiceProvider")) {
//        if (jsonObj.get("tblFacilityServiceProvider").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityServiceProvider = Gson().fromJson<ArrayList<TblFacilityServiceProvider>>(jsonObj.get("tblFacilityServiceProvider").toString(), object : TypeToken<ArrayList<TblFacilityServiceProvider>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityServiceProvider.add(Gson().fromJson<TblFacilityServiceProvider>(jsonObj.get("tblFacilityServiceProvider").toString(), TblFacilityServiceProvider::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblTerminationCodeType")) {
//        if (jsonObj.get("tblTerminationCodeType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblTerminationCodeType = Gson().fromJson<ArrayList<TblTerminationCodeType>>(jsonObj.get("tblTerminationCodeType").toString(), object : TypeToken<ArrayList<TblTerminationCodeType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
//            FacilityDataModelOrg.getInstance().tblTerminationCodeType.add(Gson().fromJson<TblTerminationCodeType>(jsonObj.get("tblTerminationCodeType").toString(), TblTerminationCodeType::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblOfficeType")) {
//        if (jsonObj.get("tblOfficeType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblOfficeType = Gson().fromJson<ArrayList<TblOfficeType>>(jsonObj.get("tblOfficeType").toString(), object : TypeToken<ArrayList<TblOfficeType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
//            FacilityDataModelOrg.getInstance().tblOfficeType.add(Gson().fromJson<TblOfficeType>(jsonObj.get("tblOfficeType").toString(), TblOfficeType::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityManagers")) {
//        if (jsonObj.get("tblFacilityManagers").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityManagers = Gson().fromJson<ArrayList<TblFacilityManagers>>(jsonObj.get("tblFacilityManagers").toString(), object : TypeToken<ArrayList<TblFacilityManagers>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityManagers.add(Gson().fromJson<TblFacilityManagers>(jsonObj.get("tblFacilityManagers").toString(), TblFacilityManagers::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblTimezoneType")) {
//        if (jsonObj.get("tblTimezoneType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblTimezoneType = Gson().fromJson<ArrayList<TblTimezoneType>>(jsonObj.get("tblTimezoneType").toString(), object : TypeToken<ArrayList<TblTimezoneType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
//            FacilityDataModelOrg.getInstance().tblTimezoneType.add(Gson().fromJson<TblTimezoneType>(jsonObj.get("tblTimezoneType").toString(), TblTimezoneType::class.java))
//        }
//    }
//
//
//    if (jsonObj.has("tblVisitationTracking")) {
//        if (jsonObj.get("tblVisitationTracking").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblVisitationTracking = Gson().fromJson<ArrayList<TblVisitationTracking>>(jsonObj.get("tblVisitationTracking").toString(), object : TypeToken<ArrayList<TblVisitationTracking>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
//            FacilityDataModelOrg.getInstance().tblVisitationTracking.add(Gson().fromJson<TblVisitationTracking>(jsonObj.get("tblVisitationTracking").toString(), TblVisitationTracking::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityType")) {
//        if (jsonObj.get("tblFacilityType").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityType = Gson().fromJson<ArrayList<TblFacilityType>>(jsonObj.get("tblFacilityType").toString(), object : TypeToken<ArrayList<TblFacilityType>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityType.add(Gson().fromJson<TblFacilityType>(jsonObj.get("tblFacilityType").toString(), TblFacilityType::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblSurveySoftwares")) {
//        if (jsonObj.get("tblSurveySoftwares").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblSurveySoftwares = Gson().fromJson<ArrayList<TblSurveySoftwares>>(jsonObj.get("tblSurveySoftwares").toString(), object : TypeToken<ArrayList<TblSurveySoftwares>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
//            FacilityDataModelOrg.getInstance().tblSurveySoftwares.add(Gson().fromJson<TblSurveySoftwares>(jsonObj.get("tblSurveySoftwares").toString(), TblSurveySoftwares::class.java))
//        }
//    }
//
//
//    if (jsonObj.has("tblPaymentMethods")) {
//        if (jsonObj.get("tblPaymentMethods").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPaymentMethods = Gson().fromJson<ArrayList<TblPaymentMethods>>(jsonObj.get("tblPaymentMethods").toString(), object : TypeToken<ArrayList<TblPaymentMethods>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
//            FacilityDataModelOrg.getInstance().tblPaymentMethods.add(Gson().fromJson<TblPaymentMethods>(jsonObj.get("tblPaymentMethods").toString(), TblPaymentMethods::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblAddress")) {
//        if (jsonObj.get("tblAddress").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAddress = Gson().fromJson<ArrayList<TblAddress>>(jsonObj.get("tblAddress").toString(), object : TypeToken<ArrayList<TblAddress>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
//            FacilityDataModelOrg.getInstance().tblAddress.add(Gson().fromJson<TblAddress>(jsonObj.get("tblAddress").toString(), TblAddress::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblPhone")) {
//        if (jsonObj.get("tblPhone").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPhone = Gson().fromJson<ArrayList<TblPhone>>(jsonObj.get("tblPhone").toString(), object : TypeToken<ArrayList<TblPhone>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
//            FacilityDataModelOrg.getInstance().tblPhone.add(Gson().fromJson<TblPhone>(jsonObj.get("tblPhone").toString(), TblPhone::class.java))
//        }
//    }
//
//
//
//    if (jsonObj.has("tblFacilityEmail")) {
//        if (jsonObj.get("tblFacilityEmail").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityEmail = Gson().fromJson<ArrayList<TblFacilityEmail>>(jsonObj.get("tblFacilityEmail").toString(), object : TypeToken<ArrayList<TblFacilityEmail>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityEmail.add(Gson().fromJson<TblFacilityEmail>(jsonObj.get("tblFacilityEmail").toString(), TblFacilityEmail::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblHours")) {
//        if (jsonObj.get("tblHours").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblHours = Gson().fromJson<ArrayList<TblHours>>(jsonObj.get("tblHours").toString(), object : TypeToken<ArrayList<TblHours>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
//            FacilityDataModelOrg.getInstance().tblHours.add(Gson().fromJson<TblHours>(jsonObj.get("tblHours").toString(), TblHours::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityClosure")) {
//        if (jsonObj.get("tblFacilityClosure").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityClosure = Gson().fromJson<ArrayList<TblFacilityClosure>>(jsonObj.get("tblFacilityClosure").toString(), object : TypeToken<ArrayList<TblFacilityClosure>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityClosure.add(Gson().fromJson<TblFacilityClosure>(jsonObj.get("tblFacilityClosure").toString(), TblFacilityClosure::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblLanguage")) {
//        if (jsonObj.get("tblLanguage").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblLanguage = Gson().fromJson<ArrayList<TblLanguage>>(jsonObj.get("tblLanguage").toString(), object : TypeToken<ArrayList<TblLanguage>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
//            FacilityDataModelOrg.getInstance().tblLanguage.add(Gson().fromJson<TblLanguage>(jsonObj.get("tblLanguage").toString(), TblLanguage::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblPersonnel")) {
//        if (jsonObj.get("tblPersonnel").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPersonnel = Gson().fromJson<ArrayList<TblPersonnel>>(jsonObj.get("tblPersonnel").toString(), object : TypeToken<ArrayList<TblPersonnel>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
//            FacilityDataModelOrg.getInstance().tblPersonnel.add(Gson().fromJson<TblPersonnel>(jsonObj.get("tblPersonnel").toString(), TblPersonnel::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblAmendmentOrderTracking") && !jsonObj.has("tblAmendmentOrderTracking /")) {
//        if (jsonObj.get("tblAmendmentOrderTracking").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking = Gson().fromJson<ArrayList<TblAmendmentOrderTracking>>(jsonObj.get("tblAmendmentOrderTracking").toString(), object : TypeToken<ArrayList<TblAmendmentOrderTracking>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
//            FacilityDataModelOrg.getInstance().tblAmendmentOrderTracking.add(Gson().fromJson<TblAmendmentOrderTracking>(jsonObj.get("tblAmendmentOrderTracking").toString(), TblAmendmentOrderTracking::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblAARPortalAdmin")) {
//        if (jsonObj.get("tblAARPortalAdmin").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAARPortalAdmin = Gson().fromJson<ArrayList<TblAARPortalAdmin>>(jsonObj.get("tblAARPortalAdmin").toString(), object : TypeToken<ArrayList<TblAARPortalAdmin>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
//            FacilityDataModelOrg.getInstance().tblAARPortalAdmin.add(Gson().fromJson<TblAARPortalAdmin>(jsonObj.get("tblAARPortalAdmin").toString(), TblAARPortalAdmin::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblScopeofService")) {
//        if (jsonObj.get("tblScopeofService").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblScopeofService = Gson().fromJson<ArrayList<TblScopeofService>>(jsonObj.get("tblScopeofService").toString(), object : TypeToken<ArrayList<TblScopeofService>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
//            FacilityDataModelOrg.getInstance().tblScopeofService.add(Gson().fromJson<TblScopeofService>(jsonObj.get("tblScopeofService").toString(), TblScopeofService::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblPrograms")) {
//        var tempPrograms = ArrayList<TblPrograms>()
//        if (jsonObj.get("tblPrograms").toString().startsWith("[")) {
//            tempPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
////                FacilityDataModelOrg.getInstance().tblPrograms = Gson().fromJson<ArrayList<TblPrograms>>(jsonObj.get("tblPrograms").toString(), object : TypeToken<ArrayList<TblPrograms>>() {}.type)
//            tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModel.getInstance().tblPrograms)
//            tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModelOrg.getInstance().tblPrograms)
//        } else {
//            tempPrograms .add(Gson().fromJson<TblPrograms>(jsonObj.get("tblPrograms").toString(), TblPrograms::class.java))
//            tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModel.getInstance().tblPrograms)
//            tempPrograms.sortedWith(compareBy<TblPrograms> { it.expDate}).toCollection(FacilityDataModelOrg.getInstance().tblPrograms)
//        }
//    }
//
//    if (jsonObj.has("tblFacilityServices")) {
//        if (jsonObj.get("tblFacilityServices").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityServices = Gson().fromJson<ArrayList<TblFacilityServices>>(jsonObj.get("tblFacilityServices").toString(), object : TypeToken<ArrayList<TblFacilityServices>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityServices.add(Gson().fromJson<TblFacilityServices>(jsonObj.get("tblFacilityServices").toString(), TblFacilityServices::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblAffiliations")) {
//        if (jsonObj.get("tblAffiliations").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAffiliations = Gson().fromJson<ArrayList<TblAffiliations>>(jsonObj.get("tblAffiliations").toString(), object : TypeToken<ArrayList<TblAffiliations>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
//            FacilityDataModelOrg.getInstance().tblAffiliations.add(Gson().fromJson<TblAffiliations>(jsonObj.get("tblAffiliations").toString(), TblAffiliations::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblDeficiency")) {
//        if (jsonObj.get("tblDeficiency").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblDeficiency = Gson().fromJson<ArrayList<TblDeficiency>>(jsonObj.get("tblDeficiency").toString(), object : TypeToken<ArrayList<TblDeficiency>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
//            FacilityDataModelOrg.getInstance().tblDeficiency.add(Gson().fromJson<TblDeficiency>(jsonObj.get("tblDeficiency").toString(), TblDeficiency::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblComplaintFiles")) {
//        if (jsonObj.get("tblComplaintFiles").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblComplaintFiles = Gson().fromJson<ArrayList<TblComplaintFiles>>(jsonObj.get("tblComplaintFiles").toString(), object : TypeToken<ArrayList<TblComplaintFiles>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
//            FacilityDataModelOrg.getInstance().tblComplaintFiles.add(Gson().fromJson<TblComplaintFiles>(jsonObj.get("tblComplaintFiles").toString(), TblComplaintFiles::class.java))
//        }
//    }
//
//    if (jsonObj.has("NumberofComplaints")) {
//        if (jsonObj.get("NumberofComplaints").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
//            FacilityDataModelOrg.getInstance().NumberofComplaints = Gson().fromJson<ArrayList<numberofComplaints>>(jsonObj.get("NumberofComplaints").toString(), object : TypeToken<ArrayList<numberofComplaints>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
//            FacilityDataModelOrg.getInstance().NumberofComplaints.add(Gson().fromJson<numberofComplaints>(jsonObj.get("NumberofComplaints").toString(), numberofComplaints::class.java))
//        }
//    }
//
//    if (jsonObj.has("NumberofJustifiedComplaints")) {
//        if (jsonObj.get("NumberofJustifiedComplaints").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
//            FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints = Gson().fromJson<ArrayList<numberofJustifiedComplaints>>(jsonObj.get("NumberofJustifiedComplaints").toString(), object : TypeToken<ArrayList<numberofJustifiedComplaints>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
//            FacilityDataModelOrg.getInstance().NumberofJustifiedComplaints.add(Gson().fromJson<numberofJustifiedComplaints>(jsonObj.get("NumberofJustifiedComplaints").toString(), numberofJustifiedComplaints::class.java))
//        }
//    }
//
//    if (jsonObj.has("JustifiedComplaintRatio")) {
//        if (jsonObj.get("JustifiedComplaintRatio").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
//            FacilityDataModelOrg.getInstance().JustifiedComplaintRatio = Gson().fromJson<ArrayList<justifiedComplaintRatio>>(jsonObj.get("JustifiedComplaintRatio").toString(), object : TypeToken<ArrayList<justifiedComplaintRatio>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
//            FacilityDataModelOrg.getInstance().JustifiedComplaintRatio.add(Gson().fromJson<justifiedComplaintRatio>(jsonObj.get("JustifiedComplaintRatio").toString(), justifiedComplaintRatio::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityPhotos")) {
//        if (jsonObj.get("tblFacilityPhotos").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityPhotos = Gson().fromJson<ArrayList<TblFacilityPhotos>>(jsonObj.get("tblFacilityPhotos").toString(), object : TypeToken<ArrayList<TblFacilityPhotos>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityPhotos.add(Gson().fromJson<TblFacilityPhotos>(jsonObj.get("tblFacilityPhotos").toString(), TblFacilityPhotos::class.java))
//        }
//    }
//
//    if (jsonObj.has("Billing")) {
//        if (jsonObj.get("Billing").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblBilling = Gson().fromJson<ArrayList<TblBilling>>(jsonObj.get("Billing").toString(), object : TypeToken<ArrayList<TblBilling>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
//            FacilityDataModelOrg.getInstance().tblBilling.add(Gson().fromJson<TblBilling>(jsonObj.get("Billing").toString(), TblBilling::class.java))
//        }
//    }
//
//    if (jsonObj.has("BillingPlan")) {
//        if (jsonObj.get("BillingPlan").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblBillingPlan = Gson().fromJson<ArrayList<TblBillingPlan>>(jsonObj.get("BillingPlan").toString(), object : TypeToken<ArrayList<TblBillingPlan>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
//            FacilityDataModelOrg.getInstance().tblBillingPlan.add(Gson().fromJson<TblBillingPlan>(jsonObj.get("BillingPlan").toString(), TblBillingPlan::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityBillingDetail")) {
//        if (jsonObj.get("tblFacilityBillingDetail").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityBillingDetail = Gson().fromJson<ArrayList<TblFacilityBillingDetail>>(jsonObj.get("tblFacilityBillingDetail").toString(), object : TypeToken<ArrayList<TblFacilityBillingDetail>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityBillingDetail.add(Gson().fromJson<TblFacilityBillingDetail>(jsonObj.get("tblFacilityBillingDetail").toString(), TblFacilityBillingDetail::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblInvoiceInfo")) {
//        if (jsonObj.get("tblInvoiceInfo").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblInvoiceInfo = Gson().fromJson<ArrayList<TblInvoiceInfo>>(jsonObj.get("tblInvoiceInfo").toString(), object : TypeToken<ArrayList<TblInvoiceInfo>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
//            FacilityDataModelOrg.getInstance().tblInvoiceInfo.add(Gson().fromJson<TblInvoiceInfo>(jsonObj.get("tblInvoiceInfo").toString(), TblInvoiceInfo::class.java))
//        }
//    }
//
//    if (jsonObj.has("VendorRevenue")) {
//        if (jsonObj.get("VendorRevenue").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblVendorRevenue = Gson().fromJson<ArrayList<TblVendorRevenue>>(jsonObj.get("VendorRevenue").toString(), object : TypeToken<ArrayList<TblVendorRevenue>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
//            FacilityDataModelOrg.getInstance().tblVendorRevenue.add(Gson().fromJson<TblVendorRevenue>(jsonObj.get("VendorRevenue").toString(), TblVendorRevenue::class.java))
//        }
//    }
//
//    if (jsonObj.has("BillingHistory")) {
//        if (jsonObj.get("BillingHistory").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblBillingHistory = Gson().fromJson<ArrayList<TblBillingHistory>>(jsonObj.get("BillingHistory").toString(), object : TypeToken<ArrayList<TblBillingHistory>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
//            FacilityDataModelOrg.getInstance().tblBillingHistory.add(Gson().fromJson<TblBillingHistory>(jsonObj.get("BillingHistory").toString(), TblBillingHistory::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblComments")) {
//        if (jsonObj.get("tblComments").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblComments = Gson().fromJson<ArrayList<TblComments>>(jsonObj.get("tblComments").toString(), object : TypeToken<ArrayList<TblComments>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
//            FacilityDataModelOrg.getInstance().tblComments.add(Gson().fromJson<TblComments>(jsonObj.get("tblComments").toString(), TblComments::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblVehicleServices")) {
//        if (jsonObj.get("tblVehicleServices").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblVehicleServices = Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblVehicleServices= Gson().fromJson<ArrayList<TblVehicleServices>>(jsonObj.get("tblVehicleServices").toString(), object : TypeToken<ArrayList<TblVehicleServices>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
//            FacilityDataModelOrg.getInstance().tblVehicleServices.add(Gson().fromJson<TblVehicleServices>(jsonObj.get("tblVehicleServices").toString(), TblVehicleServices::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblAARPortalTracking")) {
//        if (jsonObj.get("tblAARPortalTracking").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAARPortalTracking = Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAARPortalTracking= Gson().fromJson<ArrayList<TblAARPortalTracking>>(jsonObj.get("tblAARPortalTracking").toString(), object : TypeToken<ArrayList<TblAARPortalTracking>>() {}.type)
////                FacilityDataModel.getInstance().tblAARPortalTracking = FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
////                FacilityDataModelOrg.getInstance().tblAARPortalTracking = FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
//        } else {
//            FacilityDataModel.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
//            FacilityDataModelOrg.getInstance().tblAARPortalTracking.add(Gson().fromJson<TblAARPortalTracking>(jsonObj.get("tblAARPortalTracking").toString(), TblAARPortalTracking::class.java))
////                FacilityDataModel.getInstance().tblAARPortalTracking = FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
////                FacilityDataModelOrg.getInstance().tblAARPortalTracking = FacilityDataModelOrg.getInstance().tblAARPortalTracking.sortedWith(compareByDescending<TblAARPortalTracking> { it.PortalInspectionDate })
//        }
//    }
//
//    if (jsonObj.has("tblPersonnelCertification")) {
//        if (jsonObj.get("tblPersonnelCertification").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPersonnelCertification = Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPersonnelCertification= Gson().fromJson<ArrayList<TblPersonnelCertification>>(jsonObj.get("tblPersonnelCertification").toString(), object : TypeToken<ArrayList<TblPersonnelCertification>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
//            FacilityDataModelOrg.getInstance().tblPersonnelCertification.add(Gson().fromJson<TblPersonnelCertification>(jsonObj.get("tblPersonnelCertification").toString(), TblPersonnelCertification::class.java))
//        }
//    }
//
//    if (jsonObj.has("BillingAdjustments")) {
//        if (jsonObj.get("BillingAdjustments").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblBillingAdjustments = Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblBillingAdjustments= Gson().fromJson<ArrayList<TblBillingAdjustments>>(jsonObj.get("BillingAdjustments").toString(), object : TypeToken<ArrayList<TblBillingAdjustments>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
//            FacilityDataModelOrg.getInstance().tblBillingAdjustments.add(Gson().fromJson<TblBillingAdjustments>(jsonObj.get("BillingAdjustments").toString(), TblBillingAdjustments::class.java))
//        }
//    }
//
//    if (jsonObj.has("AAAPortalEmailFacilityRepTable")) {
//        if (jsonObj.get("AAAPortalEmailFacilityRepTable").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable = Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable= Gson().fromJson<ArrayList<TblAAAPortalEmailFacilityRepTable>>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), object : TypeToken<ArrayList<TblAAAPortalEmailFacilityRepTable>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
//            FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.add(Gson().fromJson<TblAAAPortalEmailFacilityRepTable>(jsonObj.get("AAAPortalEmailFacilityRepTable").toString(), TblAAAPortalEmailFacilityRepTable::class.java))
//        }
////            FacilityDataModel.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt() }).toCollection()
////            FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable = FacilityDataModelOrg.getInstance().tblAAAPortalEmailFacilityRepTable.sortedWith(compareBy{ it.Year.toInt()}).sortedWith(compareBy { it.Quarter.toInt() }).sortedWith(compareBy { it.Month.toInt()}))
//    }
////
//    if (jsonObj.has("InvoiceInfo")) {
//        if (jsonObj.get("InvoiceInfo").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated = Gson().fromJson<ArrayList<InvoiceInfo>>(jsonObj.get("InvoiceInfo").toString(), object : TypeToken<ArrayList<InvoiceInfo>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
//            FacilityDataModelOrg.getInstance().tblInvoiceInfoUpdated.add(Gson().fromJson<InvoiceInfo>(jsonObj.get("InvoiceInfo").toString(), InvoiceInfo::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacVehicles")) {
//        if (jsonObj.get("tblFacVehicles").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacVehicles = Gson().fromJson<ArrayList<TblFacVehicles>>(jsonObj.get("tblFacVehicles").toString(), object : TypeToken<ArrayList<TblFacVehicles>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
//            FacilityDataModelOrg.getInstance().tblFacVehicles.add(Gson().fromJson<TblFacVehicles>(jsonObj.get("tblFacVehicles").toString(), TblFacVehicles::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblPersonnelSigner")) {
//        if (jsonObj.get("tblPersonnelSigner").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPersonnelSigner = Gson().fromJson<ArrayList<TblPersonnelSigner>>(jsonObj.get("tblPersonnelSigner").toString(), object : TypeToken<ArrayList<TblPersonnelSigner>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
//            FacilityDataModelOrg.getInstance().tblPersonnelSigner.add(Gson().fromJson<TblPersonnelSigner>(jsonObj.get("tblPersonnelSigner").toString(), TblPersonnelSigner::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblGeocodes")) {
//        if (jsonObj.get("tblGeocodes").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblGeocodes = Gson().fromJson<ArrayList<TblGeocodes>>(jsonObj.get("tblGeocodes").toString(), object : TypeToken<ArrayList<TblGeocodes>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
//            FacilityDataModelOrg.getInstance().tblGeocodes.add(Gson().fromJson<TblGeocodes>(jsonObj.get("tblGeocodes").toString(), TblGeocodes::class.java))
//        }
//    }
//
//    if (jsonObj.has("AffiliateVendorFacilities")) {
//        if (jsonObj.get("AffiliateVendorFacilities").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities = Gson().fromJson<ArrayList<AffiliateVendorFacilities>>(jsonObj.get("AffiliateVendorFacilities").toString(), object : TypeToken<ArrayList<AffiliateVendorFacilities>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
//            FacilityDataModelOrg.getInstance().tblAffiliateVendorFacilities.add(Gson().fromJson<AffiliateVendorFacilities>(jsonObj.get("AffiliateVendorFacilities").toString(), AffiliateVendorFacilities::class.java))
//        }
//    }
//
//    if (jsonObj.has("Promotions")) {
//        if (jsonObj.get("Promotions").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblPromotions = Gson().fromJson<ArrayList<TblPromotions>>(jsonObj.get("Promotions").toString(), object : TypeToken<ArrayList<TblPromotions>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
//            FacilityDataModelOrg.getInstance().tblPromotions.add(Gson().fromJson<TblPromotions>(jsonObj.get("Promotions").toString(), TblPromotions::class.java))
//        }
//    }
//
//    if (jsonObj.has("FacilityPhotos")) {
//        if (jsonObj.get("FacilityPhotos").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().FacilityPhotos = Gson().fromJson<ArrayList<FacilityPhotos>>(jsonObj.get("FacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityPhotos>>() {}.type)
//            FacilityDataModelOrg.getInstance().FacilityPhotos = Gson().fromJson<ArrayList<FacilityPhotos>>(jsonObj.get("FacilityPhotos").toString(), object : TypeToken<ArrayList<FacilityPhotos>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().FacilityPhotos.add(Gson().fromJson<FacilityPhotos>(jsonObj.get("FacilityPhotos").toString(), FacilityPhotos::class.java))
//            FacilityDataModelOrg.getInstance().FacilityPhotos.add(Gson().fromJson<FacilityPhotos>(jsonObj.get("FacilityPhotos").toString(), FacilityPhotos::class.java))
//        }
//    }
//
//    if (jsonObj.has("tblFacilityBillingHeader")) {
//        if (jsonObj.get("tblFacilityBillingHeader").toString().startsWith("[")) {
//            FacilityDataModel.getInstance().tblFacilityBillingHeader = Gson().fromJson<ArrayList<TblFacilityBillingHeader>>(jsonObj.get("tblFacilityBillingHeader").toString(), object : TypeToken<ArrayList<TblFacilityBillingHeader>>() {}.type)
//            FacilityDataModelOrg.getInstance().tblFacilityBillingHeader = Gson().fromJson<ArrayList<TblFacilityBillingHeader>>(jsonObj.get("tblFacilityBillingHeader").toString(), object : TypeToken<ArrayList<TblFacilityBillingHeader>>() {}.type)
//        } else {
//            FacilityDataModel.getInstance().tblFacilityBillingHeader.add(Gson().fromJson<TblFacilityBillingHeader>(jsonObj.get("tblFacilityBillingHeader").toString(), TblFacilityBillingHeader::class.java))
//            FacilityDataModelOrg.getInstance().tblFacilityBillingHeader.add(Gson().fromJson<TblFacilityBillingHeader>(jsonObj.get("tblFacilityBillingHeader").toString(), TblFacilityBillingHeader::class.java))
//        }
//    }
//
//    HasChangedModel.getInstance().init()
////        IndicatorsDataModel.getInstance().validateAllScreensVisited()
//}

//fun removeEmptyJsonTagsOld(jsonObjOrg : JSONObject) : JSONObject {
//    var jsonObj = jsonObjOrg;
//
//    if (jsonObj.has("tblSurveySoftwares")) {
//        if (!jsonObj.get("tblSurveySoftwares").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblSurveySoftwares")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblSurveySoftwares"))
//                jsonObj.put("tblSurveySoftwares", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblSurveySoftwares")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblSurveySoftwares")
//    }
//
//    if (jsonObj.has("tblAddress")) {
//        if (!jsonObj.get("tblAddress").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAddress")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAddress"))
//                jsonObj.put("tblAddress", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAddress")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblAddress")
//    }
//
//    if (jsonObj.has("tblGeocodes")) {
//        if (!jsonObj.get("tblGeocodes").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblGeocodes")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblGeocodes"))
//                jsonObj.put("tblGeocodes", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblGeocodes")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblGeocodes")
//    }
////
//    if (jsonObj.has("tblVisitationTracking")) {
//        if (!jsonObj.get("tblVisitationTracking").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblVisitationTracking")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblVisitationTracking"))
//                jsonObj.put("tblVisitationTracking", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblVisitationTracking")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblVisitationTracking")
//    }
//
//    if (jsonObj.has("tblPhone")) {
//        if (!jsonObj.get("tblPhone").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblPhone")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblPhone"))
//                jsonObj.put("tblPhone", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblPhone")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblPhone")
//    }
//
//    if (jsonObj.has("tblFacilityEmail")) {
//        if (!jsonObj.get("tblFacilityEmail").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityEmail")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityEmail"))
//                jsonObj.put("tblFacilityEmail", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityEmail")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblFacilityEmail")
//    }
//
//
//    if (jsonObj.has("tblOfficeType")) {
//        if (!jsonObj.get("tblOfficeType").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblOfficeType")
//                for (i in result.length()-1 downTo 0){
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblOfficeType"))
//                jsonObj.put("tblOfficeType",result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblOfficeType")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblOfficeType")
//    }
//
//    if (jsonObj.has("tblPersonnel")) {
//        if (!jsonObj.get("tblPersonnel").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblPersonnel")
//                for (i in result.length()-1 downTo 0){
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblPersonnel"))
//                jsonObj.put("tblPersonnel",result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblPersonnel")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblPersonnel")
//    }
//
//    if (jsonObj.has("tblAmendmentOrderTracking")) {
//        if (!jsonObj.get("tblAmendmentOrderTracking").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAmendmentOrderTracking")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAmendmentOrderTracking"))
//                jsonObj.put("tblAmendmentOrderTracking", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAmendmentOrderTracking")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblAmendmentOrderTracking")
//    }
//
//    if (jsonObj.has("tblAARPortalAdmin")) {
//        if (!jsonObj.get("tblAARPortalAdmin").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAARPortalAdmin")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAARPortalAdmin"))
//                jsonObj.put("tblAARPortalAdmin", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalAdmin")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblAARPortalAdmin")
//    }
//
//
//    if (jsonObj.has("tblScopeofService")) {
//        if (!jsonObj.get("tblScopeofService").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblScopeofService")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblScopeofService"))
//                jsonObj.put("tblScopeofService", result)
//            } catch (e:Exception){
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblScopeofService")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblScopeofService")
//    }
//
//    if (jsonObj.has("tblPrograms")) {
//        if (!jsonObj.get("tblPrograms").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblPrograms")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblPrograms"))
//                jsonObj.put("tblPrograms", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblPrograms")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblPrograms")
//    }
//
//    // check if the tag exists
//    if (jsonObj.has("tblFacilityServices")) {
//        if (!jsonObj.get("tblFacilityServices").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityServices")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityServices"))
//                jsonObj.put("tblFacilityServices", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj,"tblFacilityServices")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj,"tblFacilityServices")
//    }
//
//    if (jsonObj.has("tblAffiliations")) {
//        if (!jsonObj.get("tblAffiliations").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAffiliations")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAffiliations"))
//                jsonObj.put("tblAffiliations", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAffiliations")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblAffiliations")
//    }
//
//    if (jsonObj.has("tblDeficiency")) {
//        if (!jsonObj.get("tblDeficiency").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblDeficiency")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblDeficiency"))
//                jsonObj.put("tblDeficiency",result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblDeficiency")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblDeficiency")
//    }
//
//    if (jsonObj.has("tblComplaintFiles")) {
//        if (!jsonObj.get("tblComplaintFiles").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblComplaintFiles")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblComplaintFiles"))
//                jsonObj.put("tblComplaintFiles", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblComplaintFiles")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblComplaintFiles")
//    }
//
//    if (jsonObj.has("tblFacilityPhotos")) {
//        if (!jsonObj.get("tblFacilityPhotos").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityPhotos")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityPhotos"))
//                jsonObj.put("tblFacilityPhotos", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityPhotos")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityPhotos")
//    }
//
//    if (jsonObj.has("Billing")) {
//        if (!jsonObj.get("Billing").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("Billing")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("Billing"))
//                jsonObj.put("Billing", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "Billing")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "Billing")
//    }
//
//    if (jsonObj.has("BillingPlan")) {
//        if (!jsonObj.get("BillingPlan").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblBillingPlan")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("BillingPlan"))
//                jsonObj.put("BillingPlan", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "BillingPlan")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "BillingPlan")
//    }
//
//    if (jsonObj.has("tblFacilityBillingDetail")) {
//        if (!jsonObj.get("tblFacilityBillingDetail").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityBillingDetail")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityBillingDetail"))
//                jsonObj.put("tblFacilityBillingDetail", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingDetail")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingDetail")
//    }
//
//    if (jsonObj.has("tblInvoiceInfo")) {
//        if (!jsonObj.get("tblInvoiceInfo").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblInvoiceInfo")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblInvoiceInfo"))
//                jsonObj.put("tblInvoiceInfo", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblInvoiceInfo")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblInvoiceInfo")
//    }
//
//    if (jsonObj.has("VendorRevenue")) {
//        if (!jsonObj.get("VendorRevenue").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("VendorRevenue")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("VendorRevenue"))
//                jsonObj.put("VendorRevenue", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
//    }
//
//    if (jsonObj.has("BillingHistory")) {
//        if (!jsonObj.get("BillingHistory").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("BillingHistory")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("BillingHistory"))
//                jsonObj.put("BillingHistory", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "BillingHistory")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "BillingHistory")
//    }
//
//    if (jsonObj.has("tblComments")) {
//        if (!jsonObj.get("tblComments").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblComments")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblComments"))
//                jsonObj.put("tblComments", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblComments")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblComments")
//    }
//
//    if (jsonObj.has("tblVehicleServices")) {
//        if (!jsonObj.get("tblVehicleServices").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblVehicleServices")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblVehicleServices"))
//                jsonObj.put("tblVehicleServices", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblVehicleServices")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblVehicleServices")
//    }
//
//    if (jsonObj.has("tblAARPortalTracking")) {
//        if (!jsonObj.get("tblAARPortalTracking").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAARPortalTracking")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAARPortalTracking"))
//                jsonObj.put("tblAARPortalTracking", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalTracking")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblAARPortalTracking")
//    }
//
//    if (jsonObj.has("tblPersonnelCertification")) {
//        if (!jsonObj.get("tblPersonnelCertification").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblPersonnelCertification")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblPersonnelCertification"))
//                jsonObj.put("tblPersonnelCertification", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelCertification")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelCertification")
//    }
//
//    if (jsonObj.has("BillingAdjustments")) {
//        if (!jsonObj.get("BillingAdjustments").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("BillingAdjustments")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("BillingAdjustments"))
//                jsonObj.put("BillingAdjustments", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "BillingAdjustments")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "BillingAdjustments")
//    }
//
//    if (jsonObj.has("tblAAAPortalEmailFacilityRepTable")) {
//        if (!jsonObj.get("tblAAAPortalEmailFacilityRepTable").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblAAAPortalEmailFacilityRepTable")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblAAAPortalEmailFacilityRepTable"))
//                jsonObj.put("tblAAAPortalEmailFacilityRepTable", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblAAAPortalEmailFacilityRepTable")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblAAAPortalEmailFacilityRepTable")
//    }
//
//    if (jsonObj.has("InvoiceInfo")) {
//        if (!jsonObj.get("InvoiceInfo").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("InvoiceInfo")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("InvoiceInfo"))
//                jsonObj.put("InvoiceInfo", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "InvoiceInfo")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "InvoiceInfo")
//    }
//
//    if (jsonObj.has("tblFacVehicles")) {
//        if (!jsonObj.get("tblFacVehicles").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacVehicles")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacVehicles"))
//                jsonObj.put("tblFacVehicles", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacVehicles")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacVehicles")
//    }
//
//    if (jsonObj.has("tblPersonnelSigner")) {
//        if (!jsonObj.get("tblPersonnelSigner").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblPersonnelSigner")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblPersonnelSigner"))
//                jsonObj.put("tblPersonnelSigner", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelSigner")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblPersonnelSigner")
//    }
//
//    if (jsonObj.has("tblHours")) {
//        if (!jsonObj.get("tblHours").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblHours")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblHours"))
//                jsonObj.put("tblHours", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblHours")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblHours")
//    }
//
//    if (jsonObj.has("tblTerminationCodeType")) {
//        if (!jsonObj.get("tblTerminationCodeType").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblTerminationCodeType")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblTerminationCodeType"))
//                jsonObj.put("tblTerminationCodeType", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblTerminationCodeType")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblTerminationCodeType")
//    }
//
//    if (jsonObj.has("tblBusinessType")) {
//        if (!jsonObj.get("tblBusinessType").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblBusinessType")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblBusinessType"))
//                jsonObj.put("tblBusinessType", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblBusinessType")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblBusinessType")
//    }
//
//    if (jsonObj.has("tblFacilityClosure")) {
//        if (!jsonObj.get("tblFacilityClosure").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityClosure")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityClosure"))
//                jsonObj.put("tblFacilityClosure", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityClosure")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityClosure")
//    }
//
//    if (jsonObj.has("tblFacilityManagers")) {
//        if (!jsonObj.get("tblFacilityManagers").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityManagers")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityManagers"))
//                jsonObj.put("tblFacilityManagers", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityManagers")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityManagers")
//    }
//
//    if (jsonObj.has("tblFacilityServiceProvider")) {
//        if (!jsonObj.get("tblFacilityServiceProvider").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityServiceProvider")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityServiceProvider"))
//                jsonObj.put("tblFacilityServiceProvider", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityServiceProvider")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityServiceProvider")
//    }
//
//    if (jsonObj.has("VendorRevenue")) {
//        if (!jsonObj.get("VendorRevenue").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("VendorRevenue")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("VendorRevenue"))
//                jsonObj.put("VendorRevenue", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "VendorRevenue")
//    }
//
//    if (jsonObj.has("tblFacilityType")) {
//        if (!jsonObj.get("tblFacilityType").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityType")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityType"))
//                jsonObj.put("tblFacilityType", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityType")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityType")
//    }
//
//    if (jsonObj.has("FacilityPhotos")) {
//        if (!jsonObj.get("FacilityPhotos").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("FacilityPhotos")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("FacilityPhotos"))
//                jsonObj.put("FacilityPhotos", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "FacilityPhotos")
//        }
//    }
//
//    if (jsonObj.has("Promotions")) {
//        if (!jsonObj.get("Promotions").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("Promotions")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("Promotions"))
//                jsonObj.put("Promotions", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "Promotions")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "Promotions")
//    }
//
//    if (jsonObj.has("tblFacilityBillingHeader")) {
//        if (!jsonObj.get("tblFacilityBillingHeader").toString().equals("")) {
//            try {
//                var result = jsonObj.getJSONArray("tblFacilityBillingHeader")
//                for (i in result.length() - 1 downTo 0) {
//                    if (result[i].toString().equals("")) result.remove(i);
//                }
//                jsonObj.remove(("tblFacilityBillingHeader"))
//                jsonObj.put("tblFacilityBillingHeader", result)
//            } catch (e: Exception) {
//
//            }
//        } else {
//            jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingHeader")
//        }
//    } else {
//        jsonObj = addOneElementtoKey(jsonObj, "tblFacilityBillingHeader")
//    }
////
//    return jsonObj
//}

fun addOneElementtoKey (jsonObj: JsonObject, key: String) : JsonObject {
    if (key.equals("tblFacilityServices")) {
        var oneArray = TblFacilityServices();
        oneArray.Comments = "";
        oneArray.ServiceID = "";
        oneArray.effDate = "";
        oneArray.expDate = "";
        oneArray.FacilityServicesID="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAffiliations")) {
        var oneArray = TblAffiliations()
        oneArray.AffiliationID = -1
        oneArray.AffiliationTypeDetailID = 0
        oneArray.AffiliationTypeID = 0
        oneArray.effDate = "";
        oneArray.comment = ""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblDeficiency")) {
        var oneArray = TblDeficiency()
        oneArray.ClearedDate = ""
        oneArray.Comments = ""
        oneArray.DefTypeID = "-1"
        oneArray.EnteredDate = ""
        oneArray.VisitationDate = ""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblComplaintFiles")) {
        var oneArray = TblComplaintFiles()
        oneArray.ComplaintID = ""
        oneArray.FirstName = ""
        oneArray.LastName = ""
        oneArray.ReceivedDate = ""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
        //
    } else if (key.equals("tblVisitationTracking")) {
        var oneArray = TblVisitationTracking()
        oneArray.AARSigns=""
        oneArray.visitationID="-1"
        oneArray.CertificateOfApproval=""
        oneArray.DatePerformed=""
        oneArray.MemberBenefitPoster=""
        oneArray.QualityControl=""
        oneArray.StaffTraining=""
        oneArray.automotiveSpecialistName=""
        oneArray.automotiveSpecialistSignature=null
        oneArray.email=""
        oneArray.emailVisitationPdfToFacility=false
        oneArray.facilityRepresentativeDeficienciesSignature=null
        oneArray.performedBy="00"
        oneArray.visitationType=null
        oneArray.waiveVisitations=false
        oneArray.waiverComments=""
        oneArray.waiverSignature=null
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAmendmentOrderTracking")) {
        var oneArray = TblAmendmentOrderTracking()
        oneArray.AOID = ""
        oneArray.ReasonID = ""
        oneArray.EventTypeID = ""
        oneArray.EventID = ""
        oneArray.AOTEmployee = ""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblScopeofService")) {
        var oneArray = TblScopeofService()
        oneArray.WarrantyTypeID=""
        oneArray.NumOfLifts=""
        oneArray.DiagnosticsRate=""
        oneArray.FixedLaborRate=""
        oneArray.LaborMax=""
        oneArray.LaborMin=""
        oneArray.NumOfBays=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblSurveySoftwares")) {
        var oneArray = TblSurveySoftwares()
        oneArray.FACID=0
        oneArray.SoftwareSurveyNum=0
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.updateBy=""
        oneArray.updateDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAddress")) {
        var oneArray = TblAddress()
        oneArray.BranchName=""
        oneArray.BranchNumber=""
        oneArray.CITY=""
        oneArray.County=""
        oneArray.FAC_Addr1=""
        oneArray.FAC_Addr2=""
        oneArray.LATITUDE=""
        oneArray.LONGITUDE=""
        oneArray.LocationTypeID=""
        oneArray.ST=""
        oneArray.ZIP=""
        oneArray.ZIP4=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityEmail")) {
        var oneArray = TblFacilityEmail()
        oneArray.email=""
        oneArray.emailID="-1"
        oneArray.emailTypeId=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblPhone")) {
        var oneArray = TblPhone()
        oneArray.PhoneNumber=""
        oneArray.PhoneTypeID=""
        oneArray.PhoneID="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblGeocodes")) {
        var oneArray = TblGeocodes()
        oneArray.GeoCodeTypeID=-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAARPortalAdmin")) {
        var oneArray = TblAARPortalAdmin()
        oneArray.AddendumSigned=""
        oneArray.CardReaders="-1"

        oneArray.startDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblPrograms")) {
        var oneArray = TblPrograms()
        oneArray.Comments=""
        oneArray.ProgramID = "-1"
        oneArray.ProgramTypeID=""
        oneArray.effDate=""
        oneArray.expDate=""
        oneArray.programtypename=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityPhotos")) {
        var oneArray = TblFacilityPhotos()
        oneArray.ApprovalRequested=""
        oneArray.Approved=""
        oneArray.ApprovedBy=""
        oneArray.ApprovedDate=""
        oneArray.FileDescription=""
        oneArray.FileName=""
        oneArray.LastUpdateBy=""
        oneArray.LastUpdateDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("Billing")) {
        var oneArray = TblBilling()
        oneArray.ACHParticipant=0
        oneArray.BillingAmount=0.0
        oneArray.BillingDate=""
        oneArray.BillingID=-1
        oneArray.BillingMonthNumber=0
        oneArray.CreditAmountDue=""
        oneArray.FACID=0
        oneArray.PaymentAmount=0.0
        oneArray.PendingAmount=0.0
        oneArray.PaymentDate=""
        oneArray.RevenueSourceID=0
        oneArray.SecondBillDate=""
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.updateBy=""
        oneArray.updateDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("BillingPlan")) {
        var oneArray = TblBillingPlan()
        oneArray.BillingPlanCatgID=0
        oneArray.BillingPlanID=-1
        oneArray.BillingPlanTypeID=0
        oneArray.EffectiveDate=""
        oneArray.ExpirationDate=""
        oneArray.FACID=0
        oneArray.FrequencyTypeID=0
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.updateBy=""
        oneArray.updateDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityBillingDetail")) {
        var oneArray = TblFacilityBillingDetail()
        oneArray.FacBillId=-1
        oneArray.BillingPlanID=-1
        oneArray.BillingPlanTypeID=0
        oneArray.BillAmount=0.0
        oneArray.BillDueDate=""
        oneArray.FACID=0
        oneArray.BillSeqInCycle=0
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.BillingInvoiceDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblInvoiceInfo")) {
        var oneArray = TblInvoiceInfo()
        oneArray.ACHParticipant=false
        oneArray.BillingDueDate=""
        oneArray.CreditAmount=0.0
        oneArray.InvoiceAmount=0.0
        oneArray.InvoiceFileName=""
        oneArray.FACID=0
        oneArray.InvoiceId=-1
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.updateBy=""
        oneArray.updateDate=""
        oneArray.InvoicePrintDate=""
        oneArray.InvoiceStatusId=0
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("VendorRevenue")) {
        var oneArray = TblVendorRevenue()
        oneArray.Amount=""
        oneArray.Comments=""
        oneArray.DateOfCheck=""
        oneArray.FACID=0
        oneArray.ReceiptDate=""
        oneArray.FACID=0
        oneArray.ReceiptNumber=""
        oneArray.insertBy=""
        oneArray.insertDate=""
        oneArray.updateBy=""
        oneArray.updateDate=""
        oneArray.RevenueSourceID=0
        oneArray.StateRevenueAcct=""
        oneArray.VendorRevenueID=-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("BillingHistory")) {
        var oneArray = TblBillingHistory()
        oneArray.InvoiceId = -1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblOfficeType")) {
        var oneArray = TblOfficeType()
        oneArray.OfficeName=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblComments")) {
        var oneArray = TblComments()
        oneArray.FACID=0
        oneArray.Comment=""
        oneArray.insertDate=""
        oneArray.CommentTypeID=0
        oneArray.SeqNum=0
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblPersonnel")) {
        var oneArray = TblPersonnel()
        oneArray.Addr1=""
        oneArray.Addr2=""
        oneArray.CITY=""
        oneArray.CertificationDate=""
        oneArray.CertificationNum=""
        oneArray.CertificationTypeId=""
        oneArray.ContractSigner=false
        oneArray.endDate=""
        oneArray.FirstName=""
        oneArray.LastName=""
        oneArray.PersonnelTypeID=0
        oneArray.RSP_Phone=""
        oneArray.PersonnelID = -1
        oneArray.PrimaryMailRecipient=false
        oneArray.RSP_Email=""
        oneArray.RSP_UserName=""
        oneArray.ST=""
        oneArray.SeniorityDate=""
        oneArray.ZIP=""
        oneArray.ZIP4=""
        oneArray.email=""
        oneArray.startDate=""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblVehicleServices")) {
        var oneArray = TblVehicleServices()
        oneArray.FACID = 0
        oneArray.ScopeServiceID = -1
        oneArray.VehiclesTypeID = -1
        oneArray.insertBy=""
        oneArray.insertDate = ""
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAARPortalTracking")) {
        var oneArray = TblAARPortalTracking()
        oneArray.TrackingID="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblPersonnelCertification")) {
        var oneArray = TblPersonnelCertification()
        oneArray.PersonnelID=0
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("BillingAdjustments")) {
        var oneArray = TblBillingAdjustments()
        oneArray.AdjustmentId=-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblAAAPortalEmailFacilityRepTable")) {
        var oneArray = TblAAAPortalEmailFacilityRepTable()
        oneArray.ContractSID="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("InvoiceInfo")) {
        var oneArray = InvoiceInfo()
        oneArray.InvoiceId="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacVehicles")) {
        var oneArray = TblFacVehicles()
        oneArray.VehicleID =-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblPersonnelSigner")) {
        var oneArray = TblPersonnelSigner()
        oneArray.PersonnelID = -1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblHours")) {
        var oneArray = TblHours()
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblBusinessType")) {
        var oneArray = TblBusinessType()
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblTerminationCodeType")) {
        var oneArray = TblBusinessType()
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityClosure")) {
        var oneArray = TblFacilityClosure()
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityManagers")) {
        var oneArray = TblFacilityManagers()
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityServiceProvider")) {
        var oneArray = TblFacilityServiceProvider()
        oneArray.SrvProviderId="-1"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityType")) {
        var oneArray = TblFacilityType()
        oneArray.FacilityTypeName="Independent"
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("Promotions")) {
        var oneArray = TblPromotions()
        oneArray.PromoID=-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("FacilityPhotos")) {
        var oneArray = FacilityPhotos()
        oneArray.PhotoId=-1
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    } else if (key.equals("tblFacilityBillingHeader")) {
        var oneArray = TblFacilityBillingHeader()
        oneArray.FACId=-1
        oneArray.BillBalanceDue="0.0"
        oneArray.ACHParticipant=false
//        jsonObj.put(key, Gson().toJson(oneArray))
        val array = JsonArray()
        array.add(Gson().toJsonTree(oneArray))
        jsonObj.add(key, array)
    }
    return jsonObj;
}


fun saveApplicantShops(context: Context, list: List<Place>,clearAll: Boolean= false) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    // Get existing shops
    val currentList = getApplicantShops(context).toMutableList()
    if (clearAll) {
        currentList.clear()
    }
    for (place in list) {
        // Ensure etaLabel is never null
        if (place.etaLabel == null) {
            place.etaLabel = ""
        }

        // Find existing index (use unique id)
        val index = currentList.indexOfFirst { it.id == place.id }
        if (index == -1) {
            currentList.add(place)
        }
    }

    val gson = Gson()
    val json = gson.toJson(currentList)
    editor.putString("applicantShops", json)
    editor.apply()
    Utility.showUnifiedConfirmationDialog(context,"Total Shops: ${currentList.size}")
}


fun addSavedApplicantShop(context: Context, newItem: Place) {
    val currentList = getSavedApplicantShops(context).toMutableList()
    newItem.order = currentList.size + 1
    Log.v("SavedApplicantShop", "Adding Saved Applicant Shop: ${newItem.displayName.text}")
    if (!applicantShopSaved(context,newItem.id)) {
        Log.v("SavedApplicantShop", " Not Found")
        currentList.add(newItem)
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
//        for (place in currentList) {
//            if (place.etaLabel == null) {
//                place.etaLabel = ""
//            }
//            val index = currentList.indexOfFirst { it.id == place.id }
//            if (index == -1) {
//                currentList.add(place)
//            }
//        }
        val gson = Gson()
        val json = gson.toJson(currentList)
        editor.putString("savedApplicantShops", json)
        editor.apply()
    }
    Utility.showUnifiedConfirmationDialog(context,"Total Shops: ${currentList.size}")
}

fun updateSavedApplicantShopContents(context: Context, list: List<Place>) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    val json = gson.toJson(list)
    editor.putString("savedApplicantShops", json)
    editor.apply()
    Utility.showUnifiedConfirmationDialog(context,"Total Shops: ${list.size}")
}

fun removeSavedApplicantShop(context: Context, newItem: Place) {
    val currentList = getSavedApplicantShops(context).toMutableList()
    if (applicantShopSaved(context,newItem.id)) {
        Log.v("SavedApplicantShop", " ${currentList.size}")
        currentList.removeIf { s->s.id == newItem.id }
        Log.v("SavedApplicantShop", " ${currentList.size}")
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(currentList)
        editor.putString("savedApplicantShops", json)
        editor.apply()
    }
}
fun removeApplicantShop(context: Context, id: String) {
//    Log.v("TodayVisitation", "Removing Today Visitation: ${facNumToRemove} - ClubCode: ${clubCode}")
    val currentList = getApplicantShops(context).toMutableList()
    val updatedList = currentList.filterNot { it.id == id}
    updatedList.sortedBy { it.order }.forEachIndexed { index, visitation ->
        visitation.order = index + 1
    }
    saveApplicantShops(context, updatedList)
}

fun getApplicantShops(context: Context): List<Place> {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val json = prefs.getString("applicantShops", null)
    return if (json != null) {
        val type = object : TypeToken<List<Place>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

fun getSavedApplicantShops(context: Context): List<Place> {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val json = prefs.getString("savedApplicantShops", null)
    return if (json != null) {
        val type = object : TypeToken<List<Place>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}


fun clearSavedApplicantShops(context: Context) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val gson = Gson()
    editor.putString("saveApplicantShops", null)
    editor.apply()
}

fun applicantShopExists(context: Context,id: String): Boolean {
    val currentList = getApplicantShops(context)
    return currentList.any { it.id == id}
}

fun applicantShopSaved(context: Context,id: String): Boolean {
    val currentList = getSavedApplicantShops(context)
    return currentList.any { it.id == id}
}

fun applicantShopVisited(context: Context,id: String): Boolean {
    val currentList = getSavedApplicantShops(context)
    return currentList.any { it.id == id && it.visited }
}

fun updateApplicantShopETA(
    context: Context,
    id: String,
    eta: String
) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val currentList = getApplicantShops(context).toMutableList()

    val index = currentList.indexOfFirst { it.id == id }
    if (index == -1) return

    // Update ONLY the needed field
    currentList[index] = currentList[index].apply {
        etaLabel = eta
    }

    val gson = Gson()
    val json = gson.toJson(currentList)

    prefs.edit()
        .putString("applicantShops", json)
        .apply()
}


fun updateSavedApplicantShopETA(
    context: Context,
    id: String,
    eta: String
) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val currentList = getSavedApplicantShops(context).toMutableList()

    val index = currentList.indexOfFirst { it.id == id }
    if (index == -1) return

    // Update ONLY the needed field
    currentList[index] = currentList[index].apply {
        etaLabel = eta
    }

    val gson = Gson()
    val json = gson.toJson(currentList)

    prefs.edit()
        .putString("savedApplicantShops", json)
        .apply()
}



fun updateSavedApplicantShopNotes(
    context: Context,
    id: String,
    notesStr: String
) {

    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val currentList = getSavedApplicantShops(context).toMutableList()
    val index = currentList.indexOfFirst { it.id == id }
    if (index == -1) return
    // Update ONLY the needed field
    Log.v("UpdateSavedShop","Updating Notes for Shop ID: ${id} with Notes: ${notesStr}")
    currentList[index] = currentList[index].apply {
        notes = notesStr
    }

    val gson = Gson()
    val json = gson.toJson(currentList)

    prefs.edit().putString("savedApplicantShops", json).apply()
}


fun updateSavedApplicantShopVisited(
    context: Context,
    id: String
) {

    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val currentList = getSavedApplicantShops(context).toMutableList()
    val index = currentList.indexOfFirst { it.id == id }
    if (index == -1) return

    currentList[index] = currentList[index].apply {
        visited = !visited
    }

    val gson = Gson()
    val json = gson.toJson(currentList)

    prefs.edit()
        .putString("savedApplicantShops", json)
        .apply()
}


fun normalizeJson(jsonObj: JsonObject): JsonObject {

    for ((key, defaultObj) in defaultMap) {

        val value = jsonObj.get(key)

        val array = when {
            value == null || value.isJsonNull -> JsonArray()

            value.isJsonArray -> {
                val cleaned = JsonArray()
                for (item in value.asJsonArray) {
                    if (!item.toString().isNullOrBlank() && item.toString() != "\"\"") {
                        cleaned.add(item)
                    }
                }
                cleaned
            }

            value.isJsonObject -> {
                JsonArray().apply { add(value.asJsonObject) }
            }

            else -> JsonArray()
        }

        // 🔥 IMPORTANT: if empty → inject default (-1)
        if (array.size() == 0) {
            val arr = JsonArray()
            arr.add(Gson().toJsonTree(defaultObj))
            jsonObj.add(key, arr)
        } else {
            jsonObj.add(key, array)
        }
    }

    return jsonObj
}
