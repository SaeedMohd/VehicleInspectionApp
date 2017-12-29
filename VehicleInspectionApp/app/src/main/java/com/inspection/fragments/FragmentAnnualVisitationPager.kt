package com.inspection.fragments

import android.R.attr.fragment

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
import android.R.attr.tag
import android.app.AlertDialog
import android.util.Log


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

//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        if (mListener != null) {
//            mListener!!.onFragmentInteraction(uri)
//        }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager!!)
        (activity as MainActivity).supportActionBar!!.title = "Forms"
        // Set up the ViewPager with the sections adapter.
//        mViewPager=container
//        mViewPager.adapter = mSectionsPagerAdapter

//        container.let {
//            it as ViewPager
//            (it.adapter as SectionsPagerAdapter)
//        }

//        val tabLayout = sliding_tabs as TableLayout

//        tabs.set(container)

        mSectionsPagerAdapter = SectionsPagerAdapter (fragmentManager!!)
        container.offscreenPageLimit = 16
        container.adapter = mSectionsPagerAdapter
//        tabs.setupWithViewPager(container)

        (activity as MainActivity).saveBtn.setOnClickListener{

//            Toast.makeText(context,"Validation Result : " + validateFormsInputs(),Toast.LENGTH_LONG).show()
            validateFormsInputs()
        }

    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

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
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    fun validateFormsInputs(): Boolean {
        var isValidInput : Boolean = true
        var errorText : String = ""

        val fragmentVisitation = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":0" ) as FragmentARRAnualVisitation
        val fragmentFacility = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":1" ) as FragmentARRAVFacility
        val fragmentFacilityContinued = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":2" ) as FragmentARRAVFacilityContinued
        val fragmentFacilityLocation = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":3" ) as FragmentARRAVLocation
        val fragmentPersonnel = fragmentManager!!.findFragmentByTag("android:switcher:" + R.id.container + ":4" ) as FragmentARRAVPersonnel


//        if (!fragmentVisitation.validateInputs()) {
//                isValidInput = !isValidInput
//                errorText = " Please complete General Information Form"
//        }
//        else if (!fragmentFacility.validateInputs()){
//            isValidInput = !isValidInput
//            errorText += "Please complete Facility Form"
//        }
//        else if (!fragmentFacilityContinued.validateInputs()){
//            isValidInput = !isValidInput
//            errorText += "Please complete Facility Continued Form"
//        }
//        else if (!fragmentFacilityLocation.validateInputs()){
//            isValidInput = !isValidInput
//            errorText += "Please complete Location Information Form"
//        }
        if (!fragmentPersonnel.validateInputs()){
            isValidInput = !isValidInput
            errorText += "Please complete Personnel Information Form"
        }

        val simpleAlert = AlertDialog.Builder(context).create()
        simpleAlert.setTitle("Validation Result")
        if (!errorText.isNullOrEmpty())
            simpleAlert.setMessage(errorText)
        else
            simpleAlert.setMessage("Validation Completed Succesfully  ... Proceed to submission? ")
        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
            dialogInterface, i ->
            Toast.makeText(context, "You clicked on OK", Toast.LENGTH_SHORT).show()
        })
        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",{dialogInterface, i -> })

        simpleAlert.show()
        return isValidInput
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
         * @return A new instance of fragment FragmentAnnualVisitationPager.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FragmentAnnualVisitationPager {
            val fragment = FragmentAnnualVisitationPager()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    inner class SectionsPagerAdapter(fm: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): android.support.v4.app.Fragment? {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            var ft: android.support.v4.app.Fragment? = null
            when (position) {
                0 -> ft = FragmentARRAnualVisitation.newInstance("Test","Test")
                1 -> ft = FragmentARRAVFacility.newInstance("Test","Test")
                2 -> ft = FragmentARRAVFacilityContinued.newInstance("Test","Test")
                3 -> ft = FragmentARRAVLocation.newInstance("Test","Test")
                4 -> ft = FragmentARRAVPersonnel.newInstance("Test", "Test")
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
//                3 -> {
//                    //ft=FeaturedFragment.newInstance("Test");
//                    //ft=SubmittedFragment.newInstance("Test","");
//                    Utils.strFragmentType.equals("SubmittedPoem")
//                    ft = MainFragment.newInstance("SubmittedPoem")
//                }
//                2 -> ft = PopularFragment.newInstance("Test", "")
//                1 -> ft = TopFragment.newInstance("Test")
//                else -> ft = MainFragment.newInstance("Test")
            }

            return ft
        }

        override fun getCount(): Int {
            // Show 3 total pages.
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
