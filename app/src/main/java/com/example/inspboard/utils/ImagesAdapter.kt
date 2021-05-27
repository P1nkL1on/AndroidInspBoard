package com.example.inspboard.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.inspboard.R
import com.example.inspboard.activities.loadImage
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import kotlinx.android.synthetic.main.post_item_in_feed.view.*

class   ImagesAdapter(private val listener: PostViewer, private val posts: List<Post>) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_in_profile, parent, false)
        return ViewHolder(image as ImageView)
    }

    private val postGalleryDpi = 140
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        holder.image.apply {
            loadImage(posts[position].image, postGalleryDpi, postGalleryDpi)
            setOnClickListener { listener.showPostDetails(post, PostLikes(0, false)) }
        }
    }
}