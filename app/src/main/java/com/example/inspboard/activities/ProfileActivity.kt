package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.example.inspboard.R
import com.example.inspboard.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(1) {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()

        mFirebaseHelper = FirebaseHelper(this)
        if (mFirebaseHelper.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        Log.d(TAG, "onCreate")
        image_view_leave.setOnClickListener {
            if (mFirebaseHelper.auth.currentUser != null) {
                mFirebaseHelper.auth.signOut()
                startActivity(Intent(this, FeedActivity::class.java))
                finish()
            }
        }
        image_view_edit.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        mFirebaseHelper.getCurrentUserData { user ->
            text_view_username.text = user.name
            text_view_mail_value.text = user.mail
            image_view_profile.setUserPhoto(user.photo)
        }
    }
}