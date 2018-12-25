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
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import android.content.ActivityNotFoundException
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
import java.io.File
import java.lang.Exception


/**
 * Created by sheri on 3/7/2018.
 */

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

fun createPDF(isSpecialist : Boolean ){
    val document = Document()

    //output file path
    var outpath = Environment.getExternalStorageDirectory().path+"/test.pdf"

    try {
        PdfWriter.getInstance(document, FileOutputStream(outpath))
        document.open()
        // Left
        document.addTitle("Visitation Details")
        var paragraph = Paragraph("Vistation Details")
        paragraph.alignment = Element.ALIGN_CENTER
        addEmptyLine(paragraph,2)
        document.add(paragraph)


        paragraph = Paragraph("")

        paragraph.add(createTable())

        document.add(paragraph)

        document.close()

    } catch (e: FileNotFoundException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: DocumentException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
}

private fun createTable() : PdfPTable {

    val columnWidths = floatArrayOf(2f, 4f)
    val table = PdfPTable(columnWidths)
    val titleFont = FontFactory.getFont(FontFactory.HELVETICA,10F,BaseColor.BLUE)
    val normalFont = FontFactory.getFont(FontFactory.HELVETICA,8F,BaseColor.BLACK)
    table.setWidthPercentage(100F);
    // Visitation Section
    var c1 = PdfPCell(Paragraph("Visitation", titleFont))
    c1.horizontalAlignment = Element.ALIGN_CENTER
    c1.verticalAlignment = Element.ALIGN_MIDDLE
    c1.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c1);
    var c2 = PdfPCell(Paragraph("Details" , titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2);

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

    var c3 = PdfPCell(Paragraph("Deffeciencies", normalFont));
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
                table.addCell("NA")
            }
        }
    }

    c3 = PdfPCell(Paragraph("Vendor Revenue (past 12 months)", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    c3.rowspan = if (FacilityDataModel.getInstance().tblVendorRevenue.size >5) 5 else FacilityDataModel.getInstance().tblVendorRevenue.size
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

    addDataCell(table,"Facility Representative's Signature (Deficiencies)","",normalFont,false)

    c3 = PdfPCell(Paragraph("Visitation Tracking (past 12 months)", normalFont));
    c3.horizontalAlignment = Element.ALIGN_LEFT
    try {
        c3.rowspan = FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size
    } catch (e:Exception) {
        c3.rowspan=1
    }
    c3.verticalAlignment = Element.ALIGN_MIDDLE
    table.addCell(c3)
    var strDef = ""
    FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> (Date().time - s.DatePerformed.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.apply {
        (0 until size).forEach {
            if (!get(it).performedBy.equals("00")) {
                strDef = "Performed By: " + get(it).performedBy + " - Date Performed: " + get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                strDef += "\nAAR Sign: " + get(it).AARSigns
                strDef += "\nCertificate of Approval: " + get(it).CertificateOfApproval
                strDef += "\nMember Benefits Poster(s): " + get(it).MemberBenefitPoster
                strDef += "\nQuality Control Process: " + get(it).QualityControl
                strDef += "\nStaff Training Process: " + get(it).StaffTraining
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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details" , titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2);
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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details" , titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2);

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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details" , titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2);

    FacilityDataModel.getInstance().tblPersonnel.apply {
        (0 until size).forEach {
            if (!get(it).PersonnelID.equals("-1")) {
                c1 = PdfPCell(Paragraph(TypeTablesModel.getInstance().PersonnelType.filter { s -> s.PersonnelTypeID.toInt() == get(it).PersonnelTypeID }[0].PersonnelTypeName, normalFont))
                c1.horizontalAlignment = Element.ALIGN_CENTER
                c1.verticalAlignment = Element.ALIGN_MIDDLE
                c1.backgroundColor = BaseColor.LIGHT_GRAY
                table.addCell(c1);
                c2 = PdfPCell(Paragraph(get(it).FirstName + " " + get(it).LastName, normalFont));
                c2.horizontalAlignment = Element.ALIGN_CENTER
                c2.verticalAlignment = Element.ALIGN_MIDDLE
                c2.backgroundColor = BaseColor.LIGHT_GRAY
                table.addCell(c2);
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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details", titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2)

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
        try {
            c3.rowspan = FacilityDataModel.getInstance().tblAARPortalTracking.filter { s -> (Date().time - s.PortalInspectionDate.toDateDBFormat().time) / (24 * 60 * 60 * 1000) < 365 }.size
        } catch (e: Exception) {
            c3.rowspan = 1
        }
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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details", titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2)

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
    table.addCell(c1);
    c2 = PdfPCell(Paragraph("Details", titleFont));
    c2.horizontalAlignment = Element.ALIGN_CENTER
    c2.verticalAlignment = Element.ALIGN_MIDDLE
    c2.backgroundColor = BaseColor.LIGHT_GRAY
    table.addCell(c2)

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

private fun addEmptyLine(paragraph: Paragraph, number: Int) {
    for (i in 0 until number) {
        paragraph.add(Paragraph(" "))
    }
}

fun verifyStoragePermissions(activity: FragmentActivity) {
    // Check if we have write permission
    if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            // Show an expanation to the user *asynchronously* -- don't block
//            // this thread waiting for the user's response! After the user
//            // sees the explanation, try again to request the permission.
//
//        } else {
//            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100)
//        }


    } else {
        createPDF(true)
    }
}