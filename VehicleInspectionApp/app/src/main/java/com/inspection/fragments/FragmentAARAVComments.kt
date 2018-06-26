package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

import com.inspection.R
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
        prepareCommentsSpinners()


        startDateBtn.setOnClickListener {
//            if (startDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    c.set(year, monthOfYear, dayOfMonth)
                    startDateBtn!!.text = sdf.format(c.time)
                }, year, month, day)
                dpd.show()
//            }
        }

        endDateBtn.setOnClickListener {
//            if (endDateBtn.text.equals("SELECT DATE")) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    val myFormat = "dd MMM yyyy" // mention the format you need
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

        for (i in 1..2) {

            var tableRow = TableRow(context)
            if (i % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.alt_row_color)
            }
            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // getLocationTypeName(get(it).LocationTypeID)
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
            textView.text = "Test" // get(it).FAC_Addr2
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = "Test" // get(it).CITY

            tableRow.addView(textView)


            commentsResultsTbl.addView(tableRow)
        }
//        altVenRevTableRow(2)
//            }
//        }

//        FacilityDataModel.getInstance(). .apply {
//            (0 until size).forEach {
//
//            }

    }




    private var commentsTypeList = ArrayList<TypeTablesModel.commentsType>()
    private var commentsTypeArray = ArrayList<String>()

    fun prepareCommentsSpinners() {

        commentsTypeList= TypeTablesModel.getInstance().CommentsType
        commentsTypeArray.clear()
        for (fac in commentsTypeList) {
            commentsTypeArray.add(fac.CommentTypeName)
        }
        var commentsTypeAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, commentsTypeArray)
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
