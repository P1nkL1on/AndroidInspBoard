package com.example.inspboard.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inspboard.R
import com.example.inspboard.activities.loadImage
import com.example.inspboard.activities.setLinkableText
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import kotlinx.android.synthetic.main.post_item_in_feed.view.*

class FeedAdapter(private val listener: PostViewer, private val posts: List<Post>)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var mPostLikes: Map<Int, PostLikes> = emptyMap()
    private val defaultPostLikes: PostLikes = PostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item_in_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = mPostLikes[position] ?: defaultPostLikes

        with(holder.view) {
            image_view_avatar.loadImage(post.photo)
            image_view_image.loadImage(post.image)
            image_view_image.setOnClickListener { listener.showPostDetails(post, likes) }
            text_view_name.setLinkableText(post.name)
            text_view_date.text = post.timestampDate().toString()
            text_view_likes.text = likes.likesCount.toString()
            image_view_check.setOnClickListener { listener.toggleLike(post.id) }
            image_view_check.setImageResource(if (likes.personalLike) R.drawable.ic_check_true else R.drawable.ic_check_false)
            listener.loadLikes(post.id, position)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePostLikes(position: Int, postLikes: PostLikes) {
        mPostLikes = mPostLikes + (position to postLikes)
        notifyItemChanged(position)
    }
}