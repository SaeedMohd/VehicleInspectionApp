package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import android.util.Log
import android.widget.Toast
import androidx.view.isInvisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity
import com.inspection.Utils.Consts
import com.inspection.Utils.toast
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.dialog_user_register.*
import java.text.SimpleDateFormat
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityContinued.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityContinued.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityContinued : Fragment() {

    private var opHoursArray = arrayOf("Closed", "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30"
            , "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00"
            , "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
            , "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30")
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
        saturday_closed_spinner.adapter = opHoursAdapter
        saturday_open_spinner.adapter = opHoursAdapter
        sunday_closed_spinner.adapter = opHoursAdapter
        sunday_open_spinner.adapter = opHoursAdapter
        monday_closed_spinner.adapter = opHoursAdapter
        monday_open_spinner.adapter = opHoursAdapter
        tuesday_closed_spinner.adapter = opHoursAdapter
        tuesday_open_spinner.adapter = opHoursAdapter
        wednesday_closed_spinner.adapter = opHoursAdapter
        wednesday_open_spinner.adapter = opHoursAdapter
        thursday_closed_spinner.adapter = opHoursAdapter
        thursday_open_spinner.adapter = opHoursAdapter
        friday_closed_spinner.adapter = opHoursAdapter
        friday_open_spinner.adapter = opHoursAdapter

        var emailTypeArray = arrayOf("No Email", "Business", "Personnel")
        var emailTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, emailTypeArray)
        emailTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailtype_textviewVal.adapter = emailTypeAdapter

        var phoneTypeArray = arrayOf("No Phone", "Business", "Cell", "Fax", "Home")
        var phoneTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, phoneTypeArray)
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phonetype_textviewVal.adapter = phoneTypeAdapter


        prepareFacilityContinuedPage()


    }


    fun validateInputs(): Boolean {
        var isInputsValid = true

        email_textviewVal.setError(null)
        phone_textviewVal.setError(null)
        if (emailtype_textviewVal.selectedItemId > 0 && email_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            email_textviewVal.setError("Required Field")
        }

        if (phonetype_textviewVal.selectedItemId > 0 && phone_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid = false
            phone_textviewVal.setError("Required Field")
        }

        return isInputsValid
    }


    var isFirstRun: Boolean = true

    fun prepareFacilityContinuedPage() {
        progressbarFacContinued.visibility = View.VISIBLE

        AnnualVisitationSingleton.getInstance().apply {
            if (paymentMethodsList == null) {
                getPaymentMethods()
            } else {
                setPaymentMethods()
                setFacilityHours()
                setFacilityEmail()
                setFacilityPhoneNumber()
            }
        }
        progressbarFacContinued.visibility = View.GONE

    }

    fun getPaymentMethods() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.paymentMethodsURL,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {

                        AnnualVisitationSingleton.getInstance().paymentMethodsList = Gson().fromJson(response.toString(), Array<AAAPaymentMethods>::class.java).toCollection(ArrayList())
                        setPaymentMethods()
                    })
                    getFacilityHours()
                }, Response.ErrorListener {
            activity!!.toast("Connection Error. Please check the internet connection")
        }))
    }

    fun setPaymentMethods() {
        var selectedPaymentMethods = AnnualVisitationSingleton.getInstance().paymentMethods.split(",").toCollection(ArrayList<String>())

        for (fac in AnnualVisitationSingleton.getInstance().paymentMethodsList!!) {
            if (fac.pmtmethodid == 1) {
                visa_checkbox.isChecked = selectedPaymentMethods.contains("1")
            } else if (fac.pmtmethodid == 2) {
                mastercard_checkbox.isChecked = selectedPaymentMethods.contains("2")
            } else if (fac.pmtmethodid == 3) {
                americanexpress_checkbox.isChecked = selectedPaymentMethods.contains("3")
            } else if (fac.pmtmethodid == 4) {
                discover_checkbox.isChecked = selectedPaymentMethods.contains("4")
            } else if (fac.pmtmethodid == 5) {
                paypal_checkbox.isChecked = selectedPaymentMethods.contains("5")
            } else if (fac.pmtmethodid == 6) {
                debit_checkbox.isChecked = selectedPaymentMethods.contains("6")
            } else if (fac.pmtmethodid == 7) {
                cash_checkbox.isChecked = selectedPaymentMethods.contains("7")
            } else if (fac.pmtmethodid == 8) {
                check_checkbox.isChecked = selectedPaymentMethods.contains("8")
            } else if (fac.pmtmethodid == 9) {
                goodyear_checkbox.isChecked = selectedPaymentMethods.contains("9")
            }
        }
    }

    fun getFacilityHours() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.facilityHoursURL + AnnualVisitationSingleton.getInstance().facilityId,
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        AnnualVisitationSingleton.getInstance().facilityHoursList = Gson().fromJson(response.toString(), Array<AAAFacilityHours>::class.java).toCollection(ArrayList())
                        setFacilityHours()
                    })
                    if (AnnualVisitationSingleton.getInstance().emailModel == null || AnnualVisitationSingleton.getInstance().emailModel!!.emailid > -1) {
                        getFacilityEmail()
                    } else {
                        progressbarFacContinued.visibility = View.INVISIBLE
                    }
                }, Response.ErrorListener {
            Log.v("error while loading", "error while loading Facility Timing")
            activity!!.toast("Connection Error. Please check the internet connection")
        }))
    }

    fun setFacilityHours() {

        for (fac in AnnualVisitationSingleton.getInstance().facilityHoursList!!) {
            // Monday
            if (fac.monopen.isNullOrEmpty())
                monday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.monopen))
                monday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.monclose.isNullOrEmpty())
                monday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.monclose))
                monday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Tuesday
            if (fac.tueopen.isNullOrEmpty())
                tuesday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.tueopen))
                tuesday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.tueclose.isNullOrEmpty())
                tuesday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.tueclose))
                tuesday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Wednesday
            if (fac.wedopen.isNullOrEmpty())
                wednesday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.wedopen))
                wednesday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.wedclose.isNullOrEmpty())
                wednesday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.wedclose))
                wednesday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Thursday
            if (fac.thuopen.isNullOrEmpty())
                thursday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.thuopen))
                thursday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.thuclose.isNullOrEmpty())
                thursday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.thuclose))
                thursday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Friday
            if (fac.friopen.isNullOrEmpty())
                friday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.friopen))
                friday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.friclose.isNullOrEmpty())
                friday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.friclose))
                friday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Saturday
            if (fac.satopen.isNullOrEmpty())
                saturday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.satopen))
                saturday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.satclose.isNullOrEmpty())
                saturday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.satclose))
                saturday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            // Sunday
            if (fac.sunopen.isNullOrEmpty())
                sunday_open_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.sunopen))
                sunday_open_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            if (fac.sunclose.isNullOrEmpty())
                sunday_closed_spinner.setSelection(0)
            else {
                dateTobeFormated = appFormat.format(dbFormat.parse(fac.sunclose))
                sunday_closed_spinner.setSelection(opHoursArray.indexOf(dateTobeFormated))
            }
            nightdrop_checkbox.isChecked = (fac.nightdrop == 1)
            nightinstructions_textviewVal.setText(if (fac.nightdropinstr.isNullOrEmpty()) "" else fac.nightdropinstr)
        }

    }

    private fun getFacilityEmail() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Consts.getEmailFromFacilityAndId, AnnualVisitationSingleton.getInstance().facilityId, AnnualVisitationSingleton.getInstance().emailModel!!.emailid),
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {

                        AnnualVisitationSingleton.getInstance().emailModel = Gson().fromJson(response.toString(), Array<AAAEmailModel>::class.java).toCollection(ArrayList()).get(0)
                        setFacilityEmail()
                    })

                    if (AnnualVisitationSingleton.getInstance().phoneModel != null) {
                        getFacilityPhoneNumber()
                    } else {
                        progressbarFacContinued.visibility = View.INVISIBLE
                    }

                }, Response.ErrorListener {
            activity!!.toast("Connection Error. Please check the internet connection")
        }))
    }

    private fun setFacilityEmail() {
        emailtype_textviewVal.setSelection(AnnualVisitationSingleton.getInstance().emailModel!!.emailtypeid)
        email_textviewVal.setText(AnnualVisitationSingleton.getInstance().emailModel!!.email)
    }

    private fun getFacilityPhoneNumber() {
        Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, String.format(Consts.getPhoneNumberWithFacilityAndId, AnnualVisitationSingleton.getInstance().facilityId, AnnualVisitationSingleton.getInstance().phoneModel!!.phoneid),
                Response.Listener { response ->
                    activity!!.runOnUiThread(Runnable {
                        AnnualVisitationSingleton.getInstance().phoneModel = Gson().fromJson(response.toString(), Array<AAAPhoneModel>::class.java).toCollection(ArrayList()).get(0)
                        setFacilityPhoneNumber()
                    })
                    progressbarFacContinued.visibility = View.INVISIBLE
                }, Response.ErrorListener {
            activity!!.toast("Connection Error. Please check the internet connection")
        }))
    }

    private fun setFacilityPhoneNumber() {
        phonetype_textviewVal.setSelection(AnnualVisitationSingleton.getInstance().phoneModel!!.phonetypeid)
        phone_textviewVal.setText(AnnualVisitationSingleton.getInstance().phoneModel!!.phonenumber)
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
