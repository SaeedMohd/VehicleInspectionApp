package com.inspection.fragments


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.inspection.R
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.fragment_awards_and_distinctions.*


/**
 * A simple [Fragment] subclass.
 */
class AwardsAndDistinctionsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_awards_and_distinctions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scopeOfServiceChangesWatcher()

        exitAwardsDialogeBtnId.setOnClickListener({

            awardsCard.visibility=View.GONE
            alphaBackgroundForAwardsAndDistinctionDialogs.visibility = View.GONE


        })

        showAwardsCard.setOnClickListener(View.OnClickListener {

            awardsCard.visibility=View.VISIBLE
            alphaBackgroundForAwardsAndDistinctionDialogs.visibility = View.VISIBLE


        })

        fillAwardsTableView()

        submitNewAward.setOnClickListener(View.OnClickListener {

            awardsCard.visibility=View.GONE
            alphaBackgroundForAwardsAndDistinctionDialogs.visibility = View.GONE


        })

    }

    fun fillAwardsTableView() {

        if (mainAwardsTableLayout.childCount>1) {
            for (i in mainAwardsTableLayout.childCount - 1 downTo 1) {
                mainAwardsTableLayout.removeViewAt(i)
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

//        FacilityDataModel.getInstance().tbl.apply {
//            (0 until size).forEach {
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


            mainAwardsTableLayout.addView(tableRow)


        }





    }
    fun scopeOfServiceChangesWatcher(){

//        if (FragmentARRAVScopeOfService.dataChanged) {
//
//            val builder = AlertDialog.Builder(context)
//
//            // Set the alert dialog title
//            builder.setTitle("Changes made confirmation")
//
//            // Display a message on alert dialog
//            builder.setMessage("You've Just Changed Data in General Information Page, Do you want to keep those changes?")
//
//            // Set a positive button and its click listener on alert dialog
//            builder.setPositiveButton("YES") { dialog, which ->
//
//                scopeOfServicesChangesDialogueLoadingView.visibility = View.VISIBLE
//
//
//
//                Volley.newRequestQueue(context!!).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=${FragmentARRAVScopeOfService.fixedLaborRate}&laborMin=${FragmentARRAVScopeOfService.laborRateMatrixMin}&laborMax=${FragmentARRAVScopeOfService.laborRateMatrixMax}&diagnosticRate=${FragmentARRAVScopeOfService.diagnosticLaborRate}&numOfBays=${FragmentARRAVScopeOfService.numberOfBaysEditText_}&numOfLifts=${FragmentARRAVScopeOfService.numberOfLiftsEditText_}&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
//                        Response.Listener { response ->
//                            activity!!.runOnUiThread(Runnable {
//                                Log.v("RESPONSE", response.toString())
//                                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//                                Toast.makeText(context!!, "done", Toast.LENGTH_SHORT).show()
//                                if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
//                                    FacilityDataModel.getInstance().tblScopeofService[0].apply {
//
//                                        LaborMax = if (FragmentARRAVScopeOfService.laborRateMatrixMax.isNullOrBlank()) LaborMax else FragmentARRAVScopeOfService.laborRateMatrixMax
//                                        LaborMin = if (FragmentARRAVScopeOfService.laborRateMatrixMin.isNullOrBlank())LaborMin else FragmentARRAVScopeOfService.laborRateMatrixMin
//                                        FixedLaborRate = if (FragmentARRAVScopeOfService.fixedLaborRate.isNullOrBlank())FixedLaborRate else FragmentARRAVScopeOfService.fixedLaborRate
//                                        DiagnosticsRate = if (FragmentARRAVScopeOfService.diagnosticLaborRate.isNullOrBlank())DiagnosticsRate else FragmentARRAVScopeOfService.diagnosticLaborRate
//                                        NumOfBays = if (FragmentARRAVScopeOfService.numberOfBaysEditText_.isNullOrBlank())NumOfBays else FragmentARRAVScopeOfService.numberOfBaysEditText_
//                                        NumOfLifts = if (FragmentARRAVScopeOfService.numberOfLiftsEditText_.isNullOrBlank())NumOfLifts else FragmentARRAVScopeOfService.numberOfLiftsEditText_
//
//                                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID = FragmentARRAVScopeOfService.typeIdCompare
//
//                                        FragmentARRAVScopeOfService.dataChanged =false
//
//                                    }
//
//                                }
//
//                            })
//                        }, Response.ErrorListener {
//                    Log.v("error while loading", "error while loading personnal record")
//                    Toast.makeText(context!!, "error while saving page", Toast.LENGTH_SHORT).show()
//                    scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//
//                }))
//
//
//            }
//
//
//
//
//
//            // Display a negative button on alert dialog
//            builder.setNegativeButton("No") { dialog, which ->
//                FragmentARRAVScopeOfService.dataChanged =false
//                scopeOfServicesChangesDialogueLoadingView.visibility = View.GONE
//
//            }
//
//
//
//
//            // Finally, make the alert dialog using builder
//            val dialog: AlertDialog = builder.create()
//            dialog.setCanceledOnTouchOutside(false)
//            // Display the alert dialog on app interface
//            dialog.show()
//
//        }

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
        fun newInstance(param1: String, param2: String): AwardsAndDistinctionsFragment {
            val fragment = AwardsAndDistinctionsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }


}// Required empty public constructor
