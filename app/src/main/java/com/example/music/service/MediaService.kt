package com.example.music.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.Callback
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.RepeatType
import com.example.music.domain.player.model.asLocalMediaItem
import com.example.music.ui.player.NowPlaying
import com.example.music.ui.shared.mediaItems
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
     * A coroutine job that monitors the playback state and performs some actions based on it.
     * It saves the current playback position every 5 seconds and pauses the player if the sleep time is reached.
     * The job is cancelled when the service is destroyed or the playback is stopped.
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
        Log.i(TAG, "onCreate: START")
        super.onCreate()

        Log.i(TAG, "Checking if MediaPlayer exists")
        if (!this::mediaPlayer.isInitialized) {
            // this is to attempt to have mediaPlayer be initialized before it is required.
            // Because I've run into that error numerous times while trying to play an audio file in app
            Log.e(TAG, "MEDIA PLAYER NOT INITIALIZED")
            mediaPlayer.deviceInfo
        } else {
            Log.i(TAG, "MediaPlayer exists: ${mediaPlayer.availableCommands}")
        }

        // Moved mediaSession builder here so that it will be created as part of the creation of the service
        Log.i(TAG, "Building Media Session")
        mediaSession = MediaSession.Builder(this, mediaPlayer)
            .setId("Musicality")
            .setSessionActivity(activity)
            .setCallback(this)
            .build()
        Log.i(TAG, "Media Session created")

        scope.launch{
            Log.i(TAG, "onCreate: scope launch start")

            Log.i(TAG, "Setting media player repeat mode from AppPreferences DataStore")
            mediaPlayer.repeatMode = appPreferences.getRepeatTypeAsInt()

            if (mediaPlayer.mediaItems.isNotEmpty()) {
                Log.i(TAG, "mediaPlayer's mediaItems is not empty -> setting media items to media player")
                mediaPlayer.setMediaItems(mediaPlayer.mediaItems)
            }

            // Connects the MediaService's Player.Listener to mediaPlayer
            Log.i(TAG, "Adding @MediaService listener to a media player.")
            mediaPlayer.addListener(this@MediaService)
            Log.i(TAG, "Added @MediaService listener to a media player.")

            sendBroadcast(NowPlaying.from(this@MediaService, mediaPlayer))

            Log.i(TAG, "onCreate: scope launch end")
        }

        Log.i(TAG, "onCreate: END")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: Start\n" +
                "Intent action: ${intent?.action}\n" +
                "Intent data: ${intent?.data}\n" +
                "Flags: $flags\n" +
                "Start ID: $startId")
        val action = intent?.action

        if (action == null) {
            Log.i(TAG, "onStartCommand: action is null -> sending broadcast NowPlaying")
            sendBroadcast(NowPlaying.from(this, mediaPlayer))
            return super.onStartCommand(intent, flags, startId)
        }
        Log.i(TAG, "onStartCommand: action NOT NULL")

        if (sessions.find { it.id == mediaSession?.id } == null )
            mediaSession?.let { addSession(it) }

        if (mediaPlayer.playbackState != Player.STATE_READY)
            mediaPlayer.prepare()

        Log.i(TAG, "onStartCommand: Media Player Prepared")
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
        // updating local datastore with metadata about recently played items would begin here
        when (reason) {
            Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                Log.i(TAG, "onMediaItemTransition:\n" +
                    "onMediaItemTransition reason -> automatic")
            }
            Player. MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                Log.i(TAG, "onMediaItemTransition:\n" +
                    "onMediaItemTransition reason -> playlist changed")
            }
            Player. MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                Log.i(TAG, "onMediaItemTransition:\n" +
                    "onMediaItemTransition reason -> repeat play")
            }
            Player. MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                Log.i(TAG, "onMediaItemTransition:\n" +
                    "onMediaItemTransition reason -> seek to different item")
            }
        }
        super.onMediaItemTransition(mediaItem, reason)
    }

    override fun onRepeatModeChanged(
        repeatMode: Int
    ) {
        scope.launch {
            appPreferences.updateRepeatType(RepeatType.entries[repeatMode])
            // set the dataStore repeat type to new repeatMode
        }
        super.onRepeatModeChanged(repeatMode)
    }

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
        Log.i(TAG, "onEvents: ${events.size()}")
        logPlayerEvent(events)
        if (!events.containsAny(*UPDATE_EVENTS))
            return
        sendBroadcast(NowPlaying.from(this, player))
        super.onEvents(player, events)
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(TAG, "onPlayerError: ${error.errorCode}", error)
        //Toast.makeText(this, "Unplayable file", Toast.LENGTH_SHORT).show()
        // send toast of player error
        mediaPlayer.seekToNextMediaItem()
    }

    // the entire onPlayWhenReadyChanged section was overridden for
    // pausing playback if there is a sleep timer set/created
    override fun onPlayWhenReadyChanged(
        isPlaying: Boolean,
        reason: Int
    ) {
        Log.i(TAG, "onPlayWhenReadyChanged\n" +
            "isPlaying: $isPlaying\n")
        when (reason) {
            Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback set by user to $isPlaying")
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback paused from loss of audio focus")
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback paused to avoid becoming noisy")
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback started / paused from remote change")
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback paused at end of media item")
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_SUPPRESSED_TOO_LONG -> {
                Log.i(TAG, "onPlayWhenReadyChanged reason -> playback paused from suppression for too long")
            }
        }

        super.onPlayWhenReadyChanged(isPlaying, reason)

        // this section is the sleep timer job
        if (!isPlaying) {
            sessionMonitorJob?.cancel() // cancels monitor job
            scheduledPauseTimeMillis = UNINITIALIZED_SLEEP_TIME_MILLIS // uninitializes timer for pausing playback
        } else
            sessionMonitorJob = scope.launch {
                var isPlay = mediaPlayer.playWhenReady
                do {
                    // get current playback position, set to preferences / datastore
                    //preferences[PREF_KEY_BOOKMARK] = mediaPlayer.currentPosition
                    Log.i(TAG, "Save playback position: ${mediaPlayer.currentPosition}")
                    // check if playback is scheduled to be paused
                    if (scheduledPauseTimeMillis != UNINITIALIZED_SLEEP_TIME_MILLIS && scheduledPauseTimeMillis <= System.currentTimeMillis()) {
                        // Pause the player as the scheduled pause time has been reached.
                        mediaPlayer.pause()

                        // Once the scheduled pause has been triggered, reset the scheduled time to uninitialized.
                        scheduledPauseTimeMillis = UNINITIALIZED_SLEEP_TIME_MILLIS
                    }
                    // Delay for the specified time
                    delay(SAVE_POSITION_DELAY_MILLS)

                    /**Returns whether the player is playing, i. e. getCurrentPosition() is advancing.
                    If false, then at least one of the following is true:
                    The playback state is not ready.
                    There is no intention to play.
                    Playback is suppressed for other reasons.
                    Returns:
                    Whether the player is playing.
                    See Also:
                    Player. Listener. onIsPlayingChanged(boolean)*/
                    isPlay = mediaPlayer.isPlaying
                } while (isPlay)
            }
    }

    // --- Need this because of error during initial play of a media item: UnsupportedOperationException: Make sure to implement MediaSession.Callback.onPlaybackResumption() if you add a media button receiver to your manifest or if you implement the recent media item contract with your MediaLibraryService.
    override fun onPlaybackResumption(
        session: MediaSession,
        controller: ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Log.i(TAG, "onPlaybackResumption:\n" +
                "Media Session: ${session.id}\n" +
                "Controller: ${controller.packageName}")
        return Futures.immediateFuture(
            MediaSession.MediaItemsWithStartPosition(
                emptyList(),
                C.INDEX_UNSET,
                C.TIME_UNSET
            )
        )
        //return super.onPlaybackResumption(mediaSession,controller)
    }

    /* // Intent for overriding this function is to add custom session commands to the MediaSession
    override fun onConnect(
        session: MediaSession,
        controller: ControllerInfo
    ): ConnectionResult {
        Log.i(TAG, "onConnect:\n" +
                "Media Session: ${session.id}\n" +
                "Controller: ${controller.packageName}")
        // obtain available commands by creating the set
        val available = ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()

        // adds the commands that can be set
        available.add(SessionCommand(ACTION_AUDIO_SESSION_ID, Bundle.EMPTY))
        available.add(SessionCommand(ACTION_SCHEDULE_SLEEP_TIME, Bundle.EMPTY))

        return ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(available.build())
            .build()
    }*/

    /* // Intent for overriding this function is to define custom session commands for the MediaSession to reference
    override fun onCustomCommand(
        session: MediaSession,
        controller: ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        Log.i(TAG, "onCustomCommand:\n" +
                "Media Session: ${session.id}\n" +
                "Controller: ${controller.packageName}\n" +
                "Custom Command: ${customCommand.customAction}")
        val action = customCommand.customAction
        return when (action) {
            ACTION_AUDIO_SESSION_ID -> {
                Log.i(TAG, "onCustomCommand -> ACTION_AUDIO_SESSION_ID")
                val audioSessionId = (mediaPlayer as ExoPlayer).audioSessionId
                val result = SessionResult(SessionResult.RESULT_SUCCESS) {
                    putInt(EXTRA_AUDIO_SESSION_ID, audioSessionId)
                }
                Futures.immediateFuture(result)
            }

            ACTION_SCHEDULE_SLEEP_TIME -> {
                Log.i(TAG, "onCustomCommand -> ACTION_SCHEDULE_SLEEP_TIME")
                val newTimeMillis = customCommand.customExtras.getLong(EXTRA_SCHEDULED_TIME_MILLS)
                if (newTimeMillis != 0L)
                    scheduledPauseTimeMillis = newTimeMillis

                Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS) {
                        putLong(EXTRA_SCHEDULED_TIME_MILLS, scheduledPauseTimeMillis)
                    }
                )
            }

            else -> {
                Log.e(TAG, "onCustomCommand -> Unknown Error")
                Futures.immediateFuture(SessionResult(SessionError.ERROR_UNKNOWN))
            }
        }
        //return super.onCustomCommand(session, controller, customCommand, args)
    }*/

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        mediaPlayer.playWhenReady = false
        /*if (runBlocking { preferences[PREF_KEY_CLOSE_WHEN_REMOVED, false] }) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }*/
    }

    override fun onSetMediaItems(
        session: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Log.i(TAG, "onSetMediaItems:\n" +
            "Media Session: ${session.id}\n" +
            "Controller: ${controller.packageName}\n" +
            "Media Items count: ${mediaItems.size}\n" +
            "Starting Index: $startIndex\n" +
            "Starting Position (in Ms): $startPositionMs")
        return super.onSetMediaItems(
            session,
            controller,
            mediaItems,
            startIndex,
            startPositionMs
        )
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

    override fun onDestroy() {
        mediaPlayer.release()
        mediaSession?.release()
        scope.cancel()
        super.onDestroy()
    }

    private fun logPlayerEvent(events: Player.Events) {
        if (events.contains(Player.EVENT_TIMELINE_CHANGED) ) {
            Log.i(TAG, "onEvents -> 0 :: event timeline changed")
        }
        if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ) {
            Log.i(TAG, "onEvents -> 1 :: event current media item changed or current item repeating")
        }
        if (events.contains(Player.EVENT_TRACKS_CHANGED) ) {
            Log.i(TAG, "onEvents -> 2 :: event tracks changed")
        }
        if (events.contains(Player.EVENT_IS_LOADING_CHANGED) ) {
            Log.i(TAG, "onEvents -> 3 :: event is loading changed")
        }
        if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ) {
            Log.i(TAG, "onEvents -> 4 :: event playback state changed")
        }
        if (events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ) {
            Log.i(TAG, "onEvents -> 5 :: event play when ready changed")
        }
        if (events.contains(Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED) ) {
            Log.i(TAG, "onEvents -> 6 :: event playback suppression reason changed")
        }
        if (events.contains(Player.EVENT_IS_PLAYING_CHANGED) ) {
            Log.i(TAG, "onEvents -> 7 :: event is playing changed")
        }
        if (events.contains(Player.EVENT_REPEAT_MODE_CHANGED) ) {
            Log.i(TAG, "onEvents -> 8 :: event repeat mode changed")
        }
        if (events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED) ) {
            Log.i(TAG, "onEvents -> 9 :: event shuffle mode enabled changed")
        }
        if (events.contains(Player.EVENT_PLAYER_ERROR) ) {
            Log.i(TAG, "onEvents -> 10 :: event player error occurred")
        }
        if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) ) {
            Log.i(TAG, "onEvents -> 11 :: event position discontinuity occurred")
            /** Note:
             * A position discontinuity occurs when the playing period changes, the playback
             * position jumps within the period currently being played, or when the playing
             * period has been skipped or removed.
             * onEvents(Player, Player. Events) will also be called to report this event along
             * with other events that happen in the same Looper message queue iteration.
             */
        }
        if (events.contains(Player.EVENT_PLAYBACK_PARAMETERS_CHANGED) ) {
            Log.i(TAG, "onEvents -> 12 :: event playback parameters changed")
        }
        if (events.contains(Player.EVENT_AVAILABLE_COMMANDS_CHANGED) ) {
            Log.i(TAG, "onEvents -> 13 :: event player's command(s) availability changed")
        }
        if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) ) {
            Log.i(TAG, "onEvents -> 14 :: event media metadata changed")
        }
        if (events.contains(Player.EVENT_PLAYLIST_METADATA_CHANGED) ) {
            Log.i(TAG, "onEvents -> 15 :: event playlist metadata changed")
        }
        if (events.contains(Player.EVENT_SEEK_BACK_INCREMENT_CHANGED) ) {
            Log.i(TAG, "onEvents -> 16 :: event seek back increment changed")
        }
        if (events.contains(Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED) ) {
            Log.i(TAG, "onEvents -> 17 :: event seek forward increment changed")
        }
        if (events.contains(Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED) ) {
            Log.i(TAG, "onEvents -> 18 :: event max seek to previous position changed")
        }
        if (events.contains(Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED) ) {
            Log.i(TAG, "onEvents -> 19 :: event track selection parameters changed")
        }
        if (events.contains(Player.EVENT_AUDIO_ATTRIBUTES_CHANGED) ) {
            Log.i(TAG, "onEvents -> 20 :: event audio attributes changed")
        }
        if (events.contains(Player.EVENT_AUDIO_SESSION_ID) ) {
            Log.i(TAG, "onEvents -> 21 :: event audio session id set")
        }
        if (events.contains(Player.EVENT_VOLUME_CHANGED) ) {
            Log.i(TAG, "onEvents -> 22 :: event volume changed")
        }
        if (events.contains(Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED) ) {
            Log.i(TAG, "onEvents -> 23 :: event skip silence enabled changed")
        }
        if (events.contains(Player.EVENT_SURFACE_SIZE_CHANGED) ) {
            Log.i(TAG, "onEvents -> 24 :: event surface size changed")
            /**
             * Note:
             * This is for video rendering, which is not a supported MIME type in the app.
             * So this shouldn't occur.
             */
        }
        if (events.contains(Player.EVENT_VIDEO_SIZE_CHANGED) ) {
            Log.i(TAG, "onEvents -> 25 :: event video size changed")
            /**
             * Note:
             * This is for video rendering, which is not a supported MIME type in the app.
             * So this shouldn't occur.
             */
        }
        if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME) ) {
            Log.i(TAG, "onEvents -> 26 :: event first frame rendered")
            /**
             * Note:
             * This is for video rendering, which is not a supported MIME type in the app.
             * So this shouldn't occur.
             */
        }
        if (events.contains(Player.EVENT_CUES) ) {
            Log.i(TAG, "onEvents -> 27 :: event cues changed")
        }
        if (events.contains(Player.EVENT_METADATA) ) {
            Log.i(TAG, "onEvents -> 28 :: event metadata playback changed")
        }
        if (events.contains(Player.EVENT_DEVICE_INFO_CHANGED) ) {
            Log.i(TAG, "onEvents -> 29 :: event device info changed")
        }
        if (events.contains(Player.EVENT_DEVICE_VOLUME_CHANGED) ) {
            Log.i(TAG, "onEvents -> 30 :: event device volume changed")
        }
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

        // app preference keys
        //private val APREF_KEY_SHUFFLE_TYPE = "_shuffle"
        //private val APREF_KEY_REPEAT_TYPE = "_repeat"
        //private val PREF_KEY_SHUFFLE_MODE = "_shuffle"
        //private val PREF_KEY_REPEAT_MODE = "_repeat_mode"
        //private val PREF_KEY_INDEX = "_index" // saved current item's index ?? for onCreate
        //private val PREF_KEY_BOOKMARK = "_bookmark" // this is the saved, current position?? for onCreate
        //private val PREF_KEY_RECENT_PLAYLIST_LIMIT = "_max_recent_size" // saved limit for recent playlist size
        //private val PREF_KEY_CLOSE_WHEN_REMOVED = "_stop_playback_when_removed" //
        //private val PREF_KEY_ORDERS = "_orders" //

        //private val LIST_ITEM_DELIMITER = ';'

        val UPDATE_EVENTS = intArrayOf(
            Player.EVENT_TIMELINE_CHANGED,
            Player.EVENT_MEDIA_ITEM_TRANSITION,
            Player.EVENT_TRACKS_CHANGED,
            Player.EVENT_IS_LOADING_CHANGED,
            Player.EVENT_PLAYBACK_STATE_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_REPEAT_MODE_CHANGED,
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED,
            Player.EVENT_MEDIA_METADATA_CHANGED,
            Player.EVENT_PLAYLIST_METADATA_CHANGED,
        )
    }
}