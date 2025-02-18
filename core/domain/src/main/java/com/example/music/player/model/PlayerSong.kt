package com.example.music.player.model

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.ArtistRepo
import com.example.music.domain.GetSongArtistDataUseCase
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import java.time.Duration
import java.time.Duration.ofMillis
import kotlin.time.toDuration

/**
 * Song data with necessary information to be used within a player.
 * TODO: rework SongToAlbum or create new model that contains artistInfo
 */

// class object container for media3 MediaItem
data class PlayerSong(
    var id: Long = 0,
    var title: String = "",
    var artistName: String = "",
    var albumTitle: String = "",
    var duration: Duration? = null,
    val artwork: String? = "",
    val trackNumber: Int? = 0,
    /*
    val dateAdded - song.dateAdded
    val dateModified - song.dateModified
    val dateLastPlayed - song.dateLastPlayed
    val lyric - song.lyric
     */
) {
    constructor(songInfo: SongInfo, artistInfo: ArtistInfo, albumInfo: AlbumInfo) : this(
        id = songInfo.id,
        title = songInfo.title,
        artistName = artistInfo.name,
        albumTitle = albumInfo.title,
        duration = songInfo.duration,
        artwork = albumInfo.artwork,
        trackNumber = songInfo.trackNumber, //TODO: temporary to support SongListItem's albumTrackNumber for use case when album artwork is not visible
    )
}

fun SongToAlbum.toPlayerSong(): PlayerSong =
    PlayerSong(
        id = song.id,
        title = song.title,
        artistName = "PLACEHOLDER",
        albumTitle = album.title,
        duration = song.duration,
        artwork = album.artwork,
    )

fun SongInfo.toPlayerSong(): PlayerSong {
    //PlayerSong(
    //    item,
    //    getArtistDataUseCase(item).firstOrNull() ?: ArtistInfo(),
    //    getAlbumDataUseCase(item).firstOrNull() ?: AlbumInfo(),
    //)
    return PlayerSong(
        id = id,
        title = title,
        artistName = "PLACEHOLDER",
        albumTitle = "PLACEHOLDER",
        duration = duration,
        trackNumber = trackNumber
    )
}


//convert a song into media playable item
@UnstableApi
fun MediaItem.asExternalModel() = PlayerSong(
    id = mediaId.toLong(),
    title = mediaMetadata.title.toString(),
    artistName = mediaMetadata.artist.toString(),
    albumTitle = mediaMetadata.albumTitle.toString(),
    duration = mediaMetadata.durationMs?.let { ofMillis(it) }//.toDuration()?,
)