package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
import com.inspection.Utils.Constants
import com.inspection.Utils.Constants.UpdateAffiliationsData
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.toast
import com.inspection.model.AAAAffiliationTypes
import com.inspection.model.AAAFacilityAffiliations
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
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
    private var affTypesDetailsList = ArrayList<TypeTablesModel.affiliationDetailType>()
    private var affTypesDetailsArray = ArrayList<String>()
    private var affTypesList = ArrayList<AAAAffiliationTypes>()
    private var facilityAffList = ArrayList<AAAFacilityAffiliations>()
    private var selectedTypeDetailName = ""
    var rowIndex = 0
    var indexToRemove=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_affliations   , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scopeOfServiceChangesWatcher()

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


        exitAffDialogeBtnId.setOnClickListener({

            affiliationsCard.visibility=View.GONE
            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE


        })
        edit_exitAffDialogeBtnId.setOnClickListener({

            fillAffTableView()
            altLocationTableRow(2)

            edit_affiliationsCard.visibility=View.GONE
            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE


        })

        addNewAffil.setOnClickListener(View.OnClickListener {

                        affiliationsCard.visibility=View.VISIBLE
            alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE


        })

        fillAffTableView()


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


        edit_afDtlseffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_afDtlseffective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        edit_afDtlsexpiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_afDtlsexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        submitNewAffil.setOnClickListener({
            var validAffType = true


            if (validateInputs()) {
                affLoadingView.visibility = View.VISIBLE

                var startDate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlseffective_date_textviewVal.text.toString()
                var endDate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlsexpiration_date_textviewVal.text.toString()
                var comment = affcomments_editTextVal.text.toString()
//
//                var affType = affiliations_textviewVal.selectedItem.toString()
//                var affDetail= afDetails_textviewVal.selectedItem.toString()
//


                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + "&affiliationId=4931&affiliationTypeId=19&affiliationTypeDetailsId=63&effDate=1900-01-01T00:00:00&expDate=2013-11-13T00:00:00&comment=per%2011/13/13%20visitation&active=1&insertBy=sa&insertDate=2014-07-23T22:15:44.150&updateBy=SumA&updateDate=2014-07-23T22:15:44.150",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE",response.toString())
                                affLoadingView.visibility = View.GONE


                                affiliationsCard.visibility = View.GONE
                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                           //     FacilityDataModel.getInstance().tblAARPortalAdmin.add(portalTrackingentry)
                                fillAffTableView()
                                altLocationTableRow(2)

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    affLoadingView.visibility = View.GONE

                }))
            }else
                Toast.makeText(context,"please fill all required field",Toast.LENGTH_SHORT).show()



//            for (fac in facilityAffList) {
//                if (fac.typename.equals(affiliations_textviewVal.selectedItem.toString())){
//                    context!!.toast("Affiliation Type cannot be duplicated")
//                    validAffType=false
//                }
//            }
//            if (validAffType) {

//                var item = AAAFacilityAffiliations()
//                item.affiliationid = -1
//                item.typename = affiliations_textviewVal.getSelectedItem().toString()
//                item.typedetailname= afDetails_textviewVal.getSelectedItem().toString()
//                item.effdate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlseffective_date_textviewVal.text.toString()
//                item.expdate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlsexpiration_date_textviewVal.text.toString()
//                item.comments=affcomments_editTextVal.text.toString()
//                facilityAffList.add(facilityAffList.size, item)
//              //  BuildAffiliationsList()
//            }
        })
