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
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.toAppFormat
import com.inspection.Utils.toast
import com.inspection.model.FacilityDataModel
import com.inspection.singletons.AnnualVisitationSingleton
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
    private var timeZonesArray = arrayOf("")
    private var facilityTypeArray = arrayOf("")

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
        timeZonesArray = arrayOf("Atlantic Time", "Eastern Time", "Central Time", "Mountain Time", "Pacific Time", "Hawaii  Time")
        var tzdataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, timeZonesArray)
        tzdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezone_textviewVal.adapter = tzdataAdapter


        var svcAvailabilityArray = arrayOf("Fixed-Site Service Only", "Fixed and Mobile Service", "Mobile Service Only")
        var svcAvldataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, svcAvailabilityArray)
        svcAvldataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availability_textviewVal.adapter = svcAvldataAdapter

        facilityTypeArray = arrayOf("Independent", "Service Station", "Specialty", "Dealership", "Club Owned Repair - Attached", "Club Owned Repair - Standalone")
        var facilityTypedataAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, facilityTypeArray)
        facilityTypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilitytype_textviewVal.adapter = facilityTypedataAdapter


        ARDexp_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
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
                c.set(year, monthOfYear, dayOfMonth)
                ARDexp_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
//
        InsuranceExpDate_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year, monthOfYear, dayOfMonth)
                InsuranceExpDate_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        setFieldsValues()

    }


    private fun setFieldsValues() {
        FacilityDataModel.getInstance().apply {
            try {

                if(tblFacilities[0].ACTIVE==1){
                    activeRadioButton.isChecked = true
                }else{
                    inActiveRadioButton.isChecked = true
                }

                        for (provider in tblFacilityServiceProvider) {
                            when(provider.SrvProviderId){
                                "AAR" -> {
                                    aarCheckBox.isChecked = true
                                    aarEditText.setText(""+provider.ProviderNum)
                                }

                                "AABِ" -> {
                                    aabCheckBox.isChecked = true
                                    aabEditText.setText(provider.ProviderNum)
                                }
                                "ِAAG" -> {
                                    aagCheckBox.isChecked = true
                                    aagEditText.setText(provider.ProviderNum)
                                }
                                "COG" -> {
                                    cogCheckBox.isChecked = true
                                    cogEditText.setText(provider.ProviderNum)
                                }
                                "CCR" -> {
                                    corCheckBox.isChecked = true
                                    corEditText.setText(provider.ProviderNum)
                                }
                                "ERS" -> {
                                    ersCheckBox.isChecked = true
                                    ersEditText.setText(provider.ProviderNum)
                                }
                                "MPR" -> {
                                    mprCheckBox.isChecked = true
                                    mprEditText.setText(provider.ProviderNum)
                                }
                                "PSP" -> {
                                    pspCheckBox.isChecked = true
                                    pspEditText.setText(provider.ProviderNum)
                                }

                            }
                        }

                contract_number_textviewVal.text = "" + tblFacilities[0].FACNo

                if (tblOfficeType[0].OfficeName.isNotEmpty()) {
                    office_textviewVal.text = "" + tblOfficeType[0].OfficeName
                }else{
                    office_textviewVal.text = ""
                }
                assignedto_textviewVal.text = tblFacilities[0].AssignedTo
                dba_textviewVal.text = tblFacilities[0].BusinessName
                entity_textviewVal.text = tblFacilities[0].EntityName
                bustype_textviewVal.text = tblBusinessType[0].BusTypeName
                timezone_textviewVal.setSelection(timeZonesArray.indexOf(tblTimezoneType[0].TimezoneName))
                website_textviewVal.setText(tblFacilities[0].WebSite)
                wifi_textview.isChecked = tblFacilities[0].SvcAvailability.toInt() == 1
                texno_textviewVal.text = tblFacilities[0].TaxIDNumber
                repairorder_textviewVal.setText("" + tblFacilities[0].FacilityRepairOrderCount)
                availability_textviewVal.setSelection(tblFacilities[0].SvcAvailability)
                facilitytype_textviewVal.setSelection(facilityTypeArray.indexOf(tblFacilityType[0].FacilityTypeName))
                ARDno_textviewVal.setText(tblFacilities[0].AutomotiveRepairNumber)
                ARDexp_textviewVal.text = tblFacilities[0].AutomotiveRepairExpDate.apiToAppFormat()
                terminationDateButton.text = ""+tblFacilities[0].TerminationDate.apiToAppFormat()
                terminationCommentEditText.setText(""+tblFacilities[0].TerminationComments)
                currcodate_textviewVal.text = tblFacilities[0].ContractCurrentDate.apiToAppFormat()
                initcodate_textviewVal.text = tblFacilities[0].ContractInitialDate.apiToAppFormat()
                InsuranceExpDate_textviewVal.text = tblFacilities[0].InsuranceExpDate.apiToAppFormat()


                for(paymentMethod in tblPaymentMethods){

                }

            }catch (exp:Exception){
                exp.printStackTrace()
            }
        }

        if (arguments!!.getBoolean(isValidating)) {
            validateInputs()
        }

        setPaymentMethods()

        saveButton.setOnClickListener {
            if (validateInputs()){

            }
        }

    }

    fun validateInputs() : Boolean{

        AnnualVisitationSingleton.getInstance().apply {
            if (ardNumber == -1){
                ARDno_textviewVal.error = ""
            }

            if (ardExpirationDate == -1L) {
                ARDexp_textviewVal.error = ""
            }

            if (insuranceExpirationDate == -1L) {
//                InsuranceExpDate_textviewVal.error = ""
            }
        }
        return true
    }


    fun setPaymentMethods() {

        for (fac in FacilityDataModel.getInstance().tblPaymentMethods) {
            when (fac.PmtMethodID.toInt()) {
                1 -> visa_checkbox.isChecked = true
                2 -> mastercard_checkbox.isChecked = true
                3 -> americanexpress_checkbox.isChecked = true
                4 -> discover_checkbox.isChecked = true
                5 -> paypal_checkbox.isChecked = true
                6 -> debit_checkbox.isChecked = true
                7 -> cash_checkbox.isChecked = true
                8 -> check_checkbox.isChecked = true
                9 -> goodyear_checkbox.isChecked = true
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
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
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVFacility.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVFacility {
            val fragment = FragmentARRAVFacility()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
