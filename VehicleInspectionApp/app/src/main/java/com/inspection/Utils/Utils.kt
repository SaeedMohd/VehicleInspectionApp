package com.inspection.Utils

import android.app.Activity
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
private val apiSubmitFormat = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")

fun String.toDate(): Date = dbFormat.parse(this)
fun String.toTime(): Long = this.toDate().time

fun String.appToDBFormat(): String = apiFormat.format(appFormat.parse(this))


fun String.apiToAppFormat(): String {
    return appFormat.format(apiFormat.parse(this.split("T")[0]))
}

fun String.appToApiFormat(): String = apiFormat.format(appFormat.parse(this))

fun String.appToApiSubmitFormat(): String = apiSubmitFormat.format(appFormat.parse(this))

fun Date.toAppFormat(): String = appFormat.format(this)

fun Date.toApiFormat(): String = apiFormat.format(this)

fun Date.toApiSubmitFormat(): String = apiSubmitFormat.format(this)

fun Date.toDBFormat(): String = dbFormat.format(this)

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

