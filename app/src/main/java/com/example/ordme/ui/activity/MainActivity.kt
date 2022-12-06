package com.example.ordme.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.car.ui.utils.CarUiUtils.getActivity
import com.example.ordme.R
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.ui.screen.BasketFragment
import com.example.ordme.ui.screen.ChooseRestaurantFragment
import com.example.ordme.ui.screen.LocationFragment
import com.example.ordme.ui.screen.ProfileFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private val fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer()
    }


    private fun drawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        val drawView: NavigationView = findViewById(R.id.draw_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        drawView.setNavigationItemSelectedListener {

            it.isChecked = true

            when (it.itemId) {

                R.id.nav_home -> replaceFragment(
                    ChooseRestaurantFragment(),
                "")
                R.id.nav_profile -> replaceFragment(
                    ProfileFragment(),
                    ""
                )
                R.id.nav_logout -> {
                    fbAuth.signOut()
                    finish()
                }
            }
            true

        }
    }


    private fun replaceFragment(fragment: Fragment, title: String) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }

}