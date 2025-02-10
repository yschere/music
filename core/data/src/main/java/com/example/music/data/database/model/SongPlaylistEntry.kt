package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//TODO: See if below is the correct iteration of song-playlist
//Associative entity between songs and playlists
//Intended purpose: to have the playlists song data storage here
//contains playlistId, songId, playlistTrackNumber

/**
 * Entity containing table song_playlist_entries / data class SongPlaylistEntry.
 * Used to contain many-to-many relationship between songs and playlists.
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

data class SongPlaylistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "playlist_id") var playlistId: Long,
    @ColumnInfo(name = "song_id") var songId: Long,
    @ColumnInfo(name = "playlist_track_number") var playlistTrackNumber: Int
)
