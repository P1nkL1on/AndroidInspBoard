package com.example.inspboard.activities

import android.os.Bundle
import android.renderscript.Sampler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.ValueEventListenerAdapter
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.post_item_in_feed.view.*

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

data class PostLikes(val likesCount: Int, val personalLike: Boolean)

class FeedAdapter(private val listener:Listener, private val posts: List<Post>)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var mPostLikes: Map<Int, PostLikes> = emptyMap()
    private val defaultPostLikes: PostLikes = PostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_in_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = mPostLikes[position] ?: defaultPostLikes

        with(holder.view) {
            image_view_avatar.loadImage(post.photo)
            image_view_image.loadImage(post.image)
            text_view_name.setLinkableText(post.name)
            text_view_date.text = post.timestampDate().toString()
            text_view_likes.text = likes.likesCount.toString()
            image_view_check.setOnClickListener { listener.toggleLike(post.id) }
            image_view_check.setImageResource(if (likes.personalLike) R.drawable.ic_check_true else R.drawable.ic_check_false)
            listener.loadLikes(post.id, position)
        }
    }
    override fun getItemCount(): Int = posts.size

    private fun TextView.setLinkableText(text: String) {
        val spannableString = SpannableString(text)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.context.showToast("Username is clicked")
            }
            override fun updateDrawState(ds: TextPaint) { }
        }, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        this.text = spannableString
        this.movementMethod = LinkMovementMethod.getInstance()
    }

    fun updatePostLikes(position: Int, postLikes: PostLikes) {
        mPostLikes = mPostLikes + (position to postLikes)
        notifyItemChanged(position)
    }
}