package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistory.Companion
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val searchHistory: SearchHistory,
    private val context: Context
):SearchHistoryRepository {
    private val sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)

    override fun getSearchHistory(): List<Track> {
            return searchHistory.read(sharedPreferences)
    }

    override fun saveTrackToHistory(track: Track) {
            searchHistory.write(sharedPreferences,track)
    }

    override fun clearSearchHistory() {
        searchHistory.clearHistory(sharedPreferences)
    }

    override fun onStopActivity(tracksOnHistory:List<Track>) {
        sharedPreferences.edit()
            .putString("key_track_hostory",Gson().toJson(tracksOnHistory))
            .apply()
    }
    companion object{
        const val MY_PREFERENCES= "MyPreferences"

    }
}