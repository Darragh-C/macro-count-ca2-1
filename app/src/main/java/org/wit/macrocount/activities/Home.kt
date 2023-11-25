package org.wit.macrocount.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.wit.macrocount.R
import org.wit.macrocount.databinding.HomeBinding
import timber.log.Timber
import timber.log.Timber.Forest.i

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        i("starting home")
        super.onCreate(savedInstanceState)
        i("setting up content view")
        setContentView(R.layout.content_home)

        i("setting up home binding")
        homeBinding = HomeBinding.inflate(layoutInflater)
        i("setting content view to home binding")
        setContentView(homeBinding.root)
        i("setting up drawer layout")
        drawerLayout = homeBinding.drawerLayout

        i("setting up nav controller")
        val navController = findNavController(R.id.nav_host_fragment)
        i("setting up action bar with nav controller")
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        i("setting up up/back nav")
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        i("onCreateOptionsMenu home called")
//        menuInflater.inflate(R.menu.menu_macro_list, menu)
//        return super.onCreateOptionsMenu(menu)
//
//    }
}