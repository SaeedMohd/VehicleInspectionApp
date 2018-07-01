package com.inspection.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
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
import android.util.Patterns
import android.view.Gravity
import androidx.core.view.setPadding
import com.inspection.MainActivity
import com.inspection.Utils.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentVisitation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentVisitation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVisitation : Fragment() {

    var facilityRepresentativeSignatureBitmap: Bitmap? = null
    var automotiveSpecialistSignatureBitmap: Bitmap? = null
    var facilityRepresentativeDeficienciesSignatureBitmap: Bitmap? = null
    var waiverSignatureBitmap: Bitmap? = null

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

        annualVisitationType.isClickable = false

        quarterlyVisitationType.isClickable = false

        adhocVisitationType.isClickable = false
        dataChangedYesRadioButton.isClickable = false



        dateOfVisitationButton.isClickable = false
        dataChangedNoRadioButton.isClickable = false
        clubCodeEditText.isClickable = false

        if (FragmentARRAVScopeOfService.scopeOfServicesChangesMade==true){

            dataChangedYesRadioButton.isChecked=true
        }else dataChangedNoRadioButton.isChecked=true

//              textWatcherSignature.visibility=View.INVISIBLE
//        textWatcherSignature.visibility=View.GONE


        setFieldsValues()

        visitationReasonDropListId.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, resources.getStringArray(R.array.visitation_reasons))


//            textWatcherSignature.setText(facilityRepresentativesSpinner.selectedItem.toString())

        completeButton.setOnClickListener(View.OnClickListener {
            if (validateInputs()) {
                Toast.makeText(context, "inputs validated", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "missing required fields", Toast.LENGTH_SHORT).show()
        })

        if (annualVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(6)

        }
        if (quarterlyVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(7)

        }
        if (adhocVisitationType.isChecked) {
            visitationReasonDropListId.setSelection(0)


        }
        cancelBtnPressed()

        facilityRepresentativeSignatureButton.setOnClickListener {

            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.representative
            if (facilityRepresentativeSignatureBitmap!=null){
                signatureInkView.drawBitmap(facilityRepresentativeSignatureBitmap, 0.0f, 0.0f, Paint())
            }

        }

        automotiveSpecialistSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.specialist
            if (automotiveSpecialistSignatureBitmap!=null){
                signatureInkView.drawBitmap(automotiveSpecialistSignatureBitmap, 0.0f, 0.0f, Paint())
            }
        }

        facilityRepresentativeDeficienciesSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.representativeDeficiency
            if (facilityRepresentativeDeficienciesSignatureBitmap!=null){
                signatureInkView.drawBitmap(facilityRepresentativeDeficienciesSignatureBitmap, 0.0f, 0.0f, Paint())
            }
        }

        waiversSignatureButton.setOnClickListener {
            signatureDialog.visibility = View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE
            selectedSignature = requestedSignature.waiver
            if (waiverSignatureBitmap!=null){
                signatureInkView.drawBitmap(waiverSignatureBitmap, 0.0f, 0.0f, Paint())
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
                    facilityRepresentativeSignatureBitmap = bitmap
                    if (!isEmpty){
                        facilityRepresentativeSignatureButton.text ="Edit Signature"
                        facilityRepresentativeSignatureImageView.setImageBitmap(bitmap)
                    }else{
                        facilityRepresentativeSignatureButton.text ="Add Signature"
                        facilityRepresentativeSignatureImageView.setImageBitmap(null)
                    }
                }

                requestedSignature.specialist -> {
                    automotiveSpecialistSignatureBitmap = bitmap
                    if (!isEmpty){
                        automotiveSpecialistSignatureButton.text ="Edit Signature"
                        automotiveSpecialistSignatureImageView.setImageBitmap(bitmap)
                    }else{
                        automotiveSpecialistSignatureButton.text ="Add Signature"
                        automotiveSpecialistSignatureImageView.setImageBitmap(null)
                    }
                }

                requestedSignature.representativeDeficiency -> {
                    facilityRepresentativeDeficienciesSignatureBitmap = bitmap
                    if (!isEmpty){
                        facilityRepresentativeDeficienciesSignatureButton.text ="Edit Signature"
                        facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(bitmap)
                    }else{
                        facilityRepresentativeDeficienciesSignatureButton.text ="Add Signature"
                        facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)
                    }

                }

                requestedSignature.waiver -> {
                    waiverSignatureBitmap = bitmap
                    if (!isEmpty){
                        waiversSignatureButton.text ="Edit Signature"
                        waiversSignatureImageView.setImageBitmap(bitmap)
                    }else{
                        waiversSignatureButton.text ="Add Signature"
                        waiversSignatureImageView.setImageBitmap(null)
                    }
                }


            }

            signatureInkView.clear()
            visitationFormAlphaBackground.visibility = View.GONE
            signatureDialog.visibility = View.GONE
        }

        altDeffVisitationTableRow(2)
    }

    private fun setFieldsValues() {


        var representativeSpinners = ArrayList<String>()
        representativeSpinners.add("please select a representative")

        for (fac in FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName + " " + s.LastName }.distinct()) {

            representativeSpinners.add(fac)


        }

        facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, representativeSpinners)


        //  context!!.toast("Specialist size: "+ CsiSpecialistSingletonModel.getInstance().csiSpecialists.size)

        automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, R.layout.spinner_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map { s -> s.specialistname })

        annualVisitationType.isChecked = true

        dateOfVisitationButton.text = Date().toAppFormat()

        clubCodeEditText.setText("004")


        facilityNumberEditText.setText("" + FacilityDataModel.getInstance().tblFacilities[0].FACNo)


        //   facilityNameEditText.setText(FacilityDataModel.getInstance().tblFacilities[0].EntityName)

        aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)

        certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)

        memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)

        qualityControlProcessEditText.setText(" " + FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl.replace(".  ", ". ").replace(". ", ".\n"))

        staffTrainingProcessEditText.setText(" " + FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining.replace(".  ", ". ").replace(". ", ".\n"))

        if (FacilityDataModel.getInstance().tblFacilityEmail.count() > 0) {
            emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
        }


        facilityNameAndNumberRelationForSelection()


        emailValidation()
        waiverValidation()

        fillDeficiencyTable()
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
//        textWatcherSignature.addTextChangedListener(representativeNameWatcher)


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
        }
        else
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

    fun altDeffVisitationTableRow(alt_row : Int) {
        var childViewCount = deficienciesTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= deficienciesTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
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

    fun cancelBtnPressed() {

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

}
