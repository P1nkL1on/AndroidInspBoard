package com.example.inspboard.activities

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.inspboard.R
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

fun ImageView.setUserPhoto(imageUrl: String?) {
    if ((context as Activity).isDestroyed)
        return
    Glide.with(this).load(imageUrl)
        .fallback(R.drawable.avatar_default)
        .into(this)
}