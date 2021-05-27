package com.example.inspboard.utils

import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import com.google.firebase.database.ValueEventListener

interface PostViewer {
    fun toggleLike(postId: String)
    fun mkLikeCountValueListener(postId: String, onSuccess: (PostLikes) -> Unit): ValueEventListener
    fun loadLikes(postId: String, position: Int)
    fun showPostDetails(post: Post, likes: PostLikes)
}