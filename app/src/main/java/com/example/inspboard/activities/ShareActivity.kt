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
import java.time.LocalDateTime
import java.util.*

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
        mCamera = Camera(this)

        if (mFirebase.auth.currentUser == null) {
            button_post_from_camera.isEnabled = false
            button_post_random.setOnClickListener { addRandomPosts() }
        } else {
            mFirebase.currentUserData { mUser = it }
            button_post_from_camera.setOnClickListener { mCamera.takePicture() }
            button_post_random.setOnClickListener { addRandomPosts() }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mCamera.REQUEST_CODE)
            return
        if (resultCode != RESULT_OK) {
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
                            goWatchPosts()
                        }
                    }
                }
            }
        }
    }

    private fun addRandomPosts() {
        mFirebase.apply {
            val nPosts = 100
            val imageUrls = mkRandomPostImages(400, 400, nPosts)
            createImagesAnonIdDb(imageUrls) {
                createPostsAnon(mkRandomPosts(imageUrls)) {
                    goWatchPosts()
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

    private fun randomString(length: Int) : String {
        val allowedChars = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun mkRandomPostImages(width: Int, height: Int, count: Int): List<String> =
        IntArray(count) { it }.map {
            "https://picsum.photos/seed/${Date().time - it.toLong()}/$width/$height"
        }

    private fun mkRandomPosts(imageUrls: List<String>): List<Post> {
        val count = imageUrls.count()
        return IntArray(count) { it }.map {
            val imageUrl = imageUrls.elementAt(it)
            val photoUrl = "https://picsum.photos/seed/${Date().time - (it + count).toLong()}/50/50"
            Post(
                uid = "missing",
                name = randomString(5) + '.' + randomString(10),
                photo = photoUrl,
                image = imageUrl
            )
        }
    }

    private fun goWatchPosts(){
        startActivity(Intent(this, FeedActivity::class.java))
        finish()
    }
}