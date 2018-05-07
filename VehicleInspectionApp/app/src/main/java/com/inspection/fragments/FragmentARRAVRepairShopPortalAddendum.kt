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
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

import com.inspection.R
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import kotlinx.android.synthetic.main.fragment_array_repair_shop_portal_addendum.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVRepairShopPortalAddendum.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVRepairShopPortalAddendum.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVRepairShopPortalAddendum : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_array_repair_shop_portal_addendum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                startDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

//        endDateButton.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            val month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                val myFormat = "dd MMM yyyy" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                c.set(year,monthOfYear,dayOfMonth)
//                endDateButton!!.text = sdf.format(c.time)
//            }, year, month, day)
//            dpd.show()
//        }

        addendumSignedDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                addendumSignedDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        inspectionDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                inspectionDateButton!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        fillData()
        fillPortalTrackingTableView();
    }

    fun fillData(){
        FacilityDataModel.getInstance().tblAARPortalAdmin[0].apply {
            startDateButton.text = startDate
//            endDateButton.text = PortalInspectionDate
            addendumSignedDateButton.text = AddendumSigned
            numberOfCardsReaderEditText.setText(CardReaders)
            inspectionDateButton.text = PortalInspectionDate
            numberOfUnacknowledgedRecordsEditText.setText(NumberUnacknowledgedTows)
            numberOfInProgressTwoIns.setText(InProgressTows)
            numberOfInProgressWalkIns.setText(InProgressWalkIns)

        }
    }


    fun validateInputs() : Boolean {
        var isInputsValid = true

        startDateButton.setError(null)
//        endDateButton.setError(null)
        addendumSignedDateButton.setError(null)
        numberOfCardsReaderEditText.setError(null)
        inspectionDateButton.setError(null)
        loggedIntoRspButton.setError(null)
        numberOfUnacknowledgedRecordsEditText.setError(null)
        numberOfInProgressTwoIns.setError(null)
        numberOfInProgressWalkIns.setError(null)

        if (!startDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {

//            if (endDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
//                isInputsValid = false
//                endDateButton.setError("Required Field")
//            }

            if (addendumSignedDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
                isInputsValid = false
                addendumSignedDateButton.setError("Required Field")
            }

            if (inspectionDateButton.text.toString().toUpperCase().equals("SELECT DATE")) {
                isInputsValid = false
                inspectionDateButton.setError("Required Field")
            }

            if (numberOfCardsReaderEditText.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                numberOfCardsReaderEditText.setError("Required Field")
            }

            if (numberOfUnacknowledgedRecordsEditText.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                numberOfUnacknowledgedRecordsEditText.setError("Required Field")
            }

            if (numberOfInProgressTwoIns.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                numberOfInProgressTwoIns.setError("Required Field")
            }

            if (numberOfInProgressWalkIns.text.toString().isNullOrEmpty()) {
                isInputsValid = false
                numberOfInProgressWalkIns.setError("Required Field")
            }


        }



        return isInputsValid
    }

    fun fillPortalTrackingTableView(){
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

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1F
        rowLayoutParam4.column = 4
        FacilityDataModel.getInstance().tblAARPortalAdmin.apply {

            (0 until size/2).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).PortalInspectionDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).LoggedIntoPortal
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).NumberUnacknowledgedTows
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).InProgressTows
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).InProgressWalkIns
                tableRow.addView(textView)

                aarPortalTrackingTableLayout.addView(tableRow)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVRepairShopPortalAddendum {
            val fragment = FragmentARRAVRepairShopPortalAddendum()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
