package com.inspection.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.inspection.R

import kotlinx.android.synthetic.main.fragment_visitation_form.*
import java.util.*
import kotlin.collections.ArrayList
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.Utils.*
import com.inspection.model.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentVisitation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentVisitation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVisitation : Fragment() {
//    var specialistWatcher=""

    var isFacilityRepresentativeSignatureInitialized = false
    var isAutomotiveSpecialistSignatureInitialized = false
//    var isWaiverSignatureInitialized = false
//    var isFacilityRepresentativeDeficiencySignatureInitialized = false

    var facilityRepresentativeNames = ArrayList<String>()
    var facilitySpecialistNames = ArrayList<String>()

    enum class requestedSignature {
        representative, specialist, representativeDeficiency, waiver
    }

    var selectedSignature: requestedSignature? = null

    var emailValid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layo ut for this fragment
        return inflater!!.inflate(R.layout.fragment_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FacilityDataModelOrg.getInstance().changeWasDone = false
        dataChangedNoRadioButton.isClickable=false
        dataChangedYesRadioButton.isClickable=false
        dataChangeHandling()
        checkMarkChangesDone()
        initializeFields()
        setFieldsValues()
        setFieldsListeners()
        fillTrackingData()
        IndicatorsDataModel.getInstance().tblVisitation[0].visited = true

        completeButton.isEnabled = IndicatorsDataModel.getInstance().validateAllScreensVisited()

        (activity as FormsActivity).visitationTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()


    }

    fun fillTrackingData(){
        if (trackingTableLayout.childCount>1) {
            for (i in trackingTableLayout.childCount - 1 downTo 1) {
                trackingTableLayout.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 2F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblVisitationTracking.apply {
            (0 until size).forEach {
                if (!get(it).performedBy.equals("00")) {
                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30
                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    textView.text = get(it).performedBy
                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 18f
                    textView1.minimumHeight = 30
                    textView1.text = if (get(it).DatePerformed.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).DatePerformed.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView1)

                    trackingTableLayout.addView(tableRow)
                }
            }
        }

    }



    fun checkMarkChangesDone(){
        IndicatorsDataModel.getInstance().validateVisitationSectionVisited()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun dataChangeHandling(){
            dataChangedYesRadioButton.isChecked= HasChangedModel.getInstance().changeWasMadeFroAny()
            dataChangedNoRadioButton.isChecked= ! dataChangedYesRadioButton.isChecked
    }

    private fun initializeFields() {

        annualVisitationType.isClickable = false
        annualVisitationType.isEnabled = false

        quarterlyVisitationType.isClickable = false
        quarterlyVisitationType.isEnabled = false

        adhocVisitationType.isClickable = false
        dataChangedYesRadioButton.isClickable = false

        adhocVisitationType.isEnabled = false

        dateOfVisitationButton.isClickable = false
        dataChangedNoRadioButton.isClickable = false
        clubCodeEditText.isClickable = false

        visitationReasonDropListId.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, resources.getStringArray(R.array.visitation_reasons).sorted())

        clubCodeEditText.isEnabled = false
        facilityNumberEditText.isEnabled = false

        if (annualVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(6)
        }
        if (quarterlyVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(7)
        }
        if (adhocVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(0)
        }

        if (adhocVisitationType.isChecked) {
            visitationReasonDropListId.isEnabled = true
            visitationReasonDropListId.isClickable = true
        } else {
            visitationReasonDropListId.isEnabled = false
            visitationReasonDropListId.isClickable = false
        }

        handleCancelButtonClick()

        altDeffVisitationTableRow(2)
    }

    private fun setFieldsValues() {

        facilityRepresentativeNames.clear()
        facilityRepresentativeNames.add("please select a representative")

        for (fac in FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName + " " + s.LastName }.distinct()) {
            facilityRepresentativeNames.add(fac)
        }



//        annualVisitationType.isChecked = (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Annual))
//        adhocVisitationType.isChecked = (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.Annual))
//        quarterlyVisitationType.isChecked = !(annualVisitationType.isChecked || adhocVisitationType.isChecked)

        dateOfVisitationButton.text = Date().toAppFormatMMDDYYYY()
        clubCodeEditText.setText(FacilityDataModel.getInstance().clubCode)
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
            qualityControlProcessEditText.setText(" " + FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            staffTrainingProcessEditText.setText(" " + FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining.replace(".  ", ". ").replace(". ", ".\n"))
        }

        if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
            emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {


            facilitySpecialistNames.add("Select Specialist")
            for (specialist in TypeTablesModel.getInstance().EmployeeList.sortedWith(compareBy { it.FullName })) {
                facilitySpecialistNames.add(specialist.FullName)
            }

            facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, facilityRepresentativeNames)
            //   automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname })
            automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, facilitySpecialistNames)

            facilityNameAndNumberRelationForSelection()
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName))
//            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.toUpperCase()))
            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))
        }


        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature != null) {
                isFacilityRepresentativeSignatureInitialized = true
                facilityRepresentativeSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature)
            }
        }

        if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature != null) {
                isAutomotiveSpecialistSignatureInitialized = true
                automotiveSpecialistSignatureImageView.setImageBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature)
            }
        }

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

            }
            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNotEmpty()) {
                    facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                }
            }

