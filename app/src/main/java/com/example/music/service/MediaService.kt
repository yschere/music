package com.example.music.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.Callback
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.RepeatType
import com.example.music.domain.player.model.asLocalMediaItem
import com.example.music.domain.player.model.mediaUri
import com.example.music.ui.player.NowPlaying
import com.example.music.ui.shared.isThirdPartyUri
import com.example.music.ui.shared.mediaItems
import com.example.music.ui.shared.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.roundToLong

private const val TAG = "Media Service"

/**
 * Intent: This service will control media session(s) that interacts with media player.
 *  It will extend media3's session service.
 */
@UnstableApi
@AndroidEntryPoint
class MediaService : MediaSessionService(), Callback, Player.Listener {

    /**
     * The media session of this service, it's initially null so that it can "exist" before being accessed later.
     * Used to have it be instantiated after mediaPlayer since it needs the Player object for builder creation.
     */
    private var mediaSession: MediaSession? = null

    /**
     * The timestamp, in milliseconds, representing the scheduled time to pause playback.
     * This variable is used to store the timestamp when playback should be paused in the future.
     * If no future pause is scheduled, the value is set to -1.
     */
    private var scheduledPauseTimeMillis = UNINITIALIZED_SLEEP_TIME_MILLIS

    /**
     * The scope of the media service, setting as a coroutine of Dispatchers.Main and the SupervisorJob
     */
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     *  A coroutine job that monitors the playback state and performs some actions based on it.
     *  It saves the current playback position every 5 seconds and pauses the player if the sleep time is reached.
     *  The job is cancelled when the service is destroyed or the playback is stopped.
     */
    private var sessionMonitorJob: Job? = null

    /**
     * The pending intent object that controls the activity context of the media session service
     */
    private val activity by lazy {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Likely supposed to be an object that would hold the variables/data for an equalizer object in the app settings
     */
    private var equalizer: Equalizer? = null

    /**
     * The Player object of the media session service.
     */
    @Inject
    lateinit var mediaPlayer: Player

    /**
     * The repository for accessing the app preferences data store.
     */
    @Inject
    lateinit var appPreferences: AppPreferencesRepo

    /**
     * This is how a media controller connects to a sessionService. Returns a MediaSession
     * that the controller will connect to, or null to reject the connection request.
     */
    override fun onGetSession(controllerInfo: ControllerInfo) =
        mediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: START")

        if (!this::mediaPlayer.isInitialized) {
            // this is to attempt to have mediaPlayer be initialized before it is required.
            // Because I've run into that error numerous times while trying to play an audio file in app
            Log.e(TAG, "MEDIA PLAYER NOT INITIALIZED")
            mediaPlayer.deviceInfo
        }

        // Moved mediaSession builder here so that it will be created as part of the creation of the service
        mediaSession = MediaSession.Builder(this, mediaPlayer)
            .setSessionActivity(activity)
            .build()

        // FUTURE THOUGHT: would instantiate any lateinit vars here. like the appPreferencesRepo
        //  or begin connection to preferences data store

        scope.launch{
            Log.i(TAG, "onCreate: scope launch start")
            runCatching {
                // creating the media service in this onCreate function should mean i would then need a mediaPlayer to be within its scope.
                // so that mediaPlayer needs to be here
                // but I would think that mediaPlayer being created and usable above with mediaSession means that is does exist and is connected.
                // so why do i also have songplayer here? is it separate from mediaPlayer? is it the domain version of mediaPlayer?


                //SongPlayer
                // is here where i build the connection to domain's songPlayer?
                // or do i remove songPlayer in favor of setting up an
                // actual controller to interface with mediaPlayer?

                //begin building mediaPlayer's mediaItems into queue here?
                //collect the preferences that set the queue play order

                // need a way that either begins by pulling whatever was in queue before
                // or begins by building an empty queue from scratch?
                // or waits for a user input that generates the need for mediaItems to be attached
                /*val items: List<MediaItem> = withContext(context = Dispatchers.IO) {  }*/

                //mediaPlayer.setMediaItems(items)
            }
            // want to get enabled / initial values from dataStore
                // get repeat type
                // get shuffle type
                // check if there is an active queue and retrieve it
            // get rest of media items from the playlist.getMembers
            // set media items to list items
            Log.i(TAG, "Setting media player shuffle mode from AppPreferences DataStore")
            mediaPlayer.shuffleModeEnabled = appPreferences.isShuffleEnabled()

            Log.i(TAG, "Setting media player repeat mode from AppPreferences DataStore")
            mediaPlayer.repeatMode = appPreferences.getRepeatTypeAsInt()

            if (mediaPlayer.mediaItems.isNotEmpty()) {
                Log.i(TAG, "mediaPlayer's mediaItems is not empty -> setting media items to media player")
                mediaPlayer.setMediaItems(mediaPlayer.mediaItems)
            }
            // check sort order

            Log.i(TAG, "Adding listener to a media player.")
            mediaPlayer.addListener(this@MediaService) //connects the MediaService's Player.Listener to mediaPlayer
            Log.i(TAG, "Added listener to a media player.")

            sendBroadcast(NowPlaying.from(this@MediaService, mediaPlayer))

            Log.i(TAG, "onCreate: scope launch end")
        }
        createNotificationChannel()
        showNotification()
        Log.i(TAG, "onCreate: END")
    }

