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

data class Playlist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "date_created") var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_last_accessed") var dateLastAccessed: OffsetDateTime = OffsetDateTime.now(),
    //@ColumnInfo(name = "artwork") val artwork: Bitmap?
)