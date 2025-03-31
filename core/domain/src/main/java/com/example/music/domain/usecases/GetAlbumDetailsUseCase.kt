package com.example.music.domain

import com.example.music.data.database.model.Artist
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.model.AlbumDetailsFilterResult
import com.example.music.model.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case to retrieve data for [AlbumDetailsFilterResult] domain model for AlbumDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property albumRepo [AlbumRepo] The repository for accessing Album data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetAlbumDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val albumRepo: AlbumRepo,
) {
    /**
     * Invoke with albumId to retrieve AlbumDetailsFilterResult data
     * @param albumId [Long] to return flow of AlbumDetailsFilterResult
     */
    operator fun invoke(albumId: Long): Flow<AlbumDetailsFilterResult> {
        domainLogger.info { "Get Album Details Use Case - start: AlbumID: $albumId" }
        val albumFlow = albumRepo.getAlbumWithExtraInfo(albumId)

        val artistFlow = albumRepo.getAlbumArtistByAlbumId(albumId)

        domainLogger.info { "Get Album Details Use Case - Get Songs by Album ID" }
        val songsFlow = albumRepo.getSongsByAlbumId(albumId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Album Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            albumFlow,
            artistFlow,
            songsFlow,
            pSongsFlow,
        ) {
            album,
            artist,
            songs,
            pSongs, ->
            AlbumDetailsFilterResult(
                album.asExternalModel(),
                artist.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}