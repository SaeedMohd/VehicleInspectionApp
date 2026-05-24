package com.inspection.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.inspection.FormsActivity
import com.inspection.R
import com.inspection.Utils.Utility
import com.inspection.databinding.ScopeOfServiceGroupLayoutBinding
import com.inspection.fragmentsNames
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.VisitationTypes

class ScopeOfServiceGroupFragment : Fragment(), HasTabIndicators {

    private var _binding: ScopeOfServiceGroupLayoutBinding? = null
    private val binding get() = _binding!!
    private var currentTabPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scope_of_service_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ScopeOfServiceGroupLayoutBinding.bind(view)

        listOf(
            "General Information",
            "Vehicle Services",
            "Programs",
            "Facility Services",
            "Vehicles",
            "Affiliations",
            "Promotions"
        ).forEach { addTab(binding.sosTabLayout, it) }

        (activity as FormsActivity).currentFragment = fragmentsNames.SoSGeneralInfo.toString()
        (activity as FormsActivity).saveRequired = false
        navigateToTab(0)

        binding.sosTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if ((activity as FormsActivity).preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(activity)
                    binding.sosTabLayout.post {
                        binding.sosTabLayout.selectTab(binding.sosTabLayout.getTabAt(currentTabPosition))
                    }
                    return
                }
                currentTabPosition = tab.position
                navigateToTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun navigateToTab(position: Int) {
        val (fragment, tag, fragmentName) = when (position) {
            0 -> Triple(FragmentARRAVScopeOfService.newInstance("", ""), null, fragmentsNames.SoSGeneralInfo)
            1 -> Triple(FragmentARRAVVehicleServices.newInstance("", ""), null, fragmentsNames.SoSVehicleServices)
            2 -> Triple(FragmentARRAVPrograms.newInstance("", ""), "FragmentARRAVPrograms", fragmentsNames.SoSPrograms)
            3 -> Triple(FragmentARRAVFacilityServices.newInstance("", ""), "FragmentARRAVFacilityServices", fragmentsNames.SoSFacilityServices)
            4 -> Triple(VehiclesFragmentInScopeOfServicesView.newInstance("", ""), null, fragmentsNames.SoSVehicles)
            5 -> Triple(FragmentARRAVAffliations.newInstance("", ""), "FragmentARRAVAffliations", fragmentsNames.SoSAffiliations)
            6 -> Triple(FragmentAARPromotions.newInstance("", ""), null, fragmentsNames.SoSPromotions)
            else -> return
        }
        requireFragmentManager().beginTransaction()
            .replace(R.id.facilityGroupDetailsFragment, fragment, tag)
            .commit()
        (activity as FormsActivity).currentFragment = fragmentName.toString()
        (activity as FormsActivity).saveRequired = false
        refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    override fun refreshTabIndicators() {
        val exempt = isExemptFromVisit()
        val sos = IndicatorsDataModel.getInstance().tblScopeOfServices[0]
        setTabColor(0, exempt || sos.GeneralInfoVisited)
        setTabColor(1, exempt || sos.VehicleServicesVisited)
        setTabColor(2, exempt || sos.ProgramsVisited)
        setTabColor(3, exempt || sos.FacilityServicesVisited)
        setTabColor(4, exempt || sos.VehiclesVisited)
        setTabColor(5, exempt || sos.AffiliationsVisited)
        setTabColor(6, exempt || sos.PromotionsVisited)
    }

    private fun isExemptFromVisit(): Boolean {
        val type = FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType
        return type == VisitationTypes.Deficiency || type == VisitationTypes.AdHoc
    }

    private fun addTab(tabLayout: TabLayout, text: String) {
        val tab = tabLayout.newTab()
        val tv = layoutInflater.inflate(R.layout.tab_group_item, null, false) as TextView
        tv.text = text
        tab.customView = tv
        tabLayout.addTab(tab)
    }

    private fun setTabColor(index: Int, visited: Boolean) {
        val color = if (visited) Color.parseColor("#26C3AA") else Color.parseColor("#A42600")
        (binding.sosTabLayout.getTabAt(index)?.customView as? TextView)?.setTextColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
    }
}
