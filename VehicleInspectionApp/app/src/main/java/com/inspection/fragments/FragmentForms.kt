package com.inspection.fragments


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bugfender.sdk.Bugfender

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.getTodayVisitations
import com.inspection.databinding.FragmentFormsBinding
import com.inspection.databinding.FragmentVisitationFormBinding
import com.inspection.model.TodayVisitationModel
//import kotlinx.android.synthetic.main.fragment_forms.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


class FragmentForms : androidx.fragment.app.Fragment(), OnClickListener {

    var formsStringsArray = arrayOf("Visitation Planning", "APP / Ad Hoc Visitation", "My Performance")
    private var _binding: FragmentFormsBinding? = null
    private val binding get() = _binding!!
    //another added code for frag testing > sherif yousry
   // var fragment2: VehiclesFragmentInScopeOfServicesView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as? MainActivity)?.supportActionBar?.title = "ACE AAR Inspection"
        return inflater.inflate(R.layout.fragment_forms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFormsBinding.bind(view)
        var list: List<TodayVisitationModel> = getTodayVisitations(requireContext())
        binding.todayVisitationTitle.text = "Today's Visitations (${list.size})"

        binding.programsOverviewCard.root.setOnClickListener {
            val fm = fragmentManager ?: return@setOnClickListener
            val ctx = context ?: return@setOnClickListener
            if (ApplicationPrefs.getInstance(ctx).isDirector()) {
                (activity as? MainActivity)?.supportActionBar?.title = "Director Overview"
                fm.beginTransaction()
                    .replace(R.id.fragment, DirectorOverviewFragment.newInstance())
                    .addToBackStack("frag")
                    .commit()
            } else {
                (activity as? MainActivity)?.supportActionBar?.title = "Programs Overview Details"
                fm.beginTransaction()
                    .replace(R.id.fragment, ProgramsDetailsFragment())
                    .addToBackStack("frag")
                    .commit()
            }
        }

        loadProgramsOverviewSummary()
        loadSurveyResponsesSummary()

        binding.visitationPlanningButton.setOnClickListener {
            val act = activity ?: return@setOnClickListener
            val service = act.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            val enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true
            FirebaseCrashlytics.getInstance().log("User Selected Visitation Planning Screen")
            if (!enabled) {
                val alertBuilder = AlertDialog.Builder(act)
                val inflater = LayoutInflater.from(act)
                val dialogView = inflater.inflate(R.layout.decision_dialog, null)
                alertBuilder.setView(dialogView)
                val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
                val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
                val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
                val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
                dialogTitle.setText("GPS Location is required")
                dialogMessage.setText("GPS location is required within this app. If you disagree the app will be closed")
                btnPositiveAction.setText("Agree")
                btnNegativeAction.setText("Disagree")
                val dialog = alertBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(false)
                btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss()
                })
                btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                    act.finish()
                })

                dialog.show()
            } else {
                Bugfender.i("Screen", "Visitation Planning")
                (act as? MainActivity)?.supportActionBar?.title = "Visitation Planning"
                val fragment = VisitationPlanningFragment()
                fragment.isVisitationPlanning = true
                val fm = fragmentManager ?: return@setOnClickListener
                fm.beginTransaction().replace(R.id.fragment, fragment).addToBackStack("frag").commit()
            }
        }

        binding.adHocVisitationButton.setOnClickListener {
            val act = activity ?: return@setOnClickListener
            Bugfender.i("Screen", "Ad Hoc Visitation")
            val service = act.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            val enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true
            FirebaseCrashlytics.getInstance().log("User Selected AdHoc Visitations Screen")
            if (!enabled) {
                val alertBuilder = AlertDialog.Builder(act)
                val inflater = LayoutInflater.from(act)
                val dialogView = inflater.inflate(R.layout.decision_dialog, null)
                alertBuilder.setView(dialogView)
                val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
                val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
                val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
                val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
                dialogTitle.setText("GPS Location is required")
                dialogMessage.setText("GPS location is required within this app. If you disagree the app will be closed")
                btnPositiveAction.setText("Agree")
                btnNegativeAction.setText("Disagree")
                val dialog = alertBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss()
                })
                btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                    act.finish()
                })

                dialog.show()
            } else {
                (act as? MainActivity)?.supportActionBar?.title = "APP / Ad Hoc Visitation"
                val fragment = AppAdHockVisitationFilterFragment()
                fragment.isVisitationPlanning = false
                val fm = fragmentManager ?: return@setOnClickListener
                fm.beginTransaction().replace(R.id.fragment, fragment).addToBackStack("frag").commit()
            }
        }

        binding.pinnedVisitationButton.setOnClickListener {
            val act = activity ?: return@setOnClickListener
            Bugfender.i("Screen", "Pinned")
            val service = act.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            val enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true
            FirebaseCrashlytics.getInstance().log("User Selected Today's Visitations Screen")
            if (!enabled) {
                val alertBuilder = AlertDialog.Builder(act)
                val inflater = LayoutInflater.from(act)
                val dialogView = inflater.inflate(R.layout.decision_dialog, null)
                alertBuilder.setView(dialogView)
                val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
                val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
                val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
                val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
                dialogTitle.setText("GPS Location is required")
                dialogMessage.setText("GPS location is required within this app. If you disagree the app will be closed")
                btnPositiveAction.setText("Agree")
                btnNegativeAction.setText("Disagree")
                val dialog = alertBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss()
                })
                btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                    act.finish()
                })

                dialog.show()
            } else {
                (act as? MainActivity)?.supportActionBar?.title = "Today's Visitations"
                val fragment = TodayVisitationFragment()
                val fm = fragmentManager ?: return@setOnClickListener
                fm.beginTransaction().replace(R.id.fragment, fragment).addToBackStack("frag").commit()
            }
        }

        binding.completedVisitationsButton.setOnClickListener {
            val act = activity ?: return@setOnClickListener
            Bugfender.i("Screen", "Pinned")
            val service = act.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            val enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true
            FirebaseCrashlytics.getInstance().log("User Selected Today's Visitations Screen")
            if (!enabled) {
                val alertBuilder = AlertDialog.Builder(act)
                val inflater = LayoutInflater.from(act)
                val dialogView = inflater.inflate(R.layout.decision_dialog, null)
                alertBuilder.setView(dialogView)
                val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
                val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
                val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
                val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
                dialogTitle.setText("GPS Location is required")
                dialogMessage.setText("GPS location is required within this app. If you disagree the app will be closed")
                btnPositiveAction.setText("Agree")
                btnNegativeAction.setText("Disagree")
                val dialog = alertBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss()
                })
                btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                    act.finish()
                })

                dialog.show()
            } else {
                (act as? MainActivity)?.supportActionBar?.title = "Completed Visitations"
                val fragment = CompletedVisitationsFragment()
                val fm = fragmentManager ?: return@setOnClickListener
                fm.beginTransaction().replace(R.id.fragment, fragment).addToBackStack("frag").commit()
            }
        }

        binding.myPerformanceButton.setOnClickListener {
//            val client = OkHttpClient()//.newBuilder().connectTimeout(50, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS)
//            val request = Request.Builder()
//                    .url("https://api-uat.national.aaa.com/common/oauth2/token?client_id=5d5f4i99gmj45pf5qpcnhuvr07&client_secret=1ifminse1q98jifo5qauk9207r01q2a9gvvku074bot5v560mdjb")
//                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
////                    .addHeader("Authorization", "Bearer eyJraWQiOiJGMld5M2tKT3BDdDlBa1o2cWdiR1JuVGtIWlM4YldpanhTRkJJWnh1elh3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1ZDVmNGk5OWdtajQ1cGY1cXBjbmh1dnIwNyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoicmVzLWF1dG9tb3RpdmUtdWF0XC9yc3AtcHJveHkiLCJhdXRoX3RpbWUiOjE2ODc4MTIzMTMsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX084dVRUSXQxaCIsImV4cCI6MTY4NzgxNTkxMywiaWF0IjoxNjg3ODEyMzEzLCJ2ZXJzaW9uIjoyLCJqdGkiOiJkNjM4NzJmMi04MDZhLTRjZjItYjRlZS04ODJmZGRhYzdkMjYiLCJjbGllbnRfaWQiOiI1ZDVmNGk5OWdtajQ1cGY1cXBjbmh1dnIwNyJ9.dKlBvu-RP-NGIPE2xljnN30A7IUA1QlSkxuGPN9BfDXin2PjKZ2TZrVP50DMa6Qr1Ze9ysQwjntaM8i8TMQaskA6Ai0347oddbYgRAfOdkJVvnTpPe72aCuCmAfkudWC-1m8sty6ZUYcYTyh1rxFE2lj5xIUcojxlnMxp3MnA557gEb7Nhg_OdhK4Mk8ySnexdbIaV2Sza0KeFlx91Be2nBYrmkxFwVoXdxjTzHmKo43V-7-uZGr0EE7hm2aYL10VnJGv3avTBxseCWtjWmLarm-cJtTmFdo6xCiNBLdoC9MXqE4UBrhQkfw0-ENjnJZImlMLGhMjwfN51l--GoIWQ")
////                    .addHeader("Cookie", "incap_ses_188_2617556=C6hDZrXul1EMN84seumbAsD4mWQAAAAAY6lz3NR0oS55nCN9yowEGg==; nlbi_2617556_2600297=A8xdU7mGPBquGfyudOi6ugAAAABHABE40aQKY1pavAFmF4Da; nlbi_2617556_2795788=pOd8MYLDDBSKyW+ndOi6ugAAAADF94XM3G007nF3A24IYozk; visid_incap_2400341=L40Sp9/SRKWOyleCzvTfvXJFgmMAAAAAQUIPAAAAAAAO96pQC0Hi7+uSzubCW8Wl; visid_incap_2617556=SbLkk389QqSn5OD9+J8UkuE7dmQAAAAAQUIPAAAAAABn4f3tlNKK0dj6wKeEg2eG; XSRF-TOKEN=fa307f3b-24bf-4bfb-a823-2be0c6e43ea0")
//                    .build()
//            val response = client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.v("TOKEN --> ",e.toString())
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    Log.v("TOKEN --> ",response.toString())
//                }
//            })
            (activity as? MainActivity)?.supportActionBar?.title = "My Performance"
//            var fragment = PDFGenerateFragment()
//            val fragmentManagerSC = fragmentManager
//            val ftSC = fragmentManagerSC!!.beginTransaction()
//            ftSC.replace(R.id.fragment, fragment)
//            ftSC.addToBackStack("frag")
//            ftSC.commit()
        }

        binding.applicantButton.setOnClickListener {
            var intent = Intent(context, com.inspection.ApplicantActivity::class.java)
            startActivity(intent)
        }

        //button added for fragments testing only > sherif yousry
