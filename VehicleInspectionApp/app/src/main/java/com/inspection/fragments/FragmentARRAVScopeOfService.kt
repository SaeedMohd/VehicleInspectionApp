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
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.model.AAALocations
import com.inspection.model.AAAScopeOfServices
import kotlinx.android.synthetic.main.fragment_aar_manual_visitation_form.*
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentARRAVScopeOfService.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentARRAVScopeOfService.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVScopeOfService : Fragment() {

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

        var warrantyArray= arrayOf("12/12/", "24/24", "36/36", "48/48", "60/60", "Lifetime")
        var warrantyAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, warrantyArray)
        warrantyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warrantyPeriodVal.adapter = warrantyAdapter
    }

    fun prepareScopePage () {

        if (!(activity as MainActivity).FacilityNumber.isNullOrEmpty()) {
            progressbarScope.visibility = View.VISIBLE

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.facilityScopeOfSvcURL+(activity as MainActivity).FacilityNumber,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            var facScopeOfSvcList = Gson().fromJson(response.toString(), Array<AAAScopeOfServices>::class.java).toCollection(ArrayList())
                            for (fac in facScopeOfSvcList ) {
                                fixedLaborRateEditText.setText(fac.fixedlaborrate.toString())
                                laborRateMatrixMinEditText.setText(fac.labormin.toString())
                                laborRateMatrixMaxEditText.setText(fac.labormax.toString())
                                diagnosticRateEditText.setText(fac.diagnosticsrate.toString())
                                numberOfBaysEditText.setText(fac.numofbays.toString())
                                numberOfLiftsEditText.setText(fac.numoflifts.toString())
                                warrantyPeriodVal.setSelection(fac.warrantytypeid)

                            }
                            progressbarScope.visibility = View.INVISIBLE
                        })
                    }, Response.ErrorListener {
                Log.v("error while loading", "error while loading Scope Of Services")
                Toast.makeText(activity, "Connection Error. Please check the internet connection", Toast.LENGTH_LONG).show()
            }))
        }
    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        fixedLaborRateEditText.setError(null)
        diagnosticRateEditText.setError(null)
        laborRateMatrixMaxEditText.setError(null)
        laborRateMatrixMinEditText.setError(null)


        if(fixedLaborRateEditText.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            fixedLaborRateEditText.setError("Required Field")
        }

        if(diagnosticRateEditText.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            diagnosticRateEditText.setError("Required Field")
        }

        if(laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            laborRateMatrixMaxEditText.setError("Required Field")
        }

        if(laborRateMatrixMinEditText.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            laborRateMatrixMinEditText.setError("Required Field")
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
