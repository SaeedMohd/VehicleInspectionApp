package com.inspection.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Debug
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
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
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator

import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.net.URI
import java.nio.file.Paths


/**
 * Created by sheri on 3/7/2018.
 */

val MaintitleFont = FontFactory.getFont(FontFactory.HELVETICA,12F,BaseColor.BLUE)
val titleFont = FontFactory.getFont(FontFactory.HELVETICA,10F,BaseColor.BLUE)
val normalFont = FontFactory.getFont(FontFactory.HELVETICA,8F,BaseColor.BLACK)
val normalFont7 = FontFactory.getFont(FontFactory.HELVETICA,7F,BaseColor.BLACK)

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

fun createPDF(isSpecialist : Boolean,activity : MainActivity ) {
    val document = Document()

    //output file path
    val file = File(Environment.getExternalStorageDirectory().path + "/VisitationDetails.pdf")
    var writer = PdfWriter.getInstance(document, FileOutputStream(file))
    val event = HeaderFooterPageEvent()
    writer.pageEvent = event
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
    paragraph.add(drawVisitaionSection())
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

    paragraph = Paragraph("AAR Portal", MaintitleFont)
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

//    paragraph.add(createTable())

//    document.add(paragraph)

    document.close()

}

private fun drawVisitaionSection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Type of Inspection: " + FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString(),1,false));
    table.addCell(addCell("Month Due: "+"",1,false));
    table.addCell(addCell("Changes Made: "+"",1,false));
    table.addCell(addCell("Date of Visitation: "+ FacilityDataModel.getInstance().tblVisitationTracking[0].DatePerformed.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Facility Representative's Name:",1,false));
    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName,1,false));
    table.addCell(addCell("Automotive Specialist:",1,false));
    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName,1,false));
    table.addCell(addCell("Facility Representative's Signature:",1,false));
    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature.toString(),1,false));
    table.addCell(addCell("Specialist's Signature:",1,false));
    table.addCell(addCell(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature.toString(),1,false));
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

