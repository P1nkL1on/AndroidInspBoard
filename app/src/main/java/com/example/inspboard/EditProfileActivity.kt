package com.example.inspboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.inspboard.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        button_back.setOnClickListener {
            finish()
        }

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                name_edit.setText(user!!.username, TextView.BufferType.EDITABLE)
                mail_edit.setText(currentUser.email)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }

        })

    }
}