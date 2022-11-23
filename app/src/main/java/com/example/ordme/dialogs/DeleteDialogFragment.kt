package com.example.ordme.dialogs

import android.os.Bundle
import android.view.View
import com.example.ordme.R
import com.example.ordme.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_delete.*

class DeleteDialogFragment(private val action: () -> Unit): BaseDialogFragment() {
    override val layout = R.layout.dialog_delete

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        delete.setOnClickListener {
            action()
            dismiss()
        }

        back.setOnClickListener {
            dismiss()
        }
    }

}