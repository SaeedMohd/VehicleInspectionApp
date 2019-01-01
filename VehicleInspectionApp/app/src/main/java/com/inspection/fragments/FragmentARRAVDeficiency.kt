package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
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
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.*
import com.inspection.Utils.Constants.UpdateDeficiencyData
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.dataChanged
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.diagnosticLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.fixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.laborRateMatrixMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.laborRateMatrixMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.numberOfBaysEditText_
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.numberOfLiftsEditText_
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.validationProblemFoundForOtherFragments
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_DiagnosticsRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_FixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfBays
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfLifts
import com.inspection.model.*
import kotlinx.android.synthetic.main.fragment_arrav_deficiency.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVDeficiency.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVDeficiency.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVDeficiency : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    var facilityRepresentativeDeficienciesSignatureBitmap: Bitmap? = null
    enum class requestedSignature {
        representativeDeficiency
    }
    var selectedSignature: requestedSignature? = null


    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_deficiency , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareDefSpinners()
        fillDeffTableView()
        IndicatorsDataModel.getInstance().tblDeffeciencies[0].visited = true
        deffTitle.setTextColor(Color.parseColor("#26C3AA"))
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()

        exitDeffeciencyDialogeBtnId.setOnClickListener {
            defeciencyCard.visibility=View.GONE
            (activity as FormsActivity).overrideBackButton = false
            visitationFormAlphaBackground.visibility = View.GONE
        }

        showNewDeffDialogueBtn.setOnClickListener {
            comments_editTextVal.setText("")
            newVisitationDateBtn.setText("SELECT DATE")
            signatureDateBtn.setText("SELECT DATE")
            facilityRepresentativeDeficienciesSignatureButton.setText("ADD SIGNATURE")
            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)

            newVisitationDateBtn.setError(null)
            signatureDateBtn.setError(null)
            facilityRepresentativeDeficienciesSignatureButton.setError(null)
            defeciencyCard.visibility=View.VISIBLE
            (activity as FormsActivity).overrideBackButton = true
            visitationFormAlphaBackground.visibility = View.VISIBLE


            facilityRepresentativeDeficienciesSignatureButton.setOnClickListener {
                signatureDialog.visibility = View.VISIBLE
                (activity as FormsActivity).overrideBackButton = true
                selectedSignature = requestedSignature.representativeDeficiency
                if (facilityRepresentativeDeficienciesSignatureBitmap!=null){
                    signatureInkView.drawBitmap(facilityRepresentativeDeficienciesSignatureBitmap, 0.0f, 0.0f, Paint())
                }
            }


            signatureClearButton.setOnClickListener {
                signatureInkView.clear()
            }

            signatureCancelButton.setOnClickListener {
                signatureInkView.clear()
                signatureDialog.visibility = View.GONE
                (activity as FormsActivity).overrideBackButton = false
            }

            signatureConfirmButton.setOnClickListener {

                var bitmap = signatureInkView.bitmap
                var isEmpty = bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config))
                when (selectedSignature) {
                    requestedSignature.representativeDeficiency -> {
                        facilityRepresentativeDeficienciesSignatureBitmap = bitmap
                        if (!isEmpty){
                            facilityRepresentativeDeficienciesSignatureButton.text ="Edit Signature"
                            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(bitmap)
                        }else{
                            facilityRepresentativeDeficienciesSignatureButton.text ="Add Signature"
                            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)
                        }
                    }
                }
                signatureInkView.clear()
