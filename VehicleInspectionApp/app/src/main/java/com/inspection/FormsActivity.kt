package com.inspection

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.inspection.MainActivity.Companion.activity
import com.inspection.R.id.drawer_layout
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import com.inspection.Utils.createPDF
import com.inspection.adapter.MultipartRequest
import com.inspection.fragments.*
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.typeIdCompare
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.validationProblemFoundForOtherFragments
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_DiagnosticsRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_FixedLaborRate
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMax
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_LaborMin
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfBays
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.watcher_NumOfLifts
import com.inspection.model.*
import kotlinx.android.synthetic.main.activity_forms.*
import kotlinx.android.synthetic.main.app_bar_forms.*
import kotlinx.android.synthetic.main.fragment_aarav_location.*
import kotlinx.android.synthetic.main.fragment_aarav_personnel.*
import kotlinx.android.synthetic.main.fragment_aarav_photos.*
import kotlinx.android.synthetic.main.fragment_arrav_affliations.*
import kotlinx.android.synthetic.main.fragment_arrav_deficiency.*
import kotlinx.android.synthetic.main.fragment_arrav_facility_services.*
import kotlinx.android.synthetic.main.fragment_arrav_programs.*
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.ArrayList

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
    var saveVisitedScreensRequired = false
    var overrideBackButton = false
    var imageRepSignature : Bitmap? = null
    var imageSpecSignature : Bitmap? = null
    var imageDefSignature : Bitmap? = null
    var imageWaiveSignature : Bitmap? = null
    var visitationID : String? = ""

    //    var toolbar = findViewById<Toolbar>(R.id.toolbar)
