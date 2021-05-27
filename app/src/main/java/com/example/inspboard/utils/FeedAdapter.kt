package com.example.inspboard.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.inspboard.R
import com.example.inspboard.activities.loadImage
import com.example.inspboard.activities.setLinkableText
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import kotlinx.android.synthetic.main.post_item_in_feed.view.*
import java.util.*

class FeedAdapter(private val listener: PostViewer)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    object UIJobScheduler {
        private const val MAX_JOB_TIME_MS: Float = 4f

        private var elapsed = 0L
        private val jobQueue = ArrayDeque<() -> Unit>()
        private val isOverMaxTime get() = elapsed > MAX_JOB_TIME_MS * 1_000_000
        private val handler = Handler()

        fun submitJob(job: () -> Unit) {
            jobQueue.add(job)
            if (jobQueue.size == 1) {
                handler.post { processJobs() }
            }
        }

        private fun processJobs() {
            while (!jobQueue.isEmpty() && !isOverMaxTime) {
                val start = System.nanoTime()
                jobQueue.poll().invoke()
                elapsed += System.nanoTime() - start
            }
            if (jobQueue.isEmpty()) {
                elapsed = 0
            } else if (isOverMaxTime) {
                onNextFrame {
                    elapsed = 0
                    processJobs()
                }
            }
        }

        private fun onNextFrame(callback: () -> Unit) =
            Choreographer.getInstance().postFrameCallback { callback() }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val avatarDpi = 50
        private val postDpi = 400
        fun populateFrom(listener: PostViewer, post: Post, likes: PostLikes) {
            with(view) {
                UIJobScheduler.submitJob {image_view_image.setOnClickListener { listener.showPostDetails(post, likes) }}
                UIJobScheduler.submitJob {text_view_name.setLinkableText(post.name)}
                UIJobScheduler.submitJob {text_view_date.text = post.timestampDate().toString()}
                UIJobScheduler.submitJob {text_view_likes.text = likes.likesCount.toString()}
                UIJobScheduler.submitJob {image_view_check.setOnClickListener { listener.toggleLike(post.id) }}
                UIJobScheduler.submitJob {image_view_check.setImageResource(if (likes.personalLike) com.example.inspboard.R.drawable.ic_check_true else com.example.inspboard.R.drawable.ic_check_false)}
                UIJobScheduler.submitJob {listener.loadLikes(post.id, position)}
                UIJobScheduler.submitJob {image_view_avatar.loadImage(post.photo, avatarDpi, avatarDpi)}
                UIJobScheduler.submitJob {image_view_image.loadImage(post.image, postDpi, postDpi)}
            }
        }
    }

    private val TAG = "FeedAdapter"
    private val N_POSTS_PER_PAGE: Int = 10
    private var mPostLikes: Map<Int, PostLikes> = emptyMap()
    private val mDefaultPostLikes: PostLikes = PostLikes(0, false)
    private var mPostsRequestd: Boolean = false
    private var mPosts: List<Post> = emptyList()
    private val mAsyncLayoutInflater = AsyncLayoutInflater(listener as Activity)
    private val mCachedViews = Stack<View>()

    init {
        requestPosts(0, N_POSTS_PER_PAGE)
    }

    private fun requestPosts(position: Int, count: Int) {
        if (mPostsRequestd)
            return
        mPostsRequestd = true
        for (i in position..(position + count)) {
            mAsyncLayoutInflater.inflate(R.layout.post_item_in_feed, null) { view, layoutRes, viewGroup ->
                mCachedViews.push(view)
            }
        }
        listener.requestNext(position, count) {
            receivePosts(it)
        }
    }

    private fun receivePosts(newPosts: List<Post>) {
        mPostsRequestd = false
        mPosts = mPosts + newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (mCachedViews.isEmpty()) {
            LayoutInflater.from(listener as Activity).inflate(R.layout.post_item_in_feed, parent, false)
        } else {
            mCachedViews.pop().also { it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) }
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= mPosts.size - N_POSTS_PER_PAGE / 2) {
            requestPosts(mPosts.size, N_POSTS_PER_PAGE)
        }
        if (mPosts.isEmpty())
            return

        val post = mPosts[position]
        val likes = mPostLikes[position] ?: mDefaultPostLikes
        holder.populateFrom(listener, post, likes)
    }

    override fun getItemCount(): Int = mPosts.size

    fun updatePostLikes(position: Int, postLikes: PostLikes) {
        mPostLikes = mPostLikes + (position to postLikes)
        notifyItemChanged(position)
    }
}