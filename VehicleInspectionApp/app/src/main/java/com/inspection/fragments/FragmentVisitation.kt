package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.toAppFormat
import com.inspection.Utils.toast
import com.inspection.model.CsiSpecialistSingletonModel
import com.inspection.model.FacilityDataModel

import kotlinx.android.synthetic.main.fragment_visitation_form.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentVisitation.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentVisitation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVisitation : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_visitation_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateOfVisitationButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                dateOfVisitationButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        setFieldsValues()
    }


    private fun setFieldsValues() {

        facilityRepresentativesSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FacilityDataModel.getInstance().tblPersonnel.map { s -> s.FirstName +" " + s.LastName}.distinct())

        automotiveSpecialistSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CsiSpecialistSingletonModel.getInstance().csiSpecialists.map {s -> s.specialistname})

        adhocVisitationType.isChecked = true

        dateOfVisitationButton.text = Date().toAppFormat()

        clubCodeEditText.setText("004")

        facilityNumberEditText.setText(""+FacilityDataModel.getInstance().tblFacilities[0].FACNo)

        facilityNameEditText.setText(FacilityDataModel.getInstance().tblFacilities[0].EntityName)

        aarSignEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].AARSigns)

        dataChangedYesRadioButton.isChecked = true

        certificateOfApprovalEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].CertificateOfApproval)

        memberBenefitsPosterEditText.setText(FacilityDataModel.getInstance().tblVisitationTracking[0].MemberBenefitPoster)

        qualityControlProcessEditText.setText(" "+FacilityDataModel.getInstance().tblVisitationTracking[0].QualityControl.replace(".  ", ". ").replace(". ", ".\n"))

        staffTrainingProcessEditText.setText(" "+FacilityDataModel.getInstance().tblVisitationTracking[0].StaffTraining.replace(".  ", ". ").replace(". ", ".\n"))

        if (FacilityDataModel.getInstance().tblFacilityEmail[0].email.isNullOrEmpty()){

        }else {
            emailEditText.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
        }



        fillDeficiencyTable()
    }


    private fun fillDeficiencyTable(){
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2


        FacilityDataModel.getInstance().tblDeficiency.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).Comments
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).VisitationDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).ClearedDate
                tableRow.addView(textView)

                deficienciesTableLayout.addView(tableRow)
            }
        }
    }


}
