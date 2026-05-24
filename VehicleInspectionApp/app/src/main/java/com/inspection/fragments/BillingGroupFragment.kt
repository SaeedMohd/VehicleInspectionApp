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
import com.inspection.databinding.BillingGroupLayoutBinding
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.VisitationTypes

class BillingGroupFragment : Fragment(), HasTabIndicators {

    private var _binding: BillingGroupLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.billing_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BillingGroupLayoutBinding.bind(view)

        listOf(
            "Billing Plan",
            "Billing",
            "Payments",
            "Vendor Revenue",
            "Billing History",
            "Billing Adjustment"
        ).forEach { addTab(binding.billingTabLayout, it) }

        navigateToTab(0)

        binding.billingTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                navigateToTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun navigateToTab(position: Int) {
        val fragment = when (position) {
            0 -> FragmentAARAVBillingPlans.newInstance("", "")
            1 -> FragmentAARAVBilling.newInstance("", "")
            2 -> FragmentAARAVPayments.newInstance("", "")
            3 -> FragmentAARAVVendorRevenue.newInstance("", "")
            4 -> FragmentAARAVBillingHistory.newInstance("", "")
            5 -> FragmentAARAVBillingAdjustment.newInstance("", "")
            else -> return
        }
        requireFragmentManager().beginTransaction()
            .replace(R.id.facilityGroupDetailsFragment, fragment)
            .commit()
        refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    override fun refreshTabIndicators() {
        val exempt = isExemptFromVisit()
        val billing = IndicatorsDataModel.getInstance().tblBilling[0]
        setTabColor(0, exempt || billing.BillingPlanVisited)
        setTabColor(1, exempt || billing.BillingVisited)
        setTabColor(2, exempt || billing.PaymentsVisited)
        setTabColor(3, exempt || billing.VendorRevenueVisited)
        setTabColor(4, exempt || billing.BillingHistoryVisited)
        setTabColor(5, exempt || billing.BillingAdjustmentsVisited)
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
        (binding.billingTabLayout.getTabAt(index)?.customView as? TextView)?.setTextColor(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
