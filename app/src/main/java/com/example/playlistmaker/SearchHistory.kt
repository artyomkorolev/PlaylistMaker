package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(sharedPreferences: SharedPreferences) {


    fun read(sharedPreferences: SharedPreferences): List<Track> {
        val json = sharedPreferences.getString(TRACK_LIST_KEY, null) ?: return emptyList()
        val sType = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json,sType)
    }

    // запись
    fun write(sharedPreferences: SharedPreferences, track: Track) {

        var list : MutableList<Track> = read(sharedPreferences).toMutableList()
        var number =0
        var flag = false
        for (i in list) {
            if (track.trackId == i.trackId) {
                flag = true
                break
            }
           number++
        }
        if (flag){
            list.removeAt(number)
            list.add(0,track)
        }else if(list.size==10){
            list.removeAt(list.size-1)
            list.add(0,track)
        }else{
            list.add(0,track)
        }

        val json = Gson().toJson(list)
        sharedPreferences.edit()
            .putString(TRACK_LIST_KEY, json)
            .apply()
    }
    companion object{

        const val TRACK_LIST_KEY = "tracklist_key"
    }

}
