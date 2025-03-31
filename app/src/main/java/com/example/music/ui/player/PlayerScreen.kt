package com.example.music.ui.player

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.ImageBackgroundRadialGradientScrim
import com.example.music.domain.testing.PreviewPlayerSongs
import com.example.music.domain.player.SongPlayerState
import com.example.music.domain.player.model.PlayerSong
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.isCompact
import com.example.music.util.isExpanded
import com.example.music.util.isMedium
import kotlinx.coroutines.launch
import java.time.Duration

//might/likely need a current state for repeat and shuffle

/**
 * StateFUL version of player screen
 */
@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    //navigateToSettings: () -> Unit,
    navigateToQueue: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
    //modifier: Modifier,
) {
    val uiState = viewModel.uiState //original code: not sure why its the only uiState that doesn't have state.collectAsStateWithLifecycle()
    //is it because of savedStateHandle? lets try it with playlistdetails
    PlayerScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        navigateBack = navigateBack,
        navigateToHome = navigateToHome,
        navigateToLibrary = navigateToLibrary,
        navigateToQueue = navigateToQueue, //when not added, is part of viewModel::onAddToQueue
        onStop = viewModel::onStop,
        playerControlActions = PlayerControlActions(
            onPlayPress = viewModel::onPlay,
            onPausePress = viewModel::onPause,
            onNext = viewModel::onNext,
            onPrevious = viewModel::onPrevious,
            onSeekingStarted = viewModel::onSeekingStarted,
            onSeekingFinished = viewModel::onSeekingFinished,
            onShuffle = viewModel::onShuffle,
            onRepeat = viewModel::onRepeat
        ),
    )
}

/**
 * StateLESS version of player screen
 */
@Composable
private fun PlayerScreen(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToQueue: () -> Unit,
    onStop: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
//    DisposableEffect(Unit) {
//        onDispose {
//            onStop()
//        }
//    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)
    val snackbarHostState = remember { SnackbarHostState() }

    //other screens use ScreenBackground call here and pass the Scaffold as the content for it
    Scaffold(
        //other screens add their topBar here, since they use LazyVerticalGrid.
        // Player Screen does not use lazyList/lazyGrid so no need for that here,
        // can show all the elements in one large column with correctly layered components
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,//.windowInsetsPadding(WindowInsets.navigationBars)
        //containerColor = Color.Transparent,
        //other screens add containerColor = Color.Transparent, not needed here since use different
        // background function
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) { contentPadding ->
        if (uiState.songPlayerState.currentSong != null) {
            PlayerContentWithBackground(
                uiState = uiState,
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                navigateBack = navigateBack,
                navigateToQueue = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    navigateToQueue() //was onAddToQueue()
                },
                playerControlActions = playerControlActions,
                contentPadding = contentPadding,
            )
        } else {
            FullScreenLoading()
        }
    }
}

//TODO: see if this can be used to adjust background based on album artwork
// how to rework this to be on the song data
@Composable
private fun PlayerBackground(
    song: PlayerSong?,
    //album: Album?,
    modifier: Modifier,
) {
    //how to make this into album artwork
    //ImageBackgroundColorScrim(
    ImageBackgroundRadialGradientScrim(
        //url = song?.podcastImageUrl,
        imageId = song?.artwork, //need to make this come from PlayerSong
        //color = MaterialTheme.colorScheme.primaryContainer,
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),),
        //colors = listOf(MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onTertiary),//blueDarkColorSet.primary, //TODO
        modifier = modifier,
    )
}

//combines player content with background
@Composable
fun PlayerContentWithBackground(
    uiState: PlayerUiState,
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
            song = uiState.songPlayerState.currentSong,
            modifier = Modifier.fillMaxSize()
        )
        PlayerContent(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            navigateBack = navigateBack,
            navigateToQueue = navigateToQueue,
            playerControlActions = playerControlActions,
            //modifier = Modifier.padding(contentPadding)
        )
    }
}

//wrapper for default class of possible control actions
data class PlayerControlActions(
    val onPlayPress: () -> Unit,
    val onPausePress: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    val onSeekingStarted: () -> Unit,
    val onSeekingFinished: (newElapsed: Duration) -> Unit,
    val onShuffle: () -> Unit,
    val onRepeat: () -> Unit
)

