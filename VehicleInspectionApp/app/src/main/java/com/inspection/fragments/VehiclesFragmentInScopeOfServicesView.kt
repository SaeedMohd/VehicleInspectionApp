package com.inspection.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.inspection.R


/**
 * A simple [Fragment] subclass.
 */
class VehiclesFragmentInScopeOfServicesView : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicles_fragment_in_scope_of_services_view, container, false)
    }

}// Required empty public constructor
