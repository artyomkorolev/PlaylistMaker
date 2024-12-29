package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>

    // Сохранение трека в историю поиска
    fun saveTrackToHistory(track: Track)

    // Очистка истории поиска
    fun clearSearchHistory()

    fun onStopActivity(tracksOnHistory:List<Track>)
}