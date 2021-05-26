package com.example.inspboard.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inspboard.R
import com.example.inspboard.utils.Camera
import com.example.inspboard.utils.FirebaseHelper
import com.example.inspboard.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(1) {
    private val TAG = "ProfileActivity"
    private lateinit var mCamera: Camera
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setUpBottomNavigation()

        mFirebase = FirebaseHelper(this)
        mCamera = Camera(this)

        if (mFirebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        Log.d(TAG, "onCreate")
        image_view_leave.setOnClickListener {
            if (mFirebase.auth.currentUser != null) {
                mFirebase.auth.signOut()
                startActivity(Intent(this, FeedActivity::class.java))
                finish()
            }
        }
        image_view_edit.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }
        button_add_photo.setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
        }
        recycler_view_images.layoutManager = GridLayoutManager(this, 3)
        mFirebase.currentUserImages().addValueEventListener(ValueEventListenerAdapter{ it ->
            val images = it.children.map {it.getValue(String::class.java)!!}
            // todo: its *12 is special
            recycler_view_images.adapter = ImagesAdapter(images + images + images + images + images + images + images + images + images + images + images + images + images + images)
        })
    }

    override fun onResume() {
        super.onResume()
        mFirebase.currentUserData { user ->
            text_view_username.text = user.name
            text_view_mail_value.text = user.mail
            image_view_profile.setUserPhoto(user.photo)
        }
    }
}

class ImagesAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context).inflate(R.layout.image_item_in_profile, parent, false)
        return ViewHolder(image as ImageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    private fun ImageView.loadImage(image: String) {
        Glide.with(this).load(image).centerCrop().into(this)
    }
}

class SquareImageView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
    }
}