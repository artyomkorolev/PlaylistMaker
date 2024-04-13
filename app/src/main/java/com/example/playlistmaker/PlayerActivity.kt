package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.internal.ViewUtils.dpToPx

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val track = intent.getSerializableExtra("track") as Track
        ivCover=findViewById(R.id.ivCover)
        tvTrackName=findViewById(R.id.tvTrackname)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvTime = findViewById(R.id.tvTime)

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
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}


