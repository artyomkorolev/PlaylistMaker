package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistory.Companion
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val context: Context
):SearchHistoryRepository,SearchHistory() {

    private val sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)

    override fun getSearchHistory(): List<Track> {
            return SearchHistory().read(sharedPreferences)
    }

    override fun saveTrackToHistory(track: Track) {
        SearchHistory().write(sharedPreferences,track)
    }

    override fun clearSearchHistory() {
        SearchHistory().clearHistory(sharedPreferences)
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