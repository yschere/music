package com.example.music.ui.player

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.Thumb
import androidx.compose.material3.SliderDefaults.Track
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.data.repository.RepeatType
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.AlbumImageBm
import com.example.music.designsys.component.ImageBackgroundColorFilter_Bm
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.PRIMARY_BUTTON_SIZE
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.designsys.theme.SIDE_BUTTON_SIZE
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayerMoreOptionsBottomModal
import com.example.music.ui.shared.formatString
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.ui.shared.BackNavBtn
import com.example.music.ui.shared.MoreOptionsBtn
import com.example.music.ui.shared.QueueBtn
import com.example.music.util.listItemIconMod
import com.example.music.util.playerButtonMod
import com.example.music.util.screenMargin
import com.example.music.util.isCompact
import com.example.music.util.isExpanded
import com.example.music.util.isMedium
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.math.roundToLong

private const val TAG = "Player Screen"

/**
 * Stateful version of Player Screen
 */
@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    PlayerScreen(
        currentSong = viewModel.currentSong,
        isPlaying = viewModel.isPlaying,
        isShuffled = viewModel.isShuffled,
        repeatState = viewModel.repeatState,
        progress = viewModel.progress,
        timeElapsed = viewModel.position,
        hasNext = viewModel.hasNext,
        clearQueue = viewModel::onClearQueue,
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        navigateBack = navigateBack,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToArtistDetails = navigateToArtistDetails,
        playerControlActions = PlayerControlActions(
            onPlay = viewModel::onPlay,
            onPause = viewModel::onPause,
            onNext = viewModel::onNext,
            onPrevious = viewModel::onPrevious,
            onSeek = viewModel::onSeek,
            onShuffle = viewModel::onShuffle,
            onRepeat = viewModel::onRepeat
        ),
    )
    /* // not in use after changing PlayerScreen state to be reliant on SongController values
    // TODO: determine another invocation point for error handling
    if (state.errorMessage != null) { PlayerScreenError(onRetry = viewModel::refresh) }*/
}

/**
 * Error Screen
 */
@Composable
private fun PlayerScreenError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) { Error(onRetry = onRetry, modifier = modifier) }

/**
 * Stateless version of Player Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreen(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    clearQueue: () -> Unit,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "Player Screen START\n" +
        "currentSong: $currentSong")

    val coroutineScope = rememberCoroutineScope()
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)
    val snackbarHostState = remember { SnackbarHostState() }

    val sheetState = rememberModalBottomSheetState(true)
    var showMoreOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PlayerTopAppBar(
                navigateBack = navigateBack,
                navigateToQueue = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                },
                onMoreOptionsClick = { showMoreOptions = true },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier, // Note: no window insets padding to modifier so player screen's image background draws behind system bars
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background)
    ) { contentPadding ->
        if (currentSong.id != 0L) { // keeping this explicit check for now, don't want to lose context for the FullScreenLoading function below
            PlayerContentWithBackground(
                currentSong = currentSong,
                isPlaying = isPlaying,
                isShuffled = isShuffled,
                repeatState = repeatState,
                progress = progress,
                timeElapsed = timeElapsed,
                hasNext = hasNext,
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                playerControlActions = playerControlActions,
                playerModalActions = PlayerModalActions(
                    onDismissRequest = { showMoreOptions = false },
                    goToArtist = {
                        coroutineScope.launch {
                            Log.i(TAG, "Player More Options -> Go to Artist clicked :: ${currentSong.artistId}")
                            navigateToArtistDetails(currentSong.artistId)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showMoreOptions to FALSE")
                            if(!sheetState.isVisible) showMoreOptions = false
                        }
                    },
                    goToAlbum = {
                        coroutineScope.launch {
                            Log.i(TAG, "Player More Options -> Go to Album clicked :: ${currentSong.albumId}")
                            navigateToAlbumDetails(currentSong.albumId)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showMoreOptions to FALSE")
                            if(!sheetState.isVisible) showMoreOptions = false
                        }
                    },
                    clearQueue = {
                        coroutineScope.launch {
                            Log.i(TAG, "Player More Options -> Clear Queue clicked")
                            clearQueue()
                            sheetState.hide()
                            navigateBack()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showMoreOptions to FALSE")
                            if(!sheetState.isVisible) showMoreOptions = false
                        }
                    },
                    saveQueue = {
                        coroutineScope.launch {
                            Log.i(TAG, "Player More Options -> Save Queue clicked")
                            //onPlayerAction(PlayerAction.SaveQueue())
                            //navigateToAlbumDetails(currentSong.albumId)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showMoreOptions to FALSE")
                            if(!sheetState.isVisible) showMoreOptions = false
                        }
                    },
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showMoreOptions to FALSE")
                            if(!sheetState.isVisible) showMoreOptions = false
                        }
                    },
                ),
                sheetState = sheetState,
                showMoreOptions = showMoreOptions,
                contentPadding = contentPadding,
            )
        } else {
            PlayerLoadingScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

/**
 * Loading Screen with circular progress indicator in center
 */
