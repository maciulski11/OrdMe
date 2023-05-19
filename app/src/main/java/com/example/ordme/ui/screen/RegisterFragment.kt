package com.example.ordme.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.User
import com.example.ordme.ui.view_model.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.enterNumberUser
import kotlinx.android.synthetic.main.fragment_register.enterPasswordUser
import kotlinx.android.synthetic.main.fragment_register.enterRepeatPasswordUser
import kotlinx.android.synthetic.main.fragment_register.registerButton

class RegisterFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_register

    private val fbAuth = FirebaseAuth.getInstance()
    private val viewModel: MainViewModel by viewModels()

    override fun subscribeUi() {

        registerUser()
    }

    private fun registerUser() {
        registerButton.setOnClickListener {

            val email = enterEmailUser.text.toString()
            val fullName = editFullName.text.toString()
            val number = enterNumberUser.text.toString()
            val password = enterPasswordUser.text.toString()
            val repeatPassword = enterRepeatPasswordUser.text.toString()

            if (password == repeatPassword) {
                fbAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResults ->
                        if (authResults.user != null) {
                            val user = User(
                                authResults.user!!.email,
                                authResults.user!!.uid,
                                fullName,
                                number.toLong(),
                            )
                            viewModel.createNewUser(user)
//                            cloud.collection("users")
//                                .document(user.uid!!)
//                                .set(user)

                            //send verification email to your account
                            fbAuth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Registered successfully. Please check your email for verification.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            task.exception!!.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            findNavController().navigate(R.id.action_registerUserFragment_to_loginUserFragment)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Snackbar.make(requireView(), "Something went wrong!", Snackbar.LENGTH_SHORT)
                            .show()
                        Log.d("something went wrong", exception.message.toString())
                    }
            } else if (password != repeatPassword) {
                Snackbar.make(requireView(), "Passwords are other.", Snackbar.LENGTH_SHORT).show()
            } else if (email == "" || password == "") {

                Snackbar.make(
                    requireView(),
                    "You must fill in all the fields with with asterisks!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun unsubscribeUi() {

    }
}