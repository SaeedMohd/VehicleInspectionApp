package com.inspection.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity

import com.inspection.R
import com.inspection.Utils.Consts
import com.inspection.Utils.toast
import com.inspection.model.AAALocations
import com.inspection.model.AAAPersonnelType
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*
import kotlinx.android.synthetic.main.fragment_arravpersonnel.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVLocation : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var facLocationsList = ArrayList<AAALocations>()
    private var facLocationsArray = ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravlocation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AnnualVisitationSingleton.getInstance().locationsList == null) {
            prepareLocationPage()
        }else{
            setLocations()

        }
    }

    fun prepareLocationPage (){

            progressbarLocation.visibility = View.VISIBLE

            Volley.newRequestQueue(context).add(StringRequest(Request.Method.GET, Consts.facilityLocationsURL+AnnualVisitationSingleton.getInstance().facilityId,
                    Response.Listener { response ->
                        activity!!.runOnUiThread(Runnable {
                            AnnualVisitationSingleton.getInstance().locationsList = Gson().fromJson(response.toString(), Array<AAALocations>::class.java).toCollection(ArrayList())
                            setLocations()
                        })
                    }, Response.ErrorListener {
                progressbarLocation.visibility = View.INVISIBLE
                Log.v("error while loading", "error while loading Locations")
                Log.v("error", ""+it.message)
                activity!!.toast("Connection Error. Please check the internet connection")
            }))
    }

    private fun setLocations(){
        context!!.toast("I am here in physical")
        for (fac in AnnualVisitationSingleton.getInstance().locationsList!!) {
            if (fac.loctypename.equals("Physical")) {
                phyloc1addr1branchname.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchname)
                phyloc1addr1branchno.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchnumber)
                phyloc1addr1latitude.setText(if (fac.latitude.isNullOrEmpty()) "" else fac.latitude)
                phyloc1addr1longitude.setText(if (fac.longitude.isNullOrEmpty()) "" else fac.longitude)
                phylocAddr1address.setText(if (fac.fac_addr1.isNullOrEmpty()) "" else fac.fac_addr1)
                phylocAddr2address.setText(if (fac.fac_addr2.isNullOrEmpty()) "" else fac.fac_addr2)
            } else if (fac.loctypename.equals("Mailing")) {
                mailaddr1branchname.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchname)
                mailaddr1branchno.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchnumber)
                mailAddr1address.setText(if (fac.fac_addr1.isNullOrEmpty()) "" else fac.fac_addr1)
                mailAddr2address.setText(if (fac.fac_addr2.isNullOrEmpty()) "" else fac.fac_addr2)
            } else if (fac.loctypename.equals("Billing")) {
                billaddr1branchname.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchname)
                billaddr1branchno.setText(if (fac.branchname.isNullOrEmpty()) "" else fac.branchnumber)
                billAddr1address.setText(if (fac.fac_addr1.isNullOrEmpty()) "" else fac.fac_addr1)
                billAddr2address.setText(if (fac.fac_addr2.isNullOrEmpty()) "" else fac.fac_addr2)
            }
        }
        progressbarLocation.visibility = View.INVISIBLE
    }

    fun validateInputs() : Boolean {
        var isInputsValid = true

        phyloc1addr1latitude.setError(null)
//        phyloc1addr2latitude.setError(null)
//        phyloc1addr2longitude.setError(null)
        phyloc1addr1longitude.setError(null)

        if(phyloc1addr1latitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            phyloc1addr1latitude.setError("Required Field")
        }

//        if(loc1addr2latitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2latitude.setError("Required Field")
//        }

        if(phyloc1addr1longitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            phyloc1addr1longitude.setError("Required Field")
        }

//        if(loc1addr2longitude.text.toString().isNullOrEmpty()) {
//            isInputsValid=false
//            loc1addr2longitude.setError("Required Field")
//        }


        return isInputsValid
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val isValidating = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentARRAVLocation.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(isValidating: Boolean): FragmentARRAVLocation {
            val fragment = FragmentARRAVLocation()
            val args = Bundle()
            args.putBoolean(this.isValidating, isValidating)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
