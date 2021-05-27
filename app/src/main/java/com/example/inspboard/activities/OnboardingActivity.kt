package com.example.inspboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.inspboard.R
import com.example.inspboard.models.OnboardingItem
import com.example.inspboard.utils.OnboardingItemsAdapter
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.post_item_details.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        button_next.setOnClickListener {
            if (view_pager.currentItem == onboardingItemsAdapter.itemCount - 1) {
                continueToApp()
            } else {
                view_pager.currentItem += 1
            }
        }
        text_view_skip.setOnClickListener { continueToApp() }
        button_get_started.setOnClickListener { continueToApp() }
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    R.drawable.onboarding_item_image_1,
                    "Title1",
                    "Descr1"
                ),
                OnboardingItem(
                    R.drawable.onboarding_item_image_2,
                    "Title2",
                    "Descr3"
                ),
                OnboardingItem(
                    R.drawable.onboarding_item_image_3,
                    "Title2",
                    "Descr3"
                )
            )
        )
        view_pager.adapter = onboardingItemsAdapter
        view_pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (view_pager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<View>(onboardingItemsAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            val imageView = ImageView(applicationContext)
            imageView.setImageResource(R.drawable.indicator_inactive_background)
            imageView.layoutParams = layoutParams
            indicators[i] = imageView
            layout_indicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        for (i in 0 until onboardingItemsAdapter.itemCount) {
            val child = layout_indicators.getChildAt(i) as ImageView
            val isActive = i == position
            if (isActive) {
                child.setImageResource(R.drawable.indicator_active_background)
            } else {
                child.setImageResource(R.drawable.indicator_inactive_background)
            }
        }
    }

    private fun continueToApp() {
        startActivity(Intent(this, FeedActivity::class.java))
        finish()
    }
}