//og version used to determine app view based on window sizing
//can use to switch app view between landscape and portrait
//FOR NOW: use to just select player content regular
@Composable
fun PlayerContent(
    uiState: PlayerUiState,
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
            uiState = uiState,
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
    uiState: PlayerUiState,
    navigateBack: () -> Unit, //call seekToPreviousMediaTime
    navigateToQueue: () -> Unit,
    playerControlActions: PlayerControlActions, // call from seek toNextMedium Item
    modifier: Modifier = Modifier
) {
    val playerSong = uiState.songPlayerState
    val currentSong = playerSong.currentSong ?: return
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
            queue = uiState.songPlayerState.queue,
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
            PlayerImage(
                albumImage = currentSong.title,//currentSong.artwork!!, //TODO: fix this when artwork fixed
                modifier = Modifier.weight(10f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            SongDetails(currentSong.title, currentSong.artistName, currentSong.albumTitle)

            Spacer(modifier = Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(10f)
            ) {
                PlayerSlider(
                    timeElapsed = playerSong.timeElapsed,
                    songDuration = currentSong.duration,
                    onSeekingStarted = playerControlActions.onSeekingStarted,
                    onSeekingFinished = playerControlActions.onSeekingFinished
                )
                PlayerButtons(
                    hasNext = playerSong.queue.isNotEmpty(),
                    isPlaying = playerSong.isPlaying,
                    isShuffled = playerSong.isShuffled,//true, //TODO: fix this
                    repeatState = playerSong.repeatState.name,//"on", //TODO: fix this
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
    queue: List<PlayerSong>,
    navigateBack: () -> Unit,
    navigateToQueue: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        //back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // show queue button
        //IconButton(onClick = navigateToQueue(queue)) {
        IconButton(onClick = navigateToQueue) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = stringResource(R.string.icon_queue),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // more options button
        IconButton(onClick = { /* TODO */ }) {
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
    song: PlayerSong,
    modifier: Modifier = Modifier
) {
    /*
        check if song has lyrics
        if yes, show the lyrics btn as clickable
        else show nothing

        want this to exist as two buttons, Song on left, Lyrics on right
        want the lyrics button to be disabled if no lyrics found for song (or have X on the side?)
        default is to have song side enabled, but if lyrics side enabled, keep that enabled till song side tapped
    */
    var hasLyrics by remember { mutableStateOf(false) }
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
                if (hasLyrics)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
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
                        text = stringResource(id = R.string.cd_lyrics),
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

//TODO: rework this to show album artwork
@Composable
private fun PlayerImage(
    albumImage: String,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        //albumImage = albumImage,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    )
}

@OptIn(ExperimentalFoundationApi::class)
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

fun Duration.formatString(): String {
    val minutes = this.toMinutes().toString().padStart(2, '0')
    val secondsLeft = (this.toSeconds() % 60).toString().padStart(2, '0')
    return "$minutes:$secondsLeft"
}

//TODO: rework this for songs if needed
@Composable
fun PlayerSlider( //removed private modifier to borrow this fun for BottomModals
    timeElapsed: Duration,
    songDuration: Duration?,
    onSeekingStarted: () -> Unit,
    onSeekingFinished: (newElapsed: Duration) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        var sliderValue by remember(timeElapsed) { mutableStateOf(timeElapsed) }
        val maxRange = (songDuration?.toSeconds() ?: 0).toFloat()

        Row(Modifier.fillMaxWidth()) {
            Text(
                //do i have this move along with the thumb?
                // do i split the values to the opposite ends of the slider?
                text = "${sliderValue.formatString()} • ${songDuration?.formatString()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, //TODO
            )
        }

        Slider(
            value = sliderValue.seconds.toFloat(),
            valueRange = 0f..maxRange,
            onValueChange = {
                onSeekingStarted()
                sliderValue = Duration.ofSeconds(it.toLong())
            },
            onValueChangeFinished = { onSeekingFinished(sliderValue) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onPrimary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary,
            ),
            /*interactionSource = /*MutableInteractionSource = */remember { MutableInteractionSource() },
            thumb = {
                SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
                    interactionSource = interactionSource,
                    thumbSize = DpSize(40.dp,40.dp)
                )
            }*/
        )
    }
}

//TODO: rework this for song player
@Composable
private fun PlayerButtons(
    hasNext: Boolean,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatState: String,
    onPlayPress: () -> Unit,
    onPausePress: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
    //need a state saver to handle button interactions
) {

    /*
        ----- Logic for player buttons -----
            want the play / pause button to show the action it will take if pressed
            want the shuffle and repeat buttons to show the current state they are in
            want the previous and next buttons to show enabled
                for previous: if passed starting point, it will restart the song
                    if on starting point, plays previous song in queue
                    if no previous song in queue, ends the queue/session
                    if repeat is on, check queue:
                        if no queue or repeat is one, restarts current song
                        if no queue or repeat is all, plays last song in context of current song player TODO check if need shuffle logic here too
                for next: if pressed, skips to next song
                    if has queue:
                        if repeat is on, play next song
                        if repeat is one, restart current song
                        if repeat is all and song is the last one in queue, restart queue from beginning
                            if shuffle is set to every re-queue, shuffle queue then restart
     */
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val sideButtonsModifier = Modifier
            .size(sideButtonSize)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        val primaryButtonModifier = Modifier
            .size(playerButtonSize)
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
            .semantics { role = Role.Button }

        //shuffle button
        if (isShuffled) {
            //determined that the current state IS shuffled (isShuffled is true)
            Image(
                //imageVector = Icons.Default.ShuffleOn,
                //imageVector = Icons.Outlined.ShuffleOn,
                imageVector = Icons.Filled.ShuffleOn,
                contentDescription = stringResource(R.string.cd_shuffle_on),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer), //TODO
                modifier = sideButtonsModifier
                    .clickable(enabled = true, onClick = onShuffle)
                //.clickable(enabled = isPlaying, onClick = onPrevious)//TODO: change this to make it work for shuffle
                //.alpha(if (isPlaying) 1f else 0.25f) //likely change opacity if playing
            )
        } else {
            //determined that the current state IS NOT shuffled (isShuffled is false)
            Image(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.cd_shuffle_off),
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer), //TODO
                modifier = sideButtonsModifier
                    .clickable(enabled = true, onClick = onShuffle)
            )
        }

        //Image for Skip back to previous button
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.cd_skip_previous),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = sideButtonsModifier
                .clickable(enabled = true, onClick = onPrevious)
                //.clickable(enabled = isPlaying, onClick = onPrevious)
                //.alpha(if (isPlaying) 1f else 0.25f)
        )

        if (isPlaying) {
            //determined that the current state is playing (isPlaying is true)
            Image(
                imageVector = Icons.Filled.Pause,
                //imageVector = Icons.Outlined.Pause,
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPausePress()
                    }
            )
        } else {
            //determined that the current state is paused (isPlaying is false)
            Image(
                imageVector = Icons.Filled.PlayArrow,
                //imageVector = Icons.Outlined.PlayArrow,
                contentDescription = stringResource(R.string.cd_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable {
                        onPlayPress()
                    }
            )
        }

        //skip to next playable button
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.cd_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = sideButtonsModifier
                .clickable(enabled = true, onClick = onNext)
                //.clickable(enabled = hasNext, onClick = onNext)
                //.alpha(if (hasNext) 1f else 0.25f)
        )

        //repeat button
        when (repeatState){
            "OFF" -> {
                Image( //shows unfilled icon (because it is set to off)
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = stringResource(R.string.cd_repeat_off),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = sideButtonsModifier
                        .clickable(enabled = true, onClick = onRepeat)
                    //TODO: create action for repeat queue change
                    //onAdvanceBy(Duration.ofSeconds(10))
                )
            }
            "ON" -> {
                Image( //shows the icon as the filled version (because its set to on)
                    imageVector = Icons.Filled.RepeatOn,
                    contentDescription = stringResource(R.string.cd_repeat_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = sideButtonsModifier
                        .clickable(enabled = true, onClick = onRepeat)
                    //TODO: create action for repeat queue change
                    //onAdvanceBy(Duration.ofSeconds(10))
                )
            }
            "ONE" -> {
                Image( //shows the icon with 1 in center (because its set to repeat one song only)
                    imageVector = Icons.Filled.RepeatOneOn,
                    contentDescription = stringResource(R.string.cd_repeat_one_on),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = sideButtonsModifier
                        .clickable(enabled = true, onClick = onRepeat)
                    //TODO: create action for repeat queue change
                    //onAdvanceBy(Duration.ofSeconds(10))
                )
            }
        }
    }
}

