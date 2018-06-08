package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.inspection.R
import com.inspection.model.FacilityDataModel
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
        fillDeffTableView();

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
                         var item = FacilityDataModel.TblDeficiency()
                for (fac in TypeTablesModel.getInstance().AARDeficiencyType) {
                    if (newDefSpinner.getSelectedItem().toString().equals(fac.DeficiencyName))

                        item.DefTypeID =fac.DeficiencyTypeID
                }


                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.VisitationDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString()
                item.EnteredDate = if (newVisitationDateBtn.text.equals("SELECT DATE")) "" else newVisitationDateBtn.text.toString()
                item.ClearedDate=newClearedDateBtn.text.toString()
                item.Comments = if (comments_editTextVal.text.isNullOrEmpty())  "" else comments_editTextVal.text.toString()

                FacilityDataModel.getInstance().tblDeficiency.add(item)
                //  BuildProgramsList()

                addTheLatestRowOfPortalAdmin()
            }
           // Toast.makeText(context,"please fill all required fields",Toast.LENGTH_SHORT).show()


        })

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
            textView.text = VisitationDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = EnteredDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = ClearedDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = Comments
            tableRow.addView(textView)


            DeffResultsTbl.addView(tableRow)

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
                textView.text = get(it).VisitationDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).EnteredDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).ClearedDate
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



    fun validateInputs() : Boolean {
        var isInputsValid = true

        newVisitationDateBtn.setError(null)
        signatureDateBtn.setError(null)
        facilityRepresentativeSignatureButton.setError(null)


        if(newVisitationDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
            isInputsValid=false
            newVisitationDateBtn.setError("Required Field")
        }

        for (fac in FacilityDataModel.getInstance().tblDeficiency) {

            if (!fac.ClearedDate.isNullOrEmpty()) {

                if (signatureDateBtn.text.toString().toUpperCase().equals("SELECT DATE")) {
                    isInputsValid = false
                    signatureDateBtn.setError("Required Field")
                }

                if (facilityRepresentativeSignatureButton.text.toString().toUpperCase().equals("ADD SIGNATURE")) {
                    isInputsValid = false
                    facilityRepresentativeSignatureButton.setError("Required Field")
                }
            }
            else{
                Toast.makeText(context,"all items are cleared",Toast.LENGTH_SHORT).show()
            }
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
