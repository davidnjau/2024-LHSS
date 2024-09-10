package com.intellisoftkenya.a24cblhss

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_activity_bottem_navigation)
                    as NavHostFragment
        navController = navHostFragment.navController

        // Set up AppBarConfiguration with the top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.splashFragment, // Your top-level destinations (no back button)
                R.id.loginFragment,
            )
        )

        // Set up the ActionBar to work with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Listen for changes in the destination to update back button visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Show or hide the back button depending on the fragment
            supportActionBar?.setDisplayHomeAsUpEnabled(
                appBarConfiguration.topLevelDestinations.contains(destination.id).not()
            )
        }
    }

    // Override to handle back button clicks
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}