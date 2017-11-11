package com.matics.serverTasks

import android.os.AsyncTask

abstract class AsyncTaskParent : AsyncTask<String, Void, String>() {
    abstract fun onTaskCompleted(result: String)
}
