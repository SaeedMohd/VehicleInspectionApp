package com.inspection.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bugfender.sdk.Bugfender
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.awsReference
import com.inspection.Utils.Constants.bitmapToBase64
import com.inspection.Utils.Constants.clearExpiredImages
import com.inspection.Utils.Constants.decodeBase64ToBitmap
import com.inspection.Utils.Constants.getBase64ImageIfValid
import com.inspection.Utils.Constants.internetConnectionErrMsg
import com.inspection.Utils.Constants.saveBase64Image
//import com.inspection.adapter.MultipartRequest
import com.inspection.databinding.FragmentOthersBinding
import com.inspection.databinding.FragmentVisitationFormBinding
import com.inspection.model.*
//import kotlinx.android.synthetic.main.fragment_aarav_location.*
//import kotlinx.android.synthetic.main.fragment_arrav_facility.*
//import kotlinx.android.synthetic.main.fragment_visitation_form.*
//import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.time.OffsetDateTime
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentVisitation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentVisitation.newInstance] factory method to
 * create an instance of this fragment.
 */
//    var specialistWatcher=""
class FragmentVisitation : Fragment() {
    var isFacilityRepresentativeSignatureInitialized = false
    var isAutomotiveSpecialistSignatureInitialized = false
    var saveToDBFailed = false
    var createMsg = ""
//    var isWaiverSignatureInitialized = false
//    var isFacilityRepresentativeDeficiencySignatureInitialized = false
    val isInitialized = booleanArrayOf(false, false, false, false, false, false, false, false, false,false)
    /*
        isInitialized[0] --> automotiveSpecialistSpinner
        isInitialized[1] --> facilityRepresentativesSpinner
        isInitialized[2] --> aarSignEditText
        isInitialized[3] --> certificateOfApprovalEditText
        isInitialized[4] --> qualityControlProcessEditText
        isInitialized[5] --> staffTrainingProcessEditText
        isInitialized[6] --> memberBenefitsPosterEditText
        isInitialized[7] --> waiverCommentsEditText
        isInitialized[8] --> emailEditText
        isInitialized[9] --> visitationMethodDropListId
     */
    var facilityRepresentativeNames = ArrayList<String>()
    var facilitySpecialistNames = ArrayList<String>()
//    var steps : List<Step> = listOf()
    enum class requestedSignature {
        representative, specialist, representativeDeficiency, waiver
    }
    var visitationProcessCompleted = false
    var waiveVisitationCBPreviousValue = false
    var emailPdfCBPreviousValue = false
    var waiverCommentsPreviousValue = ""
    var visitMethodPreviousValue = ""
    var emailEditTextPreviousValue = ""
    var staffTrainingProcessPreviousValue = ""
    var qualityControlProcessPreviousValue = ""
    var aarSignPreviousValue = ""
    var certificateOfApprovalPreviousValue = ""
    var memberBenefitsPosterPreviousValue = ""
    var visitationComments = ""
    var VisitationReasonList = ArrayList<TypeTablesModel.visitationReasonType>()
    var VisitationMethodList = ArrayList<TypeTablesModel.visitationMethodType>()
    var VisitationReasonArray = ArrayList<String>()
    var VisitationMethodArray = ArrayList<String>()
    var duplicateVisitationID = ""
    var specSignatureName = ""
    var repSignatureName = ""
    var defSignatureName = ""
    var waiveSignatureName = ""

    var selectedSignature: requestedSignature? = null

    var emailValid = true
    var imgCounter = 0
    var firsLoadDone = false

    private var _binding: FragmentVisitationFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        throw RuntimeException("Test Crash"); // Force a crash
        (activity as FormsActivity).saveRequired = false
        return inflater!!.inflate(R.layout.fragment_visitation_form, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVisitationFormBinding.bind(view)
        (activity as FormsActivity).saveRequired = false
        FacilityDataModelOrg.getInstance().changeWasDone = false
        binding.dataChangedNoRadioButton.isClickable = false
        binding.dataChangedYesRadioButton.isClickable = false
        dataChangeHandling()
        checkMarkChangesDone()
        binding.visitationMethodDropListId.tag = 0
        binding.facilityRepresentativesSpinner.tag = 0
        initializeFields()
        setFieldsValues()
//        setFieldsListeners()
        fillTrackingData()
        setAlertColoring()

        // NetworkStatus Indicator [Start]
        val animation: Animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        binding.netwrokStatusText.startAnimation(animation)
        // NetworkStatus Indicator [End]

        IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
//        var item = TypeTablesModel.visitationReasonType()
//        item.visitationReasonID=1
//        item.visitationReasonName="Deficiency Inspection"
//        TypeTablesModel.getInstance().VisitationReasons.add(item)
//        var item2 = TypeTablesModel.visitationReasonType()
//        item2.visitationReasonID=2
//        item2.visitationReasonName="Member Complaint"
//        TypeTablesModel.getInstance().VisitationReasons.add(item2)
//        var item3 = TypeTablesModel.visitationReasonType()
//        item3.visitationReasonID=3
//        item3.visitationReasonName="Motorsport Ticket Delivery"
//        TypeTablesModel.getInstance().VisitationReasons.add(item3)
//        var item4 = TypeTablesModel.visitationReasonType()
//        item4.visitationReasonID=4
//        item4.visitationReasonName="Battery Tester Replacement"
//        TypeTablesModel.getInstance().VisitationReasons.add(item4)
//        var item5 = TypeTablesModel.visitationReasonType()
//        item5.visitationReasonID=5
//        item5.visitationReasonName="Courtesy Visit"
//        TypeTablesModel.getInstance().VisitationReasons.add(item5)
//        var item6 = TypeTablesModel.visitationReasonType()
//        item6.visitationReasonID=7
//        item6.visitationReasonName="Quarterly Visitation"
//        TypeTablesModel.getInstance().VisitationReasons.add(item6)
//        var item7 = TypeTablesModel.visitationReasonType()
//        item7.visitationReasonID=6
//        item7.visitationReasonName="Annual Visitation"
//        TypeTablesModel.getInstance().VisitationReasons.add(item7)
//        var item8 = TypeTablesModel.visitationReasonType()
//        item8.visitationReasonID=8
//        item8.visitationReasonName="Other"
//        TypeTablesModel.getInstance().VisitationReasons.add(item8)

//        completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()

        // SAEED TO BE REVIEWED
//        (activity as FormsActivity).visitationsTitle.setTextColor(Color.parseColor("#26C3AA"))
//        (activity as FormsActivity).visitationTitle.setTextColor(Color.parseColor("#26C3AA"))
        requireActivity().findViewById<TextView>(R.id.visitationsTitle).setTextColor(Color.parseColor("#26C3AA"))
        requireActivity().findViewById<TextView>(R.id.visitationTitle).setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        (activity as FormsActivity).saveRequired = false
        // SAVE IN PROGRESS
        if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }.isEmpty()) {
            markVisitationInProgress()
        }
//        firsLoadDone = true
        steps = listOf(
            Step(1, "Save Signatures", "Pending",""),
            Step(2, "Collect Visitation Data", "Pending",""),
            Step(3, "Create Visitation", "Pending",""),
            Step(4, "Update Visitation Details", "Pending",""),
            Step(5, "Generate PDF", "Pending",""),
//            Step(6, "Upload PDF", "Pending",""),
//            Step(7, "Upload to AWS", "Pending",""),
//            Step(8, "Add APP Link", "Pending,","")
        )

        binding.visitationStatusBtn.setOnClickListener {
            visitationProcessCompleted = true
            binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
            if (steps[3].status.contains("Success") || steps[3].status.contains("Failed")) {
                (activity as FormsActivity).saveRequired = false
                (activity as FormsActivity).saveVisitedScreensRequired = false
                (activity as FormsActivity).saveDone = true
            }
            if (steps[3].status.contains("Success")) {
                binding.completeButton.isEnabled = false
            }
            refreshButtonsState()
            binding.dialogueLoadingView.visibility = View.GONE
            binding.visitationStepview.visibility = View.GONE
            binding.progressBarTextVal.text = "Loading ..."
            (activity as FormsActivity).onBackPressed()
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.stepsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = StepAdapter(steps)
        refreshButtonsState()
    }


    fun markVisitationInProgress() { /// Mark visitation as In Progress
        var strUrl = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "&clubCode=" + FacilityDataModel.getInstance().clubCode + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&facAnnualInspectionMonth=" + FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth + "&inspectionCycle=" + FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle + "&userId=" + ApplicationPrefs.getInstance(activity).loggedInUserID + "&visitedScreens=" + IndicatorsDataModel.getInstance().getVisitedScreen() + "&visitationType=" + FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType + "&cancelled=0"
        Log.v("Mark In Progress -> ", Constants.saveVisitedScreens + strUrl)
        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.saveVisitedScreens + strUrl,
                Response.Listener { response ->
                }, Response.ErrorListener {
            Log.v("Mark Visitation", " As In Progress Failed --> " + it.message)
            it.printStackTrace()
        }))
    }

    private fun setAlertColoring() {
        var toolTipStr = ""
        val animation: Animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        binding.alertVisitationRIcon.isVisible = false
        binding.alertVisitationYIcon.isVisible = false
        binding.alertVisitationRIcon.tooltipText = toolTipStr
        binding.alertVisitationYIcon.tooltipText = toolTipStr
        binding.billingAlertText.isVisible = false
        if (binding.emailEditText.text.isNotEmpty()) {
            if (!emailFormatValidation(binding.emailEditText.text.toString())) {
                binding.alertVisitationRIcon.isVisible = true
                binding.alertVisitationYIcon.isVisible = false
                binding.alertVisitationRIcon.isClickable = true
                toolTipStr = "${FacilityDataModel.getInstance().tblFacilityEmail[0].email} Format is incorrect\nPlease update the email from Location & Contact Info Screen"
                binding.alertVisitationRIcon.startAnimation(animation) //to start animation
                binding.alertVisitationYIcon.startAnimation(animation) //to start animation
                binding.alertVisitationRIcon.setOnClickListener({
//                    Utility.showMessageDialog(requireContext(), "Notification", toolTipStr)
                    Utility.showUnifiedInformationDialog(requireContext(),toolTipStr)
                })
            } else {
                binding.alertVisitationRIcon.isVisible = false
                binding.alertVisitationYIcon.isVisible = false
            }
        }
//        var billingAlerts = false
//        val filteredList = FacilityDataModel.getInstance().tblInvoiceInfo.filter { item ->
//            try {
//                val dueDate = OffsetDateTime.parse(item.BillingDueDate)
//                dueDate.isBefore(OffsetDateTime.now().minusMonths(30))
//            } catch (e: Exception) {
//                false // skip invalid date format
//            }
//        }
//        for (item in FacilityDataModel.getInstance().tblBillingHistory) {
//            if (item.InvoiceStatusName.equals("2nd") || item.InvoiceStatusName.equals("3rd")) {
//                if (!item.BillBalanceDue.equals("0")) {
//                    binding.billingAlertText.visibility = View.VISIBLE
//                    binding.billingAlertText.startAnimation(animation)
//                    break
//                }
//            }
//        }

//        for (item in FacilityDataModel.getInstance().tblBillingHistory) {
//            if (item.InvoiceStatusName.equals("2nd") || item.InvoiceStatusName.equals("3rd")) {
//                if (!item.BillBalanceDue.equals("0")) {
//                    binding.billingAlertText.visibility = View.VISIBLE
//                    binding.billingAlertText.startAnimation(animation)
//                    break
//                }
//            }
//        }

        if (FacilityDataModel.getInstance().tblBillingHistory[0].FACID!=-1 && FacilityDataModel.getInstance().tblBillingHistory[0].BillBalanceDue.toDouble()>0.0) {
            binding.billingAlertText.visibility = View.VISIBLE
            binding.billingAlertText.startAnimation(animation)
        } else {
            binding.billingAlertText.visibility = View.GONE
//            binding.billingAlertText.clearAnimation()
        }



//
//        if (RAlert) {
//            alertVisitationRIcon.isClickable = true
//            alertVisitationRIcon.setOnClickListener({
//                Utility.showMessageDialog(requireContext(), "Notification", toolTipStr)
//            })
//        } else {
//            alertVisitationRIcon.isClickable = false
//        }


    }

    fun fillTrackingData() {
        if (binding.trackingTableLayout.childCount > 1) {
            for (i in binding.trackingTableLayout.childCount - 1 downTo 1) {
                binding.trackingTableLayout.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 20
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0
        rowLayoutParam.gravity = Gravity.CENTER

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 2F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 2F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 2F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 2F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER


        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblVisitationTracking.apply {
            (0 until size).forEach {
                if (!get(it).performedBy.equals("00")) {
                    Log.v("NUMBER ------------ ", " " + it)
                    Log.v("Reason ------------ ", get(it).VisitationReasonTypeID)
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30
                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.minimumHeight = 30
                    textView.text = if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER
                    textView1.textSize = 14f
                    textView1.minimumHeight = 30
                    textView1.text = get(it).performedBy
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam1
                    textView2.gravity = Gravity.CENTER
                    textView2.textSize = 14f
                    textView2.minimumHeight = 30
                    var visitationType = ""
                    if (get(it).VisitationTypeID.equals("1")) {
                        visitationType = VisitationTypes.Annual.toString()
                    } else if (get(it).VisitationTypeID.equals("2")) {
                        visitationType = VisitationTypes.Quarterly.toString()
                    } else if (get(it).VisitationTypeID.equals("3")) {
                        visitationType = VisitationTypes.AdHoc.toString()
                    } else if (get(it).VisitationTypeID.equals("4")) {
                        visitationType = VisitationTypes.Deficiency.toString()
                    }
                    textView2.text = visitationType
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam1
                    textView3.gravity = Gravity.CENTER
                    textView3.textSize = 14f
                    textView3.minimumHeight = 30
                    textView3.text = if (get(it).VisitationMethodTypeID.equals("") || get(it).VisitationMethodTypeID.equals("0")) "" else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeID.toString().equals(get(it).VisitationMethodTypeID) }[0].TypeName
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam1
                    textView4.gravity = Gravity.CENTER
                    textView4.textSize = 14f
                    textView4.minimumHeight = 30
                    textView4.text = if (get(it).VisitationReasonTypeID.equals("") || get(it).VisitationReasonTypeID.equals("0")) "" else TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeID.toString().equals(get(it).VisitationReasonTypeID) }[0].VisitationReasonTypeName
                    tableRow.addView(textView4)

                    binding.trackingTableLayout.addView(tableRow)
                }
            }
        }

    }


    fun checkMarkChangesDone() {
        IndicatorsDataModel.getInstance().validateVisitationSectionVisited()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun dataChangeHandling() {
        binding.dataChangedYesRadioButton.isChecked = HasChangedModel.getInstance().changeWasMadeFroAny()
        binding.dataChangedNoRadioButton.isChecked = !binding.dataChangedYesRadioButton.isChecked
    }

    private fun initializeFields() {

        binding.annualVisitationType.isClickable = false
        binding.annualVisitationType.isEnabled = false

        binding.quarterlyVisitationType.isClickable = false
        binding.quarterlyVisitationType.isEnabled = false

        binding.adhocVisitationType.isClickable = false
        binding.dataChangedYesRadioButton.isClickable = false

        binding.adhocVisitationType.isEnabled = false
        binding.defVisitationType.isEnabled = false

        binding.dateOfVisitationButton.isClickable = false
        binding.dataChangedNoRadioButton.isClickable = false
//        clubCodeEditText.isClickable = false
//        clubCodeEditText.isEnabled = false
        binding.facilityNumberEditText.isEnabled = false


        handleCancelButtonClick()

        altDeffVisitationTableRow(2)
    }

    private fun setFieldsValues() {

        facilityRepresentativeNames.clear()
        facilityRepresentativeNames.add("please select a representative")

        for (fac in FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName + " " + s.LastName }.distinct()) {
            facilityRepresentativeNames.add(fac)
        }
        facilityRepresentativeNames.sort()
        VisitationReasonList = TypeTablesModel.getInstance().VisitationReasonType
        VisitationReasonArray.clear()
        VisitationReasonArray.add("-- Choose --")
        for (reason in VisitationReasonList) {
            VisitationReasonArray.add(reason.VisitationReasonTypeName)
        }

        VisitationMethodList = TypeTablesModel.getInstance().VisitationMethodType
        VisitationMethodArray.clear()
        VisitationMethodArray.add("-- Choose --")
        for (reason in VisitationMethodList) {
            VisitationMethodArray.add(reason.TypeName)
        }

        binding.visitationReasonDropListId.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, VisitationReasonArray)
        binding.visitationMethodDropListId.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, VisitationMethodArray)

        binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
        binding.facilityRepresentativeSignatureButton.isEnabled  = IndicatorsDataModel.getInstance().validateAllScreensVisited()
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == null) {
            binding.adhocVisitationType.isChecked = true
            binding.adhocVisitationType.isClickable = true
            binding.adhocVisitationType.isEnabled = true
            binding.annualVisitationType.isClickable = true
            binding.annualVisitationType.isEnabled = true
            binding.quarterlyVisitationType.isClickable = true
            binding.quarterlyVisitationType.isEnabled = true
            binding.defVisitationType.isClickable = true
            binding.defVisitationType.isEnabled = true
            binding.visitationReasonDropListId.setSelection(0, true)
            binding.visitationReasonDropListId.isEnabled = true
            binding.visitationReasonDropListId.isClickable = true
            binding.completeButton.isEnabled = true
            binding.facilityRepresentativeSignatureButton.isEnabled = true
        } else {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Annual)) {
                binding.annualVisitationType.isChecked = true
                binding.visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Annual Visitation"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Quarterly)) {
                binding.quarterlyVisitationType.isChecked = true
                binding.visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Quarterly Visitation"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) {
                binding.adhocVisitationType.isChecked = true
                // Make Type selectable
                binding.adhocVisitationType.isClickable = true
                binding.adhocVisitationType.isEnabled = true
                binding.annualVisitationType.isClickable = true
                binding.annualVisitationType.isEnabled = true
                binding.quarterlyVisitationType.isClickable = true
                binding.quarterlyVisitationType.isEnabled = true
                binding.defVisitationType.isClickable = true
                binding.defVisitationType.isEnabled = true

                // End
                binding.visitationReasonDropListId.setSelection(0, true)
//                visitationReasonDropListId.isEnabled = true
//                visitationReasonDropListId.isClickable = true
                binding.completeButton.isEnabled = true
                binding.facilityRepresentativeSignatureButton.isEnabled = true
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Deficiency)) {
                binding.defVisitationType.isChecked = true
                binding.visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Deficiency Inspection"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
                binding.completeButton.isEnabled = true
                binding.facilityRepresentativeSignatureButton.isEnabled = true
            }
        }

        binding.waiveVisitationCheckBox.isChecked = false
        binding.emailPdfCheckBox.isChecked = false
        binding.waiverCommentsEditText.setText("")
        binding.waiverCommentsEditText.tag = "0"
        binding.emailEditText.setText("")
        binding.emailEditText.tag = "0"
        binding.facilityRepresentativesSpinner.setSelection(0)
        binding.staffTrainingProcessEditText.setText("")
        binding.staffTrainingProcessEditText.tag = "0"
        binding.qualityControlProcessEditText.setText("")
        binding.qualityControlProcessEditText.tag = "0"
        binding.aarSignEditText.setText("")
        binding.aarSignEditText.tag = "0"
        binding.certificateOfApprovalEditText.setText("")
        binding.certificateOfApprovalEditText.tag = "0"
        binding.memberBenefitsPosterEditText.setText("")
        binding.memberBenefitsPosterEditText.tag = "0"
        binding.dateOfVisitationButton.text = Date().toAppFormatMMDDYYYY()
        binding.clubCodeEditVal.setText(FacilityDataModel.getInstance().clubCode)
        binding.facilityNumberEditText.setText("" + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            binding.aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            binding.certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            binding.memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            binding.qualityControlProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            binding.staffTrainingProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
            binding.emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {


            facilitySpecialistNames.add("Select Specialist")
            for (specialist in TypeTablesModel.getInstance().EmployeeList.sortedWith(compareBy { it.FullName })) {
                facilitySpecialistNames.add(specialist.FirstName.lowercase().capitalize() + " " + specialist.LastName.lowercase().capitalize())
            }

            binding.facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, facilityRepresentativeNames)
            //   automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname })
            binding.automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, facilitySpecialistNames)

            facilityNameAndNumberRelationForSelection()
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName))
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.toUpperCase()))
            binding.automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))
            binding.automotiveSpecialistSpinner.tag = binding.automotiveSpecialistSpinner.selectedItemPosition
