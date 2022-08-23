package com.example.ordme.ui.restaurant.screen

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_register_restaurant.*

class RegisterRestaurantFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_register_restaurant

    private val fbAuth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()


    override fun subscribeUi() {

        registerRestaurant()
    }

    private fun registerRestaurant() {
        registerResButton.setOnClickListener {

            val email = enterEmailRestaurant.text.toString()
            val nameRestaurant = enterNameRestaurant.text.toString()
            val number = enterNumber.text.toString()
            val secondNumber = enterSecondNumber.text.toString()
            val password = enterPasswordRes.text.toString()
            val repeatPassword = enterRepeatPasswordRes.text.toString()

            if (password == repeatPassword) {
                fbAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResults ->
                        if (authResults.user != null) {
                            val restaurant = com.example.ordme.ui.restaurant.data.Restaurants(
                                authResults.user!!.email,
                                nameRestaurant,
                                "",
                                authResults.user!!.uid,
                                "",
                                number,
                                secondNumber

                            )
                            cloud.collection("restaurants")
                                .document(restaurant.uid!!)
                                .set(restaurant)

                            findNavController().navigate(R.id.action_registerRestaurantFragment_to_fragmentEditRestaurant)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Snackbar.make(requireView(), "Something went wrong!", Snackbar.LENGTH_SHORT)
                            .show()
                        Log.d("something went wrong", exception.message.toString())
                    }
            } else if (password != repeatPassword) {
                Snackbar.make(requireView(), "Passwords are other.", Snackbar.LENGTH_SHORT).show()
            } else if (email == "" || nameRestaurant == "" || number == "" || password == "") {

                Snackbar.make(
                    requireView(),
                    "You must fill in all the fields with with asterisks!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
//                Toast.makeText(context, "You must fill in all the fields with with asterisks!" , Toast.LENGTH_SHORT)
//                    .show()
            }
        }
    }

    override fun unsubscribeUi() {

    }
}