//
//        affdeleteBtn.setOnClickListener({
//            var itemFound =false
//            var item = AAAFacilityAffiliations()
//            for (fac in facilityAffList) {
//                if (fac.typename.equals(affiliations_textviewVal.getSelectedItem().toString())){
//                    item = fac
//                    itemFound=true
//                }
//            }
//            if (itemFound) {
//                facilityAffList.remove(item)
//                //BuildAffiliationsList()
//            }
//        })
//
//        affeditBtn.setOnClickListener({
//            for (fac in facilityAffList) {
//                if (fac.typename.equals(affiliations_textviewVal.getSelectedItem().toString())){
//                    fac.effdate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE") || afDtlseffective_date_textviewVal.text.isNullOrEmpty() || afDtlseffective_date_textviewVal.text.equals("") || afDtlseffective_date_textviewVal.text.equals("NULL") || afDtlseffective_date_textviewVal.text.toString().toLowerCase().equals("no date provided")) "" else afDtlseffective_date_textviewVal.text.toString()
//                    fac.expdate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE") || afDtlsexpiration_date_textviewVal.text.isNullOrEmpty() || afDtlsexpiration_date_textviewVal.text.equals("") || afDtlsexpiration_date_textviewVal.text.equals("NULL") || afDtlsexpiration_date_textviewVal.text.toString().toLowerCase().equals("no date provided")) "" else afDtlsexpiration_date_textviewVal.text.toString()
//                    fac.comments = affcomments_editTextVal.text.toString()
//                }
//                //BuildAffiliationsList()
//            }
//        })
//
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

//        affTypesArray.clear()
//        var typeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesArray)
//        typeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        affiliations_textviewVal.adapter = typeAdapter

        // SAEED Need to implement dependency between Type & Type Detail

        affTypesDetailsList = TypeTablesModel.getInstance().AffiliationDetailType
        affTypesDetailsArray.clear()
        for (fac in affTypesDetailsList ) {
            affTypesDetailsArray.add(fac.AffiliationDetailTypeName)
        }

        var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray);
        afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        afDetails_textviewVal.adapter = afDetailsAdapter


//            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getAffTypesURL,
//                    Response.Listener { response ->
//                        activity!!.runOnUiThread(Runnable {
//                            affTypesList= Gson().fromJson(response.toString(), Array<AAAAffiliationTypes>::class.java).toCollection(ArrayList())
//                            affTypesArray.clear()
//                            for (fac in affTypesList) {
//                                if (affTypesArray.size<=1)
//                                    affTypesArray.add(fac.typename)
//                                else {
//                                    if (!fac.typename.equals(affTypesArray[affTypesArray.size - 1]))
//                                        affTypesArray.add(fac.typename)
//                                }
//                            }
//                            var typeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesArray)
//                            typeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            affiliations_textviewVal.adapter = typeAdapter
//                            affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                                    affTypesDetailsArray.clear()
//                                    afDetails_textviewVal.adapter =null
//                                    for (fac in affTypesList){
//                                        if (fac.typename.equals(affiliations_textviewVal.selectedItem.toString()) && (!fac.typedetailname.isNullOrEmpty())) {
//                                            affTypesDetailsArray.add(fac.typedetailname)
//                                        }
//                                    }
//                                    var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray)
//                                    afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    afDetails_textviewVal.adapter = afDetailsAdapter
//                                    if (!selectedTypeDetailName.isNullOrEmpty()){
//                                        afDetails_textviewVal.setSelection(affTypesDetailsArray.indexOf(selectedTypeDetailName))
//                                    }
//                                    selectedTypeDetailName=""
//                                } // to close the onItemSelected
//                                override fun onNothingSelected(parent: AdapterView<*>) {
//                                }
//                            }
//                        })
//                    }, Response.ErrorListener {
//                Log.v("error while loading", "error while loading Affiliations Types")
//                activity!!.toast("Connection Error. Please check the internet connection")
//            }))