//            automotiveSpecialistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                }
//
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    //Adding condition as a workaround not to lost the applied changes for signature. As this method is called also during adapter initialization
//
//                    if (p2 > 0) {
//                        if (isAutomotiveSpecialistSignatureInitialized) {
//                            isAutomotiveSpecialistSignatureInitialized = false
//                        } else {
////                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = facilitySpecialistNames[p2]
////                            FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = null
////                            automotiveSpecialistSignatureImageView.setImageBitmap(null)
//
//                        }
//                    } else {
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = ""
//                    }
//
//                    if (!automotiveSpecialistSpinner.tag.equals(p2) || automotiveSpecialistSpinner.tag.equals("-1")) {
//                        automotiveSpecialistSpinner.tag = "-1"
//                        if (firsLoadDone) {
//                            (activity as FormsActivity).saveRequired = true
//                            Log.v("SAVEREQUIRED -->", " automotiveSpecialistSpinner")
//                            refreshButtonsState()
//                        }
//                    }
//
//                    checkMarkChangesDone()
//                }
//
//            }
        }

        // Do Not load signatures
//        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
//            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature != null) {
//                isFacilityRepresentativeSignatureInitialized = true
//                facilityRepresentativeSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature)
//            }
//        }
//
//        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
//            if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature != null) {
//                isAutomotiveSpecialistSignatureInitialized = true
//                automotiveSpecialistSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature)
//            }
//        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature != null) {
                binding.facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature)
            }
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature != null) {
                binding.waiversSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature)
            }
        }



        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {


            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNotEmpty()) {
                binding.facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                binding.facilityRepresentativesSpinner.tag = binding.facilityRepresentativesSpinner.selectedItemPosition
                Log.v("TAG -->", binding.facilityRepresentativesSpinner.tag.toString())
            }
            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNotEmpty()) {
                    binding.facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                    binding.facilityRepresentativesSpinner.tag = binding.facilityRepresentativesSpinner.selectedItemPosition
                    Log.v("TAG -->", binding.facilityRepresentativesSpinner.tag.toString())
                }
            }

//            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNotEmpty()) {
////                    facilityRepresentativesSpinner.setSelection(CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                    facilityRepresentativesSpinner.setSelection(TypeTablesModel.getInstance().EmployeeList.map { s -> s.FullName}.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                }
//            }

            binding.facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))
            binding.facilityRepresentativesSpinner.tag = binding.facilityRepresentativesSpinner.selectedItemPosition
            Log.v("TAG -->", binding.facilityRepresentativesSpinner.tag.toString())
            if (PRGDataModel.getInstance().tblPRGVisitationHeader.filter { s->s.visitationid.equals("0") || s.visitationid==null }.isNotEmpty()) {
                var loadPrevData = true
//                if (adhocVisitationType.isChecked){
//                }
                if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].recordid == -1) {
                    loadPrevData = false
                }
                if (loadPrevData) {
                    binding.waiveVisitationCheckBox.isChecked = PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation
                    waiveVisitationCBPreviousValue = binding.waiveVisitationCheckBox.isChecked
                    binding.emailPdfCheckBox.isChecked = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf
                    emailPdfCBPreviousValue = binding.emailPdfCheckBox.isChecked
                    binding.waiverCommentsEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments)
                    waiverCommentsPreviousValue = binding.waiverCommentsEditText.text.toString()


                    if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.isNullOrEmpty()) {
                        binding.visitationMethodDropListId.setSelection(0)
                        visitMethodPreviousValue = ""
                    } else {
                        if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("0") && !PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("") && !PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("None")) {
                            var vMethood = TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeID.toString().equals(PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod) }[0].TypeName
//                        visitationMethodDropListId.setSelection((resources.getStringArray(R.array.visitation_methods)).indexOf(vMethood))
                            binding.visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(vMethood))
                            visitMethodPreviousValue = binding.visitationMethodDropListId.selectedItem.toString()
                            binding.visitationMethodDropListId.tag = binding.visitationMethodDropListId.selectedItemPosition
                        } else {
                            binding.visitationMethodDropListId.setSelection(0)
                            visitMethodPreviousValue = ""
                        }
                    }
                    binding.visitationCommentsEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                    binding.emailEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto)
                    emailEditTextPreviousValue = binding.emailEditText.text.toString()
                    binding.facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep))
                    binding.facilityRepresentativesSpinner.tag = binding.facilityRepresentativesSpinner.selectedItemPosition
                    Log.v("TAG -->", binding.facilityRepresentativesSpinner.tag.toString())
                    // get Rep Signature
