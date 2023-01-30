package com.example.ordme.ui.screen

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.example.ordme.data.model.Meal
import com.example.ordme.data.model.Restaurant
import com.example.ordme.ui.adapter.SearchViewAdapter
import com.example.ordme.ui.repository.FirebaseRepository
import com.example.ordme.ui.view_model.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search_view.*
import java.util.*
import kotlin.collections.ArrayList

class SearchViewFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_search_view

    private var restaurantList = ArrayList<Restaurant>()
    private lateinit var adapter: SearchViewAdapter

    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun subscribeUi() {

        recyclerViewSearchView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewSearchView.setHasFixedSize(true)
        //inicjujemy nasza liste:
        restaurantList = arrayListOf()
        adapter = SearchViewAdapter(restaurantList, requireView())
        recyclerViewSearchView.adapter = adapter


        viewModel.restaurantList.observe(this) {
            adapter.restaurantList = it
            adapter.notifyDataSetChanged()



            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText == "") {
                        adapter.setFilteredList(it)
                    }
                    filterList(newText)

                    return true
                }

            })
        }

        viewModel.fetchRestaurantsList()
        viewModel.fetchBasketList()

    }

    private fun filterList(query: String?) {
        val filteredList = ArrayList<Restaurant>()
        if (query != null) {
            for (i in adapter.restaurantList) {
                if (i.nameRestaurant?.lowercase(Locale.ROOT)!!.contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }


    override fun unsubscribeUi() {
        viewModel.restaurantList.removeObservers(this)
    }
}