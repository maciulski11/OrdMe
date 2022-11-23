package com.example.ordme.extensions

import android.app.Dialog
import android.view.Window
import android.view.WindowManager

fun Dialog.addFullScreenFlags(): Dialog {
    window?.addFullScreenFlags()
    return this
}

fun Window.addFullScreenFlags() {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    requestFeature(Window.FEATURE_NO_TITLE)
    setBackgroundDrawableResource(android.R.color.transparent)
    setClipToOutline(false)
}