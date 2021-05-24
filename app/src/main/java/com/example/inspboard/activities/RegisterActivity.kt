package com.example.inspboard.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {
    private val TAG = "RegisterActivity"
    private lateinit var mEmail: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDataBase = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
    }

    override fun onNext(email: String) {
        if (!email.isNotEmpty()) {
            showToast("Please enter email")
            return
        }
        mEmail = email
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onRegister(name: String, password: String) {
        if (!name.isNotEmpty() || !password.isNotEmpty()) {
            showToast("Please enter name and password")
            return
        }
        if (!mEmail.isNotEmpty()) {
            Log.e(TAG, "onRegister: mail is empty!", )
            showToast("Please enter mail")
            supportFragmentManager.popBackStack()
        }

        val email = mEmail
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                unknownRegisterError("Can't register user with such credentials", task)
                return@addOnCompleteListener
            }
            val user = User(name)
            mDataBase.child("users").child(task.result!!.user!!.uid).setValue(user).addOnCompleteListener {
                if (!it.isSuccessful) {
                    unknownRegisterError("Can't add user to database", it)
                    return@addOnCompleteListener
                }
                showToast("Registered successfully!")
                startFeedActivity()
            }
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

class EmailFragment : Fragment() {
    private lateinit var mListener: Listener
    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_next.setOnClickListener {
            val email = edit_text_mail.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment : Fragment() {
    private lateinit var mListener : Listener
    interface Listener {
        fun onRegister(name: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_register.setOnClickListener {
            val name = edit_text_name.text.toString()
            val password = edit_text_password.text.toString()
            mListener.onRegister(name, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}