package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.search.domain.models.Track

interface PlayerView {
    fun setupTrack(trackName: String?,
                   artistName: String?,
                   duration: String?,
                   collectionName: String?,
                   genre: String?,
                   country: String?,
                   releaseDate: String?,
                   coverUrl: String?)
    fun updateTimeCode(time: String)
    fun onPlayerPrepared()
    fun onPlayerStart()
    fun onPlayerPaused()
    fun onPlayerCompleted()
}