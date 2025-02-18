package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.util.Objects

/**
 * Class object ArtistWithExtraInfo contains artist object, the count of albums, and
 * the count of songs.
 * @property artist [Artist] data class that represents an Artist
 * @property albumCount [Int] the amount of albums by the artist as Album Artist. Used for sorting in the frontend.
 * @property songCount [Int] the amount of songs by the artist. Used for sorting in the frontend.
 * NOTE: it is possible for an artist to have no songs or no albums, due to Songs being able to
 * contain an artist value as 'Artist' or 'Album Artist'. This distinction means it is possible,
 * but not always the case, for an artist have no corresponding albums or no corresponding songs.
 * So when queries against this table results in null, it will get cast as 0 within the query.
 * Which is why songCount and albumCount are non-nullable.
 */
class ArtistWithExtraInfo {
    @Embedded
    lateinit var artist: Artist

    //this is the album count within library
    @ColumnInfo(name = "album_count")
    var albumCount: Int = 0

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = artist
    operator fun component2() = albumCount
    operator fun component3() = songCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ArtistWithExtraInfo -> {
            artist == other.artist &&
                    albumCount == other.albumCount &&
                    songCount == other.songCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(artist, albumCount, songCount)

}