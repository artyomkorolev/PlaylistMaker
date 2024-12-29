package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class TrackViewModel(
    private val trackId: String,
): ViewModel() {

    private var loadingLiveData = MutableLiveData(true)

    // 2
    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData



    companion object {
        // 1
        fun getViewModelFactory(trackId: String): ViewModelProvider.Factory = viewModelFactory {
            // 2
            initializer {
                // 3
                TrackViewModel(
                    trackId,

                )
            }
        }
    }
}