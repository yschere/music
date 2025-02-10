package com.example.music.model

//import com.example.music.data.database.model.SongToPlaylist

data class PlaylistToSongInfo(
    val songs: List<SongInfo> = emptyList(),
    val playlist: PlaylistInfo,
)

//fun PlaylistsToSong.asPlaylistToSongInfo(): PlaylistToSongInfo =
//    PlaylistToSongInfo(
//        song = song.asExternalModel(),
//        playlists = playlists.asPlaylistToSongInfo(),
//    )