@Composable
private fun PlayerLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Composable for Player Screen's Top App Bar.
 * FixMe: determine expected functionality for queue icon onClick
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTopAppBar(
    //queue: List<SongInfo>,
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit,
    onMoreOptionsClick: () -> Unit = {},
){
    TopAppBar(
        title = {},
        navigationIcon = { BackNavBtn(onClick = navigateBack) },
        actions = {
            QueueBtn(onClick = navigateToQueue)
            MoreOptionsBtn(onClick = onMoreOptionsClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        scrollBehavior = pinnedScrollBehavior(),
    )
}

/**
 * Draw a background image using the current song's artwork cover. Image will be
 * scaled up by 150% and blurred with provided color(s) as a filter.
 */
@Composable
private fun PlayerBackground(
    currentSong: SongInfo,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    ImageBackgroundColorFilter_Bm(
        imageId = currentSong.artworkBitmap,
        imageDescription = currentSong.title,
        color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.65f),
        modifier = modifier,
    )

    /*ImageBackgroundRadialGradientFilter_Bm(
        imageId = currentSong.artworkBitmap,
        imageDescription = currentSong.title,
        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer),
        modifier = modifier,
    )*/
}

/**
 * Composable that begins drawing the background and foreground content of the Player Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerContentWithBackground(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    playerControlActions: PlayerControlActions,
    playerModalActions: PlayerModalActions,
    sheetState: SheetState,
    showMoreOptions: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        PlayerBackground(
            currentSong = currentSong,
        )
        PlayerContent(
            currentSong = currentSong,
            isPlaying = isPlaying,
            isShuffled = isShuffled,
            repeatState = repeatState,
            progress = progress,
            timeElapsed = timeElapsed,
            hasNext = hasNext,
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            playerControlActions = playerControlActions,
            playerModalActions = playerModalActions,
            sheetState = sheetState,
            showMoreOptions = showMoreOptions,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

/**
 * Composable for determining Player Screen layout based on window sizing and device orientation.
 * TODO: Define the layouts for the different window size classes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerContent(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    playerControlActions: PlayerControlActions,
    playerModalActions: PlayerModalActions,
    sheetState: SheetState,
    showMoreOptions: Boolean,
    modifier: Modifier = Modifier
) {
    //val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    //foldingFeature used to determine player screen layouts in a lot of if elseif comparisons

    if ( windowSizeClass.isCompact ) {
        //regular look, no change
    }

    if ( windowSizeClass.isMedium ) {
        //landscape, wider look, needs to adjust components layout to fit
    }

    if ( windowSizeClass.isExpanded ) {
        //big screen, could use all the extra space better
    }

    // else, use default. currently set as the version that would be under isCompact
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PlayerContentRegular (
            currentSong = currentSong,
            isPlaying = isPlaying,
            isShuffled = isShuffled,
            repeatState = repeatState,
            progress = progress,
            timeElapsed = timeElapsed,
            hasNext = hasNext,
            playerControlActions = playerControlActions,
            playerModalActions = playerModalActions,
            sheetState = sheetState,
            showMoreOptions = showMoreOptions,
            modifier = modifier
        )
    }
}

/**
 * Composable for default Player Screen layout. Currently only supports screens in portrait mode.
 * Landscape mode is not denied, but the layout does not dynamically adjust to a better layout
 * for it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerContentRegular(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    playerControlActions: PlayerControlActions,
    playerModalActions: PlayerModalActions,
    sheetState: SheetState,
    showMoreOptions: Boolean,
    modifier: Modifier = Modifier
) {
    val hasLyrics by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .screenMargin()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SongLyricsSwitcher(
                hasLyrics = hasLyrics,
                swapVisual = { showLyrics = !showLyrics },
                modifier = Modifier.weight(0.1f)
            )

            // song section
            if (!showLyrics) {
                Spacer(modifier = Modifier.weight(1f))
                PlayerImageBm(
                    albumImage = currentSong.artworkBitmap,
                    modifier = Modifier.weight(10f).background(Color.Transparent)
                )
                Spacer(modifier = Modifier.height(32.dp))

                SongDetails(
                    songTitle = currentSong.title,
                    artistName = currentSong.artistName,
                    albumTitle = currentSong.albumTitle
                )
            } // song section end
            else {
                Spacer(modifier = Modifier.weight(1f))
                LyricsVisual(
                    currentSong = currentSong,
                    lyrics = lyric,
                    modifier = Modifier,
                )
            } // show lyrics end

            Spacer(modifier = Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(10f)
            ) {
                PlayerSlider(
                    progress = progress,
                    timeElapsed = timeElapsed,
                    songDuration = currentSong.duration,
                    onSeek = playerControlActions.onSeek,
                )
                PlayerButtons(
                    hasNext = hasNext,
                    isPlaying = isPlaying,
                    isShuffled = isShuffled,
                    repeatState = repeatState,
                    onPlay = playerControlActions.onPlay,
                    onPause = playerControlActions.onPause,
                    onNext = playerControlActions.onNext,
                    onPrevious = playerControlActions.onPrevious,
                    onShuffle = playerControlActions.onShuffle,
                    onRepeat = playerControlActions.onRepeat,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showMoreOptions) {
        Log.i(TAG, "Player Screen Content -> Player More Options is TRUE")
        PlayerMoreOptionsBottomModal(
            onDismissRequest = playerModalActions.onDismissRequest,
            sheetState = sheetState,
            song = currentSong,
            //playNext = {},
            //addToFavorites = {},
            //addToPlaylist = {},
            playerModalActions = playerModalActions,
            onClose = playerModalActions.onClose,
        )
    }
}

@Composable
private fun SongLyricsSwitcher(
    hasLyrics: Boolean,
    swapVisual: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /* TODO:
        check if song has lyrics
        if yes, show the lyrics btn as clickable
        else show nothing
        want this to exist as two buttons, Song on left, Lyrics on right
        want the lyrics button to be disabled if no lyrics found for song (or have X on the side?)
        default is to have song side enabled, but if lyrics side enabled, keep that enabled till song side tapped
    */

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(CONTENT_PADDING)
    ) {
        Text(
            text = "Song",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(SMALL_PADDING)
                .clickable(
                    enabled = true,
                    onClickLabel = "Show Song Details",
                    role = Role.Button,
                    onClick = swapVisual
                )
        )

        Text(
            text = " | ",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Lyrics",
            style = MaterialTheme.typography.titleLarge,
            color =
                if (hasLyrics) MaterialTheme.colorScheme.onSurface
                else Color.Gray,
            modifier = Modifier.padding(SMALL_PADDING)
                .clickable(
                    enabled = hasLyrics,
                    onClickLabel = "Show Song Lyrics",
                    role = Role.Button,
                    onClick = swapVisual
                )
        )
    }
}

