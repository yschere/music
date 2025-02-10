package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for table genres / data class Genre.
 * Used to contain base genre information.
 */
@Entity(tableName = "genres",
    indices = [
        Index("name", unique = true)
    ]
)
data class Genre(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String
)