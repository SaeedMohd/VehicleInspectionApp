package com.inspection

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bugfender.sdk.Bugfender
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

import com.inspection.MainActivity.Companion.activity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.applyEdgeToEdgeWithTopAbsorber
import com.inspection.Utils.Constants.IDLE_TIMEOUT
import com.inspection.Utils.Utility
import com.inspection.Utils.createPDF
import com.inspection.Utils.toast
import com.inspection.databinding.ActivityFormsBinding
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
import java.io.File

enum class fragmentsNames {
    FacilityGeneralInfo, FacilityContactInfo,FacilityRSP,FacilityPersonnel,FacilityAmedndmentsOrderTracking,
    Visitation,VisitationTracking,
    SoSGeneralInfo,SoSVehicleServices,SoSPrograms,SoSFacilityServices,SoSVehicles,SoSAffiliations,SoSPromotions,SoSAwards,SoSOthers,
    Deficiency,
    Comments,
    Complaints,
    Billing,
    Surveys,
    Photos
}

class FormsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,NetworkSpeedDetector.NetworkSpeedListener {

    var currentFragment = ""
    var saveRequired = false
    var saveVisitedScreensRequired = false
    var overrideBackButton = false
    var imageRepSignature : Bitmap? = null
    var imageSpecSignature : Bitmap? = null
    var imageDefSignature : Bitmap? = null
    var imageWaiveSignature : Bitmap? = null
    var visitationID : String? = ""
    var networkStatus : String? = ""
    var networkStatusErrorMsg : String? = ""
    var isNetworkAvailable : Boolean = true
    var saveDone : Boolean = false
    val animation: Animation = AlphaAnimation(1.0f, 0.0f)
    lateinit var binding: ActivityFormsBinding
    private lateinit var networkSpeedDetector: NetworkSpeedDetector

    //    var toolbar = findViewById<Toolbar>(R.id.toolbar)
//    var drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
//    var nav_view = findViewById<NavigationView>(R.id.nav_view)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormsBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_forms)
        setContentView(binding.root)
        // Let the toolbar's gradient background paint under the status bar
        // instead of leaving a white strip above the title. Bottom/side insets
        // still pad the drawer root so content clears the gesture nav.
        binding.root.applyEdgeToEdgeWithTopAbsorber(binding.appBarForms.toolbar)

