package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.ui.adapter.BasketAdapter
import com.example.ordme.ui.data.Basket
import com.example.ordme.ui.data.Restaurant
import com.example.ordme.ui.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketFragment: BaseFragment() {
    override val layout: Int = R.layout.fragment_basket

    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: BasketAdapter
    private lateinit var basketList: ArrayList<Basket>
    private lateinit var db: FirebaseFirestore

    override fun subscribeUi() {

        recyclerViewBasket.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewBasket.setHasFixedSize(true)

        basketList = arrayListOf()

        adapter = BasketAdapter(basketList)
        recyclerViewBasket.adapter = adapter

        downloadDataFromFirebase()

//        val uid = requireArguments().getString("uidRestaurant").toString()
//
//        textView9.text = uid


        //it transferred data from FragmentMeal to FragmentBasket and this lambda loaded these data without used navigate
//        parentFragmentManager.setFragmentResultListener("dataRestaurantAndMeal", this,
//            FragmentResultListener { _, result ->
//
//                val uidRestaurant = result.getString("uidRestaurant")
//                textView9.text = uidRestaurant.toString()
//            })



//        db.collection(FirebaseRepository.RESTAURANTS).document()
//            .collection(FirebaseRepository.MEALS).document(uidMeal)
//            .get().addOnSuccessListener { snapshot ->
//
//                Log.d("RestarantFragment", "${snapshot.data} ")
//
//                //I pulled a snapshot of the data and if it is true i let to download data
//                snapshot.toObject(Meal::class.java)?.let {
//                    meal = it
//                    nameMealTV.text = meal.nameMeal
//                    addMealToBasketBT.text = meal.price
//                    amountTV.text = "1"
//
//                    //it is the same as else
//                } ?: run {
//                    nameMealTV.text = "Null"
//                }

    }

    private fun downloadDataFromFirebase(){
        db = FirebaseFirestore.getInstance()
        db.collection(FirebaseRepository.USERS).document(fbAuth.currentUser!!.uid)
            .collection(FirebaseRepository.BASKET).document("8Bq9W4OUzH3JZLyFlt8O")
            .collection(FirebaseRepository.MEALS)
//            .limit(9)
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

                            basketList.add(dc.document.toObject(Basket::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }

    override fun unsubscribeUi() {

    }
}