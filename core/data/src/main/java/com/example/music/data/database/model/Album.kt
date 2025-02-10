package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table albums / data class Album.
 * Used to contain base album information.
 * Column 'artwork' not included yet.
 * Uses album_artist_id in place of artist_id,
 * anywhere else album's artist_id will be
 * aggregate from songs
 */

@Entity(tableName = "albums",
    indices = [
        Index("id", unique = true),
        Index("album_artist_id"),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["album_artist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ]
)

data class Album(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "album_artist_id") var albumArtistId: Long? = null,
    @ColumnInfo(name = "year") var year: Int? = 0,
    @ColumnInfo(name = "track_total") var trackTotal: Int? = 0,
    @ColumnInfo(name = "disc_number") var discNumber: Int? = 0,
    @ColumnInfo(name = "disc_total") var discTotal: Int? = 0,
    @ColumnInfo(name = "artwork") var artwork: String? = null
    //TODO: change to bitmap when able to read in file data
)