        // Guard: if singletons were cleared by OS process kill, redirect to login
        if (FacilityDataModel.getInstance().tblFacilities.isEmpty() ||
            FacilityDataModel.getInstance().tblVisitationTracking.isEmpty() ||
            IndicatorsDataModel.getInstance().tblScopeOfServices.isEmpty() ||
            IndicatorsDataModel.getInstance().tblBilling.isEmpty() ||
            IndicatorsDataModel.getInstance().tblDeffeciencies.isEmpty() ||
            IndicatorsDataModel.getInstance().tblVisitation.isEmpty() ||
            IndicatorsDataModel.getInstance().tblFacility.isEmpty() ||
            IndicatorsDataModel.getInstance().tblComplaints.isEmpty() ||
            IndicatorsDataModel.getInstance().tblSurveys.isEmpty() ||
            IndicatorsDataModel.getInstance().tblPhotos.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        setSupportActionBar(binding.appBarForms.toolbar)
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
                this, binding.drawerLayout, binding.appBarForms.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
//        toggle.onDrawerStateChanged() {
//            Toast.makeText(this,"TEST",Toast.LENGTH_LONG)
//        })
        Bugfender.enableCrashReporting();
//        Bugfender.enableUIEventLogging(application);
        binding.drawerLayout.openDrawer(GravityCompat.START)


        val navigationMenu = binding.navView.menu

        refreshMenuIndicatorsForVisitedScreens()
//        if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType == VisitationTypes.AdHoc) {
        if (!createNewVisitation){
            navigationMenu.findItem(R.id.visitation).isEnabled = false
            navigationMenu.findItem(R.id.visitation).isVisible = false
            currentFragment = fragmentsNames.FacilityGeneralInfo.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.facility))
            binding.navView.setCheckedItem(R.id.facility)
        } else {
            navigationMenu.findItem(R.id.visitation).isEnabled = true
            navigationMenu.findItem(R.id.visitation).isVisible = true
            currentFragment = fragmentsNames.Visitation.toString()
            this.onNavigationItemSelected(navigationMenu.findItem(R.id.visitation))
            binding.navView.setCheckedItem(R.id.visitation)
        }

        networkSpeedDetector = NetworkSpeedDetector(this);
        networkSpeedDetector.setNetworkSpeedListener(this);
        networkSpeedDetector.startMonitoring();
        animation.duration = 500 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
    }

    override fun onSlowNetworkDetected(message: String) {
        val sb = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
        val customView = layoutInflater.inflate(R.layout.custom_snack_no_signal, null)
        val messageTextView : TextView = customView.findViewById<TextView>(R.id.snackbar_message)
        messageTextView.setText(message)
        networkStatus = message
        isNetworkAvailable = false
        // Set Snackbar's background to transparent to only show custom layout
        sb.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        // Add the custom layout to Snackbar
        val snackbarLayout = sb.view as ViewGroup
        snackbarLayout.addView(customView, 0)
        sb.show()
        runOnUiThread {
            if (message.contains("Slow Internet")) {
                networkStatusErrorMsg = "Slow Internet Connection"
                binding.appBarForms.internetIndicator.visibility = View.GONE
                binding.appBarForms.slowInternetIndicator.visibility = View.VISIBLE
                binding.appBarForms.slowInternetIndicator.startAnimation(animation)
                binding.appBarForms.slowInternetIndicator.tooltipText = message
                binding.appBarForms.slowInternetIndicator.setOnClickListener {
//                    Utility.showMessageDialog(this, "Warning", message)
                    Utility.showUnifiedErrorDialog(this,message)
                }
                binding.appBarForms.noInternetIndicator.visibility = View.GONE
                binding.appBarForms.slowInternetIndicator.clearAnimation()
                binding.appBarForms.noInternetIndicator.tooltipText = ""
            } else {
                networkStatusErrorMsg = "No Internet Connection"
                binding.appBarForms.internetIndicator.visibility = View.GONE
                binding.appBarForms.slowInternetIndicator.visibility = View.GONE
                binding.appBarForms.slowInternetIndicator.clearAnimation()
                binding.appBarForms.slowInternetIndicator.tooltipText = ""
                binding.appBarForms.noInternetIndicator.visibility = View.VISIBLE
                binding.appBarForms.noInternetIndicator.startAnimation(animation)
                binding.appBarForms.noInternetIndicator.tooltipText = message
                binding.appBarForms.noInternetIndicator.setOnClickListener {
//                    Utility.showMessageDialog(this, "Warning", message)
                    Utility.showUnifiedErrorDialog(this,message)
                }
            }

        }
    }

    override fun onNetworkSpeedRestored() {
//        val sb = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
//        val customView = layoutInflater.inflate(R.layout.custom_snack_connected, null)
//        sb.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
//        val snackbarLayout = sb.view as ViewGroup
        networkStatus = "Connection - Speed Restored"
        isNetworkAvailable = true
//        snackbarLayout.addView(customView, 0)
//        sb.show()
        runOnUiThread {
            binding.appBarForms.internetIndicator.visibility = View.VISIBLE
            binding.appBarForms.slowInternetIndicator.visibility = View.GONE
            binding.appBarForms.slowInternetIndicator.clearAnimation()
            binding.appBarForms.slowInternetIndicator.tooltipText = ""
            binding.appBarForms.noInternetIndicator.visibility = View.GONE
            binding.appBarForms.noInternetIndicator.clearAnimation()
            binding.appBarForms.noInternetIndicator.tooltipText = ""
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (overrideBackButton) {

            if (supportFragmentManager.findFragmentByTag("FragmentARRAVLocation") != null) {
                val fragmentLocation =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVLocation") as FragmentARRAVLocation
                fragmentLocation.updateDialogs()
            }
//            if (editEmailDialog != null) editEmailDialog.visibility = View.GONE
//            if (addNewPhoneDialog != null) addNewPhoneDialog.visibility = View.GONE
//            if (editLocationDialog != null) editLocationDialog.visibility = View.GONE
//            if (editPhoneDialog != null) editPhoneDialog.visibility = View.GONE
//            if (addNewEmailDialog != null) addNewEmailDialog.visibility = View.GONE
//            if (alphaBackgroundForDialogs != null) alphaBackgroundForDialogs.visibility = View.GONE
//            if (copyHoursDialog != null) copyHoursDialog.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentARRAVPersonnel") != null) {
                val fragmentPersonnel =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVPersonnel") as FragmentARRAVPersonnel
                fragmentPersonnel.updateDialogs()
            }
//            if (addNewPersonnelDialogue != null) addNewPersonnelDialogue.visibility = View.GONE
//            if (addNewCertificateDialogue != null) addNewCertificateDialogue.visibility = View.GONE
//            if (alphaBackgroundForPersonnelDialogs != null) alphaBackgroundForPersonnelDialogs.visibility = View.GONE
//            if (addNewPersonnelDialogue != null) addNewPersonnelDialogue.visibility = View.GONE
//            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
//            if (personnelLoadingView != null) personnelLoadingView.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentARRAVDeficiency") != null) {
                val fragmentDeficiency =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVDeficiency") as FragmentARRAVDeficiency
                fragmentDeficiency.updateDialogs()
            }
//            if (defeciencyCard != null) defeciencyCard.visibility = View.GONE
//            if (defeciencyCardEdit != null) defeciencyCardEdit.visibility = View.GONE
//            if (signatureDialog != null) signatureDialog.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentARRAVAffliations") != null) {
                val fragmentAffiliations =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVAffliations") as FragmentARRAVAffliations
                fragmentAffiliations.updateDialogs()
            }
