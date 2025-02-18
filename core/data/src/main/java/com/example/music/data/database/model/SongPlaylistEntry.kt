package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity containing table song_playlist_entries / data class SongPlaylistEntry.
 * Used to represent the many-to-many relationship between songs and playlists.
 * This is necessary for playlists to search for its songs' data, and for songs to
 * reference its order within the playlist.
 */
@Entity(
    tableName = "song_playlist_entries",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Song::class,
            parentColumns = ["id"],
            childColumns = ["song_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("playlist_id"),
        Index("song_id")
    ]
)

/**
 * SongPlaylistEntry data class is the internal representation for the song_playlist_entries database table.
 * @property id [Long] primary key for record. It is possible to have used the playlist_id,
 *  song_id and playlist_track_number as a combined primary key, but this is simpler.
 * @property playlistId [Long] foreign key for playlists table
 * @property songId [Long] foreign key for songs table
 * @property playlistTrackNumber [Int] the song's track number within the playlist
 */
data class SongPlaylistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "playlist_id") var playlistId: Long,
    @ColumnInfo(name = "song_id") var songId: Long,
    @ColumnInfo(name = "playlist_track_number") var playlistTrackNumber: Int
)
