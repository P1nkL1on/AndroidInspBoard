package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.inspboard.R
import com.example.inspboard.utils.Camera
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.ImagesAdapter
import com.example.inspboard.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(2) {
    private val TAG = "ProfileActivity"
    private lateinit var mCamera: Camera
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()

        mFirebase = FirebaseHelper(this)
        mCamera = Camera(this)

        if (mFirebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        Log.d(TAG, "onCreate")
        image_view_leave.setOnClickListener {
            if (mFirebase.auth.currentUser != null) {
                mFirebase.auth.signOut()
                startActivity(Intent(this, FeedActivity::class.java))
                finish()
            }
        }
        image_view_edit.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }
        button_add_photo.setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
        }
        recycler_view_images.layoutManager = GridLayoutManager(this, 3)
        mFirebase.currentUserImages().addValueEventListener(ValueEventListenerAdapter{ it ->
            val images = it.children.map {it.getValue(String::class.java)!!}
            // todo: its *12 is special
            recycler_view_images.adapter = ImagesAdapter(images + images + images + images + images + images + images + images + images + images + images + images + images + images)
        })
    }

    override fun onResume() {
        super.onResume()
        mFirebase.currentUserData { user ->
            text_view_username.text = user.name
            text_view_mail_value.text = user.mail
            image_view_profile.loadImage(user.photo)
        }
    }
}

