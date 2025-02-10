package com.example.music.domain

import com.example.music.data.repository.PlaylistRepo
import com.example.music.model.PlaylistDetailsFilterResult
import com.example.music.model.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve Playlist data, songs in Playlist as List<SongInfo>, and songs in Playlist as List<PlayerSong>.
 * @param playlistId [Long] to return flow of PlayerSong(song, artist, album)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetPlaylistDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val playlistRepo: PlaylistRepo,
) {

    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        val playlistFlow = playlistRepo.getPlaylistExtraInfo(playlistId)

        val pSongsListFlow = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)
            .flatMapLatest { item ->
                getSongDataUseCase(item)
            }

        val songsFlow = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)

        return combine(
            playlistFlow,
            songsFlow,
            pSongsListFlow,
        ) {
            playlist, songs, pSongs ->
            PlaylistDetailsFilterResult(
                playlist.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}