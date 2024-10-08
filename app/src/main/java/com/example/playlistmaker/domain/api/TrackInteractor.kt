package com.example.playlistmaker.domain.api

import android.os.Message
import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundMovies: List<Track>?, errorMessage: String?)
    }
}