//            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.getFacilityAffiliations+ AnnualVisitationSingleton.getInstance().facilityId,
//                    Response.Listener { response ->
//                        activity!!.runOnUiThread(Runnable {
//                            facilityAffList= Gson().fromJson(response.toString(), Array<AAAFacilityAffiliations>::class.java).toCollection(ArrayList())
////                            drawProgramsTable()
//                  //          BuildAffiliationsList()
//                        })
//                    }, Response.ErrorListener {
//                Log.v("error while loading", "error while loading facility programs")
//                activity!!.toast("Connection Error. Please check the internet connection")
//            }))

    }


    fun fillAffTableView(){






        mainViewLinearId.isEnabled=true

        //val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0



        if (mainAffTableLayout.childCount>1) {
            for (i in mainAffTableLayout.childCount - 1 downTo 1) {
                mainAffTableLayout.removeViewAt(i)
            }
        }

        for (i in 0 until mainViewLinearId.childCount) {
            val child = mainViewLinearId.getChildAt(i)
            child.isEnabled = true
        }

        var childViewCount = mainAffTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= mainAffTableLayout.getChildAt(i) as TableRow;

            for (j in 0..row.getChildCount()-1) {

                var tv : TextView= row.getChildAt(j) as TextView
                tv.isEnabled=true
            }

        }



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

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 1F
        rowLayoutParam5.column = 5


        FacilityDataModel.getInstance().tblAffiliations.apply {
            (0 until size).forEach {
                if (get(it).AffiliationTypeID>0) {

                    val tableRow = TableRow(context)

                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = ""

                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView1.text = get(it).LoggedIntoPortal
                    textView1.text = if (get(it).AffiliationTypeDetailID == 0) "" else TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == get(it).AffiliationTypeDetailID }[0].AffiliationDetailTypeName
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView2.text = get(it).NumberUnacknowledgedTows
                    textView2.text = get(it).effDate.apiToAppFormat()
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView3.text = get(it).InProgressTows
                    textView3.text = get(it).expDate.apiToAppFormat()
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//                textView4.text = get(it).InProgressWalkIns
                    textView4.text = get(it).comment
                    tableRow.addView(textView4)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam5
                    updateButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
                    updateButton.text = "update"
                    tableRow.addView(updateButton)


                    updateButton.setOnClickListener(View.OnClickListener {


                        edit_afDtlseffective_date_textviewVal.setText(textView2.text)
                        edit_afDtlsexpiration_date_textviewVal.setText(textView3.text)
                        edit_affcomments_editTextVal.setText(textView4.text)
                        edit_affiliations_textviewVal.setSelection(0)
                        edit_afDetails_textviewVal.setSelection(0)




                        rowIndex = mainAffTableLayout.indexOfChild(tableRow)



                        edit_afDtlseffective_date_textviewVal.setError(null)

                        edit_affiliationsCard.visibility = View.VISIBLE
                        alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE




                        for (i in 0 until mainViewLinearId.childCount) {
                            val child = mainViewLinearId.getChildAt(i)
                            child.isEnabled = false
                        }

                        var childViewCount = mainAffTableLayout.getChildCount();

                        for (i in 1..childViewCount - 1) {
                            var row: TableRow = mainAffTableLayout.getChildAt(i) as TableRow;

                            for (j in 0..row.getChildCount() - 1) {

                                var tv: TextView = row.getChildAt(j) as TextView
                                tv.isEnabled = false

                            }

                        }


                    })
                    edit_submitNewAffil.setOnClickListener {

                        if (validateInputsForUpdate()) {

                            var startDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString()
                            var endDate = if (edit_afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlsexpiration_date_textviewVal.text.toString()
                            var comment = edit_affcomments_editTextVal.text.toString()
//
//                        var affType = edit_affiliations_textviewVal.selectedItem.toString()
//                        var affDetail= edit_afDetails_textviewVal.selectedItem.toString()


                            indexToRemove = rowIndex



                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + "&affiliationId=4931&affiliationTypeId=19&affiliationTypeDetailsId=63&effDate=1900-01-01T00:00:00&expDate=2013-11-13T00:00:00&comment=per%2011/13/13%20visitation&active=1&insertBy=sa&insertDate=2014-07-23T22:15:44.150&updateBy=SumA&updateDate=2014-07-23T22:15:44.150",
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread(Runnable {
                                            Log.v("RESPONSE", response.toString())
                                            affLoadingView.visibility = View.GONE
//
//                                        FacilityDataModel.getInstance().tblAffiliations[indexToRemove-1].startDate = edit_startDateButton.text.toString()
//                                        FacilityDataModel.getInstance().tblAffiliations[indexToRemove-1].PortalInspectionDate = "" + date
//                                        FacilityDataModel.getInstance().tblAffiliations[indexToRemove-1].LoggedIntoPortal = "" + isLoggedInRsp
//                                        FacilityDataModel.getInstance().tblAffiliations[indexToRemove-1].InProgressTows = "" + numberOfInProgressTwoInsvalue
//                                        FacilityDataModel.getInstance().tblAffiliations[indexToRemove-1].InProgressWalkIns = "" + numberOfInProgressWalkInsValue
//

                                            edit_affiliationsCard.visibility = View.GONE
                                            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE

                                            fillAffTableView()
                                            altLocationTableRow(2)

                                        })
                                    }, Response.ErrorListener {
                                Log.v("error while loading", "error while loading personnal record")
                                affLoadingView.visibility = View.GONE

                            }))

                        } else
                            Toast.makeText(context, "please fill all required field", Toast.LENGTH_SHORT).show()


                    }

                    mainAffTableLayout.addView(tableRow)
                    // Toast.makeText(context,indexToRemove.toString(),Toast.LENGTH_SHORT).show()

                }
            }
        }

    }


