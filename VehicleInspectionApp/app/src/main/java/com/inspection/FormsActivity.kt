package com.inspection

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
//import android.support.design.widget.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.inspection.R.id.drawer_layout
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
//    var toolbar = findViewById<Toolbar>(R.id.toolbar)
//    var drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
//    var nav_view = findViewById<NavigationView>(R.id.nav_view)
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

        refreshMenuIndicatorsForVisitedScreens()

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

    public fun refreshMenuIndicators() { // Method used to validate that business rules was fulfilled for each screen
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
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != VisitationTypes.AdHoc) {
                indicatorImage.setBackgroundResource(R.drawable.red_button_background)
                isAllValid = false
            }
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
        indicatorImage.visibility = View.GONE
        if (isAllValid)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)


    }

    public fun refreshMenuIndicatorsForVisitedScreens() { // Method used to validate all screens were visited
        var navigationMenu = nav_view.menu
        var indicatorImage: ImageView;
        indicatorImage = (navigationMenu.findItem(R.id.scopeOfService).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfoVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].AffiliationsVisited
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServicesVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehicleServicesVisited&& IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.visitation).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblVisitation[0].visited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != VisitationTypes.AdHoc) {
                indicatorImage.setBackgroundResource(R.drawable.red_button_background)
            }
        }

        indicatorImage = (navigationMenu.findItem(R.id.deficiency).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblDeffeciencies[0].visited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.surveys).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblSurveys[0].visited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.photos).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblPhotos[0].visited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)

        }

        indicatorImage = (navigationMenu.findItem(R.id.facility).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited && IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited &&
                IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited && IndicatorsDataModel.getInstance().tblFacility[0].RSPVisited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.complaints).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblComplaints[0].visited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.billing).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblBilling[0].BillingHistoryVisited && IndicatorsDataModel.getInstance().tblBilling[0].BillingVisited &&
                IndicatorsDataModel.getInstance().tblBilling[0].BillingPlanVisited && IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustmentsVisited &&
                IndicatorsDataModel.getInstance().tblBilling[0].PaymentsVisited && IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenueVisited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.mainIndicatorImg)
        indicatorImage.visibility = View.GONE

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
