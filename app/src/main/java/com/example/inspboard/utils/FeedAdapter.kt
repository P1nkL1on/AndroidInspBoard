package com.example.inspboard.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.inspboard.R
import com.example.inspboard.activities.loadImage
import com.example.inspboard.activities.setLinkableText
import com.example.inspboard.activities.sharedOptions
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import kotlinx.android.synthetic.main.post_item_in_feed.view.*

class FeedAdapter(private val listener: PostViewer)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private val TAG = "FeedAdapter"

    private var mPostLikes: Map<Int, PostLikes> = emptyMap()
    private val defaultPostLikes: PostLikes = PostLikes(0, false)
    private val avatarDpi = 50
    private val postDpi = 400

    var currentPosition: Int = -1
    private val postPreviewDpi: Int = 8
    private val postPreview2Dpi: Int = 100
    private val postsPerPage: Int = 5
    private var postsRequestd: Boolean = false
    private var posts: List<Post> = emptyList()

    init {
        postsRequestd = true
        listener.requestNext(0, postsPerPage) {
            addPosts(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item_in_feed, parent, false)
        return ViewHolder(view)
    }

    fun addPosts(newPosts: List<Post>) {
        postsRequestd = false
        if (newPosts.isNotEmpty()) {
            newPosts.forEach {
                val act = listener as Activity
                val imageUrl = it.image
                arrayOf(postPreviewDpi, postPreview2Dpi, postDpi).forEach { dpi ->
                    Glide.with(act).load(imageUrl)
                        .apply(sharedOptions)
//                        .listener(RequestListenerAdapter { Log.d(TAG, "addPosts: loaded post $dpi x $dpi") })
                        .preload(dpi, dpi)
                }
            }
        }
        posts = posts + newPosts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!postsRequestd && (position >= posts.size - postsPerPage / 2)) {
            postsRequestd = true
            listener.requestNext(position, postsPerPage) {
                addPosts(it)
            }
        }
        if (posts.isEmpty())
            return

        val post = posts[position]
        val likes = mPostLikes[position] ?: defaultPostLikes

        with(holder.view) {
            image_view_image.setOnClickListener { listener.showPostDetails(post, likes) }
            text_view_name.setLinkableText(post.name)
            text_view_date.text = post.timestampDate().toString()
            text_view_likes.text = likes.likesCount.toString()
            image_view_check.setOnClickListener { listener.toggleLike(post.id) }
            image_view_check.setImageResource(if (likes.personalLike) R.drawable.ic_check_true else R.drawable.ic_check_false)
            listener.loadLikes(post.id, position)

            image_view_avatar.loadImage(post.photo, avatarDpi, avatarDpi)
            arrayOf(postPreviewDpi, postPreview2Dpi, postDpi).forEach { dpi ->
                image_view_image.loadImage(post.image, dpi, dpi)
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePostLikes(position: Int, postLikes: PostLikes) {
        mPostLikes = mPostLikes + (position to postLikes)
        notifyItemChanged(position)
    }
}

class RequestListenerAdapter(private val onSuccess: () -> Unit) : RequestListener<Drawable> {
    private val TAG = "RequestListenerAdapter"

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
//        Log.d(TAG, "onLoadFailed: ")
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
//        Log.d(TAG, "onResourceReady: ")
        onSuccess()
        return false
    }
}