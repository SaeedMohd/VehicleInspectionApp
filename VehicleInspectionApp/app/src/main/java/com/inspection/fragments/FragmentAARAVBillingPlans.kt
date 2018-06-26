package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow


import com.inspection.R
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_aarav_billingplans.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var categoryTypeList = ArrayList<TypeTablesModel.billingPlanCategoryType>()
private var freqTypeList = ArrayList<TypeTablesModel.billingPlanFrequencyType>()
private var planTypeList = ArrayList<TypeTablesModel.billingPlanType>()
private var categoryTypeArray = ArrayList<String>()
private var freqTypeArray = ArrayList<String>()
private var planTypeArrayCat1 = ArrayList<String>()
private var planTypeArrayCat2 = ArrayList<String>()

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVBillingPlans.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVBillingPlans.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVBillingPlans : Fragment() {
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
        return inflater.inflate(R.layout.fragment_aarav_billingplans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareBillingPlanSpinners()

        newEffDateBtn.setOnClickListener {
//            if (newEffDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    view.setMinDate(System.currentTimeMillis() - 1000);

                    if (dayOfMonth!=1){
                        newEffDateBtn!!.text == "SELECT DATE"
                        newEffDateBtn!!.setError("Error")
                    } else if (Calendar.getInstance().get(Calendar.YEAR)> year || Calendar.getInstance().get(Calendar.MONTH)+1 > monthOfYear) {
                        newEffDateBtn!!.text == "SELECT DATE"
                        newEffDateBtn!!.setError("Error")
                    } else {
                        val myFormat = "dd MMM yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        newEffDateBtn!!.text = sdf.format(c.time)
                        newEffDateBtn!!.setError(null)
                    }
                }, year, month, day)
                dpd.show()
//            }
        }



        addNewPlanBtn.setOnClickListener( {
            showAddNewPlanDialog()
        })

        planSubmitButton.setOnClickListener({
            validateBillinPlanData()
        })

    }

    private fun validateBillinPlanData() {
        var isInputsValid = true

//        phyloc1addr1latitude.setError(null)
////        phyloc1addr2latitude.setError(null)
////        phyloc1addr2longitude.setError(null)
//        phyloc1addr1longitude.setError(null)
//
        Log.v("DATE ---- ",newEffDateBtn.text.toString().substring(0,2))
        if (newEffDateBtn.text.toString().equals("SELECT DATE")) {
            isInputsValid = false
            newEffDateBtn.setError("Required Field")
        } else if (!newEffDateBtn.text.toString().substring(0,2).equals("01")) {
            isInputsValid = false
            newEffDateBtn.setError("Must be the first Day of the month")
        } else {
            submitBillingPlanData()
        }
    }



    private fun submitBillingPlanData(){
        addNewPlanDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
    }

    private fun showAddNewPlanDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewPlanDialog.visibility = View.VISIBLE
    }


    fun fillBillinPlanTableView() {

        if (billingPlansResultsTbl.childCount>1) {
            for (i in billingPlansResultsTbl.childCount - 1 downTo 1) {
                billingPlansResultsTbl.removeViewAt(i)
            }
        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableLayout.LayoutParams.WRAP_CONTENT


        val rowLayoutParam7 = TableRow.LayoutParams()
        rowLayoutParam7.weight = 1F
        rowLayoutParam7.column = 7
        rowLayoutParam7.height = TableLayout.LayoutParams.WRAP_CONTENT

        var dateTobeFormated = ""

//        FacilityDataModel.getInstance().tblPl.apply {
//            (0 until size).forEach {
//                var tableRow = TableRow(context)
//
//                var textView = TextView(context)
//                textView.layoutParams = rowLayoutParam
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                //getTypeName
//                textView.text = getLocationTypeName(get(it).LocationTypeID)
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam1
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).FAC_Addr1
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam2
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                TableRow.LayoutParams()
//                textView.text = get(it).FAC_Addr2
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam3
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).CITY
//
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam4
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).County
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam5
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).ST
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam6
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).ZIP + "-" + get(it).ZIP4
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam7
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).LATITUDE
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam8
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).LONGITUDE
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam9
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).BranchNumber
//                tableRow.addView(textView)
//
//                textView = TextView(context)
//                textView.layoutParams = rowLayoutParam10
//                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView.text = get(it).BranchName
//                tableRow.addView(textView)
//
//                locationTbl.addView(tableRow)
//
//            }
//        }

    }
    fun prepareBillingPlanSpinners() {

        categoryTypeList = TypeTablesModel.getInstance().BillingPlanCategoryType
        categoryTypeArray.clear()
        for (fac in categoryTypeList) {
            categoryTypeArray.add(fac.BillingPlanCatgName)
        }

        var catTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, categoryTypeArray)
        catTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newCatSpinner.adapter = catTypeAdapter

        planTypeList = TypeTablesModel.getInstance().BillingPlanType
        planTypeArrayCat1.clear()
        for (fac in planTypeList) {
            if (fac.BillingPlanCatgID == "1") {
                planTypeArrayCat1.add(fac.BillingPlanTypeName)
            } else if (fac.BillingPlanCatgID == "2") {
                planTypeArrayCat2.add(fac.BillingPlanTypeName)
            }
        }
        var planTypeAdapterCat1 = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, planTypeArrayCat1)
        planTypeAdapterCat1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTypeSpinnerCat1.adapter = planTypeAdapterCat1
        var planTypeAdapterCat2 = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, planTypeArrayCat2)
        planTypeAdapterCat1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTypeSpinnerCat2.adapter = planTypeAdapterCat2
        newTypeSpinnerCat1.visibility = View.VISIBLE
        newTypeSpinnerCat2.visibility = View.GONE

        newCatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (newCatSpinner.selectedItemPosition==0) {
                    newTypeSpinnerCat1.visibility = View.VISIBLE
                    newTypeSpinnerCat2.visibility = View.GONE
                } else {
                    newTypeSpinnerCat1.visibility = View.GONE
                    newTypeSpinnerCat2.visibility = View.VISIBLE
                }
            }

        }


        freqTypeList = TypeTablesModel.getInstance().BillingPlanFrequencyType
        freqTypeArray.clear()
        for (fac in freqTypeList) {
            freqTypeArray.add(fac.BillingPlanFrequencyTypeName)
        }
        var freqTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, freqTypeArray)
        freqTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newFreqSpinner.adapter = freqTypeAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVBillingPlans.
         */
        // TODO: Rename and change types and number of parameters

        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBillingPlans().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
