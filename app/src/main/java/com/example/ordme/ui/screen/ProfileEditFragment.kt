package com.example.ordme.ui.screen

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.repository.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_profile.returnBT
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*

class ProfileEditViewModel() : ViewModel() {

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

            val editPhoto = v.findViewById<ImageButton>(R.id.editUserPhoto)
            val fullName = v.findViewById<EditText>(R.id.editFullName)
            val number = v.findViewById<EditText>(R.id.editPhoneUser)
            val street = v.findViewById<EditText>(R.id.editStreet)
            val door = v.findViewById<EditText>(R.id.editDoor)
            val flat = v.findViewById<EditText>(R.id.editFlat)
            val floor = v.findViewById<EditText>(R.id.editFloor)
            val postCode = v.findViewById<EditText>(R.id.editPostCode)
            val city = v.findViewById<EditText>(R.id.editCity)

            fullName.setText(it?.full_name)
            number.setText(it?.number.toString())
            street.setText(it?.street)
            door.setText(it?.door)
            flat.setText(it?.flat)
            floor.setText(it?.floor)
            postCode.setText(it?.postCode)
            city.setText(it?.city)

//            Glide.with(context)
//                .load(it?.photo)
//                .override(400, 400)
//                .circleCrop()
//                .into(editPhoto)

            val progress = v.findViewById<LinearLayout>(R.id.progress_bar_layout)
            val viewLayout = v.findViewById<ConstraintLayout>(R.id.view_layout)
            progress.visibility = View.GONE
            viewLayout.visibility = View.VISIBLE

        }
    }
}

class ProfileEditFragment() : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    private var profileEditViewModel = ProfileEditViewModel()

    override fun subscribeUi() {


        saveButton.setOnClickListener {

            val user = mapOf(
                "full_name" to editFullName.text.toString(),
                "number" to editPhoneUser.text.toString().toLong(),
                "street" to editStreet.text.toString(),
                "door" to editDoor.text.toString(),
                "flat" to editFlat.text.toString(),
                "floor" to editFloor.text.toString(),
                "postCode" to editPostCode.text.toString(),
                "city" to editCity.text.toString()
            )

            if (editPhoneUser.text.length > 9) {

                val toast = Toast.makeText(
                    context,
                    "Your number is too short, change it.",
                    Toast.LENGTH_SHORT
                )
                toast.show()

            } else if (editPhoneUser.text.length < 9) {

                val toast = Toast.makeText(
                    context,
                    "Your number is too long, change it.",
                    Toast.LENGTH_SHORT
                )
                toast.show()

            } else {
                FirebaseRepository().updateUser(user as Map<String, Long>)

                findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)

            }
        }

        returnBT.setOnClickListener {

            findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)
        }

        profileEditViewModel.checkConnectivityAndFirestoreAvailability(
            requireContext(),
            requireView()
        )


    }

    override fun unsubscribeUi() {

    }

}