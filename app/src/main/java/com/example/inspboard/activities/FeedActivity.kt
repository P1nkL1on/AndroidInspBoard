package com.example.inspboard.activities

import android.os.Bundle
import android.util.Log
import com.example.inspboard.R


class FeedActivity : BaseActivity(0) {
    private val TAG = "FeedActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}