package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import javax.inject.Inject
import com.example.music.util.domainLogger

/**
 * Use case which returns the count of songs, artists, albums and playlists
 * in the library to display in the navigation drawer.
 */
class GetTotalCountsUseCase @Inject constructor(
        private val songRepo:SongRepo,
        private val artistRepo: ArtistRepo,
        private val albumRepo: AlbumRepo,
        private val playlistRepo:PlaylistRepo
) {

    suspend operator fun invoke(): List<Int> {
        domainLogger.info { "GetTotalCountsUseCase start" }
        val songsTotal = songRepo.count()
        val artistsTotal = artistRepo.count()
        val albumsTotal = albumRepo.count()
        val playlistsTotal = playlistRepo.count()

        domainLogger.info { "GetTotalCountsUseCase: \n total Songs: $songsTotal \n total Artists: $artistsTotal \n total Albums: $albumsTotal \n total Playlists: $playlistsTotal" }
        return listOf(songsTotal,artistsTotal,albumsTotal,playlistsTotal)
    }
}