//                    var requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
//                    var imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_RepSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
//                    Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            //TODO: something on exception
//                            Log.v("SIGNATURE ---->", "Failed  "+e?.message.toString())
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
//                            Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
//                            facilityRepresentativeSignatureButton.text = "Edit Signature"
//                            return false
//                        }
//                    }).into(facilityRepresentativeSignatureImageView);
////                FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = facilityRepresentativeSignatureImageView.drawable.toBitmap()
//                    imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_SpecSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
//                    Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            //TODO: something on exception
//                            Log.v("SIGNATURE ---->", "Failed  "+e?.message.toString())
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
//                            Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
//                            automotiveSpecialistSignatureButton.text = "Edit Signature"
//                            return false
//                        }
//                    }).into(automotiveSpecialistSignatureImageView);
////                FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = automotiveSpecialistSignatureImageView.drawable.toBitmap()
//                    imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_WSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
//                    Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            //TODO: something on exception
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
//                            Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
//                            waiversSignatureButton.text = "Edit Signature"
//                            return false
//                        }
//                    }).into(waiversSignatureImageView);
//
//                    imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype + "_DefSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
//                    Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            //TODO: something on exception
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
//                            Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
//                            facilityRepresentativeDeficienciesSignatureButton.text = "Edit Signature"
//                            return false
//                        }
//                    }).into(facilityRepresentativeDeficienciesSignatureImageView)

                }

                if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid.isNullOrEmpty()) {

                } else {
                    if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid.equals("0") && loadPrevData) {
                        binding.staffTrainingProcessEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].stafftraining)
                        staffTrainingProcessPreviousValue = binding.staffTrainingProcessEditText.text.toString()
                        binding.qualityControlProcessEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].qualitycontrol)
                        qualityControlProcessPreviousValue = binding.qualityControlProcessEditText.text.toString()
                        binding.aarSignEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].aarsigns)
                        aarSignPreviousValue = binding.aarSignEditText.text.toString()
                        binding.certificateOfApprovalEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].certificateofapproval)
                        certificateOfApprovalPreviousValue = binding.certificateOfApprovalEditText.text.toString()
                        binding.memberBenefitsPosterEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].memberbenefitposter)
                        memberBenefitsPosterPreviousValue = binding.memberBenefitsPosterEditText.text.toString()
                        visitationComments = binding.visitationCommentsEditText.text.toString()
                    }
                }
            } else if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                binding.waiveVisitationCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations
                waiveVisitationCBPreviousValue = binding.waiveVisitationCheckBox.isChecked
                binding.emailPdfCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility
                emailPdfCBPreviousValue = binding.emailPdfCheckBox.isChecked
                binding.waiverCommentsEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments)
                waiverCommentsPreviousValue = binding.waiverCommentsEditText.text.toString()
                if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
                    binding.emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
                }
                emailEditTextPreviousValue = binding.emailEditText.text.toString()
                binding.staffTrainingProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining)
                staffTrainingProcessPreviousValue = binding.staffTrainingProcessEditText.text.toString()
                binding.qualityControlProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl)
                qualityControlProcessPreviousValue = binding.qualityControlProcessEditText.text.toString()
                binding.aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)
                aarSignPreviousValue = binding.aarSignEditText.text.toString()
                binding.certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)
                certificateOfApprovalPreviousValue = binding.certificateOfApprovalEditText.text.toString()
                binding.memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)
                memberBenefitsPosterPreviousValue = binding.memberBenefitsPosterEditText.text.toString()
            }

            if (binding.waiveVisitationCheckBox.isChecked) {
                binding.completeButton.isEnabled = true
                binding.facilityRepresentativeSignatureButton.isEnabled = true
            }
        }
        var visitationType = "";
        if (binding.annualVisitationType.isChecked) {
            visitationType = VisitationTypes.Annual.toString()
        } else if (binding.quarterlyVisitationType.isChecked) {
            visitationType = VisitationTypes.Quarterly.toString()
        } else if (binding.adhocVisitationType.isChecked) {
            visitationType = VisitationTypes.AdHoc.toString()
        } else if (binding.defVisitationType.isChecked) {
            visitationType = VisitationTypes.Deficiency.toString()
        }

        var requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        var imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_RepSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                Log.v("SIGNATURE ---->", "Failed  " + e?.message.toString())
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                binding.facilityRepresentativeSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(binding.facilityRepresentativeSignatureImageView);

        imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_SpecSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                Log.v("SIGNATURE ---->", "Failed  " + e?.message.toString())
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                binding.automotiveSpecialistSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(binding.automotiveSpecialistSignatureImageView);
//                FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = automotiveSpecialistSignatureImageView.drawable.toBitmap()
        imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_WSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                Log.v("SIGNATURE ---->", "Failed  " + e?.message.toString())
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                binding.waiversSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(binding.waiversSignatureImageView);

        imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_DefSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                binding.facilityRepresentativeDeficienciesSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(binding.facilityRepresentativeDeficienciesSignatureImageView)

        emailValidation()
        waiverValidation()
        fillDeficiencyTable()
        setFieldsListeners()
    }

    private fun setFieldsListeners() {

        binding.completeButton.setOnClickListener {
            Bugfender.i("VisitationProcess", "Complete Button Clicked")
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                binding.completeButton.isEnabled = false
                FirebaseCrashlytics.getInstance().log("Visitation Screen Complete Btn Clicked")
                if (validateInputs()) {
                    Bugfender.i("VisitationProcess", "Inputs Validated")
                    if (duplicateCheckPassed()) {
                        Bugfender.i("VisitationProcess", "Duplicate Check Passed")
//                    visitation_sv.go(0, true);
                        submitVisitationDataUpdated()
//                    Constants.visitationIDForPDF = "76497";
//                    (activity as FormsActivity).generateAndOpenPDF();
                    } else {
                        Utility.showValidationAlertDialog(
                            activity,
                            "Duplicate visitation exists with Visitation ID: $duplicateVisitationID"
                        )
                        binding.completeButton.isEnabled = true

                    }
                } else {
                    Utility.showValidationAlertDialog(activity, "Please fill all required fields")
                    binding.completeButton.isEnabled = true
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }


        binding.saveButton.setOnClickListener {
            if ((requireActivity() as FormsActivity).isNetworkAvailable) {
                FirebaseCrashlytics.getInstance().log("Visitation Screen Save Button Clicked")
//            if (!emailEditText.text.isEmpty() && !emailFormatValidation(emailEditText.text.toString())) {
                if (false) {
                    binding.emailEditText.setError("please type your email correctly")
                } else {
                    binding.emailEditText.setError(null)
                    var visitationID = 0
                    val facilityNo =
                        FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                    val clubCode = FacilityDataModel.getInstance().clubCode
                    val insertDate = Date().toApiSubmitFormat()
                    val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                    val updateDate = Date().toApiSubmitFormat()
                    val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                    val facilityRep = binding.facilityRepresentativesSpinner.selectedItem.toString()
                    val automotiveSpecialist =
                        if (binding.automotiveSpecialistSpinner.selectedItem.toString()
                                .contains("Select")
                        ) "" else binding.automotiveSpecialistSpinner.selectedItem.toString()
                    val aarSign =
                        if (binding.aarSignEditText.text.isNullOrEmpty()) "" else binding.aarSignEditText.text
                    val qa = if (binding.qualityControlProcessEditText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.qualityControlProcessEditText.text.toString()
                    val staffTraining = if (binding.staffTrainingProcessEditText.text.toString()
                            .isNullOrEmpty()
                    ) "" else binding.staffTrainingProcessEditText.text
                    val memberBenefits =
                        if (binding.memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else binding.memberBenefitsPosterEditText.text
                    val certificateOfApproval =
                        if (binding.certificateOfApprovalEditText.text.isNullOrEmpty()) "" else binding.certificateOfApprovalEditText.text
                    var visitationType = ""
//                val visitmethod = if (visitationMethodDropListId.selectedItemPosition==0) 0 else visitationMethodDropListId.selectedItemPosition+1
                    val visitmethod =
                        if (binding.visitationMethodDropListId.selectedItemPosition == 0) 0 else TypeTablesModel.getInstance().VisitationMethodType.filter { s ->
                            s.TypeName.equals(binding.visitationMethodDropListId.selectedItem.toString())
                        }[0].TypeID
                    val visitmethodStr =
                        if (binding.visitationMethodDropListId.selectedItemPosition == 0) "" else binding.visitationMethodDropListId.selectedItem.toString()
                    if (binding.annualVisitationType.isChecked) {
                        visitationType = VisitationTypes.Annual.toString()
                    } else if (binding.quarterlyVisitationType.isChecked) {
                        visitationType = VisitationTypes.Quarterly.toString()
                    } else if (binding.adhocVisitationType.isChecked) {
                        visitationType = VisitationTypes.AdHoc.toString()
                    } else if (binding.defVisitationType.isChecked) {
                        visitationType = VisitationTypes.Deficiency.toString()
                    }
                    binding.progressBarTextVal.text = "Saving ..."
                    if ((activity as FormsActivity).imageSpecSignature != null)
                        saveBmpAsFile(
                            (activity as FormsActivity).imageSpecSignature,
                            "Spec",
                            visitationType
                        )
                    if ((activity as FormsActivity).imageRepSignature != null)
                        saveBmpAsFile(
                            (activity as FormsActivity).imageRepSignature,
                            "Rep",
                            visitationType
                        )
                    if ((activity as FormsActivity).imageDefSignature != null)
                        saveBmpAsFile(
                            (activity as FormsActivity).imageDefSignature,
                            "Def",
                            visitationType
                        )
                    if ((activity as FormsActivity).imageWaiveSignature != null)
                        saveBmpAsFile(
                            (activity as FormsActivity).imageWaiveSignature,
                            "W",
                            visitationType
                        )
                    // SUSPECTED RELATED TO MISSING SIGNATURE FROM PDF
//                (activity as FormsActivity).imageSpecSignature = null
//                (activity as FormsActivity).imageRepSignature = null
//                (activity as FormsActivity).imageDefSignature = null
//                (activity as FormsActivity).imageWaiveSignature = null
                    val vcomments = URLEncoder.encode(binding.visitationCommentsEditText.text.toString(), "UTF-8");
                    binding.dialogueLoadingView.visibility = View.VISIBLE
                    var urlString =
                        facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(
                            activity
                        ).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationType.toString() + "&visitationReason=" + binding.visitationReasonDropListId.selectedItem.toString() + "&emailPDF=" + (if (binding.emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + binding.emailEditText.text + "&waiveVisitation=" + (if (binding.waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + binding.waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=" + visitmethod + "&visitMethodStr=" + visitmethodStr + "&comments=" + vcomments
                    Log.v(
                        "SAVE VISITATION || ",
                        Constants.UpdateVisitationDetailsDataProgress + urlString + Utility.getLoggingParameters(
                            activity,
                            0,
                            "Visitation Saved ... Type --> " + visitationType
                        )
                    )
                    Volley.newRequestQueue(context).add(
                        StringRequest(Request.Method.GET,
                            Constants.UpdateVisitationDetailsDataProgress + urlString + Utility.getLoggingParameters(
                                activity,
                                0,
                                "Visitation Saved ... Type --> " + visitationType
                            ),
                            Response.Listener { response ->
                                requireActivity().runOnUiThread {
                                    Log.v("VT RESPONSE ||| ", response.toString())
                                    if (response.toString().contains("Success", false)) {
                                        FirebaseCrashlytics.getInstance()
                                            .log("Visitation Screen Data Saved Successfully")
                                        waiveVisitationCBPreviousValue =
                                            binding.waiveVisitationCheckBox.isChecked
                                        emailPdfCBPreviousValue = binding.emailPdfCheckBox.isChecked
                                        waiverCommentsPreviousValue =
                                            binding.waiverCommentsEditText.text.toString()
                                        emailEditTextPreviousValue =
                                            binding.emailEditText.text.toString()
                                        staffTrainingProcessPreviousValue =
                                            binding.staffTrainingProcessEditText.text.toString()
                                        qualityControlProcessPreviousValue =
                                            binding.qualityControlProcessEditText.text.toString()
                                        aarSignPreviousValue =
                                            binding.aarSignEditText.text.toString()
                                        certificateOfApprovalPreviousValue =
                                            binding.certificateOfApprovalEditText.text.toString()
                                        memberBenefitsPosterPreviousValue =
                                            binding.memberBenefitsPosterEditText.text.toString()
                                        visitationComments =
                                            binding.visitationCommentsEditText.text.toString()
                                        visitMethodPreviousValue =
                                            binding.visitationMethodDropListId.selectedItem.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].recordid = 9999999
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation =
                                            binding.waiveVisitationCheckBox.isChecked
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf =
                                            binding.emailPdfCheckBox.isChecked
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments =
                                            binding.waiverCommentsEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto =
                                            binding.emailEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].stafftraining =
                                            binding.staffTrainingProcessEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].qualitycontrol =
                                            binding.qualityControlProcessEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].aarsigns =
                                            binding.aarSignEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].certificateofapproval =
                                            binding.certificateOfApprovalEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].memberbenefitposter =
                                            binding.memberBenefitsPosterEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments =
                                            binding.visitationCommentsEditText.text.toString()
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep =
                                            binding.facilityRepresentativesSpinner.selectedItem.toString()
                                        if (!TypeTablesModel.getInstance().VisitationMethodType.filter { s ->
                                                s.TypeName.equals(
                                                    binding.visitationMethodDropListId.selectedItem.toString()
                                                )
                                            }.isNullOrEmpty()) {
                                            PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod =
                                                TypeTablesModel.getInstance().VisitationMethodType.filter { s ->
                                                    s.TypeName.equals(binding.visitationMethodDropListId.selectedItem.toString())
                                                }[0].TypeID.toString()
                                        }
                                        (activity as FormsActivity).saveRequired = false
//                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                        (activity as FormsActivity).saveDone = true
                                        refreshButtonsState()
//                                        Utility.showMessageDialog(
//                                            activity,
//                                            "Confirmation ...",
//                                            "Visitation Data Saved Successfully"
//                                        )
                                        Utility.showUnifiedConfirmationDialog(activity,"Visitation Data Saved Successfully")
                                        (activity as FormsActivity).saveVisitedScreensRequired =
                                            false
//                                    IndicatorsDataModel.getInstance().resetAllVisitedFlags()
                                        binding.cancelButton.isEnabled = false
                                        binding.dialogueLoadingView.visibility = View.GONE
                                        binding.progressBarTextVal.text = "Loading ..."
                                    } else {
                                        binding.dialogueLoadingView.visibility = View.GONE
                                        binding.progressBarTextVal.text = "Loading ..."
                                        Utility.showSubmitAlertDialog(
                                            activity,
                                            false,
                                            "Error saving Visitation Details"
                                        )
                                        FirebaseCrashlytics.getInstance()
                                            .log("Visitation Screen Error while saving Data -> " + response.toString())
                                    }
                                }
                            },
                            Response.ErrorListener {
                                binding.dialogueLoadingView.visibility = View.GONE
                                binding.progressBarTextVal.text = "Loading ..."
                                Utility.showSubmitAlertDialog(
                                    activity,
                                    false,
                                    "Error saving Visitation Details (Error: " + it.message + " )"
                                )
                                FirebaseCrashlytics.getInstance()
                                    .log("Visitation Screen Error while saving Data -> " + it.message)
                            })
                    )
                }
            } else {
                Utility.showInternetWarningDialog(requireContext(),(requireActivity() as FormsActivity).networkStatusErrorMsg)
            }
        }




        binding.emailPdfCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility = b
            checkMarkChangesDone()
        }

        binding.facilityRepresentativesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if (p2 > 0) {
                    if (isFacilityRepresentativeSignatureInitialized) {
                        isFacilityRepresentativeSignatureInitialized = false
                    } else {//if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals(facilitySpecialistNames[p2])) {
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = facilitySpecialistNames[p2]
                        FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null
                        binding.automotiveSpecialistSignatureImageView.setImageBitmap(null)
                    }
                } else {
                    if (isFacilityRepresentativeSignatureInitialized) {
                        FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = ""
                    }
                }
                Log.v("SAVEREQUIRED -->", binding.facilityRepresentativesSpinner.tag.toString())
                if (p2 > 0 && (!binding.facilityRepresentativesSpinner.tag.equals(p2) || binding.facilityRepresentativesSpinner.tag.equals("-1"))) {
                    binding.facilityRepresentativesSpinner.tag = "-1"
                    if (isInitialized[1]) {//firsLoadDone
                        (activity as FormsActivity).saveRequired = true
                        refreshButtonsState()
                    } else {

                    }
                }
                isInitialized[1] = true
                checkMarkChangesDone()
            }

        }

