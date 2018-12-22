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
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.TypeTablesModel
import com.inspection.model.VisitationTypes
import kotlinx.android.synthetic.main.billing_group_layout.*
import kotlinx.android.synthetic.main.facility_group_layout.*
import kotlinx.android.synthetic.main.fragment_aarav_billing.*
import kotlinx.android.synthetic.main.surveys_group_layout.*
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

        var fragment = FragmentAARAVBillingPlans.newInstance("","")
        fragmentManager!!.beginTransaction()
                .replace(R.id.facilityGroupDetailsFragment, fragment)
                .commit()
        updateSelectedIndicator(R.id.billingPlanButton)

        billingPlanButton.setOnClickListener {
            var fragment = FragmentAARAVBillingPlans.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingPlanButton)
        }

        billingButton.setOnClickListener {
            var fragment = FragmentAARAVBilling.newInstance("", "")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingButton)
        }

        paymentsButton.setOnClickListener {
            var fragment = FragmentAARAVPayments.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.paymentsButton)
        }

        vendorRevenueButton.setOnClickListener {
            var fragment = FragmentAARAVVendorRevenue.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.vendorRevenueButton)
        }

        billingHistoryButton.setOnClickListener {
            var fragment = FragmentAARAVBillingHistory.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingHistoryButton)
        }

        billingAdjustmentButton.setOnClickListener {
            var fragment = FragmentAARAVBillingAdjustment.newInstance("","")
            fragmentManager!!.beginTransaction()
                    .replace(R.id.facilityGroupDetailsFragment, fragment)
                    .commit()
            updateSelectedIndicator(R.id.billingAdjustmentButton)
        }

    }


    fun updateSelectedIndicator(selectedViewId: Int){
        when(selectedViewId){
            R.id.billingPlanButton->{
                billingPlanSelectedIndicator.visibility = View.VISIBLE
                billingSelectedIndicator.visibility = View.INVISIBLE
                paymentsSelectedIndicator.visibility = View.INVISIBLE
                vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                billingHistorySelectedIndicator.visibility = View.INVISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingButton->{
                billingPlanSelectedIndicator.visibility = View.INVISIBLE
                billingSelectedIndicator.visibility = View.VISIBLE
                paymentsSelectedIndicator.visibility = View.INVISIBLE
                vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                billingHistorySelectedIndicator.visibility = View.INVISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.paymentsButton->{
                billingPlanSelectedIndicator.visibility = View.INVISIBLE
                billingSelectedIndicator.visibility = View.INVISIBLE
                paymentsSelectedIndicator.visibility = View.VISIBLE
                vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                billingHistorySelectedIndicator.visibility = View.INVISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.vendorRevenueButton->{
                billingPlanSelectedIndicator.visibility = View.INVISIBLE
                billingSelectedIndicator.visibility = View.INVISIBLE
                paymentsSelectedIndicator.visibility = View.INVISIBLE
                vendorRevenueSelectedIndicator.visibility = View.VISIBLE
                billingHistorySelectedIndicator.visibility = View.INVISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingHistoryButton->{
                billingPlanSelectedIndicator.visibility = View.INVISIBLE
                billingSelectedIndicator.visibility = View.INVISIBLE
                paymentsSelectedIndicator.visibility = View.INVISIBLE
                vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                billingHistorySelectedIndicator.visibility = View.VISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.INVISIBLE
            }

            R.id.billingAdjustmentButton->{
                billingPlanSelectedIndicator.visibility = View.INVISIBLE
                billingSelectedIndicator.visibility = View.INVISIBLE
                paymentsSelectedIndicator.visibility = View.INVISIBLE
                vendorRevenueSelectedIndicator.visibility = View.INVISIBLE
                billingHistorySelectedIndicator.visibility = View.INVISIBLE
                billingAdjustmentSelectedIndicator.visibility = View.VISIBLE
            }
        }

//        IndicatorsDataModel.getInstance().validateBillingSection()
        refreshTabIndicators()
        (activity as FormsActivity).refreshMenuIndicatorsForVisitedScreens()
    }

    fun refreshTabIndicators() {
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingVisited) billingButton.setTextColor(Color.parseColor("#26C3AA")) else billingButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingPlanVisited) billingPlanButton.setTextColor(Color.parseColor("#26C3AA")) else billingPlanButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustmentsVisited) billingAdjustmentButton.setTextColor(Color.parseColor("#26C3AA")) else billingAdjustmentButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].BillingHistoryVisited) billingHistoryButton.setTextColor(Color.parseColor("#26C3AA")) else billingHistoryButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].PaymentsVisited) paymentsButton.setTextColor(Color.parseColor("#26C3AA")) else paymentsButton.setTextColor(Color.parseColor("#A42600"))
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenueVisited) vendorRevenueButton.setTextColor(Color.parseColor("#26C3AA")) else vendorRevenueButton.setTextColor(Color.parseColor("#A42600"))
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