//            if (alphaBackgroundForAffilliationsDialogs != null) alphaBackgroundForAffilliationsDialogs.visibility = View.GONE
//            if (affiliationsCard != null) affiliationsCard.visibility = View.GONE
//            if (edit_affiliationsCard != null) edit_affiliationsCard.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentARRAVFacilityServices") != null) {
                val fragmentFacilitServices =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVFacilityServices") as FragmentARRAVFacilityServices
                fragmentFacilitServices.updateDialogs()
            }

//            if (facilityServicesCard != null) facilityServicesCard.visibility = View.GONE
//            if (editFacilityServicesCard != null) editFacilityServicesCard.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentARRAVPrograms") != null) {
                val fragmentFacilityPrograms =
                    supportFragmentManager.findFragmentByTag("FragmentARRAVPrograms") as FragmentARRAVPrograms
                fragmentFacilityPrograms.updateDialogs()
            }

//            if (programCard != null) programCard.visibility = View.GONE
//            if (edit_programCard != null) edit_programCard.visibility = View.GONE
            if (supportFragmentManager.findFragmentByTag("FragmentAARAVPhotos") != null) {
                val fragmentFacilityPhotos =
                    supportFragmentManager.findFragmentByTag("FragmentAARAVPhotos") as FragmentAARAVPhotos
                fragmentFacilityPhotos.updateDialogs()
            }


//            if (photoLoadingView != null) photoLoadingView.visibility = View.GONE
//            if (addNewPhotoDialog != null) addNewPhotoDialog.visibility = View.GONE
//            if (photosPreviewDialog != null) photosPreviewDialog.visibility = View.GONE
//            if (editPhotoDialog != null) editPhotoDialog.visibility = View.GONE
//            if (editPhotoDialog != null) editPhotoDialog.visibility = View.GONE

//            if (complaintsCard != null) complaintsCard.visibility = View.GONE

