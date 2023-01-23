package com.inspection.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Debug
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.ActivityCompat
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.itextpdf.text.*
import android.content.ActivityNotFoundException
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.transition.Transition
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.get
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.R
import com.inspection.adapter.MultipartRequest
import com.inspection.model.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.PdfName.TEXT
import com.itextpdf.text.pdf.draw.LineSeparator
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import org.jetbrains.anko.doAsync
import org.w3c.dom.Text
import java.awt.font.TextAttribute.FONT
import java.io.*

import java.lang.Exception
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.file.Paths
import javax.sql.DataSource
import com.itextpdf.text.pdf.PdfAnnotation

import com.itextpdf.text.pdf.PdfAction

import com.itextpdf.text.pdf.PdfWriter

import com.itextpdf.text.pdf.PdfContentByte

import com.itextpdf.text.pdf.PdfPCell

import com.itextpdf.text.pdf.PdfPCellEvent





/**
 * Created by sheri on 3/7/2018.
 */




val MaintitleFont = FontFactory.getFont(FontFactory.HELVETICA,12F,BaseColor.BLUE)
val SubtitleFont = FontFactory.getFont(FontFactory.HELVETICA,10F,BaseColor.DARK_GRAY)
val titleFont = FontFactory.getFont(FontFactory.HELVETICA,10F,BaseColor.BLUE)
val normalFont = FontFactory.getFont(FontFactory.HELVETICA,8F,BaseColor.BLACK)
val normalFontMissing = FontFactory.getFont(FontFactory.HELVETICA,8F,BaseColor.RED)

val normalFont7 = FontFactory.getFont(FontFactory.HELVETICA,7F,BaseColor.BLACK)
val normalFont7L = FontFactory.getFont(FontFactory.HELVETICA,7F,BaseColor.BLUE)
val symbolsFont = FontFactory.getFont(FontFactory.ZAPFDINGBATS,8F,BaseColor.BLACK)
private val apiFormat = SimpleDateFormat("yyyy-MM-dd")
private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private val appFormat = SimpleDateFormat("dd MMM yyyy")
private val appFormatMMDDYYYY = SimpleDateFormat("MM/dd/yyyy")
private val apiSubmitFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

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

fun String.apiToAppFormatMMDDYYYYDelimitSpace(): String {
    return if (this.equals("")) "" else appFormatMMDDYYYY.format(apiFormat.parse(this.split(" ")[0]))
}

fun String.appToApiFormat(): String = if (this.equals("")) "" else apiFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormat(): String = apiSubmitFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormatMMDDYYYY(): String = apiSubmitFormat.format(appFormatMMDDYYYY.parse(this))

fun Date.toAppFormat(): String = if (this.equals("")) "" else appFormat.format(this)

fun Date.toAppFormatMMDDYYYY(): String = if (this.equals("")) "" else appFormatMMDDYYYY.format(this)

fun Date.toApiFormat(): String = apiFormat.format(this)

fun Date.toApiSubmitFormat(): String = apiSubmitFormat.format(this)

fun Date.toDBFormat(): String = dbFormat.format(this)

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.hideKeyboard(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(getWindowToken(), 0)
}

fun MarkChangeWasDone(){
    FacilityDataModelOrg.getInstance().changeWasDone = true
    Log.v("Mark Change ---> " , "CALLED *****")
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



fun compareFacilityDataModelTable(type : String){
    var isDifferent = false
    if (type.equals("Personnel")){
        for(i in 0 .. FacilityDataModel.getInstance().tblPersonnel.size){
            if (FacilityDataModel.getInstance().tblPersonnel[i].ContractSigner!=FacilityDataModelOrg.getInstance().tblPersonnel[i].ContractSigner) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].PrimaryMailRecipient!=FacilityDataModelOrg.getInstance().tblPersonnel[i].PrimaryMailRecipient) isDifferent=true
            if (!FacilityDataModel.getInstance().tblPersonnel[i].startDate.equals(FacilityDataModelOrg.getInstance().tblPersonnel[i].startDate)) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].email!=FacilityDataModelOrg.getInstance().tblPersonnel[i].email) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].Phone!=FacilityDataModelOrg.getInstance().tblPersonnel[i].Phone) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].ZIP!=FacilityDataModelOrg.getInstance().tblPersonnel[i].ZIP) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].CITY!=FacilityDataModelOrg.getInstance().tblPersonnel[i].CITY) isDifferent=true
            if (FacilityDataModel.getInstance().tblPersonnel[i].Phone!=FacilityDataModelOrg.getInstance().tblPersonnel[i].Phone) isDifferent=true
            if (isDifferent) break
        }
    }


}

fun Int.monthNoToName(): String {
    var monthName=""
    when (this) {
        0->monthName=""
        1->monthName="January"
        2->monthName="February"
        3->monthName="March"
        4->monthName="April"
        5->monthName="May"
        6->monthName="June"
        7->monthName="July"
        8->monthName="August"
        9->monthName="September"
        10->monthName="October"
        11->monthName="November"
        12->monthName="December"
        else-> monthName=""
    }
    return monthName
}

fun createPDF(activity: Activity){
    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation)
        createPDFForShop(activity)

    var imageView = ImageView(activity.applicationContext)
            .doAsync {
                val imageNameRep = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_RepSignature_"+Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString()+".png"
                val imageNameSpec = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_SpecSignature_"+Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString()+".png"
                val imageNameDef = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_DefSignature_"+Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString()+".png"
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
                        (activity as FormsActivity).imageWaiveSignature?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                        var imageInByte = baos.toByteArray();
                        imageWaiveSignature = Image.getInstance(imageInByte)
                        imageWaiveSignature.scaleToFit(5F, 5F)
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }

                if ((activity as FormsActivity).imageRepSignature != null) {
                    try {
                        var baos = ByteArrayOutputStream();
                        (activity as FormsActivity).imageRepSignature?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                        var imageInByte = baos.toByteArray();
                        imageRepSignature = Image.getInstance(imageInByte)
                        imageRepSignature.scaleToFit(5F, 5F)
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }

                if ((activity as FormsActivity).imageSpecSignature != null) {
                    try {
                        val baos = ByteArrayOutputStream();
                        (activity as FormsActivity).imageSpecSignature?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                        val imageInByte = baos.toByteArray();
                        imageSpecSignature = Image.getInstance(imageInByte)
                        imageSpecSignature.scaleToFit(10F, 10F)
                    } catch (e: Exception) {
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
                        (activity as FormsActivity).imageDefSignature?.compress(Bitmap.CompressFormat.PNG, 70, baos);
                        val imageInByte = baos.toByteArray();
                        imageDefSignature = Image.getInstance(imageInByte)
                        imageDefSignature.scaleToFit(10F, 10F)
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
                createPDFForSpecialist(activity,imageRepSignature,imageSpecSignature,imageDefSignature,imageWaiveSignature)
            }

}


fun createPDFForShop(activity: Activity) {
    val document = Document()
    //output file path
//    val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForShop.pdf")
    var filePath = ""
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForSpecialist.pdf")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForShop.pdf";
    } else {
        filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForShop.pdf";
    }
//    val file = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf")
    val file = File(filePath)
    var writer = PdfWriter.getInstance(document, FileOutputStream(file))
    val event = HeaderFooterPageEvent()
    writer.pageEvent = event
    document.setMargins(20f,20f,20f,30f)
    document.open()

    document.addTitle("AAR Visitation")
    // Headewr Section
    var paragraph = Paragraph("AAR Visitation", MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)

    paragraph = Paragraph("Facility " + FacilityDataModel.getInstance().tblFacilities[0].FACNo + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName, MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)

    // Visitation Section
    paragraph = Paragraph("")
    paragraph.add(drawVisitaionSectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Deficiencies", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawDeficiencySectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Vendor Revenue (past 12 months)", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawVendorRevenueSectionForShop())
    document.add(paragraph)
    addEmptyLine(document, 1)


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
    uploadPDF(activity,file,"Shop")
}

fun createPDFForSpecialist(activity: Activity,imageRep: Image?,imageSpec: Image?,imageDef: Image?,imageWaive: Image?) {
    val document = Document()

    //output file path
    var filePath = ""
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForSpecialist.pdf")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForSpecialist.pdf";
    } else {
        filePath = activity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForSpecialist.pdf";
    }
//    val file = File(Environment.getExternalStorageDirectory().path + "/"+Constants.visitationIDForPDF+"_VisitationDetails_ForSpecialist.pdf")
    val file = File(filePath)
    var writer = PdfWriter.getInstance(document, FileOutputStream(file))
    val event = HeaderFooterPageEvent()
    writer.pageEvent = event
    document.setMargins(10f,10f,20f,30f)
    document.open()


    document.addTitle("AAR Visitation")
    // Headewr Section
    var paragraph = Paragraph("AAR Visitation", MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)

    paragraph = Paragraph("Facility " + FacilityDataModel.getInstance().tblFacilities[0].FACNo + " - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName, MaintitleFont)
    paragraph.alignment = Element.ALIGN_CENTER
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)

    // Visitation Section
    paragraph = Paragraph("")
    paragraph.add(drawVisitaionSection(imageRep,imageSpec,imageWaive))
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

    var vehicleTypeID = ""

    TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeName.equals("Automobile") }.apply {
        (0 until size).forEach {
            vehicleTypeID = get(it).VehiclesTypeID
            paragraph = Paragraph(get(it).VehiclesTypeName, MaintitleFont)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)
            document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
            addEmptyLine(document, 1)
            paragraph = Paragraph("")
            paragraph.add(drawVehicleServicesSection(vehicleTypeID))
            document.add(paragraph)
            addEmptyLine(document, 1)
        }
    }

    paragraph = Paragraph("Vehicles", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)