//            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
//                if (FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.isNotEmpty()) {
////                    facilityRepresentativesSpinner.setSelection(CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                    facilityRepresentativesSpinner.setSelection(TypeTablesModel.getInstance().EmployeeList.map { s -> s.FullName}.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
//                }
//            }

            facilityRepresentativesSpinner.setSelection(facilityRepresentativeNames.indexOf(ApplicationPrefs.getInstance(activity).loggedInUserFullName))

            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                waiveVisitationCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations
                emailPdfCheckBox.isChecked = FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility
                waiverCommentsEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments)
            }

            if (FacilityDataModel.getInstance().tblFacilityEmail.size > 0) {
                emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
            }

        }



        emailValidation()
        waiverValidation()

        fillDeficiencyTable()
    }

    private fun setFieldsListeners() {

        completeButton.setOnClickListener {
            if (validateInputs()) {
                submitVisitationData()
            } else {
                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
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
                if (p2>0) {
                    if (isFacilityRepresentativeSignatureInitialized) {
                        isFacilityRepresentativeSignatureInitialized = false
                    } else if (!FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy.equals(facilitySpecialistNames[p2])) {
                        FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = facilitySpecialistNames[p2]

                        FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null

                        automotiveSpecialistSignatureImageView.setImageBitmap(null)
                    }
                } else {
                    FacilityDataModel.getInstance().tblVisitationTracking[0].performedBy = ""
                }

                checkMarkChangesDone()
            }

        }

        automotiveSpecialistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Adding condition as a workaround not to lost the applied changes for signature. As this method is called also during adapter initialization
                if (p2>0) {
                    if (isAutomotiveSpecialistSignatureInitialized) {
                        isAutomotiveSpecialistSignatureInitialized = false
                    } else {
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = facilitySpecialistNames[p2]
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature= null
                        automotiveSpecialistSignatureImageView.setImageBitmap(null)

                    }
                } else{
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName= ""
                }
                checkMarkChangesDone()

            }

        }


        facilityRepresentativeSignatureButton.setOnClickListener {

            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.representative
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature  != null) {
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature, 0.0f, 0.0f, Paint())
            }

        }

        automotiveSpecialistSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.specialist
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature != null) {
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature, 0.0f, 0.0f, Paint())
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
                signatureInkView.drawBitmap(FacilityDataModel.getInstance().tblVisitationTracking[0].waiverSignature , 0.0f, 0.0f, Paint())
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

            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
            checkMarkChangesDone()

        }

        waiveVisitationCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations = b
            checkMarkChangesDone()
        }

        aarSignEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns = p0.toString()

                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        certificateOfApprovalEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval = p0.toString()

                checkMarkChangesDone()

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        qualityControlProcessEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl = p0.toString()

                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        staffTrainingProcessEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining = p0.toString()

                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        memberBenefitsPosterEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster = p0.toString()

                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        waiverCommentsEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments = p0.toString()

                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        emailEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblFacilityEmail[0].email = p0.toString()
                checkMarkChangesDone()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

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

        if ( facilityRepresentativesSpinner.selectedItem.toString().isNullOrEmpty() ||
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


    fun getVisitationChanges() : String {
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

    fun submitVisitationData(){
        var visitationID = 0
        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
        val clubCode =FacilityDataModel.getInstance().clubCode
        val insertDate = Date().toApiSubmitFormat()
        val insertBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val updateDate = Date().toApiSubmitFormat()
        val updateBy = ApplicationPrefs.getInstance(activity).loggedInUserID
        val aarSign = if (aarSignEditText.text.isNullOrEmpty()) "" else aarSignEditText.text
        val qa = if (qualityControlProcessEditText.text.isNullOrEmpty()) "" else qualityControlProcessEditText.text
        val staffTraining = if (staffTrainingProcessEditText.text.isNullOrEmpty()) "" else staffTrainingProcessEditText.text
        val memberBenefits = if (memberBenefitsPosterEditText.text.isNullOrEmpty()) "" else memberBenefitsPosterEditText.text
        val certificateOfApproval = if (certificateOfApprovalEditText.text.isNullOrEmpty()) "" else certificateOfApprovalEditText.text
        val performedBy = if (automotiveSpecialistSpinner.selectedItem.toString().contains("Select")) "" else automotiveSpecialistSpinner.selectedItem.toString()
        var dialogMsg = ""

        progressBarTextVal.text = "Saving ..."
        dialogueLoadingView.visibility = View.VISIBLE
        var urlString = facilityNo+"&clubcode="+clubCode+"&visitationID="+visitationID+"&performedBy="+performedBy+"&DatePerformed="+insertDate+"&DateReceived="+insertDate+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate
        Log.v("Visitation Tracking -- ",Constants.UpdateVisitationTrackingData + urlString)
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationTrackingData + urlString,
                Response.Listener { response ->
                    activity!!.runOnUiThread {
                        Log.v("VT RESPONSE ||| ",response.toString())
                        if (response.toString().contains("returnCode>0<",false)) {
                            visitationID = response.toString().substring(response.toString().indexOf("<visitationID")+14,response.toString().indexOf("" +
                                    "</visitationID")).toInt()
                            dialogMsg = "New Visitation with ID (${visitationID}) created succesfully"
                            (activity as FormsActivity).saveRequired = false
                            urlString = facilityNo+"&clubcode="+clubCode+"&StaffTraining="+staffTraining+"&QualityControl="+qa+"&AARSigns="+aarSign+"&MemberBenefitPoster="+memberBenefits+"&CertificateOfApproval="+certificateOfApproval+"&insertBy="+insertBy+"&insertDate="+insertDate+"&updateBy="+updateBy+"&updateDate="+updateDate
                            Log.v("Visitation Details --- ",Constants.UpdateVisitationDetailsData + urlString)
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.UpdateVisitationDetailsData + urlString,
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread {
                                            Log.v("VT RESPONSE ||| ",response.toString())
                                            if (response.toString().contains("returnCode>0<",false)) {
                                                Utility.showMessageDialog(activity,"Confirmation ...", dialogMsg)
                                                (activity as FormsActivity).saveRequired = false
                                            } else {
                                            dialogueLoadingView.visibility = View.GONE
                                            progressBarTextVal.text = "Loading ..."
                                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                            Utility.showSubmitAlertDialog(activity,false,dialogMsg + " ..... Visitation Details (Error: "+ errorMessage+" )")
                                            }
                                        }
                                    }, Response.ErrorListener {
                                    dialogueLoadingView.visibility = View.GONE
                                    progressBarTextVal.text = "Loading ..."
                                    Utility.showSubmitAlertDialog(activity,false,"Visitation Details (Error: "+it.message+" )")
                                }))
                            dialogueLoadingView.visibility = View.GONE
                            progressBarTextVal.text = "Loading ..."
                        } else {
                            dialogueLoadingView.visibility = View.GONE
                            progressBarTextVal.text = "Loading ..."
                            var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                            Utility.showSubmitAlertDialog(activity,false,"Visitation Tracking (Error: "+ errorMessage+" )")
                        }
                    }
                }, Response.ErrorListener {
                dialogueLoadingView.visibility = View.GONE
                progressBarTextVal.text = "Loading ..."
                Utility.showSubmitAlertDialog(activity,false,"Visitation Tracking (Error: "+it.message+" )")
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
                if (!get(it).DefTypeID.equals("-1")) {
                    var tableRow = TableRow(context)

                    if (tableRowColorSwitch) {
                        tableRow.setBackgroundColor(ContextCompat.getColor(context!!, R.color.table_row_color))
                    } else {
                        tableRow.setBackgroundColor(Color.WHITE)
                    }

                    tableRowColorSwitch = !tableRowColorSwitch //Switching smartly :)

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = TypeTablesModel.getInstance().AARDeficiencyType.filter { s -> s.DeficiencyTypeID.toString() == get(it).DefTypeID }[0].DeficiencyName
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
                    textView.text = get(it).ClearedDate.apiToAppFormatMMDDYYYY()
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


    fun validateInputs(): Boolean {

        var isInputValid = true
        automotiveSpecialistSignatureButton.setError(null)
        facilityRepresentativeSignatureButton.setError(null)
        facilityRepresentativeTextView.setError(null)


        if (adhocVisitationType.isChecked) {
            if (visitationReasonDropListId.selectedItem.toString() == visitationReasonDropListId.setSelection(0).toString()) {
                isInputValid = false
                Utility.showValidationAlertDialog(activity,"Please select visitation reason")
            }

        } else {

            if (facilityRepresentativeSignatureButton.text.toString() == "Add Signature") {
                isInputValid = false
                facilityRepresentativeSignatureButton.setError("Required field")
            }
            if (facilityRepresentativesSpinner.selectedItem.toString().contains("please")) {
                isInputValid = false

                facilityRepresentativeTextView.setError("Required Field")
            }

            if (automotiveSpecialistSignatureButton.text.toString() == "Add Signature") {

                isInputValid = false
                automotiveSpecialistSignatureButton.setError("Required field")

            }

            if (emailPdfCheckBox.isChecked) {

                if (emailEditText.text.toString().isNullOrEmpty()) {
                    isInputValid = false
                    emailEditText.setError("required field")
                }
            } else {
                emailEditText.setError(null)
            }

            if (waiveVisitationCheckBox.isChecked) {

                if (waiverCommentsEditText.text.toString().isNullOrEmpty() || waiversSignatureButton.text.toString().isNullOrEmpty()) {

                    isInputValid = false
                    waiverCommentsEditText.setError("required field")
                    waiversSignatureButton.setError("required field")
                }

            } else {
                waiverCommentsEditText.setError(null)
                waiversSignatureButton.setError(null)
            }


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

        }


        return isInputValid
    }

    fun handleCancelButtonClick() {

        cancelButton.setOnClickListener {


            val alertDialogBuilder = AlertDialog.Builder(
                    context)

            // set title
            alertDialogBuilder.setTitle("Your Title")

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to cancel")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        // if this button is clicked, close
                        // current activity
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




