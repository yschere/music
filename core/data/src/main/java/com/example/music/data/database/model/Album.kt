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
 * A record is added, updated, and/or deleted dependent on the
 * existence of the value within the songs table.
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

/**
 * Album data class is the internal representation for the albums database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on album_id.
 * @property title [String] album title.
 * @property albumArtistId [Long] foreign key for artists table.
 * @property year [Int] release year for album.
 * @property trackTotal [Int] the trackList total of album.
 * @property discNumber [Int] the disc number of album. Most albums are 1 disc, but in sets, this can be different.
 * @property discTotal [Int] the disc total of album. Most albums are 1 disc, but in sets, this can be different.
 * FUTURE properties to be supported: artwork/album_artwork as a bitmap. Currently using
 *  artwork as a String, but intend to have it as a bitmap/image eventually
 */
data class Album(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "album_artist_id") var albumArtistId: Long? = null,
    @ColumnInfo(name = "year") var year: Int? = null,
    @ColumnInfo(name = "track_total") var trackTotal: Int? = null,
    @ColumnInfo(name = "disc_number") var discNumber: Int? = null,
    @ColumnInfo(name = "disc_total") var discTotal: Int? = null,
    @ColumnInfo(name = "artwork") var artwork: String? = null
    //FUTURE: change to bitmap when able to read in file data
)