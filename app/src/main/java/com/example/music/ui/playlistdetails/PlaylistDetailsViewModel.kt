/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.ui.playlistdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.PlaylistStore
import com.example.music.model.PlaylistInfo
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

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Ready(
        val playlist: PlaylistInfo,
        val songs: List<SongInfo>,
    ) : PlaylistUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Playlist details screen.
 */
@HiltViewModel(assistedFactory = PlaylistDetailsViewModel.PlaylistDetailsViewModelFactory::class)
class PlaylistDetailsViewModel @AssistedInject constructor(
    private val songPlayer: SongPlayer,
    private val playlistStore: PlaylistStore,
    @Assisted val playlistId: Long,
) : ViewModel() {

    @AssistedFactory
    interface PlaylistDetailsViewModelFactory {
        fun create(albumId: Long): PlaylistDetailsViewModel
    }

    val state: StateFlow<PlaylistUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            playlistStore.getPlaylistExtraInfo(playlistId),
            playlistStore.getSongsInPlaylistSortedByTrackNumberAsc(playlistId)
        ) { playlist, _songs ->
            val songs = _songs.map { it.asExternalModel() }
            PlaylistUiState.Ready(
                playlist = playlist.asExternalModel(),
                songs = songs,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistUiState.Loading
        )

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}
