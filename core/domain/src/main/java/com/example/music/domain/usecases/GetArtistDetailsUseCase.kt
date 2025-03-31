package com.example.music.domain

import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistDetailsFilterResult
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve data for [ArtistDetailsFilterResult] domain model for ArtistDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property artistRepo [ArtistRepo] The repository for accessing Artist data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetArtistDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val artistRepo: ArtistRepo,
) {
    /**
     * Invoke with artistId to retrieve ArtistDetailsFilterResult data
     * @param artistId [Long] to return flow of ArtistDetailsFilterResult
     */
    operator fun invoke(artistId: Long): Flow<ArtistDetailsFilterResult> {
        domainLogger.info { "Get Artist Details Use Case - start: ArtistID: $artistId" }
        val artistFlow = artistRepo.getArtistWithExtraInfo(artistId)

        domainLogger.info { "Get Artist Details Use Case - Get Albums by Artist ID" }
        val albumsFlow = artistRepo.getAlbumsByArtistId(artistId)

        domainLogger.info { "Get Artist Details Use Case - Get Songs by Artist ID" }
        val songsFlow = artistRepo.getSongsByArtistId(artistId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Artist Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            artistFlow,
            albumsFlow,
            songsFlow,
            pSongsFlow,
        ) {
            artist,
            albums,
            songs,
            pSongs, ->
            ArtistDetailsFilterResult(
                artist.asExternalModel(),
                albums.map { it.asExternalModel() },
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}