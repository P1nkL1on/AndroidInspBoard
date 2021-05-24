package com.example.inspboard.views

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.inspboard.R
import kotlinx.android.synthetic.main.dialog_password.view.*

class PasswordDialog : DialogFragment() {
    private lateinit var mListener: Listener
    interface Listener {
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = activity as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                mListener.onPasswordConfirm(view.edit_text_password_confirmation.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                // send pass
            }
            .setTitle(getString(R.string.enter_password))
            .create()
    }
}