//                visitationFormAlphaBackground.visibility = View.GONE
                signatureDialog.visibility = View.GONE
                (activity as FormsActivity).overrideBackButton = false
            }

            try {
                var bitmap = signatureInkView.bitmap
                var isEmpty = bitmap.sameAs(Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config))
                when (selectedSignature) {


                    requestedSignature.representativeDeficiency -> {
                        facilityRepresentativeDeficienciesSignatureBitmap = bitmap
                        if (!isEmpty){
                            facilityRepresentativeDeficienciesSignatureButton.text ="Edit Signature"
                            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(bitmap)
                        }else{
                            facilityRepresentativeDeficienciesSignatureButton.text ="Add Signature"
                            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)
                        }
                    }
                }
                signatureInkView.clear()
                visitationFormAlphaBackground.visibility = View.GONE
                signatureDialog.visibility = View.GONE
                (activity as FormsActivity).overrideBackButton = false
            } catch (e: Exception) {
            }


        }

        loadDefButton.setOnClickListener {
            fillDeffTableView()
        }


        newClearedDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                newClearedDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        signatureDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                signatureDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }


        newVisitationDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "MM/dd/yyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                newVisitationDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        submitNewDeffNewBtn.setOnClickListener {
            if (validateInputs()){
                progressBarText.text = "Saving ..."
                DeffLoadingView.visibility = View.VISIBLE
                var item = TblDeficiency()
                for (fac in TypeTablesModel.getInstance().AARDeficiencyType) {
                    if (newDefSpinner.getSelectedItem().toString().equals(fac.DeficiencyName))
                        item.DefTypeID =fac.DeficiencyTypeID
                }
                item.VisitationDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.EnteredDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.ClearedDate = if (newClearedDateBtn.text.equals("SELECT DATE")) "" else newClearedDateBtn.text.toString().appToApiSubmitFormatMMDDYYYY()
                item.Comments = if (comments_editTextVal.text.isNullOrEmpty())  "" else comments_editTextVal.text.toString()
                Log.v("Deficiency--- ",UpdateDeficiencyData + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&defId=&defTypeId=${item.DefTypeID.toString()}&visitationDate=${item.VisitationDate}" +
                        "&enteredDate=${item.EnteredDate}&clearedDate=${item.ClearedDate}&comments=${item.Comments}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat())
                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateDeficiencyData + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode="+FacilityDataModel.getInstance().clubCode+"&defId=&defTypeId=${item.DefTypeID.toString()}&visitationDate=${item.VisitationDate}" +
                        "&enteredDate=${item.EnteredDate}&clearedDate=${item.ClearedDate}&comments=${item.Comments}&insertBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&insertDate="+Date().toApiSubmitFormat()+"&updateBy=${ApplicationPrefs.getInstance(activity).loggedInUserID}&updateDate="+Date().toApiSubmitFormat(),
                        Response.Listener { response ->
                            activity!!.runOnUiThread {
                                if (response.toString().contains("returnCode>0<",false)) {
                                    Utility.showSubmitAlertDialog(activity, true, "Deficiency")
                                    item.DefID = response.toString().substring(response.toString().indexOf("<DefID")+7,response.toString().indexOf("</DefID"))
                                    Utility.showMessageDialog(activity,"DEF ID",item.DefID)
                                    FacilityDataModel.getInstance().tblDeficiency.add(item)
                                    fillDeffTableView()
                                    HasChangedModel.getInstance().groupDeficiencyDef[0].DeficiencyDef= true
                                    HasChangedModel.getInstance().changeDoneForDeficiencyDef()
                                } else {
                                    var errorMessage = response.toString().substring(response.toString().indexOf("<message")+9,response.toString().indexOf("</message"))
                                    Utility.showSubmitAlertDialog(activity, false, "Deficiency (Error: "+errorMessage+" )")
                                }
                                DeffLoadingView.visibility = View.GONE
                                progressBarText.text = "Loading ..."
                                defeciencyCard.visibility=View.GONE
                                (activity as FormsActivity).overrideBackButton = false
                                visitationFormAlphaBackground.visibility = View.GONE
                            }
                        }, Response.ErrorListener {
                    Utility.showSubmitAlertDialog(activity, false, "Deficiency")
                    DeffLoadingView.visibility = View.GONE
                    progressBarText.text = "Loading ..."
                    defeciencyCard.visibility=View.GONE
                    (activity as FormsActivity).overrideBackButton = false
                    visitationFormAlphaBackground.visibility = View.GONE
                }))

            } else {
                Utility.showValidationAlertDialog(activity,"Please fill all required fields")
            }
        }
        altDeffTableRow(2)
    }

    fun altDeffTableRow(alt_row : Int) {
        var childViewCount = DeffResultsTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= DeffResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                        R.drawable.alt_row_color);
            } else row.background = getResources().getDrawable(
                    R.drawable.row_color)

        }
    }



    private var defTypeList = ArrayList<TypeTablesModel.aarDeficiencyType>()
    private var defTypeArray = ArrayList<String>()

    private var commentesTypeList = ArrayList<TypeTablesModel.warrantyPeriodType>()
    private var commentesTypeArray = ArrayList<String>()

    fun prepareDefSpinners() {

        defTypeList = TypeTablesModel.getInstance().AARDeficiencyType
        defTypeArray.clear()
        for (fac in defTypeList) {
            defTypeArray.add(fac.DeficiencyName)
        }

        var defTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, defTypeArray)
        defTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newDefSpinner.adapter = defTypeAdapter

        commentesTypeList = TypeTablesModel.getInstance().WarrantyPeriodType
        commentesTypeArray.clear()
        for (fac in commentesTypeList) {
            commentesTypeArray.add(fac.WarrantyTypeName)
        }
    }

    fun getDefTypeName(typeID: String): String {
        var typeName = ""
        for (fac in defTypeList) {
            if (fac.DeficiencyTypeID.equals(typeID)) {
                typeName= fac.DeficiencyName
            }
        }
        return typeName
    }

    fun fillDeffTableView() {

        DeffLoadingView.visibility = View.VISIBLE

        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (DeffResultsTbl.childCount>1) {
            for (i in DeffResultsTbl.childCount - 1 downTo 1) {
                DeffResultsTbl.removeViewAt(i)
            }

        }

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.leftMargin=10
        rowLayoutParam.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam.width = 0

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 0.7F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam1.width = 0

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 0.7F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam2.width = 0

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 0.7F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam3.width = 0

        val rowLayoutParam4 = TableRow.LayoutParams()
        rowLayoutParam4.weight = 1.4F
        rowLayoutParam4.column = 4
        rowLayoutParam4.height = TableRow.LayoutParams.WRAP_CONTENT
        rowLayoutParam4.width = 0

        val rowLayoutParamRow = TableRow.LayoutParams()
        rowLayoutParamRow.height = TableLayout.LayoutParams.WRAP_CONTENT

        FacilityDataModel.getInstance().tblDeficiency.apply {
            (0 until size).forEach {
                if ((filteredDefRadioButton.isChecked && get(it).ClearedDate.isNullOrEmpty()) || (filteredDefRadioButton.isChecked && get(it).ClearedDate.equals("1900-01-01T00:00:00-08:00")) || allDefRadioButton.isChecked) {
                    var tableRow = TableRow(context)
                    tableRow.layoutParams = rowLayoutParamRow
                    tableRow.minimumHeight = 30

                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30

                    textView.text = getDefTypeName(get(it).DefTypeID)
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30

                    try {
                        textView.text = get(it).VisitationDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView.text = get(it).VisitationDate

                    }

                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30
                    TableRow.LayoutParams()

                    try {
                        textView.text = get(it).EnteredDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView.text = get(it).EnteredDate

                    }

                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30


                    try {
                        textView.text = if (get(it).ClearedDate.apiToAppFormatMMDDYYYY().equals("01/01/1900")) "" else get(it).ClearedDate.apiToAppFormatMMDDYYYY()
                    } catch (e: Exception) {
                        textView.text = get(it).ClearedDate

                    }
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam4
                    textView.gravity = Gravity.CENTER_VERTICAL
                    textView.textSize = 18f
                    textView.minimumHeight = 30

                    textView.text = get(it).Comments
                    tableRow.addView(textView)


                    DeffResultsTbl.addView(tableRow)
                }
            }
        }
        altDefTableRow(2)
        DeffLoadingView.visibility = View.GONE
    }
    fun altDefTableRow(alt_row : Int) {
        var childViewCount = DeffResultsTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= DeffResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.background = getResources().getDrawable(
                        R.drawable.alt_row_color);
            } else {
                row.background = getResources().getDrawable(
                        R.drawable.row_color);
            }


        }
    }



    fun validateInputs() : Boolean {

        var defValide= TblDeficiency().isInputsValid
        defValide = true

        newVisitationDateBtn.setError(null)
        signatureDateBtn.setError(null)
        facilityRepresentativeDeficienciesSignatureButton.setError(null)


        if(newVisitationDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            defValide = false
            newVisitationDateBtn.setError("Required Field")
        }

        for (fac in FacilityDataModel.getInstance().tblDeficiency) {

            if (!fac.ClearedDate.isNullOrEmpty()) {

                if (signatureDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
                    defValide = false
                    signatureDateBtn.setError("Required Field")
                }

                if (facilityRepresentativeDeficienciesSignatureButton.text.toString() == "ADD SIGNATURE" ||
                        facilityRepresentativeDeficienciesSignatureButton.text.toString() =="Add Signature") {

                    defValide = false
                    facilityRepresentativeDeficienciesSignatureButton.setError("required field")

                }

            }

        }
        return  defValide

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
        fun newInstance(param1: String, param2: String): FragmentARRAVDeficiency {
            val fragment = FragmentARRAVDeficiency()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

