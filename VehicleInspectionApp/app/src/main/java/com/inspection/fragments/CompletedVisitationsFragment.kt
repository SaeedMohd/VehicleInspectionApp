package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.SearchDialog
import com.inspection.Utils.Utility

import com.inspection.databinding.FragmentCompletedVisitationsBinding

import com.inspection.model.CSIFacilitySingelton
import com.inspection.model.ClubCodeModel
import com.inspection.model.CompletedVisitationModel
import com.inspection.model.CsiFacility
import com.inspection.model.CsiSpecialist
import com.inspection.model.CsiSpecialistDetails
import com.inspection.model.CsiSpecialistSingletonModel
import com.inspection.model.DailyCount
import com.inspection.model.FacilityDataModel
import com.inspection.model.PRGDataModel
import com.inspection.model.PRGVisitationsLog
import com.inspection.model.TodayVisitationModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
import com.inspection.model.VisitationsStats
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
//import shaded.org.json.XML

import java.io.IOException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Locale.getDefault
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompletedVisitationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompletedVisitationsFragment : Fragment(),
    VisitationsAdapter.OpenPDFListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCompletedVisitationsBinding? = null
    private val binding get() = _binding!!
    var allClubCodes = ArrayList<String>()
    var specialistClubCodes = ArrayList<String>()
    var specialistArrayModel = ArrayList<TypeTablesModel.employeeList>()
    var specialistModel = ArrayList<CsiSpecialistDetails>()
    var requiredSpecialistName = ""
    var completedVisitations = ArrayList<CompletedVisitationModel>()
    var facilities = ArrayList<CsiFacility>()
    var facilityNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun initPieChart(chart: PieChart) {

        chart.setDrawHoleEnabled(false)
        chart.setDrawCenterText(false)
        chart.setDrawEntryLabels(true)

        chart.setHoleRadius(0f)
        chart.setTransparentCircleRadius(0f)

        chart.setUsePercentValues(false)

        chart.description.isEnabled = false
        chart.legend.isEnabled = true

        chart.setNoDataText("No data available")

        // 🔥 CRASH FIX
        chart.setTouchEnabled(false)
        chart.highlightValues(null)
    }

    override fun onOpenPDF(pdfName: String) {
        val clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        val client = clientBuilder.build()
        val request2 = okhttp3.Request.Builder().url(Constants.getS3Url + pdfName + "&type=PDF").build()
        binding.progressBarText.text = "Loading ..."
        binding.recordsProgressView.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Utility.showUnifiedErrorDialog(activity, "Failed to Get PDF - " + e.message)
                    binding.recordsProgressView.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                activity!!.runOnUiThread {
                    binding.progressBarText.text = "Loading ..."
                    binding.recordsProgressView.visibility = View.GONE
                    val signedUrl = response.body?.string()
                    if (!signedUrl.isNullOrEmpty()) {
                        // Must switch to main thread
                        requireActivity().runOnUiThread {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(signedUrl))
                            startActivity(intent)
                        }
                    } else {
                        Utility.showUnifiedErrorDialog(activity, "Failed to Get PDF URL")
                    }
                }
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_visitations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCompletedVisitationsBinding.bind(view)
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.visitationDateBtn.setText(getTodayFormatted())
        binding.visitationToDateBtn.setText(getTomorrowFormatted())
        binding.todayBtn.setOnClickListener {
            binding.visitationDateBtn.setText(getTodayFormatted())
            binding.visitationToDateBtn.setText(getTomorrowFormatted())
        }

        binding.currWeekBtn.setOnClickListener {
            getThisWeekRange().let {
                binding.visitationDateBtn.setText(it.first.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                binding.visitationToDateBtn.setText(it.second.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
            }
        }

        binding.currMonthBtn.setOnClickListener {
            getThisMonthRange().let {
                binding.visitationDateBtn.setText(it.first.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                binding.visitationToDateBtn.setText(it.second.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
            }
        }

        binding.currQtrBtn.setOnClickListener {
            getCurrentQuarterRange().let {
                binding.visitationDateBtn.setText(it.first.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                binding.visitationToDateBtn.setText(it.second.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
            }
        }

        binding.chartRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadio = group.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadio.text.toString()
            setupGraphs()
//            Toast.makeText(context, "Selected: $selectedText", Toast.LENGTH_SHORT).show()
        }

        initPieChart(binding.pieChart)

        binding.visitationDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                binding.visitationDateBtn.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        binding.visitationToDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "MM/dd/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                binding.visitationToDateBtn.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        loadFacilities()
        binding.visitationSpecialistName.setOnClickListener {
            var personnelNames = ArrayList<String>()
            (0 until TypeTablesModel.getInstance().EmployeeList.size).forEach {
                personnelNames.add(TypeTablesModel.getInstance().EmployeeList[it].FullName)
            }
            personnelNames.sort()
            personnelNames.add(0, "Any")
            var searchDialog = SearchDialog(context, personnelNames)
            searchDialog.show()
            searchDialog.setOnDismissListener {
                if (searchDialog.selectedString == "Any") {
                    binding.visitationSpecialistName.setText("")
                } else {
                    binding.visitationSpecialistName.setText(searchDialog.selectedString)
                }
            }
        }
        binding.searchBtn.setOnClickListener {
            searchVisitations()
        }
    }

    fun setupVisitationPieChart(chart: PieChart, data: Map<String, Int>?) {

        if (!isAdded || _binding == null) return

        chart.clear()
        chart.highlightValues(null)

        if (data.isNullOrEmpty()) {
            chart.visibility = View.GONE
            return
        }

        val filtered = data.filter { it.value > 0 }

        if (filtered.isEmpty()) {
            chart.visibility = View.GONE
            return
        }

        chart.visibility = View.VISIBLE

        val entries = filtered.map { (type, count) ->
            PieEntry(count.toFloat(), type)
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4A80F5"),
                Color.parseColor("#85C88A"),
                Color.parseColor("#9BBFF4"),
                Color.parseColor("#E49EDD"),
                Color.parseColor("#F18D00"),
                Color.parseColor("#FFCD56")
            )
            sliceSpace = 2f
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }

        chart.data = PieData(dataSet)
        chart.invalidate()
    }

    fun setupDailyBarChart(chart: BarChart, data: List<DailyCount>?) {

        chart.data = null

        if (data.isNullOrEmpty()) {
            chart.invalidate()
            return
        }

        val entries = data.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.count.toFloat())
        }

        val dataSet = BarDataSet(entries, "Visits per Day").apply {
            color = Color.parseColor("#4A80F5")
            valueTextSize = 11f
            setDrawValues(true)
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.5f
        }

        chart.apply {
            this.data = barData
            setFitBars(true)

            // X Axis
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(
                    data.map { it.date.substring(5).replace("-", "/") }
                )
                granularity = 1f
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 10f
            }

            // Y Axis
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f

            description.isEnabled = false
            legend.isEnabled = false

            animateY(800)
            invalidate()
        }
    }

    override fun onDestroyView() {

        try {
            binding.pieChart.apply {
                setTouchEnabled(false)
                highlightValues(null)
                clear()
                data = null
                clearAnimation()
            }

            binding.barChart.apply {
                clear()
                data = null
                clearAnimation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _binding = null
        super.onDestroyView()
    }

    fun loadSpecialists() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllSpecialists + "",
            { response ->
                Log.v("****response", response)
                requireActivity().runOnUiThread {
                    CsiSpecialistSingletonModel.getInstance().csiSpecialists = Gson().fromJson(response.toString(), Array<CsiSpecialist>::class.java).toCollection(java.util.ArrayList())
                }
            }, {
                Log.v("error while loading", "error while loading specialists")
                Log.v("Loading error", "" + it.message)
            }))

    }

    private fun loadFacilities(){
        facilities.clear()
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAllFacilities + "",
            { response ->
                Log.v("test","testtesttest-----------")
                requireActivity().runOnUiThread {
                    binding.recordsProgressView.visibility = View.INVISIBLE
                    CSIFacilitySingelton.getInstance().csiFacilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
                    facilities = Gson().fromJson(response.toString(), Array<CsiFacility>::class.java).toCollection(ArrayList())
                    loadSpecialists()
                    loadSpecialistName()
                }
            },
            {
//            Utility.showMessageDialog(activity, "Retrieve Data Error", "Connection Error while retrieving Facilities - " + it.message)
                Utility.showUnifiedErrorDialog(activity,"Error while retrieving Facilities - " + it.message)
                binding.recordsProgressView.visibility = View.INVISIBLE
                Log.v("error while loading", "error while loading facilities")
                Log.v("Loading error", "" + it.message)
            }))
    }

    private fun loadSpecialistName() {
        specialistArrayModel = TypeTablesModel.getInstance().EmployeeList
        var specMail = ApplicationPrefs.getInstance(context).loggedInUserEmail.substring(0,
            ApplicationPrefs.getInstance(context).loggedInUserEmail.indexOf("@")).lowercase()
        if (specialistArrayModel != null && specialistArrayModel.size > 0) {
            requiredSpecialistName = specialistArrayModel.filter { s -> s.Email.lowercase(getDefault())
                .startsWith(specMail)}[0].FullName
            var positionID = specialistArrayModel.filter { s -> s.Email.lowercase(getDefault()).startsWith(specMail)}[0].PositionID
            if (positionID.equals("1")) {
                binding.visitationSpecialistName.setText(requiredSpecialistName)
            }
//            ApplicationPrefs.getInstance(activity).loggedInUserID = specialistArrayModel.filter { s -> s.Email.toLowerCase().startsWith(specMail)}[0].NTLogin
//            ApplicationPrefs.getInstance(activity).loggedInUserFullName = specialistArrayModel.filter { s -> s.Email.toLowerCase().startsWith(specMail)}[0].FullName
        }
//        loadClubCodes()
    }

    private fun loadClubCodes() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getClubCodes,
            { response ->
                var clubCodeModels =
                    Gson().fromJson(response.toString(), Array<ClubCodeModel>::class.java)
                allClubCodes.clear()
                for (cc in clubCodeModels) {
                    allClubCodes.add(cc.clubcode)
                }
            }, {
                Log.v("error while loading", "error while loading club codes")
            })
        )
    }

    fun searchVisitations() {
        if (binding.visitationSpecialistName.text.toString().isEmpty()) {
            Utility.showUnifiedErrorDialog(activity, "Please select a Specialist to search.")
            return
        }
        if (binding.visitationDateBtn.text.toString()=="FROM DATE" || binding.visitationToDateBtn.text.toString()=="TO DATE") {
            Utility.showUnifiedErrorDialog(activity, "Please select date range.")
            return
        }
        binding.resultsll.isVisible = false
        completedVisitations.clear()
        var specialistName = binding.visitationSpecialistName.text.toString()
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request2 = okhttp3.Request.Builder().url(
            Constants.getCompletedVisitations + specialistName.replace(" ", "%20")
                    + "&dateFrom=" + binding.visitationDateBtn.text.toString()//.replace("/", "%2F")
                    + "&dateTo=" + binding.visitationToDateBtn.text.toString()//.replace("/", "%2F")
        ).build()
        binding.progressBarText.text = "Loading ..."
        binding.recordsProgressView.visibility = View.VISIBLE

        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Utility.showUnifiedErrorDialog(activity, "Failed to Get Completed Visitations - " + e.message)
                    binding.recordsProgressView.visibility = View.GONE
                    binding.progressBarText.text = "Loading ..."
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response.body!!.string()
                activity!!.runOnUiThread {
                    binding.progressBarText.text = "Loading ..."
                    if (!response.toString().replace(" ", "").equals("[]")) {
                        Log.v("Visitation Data", responseString)
                        completedVisitations = Gson().fromJson(responseString, Array<CompletedVisitationModel>::class.java).toCollection(ArrayList())
                        for (cv in completedVisitations) {
//                            Log.v("FacID ----> ", cv.facid)
                            var facility = facilities.filter { f -> f.facnum.equals(cv.facno) && f.clubcode.equals(cv.clubcode) }
                            if (facility != null && facility.size > 0) {
                                cv.facName = facility[0].facname
                            }
                        }
                        binding.resultsll.isVisible = true
                        binding.listRecyclerView.adapter = VisitationsAdapter(completedVisitations,this@CompletedVisitationsFragment )
                        binding.recordsProgressView.visibility = View.GONE
                        binding.filteredVal.text = completedVisitations.size.toString()
                        setupGraphs()
                        getStatistics()
                    } else {
//                        var item = PRGVisitationsLog()
//                        item.recordid = -1
//                        PRGDataModel.getInstance().tblPRGVisitationsLog.add(item)
                    }

                }
            }

        })

    }

    fun setupGraphs() {
        if (binding.typeRadioBtn.isChecked) {
            binding.pieChart.isVisible = true
            binding.barChart.isVisible = false
            setupVisitationPieChart(
                binding.pieChart,
                completedVisitations.groupingBy { it.visitationtype.toString() }.eachCount()
            )
        } else if (binding.methodRadioBtn.isChecked) {
            binding.pieChart.isVisible = true
            binding.barChart.isVisible = false
            setupVisitationPieChart(
                binding.pieChart,
                completedVisitations.groupingBy { it.visitationmethod}.eachCount())
        } else {
            binding.pieChart.isVisible = false
            binding.barChart.isVisible = true
            setupDailyBarChart(
                binding.barChart,
                completedVisitations.groupingBy {
                    it.insertdate.substring(0, 10)
                }.eachCount().map {
                    DailyCount(it.key, it.value)
                }.sortedBy { it.date }
            )
        }
    }

    fun getTodayFormatted(pattern: String = "MM/dd/yyyy"): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern))
    }

    fun getTomorrowFormatted(pattern: String = "MM/dd/yyyy"): String {
        return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(pattern))
    }

    fun getThisWeekRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        val weekEnd = today.with(DayOfWeek.SUNDAY)
        return weekStart to weekEnd
    }

    fun getThisMonthRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val start = today.withDayOfMonth(1)
        val end = today.withDayOfMonth(today.lengthOfMonth())
        return start to end
    }

    fun getCurrentQuarter(): Int {
        val month = LocalDate.now().monthValue
        return (month - 1) / 3 + 1
    }

    fun getCurrentQuarterRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val quarter = getCurrentQuarter()

        val startMonth = (quarter - 1) * 3 + 1   // 1→Jan, 4→Apr, 7→Jul, 10→Oct
        val start = LocalDate.of(today.year, startMonth, 1)

        val endMonth = startMonth + 2
        val end = LocalDate.of(today.year, endMonth, Month.of(endMonth).length(today.isLeapYear))

        return start to end
    }

    fun getStatistics() {
        var specialistName = binding.visitationSpecialistName.text.toString()
        var clientBuilder = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        var client = clientBuilder.build()
        var request2 = okhttp3.Request.Builder().url(
            Constants.getPDFStats + specialistName.replace(" ", "%20")
        ).build()
        client.newCall(request2).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                var responseString = response.body!!.string()
                activity!!.runOnUiThread {
                    if (!response.toString().replace(" ", "").equals("[]")) {
                        Log.v("Visitation Stats", responseString)
                        val stats = Gson().fromJson(responseString, Array<VisitationsStats>::class.java).toCollection(ArrayList())
                        binding.weekVal.text = stats[0].tot_week.toString()
                        binding.monthVal.text = stats[0].tot_month.toString()
                    }
                }
            }

        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompletedVisitationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompletedVisitationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

