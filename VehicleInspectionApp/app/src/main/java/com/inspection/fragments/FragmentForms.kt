package com.inspection.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.inspection.MainActivity
import com.inspection.R
import kotlinx.android.synthetic.main.fragment_forms.*

class FragmentForms : android.support.v4.app.Fragment(), OnClickListener {

    var formsStringsArray = arrayOf("Visitation Planning", "APP / AdHoc Visitation", "My Performance")

    //another added code for frag testing > sherif yousry
   // var fragment2: VehiclesFragmentInScopeOfServicesView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        (activity as MainActivity).supportActionBar!!.title = "ACE AAR Inspection"
        return inflater.inflate(R.layout.fragment_forms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitationPlanningButton.setOnClickListener {
            (activity as MainActivity).supportActionBar!!.title = "Visitation Planning"
            var fragment = VisitationPlanningFragment()
            fragment!!.isVisitationPlanning = true
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment,fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
        }

        adHocVisitationButton.setOnClickListener {
            (activity as MainActivity).supportActionBar!!.title = "App / AdHoc Visitation"
            var fragment = AppAdHockVisitationFilterFragment()
            fragment!!.isVisitationPlanning = false
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment,fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
//                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
        }

        //button added for fragments testing only > sherif yousry
//        fragmentTester.setOnClickListener {
//            fragment2 = VehiclesFragmentInScopeOfServicesView()
//          //  fragment!!.isVisitationPlanning = false
//                val fragmentManagerSC = fragmentManager
//                val ftSC = fragmentManagerSC!!.beginTransaction()
//                ftSC.replace(R.id.fragment,fragment2)
//                ftSC.addToBackStack("frag")
//                ftSC.commit()
////                (activity as MainActivity).supportActionBar!!.title = formsStringsArray[i].toString()
//        }

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
//                fragment = VisitationPlanningFragment()
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
