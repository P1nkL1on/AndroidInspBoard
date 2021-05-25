package com.example.inspboard.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.inspboard.activities.showToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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