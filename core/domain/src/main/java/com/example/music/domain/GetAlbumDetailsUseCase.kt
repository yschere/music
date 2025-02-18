package com.example.music.domain

import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistDetailsFilterResult
import com.example.music.model.asExternalModel
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
class GetAlbumDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val artistRepo: ArtistRepo,
) {

    operator fun invoke(artistId: Long): Flow<ArtistDetailsFilterResult> {
        val artistFlow = artistRepo.getArtistById(artistId)

        val albumsFlow = artistRepo.getAlbumsByArtistId(artistId)

        val songsFlow = artistRepo.getSongsByArtistId(artistId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
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