package com.example.inspboard.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.inspboard.R
import com.example.inspboard.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {
    private val TAG = "ValueEventListenerAdapter"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }
    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "request cancelled: ", error.toException())
    }
}

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


class Camera(private val activity: Activity) {
    val REQUEST_CODE: Int = 1
    var imageUri: Uri? = null
    private  var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun editUserAvatar() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) == null) {
            activity.showToast("Can't get the camera")
            return
        }
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            activity,
            "com.example.inspboard.fileprovider",
            imageFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity.startActivityForResult(takePictureIntent, REQUEST_CODE)
    }

    private fun createImageFile(): File {
        val storageDir  = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }
}

class FirebaseHelper(private val activity: Activity) {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: DatabaseReference = activity.getDatabaseReference()
    var storage: StorageReference = activity.getStorageReference()

    private fun Task<out Any>.showError(text: String) {
        activity.showToast("$text! ${exception!!.message!!}")
    }

    private fun Task<out Any>.showErrorOrContinue(errorMessage: String = "Error occurred", onSuccess: () -> Unit) {
        if (isSuccessful) {
            onSuccess()
        } else {
            showError(errorMessage)
        }
    }

    private fun currentUser(): FirebaseUser =
        auth.currentUser!!

    fun updateCurrentUserPhoto(imageUrl: String, onSuccess: () -> Unit) {
        database.child("users/${currentUser().uid}/photo").setValue(imageUrl).addOnCompleteListener {
            it.showErrorOrContinue("Can't set user photo", onSuccess)
        }
    }

    private fun storageCurrentUserPhotos(): StorageReference =
        storage.child("users/${currentUser().uid}/photo")

    fun storeUserImage(uri: Uri, onSuccess: () -> Unit) {
        storageCurrentUserPhotos().putFile(uri).addOnCompleteListener {
            it.showErrorOrContinue("Can't store image", onSuccess)
        }
    }

    fun downloadStoredUserImageUrl(onSuccess: (String) -> Unit) {
        storageCurrentUserPhotos().downloadUrl.addOnCompleteListener {
            it.showErrorOrContinue("Can't get url of stored image") {
                onSuccess(it.result.toString())
            }
        }
    }

    fun verifyEmailIsUnique(email: String, onSuccess: () -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            it.showErrorOrContinue ("Can't fetch sign in methods for email") {
                if (it.result!!.signInMethods?.isEmpty() == false) {
                    activity.showToast("This email already exists")
                } else {
                    onSuccess()
                }
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: (AuthResult) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            it.showErrorOrContinue("Can't register user with such credentials") {
                onSuccess(it.result!!)
            }
        }
    }

    fun createUser(uid: String, user: User, onSuccess: () -> Unit) {
        database.child("users").child(uid).setValue(user).addOnCompleteListener {
            it.showErrorOrContinue("Can't add user to database", onSuccess)
        }
    }

    fun getCurrentUserData(onSuccess: (user: User) -> Unit) {
        database.child("users").child(currentUser().uid).addListenerForSingleValueEvent(
            ValueEventListenerAdapter{
                val user = it.getValue(User::class.java)
                onSuccess(user!!)
            }
        )
    }

    fun updateCurrentUserData(update: Map<String, Any>, onSuccess: () -> Unit) {
        database.child("users").child(currentUser().uid).updateChildren(update).addOnCompleteListener {
            it.showErrorOrContinue("Can't update user", onSuccess)
        }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        currentUser().updateEmail(email).addOnCompleteListener {
            it.showErrorOrContinue("Can't update email", onSuccess)
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        currentUser().reauthenticate(credential).addOnCompleteListener {
            it.showErrorOrContinue("Can't reauthenticate", onSuccess)
        }
    }
}