private fun drawAARTrackingSection() : PdfPTable {
    val table = PdfPTable(7)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Portal Inspection Date", 1,true))
    table.addCell(addCellWithBorder("Logged Into Portal", 1,true))
    table.addCell(addCellWithBorder("# Unacknowledged Tows", 1,true))
    table.addCell(addCellWithBorder("In Progress Tows", 1,true))
    table.addCell(addCellWithBorder("In Progress Walk Ins", 1,true))
    table.addCell(addCell("", 1,true))
    table.addCell(addCell("", 1,true))
    FacilityDataModel.getInstance().tblAARPortalTracking.sortedWith(compareBy { it.PortalInspectionDate }).apply {
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

private fun drawVisitationTrackingSection() : PdfPTable {
    val table = PdfPTable(8)
    table.setWidthPercentage(100f)
    table.addCell(addCellWithBorder("Date Performed", 1,true))
    table.addCell(addCellWithBorder("Performed By", 1,true))
    table.addCell(addCellWithBorder("Date Received", 1,true))
    table.addCell(addCellWithBorder("Date Entered", 1,true))
    table.addCell(addCellWithBorder("Entered By", 1,true))
    table.addCell(addCell("", 1,true))
    table.addCell(addCell("", 1,true))
    table.addCell(addCell("", 1,true))
    FacilityDataModel.getInstance().tblVisitationTracking.sortedWith(compareByDescending { it.DatePerformed}).apply {
        (0 until size).forEach {
            if (!get(it).performedBy.equals("00")) {
                table.addCell(addCellWithBorder(if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder(get(it).performedBy,1,true))
                table.addCell(addCellWithBorder("",1,false));
                table.addCell(addCellWithBorder("",1,false));
                table.addCell(addCellWithBorder("",1,true))
                table.addCell(addCell("",1,true))
                table.addCell(addCell("",1,true))
                table.addCell(addCell("",1,true))
            }
        }
    }
    return table
}


private fun drawFacilitySection() : PdfPTable {
    val table = PdfPTable(4)
    table.setWidthPercentage(100f)
    table.addCell(addCell("Contract Number: " ,1,true));
    table.addCell(addCell("Contract Type: "+FacilityDataModel.getInstance().tblContractType[0].ContractTypeName,1,false));
    table.addCell(addCell("Office: "+FacilityDataModel.getInstance().tblOfficeType[0].OfficeName,1,false));
    table.addCell(addCell("Assigned To: "+ FacilityDataModel.getInstance().tblFacilities[0].AssignedTo,1,false));
    table.addCell(addCell("DBA: "+FacilityDataModel.getInstance().tblFacilities[0].BusinessName ,1,false));
    table.addCell(addCell("Entity Name: "+FacilityDataModel.getInstance().tblFacilities[0].EntityName,1,false));
    table.addCell(addCell("Business Type: "+TypeTablesModel.getInstance().BusinessType.filter { s->s.BusTypeID.equals(FacilityDataModel.getInstance().tblFacilities[0].BusTypeID.toString())}[0].BusTypeName,1,false));
    table.addCell(addCell("Time Zone: "+ FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName,1,false));
    table.addCell(addCell("Website URL: "+ FacilityDataModel.getInstance().tblFacilities[0].WebSite,1,false));
    table.addCell(addCell("Wi-Fi Available: "+ FacilityDataModel.getInstance().tblFacilities[0].InternetAccess,1,false));
    table.addCell(addCell("Tax ID: "+ FacilityDataModel.getInstance().tblFacilities[0].TaxIDNumber,1,false));
    table.addCell(addCell("Repair Order Count: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount,1,false));
    table.addCell(addCell("Annual Inspection Month: "+ FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth.monthNoToName(),1,false));
    table.addCell(addCell("Inspection Cycle: "+ FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle,1,false));
    table.addCell(addCell("Service Availability: "+ if (TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}.size > 0) TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s -> s.SrvAvaID==FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability}[0].SrvAvaName else "Undetermined",1,false));
    table.addCell(addCell("Facility Type: "+ FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName,1,false));
    table.addCell(addCell("ARD Number: "+ FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairNumber,1,false));
    table.addCell(addCell("ARD Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Provider Type: "+ FacilityDataModel.getInstance().tblFacilityServiceProvider[0].SrvProviderId,1,false));
    table.addCell(addCell("Shop Management System: ",1,false));
    table.addCell(addCell("Current Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractCurrentDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Initial Contract Date: "+ FacilityDataModel.getInstance().tblFacilities[0].ContractInitialDate.apiToAppFormatMMDDYYYY(),1,false));
    table.addCell(addCell("Billing Month: "+ FacilityDataModel.getInstance().tblFacilities[0].BillingMonth.monthNoToName(),1,false));
    table.addCell(addCell("Billing Amount: $"+ "%.3f".format(FacilityDataModel.getInstance().tblFacilities[0].BillingAmount.toFloat()),1,false));
    table.addCell(addCell("Insurance Expiration Date: "+ if (FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else FacilityDataModel.getInstance().tblFacilities[0].InsuranceExpDate.apiToAppFormatMMDDYYYY(),2,false));
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

private fun drawPersonnelSection () : PdfPTable {
    val table = PdfPTable(13)
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
    table.addCell(addCellWithBorder("Primary Mail Recepient", 1,true))
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
                table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(),1,true));
                table.addCell(addCellWithBorder(if (get(it).ContractSigner) "Yes" else "No",1,true))
                table.addCell(addCellWithBorder(if (get(it).PrimaryMailRecipient) "Yes" else "No",1,true))
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
    FacilityDataModel.getInstance().tblPersonnelCertification.sortedWith(compareBy { it.CertificationTypeId }).apply {
        (0 until size).forEach {
            if (!get(it).CertificationTypeId.isNullOrEmpty()) {
                if (FacilityDataModel.getInstance().tblPersonnel.filter { s->s.PersonnelID.equals(get(it).PersonnelID) }.isNotEmpty()) {
                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }[0].FirstName, 1,true))
                    table.addCell(addCellWithBorder(FacilityDataModel.getInstance().tblPersonnel.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) }[0].LastName, 1,true))
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A1") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A2") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A3") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A4") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A5") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A6") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A7") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A8") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("A9") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("C1") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                    if (FacilityDataModel.getInstance().tblPersonnelCertification.filter { s -> s.PersonnelID.equals(get(it).PersonnelID) && s.CertificationTypeId.equals("L1") }.isNotEmpty()) {
                        table.addCell(addCellWithBorder(if (get(it).ExpirationDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ExpirationDate.apiToAppFormatMMDDYYYY(), 1,true));
                    } else {
                        table.addCell(addCellWithBorder("", 1,true))
                    }
                }

            }
        }
    }
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

fun addCell(strValue : String, colSpan : Int,alignCenter: Boolean) : PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont));
    cell.colspan=colSpan
    cell.setBorder(Rectangle.NO_BORDER);
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
    return cell
}

fun addCellWithBorder(strValue : String, colSpan : Int,alignCenter : Boolean ) : PdfPCell {
    val cell = PdfPCell(Paragraph(strValue, normalFont7));
    cell.colspan=colSpan
    if (alignCenter) cell.horizontalAlignment = Element.ALIGN_CENTER
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
                strDef += "\nInspection Date: " + get(it).VisitationDate.apiToAppFormatMMDDYYYY() +" - Due Date: " + get(it).ClearedDate.apiToAppFormatMMDDYYYY()
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
                    strAddress += "\nLattitude: " + get(it).LATITUDE
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
                addDataCell(table, "Seniorty Date", if (get(it).SeniorityDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).SeniorityDate.apiToAppFormatMMDDYYYY(), normalFont,false)
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
                addDataCell(table, "Primary Mail Recepient", if (get(it).PrimaryMailRecipient) "Yes" else "No", normalFont,false)

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

//    table.addCell(Paragraph(title, font))
//    table.addCell(Paragraph(data, font))
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
        ColumnText.showTextAligned(writer!!.directContent, Element.ALIGN_LEFT, Phrase("Date Printed: "+Date().toAppFormatMMDDYYYY(),ffont),35f, 20f, 0f)
    }

}

