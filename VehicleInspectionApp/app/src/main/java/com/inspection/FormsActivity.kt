package com.inspection

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.inspection.R.drawable.circle
import com.inspection.fragments.*
import com.inspection.fragments.FragmentARRAVScopeOfService.Companion.validationProblemFoundForOtherFragments
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.activity_forms.*
import kotlinx.android.synthetic.main.app_bar_forms.*

class FormsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms)
        setSupportActionBar(toolbar)
        validationProblemFoundForOtherFragments=true

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        drawer_layout.openDrawer(GravityCompat.START)

        val navigationMenu = nav_view.menu

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

    fun validateFacilityDataDetails (){
        // Method to validate Facility Data Model data against the business rules


    }
}
