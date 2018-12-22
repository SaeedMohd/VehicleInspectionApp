package com.inspection.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.fragmentsNames
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
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
        (activity as FormsActivity).currentFragment= fragmentsNames.SoSGeneralInfo.toString()
        (activity as FormsActivity).saveRequired = false
        updateSelectedIndicator(R.id.generalInformationButton)

        sosgeneralInformationButton.setOnClickListener {
            var fragment = FragmentARRAVScopeOfService.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSGeneralInfo.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.generalInformationButton)
        }

        vehicleServicesButton.setOnClickListener {
            var fragment = FragmentARRAVVehicleServices.newInstance("", "")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSVehicleServices.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.vehicleServicesButton)
        }

        programsButton.setOnClickListener {
            var fragment = FragmentARRAVPrograms.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSPrograms.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.programsButton)
        }

        facilityServicesButton.setOnClickListener {
            var fragment = FragmentARRAVFacilityServices.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSFacilityServices.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.facilityServicesButton)
        }

        vehiclesButton.setOnClickListener {
            var fragment = VehiclesFragmentInScopeOfServicesView.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSVehicles.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.vehiclesButton)
        }

        AffiliationsButton.setOnClickListener {
            var fragment = FragmentARRAVAffliations.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            (activity as FormsActivity).currentFragment= fragmentsNames.SoSAffiliations.toString()
            (activity as FormsActivity).saveRequired = false
            updateSelectedIndicator(R.id.AffiliationsButton)
        }

//        promotionsButton.setOnClickListener {
//            var fragment = PromotionsFragment.newInstance("","")
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.facilityGroupDetailsFragment, fragment)
//                    .commit()
//            (activity as FormsActivity).currentFragment= fragmentsNames.SoSPromotions.toString()
//            (activity as FormsActivity).saveRequired = false
//            updateSelectedIndicator(R.id.promotionsButton)
//        }
//
//        awardsAndDistinctionsButton.setOnClickListener {
//            var fragment = AwardsAndDistinctionsFragment.newInstance("","")
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.facilityGroupDetailsFragment, fragment)
//                    .commit()
//            (activity as FormsActivity).currentFragment= fragmentsNames.SoSAwards.toString()
//            (activity as FormsActivity).saveRequired = false
//            updateSelectedIndicator(R.id.awardsAndDistinctionsButton)
//        }
//
//        otherButton.setOnClickListener {
//            var fragment = OthersFragment.newInstance("","")
//            fragmentManager!!.beginTransaction()
//                    .replace(R.id.facilityGroupDetailsFragment, fragment)
//                    .commit()
//            (activity as FormsActivity).currentFragment= fragmentsNames.SoSOthers.toString()
//            (activity as FormsActivity).saveRequired = false
//            updateSelectedIndicator(R.id.otherButton)
//        }


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
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vehicleServicesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.VISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.programsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.VISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.facilityServicesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.VISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vehiclesButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.VISIBLE
                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.AffiliationsButton->{
                generalInformationSelectedIndicator.visibility = View.INVISIBLE
                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
                programsSelectedIndicator.visibility = View.INVISIBLE
                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
                vehiclesSelectedIndicator.visibility = View.INVISIBLE
                affiliationsSelectedIndicator.visibility = View.VISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
            }

//            R.id.promotionsButton->{
//                generalInformationSelectedIndicator.visibility = View.INVISIBLE
//                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
//                programsSelectedIndicator.visibility = View.INVISIBLE
//                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
//                vehiclesSelectedIndicator.visibility = View.INVISIBLE
//                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.VISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
//            }
//
//            R.id.awardsAndDistinctionsButton->{
//                generalInformationSelectedIndicator.visibility = View.INVISIBLE
//                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
//                programsSelectedIndicator.visibility = View.INVISIBLE
//                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
//                vehiclesSelectedIndicator.visibility = View.INVISIBLE
//                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.VISIBLE
//                otherSelectedIndicator.visibility = View.INVISIBLE
//            }
//
//            R.id.otherButton->{
//                generalInformationSelectedIndicator.visibility = View.INVISIBLE
//                vehicleServicesSelectedIndicator.visibility = View.INVISIBLE
//                programsSelectedIndicator.visibility = View.INVISIBLE
//                facilityServicesSelectedIndicator.visibility = View.INVISIBLE
//                vehiclesSelectedIndicator.visibility = View.INVISIBLE
//                affiliationsSelectedIndicator.visibility = View.INVISIBLE
//                promotionsSelectedIndicator.visibility = View.INVISIBLE
//                awardsAndDistinctionsSelectedIndicator.visibility = View.INVISIBLE
//                otherSelectedIndicator.visibility = View.VISIBLE
//            }
        }
//        IndicatorsDataModel.getInstance().validateSoSSectionVisited()
        refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun refreshTabIndicators() {
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfoVisited) sosgeneralInformationButton.setTextColor(Color.parseColor("#26C3AA")) else sosgeneralInformationButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServicesVisited) facilityServicesButton.setTextColor(Color.parseColor("#26C3AA")) else facilityServicesButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited) programsButton.setTextColor(Color.parseColor("#26C3AA")) else programsButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].AffiliationsVisited) AffiliationsButton.setTextColor(Color.parseColor("#26C3AA")) else AffiliationsButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehicleServicesVisited) vehicleServicesButton.setTextColor(Color.parseColor("#26C3AA")) else vehicleServicesButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited) vehiclesButton.setTextColor(Color.parseColor("#26C3AA")) else vehiclesButton.setTextColor(Color.parseColor("#A42600"))
//        promotionsButton.setTextColor(Color.parseColor("#26C3AA"))
//        awardsAndDistinctionsButton.setTextColor(Color.parseColor("#26C3AA"))
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

        try {
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
        } catch (e: Exception) {
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
