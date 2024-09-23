package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context.applicationContext))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context.applicationContext))
    }

    private fun getHistoryRepository(context: Context):SearchHistoryRepository{
        return SearchHistoryRepositoryImpl(context.applicationContext)
    }
    fun provideHistoryInteractor(context: Context):HistoryInteractor{
        return HistoryInteractorImpl(getHistoryRepository(context.applicationContext))
    }
}