package com.example.inspboard

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity(val menuItemNumber:Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"

    fun setUpBottomNavigation () {
        bottom_navigation_view.menu.getItem(menuItemNumber).isChecked = true

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_feed -> FeedActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        R.id.nav_item_logout -> LogoutActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown id item clicked $it")
                            null
                        }
                    }

            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }
}