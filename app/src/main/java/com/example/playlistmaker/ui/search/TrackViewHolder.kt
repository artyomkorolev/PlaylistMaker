package com.example.playlistmaker.ui.search

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
    private val tvTime: TextView = itemView.findViewById(R.id.tvTime)



    fun bind(item: Track){
        val releaseDate = item.releaseDate
        val primaryGenreName = item.primaryGenreName
        val country = item.country
        val collectionName = item.collectionName
        val trackid = item.trackId
        tvTitle.text = item.trackName
        tvAuthor.text= item.artistName
        tvTime.text = item.formattedDuration()

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
            .placeholder((R.drawable.placeholder))
            .into(ivCover)



    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }


}