//            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
//            if (edit_addNewPersonnelDialogue != null) edit_addNewPersonnelDialogue.visibility = View.GONE
            overrideBackButton = false
        } else if (preventNavigation()) {
            Utility.showSaveOrCancelAlertDialog(this)
        } else if ((saveVisitedScreensRequired && !FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) ) {
            var cancelProgress = false
            var alertBuilder = AlertDialog.Builder(this);
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.decision_dialog, null)
            alertBuilder.setView(dialogView)
            val dialogMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
            val btnPositiveAction = dialogView.findViewById<Button>(R.id.btnActionPositive)
            val btnNegativeAction = dialogView.findViewById<Button>(R.id.btnActionNegative)
            dialogTitle.setText("Permission Required")
            dialogMessage.setText("Do you want to save the visited screens ?")
            btnPositiveAction.setText("YES")
            btnNegativeAction.setText("NO")
            val dialog = alertBuilder.create()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            btnPositiveAction.setOnClickListener(View.OnClickListener { v: View? ->
                updateVisitationProgress(false)
            })
            btnNegativeAction.setOnClickListener(View.OnClickListener { v: View? ->
                updateVisitationProgress(true)
            })
            dialog.show()
//            alertBuilder.setMessage("Do you want to save the visited screens ?");
//            alertBuilder.setPositiveButton("YES") { dialog, which ->
//                updateVisitationProgress(false)
//            }
//            alertBuilder.setNegativeButton("NO") { dialog, which ->
//                updateVisitationProgress(true)
//            }
//            val alert = alertBuilder.create();
//            alert.show();
            overrideBackButton = false
        } else if (FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType!!.equals(VisitationTypes.AdHoc)) {
            updateVisitationProgress(true)
        } else {
            setResult(100)
            super.onBackPressed()
        }
    }

    fun updateVisitationProgress(cancel : Boolean) {
        var strUrl = FacilityDataModel.getInstance().tblFacilities[0].FACNo.toString() + "&clubCode="+FacilityDataModel.getInstance().clubCode+"&sessionId="+ ApplicationPrefs.getInstance(activity).sessionID+"&facAnnualInspectionMonth="+FacilityDataModel.getInstance().tblFacilities[0].FacilityAnnualInspectionMonth+"&inspectionCycle="+FacilityDataModel.getInstance().tblFacilities[0].InspectionCycle+"&userId="+ ApplicationPrefs.getInstance(activity).loggedInUserID+"&visitedScreens="+IndicatorsDataModel.getInstance().getVisitedScreen()+"&visitationType="+FacilityDataModel.getInstance().tblVisitationTracking[0].visitationType +"&cancelled="
        strUrl += if (cancel && !saveDone) "1" else "0"
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
        var navigationMenu = binding.navView.menu
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

        indicatorImage = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.mainIndicatorImg)
        indicatorImage.visibility = View.GONE
        if (isAllValid)
            indicatorImage.setBackgroundResource(R.drawable.green_background_button)
        else
            indicatorImage.setBackgroundResource(R.drawable.red_button_background)


    }

    public fun refreshMenuIndicatorsForVisitedScreens() { // Method used to validate all screens were visited
        var navigationMenu = binding.navView.menu
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
            IndicatorsDataModel.getInstance().tblScopeOfServices[0].PromotionsVisited = true
            IndicatorsDataModel.getInstance().tblDeffeciencies[0].visited = true
            IndicatorsDataModel.getInstance().tblSurveys[0].visited = true
            IndicatorsDataModel.getInstance().tblPhotos[0].visited = true
            IndicatorsDataModel.getInstance().tblFacility[0].GeneralInfoVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].LocationVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].PersonnelVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].RSPVisited = true
            IndicatorsDataModel.getInstance().tblFacility[0].VisitationTrackingVisited = true
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
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehicleServicesVisited && IndicatorsDataModel.getInstance().tblScopeOfServices[0].VehiclesVisited
                && IndicatorsDataModel.getInstance().tblScopeOfServices[0].PromotionsVisited)
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

        indicatorImage = binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.mainIndicatorImg)
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
                    binding.appBarForms.toolbar.title = "Visitation - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Facility - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Scope of Services- " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Deficiency - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Deficiency - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Deficiency.toString()
                    var fragment = FragmentARRAVDeficiency()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment,"FragmentARRAVDeficiency")
                            .commit()
                }
            }


            R.id.complaints -> {
                if (preventNavigation()) {
                    Utility.showSaveOrCancelAlertDialog(this)
                } else {
                    binding.appBarForms.toolbar.title = "Complaints - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Billing - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Surveys - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Comments - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
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
                    binding.appBarForms.toolbar.title = "Photos - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo
                    setTitle("Photos - " + FacilityDataModel.getInstance().tblFacilities[0].BusinessName + " - " + FacilityDataModel.getInstance().tblFacilities[0].FACNo)
                    saveRequired = false
                    currentFragment = fragmentsNames.Photos.toString()
                    var fragment = FragmentAARAVPhotos()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, fragment,"FragmentAARAVPhotos")
                            .commit()
                }
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

