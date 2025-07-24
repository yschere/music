package com.example.music.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.RepeatType
import com.example.music.data.util.combine
import com.example.music.domain.model.SongInfo
//import com.example.music.domain.player.SongPlayer
//import com.example.music.domain.player.SongPlayerState
import com.example.music.domain.player.model.toMediaItem
import com.example.music.service.SongController
import com.example.music.domain.usecases.GetSongDataV2
import com.example.music.domain.usecases.GetThumbnailUseCase
import com.example.music.ui.Screen
//import com.example.music.util.MediaNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.music.util.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

/** Changelog:
 *
 * 7/22-23/2025 - Revised to separate PlayerUiState from songControllerState
 * Removed PlayerSong completely
 */

private const val TAG = "Player View Model"

data class PlayerUiState(
    //val songControllerState: SongControllerState = SongControllerState()
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    //val _currentSong: MediaItem,
    val currentSong: SongInfo = SongInfo(),
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val repeatState: RepeatType = RepeatType.OFF,
    val timeElapsed: Duration = Duration.ZERO,
    val hasNext: Boolean = false,
    // put in there the variables that would be necessary for populating values to the view model
    // make sure to remove as many dependencies from songController and its state as i can
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    //val mediaPlayer: Player,
    //appPreferencesRepo: AppPreferencesRepo,
    getSongDataV2: GetSongDataV2,
    private val songController: SongController, //equivalent of musicController
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    //@Inject
    //lateinit var mediaPlayer: Player

    // songId should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val _songId: String =
        savedStateHandle.get<String>(Screen.ARG_SONG_ID)!! //Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)
    private val songId = _songId.toLong()

    private val getSongData = getSongDataV2(songId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    //private lateinit var notificationManager: MediaNotificationManager
    //protected lateinit var mediaSession: MediaSession
    //private val serviceJob = SupervisorJob()
    //private val serviceScope = CoroutineScope( Dispatchers.Main + serviceJob)
    //private var isStarted = false

    private val _state = MutableStateFlow(PlayerUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlayerUiState>
        get() = _state

    init {
        logger.info { "$TAG - songID: $songId"}
        viewModelScope.launch {
            logger.info { "$TAG - init viewModelScope launch start" }
            //TODO: using for comparison between SongToAlbum/SongInfo against PlayerSong for populating Player Screen
            //songRepo.getSongAndAlbumBySongId(songId).flatMapConcat { //original code: used to get SongToAlbum to convert to PlayerSong,
            //songPlayer.currentSong = it.toPlayerSong() //original code: used to set songPlayer.currentSong from SongToAlbum to PlayerSong

            //here would need to take in songId and correlate it to Audio in MediaStore, retrieve data and populate a MediaItem to be a MediaSource for MediaService
            // then populate queue and start play

            combine(
                refreshing,
                getSongData,
            ) {
                refreshing,
                songData, ->
                logger.info { "$TAG - PlayerUiState call"}
                logger.info { "$TAG - getSongID: ${songData.id}"}
                logger.info { "$TAG - getSongTitle: ${songData.title}"}

                songController.setMediaItem(songData)
                logger.info { "$TAG - is SongController available: ${songController.currentSong?.mediaId}"}
                logger.info { "$TAG - isReady?: ${!refreshing}" }

                PlayerUiState(
                    isReady = !refreshing,
                    currentSong = songData,
                    isPlaying = songController.getIsPlaying(),
                    isShuffled = songController.getIsShuffled(),
                    repeatState = songController.getRepeatState(),
                    timeElapsed = songController.getTimeElapsed(),
                    hasNext = songController.getHasNext()
                )
            /* // previous version of getting song data to set PlayerUiState and songController
            getSongDataV2(songId).flatMapConcat { item ->
                //this needs to start the song controller with the id
                // so it can set the player/mediaPlayer with the correct data to work on

                //the actual logic here might not actually apply to here but more so to media session but just for now to see if the problem of player not playing anything might be here
                // want to make sure that the controller can propagate the songId from PlayerScreen to mediaPlayer
                // so need to setMediaItem to item

                songController.setMediaItem(item)
                songController.currentSong = item.toMediaItem
                songController.playerState
            }
            */

            }.catch { throwable ->
                emit(
                    PlayerUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{
                _state.value = it
            }
            /* // original version
            songRepo.getSongById(songId).flatMapConcat {
                songPlayer.currentSong = getSongDataUseCase(it).first() //getSongDataUseCase returns Flow<PlayerSong>, so use single() to retrieve the PlayerSong
                songPlayer.playerState
            }.map {
                PlayerUiState(songPlayerState = it)
            }.collect {
                uiState = it
            }*/
        }
        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        logger.info { "$TAG - Refresh call" }
        viewModelScope.launch {
            runCatching {
                logger.info { "$TAG - refresh runCatching" }
                refreshing.value = true
            }.onFailure {
                logger.info { "$it ::: runCatching, not sure what is failing here tho" }
            } // TODO: look at result of runCatching and show any errors

            logger.info { "$TAG - refresh to be false -> sets screen to ready state" }
            refreshing.value = false
        }
    }

    fun onPlay() {
        logger.info { "$TAG - Hitting play on the now playing screen." }
        songController.play()
        //mediaPlayer.playWhenReady
        //mediaPlayer.play()
    }

    fun onPause() {
        songController.pause()
        //mediaPlayer.pause()
    }

    fun onStop() {
        songController.stop()
        //mediaPlayer.stop()
    }

    fun onPrevious() {
        songController.previous()
        //mediaPlayer.seekToPrevious()
    }

    fun onNext() {
        songController.next()
        //mediaPlayer.seekToNextMediaItem()
    }

    fun onSeekingStarted() {
        songController.onSeekingStarted()
//        mediaPlayer.seekToDefaultPosition()
    }

    fun onSeekingFinished(duration: Duration) {
        songController.onSeekingFinished(duration)
        //mediaPlayer.seekTo(duration.toMillis())
    }

    fun onShuffle() {
        songController.onShuffle()
        //mediaPlayer.shuffleModeEnabled
    }

    fun onRepeat() {
        songController.onRepeat()
        //mediaPlayer.repeatMode
    }

    fun onDestroy() {
        //onClose()
        //mediaPlayer.release()
    }

//    fun onAddToQueue() {
//        uiState.songPlayerState.currentSong?.let {
//            songPlayer.addToQueue(it)
//        }
//    }
}