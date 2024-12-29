package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun showHistory():List<Track>
    fun clearHistory()
    fun addtoHistory(track: Track)
    fun onStopActivityHistory(tracksOnHistory:List<Track>)

}