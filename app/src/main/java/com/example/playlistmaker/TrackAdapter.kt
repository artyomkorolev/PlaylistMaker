package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.NonDisposableHandle.parent

class TrackAdapter(private var tracks: List<Track>,private val trackActionListener: TrackActionListener): RecyclerView.Adapter<TrackViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item,parent,false)
        return TrackViewHolder(view)
    }


    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            trackActionListener.onClickItem(tracks[position])
        }
    }
    interface TrackActionListener {
        fun onClickItem(track: Track)
    }
    fun submitList(list: List<Track>) {
        tracks = list
        notifyDataSetChanged()
    }


}