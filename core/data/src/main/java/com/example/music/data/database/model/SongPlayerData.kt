package com.example.music.data.database.model

/*
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import java.util.Objects

/**
 * Class SongPlayerData contains song object, song's
 * artistName (from table artists) and song's albumTitle
 * (from table albums). Use for SongPlayer screen.
 *
 * Should serve same purpose as Jet caster's
 * EpisodeToPodcast.toPlayerEpisode within PlayerEpisode model
 */

class SongPlayerData {
    @Embedded
    lateinit var song: Song

    @Relation(
        parentColumn = "artist_id", //playlists.playlist_id
        entityColumn = "id", //playlists.song_id
        associateBy = Junction(Artist::class)
    )
    lateinit var artist: Artist
    //@ColumnInfo(name = "artist_name") var artistName = artist.name
    var artistName = artist.name

    @Relation(
        parentColumn = "album_id", //playlists.playlist_id
        entityColumn = "id", //playlists.song_id
        associateBy = Junction(Album::class)
    )
    lateinit var album: Album
    //@ColumnInfo(name = "album_title") var albumTitle = album.title
    var albumTitle = album.title

    operator fun component1() = song
    operator fun component2() = artistName
    operator fun component3() = albumTitle

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is SongPlayerData -> song == other.song
                && artistName == other.artistName
                && albumTitle == other.albumTitle
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(song, artistName, albumTitle)
}
*/