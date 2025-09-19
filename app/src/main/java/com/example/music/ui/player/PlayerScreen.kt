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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.Thumb
import androidx.compose.material3.SliderDefaults.Track
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.music.designsys.component.ImageBackgroundRadialGradientScrim
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.formatString
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.isCompact
import com.example.music.util.isExpanded
import com.example.music.util.isMedium
import com.example.music.util.verticalGradientScrim
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.math.roundToLong

private const val TAG = "Player Screen"

/**
 * Stateless version of Player Screen
 */
@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
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
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        navigateBack = navigateBack,
        playerControlActions = PlayerControlActions(
            onPlayPress = viewModel::onPlay,
            onPausePress = viewModel::onPause,
            onNext = viewModel::onNext,
            onPrevious = viewModel::onPrevious,
            onSeek = viewModel::onSeek,
            onShuffle = viewModel::onShuffle,
            onRepeat = viewModel::onRepeat
        ),
    )
    //if (uiState.errorMessage != null) {
    /*if (state.errorMessage != null) { //this is changed with PlayerUiStateV2 being state
        PlayerScreenError(onRetry = viewModel::refresh)
    }*/
}

//wrapper for default class of possible control actions
data class PlayerControlActions(
    val onPlayPress: () -> Unit,
    val onPausePress: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onSeek: (Long) -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit
)

data class MiniPlayerControlActions(
    val onPlayPress: () -> Unit,
    val onPausePress: () -> Unit,
)

data class MiniPlayerExpandedControlActions(
    val onPlayPress: () -> Unit,
    val onPausePress: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit
)

/**
 * Error Screen
 */
@Composable
private fun PlayerScreenError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Stateless version of Player Screen
 */
@Composable
private fun PlayerScreen(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)
    val snackbarHostState = remember { SnackbarHostState() }

    //other screens use ScreenBackground call here and pass the Scaffold as the content for it
    Scaffold(
        // other screens add their topBar here, since they use LazyVerticalGrid.
        // Player Screen does not use lazyList/lazyGrid so no need for that here,
        // can show all the elements in one large column with correctly layered components
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,//.windowInsetsPadding(WindowInsets.navigationBars)
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                navigateBack = navigateBack,
                navigateToQueue = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                },
                playerControlActions = playerControlActions,
                contentPadding = contentPadding,
            )
        } else {
            FullScreenLoading()
        }
    }
}

/**
 * Loading Screen
 */
@Composable
private fun FullScreenLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}
//full screen circular progress - loading screen

@Composable
private fun PlayerBackground(
    song: SongInfo,
    modifier: Modifier,
) {
    //how to make this into album artwork
    ImageBackgroundRadialGradientScrim(
        //url = song?.podcastImageUrl,
        imageId = song.title, //FixMe: needs to be artwork bitmap or uri
        //color = MaterialTheme.colorScheme.primaryContainer,
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),),
        //colors = listOf(MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onTertiary),//blueDarkColorSet.primary,
        modifier = modifier,
    )
}

//combines player content with background
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
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit, //was onAddToQueue
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        PlayerBackground(
            song = currentSong,
            modifier = Modifier.fillMaxSize()
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
            navigateBack = navigateBack,
            navigateToQueue = navigateToQueue,
            playerControlActions = playerControlActions,
            //modifier = Modifier.padding(contentPadding)
        )
    }
}

//og version used to determine app view based on window sizing
//can use to switch app view between landscape and portrait
//FOR NOW: use to just select player content regular
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
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    //val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    //foldingFeature used to determine player screen layouts in a lot of if elseif comparisons

    if ( windowSizeClass.isCompact ) {
        //regular look, no change
    }

    if ( windowSizeClass.isMedium ) {
        //land scape, wider look, need something different
    }

    if ( windowSizeClass.isExpanded ) {
        //big screen, could use all the extra space better
    }

    //this was in the else as the most regular iteration
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PlayerContentRegular (
            currentSong = currentSong,
            isPlaying = isPlaying,
            isShuffled = isShuffled,
            repeatState = repeatState,
            progress = progress,
            timeElapsed = timeElapsed,
            hasNext = hasNext,
            navigateBack = navigateBack,
            navigateToQueue = navigateToQueue,
            playerControlActions = playerControlActions,
            modifier = modifier
        )
    }
}

