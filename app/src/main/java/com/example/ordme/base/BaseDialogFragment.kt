package com.example.ordme.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.ordme.R
import com.example.ordme.extensions.addFullScreenFlags

abstract class BaseDialogFragment: DialogFragment() {

    /**
     * Current dialog layout.
     */
    @get:LayoutRes
    protected abstract val layout: Int

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        isCancelable = true
        window?.setWindowAnimations(R.style.DialogAnimation)
    }.addFullScreenFlags()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(layout, container, false)

    fun show(activity: Activity): BaseDialogFragment {
        val supportFragmentManager = (activity as FragmentActivity).supportFragmentManager
        if (supportFragmentManager.findFragmentByTag(javaClass.canonicalName) != null) return this
        show(supportFragmentManager, javaClass.canonicalName)
        return this
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}