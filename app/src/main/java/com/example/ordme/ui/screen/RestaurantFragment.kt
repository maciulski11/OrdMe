package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.RestaurantAdapter
import com.example.ordme.ui.data.Meal
import com.example.ordme.ui.data.Restaurant
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_restaurant.*
import kotlinx.android.synthetic.main.item_meal.view.*

class RestaurantFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_restaurant

    private var db = FirebaseFirestore.getInstance()
    private var restaurant = Restaurant()
    private lateinit var mealsList: ArrayList<Meal>
    private lateinit var adapter: RestaurantAdapter


    override fun subscribeUi() {

        val uid = requireArguments().getString("uidRestaurant")

        recyclerViewChooseDish.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseDish.setHasFixedSize(true)

        mealsList = arrayListOf()

        adapter = RestaurantAdapter(mealsList)
        recyclerViewChooseDish.adapter = adapter

        db.collection(FirebaseRepository.RESTAURANTS).document(uid.toString())
            .get().addOnSuccessListener { snapshot ->

                Log.d("RestarantFragment", "${snapshot.data} ")

                //przerabiam dane snaphot nadane restauracji jest w formie JSON
                //let -> jezeli optional != null to sie wykona
                snapshot.toObject(Restaurant::class.java)?.let {
                    restaurant = it
                    textView7.text = restaurant.nameRestaurant
                    textView8.text = restaurant.uid
                    //run -> dziala jak else
                } ?: run {
                    textView7.text = "Empty"
                }
            }

        db.collection(FirebaseRepository.RESTAURANTS).document(uid.toString())
            .collection(FirebaseRepository.MEALS)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {

                    if (error != null) {
                        Log.e("Jest blad", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value!!.documentChanges) {

                        //sprawdxzamy czy dokument zostal poprawnie dodany:
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val meal = dc.document.toObject(Meal::class.java)
                            Log.d("Meals", "$meal ")
                            mealsList.add(dc.document.toObject(Meal::class.java))

                        }
                        //else if(dc.type == DocumentChange.Type.MODIFIED){

                       // }
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.basket_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.basket -> {
                findNavController().navigate(R.id.action_restaurantFragment_to_basketFragment)
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}