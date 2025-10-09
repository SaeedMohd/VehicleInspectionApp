package com.inspection

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.inspection.databinding.ActivityApplicantBinding
import com.inspection.fragments.ApplicantMapFragment
import com.inspection.fragments.FacilityGroupFragment
import com.inspection.model.FacilityDataModel

class ApplicantActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityApplicantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplicantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        setFields()

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    fun setFields() {
        binding.showMapLL.setOnClickListener {
            binding.topAppBar.title = "Map View"
            setMenuColor(0)
//            var fragment = ApplicantMapFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContent, ApplicantMapFragment())
                .commit()
        }
        binding.savedLL.setOnClickListener {
            binding.topAppBar.title = "Saved Shops"
            setMenuColor(1)
            var fragment = ApplicantMapFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit()
        }
        binding.visitedLL.setOnClickListener {
            binding.topAppBar.title = "Visited Shops"
            setMenuColor(2)
            var fragment = ApplicantMapFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit()
        }


    }

    fun setMenuColor(menuItem: Int) {
        when (menuItem) {
            0 -> {
                binding.mapMenuTv.setTextColor(Color.parseColor("#4572ce"))
                binding.savedMenuTv.setTextColor(Color.parseColor("#000000"))
                binding.visistedMenuTv.setTextColor(Color.parseColor("#000000"))
            }
            1 -> {
                binding.mapMenuTv.setTextColor(Color.parseColor("#000000"))
                binding.savedMenuTv.setTextColor(Color.parseColor("#4572ce"))
                binding.visistedMenuTv.setTextColor(Color.parseColor("#000000"))
            }
            2 -> {
                binding.mapMenuTv.setTextColor(Color.parseColor("#000000"))
                binding.savedMenuTv.setTextColor(Color.parseColor("#000000"))
                binding.visistedMenuTv.setTextColor(Color.parseColor("#4572ce"))
            }
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.applicant, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_applicant)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}