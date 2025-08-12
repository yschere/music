package com.example.music.domain.model

import android.util.Log
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.domain.util.Genre as GenreV2
import com.example.music.domain.util.domainLogger

private const val TAG = "GenreInfo"

/**
 * External data layer representation of a genre.
 * Intent: to represent a Genre for the UI, with the ability to
 * order genres by song count.
 */
data class GenreInfo(
    var id: Long = 0,
    var name: String = "",
    val songCount: Int = 0,
)

/**
 * Transform Genre table entry to GenreInfo domain model
 */
fun Genre.asExternalModel(): GenreInfo {
    Log.i(TAG, "Genre to GenreInfo external model constructor: \n ${this.id} + ${this.name}")
    return GenreInfo(
        id = this.id,
        name = this.name
    )
}

/**
 * Transform Genre table entry with Extra Info (songCount) to GenreInfo domain model
 */
fun GenreWithExtraInfo.asExternalModel(): GenreInfo {
    Log.i(TAG, "GenreWithExtraInfo to GenreInfo external model constructor: \n ${this.genre} + ${this.songCount}")
    return this.genre.asExternalModel().copy(
        songCount = songCount
    )
}

fun GenreV2.asExternalModel(): GenreInfo {
    Log.i(TAG, "GenreV2 to GenreInfo external model constructor: \n ${this.id} + ${this.name}")
    return GenreInfo(
        id = this.id,
        name = this.name,
        songCount = this.numTracks,
    )
}