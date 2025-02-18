package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table composers / data class Composer.
 * Used to contain base composer information.
 * A record is added, updated, and/or deleted dependent on the
 * existence of the value within the songs table.
 */
@Entity(tableName = "composers",
    indices = [
        Index("name", unique = true)
    ]
)

/**
 * Composer data class is the internal representation for the composers database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on composer_id
 * @property name [String] composer name
 */
data class Composer(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String = ""
)