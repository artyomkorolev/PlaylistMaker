package com.example.playlistmaker

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
     val trackName: String,// Название композиции
     val artistName: String,// Имя исполнителя
     val trackTimeMillis: Long,// Продолжительность трека
     val artworkUrl100: String, // Ссылка на изображение обложки
     val trackId: Long,
     val collectionName : String?,
     val releaseDate : String?,
     val primaryGenreName: String?,
     val country: String?
): Serializable {
     fun formattedDuration(): String {
         val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
         return formatter.format(trackTimeMillis)
     }
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
 }