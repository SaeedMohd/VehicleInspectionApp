package com.inspection.fragments

import android.app.Fragment
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast


import com.inspection.MainActivity
import com.inspection.R
import com.inspection.R.id.adHocVisitationButton
import com.inspection.Utils.ApplicationPrefs
import kotlinx.android.synthetic.main.fragment_forms.*

class FragmentForms : android.support.v4.app.Fragment(), OnClickListener {

    var formsStringsArray = arrayOf("Visitation Planning", "APP / AdHoc Visitation", "My Performance")
    var fragment: FragmentARRAnnualVisitationRecords? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        (activity as MainActivity).supportActionBar!!.title = "Forms"
        return inflater.inflate(R.layout.fragment_forms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitationPlanningButton.setOnClickListener {
            fragment = FragmentARRAnnualVisitationRecords()
            fragment!!.isVisitationPlanning = true
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment,fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
        }

        adHocVisitationButton.setOnClickListener {
            fragment = FragmentARRAnnualVisitationRecords()
            fragment!!.isVisitationPlanning = false
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment,fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
        }

//        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, formsStringsArray)
//        formsListView.adapter = arrayAdapter
//
//        formsListView.onItemClickListener = AdapterView.OnItemClickListener({ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
//            if (i == 0){
////                val fragment: android.support.v4.app.Fragment
////                fragment = FragmentAnnualVisitationPager()
////                val fragmentManagerSC = fragmentManager
////                val ftSC = fragmentManagerSC!!.beginTransaction()
////                ftSC.replace(R.id.fragment,fragment)
////                ftSC.addToBackStack("")
////                ftSC.commit()
////                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//
//                fragment = FragmentARRAnnualVisitationRecords()
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//            }
//        })

    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub

    }

    override fun onDestroy() {
        super.onDestroy()
    }



}
