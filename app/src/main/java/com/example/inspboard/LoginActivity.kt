package com.example.inspboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), TextWatcher {
    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        button_login.isEnabled = false
        edit_text_mail.addTextChangedListener(this)
        edit_text_password.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        button_login.isEnabled =
            edit_text_mail.text.toString().isNotEmpty() &&
            edit_text_password.text.toString().isNotEmpty();
    }
}