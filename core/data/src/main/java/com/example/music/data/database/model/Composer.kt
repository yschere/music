package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table composers / data class Composer.
 * Used to contain base composer information.
 * Similar to artists, but without connection to albums.
 * Only connected to Songs data class.
 */

@Entity(tableName = "composers",
    indices = [
        Index("name", unique = true)
    ]
)

data class Composer(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String
)