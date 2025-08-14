package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Entity for table songs / data class Song.
 * Used to contain base song information.
 * Columns album_artist_id and artwork not included.
 */

@Entity(
    tableName = "songs",
    indices = [
        Index("id", unique = true),
        Index("artist_id"),
        Index("album_id"),
        Index("genre_id"),
        Index("composer_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Genre::class,
            parentColumns = ["id"],
            childColumns = ["genre_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Composer::class,
            parentColumns = ["id"],
            childColumns = ["composer_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

/**
 * SongPlaylistEntry data class is the internal representation for the
 * song_playlist_entries database table.
 * @property id [Long] primary key for record. Serves as the reference point for foreign keys on song_id.
 * @property title [String] song title.
 * @property artistId [Long] foreign key for artists table.
 * @property albumId [Long] foreign key for albums table.
 * @property genreId [Long] foreign key for genres table.
 * @property year [Int] release year for song.
 * @property albumTrackNumber [Int] the song's track number within the album
 * @property lyrics [String] the song's lyrics
 * @property composerId [Long] foreign key for composers table.
 * @property dateAdded [OffsetDateTime] the datetime when the song entered the database/when the originating file was created
 * @property dateModified [OffsetDateTime] the latest datetime when the song was modified
 * @property dateLastPlayed [OffsetDateTime] the latest datetime when the song was played within the app
 * @property duration [Duration] the length of play time of the song
 * FUTURE properties to be supported: album_artwork or song_artwork.
 *  Consideration: song's also contain an album_artist value,
 *  but that is getting set as a property for albums instead
 */
data class Song(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "artist_id") var artistId: Long? = null,
    //@ColumnInfo(name = "album_artist_id") val albumArtistId: Int? = null,
    @ColumnInfo(name = "album_id") var albumId: Long? = null,
    @ColumnInfo(name = "genre_id") var genreId: Long? = null,
    @ColumnInfo(name = "year") var year: Int? = null,
    @ColumnInfo(name = "album_track_number") var albumTrackNumber: Int? = null,
    @ColumnInfo(name = "lyrics") var lyrics: String? = null,
    //@ColumnInfo(name = "album_artwork), //there are songs with individual artwork
    @ColumnInfo(name = "composer_id") var composerId: Long? = null,
    @ColumnInfo(name = "date_added") var dateAdded: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_modified") var dateModified: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_last_played") var dateLastPlayed: OffsetDateTime? = null,
    @ColumnInfo(name = "duration") var duration: Duration = Duration.ZERO
)