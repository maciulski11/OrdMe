package com.example.ordme.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.ordme.R

abstract class BaseFragment: Fragment() {

    protected abstract val layout : Int
    private var loadingIndicator: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingIndicator = view.findViewById(R.id.loading_indicator)
        subscribeUi()
    }

    abstract fun subscribeUi()

    abstract fun unsubscribeUi()

    fun loadingData(isLoading: Boolean) {
        loadingIndicator?.show(isLoading)
    }
}

fun View.show(visible: Boolean) {
    this.visibility = if(visible) View.VISIBLE else View.GONE
}