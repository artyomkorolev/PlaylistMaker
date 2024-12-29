package com.example.playlistmaker.search.ui.view_model

import android.graphics.drawable.Drawable
import com.example.playlistmaker.search.domain.models.Track
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TracksView:MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updateHistoryList(newHistoryList:List<Track>)

    // работа с сетевым запросом
//    fun showLoading()
//
//    fun showError(errorMessage:String, errorImage:Drawable)
//
//    fun showEmpty(emptyMessage: String, emptyImage:Drawable)
//
//   fun showContent(tracks:List<Track>)
    //render
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: TrackState)
}