    private fun createNotificationChannel() {}

    private fun showNotification() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.i(TAG, "onStartCommand: $action")

        if (action == null) {
            sendBroadcast(NowPlaying.from(this, mediaPlayer))
            return super.onStartCommand(intent, flags, startId)
        }

        if (sessions.find { it.id == mediaSession?.id } == null )
            mediaSession?.let { addSession(it) }

        if (mediaPlayer.playbackState != Player.STATE_READY)
            mediaPlayer.prepare()

        when (action) {
            NowPlaying.ACTION_TOGGLE_PLAY -> mediaPlayer.playWhenReady = !mediaPlayer.playWhenReady
            NowPlaying.ACTION_NEXT -> mediaPlayer.seekToNextMediaItem()
            NowPlaying.ACTION_PREVIOUS -> mediaPlayer.seekToPreviousMediaItem()
            NowPlaying.ACTION_SEEK_TO -> {
                val arg = intent.getFloatExtra( NowPlaying.EXTRA_SEEK_PCT, -1f )
                val position = if (arg != -1f) (arg * mediaPlayer.duration).roundToLong() else -1L
                if (position != -1L)
                    mediaPlayer.seekTo(position)
            }
        }
        mediaPlayer.play()
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("CheckResult")
    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<List<MediaItem>> =
        Futures.immediateFuture(
            mediaItems.map{ it.asLocalMediaItem }
        )

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int
    ) {
        Log.i(TAG, "onMediaItemTransition")
        scope.launch {
            // preferences[PREF_KEY_INDEX] = mediaPlayer.currentMediaItemIndex
            // save current index in preference?? seems like get current media item index
        }

        if (mediaItem == null || mediaItem.mediaUri?.isThirdPartyUri == true)
            return
        scope.launch(Dispatchers.IO) {
            // val limit of recent playlist limit
            //val limit = preferences[PREF_KEY_RECENT_PLAYLIST_LIMIT, 50]
            // playlists addToRecent
            //playlists.addToRecent(mediaItem, limit.toLong())
        }
        //mediaSession.notifyChildrenChanged(ROOT_QUEUE, 0, null)

        super.onMediaItemTransition(mediaItem, reason)
    }

    /*override fun onShuffleModeEnabledChanged(
        shuffleModeEnabled: Boolean
    ) {
        scope.launch() {
            appPreferences.updateIsShuffleEnabled(shuffleModeEnabled)
            // set the dataStore shuffle type to shuffleModeEnabled
        }
        //mediaSession.notifyChildrenChanged(ROOT_QUEUE, 0, null)
        //super.onShuffleModeEnabledChanged(shuffleModeEnabled)
    }*/

    /*override fun onRepeatModeChanged(
        repeatMode: Int
    ) {
        scope.launch() {
            appPreferences.updateRepeatType(RepeatType.entries[repeatMode])
            // set the dataStore repeat type to repeatMode
        }
    }*/

    /*override fun onTimelineChanged(
        timeline: Timeline,
        reason: Int
    ) {
        if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            val items = mediaPlayer.mediaItems

            scope.launch(Dispatchers.IO) {
                //playlists.save(items)
            }

            scope.launch {
                //preferences[PREF_KEY_ORDERS] = mediaPlayer.orders.joinToString("$LIST_ITEM_DELIMITER")
                // save playlists order
            }

            //mediaSession.notifyChildrenChanged(ROOT_QUEUE, 0, null)
        }
        super.onTimelineChanged(timeline, reason)
    }*/

    override fun onEvents(
        player: Player,
        events: Player.Events
    ) {
        if (!events.containsAny(*UPDATE_EVENTS))
            return
        sendBroadcast(NowPlaying.from(this, player))
        //super.onEvents(player, events)
    }

    override fun onPlayerError(error: PlaybackException) {
        //Toast.makeText(this, "Unplayable file", Toast.LENGTH_SHORT).show()
        // send toast of player error
        mediaPlayer.seekToNextMediaItem()
    }

    override fun onPlayWhenReadyChanged(
        isPlaying: Boolean,
        reason: Int
    ) {
        super.onPlayWhenReadyChanged(isPlaying, reason)

        if (!isPlaying) {
            sessionMonitorJob?.cancel()
            scheduledPauseTimeMillis = UNINITIALIZED_SLEEP_TIME_MILLIS
        } else
            sessionMonitorJob = scope.launch {
                var isPlay = mediaPlayer.playWhenReady
                do {
                    // get current playback position, set to preferences / datastore
                    //preferences[PREF_KEY_BOOKMARK] = mediaPlayer.currentPosition
                    Log.i(TAG, "Save playback position: ${mediaPlayer.currentPosition}")
                    if (scheduledPauseTimeMillis != UNINITIALIZED_SLEEP_TIME_MILLIS && scheduledPauseTimeMillis <= System.currentTimeMillis()) {
                        // Pause the player as the scheduled pause time has been reached.
                        mediaPlayer.pause()

                        // Once the scheduled pause has been triggered, reset the scheduled time to uninitialized.
                        scheduledPauseTimeMillis = UNINITIALIZED_SLEEP_TIME_MILLIS
                    }
                    // Delay for the specified time
                    delay(SAVE_POSITION_DELAY_MILLS)
                    isPlay = mediaPlayer.isPlaying
                    //Returns whether the player is playing, i. e. getCurrentPosition() is advancing.
                    //If false, then at least one of the following is true:
                    //The playback state is not ready.
                    //There is no intention to play.
                    //Playback is suppressed for other reasons.
                    //Returns:
                    //Whether the player is playing.
                    //See Also:
                    //Player. Listener. onIsPlayingChanged(boolean)
                    //val isPlaying = mediaPlayer.playWhenReady
                    //Whether playback will proceed when getPlaybackState() == STATE_READY.
                    //Returns:
                    //Whether playback will proceed when ready.
                    //See Also:
                    //Player. Listener. onPlayWhenReadyChanged(boolean, int)
                } while (isPlay)
        }
    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        // --- Need this because of error during initial play of a media item: UnsupportedOperationException: Make sure to implement MediaSession.Callback.onPlaybackResumption() if you add a media button receiver to your manifest or if you implement the recent media item contract with your MediaLibraryService.
        Log.i(TAG, "onPlaybackResumption: ")
        return Futures.immediateFuture(
            MediaSession.MediaItemsWithStartPosition(
                emptyList(),
                C.INDEX_UNSET,
                C.TIME_UNSET
            )
        )
        //return super.onPlaybackResumption(mediaSession,controller)
    }

    override fun onConnect(
        session: MediaSession,
        controller: ControllerInfo
    ): ConnectionResult {
        // obtain available commands by creating the set
        val available = ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()

        // adds the commands that can be set
        available.add(SessionCommand(ACTION_AUDIO_SESSION_ID, Bundle.EMPTY))
        available.add(SessionCommand(ACTION_SCHEDULE_SLEEP_TIME, Bundle.EMPTY))
        //available.add(SessionCommand(ACTION_EQUALIZER_CONFIG, Bundle.EMPTY))

        return ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(available.build())
            .build()
    }

    /*override fun onAudioSessionIdChanged(audioSessionId: Int) {
        if (audioSessionId != -1)
            super.onAudioSessionIdChanged(audioSessionId)

        if (equalizer == null || audioSessionId != -1) {
            equalizer?.release()
            equalizer = kotlin.runCatching {
                Equalizer(0, (mediaPlayer as ExoPlayer).audioSessionId)
            }.getOrNull()
        }

        scope.launch {
            equalizer?.enabled = preferences[PREK_KEY_EQUALIZER_ENABLED, false]

            val properties = preferences[PREF_KEY_EQUALIZER_PROPERTIES, ""]

            if (properties.isNotBlank()) {
                equalizer?.properties = Settings(properties)
            }
        }
    }*/

    override fun onCustomCommand(
        session: MediaSession,
        controller: ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        val action = customCommand.customAction
        return when (action) {
            ACTION_AUDIO_SESSION_ID -> {
                val audioSessionId = (mediaPlayer as ExoPlayer).audioSessionId
                val result = SessionResult(SessionResult.RESULT_SUCCESS) {
                    putInt(EXTRA_AUDIO_SESSION_ID, audioSessionId)
                }
                Futures.immediateFuture(result)
            }

            ACTION_SCHEDULE_SLEEP_TIME -> {
                val newTimeMillis = customCommand.customExtras.getLong(EXTRA_SCHEDULED_TIME_MILLS)
                if (newTimeMillis != 0L)
                    scheduledPauseTimeMillis = newTimeMillis

                Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS) {
                        putLong(EXTRA_SCHEDULED_TIME_MILLS, scheduledPauseTimeMillis)
                    }
                )
            }

            /*ACTION_EQUALIZER_CONFIG -> {
                val extras = customCommand.customExtras
                if (!extras.isEmpty) {
                    val isEqualizerEnabled = customCommand.customExtras.getBoolean(EXTRA_EQUALIZER_ENABLED)
                    val properties = customCommand.customExtras.getString(EXTRA_EQUALIZER_PROPERTIES, null)

                    scope.launch {
                        //preferences[PREF_KEY_EQUALIZER_PROPERTIES] = properties
                        //preferences[PREF_KEY_EQUALIZER_ENABLED] = isEqualizerEnabled
                    }
                    onAudioSessionIdChanged(-1)
                }

                Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS) {
                        putBoolean(
                            EXTRA_EQUALIZER_ENABLED,
                            runBlocking { preferences[PREF_KEY_EQUALIZER_ENABLED], false }
                        )
                        putString(
                            EXTRA_EQUALIZER_PROPERTIES,
                            runBlocking { preferences[PREF_KEY_EQUALIZER_PROPERTIES], "" }
                        )
                    }
                )
            }*/
            else -> {
                Futures.immediateFuture(SessionResult(SessionError.ERROR_UNKNOWN))
            }
        }
        //would need to define these custom commands that action can be
        // then use when switch cases to define the result and steps to take per action
        //return super.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaPlayer.playWhenReady = false
        /*if (runBlocking { preferences[PREF_KEY_CLOSE_WHEN_REMOVED, false] }) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }*/
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return super.onSetMediaItems(
            mediaSession,
            controller,
            mediaItems,
            startIndex,
            startPositionMs
        )
    }

    /*fun setMediaItem(uri: Uri) {
        val newItem = MediaItem.Builder()
            .setMediaId("$uri")
            .build()

        mediaPlayer.setMediaItem(newItem)
        mediaPlayer.prepare()
        mediaPlayer.play()
    }*/

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
    }

    /*override fun onPostConnect(session: MediaSession, controller: ControllerInfo) {
        super.onPostConnect(session, controller)
        return ConnectionResult.AcceptedResultBuilder(mediaSession)
            .setAvailableSessionCommands(available.build())
            .build()
    }*/

    /*override fun onMediaButtonEvent(
        session: MediaSession,
        controllerInfo: ControllerInfo,
        intent: Intent
    ): Boolean {
        return super.onMediaButtonEvent(session, controllerInfo, intent)
    }*/

    override fun getContentResolver(): ContentResolver {
        return super.getContentResolver()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        mediaSession?.release()
        //equalizer?.release()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val SAVE_POSITION_DELAY_MILLS = 5_000L // Delay in milliseconds for saving position.

        //val PLAYLIST_FAVORITES = "favorite" // The name of the global 'Favourite' playlist in [Playlists].
        //val PLAYLIST_RECENTS = "recents" // The name of the global 'Recent' playlist in [Playlists].
        //val PLAYLIST_QUEUE = "queue" // The name of the global 'Queue' playlist in [Playlists].

        //const val ROOT_QUEUE = "com.example.music.player" // The root identifier for the queue-related content served by this service.
            // Used as a key to access queue-related content in the application's data structure.

        private const val PREFIX = "com.example.music"
        const val ACTION_AUDIO_SESSION_ID = "$PREFIX.action.AUDIO_SESSION_ID" // A action string for [SessionCommand] for getting [EXTRA_AUDIO_SESSION_ID].
            // The client can use this action to send a custom command to
            // the service and request the current audio session id.
        const val EXTRA_AUDIO_SESSION_ID = "$PREFIX.extra.AUDIO_SESSION_ID" // Extra key for audio session ID.
        const val ACTION_SCHEDULE_SLEEP_TIME = "$PREFIX.action.SCHEDULE_SLEEP_TIME" // Action string for scheduling sleep time.
        const val EXTRA_SCHEDULED_TIME_MILLS = "$PREFIX.extra.AUDIO_SESSION_ID" // Extra key for scheduled time in milliseconds.
        const val UNINITIALIZED_SLEEP_TIME_MILLIS = -1L // Constant for uninitialized sleep time in milliseconds.
        //const val ACTION_EQUALIZER_CONFIG = "$PREFIX.extra.EQUALIZER" // Action string for equalizer configuration.
        //const val EXTRA_EQUALIZER_ENABLED = "$PREFIX.extra.EXTRA_EQUALIZER_ENABLED" // Extra key for equalizer enabled state.
        //const val EXTRA_EQUALIZER_PROPERTIES = "$PREFIX.extra.EXTRA_EQUALIZER_PROPERTIES" // Extra key for equalizer properties.

        // app preference keys
        //private val APREF_KEY_SHUFFLE_TYPE = "_shuffle"
        //private val APREF_KEY_REPEAT_TYPE = "_repeat"
        //private val PREF_KEY_SHUFFLE_MODE = "_shuffle"
        //private val PREF_KEY_REPEAT_MODE = "_repeat_mode"
        //private val PREF_KEY_INDEX = "_index" // saved current item's index ?? for onCreate
        //private val PREF_KEY_BOOKMARK = "_bookmark" // this is the saved, current position?? for onCreate
        //private val PREF_KEY_RECENT_PLAYLIST_LIMIT = "_max_recent_size" // saved limit for recent playlist size
        //private val PREF_KEY_EQUALIZER_ENABLED = "_equalizer_enabled" //
        //private val PREF_KEY_EQUALIZER_PROPERTIES = "_equalizer_properties" //
        //private val PREF_KEY_CLOSE_WHEN_REMOVED = "_stop_playback_when_removed" //
        //private val PREF_KEY_ORDERS = "_orders" //

        //private val LIST_ITEM_DELIMITER = ';'

        val UPDATE_EVENTS = intArrayOf(
            Player.EVENT_TIMELINE_CHANGED,
            Player.EVENT_PLAYBACK_STATE_CHANGED,
            Player.EVENT_REPEAT_MODE_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_IS_LOADING_CHANGED,
            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED,
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
            Player.EVENT_MEDIA_ITEM_TRANSITION
        )
    }
}