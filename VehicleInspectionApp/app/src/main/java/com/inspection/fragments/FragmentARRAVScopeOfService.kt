package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import com.google.android.gms.drive.metadata.CustomPropertyKey.fromJson
import com.google.gson.GsonBuilder
import com.inspection.MainActivity.Companion.activity
import com.inspection.R.id.numberOfLiftsEditText
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.fragment_visitation_form.*
import kotlin.jvm.java


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVScopeOfService.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVScopeOfService.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVScopeOfService : Fragment() {

    var warrantyArray = ArrayList<String>()



    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_scope_of_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scopeOfServicesChangesMade = false





        for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType){


                warrantyArray.add(typeWarranty.WarrantyTypeName)


        }
        var warrantyAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, warrantyArray)
        warrantyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warrantyPeriodVal.adapter = warrantyAdapter

        saveBtnPressed()
//        prepareScopePage()
        setFields()
        setFieldsListener()
    }

    fun setFieldsListener (){



        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                var laborMaxWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                Toast.makeText(context,s.toString(),Toast.LENGTH_SHORT).show()
                LaborMax=s.toString()
            }
        }
        var laborMinWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                LaborMin=s.toString()
            }
        }
        var fixedLaborWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                FixedLaborRate=s.toString()
            }
        }
        var diagnosticWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                DiagnosticsRate=s.toString()
            }
        }
        var noOfBaysWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                NumOfBays=s.toString()
            }
        }

        var noOfLiftsWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                NumOfLifts=s.toString()

            }
        }

        laborRateMatrixMaxEditText.addTextChangedListener(laborMaxWatcher)
        laborRateMatrixMinEditText.addTextChangedListener(laborMinWatcher)
                fixedLaborRateEditText.addTextChangedListener(fixedLaborWatcher)
                diagnosticRateEditText.addTextChangedListener(diagnosticWatcher)
                numberOfBaysEditText.addTextChangedListener(noOfBaysWatcher)
                numberOfLiftsEditText.addTextChangedListener(noOfLiftsWatcher)


            }


        }

        warrantyPeriodVal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {



            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType){

                    // for (warSpinner in warrantyArray ){

                    if(typeWarranty.WarrantyTypeName==warrantyPeriodVal.selectedItem){

                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID=typeWarranty.WarrantyTypeID
                    }

                }



            }

        }
    }


    fun setFields() {
        if (FacilityDataModel.getInstance().tblScopeofService.size > 0) {
            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                fixedLaborRateEditText.setText(FixedLaborRate)
                diagnosticRateEditText.setText(DiagnosticsRate)
                numberOfBaysEditText.setText(NumOfBays)
                numberOfLiftsEditText.setText(NumOfLifts)
                laborRateMatrixMaxEditText.setText(LaborMax)
                laborRateMatrixMinEditText.setText(LaborMin)
                for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType){

                    for (facWarranty in FacilityDataModel.getInstance().tblScopeofService){

                        if (facWarranty.WarrantyTypeID==typeWarranty.WarrantyTypeID){

                            for (warSpinner in warrantyArray ){

                               if(typeWarranty.WarrantyTypeName==warSpinner){

                                  var i= warrantyArray.indexOf(warSpinner)

                                   warrantyPeriodVal.setSelection(i)



                               }
                            }
                        }
                    }


                }
                           }
        }


         //   for (facWarranty in FacilityDataModel.getInstance().tblScopeofService){

           //     if (facWarranty.WarrantyTypeID==typeWarranty.WarrantyTypeID){

    }



    var isFirstRun = true

