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
        mAuth.signOut()

        button_login.setOnClickListener {
            if (mAuth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

//        mAuth.signInWithEmailAndPassword("user@gmail.com", "123456")
//            .addOnCompleteListener{
//                if (it.isSuccessful) {
//                    Log.d(TAG, "sign in success")
//                } else {
//                    Log.d(TAG, "sign in err", it.exception)
//                }
//            }
    }
}