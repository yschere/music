package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object AlbumWithExtraInfo contains album object, the count of songs,
 * and the max value of date_last_played song in album.
 * @property album [Album] data class that represents an Album
 * @property dateLastPlayed [OffsetDateTime] the datetime of the most recently played song in the album.
 * @property songCount the amount of songs within the album, NOT the album's actual trackList total. Used for sorting in the frontend.
 */
class AlbumWithExtraInfo {
    @Embedded
    lateinit var album: Album

    /* this is the MAX("date_last_played") value from songs in album,
     should default to null unless there is at least one song that
     has this value set and is the MAX value if multiple are set */
    @ColumnInfo(name = "date_last_played")
    var dateLastPlayed: OffsetDateTime? = null

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    @ColumnInfo(name = "album_artist_name")
    var albumArtistName: String? = null

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = album
    operator fun component2() = dateLastPlayed
    operator fun component3() = songCount
    operator fun component4() = albumArtistName

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is AlbumWithExtraInfo -> {
            album == other.album &&
                    dateLastPlayed == other.dateLastPlayed &&
                    songCount == other.songCount &&
                    albumArtistName == other.albumArtistName
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(album, dateLastPlayed, songCount, albumArtistName)

}