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

package com.example.music.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.AlbumStore
import com.example.music.data.repository.SongStore
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
import kotlinx.coroutines.launch

sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Ready(
        val album: AlbumInfo,
        val songs: List<SongInfo>,
    ) : AlbumUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Album details screen.
 */
@HiltViewModel(assistedFactory = AlbumDetailsViewModel.AlbumDetailsViewModelFactory::class)
class AlbumDetailsViewModel @AssistedInject constructor(
    private val songStore: SongStore,
    private val songPlayer: SongPlayer,
    private val albumStore: AlbumStore,
    @Assisted val albumId: Long,
    //albumId is an argument needed for the album details to view
) : ViewModel() {

    @AssistedFactory
    interface AlbumDetailsViewModelFactory {
        fun create(albumId: Long): AlbumDetailsViewModel
    }

    val state: StateFlow<AlbumUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            albumStore.albumWithExtraInfo(albumId),
            songStore.songsInAlbum(albumId)
        ) { album, songsToAlbum ->
            val songs = songsToAlbum.map { it.song.asExternalModel() }
            AlbumUiState.Ready(
                album = album.album.asExternalModel(),//.copy(isSubscribed = podcast.isFollowed),
                songs = songs,
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
