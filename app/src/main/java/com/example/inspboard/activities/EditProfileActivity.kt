package com.example.inspboard.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.example.inspboard.utils.Camera
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.views.PasswordDialog
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"

    private lateinit var mOldUser: User
    private lateinit var mNewUser: User

    private lateinit var mCamera: Camera
    private lateinit var mFirebase: FirebaseHelper

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = Camera(this)
        mFirebase = FirebaseHelper(this)

        enableButtonIfAllTextsNonEmpty(button_ok, mail_edit, name_edit)
        button_back.setOnClickListener { finish() }
        button_ok.setOnClickListener { updateUser() }
        image_view_avatar.setOnClickListener { mCamera.takePicture() }

        mFirebase.getCurrentUserData { user ->
            mOldUser = user
            name_edit.setText(user.name)
            mail_edit.setText(user.mail)
            image_view_avatar.setUserPhoto(user.photo)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mCamera.REQUEST_CODE)
            return
        if (resultCode != RESULT_OK) {
            showToast("Something bad with camera")
            return
        }
        mFirebase.apply {
            storeUserPhoto(mCamera.imageUri!!) {
                downloadStoredUserPhotoUrl { imageUrl ->
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
        updatesMap["name"] = mNewUser.name
        updatesMap["mail"] = mNewUser.mail
        mFirebase.updateCurrentUserData(updatesMap) {
            showToast("User updated")
            finish()
        }
    }

    private fun updateUser() {
        mNewUser = mOldUser.copy(
            name = name_edit.text.toString(),
            mail = mail_edit.text.toString(),
        )
        val err = isUserOk(mNewUser)
        if (err != null) {
            showToast(err)
            return
        }
        if (mNewUser.mail == mOldUser.mail) {
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
        val credential = EmailAuthProvider.getCredential(mOldUser.mail, password)
        mFirebase.reauthenticate(credential) {
            mFirebase.updateEmail(mNewUser.mail) {
                updateUserInDb()
            }
        }
    }

    private fun isUserOk(user: User): String? = when {
        user.name.isEmpty() -> "Please enter name"
        user.mail.isEmpty() -> "Please enter mail"
        else -> null
    }
}