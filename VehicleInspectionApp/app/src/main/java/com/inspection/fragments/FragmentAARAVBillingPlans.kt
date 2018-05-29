package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
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

private var categoryTypeArray = ArrayList<String>()
private var freqTypeArray = ArrayList<String>()

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
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newEffDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newLastUpdatedBtn.setOnClickListener {
//            if (newLastUpdatedBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newLastUpdatedBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBillingPlans().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
