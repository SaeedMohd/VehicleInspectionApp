package com.inspection.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.inspection.R
import com.inspection.Utils.toAppFormat
import com.inspection.model.CsiSpecialistSingletonModel
import com.inspection.model.FacilityDataModel

import kotlinx.android.synthetic.main.fragment_visitation_form.*
import java.util.*
import kotlin.collections.ArrayList
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import androidx.core.view.setPadding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.fixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.laborRateMatrixMax
import com.inspection.model.FacilityDataModelOrg


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentVisitation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentVisitation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVisitation : Fragment() {
    var specialistWatcher=""

    var isFacilityRepresentativeSignatureInitialized = false
    var isAutomotiveSpecialistSignatureInitialized = false
    var isWaiverSignatureInitialized = false
    var isFacilityRepresentativeDeficiencySignatureInitialized = false

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
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FacilityDataModelOrg.getInstance().changeWasDone = false
        dataChangeHandling()
        checkMarkChangesDone()
        initializeFields()
        setFieldsValues()
        setFieldsListeners()


    }


    fun checkMarkChangesDone(){
        FacilityDataModelOrg.getInstance().changeWasDone = false
        fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService()
        scopeOfServiceChangesWatcher()
        FacilityGeneralInformationFragment().checkMarkChangesWasDoneForFacilityGeneralInfo()
        FragmentARRAVPrograms().checkMarkChangesWasDone()
        FragmentARRAVRepairShopPortalAddendum().checkMarkChangesWasDone()
        FragmentARRAVPersonnel().checkMarkChangesWasDoneForPersonnel()
        FragmentARRAVFacilityServices().checkMarkChangesWasDone()
        FragmentARRAVDeficiency().checkMarkChangesWasDone()
        FragmentARRAVAmOrderTracking().checkMarkChangesWasDone()
        FragmentARRAVLocation().checkMarkChangesWasDoneForAddressTable()
        FragmentARRAVLocation().checkMarkChangesWasDoneForEmailTable()
        FragmentARRAVLocation().checkMarkChangesWasDoneForPhoneTable()
        FacilityGeneralInformationFragment().checkMarkChangesWasDoneForFacilityGeneralInfo()
        checkMarkChangesWasDone()

        dataChangeHandling()


    }
    fun dataChangeHandling(){

        if (FacilityDataModelOrg.getInstance().changeWasDone==true ){
            dataChangedYesRadioButton.isChecked=true
            dataChangedNoRadioButton.isChecked=false
        }else{
            dataChangedNoRadioButton.isChecked=true
            dataChangedYesRadioButton.isChecked=false
        }


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

        annualVisitationType.isChecked = true

        dateOfVisitationButton.text = Date().toAppFormat()

        clubCodeEditText.setText("004")


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
            for (specialist in CsiSpecialistSingletonModel.getInstance().csiSpecialists){

                facilitySpecialistNames.add(specialist.specialistname)
            }
            facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, facilityRepresentativeNames)
            //   automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname })
            automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, facilitySpecialistNames)

            facilityNameAndNumberRelationForSelection()
            automotiveSpecialistSpinner.setSelection(facilitySpecialistNames.indexOf(if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()) 0 else FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName))

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

            if (FacilityDataModel.getInstance().tblVisitationTracking.size > 0) {
                if (FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNotEmpty()) {
                    facilityRepresentativesSpinner.setSelection(CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }.indexOf(FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName))
                }
            }

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

        completeButton.setOnClickListener(View.OnClickListener {
            if (validateInputs()) {
                Toast.makeText(context, "inputs validated", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "missing required fields", Toast.LENGTH_SHORT).show()
        })

        emailPdfCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].emailVisitationPdfToFacility = b
        }

        facilityRepresentativesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Adding condition as a workaround not to lost the applied changes for signature. As this method is called also during adapter initialization
                if (p2 == 0){
                    FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName =""

                }else if (p2 > 0) {
                    if(isFacilityRepresentativeSignatureInitialized){
                        isFacilityRepresentativeSignatureInitialized = false
                    }else {
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName = facilityRepresentativeNames.get(p2)
                        facilityRepresentativeSignatureImageView.setImageBitmap(null)
                        FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeSignature = null
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


                Log.v("dataHandle9",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + p2.toString())


                if (p2>0) {
                    //FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName = CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }[p2]
                    FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName = facilitySpecialistNames[p2]
                    FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistSignature = null
                    automotiveSpecialistSignatureImageView.setImageBitmap(null)

                }else{
                    FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName = ""


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
        }

        signatureCancelButton.setOnClickListener {
            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
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
                    }
                }


            }

            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
        }

        waiveVisitationCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            FacilityDataModel.getInstance().tblVisitationTracking[0].waiveVisitations = b
        }

        aarSignEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns = p0.toString()
                if (FacilityDataModelOrg.getInstance().tblVisitationTracking.size > 0 && p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].AARSigns){
                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle7", p0.toString())

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        certificateOfApprovalEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].CertificateOfApproval){
                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle6", p0.toString())

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        qualityControlProcessEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].QualityControl){
                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle5", p0.toString())

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        staffTrainingProcessEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].StaffTraining){

                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle4", p0.toString())

                }else{


                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        memberBenefitsPosterEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].MemberBenefitPoster){

                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle3", p0.toString())

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        waiverCommentsEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblVisitationTracking[0].waiverComments = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].waiverComments){

                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle2", p0.toString())

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        emailEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                FacilityDataModel.getInstance().tblFacilityEmail[0].email = p0.toString()
                if (p0.toString()!=FacilityDataModelOrg.getInstance().tblVisitationTracking[0].email){

                    MarkChangeWasDone()
                    dataChangeHandling()
                    Log.v("dataHandle1", p0.toString())


                }
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

                facilityNameEditText.setText(fac.EntityName.toString())


            }
            if (fac.EntityName == facilityNameEditText.text.toString()) {

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


                        if (fac.EntityName == facilityNameEditText.text.toString()) {

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

                            facilityNameEditText.setText(fac.EntityName.toString())


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
    fun checkMarkChangesWasDone(){

        Log.v("dataHandle11",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName)


        if (!FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()&&!FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()){

            if (FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName!=FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName){
                MarkChangeWasDone()
                Log.v("dataHandle10",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName)

            }
        }
        if (FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()&&!FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName.isNullOrBlank()) {
            MarkChangeWasDone()
            Log.v("dataHandle12",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName)

        }

        if (!FacilityDataModelOrg.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNullOrBlank()&&!FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNullOrBlank()){

            if (FacilityDataModelOrg.getInstance().tblVisitationTracking[0].facilityRepresentativeName!=FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName){
                MarkChangeWasDone()
                Log.v("dataHandle10",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName)

            }
        }
        if (FacilityDataModelOrg.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNullOrBlank()&&!FacilityDataModel.getInstance().tblVisitationTracking[0].facilityRepresentativeName.isNullOrBlank()) {
            MarkChangeWasDone()
            Log.v("dataHandle12",automotiveSpecialistSpinner.selectedItemPosition.toString() + "====" + FacilityDataModelOrg.getInstance().tblVisitationTracking[0].automotiveSpecialistName)

        }



    }


    fun emailFormatValidation(target: CharSequence): Boolean {

        if (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            emailValid = true else emailValid = false


        return emailValid
    }

    fun emailValidation() {
        if (emailPdfCheckBox.isChecked == true) {

            emailEditText.isEnabled = true
        } else emailEditText.isEnabled = false

        emailPdfCheckBox.setOnClickListener(View.OnClickListener {
            if (emailPdfCheckBox.isChecked == true) {

                emailEditText.isEnabled = true
            } else emailEditText.isEnabled = false
        })

    }

    fun waiverValidation() {
        if (waiveVisitationCheckBox.isChecked == true) {

            waiverCommentsEditText.isEnabled = true
            waiverConditionedEnablingLayout.isEnabled = true
        } else
            waiverCommentsEditText.isEnabled = false
        waiverConditionedEnablingLayout.isEnabled = false

        waiveVisitationCheckBox.setOnClickListener(View.OnClickListener {
            if (waiveVisitationCheckBox.isChecked == true) {

                waiverCommentsEditText.isEnabled = true
                waiverConditionedEnablingLayout.isEnabled = true
            } else
                waiverCommentsEditText.isEnabled = false
            waiverConditionedEnablingLayout.isEnabled = false
        })

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
                textView.text = get(it).Comments
                textView.setPadding(5)
                textView.gravity = Gravity.CENTER_VERTICAL
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).VisitationDate
                textView.gravity = Gravity.CENTER_VERTICAL
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.text = get(it).ClearedDate
                tableRow.addView(textView)

                deficienciesTableLayout.addView(tableRow)
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


    fun validateInputs(): Boolean {

        var isInputValid = true
        automotiveSpecialistSignatureButton.setError(null)
        facilityRepresentativeSignatureButton.setError(null)
        facilityRepresentativeTextView.setError(null)


        if (adhocVisitationType.isChecked) {
            if (visitationReasonDropListId.selectedItem.toString() == visitationReasonDropListId.setSelection(0).toString()) {

                isInputValid = false
                Toast.makeText(context, "please select a visitation reason", Toast.LENGTH_LONG).show()

            }

        } else

            if (facilityRepresentativeSignatureButton.text.toString() == "Add Signature") {

                isInputValid = false
                facilityRepresentativeSignatureButton.setError("required field")

            }

//        if (facilityRepresentativeSignatureBitmap==null) {
//            isInputValid = false
//            facilityRepresentativeSignatureButton.setError("Required Field")
//        }

        if (facilityRepresentativesSpinner.selectedItem.toString().contains("please")) {
            isInputValid = false

            facilityRepresentativeTextView.setError("Required Field")
        }

        if (automotiveSpecialistSignatureButton.text.toString() == "Add Signature") {

            isInputValid = false
            automotiveSpecialistSignatureButton.setError("required field")

        }

//        if (automotiveSpecialistSignatureBitmap==null) {
//            isInputValid = false
//            automotiveSpecialistSignatureButton.setError("Required Field")
//        }
        if (emailPdfCheckBox.isChecked == true) {

            if (emailEditText.text.toString().isNullOrEmpty()) {

                isInputValid = false
                emailEditText.setError("required field")
            }


        } else {
            emailEditText.setError(null)
        }

        if (waiveVisitationCheckBox.isChecked == true) {

            if (waiverCommentsEditText.text.toString().isNullOrEmpty() || waiversSignatureButton.text.toString().isNullOrEmpty()) {

                isInputValid = false
                waiverCommentsEditText.setError("required field")
                waiversSignatureButton.setError("required field")
            }

        } else {
            waiverCommentsEditText.setError(null)
            waiversSignatureButton.setError(null)
        }


        if (emailPdfCheckBox.isChecked == true) {
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

    fun handleCancelButtonClick() {

        cancelButton.setOnClickListener(View.OnClickListener {


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
        })
    }
    fun fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService(){

        FragmentARRAVScopeOfService.dataChanged =false

        FragmentARRAVScopeOfService.fixedLaborRate = if (FragmentARRAVScopeOfService.watcher_FixedLaborRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate else FragmentARRAVScopeOfService.watcher_FixedLaborRate
        FragmentARRAVScopeOfService.diagnosticLaborRate =  if (FragmentARRAVScopeOfService.watcher_DiagnosticsRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate else FragmentARRAVScopeOfService.watcher_DiagnosticsRate
        FragmentARRAVScopeOfService.laborRateMatrixMax =  if (FragmentARRAVScopeOfService.watcher_LaborMax.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMax else FragmentARRAVScopeOfService.watcher_LaborMax
        FragmentARRAVScopeOfService.laborRateMatrixMin =  if (FragmentARRAVScopeOfService.watcher_LaborMin.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMin else FragmentARRAVScopeOfService.watcher_LaborMin
        FragmentARRAVScopeOfService.numberOfBaysEditText_ =  if (FragmentARRAVScopeOfService.watcher_NumOfBays.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays else FragmentARRAVScopeOfService.watcher_NumOfBays
        FragmentARRAVScopeOfService.numberOfLiftsEditText_ =  if (FragmentARRAVScopeOfService.watcher_NumOfLifts.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts else FragmentARRAVScopeOfService.watcher_NumOfLifts


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMax!= FragmentARRAVScopeOfService.laborRateMatrixMax){

            Log.v("compare111", laborRateMatrixMax  +  "  ===>"  +  FacilityDataModel.getInstance().tblScopeofService[0].LaborMax)

            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMin!= FragmentARRAVScopeOfService.laborRateMatrixMin){

            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate!= FragmentARRAVScopeOfService.fixedLaborRate){

            Log.v("compare222", fixedLaborRate  +  "  ===>"  +  FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate)

            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate!= FragmentARRAVScopeOfService.diagnosticLaborRate){


            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays!= FragmentARRAVScopeOfService.numberOfBaysEditText_){


            FragmentARRAVScopeOfService.dataChanged =true
        }

        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts!= FragmentARRAVScopeOfService.numberOfLiftsEditText_){


            FragmentARRAVScopeOfService.dataChanged =true
        }

    }

    fun scopeOfServiceChangesWatcher() {

        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {

            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {

                if (FragmentARRAVScopeOfService.dataChanged) {

                    val builder = AlertDialog.Builder(context)

                    // Set the alert dialog title
                    builder.setTitle("Changes made confirmation")

                    // Display a message on alert dialog
                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("YES") { dialog, which ->

                        scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE



                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())
                                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                                FragmentARRAVScopeOfService.dataChanged = false
                                                FragmentARRAVScopeOfService().checkMarkChangeWasDoneForScopeOfServiceGeneralInfo()
                                                dataChangeHandling()


                                            }

                                        }

                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading personnal record")
                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
                            scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE


                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE

                    }


                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    // Display the alert dialog on app interface
                    dialog.show()

                }

            } else {


                val builder = AlertDialog.Builder(context)

                // Set the alert dialog title
                builder.setTitle("Changes made Warning")

                // Display a message on alert dialog
                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ok") { dialog, which ->

                    FragmentARRAVScopeOfService.dataChanged = false

                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true

                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }

        }


    }

}


//FacilityDataModel.getInstance().tblVisitationTracking[0].automotiveSpecialistName = CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname }[p2]



