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
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.R
import com.inspection.Utils.Constants.UpdateFacilityServicesData
import com.inspection.Utils.MarkChangeWasDone
import com.inspection.model.FacilityDataModel
import com.inspection.model.FacilityDataModelOrg
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_arrav_facility_services.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVFacilityServices.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVFacilityServices.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVFacilityServices : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_facility_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        exitFC_ServicesDialogeBtnId.setOnClickListener({

            facilityServicesCard.visibility=View.GONE
            alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE


        })

        showNewserviceDialogueButton.setOnClickListener(View.OnClickListener {
            comments_editTextVal.setText("")
            fceffective_date_textviewVal.setText("SELECT DATE")
            fcexpiration_date_textviewVal.setText("SELECT DATE")
            fc_services_textviewVal.setSelection(0)



            comments_editTextVal.setError(null)
            fceffective_date_textviewVal.setError(null)
            fc_servicestextViewToCheckSpinner.setError(null)
            facilityServicesCard.visibility=View.VISIBLE
            alphaBackgroundForFC_ServicesDialogs.visibility = View.VISIBLE


        })


        fcexpiration_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                fcexpiration_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        fceffective_date_textviewVal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                c.set(year,monthOfYear,dayOfMonth)
                fceffective_date_textviewVal!!.text = sdf.format(c.time)
            }, year, month, day)
            dpd.show()
        }

        var servicesArray= ArrayList<String>()

        servicesArray.add("select service")

        for (fac in TypeTablesModel.getInstance().ServicesType) {


            servicesArray.add(fac.ServiceTypeName)
        }

        var servicesAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, servicesArray)
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fc_services_textviewVal.adapter = servicesAdapter

        submitNewserviceButton.setOnClickListener({

            if (validateInputs()){

                FC_LoadingView.visibility = View.VISIBLE



                var item = FacilityDataModel.TblFacilityServices()
                for (fac in TypeTablesModel.getInstance().ServicesType) {
                    if (fc_services_textviewVal.getSelectedItem().toString().equals(fac.ServiceTypeName))

                        item.ServiceID =fac.ServiceTypeID
                }
                //    item.programtypename = program_name_textviewVal.getSelectedItem().toString()
                item.effDate = if (fceffective_date_textviewVal.text.equals("SELECT DATE")) "" else fceffective_date_textviewVal.text.toString()
                item.expDate = if (fcexpiration_date_textviewVal.text.equals("SELECT DATE")) "" else fcexpiration_date_textviewVal.text.toString()
                item.Comments=comments_editTextVal.text.toString()
                FacilityDataModel.getInstance().tblFacilityServices.add(item)
                //  BuildProgramsList()


                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, UpdateFacilityServicesData + "&facilityServicesId=5307&serviceId=${item.ServiceID}&effDate=${item.effDate}&expDate=${item.expDate}&comments=${item.Comments}&active=1&insertBy=E110997&insertDate=2013-06-18T10:38:53.773&updateBy=SumA&updateDate=2013-06-18T10:38:53.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("FC_SUBMIT_RESPONSE",response.toString())
                                FC_LoadingView.visibility = View.GONE

                                facilityServicesCard.visibility=View.GONE
                                alphaBackgroundForFC_ServicesDialogs.visibility = View.GONE
                                addTheLatestRowOfPortalAdmin()


                                var itemOrgArray = FacilityDataModelOrg.getInstance().tblFacilityServices
                                var itemArray = FacilityDataModel.getInstance().tblFacilityServices
                                for (itemAr in itemArray){
                                    for (itemOrgAr in itemOrgArray){

                                        if (itemAr.Comments!=itemOrgAr.Comments||itemAr.effDate!=itemOrgAr.effDate||
                                                itemAr.expDate!=itemOrgAr.expDate||
                                                itemAr.ServiceID!=itemOrgAr.ServiceID){
                                            MarkChangeWasDone()
                                            Toast.makeText(context,"changes found",Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(context,"no changes was found",Toast.LENGTH_SHORT).show()

                                        }

                                    }
                                }


                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                    FC_LoadingView.visibility = View.GONE

                }))


            }else {
                Toast.makeText(context,"please fill required fields",Toast.LENGTH_SHORT).show()
            }

        })
        fillPortalTrackingTableView();

        altFacServiceTableRow(2)

    }

    fun fillPortalTrackingTableView() {
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

           FacilityDataModel.getInstance().tblFacilityServices.apply {

            (0 until size).forEach {
                var tableRow = TableRow(context)

                var textView = TextView(context)
                textView.layoutParams = rowLayoutParam
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                for (fac in TypeTablesModel.getInstance().ServicesType) {
                    if (get(it).ServiceID.equals(fac.ServiceTypeID))

                        textView.text =fac.ServiceTypeName
                }
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam1
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).effDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam2
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                TableRow.LayoutParams()
                textView.text = get(it).expDate
                tableRow.addView(textView)

                textView = TextView(context)
                textView.layoutParams = rowLayoutParam3
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textView.text = get(it).Comments
                tableRow.addView(textView)


                aarPortalTrackingTableLayout.addView(tableRow)
            }
        }
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
        FacilityDataModel.getInstance().tblFacilityServices[FacilityDataModel.getInstance().tblFacilityServices.size - 1].apply {


            var tableRow = TableRow(context)

            var textView = TextView(context)
            textView.layoutParams = rowLayoutParam
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            for (fac in TypeTablesModel.getInstance().ServicesType) {
                if (ServiceID.equals(fac.ServiceTypeID))

                    textView.text =fac.ServiceTypeName
            }
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam1
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = effDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam2
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            TableRow.LayoutParams()
            textView.text = expDate
            tableRow.addView(textView)

            textView = TextView(context)
            textView.layoutParams = rowLayoutParam3
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.text = Comments
            tableRow.addView(textView)


            aarPortalTrackingTableLayout.addView(tableRow)

        }

        altFacServiceTableRow(2)
    }

    fun altFacServiceTableRow(alt_row : Int) {
        var childViewCount = aarPortalTrackingTableLayout.getChildCount();

        for ( i in 1..childViewCount-1) {
            var row : TableRow= aarPortalTrackingTableLayout.getChildAt(i) as TableRow;

            if (i % alt_row != 0) {
                row.setBackground(getResources().getDrawable(
                        R.drawable.alt_row_color));
            } else {
                row.setBackground(getResources().getDrawable(
                        R.drawable.row_color));
            }

        }
    }


    fun validateInputs() : Boolean {

        var facServicesValide=FacilityDataModel.TblFacilityServices().isInputsValid
        facServicesValide = true

        fceffective_date_textviewVal.setError(null)
        fc_servicestextViewToCheckSpinner.setError(null)

        if(fceffective_date_textviewVal.text.toString().toUpperCase().equals("SELECT DATE")) {
            facServicesValide = false
            fceffective_date_textviewVal.setError("Required Field")
        }


        if(fc_services_textviewVal.selectedItem.toString().contains("select")) {
            facServicesValide = false
            fc_servicestextViewToCheckSpinner.setError("Required Field")
        }


        return facServicesValide
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
        fun newInstance(param1: String, param2: String): FragmentARRAVFacilityServices {
            val fragment = FragmentARRAVFacilityServices()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
