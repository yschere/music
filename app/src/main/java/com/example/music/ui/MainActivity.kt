package com.example.music.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.media.MediaPlayer
import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.music.ui.theme.MusicTheme
import com.example.music.R
import com.example.music.data.database.MusicDatabase
import com.example.music.service.MediaService
import com.example.music.ui.theme.scrimDark
import com.example.music.ui.theme.scrimLight
import com.example.music.util.logger
//import com.example.music.service.MusicService
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import org.apache.log4j.BasicConfigurator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val APP_PREFERENCES_NAME = "app_preferences"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /* //mediasessionservice best to instantiate here
    //@Inject
    //lateinit var mediaSessionService: MediaSessionService
    //@Inject
    //var mediaSession: MediaSession? = null
    //@Inject
    //lateinit var mediaPlayer: ExoPlayer
    //@Inject
    //lateinit var playerView: PlayerView*/

    @Inject
    lateinit var musicDatabase: MusicDatabase

    lateinit var controllerFuture: ListenableFuture<MediaController>

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BasicConfigurator.configure() //configures logging in project

        //check for and request read media audio permission
        val locationPermissionRequest =
            registerForActivityResult<String, Boolean>(
                ActivityResultContracts.RequestPermission(),
                ActivityResultCallback<Boolean> {
                    if (it) {
                        logger.info { "media permission granted" }
                    // get audio
                        //getMusic()
                    }
                }
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionRequest.launch(Manifest.permission.READ_MEDIA_AUDIO)
                return
            }
        }

        // need the code to start the file metadata read to be here?
        // something to be done before the ui begins to attempt to render anything

        val sessionToken = SessionToken(this, ComponentName(this, MediaService::class.java))
        controllerFuture =
            MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            logger.info { "SessionToken MediaController - ${controllerFuture.get()}" }
            // MediaController is available here with controllerFuture.get()
            // from What's next for AndroidX Media and ExoPlayer video:
            //playerView.player = controllerFuture.get()
        }, MoreExecutors.directExecutor())
        logger.info { "Main Activity - onCreate after sessionToken" }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.Transparent.toArgb(),Color.Transparent.toArgb()),//.auto(scrimLight, scrimDark),
            navigationBarStyle = SystemBarStyle.auto(scrimLight.toArgb(), scrimDark.toArgb())
        )
        logger.info { "Main Activity - edge to edge enabled" }


        //TODO does it make sense to initialize mediaPlayer / songPlayer class here?
        // it would need to be created, but it would just start in idle state, maybe prepared state.
        // no song given unless a play function is invoked
        //val mediaPlayer: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.Scar)

        setContent {
            logger.info { "Main Activity - set content start" }
            //now when the files are still being read in or the thread is not blocking the main activity,
            // can get the MusicApp going for real by calling MusicApp to get the main context and navigator going
            val displayFeatures = calculateDisplayFeatures(this)


            /*mediaPlayer = ExoPlayer.Builder(this).build()
            mediaSession = MediaSession.Builder(this, mediaPlayer).build()
            playerView = PlayerView(this)
            playerView.setPlayer(mediaPlayer)
            val mediaItem = MediaItem.fromUri("res/raw/scar.mp3")
            mediaPlayer.setMediaItem(mediaItem)
            mediaPlayer.prepare()
            mediaPlayer.playWhenReady*/

            //media controller build here?
            //need session token of type session service?


            MusicTheme {
                MusicApp(
                    displayFeatures
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFuture)
        android.util.Log.i("onStop", "STOPPED")
    }
}