//    fun prepareScopePage () {
//            isFirstRun = false
//            progressbarScope.visibility = View.VISIBLE
//
//            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Constants.facilityScopeOfSvcURL+AnnualVisitationSingleton.getInstance().facilityId,
//                    Response.Listener { response ->
//                        activity!!.runOnUiThread(Runnable {
//                            var facScopeOfSvcList = Gson().fromJson(response.toString(), Array<AAAScopeOfServices>::class.java).toCollection(ArrayList())
//                            for (fac in facScopeOfSvcList ) {
//                                fixedLaborRateEditText.setText(fac.fixedlaborrate.toString())
//                                //commented out code below cuz i commented out the refrenced view from xml cuz
//                                // i cant find the refrenced this view in the pdf > sherif yousry
//                        //        laborRateMatrixMinEditText.setText(fac.labormin.toString())
//                          //      laborRateMatrixMaxEditText.setText(fac.labormax.toString())
//                                diagnosticRateEditText.setText(fac.diagnosticsrate.toString())
//                                numberOfBaysEditText.setText(fac.numofbays.toString())
//                                numberOfLiftsEditText.setText(fac.numoflifts.toString())
//                                warrantyPeriodVal.setSelection(fac.warrantytypeid)
//
//                            }
//                            progressbarScope.visibility = View.INVISIBLE
//                        })
//                    }, Response.ErrorListener {
//                Log.v("error while loading", "error while loading Scope Of Services")
//                activity!!.toast("Connection Error. Please check the internet connection")
//            }))
//    }

    fun saveBtnPressed() {


        saveBtnId.setOnClickListener(View.OnClickListener {

            if (validateInputs()) {

                for (typeWarranty in TypeTablesModel.getInstance().WarrantyPeriodType){

                    // for (warSpinner in warrantyArray ){

                    if(typeWarranty.WarrantyTypeName==warrantyPeriodVal.selectedItem){

                        FacilityDataModel.getInstance().tblScopeofService[0].WarrantyTypeID=typeWarranty.WarrantyTypeID
                        //   for (facWarranty in FacilityDataModel.getInstance().tblScopeofService){

                        //     if (facWarranty.WarrantyTypeID==typeWarranty.WarrantyTypeID){



                    }
                    //     }
                    //  }
                    //    }


                }

                var fixedLaborRate = fixedLaborRateEditText.text.toString()
                var diagnosticLaborRate = diagnosticRateEditText.text.toString()
                var laborRateMatrixMax = laborRateMatrixMaxEditText.text.toString()
                var laborRateMatrixMin = laborRateMatrixMinEditText.text.toString()
                var numberOfBaysEditText = numberOfBaysEditText.text.toString()
                var numberOfLiftsEditText = numberOfLiftsEditText.text.toString()

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=$fixedLaborRate&laborMin=$laborRateMatrixMin&laborMax=$laborRateMatrixMax&diagnosticRate=$diagnosticLaborRate&numOfBays=$numberOfBaysEditText&numOfLifts=$numberOfLiftsEditText&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE", response.toString())
                                //    Toast.makeText(context,"changes saved in DB",Toast.LENGTH_SHORT).show()

                                var jsonObj: JSONObject? = null;
                                var jsonObj2: JSONObject? = null;
                                var obj: JSONObject? = null;
                                var obj2: JSONObject? = null;
                                try {

                                    obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
                                    obj2 = XML.toJSONObject(response.toString())
                                    jsonObj = obj.getJSONObject("responseXml")
                                    jsonObj2 = obj.getJSONObject("ScopeofService")


                                } catch (e: JSONException) {
                                    Log.e("JSON exception", e.message);
                                    e.printStackTrace();
                                }

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                }))
            }
        })
    }

    fun validateInputs(): Boolean {

        var scopeOfServiceValide = FacilityDataModel.TblScopeofService().isInputsValid
        scopeOfServiceValide = true

        fixedLaborRateEditText.setError(null)
        diagnosticRateEditText.setError(null)

        laborRateMatrixMaxEditText.setError(null)
        laborRateMatrixMinEditText.setError(null)


        if (fixedLaborRateEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            fixedLaborRateEditText.setError("Required Field")
        }

        if (diagnosticRateEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            diagnosticRateEditText.setError("Required Field")
        }


        if (laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            laborRateMatrixMaxEditText.setError("Required Field")
        }

        if (laborRateMatrixMinEditText.text.toString().isNullOrEmpty()) {
            scopeOfServiceValide = false
            laborRateMatrixMinEditText.setError("Required Field")
        }


        return scopeOfServiceValide

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
        var scopeOfServicesChangesMade = false

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FacilityGeneralInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVScopeOfService {
            val fragment = FragmentARRAVScopeOfService()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
