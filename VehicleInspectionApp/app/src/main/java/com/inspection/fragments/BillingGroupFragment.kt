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
import com.inspection.FormsActivity

import com.inspection.R
import com.inspection.databinding.BillingGroupLayoutBinding
import com.inspection.databinding.FragmentAwardsAndDistinctionsBinding
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
//import kotlinx.android.synthetic.main.billing_group_layout.*
//import kotlinx.android.synthetic.main.facility_group_layout.*
//import kotlinx.android.synthetic.main.fragment_aarav_billing.*
//import kotlinx.android.synthetic.main.surveys_group_layout.*
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
class BillingGroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var revSourceList = ArrayList<TypeTablesModel.revenueSourceType>()
    private var revSourceArray = ArrayList<String>()

    private var _binding: BillingGroupLayoutBinding? = null
    private val binding get() = _binding!!

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
        return inflater.inflate(R.layout.billing_group_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BillingGroupLayoutBinding.bind(view)
        var fragment = FragmentAARAVBillingPlans.newInstance("","")
        requireFragmentManager().beginTransaction()
                .replace(R.id.facilityGroupDetailsFragment, fragment)
                .commit()
        updateSelectedIndicator(R.id.billingPlanButton)

        binding.billingPlanButton.setOnClickListener {
            var fragment = FragmentAARAVBillingPlans.newInstance("","")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingPlanButton)
        }

        binding.billingButton.setOnClickListener {
            var fragment = FragmentAARAVBilling.newInstance("", "")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingButton)
        }

        binding.paymentsButton.setOnClickListener {
            var fragment = FragmentAARAVPayments.newInstance("","")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.paymentsButton)
        }

        binding.vendorRevenueButton.setOnClickListener {
            var fragment = FragmentAARAVVendorRevenue.newInstance("","")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.vendorRevenueButton)
        }

        binding.billingHistoryButton.setOnClickListener {
            var fragment = FragmentAARAVBillingHistory.newInstance("","")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingHistoryButton)
        }

        binding.billingAdjustmentButton.setOnClickListener {
            var fragment = FragmentAARAVBillingAdjustment.newInstance("","")
            requireFragmentManager().beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingAdjustmentButton)
        }

    }


    fun updateSelectedIndicator(selectedViewId: Int){
        when(selectedViewId){
            R.id.billingPlanButton->{
                binding.billingPlanSelectedIndicator.visibility = View.VISIBLE
                binding.billingSelectedIndicator.visibility = View.INVISIBLE
                binding.paymentsSelectedIndicator.visibility = View.INVISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.INVISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingButton->{
                binding.billingPlanSelectedIndicator.visibility = View.INVISIBLE
                binding.billingSelectedIndicator.visibility = View.VISIBLE
                binding.paymentsSelectedIndicator.visibility = View.INVISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.INVISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.paymentsButton->{
                binding.billingPlanSelectedIndicator.visibility = View.INVISIBLE
                binding.billingSelectedIndicator.visibility = View.INVISIBLE
                binding.paymentsSelectedIndicator.visibility = View.VISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.INVISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vendorRevenueButton->{
                binding.billingPlanSelectedIndicator.visibility = View.INVISIBLE
                binding.billingSelectedIndicator.visibility = View.INVISIBLE
                binding.paymentsSelectedIndicator.visibility = View.INVISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.VISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.INVISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingHistoryButton->{
                binding.billingPlanSelectedIndicator.visibility = View.INVISIBLE
                binding.billingSelectedIndicator.visibility = View.INVISIBLE
                binding.paymentsSelectedIndicator.visibility = View.INVISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.VISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingAdjustmentButton->{
                binding.billingPlanSelectedIndicator.visibility = View.INVISIBLE
                binding.billingSelectedIndicator.visibility = View.INVISIBLE
                binding.paymentsSelectedIndicator.visibility = View.INVISIBLE
                binding.vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                binding.billingHistorySelectedIndicator.visibility = View.INVISIBLE
                binding.billingAdjustmentSelectedIndicator.visibility = View.VISIBLE
            }
        }

//        IndicatorsDataModel.getInstance().validateBillingSection()
        refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun refreshTabIndicators() {
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingVisited) binding.billingButton.setTextColor(Color.parseColor("#26C3AA")) else binding.billingButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingPlanVisited) binding.billingPlanButton.setTextColor(Color.parseColor("#26C3AA")) else binding.billingPlanButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustmentsVisited) binding.billingAdjustmentButton.setTextColor(Color.parseColor("#26C3AA")) else binding.billingAdjustmentButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingHistoryVisited) binding.billingHistoryButton.setTextColor(Color.parseColor("#26C3AA")) else binding.billingHistoryButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].PaymentsVisited) binding.paymentsButton.setTextColor(Color.parseColor("#26C3AA")) else binding.paymentsButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenueVisited) binding.vendorRevenueButton.setTextColor(Color.parseColor("#26C3AA")) else binding.vendorRevenueButton.setTextColor(Color.parseColor("#A42600"))
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
