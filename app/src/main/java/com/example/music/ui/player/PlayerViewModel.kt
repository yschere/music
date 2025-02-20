package com.example.music.ui.player

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import coil3.Uri
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.data.repository.SongRepo
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.domain.GetSongDataUseCase
import com.example.music.model.asExternalModel
import com.example.music.player.SongPlayer
import com.example.music.player.SongPlayerState
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.Screen
//import com.example.music.util.MediaNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.music.util.logger
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.prefs.Preferences
import javax.inject.Inject

data class PlayerUiState(
    val songPlayerState: SongPlayerState = SongPlayerState()
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
//    context: Context,
    songRepo: SongRepo,
//    appPreferencesRepo: AppPreferencesRepo,
    private val getSongDataUseCase: GetSongDataUseCase,
    private val songPlayer: SongPlayer, //equivalent of musicController
    //val mediaPlayer: MediaPlayer,
    savedStateHandle: SavedStateHandle,

//    ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

//    class CurrentPreferencesRepository @Inject constructor(
//        private val dataStore: DataStore<Preferences>
//    )

    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    //val _songId = Screen.arg_song_id.toLong()
    private val _songId: String =
        savedStateHandle.get<String>(Screen.ARG_SONG_ID)!! //Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_URI)!!)
    private val songId = _songId.toLong()
    var uiState by mutableStateOf(PlayerUiState())
        private set

//    private lateinit var notificationManager: MediaNotificationManager
//    protected lateinit var mediaSession: MediaSession
//    private val serviceJob = SupervisorJob()
//    private val serviceScope = CoroutineScope( Dispatchers.Main + serviceJob)
//    private var isStarted = false

    init {
        logger.info { "Player View Model - init viewModelScope launch start" }
        logger.info { "Player View Model - songID: $songId"}
        viewModelScope.launch {
//            appPreferencesRepo.appPreferencesFlow.collect{ values ->
////                songPlayer.
//
//            }
            //TODO: using for comparison between SongToAlbum/SongInfo against PlayerSong for populating Player Screen
            //songRepo.getSongAndAlbumBySongId(songId).flatMapConcat { //original code: used to get SongToAlbum to convert to PlayerSong,
            songRepo.getSongById(songId).flatMapConcat {
                songPlayer.currentSong = getSongDataUseCase(it).first() //getSongDataUseCase returns Flow<PlayerSong>, so use single() to retrieve the PlayerSong
                //songPlayer.currentSong = it.toPlayerSong() //original code: used to set songPlayer.currentSong from SongToAlbum to PlayerSong
                songPlayer.playerState
            }.map {
                PlayerUiState(songPlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

//    fun preparePlayer(context: Context) {
//        val audioAttributes = AudioAttributes.Builder()
//            .setUsage(C.USAGE_MEDIA)
//            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
//            .build()
//
//        mediaPlayer.setAudioAttributes(audioAttributes, true)
//        mediaPlayer.repeatMode = Player.REPEAT_MODE_ALL
//        mediaPlayer.addListener(playerListener)
//        setupPlaylist(context)
//    }

//    private fun setupPlaylist(context: Context) {
//
//        val videoItems: ArrayList<MediaSource> = arrayListOf()
//        playlist.forEach {
//
//            val mediaMetaData = MediaMetadata.Builder()
//                .setArtworkUri(Uri.parse(it.teaserUrl))
//                .setTitle(it.title)
//                .setAlbumArtist(it.artistName)
//                .build()
//
//            val trackUri = Uri.parse(it.audioUrl)
//            val mediaItem = MediaItem.Builder()
//                .setUri(trackUri)
//                .setMediaId(it.id)
//                .setMediaMetadata(mediaMetaData)
//                .build()
//            val dataSourceFactory = DefaultDataSource.Factory(context)
//
//            val mediaSource =
//                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//
//            videoItems.add(
//                mediaSource
//            )
//        }
//
//        onStart(context)
//        mediaPlayer.playWhenReady = false
//        mediaPlayer.setMediaSources(videoItems)
//        mediaPlayer.prepare()
//    }

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