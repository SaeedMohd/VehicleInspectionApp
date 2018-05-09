package com.inspection.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import com.inspection.model.*
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityContinued.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityContinued.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityContinued : Fragment() {

    private var opHoursArray = arrayOf("Closed", "12:00:00 AM", "12:30:00 AM", "01:00:00 AM", "01:30:00 AM", "02:00:00 AM", "02:30:00 AM", "03:00:00 AM", "03:30:00 AM", "04:00:00 AM", "04:30:00 AM", "05:00:00 AM", "05:30:00 AM", "06:00:00 AM", "06:30:00 AM"
            , "07:00:00 AM", "07:30:00 AM", "08:00:00 AM", "08:30:00 AM", "09:00:00 AM", "09:30:00 AM", "10:00:00 AM", "10:30:00 AM", "11:00:00 AM", "11:30:00 AM", "12:00:00 PM", "12:30:00 PM", "01:00:00 PM", "01:30:00 PM", "02:00:00 PM", "02:30:00 PM"
            , "03:00:00 PM", "03:30:00 PM", "04:00:00 PM", "04:30:00 PM", "05:00:00 PM", "05:30:00 PM", "06:00:00 PM", "06:30:00 PM", "07:00:00 PM", "07:30:00 PM", "08:00:00 PM", "08:30:00 PM", "09:00:00 PM", "09:30:00 PM", "10:00:00 PM", "10:30:00 PM"
            , "11:00:00 PM", "11:30:00 PM")
    private val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val appFormat = SimpleDateFormat("HH:mm")
    private var dateTobeFormated = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravfacility_continued, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var opHoursAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, opHoursArray)
        opHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



//        saturday_closed_spinner.adapter = opHoursAdapter
//        saturday_open_spinner.adapter = opHoursAdapter
//        sunday_closed_spinner.adapter = opHoursAdapter
//        sunday_open_spinner.adapter = opHoursAdapter
//        monday_closed_spinner.adapter = opHoursAdapter
//        monday_open_spinner.adapter = opHoursAdapter
//        tuesday_closed_spinner.adapter = opHoursAdapter
//        tuesday_open_spinner.adapter = opHoursAdapter
//        wednesday_closed_spinner.adapter = opHoursAdapter
//        wednesday_open_spinner.adapter = opHoursAdapter
//        thursday_closed_spinner.adapter = opHoursAdapter
//        thursday_open_spinner.adapter = opHoursAdapter
//        friday_closed_spinner.adapter = opHoursAdapter
//        friday_open_spinner.adapter = opHoursAdapter

//        var emailTypeArray = arrayOf("No Email", "Business", "Personnel")
//        var emailTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, emailTypeArray)
//        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        emailtype_textviewVal.adapter = emailTypeAdapter
//
//        var phoneTypeArray = arrayOf("No Phone", "Business", "Cell", "Fax", "Home")
//        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
//        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        phonetype_textviewVal.adapter = phoneTypeAdapter


//        prepareFacilityContinuedPage()


    }

    fun buildLocationsTable(){
        val locationType = TextView(context)
        locationType.text = "Location Type: "


    }

//
//    fun validateInputs(): Boolean {
//        var isInputsValid = true
//
//        email_textviewVal.setError(null)
//        phone_textviewVal.setError(null)
//        if (emailtype_textviewVal.selectedItemId > 0 && email_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            email_textviewVal.setError("Required Field")
//        }
//
//        if (phonetype_textviewVal.selectedItemId > 0 && phone_textviewVal.text.toString().isNullOrEmpty()) {
//            isInputsValid = false
//            phone_textviewVal.setError("Required Field")
//        }
//
//        return isInputsValid
//    }
//
//
//    var isFirstRun: Boolean = true
//
//    fun prepareFacilityContinuedPage() {
//        progressbarFacContinued.visibility = View.VISIBLE
//
//        setFacilityHours()
//        setFacilityEmail()
//        setFacilityPhoneNumber()
//
//        progressbarFacContinued.visibility = View.GONE
//
//    }
//
//
//    fun setFacilityHours() {
//
//        for (fac in FacilityDataModel.getInstance().tblHours) {
//            // Monday
//            if (fac.MonOpen.isNullOrEmpty())
//                monday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.MonOpen
//                monday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.MonClose.isNullOrEmpty())
//                monday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.MonClose
//                monday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Tuesday
//            if (fac.TueOpen.isNullOrEmpty())
//                tuesday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.TueOpen
//                tuesday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.TueClose.isNullOrEmpty())
//                tuesday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.TueClose
//                tuesday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Wednesday
//            if (fac.WedOpen.isNullOrEmpty())
//                wednesday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.WedOpen
//                wednesday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.WedClose.isNullOrEmpty())
//                wednesday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.WedClose
//                wednesday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Thursday
//            if (fac.ThuOpen.isNullOrEmpty())
//                thursday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.ThuOpen
//                thursday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.ThuClose.isNullOrEmpty())
//                thursday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.ThuClose
//                thursday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Friday
//            if (fac.FriOpen.isNullOrEmpty())
//                friday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.FriOpen
//                friday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.FriClose.isNullOrEmpty())
//                friday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.FriClose
//                friday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Saturday
//            if (fac.SatOpen.isNullOrEmpty())
//                saturday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.SatOpen
//                saturday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.SatClose.isNullOrEmpty())
//                saturday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.SatClose
//                saturday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            // Sunday
//            if (fac.SunOpen.isNullOrEmpty())
//                sunday_open_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.SunOpen
//                sunday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            if (fac.SunClose.isNullOrEmpty())
//                sunday_closed_spinner.setSelection(0)
//            else {
//                dateTobeFormated = fac.SunClose
//                sunday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
//            }
//            nightdrop_checkbox.isChecked = fac.NightDrop
//            nightinstructions_textviewVal.setText(if (fac.NightDropInstr.isNullOrEmpty()) "" else fac.NightDropInstr)
//        }
//
//    }
//
//    private fun setFacilityEmail() {
//        emailtype_textviewVal.setSelection(FacilityDataModel.getInstance().tblFacilityEmail[0].emailTypeId.toInt())
//        email_textviewVal.setText(FacilityDataModel.getInstance().tblFacilityEmail[0].email)
//    }
//
//    private fun setFacilityPhoneNumber() {
//        phonetype_textviewVal.setSelection(FacilityDataModel.getInstance().tblPhone[0].PhoneTypeID.toInt())
//        phone_textviewVal.setText(FacilityDataModel.getInstance().tblPhone[0].PhoneNumber)
//    }

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
//        mListener = null
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
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FragmentARRAVFacilityContinued.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVFacilityContinued {
            val fragment = FragmentARRAVFacilityContinued()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
