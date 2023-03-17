package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.Glide
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Restaurant
import com.example.ordme.ui.adapter.ChooseRestaurantAdapter
import com.example.ordme.services.FirebaseRepository
import com.example.ordme.ui.activity.MainActivity
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.*
import kotlinx.android.synthetic.main.fragment_choose_restaurant.view.*
import kotlinx.android.synthetic.main.item_choose_restaurants.*
import kotlinx.android.synthetic.main.item_marker_info.*
import java.util.*


class ChooseRestaurantFragment : BaseFragment(), OnMapReadyCallback {
    override val layout: Int = R.layout.fragment_choose_restaurant

    private var restaurantsList = ArrayList<Restaurant>()
    private lateinit var adapter: ChooseRestaurantAdapter

    private lateinit var mMap: GoogleMap

    private val viewModel: MainViewModel by activityViewModels()


    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {
        recyclerViewChooseRestaurant.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewChooseRestaurant.setHasFixedSize(true)

        // We initialize our user list:
        restaurantsList = arrayListOf()

        adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
        recyclerViewChooseRestaurant.adapter = adapter

        viewModel.restaurantList.observe(this) {
            adapter.restaurantList = it
            adapter.notifyDataSetChanged()
        }


        FirebaseRepository().updateRestaurants {
            viewModel.fetchRestaurantsList()
            viewModel.fetchBasketList()

            find11_icon.visibility = View.VISIBLE
//            progressdsdvBar.visibility = View.INVISIBLE

//            progress_bar_layout.visibility = View.GONE
//            view_layout.visibility = View.VISIBLE
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

            val scrollViewMarginTop = nestedScrollView.layoutParams as MarginLayoutParams
            scrollViewMarginTop.topMargin = 0
//            nestedScrollView.layoutParams = layoutParams

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

            val scrollViewMarginTop = nestedScrollView.layoutParams as MarginLayoutParams
            scrollViewMarginTop.topMargin = 110

            expandMapButton.visibility = View.VISIBLE
            reducedMapButton.visibility = View.GONE
            recyclerViewChooseRestaurant.visibility = View.VISIBLE

        }

        all.setOnClickListener {
            viewModel.fetchRestaurantsList()
        }

        italy.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection(FirebaseRepository.RESTAURANTS)
            val category = "italy" // kategoria wybrana przez użytkownika
            val query = collectionRef.whereEqualTo("category", category)
            query.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    restaurantsList.clear()
                    for (document in documents) {
                        val restaurant = document.toObject(Restaurant::class.java)
                        if (restaurant.category == category) {
                            restaurantsList.add(restaurant)
                        }
                    }
                    adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
                    recyclerViewChooseRestaurant.adapter = adapter
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Wybrana kategoria: $category", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Nie znaleziono restauracji w kategorii $category", Toast.LENGTH_SHORT).show()
                }
            }
        }

        american.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection(FirebaseRepository.RESTAURANTS)
            val category = "american" // kategoria wybrana przez użytkownika
            val query = collectionRef.whereEqualTo("category", category)
            query.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    restaurantsList.clear()
                    for (document in documents) {
                        val restaurant = document.toObject(Restaurant::class.java)
                        if (restaurant.category == category) {
                            restaurantsList.add(restaurant)
                        }
                    }
                    adapter = ChooseRestaurantAdapter(requireContext(), restaurantsList, requireView())
                    recyclerViewChooseRestaurant.adapter = adapter
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Wybrana kategoria: $category", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Nie znaleziono restauracji w kategorii $category", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    @SuppressLint("MissingInflatedId")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Przygotowanie wektorowego pliku SVG jako Drawable
        val vectorDrawableBlack = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_location_24,
            null
        )

        // Przekonwertowanie wektorowego pliku SVG na bitmapę
        val bitmapBlack = Bitmap.createBitmap(
            vectorDrawableBlack!!.intrinsicWidth + 40,
            vectorDrawableBlack.intrinsicHeight + 40,
            Bitmap.Config.ARGB_8888
        )

        val canvasBlack = Canvas(bitmapBlack)
        vectorDrawableBlack.setBounds(0, 0, canvasBlack.width + 7, canvasBlack.height + 7)
        vectorDrawableBlack.draw(canvasBlack)

        // Przygotowanie wektorowego pliku SVG jako Drawable
        val vectorDrawableRed = VectorDrawableCompat.create(
            resources,
            R.drawable.ic_baseline_location_red_24,
            null
        )

        val bitmapRed = Bitmap.createBitmap(
            vectorDrawableRed!!.intrinsicWidth + 40,
            vectorDrawableRed.intrinsicHeight + 40,
            Bitmap.Config.ARGB_8888
        )

        val canvasRed = Canvas(bitmapRed)
        vectorDrawableRed.setBounds(0, 0, canvasRed.width + 7, canvasRed.height + 7)
        vectorDrawableRed.draw(canvasRed)

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
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmapBlack))
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

                // Przechowanie aktualnie zaznaczonego markera
                var selectedMarker: Marker? = null

                mMap.setOnMarkerClickListener { marker ->

                    // Resetowanie poprzednio zaznaczonego markera do domyślnej ikony
                    selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapBlack))

                    // Ustawienie nowego zaznaczonego markera na czerwona ikonę
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapRed))
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

                mMap.setOnMapClickListener {

                    selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapBlack))

                }
            }
        }
    }

    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}