//            Moved after set Automative Specialist Spinner as this was causing the issue of need to cancel before move
        binding.automotiveSpecialistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Adding condition as a workaround not to lost the applied changes for signature. As this method is called also during adapter initialization

                if (p2 > 0) {
                    if (isAutomotiveSpecialistSignatureInitialized) {
                        isAutomotiveSpecialistSignatureInitialized = false
                    } else {
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = facilitySpecialistNames[p2]
//                            FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = null
//                            automotiveSpecialistSignatureImageView.setImageBitmap(null)

                    }
                } else {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = ""
                }

                if (!binding.automotiveSpecialistSpinner.tag.equals(p2) || binding.automotiveSpecialistSpinner.tag.equals("-1")) {
                    binding.automotiveSpecialistSpinner.tag = "-1"
                    if (isInitialized[0]) {//firsLoadDone
                        (activity as FormsActivity).saveRequired = true
                        Log.v("SAVEREQUIRED -->", " automotiveSpecialistSpinner")
                        refreshButtonsState()
                    } else {

                    }
                }
                isInitialized[0] = true
                checkMarkChangesDone()
            }

        }


        binding.signatureLoadButton.setOnClickListener {

            val imageBase64 = getBase64ImageIfValid(requireContext(), FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_signature")
            if (imageBase64 != null) {
                val bitmap = decodeBase64ToBitmap(imageBase64)
                binding.signatureInkView.drawBitmap(bitmap,0.0f,
                    0.0f,
                    Paint())
            }

        }


        binding.facilityRepresentativeSignatureButton.setOnClickListener {
            try {
                binding.signatureDialog.visibility = View.VISIBLE
                binding.signatureLoadButton.visibility = View.VISIBLE
                binding.visitationFormAlphaBackground.visibility = View.VISIBLE
                selectedSignature = requestedSignature.representative
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature != null)
//                    binding.signatureInkView.drawBitmap(
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature,
//                        0.0f,
//                        0.0f,
//                        Paint()
//                    )
            } catch (e: Exception) {
                Log.v("ERROR -->", e.message.toString())
            }
        }

        binding.automotiveSpecialistSignatureButton.setOnClickListener {
            binding.signatureDialog.visibility = View.VISIBLE
            binding.signatureLoadButton.visibility = View.GONE
            binding.visitationFormAlphaBackground.visibility = View.VISIBLE
            try {
                selectedSignature = requestedSignature.specialist
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature != null) {
//                    try {
//                        binding.signatureInkView.drawBitmap(
//                            FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature,
//                            0.0f,
//                            0.0f,
//                            Paint()
//                        )
//                    } catch (e: Exception) {
//
//                    }
//                }
            } catch (e: Exception) {
                Log.v("ERROR -->", e.message.toString())
            }
        }

        binding.facilityRepresentativeDeficienciesSignatureButton.setOnClickListener {
            binding.signatureDialog.visibility = View.VISIBLE
            binding.signatureLoadButton.visibility = View.GONE
            binding.visitationFormAlphaBackground.visibility = View.VISIBLE
            try {
                selectedSignature = requestedSignature.representativeDeficiency
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature != null) {
//                    binding.signatureInkView.drawBitmap(
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature,
//                        0.0f,
//                        0.0f,
//                        Paint()
//                    )
//                }
            } catch (e: Exception) {
                Log.v("ERROR -->", e.message.toString())
            }
        }

        binding.waiversSignatureButton.setOnClickListener {
            binding.signatureDialog.visibility = View.VISIBLE
            binding.signatureLoadButton.visibility = View.GONE
            binding.visitationFormAlphaBackground.visibility = View.VISIBLE
            try {
                selectedSignature = requestedSignature.waiver
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature != null) {
//                    binding.signatureInkView.drawBitmap(
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature,
//                        0.0f,
//                        0.0f,
//                        Paint()
//                    )
//                }
            } catch (e: Exception) {
                Log.v("ERROR -->", e.message.toString())
            }
        }

        binding.signatureClearButton.setOnClickListener {

            binding.signatureInkView.clear()
            checkMarkChangesDone()
        }

        binding.signatureCancelButton.setOnClickListener {
            binding.signatureInkView.clear()
            binding.visitationFormAlphaBackground.visibility = View.GONE
            binding.signatureDialog.visibility = View.GONE
            checkMarkChangesDone()
        }

        binding.signatureConfirmButton.setOnClickListener {

            var bitmap = binding.signatureInkView.bitmap
            var isEmpty = bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config))
            when (selectedSignature) {
                requestedSignature.representative -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = bitmap
                    (activity as FormsActivity).imageRepSignature = bitmap
                    saveBase64Image(requireContext(),FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_signature",bitmapToBase64(bitmap))
//                    saveBmpAsFile(bitmap,"Rep")
                    if (!isEmpty) {
                        binding.facilityRepresentativeSignatureButton.text = "Edit Signature"
                        binding.facilityRepresentativeSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        binding.facilityRepresentativeSignatureButton.text = "Add Signature"
                        binding.facilityRepresentativeSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = null

                    }
                }

                requestedSignature.specialist -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = bitmap
                    (activity as FormsActivity).imageSpecSignature = bitmap
//                    saveBmpAsFile(bitmap,"Spec")
                    if (!isEmpty) {
                        binding.automotiveSpecialistSignatureButton.text = "Edit Signature"
                        binding.automotiveSpecialistSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        binding.automotiveSpecialistSignatureButton.text = "Add Signature"
                        binding.automotiveSpecialistSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null

                    }
                }

                requestedSignature.representativeDeficiency -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature = bitmap
                    (activity as FormsActivity).imageDefSignature = bitmap
//                    saveBmpAsFile(bitmap,"Def")
                    if (!isEmpty) {
                        binding.facilityRepresentativeDeficienciesSignatureButton.text = "Edit Signature"
                        binding.facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        binding.facilityRepresentativeDeficienciesSignatureButton.text = "Add Signature"
                        binding.facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature = null

                    }

                }

                requestedSignature.waiver -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature = bitmap
                    (activity as FormsActivity).imageWaiveSignature = bitmap
                    if (!isEmpty) {
                        binding.waiversSignatureButton.text = "Edit Signature"
                        binding.waiversSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        binding.waiversSignatureButton.text = "Add Signature"
                        binding.waiversSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature = null

                    }
                }

                else -> {}
            }
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
            binding.signatureInkView.clear()
            binding.visitationFormAlphaBackground.visibility = View.GONE
            binding.signatureDialog.visibility = View.GONE
            checkMarkChangesDone()
        }

        binding.waiveVisitationCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations = b
            if (b) {
                binding.completeButton.isEnabled = true
                binding.facilityRepresentativeSignatureButton.isEnabled = true
            }
            else {
                binding.waiverCommentsEditText.setText("")
                var visitationType = ""
                if (binding.annualVisitationType.isChecked) {
                    visitationType = VisitationTypes.Annual.toString()
                } else if (binding.quarterlyVisitationType.isChecked) {
                    visitationType = VisitationTypes.Quarterly.toString()
                } else if (binding.adhocVisitationType.isChecked) {
                    visitationType = VisitationTypes.AdHoc.toString()
                } else if (binding.defVisitationType.isChecked) {
                    visitationType = VisitationTypes.Deficiency.toString()
                }
                if (visitationType.equals(VisitationTypes.AdHoc) || visitationType.equals(VisitationTypes.Deficiency)) {
                    binding.completeButton.isEnabled = true
                    binding.facilityRepresentativeSignatureButton.isEnabled = true
                } else {
                    binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                    binding.facilityRepresentativeSignatureButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                }
            }
            checkMarkChangesDone()
        }

        binding.aarSignEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns = p0.toString()
                if (isInitialized[2]) {// firsLoadDone
                    if (binding.aarSignEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.aarSignEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " aarSignEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[2] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        binding.certificateOfApprovalEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval = p0.toString()
                if (isInitialized[3]) {//firsLoadDone
                    if (binding.certificateOfApprovalEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.certificateOfApprovalEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " certificateOfApprovalEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[3] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        binding.qualityControlProcessEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl = p0.toString()
                if (isInitialized[4]) {//firsLoadDone
                    if (binding.qualityControlProcessEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.qualityControlProcessEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " qualityControlProcessEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[4] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        binding.staffTrainingProcessEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining = p0.toString()
                if (isInitialized[5]) {//firsLoadDone
                    if (binding.staffTrainingProcessEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.staffTrainingProcessEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " staffTrainingProcessEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[5] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        binding.memberBenefitsPosterEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster = p0.toString()
                if (isInitialized[6]) {//firsLoadDone
                    if (binding.memberBenefitsPosterEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.memberBenefitsPosterEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " memberBenefitsPosterEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[6] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        binding.waiverCommentsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments = p0.toString()
                if (isInitialized[7]) {//firsLoadDone
                    if (binding.waiverCommentsEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.waiverCommentsEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " waiverCommentsEditText")
                    binding.memberBenefitsPosterEditText
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[7] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                FacilityDataModel.getInstance().tblFacilityEmail[0].email = p0.toString()

                if (isInitialized[8]) {// firsLoadDone
                    if (binding.emailEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    binding.emailEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " emailEditText")
                    refreshButtonsState()
//                PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf =
                    checkMarkChangesDone()
                }
                else {

                }
                isInitialized[8] = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })


        binding.visitationMethodDropListId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (binding.visitationMethodDropListId.selectedItem.equals("In Person")) {
                    binding.signatureView.visibility = View.VISIBLE
                    binding.signatureRL.visibility = View.VISIBLE
                } else {
                    binding.signatureView.visibility = View.GONE
                    binding.signatureRL.visibility = View.GONE
                }
                if (!binding.visitationMethodDropListId.tag.equals(position) || binding.visitationMethodDropListId.tag.equals("-1")) {
                    if (isInitialized[9]) { // firsLoadDone
                        if (!binding.visitationMethodDropListId.tag.equals("-2"))
                            (activity as FormsActivity).saveRequired = true
                        binding.visitationMethodDropListId.tag = "-1"
                        Log.v("SAVEREQUIRED -->", " visitationMethodDropListId")
                        refreshButtonsState()
                    }
//                    else {
//                        isInitialized[9] = true
//                    }
                }
                isInitialized[9] = true
            }
        }

        binding.adhocVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.quarterlyVisitationType.isChecked = false
                binding.annualVisitationType.isChecked = false
                binding.defVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.AdHoc
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                binding.facilityRepresentativeSignatureButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }

        }

        binding.defVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.quarterlyVisitationType.isChecked = false
                binding.annualVisitationType.isChecked = false
                binding.adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Deficiency
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                binding.facilityRepresentativeSignatureButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        binding.annualVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.quarterlyVisitationType.isChecked = false
                binding.defVisitationType.isChecked = false
                binding.adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Annual
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                binding.facilityRepresentativeSignatureButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        binding.quarterlyVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.annualVisitationType.isChecked = false
                binding.defVisitationType.isChecked = false
                binding.adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Quarterly
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                binding.completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                binding.facilityRepresentativeSignatureButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        (activity as FormsActivity).saveRequired = false
        firsLoadDone = true
    }

    fun facilityNameAndNumberRelationForSelection() {

        for (fac in FacilityDataModel.getInstance().tblFacilities) {

            if (fac.FACNo.toString() == binding.facilityNumberEditText.text.toString()) {
                binding.facilityNumberEditText.isEnabled = false

                binding.facilityNameEditText.setText(fac.BusinessName.toString())
                //facilityNameEditText.setText(fac. .toString())


            }
            if (fac.BusinessName == binding.facilityNameEditText.text.toString()) {

                binding.facilityNameEditText.isEnabled = false

                binding.facilityNumberEditText.setText(fac.FACNo.toString())
            }

        }

        var facNameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (binding.facilityNumberEditText.text.toString().isNullOrEmpty()) {

                    for (fac in FacilityDataModel.getInstance().tblFacilities) {


                        if (fac.BusinessName == binding.facilityNameEditText.text.toString()) {

                            binding.facilityNumberEditText.setText(fac.FACNo.toString())
                        }
                    }
                }
            }
        }
        var facNumberWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                if (binding.facilityNameEditText.text.toString().isNullOrEmpty()) {

                    for (fac in FacilityDataModel.getInstance().tblFacilities) {

                        if (fac.FACNo.toString() == binding.facilityNumberEditText.text.toString()) {

                            binding.facilityNameEditText.setText(fac.BusinessName.toString())


                        }
                    }


                }
            }
        }




        binding.facilityNameEditText.addTextChangedListener(facNameWatcher)
        binding.facilityNumberEditText.addTextChangedListener(facNumberWatcher)

        representativeSignatureConditionedEnabling()

    }


    fun representativeSignatureConditionedEnabling() {

        if (binding.facilityRepresentativesSpinner.selectedItem.toString().isNullOrEmpty() ||
            binding.facilityRepresentativesSpinner.selectedItem.toString().contains("please") ||
            binding.visitationReasonDropListId.selectedItem.toString().isNullOrEmpty() ||
            binding.visitationReasonDropListId.selectedItem.toString().contains("please")) {

            //TODO We should change the background color of the button if the button is NOT enabled
            //facilityRepresentativeSignatureButton.isEnabled=false
        }
        var representativeNameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                binding.facilityRepresentativeSignatureButton.setText("Add Signature")
            }
        }
    }


    fun saveBmpAsFile(bmp: Bitmap?, type: String, visitationType: String) {
        binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
        steps[0].status = "In Progress"
        recyclerView?.adapter?.notifyItemChanged(0)
        var strPrefix = if (type.equals("Rep")) "RepSignature" else if (type.equals("Spec")) "SpecSignature" else if (type.equals("W")) "WSignature" else "DefSignature"
        var statusPrefix = if (type.equals("Rep")) "Representative Signature" else if (type.equals("Spec")) "Specialist Signature" else if (type.equals("W")) "Waiver Signature" else "Deficiency Signature"
//        steps[0].comments += "${statusPrefix}"
        strPrefix += "_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString();
        try {
//            throw Exception("Intentional Exception")
            var fileName =
                FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_" + strPrefix + ".png"
            Bugfender.i("VisitationProcess", "Save Signature Started: $fileName")
            when (type) {
                "Rep" -> repSignatureName = fileName.lowercase()
                "Spec" -> specSignatureName = fileName.lowercase()
                "W" -> waiveSignatureName = fileName.lowercase()
                else -> defSignatureName = fileName.lowercase()
            }
            var filePath = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                filePath =
                    context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
//            filePath = Environment.DIRECTORY_DCIM + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
            } else {
                filePath =
                    Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
            }
//        val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png")
            val file = File(filePath)
            val fOut = FileOutputStream(file);
//        bmp.toDrawable(resources).bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            bmp?.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
//        uploadSignature(file, fileName)
            uploadSignatureWithOkHttp(file, fileName) {
                Log.v("Upload Signature", it.toString())
                requireActivity().runOnUiThread {
                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                    if (it.toString().contains("Error", false)) {
                        Bugfender.i("VisitationProcess", "Save Signature Failed => ${it.toString()}")
                        steps[0].status = "Failed"
                        steps[0].comments += "${statusPrefix} - ${it}"
                        binding.stepsRecyclerView.adapter?.notifyItemChanged(0)
//                    Utility.showMessageDialog(context, "Error", "Uploading File Failed with error (" + it + ")")
                        imgCounter--
                    } else {
//                    submitPhotoDetails()
//                        Bugfender.i("SaveBMP", type + " - Done")
                        Bugfender.i("VisitationProcess", "Save Signature Completed")
                        steps[0].comments += "Saving ${statusPrefix} Done - "
                        binding.stepsRecyclerView.adapter?.notifyItemChanged(0)
                        imgCounter--
                    }
                }
            }
        } catch (e: Exception) {
            binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
            steps[0].status = "Failed"
            steps[0].comments += "${statusPrefix} - ${e.message}"
            Bugfender.i("VisitationProcess", "Save Signature Failed => ${e.message}")
            binding.visitationStatusBtn.isEnabled = true
            binding.stepsRecyclerView.adapter?.notifyItemChanged(0)
            imgCounter--
        }