//    fun uploadPhoto(file: File, fileName: String) {
//        val multipartRequest = MultipartRequest(Constants.uploadPhoto + fileName, null, file, Response.Listener { response ->
//            try {
//
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//        }, Response.ErrorListener {
//            Utility.showMessageDialog(this, "Uploading File", "Uploading File Failed with error (" + it.message + ")")
//            Log.v("Upload Photo Error : ", it.message.toString())
//        })
//        val socketTimeout = 30000//30 seconds - change to what you want
//        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//        multipartRequest.retryPolicy = policy
//        Volley.newRequestQueue(applicationContext).add(multipartRequest)
//    }

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
//        var act = this
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
//            visitation_sv.done(true)
//            visitation_sv.go(4,true)

            createPDF(this)
                //        val file = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForSpecialist.pdf")
                //        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + FacilityDataModel.getInstance().tblFacilities[0].FACNo + "_VisitationDetails_ForShop.pdf")
                        val file = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForSpecialist.pdf")
                        val fileShop = File(Environment.getExternalStorageDirectory().path + "/" + Constants.visitationIDForPDF + "_VisitationDetails_ForShop.pdf")

    }

    private fun showPermissionSettingsDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage("Some permissions are permanently denied. You need to enable them from settings.")
        builder.setPositiveButton("Go to Settings") { _, _ ->
            // Redirect to app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 350) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("Denied", "Permission has been denied by user")
            } else {
                generateAndOpenPDF()
            }
        } else if (requestCode == 123) {
            var allGranted = true
            var permanentlyDenied = false

            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allGranted = false

                    // Check if "Don't Ask Again" was selected
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        permanentlyDenied = true
                    }
                }
            }

            when {
                allGranted -> {
                    // All permissions are granted
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234)
                }
                permanentlyDenied -> {
                    // Some permissions are permanently denied
                    showPermissionSettingsDialog()
                }
                else -> {
                    // Permissions are denied but not permanently
                    toast("Permissions denied. Please allow them to continue.")
                }
            }
        } else if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All requested permissions are granted, proceed to pick photos
                pickPhotos()
            } else {
                // Permissions denied, show a message or fallback behavior
                println("Permissions denied")
            }
        } else if (requestCode == 2001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed to capture image
//                captureImage()
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        lastActiveTime = System.currentTimeMillis()
        super.onPause()
    }

    override fun onResume() {
        val now = System.currentTimeMillis()
        if (now - lastActiveTime > IDLE_TIMEOUT) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        lastActiveTime = now
        super.onResume()
    }

    fun pickPhotos(){
        val intent: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*" // We want to pick images only
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
            }
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }
//        pickImagesLauncher.launch(intent)
    }
    companion object {
        var lastActiveTime: Long = System.currentTimeMillis()
    }
}