//    var VehCategoryName = ""


    paragraph = Paragraph(TypeTablesModel.getInstance().VehiclesType.filter { s->s.VehiclesTypeID.toInt()==1 }[0].VehiclesTypeName, SubtitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 95f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
        (0 until size).forEach {
            paragraph = Paragraph(get(it).VehCategoryName, titleFont)
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


    paragraph = Paragraph("Programs", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawProgramsSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Facility Services", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
    paragraph.add(drawFacServicesSection())
    document.add(paragraph)
    addEmptyLine(document, 1)

    paragraph = Paragraph("Affiliations", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")
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


    paragraph = Paragraph("Photos", MaintitleFont)
    paragraph.alignment = Element.ALIGN_LEFT
    document.add(paragraph)
    document.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, 0, -5f))
    addEmptyLine(document, 1)
    paragraph = Paragraph("")

    // Load Faciloty Photos from PRG DB
    val table = PdfPTable(13)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Thumbnail", 2,true))
    table.addCell(addCellWithBorder("File Name", 1,true))
    table.addCell(addCellWithBorder("File Description", 2,true))
    table.addCell(addCellWithBorder("Approval Requested", 1,true))
    table.addCell(addCellWithBorder("Approved", 1,true))
    table.addCell(addCellWithBorder("Approved By", 1,true))
    table.addCell(addCellWithBorder("Approved Date", 1,true))
//    table.addCell(addCellWithBorder("Updated By", 1,true))
//    table.addCell(addCellWithBorder("Updated Date", 1,true))
    table.addCell(addCellWithBorder("Downstream Apps", 2,true))
    table.addCell(addCellWithBorder("Image URL", 2,true))
//    var tblFacilityPhotos = ArrayList<PRGFacilityPhotos>()
//    Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getFacilityPhotos + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}",
//            Response.Listener { response ->
//                activity!!.runOnUiThread {
//                    tblFacilityPhotos = Gson().fromJson(response.toString(), Array<PRGFacilityPhotos>::class.java).toCollection(ArrayList())
                    if (PRGDataModel.getInstance().tblPRGFacilitiesPhotos.size==0) {
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
                        uploadPDF(activity, file, "Specialist")
                    } else {
                        var imageView = ImageView(activity.applicationContext)
                                .doAsync {
                                    PRGDataModel.getInstance().tblPRGFacilitiesPhotos.apply {
                                        (0 until size).forEach {
                                            if (get(it).photoid > -1) {
                                                try {
                                                    val bmp = Glide.with(activity)
                                                            .asBitmap()
                                                            .load(Constants.getImages + get(it).filename)
                                                            .apply(RequestOptions().dontTransform())
                                                            .submit()
                                                            .get()

                                                    val baos = ByteArrayOutputStream();
                                                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                                    val imageInByte = baos.toByteArray();
                                                    var image = Image.getInstance(imageInByte)
                                                    image.scaleToFit(30F,30F)
                                                    table.addCell(addImageWithBorder(image, 2, true))
                                                } catch (e: Exception){
                                                    table.addCell(addCellWithBorder("", 2, true))
                                                }
                                                table.addCell(addCellWithBorder(get(it).filename, 1, true))
                                                table.addCell(addCellWithBorder(get(it).filedescription, 2, true))
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
                                                table.addCell(addCellWithBorder(if (get(it).approveddate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).approveddate.apiToAppFormatMMDDYYYY(), 1, true))
//                                                table.addCell(addCellWithBorder(get(it).lastupdateby, 1, true))
//                                                table.addCell(addCellWithBorder(if (get(it).lastupdatedate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).lastupdatedate.apiToAppFormatMMDDYYYY(), 1, true))
                                                table.addCell(addCellWithBorder(get(it).downstreamapps, 2, true))
                                                table.addCell(addHyperLinkWithBorder(Constants.getImagesWithDomain + get(it).filename, 2, true))

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
                                                    uploadPDF(activity, file, "Specialist")
                                                }
                                            }
                                        }
                                    }
                                }
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

fun drawCommentsSection() :PdfPTable {
    val commentsTable = PdfPTable(1)
    commentsTable.setWidthPercentage(100f)
    val cell = PdfPCell(Paragraph((PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments), normalFont));
    cell.colspan=1
    cell.setBorder(Rectangle.NO_BORDER);
    commentsTable.addCell(cell)
//    commentsTable.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments, 1,false))
    return commentsTable
}

fun uploadPDF(activity: Activity,file: File,type: String) {
    var email = ApplicationPrefs.getInstance(activity).loggedInUserEmail
    if (type.equals("Shop")) {
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf && PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto.isNotEmpty()){
            email = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto
        }
    }
    var facNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo
    var visitationType = PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype
    var waived = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) 'Y' else 'N'
    var waivedComments = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments else ""
//    var sendPDF = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf) 'Y' else 'N'
    var emailPDF = if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf) "1" else "0"
    var busName = URLEncoder.encode(FacilityDataModel.getInstance().tblFacilities[0].BusinessName , "UTF-8");
    var directorEmail = if (!PRGDataModel.getInstance().tblPRGFacilityDirectors.isNullOrEmpty()) PRGDataModel.getInstance().tblPRGFacilityDirectors[0].directoremail else ""
    val multipartRequest = MultipartRequest(Constants.uploadFile+email+"&emailPDF=${emailPDF}&director=${directorEmail}&waived=${waived}&type=${type}&specialistEmail="+ApplicationPrefs.getInstance(activity).loggedInUserEmail+"&facName=${busName}&facNo=${facNo}&visitationType=${visitationType}&waivedComments=${waivedComments}&sessionId="+ApplicationPrefs.getInstance(activity).getSessionID(), null, file, Response.Listener { response ->
//    val multipartRequest = MultipartRequest(Constants.uploadFile+"saeed@pacificresearchgroup.com&type=${type}", null, file, Response.Listener { response ->
        try {
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }, Response.ErrorListener {
    })
    val socketTimeout = 30000//30 seconds
    val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    multipartRequest.retryPolicy = policy
    Volley.newRequestQueue(activity).add(multipartRequest)
}



private fun drawVisitaionSectionForShop() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Facility Representative's Name:",1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep,1,false));
    table.addCell(addCell("Automotive Specialist:",1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist,1,false));
    table.addCell(addCell("Visitation Type: " ,1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype,1,false));
    table.addCell(addCell("Date of Visitation: ",1,false));
    table.addCell(addCell(Date().toAppFormatMMDDYYYY(),1,false));

    table.addCell(addCell("Visitation Reason: " ,1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason,1,false));
    table.addCell(addCell("Visitation Method: ",1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod,1,false));

    table.addCell(addCell("Data Changes Made: "+ if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes" ,4,false));
    return table
}


private fun drawVisitaionSection(imageRep: Image?,imageSpec: Image?,imageWaiver: Image?) : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
//    table.addCell(addCell("Type of Inspection: " + FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),1,false));
    table.addCell(addCell("Type of Inspection: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype,1,false));
    table.addCell(addCell("Month Due: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.toInt().monthNoToName(),1,false));
    table.addCell(addCell("Changes Made: "+if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes" ,1,false))
    table.addCell(addCell("Date of Visitation: "+ Date().toAppFormatMMDDYYYY(),1,false));

    table.addCell(addCell("Visitation Reason: " + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason,2,false));
    table.addCell(addCell("Visitation Method: " + if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) "NA" else PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod,2,false));

    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        table.addCell(addCell("Facility Representative's Name: ",1,false));
        table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep,1,false));
    }
    table.addCell(addCell("Automotive Specialist:",1,false));
    table.addCell(addCell(PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist,1,false));
    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("In Person"))
            table.addCell(addCell("Facility Representative's Signature:", 2, false));
        else
            table.addCell(addCell("", 2, false));
    } else  {
        table.addCell(addCell("",2,false));
        table.addCell(addCell("Waiver Comments:",1,false));
        table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments.toString(),3,false));
    }
//    table.addCell(addCell("Specialist's Signature:",2,false));
    if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation) {
        table.addCell(addCell("Specialist Signature:",2,false));
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
    } else  {
        table.addCell(addCell("Waiver Signature:",2,false));
        table.addCell(addCell("",2,false));
        imageWaiver?.scaleAbsolute(50F,50F)
        val d = PdfPCell(imageWaiver)
        d.colspan = 2
        d.border = Rectangle.NO_BORDER
        d.horizontalAlignment = Element.ALIGN_CENTER
        d.rowspan = 3
        table.addCell(d)
        table.addCell(addCell("",2,false));
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
    return table
}

private fun drawAARHeaderSection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Start Date:",1,true));
    table.addCell(addCell("End Date:",1,true));
    table.addCell(addCell("Addendum Signed Date:",1,true));
    table.addCell(addCell("#of Card Readers:",1,true));
    table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY(),1,true))
    table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY(),1,true))
    table.addCell(addCell(if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY(),1,true))
    table.addCell(addCell(FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders,1,true));
    return table
}