//        visitation_sv.done(true)
    }

//    fun uploadSignature(file: File, fileName: String) {
//        val multipartRequest = MultipartRequest(Constants.uploadPhoto + fileName, null, file, Response.Listener { response ->
//            //            try {
////                submitPhotoDetails()
////            } catch (e: UnsupportedEncodingException) {
////                e.printStackTrace()
////            }
//        }, Response.ErrorListener {
//            Utility.showMessageDialog(context, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
//            Log.v("Upload Signature Error:", it.message.toString())
//        })
//        val socketTimeout = 30000//30 seconds - change to what you want
//        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//        multipartRequest.retryPolicy = policy
//        Volley.newRequestQueue((activity as FormsActivity).applicationContext).add(multipartRequest)
//    }

    fun uploadSignatureWithOkHttp(file: File,fileName: String, callback: (String?) -> Unit) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/png".toMediaTypeOrNull(), file))
            .build()

        val request = okhttp3.Request.Builder()
            .url(Constants.uploadPhoto + fileName)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Upload failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())
                } else {
                    callback("Upload failed: ${response.body}")
                }
            }
        })
    }


    fun getVisitationChanges(): String {
        var strChanges = ""
//        if (FacilityDataModel.getInstance().tblVisitationTracking[0].FacilityRepairOrderCount != FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount) {
//            strChanges += "Repair order count changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].FacilityRepairOrderCount + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].FacilityRepairOrderCount+") - "
//        }
//        if (FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName != FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName) {
//            strChanges += "Time Zone changed from (" + FacilityDataModelOrg.getInstance().tblTimezoneType[0].TimezoneName + ") to ("+FacilityDataModel.getInstance().tblTimezoneType[0].TimezoneName+") - "
//        }
//        if (FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability != FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability) {
//            strChanges += "Service Availability changed from (" + TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s->s.SrvAvaID.equals(FacilityDataModelOrg.getInstance().tblFacilities[0].SvcAvailability)}[0].SrvAvaName + ") to ("+TypeTablesModel.getInstance().ServiceAvailabilityType.filter { s->s.SrvAvaID.equals(FacilityDataModel.getInstance().tblFacilities[0].SvcAvailability)}[0].SrvAvaName+") - "
//        }
//        if (FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate != FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate) {
//            strChanges += "ARD Expiration date changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormatMMDDYYYY() + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].AutomotiveRepairExpDate+") - "
//        }
//        if (FacilityDataModel.getInstance().tblFacilities[0].WebSite != FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite) {
//            strChanges += "Website URL changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].WebSite + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].WebSite+") - "
//        }
//        if (FacilityDataModel.getInstance().tblFacilities[0].InternetAccess != FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess) {
//            strChanges += "Wi-Fi Availability changed from (" + FacilityDataModelOrg.getInstance().tblFacilities[0].InternetAccess + ") to ("+FacilityDataModel.getInstance().tblFacilities[0].InternetAccess+") - "
//        }
//        if (FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName != FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName) {
//            strChanges += "Facility Type changed from (" + FacilityDataModelOrg.getInstance().tblFacilityType[0].FacilityTypeName + ") to ("+FacilityDataModel.getInstance().tblFacilityType[0].FacilityTypeName+") - "
//        }
//        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }


    fun submitVisitationData() {
        binding.dialogueLoadingView.visibility = View.VISIBLE
        binding.visitationStepview.visibility = View.VISIBLE
        binding.visitationStatusBtn.isEnabled = false
        steps[0].status = "In Progress"
        recyclerView?.adapter?.notifyItemChanged(0)
        var visitationID = 0
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID

        val facilityRep = if (binding.facilityRepresentativesSpinner.selectedItem.toString().contains("select")) "" else binding.facilityRepresentativesSpinner.selectedItem.toString()

        val automotiveSpecialist = if (binding.automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else binding.automotiveSpecialistSpinner.selectedItem.toString()
        val aarSign = if (binding.aarSignEditText.text.isNullOrEmpty()) "" else binding.aarSignEditText.text
        val qa = if (binding.qualityControlProcessEditText.text.isNullOrEmpty()) "" else binding.qualityControlProcessEditText.text
        val staffTraining = if (binding.staffTrainingProcessEditText.text.isNullOrEmpty()) "" else binding.staffTrainingProcessEditText.text
        val memberBenefits = if (binding.memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else binding.memberBenefitsPosterEditText.text
        val certificateOfApproval = if (binding.certificateOfApprovalEditText.text.isNullOrEmpty()) "" else binding.certificateOfApprovalEditText.text
        val performedBy = if (binding.automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else binding.automotiveSpecialistSpinner.selectedItem.toString()

        val visitmethodStr = if (binding.visitationMethodDropListId.selectedItemPosition == 0) "" else binding.visitationMethodDropListId.selectedItem.toString()

        val visitmethod = if (binding.visitationMethodDropListId.selectedItemPosition == 0) 0 else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitmethodStr) }[0].TypeID

        val annualvisitationmonth = FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth
        var dialogMsg = ""
        var visitationType = ""
        var visitationTypeID = ""
        var visitationReasonID = ""
        if (binding.annualVisitationType.isChecked) {
            visitationType = VisitationTypes.Annual.toString()
            visitationTypeID = "1"

        } else if (binding.quarterlyVisitationType.isChecked) {
            visitationType = VisitationTypes.Quarterly.toString()
            visitationTypeID = "2"

        } else if (binding.adhocVisitationType.isChecked) {
            visitationType = VisitationTypes.AdHoc.toString()
            visitationTypeID = "3"

        } else if (binding.defVisitationType.isChecked) {
            visitationType = VisitationTypes.Deficiency.toString()
            visitationTypeID = "4"

        }
//        visitation_sv.done(true)
//        visitation_sv.go(1,true)
        imgCounter = 0
        if ((activity as FormsActivity).imageSpecSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageRepSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageDefSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageWaiveSignature != null)
            imgCounter++

        if ((activity as FormsActivity).imageSpecSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageSpecSignature, "Spec", visitationType)
        if ((activity as FormsActivity).imageRepSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageRepSignature, "Rep", visitationType)
        if ((activity as FormsActivity).imageDefSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageDefSignature, "Def", visitationType)
        if ((activity as FormsActivity).imageWaiveSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageWaiveSignature, "W", visitationType)

        val handler = Handler(Looper.getMainLooper())

        val conditionCheck = object : Runnable {
            override fun run() {
                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                if (imgCounter == 0) {
                    // Perform your action
                    Log.d("Condition", "Condition met!")
                    if (!steps[0].status.contains("Failed")) {
                        steps[0].status = "Success"
//                        steps[0].comments = "==> Completed"
                        visitationReasonID = TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeName.equals(binding.visitationReasonDropListId.selectedItem.toString()) }[0].VisitationReasonTypeID.toString()
//                    progressBarTextVal.text = "Saving ..."
                        binding.dialogueLoadingView.visibility = View.VISIBLE
                        steps[1].status = "In Progress"
                        recyclerView?.adapter?.notifyItemChanged(0)
                        recyclerView?.adapter?.notifyItemChanged(1)
                        // Get PRG LOGS to write changes made in PDF
                        binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}&userId=" + ApplicationPrefs.getInstance(context).loggedInUserID,
                            { response ->
                                requireActivity().runOnUiThread {
                                    PRGDataModel.getInstance().tblPRGLogChanges.clear()
                                    (activity as FormsActivity).saveDone = true
                                    if (!response.toString().replace(" ", "").equals("[]")) {
                                        PRGDataModel.getInstance().tblPRGLogChanges = Gson().fromJson(response.toString(), Array<PRGLogChanges>::class.java).toCollection(ArrayList())
                                    } else {
                                        var item = PRGLogChanges()
                                        item.recordid = -1
                                        PRGDataModel.getInstance().tblPRGLogChanges.add(item)
                                    }
                                    steps[1].status = "Success"
                                    steps[2].status = "In Progress"
                                    recyclerView?.adapter?.notifyItemChanged(1)
                                    recyclerView?.adapter?.notifyItemChanged(2)
                                    // Save Visitation
                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                    var urlString = facilityNo + "&clubcode=" + clubCode + "&DatePerformed=" + insertDate + "&DateReceived=" + insertDate + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationTypeID + "&visitationReason=" + visitationReasonID + "&emailPDF=" + (if (binding.emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + binding.emailEditText.text + "&waiveVisitation=" + (if (binding.waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + binding.waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&performedBy=" + automotiveSpecialist + "&visitationID=0&annualVisitationMonth=" + annualvisitationmonth + "&visitMethod=" + visitmethod
                                    Log.v("Visitation Tracking -- ", Constants.UpdateVisitationTrackingData + urlString)
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationTrackingData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                                        Response.Listener { response ->
                                            requireActivity().runOnUiThread {
                                                Log.v("VT RESPONSE ||| ", response.toString())
                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    steps[2].status = "Success"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - VisitationTrackingData - Success")
                                                    visitationID = response.toString().substring(response.toString().indexOf("<visitationID") + 14, response.toString().indexOf("" +
                                                            "</visitationID")).toInt()
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation ID ${visitationID}")
                                                    Constants.visitationIDForPDF = visitationID.toString()
                                                    steps[2].comments = " - Visitation ID (${visitationID}) created successfully"
                                                    steps[3].status = "In Progress"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    Constants.awsReference = Date().toApiAWSFormat() + "_" + visitationType + "_" + binding.visitationReasonDropListId.selectedItem.toString()
                                                    dialogMsg = "New Visitation with ID (${visitationID}) created succesfully"
//                                                    (activity as FormsActivity).saveRequired = false
                                                    urlString = facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationType.toString() + "&visitationReason=" + binding.visitationReasonDropListId.selectedItem.toString() + "&emailPDF=" + (if (binding.emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + binding.emailEditText.text + "&waiveVisitation=" + (if (binding.waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + binding.waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=${visitmethod}&annualVisitationMonth=" + annualvisitationmonth + "&comments=" + binding.visitationCommentsEditText.text.toString()
                                                    Log.v("Visitation Details --- ", Constants.UpdateVisitationDetailsData + urlString)
                                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
//                                                    (activity as FormsActivity).saveRequired = false
//                                                    refreshButtonsState()
//                                                    (activity as FormsActivity).saveVisitedScreensRequired = false
//                                                    (activity as FormsActivity).saveDone = true
                                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                                                        Response.Listener { response ->
                                                            requireActivity().runOnUiThread {
                                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                Log.v("VT RESPONSE ||| ", response.toString())
                                                                if (response.toString().contains("returnCode>0<", false)) {
                                                                    steps[3].status = "Success"
                                                                    steps[3].comments = " - Visitation Details updated successfully"
                                                                    recyclerView?.adapter?.notifyItemChanged(3)
                                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Success")
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].clubcode = clubCode.toInt()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].facid = facilityNo.toInt()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf = binding.emailPdfCheckBox.isChecked
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto = binding.emailEditText.text.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep = facilityRep
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist = automotiveSpecialist
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].sessionid = ApplicationPrefs.getInstance(activity).sessionID
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].userid = insertBy
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason = binding.visitationReasonDropListId.selectedItem.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype = visitationType.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments = binding.waiverCommentsEditText.text.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation = binding.waiveVisitationCheckBox.isChecked
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid = visitationID.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod = visitmethodStr
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments = binding.visitationCommentsEditText.text.toString()
                //                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid

//                                                                    (activity as FormsActivity).saveRequired = false
//                //                                                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
//                                                                    refreshButtonsState()
//                                                                    (activity as FormsActivity).saveVisitedScreensRequired = false
                //                                                IndicatorsDataModel.getInstance().resetAllVisitedFlags()
                                                                    // Disable Buttons after click complete Button
                //                                                if (visitationType.equals(VisitationTypes.AdHoc) || visitationType.equals(VisitationTypes.Deficiency)) {
                //                                                    completeButton.isEnabled = true
                //                                                } else {
                //                                                    completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                //                                                }
                                                                    binding.cancelButton.isEnabled = false

                                                                    if (!binding.waiveVisitationCheckBox.isChecked || binding.waiveVisitationCheckBox.isChecked) {
                                                                        if ((activity as FormsActivity).checkPermission()) {
                                                                            (activity as FormsActivity).generateAndOpenPDF()
                                                                        } else {
                                                                            if (!(activity as FormsActivity).checkPermission()) {
                                                                                (activity as FormsActivity).requestPermissionAndContinue();
                                                                            } else {
                                                                                (activity as FormsActivity).generateAndOpenPDF()
                                                                            }
                                                                        }

                                                                        val handler = Handler(Looper.getMainLooper())

                                                                        val conditionCheck = object : Runnable {
                                                                            override fun run() {
                                                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                                if (visitationProcessCompleted) {
                                                                                    // Perform your action

                                                                                    if (steps[4].status.equals("Success"))
                                                                                        (activity as FormsActivity).onBackPressed()
                                                                                } else {
                                                                                    // Retry after 500ms
                                                                                    if (steps[4].status.equals("Failed") || (!steps[5].status.equals("In Progress") && !steps[6].status.equals("In Progress") && !steps[7].status.equals("In Progress")))
                                                                                        binding.visitationStatusBtn.isEnabled = true
                                                                                    handler.postDelayed(this, 500)
                                                                                }
                                                                            }
                                                                        }
                                                                        handler.post(conditionCheck)

//                                                                        Handler().postDelayed({
//                                                                            progressBarTextVal.text = "Generating Visitation PDFs ..."
//                                                                            Handler().postDelayed({
//                                                                                progressBarTextVal.text = "Sending Visitation PDFs ..."
//                                                                                Handler().postDelayed({
//                                                                                    progressBarTextVal.text = "Finalizing completion process ..."
//                                                                                    Handler().postDelayed({
//                                                                                        dialogueLoadingView.visibility = View.GONE
//                                                                                        progressBarTextVal.text = "Loading ..."
//                                                                                        val builder = AlertDialog.Builder(activity)
//                                                                                        builder.setTitle("Confirmation ...")
//                                                                                        builder.setMessage(dialogMsg)
//                                                                                        builder.setPositiveButton("OK"
//                                                                                        )
//                                                                                        { dialog, id ->
//                                                                                            dialog.dismiss()
//                                                                                            (activity as FormsActivity).onBackPressed()
//                                                                                        }
//                                                                                        builder.show()
//                                                                                    }, 5000)
//                                                                                }, 5000)
//                                                                            }, 5000)
//                                                                        }, 5000)
                                                                    } else {
//                                                                        dialogueLoadingView.visibility = View.GONE
//                                                                        progressBarTextVal.text = "Loading ..."
//                                                                        //                                                                Utility.showMessageDialog(activity,"Confirmation ...", dialogMsg)
//                                                                        val builder = AlertDialog.Builder(activity)
//                                                                        builder.setTitle("Confirmation ...")
//                                                                        builder.setMessage(dialogMsg)
//                                                                        builder.setPositiveButton("OK"
//                                                                        )
//                                                                        { dialog, id ->
//                                                                            dialog.dismiss()
//                                                                            (activity as FormsActivity).onBackPressed()
//                                                                        }
//                                                                        builder.show()
                                                                        binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                        binding.visitationStatusBtn.isEnabled = true
                                                                    }
                                                                } else {
                                                                    binding.dialogueLoadingView.visibility = View.GONE
                                                                    binding.progressBarTextVal.text = "Loading ..."
                                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
//                                                                    Utility.showSubmitAlertDialog(activity, false, dialogMsg + " ..... Visitation Details (Error: " + errorMessage + " )")
                                                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                    steps[3].status = "Failed"
                                                                    steps[3].comments = errorMessage
                                                                    binding.visitationStatusBtn.isEnabled = true
                                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Error: ${errorMessage}")
                                                                }
                                                            }
                                                        }, Response.ErrorListener {
                                                            binding.dialogueLoadingView.visibility = View.GONE
                                                            binding.progressBarTextVal.text = "Loading ..."
                                                            steps[3].status = "Failed"
                                                            steps[3].comments = it.message.toString()
                                                            binding.visitationStatusBtn.isEnabled = true
//                                                            Utility.showSubmitAlertDialog(activity, false, "Visitation Details (Error: " + it.message + " )")
                                                            FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Error: ${it.message}")
                                                        }))
                //                            dialogueLoadingView.visibility = View.GONE
                //                            progressBarTextVal.text = "Loading ..."
                                                } else {

                                                    binding.dialogueLoadingView.visibility = View.GONE
                                                    binding.progressBarTextVal.text = "Loading ..."
                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                    steps[2].status = "Failed"
                                                    steps[2].comments = "Error: ${errorMessage}"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    binding.visitationStatusBtn.isEnabled = true
//                                                    Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + errorMessage + " )")
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Tracking API - Error: ${errorMessage}")
                                                }
                                            }
                                        }, Response.ErrorListener {
                                            steps[2].status = "Failed"
                                            steps[2].comments = "Error: ${it.message}"
                                            recyclerView?.adapter?.notifyItemChanged(2)
                                            binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                            binding.visitationStatusBtn.isEnabled = true
//                                            visitationStatusBtn.isEnabled = true
//                                            dialogueLoadingView.visibility = View.GONE
//                                            progressBarTextVal.text = "Loading ..."
//                                            Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + it.message + " )")
                                            FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Tracking API - Error: ${it.message}")
                                        }))

                                }
                            },
                            {
                                Log.v("Loading PRG Data error", "" + it.message)
                                it.printStackTrace()
                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                steps[1].status = "Failed"
                                steps[1].comments = "Error: ${it.message}"
                                binding.visitationStatusBtn.isEnabled = true
                                recyclerView?.adapter?.notifyItemChanged(1)
                            }))
//
//                    (activity as FormsActivity).saveRequired = false
//                    refreshButtonsState()
//                    (activity as FormsActivity).saveVisitedScreensRequired = false
//                    completeButton.isEnabled = false
//

                    } else {
//                        Utility.showMessageDialog(requireContext(),"Error", "Error Creating Visitation")
                        binding.visitationStatusBtn.isEnabled = true
//                        (activity as FormsActivity).saveRequired = false
//                        (activity as FormsActivity).saveDone = true
//                        refreshButtonsState()
                    }
                } else {
                    // Retry after 500ms
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(conditionCheck)
    }

    fun submitVisitationDataUpdated() {
        binding.dialogueLoadingView.visibility = View.VISIBLE
        binding.visitationStepview.visibility = View.VISIBLE
        binding.visitationStatusBtn.isEnabled = false
        steps[0].status = "In Progress"
        recyclerView?.adapter?.notifyItemChanged(0)
        var visitationID = 0
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val insertDate = Date().toApiSubmitFormat()
        val performedDate = Date().toAppFormatMMDDYYYY()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID

        val facilityRep = if (binding.facilityRepresentativesSpinner.selectedItem.toString().contains("select")) "" else binding.facilityRepresentativesSpinner.selectedItem.toString()

        val automotiveSpecialist = if (binding.automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else binding.automotiveSpecialistSpinner.selectedItem.toString()
        val aarSign = if (binding.aarSignEditText.text.isNullOrEmpty()) "" else URLEncoder.encode(binding.aarSignEditText.text.toString(),"UTF-8")
        val qa = if (binding.qualityControlProcessEditText.text.isNullOrEmpty()) "" else URLEncoder.encode(binding.qualityControlProcessEditText.text.toString(),"UTF-8")
        val staffTraining = if (binding.staffTrainingProcessEditText.text.isNullOrEmpty()) "" else URLEncoder.encode(binding.staffTrainingProcessEditText.text.toString(),"UTF-8")
        val memberBenefits = if (binding.memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else URLEncoder.encode(binding.memberBenefitsPosterEditText.text.toString(),"UTF-8")
        val certificateOfApproval = if (binding.certificateOfApprovalEditText.text.isNullOrEmpty()) "" else URLEncoder.encode(binding.certificateOfApprovalEditText.text.toString(),"UTF-8")
//        val specSignature = "Spec Signature"
//        val repSignature = "Rep Signature"
//        val waiveSignature = "Waive Signature"
//        val defSignature = "Def Signature"
        val performedBy = if (binding.automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else binding.automotiveSpecialistSpinner.selectedItem.toString()

        val visitmethodStr = if (binding.visitationMethodDropListId.selectedItemPosition == 0) "" else binding.visitationMethodDropListId.selectedItem.toString()

        val visitmethod = if (binding.visitationMethodDropListId.selectedItemPosition == 0) 0 else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitmethodStr) }[0].TypeID

        val annualvisitationmonth = FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth
        var dialogMsg = ""
        var visitationType = ""
        var visitationTypeID = ""
        var visitationReasonID = ""
        var visitationReason = ""

        if (binding.annualVisitationType.isChecked) {
            visitationType = VisitationTypes.Annual.toString()
            visitationTypeID = "1"

        } else if (binding.quarterlyVisitationType.isChecked) {
            visitationType = VisitationTypes.Quarterly.toString()
            visitationTypeID = "2"

        } else if (binding.adhocVisitationType.isChecked) {
            visitationType = VisitationTypes.AdHoc.toString()
            visitationTypeID = "3"

        } else if (binding.defVisitationType.isChecked) {
            visitationType = VisitationTypes.Deficiency.toString()
            visitationTypeID = "4"

        }
//        visitation_sv.done(true)
//        visitation_sv.go(1,true)
        imgCounter = 0
        if ((activity as FormsActivity).imageSpecSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageRepSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageDefSignature != null)
            imgCounter++
        if ((activity as FormsActivity).imageWaiveSignature != null)
            imgCounter++

        if ((activity as FormsActivity).imageSpecSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageSpecSignature, "Spec", visitationType)
        if ((activity as FormsActivity).imageRepSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageRepSignature, "Rep", visitationType)
        if ((activity as FormsActivity).imageDefSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageDefSignature, "Def", visitationType)
        if ((activity as FormsActivity).imageWaiveSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageWaiveSignature, "W", visitationType)

        val handler = Handler(Looper.getMainLooper())

        val conditionCheck = object : Runnable {
            override fun run() {
                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                if (imgCounter == 0) {
                    // Perform your action
                    Log.d("Condition", "Condition met!")
                    if (!steps[0].status.contains("Failed")) {
                        steps[0].status = "Success"
//                        steps[0].comments = "==> Completed"
                        visitationReasonID = TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeName.equals(binding.visitationReasonDropListId.selectedItem.toString()) }[0].VisitationReasonTypeID.toString()
                        visitationReason = binding.visitationReasonDropListId.selectedItem.toString()
//                    progressBarTextVal.text = "Saving ..."
                        binding.dialogueLoadingView.visibility = View.VISIBLE
                        steps[1].status = "In Progress"
                        recyclerView?.adapter?.notifyItemChanged(0)
                        recyclerView?.adapter?.notifyItemChanged(1)
                        // Get PRG LOGS to write changes made in PDF
                        binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                        Bugfender.i("VisitationProcess", "API Call: ${Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}&userId=" + ApplicationPrefs.getInstance(context).loggedInUserID}")
                        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}&userId=" + ApplicationPrefs.getInstance(context).loggedInUserID,
                            { response ->
                                requireActivity().runOnUiThread {
                                    PRGDataModel.getInstance().tblPRGLogChanges.clear()
                                    (activity as FormsActivity).saveDone = true
                                    if (!response.toString().replace(" ", "").equals("[]")) {
                                        PRGDataModel.getInstance().tblPRGLogChanges = Gson().fromJson(response.toString(), Array<PRGLogChanges>::class.java).toCollection(ArrayList())
                                    } else {
                                        var item = PRGLogChanges()
                                        item.recordid = -1
                                        PRGDataModel.getInstance().tblPRGLogChanges.add(item)
                                    }
                                    steps[1].status = "Success"
                                    steps[2].status = "In Progress"
                                    recyclerView?.adapter?.notifyItemChanged(1)
                                    recyclerView?.adapter?.notifyItemChanged(2)
                                    // Save Visitation
                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                    var specialistEmail = ApplicationPrefs.getInstance(activity).loggedInUserEmail
                                    var directorEmail = if (!PRGDataModel.getInstance().tblPRGFacilityDirectors.isNullOrEmpty()) PRGDataModel.getInstance().tblPRGFacilityDirectors[0].directoremail else ""
                                    var changesMade = if (PRGDataModel.getInstance().tblPRGLogChanges.isNullOrEmpty()) "No" else "Yes"
                                    awsReference = Date().toApiAWSFormat() + "_" + visitationType + "_" + binding.visitationReasonDropListId.selectedItem.toString()
                                    var awsRef = URLEncoder.encode(awsReference, "UTF-8");
                                    var changedData = ""
                                    HasChangedModel.getInstance().changeDetails.sortedWith(compareBy({ it.screen }, { it.item }, { it.tag })).forEach {
                                        changedData = changedData + it.screen + "||" + it.item + "||" + it.details +";"
                                    }
                                    var urlString = "facNum=" + facilityNo + "&clubCode=" + clubCode + "&DatePerformed=" + performedDate + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&staffTraining=" + staffTraining +"&changesMade=" + changesMade + "&qcProcess=" + qa + "&aarSign=" + aarSign + "&memberBenefits=" + memberBenefits + "&certOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationTypeId=" + visitationTypeID+"&visitationMethod=" + visitmethodStr+ "&visitationType=" + visitationType + "&visitationReasonId=" + visitationReasonID +"&visitationReason=" + visitationReason + "&emailShopFlag=" + (if (binding.emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + binding.emailEditText.text + "&waiveVisitation=" + (if (binding.waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + URLEncoder.encode(binding.waiverCommentsEditText.text.toString(),"UTF-8") + "&facilityRep=" + facilityRep + "&performedBy=" + automotiveSpecialist + "&visitationID=0&annualVisitationMonth=" + annualvisitationmonth + "&visitationMethodId=" + visitmethod + "&reference=" + awsRef + "&specialistEmail=" + specialistEmail + "&directorEmail=" + directorEmail + "&docId=0&fieldId=101" + "&specSignature=" + specSignatureName + "&repSignature=" + repSignatureName + "&waiveSignature=" + waiveSignatureName + "&defSignature=" + defSignatureName + "&annualVisitationMonth=" + annualvisitationmonth + "&changedData=$changedData&visitationComments=" + URLEncoder.encode(binding.visitationCommentsEditText.text.toString(),"UTF-8")
                                    createMsg = urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ...")
                                    Log.v("Visitation Tracking -- ", Constants.createVisitation + urlString)
                                    Bugfender.i("VisitationProcess", "Create Visitation: ${Constants.createVisitation + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ...")}")
                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.POST, Constants.createVisitation + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                                        { response ->
                                            requireActivity().runOnUiThread {
                                                Log.v("VT RESPONSE ||| ", response.toString())
                                                Bugfender.i("VisitationProcess", "Create Visitation Response: ${response.toString()}")
                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                if (response.toString().contains("returnCode>0<", false)) {
                                                    if (response.toString().contains("DBResult:Not Saved",true)) {
                                                        saveToDBFailed = true
                                                    }
                                                    steps[2].status = "Success"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - VisitationTrackingData - Success")
                                                    visitationID = response.toString().substring(response.toString().indexOf("<visitationID") + 14, response.toString().indexOf("" + "</visitationID")).toInt()
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation ID ${visitationID}")
                                                    Constants.visitationIDForPDF = visitationID.toString()
                                                    Bugfender.i("VisitationProcess", "Visitation ID => $visitationID")
                                                    steps[2].comments = " - Visitation ID (${visitationID}) created successfully"
                                                    steps[3].status = "In Progress"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    Constants.awsReference = Date().toApiAWSFormat() + "_" + visitationType + "_" + binding.visitationReasonDropListId.selectedItem.toString()
                                                    dialogMsg = "New Visitation with ID (${visitationID}) created succesfully"
//                                                    (activity as FormsActivity).saveRequired = false
                                                    urlString = facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationType.toString() + "&visitationReason=" + binding.visitationReasonDropListId.selectedItem.toString() + "&emailPDF=" + (if (binding.emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + binding.emailEditText.text + "&waiveVisitation=" + (if (binding.waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + URLEncoder.encode(binding.waiverCommentsEditText.text.toString(),"UTF-8") + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=${visitmethod}&annualVisitationMonth=" + annualvisitationmonth + "&comments=" + URLEncoder.encode(binding.visitationCommentsEditText.text.toString(),"UTF-8")
                                                    Log.v("Visitation Details --- ", Constants.UpdateVisitationDetailsData + urlString)
                                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
//                                                    (activity as FormsActivity).saveRequired = false
//                                                    refreshButtonsState()
//                                                    (activity as FormsActivity).saveVisitedScreensRequired = false
//                                                    (activity as FormsActivity).saveDone = true
                                                    Bugfender.i("VisitationProcess", "Update Visitation Details: ${Constants.UpdateVisitationDetailsData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ...")}")
                                                    Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                                                        { response ->
                                                            requireActivity().runOnUiThread {
                                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                Log.v("VT RESPONSE ||| ", response.toString())
                                                                Bugfender.i("VisitationProcess", "Update Visitation Details Response: ${response.toString()}")
                                                                if (response.toString().contains("returnCode>0<", false)) {
                                                                    steps[3].status = "Success"
                                                                    steps[3].comments = " - Visitation Details updated successfully"
                                                                    recyclerView?.adapter?.notifyItemChanged(3)
                                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Success")
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].clubcode = clubCode.toInt()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].facid = facilityNo.toInt()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf = binding.emailPdfCheckBox.isChecked
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto = binding.emailEditText.text.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep = facilityRep
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist = automotiveSpecialist
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].sessionid = ApplicationPrefs.getInstance(activity).sessionID
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].userid = insertBy
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason = binding.visitationReasonDropListId.selectedItem.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype = visitationType.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments = binding.waiverCommentsEditText.text.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation = binding.waiveVisitationCheckBox.isChecked
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid = visitationID.toString()
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod = visitmethodStr
                                                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments = binding.visitationCommentsEditText.text.toString()
                                                                    //                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid

//                                                                    (activity as FormsActivity).saveRequired = false
//                //                                                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
//                                                                    refreshButtonsState()
//                                                                    (activity as FormsActivity).saveVisitedScreensRequired = false
                                                                    //                                                IndicatorsDataModel.getInstance().resetAllVisitedFlags()
                                                                    // Disable Buttons after click complete Button
                                                                    //                                                if (visitationType.equals(VisitationTypes.AdHoc) || visitationType.equals(VisitationTypes.Deficiency)) {
                                                                    //                                                    completeButton.isEnabled = true
                                                                    //                                                } else {
                                                                    //                                                    completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                                                                    //                                                }
                                                                    binding.cancelButton.isEnabled = false

                                                                    if (!binding.waiveVisitationCheckBox.isChecked || binding.waiveVisitationCheckBox.isChecked) {
//                                                                        if ((activity as FormsActivity).checkPermission()) {
//                                                                            (activity as FormsActivity).generateAndOpenPDF()
//                                                                        } else {
//                                                                            if (!(activity as FormsActivity).checkPermission()) {
//                                                                                (activity as FormsActivity).requestPermissionAndContinue();
//                                                                            } else {
//                                                                                (activity as FormsActivity).generateAndOpenPDF()
//                                                                            }
//                                                                        }
                                                                        visitationProcessCompleted = true
                                                                        if (!saveToDBFailed) {
                                                                            Bugfender.i("VisitationProcess", "Process Completed")
                                                                            steps[4].status = "Process initiated. Check your email shortly. You may safely close this screen now"
                                                                        } else {
                                                                            steps[4].status =
                                                                                "Process Failed. Please send the below text or screenshot to PRG Support via mail before closing the screen:\n$createMsg"
                                                                            Bugfender.i("VisitationProcess", "Process Failed => $createMsg")
                                                                        }
                                                                        requireActivity().runOnUiThread {
                                                                            recyclerView?.adapter?.notifyItemChanged(4)
                                                                        }
                                                                        val handler = Handler(Looper.getMainLooper())

                                                                        val conditionCheck = object : Runnable {
                                                                            override fun run() {
                                                                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                                if (visitationProcessCompleted) {
                                                                                    // Perform your action
                                                                                    if (steps[4].status.equals("Success") || steps[4].status.contains("Process",true)) {
//                                                                                        (activity as FormsActivity).onBackPressed()
                                                                                        binding.visitationStatusBtn.isEnabled = true
                                                                                    }
                                                                                } else {
                                                                                    // Retry after 500ms
                                                                                    if (steps[4].status.equals("Failed") || (!steps[5].status.equals("In Progress") && !steps[6].status.equals("In Progress") && !steps[7].status.equals("In Progress")))
                                                                                        binding.visitationStatusBtn.isEnabled = true
                                                                                    handler.postDelayed(this, 500)
                                                                                }
                                                                            }
                                                                        }
                                                                        handler.post(conditionCheck)

//                                                                        Handler().postDelayed({
//                                                                            progressBarTextVal.text = "Generating Visitation PDFs ..."
//                                                                            Handler().postDelayed({
//                                                                                progressBarTextVal.text = "Sending Visitation PDFs ..."
//                                                                                Handler().postDelayed({
//                                                                                    progressBarTextVal.text = "Finalizing completion process ..."
//                                                                                    Handler().postDelayed({
//                                                                                        dialogueLoadingView.visibility = View.GONE
//                                                                                        progressBarTextVal.text = "Loading ..."
//                                                                                        val builder = AlertDialog.Builder(activity)
//                                                                                        builder.setTitle("Confirmation ...")
//                                                                                        builder.setMessage(dialogMsg)
//                                                                                        builder.setPositiveButton("OK"
//                                                                                        )
//                                                                                        { dialog, id ->
//                                                                                            dialog.dismiss()
//                                                                                            (activity as FormsActivity).onBackPressed()
//                                                                                        }
//                                                                                        builder.show()
//                                                                                    }, 5000)
//                                                                                }, 5000)
//                                                                            }, 5000)
//                                                                        }, 5000)
                                                                    } else {
//                                                                        dialogueLoadingView.visibility = View.GONE
//                                                                        progressBarTextVal.text = "Loading ..."
//                                                                        //                                                                Utility.showMessageDialog(activity,"Confirmation ...", dialogMsg)
//                                                                        val builder = AlertDialog.Builder(activity)
//                                                                        builder.setTitle("Confirmation ...")
//                                                                        builder.setMessage(dialogMsg)
//                                                                        builder.setPositiveButton("OK"
//                                                                        )
//                                                                        { dialog, id ->
//                                                                            dialog.dismiss()
//                                                                            (activity as FormsActivity).onBackPressed()
//                                                                        }
//                                                                        builder.show()
                                                                        binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                        binding.visitationStatusBtn.isEnabled = true
                                                                    }
                                                                } else {
                                                                    binding.dialogueLoadingView.visibility = View.GONE
                                                                    binding.progressBarTextVal.text = "Loading ..."
                                                                    var errorMessage = ""
                                                                    if (response.toString().contains("<message")) {
                                                                        errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                                    } else {
                                                                        errorMessage = "ACE API Error: " + response.toString()
                                                                    }
//                                                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
//                                                                    Utility.showSubmitAlertDialog(activity, false, dialogMsg + " ..... Visitation Details (Error: " + errorMessage + " )")
                                                                    binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                                                    steps[3].status = "Failed"
                                                                    steps[3].comments = errorMessage
                                                                    binding.visitationStatusBtn.isEnabled = true
                                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Error: ${errorMessage}")
                                                                }
                                                            }
                                                        },
                                                        {
                                                            Log.v("ERROR ->",it.message.toString())
                                                            binding.dialogueLoadingView.visibility = View.GONE
                                                            binding.progressBarTextVal.text = "Loading ..."
                                                            steps[3].status = "Failed"
                                                            steps[3].comments = it.message.toString()
                                                            binding.visitationStatusBtn.isEnabled = true
//                                                            Utility.showSubmitAlertDialog(activity, false, "Visitation Details (Error: " + it.message + " )")
                                                            FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Details API - Error: ${it.message}")
                                                        }))
                                                    //                            dialogueLoadingView.visibility = View.GONE
                                                    //                            progressBarTextVal.text = "Loading ..."
                                                } else {

                                                    binding.dialogueLoadingView.visibility = View.GONE
                                                    binding.progressBarTextVal.text = "Loading ..."
                                                    var errorMessage = ""
                                                    Bugfender.i("VisitationProcess", "Update Visitation Details Failed ${response.toString()}")
                                                    if (errorMessage.contains("<message")) {
                                                        errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                    } else {
                                                        errorMessage = "ACE API Error: " + response.toString()
                                                    }
                                                    steps[2].status = "Process Failed. Please send the below text or screenshot to PRG Support via mail before closing the screen:\n$createMsg"
                                                    steps[2].comments = "Error: ${errorMessage}"
                                                    recyclerView?.adapter?.notifyItemChanged(2)
                                                    binding.visitationStatusBtn.isEnabled = true
//                                                    Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + errorMessage + " )")
                                                    FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Tracking API - Error: ${errorMessage}")
                                                }
                                            }
                                        },
                                        {
                                            Log.v("ERROR ->",it.message.toString())
                                            Bugfender.i("VisitationProcess", "Create Visitation Failed: ${it.message}")
                                            steps[2].status = "Failed"
                                            steps[2].comments = "Error: ${it.message}"
                                            recyclerView?.adapter?.notifyItemChanged(2)
                                            binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                            binding.visitationStatusBtn.isEnabled = true
//                                            visitationStatusBtn.isEnabled = true
//                                            dialogueLoadingView.visibility = View.GONE
//                                            progressBarTextVal.text = "Loading ..."
//                                            Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + it.message + " )")
                                            FirebaseCrashlytics.getInstance().log("Visitation Screen - Visitation Tracking API - Error: ${it.message}")
                                        }))

                                }
                            },
                            {
                                Log.v("Loading PRG Data error", "" + it.message)
//                                it.printStackTrace()
                                Bugfender.i("VisitationProcess", "Loading PRG Data Failed: ${it.message}")
                                binding.netwrokStatusText.text = (activity as FormsActivity).networkStatus
                                steps[1].status = "Failed"
                                steps[1].comments = "Error: ${it.message}"
                                binding.visitationStatusBtn.isEnabled = true
                                recyclerView?.adapter?.notifyItemChanged(1)
                            }))
