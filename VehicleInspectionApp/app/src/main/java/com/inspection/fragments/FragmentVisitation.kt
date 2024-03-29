package com.inspection.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.adapter.MultipartRequest
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_visitation_form.*
import kotlinx.android.synthetic.main.fragment_visitation_form.cancelButton
import kotlinx.android.synthetic.main.fragment_visitation_form.saveButton
import kotlinx.android.synthetic.main.fragment_visitation_form.trackingTableLayout
import kotlinx.android.synthetic.main.visitation_planning_filter_fragment.*
import java.io.File
import java.io.FileOutputStream
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
//    var isWaiverSignatureInitialized = false
//    var isFacilityRepresentativeDeficiencySignatureInitialized = false

    var facilityRepresentativeNames = ArrayList<String>()
    var facilitySpecialistNames = ArrayList<String>()

    enum class requestedSignature {
        representative, specialist, representativeDeficiency, waiver
    }

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

    var selectedSignature: requestedSignature? = null

    var emailValid = true

    var firsLoadDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layo ut for this fragment
        (activity as FormsActivity).saveRequired = false
        return inflater!!.inflate(R.layout.fragment_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as FormsActivity).saveRequired = false
        FacilityDataModelOrg.getInstance().changeWasDone = false
        dataChangedNoRadioButton.isClickable = false
        dataChangedYesRadioButton.isClickable = false
        dataChangeHandling()
        checkMarkChangesDone()
        visitationMethodDropListId.tag = 0
        facilityRepresentativesSpinner.tag = 0
        initializeFields()
        setFieldsValues()
        setFieldsListeners()
        fillTrackingData()
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

        (activity as FormsActivity).visitationsTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).visitationTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        (activity as FormsActivity).saveRequired = false
        // SAVE IN PROGRESS
        if (PRGDataModel.getInstance().tblPRGVisitationsLog.filter { s -> s.facid == FacilityDataModel.getInstance().tblFacilities[0].FACNo && s.clubcode == FacilityDataModel.getInstance().clubCode.toInt() && s.facannualinspectionmonth == FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth && s.inspectioncycle == FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle && s.visitationtype == FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType.toString() }.isEmpty()) {
            markVisitationInProgress()
        }
        firsLoadDone = true
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

    fun fillTrackingData() {
        if (trackingTableLayout.childCount > 1) {
            for (i in trackingTableLayout.childCount - 1 downTo 1) {
                trackingTableLayout.removeViewAt(i)
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

                    trackingTableLayout.addView(tableRow)
                }
            }
        }

    }


    fun checkMarkChangesDone() {
        IndicatorsDataModel.getInstance().validateVisitationSectionVisited()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun dataChangeHandling() {
        dataChangedYesRadioButton.isChecked = HasChangedModel.getInstance().changeWasMadeFroAny()
        dataChangedNoRadioButton.isChecked = !dataChangedYesRadioButton.isChecked
    }

    private fun initializeFields() {

        annualVisitationType.isClickable = false
        annualVisitationType.isEnabled = false

        quarterlyVisitationType.isClickable = false
        quarterlyVisitationType.isEnabled = false

        adhocVisitationType.isClickable = false
        dataChangedYesRadioButton.isClickable = false

        adhocVisitationType.isEnabled = false
        defVisitationType.isEnabled = false

        dateOfVisitationButton.isClickable = false
        dataChangedNoRadioButton.isClickable = false
//        clubCodeEditText.isClickable = false
//        clubCodeEditText.isEnabled = false
        facilityNumberEditText.isEnabled = false


        handleCancelButtonClick()

        altDeffVisitationTableRow(2)
    }

    private fun setFieldsValues() {

        facilityRepresentativeNames.clear()
        facilityRepresentativeNames.add("please select a representative")

        for (fac in FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName + " " + s.LastName }.distinct()) {
            facilityRepresentativeNames.add(fac)
        }

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

        visitationReasonDropListId.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, VisitationReasonArray)
        visitationMethodDropListId.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, VisitationMethodArray)

        completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == null) {
            adhocVisitationType.isChecked = true
            adhocVisitationType.isClickable = true
            adhocVisitationType.isEnabled = true
            annualVisitationType.isClickable = true
            annualVisitationType.isEnabled = true
            quarterlyVisitationType.isClickable = true
            quarterlyVisitationType.isEnabled = true
            defVisitationType.isClickable = true
            defVisitationType.isEnabled = true
            visitationReasonDropListId.setSelection(0, true)
            visitationReasonDropListId.isEnabled = true
            visitationReasonDropListId.isClickable = true
            completeButton.isEnabled = true
        } else {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Annual)) {
                annualVisitationType.isChecked = true
                visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Annual Visitation"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Quarterly)) {
                quarterlyVisitationType.isChecked = true
                visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Quarterly Visitation"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) {
                adhocVisitationType.isChecked = true
                // Make Type selectable
                adhocVisitationType.isClickable = true
                adhocVisitationType.isEnabled = true
                annualVisitationType.isClickable = true
                annualVisitationType.isEnabled = true
                quarterlyVisitationType.isClickable = true
                quarterlyVisitationType.isEnabled = true
                defVisitationType.isClickable = true
                defVisitationType.isEnabled = true

                // End
                visitationReasonDropListId.setSelection(0, true)
//                visitationReasonDropListId.isEnabled = true
//                visitationReasonDropListId.isClickable = true
                completeButton.isEnabled = true
            } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Deficiency)) {
                defVisitationType.isChecked = true
                visitationReasonDropListId.setSelection(VisitationReasonArray.indexOf("Deficiency Inspection"), true)
//                visitationReasonDropListId.isEnabled = false
//                visitationReasonDropListId.isClickable = false
                completeButton.isEnabled = true
            }
        }

        waiveVisitationCheckBox.isChecked = false
        emailPdfCheckBox.isChecked = false
        waiverCommentsEditText.setText("")
        waiverCommentsEditText.tag = "0"
        emailEditText.setText("")
        emailEditText.tag = "0"
        facilityRepresentativesSpinner.setSelection(0)
        staffTrainingProcessEditText.setText("")
        staffTrainingProcessEditText.tag = "0"
        qualityControlProcessEditText.setText("")
        qualityControlProcessEditText.tag = "0"
        aarSignEditText.setText("")
        aarSignEditText.tag = "0"
        certificateOfApprovalEditText.setText("")
        certificateOfApprovalEditText.tag = "0"
        memberBenefitsPosterEditText.setText("")
        memberBenefitsPosterEditText.tag = "0"
        dateOfVisitationButton.text = Date().toAppFormatMMDDYYYY()
        clubCodeEditVal.setText(FacilityDataModel.getInstance().clubCode)
        facilityNumberEditText.setText("" + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            qualityControlProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            staffTrainingProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
            emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {


            facilitySpecialistNames.add("Select Specialist")
            for (specialist in TypeTablesModel.getInstance().EmployeeList.sortedWith(compareBy { it.FullName })) {
                facilitySpecialistNames.add(specialist.FirstName.lowercase().capitalize() + " " + specialist.LastName.lowercase().capitalize())
            }

            facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, facilityRepresentativeNames)
            //   automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname })
            automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, facilitySpecialistNames)

            facilityNameAndNumberRelationForSelection()
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName))
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.toUpperCase()))
            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))
            automotiveSpecialistSpinner.tag = automotiveSpecialistSpinner.selectedItemPosition
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
                facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature)
            }
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature != null) {
                waiversSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature)
            }
        }



        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {


            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNotEmpty()) {
                facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                facilityRepresentativesSpinner.tag = facilityRepresentativesSpinner.selectedItemPosition
                Log.v("TAG -->", facilityRepresentativesSpinner.tag.toString())
            }
            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNotEmpty()) {
                    facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                    facilityRepresentativesSpinner.tag = facilityRepresentativesSpinner.selectedItemPosition
                    Log.v("TAG -->", facilityRepresentativesSpinner.tag.toString())
                }
            }

