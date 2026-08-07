package com.example.studentorganizer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentorganizer.R
import com.example.studentorganizer.model.OnboardingItem

class OnboardingAdapter(
    private val sliderList: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.idIVSlider)
        val title: TextView = itemView.findViewById(R.id.idTVSliderTitle)
        val description: TextView = itemView.findViewById(R.id.idTVSliderDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_item, parent, false)

        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {

        val item = sliderList[position]

        holder.imageView.setImageResource(item.image)
        holder.title.text = item.title
        holder.description.text = item.description
    }

    override fun getItemCount(): Int {
        return sliderList.size
    }
}