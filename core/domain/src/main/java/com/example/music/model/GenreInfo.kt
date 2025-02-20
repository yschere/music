package com.example.music.model

import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.util.domainLogger

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
    domainLogger.info { "Genre to GenreInfo external model constructor: \n ${this.id} + ${this.name}" }
    return GenreInfo(
        id = id,
        name = name
    )
}

/**
 * Transform Genre table entry with Extra Info (songCount) to GenreInfo domain model
 */
fun GenreWithExtraInfo.asExternalModel(): GenreInfo {
    domainLogger.info { "GenreWithExtraInfo to GenreInfo external model constructor: \n ${this.genre} + ${this.songCount}" }
    return this.genre.asExternalModel().copy(
        songCount = songCount
    )
}