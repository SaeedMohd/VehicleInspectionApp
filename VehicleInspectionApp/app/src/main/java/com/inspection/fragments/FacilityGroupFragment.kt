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
import com.inspection.databinding.FacilityGroupLayoutBinding
import com.inspection.fragmentsNames
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.VisitationTypes

class FacilityGroupFragment : Fragment(), HasTabIndicators {

    private var _binding: FacilityGroupLayoutBinding? = null
    private val binding get() = _binding!!
    private var currentTabPosition = 0
    private var isRevertingTab = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.facility_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FacilityGroupLayoutBinding.bind(view)

        listOf(
            "General Information",
            "RSP Tracking",
            "Location & Contact Info",
            "Personnel",
            "Visitation Tracking"
        ).forEach { addTab(binding.facilityTabLayout, it) }

        (activity as FormsActivity).currentFragment = fragmentsNames.FacilityGeneralInfo.toString()
        (activity as FormsActivity).saveRequired = false
        navigateToTab(0)

        binding.facilityTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (isRevertingTab) return
                if ((activity as FormsActivity).preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(activity)
                    isRevertingTab = true
                    binding.facilityTabLayout.post {
                        binding.facilityTabLayout.selectTab(binding.facilityTabLayout.getTabAt(currentTabPosition))
                        isRevertingTab = false
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
            0 -> Triple(FacilityGeneralInformationFragment.newInstance(false), null, fragmentsNames.FacilityGeneralInfo)
            1 -> Triple(FragmentARRAVRepairShopPortalAddendum.newInstance("", ""), null, fragmentsNames.FacilityRSP)
            2 -> Triple(FragmentARRAVLocation.newInstance(false), "FragmentARRAVLocation", fragmentsNames.FacilityContactInfo)
            3 -> Triple(FragmentARRAVPersonnel.newInstance(false), "FragmentARRAVPersonnel", fragmentsNames.FacilityPersonnel)
            4 -> Triple(VisitationTrackingSubFragment.newInstance("", ""), null, fragmentsNames.VisitationTracking)
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
        setTabColor(0, exempt || IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited)
        setTabColor(1, exempt || IndicatorsDataModel.getInstance().tblFacility[0].RSPVisited)
        setTabColor(2, exempt || IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited)
        setTabColor(3, exempt || IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited)
        setTabColor(4, true)
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
        (binding.facilityTabLayout.getTabAt(index)?.customView as? TextView)?.setTextColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
