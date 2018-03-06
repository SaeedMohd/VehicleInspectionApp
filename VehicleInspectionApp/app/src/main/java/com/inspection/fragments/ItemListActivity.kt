package com.inspection.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.inspection.R

import com.inspection.model.AAAFacility
import com.inspection.singletons.AnnualVisitationSingleton
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*

import kotlinx.android.synthetic.main.item_list.*

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
    private var mTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title



        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        setupRecyclerView(item_list)

    }

    private val formTitles = arrayOf("General Information", "Facility", "Facility Continued", "Location Info.", "Personnel", "Order Tracking",
            "Portal Addendum", "Visitation Tracking", "Scope Of Service", "Vehicle Services", "Vehicles", "Programs", "Facility Services",
            "Affliations", "Deficiency", "Complaints").toMutableList()

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, formTitles, mTwoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: ItemListActivity,
                                        private val mValues: List<String>,
                                        private val mTwoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val position = v.tag
                var fragment : android.support.v4.app.Fragment
                if (mTwoPane) {
                    when(position){
                        0 -> fragment = FragmentARRAnualVisitation.newInstance("Test", "Test")
                        1 -> fragment = FragmentARRAVFacility.newInstance("Test", "Test")
                        2 -> fragment = FragmentARRAVFacilityContinued.newInstance("Test", "Test")
                        3 -> fragment = FragmentARRAVLocation.newInstance("Test", "Test")
                        4 -> fragment = FragmentARRAVPersonnel.newInstance("Test", "Test")
                        5 -> fragment = FragmentARRAVAmOrderTracking.newInstance("Test", "Test")
                        6 -> fragment = FragmentARRAVRepairShopPortalAddendum.newInstance("Test", "Test")
                        7 -> fragment = FragmentARRAVVisitationTracking.newInstance("Test", "Test")
                        8 -> fragment = FragmentARRAVScopeOfService.newInstance("Test", "Test")
                        9 -> fragment = FragmentARRAVVehicleServices.newInstance("Test", "Test")
                        10 -> fragment = FragmentARRAVVehicles.newInstance("Test", "Test")
                        11 -> fragment = FragmentARRAVPrograms.newInstance("Test", "Test")
                        12 -> fragment = FragmentARRAVFacilityServices.newInstance("Test", "Test")
                        13 -> fragment = FragmentARRAVAffliations.newInstance("Test", "Test")
                        14 -> fragment = FragmentARRAVDeficiency.newInstance("Test", "Test")
                        15 -> fragment = FragmentARRAVComplaints.newInstance("Test", "Test")
                        else -> fragment = FragmentARRAnualVisitation.newInstance("Test", "Test")
                    }

                    mParentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
//                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
//                        putExtra(ItemDetailFragment.ARG_ITEM_ID, "asdf")
//                    }
//                    v.context.startActivity(intent)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.mIdView.text = item

            with(holder.itemView) {
                tag = position
                setOnClickListener(mOnClickListener)
                if (tag == 0){
                    performClick()
                }
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
        }
    }
}
