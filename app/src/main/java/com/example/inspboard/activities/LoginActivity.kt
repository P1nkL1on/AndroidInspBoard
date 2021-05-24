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

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        enableButtonIfAllTextsNonEmpty(button_login, edit_text_mail, edit_text_password)
        mAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener(this)
        text_view_sign_up.setOnClickListener(this)
    }

    private fun isMailAndPasswordOk(mail: String, password: String) =
        mail.isNotEmpty() && password.isNotEmpty()

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_login -> {
                val email = edit_text_mail.text.toString()
                val password = edit_text_password.text.toString()
                if (!isMailAndPasswordOk(email, password)) {
                    showToast( "Please enter email and password")
                    return
                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (!it.isSuccessful) {
                        showToast("Incorrect mail or password!")
                        return@addOnCompleteListener
                    }
                    startActivity(Intent(this, LogoutActivity::class.java))
                    finish()
                }
            }
            R.id.text_view_sign_up -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}