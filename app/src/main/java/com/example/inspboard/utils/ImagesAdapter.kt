package com.example.inspboard.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.inspboard.R
import com.example.inspboard.activities.loadImage

class ImagesAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_in_profile, parent, false)
        return ViewHolder(image as ImageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }
}