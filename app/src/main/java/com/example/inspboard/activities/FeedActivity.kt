package com.example.inspboard.activities

import android.os.Bundle
import android.util.Log
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.ValueEventListenerAdapter
import com.google.firebase.auth.FirebaseAuth


class FeedActivity : BaseActivity(0) {
    private val TAG = "FeedActivity"
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")
        setUpBottomNavigation()

        mFirebase = FirebaseHelper(this)
        if (mFirebase.currentUser() == null)
            return
        mFirebase.currentUserPosts().addListenerForSingleValueEvent(
            ValueEventListenerAdapter { it ->
                val posts = it.children.map { it.getValue(Post::class.java)!! }
                Log.d(TAG, "posts: ${posts.joinToString("\n", "\n")}")
            }
        )
    }
}