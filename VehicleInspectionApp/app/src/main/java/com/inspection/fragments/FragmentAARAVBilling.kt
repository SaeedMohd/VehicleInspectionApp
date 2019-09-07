package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.Utils.monthNoToName
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.billing_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_billing.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVBilling.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVBilling.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVBilling : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var revSourceList = ArrayList<TypeTablesModel.revenueSourceType>()
    private var revSourceArray = ArrayList<String>()


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
        return inflater.inflate(R.layout.fragment_aarav_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareBillingSpinners()

        newBillDueDateBtn.setOnClickListener {
//            if (newBillDueDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newBillDueDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newSecondDueDateText.setOnClickListener {
//            if (newSecondDueDateText.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newSecondDueDateText!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newFinalDueDateText.setOnClickListener {
//            if (newFinalDueDateText.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newFinalDueDateText!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newACHDateBtn.setOnClickListener {
//            if (newACHDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newACHDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newPayRecDateBtn.setOnClickListener {
//            if (newPayRecDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newPayRecDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newCreditAppliedDateBtn.setOnClickListener {
//            if (newCreditAppliedDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCreditAppliedDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        addNewBillingBtn.setOnClickListener( {
            showAddNewBillingDialog()
        })

        billingSubmitButton.setOnClickListener({
            validateBillingData()
        })

        exitDialogeBtn.setOnClickListener({
            addNewBillingDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
        })

        IndicatorsDataModel.getInstance().tblBilling[0].BillingVisited = true
        (activity as FormsActivity).billingButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }





    private fun validateBillingData() {
        var isInputsValid = true
        newBillIDText.setError(null)
        newBillingAmountText.setError(null)
        newPayAmountText.setError(null)
        newBillDueDateBtn.setError(null)
        newPayRecDateBtn.setError(null)
        if (newBillIDText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            newBillIDText.setError("Required Field")
        } else if (newBillingAmountText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            newBillingAmountText.setError("Must be the first Day of the month")
        } else if (newPayAmountText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            newPayAmountText.setError("Must be the first Day of the month")
        } else if (newBillDueDateBtn.text.toString().equals("SELECT DATE")) {
            isInputsValid = false
            newBillDueDateBtn.setError("Required Field")
        } else if (newPayRecDateBtn.text.toString().equals("SELECT DATE")) {
            isInputsValid = false
            newPayRecDateBtn.setError("Required Field")
        } else {
            submitBillingData()
        }
    }

    private fun submitBillingData(){
        addNewBillingDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
    }


    private fun showAddNewBillingDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewBillingDialog.visibility = View.VISIBLE
    }

    fun prepareBillingSpinners() {

        revSourceList = TypeTablesModel.getInstance().RevenueSourceType
        revSourceArray.clear()
        for (fac in revSourceList) {
            revSourceArray.add(fac.RevenueSourceName)
        }

        var revSourceAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, revSourceArray)
        revSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newRevSourceSpinner.adapter = revSourceAdapter
        fillBillingTableView()
    }


    fun fillBillingTableView() {

        if (billingResultsTbl.childCount>1) {
            for (i in billingResultsTbl.childCount - 1 downTo 1) {
                billingResultsTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 0.4F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.6F
        rowLayoutParam2.column = 2
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 0.7F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.7F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0
        rowLayoutParam6.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblBilling.apply {
            (0 until size).forEach {
                //        for (i in 1..2) {
                if ( get(it).BillingID > 0 ) {
                    var tableRow = TableRow(context)
                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.text = get(it).BillingID.toString()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    textView.text = get(it).RevenueSourceName
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    TableRow.LayoutParams()
                    textView.text = get(it).BillingMonthNumber.monthNoToName()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = if (get(it).BillingDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).BillingDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam4
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = get(it).BillingAmount.toString()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam5
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = get(it).PaymentAmount.toString()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam6
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = if (get(it).PaymentDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).PaymentDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    billingResultsTbl.addView(tableRow)
                }
            }
        }
//        altVenRevTableRow(2)
//            }
//        }

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVBilling.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBilling().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
