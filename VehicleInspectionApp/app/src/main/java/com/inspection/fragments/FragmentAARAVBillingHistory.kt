package com.inspection.fragments

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import kotlinx.android.synthetic.main.billing_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_billinghistory.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVBillingHistory.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVBillingHistory.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVBillingHistory : Fragment() {
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
        return inflater.inflate(R.layout.fragment_aarav_billinghistory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillBillingHistoryTableView()
        IndicatorsDataModel.getInstance().tblBilling[0].BillingHistoryVisited = true
        (activity as FormsActivity).billingHistoryButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun fillBillingHistoryTableView() {

        if (billHistoryResultsTbl.childCount>1) {
            for (i in billHistoryResultsTbl.childCount - 1 downTo 1) {
                billHistoryResultsTbl.removeViewAt(i)
            }
        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0

        val rowLayoutParam6 = TableRow.LayoutParams()
        rowLayoutParam6.weight = 1F
        rowLayoutParam6.column = 6
        rowLayoutParam6.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam6.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblBillingHistory.apply {
            (0 until size).forEach {

                if (!get(it).InvoiceId.equals("-1")) {

                    var tableRow = TableRow(context)
                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

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
                    textView.text = get(it).InvoiceStatusName
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    TableRow.LayoutParams()
                    textView.text = get(it).BillingMonth
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).BillingDueDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).BillingDueDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam4
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).InvoiceAmount.isNullOrEmpty()) "" else "%.3f".format(get(it).InvoiceAmount.toFloat())
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam5
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).AmountReceived.isNullOrEmpty()) "" else "%.3f".format(get(it).InvoiceAmount.toFloat())
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam6
                    textView.gravity = Gravity.CENTER
                    textView.textSize = 18f
                    textView.text = if (get(it).ReceiptDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).BillingDueDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    billHistoryResultsTbl.addView(tableRow)
                }
            }
        }
//        altVenRevTableRow(2)
//            }
//        }

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
         * @return A new instance of fragment FragmentAARAVBillingHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBillingHistory().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
