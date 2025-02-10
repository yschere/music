package com.example.music.ui.albumdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Ready(
        val album: AlbumInfo,
        val songs: List<SongInfo>, //PersistentList<SongInfo> = persistentListOf(),
    ) : AlbumUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Album details screen.
 */
@HiltViewModel(assistedFactory = AlbumDetailsViewModel.AlbumDetailsViewModelFactory::class)
class AlbumDetailsViewModel @AssistedInject constructor(
    private val songRepo: SongRepo,
    private val songPlayer: SongPlayer,
    private val albumRepo: AlbumRepo,
    @Assisted val albumId: Long,
    //albumId is an argument needed for the selected album details to view
) : ViewModel() {

    @AssistedFactory
    interface AlbumDetailsViewModelFactory {
        fun create(albumId: Long): AlbumDetailsViewModel
    }

    val state: StateFlow<AlbumUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            albumRepo.getAlbumWithExtraInfo(albumId),
            songRepo.getSongsAndAlbumByAlbumId(albumId)
        ) { album, songsToAlbum ->
            val songs = songsToAlbum.map { it.song.asExternalModel() }
            AlbumUiState.Ready(
                album = album.album.asExternalModel(),
                songs = songs,//toPersistentList(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumUiState.Loading
        )

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}
