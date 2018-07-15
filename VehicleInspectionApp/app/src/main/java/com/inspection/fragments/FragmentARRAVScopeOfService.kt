package com.inspection.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.R
import com.inspection.model.FacilityDataModel
import com.inspection.model.SUBMITIONS.ScopeOfService
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import com.google.android.gms.drive.metadata.CustomPropertyKey.fromJson
import com.google.gson.GsonBuilder
import com.inspection.MainActivity.Companion.activity
import com.inspection.R.id.numberOfLiftsEditText
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

    var warrantyArray = emptyArray<String>()

    var saved_fixedLaborRate=""
    var saved_diagnosticLaborRate=""
    var saved_laborRateMatrixMax=""
    var saved_laborRateMatrixMin=""
    var saved_numberOfBaysEditText=""
    var saved_numberOfLiftsEditText=""




    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arrav_scope_of_service   , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scopeOfServicesChangesMade=false

        var warrantyArray= arrayOf("12/12/", "24/24", "36/36", "48/48", "60/60", "Lifetime")
        var warrantyAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, warrantyArray)
        warrantyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warrantyPeriodVal.adapter = warrantyAdapter



        saveBtnPressed()
//        prepareScopePage()
        setFields()
    }

    fun setFields(){
        if (FacilityDataModel.getInstance().tblScopeofService.size > 0){
            FacilityDataModel.getInstance().tblScopeofService[0].apply {
                fixedLaborRateEditText.setText(FixedLaborRate)
                diagnosticRateEditText.setText(DiagnosticsRate)
                numberOfBaysEditText.setText(NumOfBays)
                numberOfLiftsEditText.setText(NumOfLifts)
            }
        }
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

    fun saveBtnPressed(){


        saveBtnId.setOnClickListener(View.OnClickListener {

            if (validateInputs()){

                var fixedLaborRate=fixedLaborRateEditText.text.toString()
                var diagnosticLaborRate=diagnosticRateEditText.text.toString()
                var laborRateMatrixMax=laborRateMatrixMaxEditText.text.toString()
                var laborRateMatrixMin=laborRateMatrixMinEditText.text.toString()
                var numberOfBaysEditText=numberOfBaysEditText.text.toString()
                var numberOfLiftsEditText=numberOfLiftsEditText.text.toString()

                Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, "https://dev.facilityappointment.com/ACEAPI.asmx/UpdateScopeofServiceData?facNum=${FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString()}&clubCode=004&laborRateId=1&fixedLaborRate=$fixedLaborRate&laborMin=$laborRateMatrixMin&laborMax=$laborRateMatrixMax&diagnosticRate=$diagnosticLaborRate&numOfBays=$numberOfBaysEditText&numOfLifts=$numberOfLiftsEditText&warrantyTypeId=3&active=1&insertBy=sa&insertDate=2013-04-24T13:40:15.773&updateBy=SumA&updateDate=2015-04-24T13:40:15.773",
                        Response.Listener { response ->
                            activity!!.runOnUiThread(Runnable {
                                Log.v("RESPONSE",response.toString())
                            //    Toast.makeText(context,"changes saved in DB",Toast.LENGTH_SHORT).show()

                               var jsonObj : JSONObject?  = null;
                               var jsonObj2 : JSONObject?  = null;
                               var obj : JSONObject?  = null;
                               var obj2 : JSONObject?  = null;
try {

    obj = XML.toJSONObject(response.substring(response.indexOf("&lt;responseXml"), response.indexOf("&lt;returnCode")).replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"))
    obj2 = XML.toJSONObject(response.toString())
    jsonObj = obj.getJSONObject("responseXml")
    jsonObj2 = obj.getJSONObject("ScopeofService")


} catch ( e : JSONException) {
    Log.e("JSON exception", e.message);
    e.printStackTrace();
}

Log.d("oooXMLHERE", response.toString());

Log.d("oooJSONHERE", jsonObj.toString());
Log.d("oooJSON2HERE", jsonObj2.toString());
                                Log.d("oooOBJ_WITHOUTKEY", obj.toString())
                                Log.d("oooOBJ_WITHOUT_SUB", obj2.toString())




                                ScopeOfService.setInstance(Gson().fromJson<ScopeOfService>(jsonObj!!.get("ScopeofService").toString(), ScopeOfService::class.java))



                                saved_fixedLaborRate=fixedLaborRate
                                 saved_diagnosticLaborRate=diagnosticLaborRate
                                 saved_laborRateMatrixMax=laborRateMatrixMax
                                 saved_laborRateMatrixMin=laborRateMatrixMin
                                 saved_numberOfBaysEditText=numberOfBaysEditText
                                 saved_numberOfLiftsEditText=numberOfLiftsEditText

                            })
                        }, Response.ErrorListener {
                    Log.v("error while loading", "error while loading personnal record")
                }))
            }
        })
    }

    fun validateInputs() : Boolean {
        FacilityDataModel.TblScopeofService.isInputsValid = true

        fixedLaborRateEditText.setError(null)
        diagnosticRateEditText.setError(null)

        laborRateMatrixMaxEditText.setError(null)
        laborRateMatrixMinEditText.setError(null)


        if(fixedLaborRateEditText.text.toString().isNullOrEmpty()) {
            FacilityDataModel.TblScopeofService.isInputsValid=false
            fixedLaborRateEditText.setError("Required Field")
        }

        if(diagnosticRateEditText.text.toString().isNullOrEmpty()) {
            FacilityDataModel.TblScopeofService.isInputsValid=false
            diagnosticRateEditText.setError("Required Field")
        }


        if(laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            FacilityDataModel.TblScopeofService.isInputsValid=false
            laborRateMatrixMaxEditText.setError("Required Field")
        }

        if(laborRateMatrixMinEditText.text.toString().isNullOrEmpty()) {
            FacilityDataModel.TblScopeofService.isInputsValid=false
            laborRateMatrixMinEditText.setError("Required Field")
        }


        return FacilityDataModel.TblScopeofService.isInputsValid
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
         * @return A new instance of fragment FragmentARRAVFacility.
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
