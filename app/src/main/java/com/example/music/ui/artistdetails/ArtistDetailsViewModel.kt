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

package com.example.music.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ArtistStore
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
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

sealed interface ArtistUiState {
    data object Loading : ArtistUiState
    data class Ready(
        val artist: ArtistInfo,
        val albums: List<AlbumInfo>,
        val songs: List<SongInfo>,
    ) : ArtistUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Podcast details screen.
 */
@HiltViewModel(assistedFactory = ArtistDetailsViewModel.ArtistDetailsViewModelFactory::class)
class ArtistDetailsViewModel @AssistedInject constructor(
    //private val songStore: SongStore,
    private val songPlayer: SongPlayer,
    private val artistStore: ArtistStore,
    @Assisted private val artistId: Long,
    //artistId is an argument needed for the selected details to view
) : ViewModel() {

    @AssistedFactory
    interface ArtistDetailsViewModelFactory {
        fun create(artistId: Long): ArtistDetailsViewModel
    }

    val state: StateFlow<ArtistUiState> =
        combine(
            //should i be combining albums to artist here? as well as the songs?
            //if the details screen will contain both songs and albums, then yes i think
            //if the details screen will contain only albums, then only need to combine that
                //which, if it is just albums, how do i store the songs? and what do i do if someone selects to see songs list
            artistStore.getArtistById(artistId),
            artistStore.getAlbumsByArtistId(artistId),
            artistStore.getSongsByArtistId(artistId)
        ) { artist, albums, songs ->
            ArtistUiState.Ready(
                artist = artist.asExternalModel(),
                albums = albums.map{it.asExternalModel()},
                songs = songs.map{it.asExternalModel()},
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistUiState.Loading
        )

        //might very likely need a line for navigate to album details btn

//    fun onQueueSong(playerSong: PlayerSong) {
//        songPlayer.addToQueue(playerSong)
//    } //keeping this here for now, but would only be in use if songs list does appear on ArtistDetailsScreen
}