private fun drawAddressOverallSection() : PdfPTable {
    val columnWidths = floatArrayOf(5f, 1f,5f, 1f,10f)
    val table = PdfPTable(columnWidths)
    table.setWidthPercentage(100f)
    table.addCell(addTableInCell(drawPaymentSection(),1,true));
    table.addCell(addCell("",1,true));
    table.addCell(addTableInCell(drawLanguageSection(),1,true));
    table.addCell(addCell("",1,true));
    table.addCell(addTableInCell(drawHoursSection(),1,true));


    return table
}

private fun drawAARTrackingSection() : PdfPTable {
    val table = PdfPTable(7)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("RSP Inspection Date", 1,true))
    table.addCell(addCellWithBorder("Logged Into RSP", 1,true))
    table.addCell(addCellWithBorder("# Unacknowledged Tows", 1,true))
    table.addCell(addCellWithBorder("In Progress Tows", 1,true))
    table.addCell(addCellWithBorder("In Progress Walk Ins", 1,true))
    table.addCell(addCell("", 1,true))
    table.addCell(addCell("", 1,true))
    FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareByDescending { it.PortalInspectionDate }).apply {
        (0 until size).forEach {
            if ( !get(it).TrackingID.equals("-1") ) {
                table.addCell(addCellWithBorder(get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY(),1,true))
                table.addCell(addCellWithBorder(if (get(it).LoggedIntoPortal.equals("true")) "Yes" else "No",1,true))
                table.addCell(addCellWithBorder(get(it).NumberUnacknowledgedTows,1,true))
                table.addCell(addCellWithBorder(get(it).InProgressTows,1,true))
                table.addCell(addCellWithBorder(get(it).InProgressWalkIns,1,true))
                table.addCell(addCell("",1,true))
                table.addCell(addCell("",1,true))
            }
        }
    }
    return table

}

private fun drawProgramsSection() : PdfPTable {
    val table = PdfPTable(6)
    table.setWidthPercentage(80f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Program Name", 2,false))
    table.addCell(addCellWithBorder("Effective Date", 1,true))
    table.addCell(addCellWithBorder("Expiration Date", 1,true))
    table.addCell(addCellWithBorder("Comments", 2,false))
    FacilityDataModel.getInstance().tblPrograms.apply {
        (0 until size).forEach {
            if ( !get(it).ProgramID.equals("-1") ) {
                if (TypeTablesModel.getInstance().ProgramsType.filter { s->s.ProgramTypeID.equals(get(it).ProgramTypeID)}.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ProgramsType.filter { s -> s.ProgramTypeID.equals(get(it).ProgramTypeID) }[0].ProgramTypeName, 2, false))
                    table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(get(it).Comments, 2, false))
                }
            }
        }
    }
    return table
}

private fun drawComplaintsSection() : PdfPTable {
    val table = PdfPTable(9)
    table.setWidthPercentage(100f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Complaint ID", 2,false))
    table.addCell(addCellWithBorder("First Name", 1,true))
    table.addCell(addCellWithBorder("Last Name", 1,true))
    table.addCell(addCellWithBorder("Received Date", 1,false))
    table.addCell(addCellWithBorder("Complaint Reason", 2,false))
    table.addCell(addCellWithBorder("Complaint Resolution", 2,false))
    FacilityDataModel.getInstance().tblComplaintFiles.apply {
        (0 until size).forEach {
            if (!get(it).ComplaintID.isNullOrEmpty()) {
                table.addCell(addCellWithBorder(get(it).ComplaintID, 2, false))
                table.addCell(addCellWithBorder(get(it).FirstName, 1, false))
                table.addCell(addCellWithBorder(get(it).LastName, 1, false))
                table.addCell(addCellWithBorder(if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY(), 1, true))
                table.addCell(addCellWithBorder(get(it).ComplaintReasonName, 2, false))
                table.addCell(addCellWithBorder(get(it).ComplaintResolutionName, 2, false))
            }
        }
    }
    return table
}


private fun drawDeficienciesSection() : PdfPTable {
    val table = PdfPTable(9)
    table.setWidthPercentage(100f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCell("", 2,false))
    table.addCell(addCell("Deficient", 1,true))
    table.addCell(addCell("", 2,false))
    table.addCell(addCell("Deficient", 1,true))
    table.addCell(addCell("", 2,false))
    table.addCell(addCell("Deficient", 1,true))
    TypeTablesModel.getInstance().AARDeficiencyType.apply {
        (0 until size).forEach {
            table.addCell(addCell(get(it).DeficiencyName, 2, false))
            if (FacilityDataModel.getInstance().tblDeficiency.filter { s -> s.DefTypeID.equals(get(it).DeficiencyTypeID) }.filter { s -> s.ClearedDate.isNullOrEmpty() }.isNotEmpty()) {
                table.addCell(addCell(" X ", 1, true))
            } else {
                table.addCell(addCell(" ", 1, true))
            }

        }
    }
    if (TypeTablesModel.getInstance().AARDeficiencyType.size % 3 > 0) {
        table.addCell(addCell(" ", (TypeTablesModel.getInstance().AARDeficiencyType.size % 3)*2, false))
    }
    return table
}

private fun drawFacServicesSection() : PdfPTable {
    val table = PdfPTable(6)
    table.setWidthPercentage(80f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Service Name", 2,false))
    table.addCell(addCellWithBorder("Effective Date", 1,true))
    table.addCell(addCellWithBorder("Expiration Date", 1,true))
    table.addCell(addCellWithBorder("Comments", 2,false))
    FacilityDataModel.getInstance().tblFacilityServices.apply {
        (0 until size).forEach {
            if ( !get(it).FacilityServicesID.equals("-1") ) {
                if (TypeTablesModel.getInstance().ServicesType.filter { s->s.ServiceTypeID.equals(get(it).ServiceID)}.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().ServicesType.filter { s -> s.ServiceTypeID.equals(get(it).ServiceID) }[0].ServiceTypeName, 2, false))
                    table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(get(it).Comments, 2, false))
                }
            }
        }
    }
    return table
}

private fun drawAffiliationSection() : PdfPTable {
    val table = PdfPTable(8)
    table.setWidthPercentage(80f)
    table.horizontalAlignment = Element.ALIGN_LEFT
    table.addCell(addCellWithBorder("Affiliation Name", 2,false))
    table.addCell(addCellWithBorder("Affiliation Details", 2,false))
    table.addCell(addCellWithBorder("Effective Date", 1,true))
    table.addCell(addCellWithBorder("Expiration Date", 1,true))
    table.addCell(addCellWithBorder("Comments", 2,false))
    FacilityDataModel.getInstance().tblAffiliations.apply {
        (0 until size).forEach {
            if ( !get(it).AffiliationID.equals("-1") ) {
                if (TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID)}.isNotEmpty()) {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.equals(get(it).AffiliationTypeID) }[0].AffiliationTypeName, 2, false))
                    if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.equals(get(it).AffiliationTypeDetailID) }[0].AffiliationDetailTypeName, 2, false))
                    } else {
                        table.addCell(addCellWithBorder("", 2, false))
                    }
                    table.addCell(addCellWithBorder(if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY(), 1, true))
                    table.addCell(addCellWithBorder(get(it).comment, 2, false))
                }
            }
        }
    }
    return table
}

private fun drawVisitationTrackingSection() : PdfPTable {
    val table = PdfPTable(9)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Date Performed", 1,true))
    table.addCell(addCellWithBorder("Visitation Type", 1,true))
    table.addCell(addCellWithBorder("Deficiency (Yes/No)", 1,true))
    table.addCell(addCellWithBorder("Performed By", 1,true))
    table.addCell(addCellWithBorder("AAR Sign", 1,true))
    table.addCell(addCellWithBorder("Certificate of Approval", 1,true))
    table.addCell(addCellWithBorder("Member Benefits Poster(s)", 1,true))
    table.addCell(addCellWithBorder("Quality Control Process", 1,true))
    table.addCell(addCellWithBorder("Staff Training Process", 1,true))
    var visitationType = ""
    if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals("00")) {
        try {
            if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.isNotEmpty()) {
                FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed }).apply {
                    (0 until size).forEach {
                        if (!get(it).performedBy.equals("00")) {
                            table.addCell(addCellWithBorder(if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY(), 1, true));
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
                            table.addCell(addCellWithBorder(get(it).CertificateOfApproval, 1, false))
                            table.addCell(addCellWithBorder(get(it).MemberBenefitPoster, 1, false))
                            table.addCell(addCellWithBorder(get(it).QualityControl, 1, false))
                            table.addCell(addCellWithBorder(get(it).StaffTraining, 1, false))
                        }
                    }
                }
            }
        } catch (e:Exception){

        }
    }
    return table
}


private fun drawSoSSection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Fixed Labor Rate\n$" + FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate ,1,true));
    table.addCell(addCell("Diagnostic Rate\n$" + FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate,1,true));
    table.addCell(addCell("Labor Rate Matrix Min\n$" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMin ,1,true));
    table.addCell(addCell("Labor Rate Matrix Min\n$" + FacilityDataModel.getInstance().tblScopeofService[0].LaborMax,1,true));
    table.addCell(addCell("# of Bays: "+FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays,1,true));
    table.addCell(addCell("# of Lifts: "+FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts,1,true));
    table.addCell(addCell("Warranty Period: "+if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}.size>0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID)}[0].WarrantyTypeName else "",1,true));
    table.addCell(addCell("",1,true));
    table.addCell(addCell("Discount Percentage: "+FacilityDataModel.getInstance().tblScopeofService[0].DiscountCap + "%",1,true));
    table.addCell(addCell("Max Discount Amount: "+FacilityDataModel.getInstance().tblScopeofService[0].DiscountAmount,1,true));
    table.addCell(addCell("",2,true));
    return table
}



