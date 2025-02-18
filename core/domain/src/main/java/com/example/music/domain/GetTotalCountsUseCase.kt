package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import javax.inject.Inject
import com.example.music.util.domainLogger

/**
 * Goal: to create use case which returns the most recently played playlists and the most recently added songs to populate the Home screen
 */
class GetTotalCountsUseCase @Inject constructor(
        private val songRepo:SongRepo,
        private val artistRepo: ArtistRepo,
        private val albumRepo: AlbumRepo,
        private val playlistRepo:PlaylistRepo
) {

    //TODO: trying to rework this so that it checks stores for these items, should only be reliant on passed in stores, not individual items
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