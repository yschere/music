package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Entity for table playlists / data class Playlist.
 * Used to contain base playlist information.
 * Column 'artwork' not included yet.
 */
@Entity(tableName = "playlists",
    indices = [
        Index("id", unique = true)
    ]
)

/**
 * Playlists data class is the internal representation for the playlist database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on playlist_id.
 * @property name [String] playlist name.
 * @property description [String] an editable description for the playlist
 * @property dateCreated [OffsetDateTime] the datetime when the playlist was created
 * @property dateLastAccessed [OffsetDateTime] the latest datetime when the playlist was modified.
 *  This can be when the playlist is created, edited, or a song is played within its context.
 * FUTURE properties to be supported: artwork, intending for it to contain the artwork(s)
 *  of the first few songs within the playlist, ordered by SongPlaylistEntries.playlistTrackNumber
 */
data class Playlist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "date_created") var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_last_accessed") var dateLastAccessed: OffsetDateTime = OffsetDateTime.now(),
    //@ColumnInfo(name = "artwork") val artwork: Bitmap?
)