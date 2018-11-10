package com.inspection.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateAffiliationsData
import com.inspection.model.*
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
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
    private var affTypesList = ArrayList<TypeTablesModel.affiliationType>()
    private var edit_affTypesArray = ArrayList<String>()
    private var edit_affTypesDetailsList = ArrayList<TypeTablesModel.affiliationDetailType>()
    private var edit_affTypesDetailsArray = ArrayList<String>()
    private var edit_affTypesList = ArrayList<TypeTablesModel.affiliationType>()
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
//        scopeOfServiceChangesWatcher()
        edit_afDetails_textviewVal.tag = "0"
//        var affiliationsArray = arrayOf("ACDelco", "AutoValue", "AutoZone", "Bosch", "Carquest", "DescRepairAffil", "Federated", "Gas Brand", "Mechanical Repair", "NAPA", "Oil", "OtherRepairAffil", "Parts", "PartsPlus", "ProntoVIP", "Quick Lube", "Tire", "Transmission", "WorldPac")
//        var affiliationsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affiliationsArray)
//        affiliationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        affiliations_textviewVal.adapter = affiliationsAdapter

        affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                affTypesList = TypeTablesModel.getInstance().AARAffiliationType
                var selectedTypeID = affTypesList.filter { s->s.AffiliationTypeName.equals(affiliations_textviewVal.selectedItem.toString())}[0].AARAffiliationTypeID
                affTypesDetailsArray.clear()
                for (fac in affTypesDetailsList.filter { s->s.AARAffiliationTypeID.equals(selectedTypeID) }) {
                    affTypesDetailsArray.add(fac.AffiliationDetailTypeName)
                }
                var afTypeDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray)
                afTypeDetailsAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                afDetails_textviewVal.adapter = afTypeDetailsAdapter
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        edit_affiliations_textviewVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                edit_affTypesList = TypeTablesModel.getInstance().AARAffiliationType
                var selectedTypeID = affTypesList.filter { s->s.AffiliationTypeName.equals(edit_affiliations_textviewVal.selectedItem.toString())}[0].AARAffiliationTypeID
                edit_affTypesDetailsArray.clear()
                for (fac in edit_affTypesDetailsList.filter { s->s.AARAffiliationTypeID.equals(selectedTypeID) }) {
                    edit_affTypesDetailsArray.add(fac.AffiliationDetailTypeName)
                }
                var edit_afTypeDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, edit_affTypesDetailsArray)
                edit_afTypeDetailsAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                edit_afDetails_textviewVal.adapter = edit_afTypeDetailsAdapter
                if (!edit_afDetails_textviewVal.tag.equals("0")){
                    edit_afDetails_textviewVal.setSelection(edit_affTypesDetailsArray.indexOf(edit_afDetails_textviewVal.tag.toString()))
                    edit_afDetails_textviewVal.tag = "0"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

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
//        addNewAffil.isEnabled = false
        addNewAffil.setOnClickListener {
            affiliations_textviewVal.setSelection(0)
            afDetails_textviewVal.setSelection(0)
            afDtlseffective_date_textviewVal.text="SELECT DATE"
            afDtlsexpiration_date_textviewVal.text = "SELECT DATE"
            affcomments_editTextVal.setText("")
            affiliationsCard.visibility=View.VISIBLE
            alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE
        }

        fillAffTableView()

        afDtlseffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyyy" // mention the format you need
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
                val myFormat = "MM/dd/yyy" // mention the format you need
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
                val myFormat = "MM/dd/yyy" // mention the format you need
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
                val myFormat = "MM/dd/yyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                edit_afDtlsexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        submitNewAffil.setOnClickListener {
            if (validateInputs()) {
                progressBarText.text = "Saving ..."
                affLoadingView.visibility = View.VISIBLE
                var affiliationItem = TblAffiliations()
                affiliationItem.effDate = if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                affiliationItem.expDate = if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlsexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                affiliationItem.comment = affcomments_editTextVal.text.toString()
                affiliationItem.AffiliationTypeDetailID = TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID.toInt()
                affiliationItem.AffiliationTypeID= TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AffiliationTypeName.equals(affiliations_textviewVal.selectedItem.toString()) }[0].AARAffiliationTypeID.toInt()
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&affiliationId=&affiliationTypeId=${affiliationItem.AffiliationTypeID}&affiliationTypeDetailsId=${affiliationItem.AffiliationTypeDetailID}&effDate=${affiliationItem.effDate}&expDate=${affiliationItem.expDate}&comment=${affiliationItem.comment}&active=1&insertBy=sa&insertDate="+Date().toApiSubmitFormat()+"&updateBy=SumA&updateDate="+Date().toApiSubmitFormat(),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode&gt;0&",false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Affiliation")
                                    affiliationItem.AffiliationID = response.toString().substring(response.toString().indexOf(";AffiliationID")+18,response.toString().indexOf("&lt;/AffiliationID")).toInt()
                                    FacilityDataModel.getInstance().tblAffiliations.add(affiliationItem)
                                    fillAffTableView()
                                    altLocationTableRow(2)
                                    HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
                                    HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
                                    IndicatorsDataModel.getInstance().validateSOSAffiliations()
                                    if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].Affiliations) (activity as FormsActivity).AffiliationsButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).AffiliationsButton.setTextColor(Color.parseColor("#A42600"))
                                    (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                    Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
                                }
                                affLoadingView.visibility = View.GONE
                                progressBarText.text = "Loading ..."
                                affiliationsCard.visibility = View.GONE
                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
                    affLoadingView.visibility = View.GONE
                    affiliationsCard.visibility = View.GONE
                    alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                }))
            }else {
                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
            }
        }
        prepareAffiliations()
    }


    fun prepareAffiliations () {

        // SAEED Need to implement dependency between Type & Type Detail

        affTypesDetailsList = TypeTablesModel.getInstance().AffiliationDetailType
        affTypesDetailsArray.clear()
        for (fac in affTypesDetailsList ) {
            affTypesDetailsArray.add(fac.AffiliationDetailTypeName)
        }

        edit_affTypesDetailsList = TypeTablesModel.getInstance().AffiliationDetailType
        edit_affTypesDetailsArray.clear()
        for (fac in edit_affTypesDetailsList ) {
            edit_affTypesDetailsArray.add(fac.AffiliationDetailTypeName)
        }

        var afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray);
        afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        afDetails_textviewVal.adapter = afDetailsAdapter

        var edit_afDetailsAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesDetailsArray);
        edit_afDetailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_afDetails_textviewVal.adapter = edit_afDetailsAdapter

        affTypesList = TypeTablesModel.getInstance().AARAffiliationType
        affTypesArray.clear()
        for (fac in affTypesList ) {
            affTypesArray.add(fac.AffiliationTypeName)
        }

        edit_affTypesList = TypeTablesModel.getInstance().AARAffiliationType
        edit_affTypesArray.clear()
        for (fac in edit_affTypesList ) {
            edit_affTypesArray.add(fac.AffiliationTypeName)
        }

        var afTypeAdapter= ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, affTypesArray);
        afTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        affiliations_textviewVal.adapter = afTypeAdapter

        var edit_afTypeAdapter= ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, edit_affTypesArray);
        edit_afTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        edit_affiliations_textviewVal.adapter = edit_afTypeAdapter

    }


    fun fillAffTableView(){

        mainViewLinearId.isEnabled=true

        if (mainAffTableLayout.childCount>1) {
            for (i in mainAffTableLayout.childCount - 1 downTo 1) {
                mainAffTableLayout.removeViewAt(i)
            }
        }

//        for (i in 0 until mainViewLinearId.childCount) {
//            val child = mainViewLinearId.getChildAt(i)
//            child.isEnabled = true
//        }
//
//        var childViewCount = mainAffTableLayout.getChildCount();
//
//        for ( i in 1..childViewCount-1) {
//            var row : TableRow= mainAffTableLayout.getChildAt(i) as TableRow;
//
//            for (j in 0..row.getChildCount()-1) {
//
//                var tv : TextView= row.getChildAt(j) as TextView
//                tv.isEnabled=true
//            }
//
//        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.5F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.8F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 0.8F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 2F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.6F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblAffiliations.apply {
            (0 until size).forEach {
                if (get(it).AffiliationTypeID>0) {

                    val tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }

                    val textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    textView.text = if (get(it).AffiliationTypeID == 0) "" else TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.toInt() == get(it).AffiliationTypeID}[0].AffiliationTypeName

                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 18f
                    textView1.minimumHeight = 30
//                textView1.text = get(it).LoggedIntoPortal
                    textView1.text = if (get(it).AffiliationTypeDetailID == 0) "" else TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == get(it).AffiliationTypeDetailID }[0].AffiliationDetailTypeName
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 18f
                    textView2.minimumHeight = 30
                    textView2.text = if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER_VERTICAL
                    textView3.textSize = 18f
                    textView3.minimumHeight = 30
                    textView3.text = if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER_VERTICAL
                    textView4.textSize = 18f
                    textView4.minimumHeight = 30
                    textView4.text = get(it).comment
                    tableRow.addView(textView4)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam5
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 18f
                    updateButton.minimumHeight = 30
                    updateButton.isEnabled=true
                    updateButton.gravity = Gravity.CENTER
                    updateButton.setBackgroundColor(Color.TRANSPARENT)
                    tableRow.addView(updateButton)


                    updateButton.setOnClickListener {


                        rowIndex = mainAffTableLayout.indexOfChild(tableRow)
                        edit_afDtlseffective_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.equals("") || FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else  FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate.apiToAppFormatMMDDYYYY())
                        edit_afDtlsexpiration_date_textviewVal.setText(if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.equals("") || FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "SELECT DATE" else  FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate.apiToAppFormatMMDDYYYY())
                        edit_affcomments_editTextVal.setText(FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment)
                        edit_affiliations_textviewVal.setSelection(edit_affTypesArray.indexOf(textView.text.toString()))
                        edit_afDetails_textviewVal.setSelection(edit_affTypesDetailsArray.indexOf(textView1.text.toString()))
                        edit_afDetails_textviewVal.tag=textView1.text.toString()
                        edit_afDtlseffective_date_textviewVal.setError(null)
                        edit_affiliationsCard.visibility = View.VISIBLE
                        alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE

