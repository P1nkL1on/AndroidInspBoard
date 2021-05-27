package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import com.example.inspboard.utils.*
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(2), PostViewer {
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
        recycler_view_images.layoutManager = GridLayoutManager(this, 3)
        mFirebase.currentUserPosts().addValueEventListener(ValueEventListenerAdapter{ it ->
            val posts = it.children.map { it.asPost()!! }
                .sortedByDescending { it.timestampDate() }
            recycler_view_images.adapter = ImagesAdapter(this, posts)
        })
    }

    override fun onResume() {
        super.onResume()
        mFirebase.currentUserData { user ->
            text_view_username.text = user.name
            text_view_mail_value.text = user.mail
            image_view_profile.loadImageFullSize(user.photo)
        }
    }

    override fun toggleLike(postId: String) {
    }

    override fun mkLikeCountValueListener(postId: String, onSuccess: (PostLikes) -> Unit): ValueEventListener {
        val reference = mFirebase.database.child("likes/${postId}")
        return reference.addValueEventListener(ValueEventListenerAdapter { it ->
            val users = it.children.map { it.key }.toSet()
            val personalLike = users.contains(mFirebase.currentUser().uid)
            val postLikes = PostLikes(users.count(), personalLike)
            onSuccess(postLikes)
        })
    }

    override fun loadLikes(postId: String, position: Int) {
    }

    override fun showPostDetails(post: Post, likes: PostLikes) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putPostAndLikes(post, likes)
        startActivity(intent)
    }
}

