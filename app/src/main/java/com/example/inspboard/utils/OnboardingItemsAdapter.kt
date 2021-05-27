package com.example.inspboard.utils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inspboard.R
import com.example.inspboard.models.OnboardingItem
import kotlinx.android.synthetic.main.onboarding_item_container.view.*


class OnboardingItemsAdapter(private val onboardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingItemsAdapter.ViewHolder>(){

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_item_container, parent, false)
        return OnboardingItemsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val onboardingItem = onboardingItems[position]
        with(holder.view) {
            image_view_onboarding.setImageResource(onboardingItem.image)
            text_view_title.text = onboardingItem.title
            text_view_description.text = onboardingItem.description
        }
    }

    override fun getItemCount(): Int  = onboardingItems.count()

}