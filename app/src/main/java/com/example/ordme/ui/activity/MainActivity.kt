package com.example.ordme.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_delete.*
import kotlinx.android.synthetic.main.fragment_checkout.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.updateUserData {
            drawerUserInfo()
        }

        drawer()
        changeDrawerState()
    }

    @SuppressLint("SuspiciousIndentation")
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

    private fun drawerUserInfo() {
        FirebaseRepository().fetchUser {
            val photo = findViewById<ImageView>(R.id.imageView)
            val fullName = findViewById<TextView>(R.id.fullNameTextView)
            val email = findViewById<TextView>(R.id.emailTextView)

            fullName.text = it?.full_name
            email.text = it?.email

            Glide.with(this)
                .load(it?.photo)
                .override(450, 450)
                .circleCrop()
                .into(photo)
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
            } else if (drawer.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }

}