package com.example.playlistmaker.search.ui.view_model

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter

import com.example.playlistmaker.util.Creator
import moxy.MvpPresenter

class SearchPresenter(
    private val context: Context,
):MvpPresenter<TracksView>() {
    private val trackInteractor =Creator.provideTrackInteractor(context)
    private val historyInteractor = Creator.provideHistoryInteractor(context)

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }



    private var lastFailedSearchQuery: String? = null
    private var tracksOnHistory = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var tracks = ArrayList<Track>()





    override fun onDestroy(){
        handler.removeCallbacks(searchRunnable)
    }
    fun onStop(){
        historyInteractor.onStopActivityHistory(tracksOnHistory)
    }
    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }
    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }

        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun searchRequest(newSearchText:String){

        if(newSearchText.isNotEmpty()){
            renderState(
                TrackState.Loading
            )
            trackInteractor.searchTrack(newSearchText,object : TrackInteractor.TrackConsumer{
                override fun consume(foundTracks: List<Track>?, errorMessage:String?) {
                    handler.post {
                        if(foundTracks!= null){
                            Log.d("ConsumeState", "Tracks found: ${foundTracks.size}")
                            tracks.clear()
                            tracks.addAll(foundTracks)
                        }

                        when{
                            errorMessage != null ->{
                                Log.d("ConsumeState", "Error occurred: $errorMessage")
                                lastFailedSearchQuery = newSearchText
                                renderState(
                                    TrackState.Error(
                                        errorMessage = context.getString(R.string.something_went_wrong),
                                        errorImage = ContextCompat.getDrawable(context, R.drawable.placeholder_no_internet) ?: return@post)
                                    )
                            }

                            tracks.isEmpty() -> {
                                Log.d("ConsumeState", "No tracks found")
                                renderState(
                                    TrackState.Error(
                                        errorMessage = context.getString(R.string.nothing_found),
                                        errorImage = ContextCompat.getDrawable(context, R.drawable.placeholder_no_results) ?: return@post)
                                )
                            }

                            else -> {
                                Log.d("ConsumeState", "Tracks loaded successfully")
                                renderState(
                                    TrackState.Content(
                                        tracks = tracks
                                    )
                                )
                            }
                        }
                    }
                }
            })
        }
    }


    fun addToHistory(track: Track) {
        historyInteractor.addtoHistory(track)

    }
    fun historyToHistory(track: Track){
        historyInteractor.addtoHistory(track)
        if (!historyInteractor.showHistory().isEmpty()) {
            tracksOnHistory = historyInteractor.showHistory() as ArrayList<Track>
            viewState.updateHistoryList(tracksOnHistory)
        }
    }

    fun checkEmptyHistoryList():Boolean{
        return historyInteractor.showHistory().isEmpty()
    }
    fun showHistoryList(): ArrayList<Track>{
        return historyInteractor.showHistory() as ArrayList<Track>
    }

    fun clearHistory(){
        historyInteractor.clearHistory()
    }
    fun refreshRequest(){
        lastFailedSearchQuery?.let { query ->
            searchRequest(query)
        }
    }
    private fun renderState(state: TrackState) {
        viewState.render(state)
    }
}