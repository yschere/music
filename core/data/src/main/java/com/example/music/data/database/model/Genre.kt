package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table genres / data class Genre.
 * Used to contain base genre information.
 * A record is added, updated, and/or deleted dependent on the
 * existence of the value within the songs table.
 */
@Entity(tableName = "genres",
    indices = [
        Index("name", unique = true)
    ]
)

/**
 * Genre data class is the internal representation for the genres database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on genre_id
 * @property name [String] genre name
 */
data class Genre(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = ""
)