//    var drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
//    var nav_view = findViewById<NavigationView>(R.id.nav_view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms)
        setSupportActionBar(toolbar)
        val theIntent = getIntent(); // gets the previously created intent
        val createNewVisitation = theIntent.getBooleanExtra("createNewVisitation",true)

        validationProblemFoundForOtherFragments = true

        watcher_LaborMax = ""
        watcher_LaborMin = ""
        watcher_FixedLaborRate = ""
        watcher_DiagnosticsRate = ""
        watcher_NumOfBays = ""
        watcher_NumOfLifts = ""
        typeIdCompare = ""


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
//        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc) {
        if (!createNewVisitation){
            navigationMenu.findItem(R.id.visitation).isEnabled = false
            navigationMenu.findItem(R.id.visitation).isVisible = false
            currentFragment = fragmentsNames.FacilityGeneralInfo.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.facility))
        } else {
            navigationMenu.findItem(R.id.visitation).isEnabled = true
            navigationMenu.findItem(R.id.visitation).isVisible = true
            currentFragment = fragmentsNames.Visitation.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.visitation))
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (overrideBackButton) {
            if (editEmailDialog != null) editEmailDialog.visibility = View.GONE
            if (addNewPhoneDialog != null) addNewPhoneDialog.visibility = View.GONE
            if (editLocationDialog != null) editLocationDialog.visibility = View.GONE
            if (editPhoneDialog != null) editPhoneDialog.visibility = View.GONE
            if (addNewEmailDialog != null) addNewEmailDialog.visibility = View.GONE
            if (addNewPersonnelDialogue != null) addNewPersonnelDialogue.visibility = View.GONE
            if (addNewCertificateDialogue != null) addNewCertificateDialogue.visibility = View.GONE
            if (alphaBackgroundForPersonnelDialogs != null) alphaBackgroundForPersonnelDialogs.visibility = View.GONE
            if (alphaBackgroundForDialogs != null) alphaBackgroundForDialogs.visibility = View.GONE
            if (alphaBackgroundForAffilliationsDialogs != null) alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
            if (defeciencyCard != null) defeciencyCard.visibility = View.GONE
            if (defeciencyCardEdit != null) defeciencyCardEdit.visibility = View.GONE
//            if (signatureDialog != null) signatureDialog.visibility = View.GONE
            if (affiliationsCard != null) affiliationsCard.visibility = View.GONE
            if (edit_affiliationsCard != null) edit_affiliationsCard.visibility = View.GONE
            if (facilityServicesCard != null) facilityServicesCard.visibility = View.GONE
            if (editFacilityServicesCard != null) editFacilityServicesCard.visibility = View.GONE
            if (programCard != null) programCard.visibility = View.GONE
            if (edit_programCard != null) edit_programCard.visibility = View.GONE
            if (addNewPersonnelDialogue != null) addNewPersonnelDialogue.visibility = View.GONE
            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
            if (personnelLoadingView != null) personnelLoadingView.visibility = View.GONE
            if (photoLoadingView != null) photoLoadingView.visibility = View.GONE
            if (addNewPhotoDialog != null) addNewPhotoDialog.visibility = View.GONE
            if (photosPreviewDialog != null) photosPreviewDialog.visibility = View.GONE
            if (editPhotoDialog != null) editPhotoDialog.visibility = View.GONE
            if (copyHoursDialog != null) copyHoursDialog.visibility = View.GONE
            if (editPhoneDialog != null) editPhotoDialog.visibility = View.GONE
//            if (complaintsCard != null) complaintsCard.visibility = View.GONE

//            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
//            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
            overrideBackButton = false
        } else if (preventNavigation()) {
            Utility.showSaveOrCancelAlertDialog(this)
        } else if ((saveVisitedScreensRequired && !FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) ) {
            var cancelProgress = false
            var alertBuilder = AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission Required")
            alertBuilder.setMessage("Do you want to save the visited screens ?");
            alertBuilder.setPositiveButton("YES") { dialog, which ->
                updateVisitationProgress(false)
            }
            alertBuilder.setNegativeButton("NO") { dialog, which ->
                updateVisitationProgress(true)
            }
            val alert = alertBuilder.create();
            alert.show();
            overrideBackButton = false
        } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) {
            updateVisitationProgress(true)
        } else {
            super.onBackPressed()
        }
    }

    fun updateVisitationProgress(cancel : Boolean) {
        var strUrl = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "&clubCode="+FacilityDataModel.getInstance().clubCode+"&sessionId="+ ApplicationPrefs.getInstance(activity).sessionID+"&facAnnualInspectionMonth="+FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth+"&inspectionCycle="+FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle+"&userId="+ ApplicationPrefs.getInstance(activity).loggedInUserID+"&visitedScreens="+IndicatorsDataModel.getInstance().getVisitedScreen()+"&visitationType="+FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType +"&cancelled="
        if (cancel)
            strUrl += "1"
        else
            strUrl += "0"
        Log.v("Mark In Progress -> ",Constants.saveVisitedScreens+strUrl)
        Volley.newRequestQueue(this).add(StringRequest(Request.Method.GET, Constants.saveVisitedScreens+strUrl,
                Response.Listener { response ->
                    super.onBackPressed()
                }, Response.ErrorListener {
            Log.v("Mark Visitation", "As In Progress Failed --> " + it.message)
            it.printStackTrace()
        }))
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
                IndicatorsDataModel.getInstance().tblBilling[0].Payments && IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenue)
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
//        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency) {
//            indicatorImage = (navigationMenu.findItem(R.id.scopeOfService).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.visitation).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.deficiency).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.surveys).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.photos).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.facility).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.complaints).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//            indicatorImage = (navigationMenu.findItem(R.id.billing).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
//            indicatorImage.visibility = View.GONE
//        }
        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc || FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.Deficiency) {
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfoVisited = true
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].AffiliationsVisited = true
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServicesVisited = true
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited = true
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehicleServicesVisited = true
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited = true
            IndicatorsDataModel.getInstance().tblDeffeciencies[0].visited = true
            IndicatorsDataModel.getInstance().tblSurveys[0].visited = true
            IndicatorsDataModel.getInstance().tblPhotos[0].visited = true
            IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].RSPVisited = true
            IndicatorsDataModel.getInstance().tblComplaints[0].visited = true
            IndicatorsDataModel.getInstance().tblBilling[0].BillingHistoryVisited = true
            IndicatorsDataModel.getInstance().tblBilling[0].BillingVisited = true
            IndicatorsDataModel.getInstance().tblBilling[0].BillingPlanVisited = true
            IndicatorsDataModel.getInstance().tblBilling[0].BillingAdjustmentsVisited = true
            IndicatorsDataModel.getInstance().tblBilling[0].PaymentsVisited  = true
            IndicatorsDataModel.getInstance().tblBilling[0].VendorRevenueVisited = true
        }



        indicatorImage = (navigationMenu.findItem(R.id.scopeOfService).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView
        if (IndicatorsDataModel.getInstance().tblScopeOfServices[0].GeneralInfoVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].AffiliationsVisited
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].FacilityServicesVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].ProgramsVisited
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehicleServicesVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)
        }

        indicatorImage = (navigationMenu.findItem(R.id.visitation).actionView as FrameLayout).findViewById(R.id.menu_item_indicator_img) as ImageView

