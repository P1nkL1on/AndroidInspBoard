package com.example.inspboard.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.models.PostLikes
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

fun areAllTextsNotEmpty(vararg  inputs: EditText) = inputs.all { it.text.isNotEmpty() }

fun enableButtonIfAllTextsNonEmpty(btn: Button, vararg  inputs: EditText) {
    val watcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = areAllTextsNotEmpty(*inputs)
        }
    }
    inputs.forEach { it.addTextChangedListener(watcher) }
    btn.isEnabled = areAllTextsNotEmpty(*inputs)
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.getDatabaseReference() =
    FirebaseDatabase.getInstance(this.getString(R.string.firebase_database_url)).reference

fun Context.getStorageReference(): StorageReference =
    FirebaseStorage.getInstance(this.getString(R.string.firebase_storage_url)).reference

fun toName(name: String) =
    name.toLowerCase().replace(' ', '_')

fun ImageView.loadImage(imageUrl: String?) {
    if ((context as Activity).isDestroyed)
        return
    Glide.with(this).load(imageUrl)
        .fallback(R.drawable.avatar_default)
        .centerCrop()
        .into(this)
}

fun DatabaseReference.setTrueOrRemove(value: Boolean) =
    if (value) setValue(true) else removeValue()

fun DataSnapshot.asPost(): Post? = getValue(Post::class.java)?.copy(id = key!!)

fun TextView.setLinkableText(text: String) {
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

fun Intent.putPostAndLikes(post: Post, postLikes: PostLikes) {
    putExtra("personalLike", postLikes.personalLike)
    putExtra("likesCount", postLikes.likesCount)
    putExtra("photo", post.photo)
    putExtra("name", post.name)
    putExtra("image", post.image)
    putExtra("timestamp", post.timestampDate().toString())
}