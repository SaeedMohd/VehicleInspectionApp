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
import com.inspection.MainActivity

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacility.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacility.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacility : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd")
    private val appFprmat = SimpleDateFormat("dd MMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fill Dop Down
        var timeZonesArray = arrayOf("Atlantic Time", "Eastern  Time", "Central  Time", "Mountain  Time", "Pacific  Time", "Hawaii  Time")
        var tzdataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, timeZonesArray)
        tzdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezone_textviewVal.adapter = tzdataAdapter


        var svcAvailabilityArray = arrayOf("Fixed-Site Service Only", "Fixed and Mobile Service", "Mobile Service Only")
        var svcAvldataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, svcAvailabilityArray)
        svcAvldataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_textviewVal.adapter = svcAvldataAdapter

        var facilityTypeArray = arrayOf("Independent", "Service Station", "Specialty", "Dealership", "Club Owned Repair - Attached", "Club Owned Repair - Standalone")
        var facilityTypedataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, facilityTypeArray)
        facilityTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilitytype_textviewVal.adapter = facilityTypedataAdapter

        p2top1btn.setText("<")

        p2top1btn.setOnClickListener({
            (activity as MainActivity).viewPager?.setCurrentItem(0)
        })

        p2top3btn.setOnClickListener({
            (activity as MainActivity).viewPager?.setCurrentItem(2)
        })

        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        InsuranceExpDate_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                InsuranceExpDate_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        }


    fun validateInputs() : Boolean {
        var isInputsValid = true

        ARDno_textviewVal.setError(null)
        ARDexp_textviewVal.setError(null)
        InsuranceExpDate_textviewVal.setError(null)

        if (ARDno_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            ARDno_textviewVal.setError("Required Field")
        }

        if (ARDexp_textviewVal.text.toString().equals("SELECT DATE")) {
            isInputsValid=false
            ARDexp_textviewVal.setError("Required Field")
        }

        if (InsuranceExpDate_textviewVal.text.toString().equals("SELECT DATE")) {
            isInputsValid=false
            InsuranceExpDate_textviewVal.setError("Required Field")
        }

        return isInputsValid
    }



    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun prepareFacilityPage (){
        Log.v("i am here", "i am here")
        Log.v("I am 777777", "77777"+(activity as MainActivity).FacilityName)
        if (!(activity as MainActivity).FacilityNumber.isNullOrEmpty()) {
            contract_number_textviewVal.setText((activity as MainActivity).facilitySelected.origcontractno)
            contract_type_textviewVal.setText((activity as MainActivity).facilitySelected.contracttypeid.toString())
            office_textviewVal.setText((activity as MainActivity).facilitySelected.officeid.toString())
            assignedto_textviewVal.setText((activity as MainActivity).facilitySelected.assignedtoid.toString())
            dba_textviewVal.setText((activity as MainActivity).facilitySelected.businessname)
            entity_textviewVal.setText((activity as MainActivity).facilitySelected.entityname)
            bustype_textviewVal.setText((activity as MainActivity).facilitySelected.bustypeid.toString())
//            timezone_textviewVal.setText((activity as MainActivity).facilitySelected.timezoneid)
            website_textviewVal.setText((activity as MainActivity).facilitySelected.website)
            wifi_textview.isChecked = ((activity as MainActivity).facilitySelected.internetaccess ==1)
            texno_textviewVal.setText((activity as MainActivity).facilitySelected.taxidnumber.toString())
            repairorder_textviewVal.setText((activity as MainActivity).facilitySelected.facilityrepairordercount.toString())
            availability_textviewVal.setSelection((activity as MainActivity).facilitySelected.svcavailability)
            facilitytype_textviewVal.setSelection((activity as MainActivity).facilitySelected.facilitytypeid)
            var dateTobeFormated =""
            dateTobeFormated = appFprmat.format(dbFormat.parse((activity as MainActivity).facilitySelected.contractcurrentdate))
            currcodate_textviewVal.setText(dateTobeFormated )
            dateTobeFormated = appFprmat.format(dbFormat.parse((activity as MainActivity).facilitySelected.contractinitialdate))
            initcodate_textviewVal.setText(dateTobeFormated)
            dateTobeFormated = appFprmat.format(dbFormat.parse((activity as MainActivity).facilitySelected.automotiverepairexpdate))
            ARDexp_textviewVal.setText(dateTobeFormated)
            billingmonth_textviewVal.setText((activity as MainActivity).facilitySelected.billingmonth.toString())
            billingamount_textviewVal.setText((activity as MainActivity).facilitySelected.billingamount.toString())
            ARDno_textviewVal.setText((activity as MainActivity).facilitySelected.automotiverepairnumber.toString())

//            providertype_textviewVal.setText((activity as MainActivity).facilitySelected.)
            dateTobeFormated = appFprmat.format(dbFormat.parse((activity as MainActivity).facilitySelected.insuranceexpdate))
            InsuranceExpDate_textviewVal.setText(dateTobeFormated)

        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
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
        fun newInstance(param1: String, param2: String): FragmentARRAVFacility {
            val fragment = FragmentARRAVFacility()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
