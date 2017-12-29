package com.inspection.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.inspection.R
import kotlinx.android.synthetic.main.fragment_arravfacility_continued.*
import kotlinx.android.synthetic.main.fragment_arravlocation.*


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentARRAVLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentARRAVLocation : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_arravlocation, container, false)
    }


    fun validateInputs() : Boolean {
        var isInputsValid = true

        loc1addr1latitude.setError(null)
        loc1addr2latitude.setError(null)
        loc1addr1longitude.setError(null)
        loc1addr2longitude.setError(null)

        if(loc1addr1latitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            loc1addr1latitude.setError("Required Field")
        }

        if(loc1addr2latitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            loc1addr2latitude.setError("Required Field")
        }

        if(loc1addr1longitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            loc1addr1longitude.setError("Required Field")
        }

        if(loc1addr2longitude.text.toString().isNullOrEmpty()) {
            isInputsValid=false
            loc1addr2longitude.setError("Required Field")
        }


        return isInputsValid
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
         * @return A new instance of fragment FragmentARRAVLocation.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentARRAVLocation {
            val fragment = FragmentARRAVLocation()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
