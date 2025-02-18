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
 * Use case to retrieve Artist data, songs in Artist as List<SongInfo>, and songs in Artist as List<PlayerSong>.
 * @param artistId [Long] to return flow of PlayerSong(song, artist, album)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetArtistDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val artistRepo: ArtistRepo,
) {

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
            artist, albums, songs, pSongs ->
            ArtistDetailsFilterResult(
                artist.asExternalModel(),
                albums.map { it.asExternalModel() },
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}