//
//                    (activity as FormsActivity).saveRequired = false
//                    refreshButtonsState()
//                    (activity as FormsActivity).saveVisitedScreensRequired = false
//                    completeButton.isEnabled = false
//

                    } else {
//                        Utility.showMessageDialog(requireContext(),"Error", "Error Creating Visitation")
                        binding.visitationStatusBtn.isEnabled = true
//                        (activity as FormsActivity).saveRequired = false
//                        (activity as FormsActivity).saveDone = true
//                        refreshButtonsState()
                    }
                } else {
                    // Retry after 500ms
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(conditionCheck)
    }

    fun emailFormatValidation(target: CharSequence): Boolean {

        if (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            emailValid = true else emailValid = false
        return emailValid
    }

    fun emailValidation() {

        binding.emailEditText.isEnabled = binding.emailPdfCheckBox.isChecked

        binding.emailPdfCheckBox.setOnClickListener {
            binding.emailEditText.isEnabled = binding.emailPdfCheckBox.isChecked
            (activity as FormsActivity).saveRequired = true
            Log.v("SAVEREQUIRED -->", " emailPdfCheckBox")
            refreshButtonsState()
//            if (emailPdfCheckBox.isChecked) {
//                emailEditText.isEnabled = true
//            } else emailEditText.isEnabled = false
        }

    }

    fun waiverValidation() {

        binding.waiverCommentsEditText.isEnabled = binding.waiveVisitationCheckBox.isChecked
        binding.waiverConditionedEnablingLayout.isEnabled = binding.waiveVisitationCheckBox.isChecked

        binding.waiveVisitationCheckBox.setOnClickListener {
            binding.waiverCommentsEditText.isEnabled = binding.waiveVisitationCheckBox.isChecked
            binding.waiverConditionedEnablingLayout.isEnabled = binding.waiveVisitationCheckBox.isChecked
            (activity as FormsActivity).saveRequired = true
            Log.v("SAVEREQUIRED -->", " waiveVisitationCheckBox")
            refreshButtonsState()
        }

    }

    private fun fillDeficiencyTable() {

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = 40
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        var tableRowColorSwitch = false


        FacilityDataModel.getInstance().tblDeficiency.apply {

            (0 until size).forEach {
                if ((!get(it).DefTypeID.equals("-1")) && get(it).ClearedDate.isNullOrEmpty()) {
                    var tableRow = TableRow(context)

                    if (tableRowColorSwitch) {
                        tableRow.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.table_row_color))
                    } else {
                        tableRow.setBackgroundColor(Color.WHITE)
                    }

                    tableRowColorSwitch = !tableRowColorSwitch //Switching smartly :)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = if (TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.toString() == get(it).DefTypeID }.isNotEmpty()) TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.toString() == get(it).DefTypeID }[0].DeficiencyName else ""
                    textView.setPadding(5)
                    textView.gravity = Gravity.CENTER_VERTICAL
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = get(it).Comments
                    textView.setPadding(5)
                    textView.setEms(8)
                    textView.gravity = Gravity.CENTER_VERTICAL
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = get(it).VisitationDate.apiToAppFormatMMDDYYYY()
                    textView.gravity = Gravity.CENTER_VERTICAL
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.text = get(it).DueDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    binding.deficienciesTableLayout.addView(tableRow)
                }
            }
        }
    }

    fun altDeffVisitationTableRow(alt_row: Int) {
        var childViewCount = binding.deficienciesTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = binding.deficienciesTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount() - 1) {

                var tv: TextView = row.getChildAt(j) as TextView
                if (i % alt_row != 0) {
                    tv.setBackground(getResources().getDrawable(
                            R.drawable.alt_row_color));
                } else {
                    tv.setBackground(getResources().getDrawable(
                            R.drawable.row_color));
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
//        completeButton.isEnabled = true//IndicatorsDataModel.getInstance().validateAllScreensVisited()
    }

    private fun duplicateCheckPassed(): Boolean {
        if (PRGDataModel.getInstance().tblPRGAppVersion[0].duplicateCheckEnabled == 0) return true
        duplicateVisitationID = ""
        var visitationTypeID = ""
//        Calendar.getInstance().get(Calendar.MONTH).toString()
        if (binding.annualVisitationType.isChecked) {
            visitationTypeID = "1"
        } else if (binding.quarterlyVisitationType.isChecked) {
            visitationTypeID = "2"
        } else if (binding.adhocVisitationType.isChecked) {
            visitationTypeID = "3"
        } else if (binding.defVisitationType.isChecked) {
            visitationTypeID = "4"
        }
        var currentMonth = ""
        var currentYear = ""
        var currentDay =""
        currentDay = if ((Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString().length == 1) {
            "0" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        } else {
            (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).toString()
        }

        currentMonth = if ((Calendar.getInstance().get(Calendar.MONTH) + 1).toString().length == 1) {
            "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        } else {
            (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        }
        currentYear = (Calendar.getInstance().get(Calendar.YEAR)).toString()

        val visitationReasonID = TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeName.equals(binding.visitationReasonDropListId.selectedItem.toString()) }[0].VisitationReasonTypeID.toString()
        if (visitationTypeID=="3") {
            if (!FacilityDataModel.getInstance().tblVisitationTracking.filter { s ->
                    s.VisitationTypeID!!.equals(
                        visitationTypeID
                    ) && s.DatePerformed.substring(5, 7)
                        .equals(currentMonth) && s.DatePerformed.substring(0, 4)
                        .equals(currentYear) && s.DatePerformed.substring(8, 10)
                        .equals(currentDay) && s.VisitationReasonTypeID.equals(visitationReasonID)
                }.isNullOrEmpty()) {
                duplicateVisitationID =
                    FacilityDataModel.getInstance().tblVisitationTracking.filter { s ->
                        s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(
                            5,
                            7
                        ).equals(currentMonth) && s.DatePerformed.substring(0, 4)
                            .equals(currentYear)  && s.DatePerformed.substring(8, 10)
                            .equals(currentDay) && s.VisitationReasonTypeID.equals(
                            visitationReasonID
                        )
                    }[0].visitationID
                Bugfender.i("VisitationProcess", "Duplicate Detected $duplicateVisitationID")
                return false
            }
        } else {
            if (!FacilityDataModel.getInstance().tblVisitationTracking.filter { s ->
                    s.VisitationTypeID!!.equals(
                        visitationTypeID
                    ) && s.DatePerformed.substring(5, 7)
                        .equals(currentMonth) && s.DatePerformed.substring(0, 4)
                        .equals(currentYear) && s.VisitationReasonTypeID.equals(visitationReasonID)
                }.isNullOrEmpty()) {
                duplicateVisitationID =
                    FacilityDataModel.getInstance().tblVisitationTracking.filter { s ->
                        s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(
                            5,
                            7
                        ).equals(currentMonth) && s.DatePerformed.substring(0, 4)
                            .equals(currentYear) && s.VisitationReasonTypeID.equals(
                            visitationReasonID
                        )
                    }[0].visitationID
                Bugfender.i("VisitationProcess", "Duplicate Detected $duplicateVisitationID")
                return false
            }
        }
//        if (!FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(5, 7).equals(currentMonth) && s.VisitationReasonTypeID.equals(visitationReasonID) }.isNullOrEmpty()) {
//            duplicateVisitationID = FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(5, 7).equals(currentMonth) && s.VisitationReasonTypeID.equals(visitationReasonID) }[0].visitationID
//            return false
//        }

        return true
    }

    fun validateInputs(): Boolean {
        var isInputValid = true
        binding.automotiveSpecialistSignatureButton.setError(null)
        binding.facilityRepresentativeSignatureButton.setError(null)
        binding.facilityRepresentativeTextView.setError(null)
        binding.visitationReasonTextView.setError(null)
        binding.waiverCommentsEditText.setError(null)
        binding.waiversSignatureButton.setError(null)
        binding.emailEditText.setError(null)
        binding.automotiveSpecialistTextView.setError(null)
        binding.visitationMethodTextView.setError(null)
        if (binding.automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) {
            isInputValid = false
            binding.automotiveSpecialistTextView.setError("Required Field")
        }
        if (binding.waiveVisitationCheckBox.isChecked) {
            if (binding.waiverCommentsEditText.text.toString().isNullOrEmpty()) {
                isInputValid = false
                binding.waiverCommentsEditText.setError("required field")
            }
            if (binding.waiversSignatureButton.text.toString() == "Add Signature") {
                isInputValid = false
                binding.waiversSignatureButton.setError("required field")
            }
//            if (automotiveSpecialistSignatureButton.text.toString() == "Add Signature") {
//                isInputValid = false
//                automotiveSpecialistSignatureButton.setError("Required field")
//            }
//            if (visitationMethodDropListId.selectedItemPosition==0) {
//                isInputValid = false
//                visitationMethodTextView.setError("Required field")
//            }
        } else {
            if (binding.visitationMethodDropListId.selectedItemPosition == 0) {
                isInputValid = false
                binding.visitationMethodTextView.setError("Required field")
            }
            if ((binding.adhocVisitationType.isChecked || binding.defVisitationType.isChecked)) {
                if (binding.visitationReasonDropListId.selectedItemPosition == 0) {
                    isInputValid = false
                    binding.visitationReasonTextView.setError("Required field")
                }
            } else {
                if (binding.facilityRepresentativeSignatureButton.text.toString() == "Add Signature" && binding.visitationMethodDropListId.selectedItem.toString().equals("In Person")) {
                    isInputValid = false
                    binding.facilityRepresentativeSignatureButton.setError("Required field")
                }
            }

            if (binding.facilityRepresentativesSpinner.selectedItem.toString().contains("please")) {
                isInputValid = false

                binding.facilityRepresentativeTextView.setError("Required Field")
            }

            if (binding.automotiveSpecialistSignatureButton.text.toString() == "Add Signature") {

                isInputValid = false
                binding.automotiveSpecialistSignatureButton.setError("Required field")

            }
        }

        if (binding.emailPdfCheckBox.isChecked) {

            if (binding.emailEditText.text.toString().isNullOrEmpty()) {
                isInputValid = false
                binding.emailEditText.setError("required field")
            }
        } else {
            binding.emailEditText.setError(null)
        }

//            if (waiveVisitationCheckBox.isChecked) {
//
//                if (waiverCommentsEditText.text.toString().isNullOrEmpty() || waiversSignatureButton.text.toString() == "Add Signature") {
//
//                    isInputValid = false
//                    waiverCommentsEditText.setError("required field")
//                    waiversSignatureButton.setError("required field")
//                }
//
//            } else {
//                waiverCommentsEditText.setError(null)
//                waiversSignatureButton.setError(null)
//            }

//        if (emailPdfCheckBox.isChecked ) {
            if (!emailFormatValidation(binding.emailEditText.text.toString())) {
                isInputValid = false
                binding.emailEditText.setError("please type your email correctly")
            } else {
                binding.emailEditText.setError(null)
            }
//        } else {
//            emailEditText.setError(null)
//        }



        return isInputValid
    }

    fun refreshButtonsState(isForced: Boolean = false) {
        if (isForced) {
            binding.saveButton.isEnabled = false
            binding.cancelButton.isEnabled = false
        } else {
            binding.saveButton.isEnabled = (activity as FormsActivity).saveRequired
            binding.cancelButton.isEnabled = (activity as FormsActivity).saveRequired
        }
    }

    fun handleCancelButtonClick() {

        binding.cancelButton.setOnClickListener {

            var alertBuilder = AlertDialog.Builder(context);
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.decision_dialog, null)
            alertBuilder.setView(dialogView)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
            val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
            val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
            dialogTitle.setText("Permission Required")
            dialogMessage.setText("Are you sure you want to cancel ?")
            btnPositiveAction.setText("YES")
            btnNegativeAction.setText("NO")
            val dialog = alertBuilder.create()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                if (visitMethodPreviousValue.equals(""))
                    binding.visitationMethodDropListId.setSelection(0)
                else {
                    binding.visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(visitMethodPreviousValue))
                }
                binding.waiveVisitationCheckBox.isChecked = waiveVisitationCBPreviousValue
                binding.emailPdfCheckBox.isChecked = emailPdfCBPreviousValue
                binding.waiverCommentsEditText.setText(waiverCommentsPreviousValue)
                binding.emailEditText.setText(emailEditTextPreviousValue)
                binding.staffTrainingProcessEditText.setText(staffTrainingProcessPreviousValue)
                binding.qualityControlProcessEditText.setText(qualityControlProcessPreviousValue)
                binding.aarSignEditText.setText(aarSignPreviousValue)
                binding.certificateOfApprovalEditText.setText(certificateOfApprovalPreviousValue)
                binding.memberBenefitsPosterEditText.setText(memberBenefitsPosterPreviousValue)
                binding.visitationMethodDropListId.tag = "-2"
                (activity as FormsActivity).saveRequired = false
                refreshButtonsState(true)
                dialog.dismiss()
            })
            btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                dialog.cancel()
            })
            dialog.show()
