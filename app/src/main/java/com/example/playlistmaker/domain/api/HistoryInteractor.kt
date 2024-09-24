package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun showHistory():List<Track>
    fun clearHistory()
    fun addtoHistory(track: Track)
    fun onStopActivityHistory(tracksOnHistory:List<Track>)

}