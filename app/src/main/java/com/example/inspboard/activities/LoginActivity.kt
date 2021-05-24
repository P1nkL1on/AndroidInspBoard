package com.example.inspboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.inspboard.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        button_login.isEnabled = false
        edit_text_mail.addTextChangedListener(this)
        edit_text_password.addTextChangedListener(this)
        button_login.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {
        val email = edit_text_mail.text.toString()
        val password = edit_text_password.text.toString()
        if (!isMailAndPasswordOk(email, password)) {
            showToast( "Please enter email and password")
            return
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            startActivity(Intent(this, LogoutActivity::class.java))
            finish()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        button_login.isEnabled = isMailAndPasswordOk(
            edit_text_mail.text.toString(),
            edit_text_password.text.toString())
    }

    private fun isMailAndPasswordOk(mail: String, password: String) =
        mail.isNotEmpty() && password.isNotEmpty()
}