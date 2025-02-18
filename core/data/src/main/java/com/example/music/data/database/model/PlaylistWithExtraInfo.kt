package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object PlaylistExtraInfo contains playlist object,
 * and date last played from Playlist's last played song,
 * and the count of songs within the playlist.
 * @property playlist [Playlist] data class that represents a Playlist
 * @property dateLastPlayed [OffsetDateTime] the datetime for the most recently played song within the playlist. Used for sorting in the frontend.
 * @property songCount [Int] the amount of songs within the playlist. Used for sorting in the frontend.
 * NOTE: It is possible for songCount to be 0 if a playlist was created without any songs,
 * or has all its songs deleted. So when queries against this table results in null,
 * it will get cast as 0 within the query. Which is why songCount is non-nullable.
 */
class PlaylistWithExtraInfo {
    @Embedded
    lateinit var playlist: Playlist

    @ColumnInfo(name = "date_last_played")
    var dateLastPlayed: OffsetDateTime? = null

    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = playlist
    operator fun component2() = dateLastPlayed
    operator fun component3() = songCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PlaylistWithExtraInfo -> {
            playlist == other.playlist &&
                    dateLastPlayed == other.dateLastPlayed &&
                    songCount == other.songCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(playlist, dateLastPlayed, songCount)

}