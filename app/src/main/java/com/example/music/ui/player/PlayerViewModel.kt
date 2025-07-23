package com.example.music.ui.player

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.source.MediaSource
import com.example.music.data.repository.SongRepo
import com.example.music.domain.usecases.GetSongDataUseCase
import com.example.music.domain.player.SongPlayer
import com.example.music.domain.player.SongPlayerState
import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.player.model.toMediaItem
import com.example.music.domain.player.model.toMediaSource
import com.example.music.service.SongController
import com.example.music.service.SongControllerState
import com.example.music.domain.usecases.GetSongDataV2
import com.example.music.domain.usecases.GetThumbnailUseCase
import com.example.music.ui.Screen
//import com.example.music.util.MediaNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.music.util.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

private const val TAG = "Player View Model"

data class PlayerUiState(
    val songControllerState: SongControllerState = SongControllerState()
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    //context: Context,
    //songRepo: SongRepo,
    val mediaPlayer: Player,
    //appPreferencesRepo: AppPreferencesRepo,
    private val getSongDataV2: GetSongDataV2,
    //private val getSongDataUseCase: GetSongDataUseCase,
    private val songController: SongController, //equivalent of musicController
    savedStateHandle: SavedStateHandle,

    //ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    //@Inject
    //lateinit var mediaPlayer: Player

    // songId should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val _songId: String =
        savedStateHandle.get<String>(Screen.ARG_SONG_ID)!! //Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)
    private val songId = _songId.toLong()
    var uiState by mutableStateOf(PlayerUiState())
        private set

    //private lateinit var notificationManager: MediaNotificationManager
    //protected lateinit var mediaSession: MediaSession
    //private val serviceJob = SupervisorJob()
    //private val serviceScope = CoroutineScope( Dispatchers.Main + serviceJob)
    //private var isStarted = false

    init {
        logger.info { "$TAG - init viewModelScope launch start" }
        logger.info { "$TAG - songID: $songId"}
        viewModelScope.launch {
            //TODO: using for comparison between SongToAlbum/SongInfo against PlayerSong for populating Player Screen
            //songRepo.getSongAndAlbumBySongId(songId).flatMapConcat { //original code: used to get SongToAlbum to convert to PlayerSong,
            //songPlayer.currentSong = it.toPlayerSong() //original code: used to set songPlayer.currentSong from SongToAlbum to PlayerSong

            //here would need to take in songId and correlate it to Audio in MediaStore, retrieve data and populate a MediaItem to be a MediaSource for MediaService
            // then populate queue and start play

            getSongDataV2(songId).flatMapConcat { item ->
                //this needs to start the song controller with the id
                // so it can set the player/mediaPlayer with the correct data to work on

                //the actual logic here might not actually apply to here but more so to media session but just for now to see if the problem of player not playing anything might be here
                // want to make sure that the controller can propagate the songId from PlayerScreen to mediaPlayer
                // so need to setMediaItem to item

                songController.setMediaItem(item)

//                songController.addMediaItem(item)
//                songController.currentSong = item.toMediaSource
                songController.currentSong = item.toMediaItem()
                songController.playerState
            }.map {
                PlayerUiState(songControllerState = it)
            }.collect{
                uiState = it
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

        //preparePlayer: I assume this is now media3 sets up media player
//        songController.preparePlayer()

       // setupQueue()
    }

     //setupPlaylist: I assume this is how media3 instantiates queue list
    private fun setupQueue() {
/*
        val videoItems: ArrayList<MediaSource> = arrayListOf()
        songPlayer.queue.forEach {

            val mediaMetaData = MediaMetadata.Builder()
                .setArtworkUri(Uri.parse(it.teaserUrl))
                .setTitle(it.title)
                .setAlbumArtist(it.artistName)
                .build()

            val trackUri = Uri.parse(it.audioUrl)
            val mediaItem = MediaItem.Builder()
                .setUri(trackUri)
                .setMediaId(it.id)
                .setMediaMetadata(mediaMetaData)
                .build()
            val dataSourceFactory = DefaultDataSource.Factory(context)

            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

            videoItems.add(
                mediaSource
            )
        }
*/
//        onStart(context)

        //mediaPlayer.playWhenReady = true
//        mediaPlayer.setMediaSources(videoItems)
        //mediaPlayer.prepare()
        //songController.play()
    }

    fun onPlay() {
        logger.info("$TAG - Hitting play on the now playing screen.")
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
//        onClose()
        //mediaPlayer.release()
    }

//    fun onAddToQueue() {
//        uiState.songPlayerState.currentSong?.let {
//            songPlayer.addToQueue(it)
//        }
//    }
}