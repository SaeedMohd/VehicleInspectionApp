package com.inspection

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.inspection.fragments.FacilityGroupFragment
import com.inspection.fragments.FragmentAARAVPhotos
import com.inspection.fragments.FragmentVisitation
import com.inspection.fragments.ScopeOfServiceGroupFragment
import kotlinx.android.synthetic.main.activity_forms.*
import kotlinx.android.synthetic.main.app_bar_forms.*

class FormsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        var fragment = FragmentVisitation()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit()

        drawer_layout.openDrawer(GravityCompat.START)

        toolbar.title = "Visitation"

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }


            R.id.complaints -> {
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }


            R.id.billing -> {
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.surveys -> {
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
            }

            R.id.comments -> {
                toolbar.title = "Facility"
                var fragment = FacilityGroupFragment()
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
