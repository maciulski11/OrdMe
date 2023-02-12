package com.example.ordme.ui.screen

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.returnBT
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import java.io.ByteArrayOutputStream

class ProfileEditViewModel() : ViewModel() {

    fun uploadUserPhoto(bytes: ByteArray) {
        FirebaseRepository().uploadUserPhoto(bytes)
    }

    fun checkConnectivityAndFirestoreAvailability(context: Context, view: View) {
        FirebaseRepository().checkConnectivityAndFirestoreAvailability(
            context,
            { // success:
                fetchUser(context, view)
            },
            { // failure:
                val progress = view.findViewById<LinearLayout>(R.id.progress_bar_layout)
                val viewLayout = view.findViewById<ConstraintLayout>(R.id.view_layout)
                progress.visibility = View.VISIBLE
                viewLayout.visibility = View.GONE
            }
        )
    }

    private fun fetchUser(context: Context, v: View) {
        FirebaseRepository().fetchUser {

            val editPhoto = v.findViewById<ImageView>(R.id.editUserPhoto)
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

            Glide.with(context)
                .load(it?.photo)
                .override(450, 450)
                .circleCrop()
                .into(editPhoto)

            val progress = v.findViewById<LinearLayout>(R.id.progress_bar_layout)
            val viewLayout = v.findViewById<ConstraintLayout>(R.id.view_layout)
            progress.visibility = View.GONE
            viewLayout.visibility = View.VISIBLE

        }
    }
}

class ProfileEditFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_profile_edit

    private val PROFILE_DEBUG = "TAKE PHOTO"
    private val REQUEST_IMAGE_CAPTURE = 1

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
            requireView())

        setupTakePictureClick()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Log.d(PROFILE_DEBUG, "BITMAP: " + imageBitmap.byteCount.toString())

            Glide.with(this)
                .load(imageBitmap)
                .circleCrop()
                .override(480, 480)
                .into(editUserPhoto)

            val stream = ByteArrayOutputStream()
            val result = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if (result) profileEditViewModel.uploadUserPhoto(byteArray)
        }
    }

    private fun setupTakePictureClick() {
        //funkcja ktora odpowiada za zrobienie zdjecia po klikniecu w nasz imagebutton
        editUserPhoto.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
            try {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } catch (exc: Exception) {
                Log.d(PROFILE_DEBUG, exc.message.toString())
            }
        }
    }

    override fun unsubscribeUi() {

    }

}