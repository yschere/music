package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object PlaylistExtraInfo contains playlist object,
 * and date last played from Playlist's last played song,
 * and the count of songs within the playlist.
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