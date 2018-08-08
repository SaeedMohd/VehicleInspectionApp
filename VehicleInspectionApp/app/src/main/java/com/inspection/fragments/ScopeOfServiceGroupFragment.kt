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
import android.widget.Toast

import com.inspection.R
import com.inspection.model.FacilityDataModel
import com.inspection.model.TypeTablesModel
import kotlinx.android.synthetic.main.scope_of_service_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_billing.*
import kotlinx.android.synthetic.main.fragment_arrav_scope_of_service.*
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
class ScopeOfServiceGroupFragment : Fragment() {
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
        return inflater.inflate(R.layout.scope_of_service_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragment = FragmentARRAVScopeOfService.newInstance("","")
        fragmentManager!!.beginTransaction()
                .replace(R.id.facilityGroupDetailsFragment, fragment)
                .commit()
        updateSelectedIndicator(R.id.generalInformationButton)

        generalInformationButton.setOnClickListener {
            var fragment = FragmentARRAVScopeOfService.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.generalInformationButton)
        }

        vehicleServicesButton.setOnClickListener {
            var fragment = FragmentARRAVVehicleServices.newInstance("", "")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.vehicleServicesButton)
        }

        programsButton.setOnClickListener {
            var fragment = FragmentARRAVPrograms.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.programsButton)
        }

        facilityServicesButton.setOnClickListener {
            var fragment = FragmentARRAVFacilityServices.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.facilityServicesButton)
        }

        vehiclesButton.setOnClickListener {
            var fragment = VehiclesFragmentInScopeOfServicesView.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.vehiclesButton)
        }

        AffiliationsButton.setOnClickListener {
            var fragment = FragmentARRAVAffliations.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.AffiliationsButton)
        }

        promotionsButton.setOnClickListener {
            var fragment = PromotionsFragment.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.promotionsButton)
        }

        awardsAndDistinctionsButton.setOnClickListener {
            var fragment = AwardsAndDistinctionsFragment.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.awardsAndDistinctionsButton)
        }

        otherButton.setOnClickListener {
            var fragment = OthersFragment.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.otherButton)
        }


    }



    fun updateSelectedIndicator(selectedViewId: Int){
        when(selectedViewId){
            R.id.generalInformationButton->{
                generalInformationSelectedIndicator.visibility = View.VISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vehicleServicesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.VISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.programsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.VISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.facilityServicesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.VISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vehiclesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.VISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.AffiliationsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.VISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.promotionsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.VISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.awardsAndDistinctionsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.VISIBLE
                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.otherButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
                promotionsSelectedIndicator.visibility = View.INVISIBLE
                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
                otherSelectedIndicator.visibility = View.VISIBLE
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

    override fun onPause() {
        super.onPause()


        FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = true


        if (fixedLaborRateEditText.text.toString().isNullOrEmpty()) {
            FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = false
        }

        if (diagnosticRateEditText.text.toString().isNullOrEmpty()) {
            FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = false
        }


        if (laborRateMatrixMaxEditText.text.toString().isNullOrEmpty()) {
            FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = false
        }

        if (laborRateMatrixMinEditText.text.toString().isNullOrEmpty()) {
            FragmentARRAVScopeOfService.scopeOfServiceValideForOtherFragmentToTest = false
        }


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
