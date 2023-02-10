package com.example.ordme.ui.screen

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.User
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_meal.returnBT
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileViewModel : ViewModel() {

    fun checkConnectivityAndFirestoreAvailability(context: Context, view: View) {
        FirebaseRepository().checkConnectivityAndFirestoreAvailability(
            context,
            { // success:
                fetchUser(context, view)
            },
            { // failure:
                view.progress_bar_layout.visibility = View.VISIBLE
                view.view_layout.visibility = View.GONE
            }
        )
    }

    private fun fetchUser(context: Context, v: View) {
        FirebaseRepository().fetchUser {

            val fullName = v.findViewById<TextView>(R.id.fullNameTV)
            val number = v.findViewById<TextView>(R.id.numberET)
            val street = v.findViewById<TextView>(R.id.streetTV)
            val door = v.findViewById<TextView>(R.id.doorTV)
            val flat = v.findViewById<TextView>(R.id.flatTV)
            val floor = v.findViewById<TextView>(R.id.floorTV)
            val postCode = v.findViewById<TextView>(R.id.postCodeTV)
            val city = v.findViewById<TextView>(R.id.cityTV)
            val userPhoto = v.findViewById<ImageView>(R.id.userPhoto)

            fullName.text = it!!.full_name
            number.text = it.number!!.toString()
            street.text = it.street
            door.text = it.door
            flat.text = it.flat
            floor.text = it.floor
            postCode.text = it.postCode
            city.text = it!!.city

            Glide.with(context)
                .load(it.photo)
                .override(470, 450)
                .circleCrop()
                .into(userPhoto)

            val progress = v.findViewById<LinearLayout>(R.id.progress_bar_layout)
            val viewLayout = v.findViewById<ConstraintLayout>(R.id.view_layout)
            progress.visibility = View.GONE
            viewLayout.visibility = View.VISIBLE
        }
    }
}

class ProfileFragment : BaseFragment(), OnMapReadyCallback {
    override val layout: Int = R.layout.fragment_profile

    private lateinit var mMap: GoogleMap

    private val profileViewModel = ProfileViewModel()
    private val viewModel: MainViewModel by activityViewModels()

    override fun subscribeUi() {

        editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment_to_chooseRestaurantFragment)
        }

        profileViewModel.checkConnectivityAndFirestoreAvailability(requireContext(), requireView())
        viewModel.mapLocation(requireContext(), childFragmentManager, R.id.mapLocation)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
    }

    override fun unsubscribeUi() {

    }
}