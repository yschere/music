package com.example.music.service

import android.Manifest
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.Callback
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
//import com.example.music.domain.playback.NowPlaying
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


/*
 want this to be the service that controls media session that interacts with media player
 extend media3's session service

 need to define the list of interactions, functions, and/or components this will work with
 */

//equivalent to Audiofy's Playback
//class MediaService : Service() {
@UnstableApi
class MediaService : MediaSessionService(), Callback, Player.Listener {

    //may need a companion object for defining vars and playback change events

    // would need a way to reference data store variables, ie preferences

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val activity by lazy {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private val mediaPlayer: Player by lazy {
        val mediaSrcFactory =
            DefaultMediaSourceFactory(applicationContext)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        /*val loadControl = LoadControl()
            .setAllocator(Allocator(true, 16))
            .setBufferDurationsMs(2000, 5000, 1500, 2000)
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()*/

        ExoPlayer.Builder(this, mediaSrcFactory)
            //.setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
    }

    //private var mediaSession: MediaSession? = null
    private val mediaSession: MediaSession by lazy {
        MediaSession.Builder(this, mediaPlayer)
            .setSessionActivity(activity)
            .build()
    }

    // this is how a media controller connects to a sessionService?
    //Called when a MediaController is created with this service's SessionToken. Return a MediaSession that the controller will connect to, or null to reject the connection request.
    // ... what is the session token though ... and how does it get shared to the controller?
    override fun onGetSession(controllerInfo: ControllerInfo): MediaSession =
        mediaSession

    /* // practicing with mediaSession creation
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // creates generic instance of media source factory
        // this would need to be changed if I want to define actual media sources from given media items
        // need media items to create a media source
        val mediaSrcFactory: MediaSource.Factory =
            DefaultMediaSourceFactory(this)

        // this.applicationInfo.dataDir === /data/user/0/com.example.music/
        // this.applicationInfo.sourceDir === /data/app/~~sRkdfNjc324-5PPktPGIMQ==/com.example.music-UY4ghV5Vw91FcuuM1yo6bA==/base.apk
        // this.applicationInfo.publicSourceDir === /data/app/~~sRkdfNjc324-5PPktPGIMQ==/com.example.music-UY4ghV5Vw91FcuuM1yo6bA==/base.apk
        // Environment.getExternalStorageDirectory() === /storage/emulated/0
        // Environment.getRootDirectory() === /system
        // Environment.getDataDirectory() === /data
        // Environment.getStorageDirectory() === /storage
        //logger.info { "\n\n\n ********** ${Environment.getExternalStorageDirectory()} ********** \n\n\n"}

        // need to follow along with scoped storage, this way will not work passed Android 10

        //val path = Environment.getExternalStorageDirectory().toString() + "/Music/1208"
        //logger.info{"MEDIA SERVICE -- Path: $path"}
        //val directory = File(path)
        //val files = directory.listFiles()
        //logger.info{"MEDIA SERVICE -- Size: ${files.size}" }
        //for (i in files.indices) {
            //logger.info{"MEDIA SERVICE -- FileName: ${files[i].name}" }
        //}

        //logger.info { this.filesDir.toString() }
        //var iS: InputStream = this.resources.openRawResource(R.raw.scar)
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName)
            .appendPath("${R.raw.scar}")
            .build()


        // does not work with scoped storage permission
        val uri2 = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(packageName)
            .appendPath(Environment.getExternalStorageDirectory().toString()+"/Music/Woodkid/Iron.mp3")
            .build()


        val audiosUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.AudioColumns._ID)
        var outputArray: String = Environment.getExternalStorageDirectory().toString()+"/Music/Woodkid/Iron.mp3"//null
        //val cursor: Cursor? = contentResolver.query(audiosUri, projection, MediaStore.Audio.Media.DATA + "LIKE ?",
            //arrayOf(outputArray), null)
        //cursor?.moveToFirst()


        val mediaItem = MediaItem.fromUri(uri)
        //val mediaItem = MediaItem.fromUri("file://"+Environment.getExternalStorageDirectory().toString()+"/Music/Eve/Bunka/Kaishingeki.mp3")
        //this.applicationInfo.sourceDir//.dataDir+
                //Environment.getRootDirectory()
        val mediaPlayer: ExoPlayer = ExoPlayer.Builder(this,mediaSrcFactory).build()//.setMediaItem(mediaItem)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes, true)
        mediaSession = MediaSession.Builder(this, mediaPlayer).build()

        // Media Source is an interface that provides information and functions for media interaction
        // Clears the playlist, adds the specified MediaSource and resets the position to the default position
        //mediaPlayer.setMediaSource(mediaSrcFactory.createMediaSource(mediaItem)) // would this need to be changed if I want to define actual media sources from given media items

        // Media Item is a representation of a piece of media
        // Clears the playlist, adds the specified MediaItem and resets the position to the default position.
        // To replace a media item (possibly seamlessly) without clearing the playlist, use replaceMediaItem.
        // This method must only be called if COMMAND_SET_MEDIA_ITEM is available.
        //mediaPlayer.setMediaItem(mediaItem)

        //mediaPlayer.prepare()
        //mediaPlayer.play()




        // cleaned up version before adjusting mediaService
        //val mediaSrcFactory = DefaultMediaSourceFactory(applicationContext)

        //val mediaPlayer: ExoPlayer = ExoPlayer.Builder(this, mediaSrcFactory).build()

        //mediaSession = MediaSession.Builder(this, mediaPlayer).build()

        //val audioAttributes = AudioAttributes.Builder()
            //.setUsage(C.USAGE_MEDIA)
            //.setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            //.build()
        //mediaPlayer.setAudioAttributes(audioAttributes, true)
    }*/

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        // would instantiate any lateinit vars here
        // or begin connection to preferences data store

        scope.launch{
            runCatching {
                //begin building mediaPlayer's mediaItems into queue here?
                //collect the preferences that set the queue play order

                // need a way that either begins by pulling whatever was in queue before
                // or begins by building an empty queue from scratch?
                // or waits for a user input that generates the need for mediaItems to be attached
                /*val items: List<MediaItem> = withContext(context = Dispatchers.IO) {  }*/

                //mediaPlayer.setMediaItems(items)
            }
            mediaPlayer.addListener(this@MediaService) //connects the MediaService's Player.Listener to mediaPlayer
            //sendBroadcast(NowPlaying.from(this@MediaService, mediaPlayer))
        }
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

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }

    override fun onConnect(
        session: MediaSession,
        controller: ControllerInfo
    ): MediaSession.ConnectionResult {
        return super.onConnect(session, controller)
    }

    override fun onMediaButtonEvent(
        session: MediaSession,
        controllerInfo: ControllerInfo,
        intent: Intent
    ): Boolean {
        return super.onMediaButtonEvent(session, controllerInfo, intent)
    }

    override fun getContentResolver(): ContentResolver {
        return super.getContentResolver()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        val action = SessionCommand.COMMAND_CODE_CUSTOM
        //would need to define these custom commands that action can be
        // then use when switch cases to define the result and steps to take per action
        return super.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onDestroy() {
        mediaPlayer.release()
        mediaSession.release()
        scope.cancel()
        super.onDestroy()
    }
}