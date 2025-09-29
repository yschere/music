package com.example.music.domain.model

import android.util.Log
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.data.mediaresolver.model.Genre as GenreMR
import com.example.music.data.util.FLAG

private const val TAG = "GenreInfo"

/**
 * External data layer representation of a genre.
 * Intent: to represent a Genre for the UI, with the ability to
 * order genres by song count.
 */
data class GenreInfo(
    var id: Long = 0,
    var name: String = "",
    var songCount: Int = 0,
)

/**
 * Transform Genre table entry to GenreInfo domain model
 */
fun Genre.asExternalModel(): GenreInfo {
    if (FLAG) Log.i(TAG, "Genre to GenreInfo external model constructor: \n ${this.id} + ${this.name}")
    return GenreInfo(
        id = this.id,
        name = this.name
    )
}

/**
 * Transform Genre table entry with Extra Info (songCount) to GenreInfo domain model
 */
fun GenreWithExtraInfo.asExternalModel(): GenreInfo {
    if (FLAG) Log.i(TAG, "GenreWithExtraInfo to GenreInfo external model constructor: \n ${this.genre} + ${this.songCount}")
    return this.genre.asExternalModel().copy(
        songCount = songCount
    )
}

fun GenreMR.asExternalModel(): GenreInfo {
    if (FLAG) Log.i(TAG, "GenreMR to GenreInfo external model constructor: \n ${this.id} + ${this.name}")
    return GenreInfo(
        id = this.id,
        name = this.name,
        songCount = this.numTracks,
    )
}