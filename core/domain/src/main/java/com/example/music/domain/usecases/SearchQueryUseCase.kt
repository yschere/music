package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.ComposerRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.SearchQueryFilterResult
import com.example.music.model.asExternalModel
import com.example.music.player.model.PlayerSong
import com.example.music.util.combine
import javax.inject.Inject
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

/**
 * Use case which returns the count of songs, artists, albums and playlists
 * in the library to display in the navigation drawer.
 */
class SearchQueryUseCase @Inject constructor(
    private val getArtistDataUseCase: GetSongArtistDataUseCase,
    private val getAlbumDataUseCase: GetSongAlbumDataUseCase,
    private val songRepo:SongRepo,
    private val artistRepo: ArtistRepo,
    private val albumRepo: AlbumRepo,
    private val composerRepo: ComposerRepo
) {

    operator fun invoke(query: String): Flow<SearchQueryFilterResult> {
        if (query == "") {
            return flowOf(SearchQueryFilterResult())
        }
        domainLogger.info { "SearchQueryUseCase start" } //limit top 50 results
        val songsFlow = songRepo.searchSongsByTitle(query, 50)
        val artistsFlow = artistRepo.searchArtistsByName(query, 50)
        val albumsFlow = albumRepo.searchAlbumsByTitle(query, 50)
        val composersFlow = composerRepo.searchComposersByName(query, 50)

        return combine(
            songsFlow,
            artistsFlow,
            albumsFlow,
            composersFlow,
        ) {
            songs,
            artists,
            albums,
            composers ->

            domainLogger.info { "SearchQueryUseCase: \n " +
                    "total query Songs: ${songs.size} \n " +
                    "total query Artists: ${artists.size} \n " +
                    "total query Albums: ${albums.size} \n " +
                    "total query Composers: ${composers.size}" }
            SearchQueryFilterResult(
                songs = songs.map { it.asExternalModel() },
                pSongs = songs.map { item ->
                    val song = item.asExternalModel()
                    PlayerSong(
                        song,
                        getArtistDataUseCase(song).first(),
                        getAlbumDataUseCase(song).first()
                    )
                },
                artists = artists.map { it.asExternalModel() },
                albums = albums.map { it.asExternalModel() },
                composers = composers.map { it.asExternalModel() }
            )
        }
    }
}