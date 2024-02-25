package com.example.playlistmaker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchBatton = findViewById<Button>(R.id.search_button)
        searchBatton.setOnClickListener{
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        val playlistButton =findViewById<Button>(R.id.playlist)
        val playlistButtonClickListener: View.OnClickListener = View.OnClickListener {
            val playlistIntent = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(playlistIntent)
        }
        playlistButton.setOnClickListener(playlistButtonClickListener)

        val settingsButton = findViewById<Button>(R.id.settings)

        settingsButton.setOnClickListener{
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }


    }
}