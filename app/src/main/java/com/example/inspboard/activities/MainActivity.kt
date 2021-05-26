package com.example.inspboard.activities

import android.os.Bundle
import android.util.Log
import com.example.inspboard.R

class MainActivity : BaseActivity(0) {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

//        setUpBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}