private fun drawFacilitySection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Contract Number: " ,1,false));
    table.addCell(addCell("Contract Type: "+FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,1,false));
    table.addCell(addCell("Office: "+FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,1,false));
    table.addCell(addCell("Assigned To: "+ FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,1,false));
    table.addCell(addCell("DBA: "+FacilityDataModel.getInstance().tblFacilities[0].BusinessName ,1,false));
    table.addCell(addCell("Entity Name: "+FacilityDataModel.getInstance().tblFacilities[0].EntityName,1,false));
    table.addCell(addCell("Business Type: "+TypeTablesModel.getInstance().BusinessType.filter { s->s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())}[0].BusTypeName,1,false));
    table.addCell(addCell("Time Zone: "+ FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,1,false));
    table.addCell(addCell("Website URL: "+ FacilityDataModel.getInstance().tblFacilities[0].WebSite,1,false));
    table.addCell(addCell("Wi-Fi Available: "+ if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess) "Yes" else "No",1,false));
    table.addCell(addCell("Tax ID: "+ FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,1,false));
    table.addCell(addCell("Repair Order Count: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount,1,false));
    table.addCell(addCell("Annual Inspection Month: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),1,false));
    table.addCell(addCell("Inspection Cycle: "+ FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,1,false));
    table.addCell(addCell("Service Availability: "+ if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}[0].SrvAvaName else "Undetermined",1,false));
    table.addCell(addCell("Facility Type: "+ FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,1,false));
    table.addCell(addCell("ARD Number: "+ FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,1,false));
    table.addCell(addCell("ARD Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Provider Type: "+ if (!FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId.equals("-1")) FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId else "",1,false));
    table.addCell(addCell("Shop Management System: ",1,false));
    table.addCell(addCell("Current Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Initial Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Billing Month: "+ FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),1,false));
    table.addCell(addCell("Billing Amount: $"+ "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),1,false));
    table.addCell(addCell("Insurance Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),2,false));
    table.addCell(addCell("",2,false))
    return table
}

private fun drawHoursSection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    val hoursTable = PdfPTable(4)
    hoursTable.setWidthPercentage(100f)
    hoursTable.addCell(addCell("HOURS",1,false))
    hoursTable.addCell(addCell("Open Time",1,false))
    hoursTable.addCell(addCell("Close Time",1,false))
    hoursTable.addCell(addCell("Night Drop:",1,false))
    FacilityDataModel.getInstance().tblHours[0].apply {
        hoursTable.addCell(addCell("Sun",1,false))
        hoursTable.addCell(addCell(if (SunOpen.isNullOrEmpty()) "Closed" else SunOpen,1,false))
        hoursTable.addCell(addCell(if (SunClose.isNullOrEmpty()) "Closed" else SunClose,1,false))
        hoursTable.addCell(addCell(if (NightDrop) "True" else "False",1,false))
        hoursTable.addCell(addCell("Mon",1,false))
        hoursTable.addCell(addCell(if (MonOpen.isNullOrEmpty()) "Closed" else MonOpen,1,false))
        hoursTable.addCell(addCell(if (MonClose.isNullOrEmpty()) "Closed" else MonClose,1,false))
        hoursTable.addCell(addCell(" ",1,false))
        hoursTable.addCell(addCell("Tue",1,false))
        hoursTable.addCell(addCell(if (TueOpen.isNullOrEmpty()) "Closed" else TueOpen,1,false))
        hoursTable.addCell(addCell(if (TueClose.isNullOrEmpty()) "Closed" else TueClose,1,false))
        val cell = PdfPCell(Paragraph(("Nigh Drop Instructions:" + if (NightDropInstr.isNullOrEmpty()) "" else NightDropInstr), normalFont));
        cell.colspan=1
        cell.setBorder(Rectangle.NO_BORDER);
        cell.rowspan = 5
        hoursTable.addCell(cell)
        hoursTable.addCell(addCell("Wed",1,false))
        hoursTable.addCell(addCell(if (WedOpen.isNullOrEmpty()) "Closed" else WedOpen,1,false))
        hoursTable.addCell(addCell(if (WedClose.isNullOrEmpty()) "Closed" else WedClose,1,false))
        hoursTable.addCell(addCell("Thu",1,false))
        hoursTable.addCell(addCell(if (ThuOpen.isNullOrEmpty()) "Closed" else ThuOpen,1,false))
        hoursTable.addCell(addCell(if (ThuClose.isNullOrEmpty()) "Closed" else ThuClose,1,false))
        hoursTable.addCell(addCell("Fri",1,false))
        hoursTable.addCell(addCell(if (FriOpen.isNullOrEmpty()) "Closed" else FriOpen,1,false))
        hoursTable.addCell(addCell(if (FriClose.isNullOrEmpty()) "Closed" else FriClose,1,false))
        hoursTable.addCell(addCell("Sat",1,false))
        hoursTable.addCell(addCell(if (SatOpen.isNullOrEmpty()) "Closed" else SatOpen,1,false))
        hoursTable.addCell(addCell(if (SatClose.isNullOrEmpty()) "Closed" else SatClose,1,false))
    }
    table.addCell(addTableInCell(hoursTable,4,false))
    table.addCell(addCell(" ",4,true))
    table.addCell(addTableInCell(drawEmailSection(),4,false))
    table.addCell(addCell(" ",4,true))
    table.addCell(addTableInCell(drawPhoneSection(),2,false))
    table.addCell(addCell("",2,true))
    return table
}

private fun drawPaymentSection() : PdfPTable {
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Payment Methods", 2,false))
    table.addCell(addCellWithBorder("Accepted", 1,true))
    TypeTablesModel.getInstance().PaymentMethodsType.apply {
        (0 until size).forEach {
            table.addCell(addCellWithBorder(get(it).PmtMethodName, 2,false))
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.equals(get(it).PmtMethodID)}.isNotEmpty()) {
                Log.v("TICK "," TICK")
                table.addCell(addTick(true,true))
            } else {
                table.addCell(addCellWithBorder(" ", 1,true))
            }
        }
    }
    var noOfRowsToBeAdded = TypeTablesModel.getInstance().LanguageType.size - TypeTablesModel.getInstance().PaymentMethodsType.size
    if (FacilityDataModel.getInstance().tblPhone.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
    if (FacilityDataModel.getInstance().tblFacilityEmail.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

    for (i in 1.. noOfRowsToBeAdded){
        table.addCell(addCell(" ", 3,true))
    }
    return table
}

private fun drawPhoneSection() : PdfPTable {
    val table = PdfPTable(2)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Phone Type", 1,false))
    table.addCell(addCellWithBorder("Phone", 1,true))
    FacilityDataModel.getInstance().tblPhone.apply {
        (0 until size).forEach {
            if (!get(it).PhoneID.equals("-1")) {
                try {
                    table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneID.equals(get(it).PhoneTypeID) }[0].LocPhoneName, 1, false))
                    table.addCell(addCellWithBorder(get(it).PhoneNumber, 2, false))
                } catch (e: Exception){

                }
            }
        }
    }
    return table
}

private fun drawEmailSection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Email Type", 1,false))
    table.addCell(addCellWithBorder("Email", 3,true))
    FacilityDataModel.getInstance().tblFacilityEmail.apply {
        (0 until size).forEach {
            if (!get(it).emailID.equals("-1")) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().EmailType.filter { s->s.EmailID.equals(get(it).emailTypeId)}[0].EmailName, 1, false))
                table.addCell(addCellWithBorder(get(it).email, 3, false))
            }
        }
    }
    return table
}

private fun drawLanguageSection() : PdfPTable {
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Language(s)", 2,false))
    table.addCell(addCellWithBorder("Spoken", 1,true))
    TypeTablesModel.getInstance().LanguageType.apply {
        (0 until size).forEach {
            table.addCell(addCellWithBorder(get(it).LangTypeName, 2,false))
            if (FacilityDataModel.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.size>0) {
                table.addCell(addTick(true,true))
            } else {
                table.addCell(addCellWithBorder(" ", 1,true))
            }
        }
    }

    var noOfRowsToBeAdded = 0
    if (FacilityDataModel.getInstance().tblPhone.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblPhone.size - 1
    if (FacilityDataModel.getInstance().tblFacilityEmail.size >1) noOfRowsToBeAdded += FacilityDataModel.getInstance().tblFacilityEmail.size - 1

    for (i in 1.. noOfRowsToBeAdded){
        table.addCell(addCell(" ", 3,true))
    }

    return table
}


private fun drawDeficiencySectionForShop() : PdfPTable {
    val table = PdfPTable(3)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Deficiency", 1,true))
    table.addCell(addCellWithBorder("Inspection Date", 1,true))
    table.addCell(addCellWithBorder("Due Date", 1,true))
    FacilityDataModel.getInstance().tblDeficiency.apply {
        (0 until size).forEach {
            if (!get(it).DefTypeID.equals("-1") && get(it).ClearedDate.isNullOrEmpty()) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.equals(get(it).DefTypeID) }[0].DeficiencyName,1,true))
                table.addCell(addCellWithBorder(if (get(it).VisitationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).VisitationDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder("",1,true));
            }
        }
    }
    if (FacilityDataModel.getInstance().tblDeficiency.size % 3 > 0) {
        table.addCell(addCell(" ", (FacilityDataModel.getInstance().tblDeficiency.size % 3)*2, false))
    }
    return table
}


