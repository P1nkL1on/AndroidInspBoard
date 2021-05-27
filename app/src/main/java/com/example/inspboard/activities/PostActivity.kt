package com.example.inspboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import kotlinx.android.synthetic.main.post_item_details.*

class PostActivity() : AppCompatActivity() {
    private val TAG = "PostActivity"
    private lateinit var mPost: Post
    private lateinit var mPostLikes: PostLikes
    private lateinit var mTimestamp: String
    private var mLikeWasToggled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_item_details)
        Log.d(TAG, "onCreate")

        val bundle = intent.extras
        bundle!!.extractPostAndLikes()
        updateTextAndImages()
        updateLikes()

        image_view_image.setOnClickListener { togglePostInfo() }
        image_view_check.setOnClickListener { toggleLike() }
        rememberResult()
    }

    private fun Bundle.extractPostAndLikes() {
        val likeToggled = getBoolean("personalLike")
        val likesCount = getInt("likesCount")
        val avatar = getString("photo")
        val name = getString("name").toString()
        val image = getString("image").toString()
        mTimestamp = getString("timestamp").toString()
        mPost = Post(
            name = name,
            photo = avatar,
            image = image
        )
        mPostLikes = PostLikes(
            likesCount = likesCount,
            personalLike = likeToggled
        )
    }

    private fun togglePostInfo() {
        if (layout_post_info.visibility == View.GONE) {
            layout_post_info.visibility = View.VISIBLE
        } else {
            layout_post_info.visibility = View.GONE
        }
    }

    private fun updateTextAndImages() {
        image_view_avatar.loadImage(mPost.photo)
        image_view_image.loadImage(mPost.image)
        text_view_name.setLinkableText(mPost.name)
        text_view_date.text = mTimestamp
    }

    private fun updateLikes(){
        val hasLike = mPostLikes.personalLike.xor(mLikeWasToggled)
        image_view_check.setImageResource(if (hasLike) R.drawable.ic_check_true else R.drawable.ic_check_false)

        val likesCount = mPostLikes.likesCount + (if (mLikeWasToggled) 1 else 0) * ( if (mPostLikes.personalLike) -1 else 1)
        text_view_likes.text = likesCount.toString()
    }

    private fun toggleLike() {
        mLikeWasToggled = !mLikeWasToggled
        updateLikes()
        rememberResult()
    }

    private fun rememberResult() {
        val intent = Intent()
        intent.putExtra("likeWasToggled", mLikeWasToggled)
        setResult(RESULT_OK, intent)
    }
}