package com.example.music.ui.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.SongRepo
import com.example.music.domain.usecases.GetSongDataUseCase
import com.example.music.domain.player.SongPlayer
import com.example.music.domain.player.SongPlayerState
import com.example.music.domain.usecases.GetSongDataV2
import com.example.music.ui.Screen
//import com.example.music.util.MediaNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.music.util.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
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
    //context: Context,
    songRepo: SongRepo,
    //appPreferencesRepo: AppPreferencesRepo,
    private val getSongDataV2: GetSongDataV2,
    private val getSongDataUseCase: GetSongDataUseCase,
    private val songPlayer: SongPlayer, //equivalent of musicController
    //val mediaPlayer: MediaPlayer,
    savedStateHandle: SavedStateHandle,

    //ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

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
        logger.info { "Player View Model - init viewModelScope launch start" }
        logger.info { "Player View Model - songID: $songId"}
        viewModelScope.launch {
            /*appPreferencesRepo.appPreferencesFlow.collect{ values ->
                songPlayer.
            }*/
            //TODO: using for comparison between SongToAlbum/SongInfo against PlayerSong for populating Player Screen
            //songRepo.getSongAndAlbumBySongId(songId).flatMapConcat { //original code: used to get SongToAlbum to convert to PlayerSong,
                //songPlayer.currentSong = it.toPlayerSong() //original code: used to set songPlayer.currentSong from SongToAlbum to PlayerSong

            //here would need to take in songId and correlate it to Audio in MediaStore, retrieve data and populate a MediaItem to be a MediaSource for MediaService
            // then populate queue and start play

            getSongDataV2(songId).flatMapConcat { item ->
                songPlayer.currentSong = item
                songPlayer.playerState
            }.map {
                PlayerUiState(songPlayerState = it)
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
    }

    /* //preparePlayer: I assume this is now media3 sets up media player
    fun preparePlayer(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes, true)
        mediaPlayer.repeatMode = Player.REPEAT_MODE_ALL
        mediaPlayer.addListener(playerListener)
        setupPlaylist(context)
    }*/

    /* //setupPlaylist: I assume this is how media3 instantiates queue list
    private fun setupPlaylist(context: Context) {

        val videoItems: ArrayList<MediaSource> = arrayListOf()
        playlist.forEach {

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

        onStart(context)
        mediaPlayer.playWhenReady = false
        mediaPlayer.setMediaSources(videoItems)
        mediaPlayer.prepare()
    }*/

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
    }

    fun onRepeat() {
        songPlayer.onRepeat()
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