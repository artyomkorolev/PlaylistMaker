package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: SearchHistoryRepository):HistoryInteractor {
    override fun showHistory(): List<Track> {
       return repository.getSearchHistory()
    }

    override fun clearHistory() {
        repository.clearSearchHistory()
    }

    override fun addtoHistory(track: Track) {
        repository.saveTrackToHistory(track)
    }

    override fun onStopActivityHistory(tracksOnHistory: List<Track>) {
        repository.onStopActivity(tracksOnHistory)
    }
}