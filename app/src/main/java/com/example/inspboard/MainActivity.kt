package com.example.inspboard

import android.content.Intent
import android.os.Bundle
import android.util.Log

class MainActivity : BaseActivity(0) {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}