package com.inspection.Utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sheri on 3/7/2018.
 */

private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
private val appFormat = SimpleDateFormat("dd MMM yyyy")

fun String.toDate(): Date {
    return dbFormat.parse(this)
}

fun String.toTime(): Long {
    return this.toDate().time
}

fun String.dbToAppFormat(): String{
    return appFormat.format(dbFormat.parse(this))
}
