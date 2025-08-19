package com.example.music.ui.player

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.RepeatType
import com.example.music.data.util.combine
import com.example.music.domain.model.SongInfo
import com.example.music.service.SongController
import com.example.music.domain.usecases.GetSongDataV2
import com.example.music.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
    private val songController: SongController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // songId should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val _songId: String =
        savedStateHandle.get<String>(Screen.ARG_SONG_ID)!! //Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)
    private val songId = _songId.toLong()

    private val getSongData = getSongDataV2(songId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    private var currentSong: SongInfo? = null

    private val _state = MutableStateFlow(PlayerUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlayerUiState>
        get() = _state

    init {
        Log.i(TAG, "songID: $songId")
        viewModelScope.launch {
            Log.i(TAG, "init viewModelScope launch start")
            //Note: using for comparison between SongToAlbum/SongInfo against PlayerSong for populating Player Screen
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
                Log.i(TAG, "PlayerUiState call")
                Log.i(TAG, "getSongID: ${songData.id}")
                Log.i(TAG, "getSongTitle: ${songData.title}")

                currentSong = songData
                songController.setMediaItem(songData)
                //songController.play(songData)
                Log.i(TAG, "is SongController available: ${songController.currentSong}")
                Log.i(TAG, "isReady?: ${!refreshing}")

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
        playWhenReady()
    }

    fun refresh(force: Boolean = true) {
        Log.e(TAG, "Refresh call")
        Log.e(TAG, "refreshing: ${refreshing.value}")
        viewModelScope.launch {
            runCatching {
                Log.e(TAG,"Refresh runCatching")
                refreshing.value = true
            }.onFailure {
                Log.e(TAG, "$it ::: runCatching failed (not sure what this means)")
            }

            Log.e(TAG,"refresh to be false -> sets screen to ready state")
            refreshing.value = false
        }
    }

    /**
     * Intent: to autoplay the loaded song when the screen is loaded and the songController has the selected song queued
     */
    fun playWhenReady() {
        songController.preparePlayer()
        //songController.play(currentSong!!)
    }

    fun onPlay() {
        Log.i(TAG,"Hitting play on Player Screen.")
        songController.play(currentSong!!)
        //mediaPlayer.play()
    }

    fun onPause() {
        Log.i(TAG, "Hitting pause on Player Screen")
        songController.pause()
        //mediaPlayer.pause()
    }

    fun onStop() {
        Log.i(TAG, "Stop the Player Screen")
        songController.stop()
        //mediaPlayer.stop()
    }

    fun onPrevious() {
        Log.i(TAG, "Hitting previous button on Player Screen")
        songController.previous()
        //mediaPlayer.seekToPrevious()
    }

    fun onNext() {
        Log.i(TAG, "Hitting next button on Player Screen")
        songController.next()
        //mediaPlayer.seekToNextMediaItem()
    }

    fun onSeekingStarted() {
        songController.onSeekingStarted()
        //mediaPlayer.seekToDefaultPosition()
    }

    fun onSeekingFinished(duration: Duration) {
        songController.onSeekingFinished(duration)
        //mediaPlayer.seekTo(duration.toMillis())
    }

    fun onShuffle() {
        Log.i(TAG, "Hitting shuffle button on Player Screen")
        songController.onShuffle()
        //mediaPlayer.shuffleModeEnabled
    }

    fun onRepeat() {
        Log.i(TAG, "Hitting repeat button on Player Screen")
        songController.onRepeat()
        //mediaPlayer.repeatMode
    }

    fun setCurrSong(song: SongInfo) {
        currentSong = song
    }

    fun onDestroy() {
        //onClose()
        //mediaPlayer.release()
    }

}