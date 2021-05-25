package com.example.inspboard.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.example.inspboard.views.PasswordDialog
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*




class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"

    private lateinit var mOldEmail: String
    private lateinit var mNewEmail: String
    private lateinit var mNewUser: User

    private lateinit var mCamera: Camera
    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = Camera(this)
        mFirebaseHelper = FirebaseHelper(this)

        enableButtonIfAllTextsNonEmpty(button_ok, mail_edit, name_edit)
        button_back.setOnClickListener { finish() }
        button_ok.setOnClickListener { updateUser() }
        image_view_avatar.setOnClickListener { mCamera.editUserAvatar() }

        val auth = FirebaseAuth.getInstance()
        mOldEmail = auth.currentUser!!.email.toString()
        mail_edit.setText(mOldEmail)

        mFirebaseHelper.getCurrentUserData { user ->
            name_edit.setText(user.username, TextView.BufferType.EDITABLE)
            image_view_avatar.setUserPhoto(user.photo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mCamera.REQUEST_CODE || resultCode != RESULT_OK)
            return
        mFirebaseHelper.apply {
            storeUserImage(mCamera.imageUri!!) {
                downloadStoredUserImageUrl { imageUrl ->
                    updateCurrentUserPhoto(imageUrl) {
                        image_view_avatar.setUserPhoto(imageUrl)
                        showToast("Image saved!")
                    }
                }
            }
        }
    }

    private fun updateUserInDb() {
        val updatesMap = mutableMapOf<String, Any>()
        updatesMap["username"] = mNewUser.username
        mFirebaseHelper.updateCurrentUserData(updatesMap) {
            showToast("User updated")
            finish()
        }
    }

    private fun updateUser() {
        // todo: refactor later
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
            updateUserInDb()
        } else {
            PasswordDialog().show(supportFragmentManager, "password_dialog")
        }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isEmpty()) {
            showToast("Password is empty")
            return
        }

        val credential = EmailAuthProvider.getCredential(mOldEmail, password)
        mFirebaseHelper.reauthenticate(credential) {
            mFirebaseHelper.updateEmail(mNewEmail) {
                updateUserInDb()
            }
        }
    }

    private fun isUserOk(user: User): String? = when {
        user.username.isEmpty() -> "Please enter name"
        else -> null
    }
}