class VisitationsAdapter(
    private val visitations: List<CompletedVisitationModel>,
    private val onOpenPDF: OpenPDFListener
) : RecyclerView.Adapter<VisitationsAdapter.ViewHolder>() {

    interface OpenPDFListener {
        fun onOpenPDF(pdfName: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fac_name_text: TextView = itemView.findViewById(R.id.facNameVal)
        val fac_no_text: TextView = itemView.findViewById(R.id.facNoVal)
        val club_no_text: TextView = itemView.findViewById(R.id.clubNoVal)
        val type_text: TextView = itemView.findViewById(R.id.typeVal)
        val reason_text: TextView = itemView.findViewById(R.id.reasonVal)
        val rep_text: TextView = itemView.findViewById(R.id.repVal)
        val waived_text: TextView = itemView.findViewById(R.id.waiveVal)
        val id_text: TextView = itemView.findViewById(R.id.idVal)
        val date_text: TextView = itemView.findViewById(R.id.dateVal)
        val method_text: TextView = itemView.findViewById(R.id.methodVal)
        val link_text: TextView = itemView.findViewById(R.id.idURL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.completed_visitation_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fac_name_text.text = visitations[position].facName
        holder.fac_no_text.text = visitations[position].facno
        holder.club_no_text.text = visitations[position].clubcode
        holder.type_text.text = visitations[position].visitationtype.toString()
        holder.reason_text.text = visitations[position].visitationreason
        holder.rep_text.text = visitations[position].facilityrep
        holder.waived_text.text = if (visitations[position].waivevisitation == 1) "Yes" else "No"
        holder.id_text.text = visitations[position].visitationid
        holder.date_text.text = dbUtcToLocal(visitations[position].insertdate)
        holder.method_text.text = visitations[position].visitationmethod

        var awsFileName = ""
        if ( visitations[position].facid.isNotEmpty()) {
            Log.v("FACID--> ", visitations[position].facid)
            awsFileName = visitations[position].facid + "_101_" + visitations[position].visitationid + "_" + visitations[position].visitationid + "_VisitationDetails_ForSpecialist.pdf"
            holder.link_text.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            holder.link_text.visibility = View.VISIBLE
        } else {
            Log.v("NO FACID--> ", "HERE")
            holder.link_text.visibility = View.GONE
        }

        holder.link_text.setOnClickListener {
            onOpenPDF.onOpenPDF(awsFileName)
//                val url = "https://www.google.com"
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse(url)
//                startActivity(context,intent, null)
        }

    }

    fun dbUtcToLocal(dbDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)

        val outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())

        val instant = inputFormatter.parse(dbDate, Instant::from)
        return outputFormatter.format(instant)
    }

    override fun getItemCount() = visitations.size

}