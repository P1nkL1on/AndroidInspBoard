package com.example.inspboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logout.*

class LogoutActivity : BaseActivity(2) {
    private val TAG = "LogoutActivity"
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")

        mAuth = FirebaseAuth.getInstance();
        updateButtonsEnable()

        button_login.setOnClickListener {
            if (mAuth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        button_logout.setOnClickListener {
            if (mAuth.currentUser != null) {
                mAuth.signOut()
            }
        }
        mAuth.addAuthStateListener {
            updateButtonsEnable()
        }
    }

    private fun updateButtonsEnable() =
        (mAuth.currentUser == null).also {
            button_login.isEnabled = it
            button_logout.isEnabled = !it
        }
}