package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate


class App: Application() {
    val MY_PREFERENCES = "MyPrefs"
    var darkTheme = false
    val  EDIT_THEME_KEY ="key_for_edit_theme"
    lateinit var sharedPrefs :SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs  = getSharedPreferences(MY_PREFERENCES,Context.MODE_PRIVATE)
        val themeMode = sharedPrefs.getBoolean(EDIT_THEME_KEY, false)
        switchTheme(themeMode)



    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}


