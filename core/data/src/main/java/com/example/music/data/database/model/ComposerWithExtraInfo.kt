package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object ArtistWithExtraInfo contains artist object, the count of songs,
 * and the max value of date_last_played from songs by artist.
 */
class ArtistWithExtraInfo {
    @Embedded
    lateinit var artist: Artist

    //this is the MAX("date_last_played") value from songs by artist,
    // should default to null unless there is at least one song that
    // has this value set and is the MAX value if multiple are set
    @ColumnInfo(name = "date_last_played")
    var dateLastPlayed: OffsetDateTime? = null

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    //this is the song count within library
    @ColumnInfo(name = "album_count")
    var albumCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = artist
    operator fun component2() = dateLastPlayed
    operator fun component3() = songCount
    operator fun component4() = albumCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ArtistWithExtraInfo -> {
            artist == other.artist &&
                    dateLastPlayed == other.dateLastPlayed &&
                    songCount == other.songCount &&
                    albumCount == other.albumCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(artist, dateLastPlayed, songCount, albumCount)

}