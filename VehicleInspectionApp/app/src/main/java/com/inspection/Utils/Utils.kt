package com.inspection.Utils

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

/**
 * Created by sheri on 3/7/2018.
 */

private val apiFormat = SimpleDateFormat("yyyy-MM-dd")
private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private val appFormat = SimpleDateFormat("dd MMM yyyy")
private val appFormatMMDDYYYY = SimpleDateFormat("MM/dd/yyyy")
private val apiSubmitFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

fun String.toDate(): Date = dbFormat.parse(this)
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
