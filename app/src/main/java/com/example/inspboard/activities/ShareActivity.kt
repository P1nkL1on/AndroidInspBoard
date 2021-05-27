package com.example.inspboard.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.inspboard.R
import com.example.inspboard.models.Post
import com.example.inspboard.models.User
import com.example.inspboard.utils.Camera
import com.example.inspboard.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(1) {
    private val TAG = "ShareActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: Camera
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate ")
        setUpBottomNavigation()

        mFirebase = FirebaseHelper(this)
        mFirebase.currentUserData { mUser = it }
        mCamera = Camera(this)

        button_post_from_camera.setOnClickListener { mCamera.takePicture() }
        button_post_random.setOnClickListener { addRandomPosts() }
    }

    private fun addRandomPosts() {
        mFirebase.apply {
            val imageUrl = "https://picsum.photos/seed/picsum/1000/1000"
            createImageIdDb(imageUrl) {
                createPost(mkPost(imageUrl)) {
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mCamera.REQUEST_CODE)
            return
        if (resultCode != RESULT_OK) {
            finish()
            return
        }
        addPost(mCamera.imageUri!!)

    }

    private fun addPost(uri: Uri) {
        mFirebase.apply {
            storeUserImage(uri) {
                downloadStoredUserImageUrl(uri) { imageUrl ->
                    createImageIdDb(imageUrl) {
                        createPost(mkPost(imageUrl)) {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun mkPost(imageUrl: String): Post = Post(
        uid = mFirebase.currentUser().uid,
        name = mUser.name,
        photo = mUser.photo,
        image = imageUrl,
    )
}