//    fun fillAffTableView() {
//
//        if (mainAffTableLayout.childCount>1) {
//            for (i in mainAffTableLayout.childCount - 1 downTo 1) {
//                mainAffTableLayout.removeViewAt(i)
//            }
//        }
//
//        val rowLayoutParam = TableRow.LayoutParams()
//        rowLayoutParam.weight = 1F
//        rowLayoutParam.column = 0
//        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT
//
//        val rowLayoutParam1 = TableRow.LayoutParams()
//        rowLayoutParam1.weight = 1F
//        rowLayoutParam1.column = 1
//        rowLayoutParam1.height = TableLayout.LayoutParams.WRAP_CONTENT
//
//        val rowLayoutParam2 = TableRow.LayoutParams()
//        rowLayoutParam2.weight = 1F
//        rowLayoutParam2.column = 2
//        rowLayoutParam2.height = TableLayout.LayoutParams.WRAP_CONTENT
//
//        val rowLayoutParam3 = TableRow.LayoutParams()
//        rowLayoutParam3.weight = 1F
//        rowLayoutParam3.column = 3
//        rowLayoutParam3.height = TableLayout.LayoutParams.WRAP_CONTENT
//
//        val rowLayoutParam4 = TableRow.LayoutParams()
//        rowLayoutParam4.weight = 1F
//        rowLayoutParam4.column = 4
//        rowLayoutParam4.height = TableLayout.LayoutParams.WRAP_CONTENT
//
//   val rowLayoutParam5 = TableRow.LayoutParams()
//        rowLayoutParam5.weight = 1F
//        rowLayoutParam5.column = 5
//        rowLayoutParam5.height = TableLayout.LayoutParams.WRAP_CONTENT
//
////        FacilityDataModel.getInstance().tbl.apply {
////            (0 until size).forEach {
//        for (i in 1..2) {
//
//            var tableRow = TableRow(context)
//            if (i % 2 == 0) {
//                tableRow.setBackgroundResource(R.drawable.alt_row_color)
//            }
//            var textView = TextView(context)
//            textView.layoutParams = rowLayoutParam
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            textView.text = "Test" // getLocationTypeName(get(it).LocationTypeID)
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam1
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            textView.text = "Test" // get(it).FAC_Addr1
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam2
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            TableRow.LayoutParams()
//            textView.text = "Test" // get(it).FAC_Addr2
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam3
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            textView.text = "Test" // get(it).CITY
//
//            tableRow.addView(textView)
//
//            textView = TextView(context)
//            textView.layoutParams = rowLayoutParam4
//            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
//            textView.text = "Test" // get(it).County
//            tableRow.addView(textView)
//
//            val updateButton = Button(context)
//            updateButton.layoutParams = rowLayoutParam5
//            updateButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
//            updateButton.text = "update"
//            tableRow.addView(updateButton)
//
//
//            mainAffTableLayout.addView(tableRow)
//        }
////        altVenRevTableRow(2)
////            }
////        }
//
//    }


