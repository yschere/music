package com.example.music.ui.player

import android.content.res.Configuration
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
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.ShuffleOn
import androidx.compose.material.icons.sharp.ShuffleOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.component.ImageBackgroundColorScrim
import com.example.music.designsys.component.ImageBackgroundRadialGradientScrim
import com.example.music.designsys.theme.MusicTypography
import com.example.music.designsys.theme.blueDarkSet
import com.example.music.designsys.theme.blueLightSet
import com.example.music.ui.theme.blueDarkColorSet
import com.example.music.ui.theme.blueLightColorSet
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.player.SongPlayerState
import com.example.music.player.model.PlayerSong
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.verticalGradientScrim
import kotlinx.coroutines.launch
import java.time.Duration

//TODO: rework com.example.music.ui.album episode things to song things
//might/likely need a current state for repeat and shuffle

//StateFUL version of player screen
@Composable
fun PlayerScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onShowQueuePress: () -> Unit, //not added in jetcaster
    viewModel: PlayerViewModel = hiltViewModel(),
    //modifier: Modifier,
) {
    val uiState = viewModel.uiState
    PlayerScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        onBackPress = onBackPress,
        onShowQueuePress = onShowQueuePress, //when not added, is part of viewModel::onAddToQueue
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

//StateLESS version of player screen
@Composable
private fun PlayerScreen(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onShowQueuePress: () -> Unit, //was onAddToQueue: () -> Unit
    onStop: () -> Unit,
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    DisposableEffect(Unit) {
        onDispose {
            onStop()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue)
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { contentPadding ->
        if (uiState.songPlayerState.currentSong != null) {
            PlayerContentWithBackground(
                uiState = uiState,
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                onBackPress = onBackPress,
                onShowQueuePress = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onShowQueuePress() //was onAddToQueue()
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
@Composable
//TODO: how to rework this to be on the song data
private fun PlayerBackground(
    song: PlayerSong?,
    //album: Album?,
    modifier: Modifier,
) {
    //how to make this into album artwork
    //ImageBackgroundColorScrim(
    ImageBackgroundRadialGradientScrim(
        //url = song?.podcastImageUrl,
        imageId = "fake link", //need to make this come from PlayerSong
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        //color = Color(0x0fffffff),//blueDarkColorSet.primary, //TODO
        modifier = modifier,
    )
}

//combines player content with background
@Composable
fun PlayerContentWithBackground(
    uiState: PlayerUiState,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onBackPress: () -> Unit,
    onShowQueuePress: () -> Unit, //was onAddToQueue
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        PlayerBackground(
            song = uiState.songPlayerState.currentSong,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
        PlayerContent(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            onBackPress = onBackPress,
            onShowQueuePress = onShowQueuePress,
            playerControlActions = playerControlActions,
            //modifier = Modifier
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
    onBackPress: () -> Unit,
    onShowQueuePress: () -> Unit, //was onAddToQueue
    playerControlActions: PlayerControlActions,
    modifier: Modifier = Modifier
) {
    //val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    //foldingFeature used to determine player screen layouts in a lot of if elseif comparisons

    //this was in the else as the most regular iteration
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        PlayerContentRegular (
            uiState = uiState,
            onBackPress = onBackPress,
            onShowQueuePress = onShowQueuePress,
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
    onBackPress: () -> Unit, //call seekToPreviousMediaTime
    onShowQueuePress: () -> Unit,
    playerControlActions: PlayerControlActions, // call from seek toNextMedium Item
    modifier: Modifier = Modifier
) {
    val playerSong = uiState.songPlayerState
    val currentSong = playerSong.currentSong ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalGradientScrim(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                //color = MaterialTheme.colorScheme.secondary,
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        TopAppBar(
            onBackPress = onBackPress,
            onShowQueuePress = onShowQueuePress,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            //would need to put song / lyrics function shifter here

            Spacer(modifier = Modifier.weight(1f))
            PlayerImage(
                albumImage = currentSong.artwork!!,
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
                    isShuffled = playerSong.isPlaying, //TODO: fix this
                    repeatingState = "on", //TODO: fix this
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
private fun TopAppBar(
    onBackPress: () -> Unit,
    onShowQueuePress: () -> Unit
){
    Row(Modifier.fillMaxWidth()) {

        //back button
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back)
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        //add to playlist button
        IconButton(onClick = onShowQueuePress) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = stringResource(R.string.cd_add)
            )
        }

        //options button
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

@Composable
private fun SongLyricsSwitch(
    modifier: Modifier = Modifier
) {
    /*
        check if song has lyrics
        if yes, show the set of songs
        else show nothing

        want this to exist as two buttons, Song on left, Lyrics on right
        want the lyrics button to be disabled if no lyrics found for song (or have X on the side?)
        default is to have song side enabled, but if lyrics side enabled, keep that enabled till song side tapped

    */
    var hasLyrics by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.clickable { hasLyrics = !hasLyrics }
    ) {
        Text(
            text = "Song",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (hasLyrics) Int.MAX_VALUE else 3,
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
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // TODO: Add gradient effect
                Text(
                    text = stringResource(id = R.string.see_more),
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

//TODO: rework this to show album artwork
@Composable
private fun PlayerImage(
    albumImage: String,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        albumImage = 1,//albumImage,
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
            modifier = Modifier.basicMarquee()
        )
    }
    Text(
        text = albumTitle.toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

@Composable
private fun SongLyrics(
    title: String,
    lyrics: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    lyricTextStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = title,
        style = titleTextStyle,
        maxLines = 1,
        color = MaterialTheme.colorScheme.primary //TODO
    )
    Text(
        text = lyrics,
        style = lyricTextStyle,
        color = MaterialTheme.colorScheme.secondary //TODO
    )
}

fun Duration.formatString(): String {
    val minutes = this.toMinutes().toString().padStart(2, '0')
    val secondsLeft = (this.toSeconds() % 60).toString().padStart(2, '0')
    return "$minutes:$secondsLeft"
}

//TODO: rework this for songs if needed
@Composable
private fun PlayerSlider(
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
            //interactionSource = MutableInteractionSource = remember { MutableInteractionSource() },
//            thumb = {
//                SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
//                    interactionSource = interactionSource,
//                    thumbSize = DpSize(40.dp,40.dp)
//                )
//            }
        )
    }
}

//TODO: rework this for song player
@Composable
private fun PlayerButtons(
    hasNext: Boolean,
    isPlaying: Boolean,
    isShuffled: Boolean,
    repeatingState: String,
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
            //determined that the current state is shuffled (isShuffled is true)
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
            //determined that the current state is shuffled (isShuffled is false)
            Image(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.cd_shuffle_off),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer), //TODO
                modifier = sideButtonsModifier
                    .padding(8.dp)
                    .clickable {
                        onPlayPress()
                    }
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
        when (repeatingState){
            "off" -> {
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
            "on" -> {
                Image( //shows the icon as the filled version (because its set to on)
                    imageVector = Icons.Filled.RepeatOn,
                    contentDescription = stringResource(R.string.cd_repeat_off),
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = sideButtonsModifier
                        .clickable(enabled = true, onClick = onRepeat)
                    //TODO: create action for repeat queue change
                    //onAdvanceBy(Duration.ofSeconds(10))
                )
            }
            "one" -> {
                Image( //shows the icon with 1 in center (because its set to repeat one song only)
                    imageVector = Icons.Filled.RepeatOne,
                    contentDescription = stringResource(R.string.cd_repeat_off),
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

//@Preview (name = "light mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopAppBarPreview() {
    //uses music ui theme to display
    MusicTheme {
        //default top bar
        TopAppBar(
            onBackPress = {},
            onShowQueuePress = {},
            //action to call for more options button would go here
        )
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
            repeatingState = "one",
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
@Composable
fun PlayerButtonsPreview_Paused_NotShuffled() {
    MusicTheme {
        PlayerButtons(
            hasNext = true,
            isPlaying = false, //would show play btn because song is paused
            isShuffled = false,
            repeatingState = "one",
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
@Composable
fun PlayerButtonsPreview_Playing_Shuffled_RepeatOn() {
    MusicTheme {
        PlayerButtons(
            hasNext = true,
            isPlaying = true, //would show play btn because song is paused
            isShuffled = true,
            repeatingState = "on",
            onPlayPress = {},
            onPausePress = {},
            onShuffle = {},
            onRepeat = {},
            onNext = {},
            onPrevious = {},
        )
    }
}

@Preview
//@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayerScreenPreview() {
    MusicTheme {
        BoxWithConstraints {
            PlayerScreen(
                PlayerUiState(
                    songPlayerState = SongPlayerState(
                        currentSong = PlayerSong(
                            title = PreviewSongs[0].title,
                            duration = PreviewSongs[0].duration,
                            artistName = PreviewArtists[0].name,
                            albumTitle = PreviewAlbums[0].title,
                        ),
                        isPlaying = false,
                        queue = listOf(
                            PlayerSong(),
                            PlayerSong(),
                            PlayerSong(),
                        )
                    ),
                ),
                displayFeatures = emptyList(),
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                onBackPress = { },
                onShowQueuePress = {},
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
