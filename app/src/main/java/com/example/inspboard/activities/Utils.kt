package com.example.inspboard.activities

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.inspboard.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {
    private val TAG = "ValueEventListenerAdapter"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }
    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "request cancelled: ", error.toException())
    }
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.getDatabaseReference() =
    FirebaseDatabase.getInstance(this.getString(R.string.firebase_database_url)).reference

fun toName(name: String) =
    name.toLowerCase().replace(' ', '_')