//full screen circular progress - loading screen
@Composable
private fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

//@Preview
@Composable
fun PlayerButtonsPreview() {
    MusicTheme {
        PlayerButtons(
            hasNext = false,
            isPlaying = true, //would show pause btn because song is in play
            isShuffled = false,
            repeatState = "one",
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
@SystemDarkPreview
@Composable
fun PlayerScreenPreview() {
    MusicTheme {
        BoxWithConstraints {
            PlayerScreen(
                PlayerUiState(
                    songPlayerState = SongPlayerState(
                        currentSong = PreviewPlayerSongs[0],
                        isPlaying = false,
                        queue = PreviewPlayerSongs
                    ),
                ),
                displayFeatures = emptyList(),
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                navigateToHome = { },
                navigateToLibrary = { },
                navigateBack = { },
                navigateToQueue = {},
                onStop = {},
                playerControlActions = PlayerControlActions(
                    onPlayPress = {},
                    onPausePress = {},
                    onShuffle = {},
                    onRepeat = {},
                    onSeekingStarted = {},
                    onSeekingFinished = {},
                    onNext = {},
                    onPrevious = {},
                )
            )
        }
    }
}

private const val lyric = "Lorem ipsum odor amet, consectetuer adipiscing elit. Ligula hendrerit nunc semper varius iaculis molestie. Aenean dapibus lorem fusce consectetur venenatis. Id aliquet primis non arcu phasellus potenti nostra per. Enim massa mollis sociosqu, libero mi vivamus. Duis diam pulvinar semper, lorem ridiculus interdum molestie ipsum convallis. Facilisis laoreet et nascetur sollicitudin ornare vehicula pretium. Nunc cubilia quisque class lacus lobortis blandit aliquam mi. Tincidunt arcu proin aliquam nullam himenaeos tempor vivamus. Mattis auctor consequat praesent, cursus cras metus. Elementum lacinia nulla aliquam taciti netus dis. Sit fringilla lacinia pulvinar massa ullamcorper, sollicitudin class elit. Primis rutrum tristique congue maximus potenti suscipit fusce suscipit."