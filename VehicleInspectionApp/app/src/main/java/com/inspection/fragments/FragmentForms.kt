package com.inspection.fragments


import android.app.AlertDialog
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.inspection.MainActivity
import com.inspection.R
import com.inspection.Utils.Constants
import kotlinx.android.synthetic.main.fragment_forms.*

class FragmentForms : androidx.fragment.app.Fragment(), OnClickListener {

    var formsStringsArray = arrayOf("Visitation Planning", "APP / Ad Hoc Visitation", "My Performance")

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
            var service = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

            var enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true

            if (!enabled) {
                var alertBuilder = AlertDialog.Builder(activity);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("GPS Location is required")
                alertBuilder.setMessage("GPS location is required within this app. ");
                alertBuilder.setPositiveButton("Agree") { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                alertBuilder.setNegativeButton("Disagree") { dialog, which ->

                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                (activity as MainActivity).supportActionBar!!.title = "Visitation Planning"
                var fragment = VisitationPlanningFragment()
                fragment!!.isVisitationPlanning = true
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment, fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
            }
        }

        adHocVisitationButton.setOnClickListener {
            var service = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            var enabled = if (Constants.enableLocationTracking) service.isProviderEnabled(LocationManager.GPS_PROVIDER) else true

            if (!enabled) {
                var alertBuilder = AlertDialog.Builder(activity);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("GPS Location is required")
                alertBuilder.setMessage("GPS location is required within this app. ");
                alertBuilder.setPositiveButton("Agree") { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                alertBuilder.setNegativeButton("Disagree") { dialog, which ->

                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                (activity as MainActivity).supportActionBar!!.title = "APP / Ad Hoc Visitation"
                var fragment = AppAdHockVisitationFilterFragment()
                fragment!!.isVisitationPlanning = false
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC!!.beginTransaction()
                ftSC.replace(R.id.fragment, fragment)
                ftSC.addToBackStack("frag")
                ftSC.commit()
            }
        }

        myPerformanceButton.setOnClickListener {
            (activity as MainActivity).supportActionBar!!.title = "My Performance"
            var fragment = PDFGenerateFragment()
//            fragment!!.isVisitationPlanning = false
            val fragmentManagerSC = fragmentManager
            val ftSC = fragmentManagerSC!!.beginTransaction()
            ftSC.replace(R.id.fragment, fragment)
            ftSC.addToBackStack("frag")
            ftSC.commit()
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