private fun drawVendorRevenueSectionForShop() : PdfPTable {
    val table = PdfPTable(6)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Revenue ID", 1,true))
    table.addCell(addCellWithBorder("Revenue Source", 1,true))
    table.addCell(addCellWithBorder("Date of Check", 1,true))
    table.addCell(addCellWithBorder("Amount", 1,true))
    table.addCell(addCell("", 2,true))
    if (FacilityDataModel.getInstance().tblVendorRevenue[0].VendorRevenueID>0) {
        if (FacilityDataModel.getInstance().tblVendorRevenue.filter { s -> (Date().time - s.DateOfCheck.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.isNotEmpty()) {
            FacilityDataModel.getInstance().tblVendorRevenue.apply {
                (0 until size).forEach {
                    if (get(it).VendorRevenueID > 0) {
                        table.addCell(addCellWithBorder(get(it).VendorRevenueID.toString(), 1, true))
                        table.addCell(addCellWithBorder(get(it).RevenueSourceName, 1, true))
                        table.addCell(addCellWithBorder(if (get(it).DateOfCheck.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DateOfCheck.apiToAppFormatMMDDYYYY(), 1, true));
                        table.addCell(addCellWithBorder("%.3f".format(get(it).Amount.toFloat()), 1, true));
                        table.addCell(addCell("", 2, true))
                    }
                }
            }
        }
    }

    return table
}


private fun drawDataChangedSectionForShop() : PdfPTable {
    val table = PdfPTable(8)
    table.setWidthPercentage(100f)
//    table.addCell(addCellWithBorder("User ID", 1,true))
    table.addCell(addCellWithBorder("Group Name", 1,true))
    table.addCell(addCellWithBorder("Screen Name", 1,true))
//    table.addCell(addCellWithBorder("Section Name", 1,true))
//    table.addCell(addCellWithBorder("Action", 1,true))
    table.addCell(addCellWithBorder("Change Date", 1,true))
    table.addCell(addCellWithBorder("Changes Made", 5,false))
    PRGDataModel.getInstance().tblPRGLogChanges.apply {
        (0 until size).forEach {
            if (get(it).recordid>-1 && !get(it).sectionname.equals("Load Visitation")) {
//                table.addCell(addCellWithBorder(get(it).userid, 1, true))
                table.addCell(addCellWithBorder(get(it).groupname, 1, true))
                table.addCell(addCellWithBorder(get(it).screenname, 1, true));
//                table.addCell(addCellWithBorder(get(it).sectionname, 1, true));
//                table.addCell(addCellWithBorder(if (get(it).action) "ADD" else "EDIT", 1, true));
                table.addCell(addCellWithBorder(get(it).changedate.apiToAppFormatMMDDYYYY(), 1, true));
                table.addCell(addCellWithBorder(get(it).datachanged, 5, false));
            }
        }
    }
    return table
}

private fun drawAddressSection() : PdfPTable {
    val table = PdfPTable(17)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Type", 1,true))
    table.addCell(addCellWithBorder("Address1", 2,false))
    table.addCell(addCellWithBorder("Address2", 1,false))
    table.addCell(addCellWithBorder("City", 1,true))
    table.addCell(addCellWithBorder("State", 1,true))
    table.addCell(addCellWithBorder("ZIP", 1,true))
    table.addCell(addCellWithBorder("ZIP4", 1,true))
    table.addCell(addCellWithBorder("Country", 2,true))
    table.addCell(addCellWithBorder("Branch Name", 2,true))
    table.addCell(addCellWithBorder("Branch #", 1,true))
    table.addCell(addCellWithBorder("LATITUDE", 2,true))
    table.addCell(addCellWithBorder("LONGITUDE", 2,true))
    FacilityDataModel.getInstance().tblAddress.apply {
        (0 until size).forEach {
            if (!get(it).LocationTypeID.isNullOrEmpty()) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().LocationType.filter { s->s.LocTypeID.equals(get(it).LocationTypeID)}[0].LocTypeName, 1,true))
                table.addCell(addCellWithBorder(get(it).FAC_Addr1,2,false))
                table.addCell(addCellWithBorder(get(it).FAC_Addr2,1,false))
                table.addCell(addCellWithBorder(get(it).CITY,1,true))
                table.addCell(addCellWithBorder(get(it).ST,1,true))
                table.addCell(addCellWithBorder(get(it).ZIP,1,true))
                table.addCell(addCellWithBorder(get(it).ZIP4,1,true))
                table.addCell(addCellWithBorder(get(it).County,2,true))
                table.addCell(addCellWithBorder(get(it).BranchName,2,true))
                table.addCell(addCellWithBorder(get(it).BranchNumber,1,true))
                table.addCell(addCellWithBorder(get(it).LATITUDE,2,true))
                table.addCell(addCellWithBorder(get(it).LONGITUDE,2,true))
            }
        }
    }
    return table
}

private fun drawVehiclesSection(vehicleCatID : String) : PdfPTable {
    val columnWidths = floatArrayOf(1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f,1f, 4f)
    val table = PdfPTable(columnWidths)
    val vehicleTypeID=1
    table.widthPercentage = 100f
    TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.apply {
        (0 until size).forEach { vMakeIt ->
            if (FacilityDataModel.getInstance().tblFacVehicles.filter { s -> s.VehicleID == get(vMakeIt).VehicleID }.isNotEmpty()) {
                table.addCell(addTick(false,false))
                table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
            } else {
                table.addCell(addCell(" ", 1, false))
                table.addCell(addCell("  " + get(vMakeIt).MakeName, 1, false))
            }
        }
    }
    if (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8 > 0) {
        table.addCell(addCell(" ", (TypeTablesModel.getInstance().VehicleMakes.filter { s -> s.VehicleTypeID == vehicleTypeID.toInt() && s.VehicleCategoryID == vehicleCatID.toInt() }.size % 8)*2, false))
    }
    return table
}

private fun drawVehicleServicesSection(vehicleTypeID: String) : PdfPTable {
    val columnWidths = floatArrayOf(1f, 4f,1f, 4f,1f, 4f)
    val table = PdfPTable(columnWidths)
    table.widthPercentage = 100f
//    val p = Paragraph("This is a tick box character: ");
//    p.add(Chunk("4", symbolsFont));
//    table.addCell(p)

    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.isNotEmpty()) {
        TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.apply {
            (0 until size).forEach { innerIt ->
                if (FacilityDataModel.getInstance().tblVehicleServices.filter { s -> s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(innerIt).ScopeServiceID.toInt() }.isNotEmpty()) {
                    table.addCell(addTick(false,false))
                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
                } else {
                    table.addCell(addCell(" ",1,false))
                    table.addCell(addCell("  " + get(innerIt).ScopeServiceName,1,false))
                }
            }
        }
    }
//    if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3 > 0) {
//        table.addCell(addCell(" ", (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s -> s.VehiclesTypeID.equals(vehicleTypeID) }.size % 3)*2, false))
//    }
    table.addCell(addCell(" ",6,false))
    return table
}

