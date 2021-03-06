package com.inspection.fragments

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.inspection.R

import com.inspection.Utils.apiToAppFormatMMDDYYYY
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_aarav_comments.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVComments.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVComments.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentAARAVComments : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aarav_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService()
        scopeOfServiceChangesWatcher()
        prepareCommentsSpinners()


        startDateBtn.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    startDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
        }

        endDateBtn.setOnClickListener {
//            if (endDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "MM/dd/yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    endDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        exitDialogeBtn.setOnClickListener({
            addNewCommentsDialog.visibility = View.GONE
            alphaBackgroundForDialogs.visibility = View.GONE
        })

        addNewCommentBtn.setOnClickListener( {
            showAddNewCommentsDialog()
        })

        commentSubmitButton.setOnClickListener({
            validateCommentsData()
        })
        fillCommentsTableView()
    }


    private fun validateCommentsData() {
        var isInputsValid = true
        if (newCommentsText.text.toString().isNullOrEmpty()) {
            newCommentsText.setError("Required Field")
        } else {
            submitCommentsData()

        }

    }



    private fun submitCommentsData(){
        addNewCommentsDialog.visibility = View.GONE
        alphaBackgroundForDialogs.visibility = View.GONE
    }

    private fun showAddNewCommentsDialog() {
        alphaBackgroundForDialogs.visibility = View.VISIBLE
        addNewCommentsDialog.visibility = View.VISIBLE
    }

    fun fillCommentsTableView() {

        if (commentsResultsTbl.childCount > 1) {
            for (i in commentsResultsTbl.childCount - 1 downTo 1) {
                commentsResultsTbl.removeViewAt(i)
            }
        }


        val rowLayoutParam = TableRow.LayoutParams()
        rowLayoutParam.weight = 1F
        rowLayoutParam.column = 0
        rowLayoutParam.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam1 = TableRow.LayoutParams()
        rowLayoutParam1.weight = 1F
        rowLayoutParam1.column = 1
        rowLayoutParam1.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam2 = TableRow.LayoutParams()
        rowLayoutParam2.weight = 1F
        rowLayoutParam2.column = 2
        rowLayoutParam2.height = TableLayout.LayoutParams.WRAP_CONTENT

        val rowLayoutParam3 = TableRow.LayoutParams()
        rowLayoutParam3.weight = 1F
        rowLayoutParam3.column = 3
        rowLayoutParam3.height = TableLayout.LayoutParams.WRAP_CONTENT

        var dateTobeFormated = ""

        FacilityDataModel.getInstance().tblComments.apply {
            (0 until size).forEach {

                if (get(it).FACID > 0) {

                    var tableRow = TableRow(context)
                    if (it % 2 == 0) {
                        tableRow.setBackgroundResource(R.drawable.alt_row_color)
                    }
                    var textView = TextView(context)
                    textView.layoutParams = rowLayoutParam
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = if (get(it).CommentTypeID == 0) "" else TypeTablesModel.getInstance().CommentsType.filter { s -> s.CommentTypeID.toInt() == get(it).CommentTypeID }[0].CommentTypeName
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam1
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = "Test" // get(it).FAC_Addr1
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam2
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    TableRow.LayoutParams()
                    textView.text = get(it).insertDate.apiToAppFormatMMDDYYYY()
                    tableRow.addView(textView)

                    textView = TextView(context)
                    textView.layoutParams = rowLayoutParam3
                    textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    textView.text = get(it).Comment

                    tableRow.addView(textView)


                    commentsResultsTbl.addView(tableRow)
                }
            }
        }
//        altVenRevTableRow(2)
//            }
//        }

//        FacilityDataModel.getInstance(). .apply {
//            (0 until size).forEach {
//
//            }

    }

    fun fillFieldsIntoVariablesAndCheckDataChangedForScopeOfService(){

        FragmentARRAVScopeOfService.dataChanged =false

        FragmentARRAVScopeOfService.fixedLaborRate = if (FragmentARRAVScopeOfService.watcher_FixedLaborRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate else FragmentARRAVScopeOfService.watcher_FixedLaborRate
        FragmentARRAVScopeOfService.diagnosticLaborRate =  if (FragmentARRAVScopeOfService.watcher_DiagnosticsRate.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate else FragmentARRAVScopeOfService.watcher_DiagnosticsRate
        FragmentARRAVScopeOfService.laborRateMatrixMax =  if (FragmentARRAVScopeOfService.watcher_LaborMax.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMax else FragmentARRAVScopeOfService.watcher_LaborMax
        FragmentARRAVScopeOfService.laborRateMatrixMin =  if (FragmentARRAVScopeOfService.watcher_LaborMin.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].LaborMin else FragmentARRAVScopeOfService.watcher_LaborMin
        FragmentARRAVScopeOfService.numberOfBaysEditText_ =  if (FragmentARRAVScopeOfService.watcher_NumOfBays.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays else FragmentARRAVScopeOfService.watcher_NumOfBays
        FragmentARRAVScopeOfService.numberOfLiftsEditText_ =  if (FragmentARRAVScopeOfService.watcher_NumOfLifts.isNullOrBlank()) FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts else FragmentARRAVScopeOfService.watcher_NumOfLifts


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMax!= FragmentARRAVScopeOfService.laborRateMatrixMax){


            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].LaborMin!= FragmentARRAVScopeOfService.laborRateMatrixMin){

            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].FixedLaborRate!= FragmentARRAVScopeOfService.fixedLaborRate){


            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].DiagnosticsRate!= FragmentARRAVScopeOfService.diagnosticLaborRate){


            FragmentARRAVScopeOfService.dataChanged =true
        }


        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfBays!= FragmentARRAVScopeOfService.numberOfBaysEditText_){


            FragmentARRAVScopeOfService.dataChanged =true
        }

        if (FacilityDataModel.getInstance().tblScopeofService[0].NumOfLifts!= FragmentARRAVScopeOfService.numberOfLiftsEditText_){


            FragmentARRAVScopeOfService.dataChanged =true
        }

    }


    fun scopeOfServiceChangesWatcher() {
//        if (!FragmentARRAVScopeOfService.validationProblemFoundForOtherFragments) {
//
//
//            if (FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest) {
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
//                        commentsLoadingView.visibility = View.VISIBLE
//
//
//
//                        Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                                Response.Listener { response ->
//                                    activity!!.runOnUiThread(Runnable {
//                                        Log.v("RESPONSE", response.toString())
//                                        commentsLoadingView.visibility = View.GONE
//
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
//                            commentsLoadingView.visibility = View.GONE
//
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
//                        commentsLoadingView.visibility = View.GONE
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




    private var commentsTypeList = ArrayList<TypeTablesModel.commentsType>()
    private var commentsTypeArray = ArrayList<String>()

    fun prepareCommentsSpinners() {

        commentsTypeList= TypeTablesModel.getInstance().CommentsType
        commentsTypeArray.clear()
        for (fac in commentsTypeList) {
            commentsTypeArray.add(fac.CommentTypeName)
        }
        var commentsTypeAdapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, commentsTypeArray)
        commentsTypeAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.adapter = commentsTypeAdapter
    }

    // TODO: Rename method, update argument and hook method into UI event

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVComments.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVComments().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
