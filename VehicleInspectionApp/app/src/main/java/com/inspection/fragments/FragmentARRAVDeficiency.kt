package com.inspection.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
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
import com.inspection.R
import com.inspection.Utils.Constants.UpdateDeficiencyData
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.Utils.apiToAppFormat
import com.inspection.Utils.appToApiFormat
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.TypeTablesModel
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
        scopeOfServiceChangesWatcher()



        exitDeffeciencyDialogeBtnId.setOnClickListener({

            defeciencyCard.visibility=View.GONE
            visitationFormAlphaBackground.visibility = View.GONE


        })

        showNewDeffDialogueBtn.setOnClickListener(View.OnClickListener {
            comments_editTextVal.setText("")
            newVisitationDateBtn.setText("SELECT DATE")
            signatureDateBtn.setText("SELECT DATE")
            facilityRepresentativeDeficienciesSignatureButton.setText("ADD SIGNATURE")
            facilityRepresentativeDeficienciesSignatureImageView.setImageBitmap(null)



            newVisitationDateBtn.setError(null)
            signatureDateBtn.setError(null)
            facilityRepresentativeDeficienciesSignatureButton.setError(null)
            defeciencyCard.visibility=View.VISIBLE
            visitationFormAlphaBackground.visibility = View.VISIBLE


            facilityRepresentativeDeficienciesSignatureButton.setOnClickListener {
                signatureDialog.visibility = View.VISIBLE
                visitationFormAlphaBackground.visibility = View.VISIBLE
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
                visitationFormAlphaBackground.visibility = View.GONE
                signatureDialog.visibility = View.GONE
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
            } catch (e: Exception) {
            }


        })



        newClearedDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
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
                val myFormat = "dd MMM yyyy" // mention the format you need
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
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                newVisitationDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        newUpdatedByDateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                newUpdatedByDateBtn!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }
        submitNewDeffNewBtn.setOnClickListener({

            if (validateInputs()){

                DeffLoadingView.visibility = View.VISIBLE


                var item = FacilityDataModel.TblDeficiency()

                for (fac in TypeTablesModel.getInstance().AARDeficiencyType) {
                    if (newDefSpinner.getSelectedItem().toString().equals(fac.DeficiencyName))

                        item.DefTypeID =fac.DeficiencyTypeID
                }


                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.VisitationDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString()
                item.EnteredDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString()
                item.ClearedDate = if (newClearedDateBtn.text.equals("SELECT DATE")) "" else newClearedDateBtn.text.toString()
                item.Comments = if (comments_editTextVal.text.isNullOrEmpty())  "" else comments_editTextVal.text.toString()

                //  BuildProgramsList()



                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateDeficiencyData + "&defId=13688&defTypeId=${item.DefTypeID.toString()}&visitationDate=${item.VisitationDate}" +
                        "&enteredDate=${item.EnteredDate}&clearedDate=${item.ClearedDate}&comments=${item.Comments}&insertBy=MoritzM02&insertDate=2014-04-16T15:17:07.143&updateBy=SamA&updateDate=2014-04-30T13:45:28.477",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE",response.toString())
                                DeffLoadingView.visibility = View.GONE

                                defeciencyCard.visibility=View.GONE
                                visitationFormAlphaBackground.visibility = View.GONE
                                FacilityDataModel.getInstance().tblDeficiency.add(item)
                                addTheLatestRowOfPortalAdmin()



                                var itemOrgArray = FacilityDataModelOrg.getInstance().tblDeficiency
                                var itemArray = FacilityDataModel.getInstance().tblDeficiency
                                if (itemOrgArray.size!=itemArray.size){

                                    MarkChangeWasDone()
                                }else {

                                    for (itemAr in itemArray) {
                                        for (itemOrgAr in itemOrgArray) {

                                            if (itemAr.Comments != itemOrgAr.Comments || itemAr.VisitationDate != itemOrgAr.VisitationDate ||
                                                    itemAr.EnteredDate != itemOrgAr.EnteredDate ||
                                                    itemAr.ClearedDate != itemOrgAr.ClearedDate) {
                                                MarkChangeWasDone()
                                                Toast.makeText(context, "data submitted", Toast.LENGTH_SHORT).show()
                                            }

                                        }
                                    }
                                }


                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    DeffLoadingView.visibility = View.GONE

                }))

            }
            // Toast.makeText(context,"please fill all required fields",Toast.LENGTH_SHORT).show()


        })

        altDeffTableRow(2)


    }

    fun addTheLatestRowOfPortalAdmin() {
        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

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
        FacilityDataModel.getInstance().tblDeficiency[FacilityDataModel.getInstance().tblDeficiency.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().AARDeficiencyType) {
                if (DefTypeID.equals(fac.DeficiencyTypeID))

                    textView.text =fac.DeficiencyName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

            try {
                textView.text = VisitationDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = VisitationDate
            }

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()

            try {
                textView.text = EnteredDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = EnteredDate
            }

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

            try {
                textView.text = ClearedDate.appToApiFormat()
            } catch (e: Exception) {
                textView.text = ClearedDate
            }

            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam4
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = Comments
            tableRow.addView(textView)


            DeffResultsTbl.addView(tableRow)

        }
        altDeffTableRow(2)
    }

    fun altDeffTableRow(alt_row : Int) {
        var childViewCount = DeffResultsTbl.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= DeffResultsTbl.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

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

//        var commentesTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, commentesTypeArray)
//        commentesTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        newCommentSpinner.adapter = commentesTypeAdapter
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
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0

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
        FacilityDataModel.getInstance().tblDeficiency.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = getDefTypeName(get(it).DefTypeID)
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                try {
                    textView.text = get(it).VisitationDate.apiToAppFormat()
                } catch (e: Exception) {
                    textView.text = get(it).VisitationDate

                }

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()

                try {
                    textView.text = get(it).EnteredDate.apiToAppFormat()
                } catch (e: Exception) {
                    textView.text = get(it).EnteredDate

                }

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                try {
                    textView.text = get(it).ClearedDate.apiToAppFormat()
                } catch (e: Exception) {
                    textView.text = get(it).ClearedDate

                }

                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam4
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).Comments
                tableRow.addView(textView)


                DeffResultsTbl.addView(tableRow)
            }
        }
    }

    fun scopeOfServiceChangesWatcher(){

        if (FragmentARRAVScopeOfService.dataChanged) {

            val builder = AlertDialog.Builder(context)

            // Set the alert dialog title
            builder.setTitle("Changes made confirmation")

            // Display a message on alert dialog
            builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES") { dialog, which ->




                Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE", response.toString())

                                Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
                                if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
                                    FacilityDataModel.getInstance().tblScopeofService[0].apply {

                                        LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
                                        LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank())LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
                                        FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank())FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
                                        DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank())DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
                                        NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank())NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
                                        NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank())NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_

                                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare

                                        FragmentARRAVScopeOfService.dataChanged =false

                                    }

                                }

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()


                }))


            }





            // Display a negative button on alert dialog
            builder.setNegativeButton("No") { dialog, which ->
                FragmentARRAVScopeOfService.dataChanged =false

            }




            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            // Display the alert dialog on app interface
            dialog.show()

        }

    }


    fun validateInputs() : Boolean {

        var defValide=FacilityDataModel.TblDeficiency().isInputsValid
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

//                if (facilityRepresentativeDeficienciesSignatureBitmap==null) {
//                    isInputsValid = false
//                    facilityRepresentativeDeficienciesSignatureButton.setError("Required Field")
//                }

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

