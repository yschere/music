package com.example.music.ui.shared

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.getSongData
import com.example.music.ui.theme.MusicTheme
private const val TAG = "Mini Player"

@Composable
fun MiniPlayer(
    song: SongInfo,
    isPlaying: Boolean = true,
    navigateToPlayer: () -> Unit,
    //navigateToQueue: () -> Unit = {},
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Log.i(TAG, "Song: ${song.title}\n" +
        "has Artist?: ${song.artistName}\n" +
        "has artwork?: ${song.artworkUri}\n")

    val sideButtonsModifier = Modifier
        .size(sideButtonSize)
        .background(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    val primaryButtonModifier = Modifier
        .size(playerButtonSize)
        .background(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.fillMaxWidth(),
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.inversePrimary,
            onClick = { navigateToPlayer() },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                HeaderImage(song.artworkUri, song.title)
                Column(Modifier.padding(8.dp).weight(1f)) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = song.setSubtitle(),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                //Spacer(modifier = Modifier.weight(1f))
                if (isPlaying) {
                    //determined that the current state is playing (isPlaying is true)
                    Image(
                        imageVector = Icons.Filled.Pause,
                        contentDescription = stringResource(R.string.pb_pause),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = sideButtonsModifier
                            .padding(8.dp)
                            .clickable { onPausePress() }
                    )
                } else {
                    //determined that the current state is paused (isPlaying is false)
                    Image(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.pb_play),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = sideButtonsModifier
                            .padding(8.dp)
                            .clickable { onPlayPress() }
                    )
                }
                /*IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = stringResource(R.string.icon_queue),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = sideButtonsModifier
                            .padding(8.dp)
                            .clickable {
                                navigateToQueue()
                            }
                    )
                }*/
            }
        }
    }
}

@Composable
fun BottomSheetFullPlayer(
    song: SongInfo,
    isPlaying: Boolean = true,
    navigateToPlayer: () -> Unit,
    //navigateToQueue: () -> Unit = {},
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Log.i(TAG, "Song: ${song.title}\n" +
            "has Artist?: ${song.artistName}\n" +
            "has artwork?: ${song.artworkUri}\n")
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

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = navigateToPlayer,
        ) {
            Column {
                //track info row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    HeaderImage(song.artworkUri, song.title)
                    Column(Modifier.padding(8.dp).weight(1f)) {
                        Text(
                            text = song.title,
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = song.setSubtitle(),
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    /*IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = stringResource(R.string.icon_queue),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = sideButtonsModifier
                                .padding(8.dp)
                                .clickable {
                                    navigateToQueue()
                                }
                        )
                    }*/

                    //player buttons row
                    MiniPlayerButtons(
                        //removed private modifier to borrow this fun for BottomModals
                        //hasNext = true,
                        isPlaying = isPlaying,
                        onPlayPress = onPlayPress,
                        onPausePress = onPausePress,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        modifier = Modifier,
                        primaryButtonModifier = primaryButtonModifier,
                        sideButtonsModifier = sideButtonsModifier,
                        //need a state saver to handle button interactions
                    )

                    //slider row
                    /*PlayerSlider(
                        progress = 0f,
                        timeElapsed = 0L,
                        songDuration = song.duration,
                        onSeek = {},
                    )*/
                }
            }
        }
    }
}

@Composable
fun MiniPlayerButtons(
    //hasNext: Boolean = false,
    isPlaying: Boolean = true,
    onPlayPress: () -> Unit = {},
    onPausePress: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    modifier: Modifier = Modifier,
    sideButtonsModifier: Modifier,
    primaryButtonModifier: Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //Image for Skip back to previous button
        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.pb_skip_previous),
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
                contentDescription = stringResource(R.string.pb_pause),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable { onPausePress() }
            )
        } else {
            //determined that the current state is paused (isPlaying is false)
            Image(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.pb_play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = primaryButtonModifier
                    .padding(8.dp)
                    .clickable { onPlayPress() }
            )
        }

        //skip to next playable button
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.pb_skip_next),
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = sideButtonsModifier
                .clickable(enabled = true, onClick = onNext)
            //.alpha(if (hasNext) 1f else 0.25f)
        )
    }
}

//@CompLightPreview
//@CompDarkPreview
@Composable
fun PreviewBottomBarPlayer() {
    MusicTheme {
        MiniPlayer(
            song = getSongData(6535),
            isPlaying = true,
            navigateToPlayer = {},
            //navigateToQueue = {},
            onPlayPress = {},
            onPausePress = {},
        )
    }
}