//            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNotEmpty()) {
////                    facilityRepresentativesSpinner.setSelection(CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                    facilityRepresentativesSpinner.setSelection(TypeTablesModel.getInstance().EmployeeList.map { s -> s.FullName}.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                }
//            }

            facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))
            facilityRepresentativesSpinner.tag = facilityRepresentativesSpinner.selectedItemPosition
            Log.v("TAG -->", facilityRepresentativesSpinner.tag.toString())
            if (PRGDataModel.getInstance().tblPRGVisitationHeader.filter { s->s.visitationid.equals("") || s.visitationid==null }.isNotEmpty()) {
                var loadPrevData = true
//                if (adhocVisitationType.isChecked){
//                }
                if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].recordid == -1) {
                    loadPrevData = false
                }
                if (loadPrevData) {
                    waiveVisitationCheckBox.isChecked = PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation
                    waiveVisitationCBPreviousValue = waiveVisitationCheckBox.isChecked
                    emailPdfCheckBox.isChecked = PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf
                    emailPdfCBPreviousValue = emailPdfCheckBox.isChecked
                    waiverCommentsEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments)
                    waiverCommentsPreviousValue = waiverCommentsEditText.text.toString()


                    if (PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.isNullOrEmpty()) {
                        visitationMethodDropListId.setSelection(0)
                        visitMethodPreviousValue = ""
                    } else {
                        if (!PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("0") && !PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("") && !PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod.equals("None")) {
                            var vMethood = TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeID.toString().equals(PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod) }[0].TypeName
//                        visitationMethodDropListId.setSelection((resources.getStringArray(R.array.visitation_methods)).indexOf(vMethood))
                            visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(vMethood))
                            visitMethodPreviousValue = visitationMethodDropListId.selectedItem.toString()
                            visitationMethodDropListId.tag = visitationMethodDropListId.selectedItemPosition
                        } else {
                            visitationMethodDropListId.setSelection(0)
                            visitMethodPreviousValue = ""
                        }
                    }
                    visitationCommentsEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments)
                    emailEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto)
                    emailEditTextPreviousValue = emailEditText.text.toString()
                    facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep))
                    facilityRepresentativesSpinner.tag = facilityRepresentativesSpinner.selectedItemPosition
                    Log.v("TAG -->", facilityRepresentativesSpinner.tag.toString())
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
                        staffTrainingProcessEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].stafftraining)
                        staffTrainingProcessPreviousValue = staffTrainingProcessEditText.text.toString()
                        qualityControlProcessEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].qualitycontrol)
                        qualityControlProcessPreviousValue = qualityControlProcessEditText.text.toString()
                        aarSignEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].aarsigns)
                        aarSignPreviousValue = aarSignEditText.text.toString()
                        certificateOfApprovalEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].certificateofapproval)
                        certificateOfApprovalPreviousValue = certificateOfApprovalEditText.text.toString()
                        memberBenefitsPosterEditText.setText(PRGDataModel.getInstance().tblPRGVisitationHeader[0].memberbenefitposter)
                        memberBenefitsPosterPreviousValue = memberBenefitsPosterEditText.text.toString()
                        visitationComments = visitationCommentsEditText.text.toString()
                    }
                }
            } else if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                waiveVisitationCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations
                waiveVisitationCBPreviousValue = waiveVisitationCheckBox.isChecked
                emailPdfCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility
                emailPdfCBPreviousValue = emailPdfCheckBox.isChecked
                waiverCommentsEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments)
                waiverCommentsPreviousValue = waiverCommentsEditText.text.toString()
                if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
                    emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
                }
                emailEditTextPreviousValue = emailEditText.text.toString()
                staffTrainingProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining)
                staffTrainingProcessPreviousValue = staffTrainingProcessEditText.text.toString()
                qualityControlProcessEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl)
                qualityControlProcessPreviousValue = qualityControlProcessEditText.text.toString()
                aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)
                aarSignPreviousValue = aarSignEditText.text.toString()
                certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)
                certificateOfApprovalPreviousValue = certificateOfApprovalEditText.text.toString()
                memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)
                memberBenefitsPosterPreviousValue = memberBenefitsPosterEditText.text.toString()
            }

            if (waiveVisitationCheckBox.isChecked) completeButton.isEnabled = true
        }
        var visitationType = "";
        if (annualVisitationType.isChecked) {
            visitationType = VisitationTypes.Annual.toString()
        } else if (quarterlyVisitationType.isChecked) {
            visitationType = VisitationTypes.Quarterly.toString()
        } else if (adhocVisitationType.isChecked) {
            visitationType = VisitationTypes.AdHoc.toString()
        } else if (defVisitationType.isChecked) {
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
                facilityRepresentativeSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(facilityRepresentativeSignatureImageView);

        imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_SpecSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                Log.v("SIGNATURE ---->", "Failed  " + e?.message.toString())
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                automotiveSpecialistSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(automotiveSpecialistSignatureImageView);
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
                waiversSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(waiversSignatureImageView);

        imgFileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_DefSignature_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString() + ".png"
        Glide.with(this).load(Constants.getImages + imgFileName).apply(requestOptions).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                //TODO: something on exception
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("SIGNATURE ---->", "  LOAD COMPLETED")
                facilityRepresentativeDeficienciesSignatureButton.text = "Edit Signature"
                return false
            }
        }).into(facilityRepresentativeDeficienciesSignatureImageView)

        emailValidation()
        waiverValidation()

        fillDeficiencyTable()
    }

    private fun setFieldsListeners() {

        completeButton.setOnClickListener {
            completeButton.isEnabled = false
            if (validateInputs()) {
                if (duplicateCheckPassed()) {
                    submitVisitationData()
//                    Constants.visitationIDForPDF = "76497";
//                    (activity as FormsActivity).generateAndOpenPDF();
                } else {
                    Utility.showValidationAlertDialog(activity, "Duplicate visitation exists with Visitation ID: $duplicateVisitationID")
                    completeButton.isEnabled = true
                }
            } else {
                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
                completeButton.isEnabled = true
            }
        }


        saveButton.setOnClickListener {
            if (!emailEditText.text.isEmpty() && !emailFormatValidation(emailEditText.text.toString())) {
                emailEditText.setError("please type your email correctly")
            } else {
                emailEditText.setError(null)
                var visitationID = 0
                val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                val clubCode = FacilityDataModel.getInstance().clubCode
                val insertDate = Date().toApiSubmitFormat()
                val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val updateDate = Date().toApiSubmitFormat()
                val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                val facilityRep = facilityRepresentativesSpinner.selectedItem.toString()
                val automotiveSpecialist = if (automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else automotiveSpecialistSpinner.selectedItem.toString()
                val aarSign = if (aarSignEditText.text.isNullOrEmpty()) "" else aarSignEditText.text
                val qa = if (qualityControlProcessEditText.text.toString().isNullOrEmpty()) "" else qualityControlProcessEditText.text.toString()
                val staffTraining = if (staffTrainingProcessEditText.text.toString().isNullOrEmpty()) "" else staffTrainingProcessEditText.text
                val memberBenefits = if (memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else memberBenefitsPosterEditText.text
                val certificateOfApproval = if (certificateOfApprovalEditText.text.isNullOrEmpty()) "" else certificateOfApprovalEditText.text
                var visitationType = ""
//                val visitmethod = if (visitationMethodDropListId.selectedItemPosition==0) 0 else visitationMethodDropListId.selectedItemPosition+1
                val visitmethod = if (visitationMethodDropListId.selectedItemPosition == 0) 0 else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitationMethodDropListId.selectedItem.toString()) }[0].TypeID
                val visitmethodStr = if (visitationMethodDropListId.selectedItemPosition == 0) "" else visitationMethodDropListId.selectedItem.toString()
                if (annualVisitationType.isChecked) {
                    visitationType = VisitationTypes.Annual.toString()
                } else if (quarterlyVisitationType.isChecked) {
                    visitationType = VisitationTypes.Quarterly.toString()
                } else if (adhocVisitationType.isChecked) {
                    visitationType = VisitationTypes.AdHoc.toString()
                } else if (defVisitationType.isChecked) {
                    visitationType = VisitationTypes.Deficiency.toString()
                }
                progressBarTextVal.text = "Saving ..."
                if ((activity as FormsActivity).imageSpecSignature != null)
                    saveBmpAsFile((activity as FormsActivity).imageSpecSignature, "Spec", visitationType)
                if ((activity as FormsActivity).imageRepSignature != null)
                    saveBmpAsFile((activity as FormsActivity).imageRepSignature, "Rep", visitationType)
                if ((activity as FormsActivity).imageDefSignature != null)
                    saveBmpAsFile((activity as FormsActivity).imageDefSignature, "Def", visitationType)
                if ((activity as FormsActivity).imageWaiveSignature != null)
                    saveBmpAsFile((activity as FormsActivity).imageWaiveSignature, "W", visitationType)
                // SUSPECTED RELATED TO MISSING SIGNATURE FROM PDF
//                (activity as FormsActivity).imageSpecSignature = null
//                (activity as FormsActivity).imageRepSignature = null
//                (activity as FormsActivity).imageDefSignature = null
//                (activity as FormsActivity).imageWaiveSignature = null

                dialogueLoadingView.visibility = View.VISIBLE
                var urlString = facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationType.toString() + "&visitationReason=" + visitationReasonDropListId.selectedItem.toString() + "&emailPDF=" + (if (emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + emailEditText.text + "&waiveVisitation=" + (if (waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=" + visitmethod + "&visitMethodStr=" + visitmethodStr + "&comments=" + visitationCommentsEditText.text.toString()

                Log.v("SAVE VISITATION || ", Constants.UpdateVisitationDetailsDataProgress + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Saved ... Type --> " + visitationType))
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsDataProgress + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Saved ... Type --> " + visitationType),
                        Response.Listener { response ->
                            requireActivity().runOnUiThread {
                                Log.v("VT RESPONSE ||| ", response.toString())
                                if (response.toString().contains("Success", false)) {
                                    waiveVisitationCBPreviousValue = waiveVisitationCheckBox.isChecked
                                    emailPdfCBPreviousValue = emailPdfCheckBox.isChecked
                                    waiverCommentsPreviousValue = waiverCommentsEditText.text.toString()
                                    emailEditTextPreviousValue = emailEditText.text.toString()
                                    staffTrainingProcessPreviousValue = staffTrainingProcessEditText.text.toString()
                                    qualityControlProcessPreviousValue = qualityControlProcessEditText.text.toString()
                                    aarSignPreviousValue = aarSignEditText.text.toString()
                                    certificateOfApprovalPreviousValue = certificateOfApprovalEditText.text.toString()
                                    memberBenefitsPosterPreviousValue = memberBenefitsPosterEditText.text.toString()
                                    visitationComments = visitationCommentsEditText.text.toString()
                                    visitMethodPreviousValue = visitationMethodDropListId.selectedItem.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].recordid = 9999999
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation = waiveVisitationCheckBox.isChecked
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf = emailPdfCheckBox.isChecked
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments = waiverCommentsEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto = emailEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].stafftraining = staffTrainingProcessEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].qualitycontrol = qualityControlProcessEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].aarsigns = aarSignEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].certificateofapproval = certificateOfApprovalEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].memberbenefitposter = memberBenefitsPosterEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments = visitationCommentsEditText.text.toString()
                                    PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep = facilityRepresentativesSpinner.selectedItem.toString()
                                    if (!TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitationMethodDropListId.selectedItem.toString()) }.isNullOrEmpty()) {
                                        PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod = TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitationMethodDropListId.selectedItem.toString()) }[0].TypeID.toString()
                                    }
                                    (activity as FormsActivity).saveRequired = false
//                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                    (activity as FormsActivity).saveDone = true
                                    refreshButtonsState()
                                    Utility.showMessageDialog(activity, "Confirmation ...", "Visitation Data Saved Successfully")
                                    (activity as FormsActivity).saveVisitedScreensRequired = false
//                                    IndicatorsDataModel.getInstance().resetAllVisitedFlags()
                                    cancelButton.isEnabled = false
                                    dialogueLoadingView.visibility = View.GONE
                                    progressBarTextVal.text = "Loading ..."
                                } else {
                                    dialogueLoadingView.visibility = View.GONE
                                    progressBarTextVal.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity, false, "Error saving Visitation Details")
                                }
                            }
                        }, Response.ErrorListener {
                    dialogueLoadingView.visibility = View.GONE
                    progressBarTextVal.text = "Loading ..."
                    Utility.showSubmitAlertDialog(activity, false, "Error saving Visitation Details (Error: " + it.message + " )")
                }))
            }
        }




        emailPdfCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility = b
            checkMarkChangesDone()
        }

        facilityRepresentativesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if (p2 > 0) {
                    if (isFacilityRepresentativeSignatureInitialized) {
                        isFacilityRepresentativeSignatureInitialized = false
                    } else {//if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals(facilitySpecialistNames[p2])) {
//                        FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = facilitySpecialistNames[p2]
                        FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null
                        automotiveSpecialistSignatureImageView.setImageBitmap(null)
                    }
                } else {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = ""
                }
                Log.v("SAVEREQUIRED -->", facilityRepresentativesSpinner.tag.toString())
                if (p2 > 0 && (!facilityRepresentativesSpinner.tag.equals(p2) || facilityRepresentativesSpinner.tag.equals("-1"))) {
                    facilityRepresentativesSpinner.tag = "-1"
                    if (firsLoadDone) {
                        (activity as FormsActivity).saveRequired = true
                        refreshButtonsState()
                    }
                }
                checkMarkChangesDone()
            }

        }

        automotiveSpecialistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                if (!automotiveSpecialistSpinner.tag.equals(p2) || automotiveSpecialistSpinner.tag.equals("-1")) {
                    automotiveSpecialistSpinner.tag = "-1"
                    if (firsLoadDone) {
                        (activity as FormsActivity).saveRequired = true
                        Log.v("SAVEREQUIRED -->", " automotiveSpecialistSpinner")
                        refreshButtonsState()
                    }
                }

                checkMarkChangesDone()
            }

        }


        facilityRepresentativeSignatureButton.setOnClickListener {

            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.representative
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature != null)
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature, 0.0f, 0.0f, Paint())
        }

        automotiveSpecialistSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.specialist
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature != null) {
                try {
                    signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature, 0.0f, 0.0f, Paint())
                } catch (e: Exception) {

                }
            }
        }

        facilityRepresentativeDeficienciesSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.representativeDeficiency
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature != null) {
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature, 0.0f, 0.0f, Paint())
            }
        }

        waiversSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.waiver
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature != null) {
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature, 0.0f, 0.0f, Paint())
            }
        }

        signatureClearButton.setOnClickListener {

            signatureInkView.clear()
            checkMarkChangesDone()
        }

        signatureCancelButton.setOnClickListener {
            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
            checkMarkChangesDone()
        }

        signatureConfirmButton.setOnClickListener {

            var bitmap = signatureInkView.bitmap
            var isEmpty = bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config))
            when (selectedSignature) {
                requestedSignature.representative -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = bitmap
                    (activity as FormsActivity).imageRepSignature = bitmap
//                    saveBmpAsFile(bitmap,"Rep")
                    if (!isEmpty) {
                        facilityRepresentativeSignatureButton.text = "Edit Signature"
                        facilityRepresentativeSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        facilityRepresentativeSignatureButton.text = "Add Signature"
                        facilityRepresentativeSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = null

                    }
                }

                requestedSignature.specialist -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = bitmap
                    (activity as FormsActivity).imageSpecSignature = bitmap
