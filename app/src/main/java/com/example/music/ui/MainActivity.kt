package com.example.music.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.UnstableApi
import com.example.music.data.database.MusicDatabase
import com.example.music.service.MediaService
import com.example.music.service.SongController
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.theme.scrimDark
import com.example.music.ui.theme.scrimLight
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dagger.hilt.android.AndroidEntryPoint
import org.apache.log4j.BasicConfigurator
import javax.inject.Inject

private const val APP_PREFERENCES_NAME = "app_preferences"
private const val TAG = "Main Activity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicDatabase: MusicDatabase

    @Inject
    lateinit var songController: SongController

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate start")

        BasicConfigurator.configure() //configures logging in project

        //check for and request read media audio permission
        val locationPermissionRequest =
            registerForActivityResult<String, Boolean>(
                ActivityResultContracts.RequestPermission(),
                ActivityResultCallback<Boolean> {
                    if (it) {
                        Log.i(TAG, "Media Permission Granted")
                    // get audio
                        //getMusic()
                    }
                }
            )

        // secondary check if the device version is Android 10 or above for getting the correct media permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Sending 'Read Media Audio' Permission Request")
                locationPermissionRequest.launch(Manifest.permission.READ_MEDIA_AUDIO)
                return
            }
            Log.i(TAG, "Android 10+ 'Read Media Audio' Permission Granted")
        }

        Log.i(TAG, "Starting Media Service")
        startService(Intent(this@MainActivity, MediaService::class.java))

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.Transparent.toArgb(),Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle//.auto(scrimLight.toArgb(), scrimDark.toArgb()) //default, applies translucent scrim when in dark mode
                .light(Color.Transparent.toArgb(), Color.Transparent.toArgb()) //sets navigation bar to be transparent, overrides defaulted scrim
        )
        Log.i(TAG, "edge to edge enabled")

        setContent {
            Log.i(TAG, "onCreate - setContent start")
            val displayFeatures = calculateDisplayFeatures(this)

            Log.i(TAG, "onCreate - setContent > MusicTheme setting MusicApp displayFeatures")
            MusicTheme {
                MusicApp(
                    displayFeatures
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onStop() {
        super.onStop()
        Log.i(TAG, "STOPPED Activity")
        stopService(Intent(this, MediaService::class.java))
    }
}