//        fragmentTester.setOnClickListener {
//            fragment2 = VehiclesFragmentInScopeOfServicesView()
//          //  fragment!!.isVisitationPlanning = false
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment2)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
////                (activity as? MainActivity)?.supportActionBar?.title = formsStringsArray[i].toString()
//        }

//        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, formsStringsArray)
//        formsListView.adapter = arrayAdapter
//
//        formsListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
//            if (i == 0){
////                val fragment: android.support.v4.app.Fragment
////                fragment = FragmentAnnualVisitationPager()
////                val fragmentManagerSC = fragmentManager
////                val ftSC = fragmentManagerSC!!.beginTransaction()
////                ftSC.replace(R.id.fragment,fragment)
////                ftSC.addToBackStack("")
////                ftSC.commit()
////                (activity as? MainActivity)?.supportActionBar?.title = formsStringsArray[i].toString()
//
//                fragment = VisitationPlanningFragment()
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
//                (activity as? MainActivity)?.supportActionBar?.title = formsStringsArray[i].toString()
//            }
//        })

    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub

    }

    // -------------------------------------------------------------------------
    // Programs Overview Summary — live data on the home card.
    //
    // Filter is auto-derived from the logged-in user:
    //   - clubcode = lowest clubcode of the facilities assigned to the user
    //                (same rule VisitationPlanningFragment uses for defaultClubCode)
    //   - specialistEmail = ApplicationPrefs.loggedInUserEmail
    //
    // Hits the same pre-computed cache endpoints as ProgramsDetailsFragment:
    //   /getRspByLogin         → RSP row 1
    //   /getAaaByLogin         → AAA row 2
    // Row 3 (CSI) stays on the static placeholder.
    // -------------------------------------------------------------------------

    private var formsRequestQueue: com.android.volley.RequestQueue? = null

    // Survey Responses card — cached last results so the By-Type view can
    // compute an "Others" bucket = un-grouped Total − platform sum, giving
    // an exact grand-total match with the Total pill. Nulled on reload.
    private var lastUngroupedResponses: com.inspection.serverTasks.ResponseCountResult? = null
    private var lastGroupedResponses: com.inspection.serverTasks.GroupedResponseCountResult? = null

    private fun queue(): com.android.volley.RequestQueue? {
        val existing = formsRequestQueue
        if (existing != null) return existing
        val ctx = context?.applicationContext ?: return null
        val q = com.android.volley.toolbox.Volley.newRequestQueue(ctx)
        formsRequestQueue = q
        return q
    }

    private fun loadProgramsOverviewSummary() {
        val ctx = context ?: return
        val specEmail = com.inspection.Utils.ApplicationPrefs.getInstance(ctx).loggedInUserEmail.orEmpty()
        val userID = com.inspection.Utils.ApplicationPrefs.getInstance(ctx).loggedInUserID.orEmpty()
        Log.d("FragmentForms", "Programs Overview Summary load: userID='$userID' specEmail='$specEmail'")
        if (specEmail.isEmpty()) {
            Log.d("FragmentForms", "Skipped — missing login email")
            return
        }
        if (ApplicationPrefs.getInstance(ctx).isDirector()) {
            loadDirectorOverviewSummary(specEmail)
            return
        }
        // Paint the subtitle immediately with what we know — the specialist
        // name comes from session, the club shows "…" until facilities resolve.
        // That way the user always sees the filter line instead of the static
        // placeholder, even if the facility fetch is in flight.
        showFilterSubtitle("…", specEmail)
        showRowLoading(true)

        val cached = com.inspection.model.CSIFacilitySingelton.getInstance().csiFacilities
        Log.d("FragmentForms", "Facility cache size = ${cached?.size ?: 0}")
        if (cached != null && cached.isNotEmpty()) {
            val club = resolveDefaultClubCode(cached, userID, specEmail)
            Log.d("FragmentForms", "Resolved defaultClubCode = '$club'")
            if (club.isNotEmpty()) {
                fetchSummaryFor(club, specEmail)
            } else {
                showFilterSubtitle("?", specEmail)
                showRowLoading(false)
            }
        } else {
            Log.d("FragmentForms", "Facility cache empty, fetching /getAllFacilities")
            val req = com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.GET, Constants.getAllFacilities,
                { response ->
                    if (!isAdded || _binding == null) return@StringRequest
                    try {
                        val arr = com.google.gson.Gson().fromJson(
                            response.toString(),
                            Array<com.inspection.model.CsiFacility>::class.java
                        ).toCollection(ArrayList())
                        com.inspection.model.CSIFacilitySingelton.getInstance().csiFacilities = arr
                        val club = resolveDefaultClubCode(arr, userID, specEmail)
                        Log.d("FragmentForms", "Fetched ${arr.size} facilities, defaultClub='$club'")
                        if (club.isNotEmpty()) {
                            fetchSummaryFor(club, specEmail)
                        } else {
                            showFilterSubtitle("?", specEmail)
                            showRowLoading(false)
                        }
                    } catch (e: Exception) {
                        Log.e("FragmentForms", "getAllFacilities parse failed: ${e.message}", e)
                        showRowLoading(false)
                    }
                },
                { error ->
                    Log.e("FragmentForms", "getAllFacilities error: ${error.message}", error)
                    showRowLoading(false)
                }
            )
            // /getAllFacilities response is ~1.6 MB across ~8200 rows;
            // the default 2.5s Volley timeout is too aggressive on slower
            // connections. Match the bulk login endpoints' 60s/1-retry.
            req.retryPolicy = com.android.volley.DefaultRetryPolicy(
                60_000, 1, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            queue()?.add(req)
        }
    }

    /** Survey Responses card — fires TWO parallel calls and re-renders
     *  whenever either completes:
     *    1. Un-grouped /getResponseCount → Total pill (JetHome-tile parity;
     *       base 22-Period SP pipeline, verified 229,790 / 92.43% for Justin
     *       AAA 2026). Also used as the reference total for the By-Type
     *       "Others" bucket + grand row.
     *    2. /getResponseCount?groupBy=surveyType → By-Type pill (7 JetHome-
     *       SP-native platform rows, ivrans + ivrData). Whatever's counted
     *       here is subtracted from the un-grouped Total to derive an
     *       "Others" row, so By-Type grand exactly matches Total pill. */
    private fun loadSurveyResponsesSummary() {
        val ctx = context ?: return
        val binding = _binding ?: return
        val specEmail = com.inspection.Utils.ApplicationPrefs.getInstance(ctx).loggedInUserEmail.orEmpty()
        val card = binding.surveyResponsesCard
        wireSurveyResponsesToggle(card)
        // Reset cached results — this is a fresh load.
        lastUngroupedResponses = null
        lastGroupedResponses = null
        if (specEmail.isEmpty()) {
            card.surveyResponsesSubtitle.text = "Login required"
            return
        }
        val isDirector = ApplicationPrefs.getInstance(ctx).isDirector()
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        card.surveyResponsesSubtitle.text =
            if (isDirector) "Director scope · $year" else "Specialist · $year"
        card.surveyResponsesLoading.visibility = View.VISIBLE

        val q = queue() ?: return
        val req = com.inspection.serverTasks.ResponseCountRequest(
            parentAccount = "aaaphone",
            year = year,
            email = specEmail
        )

        // Fetch 1 — un-grouped (JetHome-tile parity for Total pill)
        com.inspection.serverTasks.SurveyResponseCountApi.fetch(
            queue = q,
            req = req.copy(aaaSurvey = 1),   // AAA family — matches aaaphone tile
            onSuccess = { r ->
                if (!isAdded || _binding == null) return@fetch
                lastUngroupedResponses = r
                val c = _binding!!.surveyResponsesCard
                c.surveyResponsesTotal.text = formatCount(r.totalResponses)
                c.surveyResponsesTop2.text = formatPercent(r.top2Score)
                tryRenderByTypeView()
                Log.d("FragmentForms", "SurveyResponses total: ${r.totalResponses} / ${r.top2Score}")
            },
            onError = { err ->
                if (!isAdded || _binding == null) return@fetch
                val c = _binding!!.surveyResponsesCard
                c.surveyResponsesTotal.text = "—"
                c.surveyResponsesTop2.text = "—"
                Log.e("FragmentForms", "SurveyResponses total failed: ${err.message}", err)
            }
        )

        // Fetch 2 — grouped (7-platform By-Type view)
        com.inspection.serverTasks.SurveyResponseCountApi.fetchGrouped(
            queue = q,
            req = req,
            onSuccess = { grouped ->
                if (!isAdded || _binding == null) return@fetchGrouped
                lastGroupedResponses = grouped
                tryRenderByTypeView()
                _binding!!.surveyResponsesCard.surveyResponsesLoading.visibility = View.GONE
                Log.d("FragmentForms", "SurveyResponses grouped: total=${grouped.totalResponses} " +
                    "top2=${grouped.top2Score} groups=${grouped.groups.size}")
            },
            onError = { err ->
                if (!isAdded || _binding == null) return@fetchGrouped
                _binding!!.surveyResponsesCard.surveyResponsesLoading.visibility = View.GONE
                Log.e("FragmentForms", "SurveyResponses grouped failed: ${err.message}", err)
            }
        )
    }

    /** Re-renders the By-Type view using whatever's currently cached. Fires
     *  after every successful fetch (either endpoint) so the view stays
     *  consistent regardless of arrival order. */
    private fun tryRenderByTypeView() {
        val binding = _binding ?: return
        val grouped = lastGroupedResponses ?: return   // no platform data yet
        renderByTypeView(binding.surveyResponsesCard, grouped, lastUngroupedResponses)
    }

    /** One-time hookup for the segmented Total / By-Type toggle. Default is
     *  Total. Idempotent — safe to call on every reload. */
    private fun wireSurveyResponsesToggle(
        card: com.inspection.databinding.IncludeSurveyResponsesCardBinding
    ) {
        val toggle = card.surveyResponsesToggle
        // Default = Total; only set if the group has no active selection yet
        // to avoid clobbering a user's mid-session choice on card refresh.
        if (toggle.checkedButtonId == View.NO_ID) {
            toggle.check(card.surveyToggleTotal.id)
        }
        toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val showTotal = checkedId == card.surveyToggleTotal.id
            card.surveyResponsesTotalView.visibility = if (showTotal) View.VISIBLE else View.GONE
            card.surveyResponsesByTypeView.visibility = if (showTotal) View.GONE else View.VISIBLE
        }
    }

    /** Renders the By-Type pill. Backend returns the SP-native 7 platforms.
     *  We drop zeros and sort by response desc. When the un-grouped Total is
     *  also available, we inject an "Others" row = Total − sum(platforms) so
     *  the By-Type grand exactly matches the Total pill (JetHome itself has
     *  this gap — CarFax/CarWise/Kukui/etc. responses the SP doesn't count).
     *  Grand row shows the un-grouped totals when available; otherwise falls
     *  back to the platform-pipeline sum. */
    private fun renderByTypeView(
        card: com.inspection.databinding.IncludeSurveyResponsesCardBinding,
        grouped: com.inspection.serverTasks.GroupedResponseCountResult,
        ungrouped: com.inspection.serverTasks.ResponseCountResult?
    ) {
        val ctx = card.root.context
        val inflater = LayoutInflater.from(ctx)
        val container = card.surveyByTypeRows
        container.removeAllViews()

        val platforms = grouped.groups
            .filter { it.totalResponses > 0 }
            .sortedByDescending { it.totalResponses }

        // "Others" row = un-grouped Total − sum of platform rows. Only
        // added when we have an un-grouped reference and a positive delta.
        val platformSum = platforms.sumOf { it.totalResponses }
        val othersCount = ungrouped?.totalResponses?.minus(platformSum)?.coerceAtLeast(0) ?: 0

        for (g in platforms) {
            val row = inflater.inflate(R.layout.item_survey_type_row, container, false)
            row.findViewById<TextView>(R.id.surveyTypeName).text = g.surveyType
            row.findViewById<TextView>(R.id.surveyTypeCount).text = formatCount(g.totalResponses)
            row.findViewById<TextView>(R.id.surveyTypeScore).text = formatPercent(g.top2Score)
            container.addView(row)
        }
        if (othersCount > 0) {
            val row = inflater.inflate(R.layout.item_survey_type_row, container, false)
            row.findViewById<TextView>(R.id.surveyTypeName).text = "Others"
            row.findViewById<TextView>(R.id.surveyTypeCount).text = formatCount(othersCount)
            // Back-solve Others.top2 from the weighted-mean identity:
            //   Total.top2 × Total.responses
            //     = Σ platform.top2 × platform.responses + Others.top2 × Others.responses
            // Approximate — the un-grouped total comes from a different pipeline
            // (base 22-Period SP) than the platforms (ivrans/ivrData), so the
            // identity is only exact if the two pipelines score identically.
            // In practice it's close enough for a UI hint. Clamp to [0, 100]
            // to hide any small numeric drift.
            val othersTop2 = computeOthersTop2(ungrouped, platforms, othersCount)
            row.findViewById<TextView>(R.id.surveyTypeScore).text = formatPercent(othersTop2)
            container.addView(row)
        }

        // Grand row = un-grouped totals when available (matches Total pill),
        // else falls back to the platform-pipeline totals from the backend.
        val grandTotal = ungrouped?.totalResponses ?: grouped.totalResponses
        val grandTop2 = ungrouped?.top2Score ?: grouped.top2Score
        card.surveyRowGrandTotal.text = formatCount(grandTotal)
        card.surveyRowGrandTop2.text = formatPercent(grandTop2)
    }

    private fun formatCount(n: Int): String =
        String.format(java.util.Locale.US, "%,d", n)

    private fun formatPercent(v: Double?): String =
        v?.let { String.format(java.util.Locale.US, "%.2f%%", it) } ?: "—"

    /** Back-solves the "Others" bucket's Top-2 % from the un-grouped total
     *  and the visible platform contributions (weighted-mean identity).
     *  Returns null when we can't compute — no ungrouped ref, no ungrouped
     *  top2, no others responses, or all platforms are null-scored. */
    private fun computeOthersTop2(
        ungrouped: com.inspection.serverTasks.ResponseCountResult?,
        platforms: List<com.inspection.serverTasks.ResponseCountResult>,
        othersCount: Int
    ): Double? {
        if (othersCount <= 0) return null
        val ref = ungrouped ?: return null
        val refTop2 = ref.top2Score ?: return null
        val totalContribution = refTop2 * ref.totalResponses
        val platformContribution = platforms.sumOf { (it.top2Score ?: 0.0) * it.totalResponses }
        val othersContribution = totalContribution - platformContribution
        val raw = othersContribution / othersCount
        // Clamp to [0, 100] to hide pipeline-mismatch drift at the edges.
        return raw.coerceIn(0.0, 100.0)
    }

    /** Director scope source: hits /getDirectorOverview and feeds the
     *  same applyRowOne/applyRowTwo plumbing using the response `summary`
     *  block. The subtitle reads "Director scope" rather than the
     *  specialist/club pair, since the card aggregates across the
     *  director's full scope. */
    private fun loadDirectorOverviewSummary(directorEmail: String) {
        val sv = _binding?.programsOverviewCard
        sv?.programsOverviewSubtitle?.text = "Director scope"
        showRowLoading(true)
        val url = Constants.getDirectorOverview +
            java.net.URLEncoder.encode(directorEmail, "UTF-8")
        Log.d("DirectorFlow", "HomeCard: GET $url")
        val req = com.android.volley.toolbox.StringRequest(
            com.android.volley.Request.Method.GET, url,
            { response ->
                if (!isAdded || _binding == null) return@StringRequest
                try {
                    val obj = org.json.JSONObject(response ?: "{}")
                    val summary = obj.optJSONObject("summary") ?: org.json.JSONObject()
                    val total = summary.optInt("totalFacilities", 0)
                    val rsp = summary.optInt("rspAutomated", 0)
                    val rspPct = summary.optInt("rspPct", 0)
                    val aaa = summary.optInt("aaaActive", 0)
                    val aaaPct = summary.optInt("aaaPct", 0)
                    Log.d("DirectorFlow", "HomeCard: ← totalFacilities=$total rsp=$rsp/$rspPct% aaa=$aaa/$aaaPct%")
                    applyRowOne(rsp, total)
                    applyRowTwo(aaa, total)
                } catch (e: Exception) {
                    Log.e("DirectorFlow", "HomeCard: parse failed: ${e.message}", e)
                    applyRowOne(0, 0)
                    applyRowTwo(0, 0)
                }
                showRowLoading(false)
            },
            { error ->
                Log.e("DirectorFlow", "HomeCard: /getDirectorOverview failed: status=${error.networkResponse?.statusCode} msg=${error.message}", error)
                applyRowOne(0, 0)
                applyRowTwo(0, 0)
                showRowLoading(false)
            }
        )
        req.retryPolicy = com.android.volley.DefaultRetryPolicy(
            30_000, 0,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue()?.add(req)
    }

    /** Lowest clubcode of facilities assigned to the logged-in user. Tries two
     *  signals (in order) since SharedPreferences sometimes has userID empty:
     *    1. `accspecid == userID` (the VisitationPlanningFragment rule)
     *    2. `specialistemail` contains the email handle (the
     *        ProgramsDetailsFragment.getFilteredFacilities rule)
     *  Falls back to "252" if neither matches — same fallback VPS uses. */
    private fun resolveDefaultClubCode(
        facilities: List<com.inspection.model.CsiFacility>,
        userID: String,
        specEmail: String
    ): String {
        // Try userID match first.
        if (userID.isNotEmpty()) {
            val mine = facilities.filter { !it.accspecid.isNullOrEmpty() && it.accspecid == userID }
            if (mine.isNotEmpty()) {
                return mine.sortedWith(compareBy { it.clubcode }).first().clubcode.orEmpty()
            }
        }
        // Fall back to email-handle match.
        if (specEmail.isNotEmpty()) {
            val handle = specEmail.substringBefore("@").lowercase()
            val mine = facilities.filter {
                !it.specialistemail.isNullOrEmpty() &&
                    it.specialistemail.lowercase().contains(handle)
            }
            if (mine.isNotEmpty()) {
                return mine.sortedWith(compareBy { it.clubcode }).first().clubcode.orEmpty()
            }
        }
        return "252"
    }

    /** Replace the static card subtitle with the live filter context so the user
     *  can see exactly which slice the numbers came from: "Specialist: Jane Doe
     *  | Club: 004". Falls back to the email handle if EmployeeList can't be
     *  matched. */
    private fun showFilterSubtitle(clubCode: String, specialistEmail: String) {
        val sv = _binding?.programsOverviewCard ?: return
        val handle = specialistEmail.substringBefore("@")
        val match = com.inspection.model.TypeTablesModel.getInstance().EmployeeList
            ?.firstOrNull { it.Email?.lowercase() == specialistEmail.lowercase() }
            ?: com.inspection.model.TypeTablesModel.getInstance().EmployeeList
                ?.firstOrNull { it.Email?.lowercase()?.startsWith(handle.lowercase()) == true }
        val fullName = match?.FullName?.takeIf { it.isNotBlank() } ?: handle
        sv.programsOverviewSubtitle.text = "Specialist: $fullName  |  Club: $clubCode"
    }

    private fun fetchSummaryFor(clubCode: String, specialistEmail: String) {
        showFilterSubtitle(clubCode, specialistEmail)
        val totalFacilities = com.inspection.model.CSIFacilitySingelton.getInstance().csiFacilities
            ?.count { it.clubcode == clubCode && !it.specialistemail.isNullOrEmpty() &&
                      it.specialistemail.lowercase().contains(specialistEmail.substringBefore("@").lowercase()) }
            ?: 0
        Log.d("FragmentForms", "Programs Overview Summary load: club=$clubCode spec=$specialistEmail facsInScope=$totalFacilities")

        // RSP — /getRspByLogin (SP-driven facility set + cache intersect)
        val rspUrl = Constants.getRspByLogin + clubCode +
            "&specialistEmail=" + java.net.URLEncoder.encode(specialistEmail, "UTF-8")
        Log.d("FragmentForms", "Request → GET $rspUrl")
        val rspReq = com.android.volley.toolbox.StringRequest(
            com.android.volley.Request.Method.GET, rspUrl,
            { response ->
                Log.d("FragmentForms", "RSP response received, size=${response?.length ?: 0}")
                if (!isAdded || _binding == null) {
                    Log.d("FragmentForms", "RSP response dropped — fragment detached or binding null")
                    return@StringRequest
                }
                try {
                    val arr = org.json.JSONArray(response)
                    var good = 0
                    for (i in 0 until arr.length()) {
                        // "RSP Automated" = facility is registered in the
                        // RSP system. The cache row's `lastupdated` is
                        // populated only when usp_GetAccountLastSynced
                        // returned a sync record, so a non-null/empty value
                        // is the canonical registration signal — date age
                        // is reflected in the Details Good/Issue split, not
                        // in the registered-or-not count.
                        val lastUpdated = arr.getJSONObject(i).optString("lastupdated", "")
                        if (lastUpdated.isNotEmpty() && lastUpdated != "null") good++
                    }
                    // Denominator MUST be the full filtered facility count (matches
                    // Details semantics — facilities not in the cache response are
                    // "Never Automated", not excluded). If the local facility list
                    // is empty for some reason, fall back to the cache row count.
                    val denom = if (totalFacilities > 0) totalFacilities else arr.length()
                    Log.d("FragmentForms", "RSP good=$good denom=$denom (arr.length=${arr.length()}, filteredFacs=$totalFacilities)")
                    applyRowOne(good, denom)
                } catch (e: Exception) {
                    Log.e("FragmentForms", "RSP summary parse failed: ${e.message}", e)
                    applyRowOne(0, 0)
                }
            },
            { error ->
                Log.e("FragmentForms", "RSP summary error: status=${error.networkResponse?.statusCode} msg=${error.message}", error)
                applyRowOne(0, 0)
            }
        )
        rspReq.retryPolicy = com.android.volley.DefaultRetryPolicy(
            30_000, 0,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val rspAdded = queue()?.add(rspReq)
        Log.d("FragmentForms", "RSP request enqueued? ${rspAdded != null}")

        // AAA — /getAaaByLogin (SP-driven facility set + cache intersect)
        val aaaUrl = Constants.getAaaByLogin + clubCode +
            "&specialistEmail=" + java.net.URLEncoder.encode(specialistEmail, "UTF-8")
        Log.d("FragmentForms", "Request → GET $aaaUrl")
        val aaaReq = com.android.volley.toolbox.StringRequest(
            com.android.volley.Request.Method.GET, aaaUrl,
            { response ->
                Log.d("FragmentForms", "AAA response received, size=${response?.length ?: 0}")
                if (!isAdded || _binding == null) {
                    Log.d("FragmentForms", "AAA response dropped — fragment detached or binding null")
                    return@StringRequest
                }
                try {
                    val arr = org.json.JSONArray(response)
                    var active = 0
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val eff = readField(obj, "eff_date", "effdate", "EffDate")
                        val exp = readField(obj, "exp_date", "expdate", "ExpDate")
                        if (eff.isNotEmpty() && (exp.isEmpty() || exp == "null")) active++
                    }
                    // Same rule as RSP — denominator is the filtered facility total,
                    // not the cache row count. Facilities missing from the cache
                    // response → "Not Active" (no ProgramTypeID=24 entry).
                    val denom = if (totalFacilities > 0) totalFacilities else arr.length()
                    Log.d("FragmentForms", "AAA active=$active denom=$denom (arr.length=${arr.length()}, filteredFacs=$totalFacilities)")
                    applyRowTwo(active, denom)
                } catch (e: Exception) {
                    Log.e("FragmentForms", "AAA summary parse failed: ${e.message}", e)
                    applyRowTwo(0, 0)
                }
            },
            { error ->
                Log.e("FragmentForms", "AAA summary error: status=${error.networkResponse?.statusCode} msg=${error.message}", error)
                applyRowTwo(0, 0)
            }
        )
        aaaReq.retryPolicy = com.android.volley.DefaultRetryPolicy(
            30_000, 0,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val aaaAdded = queue()?.add(aaaReq)
        Log.d("FragmentForms", "AAA request enqueued? ${aaaAdded != null}")
    }

    /** Same rule as ProgramsDetailsFragment.automationResultFromLastUpdated:
     *  parseable + within last 10 days → "Good". */
    private fun isWithinTenDays(raw: String): Boolean {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
        val d = try { sdf.parse(raw) } catch (e: Exception) { null } ?: return false
        val tenDaysAgo = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -10) }.time
        return !d.before(tenDaysAgo)
    }

    private fun readField(obj: org.json.JSONObject, vararg keys: String): String {
        for (k in keys) {
            if (obj.has(k) && !obj.isNull(k)) {
                val v = obj.optString(k, "")
                if (v.isNotEmpty()) return v
            }
        }
        return ""
    }

    private fun showRowLoading(loading: Boolean) {
        val sv = _binding?.programsOverviewCard ?: return
        sv.programRow1Loading.visibility = if (loading) View.VISIBLE else View.GONE
        sv.programRow1Dot.visibility = if (loading) View.GONE else View.VISIBLE
        sv.programRow2Loading.visibility = if (loading) View.VISIBLE else View.GONE
        sv.programRow2Dot.visibility = if (loading) View.GONE else View.VISIBLE
        if (loading) {
            sv.programRow1Ratio.text = "—"
            sv.programRow1Percent.text = "—"
            sv.programRow1Bar.progress = 0
            sv.programRow2Ratio.text = "—"
            sv.programRow2Percent.text = "—"
            sv.programRow2Bar.progress = 0
        }
    }

    private fun applyRowOne(numerator: Int, denominator: Int) {
        val sv = _binding?.programsOverviewCard
        Log.d("FragmentForms", "applyRowOne enter binding=${sv != null} ratioView=${sv?.programRow1Ratio} current='${sv?.programRow1Ratio?.text}'")
        if (sv == null) return
        sv.programRow1Loading.visibility = View.GONE
        sv.programRow1Dot.visibility = View.VISIBLE
        if (denominator <= 0) {
            sv.programRow1Ratio.text = "—"
            sv.programRow1Percent.text = "—"
            sv.programRow1Bar.progress = 0
            return
        }
        val pct = numerator * 100.0 / denominator
        val newText = "$numerator / $denominator"
        sv.programRow1Ratio.text = newText
        sv.programRow1Percent.text = String.format(java.util.Locale.US, "%.2f%%", pct)
        sv.programRow1Bar.max = 100
        sv.programRow1Bar.progress = pct.toInt()
        applyTier(sv.programRow1Percent, sv.programRow1Bar, sv.programRow1Dot, pct.toInt())
        Log.d("FragmentForms", "applyRowOne exit  ratio after='${sv.programRow1Ratio.text}' (set='$newText')")
    }

    private fun applyRowTwo(numerator: Int, denominator: Int) {
        val sv = _binding?.programsOverviewCard ?: return
        sv.programRow2Loading.visibility = View.GONE
        sv.programRow2Dot.visibility = View.VISIBLE
        if (denominator <= 0) {
            sv.programRow2Ratio.text = "—"
            sv.programRow2Percent.text = "—"
            sv.programRow2Bar.progress = 0
            return
        }
        val pct = numerator * 100.0 / denominator
        sv.programRow2Ratio.text = "$numerator / $denominator"
        sv.programRow2Percent.text = String.format(java.util.Locale.US, "%.2f%%", pct)
        sv.programRow2Bar.max = 100
        sv.programRow2Bar.progress = pct.toInt()
        applyTier(sv.programRow2Percent, sv.programRow2Bar, sv.programRow2Dot, pct.toInt())
    }

    /** Tier color by bucket: <60 amber, 60–79 orange, ≥80 green. Same drawables
     *  used in ProgramsDetailsFragment's applySummaryRowTier. */
    private fun applyTier(
        percentTv: TextView,
        bar: android.widget.ProgressBar,
        dot: View,
        pct: Int
    ) {
        when {
            pct >= 80 -> {
                percentTv.setTextColor(0xFF22A06B.toInt())
                bar.progressDrawable = androidx.core.content.ContextCompat.getDrawable(
                    bar.context, R.drawable.compliance_bar_high)
                dot.setBackgroundResource(R.drawable.status_dot_high)
            }
            pct >= 60 -> {
                percentTv.setTextColor(0xFFF08A2A.toInt())
                bar.progressDrawable = androidx.core.content.ContextCompat.getDrawable(
                    bar.context, R.drawable.compliance_bar_medium)
                dot.setBackgroundResource(R.drawable.status_dot_medium)
            }
            else -> {
                percentTv.setTextColor(0xFFF2B23A.toInt())
                bar.progressDrawable = androidx.core.content.ContextCompat.getDrawable(
                    bar.context, R.drawable.compliance_bar_low)
                dot.setBackgroundResource(R.drawable.status_dot_low)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        formsRequestQueue?.stop()
        formsRequestQueue = null
    }



}
