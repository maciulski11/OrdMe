package com.example.ordme.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ordme.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer()
        changeDrawerState()
    }

    private fun drawer() {
        navController = findNavController(R.id.fragment)
        drawerLayout = findViewById(R.id.drawerLayout)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

                when (it.itemId) {

                R.id.nav_profile -> {
                    findNavController(R.id.chooseRestaurant)
                        .navigate(R.id.action_chooseRestaurantFragment_to_profileFragment2)
                    drawerLayout.closeDrawers()
                }

                R.id.nav_message -> {
                    findNavController(R.id.chooseRestaurant)
                        .navigate(R.id.action_chooseRestaurantFragment_to_messageFragment)
                    drawerLayout.closeDrawers()

                }

                R.id.nav_logout -> {
                    fbAuth.signOut()
                    finish()
                }
            }
            true
        }
    }

    //this function is blocking and it is making invisible icon and functions of drawer
    private fun changeDrawerState() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        navHostFragment.navController.addOnDestinationChangedListener(listener = { _, destination, _ ->
            //set in case app asked for verify auth, signIn page drawer will be locked
            if (destination.id != R.id.chooseRestaurantFragment) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            else if (drawer.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        })
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.fragmentt)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }

}