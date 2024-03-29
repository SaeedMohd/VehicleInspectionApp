package com.inspection.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.Utils.toApiSubmitFormat
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
import kotlinx.android.synthetic.main.facility_group_layout.visitationTrackingButton
import kotlinx.android.synthetic.main.fragment_visitation_form.cancelButton
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.alphaBackgroundForVTrackingDialogs
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_VTrackingCard
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_aarsigns_val
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_certificate_val
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_exitVTrackingDialogeBtnId
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_memberbenefits_val
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_qualitycontrol_val
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_stafftraining_val
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_submitVTracking
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.edit_vid
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.mainTrackingTableLayout
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.mainVTViewLinearId
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.progressBarTexttracking
import kotlinx.android.synthetic.main.fragment_visitation_tracking_sub.trackingLoadingView
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var rowIndex = 0
/**
 * A simple [Fragment] subclass.
 * Use the [VisitationTrackingSubFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitationTrackingSubFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visitation_tracking_sub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IndicatorsDataModel.getInstance().tblFacility[0].VisitationTrackingVisited = true
        (activity as FormsActivity).visitationTrackingButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
        fillVisitationTrackingTableView()
        edit_exitVTrackingDialogeBtnId.setOnClickListener {
            fillVisitationTrackingTableView()
            altTrackingTableRow(2)
            edit_VTrackingCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            alphaBackgroundForVTrackingDialogs.visibility = View.GONE
        }
    }


    fun fillVisitationTrackingTableView(){

        mainVTViewLinearId.isEnabled=true

        if (mainTrackingTableLayout.childCount>1) {
            for (i in mainTrackingTableLayout.childCount - 1 downTo 1) {
                mainTrackingTableLayout.removeViewAt(i)
            }
        }
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 0.5F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.0F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1.0F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1.0F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1.0F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1.0F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 0.6F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblVisitationTracking.apply {
            (0 until size).forEach {
                if (get(it).visitationID>"-1") {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.setTextColor(Color.BLACK)
                    textView.minimumHeight = 30
                    textView.text = get(it).visitationID
//                    for (fac in TypeTablesModel.getInstance().AARAffiliationType) {
//                        if (get(it).AffiliationTypeID.toString().equals(fac.AARAffiliationTypeID)) {
//                            textView.text = fac.AffiliationTypeName
//                        }
//                    }
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 14f
                    textView1.setTextColor(Color.BLACK)
                    textView1.minimumHeight = 30
//                textView1.text = get(it).LoggedIntoPortal
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
                    textView1.text = visitationType
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 14f
                    textView2.setTextColor(Color.BLACK)
                    textView2.minimumHeight = 30
                    textView2.text = if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER_VERTICAL
                    textView3.textSize = 14f
                    textView3.setTextColor(Color.BLACK)
                    textView3.minimumHeight = 30
                    textView3.text = get(it).performedBy
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER_VERTICAL
                    textView4.textSize = 14f
                    textView4.setTextColor(Color.BLACK)
                    textView4.minimumHeight = 30
                    textView4.text = if (get(it).VisitationMethodTypeID.equals("") || get(it).VisitationMethodTypeID.equals("0")) "" else TypeTablesModel.getInstance().VisitationMethodType.filter { s -> s.TypeID.toString().equals(get(it).VisitationMethodTypeID) }[0].TypeName
                    tableRow.addView(textView4)

                    val textView5 = TextView(context)
                    textView5.layoutParams = rowLayoutParam5
                    textView5.gravity = Gravity.CENTER_VERTICAL
                    textView5.textSize = 14f
                    textView5.setTextColor(Color.BLACK)
                    textView5.minimumHeight = 30
                    textView5.text = if (get(it).VisitationReasonTypeID.equals("") || get(it).VisitationReasonTypeID.equals("0")) "" else TypeTablesModel.getInstance().VisitationReasonType.filter { s -> s.VisitationReasonTypeID.toString().equals(get(it).VisitationReasonTypeID) }[0].VisitationReasonTypeName
                    tableRow.addView(textView5)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam6
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 14f
                    updateButton.minimumHeight = 30
                    updateButton.isEnabled=true
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)

                    updateButton.setOnClickListener {
                        rowIndex = mainTrackingTableLayout.indexOfChild(tableRow)
                        edit_vid.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].visitationID)
                        edit_aarsigns_val.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].AARSigns)
                        edit_qualitycontrol_val.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].QualityControl)
                        edit_stafftraining_val.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].StaffTraining)
                        edit_memberbenefits_val.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].MemberBenefitPoster)
                        edit_certificate_val.setText(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].CertificateOfApproval)
                        edit_VTrackingCard.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        alphaBackgroundForVTrackingDialogs.visibility = View.VISIBLE

                        var childViewCount = mainTrackingTableLayout.getChildCount();
                        edit_submitVTracking.setOnClickListener {
                            var visitationID = FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].visitationID
                            val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
                            val clubCode = FacilityDataModel.getInstance().clubCode
                            val insertDate = Date().toApiSubmitFormat()
                            val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                            val updateDate = Date().toApiSubmitFormat()
                            val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
                            val facilityRep = FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].facilityRepresentativeName
                            val automotiveSpecialist = FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].automotiveSpecialistName
                            val aarSign = if (edit_aarsigns_val.text.isNullOrEmpty()) "" else edit_aarsigns_val.text.toString()
                            val qa = if (edit_qualitycontrol_val.text.isNullOrEmpty()) "" else edit_qualitycontrol_val.text.toString()
                            Log.v("STAFF TRAINING -->" , edit_stafftraining_val.text.toString())
                            val staffTraining = if (edit_stafftraining_val.text.isNullOrEmpty()) "" else edit_stafftraining_val.text.toString()
                            val memberBenefits = if (edit_memberbenefits_val.text.isNullOrEmpty()) "" else edit_memberbenefits_val.text.toString()
                            val certificateOfApproval = if (edit_certificate_val.text.isNullOrEmpty()) "" else edit_certificate_val.text.toString()
                            val visitmethod = FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].VisitationMethodTypeID
                            val visitmethodStr = TypeTablesModel.getInstance().VisitationMethodType.filter { s->s.TypeID.toString().equals(FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].VisitationMethodTypeID) }

                            progressBarTexttracking.text = "Saving ..."
                            var urlString = facilityNo + "&clubcode=" + clubCode + "&StaffTraining=" + staffTraining + "&QualityControl=" + qa + "&AARSigns=" + aarSign + "&MemberBenefitPoster=" + memberBenefits + "&CertificateOfApproval=" + certificateOfApproval + "&insertBy=" + insertBy + "&insertDate=" + insertDate + "&updateBy=" + updateBy + "&updateDate=" + updateDate + "&sessionId=" + ApplicationPrefs.getInstance(activity).sessionID + "&userId=" + insertBy + "&headerUpdate=NO&visitationReason=" + textView5.text + "&emailPDF=" + (if (FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].emailVisitationPdfToFacility) "1" else "0") + "&emailTo=" + FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].email + "&waiveVisitation=" + (if (FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].waiveVisitations) "1" else "0") + "&waiveComments=" + FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].waiverComments + "&facilityRep=" + facilityRep + "&automotiveSpecialist=" + automotiveSpecialist + "&visitationId=" + visitationID + "&visitMethod=" + visitmethod + "&annualVisitationMonth=" + FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth + "&visitMethodStr=" + visitmethodStr
                            Log.v("URL STRING",urlString)
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsData + urlString + Utility.getLoggingParameters(activity, 0, "Visitation Saved ... Type --> " + visitationType),
                                    Response.Listener { response ->
                                        requireActivity().runOnUiThread {
                                            Log.v("VT RESPONSE ||| ", response.toString())
                                            if (response.toString().contains("Success", false)) {
                                                FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].StaffTraining=staffTraining
                                                FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].QualityControl=qa
                                                FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].MemberBenefitPoster=memberBenefits
                                                FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].CertificateOfApproval=certificateOfApproval
                                                FacilityDataModel.getInstance().tblVisitationTracking[rowIndex-1].AARSigns=aarSign
                                                (activity as FormsActivity).saveRequired = false
                                                (activity as FormsActivity).saveDone = true
                                                Utility.showMessageDialog(activity, "Confirmation ...", "Visitation Data Saved Successfully")
                                                (activity as FormsActivity).saveVisitedScreensRequired = false
//                                            cancelButton.isEnabled = false
                                                trackingLoadingView.visibility = View.GONE
                                                progressBarTexttracking.text = "Loading ..."
                                                edit_VTrackingCard.visibility = View.GONE
                                                (activity as FormsActivity).overrideBackButton = false
                                                alphaBackgroundForVTrackingDialogs.visibility = View.GONE
                                            } else {
                                                trackingLoadingView.visibility = View.GONE
                                                progressBarTexttracking.text = "Loading ..."
                                                Utility.showSubmitAlertDialog(activity, false, "Error saving Visitation Details")
                                            }
                                        }
                                    }, Response.ErrorListener {
                                trackingLoadingView.visibility = View.GONE
                                progressBarTexttracking.text = "Loading ..."
                                Utility.showSubmitAlertDialog(activity, false, "Error saving Visitation Details (Error: " + it.message + " )")
                            }))
                        }
                    }

//                    edit_submitNewAffil.setOnClickListener {
//
//                        if (validateInputsForUpdate()) {
//                            progressBarText.text = "Saving ..."
//                            affLoadingView.visibility = View.VISIBLE
//                            var startDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
//                            var endDate = if (edit_afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlsexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
//                            var comment = edit_affcomments_editTextVal.text.toString()
////
//                            var affTypeID = TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AffiliationTypeName.equals(edit_affiliations_textviewVal.selectedItem.toString()) }[0].AARAffiliationTypeID
//                            var affDetailID = if (edit_afDetails_textviewVal.selectedItem != null) TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(edit_afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID else "0"
//                            var affiliationID = if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID>-1) FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID else ""
//                            indexToRemove = rowIndex
//                            Log.v("AFFILIATION EDIT --- ", Constants.UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}")
//                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 1, getAffiliationChanges(1,rowIndex-1)),
//                                    Response.Listener { response ->
//                                        activity!!.runOnUiThread {
//                                            if (response.toString().contains("returnCode>0<",false)) {
//                                                Utility.showSubmitAlertDialog(activity, true, "Affiliation")
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
//                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment= comment
//
//                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
//                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
//                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
//                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
//                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].comment= comment
//                                                (activity as FormsActivity).saveDone = true
//                                                affLoadingView.visibility = View.GONE
//                                                progressBarText.text = "Loading ..."
//                                                fillAffTableView()
//                                                altLocationTableRow(2)
//                                                HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
//                                                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
//                                            } else {
//                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
//                                                Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
//                                            }
//                                            affLoadingView.visibility = View.GONE
//                                            (activity as FormsActivity).overrideBackButton = false
//                                            edit_affiliationsCard.visibility = View.GONE
//                                            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
//                                        }
//                                    }, Response.ErrorListener {
//                                Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
//                                affLoadingView.visibility = View.GONE
//                                (activity as FormsActivity).overrideBackButton = false
//                                edit_affiliationsCard.visibility = View.GONE
//                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
//                            }))
//                        } else
//                            Utility.showValidationAlertDialog(activity, "Please fill all required fields \nExpiration Date should be after Effective Date")
//                    }
                    mainTrackingTableLayout.addView(tableRow)
                }
            }
        }

    }

    fun altTrackingTableRow(alt_row: Int) {
        var childViewCount = mainTrackingTableLayout.getChildCount();

        for (i in 1..childViewCount - 1) {
            var row: TableRow = mainTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }


        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VisitationTrackingSubFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                VisitationTrackingSubFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}