package com.inspection.Utils

import android.app.Activity
import android.content.Context
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
private val apiSubmitFormat = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")

fun String.toDate(): Date = dbFormat.parse(this)
fun String.toTime(): Long = this.toDate().time

fun String.appToDBFormat(): String = apiFormat.format(appFormat.parse(this))


fun String.apiToAppFormat(): String {
    return if (this.equals("")) "" else appFormat.format(apiFormat.parse(this.split("T")[0]))
}

fun String.apiToAppFormatMMDDYYYY(): String {
    return if (this.equals("")) "" else appFormatMMDDYYYY.format(apiFormat.parse(this.split("T")[0]))
}

fun String.appToApiFormat(): String = if (this.equals("")) "" else apiFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormat(): String = apiSubmitFormat.format(appFormat.parse(this))

fun Date.toAppFormat(): String = if (this.equals("")) "" else appFormat.format(this)

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
