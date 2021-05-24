package com.example.inspboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.example.inspboard.views.PasswordDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var database: DatabaseReference
    private lateinit var mOldEmail: String
    private lateinit var mNewEmail: String
    private lateinit var mNewUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mAuth = FirebaseAuth.getInstance()
        mDataBase = getDatabaseReference()

        enableButtonIfAllTextsNonEmpty(button_ok, mail_edit, name_edit)
        button_back.setOnClickListener { finish() }
        button_ok.setOnClickListener { updateUser() }

        val auth = FirebaseAuth.getInstance()
        mOldEmail = auth.currentUser!!.email.toString()
        mail_edit.setText(mOldEmail)
        database = getDatabaseReference()
        database.child("users").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(
            ValueEventListenerAdapter {
                Log.d(TAG, "user info request ok $it")
                val user = it.getValue(User::class.java)
                name_edit.setText(user!!.username, TextView.BufferType.EDITABLE)
        })
    }

    private fun updateUser() {
        mNewEmail = mail_edit.text.toString()
        if (mNewEmail.isEmpty()) {
            showToast("Please enter mail")
            return
        }
        mNewUser = User(
            username = name_edit.text.toString()
        )
        val errString = isUserOk(mNewUser)
        if (errString != null) {
            showToast(errString)
            return
        }
        showToast("Updating user...")

        if (mNewEmail == mOldEmail) {
            updateUserInDb(mNewUser)
            return
        }
        PasswordDialog().show(supportFragmentManager, "password_dialog")
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isEmpty()) {
            showToast("Password is empty")
            return
        }
        val user = mAuth.currentUser!!
        val credential = EmailAuthProvider.getCredential(mOldEmail, password)
        user.reauthenticate(credential) {
            user.updateEmail(mNewEmail) {
                updateUserInDb(mNewUser)
            }
        }
    }
    private fun updateUserInDb(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
        updatesMap["username"] = user.username
        database.updateUser(mAuth.currentUser!!.uid, updatesMap) {
            showToast("User saved")
            finish()
        }
    }

    private fun isUserOk(user: User): String? = when {
            user.username.isEmpty() -> "Please enter name"
            else -> null
        }

    private fun DatabaseReference.updateUser(uid: String, update: Map<String, Any>, onSuccess: () -> Unit) {
        child("users")
            .child(uid)
            .updateChildren(update)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                    return@addOnCompleteListener
                }
                showToast(it.exception!!.message!!)
            }
    }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
                return@addOnCompleteListener
            }
            showToast(it.exception!!.message!!)
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
                return@addOnCompleteListener
            }
            showToast(it.exception!!.message!!)
        }
    }
}