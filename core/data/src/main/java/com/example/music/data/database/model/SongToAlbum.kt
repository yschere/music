package com.example.music.data.database.model

/**
 * Entity for table song to albums / data class SongToAlbum.
 * Used to contain base album information.
 * Column 'artwork' not included yet.
 * Uses album_artist_id in place of artist_id,
 * anywhere else album's artist_id will be
 * aggregate from songs
 */

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.util.Objects

//FixMe: fix SongToAlbum so that it is actually either all columns of song and album, or is both objects as a whole
// in current state, will have a song object and an albumId. but this means that every time I've been using SongToAlbum,
// I wanted it to be a combination of both song object and album object
// High likelihood this isn't really even necessary since it just means i'm adding album_id as a column twice. So this should
// be repurposed as a different class altogether. Very likely that the more correct version is the direction i was going in
// for SongPlayerData, that is a combined version of the data needed for UI
class SongToAlbum {
    @Embedded
    lateinit var song: Song

    @Relation(parentColumn = "album_id", entityColumn = "id")
    lateinit var _albums: List<Album>

    @get:Ignore
    val album: Album
        get() = _albums[0]

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1() = song
    operator fun component2() = album

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is SongToAlbum -> song == other.song && _albums == other._albums
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(song, _albums)
}
