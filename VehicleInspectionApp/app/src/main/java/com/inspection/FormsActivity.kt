package com.inspection

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.inspection.R.drawable.circle
import com.inspection.fragments.*
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.typeIdCompare
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.validationProblemFoundForOtherFragments
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_DiagnosticsRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_FixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfBays
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfLifts
import com.inspection.model.FacilityDataModel
import com.inspection.model.IndicatorsDataModel
import kotlinx.android.synthetic.main.activity_forms.*
import kotlinx.android.synthetic.main.app_bar_forms.*


class FormsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_forms)
        setSupportActionBar(toolbar)
        validationProblemFoundForOtherFragments=true

         watcher_LaborMax=""
         watcher_LaborMin=""
         watcher_FixedLaborRate=""
         watcher_DiagnosticsRate=""
         watcher_NumOfBays=""
         watcher_NumOfLifts=""
         typeIdCompare=""


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
//        toggle.onDrawerStateChanged() {
//            Toast.makeText(this,"TEST",Toast.LENGTH_LONG)
//        })

        drawer_layout.openDrawer(GravityCompat.START)


        val navigationMenu = nav_view.menu

        refreshMenuIndicators()

        ////navigationMenu.findItem(R.id.visitation).actionView.setBackgroundResource(R.drawable.red_button_background)

        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == FacilityDataModel.VisitationTypes.AdHoc) {
            navigationMenu.findItem(R.id.visitation).isEnabled = false
            navigationMenu.findItem(R.id.visitation).isVisible = false

            this.onNavigationItemSelected(navigationMenu.findItem(R.id.facility))
            toolbar.title = "Facility"
        }else{
            navigationMenu.findItem(R.id.visitation).isEnabled = true
            Log.v("visitationtype is******", "Annual")
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, FragmentVisitation())
                    .commit()
            toolbar.title = "Visitation"
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    public fun refreshMenuIndicators() {
        var navigationMenu = nav_view.menu
        var indicatorImage: ImageView;
        var isAllValid = true
        indicatorImage = (navigationMenu.findItem(R.id.scopeOfService).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfo && IndicatorsDataModel.getInstance().tblScopeOfServices[0].Affiliations
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServices && IndicatorsDataModel.getInstance().tblScopeOfServices[0].Programs)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = (navigationMenu.findItem(R.id.visitation).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblVisitation[0].Visitation)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = (navigationMenu.findItem(R.id.deficiency).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblDeffeciencies[0].Deffeciency)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = (navigationMenu.findItem(R.id.facility).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfo && IndicatorsDataModel.getInstance().tblFacility[0].Location &&
                IndicatorsDataModel.getInstance().tblFacility[0].Personnel && IndicatorsDataModel.getInstance().tblFacility[0].RSP)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.mainIndicatorImg)
        if (isAllValid)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.forms, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.visitation -> {
                toolbar.title = "Visitation"
                var fragment = FragmentVisitation()
                        supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.facility -> {
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.scopeOfService -> {
                toolbar.title = "Scope Of Service"
                var fragment = ScopeOfServiceGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }


            R.id.deficiency -> {
                Log.v("POSITION:  ","BRAAAAAAAAAA")
                toolbar.title = "Deficiency"
                var fragment = FragmentARRAVDeficiency()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }


            R.id.complaints -> {
                toolbar.title = "Complaints"
                var fragment = FragmentARRAVComplaints()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }


            R.id.billing -> {
                toolbar.title = "Billing"
                var fragment = BillingGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.surveys -> {
                toolbar.title = "Surveys"
                var fragment = SurveysGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.comments -> {
                toolbar.title = "Comments"
                var fragment = FragmentAARAVComments()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.photos -> {
                toolbar.title = "Photos"
                var fragment = FragmentAARAVPhotos()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
