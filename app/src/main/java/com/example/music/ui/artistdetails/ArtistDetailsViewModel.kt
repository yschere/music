package com.example.music.ui.artistdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.GetArtistDetailsUseCase
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.SongPlayer
import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.usecases.GetArtistDetailsV2
import com.example.music.ui.Screen
import com.example.music.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Artist Details View Model"

/** ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 */

data class ArtistUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val artist: ArtistInfo = ArtistInfo(),
    val albums: /*Persistent*/List<AlbumInfo> = emptyList(),
    val songs: /*Persistent*/List<SongInfo> = emptyList(),
    val pSongs: /*Persistent*/List<PlayerSong> = emptyList(),
)

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    getArtistDetailsUseCase: GetArtistDetailsUseCase,
    getArtistDetailsV2: GetArtistDetailsV2,
    private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _artistId: String = savedStateHandle.get<String>(Screen.ARG_ARTIST_ID)!!
    private val artistId = _artistId.toLong()

    //private val getArtistDetailsData = getArtistDetailsUseCase(artistId)
    private val getArtistDetailsData = getArtistDetailsV2(artistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val _state = MutableStateFlow(ArtistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<ArtistUiState>
        get() = _state

    init {
        logger.info { "$TAG - artistId: $artistId" }
        viewModelScope.launch {
            logger.info { "$TAG - init viewModelScope launch start" }
            combine(
                refreshing,
                getArtistDetailsData,
            ) {
                refreshing,
                artistDetailsFilterResult, ->
                logger.info { "$TAG - ArtistUiState call" }
                logger.info { "$TAG - artistDetailsFilterResult ID: ${artistDetailsFilterResult.artist.id}" }
                logger.info { "$TAG - artistDetailsFilterResult albums: ${artistDetailsFilterResult.albums.size}" }
                logger.info { "$TAG - artistDetailsFilterResult songs: ${artistDetailsFilterResult.songs.size}" }
                logger.info { "$TAG - artistDetailsFilterResult pSongs: ${artistDetailsFilterResult.pSongs.size}" }
                logger.info { "$TAG - isReady?: ${!refreshing}" }

                ArtistUiState(
                    isReady = !refreshing,
                    artist = artistDetailsFilterResult.artist,
                    albums = artistDetailsFilterResult.albums,
                    songs = artistDetailsFilterResult.songs,
                    pSongs = artistDetailsFilterResult.pSongs
                )
            }.catch { throwable ->
                emit(
                    ArtistUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{
                _state.value = it
            }
        }
        refresh(force = false)

    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }
}

/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Artist details screen.
 */
/*
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
}*/