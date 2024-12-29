package com.example.playlistmaker.search.ui.view_model

import android.graphics.drawable.Drawable
import com.example.playlistmaker.search.domain.models.Track

sealed interface TrackState {
    object Loading : TrackState

    data class Content(
        val tracks: List<Track>
    ) : TrackState

    data class Error(
        val errorMessage: String,
        val errorImage: Drawable
    ) : TrackState

    data class Empty(
        val message: String,
        val emptyImage: Drawable
    ) : TrackState
}