//
//            val alertDialogBuilder = AlertDialog.Builder(
//                    context)
//
//            // set title
//            alertDialogBuilder.setTitle("Cancel?")
//
//            // set dialog message
//            alertDialogBuilder
//                    .setMessage("Are you sure you want to cancel")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes") { dialog, id ->
//                        binding.waiveVisitationCheckBox.isChecked = waiveVisitationCBPreviousValue
//                        binding.emailPdfCheckBox.isChecked = emailPdfCBPreviousValue
//                        binding.waiverCommentsEditText.setText(waiverCommentsPreviousValue)
//                        binding.emailEditText.setText(emailEditTextPreviousValue)
//                        binding.staffTrainingProcessEditText.setText(staffTrainingProcessPreviousValue)
//                        binding.qualityControlProcessEditText.setText(qualityControlProcessPreviousValue)
//                        binding.aarSignEditText.setText(aarSignPreviousValue)
//                        binding.certificateOfApprovalEditText.setText(certificateOfApprovalPreviousValue)
//                        binding.memberBenefitsPosterEditText.setText(memberBenefitsPosterPreviousValue)
//                        if (visitMethodPreviousValue.equals(""))
//                            binding.visitationMethodDropListId.setSelection(0)
//                        else {
////                            var vMethood = TypeTablesModel.getInstance().VisitationMethodType.filter { s->s.TypeID.toString().equals(visitMethodPreviousValue)}[0].TypeName
////                            visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(vMethood))
//                            binding.visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(visitMethodPreviousValue))
//                        }
////                        visitationMethodDropListId.setSelection((resources.getStringArray(R.array.visitation_methods)).indexOf(vMethood))
//
//                        (activity as FormsActivity).saveRequired = false
//                        refreshButtonsState()
//                        dialog.cancel()
//                    }
//                    .setNegativeButton("No") { dialog, id ->
//                        // if this button is clicked, just close
//                        // the dialog box and do nothing
//                        dialog.cancel()
//                    }
//
//            // create alert dialog
//            val alertDialog = alertDialogBuilder.create()
//
//            // show it
//            alertDialog.show()
        }
    }



}
data class Step(
    val number: Int,        // Step number
    val title: String,      // Step title
    var status: String,      // Step status
    var comments: String      // Step status
)

