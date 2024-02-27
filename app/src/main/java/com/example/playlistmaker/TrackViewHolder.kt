package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
    private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

    fun bind(item: Track){
        tvTitle.text = item.trackName
        tvAuthor.text= item.artistName
        tvTime.text = item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder((R.drawable.placeholder))
            .into(ivCover)

    }
}