private fun drawPersonnelSection () : PdfPTable {
    val table = PdfPTable(15)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Personnel Type", 1,true))
    table.addCell(addCellWithBorder("First Name", 1,true))
    table.addCell(addCellWithBorder("Last Name", 1,true))
    table.addCell(addCellWithBorder("Certification #", 2,true))
    table.addCell(addCellWithBorder("RSP User ID", 1,true))
    table.addCell(addCellWithBorder("Email Address", 2,true))
    table.addCell(addCellWithBorder("Seniority Date", 1,true))
    table.addCell(addCellWithBorder("Start Date", 1,true))
    table.addCell(addCellWithBorder("End Date", 1,true))
    table.addCell(addCellWithBorder("Contract Signer", 1,true))
    table.addCell(addCellWithBorder("Primary Mail Recipient", 1,true))
    table.addCell(addCellWithBorder("Report Recipient", 1,true))
    table.addCell(addCellWithBorder("Notification Recipient", 1,true))
    FacilityDataModel.getInstance().tblPersonnel.apply {
        (0 until size).forEach {
            if (get(it).PersonnelID>-1) {
                table.addCell(addCellWithBorder(TypeTablesModel.getInstance().PersonnelType.filter { s->s.PersonnelTypeID.equals(get(it).PersonnelTypeID.toString())}[0].PersonnelTypeName, 1,true))
                table.addCell(addCellWithBorder(get(it).FirstName,1,true))
                table.addCell(addCellWithBorder(get(it).LastName,1,true))
                table.addCell(addCellWithBorder(get(it).CertificationNum,2,true))
                table.addCell(addCellWithBorder(get(it).RSP_UserName,1,true))
                table.addCell(addCellWithBorder(get(it).RSP_Email,2,true))
                table.addCell(addCellWithBorder(if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder(if (get(it).startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).startDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder(if (get(it).endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).endDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder(if (get(it).ContractSigner) "X" else "",1,true))
                table.addCell(addCellWithBorder(if (get(it).PrimaryMailRecipient) "X" else "",1,true))
                table.addCell(addCellWithBorder(if (get(it).ReportRecipient) "X" else "",1,true))
                table.addCell(addCellWithBorder(if (get(it).NotificationRecipient) "X" else "",1,true))
            }
        }
    }
    return table
}

private fun drawSignersSection () : PdfPTable {
    val table = PdfPTable(12)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("First Name", 1,true))
    table.addCell(addCellWithBorder("Last Name", 1,true))
    table.addCell(addCellWithBorder("Address", 1,true))
    table.addCell(addCellWithBorder("Address2", 1,true))
    table.addCell(addCellWithBorder("City", 1,true))
    table.addCell(addCellWithBorder("State", 1,true))
    table.addCell(addCellWithBorder("ZIP", 1,true))
    table.addCell(addCellWithBorder("ZIP4", 1,true))
    table.addCell(addCellWithBorder("Phone", 1,true))
    table.addCell(addCellWithBorder("Email", 1,true))
    table.addCell(addCellWithBorder("Contract Start Date", 1,true))
    table.addCell(addCellWithBorder("Contract End Date", 1,true))
    FacilityDataModel.getInstance().tblPersonnelSigner.apply {
        (0 until size).forEach {
            if (get(it).PersonnelID>-1) {
                table.addCell(addCellWithBorder(get(it).FirstName,1,true))
                table.addCell(addCellWithBorder(if (FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}.isNotEmpty()) FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID==get(it).PersonnelID}[0].LastName else "",1,true))
                table.addCell(addCellWithBorder(get(it).Addr1,1,true))
                table.addCell(addCellWithBorder(get(it).Addr2,1,true))
                table.addCell(addCellWithBorder(get(it).CITY,1,true))
                table.addCell(addCellWithBorder(get(it).ST,1,true))
                table.addCell(addCellWithBorder(get(it).ZIP,1,true))
                table.addCell(addCellWithBorder(get(it).ZIP4,1,true))
                table.addCell(addCellWithBorder(get(it).Phone,1,true))
                table.addCell(addCellWithBorder(get(it).email,1,true))
                table.addCell(addCellWithBorder(if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder("",1,true));
            }
        }
    }
    return table
}

private fun drawCertificationsSection () : PdfPTable {
    val table = PdfPTable(13)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("First Name", 1,true))
    table.addCell(addCellWithBorder("Last Name", 1,true))
    table.addCell(addCellWithBorder("A1", 1,true))
    table.addCell(addCellWithBorder("A2", 1,true))
    table.addCell(addCellWithBorder("A3", 1,true))
    table.addCell(addCellWithBorder("A4", 1,true))
    table.addCell(addCellWithBorder("A5", 1,true))
    table.addCell(addCellWithBorder("A6", 1,true))
    table.addCell(addCellWithBorder("A7", 1,true))
    table.addCell(addCellWithBorder("A8", 1,true))
    table.addCell(addCellWithBorder("A9", 1,true))
    table.addCell(addCellWithBorder("C1", 1,true))
    table.addCell(addCellWithBorder("L1", 1,true))

    var personnelWithCert = ArrayList<Int>()
    FacilityDataModel.getInstance().tblPersonnelCertification.apply {
        (0 until size).forEach {
            if (!personnelWithCert.contains(get(it).PersonnelID)){
                personnelWithCert.add(get(it).PersonnelID)
            }
        }
    }

    personnelWithCert.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }.isNotEmpty()) {
                table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].FirstName, 1, true))
                table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(personnelWithCert[it]) }[0].LastName, 1, true))
                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A1") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }
                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A2") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }
                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A3") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A4") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A5") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A6") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A7") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A8") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("A9") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("C1") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
                } else {
                    table.addCell(addCellWithBorder("", 1, true))
                }

                if (FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }.isNotEmpty()) {
                    val expDate = FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).filter { s -> s.PersonnelID == personnelWithCert[it] }.filter { s -> s.CertificationTypeId.equals("L1") }[0].ExpirationDate
                    table.addCell(addCellWithBorder(if (expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else expDate.apiToAppFormatMMDDYYYY(), 1, true));
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
    return table
}

private fun drawFacilityGISection() : PdfPTable {
    val payMethodTable = PdfPTable(2)
    payMethodTable.setWidthPercentage(100f)

    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    val payMethodCel=PdfPCell()
    return table
}

fun addTitleCell(strValue : String, colSpan : Int,alignCenter: Boolean,font: Font) : PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, font));
    cell.colspan=colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCell(strValue : String, colSpan : Int,alignCenter: Boolean) : PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont));
    cell.colspan=colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addTableInCell(theTable : PdfPTable, colSpan : Int,alignCenter: Boolean) : PdfPCell {
    val cell = PdfPCell(theTable);
    cell.colspan=colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCellWithBorder(strValue : String, colSpan : Int,alignCenter : Boolean ) : PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont7));
    cell.colspan=colSpan
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

internal class LinkInCell(protected var url: String) : PdfPCellEvent {
    override fun cellLayout(cell: PdfPCell?, position: Rectangle?,
                            canvases: Array<PdfContentByte>) {
        val writer = canvases[0].pdfWriter
        val action = PdfAction(url)
        val link = PdfAnnotation.createLink(
                writer, position, PdfAnnotation.HIGHLIGHT_INVERT, action)
        writer.addAnnotation(link)
    }
}

fun addHyperLinkWithBorder(strValue : String, colSpan : Int,alignCenter : Boolean ) : PdfPCell {
    val cell = PdfPCell(Paragraph("Show Image", normalFont7L));
    cell.colspan=colSpan
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.setCellEvent(LinkInCell(
            strValue));
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}