//    fun BuildAffiliationsList() {
//        val inflater = activity!!
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val parentLayout = affListLL
//        parentLayout.removeAllViews()
//        for (fac in facilityAffList) {
//            val vAffRow = inflater.inflate(R.layout.custom_aff_list_item, parentLayout, false)
//            val affId= vAffRow.findViewById(R.id.affItemId) as TextView
//            val affTypeName= vAffRow.findViewById(R.id.affItemtypeName) as TextView
//            val affTypeDetailName= vAffRow.findViewById(R.id.affItemtypeDetailName) as TextView
//            val affEffDate= vAffRow.findViewById(R.id.affItemEffDate) as TextView
//            val affExpDate= vAffRow.findViewById(R.id.affItemExpDate) as TextView
//            affId.text = fac.affiliationid.toString()
//            affTypeName.text = fac.typename
//            affTypeDetailName.text = fac.typedetailname
//            affEffDate.text = if (fac.effdate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.effdate)) else fac.effdate
//            affExpDate.text = if (fac.expdate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.expdate)) else fac.expdate
//            vAffRow.setOnClickListener({
//                affiliations_textviewVal.setSelection(affTypesArray.indexOf(fac.typename))
////                afDetails_textviewVal.setSelection(affTypesDetailsArray.indexOf(fac.typedetailname))
//                selectedTypeDetailName=fac.typedetailname
//                afDtlseffective_date_textviewVal.text = if (fac.effdate.isNullOrEmpty() || fac.effdate.equals("NULL") || fac.effdate.equals("") || fac.effdate.toLowerCase().equals("no date provided")) "No Date Provided" else  {
//                    if (fac.effdate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.effdate)) else fac.effdate
//                }
//                afDtlsexpiration_date_textviewVal.text = if (fac.expdate.isNullOrEmpty() || fac.expdate.equals("NULL") || fac.expdate.equals("") || fac.expdate.toLowerCase().equals("no date provided")) "No Date Provided" else   {
//                    if (fac.expdate.length>11 ) Constants.appFormat.format(Constants.dbFormat.parse(fac.expdate)) else fac.expdate
//                }
//                affcomments_editTextVal.setText(fac.comments)
//            })
//            parentLayout.addView(vAffRow)
//        }
//    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        afDtlseffective_date_textviewVal.setError(null)

        if(afDtlseffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            afDtlseffective_date_textviewVal.setError("Required Field")
        }


        return isInputsValid
    }
    fun validateInputsForUpdate() : Boolean {
        var isInputsValid = true

        edit_afDtlseffective_date_textviewVal.setError(null)

        if(edit_afDtlseffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            edit_afDtlseffective_date_textviewVal.setError("Required Field")
        }


        return isInputsValid
    }
    fun scopeOfServiceChangesWatcher() {
        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {

            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {


                if (FragmentARRAVScopeOfService.dataChanged) {

                    val builder = AlertDialog.Builder(context)

                    // Set the alert dialog title
                    builder.setTitle("Changes made confirmation")

                    // Display a message on alert dialog
                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("YES") { dialog, which ->

                        affLoadingView.visibility = View.VISIBLE



                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                                Response.Listener { response ->
                                    activity!!.runOnUiThread(Runnable {
                                        Log.v("RESPONSE", response.toString())
                                        affLoadingView.visibility = View.GONE

                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                                FragmentARRAVScopeOfService.dataChanged = false

                                            }

                                        }

                                    })
                                }, Response.ErrorListener {
                            Log.v("error while loading", "error while loading personnal record")
                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()

                            affLoadingView.visibility = View.GONE

                        }))


                    }


                    // Display a negative button on alert dialog
                    builder.setNegativeButton("No") { dialog, which ->
                        FragmentARRAVScopeOfService.dataChanged = false
                        affLoadingView.visibility = View.GONE

                    }


                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    // Display the alert dialog on app interface
                    dialog.show()

                }

            } else {


                val builder = AlertDialog.Builder(context)

                // Set the alert dialog title
                builder.setTitle("Changes made Warning")

                // Display a message on alert dialog
                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Ok") { dialog, which ->

                    FragmentARRAVScopeOfService.dataChanged = false

                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true

                }


                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }

        }
    }


        fun altLocationTableRow(alt_row: Int) {
            var childViewCount = mainAffTableLayout.getChildCount();

            for (i in 1..childViewCount - 1) {
                var row: TableRow = mainAffTableLayout.getChildAt(i) as TableRow;

                if (i % alt_row != 0) {
                    row.setBackground(getResources().getDrawable(
                            R.drawable.alt_row_color));
                } else {
                    row.setBackground(getResources().getDrawable(
                            R.drawable.row_color));
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
         * @return A new instance of fragment FacilityGeneralInformationFragment.
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