//                        for (i in 0 until mainViewLinearId.childCount) {
//                            val child = mainViewLinearId.getChildAt(i)
//                            child.isEnabled = false
//                        }

                        var childViewCount = mainAffTableLayout.getChildCount();

//                        for (i in 1..childViewCount - 1) {
//                            var row: TableRow = mainAffTableLayout.getChildAt(i) as TableRow;
//
//                            for (j in 0..row.getChildCount() - 1) {
//
//                                var tv: TextView = row.getChildAt(j) as TextView
//                                tv.isEnabled = false
//
//                            }
//
//                        }


                    }
                    edit_submitNewAffil.setOnClickListener {

                        if (validateInputsForUpdate()) {
                            progressBarText.text = "Saving ..."
                            affLoadingView.visibility = View.VISIBLE
                            var startDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var endDate = if (edit_afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlsexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
                            var comment = edit_affcomments_editTextVal.text.toString()
//
                            var affTypeID = TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AffiliationTypeName.equals(edit_affiliations_textviewVal.selectedItem.toString()) }[0].AARAffiliationTypeID
                            var affDetailID = TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(edit_afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID
                            var affiliationID = if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID>-1) FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID else ""
                            indexToRemove = rowIndex

                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=sa&insertDate=2014-07-23T22:15:44.150&updateBy=SumA&updateDate=2014-07-23T22:15:44.150",
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread {
                                            if (response.toString().contains("returnCode&gt;0&",false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Affiliation")
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment= comment
                                                affLoadingView.visibility = View.GONE
                                                progressBarText.text = "Loading ..."
                                                fillAffTableView()
                                                altLocationTableRow(2)
                                                HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
                                                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
                                                IndicatorsDataModel.getInstance().validateSOSAffiliations()
                                                if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].Affiliations) (activity as FormsActivity).AffiliationsButton.setTextColor(Color.parseColor("#26C3AA")) else (activity as FormsActivity).AffiliationsButton.setTextColor(Color.parseColor("#A42600"))
                                            } else {
                                                var errorMessage = response.toString().substring(response.toString().indexOf(";message")+12,response.toString().indexOf("&lt;/message"))
                                                Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
                                            }
                                            affLoadingView.visibility = View.GONE
                                            edit_affiliationsCard.visibility = View.GONE
                                            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                                        }
                                    }, Response.ErrorListener {
                                Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
                                affLoadingView.visibility = View.GONE
                                edit_affiliationsCard.visibility = View.GONE
                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                            }))
                        } else
                            Utility.showValidationAlertDialog(activity,"Please fill all the required activity")
                    }
                    mainAffTableLayout.addView(tableRow)
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

        if (afDetails_textviewVal.selectedItem==null) {
            isInputsValid=false
            afDetails_textview.setError("Required Field")
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

        if (edit_afDetails_textviewVal.selectedItem==null) {
            isInputsValid=false
            edit_afDetails_textview.setError("Required Field")
        }

        return isInputsValid
    }
    fun scopeOfServiceChangesWatcher() {
//        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {
//
//            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {
//
//
//                if (FragmentARRAVScopeOfService.dataChanged) {
//
//                    val builder = AlertDialog.Builder(context)
//
//                    // Set the alert dialog title
//                    builder.setTitle("Changes made confirmation")
//
//                    // Display a message on alert dialog
//                    builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")
//
//                    // Set a positive button and its click listener on alert dialog
//                    builder.setPositiveButton("YES") { dialog, which ->
//
//                        affLoadingView.visibility = View.VISIBLE
//
//
//
//                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode="+FacilityDataModel.getInstance().clubCode+"&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread(Runnable {
//                                        Log.v("RESPONSE", response.toString())
//                                        affLoadingView.visibility = View.GONE
//
//                                        Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
//                                        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
//                                            FacilityDataModel.getInstance().tblScopeofService[0].apply {
//
//                                                LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
//                                                LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank()) LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
//                                                FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank()) FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
//                                                DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank()) DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
//                                                NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank()) NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
//                                                NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank()) NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_
//
//                                                FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare
//
//                                                FragmentARRAVScopeOfService.dataChanged = false
//
//                                            }
//
//                                        }
//
//                                    })
//                                }, Response.ErrorListener {
//                            Log.v("error while loading", "error while loading personnal record")
//                            Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
//
//                            affLoadingView.visibility = View.GONE
//
//                        }))
//
//
//                    }
//
//
//                    // Display a negative button on alert dialog
//                    builder.setNegativeButton("No") { dialog, which ->
//                        FragmentARRAVScopeOfService.dataChanged = false
//                        affLoadingView.visibility = View.GONE
//
//                    }
//
//
//                    // Finally, make the alert dialog using builder
//                    val dialog: AlertDialog = builder.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    // Display the alert dialog on app interface
//                    dialog.show()
//
//                }
//
//            } else {
//
//
//                val builder = AlertDialog.Builder(context)
//
//                // Set the alert dialog title
//                builder.setTitle("Changes made Warning")
//
//                // Display a message on alert dialog
//                builder.setMessage("We can't save Data changed in General Information Scope Of Service Page, due to blank required fields found")
//
//                // Set a positive button and its click listener on alert dialog
//                builder.setPositiveButton("Ok") { dialog, which ->
//
//                    FragmentARRAVScopeOfService.dataChanged = false
//
//                    FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments = true
//
//                }
//
//
//                val dialog: AlertDialog = builder.create()
//                dialog.setCanceledOnTouchOutside(false)
//                dialog.show()
//
//            }
//
//        }
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
