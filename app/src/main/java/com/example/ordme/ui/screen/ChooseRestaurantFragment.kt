package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.api.pojo.Meal
import com.example.ordme.api.pojo.MealList
import com.example.ordme.api.retrofit.RetrofitInstance
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Restaurant
import com.example.ordme.databinding.FragmentChooseRestaurantBinding
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChooseRestaurantFragment : BaseFragment(), OnMapReadyCallback {
    override val layout: Int = R.layout.fragment_choose_restaurant

//    private lateinit var binding: FragmentChooseRestaurantBinding

    private var restaurantsList = ArrayList<Restaurant>()
    private lateinit var adapter: ChooseRestaurantAdapter

    private lateinit var mMap: GoogleMap

    private val viewModel: MainViewModel by activityViewModels()

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentChooseRestaurantBinding.inflate(inflater, container, false)
//        return binding.root
//    }

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {
        recyclerViewChooseRestaurant.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseRestaurant.setHasFixedSize(true)

        //inicjujemy nasza liste:
        restaurantsList = arrayListOf()

        adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
        recyclerViewChooseRestaurant.adapter = adapter

        //API: uzywamy instancji modernizacji zeby wywolac losowy posilek
//        RetrofitInstance.api.getRandomMeal()
//            .enqueue(object : Callback<MealList> { //uzywamy wywolania zwrotnego z callback
//                override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
//                    if (response.body() != null) { //spr. czy callback nie jest nullem
//
//                        //dajemy meals i index 0 poniewaz bierzemy tylko 1 posilek
//                        val randomMeal: Meal = response.body()!!.meals[0]
//
//                        Glide.with(this@ChooseRestaurantFragment)
//                            .load(randomMeal.strMealThumb)
//                            .into(binding.rawndomMeal)
//
//
//                        Log.d("TEST", "meal id ${randomMeal.idMeal} name ${randomMeal.strMeal}")
//                    } else {
//                        return
//                    }
//                }
//
//                override fun onFailure(call: Call<MealList>, t: Throwable) {
//                    Log.d("ChooseRestaurantFragment", t.message.toString())
//                }
//
//            })


        viewModel.restaurantList.observe(this) {
            adapter.restaurantList = it
            adapter.notifyDataSetChanged()
        }

        FirebaseRepository().updateRestaurants {
            viewModel.fetchRestaurantsList()
            viewModel.fetchBasketList()
        }

        //hiding the shifter
        horizontalScrollView.isHorizontalScrollBarEnabled = false

        searchRestaurant.setOnClickListener {
            findNavController().navigate(R.id.action_chooseRestaurantFragment_to_searchViewFragment2)
        }

        // Uzyskaj referencję do widoku MapFragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)// Asynchronicznie uzyskaj mapę Google, gdy jest gotowa

        expandMapButton.setOnClickListener {
            val layoutParams = mapFragment.view?.layoutParams
            layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            mapFragment.view?.layoutParams = layoutParams

            expandMapButton.visibility = View.GONE
            reducedMapButton.visibility = View.VISIBLE
            recyclerViewChooseRestaurant.visibility = View.GONE
        }

        reducedMapButton.setOnClickListener {
            val layoutParams = mapFragment.view?.layoutParams
            val heightDp = 220
            val heightPx = (heightDp * resources.displayMetrics.density).toInt()

            layoutParams?.height = heightPx
            mapFragment.view?.layoutParams = layoutParams

            expandMapButton.visibility = View.VISIBLE
            reducedMapButton.visibility = View.GONE
            recyclerViewChooseRestaurant.visibility = View.VISIBLE
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Pobierz referencję do kolekcji markerów w bazie Firestore
        val markersRef = FirebaseFirestore.getInstance().collection(FirebaseRepository.RESTAURANTS)
        val prefs = requireContext().getSharedPreferences("markers", Context.MODE_PRIVATE)


            // Dodaj nasłuch na zmiany w kolekcji markerów
            markersRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {

                    Toast.makeText(requireContext(), "lipa", Toast.LENGTH_SHORT).show()

                    // Przetwórz dokumenty, aby utworzyć listę obiektów MarkerOptions
                    val markerOptionsList = querySnapshot.documents
                        .filterNotNull()
                        .map { document ->

                            val address = document.getString("address") ?: ""
                            val city = document.getString("city") ?: ""
                            val title = document.getString("nameRestaurant") ?: ""

                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val addressList =
                                geocoder.getFromLocationName("$city, $address", 1)
                            if (addressList!!.isNotEmpty()) {
                                val lat = addressList[0].latitude
                                val lng = addressList[0].longitude
                                val latLng = LatLng(lat, lng)

                                MarkerOptions()
                                    .position(latLng)
                                    .title(title)

                            } else {

                                null
                            }
                        }

                    // Dodaj markery z listy MarkerOptions do mapy
                    for (markerOptions in markerOptionsList) {
                        mMap.addMarker(markerOptions)
                    }

                    //add markers to file of json in SharedPreferences
                    val addMarkersJson = Gson().toJson(markerOptionsList)
                    prefs.edit().putString("markers", addMarkersJson).apply()

                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(51.123294, 16.989469), 12.8f),
                        800,
                        null
                    )
                }

                    val markersJson = prefs.getString("markers", null)
                    if (markersJson != null) {
                        // Wczytaj markery z SharedPreferences
                        val markerOptionsList =
                            Gson().fromJson(markersJson, Array<MarkerOptions>::class.java).toList()
                        Toast.makeText(requireContext(), "udalo sie", Toast.LENGTH_SHORT).show()
                        // Dodaj markery do mapy
                        for (markerOptions in markerOptionsList) {
                            mMap.addMarker(markerOptions)
                        }

                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(51.123294, 16.989469), 12.8f),
                            800,
                            null
                        )
                    }


        }
    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}