fun addImageWithBorder(image : Image, colSpan : Int,alignCenter : Boolean ) : PdfPCell {
    val cell = PdfPCell(image);
    cell.colspan=colSpan
    cell.setPadding(5F)
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addSignatures(image : Image) : PdfPCell {
    val cell = PdfPCell(image);
    cell.setPadding(5F)
    cell.verticalAlignment = Element.ALIGN_MIDDLE
    cell.horizontalAlignment = Element.ALIGN_CENTER
    cell.setBorder(Rectangle.NO_BORDER);
    return cell
}

fun addTick(alignCenter: Boolean, withBorder: Boolean) : PdfPCell {
//    val tick =  Chunk("4", symbolsFont)
//    tick.font.size = 14.0F
    var p = Paragraph("x ")
    val cell = PdfPCell(p);
    cell.colspan=1
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

private fun createTable() : PdfPTable {
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
    var c2 = PdfPCell(Paragraph("Details" , titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
//    table.addCell(c2);

//    table.setHeaderRows(1)
    // Facility Number
    addDataCell(table,"Facility Number",FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString(),normalFont,false)
    addDataCell(table,"Facility Name",FacilityDataModel.getInstance().tblFacilities[0].BusinessName,normalFont,false)
    addDataCell(table,"Visitation Type",FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),normalFont,false)
    addDataCell(table,"Date of Visitation",FacilityDataModel.getInstance().tblVisitationTracking[0].DatePerformed.apiToAppFormatMMDDYYYY(),normalFont,false)
    if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc) {
        addDataCell(table,"Visitation Reason","",normalFont,false)
    }
    addDataCell(table,"Data Changes Made","",normalFont,false)
    addDataCell(table,"Facility Representative","",normalFont,false)
    addDataCell(table,"Facility Representative Signature","",normalFont,false)
    addDataCell(table,"Automotive Specialist","",normalFont,false)
    addDataCell(table,"Automotive Specialist Signature","",normalFont,false)

    var c3 = PdfPCell(Paragraph("Deficiencies", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan = FacilityDataModel.getInstance().tblDeficiency.size
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    FacilityDataModel.getInstance().tblDeficiency.apply {
        (0 until size).forEach {
            if (!get(it).DefTypeID.equals("-1")) {
                var strDef = "Def Type: " + TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.toString() == get(it).DefTypeID }[0].DeficiencyName
                strDef += "\nInspection Date: " + get(it).VisitationDate.apiToAppFormatMMDDYYYY() +" - Due Date: " + get(it).DueDate.apiToAppFormatMMDDYYYY()
                strDef += "\nComments: " + get(it).Comments
                table.addCell(Paragraph(strDef, normalFont))
            } else {
                table.addCell(Paragraph("NA", normalFont))
            }
        }
    }

    addDataCell(table,"Facility Representative's Signature (Deficiencies)","",normalFont,false)

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
                strDef += "\nDate of Check: " + get(it).DateOfCheck.apiToAppFormatMMDDYYYY() +" - Amount: " + if (get(it).Amount.isNullOrEmpty()) "" else "%.3f".format(get(it).Amount.toFloat())
                table.addCell(Paragraph(strDef, normalFont))
            } else {
                table.addCell("NA")
            }
        }
    }



    c3 = PdfPCell(Paragraph("Visitation Tracking (past 12 months)", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT


//    try {
        c3.rowspan = if (FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size>0) FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strDef = ""
    FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.sortedWith(compareByDescending { it.DatePerformed }).apply {
//    FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed }).apply {
        (0 until size).forEach {
            if (!get(it).performedBy.equals("00")) {
                strDef = "Performed By: " + get(it).performedBy + " - Date Performed: " + get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                strDef += "\nVisitation Type: " + get(it).visitationType
                if (it==0) {
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
    addDataCell(table,"DBA",FacilityDataModel.getInstance().tblFacilities[0].BusinessName,normalFont,false)
    addDataCell(table,"Entity Name",FacilityDataModel.getInstance().tblFacilities[0].EntityName,normalFont,false)
    addDataCell(table,"Business Type",TypeTablesModel.getInstance().BusinessType.filter { s->s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())}[0].BusTypeName,normalFont,false)
    addDataCell(table,"Contract Status",TypeTablesModel.getInstance().FacilityStatusType.filter { s->s.FacilityStatusID.equals(FacilityDataModel.getInstance().tblFacilities[0].ContractTypeID.toString())}[0].FacilityStatusID,normalFont,false)
    addDataCell(table,"Contract Type",FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,normalFont,false)
    addDataCell(table,"Provider Type",FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId,normalFont,false)
    addDataCell(table,"Provider Number",FacilityDataModel.getInstance().tblFacilityServiceProvider[0].ProviderNum,normalFont,false)
    addDataCell(table,"Termination Date",FacilityDataModel.getInstance().tblFacilities[0].TerminationDate.apiToAppFormatMMDDYYYY(),normalFont,false)
    addDataCell(table,"Termination Reason",FacilityDataModel.getInstance().tblTerminationCodeType[0].TerminationCodeName,normalFont,false)
    addDataCell(table,"Termination Comments",FacilityDataModel.getInstance().tblFacilities[0].TerminationComments,normalFont,false)
    addDataCell(table,"Annual Inspection Month",FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),normalFont,false)
    addDataCell(table,"Quarterly Inspection Cycle",FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,normalFont,false)
    addDataCell(table,"Office",FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,normalFont,false)
    addDataCell(table,"Assigned To",FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,normalFont,false)
    addDataCell(table,"Manager",FacilityDataModel.getInstance().tblFacilityManagers[0].Manager,normalFont,false)
    addDataCell(table,"Admin Assistants",FacilityDataModel.getInstance().tblFacilities[0].AdminAssistants,normalFont,false)
    addDataCell(table,"Time Zone",FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,normalFont,false)
    addDataCell(table,"Website URL",FacilityDataModel.getInstance().tblFacilities[0].WebSite,normalFont,false)
    addDataCell(table,"Tax-ID",FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,normalFont,false)

    addDataCell(table,"Repair Order Count",FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount.toString(),normalFont,false)
    addDataCell(table,"Service Availability",if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}[0].SrvAvaName else "",normalFont,false)
    addDataCell(table,"Facility Type",FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,normalFont,false)
    addDataCell(table,"ARD Number",FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,normalFont,false)
    addDataCell(table,"Shop Management System","",normalFont,false)
    addDataCell(table,"Current Contract Date",FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),normalFont,false)
    addDataCell(table,"Initial Contract Date",FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),normalFont,false)
    addDataCell(table,"Billing Month",FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),normalFont,false)
    addDataCell(table,"Billing Amount","%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),normalFont,false)
    addDataCell(table,"Insurance Expiration Date",FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),normalFont,false)

    var paymentMethodsStr = ""
    TypeTablesModel.getInstance().PaymentMethodsType.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblPaymentMethods.filter { s->s.PmtMethodID.equals(get(it).PmtMethodID)}.size>0)
                paymentMethodsStr += get(it).PmtMethodName + ", "
        }
    }

    addDataCell(table,"Payment Methods",paymentMethodsStr.removeSuffix(", "),normalFont,false)

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
                if (TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeID == get(it).LocationTypeID }[0].LocTypeName.equals("Physical")) {
                    strAddress += "\nLatitude: " + get(it).LATITUDE
                    strAddress += "\nLongitude: " + get(it).LONGITUDE
                }
                strAddress += "\nBranch Number: " + get(it).BranchNumber
                strAddress += "\nBranch Name: " + get(it).BranchName
                c2 = PdfPCell(Paragraph(TypeTablesModel.getInstance().LocationType.filter { s -> s.LocTypeID == get(it).LocationTypeID }[0].LocTypeName + " Address" , normalFont));
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
                addDataCell(table,"Phone Type - " + TypeTablesModel.getInstance().LocationPhoneType.filter { s -> s.LocPhoneID == get(it).PhoneTypeID}[0].LocPhoneName,get(it).PhoneNumber,normalFont,false)
            }
        }
    }

    FacilityDataModel.getInstance().tblFacilityEmail.apply {
        (0 until size).forEach {
            if (!get(it).emailID.equals("-1")) {
                addDataCell(table,"Email Type - " + TypeTablesModel.getInstance().EmailType.filter { s -> s.EmailID == get(it).emailTypeId}[0].EmailName,get(it).email,normalFont,false)
            }
        }
    }

    c3 = PdfPCell(Paragraph("Hours of Operation", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan=1
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    table.addCell(Paragraph(""))
    FacilityDataModel.getInstance().tblHours.apply {
        (0 until size).forEach {
            addDataCell(table,"     Sunday",if (get(it).SunOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).SunOpen + "   -   Closed: " + get(it).SunClose,normalFont,false)
            addDataCell(table,"     Monday",if (get(it).MonOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).MonOpen + "   -   Closed: " + get(it).MonClose,normalFont,false)
            addDataCell(table,"     Tuesday",if (get(it).TueOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).TueOpen + "   -   Closed: " + get(it).TueClose,normalFont,false)
            addDataCell(table,"     Wednesday",if (get(it).WedOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).WedOpen + "   -   Closed: " + get(it).WedClose,normalFont,false)
            addDataCell(table,"     Thursday",if (get(it).ThuOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).ThuOpen + "   -   Closed: " + get(it).ThuClose,normalFont,false)
            addDataCell(table,"     Friday",if (get(it).FriOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).FriOpen + "   -   Closed: " + get(it).FriClose,normalFont,false)
            addDataCell(table,"     Saturday",if (get(it).SatOpen.isNullOrEmpty()) "Closed" else "Open: " + get(it).SatOpen + "   -   Closed: " + get(it).SatClose,normalFont,false)
        }
    }


    addDataCell(table,"Night Drop",if (FacilityDataModel.getInstance().tblHours[0].NightDrop) "Available" else "Not Available",normalFont,false)
    addDataCell(table,"Night Drop Instructions",if (FacilityDataModel.getInstance().tblHours[0].NightDropInstr.isNullOrEmpty()) "" else FacilityDataModel.getInstance().tblHours[0].NightDropInstr,normalFont,false)

    var strLanguages = ""
    TypeTablesModel.getInstance().LanguageType.apply {
        (0 until size).forEach {
            if (FacilityDataModel.getInstance().tblLanguage.filter { s->s.LangTypeID.equals(get(it).LangTypeID)}.size>0)
                strLanguages += get(it).LangTypeName+ ", "
        }
    }

    addDataCell(table,"Languages",strLanguages.removeSuffix(", "),normalFont,false)

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
                c1 = PdfPCell(Paragraph(TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeID.toInt() == get(it).PersonnelTypeID }[0].PersonnelTypeName + ":  " + get(it).FirstName + " " + get(it).LastName, normalFont))
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
                addDataCell(table, "Certification Number", get(it).CertificationNum, normalFont,false)
                addDataCell(table, "RSP User ID", get(it).RSP_UserName, normalFont,false)
                addDataCell(table, "RSP Email Address", get(it).RSP_Email, normalFont,false)
                addDataCell(table, "Seniority Date", if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(), normalFont,false)
                addDataCell(table, "Start Date", if (get(it).ContractStartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(), normalFont,false)
                addDataCell(table, "End Date", if (get(it).ContractEndDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ContractStartDate.apiToAppFormatMMDDYYYY(), normalFont,false)
                addDataCell(table, "Contract Signer", if (get(it).ContractSigner) "Yes" else "No", normalFont,false)
                if (get(it).ContractSigner) {
                    if (FacilityDataModel.getInstance().tblPersonnelSigner.filter { S -> S.PersonnelID == get(it).PersonnelID }.size > 0) {
                        FacilityDataModel.getInstance().tblPersonnelSigner.filter { S -> S.PersonnelID == get(it).PersonnelID }.forEach { Signer ->
                            addDataCell(table, "(Contract Signer) Address 1", Signer.Addr1, normalFont,false)
                            addDataCell(table, "(Contract Signer) Address 2", Signer.Addr2, normalFont,false)
                            addDataCell(table, "(Contract Signer) City", Signer.CITY, normalFont,false)
                            addDataCell(table, "(Contract Signer) State", Signer.ST, normalFont,false)
                            addDataCell(table, "(Contract Signer) Zip", Signer.ZIP, normalFont,false)
                            addDataCell(table, "(Contract Signer) Zip 4", Signer.ZIP4, normalFont,false)
                            addDataCell(table, "(Contract Signer) Phone", Signer.Phone, normalFont,false)
                            addDataCell(table, "(Contract Signer) Email Address", Signer.email, normalFont,false)
                            addDataCell(table, "(Contract Signer) Contract Start Date", if (Signer.ContractStartDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else Signer.ContractStartDate.apiToAppFormatMMDDYYYY(), normalFont,false)
                            addDataCell(table, "(Contract Signer) Contract End Date", "", normalFont,false)
                        }
                    }
                }
                addDataCell(table, "Primary Mail Recipient", if (get(it).PrimaryMailRecipient) "Yes" else "No", normalFont,false)

                if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }.count() > 0) {
                    FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }.forEach { item ->
                        addDataCell(table, item.CertificationTypeId + " Certification Date", if (item.CertificationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else item.CertificationDate.apiToAppFormatMMDDYYYY(), normalFont,false)
                        addDataCell(table, item.CertificationTypeId + " Expiration Date", if (item.ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else item.ExpirationDate.apiToAppFormatMMDDYYYY(), normalFont,false)
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
        addDataCell(table, "Start Date", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].startDate.apiToAppFormatMMDDYYYY(), normalFont,false)
        addDataCell(table, "End Date", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].endDate.apiToAppFormatMMDDYYYY(), normalFont,false)
        addDataCell(table, "Addendum Signed Date", if (FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblAARPortalAdmin[0].AddendumSigned.apiToAppFormatMMDDYYYY(), normalFont,false)
        addDataCell(table, "Number of Card Readers", FacilityDataModel.getInstance().tblAARPortalAdmin[0].CardReaders, normalFont,false)
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
            c3.rowspan = if (FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size > 0) FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size else 1
//        } catch (e: Exception) {
//            c3.rowspan = 1
//        }
        c3.verticalAlignment = Element.ALIGN_MIDDLE
        table.addCell(c3)

        FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.apply {
            (0 until size).forEach {
                if (!get(it).TrackingID.equals("-1")) {
                    strTracking = "Inspection Date: " + if (get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).PortalInspectionDate.apiToAppFormatMMDDYYYY()
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
        addDataCell(table, "Fixed Labor Rate",if (FixedLaborRate.isNullOrEmpty()) "0" else FixedLaborRate, normalFont,false)
        addDataCell(table, "Diagnostic Rate",if (DiagnosticsRate.isNullOrEmpty()) "0" else DiagnosticsRate, normalFont,false)
        addDataCell(table, "Labor Rate Matrix Min",if (LaborMin.isNullOrEmpty()) "0" else LaborMin, normalFont,false)
        addDataCell(table, "Labor Rate Matrix Max",if (LaborMax.isNullOrEmpty()) "0" else LaborMax, normalFont,false)
        addDataCell(table, "Number of Bays",if (NumOfBays.isNullOrEmpty()) "0" else NumOfBays, normalFont,false)
        addDataCell(table, "Number of Lifts",if (NumOfLifts.isNullOrEmpty()) "0" else NumOfLifts, normalFont,false)
        addDataCell(table, "Warranty Period",if (TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(WarrantyTypeID)}.size>0) TypeTablesModel.getInstance().WarrantyPeriodType.filter { s->s.WarrantyTypeID.equals(WarrantyTypeID)}[0].WarrantyTypeName else "", normalFont,false)
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
    var vehicleTypeID=""
    TypeTablesModel.getInstance().VehiclesType.apply {
        (0 until size).forEach {
            vehicleTypeID = get(it).VehiclesTypeID
            if (TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.isNotEmpty()) {
                addDataCell(table, get(it).VehiclesTypeName, "", titleFont,false)
                TypeTablesModel.getInstance().ScopeofServiceTypeByVehicleType.filter { s->s.VehiclesTypeID.equals(vehicleTypeID)}.apply {
                    (0 until size).forEach { innerIt ->
                        if (FacilityDataModel.getInstance().tblVehicleServices.filter { s-> s.VehiclesTypeID == vehicleTypeID.toInt() && s.ScopeServiceID == get(innerIt).ScopeServiceID.toInt()}.isNotEmpty()){
                            addDataCell(table, get(innerIt).ScopeServiceName, "X", normalFont,true)
                        } else {
                            addDataCell(table, get(innerIt).ScopeServiceName, "", normalFont,false)
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
    vehicleTypeID="1"
    TypeTablesModel.getInstance().VehiclesMakesCategoryType.apply {
        (0 until size).forEach {
            addDataCell(table,get(it).VehCategoryName,"",titleFont,true)
//            if (TypeTablesModel.getInstance().VehicleMakes.filter { s->s.VehicleTypeID.equals(vehicleTypeID) && s.VehicleCategoryID.equals(get(it).VehCategoryID)}.isNotEmpty()) {
            TypeTablesModel.getInstance().VehicleMakes.filter { s->s.VehicleTypeID==vehicleTypeID.toInt() && s.VehicleCategoryID==get(it).VehCategoryID.toInt()}.apply {
                (0 until size).forEach {vMakeIt ->
                    addDataCell(table, get(vMakeIt).MakeName, if (FacilityDataModel.getInstance().tblFacVehicles.filter { s->s.VehicleID==get(vMakeIt).VehicleID}.isNotEmpty()) "X" else "", normalFont, true)
                }
            }
        }
    }

    c1 = PdfPCell(Paragraph("Programs", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    c1.colspan=2
    table.addCell(c1);

    // Programs
    c3 = PdfPCell(Paragraph("Programs", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
        c3.rowspan = if (FacilityDataModel.getInstance().tblPrograms.size > 0) FacilityDataModel.getInstance().tblPrograms.size else 1
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
                strPrograms += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
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
    c1.colspan=2
    table.addCell(c1);

    // Facility Services
    c3 = PdfPCell(Paragraph("Facility Services", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
        c3.rowspan = if (FacilityDataModel.getInstance().tblFacilityServices.size>0) FacilityDataModel.getInstance().tblFacilityServices.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strFacServices = ""
    FacilityDataModel.getInstance().tblFacilityServices.apply {
        (0 until size).forEach {
            if (!get(it).FacilityServicesID.equals("-1")) {
                strFacServices = "Service Name: " + TypeTablesModel.getInstance().ServicesType.filter { s->s.ServiceTypeID.equals(get(it).ServiceID) }[0].ServiceTypeName
                strFacServices += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
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
    c1.colspan=2
    table.addCell(c1);

    // Affiliations
    c3 = PdfPCell(Paragraph("Affiliations", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
        c3.rowspan = if (FacilityDataModel.getInstance().tblAffiliations.size>0) FacilityDataModel.getInstance().tblAffiliations.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strAffiliation= ""
    FacilityDataModel.getInstance().tblAffiliations.apply {
        (0 until size).forEach {
            if (!(get(it).AffiliationID==-1)) {
                strAffiliation = "Affiliation Name: " + if (get(it).AffiliationTypeID>0) TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AARAffiliationTypeID.toInt()==get(it).AffiliationTypeID}[0].AffiliationTypeName else ""
                strAffiliation += "\nAffiliation Details: " + if (get(it).AffiliationTypeDetailID>0) TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationTypeDetailID.toInt()==get(it).AffiliationTypeDetailID}[0].AffiliationDetailTypeName else ""
                strAffiliation += "\nEffective Date: " + if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY() + " - Expiration Date: " + if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
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
    c1.colspan=2
    table.addCell(c1);

    c3 = PdfPCell(Paragraph("Complaints", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
//    try {
        c3.rowspan = if (FacilityDataModel.getInstance().tblComplaintFiles.size>0) FacilityDataModel.getInstance().tblComplaintFiles.size else 1
//    } catch (e:Exception) {
//        c3.rowspan=1
//    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strComplaints= ""
    FacilityDataModel.getInstance().tblComplaintFiles.apply {
        (0 until size).forEach {
            if (!get(it).ComplaintID.equals("")) {
                strComplaints = "Complaint ID: " + get(it).ComplaintID
                strComplaints += "\nFirst Name: " + get(it).FirstName
                strComplaints += "\nLastName: " + get(it).LastName
                strComplaints += "\nReceived Date: " + if (get(it).ReceivedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ReceivedDate.apiToAppFormatMMDDYYYY()
                strComplaints += "\nComplaints Reason: " + get(it).ComplaintReasonName
                strComplaints += "\nComplaints Resolution: " + get(it).ComplaintResolutionName
                table.addCell(Paragraph(strComplaints, normalFont))
            } else {
                table.addCell(Paragraph("", normalFont))
            }
        }
    }

//    if (strComplaints.equals("")) table.addCell(Paragraph("", normalFont))

    addDataCell(table,"Number of Complaints during previous 12 months",FacilityDataModel.getInstance().NumberofComplaints[0].NumberofComplaintslast12months,normalFont,true)
    addDataCell(table,"Number of Justified Complaints during previous 12 months",FacilityDataModel.getInstance().NumberofJustifiedComplaints[0].NumberofJustifiedComplaintslast12months,normalFont,true)
    addDataCell(table,"Justified Complaints Ratio",FacilityDataModel.getInstance().JustifiedComplaintRatio[0].JustifiedComplaintRatio,normalFont,true)

    return table
}

private fun addDataCell(table: PdfPTable,title: String,data: String,font : Font,alignCenter : Boolean){
    val c1 = PdfPCell(Paragraph(title, font))
    c1.horizontalAlignment = Element.ALIGN_LEFT
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c1);
    val c2 = PdfPCell(Paragraph(data, font));
    c2.horizontalAlignment = if (alignCenter) Element.ALIGN_CENTER else Element.ALIGN_LEFT // CHECK ALIGNMENT
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
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_CENTER, Phrase("Date: "+Date().toAppFormatMMDDYYYY(),ffont),((document!!.right()-document.left()) / 2 + document.leftMargin()),
//               document!!.bottom().minus(10), 0F)
//        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, Phrase("Top Right"), 550f, 820f, 0f)
    }

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_CENTER, Phrase("http://www.xxxx-your_example.com/"), 110f, 30f, 0f)
        ColumnText.showTextAligned(writer?.directContent, Element.ALIGN_CENTER, Phrase("Page " + document!!.pageNumber,ffont), 550f, 20f, 0f)
//        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_LEFT, Phrase("Date Printed: "+Date().toAppFormatMMDDYYYY(),ffont),35f, 20f, 0f)
        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_LEFT, Phrase("Fac No: "+FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + " - Name: "+FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - Visitation ID:"+Constants.visitationIDForPDF,ffont),20f, 20f, 0f)
    }

}

