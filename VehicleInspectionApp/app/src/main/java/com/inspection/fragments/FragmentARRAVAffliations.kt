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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.Utils.toast
import com.inspection.model.AAAAffiliationTypes
import com.inspection.model.AAAFacilityAffiliations
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
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
    private var affTypesArray = ArrayList<String>()
    private var affTypesDetailsArray = ArrayList<String>()
    private var affTypesList = ArrayList<AAAAffiliationTypes>()
    private var facilityAffList = ArrayList<AAAFacilityAffiliations>()
    private var selectedTypeDetailName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_affliations   , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var affiliationsArray = arrayOf("ACDelco", "AutoValue", "AutoZone", "Bosch", "Carquest", "DescRepairAffil", "Federated", "Gas Brand", "Mechanical Repair", "NAPA", "Oil", "OtherRepairAffil", "Parts", "PartsPlus", "ProntoVIP", "Quick Lube", "Tire", "Transmission", "WorldPac")
//        var affiliationsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affiliationsArray)
//        affiliationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        affiliations_textviewVal.adapter = affiliationsAdapter


//        affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                if (position==7) {
//                    var afDetailsArray = arrayOf("Amoco", "ARCO", "BP", "Cenex", "Chevron", "CITGO", "Conoco", "Esso", "Exxon", "Gulf", "Hess", "Husky", "Marathon/Speedway", "Mobil", "Petro-Canada", "Phillips 66", "Shell", "Sinclair", "Sunoco", "Texaco", "Union 76")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==9) {
//                    var afDetailsArray = arrayOf("All Tune & Lube", "Car-X", "Certified", "Kwik Kar", "Meineke", "Midas", "Monro", "NAPA AutoCare Program", "Precision Tune", "Tuffy")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==10) {
//                    var afDetailsArray = arrayOf("Ataram", "B E", "Castrol", "Felt Oil", "Ford", "H & H", "NAPA", "Rorick", "Timmons", "United", "Valvoline", "Velvin")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==12) {
//                    var afDetailsArray = arrayOf("AC Delco", "Advanced Auto", "AutoValue", "AutoZone", "Bosch", "Carquest", "Excel", "Federated", "Motorcraft", "NAPA Quality Parts Program", "Oreillys", "PartsPlus", "ProntoVIP", "WorldPac")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==15) {
//                    var afDetailsArray = arrayOf("Citgo Fast Lube", "Econo", "Grease Monkey", "Jiffy Lube", "Lube Pros", "Oil Can Henry's", "Quaker State", "Q Lube", "Texaco", "Xpress Lube", "Valvoline Instant Oil Change")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==16) {
//                    var afDetailsArray = arrayOf("Active Green + Ross", "Big O Tires", "Discount Tire", "Firestone", "Complete Auto Care", "Goodyear", "Gemini", "National Tire & Battery / Tire Kingdom", "Tires Plus")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else if (position==17) {
//                    var afDetailsArray = arrayOf("AAMCO Transmissions", "Cottman Transmissions", "Lee Myles Transmissions", "Mr. Transmission", "Not Applicable")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                } else {
//                    var afDetailsArray = arrayOf("Not Applicable")
//                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, afDetailsArray)
//                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    afDetails_textviewVal.adapter = afDetailsAdapter
//                }
//            } // to close the onItemSelected
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }

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

        affaddBtn.setOnClickListener({
            var validAffType = true
            for (fac in facilityAffList) {
                if (fac.typename.equals(affiliations_textviewVal.selectedItem.toString())){
                    context!!.toast("Affiliation Type cannot be duplicated")
                    validAffType=false
                }
            }
            if (validAffType) {
                var item = AAAFacilityAffiliations()
                item.affiliationid = -1
                item.typename = affiliations_textviewVal.getSelectedItem().toString()
                item.typedetailname= afDetails_textviewVal.getSelectedItem().toString()
                item.effdate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlseffective_date_textviewVal.text.toString()
                item.expdate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlsexpiration_date_textviewVal.text.toString()
                item.comments=affcomments_editTextVal.text.toString()
                facilityAffList.add(facilityAffList.size, item)
                BuildAffiliationsList()
            }
        })

        affdeleteBtn.setOnClickListener({
            var itemFound =false
            var item = AAAFacilityAffiliations()
            for (fac in facilityAffList) {
                if (fac.typename.equals(affiliations_textviewVal.getSelectedItem().toString())){
                    item = fac
                    itemFound=true
                }
            }
            if (itemFound) {
                facilityAffList.remove(item)
                BuildAffiliationsList()
            }
        })

        affeditBtn.setOnClickListener({
            for (fac in facilityAffList) {
                if (fac.typename.equals(affiliations_textviewVal.getSelectedItem().toString())){
                    fac.effdate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE") || afDtlseffective_date_textviewVal.text.isNullOrEmpty() || afDtlseffective_date_textviewVal.text.equals("") || afDtlseffective_date_textviewVal.text.equals("NULL") || afDtlseffective_date_textviewVal.text.toString().toLowerCase().equals("no date provided")) "" else afDtlseffective_date_textviewVal.text.toString()
                    fac.expdate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE") || afDtlsexpiration_date_textviewVal.text.isNullOrEmpty() || afDtlsexpiration_date_textviewVal.text.equals("") || afDtlsexpiration_date_textviewVal.text.equals("NULL") || afDtlsexpiration_date_textviewVal.text.toString().toLowerCase().equals("no date provided")) "" else afDtlsexpiration_date_textviewVal.text.toString()
                    fac.comments = affcomments_editTextVal.text.toString()
                }
                BuildAffiliationsList()
            }
        })

        prepareAffiliations()

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


    fun prepareAffiliations () {


            progressbarAff.visibility = View.VISIBLE
            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getAffTypesURL,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            affTypesList= Gson().fromJson(response.toString(), Array<AAAAffiliationTypes>::class.java).toCollection(ArrayList())
                            affTypesArray.clear()
                            for (fac in affTypesList) {
                                if (affTypesArray.size<=1)
                                    affTypesArray.add(fac.typename)
                                else {
                                    if (!fac.typename.equals(affTypesArray[affTypesArray.size - 1]))
                                        affTypesArray.add(fac.typename)
                                }
                            }
                            var typeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesArray)
                            typeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            affiliations_textviewVal.adapter = typeAdapter
                            affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    affTypesDetailsArray.clear()
                                    afDetails_textviewVal.adapter =null
                                    for (fac in affTypesList){
                                        if (fac.typename.equals(affiliations_textviewVal.selectedItem.toString()) && (!fac.typedetailname.isNullOrEmpty())) {
                                            affTypesDetailsArray.add(fac.typedetailname)
                                        }
                                    }
                                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray)
                                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    afDetails_textviewVal.adapter = afDetailsAdapter
                                    if (!selectedTypeDetailName.isNullOrEmpty()){
                                        afDetails_textviewVal.setSelection(affTypesDetailsArray.indexOf(selectedTypeDetailName))
                                    }
                                    selectedTypeDetailName=""
                                } // to close the onItemSelected
                                override fun onNothingSelected(parent: AdapterView<*>) {
                                }
                            }
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading Affiliations Types")
                activity!!.toast("Connection Error. Please check the internet connection")
            }))

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.getFacilityAffiliations+ AnnualVisitationSingleton.getInstance().facilityId,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            facilityAffList= Gson().fromJson(response.toString(), Array<AAAFacilityAffiliations>::class.java).toCollection(ArrayList())
//                            drawProgramsTable()
                            BuildAffiliationsList()
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading facility programs")
                activity!!.toast("Connection Error. Please check the internet connection")
            }))

            progressbarAff.visibility = View.INVISIBLE
    }

    fun BuildAffiliationsList() {
        val inflater = activity!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentLayout = affListLL
        parentLayout.removeAllViews()
        for (fac in facilityAffList) {
            val vAffRow = inflater.inflate(R.layout.custom_aff_list_item, parentLayout, false)
            val affId= vAffRow.findViewById(R.id.affItemId) as TextView
            val affTypeName= vAffRow.findViewById(R.id.affItemtypeName) as TextView
            val affTypeDetailName= vAffRow.findViewById(R.id.affItemtypeDetailName) as TextView
            val affEffDate= vAffRow.findViewById(R.id.affItemEffDate) as TextView
            val affExpDate= vAffRow.findViewById(R.id.affItemExpDate) as TextView
            affId.text = fac.affiliationid.toString()
            affTypeName.text = fac.typename
            affTypeDetailName.text = fac.typedetailname
            affEffDate.text = if (fac.effdate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.effdate)) else fac.effdate
            affExpDate.text = if (fac.expdate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.expdate)) else fac.expdate
            vAffRow.setOnClickListener({
                affiliations_textviewVal.setSelection(affTypesArray.indexOf(fac.typename))
//                afDetails_textviewVal.setSelection(affTypesDetailsArray.indexOf(fac.typedetailname))
                selectedTypeDetailName=fac.typedetailname
                afDtlseffective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.toLowerCase().equals("no date provided")) "No Date Provided" else  {
                    if (fac.effdate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.effdate)) else fac.effdate
                }
                afDtlsexpiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.toLowerCase().equals("no date provided")) "No Date Provided" else   {
                    if (fac.expdate.length>11 ) Consts.appFormat.format(Consts.dbFormat.parse(fac.expdate)) else fac.expdate
                }
                affcomments_editTextVal.setText(fac.comments)
            })
            parentLayout.addView(vAffRow)
        }
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
