package com.inspection.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.inspection.R
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    var isValidating = false

    private var mTwoPane: Boolean = false
    var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

//        setSupportActionBar(toolbar)
//        toolbar.title = title



        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        setupRecyclerView(item_list)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.validate_submit_visitation, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.validate_and_submit_menu_item -> {
                validateAndSubmit()
            }
        }
        return true
    }

    private fun validateAndSubmit() {
        isValidating = true
        AnnualVisitationSingleton.getInstance().apply {
            //            if (facilityRepresentative.isNullOrBlank()){
            item_list.get(0).performClick()
//            }
        }
    }

    private val formTitles = arrayOf("Facility", "General Information", "RSP", "Contact Info", "Personnel", "Visitation Tracking", "Amendment Orders Tracking",
            "Scope Of Service", "General Information", "Vehicle Services", "Programs", "Facility Services", "Vehicles",
            "Affiliations", "Promotions", "Awards And Distinctions", "Others", "Deficiency", "Deficiency", "Complaints", "Complaints", "Billing", "Billing Plan", "Billing", "Payments",
            "Vendor Revenue", "Billing History", "Billing Adjustments", "Surveys", "CSI Results", "Software", "Comments", "Comments", "Photos", "Photos").toMutableList()

    private val formTitlesWithVisitations = arrayOf("VVVVisitation", "Visitation", "Facility", "General Information", "RSP", "Contact Info", "Personnel", "Visitation Tracking", "Amendment Orders Tracking",
            "Scope Of Service", "General Information", "Vehicle Services", "Programs", "Facility Services", "Vehicles",
            "Affiliations", "Promotions", "Awards And Distinctions", "Others", "Deficiency", "Deficiency", "Complaints", "Complaints", "Billing", "Billing Plan", "Billing", "Payments",
            "Vendor Revenue", "Billing History", "Billing Adjustments", "Surveys", "CSI Results", "Software", "Comments", "Comments", "Photos", "Photos").toMutableList()

//    private val formTitles = arrayOf("General Information", "Facility", "Facility Continued", "Location Info.", "Personnel", "Order Tracking",
//            "Portal Addendum", "Visitation Tracking", "Scope Of Service", "Vehicle Services", "Vehicles", "Programs", "Facility Services",
//            "Affliations", "Deficiency", "Complaints").toMutableList()

    private fun setupRecyclerView(recyclerView: RecyclerView) {
//        if (FragmentARRAnnualVisitationRecords.shouldShowVisitation){
        if (true) {
            recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, formTitlesWithVisitations, mTwoPane)
        } else {
            recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, formTitles, mTwoPane)
        }
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: ItemListActivity,
                                        private val mValues: List<String>,
                                        private val mTwoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private var selectedPosition = 1
        val formHeadersIndexes = arrayOf(0, 7, 17, 19, 21, 28, 31, 33)
        val formHeadersIndexesWithVisitation = arrayOf(0, 2, 9, 19, 21, 23, 30, 33, 35)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)


            return ViewHolder(view)
        }

        private var shouldNavigate = true

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val item = mValues[position]
            holder.mIdView.text = item
