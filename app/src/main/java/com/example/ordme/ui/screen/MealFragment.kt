package com.example.ordme.ui.screen

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_meal.*
import kotlinx.android.synthetic.main.fragment_meal.nameMealTV

class MealFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_meal

    private var db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()
    private val random = java.util.Random().nextInt(999999999).toString()

    private var meal = Meal()

    override fun subscribeUi() {
        //get data from RestaurantAdapter
        val uidMeal = requireArguments().getString("uidMeal").toString()
        val uidRestaurant = requireArguments().getString("uidRestaurant").toString()

        //download date from firebase with uid restaurant and meal
        db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
            .collection(FirebaseRepository.MEALS).document(uidMeal)
            .get().addOnSuccessListener { snapshot ->

                Log.d("RestarantFragment", "${snapshot.data} ")

                //I pulled a snapshot of the data and if it is true i let to download data
                snapshot.toObject(Meal::class.java)?.let {
                    meal = it
                    nameMealTV.text = meal.nameMeal
                    addMealToBasketBT.text = meal.price
                    amountTV.text = "1"

                    //it is the same as else
                } ?: run {
                    nameMealTV.text = "Null"
                }

                addMealToBasketBT.setOnClickListener {
                    Toast.makeText(requireContext(), "${meal.price}", Toast.LENGTH_SHORT).show()

                    val dish = hashMapOf(
                        "price" to meal.price,
                        "nameMeal" to meal.nameMeal,
                        "amountMeal" to meal.amount
//                        "list" to arrayListOf("${meal.nameMeal}")
                    )

                    db.collection(FirebaseRepository.USERS).document(fbAuth.currentUser!!.uid)
                        .collection(FirebaseRepository.BASKET).document(uidRestaurant)
                        .collection(FirebaseRepository.MEALS).document(random)
                        .set(dish)

                    val bundle = Bundle()
                    bundle.putString(
                        "uidMeal",
                        uidMeal
                    )
                    bundle.putString(
                        "uidRestaurant",
                        uidRestaurant
                    )

                    //it transferred data to FragmentBasket without used navigate
                    parentFragmentManager.setFragmentResult("dataRestaurantAndMeal", bundle)

                    findNavController().navigate(
                        R.id.action_mealFragment_to_restaurantFragment,
                        bundle
                    )
                }
            }


        var i = 1

        plusBT.setOnClickListener {
            i++
            val x = meal.price.toString().toDouble()

            val w = x * i
            amountTV.text = i.toString()

            Log.d("REPO_DEBUG", "$w")

            //update my price and amount in my firebase and download data to layout
            db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
                .collection(FirebaseRepository.MEALS).document(uidMeal)
                .update("price", w.toString(), "amount", i.toString())
                .addOnSuccessListener {
                    Log.d("REPO_DEBUG", "Zaktualizowano dane!")
                    Log.d("REPO", "$x")

                }
                .addOnFailureListener {
                    Log.d("REPO_DEBUG", it.toString())
                }

            //updates data in real time
            getRealTimeData().observe(viewLifecycleOwner) { priceMeal ->
                addMealToBasketBT.text = priceMeal.price
            }

            addMealToBasketBT.setOnClickListener {
                Toast.makeText(requireContext(), "${meal.price}", Toast.LENGTH_SHORT).show()

                val dish = hashMapOf(
                    "price" to w.toString(),
                    "nameMeal" to meal.nameMeal,
                    "amountMeal" to meal.amount
//                        "list" to arrayListOf("${meal.nameMeal}")
                )

                db.collection(FirebaseRepository.USERS).document(fbAuth.currentUser!!.uid)
                    .collection(FirebaseRepository.BASKET).document(uidRestaurant)
                    .collection(FirebaseRepository.MEALS).document(random)
                    .set(dish)

                val bundle = Bundle()
                bundle.putString(
                    "uidMeal",
                    uidMeal
                )
                bundle.putString(
                    "uidRestaurant",
                    uidRestaurant
                )

                findNavController().navigate(R.id.action_mealFragment_to_restaurantFragment, bundle)

                val n = 1

                db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
                    .collection(FirebaseRepository.MEALS).document(uidMeal)
                    .update("price", x.toString(), "amount", n.toString())
                    .addOnSuccessListener {
                        Log.d("REPO_DEBUG", "Zaktualizowano dane!")
                    }
                    .addOnFailureListener {
                        Log.d("REPO_DEBUG", it.toString())
                    }
            }
        }

        minusBT.setOnClickListener {
            if (i <= 1) {
                onStop()
            } else {
                i--
                val x = meal.price.toString().toDouble()

                val w = x * i

                amountTV.text = i.toString()

                Log.d("REPO_DEBUG", "$w")

                addMealToBasketBT.setOnClickListener {
                    Toast.makeText(requireContext(), "$w", Toast.LENGTH_SHORT).show()
                }

                db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
                    .collection(FirebaseRepository.MEALS).document(uidMeal)
                    .update("price", w.toString(), "amount", i.toString())
                    .addOnSuccessListener {
                        Log.d("REPO_DEBUG", "Zaktualizowano dane!")
                    }
                    .addOnFailureListener {
                        Log.d("REPO_DEBUG", it.toString())
                    }

                getRealTimeData().observe(viewLifecycleOwner) { priceMeal ->
                    addMealToBasketBT.text = priceMeal.price
                }

                addMealToBasketBT.setOnClickListener {
                    Toast.makeText(requireContext(), "${meal.price}", Toast.LENGTH_SHORT).show()

                    val dish = hashMapOf(
                        "price" to w.toString(),
                        "nameMeal" to meal.nameMeal,
                        "amountMeal" to meal.amount
//                        "list" to arrayListOf("${meal.nameMeal}")
                    )

                    db.collection(FirebaseRepository.USERS).document(fbAuth.currentUser!!.uid)
                        .collection(FirebaseRepository.BASKET).document(uidRestaurant)
                        .collection(FirebaseRepository.MEALS).document(random)
                        .set(dish)

                    val bundle = Bundle()
                    bundle.putString(
                        "uidMeal",
                        uidMeal
                    )
                    bundle.putString(
                        "uidRestaurant",
                        uidRestaurant
                    )

                    findNavController().navigate(
                        R.id.action_mealFragment_to_restaurantFragment,
                        bundle
                    )

                    val n = 1

                    db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
                        .collection(FirebaseRepository.MEALS).document(uidMeal)
                        .update("price", x.toString(), "amount", n.toString())
                        .addOnSuccessListener {
                            Log.d("REPO_DEBUG", "Zaktualizowano dane!")
                        }
                        .addOnFailureListener {
                            Log.d("REPO_DEBUG", it.toString())
                        }

                }
            }
        }

        //if you click, return all the data in that meal back as it was
        returnBT.setOnClickListener {
            val x = meal.price.toString().toDouble()
            i = 1

            val bundle = Bundle()
            bundle.putString(
                "uidMeal",
                uidMeal
            )
            bundle.putString(
                "uidRestaurant",
                uidRestaurant
            )

            findNavController().navigate(R.id.action_mealFragment_to_restaurantFragment, bundle)

            db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
                .collection(FirebaseRepository.MEALS).document(uidMeal)
                .update("price", x.toString(), "amount", i.toString())
                .addOnSuccessListener {
                    Log.d("REPO_DEBUG", "Zaktualizowano dane!")
                }
                .addOnFailureListener {
                    Log.d("REPO_DEBUG", it.toString())
                }
        }
    }

    //this function updates my data in real time in firebase
    private fun getRealTimeData(): LiveData<Meal> {
        val uidMeal = requireArguments().getString("uidMeal").toString()
        val uidRestaurant = requireArguments().getString("uidRestaurant").toString()
        val cloudResult = MutableLiveData<Meal>()

        //tworzymy zapytacie do kolekcji, podajemy sciezke czyli naszego user
        db.collection(FirebaseRepository.RESTAURANTS).document(uidRestaurant)
            .collection(FirebaseRepository.MEALS).document(uidMeal)
            .get()//potem uzyskaj ten dokument
            .addOnSuccessListener {
                val meal = it.toObject(Meal::class.java)
                cloudResult.postValue(meal!!)
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }

        return cloudResult
    }


    override fun unsubscribeUi() {

    }
}