package com.example.inspboard.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.inspboard.utils.Camera
import com.example.inspboard.utils.FirebaseHelper

class ShareActivity : AppCompatActivity() {
    private val TAG = "ShareActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        mFirebase = FirebaseHelper(this)
        mCamera = Camera(this)
        mCamera.takePicture()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mCamera.REQUEST_CODE)
            return
        if (resultCode != RESULT_OK) {
            finish()
            return
        }
        mFirebase.apply {
            val uri: Uri = mCamera.imageUri!!
            storeUserImage(uri) {
                downloadStoredUserImageUrl(uri) { imageUrl ->
                    createImage(imageUrl) {
                        finish()
                    }
                }
            }
        }
    }
}