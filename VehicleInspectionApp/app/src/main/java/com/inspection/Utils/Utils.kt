package com.inspection.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sheri on 3/7/2018.
 */

private val apiFormat = SimpleDateFormat("yyyy-MM-dd")
private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private val appFormat = SimpleDateFormat("dd MMM yyyy")

fun String.toDate(): Date {
    return dbFormat.parse(this)
}

fun String.toTime(): Long {
    return this.toDate().time
}

fun String.appToDBFormat(): String{
    return apiFormat.format(appFormat.parse(this))
}


fun String.apiToAppFormat(): String{
    Log.v("*****apiToAppFormat", appFormat.format(apiFormat.parse(this.split("T")[0])))
    return appFormat.format(apiFormat.parse(this.split("T")[0]))
}

fun String.appToApiFormat(): String{
    return apiFormat.format(appFormat.parse(this))
}

fun Date.toAppFormat(): String{
    return appFormat.format(this)
}

fun Date.toApiFormat(): String{
    return apiFormat.format(this)
}

fun Date.toDBFormat(): String{
    return dbFormat.format(this)
}

fun Context.toast(message: String, length : Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}

