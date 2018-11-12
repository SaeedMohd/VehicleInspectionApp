    package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.billing_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_payments.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVPayments.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVPayments.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVPayments : Fragment() {
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
        return inflater.inflate(R.layout.fragment_aarav_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preparePaymentsSpinners()
        newCheckDateBtn.setOnClickListener {
//            if (newCheckDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newCheckDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        newTrxDateBtn.setOnClickListener {
//            if (newTrxDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    newTrxDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }
//        newLastUpdatedBtn.setOnClickListener {
////            if (newLastUpdatedBtn.text.equals("SELECT DATE")) {
//                val c = Calendar.getInstance()
//                val year = c.get(Calendar.YEAR)
//                val month = c.get(Calendar.MONTH)
//                val day = c.get(Calendar.DAY_OF_MONTH)
//                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    // Display Selected date in textbox
//                    val myFormat = "dd MMM yyyy" // mention the format you need
//                    val sdf = SimpleDateFormat(myFormat, Locale.US)
//                    c.set(year, monthOfYear, dayOfMonth)
//                    newLastUpdatedBtn!!.text = sdf.format(c.time)
//                }, year, month, day)
//                dpd.show()
////            }
//        }

        addNewPaymentBtn.setOnClickListener( {
            showAddNewPaymentDialog()
        })

        paymentSubmitButton.setOnClickListener({
            validateBillinPlanData()
        })

        fillInvoicesTableView()
        fillPaymentsTableView()

        exitDialogeBtn.setOnClickListener({
            addNewPaymentDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
        })

        IndicatorsDataModel.getInstance().tblBilling[0].PaymentsVisited = true
        (activity as FormsActivity).paymentsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

    }

    private var paymentTypeList = ArrayList<TypeTablesModel.invoicePaymentType>()
    private var paymentTypeArray = ArrayList<String>()

    fun preparePaymentsSpinners() {

        paymentTypeList= TypeTablesModel.getInstance().InvoicePaymentType
        paymentTypeArray.clear()
        for (fac in paymentTypeList) {
            paymentTypeArray.add(fac.PaymentName)
        }
        var paymentTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, paymentTypeArray)
        paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPaymentTypeSpinner.adapter = paymentTypeAdapter
    }



    private fun validateBillinPlanData() {
        var isInputsValid = true
        newInvNoText.setError(null)
        newTrxDateBtn.setError(null)
        if (newInvNoText.text.toString().isNullOrEmpty()) {
            newInvNoText.setError("Required Field")
        } else if (newPaymentIDText.text.toString().isNullOrEmpty()) {
            newPaymentIDText.setError("Required Field")
        } else if (newTrxDateBtn.text.toString().equals("SELECT DATE")) {
            isInputsValid = false
            newTrxDateBtn.setError("Required Field")
        } else {
            submitPaymentData()
        }
    }



    private fun submitPaymentData(){
        addNewPaymentDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
    }

    private fun showAddNewPaymentDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewPaymentDialog.visibility = View.VISIBLE
    }

    fun fillInvoicesTableView() {

        if (InvoiceResultsTbl.childCount > 1) {
            for (i in InvoiceResultsTbl.childCount - 1 downTo 1) {
                InvoiceResultsTbl.removeViewAt(i)
            }
        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = 30
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = 30
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = 30
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = 30
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = 30
        rowLayoutParam4.width = 0

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = 30
        rowLayoutParam5.width = 0

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = 30
        rowLayoutParam6.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblInvoiceInfoUpdated.apply {
            (0 until size).forEach {

                var tableRow = TableRow(context)
                if (it % 2 == 0) {
                    tableRow.setBackgroundResource(R.drawable.alt_row_color)
                }
                tableRow.layoutParams = rowLayoutParamRow
                tableRow.minimumHeight = 30

                if (!get((it)).InvoiceId.equals("-1")) {
                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.text = get(it).InvoiceNumber
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = get(it).BillingMonth
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    TableRow.LayoutParams()
                    textView.text = if (get(it).BillingDueDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).BillingDueDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).InvoiceAmount.isNullOrEmpty()) "" else "%.3f".format(get(it).InvoiceAmount.toFloat())
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam4
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).AmountReceived.isNullOrEmpty()) "" else "%.3f".format(get(it).InvoiceAmount.toFloat())
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam5
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).BillBalanceDue.isNullOrEmpty()) "" else "%.3f".format(get(it).BillBalanceDue.toFloat())
                    tableRow.addView(textView)

                    InvoiceResultsTbl.addView(tableRow)

                }
            }
        }
    }

    fun fillPaymentsTableView() {

        if (paymentsResultsTbl.childCount > 1) {
            for (i in paymentsResultsTbl.childCount - 1 downTo 1) {
                paymentsResultsTbl.removeViewAt(i)
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



        var dateTobeFormated = ""


//        FacilityDataModel.getInstance(). .apply {
//            (0 until size).forEach {
//
//            }

        for (i in 1..2) {

            var tableRow = TableRow(context)
            if (i % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.alt_row_color)
            }
            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // getLocationTypeName(get(it).LocationTypeID)
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).FAC_Addr1
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = "Test" // get(it).FAC_Addr2
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).CITY
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).CITY
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam5
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).CITY
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam6
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).CITY
            tableRow.addView(textView)

            paymentsResultsTbl.addView(tableRow)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVPayments.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVPayments().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
