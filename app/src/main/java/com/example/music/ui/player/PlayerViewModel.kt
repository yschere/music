package com.example.music.ui.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.SongStore
import com.example.music.player.SongPlayer
import com.example.music.player.SongPlayerState
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

data class PlayerUiState(
    val songPlayerState: SongPlayerState = SongPlayerState()
)
private val logger = KotlinLogging.logger{}
/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    songStore: SongStore,
    private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    //val _songId = Screen.arg_song_id.toLong()
    private val _songId: String =
        savedStateHandle.get<String>(Screen.arg_song_id)!! //Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)
    private val songId = _songId.toLong()
    var uiState by mutableStateOf(PlayerUiState())
        private set
//    BasicConfigurator.configure()
//    logger.info { "Home Content function start" }
    init {
        viewModelScope.launch {
            songStore.songAndAlbumBySongId(songId).flatMapConcat { //get song and album by id? thru songstore
                songPlayer.currentSong = it.toPlayerSong()
                songPlayer.playerState
            }.map {
                PlayerUiState(songPlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

    fun onPlay() {
        songPlayer.play()
    }

    fun onPause() {
        songPlayer.pause()
    }

    fun onStop() {
        songPlayer.stop()
    }

    fun onPrevious() {
        songPlayer.previous()
    }

    fun onNext() {
        songPlayer.next()
    }

    fun onSeekingStarted() {
        songPlayer.onSeekingStarted()
    }

    fun onSeekingFinished(duration: Duration) {
        songPlayer.onSeekingFinished(duration)
    }

    fun onShuffle() {
        songPlayer.onShuffle()
    }//TODO

    fun onRepeat() {
        songPlayer.onRepeat()
    }//TODO

//    fun onAddToQueue() {
//        uiState.songPlayerState.currentSong?.let {
//            songPlayer.addToQueue(it)
//        }
//    }
}