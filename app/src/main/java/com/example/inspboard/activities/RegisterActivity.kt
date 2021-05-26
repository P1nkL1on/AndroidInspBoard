package com.example.inspboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.example.inspboard.utils.FirebaseHelper

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {
    private val TAG = "RegisterActivity"
    private lateinit var mEmail: String
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // todo: make buttons in layout connected to text
        //  edit rather than bottom because opf scrolling in keyboard mode
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebase = FirebaseHelper(this)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_layout, EmailFragment())
                .commit()
    }

    override fun onNext(email: String) {
        if (email.isEmpty()) {
            showToast("Please enter email")
            return
        }
        mFirebase.verifyEmailIsUnique(email) {
            mEmail = email
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, NamePassFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onRegister(name: String, password: String) {
        if (name.isEmpty() || password.isEmpty()) {
            showToast("Please enter name and password")
            return
        }
        if (mEmail.isEmpty()) {
            Log.e(TAG, "onRegister: mail is empty!", )
            showToast("Please enter mail")
            supportFragmentManager.popBackStack()
        }
        mFirebase.createUserWithEmailAndPassword(mEmail, password) {
            // todo: add name conversion
            val user = User(
                name = name,
                mail = mEmail
            )
            mFirebase.createUser(it.user!!.uid, user) {
                showToast("Registered successfully!")
                startProfileActivity()
            }
        }
    }

    private fun startProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}



