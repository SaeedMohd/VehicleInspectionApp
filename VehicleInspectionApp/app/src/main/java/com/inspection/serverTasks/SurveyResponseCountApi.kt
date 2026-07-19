package com.inspection.serverTasks

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.inspection.Utils.Constants
import org.json.JSONObject
import java.net.URLEncoder

data class ResponseCountRequest(
    val aaaSurvey: Int = 1,       // 1 = AAA family, 0 = AARWeb family (ignored in grouped mode)
    val parentAccount: String,    // strAAAParentAccount, e.g. "aaaphone"
    val year: Int,
    val quarter: Int? = null,
    val month: Int? = null,
    val startMonth: Int? = null,  // set both start/end for custom range (scores null in this mode)
    val endMonth: Int? = null,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val clubCode: String? = null,
    val facNum: String? = null,   // Corresponds to SP @Facility (JetHome misnames this `intAccountId`)
    val email: String? = null
)

data class ResponseCountResult(
    val surveyType: String,       // "AAA" or "AARWeb"
    val totalResponses: Int,
    val top2Score: Double?        // Q1 Satisfied % (Top 2 Box) — null if no matching rows
)

data class GroupedResponseCountResult(
    val groups: List<ResponseCountResult>,   // One entry per survey type (AAA, AARWeb)
    val totalResponses: Int,                 // Grand sum across groups
    val top2Score: Double?                   // Response-weighted grand mean
)

object SurveyResponseCountApi {
    private const val TAG = "SurveyResponseCount"

    // Server work is heavy: `AAAGetloginLocations` for a director scope (~4K
    // facilities) + inline aggregate queries against ivrans/ivrData. The
    // default Volley timeout (2.5s) will always trip. Match the 60s / 1-retry
    // policy used by /getAllFacilities in FragmentForms.
    private const val TIMEOUT_MS = 60_000
    private const val MAX_RETRIES = 1

    private fun retryPolicy() = DefaultRetryPolicy(
        TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )

    fun fetch(
        queue: RequestQueue,
        req: ResponseCountRequest,
        onSuccess: (ResponseCountResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = buildUrl(req)
        Log.d(TAG, "GET $url")

        val http = StringRequest(Request.Method.GET, url,
            { body ->
                try {
                    val root = JSONObject(body)
                    onSuccess(parseResult(root))
                } catch (e: Exception) {
                    Log.e(TAG, "parse failed: ${e.message}", e)
                    onError(e)
                }
            },
            { err ->
                Log.e(TAG, "error status=${err.networkResponse?.statusCode} ${err.message}", err)
                onError(err)
            }
        )
        http.retryPolicy = retryPolicy()
        queue.add(http)
    }

    private fun parseResult(root: JSONObject): ResponseCountResult {
        val surveyType = root.optString("surveyType", "AAA")
        val total = root.optInt("totalResponses", 0)
        val top2 = optNullableDouble(root, "top2Score")
        return ResponseCountResult(surveyType, total, top2)
    }

    /** Fires `/getResponseCount?groupBy=surveyType&…` and parses the grouped
     *  response: `{groups:[{surveyType,totalResponses,top2Score}, …],
     *  totalResponses:<grand>, top2Score:<weighted>}`. Callers ignore the
     *  request's `aaaSurvey` field — grouped mode always returns both
     *  families. */
    fun fetchGrouped(
        queue: RequestQueue,
        req: ResponseCountRequest,
        onSuccess: (GroupedResponseCountResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val url = buildUrl(req) + "&groupBy=surveyType"
        Log.d(TAG, "GET $url")

        val http = StringRequest(Request.Method.GET, url,
            { body ->
                try {
                    val root = JSONObject(body)
                    val arr = root.optJSONArray("groups") ?: org.json.JSONArray()
                    val groups = ArrayList<ResponseCountResult>(arr.length())
                    for (i in 0 until arr.length()) {
                        groups.add(parseResult(arr.getJSONObject(i)))
                    }
                    val total = root.optInt("totalResponses", 0)
                    val top2 = optNullableDouble(root, "top2Score")
                    onSuccess(GroupedResponseCountResult(groups, total, top2))
                } catch (e: Exception) {
                    Log.e(TAG, "grouped parse failed: ${e.message}", e)
                    onError(e)
                }
            },
            { err ->
                Log.e(TAG, "grouped error status=${err.networkResponse?.statusCode} ${err.message}", err)
                onError(err)
            }
        )
        http.retryPolicy = retryPolicy()
        queue.add(http)
    }

    private fun optNullableDouble(root: JSONObject, key: String): Double? =
        if (root.has(key) && !root.isNull(key)) root.optDouble(key) else null

    private fun buildUrl(r: ResponseCountRequest): String {
        val q = StringBuilder(Constants.getResponseCount)
        fun add(name: String, value: Any?) {
            if (value == null) return
            if (q.last() != '?') q.append('&')
            q.append(name).append('=').append(URLEncoder.encode(value.toString(), "UTF-8"))
        }

        // Scope — mirrors JetHome.aspx.cs branch at lines 1033/1050.
        if (!r.clubCode.isNullOrEmpty() && r.clubCode != "-99") {
            add("intClubCode", r.clubCode)
            add("facNum", r.facNum)
        } else {
            add("email", r.email)
        }
        add("strAAAParentAccount", r.parentAccount)
        add("AAASurvey", r.aaaSurvey)

        // Custom range wins if both start/end are set (PrintCSI path).
        if (r.startMonth != null && r.endMonth != null) {
            add("startMonth", r.startMonth)
            add("endMonth", r.endMonth)
            add("startYear", r.startYear ?: r.year)
            add("endYear", r.endYear ?: r.year)
        } else {
            add("month", r.month)
            add("qtr", r.quarter)
            add("year", r.year)
        }
        return q.toString()
    }
}
