package com.inspection.fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVPersonnel.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVPersonnel : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravpersonnel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //inputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//

        var personTypeArray = arrayOf("Assistant Manager",  "Body Shop Manager", "Cashier",  "Chief Executive Officer",  "Chief Financial Officer", "Controller",  "Delivery Driver", "Director", "Fixed Ops Director", "Floating Manager",  "Foreman", "General Manager", "General Partner", "General Service", "Limited Partner", "Manager", "Managing Member", "Marketing Manager", "Member", "Office Manager", "Owner", "Partner", "Parts and Service Director", "Parts Manager", "Porter", "President", "Registered Agent", "Retail Manager", "Secretary Service", "Director", "Service Manager", "Service Writer", "Shop Foreman", "Store Manager", "Supervisor", "Technician", "Treasurer", "Vice President")
        var personTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, personTypeArray)
        personTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personType_textviewVal.adapter = personTypeAdapter

        var statesArray = arrayOf("Alabama ","Alaska ","Arizona ","Arkansas ","California ","Colorado ","Connecticut ","Delaware ","Florida ","Georgia ","Hawaii ","Idaho ","Illinois","Indiana ","Iowa ","Kansas ","Kentucky ","Louisiana ","Maine ","Maryland ","Massachusetts ","Michigan ","Minnesota ","Mississippi ","Missouri ","Montana","Nebraska ","Nevada ","New Hampshire ","New Jersey ","New Mexico ","New York ","North Carolina ","North Dakota ","Ohio ","Oklahoma ","Oregon ","Pennsylvania","Rhode Island ","South Carolina ","South Dakota ","Tennessee ","Texas ","Utah ","Vermont ","Virginia ","Washington ","West Virginia ","Wisconsin ","Wyoming")
        var statesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, statesArray)
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coSignerStateVal.adapter = statesAdapter

        a1CertDateVal.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year,monthOfYear,dayOfMonth)
                    a1CertDateVal!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
        }
        a2CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a2CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a3CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a3CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a4CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a4CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a5CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a5CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a6CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a6CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a7CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a7CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a8CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a8CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        c1CertDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                c1CertDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        a1ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a1ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a2ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a2ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a3ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a3ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a4ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a4ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a5ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a5ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a6ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a6ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a7ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a7ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        a8ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                a8ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        c1ExpDateVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                c1ExpDateVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
    }



    fun validateInputs() : Boolean {
        var isInputsValid = true

        firstName_textviewVal.setError(null)
        lastName_textviewVal.setError(null)
        coSignerAddr1Val.setError(null)
        coSignerAddr2Val.setError(null)
        coSignerCityVal.setError(null)
        coSignerZip4Val.setError(null)
        coSignerZipVal.setError(null)
        coSignerEmailVal.setError(null)
        coSignerPhoneVal.setError(null)
        coSignerCoEndDateVal.setError(null)
        coSignerCoStartDateVal.setError(null)
        a1CertDateVal.setError(null)
        a1ExpDateVal.setError(null)
        a2CertDateVal.setError(null)
        a2ExpDateVal.setError(null)
        a3CertDateVal.setError(null)
        a3ExpDateVal.setError(null)
        a4CertDateVal.setError(null)
        a4ExpDateVal.setError(null)
        a5CertDateVal.setError(null)
        a5ExpDateVal.setError(null)
        a6CertDateVal.setError(null)
        a6ExpDateVal.setError(null)
        a7CertDateVal.setError(null)
        a7ExpDateVal.setError(null)
        a8CertDateVal.setError(null)
        a8ExpDateVal.setError(null)
        c1CertDateVal.setError(null)
        c1ExpDateVal.setError(null)

        if(firstName_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            firstName_textviewVal.setError("Required Field")
        }

        if(lastName_textviewVal.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            lastName_textviewVal.setError("Required Field")
        }

        if (contractSignerCheckBox.isChecked) {
            if(coSignerAddr1Val.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerAddr1Val.setError("Required Field")
            }
            if(coSignerAddr2Val.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerAddr2Val.setError("Required Field")
            }
            if(coSignerCityVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerCityVal.setError("Required Field")
            }
            if(coSignerZipVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerZipVal.setError("Required Field")
            }
            if(coSignerZip4Val.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerZip4Val.setError("Required Field")
            }
            if(coSignerPhoneVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerPhoneVal.setError("Required Field")
            }
            if(coSignerEmailVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerEmailVal.setError("Required Field")
            }
            if(coSignerCoStartDateVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerCoStartDateVal.setError("Required Field")
            }
            if(coSignerCoEndDateVal.text.toString().isNullOrEmpty()) {
                isInputsValid=false
                coSignerCoEndDateVal.setError("Required Field")
            }
        }
        if (!a1CertDateVal.text.equals("SELECT DATE")) {
            if(a1ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a1ExpDateVal.setError("Required Field")
            }
        }

        if (!a2CertDateVal.text.equals("SELECT DATE")) {
            if(a2ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a2ExpDateVal.setError("Required Field")
            }
        }

        if (!a3CertDateVal.text.equals("SELECT DATE")) {
            if(a3ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a3ExpDateVal.setError("Required Field")
            }
        }

        if (!a4CertDateVal.text.equals("SELECT DATE")) {
            if(a4ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a4ExpDateVal.setError("Required Field")
            }
        }

        if (!a5CertDateVal.text.equals("SELECT DATE")) {
            if(a5ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a5ExpDateVal.setError("Required Field")
            }
        }

        if (!a6CertDateVal.text.equals("SELECT DATE")) {
            if(a6ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a6ExpDateVal.setError("Required Field")
            }
        }

        if (!a7CertDateVal.text.equals("SELECT DATE")) {
            if(a7ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a7ExpDateVal.setError("Required Field")
            }
        }

        if (!a8CertDateVal.text.equals("SELECT DATE")) {
            if(a8ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                a8ExpDateVal.setError("Required Field")
            }
        }

        if (!c1CertDateVal.text.equals("SELECT DATE")) {
            if(c1ExpDateVal.text.equals("SELECT DATE")) {
                isInputsValid=false
                c1ExpDateVal.setError("Required Field")
            }
        }


        return isInputsValid
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
         * @return A new instance of fragment FragmentARRAVPersonnel.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVPersonnel {
            val fragment = FragmentARRAVPersonnel()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
