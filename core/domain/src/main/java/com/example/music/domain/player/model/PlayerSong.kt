package com.example.music.domain.player.model

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.music.data.database.model.SongToAlbum
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.util.Audio
import com.example.music.domain.util.domainLogger
import com.example.music.domain.util.toAlbumArtUri
import java.time.Duration
import java.time.Duration.ofMillis

private const val TAG = "PlayerSong"
/**
 * Song data with necessary information to be used within a player.
 * TODO: rework SongToAlbum or create new model that contains artistInfo
 */

// class object container for media3 MediaItem
data class PlayerSong(
    var id: Long = 0,
    var title: String = "",
    val artistId: Long = -1,
    var artistName: String = "",
    val albumId: Long = -1,
    var albumTitle: String = "",
    var duration: Duration = Duration.ZERO,
    val artwork: String? = "",
    val artworkUri: Uri? = null,
    val art: Bitmap? = null,
    val trackNumber: Int? = 0,
    //val trackNum: Int = 0,
    val discNumber: Int? = 0,
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
        artistId = songInfo.artistId ?: -1,
        artistName = artistInfo.name,
        albumId = songInfo.albumId ?: -1,
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
    return PlayerSong(
        id = id,
        title = title,
        artistId = artistId ?: -1,
        artistName = artistName ?: "",
        albumId = albumId ?: -1,
        albumTitle = albumTitle ?: "",
        duration = duration,
        trackNumber = trackNumber,
        discNumber = discNumber,
        artwork = "",
    )
}


//convert a song into media playable item
@UnstableApi
fun MediaItem.asExternalModel() = PlayerSong(
    id = mediaId.toLong(),
    title = mediaMetadata.title.toString(),
    artistName = mediaMetadata.artist.toString(),
    albumTitle = mediaMetadata.albumTitle.toString(),
    duration = ofMillis(mediaMetadata.durationMs?: 0),
    artworkUri = toAlbumArtUri(mediaId.toLong()),
    //artwork = ContentResolver.(mediaMetadata.artworkUri)
)

fun Audio.audioToPlayerSong(): PlayerSong {
    domainLogger.info { "$TAG - Audio to PlayerSong - id: $id + title: $title + \n" +
            "trackNumber: $trackNumber + discNumber: $discNumber + \n" +
            "cdTrackNum: $cdTrackNumber + srcTrackNum: $srcTrackNumber + \n" +
            "genre: $genre + composer: $composer" }
    return PlayerSong(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artist,
        albumId = albumId,
        albumTitle = album,
        duration = ofMillis(duration.toLong()),
        trackNumber = cdTrackNumber,
        discNumber = discNumber,
        artwork = toAlbumArtUri(id).toString(),
        artworkUri = toAlbumArtUri(id),
    )
}

@OptIn(UnstableApi::class)
fun PlayerSong.toMediaItem() = MediaItem.Builder()
    .setMediaId(id.toString())
    .setUri("/storage/emulated/0/Music/Homestuck/Homestuck - Strife!/01 Stormspirit.mp3")
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artistName)
            .setAlbumTitle(albumTitle)
            .setDurationMs(duration.toMillis())
            .setTrackNumber(trackNumber)
            .setArtworkUri(toAlbumArtUri(id))
            .build()
    )
    .build()