//        if (IndicatorsDataModel.getInstance().tblVisitation[0].visited)
        if (IndicatorsDataModel.getInstance().validateAllScreensVisited())
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else {
            if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != VisitationTypes.AdHoc && FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType != VisitationTypes.Deficiency) {
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

//        menuInflater.inflate(R.menu.forms, menu)

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
                    saveVisitedScreensRequired = true
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
                    saveVisitedScreensRequired = true
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
                    saveVisitedScreensRequired = true
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

    fun uploadPhoto(file: File, fileName: String) {
        val multipartRequest = MultipartRequest(Constants.uploadPhoto + fileName, null, file, Response.Listener { response ->
            try {

            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            Utility.showMessageDialog(this, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
            Log.v("Upload Photo Error : ", it.message.toString())
        })
        val socketTimeout = 30000//30 seconds - change to what you want
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        multipartRequest.retryPolicy = policy
        Volley.newRequestQueue(applicationContext).add(multipartRequest)
    }

    fun preventNavigation(): Boolean {
        if (saveRequired) return true
//        else if (currentFragment.equals(fragmentsNames.FacilityGeneralInfo.toString()) && HasChangedModel.getInstance().changeDoneForFacilityGeneralInfo() ) return true
        else return false

    }


    fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }



    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                var alertBuilder = AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission Required")
                alertBuilder.setMessage("Storage permission is required to create generate the completed visitation PDF, ");
                alertBuilder.setPositiveButton("YES") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 350);
                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 350);
            }
        } else {
            generateAndOpenPDF()
        }
    }

    fun generateAndOpenPDF() {
        var act = this
//        Volley.newRequestQueue(activity).add(StringRequest(Request.Method.GET, Constants.getLoggedActions + FacilityDataModel.getInstance().tblFacilities[0].FACNo+"&clubCode=${FacilityDataModel.getInstance().clubCode}&userId="+ApplicationPrefs.getInstance(this).loggedInUserID,
//                Response.Listener { response ->
//                    activity!!.runOnUiThread {
//                        PRGDataModel.getInstance().tblPRGLogChanges.clear()
//                        if (!response.toString().replace(" ","").equals("[]")) {
//                            PRGDataModel.getInstance().tblPRGLogChanges = Gson().fromJson(response.toString(), Array<PRGLogChanges>::class.java).toCollection(ArrayList())
//                        } else {
//                            var item = PRGLogChanges()
//                            item.recordid=-1
//                                PRGDataModel.getInstance().tblPRGLogChanges.add(item)
//                        }
//                        createPDF(act)
//                //        val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForSpecialist.pdf")
//                //        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForShop.pdf")
//                        val file = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf")
//                        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf")
//                    }
//                }, Response.ErrorListener {
//            Log.v("Loading PRG Data error", "" + it.message)
////                            launchNextAction(isCompleted)
//            it.printStackTrace()
//        }))
                        createPDF(this)
                //        val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForSpecialist.pdf")
                //        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForShop.pdf")
                        val file = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf")
                        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 350) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("Denied", "Permission has been denied by user")
            } else {
                generateAndOpenPDF()
            }
        }
    }

}

