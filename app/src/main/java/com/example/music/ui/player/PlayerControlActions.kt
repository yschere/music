package com.example.music.ui.player

// wrappers for default class of possible control actions
data class PlayerControlActions(
    val onPlay: () -> Unit,
    val onPause: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onSeek: (Long) -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit
)

data class MiniPlayerControlActions(
    val onPlay: () -> Unit,
    val onPause: () -> Unit,
)
