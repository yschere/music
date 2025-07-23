package com.example.music.ui.shared
//
//import android.app.PendingIntent
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import androidx.annotation.OptIn
//import androidx.media3.common.Player
//import android.util.Size
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.session.MediaController
//import androidx.media3.session.SessionToken
//import androidx.media3.ui.PlayerNotificationManager
//import androidx.media3.ui.PlayerNotificationManager.NotificationListener
//import com.example.music.R
//import com.example.music.ui.player.PlayerUiState
//import com.example.music.ui.search.SearchFieldState
//import com.google.common.util.concurrent.ListenableFuture
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import javax.inject.Inject
//
//@UnstableApi
//class MediaNotificationManager @OptIn(UnstableApi::class) constructor(
//    private val context: Context,
//    sessionToken: SessionToken,
//    notificationListener: NotificationListener,
//) {
//    @Inject
//    lateinit var mediaPlayer: Player
//
//    private val serviceJob = SupervisorJob()
//    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
//    private val notificationManager: PlayerNotificationManager
//
//    init {
//        val mediaController = MediaController.Builder(context, sessionToken).buildAsync()
//
//        notificationManager = PlayerNotificationManager.Builder(
//            context, NOW_PLAYING_NOTIFICATION_ID, NOW_PLAYING_CHANNEL_ID
//        )
//            .setChannelNameResourceId(R.string.media_notification_channel)
//            .setChannelDescriptionResourceId(R.string.media_notification_channel_description)
//            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
//            .setNotificationListener(notificationListener)
//            .setSmallIconResourceId(com.example.music.glancewidget.R.drawable.outline_play_arrow_24)
//            .build()
//            .apply {
//                setPlayer(mediaPlayer)
//                setUseRewindAction(true)
//                setUseFastForwardAction(true)
//                setUseRewindActionInCompactView(true)
//                setUseFastForwardActionInCompactView(true)
//                setUseRewindActionInCompactView(true)
//                setUseFastForwardActionInCompactView(true)
//            }
//    }
//
//    fun hideNotification() {
//        notificationManager.setPlayer(null)
//    }
//
//    fun showNotificationForPlayer(player: Player) {
//        notificationManager.setPlayer(player)
//    }
//
//    private inner class DescriptionAdapter(
//        private val controller: ListenableFuture<MediaController>
//    ): PlayerNotificationManager.MediaDescriptionAdapter {
//
//        var currentIconUri: Uri? = null
//        var currentBitmap: Bitmap? = null
//
//        override fun createCurrentContentIntent(player: Player): PendingIntent? =
//            controller.get().sessionActivity
//
//        override fun getCurrentContentText(player: Player) =
//            ""
//
//        override fun getCurrentContentTitle(player: Player) =
//            controller.get().mediaMetadata.title.toString()
//
//        override fun getCurrentLargeIcon(
//            player: Player,
//            callback: PlayerNotificationManager.BitmapCallback
//        ): Bitmap? {
//            val iconUri = controller.get().mediaMetadata.artworkUri
//            return if (currentIconUri != iconUri || currentBitmap == null) {
//                currentIconUri = iconUri
//                serviceScope.launch {
//                    currentBitmap = iconUri?.let {
//                        context.contentResolver.loadThumbnail(
//                            it, Size(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE), null)
//                    }
//                    currentBitmap?.let { callback.onBitmap(it) }
//                }
//                null
//            } else {
//                currentBitmap
//            }
//        }
//    }
//}
//
//const val NOTIFICATION_LARGE_ICON_SIZE = 144
//
//const val NOW_PLAYING_CHANNEL_ID = "media.NOW_PLAYING"
//
//const val NOW_PLAYING_NOTIFICATION_ID = 0xb339
//
//@HiltViewModel
//class MediaViewModel @Inject constructor(
//    val player: Player
//): ViewModel() {
//
//    private val _currentPlayingIndex = MutableStateFlow(0)
//    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()
//
//    private val _totalDurationInMS = MutableStateFlow(0L)
//    val totalDurationInMS = _totalDurationInMS.asStateFlow()
//
//    private val _isPlaying = MutableStateFlow(false)
//    val isPlaying = _isPlaying.asStateFlow()
//
////    val uiState: StateFlow<PlayerUiState> = MutableStateFlow(PlayerUiState)
////        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = PlayerUiState)
//
////    private val _searchFieldState: MutableStateFlow<SearchFieldState> =
////        MutableStateFlow(SearchFieldState.Idle)
////    val searchFieldState: StateFlow<SearchFieldState>
////        get() = _searchFieldState
//}