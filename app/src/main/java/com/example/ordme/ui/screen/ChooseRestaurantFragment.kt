package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Restaurant
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.view.*
import kotlinx.android.synthetic.main.item_choose_restaurants.*
import kotlinx.android.synthetic.main.item_choose_restaurants.icon
import kotlinx.android.synthetic.main.item_marker_info.*
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

    @SuppressLint("MissingInflatedId")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Pobierz referencję do kolekcji markerów w bazie Firestore
        val markersRef = FirebaseFirestore.getInstance().collection(FirebaseRepository.RESTAURANTS)

        // Dodaj nasłuch na zmiany w kolekcji markerów
        markersRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            if (querySnapshot != null) {

                // Przetwórz dokumenty, aby utworzyć listę obiektów MarkerOptions
                val markerOptionsList = querySnapshot.documents
                    .filterNotNull()
                    .map { document ->

                        val address = document.getString("address") ?: ""
                        val city = document.getString("city") ?: ""
                        val title = document.getString("nameRestaurant") ?: ""
                        val uid = document.getString("uid") ?: ""

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
                                .snippet(uid)

                        } else {

                            null
                        }
                    }

                // Dodaj markery z listy MarkerOptions do mapy
                for (markerOptions in markerOptionsList) {
                    mMap.addMarker(markerOptions)
                }

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(51.123294, 16.989469), 12.8f),
                    800,
                    null
                )

                // Przygotowanie ikon markerów
                val defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                val selectedMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)

                // Przechowanie aktualnie zaznaczonego markera
                var selectedMarker: Marker? = null

                mMap.setOnMarkerClickListener { marker ->

                    // Resetowanie poprzednio zaznaczonego markera do domyślnej ikony
                    selectedMarker?.setIcon(defaultMarkerIcon)

                    // Ustawienie nowego zaznaczonego markera na niebieską ikonę
                    marker.setIcon(selectedMarkerIcon)
                    selectedMarker = marker

                    FirebaseRepository().fetchRestaurant(marker.snippet) { restaurant ->

                        val dialogView = layoutInflater.inflate(R.layout.item_marker_info, null)
                        val markerNameTextView =
                            dialogView.findViewById<TextView>(R.id.markerNameTextView)
                        val icon = dialogView.findViewById<ImageView>(R.id.iconMarkerDialog)
                        val button = dialogView.findViewById<Button>(R.id.goButton)
                        markerNameTextView.text = marker.title

                        Glide.with(requireContext())
                            .load(restaurant?.icon)
                            .override(230, 230)
                            .circleCrop()
                            .into(icon)

                        // worzenie okna dialogowego
                        val builder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setCancelable(true)

                        val dialog = builder.create()

                        // Wyłączenie zaciemnienia ekranu
                        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                        // Wyświetlanie okna dialogowego na dole ekranu
                        val layoutParams = dialog.window?.attributes
                        layoutParams?.gravity = Gravity.BOTTOM
                        layoutParams?.y = 50
                        dialog.window?.attributes = layoutParams
                        dialog.show()

                        button.setOnClickListener {

                            // Ukrywanie okna dialogowego
                            dialog.dismiss()

                            val bundle = Bundle()
                            bundle.putString(
                                "uidRestaurant",
                                restaurant?.uid
                            )

                            findNavController().navigate(
                                R.id.action_chooseRestaurantFragment_to_restaurantFragment,
                                bundle
                            )
                        }
                    }

                    true
                }
            }
        }
    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}