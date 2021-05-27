package com.example.inspboard.utils

import android.app.Activity
import android.net.Uri
import com.example.inspboard.activities.getDatabaseReference
import com.example.inspboard.activities.getStorageReference
import com.example.inspboard.activities.showToast
import com.example.inspboard.models.Post
import com.example.inspboard.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

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

    fun currentUser(): FirebaseUser = auth.currentUser!!

    fun updateCurrentUserPhoto(imageUrl: String, onSuccess: () -> Unit) {
        database.child("users/${currentUser().uid}/photo").setValue(imageUrl).addOnCompleteListener {
            it.showErrorOrContinue("Can't set user photo", onSuccess)
        }
    }

    private fun storageCurrentUserPhotos(): StorageReference =
        storage.child("users/${currentUser().uid}/photo")

    private fun storageCurrentUserImage(uri: Uri): StorageReference =
        storage.child("users/${currentUser().uid}/images/${uri.lastPathSegment}")

    fun currentUserImages(): DatabaseReference =
        database.child("images/${currentUser().uid}")

    fun currentUserPosts(): DatabaseReference =
        database.child("posts/${currentUser().uid}")

    fun storeUserPhoto(uri: Uri, onSuccess: () -> Unit) {
        storageCurrentUserPhotos().putFile(uri).addOnCompleteListener {
            it.showErrorOrContinue("Can't store photo", onSuccess)
        }
    }

    fun downloadStoredUserPhotoUrl(onSuccess: (String) -> Unit) {
        storageCurrentUserPhotos().downloadUrl.addOnCompleteListener {
            it.showErrorOrContinue("Can't get url of stored photo") {
                onSuccess(it.result.toString())
            }
        }
    }

    fun storeUserImage(uri: Uri, onSuccess: () -> Unit) {
        storageCurrentUserImage(uri).putFile(uri).addOnCompleteListener {
            it.showErrorOrContinue("Can't store image", onSuccess)
        }
    }

    fun downloadStoredUserImageUrl(uri: Uri, onSuccess: (String) -> Unit) {
        storageCurrentUserImage(uri).downloadUrl.addOnCompleteListener {
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

    fun createPost(post: Post, onSuccess: () -> Unit) {
        database.child("posts/${currentUser().uid}").push().setValue(post).addOnCompleteListener {
            it.showErrorOrContinue("Can't add post to database", onSuccess)
        }
    }

    fun currentUserData(onSuccess: (user: User) -> Unit) {
        database.child("users").child(currentUser().uid).addListenerForSingleValueEvent(
            ValueEventListenerAdapter {
                val user = it.getValue(User::class.java)
                onSuccess(user!!)
            }
        )
    }

    fun createImageIdDb(url: String, onSuccess: () -> Unit) {
        database.child("images").child(currentUser().uid).push().setValue(url).addOnCompleteListener {
            it.showErrorOrContinue("Can't save image url in database", onSuccess)
        }
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