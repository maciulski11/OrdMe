package com.example.ordme.ui.screen

import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.repository.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_meal.returnBT
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileViewModel: ViewModel(){

    fun fetchUser(v: View) {
        FirebaseRepository().fetchUser {

            val name = v.findViewById<TextView>(R.id.nameET)
            val surname = v.findViewById<TextView>(R.id.surnameET)
            val number = v.findViewById<TextView>(R.id.numberET)
            val street = v.findViewById<TextView>(R.id.streetTV)
            val door = v.findViewById<TextView>(R.id.doorTV)
            val flat = v.findViewById<TextView>(R.id.flatTV)
            val floor = v.findViewById<TextView>(R.id.floorTV)
            val postCode = v.findViewById<TextView>(R.id.postCodeTV)
            val city = v.findViewById<TextView>(R.id.cityTV)

            name.text = it!!.name
            surname.text = it.surname
            number.text = it.number.toString()
            street.text = it.street
            door.text = it.door
            flat.text = it.flat
            floor.text = it.floor
            postCode.text = it.postCode
            city.text = it.city

        }
    }

}

class ProfileFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_profile

    private val profileViewModel = ProfileViewModel()

    override fun subscribeUi() {

        profileViewModel.fetchUser(requireView())

        editProfileBT.setOnClickListener {

            
        }

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_profileFragment2_to_chooseRestaurantFragment)
        }

    }


    override fun unsubscribeUi() {

    }
}