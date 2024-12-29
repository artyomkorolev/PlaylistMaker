package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val repository: SearchHistoryRepository): HistoryInteractor {
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