//            if (FragmentARRAnnualVisitationRecords.shouldShowVisitation) {
            if (true    ) {
                if (position in formHeadersIndexesWithVisitation) {
                    holder.mIdView.setTextColor(Color.WHITE)
                    holder.listLayout.setBackgroundColor(Color.GRAY)
                } else {
                    holder.mIdView.setTextColor(Color.BLACK)
                    holder.listLayout.setBackgroundColor(Color.WHITE)
                }
            } else {
                if (position in formHeadersIndexes) {
                    holder.mIdView.setTextColor(Color.WHITE)
                    holder.listLayout.setBackgroundColor(Color.GRAY)
                } else {
                    holder.mIdView.setTextColor(Color.BLACK)
                    holder.listLayout.setBackgroundColor(Color.WHITE)
                }
            }

            if (selectedPosition == position) {
                holder.listLayout.setBackgroundColor(mParentActivity.getColor(R.color.light_gray))
                var fragment: Fragment
                if (mTwoPane) {
                    Log.v("POSITION:  ", position.toString())
//                    if (FragmentARRAnnualVisitationRecords.shouldShowVisitation) {
                    if (true) {
                        when (position) {
                            1 -> fragment = FragmentVisitation()
                            3 -> fragment = FacilityGeneralInformationFragment.newInstance(mParentActivity.isValidating)
                            300 -> fragment = FacilityGeneralInformationFragment.newInstance(mParentActivity.isValidating)
                            5 -> fragment = FragmentARRAVFacilityContinued.newInstance(mParentActivity.isValidating)
                            100 -> fragment = FragmentARRAVLocation.newInstance(mParentActivity.isValidating)
                            6 -> fragment = FragmentARRAVPersonnel.newInstance(mParentActivity.isValidating)
                            8 -> fragment = FragmentARRAVAmOrderTracking.newInstance("test", "test")
                            4 -> fragment = FragmentARRAVRepairShopPortalAddendum.newInstance("test", "test")
                            7 -> fragment = FragmentARRAVVisitationTracking.newInstance("test", "test")
                            10 -> fragment = FragmentARRAVScopeOfService.newInstance("test", "test")
                            11 -> fragment = FragmentARRAVVehicleServices.newInstance("test", "test")
                            14 -> fragment = VehiclesFragmentInScopeOfServicesView.newInstance("test", "test")
                            12 -> fragment = FragmentARRAVPrograms.newInstance("test", "test")
                            13 -> fragment = FragmentARRAVFacilityServices.newInstance("test", "test")
                            15 -> fragment = FragmentARRAVAffliations.newInstance("test", "test")
                            20 ->  fragment = FragmentARRAVDeficiency.newInstance("test", "test")
                            22 -> fragment = FragmentARRAVComplaints.newInstance("test", "test")
                            24 -> fragment = FragmentAARAVBillingPlans.newInstance("test", "test")
                            25 -> fragment = FragmentAARAVBilling.newInstance("test", "test")
                            26 -> fragment = FragmentAARAVPayments.newInstance("test", "test")
                            27 -> fragment = FragmentAARAVVendorRevenue.newInstance("test", "test")
                            28 -> fragment = FragmentAARAVBillingHistory.newInstance("test", "test")
                            29 -> fragment = FragmentAARAVBillingAdjustment.newInstance("test", "test")
                            32 -> fragment = FragmentAARAVSoftware.newInstance("test", "test")
                        //this frag below PromotionsFragment is used instead the original one FragmentARRAVDeficiency > sherif yousry
                            16 -> fragment = PromotionsFragment.newInstance("test", "test")
                            17 -> fragment = AwardsAndDistinctionsFragment.newInstance("test", "test")
                            18 -> fragment = OthersFragment.newInstance("test", "test")
//                            19 -> fragment = FragmentARRAVComplaints.newInstance("test", "test")
                            34 -> fragment = FragmentAARAVComments.newInstance("test", "test")
                            36 -> fragment = FragmentAARAVPhotos.newInstance("test", "test")
                            else -> {
                                fragment = FragmentARRAnualVisitation.newInstance(mParentActivity.isValidating)
                                shouldNavigate = false
                            }
                        }

                    } else {
                        when (position) {
                            1 -> fragment = FacilityGeneralInformationFragment.newInstance(mParentActivity.isValidating)
                            300 -> fragment = FacilityGeneralInformationFragment.newInstance(mParentActivity.isValidating)
                            3 -> fragment = FragmentARRAVFacilityContinued.newInstance(mParentActivity.isValidating)
                            100 -> fragment = FragmentARRAVLocation.newInstance(mParentActivity.isValidating)
                            4 -> fragment = FragmentARRAVPersonnel.newInstance(mParentActivity.isValidating)
                            6 -> fragment = FragmentARRAVAmOrderTracking.newInstance("test", "test")
                            2 -> fragment = FragmentARRAVRepairShopPortalAddendum.newInstance("test", "test")
                            5 -> fragment = FragmentARRAVVisitationTracking.newInstance("test", "test")
                            8 -> fragment = FragmentARRAVScopeOfService.newInstance("test", "test")
                            9 -> fragment = FragmentARRAVVehicleServices.newInstance("test", "test")
                            12 -> fragment = VehiclesFragmentInScopeOfServicesView.newInstance("test", "test")
                            10 -> fragment = FragmentARRAVPrograms.newInstance("test", "test")
                            11 -> fragment = FragmentARRAVFacilityServices.newInstance("test", "test")
                            13 -> fragment = FragmentARRAVAffliations.newInstance("test", "test")
                        //this frag below PromotionsFragment is used instead the original one FragmentARRAVDeficiency > sherif yousry
                            14 -> fragment = PromotionsFragment.newInstance("test", "test")
                            15 -> fragment = AwardsAndDistinctionsFragment.newInstance("test", "test")
                            16 -> fragment = OthersFragment.newInstance("test", "test")
//                            17 -> fragment = FragmentARRAVComplaints.newInstance("test", "test")
                            22 -> fragment = FragmentAARAVBillingPlans.newInstance("test", "test")
                            23 -> fragment = FragmentAARAVBilling.newInstance("test", "test")
                            24 -> fragment = FragmentAARAVPayments.newInstance("test", "test")
                            25 -> fragment = FragmentAARAVVendorRevenue.newInstance("test", "test")
                            26 -> fragment = FragmentAARAVBillingHistory.newInstance("test", "test")
                            27 -> fragment = FragmentAARAVBillingAdjustment.newInstance("test", "test")
                            30 -> fragment = FragmentAARAVSoftware.newInstance("test", "test")
                            32 -> fragment = FragmentAARAVComments.newInstance("test", "test")
                            34 -> fragment = FragmentAARAVPhotos.newInstance("test", "test")
                            else -> {
                                fragment = FragmentARRAnualVisitation.newInstance(mParentActivity.isValidating)
                                shouldNavigate = false
                            }
                        }
                    }
                    if (shouldNavigate) {

                        mParentActivity.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit()
                        mParentActivity.isValidating = false
                    }else{
                        shouldNavigate = true
                    }
                }
            }

            holder.listLayout.setOnClickListener(View.OnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            })

//            with(holder.itemView) {
//                tag = position
//                setOnClickListener(mOnClickListener)
//                if (tag == 0) {
//                    performClick()
//                }
//            }
        }


        override fun getItemCount(): Int {
            return mValues.size
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
            val listLayout: LinearLayout = mView.listLayout
        }


    }
}
