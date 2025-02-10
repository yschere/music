package com.example.music.model

import com.example.music.data.database.model.SongToAlbum

/**
 * Data class for an object that combines a song and its album data.
 */
data class AlbumToSongInfo (
    val song: SongInfo,
    val album: AlbumInfo,
)

fun SongToAlbum.asAlbumToSongInfo(): AlbumToSongInfo =
    AlbumToSongInfo(
        song = song.asExternalModel(),
        album = album.asExternalModel()
    )