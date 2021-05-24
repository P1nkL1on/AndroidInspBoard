package com.example.inspboard.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_email.edit_text_mail
import kotlinx.android.synthetic.main.fragment_register_namepass.*
import kotlinx.android.synthetic.main.fragment_register_namepass.edit_text_password

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {
    private val TAG = "RegisterActivity"
    private lateinit var mEmail: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDataBase = getDatabaseReference()

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
    }

    override fun onNext(email: String) {
        if (email.isEmpty()) {
            showToast("Please enter email")
            return
        }
        mAuth.fetchSignInMethodsForEmail(email) {
            mEmail = email
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, NamePassFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onRegister(name: String, password: String) {
        if (name.isEmpty() || password.isEmpty()) {
            showToast("Please enter name and password")
            return
        }
        if (mEmail.isEmpty()) {
            Log.e(TAG, "onRegister: mail is empty!", )
            showToast("Please enter mail")
            supportFragmentManager.popBackStack()
        }
        mAuth.createUserWithEmailAndPassword(mEmail, password) {
            val user = User(name)
            mDataBase.createUser(it.user!!.uid, user) {
                showToast("Registered successfully!")
                startFeedActivity()
            }
        }

    }

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String, onSuccess: () -> Unit) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (!it.isSuccessful) {
                showToast(it.exception!!.message!!)
                return@addOnCompleteListener
            } else if (it.result!!.signInMethods?.isEmpty() == false) {
                showToast("This email already exists")
                return@addOnCompleteListener
            } else {
                onSuccess()
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String, onSuccess: (AuthResult) -> Unit) {
        createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                unknownRegisterError("Can't register user with such credentials", it)
                return@addOnCompleteListener
            }
            onSuccess(it.result!!)
        }
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {
        child("users")
            .child(uid).setValue(user)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    unknownRegisterError("Can't add user to database", it)
                    return@addOnCompleteListener
                }
                onSuccess()
        }
    }

    private fun unknownRegisterError(text: String, it: Task<out Any>) {
        showToast(text)
        Log.e(TAG, "unknownRegisterError: ", it.exception)
    }

    private fun startFeedActivity() {
        startActivity(Intent(this, FeedActivity::class.java))
        finish()
    }
}



