package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.content.res.ResourcesCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity

import com.example.playlistmaker.search.domain.models.Track

class PlayerPresenter(
    private val playerView: PlayerView,
) {
    private var mediaPlayer = MediaPlayer()
    val timeHandler= Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    var duration:Int = 30
    var currentTime: Int = 0
    var pausedTime: Int = 0
    var isPaused: Boolean = false
    val updateProgress = object : Runnable {
        override fun run() {

//            timeCode.text = formatTime(currentTime)
            playerView.updateTimeCode(formatTime(currentTime))
            if (currentTime < duration) {
                currentTime += 1
                timeHandler.postDelayed(this, 1000)
            }
        }
    }

    fun preparePlayer(url:String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            duration = mediaPlayer.duration / 1000
//            playStop.isEnabled = true
            playerView.onPlayerPrepared()
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
//            val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.play_stop, null)
//            playStop.setImageDrawable(newImage)
            playerView.onPlayerCompleted()
            playerState = STATE_PREPARED
            timeHandler.removeCallbacks(updateProgress)
            playerView.updateTimeCode("00:00")
//            timeCode.text="00:00"
        }
    }
    fun startPlayer() {
        if (isPaused) {
            currentTime = pausedTime // Восстанавливаем текущее время после паузы
            isPaused = false
        } else {
            currentTime = 0 // Сбрасываем текущее время, если не было паузы
        }
        mediaPlayer.start()
//        val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.pause_button, null)
//        playStop.setImageDrawable(newImage)
        playerState = STATE_PLAYING
        timeHandler.post(updateProgress) // Запускаем обновление прогресса
        playerView.onPlayerStart()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        pausedTime = currentTime // Сохраняем текущее время при паузе
        isPaused = true
//        val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.play_stop, null)
//        playStop.setImageDrawable(newImage)
        playerState = STATE_PAUSED
        timeHandler.removeCallbacks(updateProgress) // Останавливаем обновление прогресса
        playerView.onPlayerPaused()
    }
    fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secondsRemaining = seconds % 60
        return String.format("%02d:%02d", minutes, secondsRemaining)
    }
    fun releasePlayer() {
        mediaPlayer.release()
    }

    // Новая функция для загрузки информации о треке
    fun loadTrackData(track: Track) {
        // Отправляем информацию во View для отображения
        playerView.setupTrack(
            track.trackName,
            track.artistName,
            track.formattedDuration(),
            track.collectionName,
            track.primaryGenreName,
            track.country,
            track.releaseDate?.substring(0, 4),
            track.getCoverArtwork()
        )

        // Готовим плеер, если доступен URL
        track.previewUrl?.let {
            preparePlayer(it)
        }
    }

}