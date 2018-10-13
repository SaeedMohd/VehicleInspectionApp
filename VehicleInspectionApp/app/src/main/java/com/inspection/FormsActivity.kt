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
import com.inspection.Utils.Utility
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
import com.inspection.model.HasChangedModel
import com.inspection.model.IndicatorsDataModel
import com.inspection.model.VisitationTypes
import kotlinx.android.synthetic.main.activity_forms.*
import kotlinx.android.synthetic.main.app_bar_forms.*

enum class fragmentsNames {
    FacilityGeneralInfo, FacilityContactInfo,FacilityRSP,FacilityPersonnel,FacilityAmedndmentsOrderTracking,
    Visitation,
    SoSGeneralInfo,SoSVehicleServices,SoSPrograms,SoSFacilityServices,SoSVehicles,SoSAffiliations,SoSPromotions,SoSAwards,SoSOthers,
    Deficiency,
    Comments,
    Complaints,
    Billing,
    Surveys,
    Photos
}

class FormsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var currentFragment = ""
    var saveRequired = false
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

        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc) {
            navigationMenu.findItem(R.id.visitation).isEnabled = false
            navigationMenu.findItem(R.id.visitation).isVisible = false
            currentFragment = fragmentsNames.FacilityGeneralInfo.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.facility))
//            toolbar.title = "Facility - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo

        }else{
            navigationMenu.findItem(R.id.visitation).isEnabled = true
            navigationMenu.findItem(R.id.visitation).isVisible = true
            currentFragment = fragmentsNames.Visitation.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.visitation))
//            Log.v("visitationtype is******", "Annual")
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.fragment, FragmentVisitation())
//                    .commit()
//            toolbar.title = "Visitation - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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

        indicatorImage = (navigationMenu.findItem(R.id.surveys).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblSurveys[0].Surveys)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = (navigationMenu.findItem(R.id.photos).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblPhotos[0].Photos)
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

        indicatorImage = (navigationMenu.findItem(R.id.complaints).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblComplaints[0].Complaints)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            isAllValid = false
        }

        indicatorImage = (navigationMenu.findItem(R.id.billing).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblBilling[0].BillingHistory && IndicatorsDataModel.getInstance().tblBilling[0].Billing &&
                IndicatorsDataModel.getInstance().tblBilling[0].BillingPlan && IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustments &&
                IndicatorsDataModel.getInstance().tblBilling[0].Payments&& IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenue)
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
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Visitation - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Visitation - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Visitation.toString()
                    var fragment = FragmentVisitation()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }

            R.id.facility -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Facility - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Facility - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.FacilityGeneralInfo.toString()
                    var fragment = FacilityGroupFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()


                }
            }

            R.id.scopeOfService -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Scope of Services- " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Scope of Services- " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.SoSGeneralInfo.toString()
                    var fragment = ScopeOfServiceGroupFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }


            R.id.deficiency -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Deficiency - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Deficiency - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Deficiency.toString()
                    var fragment = FragmentARRAVDeficiency()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }


            R.id.complaints -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Complaints - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Complaints - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Complaints.toString()
                    var fragment = FragmentARRAVComplaints()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }


            R.id.billing -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Billing - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Billing - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Billing.toString()
                    var fragment = BillingGroupFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }

            R.id.surveys -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Surveys - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Surveys - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Surveys.toString()
                    var fragment = SurveysGroupFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }

            R.id.comments -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Comments - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Comments - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Comments.toString()
                    var fragment = FragmentAARAVComments()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }

            R.id.photos -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    toolbar.title = "Photos - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Photos - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Photos.toString()
                    var fragment = FragmentAARAVPhotos()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment)
                            .commit()
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun preventNavigation() : Boolean{
        if (saveRequired) return true
//        else if (currentFragment.equals(fragmentsNames.FacilityGeneralInfo.toString()) && HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo() ) return true
        else return false

    }
}
