package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table artists / data class Artist.
 * Used to contain base artist information.
 * A record is added, updated, and/or deleted dependent on the
 * existence of the value within the songs table and the albums table.
 */
@Entity(tableName = "artists",
    indices = [
        Index("id", unique = true),
    ]
)

/**
 * Artist data class is the internal representation for the artists database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on artist_id and album_artist_id
 * @property name [String] artist name
 *  this used to contain a genre_id that would have been the foreign key for Genre, but
 *  was removed since it added a layer of complexity I could not properly account for. Need
 *  to return to this point if the need for a direct reference from artist to genre is necessary.
 */
data class Artist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = ""
)