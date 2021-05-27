package com.example.inspboard.activities

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inspboard.R
import com.example.inspboard.models.PostLikes
import com.example.inspboard.utils.FeedAdapter
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.ValueEventListenerAdapter
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : BaseActivity(0), FeedAdapter.Listener {
    private val TAG = "FeedActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var mLikeListeners: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        Log.d(TAG, "onCreate ")
        setUpBottomNavigation()

        mFirebase = FirebaseHelper(this)
        if (mFirebase.auth.currentUser == null) {
            // todo: code for anon
            return
        }
        // code for authorized
        mFirebase.currentUserPosts().addListenerForSingleValueEvent(
            ValueEventListenerAdapter { it ->
                val posts = it.children.map { it.asPost()!! }
                    .sortedByDescending { it.timestampDate() }
                mAdapter = FeedAdapter(this, posts)
                recycler_view_feed.adapter = mAdapter
                recycler_view_feed.layoutManager = LinearLayoutManager(this)
            }
        )
    }

    override fun toggleLike(postId: String) {
        val reference = mFirebase.database.child("likes/${postId}/${mFirebase.currentUser().uid}")
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
            reference.setTrueOrRemove(!it.exists())
        })
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener(): ValueEventListener {
            val reference = mFirebase.database.child("likes/${postId}")
            return reference.addValueEventListener(ValueEventListenerAdapter { it ->
                val users = it.children.map { it.key }.toSet()
                val personalLike = users.contains(mFirebase.currentUser().uid)
                val postLikes = PostLikes(users.count(), personalLike)
                mAdapter.updatePostLikes(position, postLikes)
            })
        }
        if (mLikeListeners[postId] == null) {
            mLikeListeners = mLikeListeners + (postId to createListener())
        }
    }

    override fun onDestroy() {
        mLikeListeners.values.forEach { mFirebase.database.removeEventListener(it) }
        super.onDestroy()
    }
}

