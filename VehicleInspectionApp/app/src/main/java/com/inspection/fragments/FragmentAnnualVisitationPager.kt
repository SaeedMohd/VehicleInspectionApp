package com.inspection.fragments

import android.content.Context

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.inspection.R
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Toast
import com.inspection.MainActivity

import kotlinx.android.synthetic.main.fragment_main_visitation.*
import android.app.AlertDialog
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import com.inspection.Utils.toast
import kotlinx.android.synthetic.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAnnualVisitationPager.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAnnualVisitationPager.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAnnualVisitationPager : android.support.v4.app.Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var mViewPager: ViewPager? = null
    var mSectionsPagerAdapter: SectionsPagerAdapter? = null

//    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        setC(R.layout.activity_main);
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main_visitation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager!!)
//        (activity as MainActivity).supportActionBar!!.title = "Forms"


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        container.clearFindViewByIdCache()
        mSectionsPagerAdapter = null
        fragmentManager!!.fragments.clear()
        fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":0")
        container.removeAllViews()
        container.adapter = null
        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager!!)
        (activity as MainActivity).viewPager = container
        container.offscreenPageLimit = 16
        container.adapter = mSectionsPagerAdapter
        container?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 4) {
                    val fragmentPersonnel = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":4") as? FragmentARRAVPersonnel
                    fragmentPersonnel?.preparePersonnelPage()
                } else if (position == 3) {
                    val fragmentLocations = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":3") as? FragmentARRAVLocation
                    fragmentLocations?.prepareLocationPage()
                } else if (position == 2) {
                    val fragmentFacilityCont = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":2") as? FragmentARRAVFacilityContinued
//                    fragmentFacilityCont?.prepareFacilityContinuedPage()
                } else if (position == 8) {
                    val fragmentScope = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":8") as? FragmentARRAVScopeOfService
                    fragmentScope?.prepareScopePage()
                } else if (position == 11) {
                    val fragmentProgramType = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":11") as? FragmentARRAVPrograms
                    fragmentProgramType?.prepareProgramTypes()
                } else if (position == 1) {
                    val fragmentFac = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":1") as? FragmentARRAVFacility

                } else if (position == 9) {
                    val fragmentFac = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":9") as? FragmentARRAVVehicleServices
                    fragmentFac?.prepareView()
                } else if (position == 10) {
                    val fragmentFac = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":10") as FragmentARRAVVehicles
                    fragmentFac.prepareView()

                } else if (position == 13) {
                    Log.v("PREPARE AFF --- ","Called form Pager")
                    val fragmentAff = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":13") as? FragmentARRAVAffliations
                    fragmentAff?.prepareAffiliations()
                } else if (position == 15) {
//                    Log.v("PREPARE AFF --- ","Called form Pager")
                    val fragmentComplaints = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":15") as? FragmentARRAVComplaints
                    fragmentComplaints?.prepareComplaints(false)
                }
            }

        })

        (activity as MainActivity).saveBtn.setOnClickListener {
            validateFormsInputs()
        }
    }

