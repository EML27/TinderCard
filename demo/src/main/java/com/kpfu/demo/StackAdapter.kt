package com.kpfu.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class StackAdapter(
    var items: List<Food> = emptyList()
) : RecyclerView.Adapter<StackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.cost.text = "$${item.price}"
        Glide.with(holder.image)
            .load(item.url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(holder.image)
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvFoodName)
        val cost: TextView = view.findViewById(R.id.tvFoodCost)
        val image: ImageView = view.findViewById(R.id.ivPicture)
    }
}