@Composable
private fun LyricsVisual(
    currentSong: SongInfo,
    lyrics: String = "",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseOutExpo
                )
            )
    ) {
        Text(
            text = currentSong.title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = currentSong.artistName,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = lyrics,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = SCREEN_PADDING)
                .align(Alignment.Start)
        )
    }
}

/**
 * Loads current song's artwork using its albumArt Uri
 */
@Composable
private fun PlayerImage(
    albumImage: Uri,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        albumImage = albumImage,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraLarge)
    )
}

/**
 * Loads current song's artwork using its thumbnail bitmap
 */
@Composable
private fun PlayerImageBm(
    albumImage: Bitmap?,
    modifier: Modifier = Modifier
) {
    AlbumImageBm(
        albumImage = albumImage,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .listItemIconMod(250.dp, MaterialTheme.shapes.extraLarge)
            .aspectRatio(1f)
    )
}

@Composable
private fun SongDetails(
    songTitle: String? = "",
    artistName: String? = "",
    albumTitle: String? = "",
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    if (songTitle != null) {
        Text(
            text = songTitle,
            style = titleTextStyle,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.basicMarquee()
        )
    }
    artistName?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier
        )
    }
    albumTitle?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSlider(
    progress: Float,
    timeElapsed: Long,
    songDuration: Duration?,
    onSeek: (Long) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        var newElapse by remember(progress) { mutableFloatStateOf(progress) }
        val interactionSource = remember { MutableInteractionSource() }

        Row(Modifier.fillMaxWidth()
            .padding(horizontal = CONTENT_PADDING)
        ) {
            Text(
                text = Duration.ofMillis(timeElapsed).formatString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${songDuration?.formatString()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Slider(
            value = progress,
            valueRange = 0f..1f,
            onValueChange = {
                newElapse = it
                Log.i(TAG, "in PlayerSlider -> onValueChange ->\n" +
                    "newElapsed: $newElapse")
            },
            onValueChangeFinished = {
                Log.i(TAG, "in PlayerSlider -> onValueChangeFinished ->\n" +
                    "newElapsed: $newElapse")
                //take the finished float, times the duration, then round to Long to send new progress / timeElapsed
                onSeek(newElapse.times(songDuration!!.toMillis()).roundToLong())
            },
            interactionSource = interactionSource,
            thumb = { _ ->
                Thumb(
                    interactionSource = interactionSource,
                    modifier = Modifier,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = true,
                    thumbSize = DpSize(
                        width = 24.dp,
                        height = 24.dp
                    ),
                )
            },
            track = { slider ->
                Track(
                    sliderState = slider,
                    modifier = Modifier.height(10.dp),
                    enabled = true,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.LightGray,
                    ),
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 1.dp,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun PlayerButtons(
    hasNext: Boolean,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonSize: Dp = PRIMARY_BUTTON_SIZE,
    sideButtonSize: Dp = SIDE_BUTTON_SIZE,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth(),
    ) {
        val sideButtonsModifier = modifier
            .playerButtonMod(
                size = sideButtonSize,
                color = MaterialTheme.colorScheme.primary
            )

        val primaryButtonModifier = modifier
            .playerButtonMod(
                size = primaryButtonSize,
                color = MaterialTheme.colorScheme.primary
            )

        // Shuffle btn
        if (isShuffled) {
            //determined that the current state IS shuffled (isShuffled is true)
            Image(
                imageVector = Icons.Filled.ShuffleOn,
                contentDescription = stringResource(R.string.pb_shuffle_on),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                modifier = sideButtonsModifier
                    .clickable { onShuffle() }
                    .clip(CircleShape)
            )
        }
        else {
            //determined that the current state IS NOT shuffled (isShuffled is false)
            Image(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.pb_shuffle_off),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                modifier = sideButtonsModifier
                        .clip(CircleShape)
                    .clickable { onShuffle() }
            )
        }

        // Skip back to previous btn
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.pb_skip_previous),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
            modifier = sideButtonsModifier
                .clip(CircleShape)
                .clickable { onPrevious() }
        )

        // Play and Pause btn
        if (isPlaying) {
            //determined that the current state is playing (isPlaying is true)
            Image(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(R.string.pb_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                modifier = primaryButtonModifier
                    .padding(CONTENT_PADDING)
                        .clip(CircleShape)
                    .clickable { onPause() }
            )
        }
        else {
            //determined that the current state is paused (isPlaying is false)
            Image(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.pb_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                modifier = primaryButtonModifier
                    .padding(CONTENT_PADDING)
                        .clip(CircleShape)
                    .clickable { onPlay() }
            )
        }

        // Skip to next btn
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.pb_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
            modifier = sideButtonsModifier
                .clip(CircleShape)
                .clickable(enabled = hasNext, onClick = onNext)
                .alpha(if (hasNext) 1f else 0.25f)
        )

        // Repeat btn
        when (repeatState){
            RepeatType.OFF -> {
            //"OFF" -> {
                Image( //shows unfilled icon (because it is set to off)
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = stringResource(R.string.pb_repeat_off),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                    modifier = sideButtonsModifier
                        .clip(CircleShape)
                        .clickable { onRepeat() }
                )
            }
            RepeatType.ONE -> {
            //"ONE" -> {
                Image( //shows the icon with 1 in center (because its set to repeat one song only)
                    imageVector = Icons.Filled.RepeatOneOn,
                    contentDescription = stringResource(R.string.pb_repeat_one_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                    modifier = sideButtonsModifier
                        .clip(CircleShape)
                        .clickable { onRepeat() }
                )
            }
            RepeatType.ON -> {
            //"ON" -> {
                Image( //shows the icon as the filled version (because its set to on)
                    imageVector = Icons.Filled.RepeatOn,
                    contentDescription = stringResource(R.string.pb_repeat_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                    modifier = sideButtonsModifier
                        .clip(CircleShape)
                        .clickable { onRepeat() }
                )
            }
        }
    }
}

//@Preview
@Composable
fun PlayerButtonsPreview() {
    MusicTheme {
        PlayerButtons(
            hasNext = false,
            isPlaying = true,
            isShuffled = false,
            repeatState = RepeatType.ONE,
            onPlay = {},
            onPause = {},
            onShuffle = {},
            onRepeat = {},
            onNext = {},
            onPrevious = {},
        )
    }
}

//@Preview
@SystemLightPreview
@SystemDarkPreview
@Composable
fun PlayerScreenPreview() {
    MusicTheme {
        BoxWithConstraints {
            PlayerScreen(
                currentSong = PreviewSongs[0],
                isPlaying = true,
                isShuffled = true,
                repeatState = RepeatType.ON,
                progress =  154604L / ( PreviewSongs[0].duration.toMillis() ) .toFloat(),
                timeElapsed = 154604L,
                hasNext = true,
                clearQueue = {},
                displayFeatures = emptyList(),
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                navigateBack = {},
                navigateToAlbumDetails = {},
                navigateToArtistDetails = {},
                playerControlActions = PlayerControlActions(
                    onPlay = {},
                    onPause = {},
                    onShuffle = {},
                    onRepeat = {},
                    onSeek = {},
                    onNext = {},
                    onPrevious = {},
                )
            )
        }
    }
}

private const val lyric = "Lorem ipsum odor amet, consectetuer adipiscing elit. Ligula hendrerit nunc semper varius iaculis molestie. Aenean dapibus lorem fusce consectetur venenatis. Id aliquet primis non arcu phasellus potenti nostra per. Enim massa mollis sociosqu, libero mi vivamus. Duis diam pulvinar semper, lorem ridiculus interdum molestie ipsum convallis. Facilisis laoreet et nascetur sollicitudin ornare vehicula pretium. Nunc cubilia quisque class lacus lobortis blandit aliquam mi. Tincidunt arcu proin aliquam nullam himenaeos tempor vivamus. Mattis auctor consequat praesent, cursus cras metus. Elementum lacinia nulla aliquam taciti netus dis. Sit fringilla lacinia pulvinar massa ullamcorper, sollicitudin class elit. Primis rutrum tristique congue maximus potenti suscipit fusce suscipit."