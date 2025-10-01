package com.example.music.ui.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.music.data.repository.RepeatType
import com.example.music.domain.model.SongInfo

interface PlayerState {
    val currentMedia: MediaItem?
    var isPlaying: Boolean
    val player: Player?
    var progress: Float
    var position: Long
    var isShuffled: Boolean
    var repeatState: RepeatType
}

interface MiniPlayerState {
    var currentSong: SongInfo
    var isPlaying: Boolean
    val player: Player?
}
