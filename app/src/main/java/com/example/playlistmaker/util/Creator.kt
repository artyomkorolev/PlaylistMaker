package com.example.playlistmaker.util

import android.app.Activity
import android.content.Context
import com.example.playlistmaker.player.ui.view_model.PlayerPresenter
import com.example.playlistmaker.player.ui.view_model.PlayerView
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.SearchPresenter
import com.example.playlistmaker.search.ui.view_model.TracksView

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context.applicationContext))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context.applicationContext))
    }

    private fun getHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context.applicationContext)
    }
    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(context.applicationContext))
    }

    fun provideTrackSearchPresenter(context: Context) : SearchPresenter{
        return SearchPresenter(context)
    }
    fun provideTrackPlayer(view: PlayerView):PlayerPresenter{
        return PlayerPresenter(view)
    }
}