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
import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.billing_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_billingadjustment.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVBillingAdjustment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVBillingAdjustment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVBillingAdjustment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_aarav_billingadjustment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareAdjSpinners()
        newEffDateBtn.setOnClickListener {
//            if (newEffDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
//                    if (Calendar.getInstance().get(Calendar.YEAR)> year || Calendar.getInstance().get(Calendar.MONTH) > monthOfYear) {
//                        newEffDateBtn!!.text == "SELECT DATE"
//                        newEffDateBtn!!.setError("Must be Future Date")
//                    } else if ((Calendar.getInstance().get(Calendar.YEAR)== year && Calendar.getInstance().get(Calendar.MONTH)+1 == monthOfYear) && (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)>dayOfMonth)) {
//                        newEffDateBtn!!.text == "SELECT DATE"
//                        newEffDateBtn!!.setError("Must be Future Date")
//                    } else {
                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        c.set(year, monthOfYear, dayOfMonth)
                        newEffDateBtn!!.text = sdf.format(c.time)
                        newEffDateBtn!!.setError(null)
//                    }
                }, year, month, day)
                dpd.show()
//            }
        }

//        newUpdatedByDateBtn.setOnClickListener {
////            if (newUpdatedByDateBtn.text.equals("SELECT DATE")) {
//                val c = Calendar.getInstance()
//                val year = c.get(Calendar.YEAR)
//                val month = c.get(Calendar.MONTH)
//                val day = c.get(Calendar.DAY_OF_MONTH)
//                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    // Display Selected date in textbox
//                    val myFormat = "dd MMM yyyy" // mention the format you need
//                    val sdf = SimpleDateFormat(myFormat, Locale.US)
//                    c.set(year, monthOfYear, dayOfMonth)
//                    newUpdatedByDateBtn!!.text = sdf.format(c.time)
//                }, year, month, day)
//                dpd.show()
////            }
//        }

        fillBillAdjTableView ()
        addNewVenRevBtn.setOnClickListener( {
            showAddNewBillAdjDialog()
        })

        billAdjSubmitButton.setOnClickListener({
            validateBillAdjData()
        })

        exitDialogeBtn.setOnClickListener({
            addNewBillAdjDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
        })

        IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustmentsVisited = true
        (activity as FormsActivity).billingAdjustmentButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    private var descTypeList = ArrayList<TypeTablesModel.adjustmentDescriptionType>()
    private var descTypeArray = ArrayList<String>()

    fun fillBillAdjTableView() {

        if (billAdjResultsTbl.childCount>1) {
            for (i in billAdjResultsTbl.childCount - 1 downTo 1) {
                billAdjResultsTbl.removeViewAt(i)
            }
        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 0.5F
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

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
        rowLayoutParam5.weight = 1F
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

        FacilityDataModel.getInstance().tblBillingAdjustments.apply {
            (0 until size).forEach {

                if (get(it).AdjustmentId > -1) {

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
                    textView.text = get(it).AdjustmentId.toString()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = if (get(it).EffectiveDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).EffectiveDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
                    TableRow.LayoutParams()
                    textView.text = get(it).Type
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 14f
//                    textView.text = TypeTablesModel.getInstance().AdjustmentDescriptionType.filter { s->s.AdjustmentDescId==get(it).DescriptionId }[0].Description
                    textView.text = get(it).Description
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam4
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = get(it).Amount.toString()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam5
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = get(it).LastUpdateBy
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam6
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 14f
                    textView.text = if (get(it).LastUpdateDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).LastUpdateDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    billAdjResultsTbl.addView(tableRow)
                }
            }
        }
//        altVenRevTableRow(2)
//            }
//        }

    }

    private fun validateBillAdjData() {
        var isInputsValid = true
        newAmountText.setError(null)
        newEffDateBtn.setError(null)
        newCommentsText.setError(null)
        if (newAmountText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            newAmountText.setError("Required Field")
        } else if (newEffDateBtn.text.toString().equals("SELECT DATE")) {
            isInputsValid = false
            newEffDateBtn.setError("Required Field")
        } else if (newCommentsText.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            newCommentsText.setError("Required Field")
        } else {
            submitBillAdjData()
        }
    }

    private fun submitBillAdjData(){
        addNewBillAdjDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
    }

    private fun showAddNewBillAdjDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewBillAdjDialog.visibility = View.VISIBLE
    }

    fun prepareAdjSpinners() {

        descTypeList= TypeTablesModel.getInstance().AdjustmentDescriptionType
        descTypeArray.clear()
        for (fac in descTypeList) {
            descTypeArray.add(fac.Description)
        }
        var descTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, descTypeArray)
        descTypeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newDescSpinner.adapter = descTypeAdapter
    }
    // TODO: Rename method, update argument and hook method into UI event

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVBillingAdjustment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBillingAdjustment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
