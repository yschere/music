package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object GenreWithExtraInfo contains genre object and the count of songs.
 * @property genre [Genre] data class that represents a Genre
 * @property songCount [Int] the amount of songs within the genre. Used for sorting in the frontend.
 */
class GenreWithExtraInfo {
    @Embedded
    lateinit var genre: Genre

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = genre
    operator fun component2() = songCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is GenreWithExtraInfo -> {
            genre == other.genre &&
                    songCount == other.songCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(genre, songCount)

}