//                    saveBmpAsFile(bitmap,"Spec")
                    if (!isEmpty) {
                        automotiveSpecialistSignatureButton.text = "Edit Signature"
                        automotiveSpecialistSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        automotiveSpecialistSignatureButton.text = "Add Signature"
                        automotiveSpecialistSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null

                    }
                }

                requestedSignature.representativeDeficiency -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature = bitmap
                    (activity as FormsActivity).imageDefSignature = bitmap
//                    saveBmpAsFile(bitmap,"Def")
                    if (!isEmpty) {
                        facilityRepresentativeDeficienciesSignatureButton.text = "Edit Signature"
                        facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        facilityRepresentativeDeficienciesSignatureButton.text = "Add Signature"
                        facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeDeficienciesSignature = null

                    }

                }

                requestedSignature.waiver -> {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature = bitmap
                    (activity as FormsActivity).imageWaiveSignature = bitmap
                    if (!isEmpty) {
                        waiversSignatureButton.text = "Edit Signature"
                        waiversSignatureImageView.setImageBitmap(bitmap)
                    } else {
                        waiversSignatureButton.text = "Add Signature"
                        waiversSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature = null

                    }
                }
            }
            (activity as FormsActivity).saveRequired = true
            refreshButtonsState()
            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
            checkMarkChangesDone()
        }

        waiveVisitationCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations = b
            if (b) completeButton.isEnabled = true
            else {
                waiverCommentsEditText.setText("")
                var visitationType = ""
                if (annualVisitationType.isChecked) {
                    visitationType = VisitationTypes.Annual.toString()
                } else if (quarterlyVisitationType.isChecked) {
                    visitationType = VisitationTypes.Quarterly.toString()
                } else if (adhocVisitationType.isChecked) {
                    visitationType = VisitationTypes.AdHoc.toString()
                } else if (defVisitationType.isChecked) {
                    visitationType = VisitationTypes.Deficiency.toString()
                }
                if (visitationType.equals(VisitationTypes.AdHoc) || visitationType.equals(VisitationTypes.Deficiency)) {
                    completeButton.isEnabled = true
                } else {
                    completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
                }
            }
            checkMarkChangesDone()
        }

        aarSignEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns = p0.toString()
                if (firsLoadDone) {
                    if (aarSignEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    aarSignEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " aarSignEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        certificateOfApprovalEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval = p0.toString()
                if (firsLoadDone) {
                    if (certificateOfApprovalEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    certificateOfApprovalEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " certificateOfApprovalEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        qualityControlProcessEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl = p0.toString()
                if (firsLoadDone) {
                    if (qualityControlProcessEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    qualityControlProcessEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " qualityControlProcessEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        staffTrainingProcessEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining = p0.toString()
                if (firsLoadDone) {
                    if (staffTrainingProcessEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    staffTrainingProcessEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " staffTrainingProcessEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        memberBenefitsPosterEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster = p0.toString()
                if (firsLoadDone) {
                    if (memberBenefitsPosterEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    memberBenefitsPosterEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " memberBenefitsPosterEditText")
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        waiverCommentsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments = p0.toString()
                if (firsLoadDone) {
                    if (waiverCommentsEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    waiverCommentsEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " waiverCommentsEditText")
                    memberBenefitsPosterEditText
                    refreshButtonsState()
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
//                FacilityDataModel.getInstance().tblFacilityEmail[0].email = p0.toString()

                if (firsLoadDone) {
                    if (emailEditText.tag == "-1")
                        (activity as FormsActivity).saveRequired = true
                    emailEditText.tag = "-1"
                    Log.v("SAVEREQUIRED -->", " emailEditText")
                    refreshButtonsState()
//                PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf =
                    checkMarkChangesDone()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })


        visitationMethodDropListId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (visitationMethodDropListId.selectedItem.equals("In Person")) {
                    signatureView.visibility = View.VISIBLE
                    signatureRL.visibility = View.VISIBLE
                } else {
                    signatureView.visibility = View.GONE
                    signatureRL.visibility = View.GONE
                }
                if (!visitationMethodDropListId.tag.equals(position) || visitationMethodDropListId.tag.equals("-1")) {
                    visitationMethodDropListId.tag = "-1"
                    if (firsLoadDone) {
                        (activity as FormsActivity).saveRequired = true
                        Log.v("SAVEREQUIRED -->", " visitationMethodDropListId")
                        refreshButtonsState()
                    }
                }
            }
        }

        adhocVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                quarterlyVisitationType.isChecked = false
                annualVisitationType.isChecked = false
                defVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.AdHoc
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }

        }

        defVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                quarterlyVisitationType.isChecked = false
                annualVisitationType.isChecked = false
                adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Deficiency
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        annualVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                quarterlyVisitationType.isChecked = false
                defVisitationType.isChecked = false
                adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Annual
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        quarterlyVisitationType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                annualVisitationType.isChecked = false
                defVisitationType.isChecked = false
                adhocVisitationType.isChecked = false
                FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType = VisitationTypes.Quarterly
                IndicatorsDataModel.getInstance().init()
                IndicatorsDataModel.getInstance().tblVisitation[0].visited = true
                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
            }
        }

        (activity as FormsActivity).saveRequired = false
    }

    fun facilityNameAndNumberRelationForSelection() {

        for (fac in FacilityDataModel.getInstance().tblFacilities) {

            if (fac.FACNo.toString() == facilityNumberEditText.text.toString()) {
                facilityNumberEditText.isEnabled = false

                facilityNameEditText.setText(fac.BusinessName.toString())
                //facilityNameEditText.setText(fac. .toString())


            }
            if (fac.BusinessName == facilityNameEditText.text.toString()) {

                facilityNameEditText.isEnabled = false

                facilityNumberEditText.setText(fac.FACNo.toString())
            }

        }

        var facNameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (facilityNumberEditText.text.toString().isNullOrEmpty()) {

                    for (fac in FacilityDataModel.getInstance().tblFacilities) {


                        if (fac.BusinessName == facilityNameEditText.text.toString()) {

                            facilityNumberEditText.setText(fac.FACNo.toString())
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

                if (facilityNameEditText.text.toString().isNullOrEmpty()) {

                    for (fac in FacilityDataModel.getInstance().tblFacilities) {

                        if (fac.FACNo.toString() == facilityNumberEditText.text.toString()) {

                            facilityNameEditText.setText(fac.BusinessName.toString())


                        }
                    }


                }
            }
        }




        facilityNameEditText.addTextChangedListener(facNameWatcher)
        facilityNumberEditText.addTextChangedListener(facNumberWatcher)

        representativeSignatureConditionedEnabling()

    }


    fun representativeSignatureConditionedEnabling() {

        if (facilityRepresentativesSpinner.selectedItem.toString().isNullOrEmpty() ||
                facilityRepresentativesSpinner.selectedItem.toString().contains("please") ||
                visitationReasonDropListId.selectedItem.toString().isNullOrEmpty() ||
                visitationReasonDropListId.selectedItem.toString().contains("please")) {

            //TODO We should change the background color of the button if the button is NOT enabled
            //facilityRepresentativeSignatureButton.isEnabled=false
        }
        var representativeNameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                facilityRepresentativeSignatureButton.setText("Add Signature")
            }
        }
    }


    fun saveBmpAsFile(bmp: Bitmap?, type: String, visitationType: String) {
        var strPrefix = if (type.equals("Rep")) "RepSignature" else if (type.equals("Spec")) "SpecSignature" else if (type.equals("W")) "WSignature" else "DefSignature"
        strPrefix += "_" + Calendar.getInstance().get(Calendar.MONTH).toString() + "_" + Calendar.getInstance().get(Calendar.YEAR).toString();
        var fileName = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "_" + FacilityDataModel.getInstance().clubCode + "_" + visitationType + "_" + strPrefix + ".png"
        var filePath = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filePath = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
//            filePath = Environment.DIRECTORY_DCIM + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
        } else {
            filePath = Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png"
        }
//        val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_" + FacilityDataModel.getInstance().clubCode + "_" + strPrefix + ".png")
        val file = File(filePath)
        val fOut = FileOutputStream(file);
//        bmp.toDrawable(resources).bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        bmp?.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
        uploadSignature(file, fileName)
    }

    fun uploadSignature(file: File, fileName: String) {
        val multipartRequest = MultipartRequest(Constants.uploadPhoto + fileName, null, file, Response.Listener { response ->
            //            try {
//                submitPhotoDetails()
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
        }, Response.ErrorListener {
            Utility.showMessageDialog(context, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
            Log.v("Upload Signature Error:", it.message.toString())
        })
        val socketTimeout = 30000//30 seconds - change to what you want
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        multipartRequest.retryPolicy = policy
        Volley.newRequestQueue((activity as FormsActivity).applicationContext).add(multipartRequest)
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
        var visitationID = 0
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode = FacilityDataModel.getInstance().clubCode
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID

        val facilityRep = if (facilityRepresentativesSpinner.selectedItem.toString().contains("select")) "" else facilityRepresentativesSpinner.selectedItem.toString()

        val automotiveSpecialist = if (automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else automotiveSpecialistSpinner.selectedItem.toString()
        val aarSign = if (aarSignEditText.text.isNullOrEmpty()) "" else aarSignEditText.text
        val qa = if (qualityControlProcessEditText.text.isNullOrEmpty()) "" else qualityControlProcessEditText.text
        val staffTraining = if (staffTrainingProcessEditText.text.isNullOrEmpty()) "" else staffTrainingProcessEditText.text
        val memberBenefits = if (memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else memberBenefitsPosterEditText.text
        val certificateOfApproval = if (certificateOfApprovalEditText.text.isNullOrEmpty()) "" else certificateOfApprovalEditText.text
        val performedBy = if (automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else automotiveSpecialistSpinner.selectedItem.toString()

        val visitmethodStr = if (visitationMethodDropListId.selectedItemPosition == 0) "" else visitationMethodDropListId.selectedItem.toString()

        val visitmethod = if (visitationMethodDropListId.selectedItemPosition == 0) 0 else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeName.equals(visitmethodStr) }[0].TypeID

        val annualvisitationmonth = FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth
        var dialogMsg = ""
        var visitationType = ""
        var visitationTypeID = ""
        var visitationReasonID = ""
        if (annualVisitationType.isChecked) {
            visitationType = VisitationTypes.Annual.toString()
            visitationTypeID = "1"

        } else if (quarterlyVisitationType.isChecked) {
            visitationType = VisitationTypes.Quarterly.toString()
            visitationTypeID = "2"

        } else if (adhocVisitationType.isChecked) {
            visitationType = VisitationTypes.AdHoc.toString()
            visitationTypeID = "3"

        } else if (defVisitationType.isChecked) {
            visitationType = VisitationTypes.Deficiency.toString()
            visitationTypeID = "4"

        }
        if ((activity as FormsActivity).imageSpecSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageSpecSignature, "Spec", visitationType)
        if ((activity as FormsActivity).imageRepSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageRepSignature, "Rep", visitationType)
        if ((activity as FormsActivity).imageDefSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageDefSignature, "Def", visitationType)
        if ((activity as FormsActivity).imageWaiveSignature != null)
            saveBmpAsFile((activity as FormsActivity).imageWaiveSignature, "W", visitationType)
//        (activity as FormsActivity).imageSpecSignature = null
//        (activity as FormsActivity).imageRepSignature = null
//        (activity as FormsActivity).imageDefSignature = null
//        (activity as FormsActivity).imageWaiveSignature = null
        visitationReasonID = TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeName.equals(visitationReasonDropListId.selectedItem.toString()) }[0].VisitationReasonTypeID.toString()
        progressBarTextVal.text = "Saving ..."
        dialogueLoadingView.visibility = View.VISIBLE

        // Get PRG LOGS to write changes made in PDF

        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "&clubCode=${FacilityDataModel.getInstance().clubCode}&userId=" + ApplicationPrefs.getInstance(context).loggedInUserID,
                Response.Listener { response ->
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
                    }
                }, Response.ErrorListener {
            Log.v("Loading PRG Data error", "" + it.message)
            it.printStackTrace()
        }))

        (activity as FormsActivity).saveRequired = false
        refreshButtonsState()
        (activity as FormsActivity).saveVisitedScreensRequired = false
        completeButton.isEnabled = false

        var urlString = facilityNo + "&clubcode=" + clubCode + "&DatePerformed=" + insertDate + "&DateReceived=" + insertDate + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationTypeID + "&visitationReason=" + visitationReasonID + "&emailPDF=" + (if (emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + emailEditText.text + "&waiveVisitation=" + (if (waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&performedBy=" + automotiveSpecialist + "&visitationID=0&annualVisitationMonth=" + annualvisitationmonth + "&visitMethod=" + visitmethod
        Log.v("Visitation Tracking -- ", Constants.UpdateVisitationTrackingData + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationTrackingData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                Response.Listener { response ->
                    requireActivity().runOnUiThread {
                        Log.v("VT RESPONSE ||| ", response.toString())
                        if (response.toString().contains("returnCode>0<", false)) {
                            visitationID = response.toString().substring(response.toString().indexOf("<visitationID") + 14, response.toString().indexOf("" +
                                    "</visitationID")).toInt()
                            Constants.visitationIDForPDF = visitationID.toString()
                            dialogMsg = "New Visitation with ID (${visitationID}) created succesfully"
                            (activity as FormsActivity).saveRequired = false
                            urlString = facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&visitationType=" + visitationType.toString() + "&visitationReason=" + visitationReasonDropListId.selectedItem.toString() + "&emailPDF=" + (if (emailPdfCheckBox.isChecked) "1" else "0") + "&emailTo=" + emailEditText.text + "&waiveVisitation=" + (if (waiveVisitationCheckBox.isChecked) "1" else "0") + "&waiveComments=" + waiverCommentsEditText.text + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=${visitmethod}&annualVisitationMonth=" + annualvisitationmonth + "&comments=" + visitationCommentsEditText.text.toString()
                            Log.v("Visitation Details --- ", Constants.UpdateVisitationDetailsData + urlString)
                            (activity as FormsActivity).saveDone = true
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Completed ..."),
                                    Response.Listener { response ->
                                        requireActivity().runOnUiThread {
                                            Log.v("VT RESPONSE ||| ", response.toString())
                                            if (response.toString().contains("returnCode>0<", false)) {
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].clubcode = clubCode.toInt()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].facid = facilityNo.toInt()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailpdf = emailPdfCheckBox.isChecked
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].emailto = emailEditText.text.toString()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].facilityrep = facilityRep
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].automotivespecialist = automotiveSpecialist
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].sessionid = ApplicationPrefs.getInstance(activity).sessionID
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].userid = insertBy
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationreason = visitationReasonDropListId.selectedItem.toString()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationtype = visitationType.toString()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivecomments = waiverCommentsEditText.text.toString()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].waivevisitation = waiveVisitationCheckBox.isChecked
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid = visitationID.toString()
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitmethod = visitmethodStr
                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].comments = visitationCommentsEditText.text.toString()
//                                                PRGDataModel.getInstance().tblPRGVisitationHeader[0].visitationid

                                                (activity as FormsActivity).saveRequired = false
//                                                (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                                refreshButtonsState()
                                                (activity as FormsActivity).saveVisitedScreensRequired = false
//                                                IndicatorsDataModel.getInstance().resetAllVisitedFlags()
                                                // Disable Buttons after click complete Button
//                                                if (visitationType.equals(VisitationTypes.AdHoc) || visitationType.equals(VisitationTypes.Deficiency)) {
//                                                    completeButton.isEnabled = true
//                                                } else {
//                                                    completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()
//                                                }
                                                cancelButton.isEnabled = false

                                                if (!waiveVisitationCheckBox.isChecked || waiveVisitationCheckBox.isChecked) {
                                                    if ((activity as FormsActivity).checkPermission()) {
                                                        (activity as FormsActivity).generateAndOpenPDF()
                                                    } else {
                                                        if (!(activity as FormsActivity).checkPermission()) {
                                                            (activity as FormsActivity).requestPermissionAndContinue();
                                                        } else {
                                                            (activity as FormsActivity).generateAndOpenPDF()
                                                        }
                                                    }
                                                    Handler().postDelayed({
                                                        progressBarTextVal.text = "Generating Visitation PDFs ..."
                                                        Handler().postDelayed({
                                                            progressBarTextVal.text = "Sending Visitation PDFs ..."
                                                            Handler().postDelayed({
                                                                progressBarTextVal.text = "Finalizing completion process ..."
                                                                Handler().postDelayed({
                                                                    dialogueLoadingView.visibility = View.GONE
                                                                    progressBarTextVal.text = "Loading ..."
//                                                                Utility.showMessageDialog(activity,"Confirmation ...", dialogMsg)
                                                                    val builder = AlertDialog.Builder(activity)
                                                                    builder.setTitle("Confirmation ...")
                                                                    builder.setMessage(dialogMsg)
                                                                    builder.setPositiveButton("OK"
                                                                    )
                                                                    { dialog, id ->
                                                                        dialog.dismiss()
                                                                        (activity as FormsActivity).onBackPressed()
                                                                    }
                                                                    builder.show()
                                                                }, 5000)
                                                            }, 5000)
                                                        }, 5000)
                                                    }, 5000)
                                                } else {
                                                    dialogueLoadingView.visibility = View.GONE
                                                    progressBarTextVal.text = "Loading ..."
                                                    //                                                                Utility.showMessageDialog(activity,"Confirmation ...", dialogMsg)
                                                    val builder = AlertDialog.Builder(activity)
                                                    builder.setTitle("Confirmation ...")
                                                    builder.setMessage(dialogMsg)
                                                    builder.setPositiveButton("OK"
                                                    )
                                                    { dialog, id ->
                                                        dialog.dismiss()
                                                        (activity as FormsActivity).onBackPressed()
                                                    }
                                                    builder.show()
                                                }
                                            } else {
                                                dialogueLoadingView.visibility = View.GONE
                                                progressBarTextVal.text = "Loading ..."
                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                                                Utility.showSubmitAlertDialog(activity, false, dialogMsg + " ..... Visitation Details (Error: " + errorMessage + " )")
                                            }
                                        }
                                    }, Response.ErrorListener {
                                dialogueLoadingView.visibility = View.GONE
                                progressBarTextVal.text = "Loading ..."
                                Utility.showSubmitAlertDialog(activity, false, "Visitation Details (Error: " + it.message + " )")
                            }))
