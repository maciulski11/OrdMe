package com.example.ordme.ui.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.ordme.data.model.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseRepository {

    private val fbAuth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val currentUserId: String?
        get() = fbAuth.currentUser?.uid

    companion object {
        const val RESTAURANTS = "restaurants"
        const val MEALS = "meals"
        const val USERS = "users"
        const val BASKET = "basket"
        const val ORDER = "orders"
        const val PHOTO = "photo"
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

    fun updateUserData(success: () -> Unit) {
        val docRef = db.collection(USERS).document(currentUserId!!)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Update your UI with the new data here
                success()
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    fun updateRestaurants(success: () -> Unit) {
        val docRef = db.collection(RESTAURANTS)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Update your UI with the new data here
                success()
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    fun updateMealsInRestaurant(uidRestaurant: String, success: () -> Unit) {
        val docRef = db.collection(RESTAURANTS).document(uidRestaurant)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Update your UI with the new data here
                success()
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
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

    fun updateUser(map: Map<String, Long>) {
        db.collection("users")
            .document(fbAuth.currentUser!!.uid)
            .update(map)
            .addOnSuccessListener {
                Log.d("REPO_DEBUG", "Zaktualizowano dane!")
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    @SuppressLint("MissingPermission")
    fun checkConnectivityAndFirestoreAvailability(context: Context, success: () -> Unit, failure: () -> Unit) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Internet is available
            FirebaseFirestore.getInstance().runBatch {
                // Perform a dummy write operation to Firestore
                setOf(
                    FirebaseFirestore.getInstance().collection("test").document(),
                    mapOf("test" to "test")
                )
            }.addOnSuccessListener {
                // Firestore is available
                success()
            }.addOnFailureListener {
                // Firestore is not available
                failure()
            }
        }
    }

    //wczytanie zdjecia
    fun uploadUserPhoto(bytes: ByteArray) {
        storage.getReference("photoUser")
            .child("${currentUserId!!}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener {
                Log.d("REPO_DEBUG", "COMPLETE UPLOAD PHOTO")
            }
            .addOnSuccessListener {
                getPhotoDownloadUrl(it.storage)

            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    private fun getPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener {
                updateUserPhoto(it.toString())
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    private fun updateUserPhoto(url: String?) {
        db.collection(USERS)
            .document(currentUserId!!)
            .update(PHOTO, url)
            .addOnSuccessListener {
                Log.d("REPO_DEBUG", "UPDATE USER PHOTO!")
            }
            .addOnFailureListener {
                Log.d("REPO_DEBUG", it.message.toString())
            }
    }

    fun mapLocation(context: Context, childFragmentManager: FragmentManager, layoutId: Int) {
        val mapFragment =
            childFragmentManager.findFragmentById(layoutId) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            FirebaseRepository().fetchUser {
                val geocoder = Geocoder(context)
                val address = geocoder.getFromLocationName(
                    "${it?.postCode}, ${it?.city}, ${it?.street}, ${it?.door}", 1
                )

                if (address!!.size > 0) {
                    val latitude = address[0].latitude
                    val longitude = address[0].longitude

                    val latLng = LatLng(latitude, longitude)
                    googleMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title("${it?.street} ${it?.door}/${it?.flat}")
                    )
                        .showInfoWindow()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.6f), 1700, null)

                    googleMap.setOnMarkerClickListener {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.6f), 1700, null)
                        true
                    }
                }
            }
        }
    }

}