package com.example.music.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.media.MediaPlayer
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.music.ui.theme.MusicTheme
import com.example.music.R
import com.example.music.data.database.MusicDatabase
//import com.example.music.service.MusicService
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //mediasessionservice best to instantiate here

    @Inject
    lateinit var musicDatabase: MusicDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        //TODO does it make sense to initialize mediaPlayer / songPlayer class here?
        // it would need to be created, but it would just start in idle state, maybe prepared state. no song given unless a play function is invoked
        //val mediaPlayer: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.Scar)

        setContent {
            val displayFeatures = calculateDisplayFeatures(this)

            MusicTheme {
                MusicApp(
                    displayFeatures
                )
            }
        }
    }
}
