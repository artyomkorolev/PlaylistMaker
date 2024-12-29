package com.example.playlistmaker.player.ui

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
import androidx.activity.ComponentActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.view_model.PlayerPresenter
import com.example.playlistmaker.player.ui.view_model.PlayerView
import com.example.playlistmaker.player.ui.view_model.TrackViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.util.Creator

class PlayerActivity : ComponentActivity(), PlayerView {
    private lateinit var playerPresenter: PlayerPresenter
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

    private lateinit var viewModel: TrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerPresenter = Creator.provideTrackPlayer(this)




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
        backButton = findViewById(R.id.back_button)
        playStop = findViewById(R.id.playStop)
        val track = intent.getSerializableExtra("track") as Track

        viewModel = ViewModelProvider(this,TrackViewModel.getViewModelFactory(track.trackId.toString()))[TrackViewModel::class.java]

        playerPresenter.loadTrackData(track)

        playStop.setOnClickListener {
            playerPresenter.playbackControl()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    override fun onPause() {
        super.onPause()
        playerPresenter.pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        playerPresenter.releasePlayer()
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    override fun setupTrack(
        trackName: String?,
        artistName: String?,
        duration: String?,
        collectionName: String?,
        genre: String?,
        country: String?,
        releaseDate: String?,
        coverUrl: String?
    ) {
        tvTrackName.text = trackName
        tvAuthor.text = artistName
        tvTime.text = duration
        tvCollectionName.text = collectionName
        tvGenre.text = genre
        this.country.text = country
        reliaseDate.text = releaseDate

        if (tvCollectionName.text.isNullOrEmpty()) {
            tvCollectionName.visibility = View.GONE
            ntvCollectionName.visibility = View.GONE
        }
        if (tvGenre.text.isNullOrEmpty()) {
            tvGenre.visibility = View.GONE
            ntvGenre.visibility = View.GONE
        }
        if (this.country.text.isNullOrEmpty()) {
            this.country.visibility = View.GONE
            ncountry.visibility = View.GONE
        }
        if (reliaseDate.text.isNullOrEmpty()) {
            reliaseDate.visibility = View.GONE
            nreliaseDate.visibility = View.GONE
        }

        // Загрузка изображения
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .placeholder(R.drawable.placeholder)
            .into(ivCover)
    }

    override fun updateTimeCode(time: String) {
        timeCode.text = time
    }

    override fun onPlayerPrepared() {
        playStop.isEnabled = true
    }

    override fun onPlayerStart() {
        playStop.setImageResource(R.drawable.pause_button)
    }

    override fun onPlayerPaused() {
        playStop.setImageResource(R.drawable.play_stop)
    }

    override fun onPlayerCompleted() {
        playStop.setImageResource(R.drawable.play_stop)
    }


}


