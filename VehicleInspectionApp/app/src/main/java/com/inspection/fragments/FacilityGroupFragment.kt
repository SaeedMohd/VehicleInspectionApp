package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.inspection.R
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.facility_group_layout.view.*
import kotlinx.android.synthetic.main.fragment_aarav_billing.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentAARAVBilling.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentAARAVBilling.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FacilityGroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var revSourceList = ArrayList<TypeTablesModel.revenueSourceType>()
    private var revSourceArray = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.facility_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragment = FragmentARRAVFacility.newInstance(false)
        fragmentManager!!.beginTransaction()
                .replace(R.id.facilityGroupDetailsFragment, fragment)
                .commit()
        updateSelectedIndicator(R.id.generalInformationButton)

        generalInformationButton.setOnClickListener {
            var fragment = FragmentARRAVFacility.newInstance(false)
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.generalInformationButton)
        }

        rspButton.setOnClickListener {
            var fragment = FragmentARRAVRepairShopPortalAddendum.newInstance("", "")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.rspButton)
        }

        contactInfoButton.setOnClickListener {
            var fragment = FragmentARRAVLocation.newInstance(false)
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.contactInfoButton)
        }

        personnelButton.setOnClickListener {
            var fragment = FragmentARRAVPersonnel.newInstance(false)
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.personnelButton)
        }

//        visitationTrackingButton.setOnClickListener {
//            var fragment = FragmentARRAVVisitationTracking.newInstance("","")
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.facilityGroupDetailsFragment, fragment)
//                    .commit()
//            updateSelectedIndicator(R.id.visitationTrackingButton)
//        }

        amendmentOrdersTrackingButton.setOnClickListener {
            var fragment = FragmentARRAVAmOrderTracking.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.amendmentOrdersTrackingButton)
        }
    }

    fun updateSelectedIndicator(selectedViewId: Int){
        when(selectedViewId){
            R.id.generalInformationButton->{
                generalInformationSelectedIndicator.visibility = View.VISIBLE
                        rspSelectedIndicator.visibility = View.INVISIBLE
                        contactInfoSelectedIndicator.visibility = View.INVISIBLE
                        personnelSelectedIndicator.visibility = View.INVISIBLE
                        amendmentOrdersTrackingSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.rspButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                rspSelectedIndicator.visibility = View.VISIBLE
                contactInfoSelectedIndicator.visibility = View.INVISIBLE
                personnelSelectedIndicator.visibility = View.INVISIBLE
                amendmentOrdersTrackingSelectedIndicator.visibility = View.INVISIBLE
            }
            
            R.id.contactInfoButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                rspSelectedIndicator.visibility = View.INVISIBLE
                contactInfoSelectedIndicator.visibility = View.VISIBLE
                personnelSelectedIndicator.visibility = View.INVISIBLE
                amendmentOrdersTrackingSelectedIndicator.visibility = View.INVISIBLE
            }
            
            R.id.personnelButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                rspSelectedIndicator.visibility = View.INVISIBLE
                contactInfoSelectedIndicator.visibility = View.INVISIBLE
                personnelSelectedIndicator.visibility = View.VISIBLE
                amendmentOrdersTrackingSelectedIndicator.visibility = View.INVISIBLE
            }
            
//            R.id.visitationTrackingButton->{
//                generalInformationSelectedIndicator.visibility = View.INVISIBLE
//                rspSelectedIndicator.visibility = View.INVISIBLE
//                contactInfoSelectedIndicator.visibility = View.INVISIBLE
//                personnelSelectedIndicator.visibility = View.INVISIBLE
//                amendmentOrdersTrackingSelectedIndicator.visibility = View.INVISIBLE
//            }
            
            R.id.amendmentOrdersTrackingButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                rspSelectedIndicator.visibility = View.INVISIBLE
                contactInfoSelectedIndicator.visibility = View.INVISIBLE
                personnelSelectedIndicator.visibility = View.INVISIBLE
                amendmentOrdersTrackingSelectedIndicator.visibility = View.VISIBLE
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAARAVBilling.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAARAVBilling().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
