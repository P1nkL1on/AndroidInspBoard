package com.example.inspboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        button_back.setOnClickListener {
            finish()
        }

        val auth = FirebaseAuth.getInstance()
        mail_edit.setText(auth.currentUser!!.email)
        database = FirebaseDatabase.getInstance().reference
        database.child("users").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(
            ValueEventListenerAdapter {
                Log.d(TAG, "user info request ok $it")
                val user = it.getValue(User::class.java)
                name_edit.setText(user!!.username, TextView.BufferType.EDITABLE)
        })
    }
}