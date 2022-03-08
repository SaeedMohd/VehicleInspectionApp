package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
//import android.app.Fragment
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.inspection.R
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


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
        return inflater!!.inflate(R.layout.fragment_aarav_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareFacilityContinuedSpinners()
        prepareFacilityContinuedPage()
//        var opHoursAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, opHoursArray)
//        opHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        addNewLocBtn.setOnClickListener({
//            submitLocation()
//        })

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

    }

    fun submitLocation() {
//        val locType = locationTypeList.filter { s -> s.LocTypeName == newLocTypeSpinner.selectedItem.toString()}[0].LocTypeID
//        val locAddr1 = if (newLocAddr1Text.text.isNullOrEmpty())  "" else newLocAddr1Text.text
//        val locAddr2 = if (newLocAddr2Text.text.isNullOrEmpty())  "" else newLocAddr2Text.text
//        val locCity = if (newLocCityText.text.isNullOrEmpty())  "" else newLocCityText.text
//        val locCountry = if (newLocCountryText.text.isNullOrEmpty())  "" else newLocCountryText.text
//        val locState = "CA"//newLocStateSpinner.selectedItem.toString()
//        val locZip3= if (newLocZipText.text.isNullOrEmpty())  "" else newLocZipText.text
////        val locZip4= if (newLocZip4Text.text.isNullOrEmpty())  "" else newLocZip4Text.text
//        val locLat= if (newLocLatText.text.isNullOrEmpty())  "" else newLocLatText.text
//        val locLong = if (newLocLongText.text.isNullOrEmpty())  "" else newLocLongText.text
//        val locBranchNo = if (newLocBranchNoText.text.isNullOrEmpty())  "" else newLocBranchNoText.text
//        val locBranchName = if (newLocBranchNameText.text.isNullOrEmpty())  "" else newLocBranchNameText.text
//        val insertDate = Date() ///HERE
//        val facilityNo = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()
//        val clubCode ="004"
//        val insertedBy ="sa"
////        val urlString = facilityNo+"&clubcode="+clubCode+"&LocationTypeID="+locType+"&FAC_Addr1="+locAddr1+"&FAC_Addr2="+locAddr2+"&CITY="+locCity+"&ST="+locState+"&ZIP="+locZip3+"&ZIP4="+locZip4+"&Country="+locCountry+"&BranchName="+locBranchName+"&BranchNumber="+locBranchNo+"&LATITUDE="+locLat+"&LONGITUDE="+locLong+"&insertBy="+insertedBy+"&insertDate="+insertDate.toDate().toString()+"&updateBy="+insertedBy+"&updateDate="+insertDate+"&active=1"
//        var urlString = ""
//        Volley.newRequestQueue(context).add(StringRequest(Request.Method.POST, Constants.submitContactInfoAddress + urlString,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread(Runnable {
//                        Log.v("RESPONSE",response.toString())
////                        facilitiesList = Gson().fromJson(response.toString(), Array<AAAFacilityComplete>::class.java).toCollection(ArrayList())
////                        facilityNames.clear()
////                        for (fac in facilitiesList) {
////                            facilityNames.add(fac.businessname)
////                        }
////                        facilityNameListView.visibility = View.VISIBLE
////                        facilityNameListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, facilityNames)
//                    })
//                }, Response.ErrorListener {
//            Log.v("error while loading", "error while loading")
//        }))
    }

    fun prepareFacilityContinuedPage(){
        facilityIsOpenEffDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenEffDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        facilityIsOpenExpDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                facilityIsOpenExpDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
    }

    private var locationTypeList = ArrayList<TypeTablesModel.locationType>()
    private var locationypeArray = ArrayList<String>()

    private var phoneTypeList  = ArrayList<TypeTablesModel.locationPhoneType>()
    private var phoneTypeArray = ArrayList<String>()

    private var availabilityTypeList  = ArrayList<TypeTablesModel.facilityAvailabilityType>()
    private var availabilityTypeArray = ArrayList<String>()

    fun prepareFacilityContinuedSpinners() {

        locationTypeList = TypeTablesModel.getInstance().LocationType
        locationypeArray.clear()
        for (fac in locationTypeList) {
            locationypeArray.add(fac.LocTypeName)
        }

//        var locTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, locationypeArray)
//        locTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newLocTypeSpinner.adapter = locTypeAdapter
//        newLoc2TypeSpinner.adapter = locTypeAdapter

        phoneTypeList = TypeTablesModel.getInstance().LocationPhoneType
        phoneTypeArray .clear()
        for (fac in phoneTypeList) {
            phoneTypeArray .add(fac.LocPhoneName)
        }

//        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray )
//        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newPhoneTypeSpinner.adapter = phoneTypeAdapter
//        newPhone2TypeSpinner.adapter = phoneTypeAdapter

        availabilityTypeList = TypeTablesModel.getInstance().FacilityAvailabilityType
        availabilityTypeArray.clear()
        for (fac in availabilityTypeList) {
            availabilityTypeArray.add(fac.FacilityAvailabilityName)
        }

        var availabilityAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, availabilityTypeArray )
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facilityIsOpenSpinner.adapter = availabilityAdapter
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

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
////        if (context is OnFragmentInteractionListener) {
////            mListener = context
////        } else {
////            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
////        }
//    }

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
