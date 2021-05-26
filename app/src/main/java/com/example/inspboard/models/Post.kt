package com.example.inspboard.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Post(
    val uid: String = "", val name: String = "", val photo: String? = null,
    val image: String = "", val timestamp: Any = ServerValue.TIMESTAMP,
    @Exclude val id: String = ""
) {

    fun timestampDate(): Date = Date(timestamp as Long)
}