//    fun flagLoadNewDetailsRequired(){
//        (fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":2") as FragmentARRAVFacilityContinued).isFirstRun = true
//        (fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":4") as FragmentARRAVPersonnel).isFirstRun = true
//        (fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":8") as FragmentARRAVScopeOfService).isFirstRun = true
//        (fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":9") as FragmentARRAVVehicleServices).isFirstRun = true
//        (fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":10") as FragmentARRAVVehicles).isFirstRun = true
//    }

    fun validateFormsInputs(): Boolean {
        var isValidInput: Boolean = true
        var errorText: String = ""

        val fragmentVisitation = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":0") as FragmentARRAnualVisitation
        val fragmentFacility = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":1") as FragmentARRAVFacility
        val fragmentFacilityContinued = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":2") as FragmentARRAVFacilityContinued
        val fragmentFacilityLocation = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":3") as FragmentARRAVLocation
        val fragmentPersonnel = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":4") as FragmentARRAVPersonnel
        val fragmentRepairShopPortal = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":6") as FragmentARRAVRepairShopPortalAddendum
        val fragmentScopeOfServices = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":8") as FragmentARRAVScopeOfService
        val fragmentPrograms = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":11") as FragmentARRAVPrograms
        val fragmentFcServices = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":12") as FragmentARRAVFacilityServices
        val fragmentAffiliations = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":13") as FragmentARRAVAffliations
        val fragmentDefficiencies = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":14") as FragmentARRAVDeficiency

        if (!fragmentVisitation.validateInputs()) {
            isValidInput = !isValidInput
            errorText = " Please complete General Information Form"
//        } else if (!fragmentFacilityContinued.validateInputs()) {
//            isValidInput = !isValidInput
//            errorText += "Please complete Facility Continued Form"
        } else if (!fragmentFacilityLocation.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Location Information Form"
        } else if (!fragmentPersonnel.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Personnel Information Form"
        } else if (!fragmentRepairShopPortal.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Repair Shop Portal Addendum Form"
        } else if (!fragmentScopeOfServices.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Scope of Services Form"
        } else if (!fragmentPrograms.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Programs Form"
        } else if (!fragmentFcServices.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Facility Services Form"
        } else if (!fragmentAffiliations.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Affiliations Form"
        } else if (!fragmentDefficiencies.validateInputs()) {
            isValidInput = !isValidInput
            errorText += "Please complete Deficiencies Form"
        }

        val simpleAlert = AlertDialog.Builder(context).create()
        simpleAlert.setTitle("Validation Result")
        if (!errorText.isNullOrEmpty())
            simpleAlert.setMessage(errorText)
        else
            simpleAlert.setMessage("Validation Completed Succesfully  ... Proceed to submission? ")
        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { dialogInterface, i ->

        })
        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", { dialogInterface, i -> })

        simpleAlert.show()
        return isValidInput
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): FragmentAnnualVisitationPager {
            val fragment = FragmentAnnualVisitationPager()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    inner class SectionsPagerAdapter(fm: android.support.v4.app.FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): android.support.v4.app.Fragment? {
            var ft: android.support.v4.app.Fragment? = null
            when (position) {
//                 0 -> ft = FragmentARRAnualVisitation.newInstance("Test", "Test")
                 1 -> ft = FragmentARRAVFacility.newInstance(false)
                 2 -> ft = FragmentARRAVFacilityContinued.newInstance(false)
                 3 -> ft = FragmentARRAVLocation.newInstance(false)
                 4 -> ft = FragmentARRAVPersonnel.newInstance(false)
                 5 -> ft = FragmentARRAVAmOrderTracking.newInstance("Test", "Test")
                 6 -> ft = FragmentARRAVRepairShopPortalAddendum.newInstance("Test", "Test")
                 7 -> ft = FragmentARRAVVisitationTracking.newInstance("Test", "Test")
                 8 -> ft = FragmentARRAVScopeOfService.newInstance("Test", "Test")
                 9 -> ft = FragmentARRAVVehicleServices.newInstance("Test", "Test")
                10 -> ft = FragmentARRAVVehicles.newInstance("Test", "Test")
                11 -> ft = FragmentARRAVPrograms.newInstance("Test", "Test")
                12 -> ft = FragmentARRAVFacilityServices.newInstance("Test", "Test")
                13 -> ft = FragmentARRAVAffliations.newInstance("Test", "Test")
                14 -> ft = FragmentARRAVDeficiency.newInstance("Test", "Test")
                15 -> ft = FragmentARRAVComplaints.newInstance("Test", "Test")
            }
            return ft
        }

        override fun getCount(): Int {
            return 16
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "LATEST"
//                3 -> return "MY RHYMES"
//                2 -> return "POPULAR"
                1 -> return "TOP RATED"
            }
            return null
        }
    }



}// Required empty public constructor
