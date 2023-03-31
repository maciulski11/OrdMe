package com.example.ordme.ui.screen

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.User
import com.example.ordme.services.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_meal.returnBT
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileViewModel : ViewModel() {


    fun fetchUser(callback: ((User?) -> Unit)) {
        FirebaseRepository().fetchUser {
            callback(it)
        }
    }

    fun checkConnectivityAndFirestoreAvailability(context: Context, view: View) {

        FirebaseRepository().checkConnectivityAndFirestoreAvailability(
            context,
            { // success:
                FirebaseRepository().updateUserData {
                    fetchUser(context, view)
                }
//                view.progress_bar_layout.visibility = View.GONE // Ukryj progress_bar
//                view.view_layout.visibility = View.VISIBLE // Ukryj dane z Firestore
            },
            { // failure:

//                view.progress_bar_layout.visibility = View.VISIBLE // Ukryj progress_bar
                view.view_layout.visibility = View.GONE // Ukryj dane z Firestore
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

            if (it.flat!!.isEmpty()) {
                val slash = v.findViewById<TextView>(R.id.slashTV)
                slash.visibility = View.GONE
            }

            Glide.with(context)
                .load(it.photo)
                .override(470, 450)
                .circleCrop()
                .into(userPhoto)

            val progress = v.findViewById<LinearLayout>(R.id.progress_bar_layout)
            val viewLayout = v.findViewById<ConstraintLayout>(R.id.view_layout)

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

//        profileViewModel.checkConnectivityAndFirestoreAvailability(requireContext(), requireView())

        viewModel.mapLocation(requireContext(), childFragmentManager, R.id.mapLocation)

        // Uzyskaj referencję do widoku MapFragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)// Asynchronicznie uzyskaj mapę Google, gdy jest gotowa


        loadingData(true)

        profileViewModel.fetchUser {
            it?.let {
                setUserUI(it)
            }

            loadingData(false)
        }
    }

    private fun setUserUI(user: User) {
        val fullName = view?.findViewById<TextView>(R.id.fullNameTV)
        val number = view?.findViewById<TextView>(R.id.numberET)
        val street = view?.findViewById<TextView>(R.id.streetTV)
        val door = view?.findViewById<TextView>(R.id.doorTV)
        val flat = view?.findViewById<TextView>(R.id.flatTV)
        val floor = view?.findViewById<TextView>(R.id.floorTV)
        val postCode = view?.findViewById<TextView>(R.id.postCodeTV)
        val city = view?.findViewById<TextView>(R.id.cityTV)
        val userPhoto = view?.findViewById<ImageView>(R.id.userPhoto)

        fullName?.text = user.full_name
        number?.text = user.number!!.toString()
        street?.text = user.street
        door?.text = user.door
        flat?.text = user.flat
        floor?.text = user.floor
        postCode?.text = user.postCode
        city?.text = user.city

        if (user.flat!!.isEmpty()) {
            val slash = view?.findViewById<TextView>(R.id.slashTV)
            slash?.visibility = View.GONE
        }

        userPhoto?.let {
            Glide.with(requireContext())
                .load(user.photo)
                .override(470, 450)
                .circleCrop()
                .into(it)
        }

        val viewLayout = view?.findViewById<ConstraintLayout>(R.id.view_layout)

        viewLayout?.visibility = View.VISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

    }

    override fun unsubscribeUi() {

    }
}