class StepAdapter(
    private val steps: List<Step>
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    // ViewHolder for each step item
    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circle_text: TextView = itemView.findViewById(R.id.step_circle_text)
        val failed_icon_img: AppCompatImageView = itemView.findViewById(R.id.failed_icon)
        val success_icon_img: AppCompatImageView = itemView.findViewById(R.id.success_icon)
        val step_progress_bar: ProgressBar = itemView.findViewById(R.id.step_progress)
        val title: TextView = itemView.findViewById(R.id.step_title)
        val status: TextView = itemView.findViewById(R.id.step_status)
        val stepLine : View  = itemView.findViewById(R.id.step_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.step_item, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        // Step Number
        holder.circle_text.text = step.number.toString()
        // Step Title
        holder.title.text = step.title
        // Step Status
        if (step.comments.isEmpty())
            holder.status.text = step.status
        else
            holder.status.text = "${step.status} - ${step.comments}"

        if (step.status.contains("Failed", true)) {
                holder.failed_icon_img.visibility = View.VISIBLE
                holder.success_icon_img.visibility = View.GONE
                holder.circle_text.visibility = View.GONE
                holder.step_progress_bar.visibility = View.GONE
                holder.stepLine.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.alertColor))
        } else if (step.status.contains("Success", true) || step.status.contains("Initiated", true)) {
                holder.failed_icon_img.visibility = View.GONE
                holder.success_icon_img.visibility = View.VISIBLE
                holder.circle_text.visibility = View.GONE
                holder.step_progress_bar.visibility = View.GONE
                if (step.status.contains("Success", true)) {
                    holder.stepLine.visibility = View.VISIBLE
                    holder.stepLine.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.cb_green
                        )
                    )
                } else {
                    holder.stepLine.visibility = View.GONE
                    holder.stepLine.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.cb_green
                        )
                    )
                }
        } else if (step.status.contains("Pending", true)) {
                holder.failed_icon_img.visibility = View.GONE
                holder.success_icon_img.visibility = View.GONE
                holder.circle_text.visibility = View.VISIBLE
                holder.step_progress_bar.visibility = View.GONE
                holder.stepLine.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
        } else {
                holder.failed_icon_img.visibility = View.GONE
                holder.success_icon_img.visibility = View.GONE
                holder.circle_text.visibility = View.GONE
                holder.step_progress_bar.visibility = View.VISIBLE
                holder.stepLine.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
        }
    }

    override fun getItemCount() = steps.size
}



