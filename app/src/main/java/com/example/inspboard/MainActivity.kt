package com.example.inspboard

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth;

class MainActivity : BaseActivity(0) {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")

        val auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("user@gmail.com", "123456")
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    Log.d(TAG, "sign in success")
                } else {
                    Log.d(TAG, "sign in err", it.exception)
                }
            }
    }
}