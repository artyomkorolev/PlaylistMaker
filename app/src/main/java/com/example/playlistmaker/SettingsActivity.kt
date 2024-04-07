package com.example.playlistmaker


import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        val shareButton = findViewById<Button>(R.id.share_app)
        shareButton.setOnClickListener {
            val shareMessage = getString(R.string.link_android_dev)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type= "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareMessage)
            startActivity(shareIntent)
        }

        val supportButton = findViewById<Button>(R.id.write_to_support)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            val message= getString(R.string.gratitude_devs)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_yandex)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.messege_to_devs))
            startActivity(supportIntent)
        }

        val userAgreButton = findViewById<Button>(R.id.user_agre)
        userAgreButton.setOnClickListener {
            val argeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_practicum_offer)))
            startActivity(argeIntent)
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val app = application as App


        themeSwitcher.isChecked = app.darkTheme

        val sharedPrefs = getSharedPreferences(app.MY_PREFERENCES, MODE_PRIVATE)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit().putBoolean(app.EDIT_THEME_KEY,checked).apply()
        }



    }
}