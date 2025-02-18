package com.example.music.domain

import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.AlbumDetailsFilterResult
import com.example.music.model.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Use case to retrieve Album data, songs in Album as List<SongInfo>, and songs in Album as List<PlayerSong>.
 * @param albumId [Long] to return flow of PlayerSong(song, artist, album)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetAlbumDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val albumRepo: AlbumRepo,
    private val songRepo: SongRepo
) {

    operator fun invoke(albumId: Long): Flow<AlbumDetailsFilterResult> {
        domainLogger.info { "Get Album Details Use Case - start: AlbumID: $albumId" }
        val albumFlow = albumRepo.getAlbumWithExtraInfo(albumId)

        domainLogger.info { "Get Album Details Use Case - Get Songs by Album ID" }
        val songsFlow = songRepo.getSongsByAlbumId(albumId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Album Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            albumFlow,
            songsFlow,
            pSongsFlow,
        ) {
            album, songs, pSongs ->
            AlbumDetailsFilterResult(
                album.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}