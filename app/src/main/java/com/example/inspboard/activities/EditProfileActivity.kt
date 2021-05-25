package com.example.inspboard.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.example.inspboard.views.PasswordDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private val REQUEST_IMAGE_CAPTURE: Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var mStorage: StorageReference

    private lateinit var mOldEmail: String
    private lateinit var mNewEmail: String
    private lateinit var mNewUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: DatabaseReference
    private  var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private lateinit var mImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mAuth = FirebaseAuth.getInstance()
        mDataBase = getDatabaseReference()
        mStorage = getStorageReference()

        enableButtonIfAllTextsNonEmpty(button_ok, mail_edit, name_edit)
        button_back.setOnClickListener { finish() }
        button_ok.setOnClickListener { updateUser() }
        image_view_avatar.setOnClickListener { editUserAvatar() }

        val auth = FirebaseAuth.getInstance()
        mOldEmail = auth.currentUser!!.email.toString()
        mail_edit.setText(mOldEmail)
        database = getDatabaseReference()
        database.child("users").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(
            ValueEventListenerAdapter {
                Log.d(TAG, "user info request ok $it")
                val user = it.getValue(User::class.java)
                name_edit.setText(user!!.username, TextView.BufferType.EDITABLE)
                Glide.with(this).load(user.photo).into(image_view_avatar)
            })
    }

    private fun editUserAvatar() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) == null) {
            showToast("Can't get the camera")
            return
        }
        val imageFile = createImageFile()
        mImageUri = FileProvider.getUriForFile(
            this,
            "com.example.inspboard.fileprovider",
            imageFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        // Create an image file name
        // Create an image file name
        val storageDir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_IMAGE_CAPTURE || resultCode != RESULT_OK)
            return
        val uid = mAuth.currentUser!!.uid
        val storageRef = mStorage.child("users/$uid/photo")
        storageRef.putFile(mImageUri).addOnCompleteListener {
            if (!it.isSuccessful) {
                showToast(it.exception!!.message!!)
                return@addOnCompleteListener
            }
            storageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                if (!downloadUrlTask.isSuccessful) {
                    showToast(downloadUrlTask.exception!!.message!!)
                    return@addOnCompleteListener
                }
                val imageUrl = downloadUrlTask.result.toString()
                Log.d(TAG, "onActivityResult: $imageUrl")
                mDataBase.child("users/$uid/photo").setValue(imageUrl).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        showToast(it.exception!!.message!!)
                        return@addOnCompleteListener
                    }
                    showToast("Image saved!")
                    Glide.with(this).load(imageUrl).into(image_view_avatar)
                }
            }
        }
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

    private fun DatabaseReference.updateUser(
        uid: String,
        update: Map<String, Any>,
        onSuccess: () -> Unit
    ) {
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