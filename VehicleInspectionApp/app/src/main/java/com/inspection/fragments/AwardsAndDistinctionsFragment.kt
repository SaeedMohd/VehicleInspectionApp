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
class AwardsAndDistinctionsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_awards_and_distinctions, container, false)
    }

}// Required empty public constructor
