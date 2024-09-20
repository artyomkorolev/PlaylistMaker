package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository:TrackRepository):TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
    }

}}