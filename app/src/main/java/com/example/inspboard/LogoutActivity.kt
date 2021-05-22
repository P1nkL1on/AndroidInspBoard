package com.example.inspboard

import android.os.Bundle
import android.util.Log


class LogoutActivity : BaseActivity(2) {
    private val TAG = "LogoutActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}