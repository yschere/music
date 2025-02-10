package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table artists / data class Artist.
 * Used to contain base artist information.
 * Album.album_artist_id is same as artists.id.
 */

@Entity(tableName = "artists",
    indices = [
        Index("id", unique = true),
    ]
)

data class Artist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
)