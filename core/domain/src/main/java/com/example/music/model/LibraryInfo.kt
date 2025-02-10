package com.example.music.model

/**
 * Data class for Library, contains a list of AlbumToSongInfo, ie an object list of combined album and song data
 * TODO: likely needs to be revisited if the structure of contents for Library changes
 */

data class LibraryInfo(
    val songs: List<AlbumToSongInfo> = emptyList()
) : List<AlbumToSongInfo> by songs

/*
data class LibraryInfo(
    val songs: List<SongInfo> = emptyList()
) : List<SongInfo> by songs //iteration of Library info that was just list of songs

data class LibraryInfo(
    val songs: List<PlaylistToSongInfo> = emptyList()
) : List<PlaylistToSongInfo> by songs //iteration of Library info that was an object list of combined playlist and song data
 */

/*
    When LibraryInfo was list<PlaylistToSongInfo>, initially intended to store songs and
    playlists data together. Later set to SongInfo so that at
    least the song data will be shown to Home Screen. Currently set to AlbumToSongInfo so that
    both song data and album data are stored and shown.
 */