//regular view set for player content (borrowed from list of portrait/landscape options
//could take another to rework for landscape and portrait sake
//FOR NOW: default to portrait view
@Composable
private fun PlayerContentRegular(
    currentSong: SongInfo,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: RepeatType,
    progress: Float,
    timeElapsed: Long,
    hasNext: Boolean,
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            //.radialGradientScrimBottomRight( colors = listOf(MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.background) )
            //.radialGradientScrimCentered( color = MaterialTheme.colorScheme.onPrimary )
            //.verticalGradientScrim( color = MaterialTheme.colorScheme.onPrimary, startYPercentage = 1f, endYPercentage = 0f )
            //.radialGradientScrimAnyOffset( color = MaterialTheme.colorScheme.onPrimary, xOffset = 2, yOffset = 3 )
            //.radialGradientScrimAnyColorsOffset( colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onPrimary), xOffset = 2, yOffset = 2  )
            .systemBarsPadding()//why is this getting called again when it was passed into the column around PlayerContentRegular?
            .padding(horizontal = 8.dp)
    ) {
        PlayerTopAppBar(
            //queue = uiState.songControllerState.queue,
            navigateBack = navigateBack,
            navigateToQueue = navigateToQueue,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            //would need to put song / lyrics function shifter here
            SongLyricsSwitch(currentSong, modifier = Modifier.weight(0.1f))

            Spacer(modifier = Modifier.weight(1f))
            PlayerImageBm(
                albumImage = currentSong.artworkBitmap,//currentSong.artMap ?: ,//currentSong.artwork!!, //FixMe: change this to bitmap or url when artwork fixed
                modifier = Modifier.weight(10f).background(Color.Transparent)
            )
            Spacer(modifier = Modifier.height(32.dp))

            SongDetails(currentSong.title, currentSong.artistName, currentSong.albumTitle)

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
                    hasNext = hasNext, // Question: should uiState have access to queue or should it be able to return a boolean here that viewmodel asks to songController?
                    isPlaying = isPlaying,
                    isShuffled = isShuffled,
                    repeatState = repeatState,
                    onPlayPress = playerControlActions.onPlayPress,
                    onPausePress = playerControlActions.onPausePress,
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
}

//no background, icons only, default top bar
@Composable
private fun PlayerTopAppBar(
    //queue: List<SongInfo>, // queue is not currently in use here for navigation to QueueScreen, don't think it should be used for nav at all
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit,
    onMoreOptionsClick: () -> Unit = {},
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Back btn
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // navigateToQueue btn
        IconButton(onClick = navigateToQueue) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = stringResource(R.string.icon_queue),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // More Options btn
        IconButton(onClick = onMoreOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun SongLyricsSwitch(
    song: SongInfo,
    modifier: Modifier = Modifier
) {
    /* TODO:
        check if song has lyrics
        if yes, show the lyrics btn as clickable
        else show nothing
        want this to exist as two buttons, Song on left, Lyrics on right
        want the lyrics button to be disabled if no lyrics found for song (or have X on the side?)
        default is to have song side enabled, but if lyrics side enabled, keep that enabled till song side tapped
    */
    val hasLyrics by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(
            text = "Song",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(4.dp)
                .clickable(
                    enabled = true,
                    onClickLabel = "Show Song Details",
                    role = Role.Button,
                    onClick = { showLyrics = false }
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
            modifier = Modifier.padding(4.dp)
                .clickable(
                    enabled = hasLyrics,
                    onClickLabel = "Show Song Details",
                    role = Role.Button,
                    onClick = { showLyrics = true }
                )
        )
    }
    if (showLyrics) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = lyric,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Song",
                style = MaterialTheme.typography.bodyMedium,
                maxLines =
                if (hasLyrics) Int.MAX_VALUE
                else 3,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    showLyrics = result.hasVisualOverflow
                },
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = EaseOutExpo
                    )
                )
            )
            if (showLyrics) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        //.align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // TODO: Add gradient effect
                    Text(
                        text = stringResource(id = R.string.pb_show_lyrics),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

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
            .clip(MaterialTheme.shapes.medium)
    )
}

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
            .size(250.dp)
            //.sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
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
            modifier = Modifier//.basicMarquee()
        )
    }
    albumTitle?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier//.basicMarquee()
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

        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            Text(
                text = "${Duration.ofMillis(timeElapsed).formatString()} • ${songDuration?.formatString()}",
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
    //repeatState: String,
    repeatState: RepeatType,
    onPlayPress: () -> Unit,
    onPausePress: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val sideButtonsModifier = Modifier
            .size(sideButtonSize)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val primaryButtonModifier = Modifier
            .size(primaryButtonSize)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        // Shuffle btn
        if (isShuffled) {
            //determined that the current state IS shuffled (isShuffled is true)
            Image(
                imageVector = Icons.Filled.ShuffleOn,
                contentDescription = stringResource(R.string.pb_shuffle_on),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = sideButtonsModifier
                    .clickable { onShuffle() }
            )
        }
        else {
            //determined that the current state IS NOT shuffled (isShuffled is false)
            Image(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.pb_shuffle_off),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = sideButtonsModifier
                    .clickable { onShuffle() }
            )
        }

        // Skip back to previous btn
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.pb_skip_previous),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = sideButtonsModifier
                .clickable { onPrevious() }
        )

        // Play and Pause btn
        if (isPlaying) {
            //determined that the current state is playing (isPlaying is true)
            Image(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(R.string.pb_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable { onPausePress() }
            )
        }
        else {
            //determined that the current state is paused (isPlaying is false)
            Image(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.pb_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable { onPlayPress() }
            )
        }

        // Skip to next btn
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.pb_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = sideButtonsModifier
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
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = sideButtonsModifier
                        .clickable { onRepeat() }
                )
            }
            RepeatType.ONE -> {
            //"ONE" -> {
                Image( //shows the icon with 1 in center (because its set to repeat one song only)
                    imageVector = Icons.Filled.RepeatOneOn,
                    contentDescription = stringResource(R.string.pb_repeat_one_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = sideButtonsModifier
                        .clickable { onRepeat() }
                )
            }
            RepeatType.ON -> {
            //"ON" -> {
                Image( //shows the icon as the filled version (because its set to on)
                    imageVector = Icons.Filled.RepeatOn,
                    contentDescription = stringResource(R.string.pb_repeat_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = sideButtonsModifier
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
            repeatState = RepeatType.ONE,//"one",
            onPlayPress = {},
            onPausePress = {},
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
                displayFeatures = emptyList(),
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                navigateBack = { },
                playerControlActions = PlayerControlActions(
                    onPlayPress = {},
                    onPausePress = {},
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