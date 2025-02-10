package com.example.music.ui.artistdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ArtistRepo
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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ArtistUiState {
    data object Loading : ArtistUiState
    data class Ready(
        val artist: ArtistInfo,
        val albums: PersistentList<AlbumInfo> = persistentListOf(),
        val songs: PersistentList<SongInfo> = persistentListOf(),
    ) : ArtistUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Artist details screen.
 */
@HiltViewModel(assistedFactory = ArtistDetailsViewModel.ArtistDetailsViewModelFactory::class)
class ArtistDetailsViewModel @AssistedInject constructor(
    private val songPlayer: SongPlayer,
    private val artistRepo: ArtistRepo,
    @Assisted private val artistId: Long,
    //artistId is an argument needed for the selected artist details to view
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
            artistRepo.getArtistById(artistId),
            artistRepo.getAlbumsByArtistId(artistId),
            artistRepo.getSongsByArtistId(artistId)
        ) { artist, albums, songs ->
            ArtistUiState.Ready(
                artist = artist.asExternalModel(),
                albums = albums.map{it.asExternalModel()}.toPersistentList(),
                songs = songs.map{it.asExternalModel()}.toPersistentList(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistUiState.Loading
        )

    fun onAlbumSelect(album: AlbumInfo) {
        viewModelScope.launch{
            //TODO: load albumDetails page for album selected here?
        }
    }

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    } //keeping this here for now, but would only be in use if songs list does appear on ArtistDetailsScreen
}
