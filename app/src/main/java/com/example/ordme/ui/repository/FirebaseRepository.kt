package com.example.ordme.ui.repository


import android.annotation.SuppressLint
import android.util.Log
import com.example.ordme.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseRepository {

    private val fbAuth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private val currentUserId: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        const val RESTAURANTS = "restaurants"
        const val MEALS = "meals"
        const val USERS = "users"
        const val BASKET = "basket"
        const val ORDER = "orders"
    }

    @SuppressLint("RestrictedApi")
    fun createNewUser(user: User) {
        db.collection(USERS)
            .document(user.uid!!)
            .set(user)
    }

    fun update(basket: Basket) {
        if (currentUserId == null || basket.uid == null) return

        db.collection(USERS)
            .document(currentUserId!!)
            .collection(BASKET)
            .document(basket.uid!!)
            .set(basket)
    }

    fun fetchRestaurantMeals(uid: String, onComplete: (ArrayList<Meal>) -> Unit) {
        db.collection(RESTAURANTS).document(uid)
            .collection(MEALS)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {

                    if (error != null) {
                        Log.e("Jest blad", error.message.toString())
                        onComplete.invoke(arrayListOf())
                        return
                    }

                    val mealList = arrayListOf<Meal>()
                    for (dc: DocumentChange in value!!.documentChanges) {

                        //sprawdxzamy czy dokument zostal poprawnie dodany:
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val meal = dc.document.toObject(Meal::class.java)
                            Log.d("Meals", "$meal ")
                            mealList.add(dc.document.toObject(Meal::class.java))
                        }
                        //else if(dc.type == DocumentChange.Type.MODIFIED){

                        // }
                    }

                    onComplete.invoke(mealList)
                }
            })
    }

    fun fetchRestaurantsList(onComplete: (ArrayList<Restaurant>) -> Unit) {
        db.collection(RESTAURANTS)
            .limit(9)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {

                    if (error != null) {
                        Log.e("Jest blad", error.message.toString())
                        onComplete.invoke(arrayListOf())
                        return
                    }

                    val list = arrayListOf<Restaurant>()
                    for (dc: DocumentChange in value!!.documentChanges) {
                        //sprawdxzamy czy dokument zostal poprawnie dodany:
                        if (dc.type == DocumentChange.Type.ADDED) {

                            list.add(dc.document.toObject(Restaurant::class.java))
                        }
                    }

                    onComplete.invoke(list)
                }
            })
    }

    fun fetchRestaurant(uid: String, onComplete: (Restaurant?) -> Unit) {
        db.collection(RESTAURANTS).document(uid)
            .get().addOnSuccessListener { snapshot ->

                Log.d("RestarantFragment", "${snapshot.data} ")

                //przerabiam dane snaphot nadane restauracji jest w formie JSON
                //let -> jezeli optional != null to sie wykona
                snapshot.toObject(Restaurant::class.java)?.let {
                    onComplete.invoke(it)
                    //run -> dziala jak else
                } ?: run {
                    onComplete.invoke(null)
                }
            }
    }

    fun fetchBasketListForCurrentUser(onComplete: (ArrayList<Basket>) -> Unit) {
        if (fbAuth.currentUser == null) {
            onComplete.invoke(arrayListOf())
            return
        }
        db.collection(USERS)
            .document(fbAuth.currentUser!!.uid)
            .collection(BASKET)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("FirebaseRepository", "$error")
                    onComplete.invoke(arrayListOf())
                    return@addSnapshotListener
                }

                val list = arrayListOf<Basket>()
                for (dc: DocumentChange in value!!.documentChanges) {
                    //sprawdxzamy czy dokument zostal poprawnie dodany:
                    if (dc.type == DocumentChange.Type.ADDED) {
                        list.add(dc.document.toObject(Basket::class.java))
                    }
                }
                onComplete.invoke(list)
            }
    }

    fun fetchMeal(uidRestaurant: String, uidMeal: String, onComplete: (Meal?) -> Unit) {
        //download date from firebase with uid restaurant and meal
        db.collection(RESTAURANTS).document(uidRestaurant)
            .collection(MEALS).document(uidMeal)
            .get().addOnSuccessListener { snapshot ->

                Log.d("RestarantFragment", "${snapshot.data} ")

                //I pulled a snapshot of the data and if it is true i let to download data
                snapshot.toObject(Meal::class.java)?.let {
                    onComplete.invoke(it)

                    //it is the same as else
                } ?: run {
                    onComplete.invoke(null)
                }
            }
    }

    fun fetchBasket(uid: String, onComplete: (Basket?) -> Unit) {
        if (currentUserId == null) {
            onComplete.invoke(null)
            return
        }

        db.collection(USERS).document(currentUserId!!)
            .collection(BASKET).document(uid)
            .get().addOnSuccessListener { snapshot ->
                snapshot.toObject(Basket::class.java)?.let {
                    Log.d("REPO FetchBasket", it.toString())
                    onComplete.invoke(it)
                    //it is the same as else
                } ?: run {
                    onComplete.invoke(null)
                }
            }
            .addOnFailureListener {
                Log.d("REPO", it.toString())
            }
    }

    fun fetchAdditions(uidRestaurant: String, uidMeal: String, onComplete: (Meal) -> Unit) {
        db.collection(RESTAURANTS).document(uidRestaurant)
            .collection(MEALS).document(uidMeal)
            .get().addOnSuccessListener { snapshot ->
                snapshot.toObject(Meal::class.java)?.let {
                    Log.d("REPO FetchAdditions", it.toString())
                    onComplete.invoke(it)

                }
            }
            .addOnFailureListener {
                Log.d("REPO", it.toString())
            }
    }

    fun addTotalPrice(basket: String, totalPrice: HashMap<String, Double>) {
        db.collection(USERS).document(currentUserId!!)
            .collection(BASKET).document(basket)
            .update(totalPrice as Map<String, Any>)
    }

    fun submitOrder(order: Order, uidRestaurant: String, uid: String) {
        if (currentUserId == null) return

        db.collection(RESTAURANTS)
            .document(uidRestaurant)
            .collection(ORDER)
            .document(uid)
            .set(order)

    }

    fun fetchUser(onComplete: (User?) -> Unit) {
        if (currentUserId == null) {
            onComplete.invoke(null)
            return
        }

            db.collection(USERS)
                .document(currentUserId!!)
                .get().addOnSuccessListener { snapshot ->
                    snapshot.toObject(User::class.java)?.let {
                        Log.d("REPO FetchUser", "${snapshot.data}")
                        onComplete.invoke(it)
                        //it is the same as else
                    } ?: run {
                        onComplete.invoke(null)
                    }
                }
                .addOnFailureListener {
                    Log.d("REPO", it.toString())
                }
    }

}