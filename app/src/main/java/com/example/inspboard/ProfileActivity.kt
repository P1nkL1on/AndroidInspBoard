package com.example.inspboard

import android.os.Bundle
import android.util.Log


class ProfileActivity : BaseActivity(1) {
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}