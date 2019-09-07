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

        IndicatorsDataModel.getInstance().tblScopeOfServices[0].AffiliationsVisited = true
        (activity as FormsActivity).AffiliationsButton.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        edit_afDetails_textviewVal.tag = "0"

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

        exitAffDialogeBtnId.setOnClickListener {
            affiliationsCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
        }

        edit_exitAffDialogeBtnId.setOnClickListener {
            fillAffTableView()
            altLocationTableRow(2)
            edit_affiliationsCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
        }
//        addNewAffil.isEnabled = false
        addNewAffil.setOnClickListener {
            affiliations_textviewVal.setSelection(0)
            afDetails_textviewVal.setSelection(0)
            afDtlseffective_date_textviewVal.text="SELECT DATE"
            afDtlsexpiration_date_textviewVal.text = "SELECT DATE"
            affcomments_editTextVal.setText("")
            affiliationsCard.visibility=View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
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

                if (afDetails_textviewVal.selectedItem != null){
                    affiliationItem.AffiliationTypeDetailID = TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID.toInt()
                } else {
                    affiliationItem.AffiliationTypeDetailID = 0
                }

                affiliationItem.AffiliationTypeID= TypeTablesModel.getInstance().AARAffiliationType.filter { s->s.AffiliationTypeName.equals(affiliations_textviewVal.selectedItem.toString()) }[0].AARAffiliationTypeID.toInt()
                Log.v("Affiliations ADD --- ",UpdateAffiliationsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&affiliationId=&affiliationTypeId=${affiliationItem.AffiliationTypeID}&affiliationTypeDetailsId=${affiliationItem.AffiliationTypeDetailID}&effDate=${affiliationItem.effDate}&expDate=${affiliationItem.expDate}&comment=${affiliationItem.comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat())
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&affiliationId=&affiliationTypeId=${affiliationItem.AffiliationTypeID}&affiliationTypeDetailsId=${affiliationItem.AffiliationTypeDetailID}&effDate=${affiliationItem.effDate}&expDate=${affiliationItem.expDate}&comment=${affiliationItem.comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat() + Utility.getLoggingParameters(activity, 0, getAffiliationChanges(0,0)),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<",false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Affiliation")
                                    affiliationItem.AffiliationID = response.toString().substring(response.toString().indexOf("<AffiliationID")+15,response.toString().indexOf("</AffiliationID")).toInt()
                                    FacilityDataModel.getInstance().tblAffiliations.add(affiliationItem)
                                    FacilityDataModelOrg.getInstance().tblAffiliations.add(affiliationItem)
                                    fillAffTableView()
                                    altLocationTableRow(2)
                                    HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
                                    HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
                                }
                                affLoadingView.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                progressBarText.text = "Loading ..."
                                affiliationsCard.visibility = View.GONE
                                alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
                    affLoadingView.visibility = View.GONE
                    (activity as FormsActivity).overrideBackButton = false
                    affiliationsCard.visibility = View.GONE
                    alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                }))
            }else {
                Utility.showValidationAlertDialog(activity, "Please fill all required fields")
            }
        }
        prepareAffiliations()
    }

    fun getAffiliationChanges(action : Int, rowId: Int) : String { // 0: Add 1: Edit
        var strChanges = ""
        if (action==0) {
            strChanges = "Affiliations added with "
            strChanges += "Type (" + affiliations_textviewVal.getSelectedItem().toString()+ ") - "
            if (afDetails_textviewVal.selectedItem != null) {
                strChanges += "Type Detail (" + afDetails_textviewVal.getSelectedItem().toString() + ") - "
            }
            strChanges += "Effective Date (" + if (afDtlsexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlsexpiration_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY() + ") - "
            strChanges += "Expiration Date (" + if (afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY() + ") - "
            strChanges += "Comments (" + affcomments_editTextVal.text.toString() + ")"
        }
        if (action==1) {
            val Comments = edit_affcomments_editTextVal.text.toString()
            val effDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
            val expDate = if (edit_afDtlseffective_date_textviewVal.text.equals("SELECT DATE")) "" else edit_afDtlseffective_date_textviewVal.text.toString().appToApiSubmitFormatMMDDYYYY()
            val afType = edit_affiliations_textviewVal.selectedItem.toString()
            val afTypeDetail = if (edit_afDetails_textviewVal.selectedItem == null) "" else edit_afDetails_textviewVal.selectedItem.toString()
            if (Comments != FacilityDataModelOrg.getInstance().tblAffiliations[rowId].comment) {
                strChanges += "Comments changed from (" + FacilityDataModelOrg.getInstance().tblAffiliations[rowId].comment+ ") to (${Comments}) - "
            }
            if (effDate != FacilityDataModelOrg.getInstance().tblAffiliations[rowId].effDate.apiToAppFormatMMDDYYYY()) {
                strChanges += "Effective Date changed from (" + FacilityDataModelOrg.getInstance().tblAffiliations[rowId].effDate.apiToAppFormatMMDDYYYY() + ") to (" + effDate + ") - "
            }
            if (expDate != FacilityDataModelOrg.getInstance().tblAffiliations[rowId].expDate.apiToAppFormatMMDDYYYY()) {
                strChanges += "Expiration Date changed from (" + FacilityDataModelOrg.getInstance().tblAffiliations[rowId].expDate.apiToAppFormatMMDDYYYY() + ") to (" + expDate + ") - "
            }

            if (afType != (TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.toInt() == FacilityDataModelOrg.getInstance().tblAffiliations[rowId].AffiliationTypeID }[0].AffiliationTypeName)) {
                strChanges += "Affiliation Type changed from (" + TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.toInt() == FacilityDataModelOrg.getInstance().tblAffiliations[rowId].AffiliationTypeID }[0].AffiliationTypeName + ") to (" + afType + ") - "
            }

            if (afTypeDetail.isNotEmpty()) {
                if (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == FacilityDataModelOrg.getInstance().tblAffiliations[rowId].AffiliationTypeDetailID}.isNotEmpty()) {
                    if (afTypeDetail != (TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == FacilityDataModelOrg.getInstance().tblAffiliations[rowId].AffiliationTypeDetailID }[0].AffiliationDetailTypeName)) {
                        strChanges += "Affiliation Type Detail changed from (" + TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == FacilityDataModelOrg.getInstance().tblAffiliations[rowId].AffiliationTypeDetailID }[0].AffiliationDetailTypeName + ") to (" + afTypeDetail + ") - "
                    }
                } else {
                    strChanges += "Affiliation Type Detail changed from ( ) to (" + afTypeDetail + ") - "
                }
            }
        }
        strChanges = strChanges.removeSuffix(" - ")
        return strChanges
    }

    fun prepareAffiliations () {

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
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1.5F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin = 10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.gravity = Gravity.CENTER_VERTICAL
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1.5F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0
        rowLayoutParam1.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.8F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0
        rowLayoutParam2.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 0.8F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0
        rowLayoutParam3.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 2F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0
        rowLayoutParam4.gravity = Gravity.CENTER_VERTICAL

        val rowLayoutParam5 = TableRow.LayoutParams()
        rowLayoutParam5.weight = 0.6F
        rowLayoutParam5.column = 5
        rowLayoutParam5.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam5.width = 0
        rowLayoutParam5.gravity = Gravity.CENTER_VERTICAL

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
                    textView.textSize = 14f
                    textView.minimumHeight = 30
                    textView.text = if (get(it).AffiliationTypeID == 0) "" else TypeTablesModel.getInstance().AARAffiliationType.filter { s -> s.AARAffiliationTypeID.toInt() == get(it).AffiliationTypeID}[0].AffiliationTypeName

                    tableRow.addView(textView)

                    val textView1 = TextView(context)
                    textView1.layoutParams = rowLayoutParam1
                    textView1.gravity = Gravity.CENTER_VERTICAL
                    textView1.textSize = 14f
                    textView1.minimumHeight = 30
//                textView1.text = get(it).LoggedIntoPortal
                    textView1.text = if (get(it).AffiliationTypeDetailID == 0) "" else TypeTablesModel.getInstance().AffiliationDetailType.filter { s -> s.AffiliationTypeDetailID.toInt() == get(it).AffiliationTypeDetailID }[0].AffiliationDetailTypeName
                    tableRow.addView(textView1)

                    val textView2 = TextView(context)
                    textView2.layoutParams = rowLayoutParam2
                    textView2.gravity = Gravity.CENTER_VERTICAL
                    textView2.textSize = 14f
                    textView2.minimumHeight = 30
                    textView2.text = if (get(it).effDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).effDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView2)

                    val textView3 = TextView(context)
                    textView3.layoutParams = rowLayoutParam3
                    textView3.gravity = Gravity.CENTER_VERTICAL
                    textView3.textSize = 14f
                    textView3.minimumHeight = 30
                    textView3.text = if (get(it).expDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).expDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView3)

                    val textView4 = TextView(context)
                    textView4.layoutParams = rowLayoutParam4
                    textView4.gravity = Gravity.CENTER_VERTICAL
                    textView4.textSize = 14f
                    textView4.minimumHeight = 30
                    textView4.text = get(it).comment
                    tableRow.addView(textView4)

                    val updateButton = Button(context)
                    updateButton.layoutParams = rowLayoutParam5
                    updateButton.setTextColor(Color.BLUE)
                    updateButton.text = "EDIT"
                    updateButton.textSize = 14f
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
                        if (textView1.text.isNotEmpty()) {
                            edit_afDetails_textviewVal.setSelection(edit_affTypesDetailsArray.indexOf(textView1.text.toString()))
                        }
                        edit_afDetails_textviewVal.tag=textView1.text.toString()
                        edit_afDtlseffective_date_textviewVal.setError(null)
                        edit_affiliationsCard.visibility = View.VISIBLE
                        (activity as FormsActivity).overrideBackButton = true
                        alphaBackgroundForAffilliationsDialogs.visibility = View.VISIBLE

                        var childViewCount = mainAffTableLayout.getChildCount();

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
                            var affDetailID = if (edit_afDetails_textviewVal.selectedItem != null) TypeTablesModel.getInstance().AffiliationDetailType.filter { s->s.AffiliationDetailTypeName.equals(edit_afDetails_textviewVal.selectedItem.toString()) }[0].AffiliationTypeDetailID else "0"
                            var affiliationID = if (FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID>-1) FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationID else ""
                            indexToRemove = rowIndex
                            Log.v("AFFILIATION EDIT --- ",UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}")
                            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateAffiliationsData + "${FacilityDataModel.getInstance().tblFacilities[0].FACNo}&clubCode=${FacilityDataModel.getInstance().clubCode}&affiliationId=${affiliationID}&affiliationTypeId=${affTypeID}&affiliationTypeDetailsId=${affDetailID}&effDate=${startDate}&expDate=${endDate}&comment=${comment}&active=1&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate=${Date().toApiSubmitFormat()}&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate=${Date().toApiSubmitFormat()}" + Utility.getLoggingParameters(activity, 1, getAffiliationChanges(1,rowIndex-1)),
                                    Response.Listener { response ->
                                        activity!!.runOnUiThread {
                                            if (response.toString().contains("returnCode>0<",false)) {
                                                Utility.showSubmitAlertDialog(activity, true, "Affiliation")
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
                                                FacilityDataModel.getInstance().tblAffiliations[rowIndex-1].comment= comment

                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeID = affTypeID.toInt()
                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].AffiliationTypeDetailID = affDetailID.toInt()
                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].effDate= startDate
                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].expDate= endDate
                                                FacilityDataModelOrg.getInstance().tblAffiliations[rowIndex-1].comment= comment

                                                affLoadingView.visibility = View.GONE
                                                progressBarText.text = "Loading ..."
                                                fillAffTableView()
                                                altLocationTableRow(2)
                                                HasChangedModel.getInstance().groupSoSAffiliations[0].SoSAffiliations= true
                                                HasChangedModel.getInstance().checkIfChangeWasDoneforSoSAffiliations()
                                            } else {
                                                var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                                Utility.showSubmitAlertDialog(activity,false,"Affiliation (Error: "+ errorMessage+" )")
                                            }
                                            affLoadingView.visibility = View.GONE
                                            (activity as FormsActivity).overrideBackButton = false
                                            edit_affiliationsCard.visibility = View.GONE
                                            alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
                                        }
                                    }, Response.ErrorListener {
                                Utility.showSubmitAlertDialog(activity, false, "Affiliation (Error: "+it.message+" )")
                                affLoadingView.visibility = View.GONE
                                (activity as FormsActivity).overrideBackButton = false
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


    fun validateInputs() : Boolean {
        var isInputsValid = true

        afDtlseffective_date_textviewVal.setError(null)

        if(afDtlseffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            afDtlseffective_date_textviewVal.setError("Required Field")
        }

//        if (afDetails_textviewVal.selectedItem==null) {
//            isInputsValid=false
//            afDetails_textview.setError("Required Field")
//        }


        return isInputsValid
    }
    fun validateInputsForUpdate() : Boolean {
        var isInputsValid = true

        edit_afDtlseffective_date_textviewVal.setError(null)

        if(edit_afDtlseffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            edit_afDtlseffective_date_textviewVal.setError("Required Field")
        }

//        if (edit_afDetails_textviewVal.selectedItem==null) {
//            isInputsValid=false
//            edit_afDetails_textview.setError("Required Field")
//        }

        return isInputsValid
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
