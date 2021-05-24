package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.inspboard.R
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : BaseActivity(1) {
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()
        Log.d(TAG, "onCreate")

        image_view_edit.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}