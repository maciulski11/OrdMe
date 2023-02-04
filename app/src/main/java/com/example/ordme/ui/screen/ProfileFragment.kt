package com.example.ordme.ui.screen

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.User
import com.example.ordme.ui.repository.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_meal.returnBT
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.progress_bar_layout
import kotlinx.android.synthetic.main.fragment_profile.view.view_layout

class ProfileViewModel: ViewModel(){

    var user: MutableLiveData<User?> = MutableLiveData(null)

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
            city.text = it.city

                Glide.with(context)
                    .load(it.photo)
                    .override(400, 400)
                    .circleCrop()
                    .into(userPhoto)

            val progress = v.findViewById<LinearLayout>(R.id.progress_bar_layout)
            val viewLayout = v.findViewById<ConstraintLayout>(R.id.view_layout)
            progress.visibility = View.GONE
            viewLayout.visibility = View.VISIBLE
        }
    }
}

class ProfileFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    private val profileViewModel = ProfileViewModel()

    override fun subscribeUi() {

        profileViewModel.user.observe(this) {
            profileViewModel.checkConnectivityAndFirestoreAvailability(requireContext(), requireView())
        }

        editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment_to_chooseRestaurantFragment)
        }

    }


    override fun unsubscribeUi() {

    }
}