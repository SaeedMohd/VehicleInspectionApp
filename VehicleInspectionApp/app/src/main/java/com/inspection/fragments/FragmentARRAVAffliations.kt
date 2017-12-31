package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
import kotlinx.android.synthetic.main.fragment_arrav_facility.*
import kotlinx.android.synthetic.main.fragment_arrav_facility_services.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVAffliations.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVAffliations.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVAffliations : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_affliations   , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var affiliationsArray= arrayOf("ACDelco", "AutoValue", "AutoZone", "Bosch", "Carquest", "DescRepairAffil", "Federated", "Gas Brand", "Mechanical Repair", "NAPA", "Oil", "OtherRepairAffil", "Parts", "PartsPlus", "ProntoVIP", "Quick Lube", "Tire", "Transmission", "WorldPac")
        var affiliationsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affiliationsArray)
        affiliationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        affiliations_textviewVal.adapter = affiliationsAdapter


        affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position==7) {
                    var afDetailsArray = arrayOf("Amoco", "ARCO", "BP", "Cenex", "Chevron", "CITGO", "Conoco", "Esso", "Exxon", "Gulf", "Hess", "Husky", "Marathon/Speedway", "Mobil", "Petro-Canada", "Phillips 66", "Shell", "Sinclair", "Sunoco", "Texaco", "Union 76")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==9) {
                    var afDetailsArray = arrayOf("All Tune & Lube", "Car-X", "Certified", "Kwik Kar", "Meineke", "Midas", "Monro", "NAPA AutoCare Program", "Precision Tune", "Tuffy")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==10) {
                    var afDetailsArray = arrayOf("Ataram", "B E", "Castrol", "Felt Oil", "Ford", "H & H", "NAPA", "Rorick", "Timmons", "United", "Valvoline", "Velvin")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==12) {
                    var afDetailsArray = arrayOf("AC Delco", "Advanced Auto", "AutoValue", "AutoZone", "Bosch", "Carquest", "Excel", "Federated", "Motorcraft", "NAPA Quality Parts Program", "Oreillys", "PartsPlus", "ProntoVIP", "WorldPac")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==15) {
                    var afDetailsArray = arrayOf("Citgo Fast Lube", "Econo", "Grease Monkey", "Jiffy Lube", "Lube Pros", "Oil Can Henry's", "Quaker State", "Q Lube", "Texaco", "Xpress Lube", "Valvoline Instant Oil Change")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==16) {
                    var afDetailsArray = arrayOf("Active Green + Ross", "Big O Tires", "Discount Tire", "Firestone", "Complete Auto Care", "Goodyear", "Gemini", "National Tire & Battery / Tire Kingdom", "Tires Plus")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else if (position==17) {
                    var afDetailsArray = arrayOf("AAMCO Transmissions", "Cottman Transmissions", "Lee Myles Transmissions", "Mr. Transmission", "Not Applicable")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                } else {
                    var afDetailsArray = arrayOf("Not Applicable")
                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    afDetails_textviewVal.adapter = afDetailsAdapter
                }
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        afDtlseffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                afDtlseffective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        afDtlsexpiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                afDtlsexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }


//        affiliations_textviewVal.onItemSelectedListener = AdapterView.OnItemSelectedListener { adapterView, view, i, l ->
//            if (affiliations_textviewVal.selectedItemPosition==7) {
//                var afDetailsArray = arrayOf("Amoco", "ARCO", "BP", "Cenex", "Chevron", "CITGO", "Conoco", "Esso", "Exxon", "Gulf", "Hess", "Husky", "Marathon/Speedway", "Mobil", "Petro-Canada", "Phillips 66", "Shell", "Sinclair", "Sunoco", "Texaco", "Union 76")
//                var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                afDetails_textviewVal.adapter = afDetailsAdapter
//            } else {
//                var afDetailsArray = arrayOf("No Details")
//                var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                afDetails_textviewVal.adapter = afDetailsAdapter
//            }
//        }

//        affiliations_textviewVal.setOnItemClickListener({
//            if (affiliations_textviewVal.selectedItemPosition==7) {
//                var afDetailsArray = arrayOf("Amoco", "ARCO", "BP", "Cenex", "Chevron", "CITGO", "Conoco", "Esso", "Exxon", "Gulf", "Hess", "Husky", "Marathon/Speedway", "Mobil", "Petro-Canada", "Phillips 66", "Shell", "Sinclair", "Sunoco", "Texaco", "Union 76")
//                var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                afDetails_textviewVal.adapter = afDetailsAdapter
//            } else {
//                var afDetailsArray = arrayOf("No Details")
//                var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                afDetails_textviewVal.adapter = afDetailsAdapter
//            }
//
//        })

    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        afDtlseffective_date_textviewVal.setError(null)

        if(afDtlseffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            afDtlseffective_date_textviewVal.setError("Required Field")
        }


        return isInputsValid
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
        fun newInstance(param1: String, param2: String): FragmentARRAVAffliations {
            val fragment = FragmentARRAVAffliations()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
