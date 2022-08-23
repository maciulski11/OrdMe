package com.example.ordme.ui.user.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.restaurant.data.Restaurants
import com.example.ordme.ui.user.adapter.MainUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_main_user.*

class MainUserFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_main_user

    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: MainUserAdapter
    private lateinit var restaurantsList: ArrayList<Restaurants>
    private lateinit var db: FirebaseFirestore

    override fun subscribeUi() {
        recyclerViewChooseRestaurant.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseRestaurant.setHasFixedSize(true)

        //inicjujemy nasza liste:
        restaurantsList = arrayListOf()

        adapter = MainUserAdapter(restaurantsList)
        recyclerViewChooseRestaurant.adapter = adapter

        downloadDataFromFirebase()
    }

    private fun downloadDataFromFirebase(){
        db = FirebaseFirestore.getInstance()
        db.collection("restaurants")
            .limit(9)
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

                            restaurantsList.add(dc.document.toObject(Restaurants::class.java))
                        }
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
        inflater.inflate(R.menu.logout_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutUserAction -> {
                fbAuth.signOut()
                findNavController().navigate(R.id.action_mainUserFragment_to_loginUserFragment)
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}