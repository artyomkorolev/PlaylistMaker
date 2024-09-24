package com.example.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchActivity

class PlayerActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var ivCover:ImageView
    private lateinit var tvTrackName:TextView
    private lateinit var tvAuthor:TextView
    private lateinit var tvTime: TextView
    private lateinit var ntvCollectionName: TextView
    private lateinit var ntvGenre: TextView
    private lateinit var ncountry: TextView
    private lateinit var nreliaseDate:TextView
    private lateinit var tvCollectionName:TextView
    private lateinit var tvGenre:TextView
    private lateinit var country:TextView
    private lateinit var reliaseDate:TextView
    private lateinit var playStop:ImageButton
    private lateinit var timeCode:TextView
    private var mediaPlayer = MediaPlayer()
    val timeHandler= Handler(Looper.getMainLooper())
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    var duration:Int = 30
    var currentTime: Int = 0
    var pausedTime: Int = 0
    var isPaused: Boolean = false
    val updateProgress = object : Runnable {
        override fun run() {

            timeCode.text = formatTime(currentTime)

            if (currentTime < duration) {
                currentTime += 1
                timeHandler.postDelayed(this, 1000)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val track = intent.getSerializableExtra("track") as Track
        ivCover=findViewById(R.id.ivCover)
        tvTrackName=findViewById(R.id.tvTrackname)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvTime = findViewById(R.id.tvTime)
        timeCode = findViewById(R.id.timeCode)
        tvCollectionName = findViewById(R.id.tvAlbome)
        tvGenre=findViewById(R.id.tvgenre)
        country=findViewById(R.id.Country)
        reliaseDate=findViewById(R.id.tvYear)

        ntvCollectionName = findViewById(R.id.ntvAlbome)
        ntvGenre=findViewById(R.id.ntvgenre)
        ncountry=findViewById(R.id.nCountry)
        nreliaseDate=findViewById(R.id.ntvYear)

        reliaseDate.text = track.releaseDate?.substring(0, 4)
        tvTrackName.text = track.trackName
        tvAuthor.text = track.artistName
        tvTime.text = track.formattedDuration()
        tvCollectionName.text = track.collectionName
        tvGenre.text = track.primaryGenreName
        country.text = track.country



        val playUrl =track.previewUrl
        playStop = findViewById(R.id.playStop)
        playStop.isEnabled = false
        if (playUrl != null) {
            preparePlayer(playUrl)
        }
        playStop.setOnClickListener{
            playbackControl()
        }







        if (tvCollectionName.text.isNullOrEmpty()) {
            tvCollectionName.visibility = View.GONE
            ntvCollectionName.visibility = View.GONE
        }
        if (tvGenre.text.isNullOrEmpty()) {
            tvGenre.visibility = View.GONE
            ntvGenre.visibility = View.GONE
        }
        if (country.text.isNullOrEmpty()) {
            country.visibility = View.GONE
            ncountry.visibility = View.GONE
        }
        if (reliaseDate.text.isNullOrEmpty()) {
            reliaseDate.visibility = View.GONE
            nreliaseDate.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .transform(RoundedCorners(dpToPx(8f,this)))
            .placeholder(R.drawable.placeholder)
            .into(ivCover)



        backButton = findViewById(R.id.back_button)



        backButton.setOnClickListener {
            val backIntent = Intent(this, SearchActivity::class.java)
            startActivity(backIntent)
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
    private fun preparePlayer(url:String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            duration = mediaPlayer.duration / 1000
            playStop.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.play_stop, null)
            playStop.setImageDrawable(newImage)
            playerState = STATE_PREPARED
            timeHandler.removeCallbacks(updateProgress)
            timeCode.text="00:00"
        }
    }
    private fun startPlayer() {
        if (isPaused) {
            currentTime = pausedTime // Восстанавливаем текущее время после паузы
            isPaused = false
        } else {
            currentTime = 0 // Сбрасываем текущее время, если не было паузы
        }
        mediaPlayer.start()
        val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.pause_button, null)
        playStop.setImageDrawable(newImage)
        playerState = STATE_PLAYING
        timeHandler.post(updateProgress) // Запускаем обновление прогресса
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pausedTime = currentTime // Сохраняем текущее время при паузе
        isPaused = true
        val newImage = ResourcesCompat.getDrawable(this.resources, R.drawable.play_stop, null)
        playStop.setImageDrawable(newImage)
        playerState = STATE_PAUSED
        timeHandler.removeCallbacks(updateProgress) // Останавливаем обновление прогресса
    }
    private fun playbackControl() {
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


}