//                            dialogueLoadingView.visibility = View.GONE
//                            progressBarTextVal.text = "Loading ..."
                        } else {
                            dialogueLoadingView.visibility = View.GONE
                            progressBarTextVal.text = "Loading ..."
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message") + 9, response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + errorMessage + " )")
                        }
                    }
                }, Response.ErrorListener {
            dialogueLoadingView.visibility = View.GONE
            progressBarTextVal.text = "Loading ..."
            Utility.showSubmitAlertDialog(activity, false, "Visitation Tracking (Error: " + it.message + " )")
        }))
    }

    fun emailFormatValidation(target: CharSequence): Boolean {

        if (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            emailValid = true else emailValid = false


        return emailValid
    }

    fun emailValidation() {

        emailEditText.isEnabled = emailPdfCheckBox.isChecked

        emailPdfCheckBox.setOnClickListener {
            emailEditText.isEnabled = emailPdfCheckBox.isChecked
            (activity as FormsActivity).saveRequired = true
            Log.v("SAVEREQUIRED -->", " emailPdfCheckBox")
            refreshButtonsState()
//            if (emailPdfCheckBox.isChecked) {
//                emailEditText.isEnabled = true
//            } else emailEditText.isEnabled = false
        }

    }

    fun waiverValidation() {

        waiverCommentsEditText.isEnabled = waiveVisitationCheckBox.isChecked
        waiverConditionedEnablingLayout.isEnabled = waiveVisitationCheckBox.isChecked

        waiveVisitationCheckBox.setOnClickListener {
            waiverCommentsEditText.isEnabled = waiveVisitationCheckBox.isChecked
            waiverConditionedEnablingLayout.isEnabled = waiveVisitationCheckBox.isChecked
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

                    deficienciesTableLayout.addView(tableRow)
                }
            }
        }
    }

    fun altDeffVisitationTableRow(alt_row: Int) {
        var childViewCount = deficienciesTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = deficienciesTableLayout.getChildAt(i) as TableRow;

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
        if (annualVisitationType.isChecked) {
            visitationTypeID = "1"
        } else if (quarterlyVisitationType.isChecked) {
            visitationTypeID = "2"
        } else if (adhocVisitationType.isChecked) {
            visitationTypeID = "3"
        } else if (defVisitationType.isChecked) {
            visitationTypeID = "4"
        }
        var currentMonth = ""
        currentMonth = if ((Calendar.getInstance().get(Calendar.MONTH) + 1).toString().length == 1) {
            "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        } else {
            (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
        }
        val visitationReasonID = TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeName.equals(visitationReasonDropListId.selectedItem.toString()) }[0].VisitationReasonTypeID.toString()
        if (!FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(5, 7).equals(currentMonth) && s.VisitationReasonTypeID.equals(visitationReasonID) }.isNullOrEmpty()) {
            duplicateVisitationID = FacilityDataModel.getInstance().tblVisitationTracking.filter { s -> s.VisitationTypeID!!.equals(visitationTypeID) && s.DatePerformed.substring(5, 7).equals(currentMonth) && s.VisitationReasonTypeID.equals(visitationReasonID) }[0].visitationID
            return false
        }

        return true
    }

    fun validateInputs(): Boolean {
        var isInputValid = true
        automotiveSpecialistSignatureButton.setError(null)
        facilityRepresentativeSignatureButton.setError(null)
        facilityRepresentativeTextView.setError(null)
        visitationReasonTextView.setError(null)
        waiverCommentsEditText.setError(null)
        waiversSignatureButton.setError(null)
        emailEditText.setError(null)
        visitationMethodTextView.setError(null)
        if (waiveVisitationCheckBox.isChecked) {
            if (waiverCommentsEditText.text.toString().isNullOrEmpty()) {
                isInputValid = false
                waiverCommentsEditText.setError("required field")
            }
            if (waiversSignatureButton.text.toString() == "Add Signature") {
                isInputValid = false
                waiversSignatureButton.setError("required field")
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
            if (visitationMethodDropListId.selectedItemPosition == 0) {
                isInputValid = false
                visitationMethodTextView.setError("Required field")
            }
            if ((adhocVisitationType.isChecked || defVisitationType.isChecked)) {
                if (visitationReasonDropListId.selectedItemPosition == 0) {
                    isInputValid = false
                    visitationReasonTextView.setError("Required field")
                }
            } else {
                if (facilityRepresentativeSignatureButton.text.toString() == "Add Signature" && visitationMethodDropListId.selectedItem.toString().equals("In Person")) {
                    isInputValid = false
                    facilityRepresentativeSignatureButton.setError("Required field")
                }
            }

            if (facilityRepresentativesSpinner.selectedItem.toString().contains("please")) {
                isInputValid = false

                facilityRepresentativeTextView.setError("Required Field")
            }

            if (automotiveSpecialistSignatureButton.text.toString() == "Add Signature") {

                isInputValid = false
                automotiveSpecialistSignatureButton.setError("Required field")

            }
        }
        if (emailPdfCheckBox.isChecked) {

            if (emailEditText.text.toString().isNullOrEmpty()) {
                isInputValid = false
                emailEditText.setError("required field")
            }
        } else {
            emailEditText.setError(null)
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


        if (emailPdfCheckBox.isChecked) {
            if (!emailFormatValidation(emailEditText.text.toString())) {
                isInputValid = false
                emailEditText.setError("please type your email correctly")


            } else {
                emailEditText.setError(null)
            }
        } else {
            emailEditText.setError(null)
        }



        return isInputValid
    }

    fun refreshButtonsState() {

        saveButton.isEnabled = (activity as FormsActivity).saveRequired
        cancelButton.isEnabled = (activity as FormsActivity).saveRequired
    }

    fun handleCancelButtonClick() {

        cancelButton.setOnClickListener {


            val alertDialogBuilder = AlertDialog.Builder(
                    context)

            // set title
            alertDialogBuilder.setTitle("Cancel?")

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to cancel")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        waiveVisitationCheckBox.isChecked = waiveVisitationCBPreviousValue
                        emailPdfCheckBox.isChecked = emailPdfCBPreviousValue
                        waiverCommentsEditText.setText(waiverCommentsPreviousValue)
                        emailEditText.setText(emailEditTextPreviousValue)
                        staffTrainingProcessEditText.setText(staffTrainingProcessPreviousValue)
                        qualityControlProcessEditText.setText(qualityControlProcessPreviousValue)
                        aarSignEditText.setText(aarSignPreviousValue)
                        certificateOfApprovalEditText.setText(certificateOfApprovalPreviousValue)
                        memberBenefitsPosterEditText.setText(memberBenefitsPosterPreviousValue)
                        if (visitMethodPreviousValue.equals(""))
                            visitationMethodDropListId.setSelection(0)
                        else {
//                            var vMethood = TypeTablesModel.getInstance().VisitationMethodType.filter { s->s.TypeID.toString().equals(visitMethodPreviousValue)}[0].TypeName
//                            visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(vMethood))
                            visitationMethodDropListId.setSelection((VisitationMethodArray).indexOf(visitMethodPreviousValue))
                        }
//                        visitationMethodDropListId.setSelection((resources.getStringArray(R.array.visitation_methods)).indexOf(vMethood))

                        (activity as FormsActivity).saveRequired = false
                        refreshButtonsState()
                        dialog.cancel()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel()
                    }

            // create alert dialog
            val alertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()
        }
    }
}




