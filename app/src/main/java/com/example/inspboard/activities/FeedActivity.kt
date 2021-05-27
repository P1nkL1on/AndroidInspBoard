package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import com.example.inspboard.utils.FeedAdapter
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.PostViewer
import com.example.inspboard.utils.ValueEventListenerAdapter
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : BaseActivity(0), PostViewer {
    private val TAG = "FeedActivity"
    private val LIKE_TOGGLE_REQUEST_CODE: Int = 1
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var mLikeListeners: Map<String, ValueEventListener> = emptyMap()
    private lateinit var mPostDetailsId: String

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
        val valueEventListener = mkLikeCountValueListener(postId) { postLikes ->
            mAdapter.updatePostLikes(position, postLikes)
        }
        if (mLikeListeners[postId] == null) {
            mLikeListeners = mLikeListeners + (postId to valueEventListener)
        }
    }

    override fun showPostDetails(post: Post, likes: PostLikes) {
        mPostDetailsId = post.id
        val intent = Intent(this, PostActivity::class.java)
        intent.putPostAndLikes(post, likes)
        startActivityForResult(intent, LIKE_TOGGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != LIKE_TOGGLE_REQUEST_CODE)
            return
        if (resultCode != RESULT_OK) {
            showToast("Something bad happened with post details")
            return
        }
        val likeToggled = data!!.getBooleanExtra("likeWasToggled", false)
        if (!likeToggled)
            return
        toggleLike(mPostDetailsId)
    }

    override fun onDestroy() {
        mLikeListeners.values.forEach { mFirebase.database.removeEventListener(it) }
        super.onDestroy()
    }
}

